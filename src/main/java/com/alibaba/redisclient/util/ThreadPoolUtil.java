package com.alibaba.redisclient.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {
    private ExecutorService executorService;

    public ThreadPoolUtil(int numThreads) {
        executorService = Executors.newFixedThreadPool(numThreads);
    }

    public void execute(Runnable task) {
        executorService.execute(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}