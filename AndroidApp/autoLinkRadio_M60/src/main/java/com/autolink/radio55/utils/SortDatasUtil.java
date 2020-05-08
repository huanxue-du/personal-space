package com.autolink.radio55.utils;

import com.autolink.radio55.adapter.RadioEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class SortDatasUtil {

    public static void sotrDatas(LinkedList<RadioEntity> mLinkedList) {
        if (mLinkedList == null) {
            return;
        }
        Collections.sort(mLinkedList, new Comparator<RadioEntity>() {

            @Override
            public int compare(RadioEntity lhs, RadioEntity rhs) {
                if (Integer.parseInt(lhs.getFrequency()) > Integer.parseInt(rhs.getFrequency())) {
                    return 1;
                }
                return -1;
            }
        });
    }
}
