package com.sz.huanxue.androidapp.ui.activity;


import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.sz.huanxue.androidapp.R;

/**
 * @author huanxue
 * Created by Administrator on 2020/4/8.
 */
public class ClockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("logcat", "ClockActivity------onCreate");
        setContentView(R.layout.activity_clock);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("logcat", "ClockActivity------onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("logcat", "ClockActivity------onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("logcat", "ClockActivity------onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("logcat", "ClockActivity------onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("logcat", "SecondActivity------onResume");
    }
}
