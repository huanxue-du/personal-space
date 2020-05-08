package com.sz.huanxue.androidapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sz.huanxue.androidapp.R;


/**
 * listview  baseAdapter
 * @author huanxue
 * Created by Administrator on 2019/5/8.
 */
public class MyListAdapter extends BaseAdapter {

    private String[] mListStrings;
    private Context mContext;
    private LayoutInflater mInflater;

    public MyListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
//        this.mListStrings = context.getResources().getStringArray(R.array.list_usm_array);
    }


    @Override
    public int getCount() {
        return mListStrings.length;
    }

    @Override
    public Object getItem(int position) {
        return mListStrings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.simple_list_item_textview_only, null);
            holder = new ViewHolder(convertView);
            holder.text1.setText(mListStrings[position]);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    private class ViewHolder {

        private final TextView text1;
        private final View root;

        private ViewHolder(View root) {
            text1 = (TextView) root.findViewById(R.id.ku_usm_list_item_text);
            this.root = root;
        }
    }
}
