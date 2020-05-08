package com.sz.autolink.javademo;

/**
 * @author huanxue
 * Created by Administrator on 2019/9/16.
 */
public class FreeCalculation {

    public static void main(String[] args) {
        int years = getYears(15, 40);
        System.out.println("need years is = " + years);
    }

    private static int getYears(int num1, int num2) {

        double sum = 3.5;

        for (int i = 0; sum < num2; i++) {
            sum = ((sum * 1.06) + num1);
            System.out.println(sum);
            if (sum >= num2) {
                return i+1;
            }
        }

        return 0;
    }
}
