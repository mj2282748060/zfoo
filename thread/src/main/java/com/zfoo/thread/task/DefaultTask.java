package com.zfoo.thread.task;

public class DefaultTask {
    private  Runnable runnable;

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public void run() {
        runnable.run();
    }

    public void clear() {
        runnable = null;
    }
}
