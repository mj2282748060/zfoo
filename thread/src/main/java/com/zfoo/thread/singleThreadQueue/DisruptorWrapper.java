package com.zfoo.thread.singleThreadQueue;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.zfoo.thread.task.DefaultTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DisruptorWrapper implements Executor {
  private static final Logger logger = LoggerFactory.getLogger(DisruptorWrapper.class);
  private static final  int ringBufferSize = 1024*8;
  public  int id;
  public  Thread thread;
  public final String name;

  private final Disruptor<DefaultTask> disruptor;
  private final RingBuffer<DefaultTask> ringBuffer;

  DisruptorWrapper(int id, String name, ThreadGroup threadGroup) {
    this.id = id;
    this.name = name;
    this.disruptor =
        new Disruptor<>(
                DefaultTask::new,
                ringBufferSize,
            r -> {
              return new Thread(threadGroup, r, name);
            });

      disruptor.handleEventsWith((event, sequence, endOfBatch) -> handleEvent(event));
      disruptor.setDefaultExceptionHandler(
        new ExceptionHandler<>() {
          @Override
          public void handleEventException(Throwable ex, long sequence, DefaultTask event) {
            handleException(ex);
          }

          @Override
          public void handleOnStartException(Throwable ex) {
            handleException(ex);
          }

          @Override
          public void handleOnShutdownException(Throwable ex) {
            handleException(ex);
          }
        });

    this.ringBuffer = disruptor.getRingBuffer();
    disruptor.start();
    try {
      this.thread = CompletableFuture.supplyAsync(Thread::currentThread, this).get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    logger.info("Disruptor 线程队列启动， id={}, name={}, ringBufferSize={}", id, name, ringBufferSize);
  }

  public void shutdown() {
    thread = null;
    disruptor.shutdown();
    logger.info("Disruptor 线程队列关闭， id={}, name={}, ringBufferSize={}", id, name, ringBufferSize);
  }

  private void handleEvent(DefaultTask task) {
    try {
      task.run();
    } catch (Throwable t) {
      logger.error("Disruptor线程{}处理事件异常！", this, t);
    } finally {
      task.clear();
    }
  }

  private void handleException(Throwable t) {
    logger.error("Disruptor线程{}捕获异常！", this, t);
  }

  @Override
  public void execute(Runnable runnable) {
    if (runnable == null) {
      return;
    }

    long seq = ringBuffer.next();
    try {
      ringBuffer.get(seq).setRunnable(runnable);
    } finally {
      ringBuffer.publish(seq);
    }
  }
}
