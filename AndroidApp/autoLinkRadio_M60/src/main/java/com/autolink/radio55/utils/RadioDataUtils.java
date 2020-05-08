package com.autolink.radio55.utils;

import com.autolink.serial.mcu.manager.radio.RadioManager;

/**
 * 数据处理帮助类，频点转换
 *
 * @author Administrator
 */
public class RadioDataUtils {
    public static final String AM_DB = "'AM'";
    public static final String FM_DB = "'FM'";
    public static final String AM_KHZ = "KHz";
    public static final String FM_MHZ = "MHz";
    public static final String AM = "AM";
    public static final String FM = "FM";
    public static final String ASC = "frequency asc";
    public static final String DESC = "frequency desc";
    public static final int MIN_FM_FREQ = 8750;
    public static final int MAX_FM_FREQ = 10800;
    public static final int MIN_AM_FREQ = 531;
    public static final int MAX_AM_FREQ = 1602;
    public static final int ISCOLL_TRUE = 0;
    public static final int ISCOLL_FALSE = 5;

    /**
     * 将未处理int类型频点传入，转化为可显示的String类型频点，主要用于页面显示
     *
     * @param fregp
     * @param band
     * @return
     */
    public static String getFregp(int fregp, int band) {

        if (band == RadioManager.VALUE_BAND_AM) {
            return fregp + "";
        } else if (band == RadioManager.VALUE_BAND_FM) {
            if (fregp >= MIN_FM_FREQ && fregp <= MAX_FM_FREQ) {
                float freq = fregp;
                String str = freq / 100 + "";
                return str;
            }
        }

        return "";
    }

    /**
     * 将未处理String类型频点传入，转化为可下发频点的int类型,主要用于设置频点
     *
     * @param freq
     * @param band
     * @return
     */
    public static int getFreq(String freq, int band) {
        if (band == RadioManager.VALUE_BAND_AM) {
            return Integer.valueOf(freq);
        } else if (band == RadioManager.VALUE_BAND_FM) {
            if (freq.contains(".")) {
                freq = freq + "0";
                freq = freq.replace(".", "");
                return Integer.valueOf(freq);
            } else {
                return Integer.valueOf(freq);
            }
        }
        return 0;
    }

    /**
     * 将未处理String类型频点传入，转化为可显示的String类型频点
     *
     * @param fregp
     * @param band
     * @return
     */
    public static String getFregp(String fregp, String band) {

        if (band.equals(AM)) {
            return fregp;
        } else if (band.equals(FM)) {
            float fregpI = Float.valueOf(fregp);
            if (fregpI >= MIN_FM_FREQ && fregpI <= MAX_FM_FREQ) {
                return fregpI / 100 + "";
            }
        }
        return "";
    }

    /**
     * 根据波段返回波段单位制
     *
     * @param band
     * @return
     */
    public static String getMKHZ(int band) {
        if (band == RadioManager.VALUE_BAND_AM) {
            return AM_KHZ;
        } else if (band == RadioManager.VALUE_BAND_FM) {
            return FM_MHZ;
        }

        return "";
    }

    /**
     * 根据单位制返回AM或FM标识
     *
     * @param MKHZ
     * @return
     */
    public static String getFmAmStrByMKHz(String MKHZ) {
        if (MKHZ.equals(AM_KHZ)) {
            return AM;
        } else if (MKHZ.equals(FM_MHZ)) {
            return FM;
        }
        return "";
    }

    /**
     * 波段类型int转String
     *
     * @param band
     * @return
     */
    public static String getFmAmStr(int band) {
        if (band == RadioManager.VALUE_BAND_AM) {
            return AM;
        } else if (band == RadioManager.VALUE_BAND_FM) {
            return FM;
        }
        return "";
    }

    /**
     * 波段类型String转int
     *
     * @param band
     * @return
     */
    public static int getFmAmTypeByStr(String band) {
        if (band.equals(AM)) {
            return RadioManager.VALUE_BAND_AM;
        } else if (band.equals(FM)) {
            return RadioManager.VALUE_BAND_FM;
        }
        return -1;
    }
}
