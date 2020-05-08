package com.sz.autolink.javademo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.18.
 */
public class Demo implements Cloneable {


    static Demo sInstance;

    public static void main(String[] args) {
        B b = new B();
        b.setDemo();
    }

    public synchronized Demo getInstance() {
        if (sInstance == null) {
            sInstance = new Demo();
        }
        return sInstance;
    }

    static class B extends Demo {

        private void setDemo() {
            List<Object> list = new ArrayList<>();

            list.add(new Demo());
            list.add("b");
            list.add(new Demo());
            for (Object object : list) {
                if (object instanceof Cloneable) {
                    System.out.println("1111111111");
                } else {
                    System.out.println("22222222222");
                }
            }
        }
    }
}
