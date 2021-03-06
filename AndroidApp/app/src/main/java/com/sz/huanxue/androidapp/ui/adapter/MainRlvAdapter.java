package com.sz.huanxue.androidapp.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sz.huanxue.androidapp.R;
import com.sz.huanxue.androidapp.ui.activity.MyBaseActivity;
import com.sz.huanxue.androidapp.utils.ActivityManager;

import java.util.List;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.24.
 */
public class MainRlvAdapter extends MyBaseRlvAdapter<Class<? extends MyBaseActivity>> {

    private static final String TAG = "MainRlvAdapter";
    private Context mContext;

    public MainRlvAdapter(Context context, List<Class<? extends MyBaseActivity>> data) {
        super(context, data);
        mContext = context;
        mData = data;
    }

    @Override
    public void callbackViewHolder(BaseHolder holder, final int position) {
        Log.i("logcat", TAG + "--binding--" + position);
        Button button = (Button) holder.getView(R.id.item_btn_ryl_main);
        button.setText(mData.get(position).getSimpleName());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.startApp(mContext, mData.get(position));
            }
        });
        Log.i("logcat", TAG + "---binding--name:" + button.getText());
    }

    @Override
    public int getLayout() {
        return R.layout.item_ryl_main;
    }
}
