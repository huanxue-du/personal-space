package com.sz.huanxue.androidapp.ui.adapter;

import android.content.Context;

import com.sz.huanxue.androidapp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 展示歌词的适配器
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.7.24.
 */
public class LyricAdapter extends MyBaseRlvAdapter {

    private static final String TAG = "LyricAdapter";
    private Context mContext;//上下文
    private List<String> mData = null;//数据源
    /**
     * 当前需要高亮显示歌词的pos
     */
    private int mCurrentPos = -1;

    public LyricAdapter(Context context, List<String> data) {
        super(context, data);
        mContext = context;
        mData = data;
    }

    public int getCurrentPos() {
        return mCurrentPos;
    }

    public void setCurrentPos(int currentPos) {
        mCurrentPos = currentPos;
    }

    public List<String> getData() {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        return mData;
    }

    public void setData(List<String> lyrics) {
        mData.clear();
    /*    if (lyrics.size() != 0) {
            this.mData.add(new LyricLine(0, " "));
            this.mData.addAll(lyrics);
            this.mData.add(new LyricLine(Integer.MAX_VALUE, " "));
        } else {
            mData.add(new LyricLine(0, " "));
            mData.add(new LyricLine(0, " "));
            mData.add(new LyricLine(1, mContext.getResources().getString(R.string.no_play_lyric)));
        }*/
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        } else {
            return mData.size();
        }
    }

    @Override
    public void callbackViewHolder(BaseHolder holder, int position) {
  /*   LyricLine lyricLine = mData.get(position);
        if (lyricLine != null) {
            TextView textView = (TextView) holder.getView(R.id.tv_lyric);
            textView.setText(lyricLine.getLyric());
            if (mCurrentPos == position) {//歌词高亮显示
                textView.setSelected(true);
                Log.i("huanxue", TAG + "  binding  mCurrentPos:" + mCurrentPos + "  position:" + position);
            } else {
                textView.setSelected(false);
            }
        }*/
    }

    @Override
    public int getLayout() {
        return R.layout.item_rlv_lyric;
    }


}
