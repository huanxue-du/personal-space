package com.sz.huanxue.androidapp.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.Log;


import java.util.List;

/**
 * created
 *
 * @author Daikin.Da
 */
public class ActivityTaskUtils {

    @SuppressLint("WrongConstant")
    public static boolean moveTaskToFront(Context context, String packageName) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RecentTaskInfo> pis = manager.getRecentTasks(48, 0x0002);
            if (pis != null && pis.size() > 0) {
                for (RecentTaskInfo taskInfo : pis) {
                    if (VERSION.SDK_INT >= VERSION_CODES.M) {
                        if (taskInfo != null && taskInfo.baseActivity != null && TextUtils.equals(taskInfo.baseActivity.getPackageName(), packageName)) {
                            manager.moveTaskToFront(taskInfo.id, 0x00000001);
                            return true;
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }


    @SuppressLint("WrongConstant")
    public static void moveLauncherTaskToFront(Context context) {
        boolean flag = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RecentTaskInfo> pis = manager.getRecentTasks(48, 0x0002);
        if (pis != null && pis.size() > 0) {
            for (RecentTaskInfo taskInfo : pis) {
                if (VERSION.SDK_INT >= VERSION_CODES.M) {
                    if (taskInfo != null && taskInfo.baseActivity != null && TextUtils.equals(taskInfo.baseActivity.getPackageName(), "com.hsae.launcher")) {
                        manager.moveTaskToFront(taskInfo.id, 0x00000001);
                        Log.i("GOODDDDDDDDDDDDDDDDDD", "-----moveLauncherTaskToFront--->");
                        flag = true;
                    }
                }
            }
        }

        if (!flag) {
          /*  if (VersionUtil.isLowVersion()) {
                startLauncherLow(context, 0);
            } else {
                Log.i("GOODDDDDDDDDDDDDDDDDD", "-----moveLauncherTaskToFront--->3");
                startLauncherOnlyChangeTab(context, 0);
            }*/
        }

    }

    public static void startLauncher(Context context, int index) {
       /* if (VersionUtil.isLowVersion()) {
            startLauncherLow(context, 0);
        } else {
            Log.i("GOODDDDDDDDDDDDDDDDDD", "-----moveLauncherTaskToFront--->3");
            startLauncherOnlyChangeTab(context, index);
        }*/
    }

    private static void startLauncherLow(Context context, int index) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN_LAUNCHERSS_low");
            intent.putExtra("index", index);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void startLauncherOnlyChangeTab(Context context, int index) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN_LAUNCHERSS");
            intent.putExtra("index", index);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
