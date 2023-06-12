package com.zfoo.thread.manager;

import com.zfoo.thread.IThreadGroup;
import com.zfoo.thread.enums.RandomThreadGroup;
import com.zfoo.thread.enums.ScheduleThreadGroup;
import com.zfoo.thread.enums.SingleThreadGroup;
import com.zfoo.thread.singleThreadQueue.SingleThreadQueue;
import com.zfoo.thread.singleThreadQueue.SingleThreadQueues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class ThreadBalanceExecutor implements IThreadBalanceExecutor {
  private static final Logger logger = LogManager.getLogger(ThreadBalanceExecutor.class);
  /**
   * 单任务线程池组
   */
  private  SingleThreadQueues[] singleGroups;

  /**
   *随机线程池组
   */
  private  ExecutorService[] randomGroups;

  /**
   *定时任务线程池组
   */
  private  ScheduledExecutorService[] scheduledGroups;

  public ThreadBalanceExecutor() {
    SingleThreadGroup[] groupTypes = SingleThreadGroup.values();
    if (groupTypes.length > 0) {
      singleGroups = new SingleThreadQueues[groupTypes.length];
      for (int i = 0; i < groupTypes.length; i++) {
        singleGroups[i] = new SingleThreadQueues(groupTypes[i]);
      }
      logger.info("创建{}个单任务线程池完成", groupTypes.length);
    }

    RandomThreadGroup[] randomThreadGroups = RandomThreadGroup.values();
    if (randomThreadGroups.length > 0) {
      randomGroups = new ExecutorService[randomThreadGroups.length];
      for (int i = 0; i < randomThreadGroups.length; i++) {
        randomGroups[i] = randomThreadGroups[i].instance();
      }
      logger.info("创建{}个随机任务线程池完成", groupTypes.length);
    }

    ScheduleThreadGroup[] scheduleThreadGroups = ScheduleThreadGroup.values();
    if (scheduleThreadGroups.length > 0) {
      scheduledGroups = new ScheduledExecutorService[scheduleThreadGroups.length];
      for (int i = 0; i < scheduleThreadGroups.length; i++) {
        scheduledGroups[i] = scheduleThreadGroups[i].instance();
      }
      logger.info("创建{}个定时线程池完成", groupTypes.length);
    }
  }

  public void shutdown() {
    if (scheduledGroups != null) {
      for (ScheduledExecutorService scheduledGroup : scheduledGroups) {
        scheduledGroup.shutdown();
      }
    }

    if (randomGroups != null) {
      for (ExecutorService randomGroup : randomGroups) {
        randomGroup.shutdown();
      }
    }

    if (singleGroups != null) {
      for (SingleThreadQueues singleThreadQueues : singleGroups) {
        singleThreadQueues.shutdown();
      }
    }
  }

  @Override
  public SingleThreadQueue role(long key) {
    if (scheduledGroups == null) {
      return null;
    }

    return singleGroups[SingleThreadGroup.ROLES.ordinal()].getByMod(key);
  }

  @Override
  public SingleThreadQueue login() {
    if (scheduledGroups == null) {
      return null;
    }

    return singleGroups[SingleThreadGroup.LOGIN.ordinal()].getByMod(0);
  }

  @Override
  public void addTask2Login(Runnable runnable) {
    if (scheduledGroups == null || runnable == null) {
      return ;
    }

    singleGroups[SingleThreadGroup.LOGIN.ordinal()].getByMod(0).addTask(runnable);
  }

  @Override
  public void addTask2Role(long roleId, Runnable runnable) {
    if (scheduledGroups == null || roleId < 0 || runnable == null) {
      return;
    }

    singleGroups[SingleThreadGroup.ROLES.ordinal()].addTask(roleId, runnable);
  }

  @Override
  public void addTask2SingleGroup(SingleThreadGroup group, long key, Runnable runnable) {
    if (singleGroups == null || group == null || key < 0) {
      return ;
    }

    singleGroups[group.ordinal()].getByMod(key).addTask(runnable);
  }

  @Override
  public SingleThreadQueue singleThreadQueue(SingleThreadGroup group, long key) {
    if (singleGroups == null || group == null) {
      return null;
    }
    return  singleGroups[group.ordinal()].getByMod(key);
  }

  @Override
  public ScheduledExecutorService schedulePool(ScheduleThreadGroup schedule) {
    if (scheduledGroups == null || schedule == null) {
      return null;
    }

    return scheduledGroups[schedule.ordinal()];
  }

  @Override
  public ExecutorService randomPool(RandomThreadGroup randomThreadGroup) {
    if (randomGroups == null || randomThreadGroup == null) {
      return null;
    }

    return randomGroups[randomThreadGroup.ordinal()];
  }

  @Override
  public void execute(IThreadGroup threadGroup, long executorHash, Runnable runnable) {
      if (threadGroup == null) {
        return;
      }

     if (threadGroup instanceof SingleThreadGroup) {
       addTask2SingleGroup((SingleThreadGroup) threadGroup, executorHash, runnable);
     }else if (threadGroup instanceof RandomThreadGroup) {
       randomPool((RandomThreadGroup) threadGroup).execute(runnable);
     }else if (threadGroup instanceof ScheduleThreadGroup) {
       schedulePool((ScheduleThreadGroup) threadGroup).execute(runnable);
    }
  }
}
