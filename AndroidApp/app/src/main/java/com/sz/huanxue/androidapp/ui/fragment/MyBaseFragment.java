package com.sz.huanxue.androidapp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.18.
 */
public class MyBaseFragment extends Fragment implements View.OnClickListener {



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
