package com.sz.huanxue.androidapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * @author huanxue
 * Created by Administrator on 2019/5/20.
 */
public class DemoRaduiButton extends CheckBox {
    public DemoRaduiButton(Context context) {
        super(context);
    }

    public DemoRaduiButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DemoRaduiButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DemoRaduiButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public void toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if (!isChecked()) {
            super.toggle();
        }
    }

}
