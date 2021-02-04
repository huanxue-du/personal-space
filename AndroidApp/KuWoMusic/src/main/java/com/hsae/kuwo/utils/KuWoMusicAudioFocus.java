package com.hsae.kuwo.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 负责酷我音乐音频焦点相关的逻辑处理
 */
public class KuWoMusicAudioFocus implements Handler.Callback {

    public static final int MSG_PLAY = 0x0000001;
    public static final int MSG_AUDIOFOCUS_GAIN = 0x0000008;
    public static final int MSG_AUDIOFOCUS_LOSS = 0x0000009;
    public static final int MSG_AUDIOFOCUS_LOSS_TRANSIENT = 0x0000010;
    public static final int MSG_CHECK_KWMUSIC = 0x0000011;
    private static final KuWoMusicAudioFocus sKuWoMusicAudioFocus = new KuWoMusicAudioFocus();
    private final Handler mHandler = new Handler(this);
    /**
     * 当前歌曲是否播放中
     */
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    /**
     * 酷我音乐是否拥有音源焦点
     */
    private final AtomicBoolean hasFocus = new AtomicBoolean(false);
    /**
     * VR控制酷我音乐播放
     */
    private final AtomicBoolean isNeedPlay = new AtomicBoolean(false);
    /**
     * 酷我音乐是否释放过音源焦点，为了解决Media按键切源导致的酷我音乐数据无法更新问题
     */
    private final AtomicBoolean hasRelease = new AtomicBoolean(false);
    /**
     * 酷我音乐焦点是否释放
     */
    private final AtomicBoolean audioRelease = new AtomicBoolean(true);
//    com.hsae.autosdk.source.Source autoSdkSource = com.hsae.autosdk.source.Source.getInstance();
    private AudioFocusRequest mFocusRequest;
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mOnMusicFocusChangeLisener;
    private IAudioCallBack mAudioCallback;
    private IReqFocusCallBack mFocusCallback;
//    private Source mDataHelperSource;
    /**
     * 焦点恢复时是否播放推荐歌单
     */
    private boolean isSwitchRecommend = false;
    /**
     * 规避由于快速得到、丢失焦点导致的音乐播放状态出错问题
     */
    private boolean hasHandlerMsgDelay = false;
    /**
     * 申请到延迟焦点
     */
    private boolean hasDelayFocus = false;

    public KuWoMusicAudioFocus() {
        Log.w("huanxue", "KuWoMusicAudioFocus: init");
    }

    public static KuWoMusicAudioFocus self() {
        return sKuWoMusicAudioFocus;
    }

    public AtomicBoolean getAudioRelease() {
        return audioRelease;
    }

    public AtomicBoolean getIsPlaying() {
        Log.w("huanxue", "KuWoMusicAudioFocus: getIsPlaying");
        return isPlaying;
    }

    public AtomicBoolean getIsNeedPlay() {
        return isNeedPlay;
    }

    public AtomicBoolean getHasRelease() {
        return hasRelease;
    }

    public AtomicBoolean getHasFocus() {
        return hasFocus;
    }

    public void initContext(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initFocusLisener();
//        mDataHelperSource = new Source();
    }

    public void setAudioCallback(IAudioCallBack audioCallback) {
        mAudioCallback = audioCallback;
    }

