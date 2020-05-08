package com.sz.huanxue.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.sz.huanxue.androidapp.MyApplication;

/**
 * @author huanxue
 * Created by Administrator on 2019/6/27.
 */
public class SharedPrepreferenceUtils {

    public static final String TAG = "SharedPrepreferenceUtils";
    private static final String SP_NAME = "USM_SP";
    private static final SharedPrepreferenceUtils instance = new SharedPrepreferenceUtils(MyApplication.getInstance());
    private Context mContext;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private SharedPrepreferenceUtils(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SharedPrepreferenceUtils getInstance() {
        return instance;
    }

    public void setIntSharedPreferences(String key, int values) {
        editor.putInt(key, values);//不区分账户存储
        editor.commit();
    }


    public void setStringSharedPreferences(String key, String values) {
        editor.putString(key, values);
        editor.commit();
    }

    public void setBooleanSharedPreferences(String key, boolean values) {
        editor.putBoolean(key, values);
        editor.commit();
    }

    public int getIntSharedPreferences(String key) {
        return sp.getInt(key, -1);//与账户关联获取
    }

    public int getIntSharedPreferences(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);//与账户关联获取
    }

    public String getStringSharedPreferences(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public boolean getBooleanSharedPreferences(String key) {
        return sp.getBoolean(key, false);
    }
}
