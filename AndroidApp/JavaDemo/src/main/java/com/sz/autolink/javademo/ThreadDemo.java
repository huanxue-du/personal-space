package com.sz.autolink.javademo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.11.
 */
public class ThreadDemo {

    static CountThread mThread1 = new CountThread();
    static ReadNumThread mThread2 = new ReadNumThread();

    public static void main(String[] args) throws InterruptedException{
//        mThread1.start();
//        mThread2.start();
        //实例化站台对象，并为每一个站台取名字
        Station station1 = new Station("线程 1");
        Station station2 = new Station("线程 2");
        Station station3 = new Station("线程 3");
        // 让每一个站台对象各自开始工作
        station1.start();
        station2.start();
        station3.start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Station("线程 4"));
        executor.submit(new Station("线程 5"));
        executor.submit(new Station("线程 6"));
        executor.shutdown();
        //实例化站台对象，并为每一个站台取名字
        Station station11 = new Station("子线程7");
        station11.start();
//        station11.join();
        Station station22 = new Station("子线程8");
        station22.start();
//        station22.join();
        Station station33 = new Station("子线程9");
        // 让每一个站台对象各自开始工作
        station33.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程终于都执行起来啦");
            }
        }).start();

    }

    static class CountThread extends Thread {

        private int num = 0;

        @Override
        public void run() {
            while (num < 10) {
                num++;
                System.out.println("CountThread :" + num);
                try {
                    Thread.sleep(5000);//使线程睡眠的方式
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    static class ReadNumThread extends Thread {

        @Override
        public void run() {
            while (mThread1.num < 10) {
                System.out.println("ReadNumThread Read num:" + mThread1.num);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public static class Station extends Thread {

        // 为了保持票数的一致，票数要静态
        static int tick = 50;
        // 创建一个静态钥匙
        static Object ob = "aa";//值是任意的

        // 通过构造方法给线程名字赋值
        public Station(String name) {
            super(name);// 给线程名字赋值
        }

        // 重写run方法，实现买票操作
        @Override
        public void run() {
            while (tick > 0) {
                synchronized (ob) {// 这个很重要，必须使用一个锁，
                    // 进去的人会把钥匙拿在手上，出来后才把钥匙拿让出来
                    if (tick > 0) {
                        System.out.println(getName() + "卖出了第" + tick + "张票");
                        tick--;
                    } else {
                        System.out.println("票卖完了");
                    }
                }
                try {
                    sleep(100);//休息一秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
