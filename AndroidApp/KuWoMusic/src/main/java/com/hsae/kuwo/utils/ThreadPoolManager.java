package com.hsae.kuwo.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    private static ThreadPoolManager sInstance;
    private int corePoolSize;//核心线程池的数量，同时能够执行的线程数量
    private int maximumPoolSize;//最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
    private long keepAliveTime = 30;//存活时间
    private TimeUnit unit = TimeUnit.MINUTES;
    private ThreadPoolExecutor executor;

    public ThreadPoolManager() {
        //当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        maximumPoolSize = corePoolSize; //maximumPoolSize用不到，但是需要赋值，否则报错
        executor = new ThreadPoolExecutor(corePoolSize, //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
            maximumPoolSize, keepAliveTime, //表示的是maximumPoolSize当中等待任务的存活时间
            unit, new LinkedBlockingQueue<Runnable>(), //缓冲队列，用于存放等待任务，Linked的先进先出
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy() //用来对超出maximumPoolSize的任务的处理策略
        );
    }

    public static ThreadPoolManager getInstance() {
        if (null == sInstance) {
            sInstance = new ThreadPoolManager();
        }
        return sInstance;
    }

    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        executor.execute(runnable);
    }

    /**
     * 线程池中移除
     */
    public void remove(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        executor.remove(runnable);
    }
}
