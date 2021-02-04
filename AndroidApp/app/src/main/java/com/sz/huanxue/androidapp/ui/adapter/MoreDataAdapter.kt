package com.sz.huanxue.androidapp.ui.adapter

import android.content.Context
import com.sz.huanxue.androidapp.R


/**
 * @author  huanxue
 * Created by HSAE_DCY on 2021.1.27.
 *
 */
class MoreDataAdapter(context: Context, data: List<String>) : MyBaseRlvAdapter<String>(context, data) {

    private val mStringData: List<String>

    override fun callbackViewHolder(holder: BaseHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.item_rlv_common
    }

    init {
        mContext = context
        mStringData = data
        mData = data
    }

    override fun getItemCount(): Int {
        return 35
    }

}