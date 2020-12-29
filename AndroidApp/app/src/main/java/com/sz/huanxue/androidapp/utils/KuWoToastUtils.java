package com.sz.huanxue.androidapp.utils;

import android.widget.Toast;


/**
 * 自定义吐司
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.8.13.
 */
public class KuWoToastUtils {

    private static Toast mToast;

  /*  public static void makeText(final String text, final int duration) {
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
                    mToast.show();
                }
            });
        }
    }*/

 /*   public static void makeText(final int stringID, final int duration) {
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
                    mToast.show();
                }
            });
        }
    }*/
}
