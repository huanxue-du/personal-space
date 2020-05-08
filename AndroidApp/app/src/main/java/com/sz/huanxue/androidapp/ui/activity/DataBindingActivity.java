package com.sz.huanxue.androidapp.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.sz.huanxue.androidapp.R;
import com.sz.huanxue.androidapp.ui.datamodule.UserInfo;

/**
 * 练习DataBinding+ConstraintLayout
 *
 * @author huanxue
 * Created by HSAE_DCY on 2019/12/23.
 */
public class DataBindingActivity extends AppCompatActivity {

    private ViewDataBinding mBinding;
    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new UserInfo("hsae", "南山区航盛科技大厦");
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_databind);
    }
}
