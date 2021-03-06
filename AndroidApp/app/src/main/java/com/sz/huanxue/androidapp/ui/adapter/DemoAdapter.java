package com.sz.huanxue.androidapp.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.sz.huanxue.androidapp.R;

import java.util.List;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.18.
 */
public class DemoAdapter extends MyBaseRlvAdapter<String> {

    private Context mContext;


    public DemoAdapter(Context context, List<String> data) {
        super(context, data);
        mContext = context;
        mData = data;
    }

    @Override
    public void callbackViewHolder(BaseHolder holder, int position) {

        if (position % 2 == 0) {
            TextView view = (TextView) holder.getView(R.id.ku_usm_list_item_text);
            view.setText(R.string.text_welcome);
        } else {
            TextView view = (TextView) holder.getView(R.id.ku_usm_list_item_text);
            view.setText(mData.get(position));
        }

    }

    @Override
    public int getLayout() {
        return R.layout.simple_list_item_textview_only;
    }

}
