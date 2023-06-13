package com.zfoo.thread;

import com.zfoo.thread.enums.ThreadGroupEnum;

import java.util.concurrent.Executor;

public  interface IThreadExecutor extends Executor {
    /**
     * 线程名字分割符
     */
    String NAME_LIMIT = "_";

    /**
     * 关闭线程池
     */
    void shutdown();

    /**
     * 线程池池中对应执行任务或线程id相等的执行器
     */
    default Executor runThread(long threadId, long key) {return null;}

    /**
     * 线程池对应类型枚举
     */
    ThreadGroupEnum threadGroup();

    }