package com.hsae.kuwo.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hsae.kuwo.KuWoInterface;
import com.hsae.kuwo.R;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.VipUserInfo;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.core.observers.IPlayControlObserver;
import cn.kuwo.core.observers.IPlayerObserver;
import cn.kuwo.core.observers.IUserInfoMgrObserver;
import cn.kuwo.core.observers.IVipMgrObserver;
import cn.kuwo.mod.lyric.LyricLine;
import cn.kuwo.open.base.MusicChargeType;
import cn.kuwo.service.PlayDelegate.ErrorCode;

/**
 * 所有酷我SDK的回调消息处理统一在该类中；
 * 所有数据在传输出去前进行非空判断，保证数据不为空
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.5.19.
 */
public class KuWoCallback implements IPlayControlObserver, IPlayerObserver, IUserInfoMgrObserver, IVipMgrObserver {

    private static final KuWoCallback sInstances = new KuWoCallback();
    private final String TAG = "KuWoCallback";
//    private RemoteCallbackList<IKuWoMusicCallBack> mCallbacks = new RemoteCallbackList<IKuWoMusicCallBack>();
    private KuWoInterface mKuWoInterface;
    /**
     * 播放混合列表中某首音乐结束时，需要等待播放器返回暂停音乐的状态
     */
    private boolean waitPause = false;

    public KuWoCallback() {

    }

    public static KuWoCallback getInstance() {
        return sInstances;
    }


   /* public void registerKuWoCallback(IKuWoMusicCallBack callback) {
        if (callback != null) {

            mCallbacks.register(callback);
        }
    }*/


   /* public void unregisterKuWoCallback(IKuWoMusicCallBack callback) {
        if (callback != null) {
            mCallbacks.unregister(callback);
        }
    }*/

    public void registerKuWoCallBack(KuWoInterface Interface) {
        this.mKuWoInterface = Interface;
    }

    public void unRegisterKuWoCallBack() {
        this.mKuWoInterface = null;
    }

    @Override
    public void IPlayControlObserver_ReadyPlay(Music music) {
        Log.i("huanxue", TAG + "---ReadyPlay:" + music.name + "   music.isEQ:" + music.isEQ());
        if (waitPause) {//由于调用KuWoSdk.getInstance().playPause()存在延时，导致不需要的歌曲信息返回给UI显示
            Log.i("huanxue", TAG + "   ReadyPlay   waitPause  is true");
            return;
        }
        //可能存在此次TTS播报是由于上次VR播放指令未执行导致的
        Music currentMusic = KuWoMemoryData.getInstance().getCurrentPlayMusic();
        if (KuWoMemoryData.getInstance().getNeedTTS() && !music.equals(currentMusic)) {
            Log.i("huanxue", TAG + "----ReadyPlay   NeedTTS    ");
//            TTSManager.sendMusicMsgToTTS(music);
            KuWoMemoryData.getInstance().setNeedTTS(false);
            //本次TTS播放结束需要恢复播放状态
            KuWoMusicAudioFocus.self().sendPlayContinueMsg();
        }
        callBackReadyPlayMusic(music);
        KuWoSdk.getInstance().getPlayingMusicBitmap();
        KuWoSdk.getInstance().getLyric();
    }

    @Override
    public void IPlayControlObserver_Play(Music music) {
        Log.i("huanxue", TAG + "---Play");
    }

    @Override
    public void IPlayControlObserver_PreSart(Music music, boolean buffering) {
        Log.i("huanxue", TAG + "---PreSart    buffering:" + buffering);
        if (buffering) {
            callBackLoadingStatus(true);
        }
    }

    @Override
    public void IPlayControlObserver_RealPlay(Music music) {
        Log.i("huanxue", TAG + "---RealPlay   music:" + music.name + "   music.isEQ:" + music.isEQ());
        Log.i("huanxue", TAG + "---RealPlay   music:" + music.name + "   music.duration:" + music.duration);
        callBackPlayStatus(true);
        callBackRealPlayMusic(music);
        callBackLoadingStatus(false);
        //新增校验ReadyPlay music 与RealPlay music，测试过程中发现会产生两者不一致的现象
        Music readyMusic = KuWoMemoryData.getInstance().getCurrentPlayMusic();
        if (music.equals(readyMusic)) {
            return;
        }
        //重新发送实际播放的歌曲信息
        KuWoSdk.getInstance().getPlayingMusicBitmap();
        KuWoSdk.getInstance().getLyric();
        KuWoMemoryData.getInstance().setCurrentPlayMusic(music);
    }

