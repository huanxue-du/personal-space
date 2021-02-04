package com.sz.huanxue.androidapp.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.sz.huanxue.androidapp.R;

import androidx.appcompat.widget.AppCompatImageView;
import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatImageHelper;
import skin.support.widget.SkinCompatSupportable;


/***
 * @author daikin
 */
public class RoundImageView extends AppCompatImageView implements SkinCompatSupportable {

    private Paint mPaint;
    private Bitmap mOutBitmap;
    private boolean mRadius;
    private float mBorderRadius;
    private Drawable mPrDrawable;
    private PorterDuffXfermode xfermode =new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private SkinCompatImageHelper mImageHelper;
    private SkinCompatBackgroundHelper mBackgroundHelper;

	public RoundImageView(Context context) {
		this(context, null);
	}

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

	public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
		try {
			mRadius = a.getBoolean(R.styleable.RoundImageView_view_radius, false);
			if (!mRadius) {
				mBorderRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_border_radius, 0);
			}
		} finally {
			a.recycle();
		}
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mImageHelper = new SkinCompatImageHelper(this);
		mImageHelper.loadFromAttributes(attrs, defStyleAttr);
		mBackgroundHelper = new SkinCompatBackgroundHelper(this);
		mBackgroundHelper.loadFromAttributes(attrs, defStyleAttr);
	}

	@Override
    protected void onDraw(Canvas canvas) {
        int layerCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        if (mPrDrawable == null) mPrDrawable = getDrawable();
        //首先需要一个形状的bitmap；
        if (mOutBitmap == null || mOutBitmap.getWidth() != canvas.getWidth() || mOutBitmap.getHeight() != canvas.getHeight() || mPrDrawable != getDrawable()) {
            mPrDrawable = getDrawable();
            mOutBitmap = genOutBitmap();
        }
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(mOutBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerCount);
    }

    public Bitmap genOutBitmap() {
        int width = getWidth();
        int height = getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(bitmap);
        mPaint.setColor(Color.GREEN);
        if (mRadius) {
            tempCanvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2, mPaint);
        } else {
            tempCanvas.drawRoundRect(new RectF(0, 0, width, height), mBorderRadius, mBorderRadius, mPaint);
        }
        return bitmap;
    }

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if (mImageHelper != null) {
			mImageHelper.setImageResource(resId);
		}
	}

	@Override
	public void setBackgroundResource(int resId) {
		super.setBackgroundResource(resId);
		if (mBackgroundHelper != null) {
			mBackgroundHelper.applySkin();
		}
	}

	@Override
	public void applySkin() {
		if (mImageHelper != null) {
			mImageHelper.applySkin();
		}
		if (mBackgroundHelper != null) {
			mBackgroundHelper.applySkin();
		}
	}
}
