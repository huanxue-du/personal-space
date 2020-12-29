package com.sz.huanxue.androidapp.utils.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.security.MessageDigest;

/**
 * Created by Administrator on 2018/12/26.
 */

public class ReflectTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID =
            "com.hsae.android.ui.utils.transform.ReflectTransformation." + VERSION + System.nanoTime();

    @Override
    protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool, @NonNull Bitmap originalImage, int outWidth, int outHeight) {
        // 反射图片和原始图片中间的间距
        final int reflectionGap = 16;
        int width = outWidth;
        int height = outHeight;

        //通过矩阵对图像进行变换
        Matrix matrix = new Matrix();
        // 第一个参数为1，表示x方向上以原比例为准保持不变，正数表示方向不变。
        // 第二个参数为-1，表示y方向上以原比例为准保持不变，负数表示方向取反。
        matrix.preScale(1, -1); // 实现图片的反转

        // 创建反转后的图片Bitmap对象，图片高是原图的一半
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                height / 2, width, height / 2, matrix, false);

        // 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);

        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
        // 将反转后的图片画到画布中
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        // 创建线性渐变LinearGradient 对象。
        LinearGradient shader = new LinearGradient(0, originalImage
                .getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);

        paint.setShader(shader);
        // 倒影遮罩效果
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID).getBytes(CHARSET));

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReflectTransformation)) {
            return false;
        }
        return hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
