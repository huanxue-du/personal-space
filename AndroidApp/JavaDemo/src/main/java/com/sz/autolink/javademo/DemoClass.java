package com.sz.autolink.javademo;

import java.util.ArrayList;

/**
 * @author huanxue
 * Created by Administrator on 2019/7/16.
 */

public class DemoClass {

    private static int sumNum = 44444;

    public static void main(String[] args) {

        //        RadioEntity entity1 = new RadioEntity();
        //        entity1.setFreq(87.5f);
        //        RadioEntity entity2 = new RadioEntity();
        //        entity2.setFreq(87.5f);
        //        if (entity1 == entity2) {
        //            System.out.println("entity1  is   entity2  true");
        //        }else {
        //            System.out.println("not is one ");
        //        }
        //
        //        if (entity1.getFreq() == entity2.getFreq()) {
        //            System.out.println("float is true  two is one");
        //        }

//        double myMoney = getMoney(5, 5, 8);
//        System.out.println("myMoney-------:" + myMoney);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(999);
        list.add(999);
        list.add(999);
        list.add(sumNum);
        list.set(0, 11111111);
        list.set(1, 222222);
        list.set(2, 333333333);
        sumNum = 888;
        list.add(sumNum);
//        getListData(list);
        for (int i : list) {
            System.out.println(i);
        }
    }

    private static double getMoney(double initMoney, int year, int addMoney) {
        double sumNum = initMoney;
        for (int i = 0; i < year; i++) {
            sumNum = sumNum * 1.10 + addMoney;
        }

        return sumNum;
    }

    private static void getListData(ArrayList<Integer> arrayList) {
        System.out.println("arrayList  size  :" + arrayList.size());
        for (int i : arrayList) {
            System.out.println(i);
        }
        arrayList.clear();
        if (arrayList == null) {
            System.out.println("arrayList  is   null");
            System.out.println(arrayList.size());
        } else {
            System.out.println("arrayList  is   clear  but   not  null");
            System.out.println("arrayList  size  :" + arrayList.size());
        }
        arrayList.add(999);
        arrayList.add(999);
        arrayList.set(0, 66666);

        for (int i : arrayList) {
            System.out.println(i);
        }
        System.out.println("arrayList  size  :" + arrayList.size());

    }


}