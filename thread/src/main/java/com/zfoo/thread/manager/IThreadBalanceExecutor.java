package com.zfoo.thread.manager;

import com.zfoo.thread.IThreadGroup;
import com.zfoo.thread.enums.RandomThreadGroup;
import com.zfoo.thread.enums.ScheduleThreadGroup;
import com.zfoo.thread.enums.SingleThreadGroup;
import com.zfoo.thread.singleThreadQueue.SingleThreadQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程任务均衡器， 所有的线程资源都从这里获取，方便统一管理
 */
public interface IThreadBalanceExecutor {
  /**
   * 通过定时类型获取对应的定时线程池
   */
  ScheduledExecutorService schedulePool(ScheduleThreadGroup schedule);

  /**
   * 通过随机类型获取对应的随机线程池
   */
  ExecutorService randomPool(RandomThreadGroup randomThreadGroup);

  /**
   * 将任务加入单任务线程任务队列
   */
  void addTask2SingleGroup(SingleThreadGroup group, long key, Runnable runnable);

  /**
   * 获得单任务线程任务队列
   */
  SingleThreadQueue singleThreadQueue(SingleThreadGroup group, long key);

  /**
   * 获得登录单任务线程任务队列
   */
  SingleThreadQueue login();

  /**
   * 获得玩家单任务线程任务队列
   */
  SingleThreadQueue role(long key);

  /**
   * 加入任务到登录单任务线程任务队列
   */
  void addTask2Login(Runnable runnable);

  /**
   * 加入任务到玩家单任务线程任务队列
   */
  void addTask2Role(long roleId, Runnable runnable);

  /**
   * 释放所有线程池
   */
  void  shutdown();

    void execute(IThreadGroup threadGroup, long executorHash, Runnable runnable);
}
