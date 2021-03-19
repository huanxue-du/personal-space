package com.sz.huanxue.androidapp.ui.activity;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.3.11.
 */
public class LyricActivity extends MyBaseActivity{

    private void initLyric() {
     /*   mRlvFullLyric = (RecyclerView) findViewById(R.id.rlv_full_lyric);
        mRlvFullLyric.setOnTouchListener(new OnTouchListener() {//屏蔽用户手动触摸歌词
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mRlvFullLyric.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i("huanxue", TAG + "  OnScrollListener  newState:" + newState);
                if (mLyricStatus != newState) {
                    if (mLyricStatus == RecyclerView.SCROLL_STATE_SETTLING && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int firstPos = mLinearLayoutManager.findFirstVisibleItemPosition();
                        int lastPos = mLinearLayoutManager.findLastVisibleItemPosition();
                        Log.i("huanxue", TAG + "  OnScrollListener  firstPos:" + firstPos + "  lastPos:" + lastPos + "  mCurrentPos:" + mCurrentPos);
                        //当高亮显示不在屏幕显示范围内时，校准歌词显示.
                        //歌词直接拉取到底部时，不用处理
                        if (mCurrentPos < firstPos || mCurrentPos > lastPos) {
                            Log.i("huanxue", TAG + "  OnScrollListener   checked  mCurrentPos  into  VISIBLE");
                            mRlvFullLyric.scrollToPosition(mCurrentPos);
                        }
                    }
                    mLyricStatus = newState;
                }

            }
        });*/
    }

    /**
     * 动态改变进度球的显示，解决进度球首尾显示超出seekbar问题
     *
     * @param maxProgress 进度条最大值
     * @param currentProgress 当前进度值
     */
    private void changeProgressThumb(int maxProgress, int currentProgress) {
        if (maxProgress == 0 || currentProgress == 0) {
            return;
        }
        int percentage = (currentProgress * 1000) / maxProgress;//千分比值
        /**
         * 76 thumb width
         * 13 thumb nums
         * 986 seekbar width
         */
    /*    int percentage_unit = ((76 * 1000) / 13) / 986;//每一级的单位比值
        //进度球从短变长的过程
        if (percentage >= (percentage_unit * 12)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb13));
        } else if (percentage > (percentage_unit * 11)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb12));
        } else if (percentage >= (percentage_unit * 10)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb11));
        } else if (percentage >= (percentage_unit * 9)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb10));
        } else if (percentage >= (percentage_unit * 8)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb9));
        } else if (percentage >= (percentage_unit * 7)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb8));
        } else if (percentage >= (percentage_unit * 6)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb7));
        } else if (percentage >= (percentage_unit * 5)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb6));
        } else if (percentage >= (percentage_unit * 4)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb5));
        } else if (percentage >= (percentage_unit * 3)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb4));
        } else if (percentage >= (percentage_unit * 2)) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb3));
        } else if (percentage >= percentage_unit) {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb2));
        } else {
            mProgressbarKuwo.setThumb(SkinCompatResources.getDrawable(this, R.mipmap.seekbar_thumb1));
        }
        //解决进度球与进度条不匹配问题
        if (percentage > 990) {
            mProgressbarKuwo.setThumbOffset(66);
        } else {
            mProgressbarKuwo.setThumbOffset(56);
        }*/
    }


    /**
     * 计算当前需要高亮歌词的pos,正常播放时的歌词显示
     */
    public void updateLyricProgress(int playPos) {
 /*       if (isLyricBottom) {
            Log.i("huanxue", TAG + " lyric hsa been bottom  ");
            return;
        }
        if (playPos < 1000 && !isFirstCreate) {
            //视为从头播放
            updateLyricBySeekProgress(0);
            Log.i("huanxue", TAG + "  play again  so  updateLyricBySeekProgress");
            return;
        }
        int size = mAdapter.getData().size();
        int position = mCurrentPos;
        if (size >= 2 && mAdapter.getData().get(size - 1).getTime() < playPos) {
            mCurrentPos = size - 1;
            mAdapter.setCurrentPos(mCurrentPos);
            mAdapter.notifyDataSetChanged();
            isLyricBottom = true;
            Log.i("huanxue", TAG + " lyric is bottom   lyric.size:" + size + "---mCurrentPos:" + mCurrentPos);
            return;
        }
        // USB音乐每秒检查歌词，可能造成歌词延时滚动。添加检查时如果当前进度与即将开始的歌词误差仅500ms，就直接滚动
        long forward = mSource.getMusicType() == Constants.MUSIC_USB ? 500 : 0;
        for (int i = position; i < size; i++) {
            int lyricTime = mAdapter.getData().get(i).getTime();
            if (lyricTime >= playPos + forward) {
                if (i < 1) {
                    position = 0;
                } else {
                    position = i - 1;
                }

                if (mCurrentPos != position) {
                    mCurrentPos = position;
                    if (mCurrentPos > 2) {
                        mRlvFullLyric.smoothScrollToPosition(mCurrentPos + 2);
                    } else {
                        mRlvFullLyric.smoothScrollToPosition(0);
                    }
                    Log.i("huanxue", TAG + "---updateLyricProgress----" + mCurrentPos);
                    mAdapter.setCurrentPos(mCurrentPos);
                    mAdapter.notifyDataSetChanged();
                }
                return;
            }
        }*/
    }

    /**
     * 手动触摸进度条后歌词的显示
     */
    private void updateLyricBySeekProgress(int seekPos) {
      /*  isLyricBottom = false;
        boolean isMoveToLeft = false;
        int size = mAdapter.getData().size();
//        Log.i("huanxue", TAG + "---updateLyricBySeekProgress----size：" + size);
        if (size == 0) {
            return;
        }
        if (seekPos <= mAdapter.getData().get(0).getTime()) {//考虑歌词被默认添加一行
            //小于第一行歌词
            mCurrentPos = 0;
        } else if (seekPos > mAdapter.getData().get(size - 2).getTime()) {//考虑歌词最后被添加一行
            //大于最后一行歌词
            mCurrentPos = size - 1;
        } else {
            //中间歌词显示
            for (int i = 0; i < size; i++) {
                if (mAdapter.getData().get(i).getTime() > seekPos) {
                    isMoveToLeft = mCurrentPos > i;
                    mCurrentPos = i - 1;
                    break;
                }
            }
        }
        if (mCurrentPos < 5) {
            mRlvFullLyric.scrollToPosition(0);
        } else {
            if (isMoveToLeft) {
                mRlvFullLyric.scrollToPosition(mCurrentPos - 2);
            } else {
                mRlvFullLyric.scrollToPosition(mCurrentPos + 2);
            }
        }
        Log.i("huanxue", TAG + "---updateLyricBySeekProgress----seekPos:" + seekPos + "   mCurrentPos:" + mCurrentPos);
        mAdapter.setCurrentPos(mCurrentPos);
        mAdapter.notifyDataSetChanged();*/
    }
}
