package com.hsae.kuwo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hsae.kuwo.R;
import com.hsae.kuwo.bean.LoginInfo;
import com.hsae.kuwo.bean.OpenRecord;
import com.hsae.kuwo.db.HistorySearchDBManager;
import com.hsae.kuwo.db.HotCategoryDBManager;
import com.hsae.kuwo.net.NetWorkManager;
import com.hsae.kuwo.utils.KuWoMusicAudioFocus.IReqFocusCallBack;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.kuwo.base.bean.ListType;
import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.MusicList;
import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.online.OnlineRootInfo;
import cn.kuwo.base.bean.quku.AlbumInfo;
import cn.kuwo.base.bean.quku.ArtistInfo;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.BaseQukuItemList;
import cn.kuwo.base.bean.quku.MusicInfo;
import cn.kuwo.base.bean.quku.SongListInfo;
import cn.kuwo.core.messagemgr.MessageManager;
import cn.kuwo.login.open.UserLoginUtils;
import cn.kuwo.mod.ModMgr;
import cn.kuwo.mod.PlayMusicHelper;
import cn.kuwo.mod.lyric.LyricLine;
import cn.kuwo.mod.quku.QukuRequestState;
import cn.kuwo.mod.userinfo.UserInfoHelper;
import cn.kuwo.open.FavoriteType;
import cn.kuwo.open.ImageSize;
import cn.kuwo.open.KwApi;
import cn.kuwo.open.KwApi.OnFetchListener;
import cn.kuwo.open.OnCategoriesHotTagListener;
import cn.kuwo.open.OnDailyRecommendFetchListener;
import cn.kuwo.open.OnHotKeywordsFetchListener;
import cn.kuwo.open.OnImageFetchListener;
import cn.kuwo.open.OnMusicsChargeListener;
import cn.kuwo.open.base.MusicChargeType;
import cn.kuwo.open.base.SearchType;
import cn.kuwo.service.PlayProxy.Status;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.hsae.kuwo.utils.KuWoConstants.CHARGE_ACTIONTYPE;
import static com.hsae.kuwo.utils.KuWoConstants.CHARGE_QUALITY;
import static com.hsae.kuwo.utils.KuWoConstants.MUSIC_ONLINE_PAGE_FIRST;
import static com.hsae.kuwo.utils.KuWoConstants.VR_MUSICLIST_SIZE;
import static com.hsae.kuwo.utils.KuWoConstants.VR_SEARCH_ARTIST_SIZE;
import static com.hsae.kuwo.utils.KuWoConstants.VR_SEARCH_MUSIC_SIZE;


/**
 * 调用酷我SDK  api
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.5.22.
 */
public class KuWoSdk implements OnFetchListener {

    private static final KuWoSdk sInstances = new KuWoSdk();
    private static final int MIN_NETWORK_ERROR_TOAST_TIME = 3000;//调用kuwosdk提示网络异常的时间间隔
    /**
     * 记录上次执行时间
     */
    private static long lastTime = 0;
    private final String TAG = "KuWoSdk";

    public KuWoSdk() {
      /*  if (!KuWoMemoryData.getInstance().isLowOption()) {
            Log.i("huanxue", TAG + "KuWoSdk  init   TokenListener");
            UserProfileProxy.getInstance().registerListener(new TokenListener());
        } else {
            Log.i("huanxue", TAG + "current version  info  is low");
        }*/
    }

    public static KuWoSdk getInstance() {
        if (!NetWorkManager.getInstance().isNetworkConnected()) {
            if (onToastDisplay()) {
                KuWoToastUtils.makeText(R.string.toast_network_error, Toast.LENGTH_SHORT);
            }
        }
        return sInstances;
    }

