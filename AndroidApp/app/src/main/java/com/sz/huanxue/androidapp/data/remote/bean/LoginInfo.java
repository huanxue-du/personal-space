package com.sz.huanxue.androidapp.data.remote.bean;

/**
 * 无感登录获取登录参数的实体类
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.7.20.
 */
public class LoginInfo {


    /**
     * statusCode : 0
     * statusMessage : 请求成功
     * data : {"randomstring":"cc9ef57439eb4ad68384532fe5c0e077","redirect_uri":"https://fawivi-cpgw-uat.faw.cn:63100/oauth/noOauth/randomStringGetVin"}
     */

    private String statusCode;
    private String statusMessage;
    private DataBean data;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        /**
         * randomstring : cc9ef57439eb4ad68384532fe5c0e077
         * redirect_uri : https://fawivi-cpgw-uat.faw.cn:63100/oauth/noOauth/randomStringGetVin
         */

        private String randomstring;
        private String redirect_uri;

        public String getRandomstring() {
            return randomstring;
        }

        public void setRandomstring(String randomstring) {
            this.randomstring = randomstring;
        }

        public String getRedirect_uri() {
            return redirect_uri;
        }

        public void setRedirect_uri(String redirect_uri) {
            this.redirect_uri = redirect_uri;
        }
    }
}
