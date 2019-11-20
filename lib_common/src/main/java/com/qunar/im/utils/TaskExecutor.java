package com.qunar.im.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lihaibin.li on 2017/11/22.
 */

public class TaskExecutor {
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 4, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<Runnable>(12),
            new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    return new Thread(r, "qunar_sub_thread");
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static <T> Future<T> submit(Callable<T> task) {
        return threadPoolExecutor.submit(task);
    }
}
