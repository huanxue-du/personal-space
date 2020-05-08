package com.sz.autolink.javademo;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.3.17.
 */
public class ProcessDemo {

    public static void main(String[] args) {
        setPro(5, 9);
    }

    private static void setPro(int pro, int num) {
        switch (num) {
            case 9:
                if (pro < 10) {
                    System.out.println("111111111111");
                } else if (pro < 20) {
                    System.out.println("2222222");
                } else if (pro < 30) {
                    System.out.println("333333333");
                } else if (pro < 40) {
                    System.out.println("44444444");
                } else if (pro < 50) {
                    System.out.println("555555555");
                } else if (pro < 60) {
                    System.out.println("66666666");
                } else if (pro < 70) {
                    System.out.println("77777777");
                } else if (pro < 80) {
                    System.out.println("88888888");
                } else if (pro < 90) {
                    System.out.println("9999999999");
                } else if (pro <= 100) {
                    System.out.println("101010101010");
                }else if (pro<5){
                    System.out.println("5!5!5!5!5!5!5!");
                }
                break;
            case 8:
                break;
        }

    }

}
