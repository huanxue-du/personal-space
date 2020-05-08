package com.sz.autolink.javademo;

import java.util.Random;

/**
 * @author huanxue
 * Created by Administrator on 2019/8/27.
 */
public class RandomDemo {

    static int x = 0;
    static int y = 0;
    static StringBuffer sBuffer = new StringBuffer();

    public static void main(String[] args) {
        for (int i = 0; i <= 10000; i++) {
            doRandom();
        }
        System.out.println(x);
        System.out.println(y);
    }

    private static void doRandom() {
        sBuffer.append(new Random().nextInt(2));
        System.out.println(sBuffer);
        if (sBuffer.length() >= 3) {
            String string = sBuffer.substring(sBuffer.length() - 3, sBuffer.length());
            System.out.println(string);
            if (string.equals("100")) {
                x += 1;
                sBuffer.setLength(0);
            } else if (string.equals("001")) {
                y += 1;
                sBuffer.setLength(0);
            }
        }


    }
}

