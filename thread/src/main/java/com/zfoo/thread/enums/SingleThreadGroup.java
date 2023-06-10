package com.zfoo.thread.enums;

/**
 * 固定的任务key, 分给池内固定线程执行
 */
public enum SingleThreadGroup {
    LOGIN("login",1),

    ROLES("role", Runtime.getRuntime().availableProcessors()<<1),
    ;


    /**
     * 线程组名字
     */
    private final String name;
    /**
     * 线程组内线程数量（为1 或者 2的幂次方倍， 方便取模运算转为位运算）
     */
    private final int threadCount;


    SingleThreadGroup(String name, int threadCount) {
        this.name = name;
        this.threadCount = threadCount;
    }

    public static SingleThreadGroup takeByOrdinal(int i) {
        SingleThreadGroup[] values = values();
        return i < 0 || i >= values.length ? null : values[i];
    }

    public String getName() {
        return name;
    }

    public int getThreadCount() {
        return threadCount;
    }
}
