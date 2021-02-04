package com.hsae.kuwo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.VipUserInfo;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.BaseQukuItemList;
import cn.kuwo.mod.lyric.LyricLine;
import cn.kuwo.open.base.MusicChargeType;


/**
 * 与酷我音乐相关的应用内数据暂存,仅对外提供set、get方法
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.8.1.
 */
public class KuWoMemoryData {

    private static final KuWoMemoryData sInstances = new KuWoMemoryData();
    //    private static boolean sUserStatus = false;
    private final String TAG = "KuWoMemoryData";
    private Handler mHandler = null;
    /**
     * 当前用户登录状态(已弃用)
     */
    private AtomicBoolean mUserStatus = new AtomicBoolean(false);
    /**
     * 当前展示列表，所有只显示一个列表的地方均使用该列表
     * 具体如：播放列表、搜索结果、每日推荐歌单、更多数据列表、历史记录、收藏等
     */
    private List<Music> mCurrentShowList;
    /**
     * 当前展示列表对应的歌曲权限信息集合
     */
    private List<MusicChargeType> mChargeList;
    /**
     * 正在播放专辑的歌曲列表集合
     */
    private List<Music> mPlayingList;
    /**
     * 正在播放专辑的权限信息集合
     */
    private List<MusicChargeType> mChargePlayingList;
    /**
     * 当前播放歌曲
     */
    private Music mCurrentPlayMusic;
    /**
     * 当前播放歌曲的图片
     */
    private Bitmap mCurrentMusicBitmap;
    /**
     * 当前播放歌曲图片地址
     */
    private String mImageUrl;
    /**
     * 当前播放歌曲的歌词
     */
    private List<LyricLine> mCurrentPlayLyric;
    /**
     * 播放状态
     */
    private boolean mPlayStatus;
    /**
     * 播放推荐列表状态
     */
    private boolean mPlayRecommendStatus;
    /**
     * 区分发现页当前被点击的是哪个title
     */
    private String mClickTitle = " ";
    /**
     * 缓存当前点击的是哪个搜索词条
     */
    private String mClickSearchItem = " ";
    /**
     * 点击分类标签后的分类歌单，
     */
    private List<BaseQukuItem> mCategoriesList;
    /**
     * 当前播放的音质
     */
    private int mPlayQuality = 0;
    /**
     * 是否正在播放混合列表
     */
    private boolean isPlayMixList = false;
    /**
     * 当前无感登录是否成功
     */
    private boolean isLogon = false;
    /**
     * 获取当前VIP标识
     */
    private int mVipLevel = 0;
    /**
     * 标记全屏歌曲列表的title显示
     */
    private String mPlayListTitle = " ";
    /**
     * 全局进程Context
     */
    private Context mContext;
    /**
     * 发送给外部应用的，ID3信息
     */
//    private MediaInfo mMediaInfo;
    /**
     * 发送给外部应用的，是否正在播放推荐列表标识
     */
    private boolean isRecommend = false;
    /**
     * 发送给外部应用的，推荐列表专辑图片
     */
    private String mCoverUrl = " ";
    /**
     * 每日推荐列表
     */
    private List<Music> mDailyMusics = new ArrayList<Music>();
    /**
     * 用户点击的专辑ID
     */
    private long mClickAlbumID = -1;
    /**
     * 正在播放的专辑ID
     */
    private long mPLayingAlbumID = -2;
    /**
     * 当前展示的歌单专辑（用于显示歌曲列表）
     */
    private BaseQukuItem mShowAlbum = null;
    /**
     * 当前展示的分类歌单专辑（用于显示分类歌单）
     */
    private BaseQukuItemList mShowCategoryAlbum = null;
    /**
     * 更多推荐封面集合
     */
    private List<BaseQukuItem> mRecommendAlbums = new ArrayList<BaseQukuItem>();
    /**
     * 更多新歌封面集合
     */
    private List<BaseQukuItem> mNewSongAlbums = new ArrayList<BaseQukuItem>();
    /**
     * 更多排行榜封面集合
     */
    private List<BaseQukuItem> mBillBroadAlbums = new ArrayList<BaseQukuItem>();
    /**
     * 更多歌手封面集合
     */
    private List<BaseQukuItem> mArtistdAlbums = new ArrayList<BaseQukuItem>();
    /**
     * 主页推荐的专辑
     */
    private BaseQukuItem mPlayingForLauncher = null;
    /**
     * 热门分类标签集合
     */
    private List<BaseQukuItem> mHotCategories = new ArrayList<BaseQukuItem>();
    /**
     * 更多分类标签集合
     */
    private List<BaseQukuItem> mMoreCategories = new ArrayList<BaseQukuItem>();
    /**
     * 全部分类标签集合(热门+更多)
     */
    private List<BaseQukuItem> mAllCategories = new ArrayList<BaseQukuItem>();
    /**
     * 我的全部收藏专辑集合
     */
    private List<BaseQukuItem> mAllCollAlbums = new ArrayList<BaseQukuItem>();
    /**
     * 当前用户VIP信息
     */
    private VipUserInfo mVipUserInfo = new VipUserInfo();
    /**
     * 当前用户搜索的词条
     */
    private String mKeyWord = " ";
    /**
     * 正在请求更多数据的对象
     */
    private Object mLoadObject = new Object();
    /**
     * 是否需要播报TTS
     */
    private boolean isNeedTTS = false;
    /**
     * 当前用户ID
     */
    private String mUserId = " ";
    /**
     * 退出之前的用户ID
     */
    private String mUserIdByPre = " ";
    /**
     * 当前在线音乐是否在前台（使用某几个在线音乐的activity状态判断）
     */
    private boolean isVisibility = false;
    /**
     * 红旗后台地址
     */
    private String mBaseUrl = " ";
    /**
     * 更多分类中的title
     */
    private List<String> mCategoryTitle = new ArrayList<>();
    /**
     * 更多分类下词条集合
     */
    private HashMap<String, List<BaseQukuItem>> mCategoryWords = new HashMap<>();

