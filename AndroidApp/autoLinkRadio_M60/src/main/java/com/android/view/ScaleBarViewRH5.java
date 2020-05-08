package com.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.autolink.radio55.R;
import com.autolink.radio55.app.AppDataUtils;
import com.autolink.radio55.view.RadioMainView;

/**
 * @author hzz-Tony
 * @version create2017
 */
@SuppressLint("Recycle")
public class ScaleBarViewRH5 extends View {

	public static final int MOD_TYPE_FM = 5;
	public static final int MOD_TYPE_AM = 9;
	public static final int MOD_TYPE_SPILT_NUM = 5;
	public static final int MOD_TYPE_FM_INT = 10;
	public static final int MOD_TYPE_AM_INT = 9;
	private static final int ITEM_DIVIDER = 25;
	private static final int ITEM_MAX_HEIGHT = 40;
	private static final int ITEM_MIN_HEIGHT = 20;
	private static final int TEXT_SIZE = 20;
	private static final int TEXT_MAX_SIZE = 28;
	int maxTextColor = Color.parseColor("#FDE43A");
	int minTextColor = Color.parseColor("#DDDDDD");
	int lineColor = Color.parseColor("#DDDDDD");

	private float mDensity;
	private int mValue = 8750, mMaxValue = Integer.MAX_VALUE, mModType = MOD_TYPE_FM, mLineDivider = ITEM_DIVIDER;
	private int minValue = 8750;
	private int maxValue = 10800;
	private static final int LINE_MINDL_COLOR = Color.RED;
	private int mLastX, mMove;
	private int mWidth, mHeight;

	private int mMinVelocity;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private CurrentChangeListener mListener;

	@SuppressWarnings("deprecation")
	public ScaleBarViewRH5(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(getContext());
		mDensity = getContext().getResources().getDisplayMetrics().density;

		mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

	}

	private GradientDrawable createBackground() {
		float strokeWidth = 4 * mDensity; 
		float roundRadius = 6 * mDensity; 
		int strokeColor = Color.parseColor("#FF666666");

		

		setPadding((int) strokeWidth, (int) strokeWidth, (int) strokeWidth, 0);

		int colors[] = { 0xFF999999, 0xFFFFFFFF, 0xFF999999 };
		GradientDrawable bgDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
		bgDrawable.setCornerRadius(roundRadius);
		bgDrawable.setStroke((int) strokeWidth, strokeColor);
		return bgDrawable;
	}

	public void initViewParam(int defaultValue, int model) {
		switch (model) {
		case MOD_TYPE_AM:
			mModType = MOD_TYPE_AM;
			mLineDivider = ITEM_DIVIDER;
			mValue = defaultValue;
			this.maxValue = 1602;
			this.minValue = 531;
			break;
		case MOD_TYPE_FM:
			mModType = MOD_TYPE_FM;
			mLineDivider = ITEM_DIVIDER;
			mValue = defaultValue;
			this.maxValue = 10800;
			this.minValue = 8750;
			break;

		default:
			break;
		}
		invalidate();

		mLastX = 0;
		mMove = 0;
		notifyValueChange();
	}

	public void setCurrentChangeListener(CurrentChangeListener listener) {
		mListener = listener;
	}

	public float getValue() {
		return mValue;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		mWidth = getWidth();
		mHeight = getHeight();
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawScaleLine(canvas);
		// drawMiddleLine(canvas);
	}

	private void drawWheel(Canvas canvas) {
		Drawable wheel = getResources().getDrawable(R.drawable.ic_launcher);
		wheel.setBounds(0, 0, getWidth(), getHeight());
		wheel.draw(canvas);
	}

	private float textWidth, textHeight;

