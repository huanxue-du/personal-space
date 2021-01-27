package com.sz.huanxue.androidapp.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sz.huanxue.androidapp.R;

import java.util.Calendar;


/**
 * 虚拟时钟UI
 */
public class ClockView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public static final String TAG = "ClockView";
    private static final int DEFAULT_RADIUS = 190;  // 默认半径
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean flag;
    private Context mContext;

    private Paint mPaint;                           // 圆和刻度的画笔
    private Paint mPointerPaint;                    // 指针画笔
    private int mCanvasWidth, mCanvasHeight;        // 画布的宽高
    private int mRadius = DEFAULT_RADIUS;           // 时钟半径
    private int mSecondPointerLength;               // 秒针长度
    private int mMinutePointerLength;               // 分针长度
    private int mHourPointerLength;                 // 时针长度
    private int mHourDegreeLength;                  // 时刻度长度
    private int mSecondDegreeLength;                // 秒刻度

    private int mHour, mMinute, mSecond;            // 时钟显示的时、分、秒
    private int mWidthSize;
    private int mHeightSize;

    public ClockView(Context context) {
        super(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mSecond = Calendar.getInstance().get(Calendar.SECOND);

        mHolder = getHolder();
        this.setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);

        mPaint = new Paint();
        mPointerPaint = new Paint();

        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mPointerPaint.setColor(Color.BLACK);
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setFilterBitmap(true);
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPointerPaint.setTextSize(22);
        mPointerPaint.setTextAlign(Paint.Align.CENTER);

        setFocusable(true);
        setFocusableInTouchMode(true);
        Log.i("logcat", TAG + "mHour:" + mHour + "---mMinute:" + mMinute + "---mSecond:" + mSecond);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //当前显示宽度
        mWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //当前显示高度
        mHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.i("logcat", TAG + "--widthSize:" + mWidthSize + "---heightSize:" + mHeightSize);
        Log.i("logcat", TAG + "--widthMode:" + widthMode + "---heightMode:" + heightMode);
    }

    /**
     * 需要保证切图的宽高是一致的，如果不一致，也可以通过代码动态调整中心点进行居中
     * 调整drawBitmap时的位置，可以改变画笔的中心点（根据切图的宽高的二分之一来决定）
     */
    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            Bitmap bmp = null;

            if (mCanvas != null) {
                //将坐标系原点移至去除内边距后的画布中心，该数值取的是屏幕分辨率的二分之一
                mCanvas.translate(mWidthSize / 2, mHeightSize / 2);

                //绘制表盘背景
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.adc_clock_bg1);
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                //-190是背景切图高/宽的二分之一
                mCanvas.drawBitmap(bmp, -190, -190, mPaint);

                //绘制时针
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.adc_hour_hand_down1);
                mCanvas.save();
                //因为指针图片是垂直的,由上到下画图片一次旋转180度，加上分钟造成的偏移量
                mCanvas.rotate(180 + mHour % 12 * 30 + mMinute * 1.0f / 60 * 30);
                //因为素材宽度为16,所以圆球半径为8 drawbitmap的绘制起点为0,0 要保证指针圆球在中心，所以偏移-8,-8
                mCanvas.drawBitmap(bmp, -8, -8, mPointerPaint);
                mCanvas.restore();

                //绘制分针
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.adc_minute_hand_down1);
                mCanvas.save();
                //因为指针图片是垂直的,由上到下画图片一次旋转180度，加上分钟造成的偏移量
                mCanvas.rotate(180 + mMinute * 6);
                //因为素材宽度为16,所以圆球半径为8 drawbitmap的绘制起点为0,0 要保证指针圆球在中心，所以偏移-8,-8
                mCanvas.drawBitmap(bmp, -8, -8, mPointerPaint);
                mCanvas.restore();

                //绘制秒针
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.adc_second_hand);
                mCanvas.save();
                // 因为指针图片是垂直的,由上到下画图片一次旋转180度，加上分钟造成的偏移量
                mCanvas.rotate(180 + mSecond * 6);
                //画完看起来偏右所以向左做了偏移
                mCanvas.drawBitmap(bmp, -7, -7, mPointerPaint);
                mCanvas.restore();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        flag = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    @Override
    public void run() {
        long start, end;
        while (flag) {
            start = System.currentTimeMillis();
            draw();
            logic();
            end = System.currentTimeMillis();
            try {
                if (end - start < 1000) {//控制每秒执行一次
                    Thread.sleep(1000 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行时分秒的计算
     */
    private void logic() {
        mSecond++;
        if (mSecond == 60) {
            mSecond = 0;
            mMinute++;
            if (mMinute == 60) {
                mMinute = 0;
                mHour++;
                if (mHour == 24) {
                    mHour = 0;
                }
            }
        }
    }

    /**
     * 获取主题风格
     *
     * @return 0:第一套， 1：第二套， 2：第三套
     */
    public int getThemeTag(Context mContext) {
        return Settings.System.getInt(mContext.getContentResolver(), "ThemeTag", 0);
    }
}
