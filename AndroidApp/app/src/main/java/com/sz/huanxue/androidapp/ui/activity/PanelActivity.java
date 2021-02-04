package com.sz.huanxue.androidapp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sz.huanxue.androidapp.R;
import com.sz.huanxue.androidapp.ui.view.RightPanel;
import com.sz.huanxue.androidapp.utils.ThemeUtils;

import androidx.annotation.Nullable;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.27.
 */
public class PanelActivity extends MyBaseActivity {
    private Button mButton;
    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        mButton = findViewById(R.id.btn_switch);
        mView = findViewById(R.id.panel_main);
        //不显示侧滑内的布局，暂未分析出原因
        RightPanel rightPanel = new RightPanel(this, mView, 200, 300);
        rightPanel.setContentView(this.getLayoutInflater().inflate(R.layout.layout_rightpanel, null));
        rightPanel.setBindView(mButton);
        if (ThemeUtils.INSTANCE.getChangingTheme() == 1) {
            ThemeUtils.INSTANCE.registerSkinCompleteListener(new ThemeUtils.SkinCompleteListener() {
                @Override
                public void onSuccess() {

                }
            });
        }
    }
}
