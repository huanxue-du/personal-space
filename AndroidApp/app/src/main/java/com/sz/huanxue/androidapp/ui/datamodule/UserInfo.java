package com.sz.huanxue.androidapp.ui.datamodule;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.25.
 */
public class UserInfo {

    private String name;
    private String password;

    public UserInfo(String s1, String s2) {
        this.name = s1;
        this.password = s2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}