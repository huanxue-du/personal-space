package com.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.autolink.radio55.R;

public class ArcRadioSeekBar extends View {
    private final static String TAG = ArcRadioSeekBar.class.getSimpleName();
    private final static int SECTOR_START_DEGREE = 90;
    private final static int SECTOR_SWEEP_DEGREE = 360;
    private final static int PROGRESS_MAX = 360;

    private Bitmap mBitmapProgressDst;
    private Bitmap mBitmapProgressSrc;
    private Canvas mBitmapProgressSrcCanvas;
    private RectF mBitmapProgressSrcOval;
    private Bitmap mBitmapProgress;
    private Canvas mBitmapProgressCanvas;
    //    private Bitmap mBitmapBackground;
    //    private Bitmap mBitmapHalo;
    private Bitmap mBitmapSubHalo;
    private int mWidth, mHeight;
    private int mRadius;
    private int minValidateTouchArcRadius;
    private int maxValidateTouchArcRadius;
    /**
     * 实时进度值
     */
    private float mDegreeProgress = 0;
    /**
     * 最大值
     */
    private int mMax = 100;
    private int mProgress = 0;
    private boolean mHaloVisible = true;
    private String mText = "";
    private Paint textPaint;
    private Paint paint;
    private Paint mBitmapProgressSrcPaint;

    public ArcRadioSeekBar(Context context) {
        this(context, null);
    }

    public ArcRadioSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcRadioSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBitmapProgressDst = BitmapFactory.decodeResource(getResources(), R.drawable.bg_loading_n);
        initBitmapProgress();
        //        mBitmapBackground = createProgressBackground();
        //        mBitmapHalo = BitmapFactory.decodeResource(getResources(), R.drawable.radio_loading_point);
        mBitmapSubHalo = null;
        mRadius = mBitmapProgressDst.getWidth() / 2;
        minValidateTouchArcRadius = mRadius - 40;
        maxValidateTouchArcRadius = mRadius + 40;
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(24);
        paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShader(null);

    }

    //    private Bitmap createProgressBackground() {
    //        ELog.i("createProgressBackground");
    //        Bitmap dst = BitmapFactory.decodeResource(getResources(), R.drawable.bg_loading_p);
    //        int length = dst.getWidth();
    //        if (dst.getHeight() > dst.getWidth()) length = dst.getHeight();
    //        Bitmap src = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
    //        Canvas srcCanvas = new Canvas(src);
    //        RectF oval = new RectF(0, 0, length, length);
    //        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    //        p.setColor(0xFFFFCC44);
    //        srcCanvas.drawArc(oval, SECTOR_START_DEGREE, SECTOR_SWEEP_DEGREE, true, p);
    //        Bitmap result = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
    //        Canvas canvas = new Canvas(result);
    //        Paint paint = new Paint();
    //        paint.setFilterBitmap(false);
    //        canvas.drawBitmap(dst, 0, 0, paint);
    //        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    //        canvas.drawBitmap(src, 0, 0, paint);
    //        paint.setXfermode(null);
    //        ELog.i("createProgressBackground:yes");
    //        return result;
    //    }

    private void initBitmapProgress() {
        int length = mBitmapProgressDst.getWidth();
        if (mBitmapProgressDst.getHeight() > mBitmapProgressDst.getWidth())
            length = mBitmapProgressDst.getHeight();
        mBitmapProgressSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapProgressSrcPaint.setColor(0xFFFFCC44);
        mBitmapProgressSrc = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        mBitmapProgressSrcCanvas = new Canvas(mBitmapProgressSrc);
        mBitmapProgressSrcOval = new RectF(0, 0, length, length);
        mBitmapProgress = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        mBitmapProgressCanvas = new Canvas(mBitmapProgress);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDegreeProgress == 0) return;
        Bitmap progress = getBitmapProgress();
        if (progress == null) {
            return;
        }
        canvas.drawBitmap(progress, (mWidth - progress.getWidth()) / 2, (mHeight - progress.getHeight()) / 2, paint);


        if (mBitmapSubHalo == null) {
            return;
        }
    }

    private Matrix matrix = new Matrix();
    private Bitmap targetBitmap;
    private Canvas targetCanvas;

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        if (targetBitmap == null) {
            targetBitmap = Bitmap.createBitmap(source.getWidth(), source.getWidth(), Bitmap.Config.ARGB_8888);
            targetCanvas = new Canvas(targetBitmap);
        }
        targetCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        matrix.setRotate(angle, source.getWidth() / 2, source.getHeight() / 2);
        targetCanvas.drawBitmap(source, matrix, paint);
        return targetBitmap;
    }

    Point point = new Point();

    private Point getHaloPosition(Bitmap halo) {
        if (halo == null) return null;
        double radians = (Math.PI / 180) * (SECTOR_START_DEGREE + mDegreeProgress - 1);
        int radius = mRadius - 7;
        point.x = (int) (mWidth / 2 + Math.cos(radians) * radius - halo.getWidth() / 2);
        point.y = (int) (mHeight / 2 + Math.sin(radians) * radius - halo.getHeight() / 2);
        return point;
    }

    private Bitmap getBitmapProgress() {
        mBitmapProgressSrcCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        mBitmapProgressSrcCanvas.drawArc(mBitmapProgressSrcOval, SECTOR_START_DEGREE, (float) mDegreeProgress, true, mBitmapProgressSrcPaint);
        mBitmapProgressCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        paint.setFilterBitmap(false);
        mBitmapProgressCanvas.drawBitmap(mBitmapProgressDst, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mBitmapProgressCanvas.drawBitmap(mBitmapProgressSrc, 0, 0, paint);
        paint.setXfermode(null);
        return mBitmapProgress;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;

        super.onSizeChanged(w, h, oldw, oldh);
    }


    private double updateDegreeProgress(double progress) {
        double degreeProgress = (PROGRESS_MAX * (progress / (double) mMax));
        return degreeProgress;
    }


    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        if (progress >= 0 && progress <= mMax) {
//            if (this.mProgress == progress) {
//                return;
//            }
            this.mProgress = progress;
            mDegreeProgress = (float) updateDegreeProgress(this.mProgress);
            invalidate();
        }
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public void setText(String text) {
        mText = text;
    }


}