	private void drawScaleLine(Canvas canvas) {
		canvas.save();

		if (mValue < minValue) {
			mValue = maxValue;
		} else if (mValue > maxValue) {
			mValue = minValue;
		}
		int doType = 10;
		switch (mModType) {
		case MOD_TYPE_FM:
			doType = MOD_TYPE_FM_INT;
			break;
		case MOD_TYPE_AM:
			doType = MOD_TYPE_AM_INT;
			break;

		default:
			break;
		}

		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(2);
		linePaint.setColor(lineColor);

		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(TEXT_SIZE * mDensity);
		textPaint.setColor(minTextColor);

		int width = mWidth, drawCount = 0;
		int xPosition = 0, textWidth = (int) Layout.getDesiredWidth("0", textPaint);
		textHeight = textWidth;
		int size = (int) ((4 * width) / (2 * mLineDivider * mDensity));
		for (int i = 0; drawCount <= 4 * width; i++) {
			if (i > 20) {
				break;
			}
			xPosition = (int) ((width / 2 - mMove) + i * mLineDivider * mDensity);
			if (xPosition + getPaddingRight() < mWidth) {
				if (((doType == MOD_TYPE_FM_INT ? ((mValue / MOD_TYPE_FM_INT) + i) : (mValue + i * MOD_TYPE_AM_INT)))
						% MOD_TYPE_SPILT_NUM == (doType == MOD_TYPE_FM_INT ? 0 : 1)) {
					canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * ITEM_MAX_HEIGHT,
							linePaint);
					if (mValue + i <= mMaxValue) {
						if (xPosition == (mWidth / 2)) {
							textPaint.setTextSize(TEXT_MAX_SIZE * mDensity);
							textPaint.setColor(maxTextColor);
							float textWidths = Layout.getDesiredWidth("0", textPaint);
							canvas.drawText(changLeftNumber((mValue + i * doType)),
									countLeftStart(mValue + i, xPosition, textWidths), 3 * textWidth, textPaint);
						} else {
							textPaint.setColor(minTextColor);
							textPaint.setTextSize(TEXT_SIZE * mDensity);
							textPaint.setAlpha(255 - (((255) / size) * i + i * 6));
							canvas.drawText(changLeftNumber((mValue + i * doType)),
									countLeftStart(mValue + i, xPosition, textWidth), 3 * textWidth, textPaint);
						}
					}
				} else {
					linePaint.setAlpha(255 - (((255) / size) * i + i * 6));
					canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * ITEM_MIN_HEIGHT,
							linePaint);
				}
			}