    /***
     * 控制网络异常提示
     * @return
     */
    private static boolean onToastDisplay() {
        long currentTime = SystemClock.elapsedRealtime();
        Log.d("huanxue", "KuWoSdk---onToastDisplay----currentTime:" + currentTime + "----lastTime:" + lastTime);
        if ((currentTime - lastTime) >= MIN_NETWORK_ERROR_TOAST_TIME) {
            lastTime = currentTime;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 默认播放当前展示的列表及用户点击的position
     *
     * @param pos 角标
     * @param maxPlayList 歌曲上限
     */
    public void playMusics(final int pos, final int maxPlayList) {
        final List<Music> currentMusics = KuWoMemoryData.getInstance().getCurrentShowList();
        //真正开始播放之前，根据当前用户VIP状态进行歌曲列表筛选过滤（只有触发列表替换时才需要）
        if (currentMusics != null) {
            Log.d("huanxue", TAG + "---playMusics   onChargeSuccess---start:");
            KwApi.chargeMusics(CHARGE_ACTIONTYPE, CHARGE_QUALITY, currentMusics, new OnMusicsChargeListener() {
                @Override
                public void onChargeSuccess(List<Music> chargeMusics, List<MusicChargeType> chargeResults) {
                    if (chargeResults != null && chargeResults.size() > 0 && chargeMusics.size() > 0) {
                        Log.d("huanxue",
                                TAG + "---playMusics   onChargeSuccess---musics.size:" + chargeMusics.size() + "---chargeResults.size:" + chargeResults
                                        .size());
                        KuWoMemoryData.getInstance().setPlayingList(chargeMusics);
                        KuWoMemoryData.getInstance().setChargePlayingList(chargeResults);
                        List<Music> playlist = new ArrayList<>();
                        Music clickMusic = chargeMusics.get(pos);
                        int count = chargeMusics.size();
                        Log.d("huanxue", TAG + "---playMusics   onChargeSuccess---count:" + count);
                        if (KuWoMemoryData.getInstance().getVipLevel() == KuWoConstants.VIP_NO) {//非VIP用户
                            for (int i = 0; i < count; i++) {
                                MusicChargeType chargeType = chargeResults.get(i);
                                Music music = chargeMusics.get(i);
                                switch (chargeType) {
                                    case FREE:
                                        playlist.add(music);
                                        break;
                                    case NEED_VIP:
                                    case NEED_VIP_SONG:
                                    case NEED_VIP_ALBUM:
                                        if (music.is30Auditions()) {
                                            playlist.add(music);
                                        }
                                        break;
                                }
                                if (playlist.size() >= maxPlayList) {
                                    break;
                                }
                            }
                        } else {//Vip用户
                            for (int i = 0; i < count; i++) {
                                MusicChargeType chargeType = chargeResults.get(i);
                                Music music = chargeMusics.get(i);
                                switch (chargeType) {
                                    case FREE:
                                    case NEED_VIP:
                                    case NEED_VIP_SONG:
                                    case NEED_VIP_ALBUM:
                                        playlist.add(music);
                                        break;
                                }
                                if (playlist.size() >= maxPlayList) {
                                    break;
                                }
                            }
                        }
                        int finalPlayPos = playlist.indexOf(clickMusic);
                        if (finalPlayPos < 0) {
                            finalPlayPos = 0;
                        }
                        //替换列表经过处理后交给播放器开始播放
                        Log.d("huanxue",
                                TAG + "---playMusics   onChargeSuccess---playlist.size:" + playlist.size() + "----finalPlayPos:" + finalPlayPos);
                        playKuWoSDK(KuWoConstants.PLAY_TYPE_REPLACEANDPLAY, null, playlist, false, finalPlayPos);
                    }
                }

                @Override
                public void onChargeFaild(String msg) {
                    KuWoToastUtils.makeText(R.string.toast_charge_faild, Toast.LENGTH_SHORT);
                }
            });
        }
    }

    /**
     * 适用于当前播放列表的点击事件，不变更当前播放列表
     */
    public void playCurrentPlayList(int pos) {
        List<Music> musics = getNowPlayingMusicList();
        playKuWoSDK(KuWoConstants.PLAY_TYPE_CURRENT_PLAYLIST, null, musics, false, pos);
    }

    /**
     * 单曲在线音乐歌曲播放，包含本网融合中的收藏和历史记录
     */
    public void playSingleMusic(Music music, boolean mixList) {
        KuWoMemoryData.getInstance().setPLayingAlbumID(0);
        playKuWoSDK(KuWoConstants.PLAY_TYPE_SINGLE, music, null, mixList, -1);
    }

    /**
     * 真正调用酷我SDK中的播放方法
     */
    private void playKuWoSDK(final int playType, final Music singleMusic, final List<Music> musics, final boolean mixList, final int pos) {
        Log.d("huanxue", TAG + "---playKuWoSDK--playType:" + playType + "---singleMusic:" + singleMusic + "---musics:" + String
                .valueOf(musics != null ? musics.size() : 0) + "---mixList:" + mixList + "---pos:" + pos);
        KuWoMusicAudioFocus.self().setReqFocusCallback(new IReqFocusCallBack() {
            @Override
            public void callBackHasFocus() {
                KuWoMemoryData.getInstance().setPlayMixList(mixList);
                //调用播放时，非混合列表即为纯酷我音乐歌单
                KuWoCallback.getInstance().callBackReadyPlayKuWoMusicList(mixList);
                if (mixList) {
                    KuWoMemoryData.getInstance().setPLayingAlbumID(0);
                }
                switch (playType) {
                    case KuWoConstants.PLAY_TYPE_REPLACEANDPLAY:
                        PlayMusicHelper.replaceAndPlay(musics, pos);
                        break;
                    case KuWoConstants.PLAY_TYPE_SINGLE:
                        PlayMusicHelper.play(singleMusic);
                        break;
                    case KuWoConstants.PLAY_TYPE_MUSICLIST:
//                    PlayMusicHelper.playMusicList(musicLists, pos);
                        break;
                    case KuWoConstants.PLAY_TYPE_CURRENT_PLAYLIST:
                        PlayMusicHelper.play(musics, pos);
                        break;
                }
                BaseQukuItem recommendAlbum = KuWoMemoryData.getInstance().getPlayingForLauncher();
                if (recommendAlbum == null) {
                    KuWoMemoryData.getInstance().setRecommend(false);
                } else {
                    if (Long.valueOf(KuWoMemoryData.getInstance().getPLayingAlbumID())
                            .equals(KuWoMemoryData.getInstance().getPlayingForLauncher().getId())) {
                        KuWoMemoryData.getInstance().setRecommend(true);
                    } else {
                        KuWoMemoryData.getInstance().setRecommend(false);
                    }
                }
//                OtherUtils.setNoMute();
            }
        });
    }

    /**
     * 播放或暂停
     */
    public void playOrPause() {
        Status status = getStatus();
        Log.d("huanxue", TAG + "----playOrPause---PlayProxy.Status:" + status);
      /*  if (new Source().getMusicType() != KuWoConstants.MUSIC_KUWO) {
            Log.d("huanxue", TAG + "---current play isnot kuwoMusic");
            return;
        }*/
        if (getNowPlayingMusic() == null) {
            Log.d("huanxue", TAG + "---current play music is null");
            return;
        }
        switch (status) {
            case PLAYING:
            case BUFFERING:
                playPause();
                break;
            case PAUSE:
            case INIT:
            case STOP:
                playContinue();
                break;
        }
    }

    /**
     * 获取当前播放器状态
     *
     * @return 播放器状态
     */
    public Status getStatus() {
        return ModMgr.getPlayControl().getStatus();
    }

    /**
     * 播放下一曲
     */
    public void playNext() {
        if (getNowPlayingMusic() == null) {
            return;
        }
        Log.d("huanxue", TAG + "-----playNext:");
        KuWoMusicAudioFocus.self().setReqFocusCallback(new IReqFocusCallBack() {
            @Override
            public void callBackHasFocus() {
                ModMgr.getPlayControl().playNext();
//                PointUtils.sendGatherDataFromCommon(PointUtils.BEHAVIOR_ID7, getNowPlayingMusic(), PointUtils.UNKNOW, null, PointUtils.CONTROL_TYPE);
            }
        });
    }

    /**
     * 播放上一曲
     */
    public void playPre() {
        if (getNowPlayingMusic() == null) {
            return;
        }
        Log.d("huanxue", TAG + "-----playPre:");
        KuWoMusicAudioFocus.self().setReqFocusCallback(new IReqFocusCallBack() {
            @Override
            public void callBackHasFocus() {
                ModMgr.getPlayControl().playPre();
//                PointUtils.sendGatherDataFromCommon(PointUtils.BEHAVIOR_ID6, getNowPlayingMusic(), PointUtils.UNKNOW, null, PointUtils.CONTROL_TYPE);
            }
        });
    }

    /**
     * 暂停播放
     */
    public void playPause() {
        Log.d("huanxue", TAG + "-----playPause:");
        ModMgr.getPlayControl().pause();
    }

    /**
     * 停止播放
     */
    public void playStop() {
        Log.d("huanxue", TAG + "-----playStop:");
        ModMgr.getPlayControl().stop();
    }

    /**
     * 继续播放
     *
     * @return 执行结果
     */
    public boolean playContinue() {
        Log.d("huanxue", TAG + "-----playContinue:");
        Music music = getNowPlayingMusic();
        if (music != null) {
            int focus = KuWoMusicAudioFocus.self().requestFocus();
            if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                boolean isSuccess = ModMgr.getPlayControl().continuePlay();
//                OtherUtils.setNoMute();
                if (!isSuccess) {
                    KuWoToastUtils.makeText(R.string.toast_network_error, Toast.LENGTH_SHORT);
                }
                Log.d("huanxue", TAG + "----playContinue----isSuccess:" + isSuccess);
                return isSuccess;
            } else if (focus == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                //获取到延迟焦点时，必然是VR控制，需要得到焦点时恢复播放
                KuWoMusicAudioFocus.self().sendPlayContinueMsg();
                return true;
            }
        }
        KuWoCallback.getInstance().callBackPlayStatus(false);
        return false;
    }

    /**
     * 获取当前播放模式
     *
     * @return 播放模式类型
     */
    public int getPlayMode() {
        return ModMgr.getPlayControl().getPlayMode();
    }

    /**
     * 提供给VR使用，确定要设置某种播放模式
     */
    public void setPlayMode(int playMode) {
        Log.d("huanxue", TAG + "---setPlayMode---" + playMode);
        ModMgr.getPlayControl().setPlayMode(playMode);
    }

    /**
     * 设置当前播放模式,默认是顺序播放
     * 切换顺序为：随机播放、单曲循环、循环播放
     */
    public void setPlayMode() {
        Log.d("huanxue", TAG + "-----setPlayMode:");
        int playMode = getPlayMode();
        switch (playMode) {
            case KuWoConstants.MODE_SINGLE_CIRCLE:
                playMode = 2;
                break;
            case KuWoConstants.MODE_ALL_ORDER:
                playMode = 3;
                break;
            case KuWoConstants.MODE_ALL_CIRCLE:
                playMode = 1;
                break;
            case KuWoConstants.MODE_ALL_RANDOM:
                playMode = 0;
                break;
        }
        Log.d("huanxue", TAG + "---setPlayMode---" + playMode);
        ModMgr.getPlayControl().setPlayMode(playMode);
    }

    /**
     * 服务启动&网络可用后，一次性拉取更多推荐、新歌、排行榜、歌手、分类标签等数据
     * 需求保证每次上电周期内上述专辑不会改变，新歌&歌手&分类歌单拉取默认30，其它为全部数据
     */
    public synchronized void requestAllFindTabDatas(final String dataType) {
        /**
         * 更多推荐歌单
         */
        if (KuWoMemoryData.getInstance().getAllRecommendAlbums().size() == 0) {
            KwApi.fetchRecommendSongList(new OnFetchListener() {
                @Override
                public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                    if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                        List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                        List<BaseQukuItem> itemList = new ArrayList<>();
                        Log.d("huanxue", TAG + "-----onFetched----RecommendSongList:" + s + "---OnlineRootInfo:" + onlineRootInfo);
                        for (BaseOnlineSection section : list) {
                            String label = section.getLabel();
//                            Log.i("huanxue", TAG + "----name:" + label + section.getType());
                            if ("banner".equals(section.getType())) {
                                continue;
                            }
                            for (BaseQukuItem item : section.getOnlineInfos()) {
                                if (item instanceof SongListInfo) {
                                    item.setDescription(((SongListInfo) item).getDescript());
                                    itemList.add(item);
//                                    Log.i("huanxue", TAG + "----SongListInfo:" + item.getName());
                                }
                            }
                        }
                        if (itemList.size() > 5) {
                            KuWoMemoryData.getInstance().setRecommendAlbums(itemList);
                            KuWoMemoryData.getInstance().setPlayingForLauncher();
                            KuWoCallback.getInstance().callBackRecommendList(itemList, dataType);
                        }
                    }
                }
            });
        } else {
            KuWoCallback.getInstance().callBackRecommendList(KuWoMemoryData.getInstance().getAllRecommendAlbums(), dataType);
        }
        /**
         * 第一页新歌歌单
         */
        if (KuWoMemoryData.getInstance().getNewSongAlbums().size() == 0) {
            KwApi.fetchNewSongList(MUSIC_ONLINE_PAGE_FIRST, KuWoConstants.MUSIC_ONLINE_SIZE, new OnFetchListener() {
                @Override
                public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                    if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                        Log.d("huanxue", TAG + "-----onFetched----fetchNewSongList:" + s + "---OnlineRootInfo:" + onlineRootInfo);
                        List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                        List<BaseQukuItem> itemList = new ArrayList<>();
                        for (BaseOnlineSection section : list) {
//                            Log.d("huanxue", TAG + "----NewSong----name:" + section.getName());
                            itemList.addAll(section.getOnlineInfos());
                        }
                        if (itemList.size() > 5) {
                            KuWoMemoryData.getInstance().setNewSongAlbums(itemList);
                            KuWoCallback.getInstance().callBackNewSongList(itemList, dataType);
                        }
                    }
                }
            });
        } else {
            KuWoCallback.getInstance().callBackNewSongList(KuWoMemoryData.getInstance().getNewSongAlbums(), dataType);
        }
        /**
         * 更多排行榜歌单
         */
        if (KuWoMemoryData.getInstance().getBillBroadAlbums().size() == 0) {
            KwApi.fetchBillBroad(new OnFetchListener() {
                @Override
                public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                    if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                        Log.d("huanxue", TAG + "-----onFetched----fetchBillBroad:" + s + "---OnlineRootInfo:" + onlineRootInfo);
                        List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                        List<BaseQukuItem> itemList = new ArrayList<>();
                        for (BaseOnlineSection section : list) {
//                            Log.d("huanxue", TAG + "----BillBroad----name:" + section.getName());
                            itemList.addAll(section.getOnlineInfos());
                        }
                        if (itemList.size() > 5) {
                            KuWoMemoryData.getInstance().setBillBroadAlbums(itemList);
                            KuWoCallback.getInstance().callBackBillBroadList(itemList, dataType);
                        }
                    }
                }
            });
        } else {
            KuWoCallback.getInstance().callBackBillBroadList(KuWoMemoryData.getInstance().getBillBroadAlbums(), dataType);
        }
        /**
         * 第一页歌手
         */
        if (KuWoMemoryData.getInstance().getArtistdAlbums().size() == 0) {
            KwApi.fetchAllArtist(true, MUSIC_ONLINE_PAGE_FIRST, KuWoConstants.MUSIC_ONLINE_SIZE, new OnFetchListener() {
                @Override
                public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                    if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                        Log.d("huanxue", TAG + "-----onFetched----fetchAllArtist:" + s + "---OnlineRootInfo:" + onlineRootInfo);
                        List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                        List<BaseQukuItem> itemList = new ArrayList<>();
                        for (BaseOnlineSection section : list) {
//                            Log.d("huanxue", TAG + "----Artist----name:" + section.getName());
                            itemList.addAll(section.getOnlineInfos());
                        }
                        if (itemList.size() > 5) {
                            KuWoMemoryData.getInstance().setArtistdAlbums(itemList);
                            KuWoCallback.getInstance().callBackArtistList(itemList, dataType);
                        }
                    }
                }
            });
        } else {
            KuWoCallback.getInstance().callBackArtistList(KuWoMemoryData.getInstance().getArtistdAlbums(), dataType);
        }
        /**
         * 每日推荐歌单
         */
        if (KuWoMemoryData.getInstance().getDailyMusics().size() == 0) {
            getDailyRecommendList(false, false);
        }
        /**
         * 默认9个热门分类标签
         */
        if (KuWoMemoryData.getInstance().getHotCategories().size() == 0) {
            KwApi.requestCategoriesHotTag(new OnCategoriesHotTagListener() {
                @Override
                public void onFetch(QukuRequestState state, String message, final List<BaseQukuItem> info) {
                    if (state == QukuRequestState.SUCCESS && info != null) {
                        Log.d("huanxue", TAG + "----getHotCategories--message:" + message + "info:" + info.size());
                        KuWoMemoryData.getInstance().setHotCategories(info);
                    }
                }
            });
        }
        /**
         * 更多分类标签
         */
        if (KuWoMemoryData.getInstance().getMoreCategories().size() == 0) {
            KwApi.fetchMusicCategories(new OnFetchListener() {
                @Override
                public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                    if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                        Log.d("huanxue", TAG + "-----onFetched----getMoreCategories:" + s + "---OnlineRootInfo:" + onlineRootInfo);
                        List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                        List<BaseQukuItem> itemList = new ArrayList<>();
                        List<String> categoryTitle = KuWoMemoryData.getInstance().getCategoryTitle();
                        HashMap<String, List<BaseQukuItem>> categoryWords = KuWoMemoryData.getInstance().getCategoryWords();
                        if (categoryTitle.size() != 0) {
                            categoryTitle.clear();
                        }
                        if (categoryWords.size() != 0) {
                            categoryWords.clear();
                        }
                        for (BaseOnlineSection section : list) {
//                            Log.d("huanxue", TAG+"    onFetched    getMoreCategories    section:"+section.getName());
                            categoryTitle.add(section.getName());
                            List<BaseQukuItem> words = new ArrayList<>(section.getOnlineInfos());
                            categoryWords.put(section.getName(), words);
                            itemList.addAll(section.getOnlineInfos());
//                            for (BaseQukuItem qukuItem : section.getOnlineInfos()) {
//                                Log.d("huanxue", TAG + "    onFetched    getMoreCategories    BaseQukuItem:" + qukuItem.getName());
//                            }
                        }
                        KuWoMemoryData.getInstance().setMoreCategories(itemList);
                        KuWoCallback.getInstance().callBackMoreCategoriesList(itemList, KuWoConstants.DATA_MORE_CATEGORIES);
                    }
                }
            });
        }
    }

    /**
     * 加载更多新歌
     *
     * @param page 页数
     * @param size 专辑数量
     */
    public void LoadMoreNewSongList(int page, int size) {
        Log.i("huanxue", TAG + "---LoadMoreNewSongList----page:" + page + "---size:" + size);
        KwApi.fetchNewSongList(page, size, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                    Log.d("huanxue", TAG + "-----onFetched----fetchNewSongList:" + s + "---OnlineRootInfo:" + onlineRootInfo);
                    List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                    List<BaseQukuItem> itemList = new ArrayList<>();
                    for (BaseOnlineSection section : list) {
                        itemList.addAll(section.getOnlineInfos());
                    }
                    KuWoCallback.getInstance().callBackNewSongList(itemList, KuWoConstants.DATA_FIND_MORE);
                }
            }
        });
    }

    /**
     * 加载更多歌手
     *
     * @param page 页数
     * @param size 专辑数量
     */
    public void LoadMoreArtist(int page, int size) {
        Log.i("huanxue", TAG + "---LoadMoreArtist----page:" + page + "---size:" + size);
        KwApi.fetchAllArtist(true, page, size, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                    Log.d("huanxue", TAG + "-----onFetched----fetchAllArtist:" + s + "---OnlineRootInfo:" + onlineRootInfo);
                    List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                    List<BaseQukuItem> itemList = new ArrayList<>();
                    for (BaseOnlineSection section : list) {
                        itemList.addAll(section.getOnlineInfos());
                    }
                    KuWoCallback.getInstance().callBackArtistList(itemList, KuWoConstants.DATA_FIND_MORE);
                }
            }
        });
    }

    /**
     * 获取更多分类歌单
     */
    public void getMoreCategories() {
        Log.d("huanxue", TAG + "------getMoreCategories :");
        if (KuWoMemoryData.getInstance().getMoreCategories().size() == 0) {
            requestAllFindTabDatas(" ");
        } else {
            KuWoCallback.getInstance()
                    .callBackMoreCategoriesList(KuWoMemoryData.getInstance().getMoreCategories(), KuWoConstants.DATA_MORE_CATEGORIES);
        }
    }

    /**
     * 获取推荐歌单
     */
    public void getRecommendlist(String dataType) {
        if (KuWoMemoryData.getInstance().getAllRecommendAlbums().size() == 0) {
            requestAllFindTabDatas(dataType);
        } else {
            KuWoCallback.getInstance().callBackRecommendList(KuWoMemoryData.getInstance().getAllRecommendAlbums(), dataType);
        }
    }


    /**
     * 获取曲库分类热门标签，默认返回9个，该数据为数据库记忆的用户最近点击过的标签
     */
    public void getHotCategories() {
        Log.d("huanxue", TAG + "------getHotCategories ----:");
        KwApi.requestCategoriesHotTag(new OnCategoriesHotTagListener() {
            @Override
            public void onFetch(QukuRequestState state, String message, final List<BaseQukuItem> info) {
                if (state == QukuRequestState.SUCCESS && info != null) {
                    Log.d("huanxue", TAG + "----getHotCategories--message:" + message + "info:" + info.size());
                    KuWoCallback.getInstance().callBackHotCategoriesList(info);
                    HotCategoryDBManager.getInstance().insertDefaultCategory(info);
                }
            }
        });
    }

    /**
     * 获取排行榜
     */
    public void getBillBroad(String dataType) {
        if (KuWoMemoryData.getInstance().getBillBroadAlbums().size() == 0) {
            requestAllFindTabDatas(dataType);
        } else {
            KuWoCallback.getInstance().callBackBillBroadList(KuWoMemoryData.getInstance().getBillBroadAlbums(), dataType);
        }
    }

    /**
     * 获取歌手
     */
    public void getAllArtist(String dataType) {//获取数量要可扩展
        //获取全部歌手，按热度排序
        if (KuWoMemoryData.getInstance().getArtistdAlbums().size() == 0) {
            requestAllFindTabDatas(dataType);
        } else {
            KuWoCallback.getInstance().callBackArtistList(KuWoMemoryData.getInstance().getArtistdAlbums(), dataType);
        }

    }

    /**
     * 获取最新歌单
     */
    public void getNewSongList(String dataType) {//获取数量要可扩展
        if (KuWoMemoryData.getInstance().getNewSongAlbums().size() == 0) {
            requestAllFindTabDatas(dataType);
        } else {
            KuWoCallback.getInstance().callBackNewSongList(KuWoMemoryData.getInstance().getNewSongAlbums(), dataType);
        }
    }

    /**
     * 搜索功能
     *
     * @param keyWord 搜索字段
     * @param searchType 搜索类型
     */
    public void search(String keyWord, int searchType, int page, int size) {
        Log.d("huanxue", TAG + "-------search -----keyWord:" + keyWord + "-----searchType:" + searchType + "----page:" + page + "---size:" + size);
        switch (searchType) {
            case KuWoConstants.SEARCHTYPE_MUSIC:
                KwApi.search(keyWord, SearchType.MUSIC, page, size, this);
                break;
            case KuWoConstants.SEARCHTYPE_ARTIST:
                KwApi.search(keyWord, SearchType.ARTIST, page, size, this);
                break;
            case KuWoConstants.SEARCHTYPE_ALBUM:
                KwApi.search(keyWord, SearchType.ALBUM, page, size, this);
                break;
            case KuWoConstants.SEARCHTYPE_SONGLIST:
                KwApi.search(keyWord, SearchType.SONGLIST, page, size, this);
                break;
            default:
                Log.d("huanxue", "-------search----searchType  is  unknow");
                return;
        }
        String memoryKeyWord = KuWoMemoryData.getInstance().getKeyWord();
        if (TextUtils.isEmpty(memoryKeyWord) || !keyWord.equals(memoryKeyWord)) {
            KuWoMemoryData.getInstance().setKeyWord(keyWord);
            HistorySearchDBManager.getInstance().insert(keyWord);
        }
    }

    /**
     * 主要用于搜索显示数据
     */
    @Override
    public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
        if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
            Log.d("huanxue", TAG + "-----onFetched----" + s + "---OnlineRootInfo:" + onlineRootInfo);
            List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
            List<BaseQukuItem> itemList = new ArrayList<>();
            for (BaseOnlineSection section : list) {
                itemList.addAll(section.getOnlineInfos());
            }
            Log.d("huanxue", TAG + "-----onFetched----itemList:" + itemList.size());
            KuWoCallback.getInstance().callBackSearchResultList(itemList);
        }
    }

    /**
     * 获取每日推荐歌单
     *
     * @param needPlay 是否需要播放
     * @param needCallback 是否需要回调
     */
    public void getDailyRecommendList(final boolean needPlay, final boolean needCallback) {
        List<Music> musicList = KuWoMemoryData.getInstance().getDailyMusics();
        Log.d("huanxue", TAG + "----getDailyRecommendList---musicList.size:" + musicList.size());
        if (musicList.size() > 0) {
            if (needPlay) {
                KuWoMemoryData.getInstance().setCurrentShowList(musicList);
                KuWoMemoryData.getInstance().setClickAlbumID(KuWoConstants.ID_DAILY_RECOMMEND);
                playMusics(0, KuWoConstants.MAX_PLAYLIST_DEFAULT);
            } else {
                if (needCallback) {
                    KuWoCallback.getInstance().callBackCurrentList(musicList, KuWoConstants.ID_DAILY_RECOMMEND);
                }
            }
        } else {
            KwApi.fetchDailyRecommend(new OnDailyRecommendFetchListener() {
                @Override
                public void onFetch(QukuRequestState state, String message, List<Music> musics) {
                    if (musics != null) {
                        KuWoMemoryData.getInstance().setDailyMusics(musics);
                        Log.d("huanxue", TAG + "----getDailyRecommendList---musics:" + musics.size());
                        if (needPlay) {
                            KuWoMemoryData.getInstance().setCurrentShowList(musics);
                            KuWoMemoryData.getInstance().setClickAlbumID(KuWoConstants.ID_DAILY_RECOMMEND);
                            playMusics(0, KuWoConstants.MAX_PLAYLIST_DEFAULT);
                        } else {
                            if (needCallback) {
                                KuWoCallback.getInstance().callBackCurrentList(musics, KuWoConstants.ID_DAILY_RECOMMEND);
                            }
                        }
                    } else {
                        Log.d("huanxue", TAG + "----getDailyRecommendList---state:" + state + "---message:" + message);
                    }
                }
            });
        }
    }

    /**
     * 获取歌词解析，网络音乐每首歌都需要请求歌词显示,根据当前播放的歌曲去获取歌词显示
     */
    public void getLyric() {

        KwApi.fetchLyric(getNowPlayingMusic(), new KwApi.OnLyricFetchListener() {
            @Override
            public void onFetched(QukuRequestState state, String message, final String lyrics) {
                Log.d("huanxue", TAG + "----fetchLyric----state:" + state + "----message:" + message);
                //说明，返回的歌词会有两中情况一种是标准的lrc格式一种是酷我的歌词格式
                //请用正则表达式将酷我格式中的<,>对去掉，就是标准的lrc格式
                if (state == QukuRequestState.SUCCESS) {
                    MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
                        @Override
                        public void call() {
                            List<LyricLine> lyricLines = ModMgr.getLyricParser().parse(lyrics, true);
                            List<String> lyric = new ArrayList<>();
                            for (LyricLine lyricLine : lyricLines) {
//                                Log.d("huanxue", TAG + "-----:" + lyricLine.getLyric());//使用此类型显示歌词
//                                Log.d("huanxue", TAG + "-----:" + lyricLine.getTime());//使用此类型显示歌词
                                //将每句歌词回传给上层UI显示
                                lyric.add(lyricLine.getLyric());
                            }
                            KuWoCallback.getInstance().callBackLyric(lyricLines);
                        }
                    });
                } else {
                    KuWoCallback.getInstance().callBackLyric(null);
                }
            }
        });
    }

    /**
     * 获取当前正在播放的歌曲列表
     *
     * @return 歌曲集合
     */
    public List<Music> getNowPlayingMusicList() {
        MusicList list = ModMgr.getPlayControl().getNowPlayingList();
        return KuWoTypeUtils.changeMusicList(list);
    }

    /**
     * 获取当前正在播放的音乐
     */
    public Music getNowPlayingMusic() {
        Music currentMusic = ModMgr.getPlayControl().getNowPlayingMusic();
        if (currentMusic == null) {
            Log.d("huanxue", TAG + "----当前没有播放歌曲----");
            return null;
        }
        return currentMusic;
    }

    /**
     * 获取当前正在播放歌曲的图片
     */
    public void getPlayingMusicBitmap() {
        final Music currentMusic = ModMgr.getPlayControl().getNowPlayingMusic();
        if (currentMusic == null) {
            Log.d("huanxue", TAG + "----当前没有播放歌曲----");
            return;
        }
        KwApi.fetchImage(currentMusic, new OnImageFetchListener() {
            @Override
            public void onFetched(QukuRequestState state, String message, final String imageUrl) {
                if (state == QukuRequestState.SUCCESS) {

                    MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
                        @Override
                        public void call() {
                            Log.d("huanxue", TAG + "----fetchImage-----imageUrl:" + imageUrl);
                            getMusicBitmap(imageUrl);
                            KuWoMemoryData.getInstance().setImageUrl(imageUrl);
                            //获取当前音乐专辑图片后返回给外部应用
                            KuWoCallback.getInstance().sendMusicInfo(currentMusic, imageUrl);
                        }
                    });
                }
            }
        }, ImageSize.SIZE_500);

    }

    /**
     * 根据传入url获取到它的图片
     */
    private void getMusicBitmap(final String imgUrl) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL bitmapUrl = new URL(imgUrl);
                    connection = (HttpURLConnection) bitmapUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    //通过返回码判断网络是否请求成功
                    if (connection.getResponseCode() == 200) {
                        InputStream inputStream = connection.getInputStream();
                        Bitmap shareBitmap = BitmapFactory.decodeStream(inputStream);
                        KuWoCallback.getInstance().callBackCurrentMusicBitmap(shareBitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    /**
     * 收藏当前正在播放的歌曲
     */
    public int collCurrentMusic() {
        Log.i("huanxue", TAG + "---collCurrentMusic---");
        Music music = getNowPlayingMusic();
        if (getMusicCollStatus(music) == KuWoConstants.COLL_FALSE) {//-1表示当前列表没有该歌曲
            return ModMgr.getListMgr().insertMusic(ListType.LIST_NAME_MY_FAVORITE, music);
        } else {
            boolean delete = ModMgr.getListMgr().deleteMusic(ListType.LIST_NAME_MY_FAVORITE, music);
            if (delete) {
                //删除成功
                return KuWoConstants.COLL_FALSE;
            } else {
                //删除失败
                return KuWoConstants.COLL_TRUE;
            }
        }
    }

    /**
     * 收藏用户从列表中点击的歌曲
     */
    public void collMusic(Music music) {
        if (getMusicCollStatus(music) == KuWoConstants.COLL_FALSE) {//-1表示当前列表没有该歌曲
            ModMgr.getListMgr().insertMusic(ListType.LIST_NAME_MY_FAVORITE, music);
        } else {
            ModMgr.getListMgr().deleteMusic(ListType.LIST_NAME_MY_FAVORITE, music);
        }
    }

    /**
     * 获取当前歌曲是否在收藏列表中
     *
     * @param music 歌曲信息
     * @return -1表示未找到
     */
    public int getMusicCollStatus(Music music) {
        return ModMgr.getListMgr().indexOf(ListType.LIST_NAME_MY_FAVORITE, music);
    }

    /**
     * 获取当前用户的单曲收藏
     */
    public void getCollMusicList() {
        MusicList musicList = ModMgr.getListMgr().getList(ListType.LIST_NAME_MY_FAVORITE);
    }

    /**
     * 获取收藏歌单
     */
    public void getUserCollSongList() {
        Log.d("huanxue", TAG + "-------UserCollSongList:");
        ModMgr.getUserFavoriteMgr().fetchUserFavorite(FavoriteType.SongList, 0, 30, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
            }
        });
    }

    /**
     * 获取收藏专辑
     */
    public void getUserCollAlbumList() {
        Log.d("huanxue", TAG + "-------getUserCollAlbumList----");
        ModMgr.getUserFavoriteMgr().fetchUserFavorite(FavoriteType.Album, 0, 30, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {

            }
        });
    }

    /**
     * 获取当前歌曲开始播放的时间
     */
    public int getStartTime() {
        return ModMgr.getPlayControl().getStartTime();
    }

    /**
     * 获取当前歌曲停止播放的时间
     */
    public int getStopTime() {
        return ModMgr.getPlayControl().getStopTime();
    }

    /**
     * 获取当前歌曲总时长
     * 需要在真正开始播放后获取,未真正开始播放前获取会存在进度为零的现象
     */
    public int getDuration() {
        return ModMgr.getPlayControl().getDuration();
    }

    /**
     * 获取当前播放进度
     */
    public int getCurrentPos() {
        return ModMgr.getPlayControl().getCurrentPos();
    }

    /**
     * 设置播放进度
     */
    public void seek(int pos) {
        ModMgr.getPlayControl().seek(pos);
        KuWoCallback.getInstance().callBackLoadingStatus(true);
    }


    /**
     * 获取当前用户状态,获取的是用户是否扫描二维码登录
     * true  已登录
     * false 未登录
     */
 /*   public boolean getUserLogon() {
        new Thread() {
            @Override
            public void run() {
                final LoginResult loginResult = BaseScanQrCodeMgr.getInstance().checkResult();
                if (loginResult.getState() == LoginResult.STATE_LOGIN) {
                    Log.d("huanxue", TAG + "----当前帐号 已经登录:");
                    KuWoMemoryData.getInstance().getUserStatus().set(true);
                } else {
                    Log.d("huanxue", TAG + "----当前帐号 未登录，请使用二维码登录:");
                    KuWoMemoryData.getInstance().getUserStatus().set(false);
                }
            }
        }.start();

        return KuWoMemoryData.getInstance().getUserStatus().get();
    }*/

    /**
     * 获取歌曲列表
     *
     * @param list 歌单信息
     */
    public void getMusicList(final BaseQukuItemList list, final int page, int size) {
        Log.d("huanxue", TAG + "----getMusicList----BaseQukuItemList:" + list);
        KwApi.fetch(list, page, size, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                if (page != MUSIC_ONLINE_PAGE_FIRST) {
                    BaseQukuItem mQukuItem = KuWoMemoryData.getInstance().getShowAlbum();
                    if (mQukuItem instanceof BaseQukuItemList) {
                        if (!list.equals(mQukuItem)) {
                            Log.d("huanxue", TAG + "   online data is out-of-date");
                            return;
                        }
                    } else {
                        Log.d("huanxue", TAG + "   online data is out-of-date");
                        return;
                    }
                }

                if (state == QukuRequestState.SUCCESS && info != null) {
                    Log.d("huanxue", TAG + "----getMusicList----info" + info);
                    List<BaseOnlineSection> onlineList = info.getOnlineSections();
                    List<Music> musics = new ArrayList<>();
                    for (BaseOnlineSection section : onlineList) {
                        for (BaseQukuItem item : section.getOnlineInfos()) {
                            if (item instanceof MusicInfo) {
                                Music music = ((MusicInfo) item).getMusic();
                                musics.add(music);
                            }
                        }
                    }
                    KuWoCallback.getInstance().callBackCurrentList(musics, list.getId());
                }
            }
        });
    }

    /**
     * 获取歌手歌单
     *
     * @param artistInfo 歌手信息
     */
    public void getArtistMusicList(final ArtistInfo artistInfo, final int page, int size) {
        Log.d("huanxue", TAG + "----getArtistMusicList----artistInfo:" + artistInfo + "---page:" + page + "---size:" + size);
        KwApi.fetchArtistMusic(artistInfo, page, size, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                if (page != MUSIC_ONLINE_PAGE_FIRST) {
                    BaseQukuItem mQukuItem = KuWoMemoryData.getInstance().getShowAlbum();
                    if (mQukuItem instanceof ArtistInfo) {
                        if (!artistInfo.equals(mQukuItem)) {
                            Log.d("huanxue", TAG + "   online data is out-of-date");
                            return;
                        }
                    } else {
                        Log.d("huanxue", TAG + "   online data is out-of-date");
                        return;
                    }
                }

                if (state == QukuRequestState.SUCCESS && info != null) {
                    Log.d("huanxue", TAG + "----getArtistMusicList----info" + info);
                    List<BaseOnlineSection> onlineList = info.getOnlineSections();
                    List<Music> musics = new ArrayList<>();
                    for (BaseOnlineSection section : onlineList) {
                        for (BaseQukuItem item : section.getOnlineInfos()) {
                            if (item instanceof MusicInfo) {
                                Music music = ((MusicInfo) item).getMusic();
//                                Log.d("huanxue", TAG + "---getMusicList-----Music:" + music.name);
                                musics.add(music);
                            }
                        }
                    }
                    KuWoCallback.getInstance().callBackCurrentList(musics, artistInfo.getId());
                }
            }
        });
    }

    /**
     * 获取更多分类下的推荐歌单
     *
     * @param list CategoryListInfo 分类歌单信息
     */
    public void getCategoriesList(final BaseQukuItemList list, final int page, int size) {
        Log.d("huanxue", TAG + "----getCategoriesMusicList----CategoryListInfo:" + list + "---page:" + page + "---size:" + size);
        KwApi.fetch(list, page, size, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                if (page != MUSIC_ONLINE_PAGE_FIRST) {
                    BaseQukuItemList mQukuItem = KuWoMemoryData.getInstance().getShowCategoryAlbum();
                    if (!list.equals(mQukuItem)) {
                        Log.d("huanxue", TAG + "   online data is out-of-date");
                        return;
                    }
                }

                if (state == QukuRequestState.SUCCESS && info != null) {
                    Log.d("huanxue", TAG + "----getCategoriesMusicList----SUCCESS:");
                    List<BaseOnlineSection> onlineList = info.getOnlineSections();
                    List<BaseQukuItem> itemList = new ArrayList<>();
                    for (BaseOnlineSection section : onlineList) {
                        itemList.addAll(section.getOnlineInfos());
                    }
                    KuWoCallback.getInstance().callBackMoreCategoriesList(itemList, KuWoConstants.DATA_CATEGORIES_SONGLIST);
                } else {
                    Log.d("huanxue", TAG + "----getCategoriesMusicList----state:" + state + "---message:" + message);
                }
            }
        });
    }

    /**
     * 获取热门搜索标签
     */
    public void getSearchHotKeywords() {
        Log.d("huanxue", TAG + "----getSearchHotKeywords----");
        KwApi.fetchSearchHotKeywords(new OnHotKeywordsFetchListener() {
            @Override
            public void onFetch(QukuRequestState state, String message, List<String> hotWords) {
                if (state == QukuRequestState.SUCCESS && hotWords != null) {
//                    for (String string : hotWords) {
//                        Log.i("huanxue", TAG + "----getSearchHotKeywords---SUCCESS" + string);
//                    }
                    KuWoCallback.getInstance().callBackSearchHotKeywords(hotWords);
                }
            }
        });
    }

    /**
     * 获取专辑内的歌曲
     *
     * @param albumInfo 专辑信息
     */
    public void getAlbumMusic(final AlbumInfo albumInfo, final int page, int size) {
        Log.d("huanxue", TAG + "----getAlbumMusic----" + "---page:" + page + "---size:" + size);
        KwApi.fetchAlbumMusic(albumInfo, page, size, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                if (page != MUSIC_ONLINE_PAGE_FIRST) {
                    BaseQukuItem mQukuItem = KuWoMemoryData.getInstance().getShowAlbum();
                    if (mQukuItem instanceof AlbumInfo) {
                        if (!albumInfo.equals(mQukuItem)) {
                            Log.d("huanxue", TAG + "   online data is out-of-date");
                            return;
                        }
                    } else {
                        Log.d("huanxue", TAG + "   online data is out-of-date");
                        return;
                    }
                }

                if (state == QukuRequestState.SUCCESS && info != null) {
                    List<BaseOnlineSection> onlineList = info.getOnlineSections();
                    List<Music> musics = new ArrayList<>();
                    for (BaseOnlineSection section : onlineList) {
                        for (BaseQukuItem item : section.getOnlineInfos()) {
                            if (item instanceof MusicInfo) {
                                Music music = ((MusicInfo) item).getMusic();
                                musics.add(music);
                            }
                        }
                    }
                    KuWoCallback.getInstance().callBackCurrentList(musics, albumInfo.getId());
                }
            }
        });
    }

    /**
     * 设置歌曲音质
     *
     * @param quality 音质类型
     * @return 是否设置成功
     */
    public boolean setPlayQuality(int quality) {
        Log.d("huanxue", TAG + "---setPlayQuality---quality:" + quality);
        boolean success = ModMgr.getSettingMgr().setDownloadWhenPlayQuality(quality);
        if (success) {
//            PointUtils.sendGatherDataFromQuality(PointUtils.BEHAVIOR_ID34, quality);
        }
        return success;
    }

    /**
     * 获取当前歌曲音质
     *
     * @return 音质类型
     */
    public int getPlayQuality() {
        Log.d("huanxue", TAG + "---getPlayQuality---");
        return ModMgr.getSettingMgr().getDownloadWhenPlayQuality();
    }

    /**
     * 酷我无感登录
     */
    public void loginNoSense() {

       /* UserInfo userInfo = UserProfileProxy.getInstance().getCurrentUserInfo();
        if (userInfo != null && "2".equals(userInfo.getUserId())) {
            Log.d("huanxue", TAG + "---loginNoSense---isUserLogon  userInfo is empty or 访客");
            return;
        }*/

//        String token = UserProfileProxy.getInstance().getToken();
//        Log.d("huanxue", TAG + "------loginNoSense:" + token);
       /* if (TextUtils.isEmpty(token)) {//个人中心红旗帐号未登录
//            UserProfileProxy.getInstance().forwardPage(UserProfileConst.LOGIN_PAGE);
            Log.d("huanxue", TAG + "---loginNoSense---isUserLogon---token   is  empty  or  error");
            return;
        }*/

        if (isUserLogon()) {
            KuWoMemoryData.getInstance().setLogon(KuWoConstants.LOGON_TRUE);
            Log.d("huanxue", TAG + "---loginNoSense---isUserLogon---user  is  login");
            return;
        }

        KuWoMemoryData.getInstance().setLogon(KuWoConstants.LOGON_FALSE);
        //请求服务器的地址
        String url = KuWoMemoryData.getInstance().getBaseUrl() + "/oauth/noOauth/randomString";
        Log.d("huanxue", TAG + "---loginNoSense---url  :" + url);
        //创建okhttp端口
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建要发送的请求（这里创建的是没有参数的GET请求）
        Request request = new Request.Builder().url(url)
                .header(KuWoConstants.NET_REQUEST_TITLE_HEADER, KuWoConstants.NET_REQUEST_VALUE_HEADER + "token").build();
        Call call = okHttpClient.newCall(request);
        //CallBack是请求回调
        call.enqueue(new Callback() {
            @Override
            //请求失败执行的方法
            public void onFailure(Call call, IOException e) {
                //这里的失败指的是没有网络请求发送不出去，或者请求地址有误找不到服务器这类情况
                //如果服务器返回的是404错误也说明请求到服务器了，属于请求成功的情况，要在下面的方法中处理
                Log.d("huanxue", TAG + "----onFailure:" + e);
            }

            @Override
            //请求成功执行的方法
            public void onResponse(Call call, Response response) throws IOException {
                //请求成功以后的操作在这个方法里执行，并且这是个子线程，不能做更新界面的操作
                if (response.isSuccessful()) {
                    String jsondata = response.body().string();
                    Log.d("huanxue", TAG + "--loginNoSense--onResponse:" + jsondata);
                    Gson gson = new Gson();
                    LoginInfo info = gson.fromJson(jsondata, LoginInfo.class);
                    if (info.getStatusCode().equals("11005109")) {
//                        UserProfileProxy.getInstance().refreshToken();
                        return;
                    }

                    String randomstrin = info.getData().getRandomstring();
                    String redirect_uri = info.getData().getRedirect_uri();
                    Log.d("huanxue", TAG + "---loginNoSense----LoginInfo:" + randomstrin);
                    Log.d("huanxue", TAG + "---loginNoSense----LoginInfo:" + redirect_uri);
                    if (randomstrin == null || redirect_uri == null) {
                        Log.d("huanxue", TAG + " loginNoSense response data is null");
                        return;
                    }
                    UserLoginUtils.login(randomstrin, KuWoConstants.APP_ID_HQ, redirect_uri, KuWoConstants.APP_ID_KW);
                }

            }
        });

    }

    /**
     * 获取对账服务开通状态
     */
    public void getRecord() {
      /*  final UserInfo userInfo = UserProfileProxy.getInstance().getCurrentUserInfo();
        if (userInfo != null && "2".equals(userInfo.getUserId())) {
            Log.d("huanxue", TAG + "---getRecord---isUserLogon  userInfo is empty or 访客");
            return;
        }*/

        final SharedPreferences sp = KuWoMemoryData.getInstance().getContext().getSharedPreferences("MediaMusic_SP", Context.MODE_PRIVATE);

//        boolean hasOpen = sp.getBoolean(KuWoConstants.SP_RECORD_STATUS + userInfo.getUserId(), false);
//        Log.d("huanxue", TAG + "---getRecord---hasOpen :" + hasOpen);
//        if (!hasOpen) {
            //请求服务器的地址
            String url = KuWoMemoryData.getInstance().getBaseUrl() + "/cp-openingrecord/cpBill/billRecord";
            OkHttpClient okHttpClient = new OkHttpClient();
            final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
            String text = monthFormat.format(new Date());
            Log.d("huanxue", TAG + "  getRecord  text:" + text);
            String jsonStr = "{\n" +
                    "\"oauthProviderId\": \"kuwo\",\n" +
                    "\"statusCode\": \"0\",\n" +
                    "\"statusComment\": \"成功\",\n" +
                    "\"openTime\": \"" + text + "\"\n" +
                    "}";
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonStr, JSON);
            Request request = new Request.Builder().url(url)
                    .header(KuWoConstants.NET_REQUEST_TITLE_HEADER, KuWoConstants.NET_REQUEST_VALUE_HEADER + "token")
                    .post(body).build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsondata = response.body().string();
                        Log.d("huanxue", TAG + "--getRecord--onResponse:" + jsondata);
                        Gson gson = new Gson();
                        OpenRecord record = gson.fromJson(jsondata, OpenRecord.class);
                        if ("0".equals(record.getStatusCode())) {
                            //请求对账服务成功
                            SharedPreferences.Editor editor = sp.edit();
//                            editor.putBoolean(KuWoConstants.SP_RECORD_STATUS + userInfo.getUserId(), true);
                            editor.apply();
                        }

                    }
                }
            });

