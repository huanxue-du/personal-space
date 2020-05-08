// IMyAidlCallback.aidl
package com.sz.huanxue.androidapp;

// Declare any non-default types here with import statements

interface IMyAidlCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

              /**
                 * onDriverTalkCallBack
                 *
                 * @param callback callbank
                 */
                void onDriverTalkCallBack(boolean isFoucs);
}
