// IMyAidlInterface.aidl
package com.sz.huanxue.androidapp;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

             /**
                 * 请求焦点
                 *
                 * @param callback callback
                 */
                  void setRequestAudioFocus();
                 /**
                     * 释放焦点
                     *
                     * @param callback callback
                     */
                  void setLossAudioFoucus();
}