			xPosition = (int) ((width / 2 - mMove) - i * mLineDivider * mDensity);
			if (xPosition > getPaddingLeft()) {
				if (((doType == MOD_TYPE_FM_INT ? ((mValue / MOD_TYPE_FM_INT) - i) : (mValue - i * MOD_TYPE_AM_INT)))
						% MOD_TYPE_SPILT_NUM == (doType == MOD_TYPE_FM_INT ? 0 : 1)) {
					canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * ITEM_MAX_HEIGHT,
							linePaint);

					if (mValue - i * MOD_TYPE_AM_INT >= 0) {
						if (xPosition != (mWidth / 2)) {
							textPaint.setAlpha(255 - (((255) / size) * i + i * 6));
							canvas.drawText(changLeftNumber((mValue - i * doType)),
									countLeftStart(mValue - i, xPosition, textWidth), 3 * textWidth, textPaint);
						}
					}
				} else {
					linePaint.setAlpha(255 - (((255) / size) * i + i * 6));
					canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * ITEM_MIN_HEIGHT,
							linePaint);
				}
			}

			drawCount += 2 * mLineDivider * mDensity;
		}

		canvas.restore();
	}

	/**
	 * 根据不同的位置得出应该画的频点数字
	 * 
	 * @param value
	 *            当前位置（根据中心点计算出来的数字）
	 * @return
	 */
	private String changLeftNumber(int value) {
		if (mModType == MOD_TYPE_FM) {// FM的ITEM正好除尽，所以108.0和78.5之间要间隔一整个大格
			if (value < minValue) {
				if (minValue - value == MOD_TYPE_SPILT_NUM * MOD_TYPE_FM_INT) {
					return getFregp(maxValue);
				} else if (minValue - value > MOD_TYPE_SPILT_NUM * MOD_TYPE_FM_INT) {
					return getFregp(maxValue - (minValue - value));
				}
			} else if (value > maxValue) {
				if (value - maxValue == MOD_TYPE_SPILT_NUM * MOD_TYPE_FM_INT) {
					return getFregp(minValue);
				} else if (value - maxValue > MOD_TYPE_SPILT_NUM * MOD_TYPE_FM_INT) {
					return getFregp(value - (maxValue) + minValue);
				}
			}
		} else if (mModType == MOD_TYPE_AM) {// AM的ITEM除9余4，1602到531只差一小格。
			if (value < minValue) {
				if (minValue - value == MOD_TYPE_SPILT_NUM * MOD_TYPE_AM_INT) {
					return getFregp(maxValue - 4 * MOD_TYPE_AM_INT);
				} else if (minValue - value > MOD_TYPE_SPILT_NUM * MOD_TYPE_AM_INT) {
					return getFregp(maxValue - (minValue - value) - 4 * MOD_TYPE_AM_INT);
				}
			} else if (value > maxValue) {
				if (value - maxValue == MOD_TYPE_AM_INT) {
					return getFregp(minValue);
				} else if (value - maxValue > MOD_TYPE_AM_INT) {
					return getFregp(value - (maxValue) + minValue);
				}
			}
		}

		return getFregp(value);
	}

	public String getFregp(int fregp) {

		if (mModType == MOD_TYPE_AM) {
			return "" + fregp;
		} else if (mModType == MOD_TYPE_FM) {
			if (fregp >= 8750 && fregp <= 10850) {
				return Double.parseDouble(fregp / 100 + "." + fregp % 100) + "";
			}
		}

		return "";
	}

	@SuppressLint("ClickableViewAccessibility")
	private float countLeftStart(int value, float xPosition, float textWidth) {
		float xp = 0f;
		if (value < 20) {
			xp = xPosition - (textWidth * 1 / 2);
		} else {
			xp = xPosition - (textWidth * 2 / 2);
			if ((value + "").length() > 3) {
				xp = xp - 10;
			}
		}
		return xp;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int xPosition = (int) event.getX();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mScroller.forceFinished(true);
			mLastX = xPosition;
			mMove = 0;
			AppDataUtils.getInstance().getHandler().removeMessages(RadioMainView.SET_MAINFREQ);
			break;
		case MotionEvent.ACTION_MOVE:
			mMove += (mLastX - xPosition);
			changeMoveAndValue();
            AppDataUtils.getInstance().getHandler().removeMessages(RadioMainView.SET_MAINFREQ);
			break;
		case MotionEvent.ACTION_UP:

		case MotionEvent.ACTION_CANCEL:
			if (mMove == 0) {
				mMove += (xPosition - getWidth() / 2);
				changeMoveAndValue();
			}
			countMoveEnd();
			countVelocityTracker(event);
			return false;
		default:
			break;
		}

		mLastX = xPosition;
		return true;
	}

	private void countVelocityTracker(MotionEvent event) {
		mVelocityTracker.computeCurrentVelocity(1000);
		float xVelocity = mVelocityTracker.getXVelocity();
		if (Math.abs(xVelocity) > mMinVelocity) {
			mScroller.fling(0, 0, (int) xVelocity / 4, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
		}
	}

	private void changeMoveAndValue() {
		int tValue = (int) (mMove / (mLineDivider * mDensity));
		if (Math.abs(tValue) > 0) {
			mValue += tValue * (mModType == MOD_TYPE_FM ? MOD_TYPE_FM_INT : MOD_TYPE_AM_INT);
			mMove -= tValue * mLineDivider * mDensity;
			if (mValue <= 0 || mValue > mMaxValue) {
				mValue = mValue <= 0 ? 0 : mMaxValue;
				mMove = 0;
				mScroller.forceFinished(true);
			}
			notifyValueChange();
		}
		postInvalidate();
	}

	private void countMoveEnd() {
		int roundMove = Math.round(mMove / (mLineDivider * mDensity));
		mValue = mValue + roundMove * (mModType == MOD_TYPE_FM ? MOD_TYPE_FM_INT : MOD_TYPE_AM_INT);
		mValue = mValue <= 0 ? 0 : mValue;
		mValue = mValue > mMaxValue ? mMaxValue : mValue;

		mLastX = 0;
		mMove = 0;

		notifyValueChange();
		postInvalidate();
	}

	private void notifyValueChange() {
		if (null != mListener) {
			if (mValue < minValue) {
				mValue = maxValue;
			} else if (mValue > maxValue) {
				mValue = minValue;
			}
			mListener.callbackCurrentChange(mValue);
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
				countMoveEnd();
			} else {
				int xPosition = mScroller.getCurrX();
				mMove += (mLastX - xPosition);
				changeMoveAndValue();
				mLastX = xPosition;
			}
		}
	}

	public interface CurrentChangeListener {
		public void callbackCurrentChange(float value);
	}
}