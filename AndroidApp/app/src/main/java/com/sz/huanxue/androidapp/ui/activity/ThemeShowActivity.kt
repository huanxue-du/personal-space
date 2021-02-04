package com.sz.huanxue.androidapp.ui.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sz.huanxue.androidapp.HuanXueApp
import com.sz.huanxue.androidapp.R
import com.sz.huanxue.androidapp.ui.adapter.MoreDataAdapter
import com.sz.huanxue.androidapp.utils.LogUtils
import com.sz.huanxue.androidapp.utils.ThemeUtils
import com.yanzhenjie.recyclerview.SwipeRecyclerView

/**
 * 用于展示主题切换功能
 * @author  huanxue
 * Created by HSAE_DCY on 2021.1.27.
 *
 */
class ThemeShowActivity : MyBaseActivity() {
    private lateinit var mRlvMore: SwipeRecyclerView
    private lateinit var moreDataAdapter: MoreDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_data)
        mRlvMore = findViewById(R.id.rlv_more)
        val layoutManager = GridLayoutManager(this, 7)
        mRlvMore.layoutManager = layoutManager
        val list = arrayListOf<String>("1", "2", "3", "1", "2", "3", "1", "2", "3")
        moreDataAdapter = MoreDataAdapter(this, list)
        mRlvMore.adapter = moreDataAdapter

        /*   val moreJavaAdapter = MoreJavaAdapter(this, list);
           mRlvMore.adapter = moreJavaAdapter*/


        HuanXueApp.getMainThreadHandler().post { LogUtils.d("mhandler to log message") }
        mRlvMore.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.top = 10
                outRect.bottom = 15
            }
        })

//        mRlvMore.addItemDecoration(object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//                outRect.left = this@MoreDataActivity.getResources().getDimensionPixelOffset(R.dimen.dp_33)
//            }
//        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_theme, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                onBackPressed()
                return true
            }
            R.id.action_theme1 -> {
                ThemeUtils.changeTheme(ThemeUtils.DEFAULT_NAME)
                return true
            }
            R.id.action_theme2 -> {
                ThemeUtils.changeTheme(ThemeUtils.TECHNOLOGICAL_NAME)
                return true
            }
        }
        return false
    }
}