package com.sz.huanxue.androidapp.data.remote.bean;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.12.1.
 */
public class QrBean {

    /**
     * rqRandomCodeInfo : {"appDownLoadPath":"http://hqapp.hqxs.faw.cn/app/index.html?vin=LFPH6BCP3L2L00010&screenCode=11"}
     */

    private RqRandomCodeInfoBean rqRandomCodeInfo;

    public RqRandomCodeInfoBean getRqRandomCodeInfo() {
        return rqRandomCodeInfo;
    }

    public void setRqRandomCodeInfo(RqRandomCodeInfoBean rqRandomCodeInfo) {
        this.rqRandomCodeInfo = rqRandomCodeInfo;
    }

    public static class RqRandomCodeInfoBean {
        /**
         * appDownLoadPath : http://hqapp.hqxs.faw.cn/app/index.html?vin=LFPH6BCP3L2L00010&screenCode=11
         */

        private String appDownLoadPath;

        public String getAppDownLoadPath() {
            return appDownLoadPath;
        }

        public void setAppDownLoadPath(String appDownLoadPath) {
            this.appDownLoadPath = appDownLoadPath;
        }
    }
}
