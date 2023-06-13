package com.zfoo.thread.randomThread;

import com.zfoo.thread.IThreadExecutor;
import com.zfoo.thread.enums.ThreadGroupEnum;

import java.util.concurrent.ExecutorService;

public class RandomExecutor implements IThreadExecutor {
    private final ExecutorService executorService;
    private final ThreadGroupEnum randomThreadGroup;

    public RandomExecutor(ThreadGroupEnum randomThreadGroup, ExecutorService executorService) {
        this.randomThreadGroup = randomThreadGroup;
        this.executorService = executorService;
    }


    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public ThreadGroupEnum threadGroup() {
        return randomThreadGroup;
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            return;
        }
        executorService.execute(command);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
