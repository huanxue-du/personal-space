package com.sz.huanxue.androidapp.utils;

import android.graphics.Bitmap;

/**
 * 使用com.google.zxing生成二维码
 *
 * @Date 2020/9/14 15:33
 * @Description 二维码工具类
 */
public class QRCodeUtil {

    /**
     * 根据内容生成宽高为120*120不留白边的二维码
     *
     * @param content 内容
     * @return 二维码
     */
    public static Bitmap generateBitmap(String content) {
        return generateBitmap(content, 120, 120, false);
    }

    /**
     * 根据内容生成二维码
     *
     * @param content 需要生成二维码的内容
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param needDeleteWhiteBorder 是否需要白边
     */
    public static Bitmap generateBitmap(String content, int width, int height, boolean needDeleteWhiteBorder) {
      /*  try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            if (needDeleteWhiteBorder) {
                //删除白边
                matrix = deleteWhite(matrix);
            }
            width = matrix.getWidth();
            height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK;
                    } else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
        return null;
    }

/*    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }*/

    /**
     * 解码二维码
     *
     * @param bm 二维码
     * @return 解码后的字符串
     */
    public String decodeQrCode(Bitmap bm) {
    /*    String contents = "";
        int[] intArray = new int[bm.getWidth() * bm.getHeight()];
        bm.getPixels(intArray, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bm.getWidth(), bm.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        // use this otherwise ChecksumException
        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
        } catch (Exception ignored) {
        }
        return contents;*/
        return "";
    }
}
