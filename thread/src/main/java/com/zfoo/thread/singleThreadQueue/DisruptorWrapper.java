package com.zfoo.thread.singleThreadQueue;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.zfoo.thread.task.DefaultTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisruptorWrapper implements SingleThreadQueue{
  private static final Logger logger = LoggerFactory.getLogger(DisruptorWrapper.class);
  private static final  int ringBufferSize = 1024*8;
  public final int id;
  public final String name;

  private final Disruptor<DefaultTask> disruptor;
  private final RingBuffer<DefaultTask> ringBuffer;

  DisruptorWrapper(int id, String name) {
    this.id = id;
    this.name = name;
    this.disruptor =
        new Disruptor<>(
                DefaultTask::new,
                ringBufferSize,
            r -> {
              return new Thread(r, name);
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
    logger.info("Disruptor 线程队列启动， id={}, name={}, ringBufferSize={}", id, name, ringBufferSize);
  }

  @Override
  public void shutdown() {
    disruptor.shutdown();
    logger.info("Disruptor 线程队列关闭， id={}, name={}, ringBufferSize={}", id, name, ringBufferSize);
  }

  @Override
  public void addTask(Runnable runnable) {
    long seq = ringBuffer.next();
    try {
      ringBuffer.get(seq).setRunnable(runnable);
    } finally {
      ringBuffer.publish(seq);
    }
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
}
