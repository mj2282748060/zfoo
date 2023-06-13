package com.zfoo.thread.schedule;

import com.zfoo.thread.IThreadExecutor;
import com.zfoo.thread.enums.ThreadGroupEnum;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class ScheduleExecutor implements IThreadExecutor {
    private  ScheduledExecutorService scheduledExecutorService;
    private final ThreadGroupEnum scheduleThreadGroup;
    private  Thread thread ;
    private  ThreadFactory threadFactory ;

    public ScheduleExecutor(ThreadGroupEnum scheduleThreadGroup) {
        this.scheduleThreadGroup = scheduleThreadGroup;
    }


    @Override
    public void shutdown() {
        thread = null;
        scheduledExecutorService.shutdown();
    }

    @Override
    public ThreadGroupEnum threadGroup() {
        return scheduleThreadGroup;
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            return;
        }
        scheduledExecutorService.execute(command);
    }

    @Override
    public Executor runThread(long threadId, long key) {
        return threadId == thread.getId() ? scheduledExecutorService : null;
    }

    public ThreadFactory getThreadFactory() {
        if (threadFactory == null) {
            threadFactory = (r) ->{
                Thread t = new Thread(new ThreadGroup(scheduleThreadGroup.getName()), r,scheduleThreadGroup.getName());
                thread = t;
                return t;
            };
        }
        return threadFactory;
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
