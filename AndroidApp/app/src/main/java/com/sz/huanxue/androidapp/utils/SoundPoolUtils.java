package com.sz.huanxue.androidapp.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.util.SparseIntArray;
import com.sz.huanxue.androidapp.R;

/**
 * 需要跟随应用进程初始化
 * 解决第一次调用play不播放问题的两种方式：
 * 1、跟随应用进程直接进行初始化
 * 2、在onLoadComplete中进行判断要播哪一个
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.3.24.
 */
public class SoundPoolUtils implements OnLoadCompleteListener {

    private static SoundPoolUtils sInstance;
    private SoundPool mSoundPool;
    private SparseIntArray mSoundId = new SparseIntArray();

    private SoundPoolUtils(Context context) {
//        if (Build.VERSION.SDK_INT >= 21) {
//            SoundPool.Builder builder = new SoundPool.Builder();
//            //传入最多播放音频数量,
//            builder.setMaxStreams(1);
//            //AudioAttributes是一个封装音频各种属性的方法
//            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
//            //设置音频流的合适的属性
//            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
//            //加载一个AudioAttributes
//            builder.setAudioAttributes(attrBuilder.build());
//            soundPool = builder.build();
//        } else {
//            /**
//             * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
//             * 第二个参数：int streamType：AudioManager中描述的音频流类型 填入10才有效
//             *第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
//             */
//            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//        }
        mSoundPool = new SoundPool(1, AudioManager.STREAM_ACCESSIBILITY, 0);
        mSoundId.put(1, mSoundPool.load(context, R.raw.apa_terminated, 1));
        mSoundId.put(2, mSoundPool.load(context, R.raw.apa_failure, 1));
        mSoundId.put(3, mSoundPool.load(context, R.raw.apa_complete, 1));
        mSoundId.put(4, mSoundPool.load(context, R.raw.apa_start, 1));
        //异步需要等待加载完成，音频才能播放成功
        mSoundPool.setOnLoadCompleteListener(this);

    }


    public static SoundPoolUtils getInstance(Context mContext) {
        if (sInstance == null) {
            sInstance = new SoundPoolUtils(mContext);
        }
        return sInstance;
    }

    public void playSound(int soundType, int loop) {

        mSoundPool.play(mSoundId.get(soundType), 1, 1, 0, loop, 1);
    }

    public void playSound(int soundType) {
        switch (soundType) {
            case 0:
                mSoundPool.play(mSoundId.get(0), 1, 1, 0, 0, 1);
                break;
            case 1:
                mSoundPool.play(mSoundId.get(1), 1, 1, 0, 0, 1);
                break;
            case 2:
                mSoundPool.play(mSoundId.get(2), 1, 1, 0, 3, 1);
                break;
            case 3:
                mSoundPool.play(mSoundId.get(3), 1, 1, 0, 5, 1);
                break;
        }
    }

    /**
     * @param soundPool 当前触发事件的声音池。
     * @param sampleId 当前装载完成的音频资源在音频池中的ID。
     * @param status 状态码，展示没有意义，为预留参数，会传递0。
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.i("logcat", "----onLoadComplete-------");
        //第一个参数soundID
        //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
        //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
        //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
        //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
        //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
        //soundPool.play(mSoundId.get(1), 1, 1, 1, 0, 1);

    }

    //	当Activity意外被销毁时 进行资源的释放、
    public void releaseResource() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;

        }
        sInstance = null;
    }
}