    @Override
    public void IPlayControlObserver_Pause() {
        Log.i("huanxue", TAG + "---Pause");
        waitPause = false;
        callBackPlayStatus(false);
        callBackLoadingStatus(false);
        Music music = KuWoSdk.getInstance().getNowPlayingMusic();
//        PointUtils.sendGatherDataFromCommon(PointUtils.BEHAVIOR_ID3, music, PointUtils.UNKNOW, PointUtils.UNKNOW_EMPTY, null);
    }

    @Override
    public void IPlayControlObserver_Continue() {
        Log.i("huanxue", TAG + "---Continue");
        callBackPlayStatus(true);
        //解决弱网下快速播放暂停，RealPlay接口无回调问题;通过判断是否释放过焦点，来决定是否回调歌曲信息给上层UI显示
        Music music = KuWoSdk.getInstance().getNowPlayingMusic();
        if (KuWoMusicAudioFocus.self().getHasRelease().get()) {
            callBackContinuePlayMusic(music);
            KuWoSdk.getInstance().getPlayingMusicBitmap();
            KuWoSdk.getInstance().getLyric();
            KuWoMusicAudioFocus.self().getHasRelease().set(false);
        }
        //播放酷我音乐时需要改变当前播放的列表类型
        callBackReadyPlayKuWoMusicList(KuWoMemoryData.getInstance().getPlayMixList());
//        PointUtils.sendGatherDataFromCommon(PointUtils.BEHAVIOR_ID12, music, PointUtils.UNKNOW, null, PointUtils.CONTROL_TYPE);
    }

    @Override
    public void IPlayControlObserver_Seek(int pos) {
        //歌词与真实进度显示不符的，可以考虑通过该接口进行二次校验
        Log.i("huanxue", TAG + "---Seek    pos:" + pos);
        callBackLoadingStatus(false);
    }

    @Override
    public void IPlayControlObserver_SetVolumn(int pos) {

    }

    @Override
    public void IPlayControlObserver_SetMute(boolean mute) {

    }

    @Override
    public void IPlayControlObserver_PlayFailed(ErrorCode errorCode) {
        Log.i("huanxue", TAG + "---PlayFailed--:" + errorCode);
        StringBuilder errorMsg = new StringBuilder("播放失败,");
        switch (errorCode) {
            case NETWORK_ERROR:
                errorMsg.append("网络错误");
                break;
            case NETWORK_ERROR_OOT_START:
                errorMsg.append("播放超时");
                break;
            case NETWORK_ERROR_ANTISTEALING:
                errorMsg.append("获取歌曲连接失败");
                break;
            case DECODE_FAILE:
                errorMsg.append("解码错误");
                break;
            case NO_DECODER:
                errorMsg.append("无法解码");
                break;
            case NO_HTTP_URL:
                errorMsg.append("获取歌曲url为空");
                break;
            case IO_ERROR:
                errorMsg.append("本地读写错误");
                break;
            case NO_NETWORK:
                errorMsg.append("无网络");
                KuWoSdk.getInstance().playPause();
                break;
            case NOCOPYRIGHT:
                errorMsg.append("该歌曲暂无版权");
                break;
            case NEED_VIP:
                errorMsg.append("需要购买VIP");
                break;
            case NEED_SING_SONG:
                errorMsg.append("需要购买单曲");
                break;
            case NEED_ALBUM:
                errorMsg.append("需要购买专辑");
                break;
            case ERROR_INVALID:
                errorMsg.append("无效播放数据");
                break;
            default:
                return;
        }
        KuWoToastUtils.makeText(errorMsg.toString(), Toast.LENGTH_SHORT);
        callBackPlayStatus(false);
    }

