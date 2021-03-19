package com.sz.huanxue.androidapp.utils;

import android.content.Context;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.7.10.
 */
public class TextUtils {

    /**
     * 改变过长的歌手名为群星
     *
     * @param context 上下文
     * @param songName 网络获取到的歌手名
     * @return 适配过后的歌手名
     */
    public static String changeSingerName(Context context, String songName) {
        String str = "&";
        String name = songName;
        int count = 0;
        while (name.contains(str)) {
            name = name.substring(name.indexOf(str) + 1);
            ++count;
        }
        if (count < 3) {
            return songName;
        }

        return "群星";
    }

    /**
     * 改变歌曲总时长显示方式
     *
     * @param ms_time 毫秒
     * @return 时间显示
     */
    public static String changeMusicTime(int ms_time) {
        if (ms_time < 1000) {
            return "00:00";
        }
        StringBuilder musicTime = new StringBuilder();
        int s_total = ms_time / 1000;//歌曲总时长，单位秒
        int m_time = s_total / 60;
        int s_time = s_total % 60;
        //判断小时数
        if (m_time >= 60) {
            int h_time = m_time / 60;
            m_time = m_time % 60;
            if (h_time > 10) {
                musicTime.append(h_time);
            } else {
                musicTime.append("0");
                musicTime.append(h_time);
                musicTime.append(":");
            }
        }
        //判断分钟数
        if (m_time >= 10) {//大于10分钟
            musicTime.append(m_time);
            musicTime.append(":");
        } else if (m_time > 0) {//处于1-9分钟
            musicTime.append("0");
            musicTime.append(m_time);
            musicTime.append(":");
        } else {
            musicTime.append("00:");
        }
        //判断秒数
        if (s_time >= 10) {//处于10-59S
            musicTime.append(s_time);
        } else if (s_time > 0) {//处于1-9S
            musicTime.append("0");
            musicTime.append(s_time);
        } else {
            musicTime.append("00");
        }

        return musicTime.toString();
    }

}
