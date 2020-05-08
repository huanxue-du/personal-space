package com.autolink.radio55.app;

/**
 * @author huanxue
 *         Created by Administrator on 2017/11/20.
 */

public interface CallBackAppData {

    void callBackGetMainFreqFromApp(int freq);

    void callBackGetBandFromApp(int band);

    void callBackIsOnRadioStatus(boolean isOnRadio);

    void callBackCollPrestoreListFromApp(int band);

    void callBackScanTypeFromApp(int type);

    void callBackPercentageFromApp(int process);

    void callBackBindServiceFromApp(boolean isOk);
}
