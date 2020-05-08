package com.sz.huanxue.androidapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;


/**
 * 开机自启动
 *
 * @author huanxue
 * Created by Administrator on 2019.10.22.
 */
public class UsmService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
