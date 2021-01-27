package com.sz.huanxue.androidapp.ui.activity;

import android.os.Bundle;

import com.sz.huanxue.androidapp.R;
import com.sz.huanxue.androidapp.ui.adapter.MainRlvAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 做一个跳转各子模块的功能入口
 *
 * @author huanxue
 * Created by HSAE_DCY on 2019/12/23.
 */
public class MainActivity extends MyBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        List<Class<? extends MyBaseActivity>> mList = new ArrayList<>();
        mList.add(DataBindingActivity.class);
        mList.add(MainActivity.class);
        mList.add(SecondActivity.class);
        mList.add(DemoActivity.class);
        mList.add(ClockActivity.class);
        mList.add(PanelActivity.class);
        mList.add(DataBindingActivity.class);
        mList.add(MainActivity.class);
        mList.add(SecondActivity.class);
        mList.add(DemoActivity.class);
        mList.add(ClockActivity.class);
        RecyclerView mRylMainView = (RecyclerView) findViewById(R.id.ryl_main_view);
//        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRylMainView.setLayoutManager(layoutManager);
        MainRlvAdapter mRlvAdapter = new MainRlvAdapter(this, mList);
        mRylMainView.setAdapter(mRlvAdapter);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
