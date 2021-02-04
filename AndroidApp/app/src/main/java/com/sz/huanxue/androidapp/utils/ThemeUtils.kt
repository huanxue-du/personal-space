@file:Suppress("UNREACHABLE_CODE")

package com.sz.huanxue.androidapp.utils

import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.sz.huanxue.androidapp.HuanXueApp
import skin.support.SkinCompatManager
import skin.support.utils.SkinPreference

/**
 * 主题切换工具类
 * @author  huanxue
 * Created by HSAE_DCY on 2021.1.27.
 *
 */
object ThemeUtils : SkinCompatManager.SkinLoaderListener {
    private val TAG = "ThemeUtils"
    const val TECHNOLOGICAL_NAME = "technology.skin"
    private const val SPORTS_NAME = "sport.skin"
    private const val THEME_TAG = "themeTag"
    private const val DEFAULT = "0"
    private const val TECHNOLOGICAL = "1"
    private const val SPORTS = "2"
    const val DEFAULT_NAME = ""
    var changingTheme = 0
    private var skinlistener: SkinCompleteListener? = null

    init {
        SkinCompatManager.getInstance().loadSkin(this)
    }

    override fun onStart() {
        this.changingTheme = 1
    }

    override fun onSuccess() {
        this.changingTheme = 0
        skinlistener?.onSuccess()
        skinlistener = null
    }

    override fun onFailed(errMsg: String?) {
        this.changingTheme = 0
    }

    /**
     * 检查主题是否需要更新
     */
    fun checkTheme() {
        val themeTag = Settings.System.getString(HuanXueApp.getContext().contentResolver, THEME_TAG)
        Log.i("huanxue", "$TAG-----changeTheme----:$themeTag")
        if (TextUtils.isEmpty(themeTag)) {
            changeTheme(DEFAULT_NAME)
            return
        }
        when (themeTag) {
            DEFAULT -> changeTheme(DEFAULT_NAME)
            TECHNOLOGICAL -> changeTheme(TECHNOLOGICAL_NAME)
            SPORTS -> changeTheme(SPORTS_NAME)
            else -> {
            }
        }
        changeTheme("a")
    }

    /**
     * 切换到某主题
     */
    fun changeTheme(string: String) {
        val curSkinName = SkinPreference.getInstance().skinName
        if (curSkinName != string) {
            SkinCompatManager.getInstance().loadSkin(string, this, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS)
        }
    }

    fun registerSkinCompleteListener(listener: SkinCompleteListener) {
        this.skinlistener = listener
    }

    /**
     * 皮肤加载完成监听
     */
    interface SkinCompleteListener {
        fun onSuccess()
    }
}