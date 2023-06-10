package com.zfoo.thread.singleThreadQueue;

import com.zfoo.thread.enums.SingleThreadGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;


public class SingleThreadQueues {
  private static final Logger logger = LogManager.getLogger(SingleThreadQueues.class);
  private final SingleThreadQueue[] singleThreadQueues;
  private  final  long LEN;
  private  final  String NAME_LIMIT = "_";

  public SingleThreadQueues(SingleThreadGroup groupType) {
    checkLen(groupType);
    LEN = groupType.getThreadCount();
    singleThreadQueues = new SingleThreadQueue[(int) LEN];
    String namePre = groupType.getName() + NAME_LIMIT;
    for (int i = 0; i < LEN; i++) {
      singleThreadQueues[i] = createSingleThreadQueue(i,  namePre + i);
    }
    logger.info("{}线程池创建完成， 线程队列个数={}", groupType.getName(), LEN);
  }

  private void checkLen(SingleThreadGroup groupType) {
    int threadCount = groupType.getThreadCount();
    Assert.isTrue((threadCount>0) &&  (0 == (threadCount & (threadCount-1)) ), groupType.getName()+" 单任务线程队列的线程个数必须是2的幂次方倍， 现在是= "+threadCount);
  }

  /**
   * 此处用Disruptor队列实现 单任务线程队列， 可以采用其他方式实现
   */
  private SingleThreadQueue createSingleThreadQueue(int id, String name) {
    return new DisruptorWrapper(id,  name);
  }

  public void addTask(long key, Runnable r) {
     getByMod(key).addTask(r);
  }

  public SingleThreadQueue getByMod(long key) {
    int index = (int) (key & (LEN-1));
    return singleThreadQueues[index];
  }

  public int size() {
    return (int) LEN;
  }

  public void shutdown() {
    for (SingleThreadQueue d : singleThreadQueues) {
      d.shutdown();
    }
  }
}
