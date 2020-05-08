package com.autolink.radio55.widget;

import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autolink.radio55.R;
import com.autolink.radio55.app.AppDataUtils;
import com.autolink.radio55.utils.ELog;
import com.autolink.radio55.utils.RadioDataUtils;
import com.autolink.refutils.ViewBaseManager;
import com.autolink.refutils.ViewOptManagerEnum;

public class ObjViewAvn extends ViewBaseManager implements View.OnClickListener, View.OnLongClickListener {
    /**
     * AvnView与RadioMainView之间通信只能用广播
     */
    public static final String ACTION_LONG_PRE = "action.long.pre";
    public static final String ACTION_LONG_NEXT = "action.long.next";
    public static final String ACTION_AVN = "action.avn";
    public static final String ACTION_START_RADIO = "com.autolink.radio.aidl.service.start";//开机是否启动收音机
    private static final String FOCUS_Radio = "com.autolink.media.mvc.data.bean.MediaPlayerRadio";
    private RelativeLayout widget_show_mainfreq, widget_avn;
    private TextView widget_mainfreq, widget_mainfreq_mkz, widget_enter_radio;
    private Context mContext;
    private RWidgetBroad mRWidgetBroad;
    private boolean mIsOnRadio = false;//
    private Handler mAvnHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    public View onCreateView(Application application, Context context, Context contexts, Object... objects) {
        super.onCreateView(application, context, contexts, objects);
        ViewGroup widgetViewGroup;
        ImageButton widget_button_pre, widget_button_next;
        try {
            mContext = context.createPackageContext("com.autolink.radio55", Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppDataUtils.getInstance().setRegisterMvc(context, application);
        AppDataUtils.getInstance().setUiHandle(mAvnHandler);


        widgetViewGroup = (ViewGroup) View.inflate(mContext, R.layout.radio_view_widget, null);
        widget_avn = (RelativeLayout) widgetViewGroup.findViewById(R.id.widget_avn);
        widget_show_mainfreq = (RelativeLayout) widgetViewGroup.findViewById(R.id.widget_show_mainfreq);
        widget_button_pre = (ImageButton) widgetViewGroup.findViewById(R.id.widget_button_pre);
        widget_button_next = (ImageButton) widgetViewGroup.findViewById(R.id.widget_button_next);
        widget_mainfreq = (TextView) widgetViewGroup.findViewById(R.id.widget_mainfreq);
        widget_mainfreq_mkz = (TextView) widgetViewGroup.findViewById(R.id.widget_mainfreq_mkz);
        widget_enter_radio = (TextView) widgetViewGroup.findViewById(R.id.widget_enter_radio);

        widget_avn.setOnClickListener(this);
        widget_button_pre.setOnClickListener(this);
        widget_button_next.setOnClickListener(this);
        widget_button_pre.setOnLongClickListener(this);
        widget_button_next.setOnLongClickListener(this);

        mRWidgetBroad = new RWidgetBroad();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppDataUtils.ON_RADIO_ACTION);
        intentFilter.addAction(AppDataUtils.ACTION_UPDATE_LANGUAGE);
        intentFilter.addAction(ACTION_START_RADIO);
        intentFilter.addAction(ACTION_AVN);
        this.mContext.registerReceiver(mRWidgetBroad, intentFilter);


        return widgetViewGroup;
    }

    @Override
    public void onActivityContext(Context context, Dialog dialog) {
    }

    @Override
    public void onViewOptMethod(int i, Object... objects) {
        switch (i) {
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_FIRST:
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_DESTROY:
                onDestroy();
                ELog.i("onDestroy");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_RESUME:
                onResume();
                ELog.i("onResume");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_PAUSE:
                onPause();
                ELog.i("onPause");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_STOP:
                onStop();
                ELog.i("onStop");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_FOCUS:
                onFocus();
                ELog.i("onFocus");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_LOSEFOCUS:
                onLoseFocus();
                ELog.i("onLoseFocus");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_START:
                onStart();
                ELog.i("onStart");
            default:
                break;
        }
    }

    private void onLoseFocus() {
    }

    private void onFocus() {
    }

    private void onStart() {
        if (isAudioFocus(FOCUS_Radio)) {
            setAvnShow();
        }
    }

    private void onResume() {
        AppDataUtils.getInstance().isShowingAvn = true;
        setAVNShowing(AppDataUtils.getInstance().getBand(), AppDataUtils.getInstance().getMainFreq());
    }

    private void onPause() {
        AppDataUtils.getInstance().isShowingAvn = false;
    }

    private void onStop() {
    }

    private void onDestroy() {
        this.mContext.unregisterReceiver(mRWidgetBroad);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.widget_avn:
                Intent intents = new Intent("com.autolink.media.main_activity");
                intents.putExtra("name", "radio");
                intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intents);
                break;
            case R.id.widget_button_pre:
                doRadioShow();
                if (mIsOnRadio) {
                    AppDataUtils.getInstance().setSingleStepPre();
                }
                break;
            case R.id.widget_button_next:
                doRadioShow();
                if (mIsOnRadio) {
                    AppDataUtils.getInstance().setSingleStepNext();
                }

                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {//跨进程调用，只能用广播
        if (!mIsOnRadio) {
            doRadioShow();
            return true;
        }
        Intent intent;
        switch (v.getId()) {
            case R.id.widget_button_pre:
                intent = new Intent(ACTION_LONG_PRE);
                this.mContext.sendBroadcast(intent);
                break;
            case R.id.widget_button_next:
                intent = new Intent(ACTION_LONG_NEXT);
                this.mContext.sendBroadcast(intent);
                break;
        }
        return true;

    }

    /**
     * 进入收音机
     */
    private void doRadioShow() {
        if (!mIsOnRadio) {
            Intent intents = new Intent("com.autolink.media.main_activity");
            intents.putExtra("name", "radio");
            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intents);
        }
    }

