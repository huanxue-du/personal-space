package com.hsae.kuwo.utils;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.hsae.kuwo.R;

/**
 * 自定义吐司
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.8.13.
 */
public class KuWoToastUtils {

    private static Toast mToast;

    public static void makeText(final String text, final int duration) {
        Handler handler = KuWoMemoryData.getInstance().getHandler();
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = new Toast(KuWoMemoryData.getInstance().getContext());
                    View view = View.inflate(KuWoMemoryData.getInstance().getContext(), R.layout.layout_mytoast, null);
                    TextView textView = view.findViewById(R.id.toast_text);
                    textView.setText(text);
                    mToast.setView(view);
                    mToast.setDuration(duration);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    resetKuWoToastTheme(KuWoMemoryData.getInstance().getContext(), textView);
                    mToast.show();
                }
            });
        }
    }

    public static void makeText(final int stringID, final int duration) {
        Handler handler = KuWoMemoryData.getInstance().getHandler();
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = new Toast(KuWoMemoryData.getInstance().getContext());
                    View view = View.inflate(KuWoMemoryData.getInstance().getContext(), R.layout.layout_mytoast, null);
                    TextView textView = view.findViewById(R.id.toast_text);
                    textView.setText(KuWoMemoryData.getInstance().getContext().getString(stringID));
                    mToast.setView(view);
                    mToast.setDuration(duration);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    resetKuWoToastTheme(KuWoMemoryData.getInstance().getContext(), textView);
                    mToast.show();
                }
            });
        }
    }

    /**
     * 弹出提示前判断当前主题，更换对应背景显示
     */
    private static void resetKuWoToastTheme(Context context, TextView textView) {
        String themeTag = Settings.System.getString(context.getContentResolver(), "themeTag");
        Log.i("huanxue", "KuWoToastUtils-----resetKuWoToastTheme----:" + themeTag);
        if (TextUtils.isEmpty(themeTag)) {
            textView.setBackground(context.getResources().getDrawable(R.mipmap.toast_bg));
            return;
        }
        switch (themeTag) {
            case "1":
                textView.setBackground(context.getResources().getDrawable(R.mipmap.toast_bg_technology));
                break;
            case "2":
                textView.setBackground(context.getResources().getDrawable(R.mipmap.toast_bg_sport));
                break;
            default:
                textView.setBackground(context.getResources().getDrawable(R.mipmap.toast_bg));
                break;
        }
    }
}
