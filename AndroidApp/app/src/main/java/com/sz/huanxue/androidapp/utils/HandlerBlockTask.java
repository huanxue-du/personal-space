package com.sz.huanxue.androidapp.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

/**
 * UI卡顿检测的实现
 * 思路:
 * 想要监控线上用户UI线程的卡顿，也就是要把UI线程中的耗时逻辑找出来，然后进行优化开发。那么我们如何如做呢？
 * Android中的应用程序是消息驱动的，也就是UI线程执行的所有操作，通常都会经过消息机制来进行传递（也就是Handler通信机制）。
 * Handler的handleMessage负责在UI线程中处理UI相关逻辑，如果我们能在handleMessage执行之前和handleMessage执行之后，分别插入一段我们的日志代码，不就可以实现UI任务执行时间的监控了吗？
 * <p>
 * 具体实现方式:
 * 我们使用了一个工作线程mBlockThread来监控UI线程的卡顿。
 * 每次Looper的loop方法对消息进行处理之前，我们添加一个定时监控器。
 * 如果UI线程中的消息处理时间小于我们设定的阈值BLOCK_TMME，则取消已添加的定时器。
 * 当UI线程执行耗时任务，超过我们设定的阈值时，就会执行mBlockRunnable这个Rnnable，在它的run方法中，打印出主线程卡顿时的代码堆栈。
 * 我们把堆栈日志收集起来，进行归类分析，就可以定位到产生卡顿问题的具体代码行号了。
 *
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.7.
 */
public class HandlerBlockTask {
    private final static String TAG = HandlerBlockTask.class.getSimpleName();
    public final int BLOCK_TMME = 1000;
    private final HandlerThread mBlockThread = new HandlerThread("blockThread");
    private final Runnable mBlockRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement s : stackTrace) {
                sb.append(s.toString()).append("\n");
            }
            Log.d(TAG, sb.toString());
        }
    };
    private Handler mHandler;

    /**
     * 调用该方法即可启动检测UI卡顿的异步任务
     */
    public void startWork() {
        mBlockThread.start();
        mHandler = new Handler(mBlockThread.getLooper());
        Looper.getMainLooper().setMessageLogging(new Printer() {
            private static final String START = ">>>>> Dispatching";
            private static final String END = "<<<<< Finished";

            @Override
            public void println(String x) {
                if (x.startsWith(START)) {
                    startMonitor();
                }
                if (x.startsWith(END)) {
                    removeMonitor();
                }
            }
        });
    }

    private void startMonitor() {
        mHandler.postDelayed(mBlockRunnable, BLOCK_TMME);
    }

    private void removeMonitor() {
        mHandler.removeCallbacks(mBlockRunnable);
    }
}
