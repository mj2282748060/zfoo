package com.zfoo.thread.singleThreadQueue;

public interface SingleThreadQueue {

   /**
    * 关闭当任务线程队列
    */
   void shutdown();

   /**
    * 加入任务到任务线程队列
    */
   void addTask(Runnable runnable);
}
