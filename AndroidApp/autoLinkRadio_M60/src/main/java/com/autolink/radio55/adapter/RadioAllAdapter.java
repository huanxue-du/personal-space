package com.autolink.radio55.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.autolink.radio55.R;
import com.autolink.radio55.app.AppDataUtils;
import com.autolink.radio55.utils.RadioDataUtils;
import com.autolink.serial.mcu.manager.radio.RadioManager;

import java.util.LinkedList;

public class RadioAllAdapter extends BaseAdapter {

    private Context mContext;
    private LinkedList<RadioEntity> mLinkedList = new LinkedList<RadioEntity>();
    private LayoutInflater mInflater;
    private  RadioManager manager ;
    public RadioAllAdapter(Context context, int band) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        getData(band);

    }

    public void getData(int band) {
        this.mLinkedList = AppDataUtils.getInstance().getAllRadioData(band);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mLinkedList.size();
    }


    @Override
    public Object getItem(int position) {
        return mLinkedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HoldView view;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.radio_list_item_all, null);
            view = new HoldView();
            view.frequency = (TextView) convertView.findViewById(R.id.amOrFm_list_freq_show);
            view.frequencyType = (TextView) convertView.findViewById(R.id.amOrFm_list_band_show);
            view.collect_iamge_iv = (ImageView) convertView.findViewById(R.id.collect_iamge_iv);
            view.collect_delete_coll = (ImageView) convertView.findViewById(R.id.collect_delete_coll);
            convertView.setTag(view);
        } else {
            view = (HoldView) convertView.getTag();

        }

        RadioEntity mEntity = mLinkedList.get(position);
        String frep = RadioDataUtils.getFregp(mEntity.getFrequency(), mEntity.getType());//用于显示
        String band = mEntity.getType();
        final String freqs = mEntity.getFrequency();//用于下发
        view.frequency.setText(frep);
        view.frequencyType.setText(band);
        view.collect_delete_coll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBackAll.CallBackCollFreq(freqs);
            }
        });

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int frep = Integer.parseInt(freqs);
                mCallBackAll.CallBackOnclickItem(frep, AppDataUtils.getInstance().getBand());

            }
        });

            /*
             * 判断心型收藏图标的显示
			 */
        if (AppDataUtils.getInstance().getCollData(freqs)) {
            view.collect_delete_coll.setImageResource(R.drawable.item_coll_p);
        } else {
            view.collect_delete_coll.setImageResource(R.drawable.item_coll_n);
        }


        //             设置高亮
        if ((AppDataUtils.getInstance().getMainFreq() + "").equals(freqs)) {
            view.frequencyType.setSelected(true);
            view.frequency.setSelected(true);
            view.collect_iamge_iv.setBackgroundResource(R.drawable.radio_play_list_play_indicator);
            AnimationDrawable animationDrawable = (AnimationDrawable) view.collect_iamge_iv.getBackground();
            animationDrawable.start();
            view.collect_iamge_iv.setVisibility(View.VISIBLE);
        } else {
            view.frequencyType.setSelected(false);
            view.frequency.setSelected(false);
            view.collect_iamge_iv.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }


    class HoldView {
        TextView frequency;
        TextView frequencyType;
        ImageView collect_iamge_iv;
        ImageView collect_delete_coll;
    }


    protected CallBackAll mCallBackAll;

    public void regedistCallBack(CallBackAll mCallBackAll) {
        this.mCallBackAll = mCallBackAll;
    }

    /**
     * 适配器刷新选中电台到最上方显示
     */
    public void setSelectItem(int band) {
        RadioEntity mEntity = AppDataUtils.getInstance().getFocusRadioEntity(AppDataUtils.RADIO_LIST_TYPE.LIST_AM_FM);
        int position = mLinkedList.indexOf(mEntity);
        if (band == RadioManager.VALUE_BAND_FM) {
            mCallBackAll.CallBackSelectionIndex(position, AppDataUtils.ADAPTER_FM);
        } else if (band == RadioManager.VALUE_BAND_AM) {
            mCallBackAll.CallBackSelectionIndex(position, AppDataUtils.ADAPTER_AM);
        }


    }

}