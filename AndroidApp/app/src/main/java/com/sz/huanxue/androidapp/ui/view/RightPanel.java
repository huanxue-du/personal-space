package com.sz.huanxue.androidapp.ui.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
*右侧滑出滑入布局
  * @author huanxue Created by Administrator on 2017/5/17.
 */

public class RightPanel extends LinearLayout {

	/**
	 * 每次自动展开/收缩的范围
	 */
	private final static int SPEED = 20;
	private int MAX_WIDTH = 0;
	private Context mContext;

	/**
	 * viewBeside自动布局以适应Panel展开/收缩的空间变化
	 */
	public RightPanel(Context context, View viewBeside, int width, int height) {
		super(context);
		this.mContext = context;

		// 必须改变Panel左侧组件的weight属性
		LayoutParams p = (LayoutParams) viewBeside.getLayoutParams();
		p.weight = 1;// 支持挤压
		viewBeside.setLayoutParams(p);

		// 设置Panel本身的属性
		LayoutParams lp = new LayoutParams(width, height);
		lp.rightMargin = -lp.width;
		MAX_WIDTH = Math.abs(lp.rightMargin);
		this.setLayoutParams(lp);

	}

	public RightPanel(Context context, int width, int height) {
		super(context);
		this.mContext = context;
		// 设置Panel本身的属性
		LayoutParams lp = new LayoutParams(width, height);
		lp.leftMargin = -lp.width;
		MAX_WIDTH = Math.abs(lp.leftMargin);
		this.setLayoutParams(lp);
	}

	/**
	 * @param context
	 * @param viewBeside
	 * @param width
	 * @param height
	 * @param bindView
	 * @param contentView
	 */
	public RightPanel(Context context, View viewBeside, int width, int height, View bindView, View contentView) {
		this(context, viewBeside, width, height);
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
				Log.i("dcy.614", "setOnClickListener-------true");
				LayoutParams lp = (LayoutParams) getLayoutParams();
				if (lp.rightMargin < 0) {// CLOSE的状态
					Log.i("dcy.614", "setBindView------onClick------lp.rightMargin < 0----111");
					new AsynMove().execute(new Integer[] { SPEED });// 正数展开
				} else if (lp.rightMargin >= 0) {// OPEN的状态
					Log.i("dcy.614", "setBindView------onClick------lp.rightMargin > 0---222");
					new AsynMove().execute(new Integer[] { -SPEED });// 负数收缩
				}
			}

		});
	}

	public class AsynMove extends AsyncTask<Integer, Integer, Void> {

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
			if (params[0] < 0)
				lp.rightMargin = Math.max(lp.rightMargin + params[0], -MAX_WIDTH);
			else
				lp.rightMargin = Math.min(lp.rightMargin + params[0], 0);

			if (lp.rightMargin == 0 && onPanelStatusChangedListener != null) {// 展开之后
				onPanelStatusChangedListener.onPanelOpened(RightPanel.this);// 调用OPEN回调函数
			} else if (lp.rightMargin == -MAX_WIDTH && onPanelStatusChangedListener != null) {// 收缩之后
				onPanelStatusChangedListener.onPanelClosed(RightPanel.this);// 调用CLOSE回调函数
			}
			setLayoutParams(lp);
		}
	}

	public interface OnPanelStatusChangedListener {
		void onPanelOpened(RightPanel panel);

		void onPanelClosed(RightPanel panel);
	}

	private OnPanelStatusChangedListener onPanelStatusChangedListener;

	public void setOnPanelStatusChangedListener(OnPanelStatusChangedListener onPanelStatusChangedListener) {
		this.onPanelStatusChangedListener = onPanelStatusChangedListener;
	}

}
