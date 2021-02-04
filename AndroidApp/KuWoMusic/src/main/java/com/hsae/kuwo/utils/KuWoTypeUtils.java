package com.hsae.kuwo.utils;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.MusicList;
import cn.kuwo.mod.lyric.LyricLine;

/**
 * 类型格式转换工具类
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.7.31.
 */
public class KuWoTypeUtils {

    /**
     * 将标准歌词转换为String类型歌词
     *
     * @param lyricLines 标准歌词
     * @return List<String>
     */
    public static List<String> getStringLyric(List<LyricLine> lyricLines) {
        List<String> strings = new ArrayList<>();
        for (LyricLine lyric : lyricLines) {
            strings.add(lyric.getLyric());
        }
        return strings;
    }

   /* *//**
     * 将Music 转换为 Song 不带歌曲图片
     *
     * @param music 酷我歌曲类型
     * @return Song类型
     *//*
    public static MediaInfo getSong(Music music, String imageUrl) {
        if (music == null) {
            return null;
        }
        return new MediaInfo(SourceConst.App.KUWO_MUSIC.ordinal(), music.name, music.artist, imageUrl);
    }*/

    /**
     * 将MusicList 转换为 music 集合
     *
     * @param list 整个列表
     * @return music 集合
     */
    public static List<Music> changeMusicList(MusicList list) {

        List<Music> musicList = new ArrayList<>();
        if (list != null) {
            for (Music music : list) {
                musicList.add(music);
            }
        }
        return musicList;
    }
}
