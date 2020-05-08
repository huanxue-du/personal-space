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

    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String STOP_SERVICE = "android.intent.action.STOP_USM_SERVICE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("logcat", "BootReceiver----:" + intent.getAction());


    }
}
