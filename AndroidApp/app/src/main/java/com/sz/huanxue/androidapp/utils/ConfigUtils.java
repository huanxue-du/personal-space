package com.sz.huanxue.androidapp.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.util.Log;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.3.11.
 */
public class ConfigUtils {
    /**
     * 判断是否远程进程
     */
    private boolean isRemotePid(Context context) {
        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am.getRunningAppProcesses() == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == Process.myPid()) {
                if (info.processName.endsWith(":remote")) {
                    Log.i("huanxue", "MyApplication   remote pid: " + info.processName);
                    return true;
                }
            }
        }
        return false;
    }

    public String getVersionName(Context context) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
