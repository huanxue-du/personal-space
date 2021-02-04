package com.hsae.kuwo.utils;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.5.18.
 */
public class KuWoConstants {

    public static final int MSG_DELAY_TIME = 5000;
    public static final int MSG_SDK_PLAY = 0x0000001;
    public static final int MSG_CALLBACK_OTHER_LIST = 0x0000008;
    public static final int MSG_CALLBACK_QRCODE = 0x0000009;

    public static final String MEMORY_FRAGMENT = "memoryFragment";
    /**
     * 红旗APP_ID
     */
    public static final String APP_ID_HQ = "5ee6a414d2d111e9812f6c92bf62a58c";
    /**
     * 酷我音乐APP_ID
     */
    public static final String APP_ID_KW = "az3cjkk6d2r9";
    public static final String APP_KEY = "9jj6hc6qmz";

    public static final String APP_USERID = "8edb5e7d65c94096bf32fc4c8bcacf15";

    public static final String NET_REQUEST_TITLE_HEADER = "Authorization";
    public static final String NET_REQUEST_VALUE_HEADER = "Bearer ";
    /**
     * 帐号切换广播
     */
    public static final String ACTION_CHANGE_USERINFO = "com.hsae.user.CHANGE";
    /**
     * 记忆酷我音乐歌名
     */
    public static final String SP_MUSIC_NAME = "kuwo_music_name";
    /**
     * 记忆酷我音乐歌手
     */
    public static final String SP_MUSIC_SINGER = "kuwo_music_singer";
    /**
     * 记忆酷我音乐rid
     */
    public static final String SP_MUSIC_RID = "kuwo_music_rid";
    /**
     * 记忆酷我音乐歌曲总长度
     */
    public static final String SP_MUSIC_DUR = "kuwo_music_dur";
    /**
     * 记忆酷我音乐播放状态
     */
    public static final String SP_MUSIC_STATUS = "kuwo_music_status";
    /**
     * 记忆酷我音乐播放进度
     */
    public static final String SP_MUSIC_PROGRESS = "kuwo_music_progress";
    /**
     * 记忆酷我音乐对账服务状态
     */
    public static final String SP_RECORD_STATUS = "kuwo_music_record_status";
    /**
     * tts播报：好的
     */
    public static final String TTS_OK = "OK";
    /**
     * tts播报：网络异常
     */
    public static final String TTS_NETWORK_ERROR = "当前网络异常，请稍后再试";
    /**
     * tts播报：未找到相似歌曲
     */
    public static final String TTS_NO_SIMILAR = "抱歉，未找到相似歌曲";
    /**
     * tts播报：未找到歌曲
     */
    public static final String TTS_NO_SEARCH = "抱歉，没有找到你说的，现在为你推荐其他歌曲";
    /**
     * 表示默认
     */
    public static final String DEFAULT = "default";
    /**
     * 表示发现页hot标签
     */
    public static final String DATA_FIND_HOT = "hot";
    /**
     * 表示发现页更多
     */
    public static final String DATA_FIND_MORE = "more";
    /**
     * 表示分类歌单
     */
    public static final String DATA_CATEGORIES_SONGLIST = "categories_songlist";
    /**
     * 表示更多分类标签
     */
    public static final String DATA_MORE_CATEGORIES = "more_categories";
    /**
     * 酷我音乐
     */
    public static final int MUSIC_KUWO = 1;
    /**
     * 搜索音乐
     */
    public static final int SEARCHTYPE_MUSIC = 1;
    /**
     * 搜索歌手
     */
    public static final int SEARCHTYPE_ARTIST = 2;
    /**
     * 搜索专辑
     */
    public static final int SEARCHTYPE_ALBUM = 3;
    /**
     * 搜索歌单
     */
    public static final int SEARCHTYPE_SONGLIST = 4;
    /**
     * 单曲循环
     */
    public static final int MODE_SINGLE_CIRCLE = 0;
    /**
     * 顺序播放
     */
    public static final int MODE_ALL_ORDER = 1;
    /**
     * 循环播放
     */
    public static final int MODE_ALL_CIRCLE = 2;
    /**
     * 随机播放
     */
    public static final int MODE_ALL_RANDOM = 3;

    /**
     * 流畅品质
     */
    public static final int PLAY_QUALITY_ONE = 1;
    /**
     * 高品质
     */
    public static final int PLAY_QUALITY_TWO = 2;
    /**
     * 超高品质
     */
    public static final int PLAY_QUALITY_THREE = 3;
    /**
     * 无损品质
     */
    public static final int PLAY_QUALITY_FOUR = 4;

