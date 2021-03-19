package com.sz.huanxue.androidapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sz.huanxue.androidapp.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import androidx.annotation.Nullable;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.2.25.
 */
public class MyLoadMoreView extends LinearLayout implements SwipeRecyclerView.LoadMoreView {

    private ProgressBar mProgressBar;
    private TextView mTvMessage;
    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener;

    public MyLoadMoreView(Context context) {
        this(context, null);
    }

    public MyLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        setGravity(Gravity.CENTER);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int minHeight = (int) (displayMetrics.density * 60 + 0.5);
        setMinimumHeight(minHeight);
        inflate(context, R.layout.view_load_more, this);
        mProgressBar = findViewById(R.id.progress_load_more);
        mTvMessage = findViewById(R.id.tv_load_more_message);
    }

    @Override
    public void onLoading() {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        mTvMessage.setVisibility(VISIBLE);
        mTvMessage.setText(R.string.load_more_message);
    }

    @Override
    public void onLoadFinish(boolean dataEmpty, boolean hasMore) {
        if (!hasMore) {
            setVisibility(VISIBLE);
            mProgressBar.setVisibility(INVISIBLE);
            mTvMessage.setVisibility(VISIBLE);
            mTvMessage.setText(R.string.load_more_empty);
        } else {
            setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onWaitToLoadMore(SwipeRecyclerView.LoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;

        setVisibility(VISIBLE);
    }

    @Override
    public void onLoadError(int errorCode, String errorMessage) {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(INVISIBLE);
        mTvMessage.setVisibility(VISIBLE);
        mTvMessage.setText(R.string.load_more_empty);
    }


}
