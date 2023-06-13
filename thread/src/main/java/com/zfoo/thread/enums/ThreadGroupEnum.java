package com.zfoo.thread.enums;

import com.zfoo.thread.IThreadExecutor;
import com.zfoo.thread.randomThread.RandomExecutor;
import com.zfoo.thread.schedule.ScheduleExecutor;
import com.zfoo.thread.singleThreadQueue.SingleThreadQueues;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池类型枚举， 此处定义后在ThreadBalanceExecutor中自动创建
 */
public enum ThreadGroupEnum {
    LOGIN("login",1, ThreadGroupEnum.SINGLE_TYPE),

    ROLES("role", 16 , ThreadGroupEnum.SINGLE_TYPE),

    RANDOM("random", Runtime.getRuntime().availableProcessors()<<1, ThreadGroupEnum.RANDOM_TYPE){
        @Override
        public IThreadExecutor instance() {
            ThreadFactory threadFactory = (r)-> new Thread(new ThreadGroup(this.getName()), r,this.getName());
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(getThreadCount()>>1, getThreadCount(), 10L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), threadFactory);
            return new RandomExecutor(this, threadPoolExecutor);
        }
    },

    SCHEDULE("schedule",1, ThreadGroupEnum.SCHEDULE_TYPE){
        @Override
        public IThreadExecutor instance() {
            ScheduleExecutor scheduleExecutor = new ScheduleExecutor(this);
            //必须使用scheduleExecutor返回的线程工厂
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(getThreadCount(), scheduleExecutor.getThreadFactory());
            scheduleExecutor.setScheduledExecutorService(scheduledThreadPoolExecutor);
            return scheduleExecutor;
        }
    },

    SCHEDULE_TEST("schedule_test",1, ThreadGroupEnum.SCHEDULE_TYPE){
        @Override
        public IThreadExecutor instance() {
            ScheduleExecutor scheduleExecutor = new ScheduleExecutor(this);
            //必须使用scheduleExecutor返回的线程工厂
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(getThreadCount(), scheduleExecutor.getThreadFactory());
            scheduleExecutor.setScheduledExecutorService(scheduledThreadPoolExecutor);
            return scheduleExecutor;
        }
    },
    ;



    ThreadGroupEnum(String name, int threadCount, int groupType) {
        this.name = name;
        this.threadCount = threadCount;
        this.groupType = groupType;
    }

    public static ThreadGroupEnum takeByOrdinal(int index) {
        ThreadGroupEnum[] values = values();
        return index < 0 || index >= values.length ? null : values[index];
    }


    public String getName() {
        return name;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public final int groupType() {
        return groupType;
    }

    public IThreadExecutor instance() {
        return new SingleThreadQueues(this);
    }

    public boolean isSingle() {
        return ThreadGroupEnum.SINGLE_TYPE == groupType;
    }

    public boolean isSchedule() {
        return ThreadGroupEnum.SCHEDULE_TYPE == groupType;
    }

    public boolean isRandom() {
        return ThreadGroupEnum.RANDOM_TYPE == groupType;
    }


    /** ***************************************************属性 start********************
     * 线程组类型-单任务
     */
    public final static  int SINGLE_TYPE = 0;
    /**
     * 线程组类型-随机任务
     */
    public final static int RANDOM_TYPE = 1;
    /**
     * 线程组类型-定时任务
     */
    public final static int SCHEDULE_TYPE = 2;

    /**
     * 线程组名字
     */
    private final String name;
    /**
     * 线程组内线程数量（单任务线程池时为1 或者 2的幂次方倍， 方便取模运算转为位运算）
     */
    private final int threadCount;
    /**
     * 线程组类型
     */
    private final int groupType;
    /**
     * *******************************************************属性 end ***********************
     */
}
