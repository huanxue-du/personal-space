@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.sz.huanxue.androidapp.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.sz.huanxue.androidapp.HuanXueApp

/**
 * @author huanxue
 * Created by Administrator on 2019/6/27.
 */
@SuppressLint("CommitPrefEdits")
object SPUtils {
    val mContext: Context = HuanXueApp.getContext()
    private val sp: SharedPreferences
    private val editor: SharedPreferences.Editor
    private const val SP_NAME = "MediaMusic_SP"
    fun setIntSharedPreferences(key: String, values: Int) {
        editor.putInt(key, values)
        editor.commit()
    }

    fun setStringSharedPreferences(key: String, values: String?) {
        editor.putString(key, values)
        editor.commit()
    }

    fun setBooleanSharedPreferences(key: String, values: Boolean) {
        editor.putBoolean(key, values)
        editor.commit()
    }

    fun getIntSharedPreferences(key: String): Int {
        return sp.getInt(key, -1)
    }

    fun getIntSharedPreferences(key: String, defaultValue: Int): Int {
        return sp.getInt(key, defaultValue)
    }

    fun getStringSharedPreferences(key: String, defValue: String?): String {
        return sp.getString(key, defValue)
    }

    fun getBooleanSharedPreferences(key: String, defValues: Boolean): Boolean {
        return sp.getBoolean(key, defValues)
    }

    fun registerListener(listener: OnSharedPreferenceChangeListener?) {
        sp.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: OnSharedPreferenceChangeListener?) {
        sp.unregisterOnSharedPreferenceChangeListener(listener)
    }

    init {
        sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        editor = sp.edit()
    }
}