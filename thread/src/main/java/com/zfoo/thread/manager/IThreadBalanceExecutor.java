package com.zfoo.thread.manager;

import com.zfoo.thread.IThreadExecutor;
import com.zfoo.thread.enums.ThreadGroupEnum;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程任务均衡器， 所有的线程资源都从这里获取，方便统一管理
 */
public interface IThreadBalanceExecutor {
  /**
   * 获得登录单任务线程任务队列
   */
  IThreadExecutor login();

  /**
   * 获得玩家单任务线程任务队列
   */
  IThreadExecutor role(long key);

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

  /**
   * 将任务放入threadGroup对应的线程池， executorHash取模单列线程组值去对应线程执行
   */
  void execute(ThreadGroupEnum threadGroup, long executorHash, Runnable runnable);

  /**
   * threadGroup对应的线程池
   */
  IThreadExecutor executor(ThreadGroupEnum threadGroup);

  /**
   * 入threadGroup对应的线程池， executorHash取模单列线程执行器
   */
  Executor takeExecutor(long currentThreadId, long key);

  /**
   * threadGroup对应的定时线程池
   */
  ScheduledExecutorService schedulePool(ThreadGroupEnum schedule);

  /**
   * threadGroup对应的随机线程池
   */
  ExecutorService randomPool(ThreadGroupEnum random);
}
