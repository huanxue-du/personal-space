package com.sz.huanxue.androidapp.utils.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import java.security.MessageDigest;

public class ColorFilterTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID =
            "jp.wasabeef.glide.transformations.ColorFilterTransformation." + VERSION;

    private int color;
    private int mWidth;
    private int mHeight;

    public ColorFilterTransformation(int color) {
        this.color = color;
    }

    @Override protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool,
                                         @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();

        Bitmap.Config config =
                toTransform.getConfig() != null ? toTransform.getConfig() : Bitmap.Config.RGB_565;
        Bitmap bitmap = pool.get(width, height, config);

        toTransform = TransformationUtils.centerCrop(pool, toTransform, width, height);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(toTransform, 0, 0, paint);

        return bitmap;
    }

    @Override public String toString() {
        return "ColorFilterTransformation(color=" + color + ")";
    }

    @Override public boolean equals(Object o) {
        return o instanceof ColorFilterTransformation &&
                ((ColorFilterTransformation) o).color == color;
    }

    @Override public int hashCode() {
        return ID.hashCode() + color * 10;
    }

    @Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + color).getBytes(CHARSET));
    }
}
