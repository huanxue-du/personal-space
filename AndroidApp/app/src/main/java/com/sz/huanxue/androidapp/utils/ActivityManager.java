package com.sz.huanxue.androidapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * created
 *
 * @author Daikin.Da
 */
public class ActivityManager implements Thread.UncaughtExceptionHandler, Application.ActivityLifecycleCallbacks {

    private List<Activity> mActivities;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context context;
    private Activity mCurrActivity;
    private boolean isBackgroud = false;

    public Context getContext() {
        return context;
    }

    private static final class ActivityManagerHolder {
        private static final ActivityManager INSTANCE = new ActivityManager();
    }

    private ActivityManager() {
        mActivities = new ArrayList<Activity>();
    }

    public static ActivityManager self() {
        return ActivityManagerHolder.INSTANCE;
    }

    public Activity getmCurrActivity() {
        if (mCurrActivity == null) {
            return topActivity();
        }
        return mCurrActivity;
    }


    public void init(Application context) {
        this.context = context.getApplicationContext();
        context.registerActivityLifecycleCallbacks(this);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public boolean isBackgroud() {
        return isBackgroud;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
//       mDefaultHandler.uncaughtException(t,e);
//        Log.i("GOOD_DA","--------------------Crash start--------------------------------------->");
//        e.printStackTrace();
//        Log.i("GOOD_DA","--------------------Crash end--------------------------------------->");
        exitApp();

    }


    private void add(Activity activity) {
        final List<Activity> activities = mActivities;
        if (activity == null || activities.contains(activity)) {
            return;
        }
        activities.add(activity);
    }

    /**
     * 顶部Activity
     *
     * @return
     */
    public Activity topActivity() {
        if (mActivities != null && mActivities.size() > 0) {
            return mActivities.get(mActivities.size() - 1);
        }
        return null;
    }

    private void remove(Activity activity) {
        mActivities.remove(activity);
    }

    public void exitApp() {
        exitApp(null, true);
    }

    public void exitAppWithoutShutdown() {
        exitApp(null, false);
    }

    public void exitApp(Context context, boolean flag) {

        for (Activity activity : mActivities) {
            activity.finish();
        }
        if (flag) {
            System.exit(0);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCurrActivity = activity;
        //是否从后台切换至前台；
        boolean isFrontFromBack = isBackgroud;
        isBackgroud = false;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mCurrActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        isBackgroud = !isForeground();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        remove(activity);
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    private boolean isForeground() {
        android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<android.app.ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            android.app.ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
            if (topAppProcess != null && topAppProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : topAppProcess.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        return true;
                    }
                }
            }
        } else {
            List localList = manager.getRunningTasks(1);
            android.app.ActivityManager.RunningTaskInfo taskInfo = (android.app.ActivityManager.RunningTaskInfo) localList.get(0);
            if (taskInfo != null) {
                ComponentName componentName = taskInfo.topActivity;
                String packageName = componentName.getPackageName();
                if (!TextUtils.isEmpty(packageName) && packageName.equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

}
