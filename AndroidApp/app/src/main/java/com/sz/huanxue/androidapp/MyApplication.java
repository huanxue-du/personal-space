package com.sz.huanxue.androidapp;

import android.app.Application;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.sz.huanxue.androidapp.utils.SoundPoolUtils;
import skin.support.SkinCompatManager;
import skin.support.app.SkinAppCompatViewInflater;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;


/**
 * @author huanxue
 * Created by Administrator on 2019/7/18.
 */
public class MyApplication extends Application {

    public static final String TAG = "MyApplication";
    private final static int OSD_NO_TITLE_ID = -98;   //不带标题的OSD的notification的id
    private static Context mContext;
    static AudioManager.OnAudioFocusChangeListener mListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.i("logcat", "MyApplication-----OnAudioFocusChangeListener:" + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    //更新UI

                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //更新UI

                    //失去焦点如果DT页面不在前台，弹出OSD信息
                    sendBroadForDTWidget();
                    break;
            }
        }
    };
    private static AudioManager mAudioManager;
    private AppBroad mBroad;

    public static Context getInstance() {
        return mContext;
    }

    public static boolean requestAudioFocus() {
        Log.i("logcat", "MyApplication-----requestAudioFocus");
        int result = mAudioManager.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    public static void LossAudioFoucus() {
        Log.i("logcat", "MyApplication-----LossAudioFoucus");
        mAudioManager.abandonAudioFocus(mListener);
    }


    /**
     * system UI interface show no title osd
     *
     * @param context context
     * @param text text
     */
    public static void showNoTitleOsd(Context context, String text) {
        Log.d("logcat", "MyApplication----showNoTitleOsd: text=" + text);
        NotificationManager notifyManager = (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);//实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_launcher_background)//设置小图标,任意内容OSD不关心，但是需要设置。
            .setContentTitle(text + "-&-")  //任意内容OSD不关心,但需要有
            .setContentText(text)   //任意内容OSD不关心,但需要有
            .setTicker(text);
        notifyManager.notify(OSD_NO_TITLE_ID, builder.build());
    }

    /**
     * 被动失去焦点通知小部件更新UI
     */
    private static void sendBroadForDTWidget() {
        Log.i("logcat", "MyApplication---sendBroadForDTWidget----");
        Intent intent = new Intent();
        intent.setAction("action.drivertalk.lossfoucus");
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mBroad = new AppBroad();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.drivertalk");
        intentFilter.addAction("com.hsae.nightpanel.close");
        mContext.registerReceiver(mBroad, intentFilter);
        Log.i("logcat", "MyApplication-----onCreate");
//        mContext.sendBroadcast(new Intent("android.intt.action.STOP_USM_SERVICE"));
        SoundPoolUtils.getInstance(mContext);
        SkinCompatManager.withoutActivity(this)
            .addInflater(new SkinAppCompatViewInflater())           // 基础控件换肤初始化
            .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
            .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
            .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
            .setSkinStatusBarColorEnable(false)                     // 关闭状态栏换肤，默认打开[可选]
            .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
            .loadSkin();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mContext.unregisterReceiver(mBroad);
    }


    private class AppBroad extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if ("action.drivertalk".equals(arg1.getAction())) {
                int foucs = arg1.getExtras().getInt("foucs");
                if (foucs == 0) {
                    LossAudioFoucus();
                } else if (foucs == 1) {
                    requestAudioFocus();
                }
            } else if ("com.hsae.nightpanel.close".equals(arg1.getAction())) {
                boolean poweroff = arg1.getExtras().getBoolean("power_status");
                if (poweroff) {
                    LossAudioFoucus();
                }
            }
        }
    }
}
