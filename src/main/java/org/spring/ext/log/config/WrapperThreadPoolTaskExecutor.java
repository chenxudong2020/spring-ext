package org.spring.ext.log.config;
import org.slf4j.MDC;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public final class WrapperThreadPoolTaskExecutor extends org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor {
    public WrapperThreadPoolTaskExecutor() {
        super();
    }

    @Override
    public void execute(Runnable task) {
        super.execute(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }


    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }
}