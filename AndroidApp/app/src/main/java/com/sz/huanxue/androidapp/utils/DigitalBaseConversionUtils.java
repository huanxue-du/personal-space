package com.sz.huanxue.androidapp.utils;

/**
 * @author huanxue
 * Created by Administrator on 2019.10.10.
 */
public class DigitalBaseConversionUtils {
    public static String byteToHexStr(byte value) {
        String head = "";
        if (value <= 15 && value >= 0) {
            head += "0";
        }
        return head + String.format("%X", value);
    }

    public static String byteToHexStr(byte[] values) {
        StringBuilder result = new StringBuilder("{");
        for (byte value : values) {
            result.append(byteToHexStr(value) + ", ");
        }
        result.append("}");
        return result.toString();
    }

    public static byte getHighByte(int value) {
        byte high = (byte) ((value & 0xFFFF) / 256);
        return high;
    }

    public static byte getLowByte(int value) {
        byte low = (byte) ((value & 0xFFFF) % 256);
        return low;
    }

    public static String intToDbText(int v) {
        String result = "";
        result = String.format("%.1f", v * 0.1) + "dB";
        return result;
    }

    public static String intToDbuVText(int v) {
        String result = "";
        result = String.format("%.1f", v * 0.1) + "dBuV";
        return result;
    }

    public static String intToHzText(int v) {
        String result = "";
        result = String.format("%d", v) + "Hz";
        return result;
    }

    public static String intTokHzText(int v) {
        String result = "";
        result = String.format("%.1f", v * 0.1) + "kHz";
        return result;
    }

    public static String intToMHzText(int v) {
        String result = "";
        result = String.format("%d", v) + "MHz";
        return result;
    }

    public static String intToPercentText(int v) {
        String result = "";
        result = String.format("%.1f", v * 0.1) + "%";
        return result;
    }

    public static byte[] translateParam(int[] params) {//转换数据为高低8位
        byte[] result = new byte[params.length * 2];
        for (int i = 0; i < params.length; i++) {
            result[i * 2] = getHighByte(params[i]);
            result[i * 2 + 1] = getLowByte(params[i]);
        }
        return result;
    }

    public static int[] parseDoubleBytes(byte[] params) {//转换MCU数据为int类型负数
        int[] result = new int[params.length / 2];
        for (int i = 0; i < result.length; i++) {
            short a = params[i * 2];
            short b;

            if (a > 0) {
                a = (short) (params[i * 2] & 0x0FF);
                b = (short) (params[i * 2 + 1] & 0x0FF);
                result[i] = a * 256 + b;
            } else {
                a = (short) (params[i * 2] & 0xff * 256);
                b = (short) (params[i * 2 + 1] & 0xff);
                result[i] = a + b;
            }
        }

        return result;
    }

    public static int[] parseDoubleBytesPlus(byte[] params) {//转换MCU数组数据为int类型正数0-65535
        int[] result = new int[params.length / 2];
        for (int i = 0; i < result.length; i++) {
            int a = params[i * 2] & 0xff;
            int b;

            if (a > 0) {
                a = (params[i * 2] & 0x0FF);
                b = (params[i * 2 + 1] & 0x0FF);
                result[i] = a * 256 + b;
            } else {
                a = (params[i * 2] & 0xff * 256);
                b = (params[i * 2 + 1] & 0xff);
                result[i] = a + b;
            }
        }

        return result;
    }

    public static int parseDoubleBytes(byte high, byte low) {//转换MCU两个byte数据为一个int类型负数
        short result = 0;

        short a = high;
        short b= low;

        if (a > 0) {
            a = (short) (high& 0x0FF);
            b = (short) (low& 0x0FF);
            result = (short) (a * 256 + b);
        } else {
            a = (short) ((short) high & 0xff * 256);
            b = (short) (low & 0xff);
            result = (short) (a + b);
        }

        return result;
    }


    public static String intToTimeText(int v) {
        String result = "";
        result = v + "ms";
        return result;
    }

    public static String intToTimeMsText(int v) {
        String result = "";
        result = String.format("%.1f", v * 0.1) + "ms";
        return result;
    }

    public static String intToTimeusText(int v) {
        String result = "";
        result = v + "us";
        return result;
    }
}