    public KuWoMemoryData() {
        mCurrentShowList = new ArrayList<>();
        mChargeList = new ArrayList<>();
    }

    public static KuWoMemoryData getInstance() {
        return sInstances;
    }

    public List<Music> getPlayingList() {
        return mPlayingList;
    }

    public void setPlayingList(List<Music> playingList) {
        mPlayingList = playingList;
    }

    public List<MusicChargeType> getChargePlayingList() {
        return mChargePlayingList;
    }

    public void setChargePlayingList(List<MusicChargeType> chargePlayingList) {
        mChargePlayingList = chargePlayingList;
    }

    public String getUserIdByPre() {
        return mUserIdByPre;
    }

    public void setUserIdByPre(String userIdByPre) {
        mUserIdByPre = userIdByPre;
        Log.d("huanxue", TAG + "  setUserIdByPre   userIdByPre:" + userIdByPre);
    }

    public HashMap<String, List<BaseQukuItem>> getCategoryWords() {
        return mCategoryWords;
    }

    public void setCategoryWords(HashMap<String, List<BaseQukuItem>> categoryWords) {
        mCategoryWords = categoryWords;
    }

    public List<String> getCategoryTitle() {
        return mCategoryTitle;
    }

    public void setCategoryTitle(List<String> categoryTitle) {
        mCategoryTitle = categoryTitle;
    }


    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public boolean getVisibility() {
        return isVisibility;
    }

    public void setVisibility(boolean visibility) {
        if (isVisibility != visibility) {
            isVisibility = visibility;
            KuWoCallback.getInstance().sendInfoToVR();
        }
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
        Log.i("huanxue", TAG + "  setUserId   mUserId:" + mUserId);
    }

    public boolean getNeedTTS() {
        return isNeedTTS;
    }

    public void setNeedTTS(boolean needTTS) {
        isNeedTTS = needTTS;
    }

    public Object getLoadObject() {
        return mLoadObject;
    }

    public void setLoadObject(Object loadObject) {
        mLoadObject = loadObject;
    }

    public String getKeyWord() {
        return mKeyWord;
    }

    public void setKeyWord(String keyWord) {
        mKeyWord = keyWord;
    }

    public BaseQukuItemList getShowCategoryAlbum() {
        return mShowCategoryAlbum;
    }

    public void setShowCategoryAlbum(BaseQukuItemList showCategoryAlbum) {
        mShowCategoryAlbum = showCategoryAlbum;
    }

    public BaseQukuItem getShowAlbum() {
        return mShowAlbum;
    }