    /**
     * 未收藏状态
     */
    public static final int COLL_FALSE = -1;

    /**
     * 收藏状态
     */
    public static final int COLL_TRUE = 1;

    /**
     * 历史记录最多保存10条
     */
    public static final int MAX_KEY_WORDS = 10;
    /**
     * 播放与Usb音乐混合列表
     */
    public static final boolean PLAY_MIXLIST_TRUE = true;
    /**
     * 播放非混合列表
     */
    public static final boolean PLAY_MIXLIST_FALSE = false;
    /**
     * 酷我音乐缓存空间
     */
    public static final int MAX_CACHE_SPACE = 1024;
    /**
     * 无感登录未成功
     */
    public static final boolean LOGON_FALSE = false;
    /**
     * 无感登录成功
     */
    public static final boolean LOGON_TRUE = true;

    /**
     * 普通用户
     */
    public static final int VIP_NO = 0;
    /**
     * 车载会员
     */
    public static final int VIP_CAR = 1;
    /**
     * 音乐包用户
     */
    public static final int VIP = 2;
    /**
     * 豪华VIP用户
     */
    public static final int VIP_LUXURY = 3;
    /**
     * 播放列表：酷我音乐
     */
    public static final int LIST_KUWO = 1;
    /**
     * 酷我音乐播放类型,替换Music列表并播放
     */
    public static final int PLAY_TYPE_REPLACEANDPLAY = 1;
    /**
     * 酷我音乐播放类型,单曲播放,追加该歌曲在当前列表之后
     */
    public static final int PLAY_TYPE_SINGLE = 2;
    /**
     * 酷我音乐播放类型,自建歌单MusicList播放
     */
    public static final int PLAY_TYPE_MUSICLIST = 3;
    /**
     * 酷我音乐播放类型,当前播放列表的点击事件
     */
    public static final int PLAY_TYPE_CURRENT_PLAYLIST = 4;
    /**
     * 酷我音乐播放类型,播放酷我电台
     */
    public static final int PLAY_TYPE_RADIO = 5;

    /**
     * 音乐主页面tab：发现
     */
    public static final int FRAGMENT_TAB_KUWO = 1;
    /**
     * 音乐主页面tab：本地
     */
    public static final int FRAGMENT_TAB_LOCAL = 2;
    /**
     * 音乐主页面tab：我的
     */
    public static final int FRAGMENT_TAB_MINE = 3;
    /**
     * 音乐主页面tab：搜索
     */
    public static final int FRAGMENT_TAB_SEARCH = 4;
    /**
     * 每日推荐专辑ID
     */
    public static final long ID_DAILY_RECOMMEND = 9;
    /**
     * 歌曲播放列表上限，VR需求
     */
    public static final int MAX_PLAYLIST = 30;
    /**
     * 歌曲播放列表上限默认值
     */
    public static final int MAX_PLAYLIST_DEFAULT = Integer.MAX_VALUE;

    /**
     * 每次请求的歌曲数量
     */
    public static final int MUSIC_ONLINE_SIZE = 30;
    /**
     * 首次请求的歌曲页数
     */
    public static final int MUSIC_ONLINE_PAGE_FIRST = 0;
    /**
     * 播放列表ID
     */
    public static final long ID_PLAYLIST = -1;
    /**
     * 搜索单曲列表ID
     */
    public static final long ID_SEARCH_SINGLE = -2;

    /**
     * 1、 恢复焦点
     */
    public static final int FOCUS_STATUS_GAIN = 1;
    /**
     * 2、失去焦点
     */
    public static final int FOCUS_STATUS_LOST = 2;
    /**
     * 3、释放焦点
     */
    public static final int FOCUS_STATUS_RELEASE = 3;

    /**
     * VR需求：搜索指定歌曲，匹配专辑，随机取结果中前三首的一个
     */
    public static final int VR_SEARCH_MUSIC_SIZE = 3;
    /**
     * VR需求：搜索指定歌手/专辑的歌曲，取结果中的第一个结果
     */
    public static final int VR_SEARCH_ARTIST_SIZE = 1;
    /**
     * VR需求：指定的专辑，取结果中前30首
     * 歌手名、歌曲名、专辑名，结果需要校验，如果返回很多，取返回页第一页30首筛选
     */
    public static final int VR_MUSICLIST_SIZE = 30;
    /**
     * 歌曲权限信息校验 actionType 1: 试听
     */
    public static final int CHARGE_ACTIONTYPE = 1;
    /**
     * 歌曲权限信息校验 quality  0:默认当前播放音质
     */
    public static final int CHARGE_QUALITY = 0;
}

