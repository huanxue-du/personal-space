package com.sz.huanxue.androidapp.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.sz.huanxue.androidapp.R;

import java.lang.ref.SoftReference;

/**
 * @author huanxue  全局弹框
 * Created by HSAE_DCY on 2019.11.14.
 */
public class DialogUtils {

    public static final String TAG = "DialogUtils";
    private static final int MSG_UPDATE_DRIVE_MODE_CHANGE_STATUS = 0X000002;
    private static final int MSG_UPDATE_POP_DISMISS = 0X000003;
    private static final int MSG_UPDATE_DIALOG_SHOW = 0X000008;
    private static SoftReference<DialogUtils> mInstances;
    private Context mContext;
    private Dialog mDialog;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_DRIVE_MODE_CHANGE_STATUS:
                    break;
                case MSG_UPDATE_POP_DISMISS:
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    break;
                case MSG_UPDATE_DIALOG_SHOW:
                    if (!mDialog.isShowing()) {
                        mDialog.show();
                    }
                    break;
            }
            return false;
        }
    });
    /**
     * 弹框确定按钮响应事件
     */
    private OnDialogSureListener mSureListener;

    public DialogUtils(Context context) {
        this.mContext = context;
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.dialog_style);
        }
    }

    public static DialogUtils getInstance(Context context) {
        if (mInstances == null || mInstances.get() == null) {
            DialogUtils instance = new DialogUtils(context);
            mInstances = new SoftReference<DialogUtils>(instance);
        }
        return mInstances.get();
    }

    public void showAllWindowDialogView(byte state) {
        Log.i("logcat", TAG + "----showAllWindowDialogView:" + state);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mDialogView = inflater.inflate(R.layout.pop_driver_mode_comfort, null);
        mDialog.setCancelable(true);
        mDialog.setContentView(mDialogView);
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = 679;
        lp.height = 512;
        dialogWindow.setAttributes(lp);


        mDialogView.findViewById(R.id.pop_iv_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        mHandler.removeMessages(MSG_UPDATE_POP_DISMISS);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_POP_DISMISS, 5000);
    }

    /**
     * 清空历史记录弹框
     */
    public void showDeleteDialog(final OnDialogSureListener listener) {
        Log.i("huanxue", TAG + "----showDeleteDialog:");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mDialogView = inflater.inflate(R.layout.activity_demo, null);
        mDialog.setCancelable(true);
        mDialog.setContentView(mDialogView);
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = 543;
        lp.height = 225;
        dialogWindow.setAttributes(lp);
        if (listener != null) {
            mSureListener = listener;
        }

        mDialogView.findViewById(R.id.btn_no).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        mDialogView.findViewById(R.id.btn_yes).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (mSureListener != null) {
                    mSureListener.onSure();
                }
            }
        });

        mHandler.sendEmptyMessage(MSG_UPDATE_DIALOG_SHOW);
    }

    public interface OnDialogSureListener {

        void onSure();
    }


}
