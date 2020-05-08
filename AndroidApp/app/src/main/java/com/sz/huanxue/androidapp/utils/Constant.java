package com.sz.huanxue.androidapp.utils;

import android.content.Context;
import android.content.Intent;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.24.
 */
public class Constant {

    public static void startApp(Context context, Class cls) {
        Intent i = new Intent(context, cls);
//        i.setClassName("com.sz.hsae.ku_usm", "com.sz.hsae.ku_usm.activity.MainActivity");
        i.addFlags(
            Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}