    @Override
    public void IPlayControlObserver_PlayStop(boolean end) {
        Log.i("huanxue", TAG + "---PlayStop---" + end);
        callBackPlayStatus(false);
        Music music = KuWoMemoryData.getInstance().getCurrentPlayMusic();
        if (end && KuWoMemoryData.getInstance().getPlayMixList()) {
            KuWoSdk.getInstance().playPause();
            waitPause = true;
            //触发上层继续播本网列表下一曲
            if (mKuWoInterface != null) {
                Log.i("huanxue", TAG + "----PlayStop-----callBackMediaCompletion");
                mKuWoInterface.callBackMediaCompletion();
                //从获取到要播放完成，到收到真的暂停为止，不允许readly的数据发送
            }
        }
        if (end && KuWoMemoryData.getInstance().getVipLevel() == KuWoConstants.VIP_NO) {
            if (music != null && music.is30Auditions()) {
                KuWoToastUtils.makeText(R.string.toast_audition_end, Toast.LENGTH_SHORT);
            }
        }
        if (end) {
//            PointUtils.sendGatherDataFromCommon(PointUtils.BEHAVIOR_ID11, music, PointUtils.UNKNOW, null, null);
        }
    }

    @Override
    public void IPlayControlObserver_Progress(int playPos, int bufferPos) {
//        Log.i("huanxue", TAG + "---Progress---playPos:" + playPos + "------bufferPos:" + bufferPos);
        callBackProgress(playPos, bufferPos);
    }

    @Override
    public void IPlayControlObserver_ChangeCurList() {
        //清空当前播放列表后会执行
        Log.i("huanxue", TAG + "---ChangeCurList");
        KuWoMemoryData.getInstance().setPLayingAlbumID(KuWoMemoryData.getInstance().getClickAlbumID());
        callBackChangeCurList();
    }

    @Override
    public void IPlayControlObserver_ChangePlayMode(int playmode) {
        Log.i("huanxue", TAG + "---ChangePlayMode:" + playmode);
        callBackPlayMode(playmode);
        Music music = KuWoSdk.getInstance().getNowPlayingMusic();
//        PointUtils.sendGatherDataFromCommon(PointUtils.BEHAVIOR_ID8, music, PointUtils.UNKNOW, null, null);
    }

    @Override
    public void IPlayControlObserver_WaitForBuffering() {
        Log.i("huanxue", TAG + "---WaitForBuffering---");
    }

    @Override
    public void IPlayControlObserver_WaitForBufferingFinish() {
        Log.i("huanxue", TAG + "---WaitForBufferingFinish");
    }

    @Override
    public void IPlayerObserver_ready() {
        Log.i("huanxue", TAG + "---ready");
    }

    /**
     * 所有获取到的歌曲列表需要去查询一次歌曲权限信息才可以回调给应用层显示
     *
     * @param songList 歌曲列表
     */
    public void callBackCurrentList(List<Music> songList, long albumID) {
        if (mKuWoInterface != null) {
            Log.i("huanxue", TAG + "----callBackCurrentList----songList:" + songList.size());
            //酷我歌曲列表返回客户端之前主动查询一次歌曲权限信息
            KuWoSdk.getInstance().chargeMusicList(songList, albumID);
        }
    }