    /**
     * 设置AVN的显示
     *
     * @param band 波段值0FM或1AM
     * @param freq 频点值
     */
    public void setAVNShowing(int band, int freq) {
        String str = RadioDataUtils.getFregp(freq, band);
        if (widget_mainfreq != null && widget_mainfreq_mkz != null) {
            if (freq < 0) {
                widget_mainfreq.setText("");
            } else {
                widget_mainfreq.setText(str);
            }

            if (band == 0) {
                widget_mainfreq_mkz.setText(RadioDataUtils.FM_MHZ);
            } else if (band == 1) {
                widget_mainfreq_mkz.setText(RadioDataUtils.AM_KHZ);
            } else {
                widget_mainfreq_mkz.setText("");
            }
        }

    }

    private void setAvnShow() {
        widget_show_mainfreq.setVisibility(View.VISIBLE);
        widget_enter_radio.setVisibility(View.INVISIBLE);

    }

    private boolean isAudioFocus(String tag) {
        AudioManager audiomanage = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);

        return audiomanage.getCurrentClientId().contains(tag);//使用framework扩展API方法
    }

    private class RWidgetBroad extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (AppDataUtils.ON_RADIO_ACTION.equals(arg1.getAction())) {
                boolean isOnRadio = arg1.getBooleanExtra(AppDataUtils.ON_RADIO_BOOLEAN, false);
                // callBackWidgetStatus(isOnRadio);接收广播后手动调用
                if (isOnRadio) {
                    mIsOnRadio = true;
                    setAvnShow();
                } else {
                    mIsOnRadio = false;
                    widget_enter_radio.setVisibility(View.VISIBLE);
                    widget_show_mainfreq.setVisibility(View.INVISIBLE);
                }

            } else if (AppDataUtils.ACTION_UPDATE_LANGUAGE.equals(arg1.getAction())) {
                widget_enter_radio.setText(R.string.radio_avn_main);

            } else if (ObjViewAvn.ACTION_START_RADIO.equals(arg1.getAction())) {
                setAvnShow();
            } else if (ObjViewAvn.ACTION_AVN.equals(arg1.getAction())) {
                int freq = arg1.getExtras().getInt("freq");
                int band = arg1.getExtras().getInt("band");
                // 调用封装方法，AVN显示,使用hangdler
                setAVNShowing(band, freq);
            }


        }
    }
}

