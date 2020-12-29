package com.sz.huanxue.androidapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * @author huanxue
 * Created by Administrator on 2019.10.22.
 */
public class BootReceiver extends BroadcastReceiver {
    /**
     * 开机启动广播
     */
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("logcat", "BootReceiver----:" + intent.getAction());


    }
}
