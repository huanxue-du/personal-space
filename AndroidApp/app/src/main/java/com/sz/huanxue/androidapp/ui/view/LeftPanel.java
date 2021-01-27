package com.sz.huanxue.androidapp.ui.view;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author huanxue
 * Created by Administrator on 2017/5/17.
 */

public class LeftPanel extends LinearLayout {

    /**
     * 每次自动展开/收缩的范围
     */
    private final static int SPEED = 20;
    private int MAX_WIDTH = 0;
    private Context mContext;
    private OnPanelStatusChangedListener onPanelStatusChangedListener;

    public LeftPanel(Context context, int width, int height) {
        super(context);
        this.mContext = context;
        //设置Panel本身的属性
        LayoutParams lp = new LayoutParams(width, height);
        lp.leftMargin = -lp.width;
        MAX_WIDTH = Math.abs(lp.leftMargin);
        this.setLayoutParams(lp);
    }

    /**
     * @param context
     * @param width
     * @param height
     * @param bindView
     * @param contentView
     */
    public LeftPanel(Context context, int width, int height, View bindView, View contentView) {
        this(context, width, height);
        setBindView(bindView);
        setContentView(contentView);
    }

    /**
     * 把View放在Panel中
     *
     * @param v
     */
    public void setContentView(View v) {
        this.addView(v);
    }

    /**
     * 绑定触发动画的View
     *
     * @param bindView
     */
    public void setBindView(View bindView) {
        bindView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LayoutParams lp = (LayoutParams) getLayoutParams();
                if (lp.leftMargin < 0)// CLOSE的状态
                    new AsynMove().execute(new Integer[]{SPEED});// 正数展开
                else if (lp.leftMargin >= 0)// OPEN的状态
                    new AsynMove().execute(new Integer[]{-SPEED});// 负数收缩
            }

        });
    }

    public void setOnPanelStatusChangedListener(OnPanelStatusChangedListener onPanelStatusChangedListener) {
        this.onPanelStatusChangedListener = onPanelStatusChangedListener;
    }

    public interface OnPanelStatusChangedListener {
        void onPanelOpened(LeftPanel panel);

        void onPanelClosed(LeftPanel panel);
    }

    class AsynMove extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int times;
            if (MAX_WIDTH % Math.abs(params[0]) == 0)// 整除
                times = MAX_WIDTH / Math.abs(params[0]);
            else
                times = MAX_WIDTH / Math.abs(params[0]) + 1;// 有余数

            for (int i = 0; i < times; i++) {
                publishProgress(params);
                try {
                    Thread.sleep(Math.abs(params[0]));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {
            LayoutParams lp = (LayoutParams) getLayoutParams();
            if (params[0] < 0) {//关闭
                lp.leftMargin = Math.max(lp.leftMargin + params[0], -MAX_WIDTH);
            } else {//打开
                lp.leftMargin = Math.min(lp.leftMargin + params[0], MAX_WIDTH);
            }
            if (lp.leftMargin == 0 && onPanelStatusChangedListener != null) {//展开之后
                onPanelStatusChangedListener.onPanelOpened(LeftPanel.this);//调用OPEN回调函数
            } else if (lp.leftMargin == -MAX_WIDTH && onPanelStatusChangedListener != null) {//收缩之后
                onPanelStatusChangedListener.onPanelClosed(LeftPanel.this);//调用CLOSE回调函数
            }
            setLayoutParams(lp);
        }
    }
}
