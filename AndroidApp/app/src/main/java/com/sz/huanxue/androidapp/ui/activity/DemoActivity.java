package com.sz.huanxue.androidapp.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import com.sz.huanxue.androidapp.R;

/**
 *
 * @author huanxue
 * Created by Administrator on 2019/7/16.
 */
public class DemoActivity extends AppCompatActivity implements OnClickListener {

    private static final int THREAD_DELAY = 0X000001;
    volatile Thread1 mThread1 = new Thread1();
    Thread2 mThread2 = new Thread2();
    Handler mHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case THREAD_DELAY:
                    mThread1.run();
                    mThread2.run();
                    mHandler.sendEmptyMessageDelayed(THREAD_DELAY, 5000);
                    break;
            }
            return false;
        }
    });
    private Button mButton;
    private ProgressBar mPbSearching;
    private RadioButton mRbButton;
    private CheckBox mCbButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("logcat", "DemoActivity------onCreate");
        setContentView(R.layout.activity_demo);
//        mHandler.sendEmptyMessage(THREAD_DELAY);


        initView();

        findViewById(R.id.button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, SecondActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.button3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        mPbSearching = (ProgressBar) findViewById(R.id.pb_searching);
//        mRbButton = (RadioButton) findViewById(R.id.rb_button);
//        mCbButton = (CheckBox) findViewById(R.id.cb_button);

        mButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:

                break;
        }
    }

    public class Thread1 extends Thread {

        private int mCount = 0;

        public int getCount() {
            return mCount;
        }

        @Override
        public void run() {

            mCount++;
            System.out.println();
            Log.i("logcat", "Thread1 :" + mCount);
            if (mCount >= 10) {
                return;
            }
        }
    }

    public class Thread2 extends Thread {

        @Override
        public void run() {
            Log.i("logcat", "Thread2 :" + mThread1.getCount());
        }
    }
}
