
package com.zfoo.thread;

import com.zfoo.thread.manager.IThreadBalanceExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * 线程资源上下文
 */

@Component
public class ThreadContext implements ApplicationContextAware, ApplicationListener {
    private static final Logger logger = LoggerFactory.getLogger(ThreadContext.class);
    private static ThreadContext instance;
    private static IThreadBalanceExecutor threadBalanceExecutor;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        instance = this;
        instance.applicationContext = applicationContext;
        threadBalanceExecutor = applicationContext.getBean(IThreadBalanceExecutor.class);
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextClosedEvent) {
            threadBalanceExecutor.shutdown();
            logger.info("ThreadContext shutdown successfully");
        }
    }

    public static ThreadContext getThreadContext() {
        return instance;
    }

    public static ApplicationContext getApplicationContext() {
        return instance.applicationContext;
    }

    public static IThreadBalanceExecutor getIBusinessExecutor() {
        return threadBalanceExecutor;
    }
}
