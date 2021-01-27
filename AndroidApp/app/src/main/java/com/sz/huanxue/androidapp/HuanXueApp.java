package com.sz.huanxue.androidapp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

import com.sz.huanxue.androidapp.data.local.DataStoreUtils;
import com.sz.huanxue.androidapp.data.local.SPUtils;
import com.sz.huanxue.androidapp.data.local.SharedPrepreferenceUtils;
import com.sz.huanxue.androidapp.data.remote.RetrofitManager;
import com.sz.huanxue.androidapp.utils.LogUtils;

import skin.support.SkinCompatManager;
import skin.support.app.SkinAppCompatViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.12.29
 */
public class HuanXueApp extends Application {

    public static final String TAG = HuanXueApp.class.getSimpleName();
    private final static Handler mAppHandler = new Handler();
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public static Handler getMainThreadHandler() {
        return mAppHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG + "  onCreate ");
        sContext = getApplicationContext();
        if (!isRemotePid()) {
            initSkip();
            RetrofitManager.self().init(new RetrofitManager.Config()
                    .setConnectTimeout(10).setReadTimeout(40)
                    .setWriteTimeout(70)
                    .setBaseUrl("https://fawivi-gw-public-uat.faw.cn:63443"));
        }
        SharedPrepreferenceUtils.getInstance().setBooleanSharedPreferences("11", true);
        SPUtils.INSTANCE.setBooleanSharedPreferences("12", false);
        DataStoreUtils.INSTANCE.putSyncData("13", "HuanXueApp");
        DataStoreUtils.INSTANCE.putSyncData("14", 1008611);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            LogUtils.d(TAG + "   onTrimMemory    APP遁入后台: ");
        }
    }

    /**
     * 判断是否远程进程
     */
    private boolean isRemotePid() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am.getRunningAppProcesses() == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == Process.myPid()) {
                if (info.processName.endsWith(":remote")) {
                    LogUtils.d(TAG + "   remote pid: " + info.processName);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 初始化换肤功能模块
     */
    private void initSkip() {
        SkinCompatManager.withoutActivity(this).addInflater(new SkinAppCompatViewInflater())           // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
                .addInflater(new SkinAppCompatViewInflater())           // SkinAppCompatView 控件换肤初始化
//            .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .setSkinStatusBarColorEnable(false)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
    }
}
