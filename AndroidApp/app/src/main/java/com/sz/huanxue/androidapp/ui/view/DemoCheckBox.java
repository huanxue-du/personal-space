package com.sz.huanxue.androidapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * @author huanxue
 * Created by Administrator on 2019/5/20.
 */
public class DemoCheckBox extends RadioButton  {
    private boolean mChecked = false;

    public DemoCheckBox(Context context) {
        super(context);
    }

    public DemoCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DemoCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DemoCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * {@inheritDoc}
     * <p>
     * If the radio button is already checked, this method will not toggle the radio button.
     */
    @Override
    public void toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        setChecked(!mChecked);
        mChecked = !mChecked;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return RadioButton.class.getName();
    }


}
