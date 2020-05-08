package com.sz.huanxue.androidapp.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CheckBox;
import com.sz.huanxue.androidapp.R;


public class UISwitch extends CheckBox {

    public static final int ANIMATION_FRAME_DURATION = 1000 / 60;
    private static final int MSG_ANIMATE = 1000;
    private static final int MSG_ONCLICK = 0x00005;
    private static final int MSG_CHECK_SWITCH_STATUS = 0x00006;
    private final int MAX_ALPHA = 255;
    private final float VELOCITY = 350;// 定义按钮动画移动的最大长度
    private final int COMMON_WIDTH_IN_PIXEL = 96;// 默认宽度
    private final int COMMON_HEIGHT_IN_PIXEL = 50;// 默认高度
    private final int COMMON_SLIDER_WIDTH_IN_PIXEL = 48;
    private final int COMMON_SLIDER_HEIGHT_IN_PIXEL = 48;
    private Paint mPaint;
    private RectF mSaveLayerRectF;
    private float mFirstDownY;
    private float mFirstDownX;
    private int mClickTimeout;
    private int mTouchSlop;
    private int mAlpha = MAX_ALPHA;
    private boolean mChecked = true;
    private boolean mBroadcasting;// 标示是否正在执行监听事件中
    private boolean mTurningOn;// 标示位置是否达到开启状态
    private PerformClick mPerformClick;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnClickListener mOnClickListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
    private boolean mAnimating;// 标示是否继续执行移动动画
    private float mVelocity;// 按钮动画移动的最大像素长度
    private float mAnimationPosition;// 按钮动画移动的当前位置
    private float mAnimatedVelocity;// 按钮动画移动的实际位移(+mVelocity/-mVelocity)
    private Bitmap bmBgGreen;// 绿色背景
    private Bitmap bmBgWhite;// 白色背景
    private Bitmap bmBgEnable;// 不可用时背景
    private Bitmap bmBtnNormal;// 未按下时按钮
    private Bitmap bmBtnSliderOff;// g关闭时slider
    private Bitmap bmCurBtnPic;// 当前显示的按钮图片
    private Bitmap bmCurBgPic;// 当前背景图片
    private float bgWidth;// 背景宽度
    private float bgHeight;// 背景宽度
    private float btnWidth;// 按钮宽度
    private float offBtnPos;// 按钮关闭时位置
    private float onBtnPos;// 按钮开启时位置
    private float curBtnPos;// 按钮当前位置
    private float startBtnPos;// 开始按钮位置
    private int sliderTop;//slider距离顶部的距离
    private boolean localcall;
    private boolean isEnable = true;
    private Handler mHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ANIMATE:
                    if (msg.obj != null) {
                        ((Runnable) msg.obj).run();
                    }
                    break;
                case MSG_ONCLICK:
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(UISwitch.this);
                    }
                    break;
                case MSG_CHECK_SWITCH_STATUS:
                    startAnimation(!mChecked);
                    break;
            }
            return false;
        }
    });

    public UISwitch(Context context) {
        this(context, null);
    }

    public UISwitch(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public UISwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        Resources resources = context.getResources();

        // get attrConfiguration
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UISwitch);

        int width = (int) array.getDimensionPixelSize(R.styleable.UISwitch_bmWidth, 0);
        int height = (int) array.getDimensionPixelSize(R.styleable.UISwitch_bmHeight, 0);
        int sliderWidth = (int) array.getDimensionPixelSize(R.styleable.UISwitch_sliderWidth, 0);
        int sliderHeight = (int) array.getDimensionPixelSize(R.styleable.UISwitch_sliderHeight, 0);
        array.recycle();

        // size width or height
        if (width <= 0 || height <= 0 || sliderWidth <= 0 || sliderHeight <= 0) {
            width = COMMON_WIDTH_IN_PIXEL;
            height = COMMON_HEIGHT_IN_PIXEL;
            sliderWidth = COMMON_SLIDER_WIDTH_IN_PIXEL;
            sliderHeight = COMMON_SLIDER_HEIGHT_IN_PIXEL;
        }
        sliderTop = (height - sliderHeight) / 2;

        // get viewConfiguration
        mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // get Bitmap
        bmBgGreen = BitmapFactory.decodeResource(resources, R.drawable.switch_on);
        bmBgWhite = BitmapFactory.decodeResource(resources, R.drawable.switch_off);
        bmBgEnable = BitmapFactory.decodeResource(resources, R.drawable.switch_disabled);
        bmBtnNormal = BitmapFactory.decodeResource(resources, R.drawable.switch_slider_on);
        bmBtnSliderOff = BitmapFactory.decodeResource(resources, R.drawable.switch_slider_off);

        // size Bitmap
        bmBgGreen = Bitmap.createScaledBitmap(bmBgGreen, width, height, true);
        bmBgWhite = Bitmap.createScaledBitmap(bmBgWhite, width, height, true);
        bmBgEnable = Bitmap.createScaledBitmap(bmBgEnable, width, height, true);
        bmBtnNormal = Bitmap.createScaledBitmap(bmBtnNormal, sliderWidth, sliderHeight, true);
        bmBtnSliderOff = Bitmap.createScaledBitmap(bmBtnSliderOff, sliderWidth, sliderHeight, true);

        bmCurBtnPic = mChecked ? bmBtnNormal : bmBtnSliderOff;// 初始按钮图片
        bmCurBgPic = mChecked ? bmBgGreen : bmBgWhite;// 初始背景图片
        bgWidth = bmBgGreen.getWidth();// 背景宽度
        bgHeight = bmBgGreen.getHeight();// 背景高度
        btnWidth = bmBtnNormal.getWidth();// 按钮宽度
        offBtnPos = sliderTop;// 关闭时在最左边
        onBtnPos = bgWidth - btnWidth - sliderTop;// 开始时在右边
        curBtnPos = mChecked ? onBtnPos : offBtnPos;// 按钮当前为初始位置

        // get density
        float density = resources.getDisplayMetrics().density;
        mVelocity = (int) (VELOCITY * density + 0.5f);// 动画距离
        mSaveLayerRectF = new RectF(0, 0, bgWidth, bgHeight);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        localcall = false;
        isEnable = enabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) bgWidth, (int) bgHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        float deltaX = Math.abs(x - mFirstDownX);
        float deltaY = Math.abs(y - mFirstDownY);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                ViewParent mParent = getParent();
                if (mParent != null) {
                    // 通知父控件不要拦截本view的触摸事件
                    mParent.requestDisallowInterceptTouchEvent(true);
                }
                mFirstDownX = x;
                mFirstDownY = y;
                startBtnPos = mChecked ? onBtnPos : offBtnPos;
                break;
            case MotionEvent.ACTION_MOVE:
                float time = event.getEventTime() - event.getDownTime();
                curBtnPos = startBtnPos + event.getX() - mFirstDownX;
                if (curBtnPos >= onBtnPos) {
                    curBtnPos = onBtnPos;
                }
                if (curBtnPos <= offBtnPos) {
                    curBtnPos = offBtnPos;
                }
                mTurningOn = curBtnPos > bgWidth / 2 - btnWidth / 2;
                break;
            case MotionEvent.ACTION_UP:
                setEnabled(false, true);
                postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (localcall) {
                            setEnabled(true);
                        }
                    }
                }, 200);