//        }
    }

    /**
     * 酷我帐号是否登录
     *
     * @return 登录状态
     */
    public boolean isUserLogon() {
        return UserInfoHelper.isUserLogon();
    }

    /**
     * 酷我帐号自动登录
     */
    private void autoLogin() {
        Log.d("huanxue", TAG + "----autoLogin-----");
        UserInfoHelper.autoLogin();
    }

    /**
     * 退出帐号
     */
    public void logout() {
        Log.d("huanxue", TAG + "----logout-----");
        UserInfoHelper.logout();
    }

    /**
     * 清空全部正在播放歌单
     */
    public void deleteAllPlayList() {
        Log.d("huanxue", TAG + "----deleteAllPlayList-----");
        MusicList list = ModMgr.getListMgr().getList(ListType.LIST_NAME_TEMPORARY);
        if (list != null) {
            ModMgr.getListMgr().deleteMusic(list.getName());
        }
    }

    /**
     * 根据角标删除正在播放歌单中的音乐
     */
    public void deletePlayList(List<Integer> posList) {
        Log.d("huanxue", TAG + "----deletePlayList-----" + posList);
        if (posList != null) {
            for (int pos : posList) {
                deletePlayListByPos(pos);
            }
        }
    }

    /**
     * 根据角标删除正在播放歌单中的音乐
     */
    public void deletePlayListByPos(int pos) {
        MusicList list = ModMgr.getListMgr().getList(ListType.LIST_NAME_TEMPORARY);
        if (list != null) {
            Music music = list.get(pos);
//            PointUtils.sendGatherDataFromDelete(PointUtils.BEHAVIOR_ID30, music);
            ModMgr.getListMgr().deleteMusic(list.getName(), pos);
        }
    }

    /**
     * 供Launcher调用的播放推荐歌单，随便推送某一推荐歌单集合中的专辑（包括每日推荐），每次上电周期内保持专辑封面一致
     */
    public void getAllRecommendListForLauncher() {
        KuWoMemoryData.getInstance().setPlayingForLauncher();
    }


    /**
     * 查询当前播放歌曲的权限信息,默认试听当前播放音质
     *
     * @param musics 需要查询权限信息的歌单
     * @param albumID 歌单ID   -1代表搜索单曲
     */
    public void chargeMusicList(List<Music> musics, final long albumID) {
        Log.d("huanxue", TAG + "   chargeMusicList    albumID:" + albumID);
        if (musics != null && musics.size() > 0) {
            KwApi.chargeMusics(CHARGE_ACTIONTYPE, CHARGE_QUALITY, musics, new OnMusicsChargeListener() {
                @Override
                public void onChargeSuccess(List<Music> chargeMusics, List<MusicChargeType> chargeResults) {
                    if (chargeResults != null && chargeResults.size() > 0) {
                      /*  for (MusicChargeType type : chargeResults) {
                            Log.d("huanxue", TAG + "   chargeMusicList   onChargeSuccess   MusicChargeType:" + type.getName());
                        }*/
                        KuWoCallback.getInstance().callBackChargeMusic(chargeMusics, chargeResults, albumID);
                    }
                }

                @Override
                public void onChargeFaild(String msg) {
                    KuWoToastUtils.makeText(R.string.toast_charge_faild, Toast.LENGTH_SHORT);
                }
            });
        }
    }

    /**
     * 用户点击专辑歌单封面，后台请求专辑歌单并播放
     */
    public void requestClickItemMusicList(final BaseQukuItem qukuItem) {
        if (qukuItem == null) {
            return;
        }
        //根据当前点击的专辑不同，请求不同的歌单进行播放（参考在线电台，将歌曲请求下来后直接做播放动作）
        Log.d("huanxue", TAG + "   requestClickItemMusicList   " + qukuItem.getId());
        KuWoMemoryData.getInstance().setClickAlbumID(qukuItem.getId());
        if (qukuItem instanceof AlbumInfo) {
            KwApi.fetchAlbumMusic((AlbumInfo) qukuItem, 0, 30, new OnFetchListener() {
                @Override
                public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                    Log.d("huanxue", TAG + "   fetchAlbumMusic   message:" + message);
                    if (state == QukuRequestState.SUCCESS && info != null) {
                        List<BaseOnlineSection> onlineList = info.getOnlineSections();
                        if (onlineList.size() == 0) {
                            Log.i("huanxue", TAG + "   fetchAlbumMusic   result is null");
//                            TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);

                            return;
                        }
                        List<Music> albumMusics = new ArrayList<>();
                        for (BaseOnlineSection section : onlineList) {
                            for (BaseQukuItem item : section.getOnlineInfos()) {
                                if (item instanceof MusicInfo) {
                                    Music music = ((MusicInfo) item).getMusic();
                                    albumMusics.add(music);
                                }
                            }
                        }
                        KuWoMemoryData.getInstance().setCurrentShowList(albumMusics);
                        playMusics(0, KuWoConstants.MAX_PLAYLIST_DEFAULT);
                    }
                }
            });
        } else if (qukuItem instanceof BaseQukuItemList) {
            KwApi.fetch((BaseQukuItemList) qukuItem, 0, 30, new OnFetchListener() {
                @Override
                public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                    Log.d("huanxue", TAG + "   fetchBaseQukuItemList   message:" + message);
                    if (state == QukuRequestState.SUCCESS && info != null) {
                        List<BaseOnlineSection> onlineList = info.getOnlineSections();
                        if (onlineList.size() == 0) {
                            Log.i("huanxue", TAG + "   fetchBaseQukuItemList   result is null");
//                            TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                            KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                            return;
                        }
                        List<Music> baseQukuMusics = new ArrayList<>();
                        for (BaseOnlineSection section : onlineList) {
                            for (BaseQukuItem item : section.getOnlineInfos()) {
                                if (item instanceof MusicInfo) {
                                    Music music = ((MusicInfo) item).getMusic();
                                    baseQukuMusics.add(music);
                                }
                            }
                        }
                        KuWoMemoryData.getInstance().setCurrentShowList(baseQukuMusics);
                        playMusics(0, KuWoConstants.MAX_PLAYLIST_DEFAULT);
                    }
                }
            });
        } else if (qukuItem instanceof ArtistInfo) {
            KwApi.fetchArtistMusic((ArtistInfo) qukuItem, 0, 30, new OnFetchListener() {
                @Override
                public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                    Log.d("huanxue", TAG + "   fetchArtistMusic   message:" + message);
                    if (state == QukuRequestState.SUCCESS && info != null) {
                        List<BaseOnlineSection> onlineList = info.getOnlineSections();
                        if (onlineList.size() == 0) {
                            Log.i("huanxue", TAG + "   fetchArtistMusic   result is null");
//                            TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                            KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                            return;
                        }
                        List<Music> artistMusics = new ArrayList<>();
                        for (BaseOnlineSection section : onlineList) {
                            for (BaseQukuItem item : section.getOnlineInfos()) {
                                if (item instanceof MusicInfo) {
                                    Music music = ((MusicInfo) item).getMusic();
                                    artistMusics.add(music);
                                }
                            }
                        }
                        KuWoMemoryData.getInstance().setCurrentShowList(artistMusics);
                        playMusics(0, KuWoConstants.MAX_PLAYLIST_DEFAULT);
                    }
                }
            });
        } else {
            Log.i("huanxue", TAG + "   requestClickItemMusicList   未找到匹配歌曲");
        }
    }

    /**
     * 获取相似歌曲推荐
     */
    public void playSimilarSongForVR() {
        Music music = getNowPlayingMusic();//请从其他接口获取music的实例
        KwApi.fetchSimilarSong(music, VR_MUSICLIST_SIZE, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                if (state == QukuRequestState.SUCCESS && info != null) {
                    List<BaseOnlineSection> onlineList = info.getOnlineSections();
                    if (onlineList.size() == 0) {
                        Log.i("huanxue", TAG + "   playSimilarSongForVR   result is null");
//                        TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SIMILAR);
                        return;
                    }
                    List<Music> similarMusics = new ArrayList<>();
                    for (BaseOnlineSection section : onlineList) {
                        for (BaseQukuItem item : section.getOnlineInfos()) {
                            if (item instanceof MusicInfo) {
                                Music music = ((MusicInfo) item).getMusic();
                                similarMusics.add(music);
                            }
                        }
                    }
                    KuWoMemoryData.getInstance().setNeedTTS(true);
                    playMusics(new Random().nextInt(similarMusics.size()), KuWoConstants.MAX_PLAYLIST_DEFAULT);
                }
            }
        });
    }

    /**
     * VR最终播放推荐歌曲
     */
    public void playFinalRecommendForVR() {
        Log.i("huanxue", TAG + "   playFinalRecommendForVR  ");
        KuWoMusicAudioFocus.self().getIsPlaying().set(false);
        List<Music> musicList = KuWoMemoryData.getInstance().getDailyMusics();
        int size = musicList.size();
        if (size == 0) {
            //每日推荐音乐找不到，播放本地音乐
            KuWoCallback.getInstance().callBackKuWoResult(null);
        } else {
            KuWoMemoryData.getInstance().setCurrentShowList(musicList);
            int pos = new Random().nextInt(size);
            KuWoMemoryData.getInstance().setNeedTTS(true);
            playMusics(pos, KuWoConstants.MAX_PLAYLIST_DEFAULT);
        }
    }

    /**
     * VR搜索音乐功能
     *
     * @param musicName 歌曲名称
     * @param singerList 歌手集合
     * @param albumName 专辑名称
     */
    public void searchForVR(final String musicName, final ArrayList<String> singerList, final String albumName) {
        Log.d("huanxue", TAG + "-------searchForVR -----musicName:" + musicName + "-----singerList:" + singerList + "----albumName:" + albumName);
        int searchType = 0;
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(musicName)) {
            searchType = KuWoConstants.SEARCHTYPE_MUSIC;
            builder.append(musicName);
        }
        if (!TextUtils.isEmpty(albumName)) {
            searchType = KuWoConstants.SEARCHTYPE_ALBUM;
            builder.append(albumName);
        }
        if (singerList != null && singerList.size() > 0) {
            for (String string : singerList) {
                builder.append(string);
            }
            if (searchType != KuWoConstants.SEARCHTYPE_MUSIC && searchType != KuWoConstants.SEARCHTYPE_ALBUM) {
                searchType = KuWoConstants.SEARCHTYPE_ARTIST;
            }
        }
        switch (searchType) {
            case KuWoConstants.SEARCHTYPE_MUSIC:
                searchMusicNameForVR(builder, musicName);
                break;
            case KuWoConstants.SEARCHTYPE_ARTIST:
                searchSingerForVR(builder);
                break;
            case KuWoConstants.SEARCHTYPE_ALBUM:
                searchAlbumForVR(builder);
                break;
        }
    }

    /**
     * VR搜索合唱歌曲,单曲插入
     *
     * @param string 合唱歌手搜索词条
     */
    public void searchChorusMusicForVR(String string) {
        Log.i("huanxue", TAG + "   searchChorusMusicForVR   string:" + string);
        KwApi.search(string, SearchType.MUSIC, MUSIC_ONLINE_PAGE_FIRST, VR_SEARCH_MUSIC_SIZE, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                Log.i("huanxue", TAG + "   searchSingerForVR  onFetched:    qukuRequestState:" + qukuRequestState + "    msg:" + s);
                if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                    //取3首歌曲中随机一首插入当前播放列表，
                    List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                    if (list.size() == 0) {
                        Log.i("huanxue", TAG + "   searchMusicNameForVR   result is null");
                        //TTS播放未找到
//                        TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                        KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                        return;
                    }
                    List<Music> musicList = new ArrayList<>();
                    for (BaseOnlineSection section : list) {
                        for (BaseQukuItem qukuItem : section.getOnlineInfos()) {
                            if (qukuItem instanceof MusicInfo) {
                                MusicInfo musicInfo = (MusicInfo) qukuItem;
                                Music music = musicInfo.getMusic();
                                musicList.add(music);
                            }
                        }
                    }
                    //未匹配到,3首歌曲中随机播放一首
                    int pos = new Random().nextInt(3);
                    KuWoMemoryData.getInstance().setNeedTTS(true);
                    playSingleMusic(musicList.get(pos), false);
                }
            }
        });
    }


    /**
     * VR搜索歌曲,单曲插入
     *
     * @param builder 搜索词条
     * @param musicName 歌曲名称
     */
    private void searchMusicNameForVR(StringBuilder builder, final String musicName) {
        Log.i("huanxue", TAG + "   searchMusicNameForVR   string:" + builder.toString() + "   musicName:" + musicName);
        KwApi.search(builder.toString(), SearchType.MUSIC, MUSIC_ONLINE_PAGE_FIRST, VR_SEARCH_MUSIC_SIZE, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                    //取3首歌曲中随机一首插入当前播放列表，
                    List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                    if (list.size() == 0) {
                        Log.i("huanxue", TAG + "   searchMusicNameForVR   result is null");
                        //TTS播放未找到
//                        TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                        KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                        return;
                    }
                    List<Music> musicList = new ArrayList<>();
                    for (BaseOnlineSection section : list) {
                        for (BaseQukuItem qukuItem : section.getOnlineInfos()) {
                            if (qukuItem instanceof MusicInfo) {
                                MusicInfo musicInfo = (MusicInfo) qukuItem;
                                Music music = musicInfo.getMusic();
                                if (musicName != null && musicName.equals(music.name)) {
                                    KuWoMemoryData.getInstance().setNeedTTS(true);
                                    playSingleMusic(music, false);
                                    return;
                                }
                                musicList.add(music);
                            }
                        }
                    }
                    //未匹配到,3首歌曲中随机播放一首
                    int pos = new Random().nextInt(musicList.size());
                    KuWoMemoryData.getInstance().setNeedTTS(true);
                    playSingleMusic(musicList.get(pos), false);
                }
            }
        });
    }

    /**
     * VR搜索歌手，歌单替换&过滤vip&随机顺序
     *
     * @param builder 搜索词条
     */
    private void searchSingerForVR(StringBuilder builder) {
        Log.i("huanxue", TAG + "   searchSingerForVR  builder:" + builder.toString());
        KwApi.search(builder.toString(), SearchType.ARTIST, MUSIC_ONLINE_PAGE_FIRST, VR_SEARCH_ARTIST_SIZE, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                Log.i("huanxue", TAG + "   searchSingerForVR  onFetched:    qukuRequestState:" + qukuRequestState + "    msg:" + s);
                if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                    List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                    if (list.size() == 0) {
                        Log.i("huanxue", TAG + "   searchSingerForVR   result is null");
                        //TTS播放未找到
//                        TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                        KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                        return;
                    }
                    ArtistInfo artistInfo = (ArtistInfo) onlineRootInfo.getOnlineSections().get(0).getOnlineInfos().get(0);
                    KwApi.fetchArtistMusic(artistInfo, MUSIC_ONLINE_PAGE_FIRST, VR_MUSICLIST_SIZE, new OnFetchListener() {
                        @Override
                        public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                            if (state == QukuRequestState.SUCCESS && info != null) {
                                List<BaseOnlineSection> onlineList = info.getOnlineSections();
                                if (onlineList.size() == 0) {
                                    Log.i("huanxue", TAG + "   searchSingerForVR  ArtistInfo  result is null");
                                    //TTS播放未找到
//                                    TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                                    KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                                    return;
                                }
                                List<Music> musics = new ArrayList<>();
                                for (BaseOnlineSection section : onlineList) {
                                    for (BaseQukuItem item : section.getOnlineInfos()) {
                                        if (item instanceof MusicInfo) {
                                            Music music = ((MusicInfo) item).getMusic();
                                            musics.add(music);
                                        }
                                    }
                                }
                                //查询歌曲权限，过滤歌曲
                                KuWoMemoryData.getInstance().setCurrentShowList(musics);
                                int pos = new Random().nextInt(musics.size());
                                KuWoMemoryData.getInstance().setNeedTTS(true);
                                playMusics(pos, KuWoConstants.MAX_PLAYLIST);
                            }

                        }
                    });
                }

            }
        });
    }

    /**
     * VR搜索专辑，歌单替换&过滤vip
     *
     * @param builder 搜索词条
     */
    private void searchAlbumForVR(StringBuilder builder) {
        Log.i("huanxue", TAG + "   searchSingerForVR  builder:" + builder.toString());
        KwApi.search(builder.toString(), SearchType.ALBUM, MUSIC_ONLINE_PAGE_FIRST, VR_SEARCH_ARTIST_SIZE, new OnFetchListener() {
            @Override
            public void onFetched(QukuRequestState qukuRequestState, String s, OnlineRootInfo onlineRootInfo) {
                Log.i("huanxue", TAG + "   searchSingerForVR  onFetched:    qukuRequestState:" + qukuRequestState + "    msg:" + s);
                if (qukuRequestState == QukuRequestState.SUCCESS && onlineRootInfo != null) {
                    List<BaseOnlineSection> list = onlineRootInfo.getOnlineSections();
                    if (list.size() == 0) {
                        Log.i("huanxue", TAG + "   searchAlbumForVR   result is null");
                        //TTS播放未找到
//                        TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                        KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                        return;
                    }
                    AlbumInfo albumInfo = (AlbumInfo) onlineRootInfo.getOnlineSections().get(0).getOnlineInfos().get(0);
                    KwApi.fetchAlbumMusic(albumInfo, MUSIC_ONLINE_PAGE_FIRST, VR_MUSICLIST_SIZE, new OnFetchListener() {
                        @Override
                        public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                            if (state == QukuRequestState.SUCCESS && info != null) {
                                List<BaseOnlineSection> onlineList = info.getOnlineSections();
                                if (onlineList.size() == 0) {
                                    Log.i("huanxue", TAG + "   searchAlbumForVR  AlbumInfo  result is null");
                                    //TTS播放未找到
//                                    TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                                    KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                                    return;
                                }
                                List<Music> musics = new ArrayList<>();
                                for (BaseOnlineSection section : onlineList) {
                                    for (BaseQukuItem item : section.getOnlineInfos()) {
                                        if (item instanceof MusicInfo) {
                                            Music music = ((MusicInfo) item).getMusic();
                                            musics.add(music);
                                        }
                                    }
                                }
                                //查询歌曲权限，过滤歌曲
                                KuWoMemoryData.getInstance().setCurrentShowList(musics);
                                int pos = new Random().nextInt(musics.size());
                                KuWoMemoryData.getInstance().setNeedTTS(true);
                                playMusics(pos, KuWoConstants.MAX_PLAYLIST);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 根据专辑名称匹配对应的歌单，在分类总集合中查找
     *
     * @param albumName 专辑名称
     */
    public void matchCategoryAlbumForVR(String albumName) {
        List<BaseQukuItem> allCategories = KuWoMemoryData.getInstance().getAllCategories();
        Log.i("huanxue", TAG + "   matchCategoryAlbumForVR   allCategories.size:" + allCategories.size() + "    albumName:" + albumName);
        if (allCategories.size() > 0) {
            for (BaseQukuItem qukuItem : allCategories) {
                if (qukuItem.getName().equals(albumName)) {
                    KuWoMemoryData.getInstance().setNeedTTS(true);
                    //需要先请求分类下的专辑，然后再使用requestClickItemMusicList进行播放
                    KwApi.fetch((BaseQukuItemList) qukuItem, MUSIC_ONLINE_PAGE_FIRST, VR_SEARCH_MUSIC_SIZE, new OnFetchListener() {
                        @Override
                        public void onFetched(QukuRequestState state, String message, OnlineRootInfo info) {
                            Log.i("huanxue", TAG + "   matchCategoryAlbumForVR  onFetched:    qukuRequestState:" + state + "    msg:" + message);
                            if (state == QukuRequestState.SUCCESS && info != null) {
                                List<BaseOnlineSection> onlineList = info.getOnlineSections();
                                if (onlineList.size() == 0) {
                                    Log.i("huanxue", TAG + "   matchCategoryAlbumForVR  AlbumInfo  result is null");
                                    //TTS播放未找到
//                                    TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
                                    KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
                                    return;
                                }
                                //成功请求到匹配的数据歌单
                                List<BaseQukuItem> qukuItemLists = onlineList.get(new Random().nextInt(onlineList.size())).getOnlineInfos();
                                BaseQukuItem item = qukuItemLists.get(new Random().nextInt(qukuItemLists.size()));
                                requestClickItemMusicList(item);
                            }
                        }
                    });
                    return;
                }
            }
            //播放推荐歌单
//            TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
            KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
        } else {
            //播放推荐歌单
//            TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
            KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
        }
    }

    /**
     * 根据专辑名称匹配对应的歌单，在排行榜总集合中查找
     *
     * @param billBroadName 排行榜名称
     */
    public void matchBillBroadForVR(String billBroadName) {
        List<BaseQukuItem> allBillBroads = KuWoMemoryData.getInstance().getBillBroadAlbums();
        Log.i("huanxue", TAG + "   matchBillBroadForVR   allBillBroads.size:" + allBillBroads.size() + "    billBroadName:" + billBroadName);
        if (allBillBroads.size() > 0) {
            for (BaseQukuItem qukuItem : allBillBroads) {
                if (qukuItem.getName().equals(billBroadName)) {
                    KuWoMemoryData.getInstance().setNeedTTS(true);
                    requestClickItemMusicList(qukuItem);
                    return;
                }
            }
            //播放推荐歌单
//            TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
            KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
        } else {
            //播放推荐歌单
//            TTSManager.sendMsgToTTS(KuWoConstants.TTS_NO_SEARCH);
            KuWoMusicAudioFocus.self().sendPlayRecommendMsg();
        }
    }

  /*  private class TokenListener extends UserProfileListener {

        @Override
        public void tokenRefresh(String token, boolean result) {
            Log.d("huanxue", TAG + "----tokenRefresh----");
            loginNoSense();
        }

        @Override
        public void onCurrentUserInfoRefresh(final UserInfo info) {
            Log.d("huanxue", TAG + "----onCurrentUserInfoRefresh----");
            //获取当前用户ID，判断是否为同一账号，不同账户进行不同的酷我账户无感登录
            Handler handler = KuWoMemoryData.getInstance().getHandler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (info != null) {
                        Log.d("huanxue", TAG + "----onCurrentUserInfoRefresh----UserInfo!=null:" + info.getUserId());
                        if (info.getUserId().equals("2")) {
                            //访客登录
                            return;
                        }
                        if (!KuWoMemoryData.getInstance().getUserId().equals(info.getUserId())) {
                            //切换账号,需要清除上一用户的全部相关数据
                            if (isUserLogon()) {
                                logout();
                            }
                            loginNoSense();
                            KuWoMemoryData.getInstance().setUserId(info.getUserId());
                            if (!TextUtils.equals(KuWoMemoryData.getInstance().getUserIdByPre(), info.getUserId())) {
                                //登录其他账户，记忆当前用户的sp信息，发送local广播给service
                                Context context = KuWoMemoryData.getInstance().getContext();
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_CHANGE_USERINFO));
                            }

                        } else {
                            //防止帐号登录过期
                            if (!isUserLogon()) {
                                autoLogin();
                            }
                        }
                    }
                }
            });

        }
    }*/
}
