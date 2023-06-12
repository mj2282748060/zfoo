package com.zfoo.thread;

public  interface IThreadGroup {
         int SINGLE_TYPE = 0;
         int RANDOM_TYPE = 1;
         int SCHEDULE_TYPE = 2;
    
         int groupType();
         int groupKey();
    }