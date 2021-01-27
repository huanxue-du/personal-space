package com.sz.huanxue.androidapp.ui.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.sz.huanxue.androidapp.R;
import com.sz.huanxue.androidapp.ui.view.ArcRadioSeekBar;
import com.sz.huanxue.androidapp.ui.view.RightPanel;
import com.sz.huanxue.androidapp.ui.view.ScaleBarView;
import com.sz.huanxue.androidapp.utils.LogUtils;

import java.util.Random;

/**
 * @author huanxue
 * Created by Administrator on 2019/7/16.
 */
public class DemoActivity extends MyBaseActivity implements OnClickListener, Handler.Callback {

    private static final int THREAD_DELAY = 0X000001;
    private static final String TAG = DemoActivity.class.getSimpleName();
    private final Handler mHandler = new Handler(this);
    private Button mButton;
    private Button mButton2;
    private Button mButton3;
    private Button mButtonShow;
    private ProgressBar mPbSearching;
    private RadioButton mRbButton;
    private CheckBox mCbButton;
    private ArcRadioSeekBar mArcRadioSeekBar;
    private int pro = 0;
    private ScaleBarView mScaleBarViewRH5;
    private RightPanel mRightPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("logcat", "DemoActivity------onCreate");
        setContentView(R.layout.activity_demo);
        initView();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("logcat", "DemoActivity------onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("logcat", "DemoActivity------onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("logcat", "DemoActivity------onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("logcat", "DemoActivity------onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("logcat", "DemoActivity------onResume");
    }

    private void initView() {
        mButton = (Button) findViewById(R.id.button);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mPbSearching = (ProgressBar) findViewById(R.id.pb_searching);
//        mRbButton = (RadioButton) findViewById(R.id.rb_button);
//        mCbButton = (CheckBox) findViewById(R.id.cb_button);
        mButtonShow = findViewById(R.id.button_show);
        mArcRadioSeekBar = findViewById(R.id.seekbar_arc);
        mScaleBarViewRH5 = findViewById(R.id.scaleView);
        mButton.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButtonShow.setOnClickListener(this);

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_show:
                pro = 0;
                mHandler.sendEmptyMessage(1);
                break;
            case R.id.button:
                break;
            case R.id.button2:
                int fm = new Random().nextInt() * (10800 - 8750 + 1) + 8750;
                mScaleBarViewRH5.initViewParam(fm, ScaleBarView.MOD_TYPE_FM);
                LogUtils.d(TAG + " initViewParam  MOD_TYPE_FM:" + fm);
                break;
            case R.id.button3:
                int am = new Random().nextInt() * (1602 - 531 + 1) + 531;
                mScaleBarViewRH5.initViewParam(am, ScaleBarView.MOD_TYPE_AM);
                LogUtils.d(TAG + "  initViewParam  MOD_TYPE_AM:" + am);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                LogUtils.d(TAG + "  handleMessage  setProgress:" + pro);
                mArcRadioSeekBar.setProgress(pro);
                pro = pro + 10;
                if (pro > mArcRadioSeekBar.getMax()) {
                    return false;
                }
                mHandler.sendEmptyMessageDelayed(1, 500);
                break;
            case 2:
                break;
        }

        return false;
    }
}
