package com.sz.huanxue.androidapp.data.remote.bean;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.4.
 */
public class RecordBean {

    /**
     * oauthProviderId : kuwo
     * statusCode : 0
     * statusComment : 成功
     * openTime : 2020-12-28 19:30:40
     */

    private String oauthProviderId;
    private String statusCode;
    private String statusComment;
    private String openTime;

    public RecordBean(String oauthProviderId, String statusCode, String statusComment, String openTime) {
        this.oauthProviderId = oauthProviderId;
        this.statusCode = statusCode;
        this.statusComment = statusComment;
        this.openTime = openTime;
    }

    @Override
    public String toString() {
        return "RecordBean{" +
                "oauthProviderId='" + oauthProviderId + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", statusComment='" + statusComment + '\'' +
                ", openTime='" + openTime + '\'' +
                '}';
    }

    public String getOauthProviderId() {
        return oauthProviderId;
    }

    public void setOauthProviderId(String oauthProviderId) {
        this.oauthProviderId = oauthProviderId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusComment() {
        return statusComment;
    }

    public void setStatusComment(String statusComment) {
        this.statusComment = statusComment;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }
}
