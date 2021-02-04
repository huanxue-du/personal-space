package com.sz.huanxue.androidapp.ui.adapter;

import android.content.Context;

import com.sz.huanxue.androidapp.R;

import java.util.List;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.28.
 */
public class MoreJavaAdapter extends MyBaseRlvAdapter<String> {

    public MoreJavaAdapter(Context context, List<String> data) {
        super(context, data);
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemCount() {
        return 14;
    }

    @Override
    public void callbackViewHolder(BaseHolder holder, int position) {

    }

    @Override
    public int getLayout() {
        return R.layout.item_rlv_common;
    }
}
