package com.autolink.radio55.adapter;

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

public class RadioCollAdapter extends BaseAdapter {

    private Context mContext;
    private LinkedList<RadioEntity> mLinkedList = new LinkedList<RadioEntity>();
    private LayoutInflater mInflater;

    public RadioCollAdapter(Context context, int band) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        getData(band);
    }

    public void getData(int band) {
        if (band == RadioManager.VALUE_BAND_FM) {
            AppDataUtils.getInstance().UpdateFmCollList();
            this.mLinkedList = AppDataUtils.getInstance().queryFmCollRadiosFromData();
        } else if (band == RadioManager.VALUE_BAND_AM) {
            AppDataUtils.getInstance().UpdateAmCollList();
            this.mLinkedList = AppDataUtils.getInstance().queryAmCollRadiosFromData();
        }

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mLinkedList == null ? 0 : mLinkedList.size();
    }

    @Override
    public Object getItem(int position) {
        return mLinkedList == null ? null : mLinkedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HoldView view;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.radio_list_item_coll, null);
            view = new HoldView();
            view.frequency = (TextView) convertView.findViewById(R.id.amOrFm_list_freq_show);
            view.frequencyType = (TextView) convertView.findViewById(R.id.amOrFm_list_band_show);
            view.collect_iamge_iv = (ImageView) convertView.findViewById(R.id.collect_iamge_iv);
            view.collect_delete_coll = (ImageView) convertView.findViewById(R.id.collect_delete_coll);
            convertView.setTag(view);
        } else {
            view = (HoldView) convertView.getTag();
        }


        final RadioEntity mEntity = mLinkedList.get(position);
        String frep = RadioDataUtils.getFregp(mEntity.getFrequency(), mEntity.getType());//用于显示
        String band = mEntity.getType();
        final String freqs = mEntity.getFrequency();//用于下发
        view.frequency.setText(frep);
        view.frequencyType.setText(band);
        view.collect_delete_coll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBackColl != null) {
                    //触发删除操作，即调用数据库方法修改收藏标识。需考虑是否需要回调到主页面UI以继续操作
                    mCallBackColl.CallBackDeleteFreq(freqs);
                }
            }
        });

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (AppDataUtils.getInstance().isReplaceColl) {
                    mCallBackColl.CallBackReplaceColl(mEntity);
                    return;
                }
                int frep = Integer.parseInt(freqs);
                mCallBackColl.CallBackOnclickItem(frep, AppDataUtils.getInstance().getBand());
            }
        });


        if ((AppDataUtils.getInstance().getMainFreq() + "").equals(mLinkedList.get(position).getFrequency())) {
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

    private CallBackColl mCallBackColl;

    public void registerRadioCollAdapterCallBack(CallBackColl mCallBackColl) {
        this.mCallBackColl = mCallBackColl;
    }

    public void setSelectItem(int band) {
        RadioEntity mEntity = AppDataUtils.getInstance().getFocusRadioEntity(AppDataUtils.RADIO_LIST_TYPE.COLL_LIST);

        if (band == RadioManager.VALUE_BAND_FM) {
            mCallBackColl.CallBackSelectionIndex(mEntity.getIndex()-1, AppDataUtils.ADAPTER_COLL_FM);
        } else if (band == RadioManager.VALUE_BAND_AM) {
            mCallBackColl.CallBackSelectionIndex(mEntity.getIndex()-1, AppDataUtils.ADAPTER_COLL_AM);
        }


    }
}