    public void setShowAlbum(BaseQukuItem showAlbum) {
        mShowAlbum = showAlbum;
        Log.i("huanxue", TAG + "  setShowAlbum   showAlbum.getName:" + showAlbum.getName() + "    showAlbum.getID:" + showAlbum.getId());
    }

    public List<MusicChargeType> getChargeList() {
        return mChargeList;
    }

    public void setChargeList(List<MusicChargeType> chargeList) {
        mChargeList = chargeList;
    }

    public VipUserInfo getVipUserInfo() {
        return mVipUserInfo;
    }

    public void setVipUserInfo(VipUserInfo vipUserInfo) {
        mVipUserInfo = vipUserInfo;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public List<BaseQukuItem> getAllCollAlbums() {
        return mAllCollAlbums;
    }

    public void setAllCollAlbums(List<BaseQukuItem> allCollAlbums) {
        mAllCollAlbums = allCollAlbums;
    }

    public List<BaseQukuItem> getMoreCategories() {
        return mMoreCategories;
    }

    public void setMoreCategories(List<BaseQukuItem> moreCategories) {
        mMoreCategories = moreCategories;
        if (getHotCategories().size() != 0) {
            setAllCategories(getHotCategories(), moreCategories);
        }
        Log.i("huanxue", TAG + "---setMoreCategories---" + mMoreCategories.size());
    }

    public List<BaseQukuItem> getAllCategories() {
        if (mAllCategories.size() == 0) {
            setAllCategories(getHotCategories(), getMoreCategories());
        }
        return mAllCategories;
    }

    /**
     * 合并热门分类标签集合+更多分类标签集合
     *
     * @param hotCategories 热门分类标签
     * @param moreCategories 更多分类标签
     */
    public void setAllCategories(List<BaseQukuItem> hotCategories, List<BaseQukuItem> moreCategories) {
        mAllCategories.clear();
        mAllCategories.addAll(hotCategories);
        mAllCategories.addAll(moreCategories);
        Log.i("huanxue", TAG + "---setAllCategories----" + mAllCategories.size());
    }

    public List<BaseQukuItem> getHotCategories() {
        return mHotCategories;
    }

    public void setHotCategories(List<BaseQukuItem> hotCategories) {
        mHotCategories = hotCategories;
        if (getAllCategories().size() != 0) {
            setAllCategories(hotCategories, getAllCategories());
        }
        Log.i("huanxue", TAG + "---setHotCategories---" + mHotCategories.size());
    }

    public List<BaseQukuItem> getNewSongAlbums() {
        return mNewSongAlbums;
    }

    public void setNewSongAlbums(List<BaseQukuItem> newSongAlbums) {
        mNewSongAlbums = newSongAlbums;
    }

    public List<BaseQukuItem> getBillBroadAlbums() {
        return mBillBroadAlbums;
    }

    public void setBillBroadAlbums(List<BaseQukuItem> billBroadAlbums) {
        mBillBroadAlbums = billBroadAlbums;
    }

    public List<BaseQukuItem> getArtistdAlbums() {
        return mArtistdAlbums;
    }

    public void setArtistdAlbums(List<BaseQukuItem> artistdAlbums) {
        mArtistdAlbums = artistdAlbums;
    }

    public BaseQukuItem getPlayingForLauncher() {
        return mPlayingForLauncher;
    }

    public AtomicBoolean getUserStatus() {
        return mUserStatus;
    }

    /**
     * 随机获取本次要推荐给主页的歌单
     */
    public void setPlayingForLauncher() {
        if (mPlayingForLauncher == null && mRecommendAlbums.size() != 0) {
            Random random = new Random();
            int n = random.nextInt(mRecommendAlbums.size());
            mPlayingForLauncher = mRecommendAlbums.get(n);
            KuWoCallback.getInstance().sendCoverInfo(mPlayingForLauncher.getImageUrl());
            Log.d("huanxue", TAG + "---setPlayingForLauncher----mLauncherAlbum---" + mPlayingForLauncher.getName());
        } else {
            Log.w("huanxue", TAG + "---haved  Launcher album  or  RecommendAlbums size is 0");
        }
    }


    public List<BaseQukuItem> getAllRecommendAlbums() {
        return mRecommendAlbums;
    }

    public void setRecommendAlbums(List<BaseQukuItem> recommendAlbums) {
        this.mRecommendAlbums = recommendAlbums;
    }

    public long getClickAlbumID() {
        return mClickAlbumID;
    }

    //收藏歌单专辑取相反ID，用负数表示
    public void setClickAlbumID(long clickAlbumID) {
        Log.d("huanxue", TAG + "----setClickAlbumID------:" + clickAlbumID);
        mClickAlbumID = clickAlbumID;
    }

    public long getPLayingAlbumID() {
        return mPLayingAlbumID;
    }

    public void setPLayingAlbumID(long PLayingAlbumID) {
        mPLayingAlbumID = PLayingAlbumID;
        Log.d("huanxue", TAG + "----setPLayingAlbumID------:" + mPLayingAlbumID);
    }


    public List<Music> getDailyMusics() {
        return mDailyMusics;
    }

    public void setDailyMusics(List<Music> recommendMusics) {
        mDailyMusics = recommendMusics;
    }

    public boolean getPlayRecommendStatus() {
        return mPlayRecommendStatus;
    }

    public void setPlayRecommendStatus(boolean playRecommendStatus) {
        mPlayRecommendStatus = playRecommendStatus;
    }

    public boolean getRecommend() {
        return isRecommend;
    }

    public void setRecommend(boolean recommend) {
        isRecommend = recommend;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
    }

  /*  public MediaInfo getMediaInfo() {
        return mMediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        mMediaInfo = mediaInfo;
    }*/

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public String getPlayListTitle() {
        return mPlayListTitle;
    }

    public void setPlayListTitle(String playListTitle) {
        mPlayListTitle = playListTitle;
    }

    public int getVipLevel() {
        return mVipLevel;
    }

    public void setVipLevel(int vipLevel) {
        mVipLevel = vipLevel;
    }

    public boolean getLogon() {
        return isLogon;
    }

    public void setLogon(boolean logon) {
        isLogon = logon;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public boolean getPlayMixList() {
        return isPlayMixList;
    }

    public void setPlayMixList(boolean playMixList) {
        isPlayMixList = playMixList;
    }

    public List<Music> getCurrentShowList() {
        return mCurrentShowList;
    }

    public void setCurrentShowList(List<Music> currentShowList) {
        mCurrentShowList = currentShowList;
    }

    public Music getCurrentPlayMusic() {
        return mCurrentPlayMusic;
    }

    public void setCurrentPlayMusic(Music currentPlayMusic) {
        mCurrentPlayMusic = currentPlayMusic;
    }

    public Bitmap getCurrentMusicBitmap() {
        return mCurrentMusicBitmap;
    }

    public void setCurrentMusicBitmap(Bitmap currentMusicBitmap) {
        mCurrentMusicBitmap = currentMusicBitmap;
    }

    public List<LyricLine> getCurrentPlayLyric() {
        return mCurrentPlayLyric;
    }

    public void setCurrentPlayLyric(List<LyricLine> currentPlayLyric) {
        mCurrentPlayLyric = currentPlayLyric;
    }

    public boolean getPlayStatus() {
        return mPlayStatus;
    }

    public void setPlayStatus(boolean playStatus) {
        if (mPlayStatus != playStatus) {
            mPlayStatus = playStatus;
            KuWoCallback.getInstance().sendInfoToVR();
        }
    }

    public String getClickTitle() {
        return mClickTitle;
    }

    public void setClickTitle(String clickTitle) {
        mClickTitle = clickTitle;
    }

    public String getClickSearchItem() {
        return mClickSearchItem;
    }

    public void setClickSearchItem(String clickSearchItem) {
        mClickSearchItem = clickSearchItem;
    }

    public List<BaseQukuItem> getCategoriesList() {
        return mCategoriesList;
    }

    public void setCategoriesList(List<BaseQukuItem> categoriesList) {
        mCategoriesList = categoriesList;
    }

    public int getPlayQuality() {
        return mPlayQuality;
    }

    public void setPlayQuality(int playQuality) {
        if (mPlayQuality != playQuality) {
            mPlayQuality = playQuality;
        }
    }

  /*  *//**
     * 仅判断是否含Tbox
     *
     * @return true？低配，不含Tbox，false？高中配，含Tbox
     *//*
    public boolean isLowOption() {
        String config = SystemProperties.get("car.config");
        try {
            int value = Integer.parseInt(config);
            value = value & 0xffff;
            value = (value >> 8) >> 4;
            value = (value & 0x02) >> 1;
            return value != 1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // 默认返回低配
        return true;
    }*/
}
