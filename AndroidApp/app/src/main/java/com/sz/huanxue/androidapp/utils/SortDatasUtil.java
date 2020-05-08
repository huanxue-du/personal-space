package com.sz.huanxue.androidapp.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
//待封装成适应性更广泛的形式
public class SortDatasUtil {

	public static void sotrDatas(LinkedList<Integer> mLinkedList) {

		try {
			if (mLinkedList != null) {
				Collections.sort(mLinkedList, new Comparator<Integer>() {

					@Override
					public int compare(Integer lhs, Integer rhs) {
						if (lhs> rhs) {
							return 1;
						}
						return -1;
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
