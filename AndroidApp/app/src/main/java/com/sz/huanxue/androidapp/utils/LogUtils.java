package com.sz.huanxue.androidapp.utils;

import android.util.Log;

import java.util.Arrays;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.4.
 */
public final class LogUtils {
    public static final int V = Log.VERBOSE;
    public static final int D = Log.DEBUG;
    public static final int I = Log.INFO;
    public static final int W = Log.WARN;
    public static final int E = Log.ERROR;
//    public static final int A = Log.ASSERT;
    /**
     * log总开关，默认开
     */
    private static boolean sLogSwitch = true;
    /**
     * log标签
     */
    private static String sGlobalTag = "mySelf";

    private LogUtils() {
        throw new UnsupportedOperationException("u can't init me...");
    }

    public static void v(final Object contents) {
        log(V, sGlobalTag, contents);
    }

    private static void v(final String tag, final Object... contents) {
        log(V, tag, contents);
    }

    public static void d(final Object contents) {
        log(D, sGlobalTag, contents);
    }

    private static void d(final String tag, final Object... contents) {
        log(D, tag, contents);
    }

    public static void i(final Object contents) {
        log(I, sGlobalTag, contents);
    }

    private static void i(final String tag, final Object... contents) {
        log(I, tag, contents);
    }

    public static void w(final Object contents) {
        log(W, sGlobalTag, contents);
    }

    private static void w(final String tag, final Object... contents) {
        log(W, tag, contents);
    }

    public static void e(final Object contents) {
        log(E, sGlobalTag, contents);
    }

    private static void e(final String tag, final Object... contents) {
        log(E, tag, contents);
    }

/*    public static void a(final Object contents) {
        log(A, sGlobalTag, contents);
    }

    private static void a(final String tag, final Object... contents) {
        log(A, tag, contents);
    }*/

    private static void log(final int type, final String tag, final Object... contents) {
        if (!sLogSwitch) return;

        Log.println(type, tag, Arrays.toString(contents));
    }
}
