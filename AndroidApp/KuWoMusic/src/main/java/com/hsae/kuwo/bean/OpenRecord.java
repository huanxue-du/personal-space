package com.hsae.kuwo.bean;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.12.28.
 */
public class OpenRecord {

    /**
     * data : {}
     * statusCode : 0
     * statusMessage : 数据已存在
     */

    private DataBean data;
    private String statusCode;
    private String statusMessage;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

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

    public static class DataBean {
    }
}
