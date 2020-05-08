package com.autolink.radio55.adapter;

/**
 * @author huanxue
 *         Created by Administrator on 2017/11/17.
 */

public interface CallBackAll {
    void CallBackCollFreq(String  freq);

    void CallBackSelectionIndex(int position,int adapter);

    void CallBackOnclickItem(int freq, int band);
}
