package com.sz.huanxue.androidapp.ui.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.sz.huanxue.androidapp.R;
import skin.support.SkinCompatManager;

/**
 * @author huanxue
 * Created by Administrator on 2019/7/16.
 */
public class SecondActivity extends AppCompatActivity implements OnClickListener {


    private Button mBtnStyle1;
    private Button mBtnStyle2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("logcat", "SecondActivity------onCreate");
        setContentView(R.layout.activity_second);
        initView();


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("logcat", "SecondActivity------onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("logcat", "SecondActivity------onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("logcat", "SecondActivity------onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("logcat", "SecondActivity------onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("logcat", "SecondActivity------onResume");
    }


    private void initView() {
        mBtnStyle1 = (Button) findViewById(R.id.btn_style1);
        mBtnStyle2 = (Button) findViewById(R.id.btn_style2);

        mBtnStyle1.setOnClickListener(this);
        mBtnStyle2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_style1:
                Log.i("logcat", "SecondActivity-----style111111");
                SkinCompatManager.getInstance().restoreDefaultTheme();
                break;
            case R.id.btn_style2:
                Log.i("logcat", "SecondActivity-----style2222222");
                SkinCompatManager.getInstance().loadSkin("two", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
//                SkinCompatUserThemeManager.get().addColorState(R.color.colorPrimary, "#38F803");
                break;
        }
    }
}
