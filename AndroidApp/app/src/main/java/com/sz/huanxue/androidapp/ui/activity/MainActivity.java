package com.sz.huanxue.androidapp.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sz.huanxue.androidapp.R;
import com.sz.huanxue.androidapp.ui.adapter.MainRlvAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 做一个跳转各子模块的功能入口
 *
 * @author huanxue
 * Created by HSAE_DCY on 2019/12/23.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRylMainView;
    private MainRlvAdapter mRlvAdapter;
    private List<Class> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mList = new ArrayList<>();
        mList.add(DataBindingActivity.class);
        mList.add(MainActivity.class);
        mList.add(SecondActivity.class);
        mList.add(DemoActivity.class);
        mList.add(ClockActivity.class);
        mRylMainView = (RecyclerView) findViewById(R.id.ryl_main_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        mRylMainView.setLayoutManager(layoutManager);
        mRlvAdapter = new MainRlvAdapter(this, mList);
        mRylMainView.setAdapter(mRlvAdapter);

    }
}
