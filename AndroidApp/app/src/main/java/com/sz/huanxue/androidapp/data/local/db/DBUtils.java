package com.sz.huanxue.androidapp.data.local.db;

/**
 * 非必要
 * 如果需要统一操作、实现复杂逻辑、组合逻辑，需要该工具类，
 * 也可以直接调用AppDataBase.getInstance()
 *
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.4.
 */
public class DBUtils {
    private static DBUtils instance;

    private DBUtils() {
    }

    public static DBUtils getInstance() {
        if (instance == null) {
            synchronized (DBUtils.class) {
                if (instance == null) {
                    instance = new DBUtils();
                }
            }
        }
        return instance;
    }

    public String getAccessToken(String userId) {
        return AppDataBase.getInstance().userDao().queryToken(userId);
    }
}