//                bmCurBtnPic = bmBtnNormal;
                time = event.getEventTime() - event.getDownTime();
                if (deltaY < mTouchSlop && deltaX < mTouchSlop && time < mClickTimeout) {
                    if (mPerformClick == null) {
                        mPerformClick = new PerformClick();
                    }
                    if (!post(mPerformClick)) {
                        performClick();
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_ONCLICK, 500);
                } else {
                    startAnimation(mTurningOn);
                }
                if (deltaX > mTouchSlop) {
                    mHandler.sendEmptyMessageDelayed(MSG_ONCLICK, 500);
                }
                mHandler.sendEmptyMessageDelayed(MSG_CHECK_SWITCH_STATUS, 1000);
                break;
        }
        invalidate();
        return isEnabled();
    }

    private void setEnabled(boolean enabled, boolean localcall) {
        setEnabled(enabled);

        this.localcall = localcall;
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean performClick() {
        startAnimation(!mChecked);
        return true;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;

            // 初始化按钮位置
            curBtnPos = checked ? onBtnPos : offBtnPos;
            // 改变背景图片
            bmCurBgPic = checked ? bmBgGreen : bmBgWhite;
            bmCurBtnPic = checked ? bmBtnNormal : bmBtnSliderOff;
            invalidate();

            if (mBroadcasting) {
                // NO-OP
                return;
            }
            // 正在执行监听事件
            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(UISwitch.this, mChecked);
            }

            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(UISwitch.this, mChecked);
            }
            // 监听事件结束
            mBroadcasting = false;
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayerAlpha(mSaveLayerRectF, mAlpha, Canvas.ALL_SAVE_FLAG);

        // 绘制底部图片
        canvas.drawBitmap(bmCurBgPic, 0, 0, mPaint);

        // 绘制按钮
        canvas.drawBitmap(bmCurBtnPic, curBtnPos, sliderTop, mPaint);

        if (!isEnable) {
            canvas.drawBitmap(bmBgEnable, 0, 0, mPaint);
        }

        canvas.restore();
    }

    private void setCheckedDelayed(final boolean checked) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setChecked(checked);
            }
        }, 10);
    }

    public void setUIChecked(boolean uiChecked) {
        if (mChecked != uiChecked) {
            mChecked = uiChecked;

            // 初始化按钮位置
            curBtnPos = uiChecked ? onBtnPos : offBtnPos;
            // 改变背景图片
            bmCurBgPic = uiChecked ? bmBgGreen : bmBgWhite;
            bmCurBtnPic = uiChecked ? bmBtnNormal : bmBtnSliderOff;
            invalidate();
            if (mBroadcasting) {
                // NO-OP
                return;
            }
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }


    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    private void startAnimation(boolean turnOn) {
        mAnimating = true;
        mAnimatedVelocity = turnOn ? mVelocity : -mVelocity;
        mAnimationPosition = curBtnPos;
        new SwitchAnimation().run();
    }

    private void stopAnimation() {
        mAnimating = false;
    }

    private void doAnimation() {
        mAnimationPosition += mAnimatedVelocity * ANIMATION_FRAME_DURATION / 1000;
        if (mAnimationPosition <= offBtnPos) {
            stopAnimation();
            mAnimationPosition = offBtnPos;
            setCheckedDelayed(false);
        } else if (mAnimationPosition >= onBtnPos) {
            stopAnimation();
            mAnimationPosition = onBtnPos;
            setCheckedDelayed(true);
        }
        curBtnPos = mAnimationPosition;
        invalidate();
    }

    public void requestAnimationFrame(Runnable runnable) {
        Message message = new Message();
        message.what = MSG_ANIMATE;
        message.obj = runnable;
        mHandler.sendMessageDelayed(message, ANIMATION_FRAME_DURATION);
    }

    public void removeCheckSwitchMsg() {
        mHandler.removeMessages(MSG_CHECK_SWITCH_STATUS);
        Log.i("logcat", "----UISwitch:-------------removeCheckSwitchMsg" + MSG_CHECK_SWITCH_STATUS);
    }


    private class PerformClick implements Runnable {

        public void run() {
            performClick();
        }
    }

    private final class SwitchAnimation implements Runnable {

        @Override
        public void run() {
            if (!mAnimating) {
                return;
            }
            doAnimation();
            requestAnimationFrame(this);
        }
    }
}
