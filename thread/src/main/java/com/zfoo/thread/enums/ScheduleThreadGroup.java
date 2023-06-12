package com.zfoo.thread.enums;

import com.zfoo.thread.IThreadGroup;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *定时任务
 */
public enum ScheduleThreadGroup implements IThreadGroup {
    SCHEDULE("schedule"),

    SCHEDULE_TEST("schedule_test"),
    ;

    /**
     * 线程组名字
     */
    private final String name;


    ScheduleThreadGroup(String name) {
        this.name = name;
    }

    public static ScheduleThreadGroup takeByOrdinal(int i) {
        ScheduleThreadGroup[] values = values();
        return i < 0 || i >= values.length ? null : values[i];
    }

    public String getName() {
        return name;
    }

    public ScheduledExecutorService instance() {
        return new ScheduledThreadPoolExecutor(1);
    }

    @Override
    public int groupKey() {
        return this.ordinal();
    }

    @Override
    public int groupType() {
        return SCHEDULE_TYPE;
    }
}
