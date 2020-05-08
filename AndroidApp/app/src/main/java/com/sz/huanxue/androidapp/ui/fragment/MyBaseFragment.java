package com.sz.huanxue.androidapp.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.sz.huanxue.androidapp.R;
import com.sz.huanxue.androidapp.ui.activity.DemoActivity;
import com.sz.huanxue.androidapp.utils.SharedPrepreferenceUtils;


public class MyBaseFragment extends Fragment implements View.OnClickListener {

    public SharedPrepreferenceUtils mUtils;
    private Dialog mDialog;
    protected DemoActivity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils = SharedPrepreferenceUtils.getInstance();
        activity = (DemoActivity) getActivity();
    }

    /**
     * 弹框
     */
    protected void showDialog(Context context) {
        Button ok_bnt, cancel_bnt;
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.dialog_style);
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View mDialogView = inflater.inflate(R.layout.dialog_reset, null);
        //        mainFreq_tv = (TextView) mDialogView.findViewById(R.id.title);
        //        mainFreq_tv.setText(R.string.dialog_content1);
        ok_bnt = (Button) mDialogView.findViewById(R.id.yes);
        //        delete_bnt.setText(R.string.dialog_yes_btn);
        cancel_bnt = (Button) mDialogView.findViewById(R.id.no);
        //        cancel_bnt.setText(R.string.dialog_no_btn);
        mDialog.setCancelable(true);
        mDialog.setContentView(mDialogView);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = 642;
        lp.height = 339;
        dialogWindow.setAttributes(lp);
        ok_bnt.setOnClickListener(this);
        cancel_bnt.setOnClickListener(this);
        mDialog.show();
    }

    protected void showPopDialog(Context context) {
        Button ok_bnt;
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.dialog_style);
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View mDialogView = inflater.inflate(R.layout.dialog_popup, null);
        ok_bnt = (Button) mDialogView.findViewById(R.id.yes);
        mDialog.setCancelable(true);
        mDialog.setContentView(mDialogView);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = 642;
        lp.height = 339;
        dialogWindow.setAttributes(lp);
        ok_bnt.setOnClickListener(this);
        mDialog.show();
    }

    protected void hideDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onClick(View v) {

    }


    protected void setViewEnable(View view, View textView, boolean enable) {
        view.setEnabled(enable);
        textView.setEnabled(enable);
    }

    protected void setRadioButtonStatus(RadioButton button, TextView textView, boolean enable) {
        button.setClickable(!enable);
        textView.setSelected(enable);
    }


}
