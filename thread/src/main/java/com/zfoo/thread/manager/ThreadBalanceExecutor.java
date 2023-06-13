package com.zfoo.thread.manager;

import com.zfoo.thread.IThreadExecutor;
import com.zfoo.thread.enums.ThreadGroupEnum;
import com.zfoo.thread.randomThread.RandomExecutor;
import com.zfoo.thread.schedule.ScheduleExecutor;
import com.zfoo.thread.singleThreadQueue.SingleThreadQueues;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class ThreadBalanceExecutor implements IThreadBalanceExecutor {
  /**
   * 线程池组
   */
  private final IThreadExecutor[] threadExecutors;

  public ThreadBalanceExecutor() {
    ThreadGroupEnum[] threadGroupEnums = ThreadGroupEnum.values();
    Assert.isTrue(threadGroupEnums.length > 0, "线程池枚举（ThreadGroupEnum）个数必须大于0");
    threadExecutors = new IThreadExecutor[threadGroupEnums.length];
    for (int i = 0; i < threadGroupEnums.length; i++) {
      threadExecutors[i] = threadGroupEnums[i].instance();
    }
  }

  public void shutdown() {
    for (IThreadExecutor threadExecutor : threadExecutors) {
      threadExecutor.shutdown();
    }
  }

  @Override
  public IThreadExecutor role(long key) {
    return threadExecutors[ThreadGroupEnum.ROLES.ordinal()];
  }

  @Override
  public IThreadExecutor login() {
    return threadExecutors[ThreadGroupEnum.LOGIN.ordinal()];
  }

  @Override
  public void addTask2Login(Runnable runnable) {
    if (runnable == null) {
      return ;
    }

    execute(ThreadGroupEnum.LOGIN, 0, runnable);
  }

  @Override
  public void addTask2Role(long roleId, Runnable runnable) {
    if ( roleId < 0 || runnable == null) {
      return;
    }
    execute(ThreadGroupEnum.ROLES, roleId, runnable);
  }


  @Override
  public void execute(ThreadGroupEnum threadGroup, long executorHash, Runnable runnable) {
    if (threadGroup == null || runnable == null) {
      return;
    }
    IThreadExecutor executor = executor(threadGroup);
    if (executor == null) {
      return;
    }
    executor.executeTask(executorHash, runnable);
  }

  @Override
  public IThreadExecutor executor(ThreadGroupEnum threadGroup) {
    if (threadGroup == null) {
      return null;
    }

    return threadExecutors[threadGroup.ordinal()];
  }

  @Override
  public Executor takeExecutor(long currentThreadId, long key) {
    for (IThreadExecutor threadExecutor : threadExecutors) {
      Executor executor = threadExecutor.runThread(currentThreadId, key);
      if ( executor != null) {
        return executor;
      }
    }
    return null;
  }

  @Override
  public ScheduledExecutorService schedulePool(ThreadGroupEnum schedule) {
    if (schedule == null || !schedule.isSchedule()) {
      return null;
    }

    IThreadExecutor executor = threadExecutors[schedule.ordinal()];
    if (!(executor instanceof ScheduleExecutor scheduleExecutor)) {
      return null;
    }

    return scheduleExecutor.getScheduledExecutorService();
  }

  @Override
  public ExecutorService randomPool(ThreadGroupEnum threadGroupEnum) {
    if (threadGroupEnum == null || !threadGroupEnum.isRandom()) {
      return null;
    }

    IThreadExecutor executor = threadExecutors[threadGroupEnum.ordinal()];
    if (!(executor instanceof RandomExecutor randomExecutor)) {
      return null;
    }

    return randomExecutor.getExecutorService();
  }
}
