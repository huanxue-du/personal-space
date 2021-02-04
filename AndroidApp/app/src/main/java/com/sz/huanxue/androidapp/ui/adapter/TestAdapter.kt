package com.sz.huanxue.androidapp.ui.adapter

import android.content.Context
import android.widget.TextView
import com.sz.huanxue.androidapp.R

/**
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.18.
 */
class TestAdapter(context: Context, data: List<String?>) : MyBaseRlvAdapter<String?>(context, data) {

    private val mStringData: List<String>
    override fun callbackViewHolder(holder: BaseHolder, position: Int) {
        if (position % 2 == 0) {
            val view = holder.getView(R.id.ku_usm_list_item_text) as TextView
            view.setText(R.string.text_welcome)
        } else {
            val view = holder.getView(R.id.ku_usm_list_item_text) as TextView
            view.text = mStringData[position]
        }
    }

    override fun getLayout(): Int {
        return R.layout.simple_list_item_textview_only
    }

    init {
        mContext = context
        mStringData = data as List<String>
    }
}