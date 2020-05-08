package com.autolink.radio55.adapter;

/**
 * @author huanxue
 *         Created by Administrator on 2017/11/17.
 */

public interface CallBackColl {

    void CallBackSelectionIndex(int position,int adapter);

    void CallBackOnclickItem(int frep, int band);

    void CallBackDeleteFreq(String   freq);

    void CallBackReplaceColl(RadioEntity radioEntity);
}
