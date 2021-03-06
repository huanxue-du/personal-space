package com.sz.huanxue.androidapp.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sz.huanxue.androidapp.ui.adapter.MyBaseRlvAdapter.BaseHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView BaseAdapter
 *
 * @author huanxue
 * Created by HSAE_DCY on 2019.12.17.
 */
public abstract class MyBaseRlvAdapter<T> extends RecyclerView.Adapter<BaseHolder> {

    Context mContext;
    List<T> mData;

    public MyBaseRlvAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mData = data;
    }


    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(getLayout(), null);
        return new BaseHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBaseRlvAdapter.BaseHolder holder, int position) {
        callbackViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract void callbackViewHolder(BaseHolder holder, int position);

    /**
     * 抽象，必须让子类重写，重写的时候给具体的布局的id，item_layout.xml
     *
     * @return 布局文件索引
     */
    public abstract int getLayout();


    /**
     * 万能适配器的Holder对象
     */
    public class BaseHolder extends RecyclerView.ViewHolder {

        //省内存 用来存放布局的控件
        SparseArray<View> viewSparseArray = new SparseArray<>();//内存效率高的集合，默认大小为10

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
        }

        //从容器中取控件
        //创建集合来封装控件的加载，是一种优化手段。减少了初始化控件的过程
        public View getView(int id) {
            View view = null;
            //从集合中根据id取控件
            view = viewSparseArray.get(id);
            //容器中没有这个控件
            if (view == null) {
                //集合中没有去itemView找
                view = itemView.findViewById(id);
                viewSparseArray.put(id, view);
            }
            return view;
        }

    }


}
