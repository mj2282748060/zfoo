package com.zfoo.thread.singleThreadQueue;

import com.zfoo.thread.IThreadExecutor;
import com.zfoo.thread.enums.ThreadGroupEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import java.util.concurrent.Executor;


public class SingleThreadQueues implements  IThreadExecutor{
  private static final Logger logger = LogManager.getLogger(SingleThreadQueues.class);
  private final DisruptorWrapper[] singleThreadQueues;
  private  final  long LEN;
  private  final ThreadGroupEnum groupType;

  public SingleThreadQueues(ThreadGroupEnum groupType) {
    checkLen(groupType);
    LEN = groupType.getThreadCount();
    this.groupType = groupType;
    singleThreadQueues = new DisruptorWrapper[(int) LEN];
    ThreadGroup threadGroup = new ThreadGroup(groupType.getName());
    for (int i = 0; i < LEN; i++) {
      singleThreadQueues[i] =  new DisruptorWrapper(i, groupType.getName() + NAME_LIMIT+i, threadGroup);
    }
    logger.info("{}线程池创建完成， 线程队列个数={}", groupType.getName(), LEN);
  }

  private void checkLen(ThreadGroupEnum groupType) {
    int threadCount = groupType.getThreadCount();
    Assert.isTrue((threadCount>0) &&  (0 == (threadCount & (threadCount-1)) ), groupType.getName()+" 单任务线程队列的线程个数必须是2的幂次方倍， 现在是= "+threadCount);
  }

  public DisruptorWrapper getByMod(long key) {
    int index = (int) (key & (LEN-1));
    return singleThreadQueues[index];
  }

  public int size() {
    return (int) LEN;
  }

  public void shutdown() {
    for (DisruptorWrapper d : singleThreadQueues) {
      d.shutdown();
    }
  }

  @Override
  public Executor runThread(long threadId, long key) {
    DisruptorWrapper wrapper = getByMod(key);
    if (wrapper.thread.getId() == threadId) {
      return wrapper;
    }

    for (DisruptorWrapper threadQueue : singleThreadQueues) {
      if (threadQueue.thread.getId() == threadId) {
        return threadQueue;
      }
    }
    return null;
  }

  @Override
  public ThreadGroupEnum threadGroup() {
    return groupType;
  }

  @Override
  public void execute(Runnable command) {
    if (command == null) {
      return;
    }
    getByMod(command.hashCode()).execute(command);
  }
}