    public void setReqFocusCallback(IReqFocusCallBack focusCallback) {
        this.mFocusCallback = focusCallback;
        int focus = requestFocus();
        Log.w("huanxue", "KuWoMusicAudioFocus  setReqFocusCallback  focus:" + focus + "   focusCallback:" + focusCallback);
        if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (mFocusCallback != null) {
                mFocusCallback.callBackHasFocus();
                mFocusCallback = null;
            }
        }
    }

    private void initFocusLisener() {
        mOnMusicFocusChangeLisener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        //重新获取到焦点
                        mHandler.sendEmptyMessage(MSG_AUDIOFOCUS_GAIN);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        //不做任何处理
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        //同媒体类型抢走焦点后，释放焦点
                        mHandler.sendEmptyMessage(MSG_AUDIOFOCUS_LOSS);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        //临时性焦点抢断需要恢复
                        mHandler.sendEmptyMessage(MSG_AUDIOFOCUS_LOSS_TRANSIENT);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 申请音源焦点
     */
    public int requestFocus() {
//        if (autoSdkSource.isAllowPlay(App.KUWO_MUSIC)) {

            AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setWillPauseWhenDucked(true)
                    .setAudioAttributes(mPlaybackAttributes).setWillPauseWhenDucked(true).setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(mOnMusicFocusChangeLisener).build();

            int focus = mAudioManager.requestAudioFocus(mFocusRequest);
            Log.d("huanxue", "---KuWoMusicAudioFocus-------requestFocus:" + focus);
            switch (focus) {
                case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                    //请求焦点和获取到焦点时，下发musicType, 1 表示酷我音乐
//                    mDataHelperSource.setMusicType(KuWoConstants.MUSIC_KUWO);
                    hasFocus.set(true);
                    audioRelease.set(false);
                    //第一个参数值与isAllowPlay接口入参值一致，第二个参数传true
//                    autoSdkSource.mainAudioChanged(App.KUWO_MUSIC, true);
                    break;
                case AudioManager.AUDIOFOCUS_REQUEST_DELAYED:
//                    mDataHelperSource.setMusicType(KuWoConstants.MUSIC_KUWO);
                    hasDelayFocus = true;
                    audioRelease.set(false);
                    break;
                default:
                    Log.d("huanxue", "---KuWoMusicAudioFocus-----requestFocus----failure");
                    break;
            }
            return focus;
//        } else {
//            Log.d("huanxue", "---KuWoMusicAudioFocus----isnot   allow---requestFocus");
//        }

//        return -1;
    }

    /**
     * 音源被动失去
     */
    private void onAudioFocusLoss() {
        Log.w("huanxue", "KuWoMusicAudioFocus: onAudioFocusLoss    hasHandlerMsgDelay:" + hasHandlerMsgDelay);
        hasFocus.set(false);
        if (!hasHandlerMsgDelay) {
            isPlaying.set(KuWoMemoryData.getInstance().getPlayStatus());
        }
        KuWoSdk.getInstance().playPause();
        if (mAudioCallback != null) {
            mAudioCallback.onPassiveLostFocus();
        }
    }

    /**
     * 音源被动获取
     */
    private void onAudioFocusGain() {
        Log.w("huanxue", "KuWoMusicAudioFocus: onAudioFocusGain  isPlaying:" + isPlaying + "   isNeedPlay:" + isNeedPlay);
        hasFocus.set(true);
        if (isNeedPlay.get()) {
            startMusicControlMsg();
        } else if (isPlaying.get()) {
//            hasHandlerMsgDelay = true;
            mHandler.sendEmptyMessageDelayed(MSG_CHECK_KWMUSIC, 0);
        }
        if (hasDelayFocus) {
            Log.i("huanxue", "KuWoMusicAudioFocus  onAudioFocusGain  hasDelayFocus");
//            autoSdkSource.mainAudioChanged(App.KUWO_MUSIC, true);
            hasDelayFocus = false;
            //callback会产生一定的延时，因为涉及到播放动作，所以需要后执行
            if (mFocusCallback != null) {
                mFocusCallback.callBackHasFocus();
                mFocusCallback = null;
            }
        }
        if (mAudioCallback != null) {
            mAudioCallback.onPassiveGainFocus();
        }
    }


    /**
     * 释放音源焦点
     */
    public void releaseFocus() {
        Log.w("huanxue", "KuWoMusicAudioFocus: releaseFocus");
        hasFocus.set(false);
        mAudioManager.abandonAudioFocusRequest(mFocusRequest);
        KuWoSdk.getInstance().playPause();
        hasRelease.set(true);
        audioRelease.set(true);
        if (mAudioCallback != null) {
            mAudioCallback.onAbandonAudioFocus();
        }
    }


    /**
     * 由于IPlayControlObserver_ReadyPlay接口中需要发送TTS播报，必然导致会存在一次丢失>恢复焦点的动作
     * 所以本次恢复播放的动作只能在onAudioFocusGain方法中执行
     */
    public void sendPlayContinueMsg() {
        Log.d("huanxue", "---KuWoMusicAudioFocus----sendPlayContinueMsg  ");
        isNeedPlay.set(true);
    }

    /**
     * TTS播报，未找到歌曲，即将播放推荐歌单时调用
     */
    public void sendPlayRecommendMsg() {
        Log.d("huanxue", "---KuWoMusicAudioFocus----sendPlayContinueMsg  ");
        isNeedPlay.set(true);
        isSwitchRecommend = true;
    }

    /**
     * 恢复焦点后开始执行控制指令
     */
    private void startMusicControlMsg() {
        Log.d("huanxue", "---KuWoMusicAudioFocus----startMusicControlMsg  MSG_PLAY    isSwitchRecommend:" + isSwitchRecommend);
        isNeedPlay.set(false);
        if (isSwitchRecommend) {
            KuWoSdk.getInstance().playFinalRecommendForVR();
            isSwitchRecommend = false;
        } else {
            KuWoSdk.getInstance().playContinue();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUDIOFOCUS_GAIN:
                if (hasFocus.get()) {
                    Log.w("huanxue", "KuWoMusicAudioFocus: already has audio focus");
                    break;
                }
                onAudioFocusGain();
                break;
            case MSG_AUDIOFOCUS_LOSS:
                releaseFocus();
                break;
            case MSG_AUDIOFOCUS_LOSS_TRANSIENT:
                if (!hasFocus.get()) {
                    Log.w("huanxue", "KuWoMusicAudioFocus: already loss audio focus");
                    break;
                }
                onAudioFocusLoss();
                break;
            case MSG_CHECK_KWMUSIC:
                Log.w("huanxue", "KuWoMusicAudioFocus MSG_CHENK_KWMUSIC  isPlaying:" + isPlaying.get());
                /**
                 * isPlaying 音源管理本身获取该参数是根据丢失焦点时酷我音乐的状态，但允许VR控制酷我音乐时，修改该参数（规避酷我音乐处于播放状态时，VR控制音乐后的短暂漏音问题）
                 * hasFocus 由于添加了延迟500ms才真正恢复酷我音乐播放状态，所以需要再次判断此时是否拥有音源焦点
                 */
                if (isPlaying.get() && hasFocus.get()) {
                    KuWoSdk.getInstance().playContinue();
                }
                hasHandlerMsgDelay = false;
                break;
        }
        return false;
    }


    public interface IAudioCallBack {

        void onPassiveGainFocus();

        void onPassiveLostFocus();

        void onAbandonAudioFocus();
    }

    /**
     * requestFocus返回值 =AudioManager.AUDIOFOCUS_REQUEST_GRANTED 立即执行
     * requestFocus返回值 =AudioManager.AUDIOFOCUS_REQUEST_DELAYED 延迟执行
     */
    public interface IReqFocusCallBack {

        void callBackHasFocus();
    }
}
