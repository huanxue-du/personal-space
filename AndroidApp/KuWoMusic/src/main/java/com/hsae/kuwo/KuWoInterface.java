package com.hsae.kuwo;

import android.graphics.Bitmap;

import java.util.List;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.mod.lyric.LyricLine;
import cn.kuwo.open.base.MusicChargeType;

/**
 * 定义酷我音乐相关接口
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.7.6.
 */
public interface KuWoInterface {

    /**
     * 返回推荐歌单
     *
     * @param songList 歌单信息
     */
    void callBackRecommendList(List<BaseQukuItem> songList, String dataType);

    /**
     * 返回新歌歌单
     *
     * @param songList 歌单信息
     */
    void callBackNewSongList(List<BaseQukuItem> songList, String dataType);

    /**
     * 返回排行榜歌单
     *
     * @param songList 歌单信息
     */
    void callBackBillBroadList(List<BaseQukuItem> songList, String dataType);

    /**
     * 返回歌手歌单
     *
     * @param songList 歌单信息
     */
    void callBackArtistList(List<BaseQukuItem> songList, String dataType);

    /**
     * 返回热门分类标签
     *
     * @param songList 歌单信息
     */
    void callBackHotCategoriesList(List<BaseQukuItem> songList);

    /**
     * 返回全部分类标签
     *
     * @param songList 歌单信息
     * @param string 区分是分类歌单还是分类标签
     */
    void callBackMoreCategoriesList(List<BaseQukuItem> songList, String string);

    /**
     * 返回搜索结果
     *
     * @param list 搜索结果信息
     */
    void callBackSearchResultList(List<BaseQukuItem> list);

    /**
     * 返回歌词
     *
     * @param lyric 当前播放音乐的歌词
     */
    void callBackLyric(List<LyricLine> lyric);

    /**
     * 返回当前播放的歌曲权限信息列表
     *
     * @param list 歌曲列表信息
     * @param chargeResults 歌曲权限信息
     */
    void callBackChargeMusic(List<Music> list, List<MusicChargeType> chargeResults, long albumID);

    /**
     * 返回当前准备播放的歌曲
     *
     * @param song 歌曲信息
     */
    void callBackReadyPlayMusic(Music song);

    /**
     * 返回当前播放的进度
     *
     * @param playProgress 正在播放的进度
     * @param bufferProgress 缓冲的进度
     */
    void callBackProgress(int playProgress, int bufferProgress);

    /**
     * 返回当前的播放状态
     *
     * @param playStatus true 播放  false  未播放
     */
    void callBackPlayStatus(boolean playStatus);

    /**
     * 返回当前播放歌曲的图片
     *
     * @param bitmap 图片
     */
    void callBackCurrentMusicBitmap(Bitmap bitmap);

    /**
     * 返回热门搜索词条
     *
     * @param hotWords 搜索词条
     */
    void callBackSearchHotKeywords(List<String> hotWords);

    /**
     * 返回播放模式
     *
     * @param playMode 播放模式类型
     */
    void callBackPlayMode(int playMode);

    /**
     * 返回搜索历史记录词条
     *
     * @param hotWords 搜索词条
     */
    void callBackSearchHistoryKeywords(List<String> hotWords);

    /**
     * 本网融合列表播放一首结束后回调
     */
    void callBackMediaCompletion();


    /**
     * 返回通知切换当前播放列表
     */
    void callBackChangeCurList();

    /**
     * 返回通知准备播放酷我音乐列表
     */
    void callBackReadyPlayKuWoMusicList(boolean isPlayMixList);

    /**
     * 返回我的收藏全部专辑集合
     */
    void callBackUpdateMyAlbums(List<BaseQukuItem> qukuItemList);

    /**
     * 返回VR指令最终执行结果,若为空，表示酷我音乐不播放
     */
    void callBackKuWoResult(Music music);

    /**
     * 当歌曲真正开始播放时回调
     *
     * @param song 歌曲信息
     */
    void callBackRealPlayMusic(Music song);

    /**
     * 当歌曲恢复播放时回调
     *
     * @param song 歌曲信息
     */
    void callBackContinuePlayMusic(Music song);

    /**
     * 返回歌曲的加载状态
     *
     * @param loadingStatus true 加载中  false  已加载
     */
    void callBackLoadingStatus(boolean loadingStatus);


}
