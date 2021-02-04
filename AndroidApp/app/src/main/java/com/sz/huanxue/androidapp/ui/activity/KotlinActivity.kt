package com.sz.huanxue.androidapp.ui.activity

import android.os.Bundle
import com.sz.huanxue.androidapp.R
import com.sz.huanxue.androidapp.utils.ThemeUtils


/**
 * @author  huanxue
 * Created by HSAE_DCY on 2021.1.27.
 *
 */
class KotlinActivity : MyBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtils.checkTheme()
        setContentView(R.layout.activity_clock)
        if (ThemeUtils.changingTheme == 1) {
            ThemeUtils.registerSkinCompleteListener(object : ThemeUtils.SkinCompleteListener {
                override fun onSuccess() {
                    //主题切换完成且成功，异步
                }
            })
        }
    }
}