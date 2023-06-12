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
package com.zfoo.event.manager;

import com.zfoo.event.model.anno.Bus;
import com.zfoo.event.model.event.IEvent;
import com.zfoo.event.model.vo.IEventReceiver;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.collection.concurrent.CopyOnWriteHashMapLongObject;
import com.zfoo.thread.IThreadGroup;
import com.zfoo.thread.ThreadContext;
import com.zfoo.thread.manager.IThreadBalanceExecutor;
import com.zfoo.util.SafeRunnable;
import com.zfoo.util.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author godotg
 * @version 3.0
 */
public abstract class EventBus {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    private static final CopyOnWriteHashMapLongObject<ExecutorService> threadMap = new CopyOnWriteHashMapLongObject<>();
    /**
     * event mapping
     */
    private static final Map<Class<? extends IEvent>, List<IEventReceiver>> receiverMap = new HashMap<>();

    /**
     * Publish the event
     */
    public static void post(IEvent event) {
        if (event == null) {
            return;
        }
        var clazz = event.getClass();
        var receivers = receiverMap.get(clazz);
        if (CollectionUtils.isEmpty(receivers)) {
            return;
        }

        for (var receiver : receivers) {
            switch (receiver.bus()) {
                case CURRENT_THREAD:
                    doReceiver(receiver, event);
                    break;
                case VirtualThread:
                    logger.error("waiting for java 21 virtual thread");
                    break;
                default:
                    execute(receiver.bus(), event.executorHash(), () -> doReceiver(receiver, event));
                    break;
            }
        }
    }

    private static void doReceiver(IEventReceiver receiver, IEvent event) {
        try {
            receiver.invoke(event);
        } catch (Exception e) {
            logger.error("eventBus {} [{}] unknown exception", receiver.bus(), event.getClass().getSimpleName(), e);
        } catch (Throwable t) {
            logger.error("eventBus {} [{}] unknown error", receiver.bus(), event.getClass().getSimpleName(), t);
        }
    }


    public static void asyncExecute(Runnable runnable) {
        execute(Bus.RANDOM_THREAD, RandomUtils.randomInt(), runnable);
    }

    /**
     * Use the event thread specified by the hashcode to execute the task
     */
    public static void execute(Bus bus, long executorHash, Runnable runnable) {
        IThreadGroup threadGroup = bus.getThreadGroup();
        if (threadGroup == null) {
            logger.error("事件异步执行错误， 线程组不能为空 ，busName= {}", bus.name());
            return;
        }
        ThreadContext.getIBusinessExecutor().execute(threadGroup, executorHash, runnable);
    }

    /**
     * Register the event and its counterpart observer
     */
    public static void registerEventReceiver(Class<? extends IEvent> eventType, IEventReceiver receiver) {
        receiverMap.computeIfAbsent(eventType, it -> new ArrayList<>(1)).add(receiver);
    }

    public static Executor threadExecutor(long currentThreadId) {
//        todo 这里有问题， net那边也要改
        return threadMap.getPrimitive(currentThreadId);
    }

    public static void sortReceiver() {
        for (List<IEventReceiver> receiverList : receiverMap.values()) {
            receiverList.sort(Comparator.comparingInt(IEventReceiver::order));
        }
    }
}


