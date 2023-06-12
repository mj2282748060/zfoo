/*
 * Copyright (C) 2020 The zfoo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.zfoo.thread;
import com.zfoo.thread.enums.RandomThreadGroup;
import com.zfoo.thread.enums.ScheduleThreadGroup;
import com.zfoo.thread.manager.IThreadBalanceExecutor;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author godotg
 * @version 3.0
 */

@Ignore
public class ApplicationTest {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationTest.class);
    int[] test= new int[]{0};

    @Test
    public void startThreadTest() {
        var context = new ClassPathXmlApplicationContext("application.xml");
        IThreadBalanceExecutor businessExecutor = context.getBean(IThreadBalanceExecutor.class);

        Runnable runnable = () -> {
                // 同一线程的一加一减， 最后值不变就证明我们的无锁化保证线程安全好了
            businessExecutor.addTask2Role(66666L, () ->{
                    test[0]++;
                    Thread thread = Thread.currentThread();
                    logger.info("thread>>>>>>>>> id = {}, name = {}, group ={}, data = {}", thread.getId(), thread.getName(), thread.getThreadGroup(), test[0]);
                });
            businessExecutor.addTask2Role(66666L, () ->{
                test[0]--;
                Thread thread = Thread.currentThread();
                logger.info("thread>>>>>>>>> id = {}, name = {}, group ={}, data = {}", thread.getId(), thread.getName(), thread.getThreadGroup(),test[0]);
            });
        };

        Runnable runnable2 = () -> {
            ThreadContext.getIBusinessExecutor().addTask2Login(() -> {
                Thread thread = Thread.currentThread();
                logger.info("thread2>> id = {}, name = {}, group ={}", thread.getId(), thread.getName(), thread.getThreadGroup());
            });
        };

        ScheduledExecutorService s1 = businessExecutor.schedulePool(ScheduleThreadGroup.SCHEDULE);
        ScheduledExecutorService s2 = businessExecutor.schedulePool(ScheduleThreadGroup.SCHEDULE_TEST);

        s1.scheduleAtFixedRate(runnable,0, 20, TimeUnit.MILLISECONDS);
        s2.scheduleAtFixedRate(runnable,1, 18, TimeUnit.MILLISECONDS);
        s1.scheduleAtFixedRate(runnable,2, 15, TimeUnit.MILLISECONDS);
        s1.scheduleAtFixedRate(runnable,3, 12, TimeUnit.MILLISECONDS);
        s2.scheduleAtFixedRate(runnable,4, 8, TimeUnit.MILLISECONDS);
        s1.scheduleAtFixedRate(runnable,5, 5, TimeUnit.MILLISECONDS);
        s2.scheduleAtFixedRate(runnable2,0, 20, TimeUnit.MILLISECONDS);
        s2.scheduleAtFixedRate(runnable2,1, 18, TimeUnit.MILLISECONDS);
        s1.scheduleAtFixedRate(runnable2,2, 15, TimeUnit.MILLISECONDS);
        s2.scheduleAtFixedRate(runnable2,3, 12, TimeUnit.MILLISECONDS);
        s1.scheduleAtFixedRate(runnable2,4, 8, TimeUnit.MILLISECONDS);
        s2.scheduleAtFixedRate(runnable2,5, 5, TimeUnit.MILLISECONDS);

        ExecutorService randomPool =  businessExecutor.randomPool(RandomThreadGroup.RANDOM);
        randomPool.submit(runnable2);
        randomPool.submit(runnable);


        //卡住主线程，不让程序立马结束
        try {
            Thread.sleep(2000);
            System.out.println("sleep ..........");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
