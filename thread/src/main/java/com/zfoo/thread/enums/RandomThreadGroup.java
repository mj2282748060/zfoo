package com.zfoo.thread.enums;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *任务随机分给池内线程执行
 */
public enum RandomThreadGroup {
    RANDOM("random"),
    ;

    /**
     * 线程组名字
     */
    private final String name;

    RandomThreadGroup(String name) {
        this.name = name;
    }

    public static RandomThreadGroup takeByOrdinal(int i) {
        RandomThreadGroup[] values = values();
        return i < 0 || i >= values.length ? null : values[i];
    }

    public String getName() {
        return name;
    }

    public ExecutorService instance() {
        int processors = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(processors, processors<<2, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    }
}