    /**
     * 将歌曲列表和歌曲权限信息捆绑到一起进行回调，避免出现不同步导致的崩溃问题
     *
     * @param songList 歌曲列表
     * @param chargeResults 歌曲权限信息列表
     */
    public void callBackChargeMusic(List<Music> songList, List<MusicChargeType> chargeResults, long albumID) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackChargeMusic(songList, chargeResults, albumID);
        }
    }

    public void callBackRecommendList(List<BaseQukuItem> songList, String dataType) {
        if (mKuWoInterface != null && songList.size() > 0) {
            mKuWoInterface.callBackRecommendList(songList, dataType);
        }
    }

    public void callBackHotCategoriesList(List<BaseQukuItem> songList) {
        if (songList != null && songList.size() != 9) {
            Log.i("huanxue", TAG + "----callBackHotCategoriesList----songList:" + songList.size());
            KuWoSdk.getInstance().getHotCategories();
            return;
        }
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackHotCategoriesList(songList);
        }
    }

    public void callBackMoreCategoriesList(List<BaseQukuItem> songList, String title) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackMoreCategoriesList(songList, title);
        }
    }

    public void callBackBillBroadList(List<BaseQukuItem> songList, String dataType) {
        if (mKuWoInterface != null && songList.size() > 0) {
            mKuWoInterface.callBackBillBroadList(songList, dataType);
        }
    }

    public void callBackArtistList(List<BaseQukuItem> songList, String dataType) {
        if (mKuWoInterface != null && songList.size() > 0) {
            mKuWoInterface.callBackArtistList(songList, dataType);
        }
    }

    public void callBackNewSongList(List<BaseQukuItem> songList, String dataType) {
        if (mKuWoInterface != null && songList.size() > 0) {
            mKuWoInterface.callBackNewSongList(songList, dataType);
        }
    }

    public void callBackLyric(List<LyricLine> lyric) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackLyric(lyric);
            KuWoMemoryData.getInstance().setCurrentPlayLyric(lyric);
        }
    }

    private void callBackProgress(int playProgress, int bufferProgress) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackProgress(playProgress, bufferProgress);
        }
    }

    public void callBackPlayStatus(boolean playStatus) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackPlayStatus(playStatus);
            KuWoMemoryData.getInstance().setPlayStatus(playStatus);
        }
        sendMusicPlayState(playStatus, KuWoMemoryData.getInstance().getRecommend());
    }

    private void callBackReadyPlayMusic(Music music) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackReadyPlayMusic(music);
            KuWoMemoryData.getInstance().setCurrentPlayMusic(music);
        }
    }

    private void callBackRealPlayMusic(Music music) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackRealPlayMusic(music);
        }
    }

    private void callBackContinuePlayMusic(Music music) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackContinuePlayMusic(music);
        }
    }

    public void callBackCurrentMusicBitmap(Bitmap bitmap) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackCurrentMusicBitmap(bitmap);
            KuWoMemoryData.getInstance().setCurrentMusicBitmap(bitmap);
        }
    }

    public void callBackSearchHotKeywords(List<String> hotWords) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackSearchHotKeywords(hotWords);
        }
    }

    public void callBackSearchHistoryKeywords(List<String> hotWords) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackSearchHistoryKeywords(hotWords);
        }
    }

    public void callBackSearchResultList(List<BaseQukuItem> searchResult) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackSearchResultList(searchResult);
        }
    }

    public void callBackPlayMode(int playMode) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackPlayMode(playMode);
        }
    }


    public void callBackChangeCurList() {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackChangeCurList();
        }
    }

    public void callBackReadyPlayKuWoMusicList(boolean isPlayMixList) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackReadyPlayKuWoMusicList(isPlayMixList);
        }
    }


    public void callBackUpdateMyAlbums(List<BaseQukuItem> qukuItemList) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackUpdateMyAlbums(qukuItemList);
        }
    }

    public void callBackKuWoResult(Music music) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackKuWoResult(music);
        }
    }

    public void callBackLoadingStatus(boolean loadingStatus) {
        if (mKuWoInterface != null) {
            mKuWoInterface.callBackLoadingStatus(loadingStatus);
        }
    }


    @Override
    public void IUserInfoMgrObserver_OnLogin(boolean success, String msg, String retErrtype) {
        Log.i("huanxue", TAG + "---OnLogin---" + success + "---:" + msg + "----:" + retErrtype);
        if (success) {//登录成功
            //帐号登录成功后需要更新当前帐号对应的每日推荐歌单
            KuWoMemoryData.getInstance().setDailyMusics(new ArrayList<Music>());
            KuWoSdk.getInstance().getDailyRecommendList(false, false);
            KuWoSdk.getInstance().getRecord();
//            KuWoToastUtils.showText("酷我帐号登录成功");
//            UserInfo userInfo = UserInfoHelper.getUserInfo();
//            Log.i("huanxue", TAG + "-----OnLogin---success--" + KuWoSdk.getInstance().isUserLogon());
        } else {//登录失败
            KuWoSdk.getInstance().loginNoSense();
        }

    }

    @Override
    public void IUserInfoMgrObserver_OnUserStatusChange(boolean isOnline, String msg) {
        Log.i("huanxue", TAG + "---OnUserStatusChange" + isOnline + "---:" + msg);
    }

    @Override
    public void IUserInfoMgrObserver_OnLogout(boolean success, String msg, int logoutType) {
        Log.i("huanxue", TAG + "---OnLogout:" + success + "---:" + msg + "----:" + logoutType);
        String userId = KuWoMemoryData.getInstance().getUserId();
        if (userId.length() > 2) {
            //ID长度大于2认为是正常用户
            KuWoMemoryData.getInstance().setUserIdByPre(userId);
        }
        KuWoMemoryData.getInstance().setUserId(" ");
        //帐号退出后，需要更换当前每日推荐歌单
        KuWoMemoryData.getInstance().setDailyMusics(new ArrayList<Music>());
        KuWoSdk.getInstance().getDailyRecommendList(false, false);
        if (KuWoMemoryData.getInstance().getPLayingAlbumID() == KuWoConstants.ID_DAILY_RECOMMEND) {
            KuWoMemoryData.getInstance().setPLayingAlbumID(-1);
        }
        KuWoMemoryData.getInstance().setVipLevel(KuWoConstants.VIP_NO);
        KuWoSdk.getInstance().setPlayQuality(KuWoConstants.PLAY_QUALITY_ONE);
    }

    @Override
    public void IUserInfoMgrObserver_OnSendRegSms(boolean success, String msg, String retErrtype) {
        Log.i("huanxue", TAG + "---OnSendRegSms" + success + "---:" + msg + "----:" + retErrtype);
    }

    @Override
    public void IUserInfoMgrObserver_OnReg(boolean success, String msg, String retErrtype) {
        Log.i("huanxue", TAG + "---OnReg" + success + "---:" + msg + "----:" + retErrtype);
    }

    @Override
    public void IVipMgrObserver_OnStateChanged() {
        Log.i("huanxue", TAG + "---VipMgr---OnStateChanged");
    }

    @Override
    public void IVipMgrObserver_OnLoaded(List<VipUserInfo> vipUserInfos) {
        Log.i("huanxue", TAG + "---VipMgr---OnLoaded:   " + vipUserInfos);
        StringBuilder sb = new StringBuilder();
        if (null == vipUserInfos || vipUserInfos.size() < 1) {
            sb.append("普通用户");
            KuWoMemoryData.getInstance().setVipLevel(KuWoConstants.VIP_NO);
        } else {
            for (VipUserInfo vipinfo : vipUserInfos) {
                if (vipinfo.isValid()) {
                    if (vipinfo.mCategray == VipUserInfo.CATEGRAY_VIP) {
                        sb.append("<音乐包用户>");
                        KuWoMemoryData.getInstance().setVipLevel(KuWoConstants.VIP);
                    } else if (vipinfo.mCategray == VipUserInfo.CATEGRAY_VIP_CAR) {
                        sb.append("<车载会员>");
                        KuWoMemoryData.getInstance().setVipLevel(KuWoConstants.VIP_CAR);
                    } else if (vipinfo.mCategray == VipUserInfo.CATEGRAY_VIP_LUXURY) {
                        sb.append("<豪华VIP用户>");
                        KuWoMemoryData.getInstance().setVipLevel(KuWoConstants.VIP_LUXURY);
                    }
                    KuWoMemoryData.getInstance().setVipUserInfo(vipinfo);
                }
            }
            if (TextUtils.isEmpty(sb.toString())) {
                sb.append("普通用户");
                KuWoMemoryData.getInstance().setVipLevel(KuWoConstants.VIP_NO);
            }

        }
        Log.i("huanxue", TAG + "---VipMgr---OnLoaded: " + sb.toString());

    }

    @Override
    public void IVIPMgrObserver_OnLoadFaild(int errorcode, String msg) {
        Log.i("huanxue", TAG + "---VipMgr---OnLoadFaild:" + errorcode + "--------:" + msg);
    }

    /**
     * 发送音乐信息给外部应用
     *
     * @param music 歌曲ID3等信息
     * @param imageUrl 歌曲图片地址
     */
    public synchronized void sendMusicInfo(Music music, String imageUrl) {
      /*  Log.i("huanxue", TAG + "----sendMusicInfo----music:" + music + "---imageUrl:" + imageUrl);
        MediaInfo mediaInfo = KuWoTypeUtils.getSong(music, imageUrl);
        KuWoMemoryData.getInstance().setMediaInfo(mediaInfo);
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {

                mCallbacks.getBroadcastItem(i).onMediaInfoChange(mediaInfo);
            } catch (RemoteException e) {
                // The RemoteCallbackList will take care of removing the dead object for us.
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
//        //由于发送歌曲新歌和播放状态存在时间差，所以统一在发送歌曲信息时发送播放状态
        sendMusicPlayState(KuWoMemoryData.getInstance().getPlayStatus(), KuWoMemoryData.getInstance().getRecommend());*/
    }

    /**
     * 发送播放状态给外部应用
     *
     * @param state 播放状态
     * @param isRem 是否播放推荐音乐
     */
    public synchronized void sendMusicPlayState(boolean state, boolean isRem) {
     /*   Log.i("huanxue", TAG + "----sendMusicPlayState----state:" + state + "---isRem:" + isRem);
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i)
                        .onMediaPlayStateChanged(SourceConst.App.KUWO_MUSIC.ordinal(), state ? MediaPlayState.PLAY : MediaPlayState.PAUSE, isRem);
            } catch (RemoteException e) {
                // The RemoteCallbackList will take care of removing the dead object for us.
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();*/
    }

    /**
     * 发送推荐歌单专辑图片给外部应用
     *
     * @param coverUrl 图片Url
     */
    public synchronized void sendCoverInfo(String coverUrl) {
      /*  Log.i("huanxue", TAG + "----sendCoverInfo----coverUrl:" + coverUrl);
        KuWoMemoryData.getInstance().setCoverUrl(coverUrl);
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).onCoverInfoChange(SourceConst.App.KUWO_MUSIC.ordinal(), coverUrl);
            } catch (RemoteException e) {
                // The RemoteCallbackList will take care of removing the dead object for us.
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();*/
    }

    /**
     * 发送VIP信息给外部应用
     *
     * @param isVip vip状态
     * @param mEndDate 有效期
     */
    public synchronized void sendKuWoVipInfo(boolean isVip, long mEndDate) {
     /*   Log.i("huanxue", TAG + "----sendKuWoVipInfo----isVip:" + isVip + "---mEndDate:" + mEndDate);
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).callBackKuWoVipInfo(isVip, mEndDate);
            } catch (RemoteException e) {
                // The RemoteCallbackList will take care of removing the dead object for us.
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();*/
    }

    /**
     * 发送音乐数据给到VR
     */
    synchronized void sendInfoToVR() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
            /*    LocalMusicBean musicBean = new LocalMusicBean();
                Music music = KuWoMemoryData.getInstance().getCurrentPlayMusic();
                if (music != null) {
                    musicBean.setSong(music.name);
                    ArrayList<String> singer = new ArrayList<>();
                    singer.add(music.artist);
                    musicBean.setSinger(singer);
                    boolean isPlaying = KuWoMemoryData.getInstance().getPlayStatus();
                    boolean isVisibility = KuWoMemoryData.getInstance().getVisibility();
                    Log.i("huanxue",
                            TAG + "   sendInfoToVR   isVisibility:" + isVisibility + "    isPlaying:" + isPlaying + "   music.name:" + music.name);
                    VRProxy.getInstance().sendMusicAppVisibilityStateToHq(musicBean, isPlaying ? 1 : 2, isVisibility);
                }*/
            }
        });
    }


}
