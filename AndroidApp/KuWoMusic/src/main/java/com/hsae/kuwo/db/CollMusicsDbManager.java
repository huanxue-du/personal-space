package com.hsae.kuwo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hsae.kuwo.utils.KuWoCallback;
import com.hsae.kuwo.utils.KuWoConstants;
import com.hsae.kuwo.utils.KuWoMemoryData;
import com.hsae.kuwo.utils.KuWoSdk;
import com.hsae.kuwo.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.Music;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.open.base.MusicChargeType;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.9.17.
 */
public class CollMusicsDbManager {

    public static final String ORDER_ID_ASC = "_id" + " ASC "; // 按_id字段升序排列
    private static final String TAG = "CollMusicsDbManager";
    private static final String SQL_CREATE_TABLE_CATEGORY =
            "CREATE TABLE IF NOT EXISTS " + CurrentStoreColumns.TABLE_NAME + "(" + CurrentStoreColumns.ID + " integer primary key autoincrement, " // id
                    + CurrentStoreColumns.BASEQUKUITEM_ID + " varchar, " // item_id
                    + CurrentStoreColumns.Music_Name + " varchar, " // item_id
                    + CurrentStoreColumns.Music_singer + " varchar, " // item_id
                    + CurrentStoreColumns.Music_rid + " varchar " // item_id
                    + ")";
    private static CollMusicsDbManager mInstance;
    private KuWoMusicDB mDatabase = null;

    public CollMusicsDbManager() {
        mDatabase = KuWoMusicDB.getInstance();
    }

    public static CollMusicsDbManager getInstance() {
        if (null == mInstance) {
            mInstance = new CollMusicsDbManager();
        }
        return mInstance;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CATEGORY);
    }

    /**
     * 操作数据库进行专辑添加
     *
     * @param mMusics 歌单列表
     * @param qukuItem 专辑
     */
    public synchronized void insertMusicList(final List<Music> mMusics, final BaseQukuItem qukuItem) {
        if (mMusics == null) {
            return;
        }
        Log.v("huanxue", TAG + "----insertMusicList----" + mMusics.size()+"  qukuItem:"+qukuItem.getName());
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.openWritableDatabase();
                for (Music music : mMusics) {
                    ContentValues values = new ContentValues();
                    values.put(CurrentStoreColumns.BASEQUKUITEM_ID, qukuItem.getId());
                    values.put(CurrentStoreColumns.Music_Name, music.name);
                    values.put(CurrentStoreColumns.Music_singer, music.artist);
                    values.put(CurrentStoreColumns.Music_rid, music.rid);
                    database.insert(CurrentStoreColumns.TABLE_NAME, null, values);
                }

            }
        });

    }

    /**
     * 操作数据库进行专辑删除
     *
     * @param mMusics 歌单列表
     * @param qukuItem 专辑
     */
    public synchronized void deleteMusicList(final List<Music> mMusics, final BaseQukuItem qukuItem) {
        if (mMusics == null) {
            return;
        }
        Log.v("huanxue", TAG + "----deleteMusicList----" + mMusics);
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.getReadableDatabase();
                database.delete(CurrentStoreColumns.TABLE_NAME, CurrentStoreColumns.BASEQUKUITEM_ID + "=?",
                        new String[]{String.valueOf(qukuItem.getId())});
            }
        });
    }

    /**
     * 获取对应专辑下的所有歌曲信息
     */
    public void getAllCollMusic(final BaseQukuItem qukuItem, final boolean needPlay) {
        if (qukuItem == null) {
            return;
        }
        Log.v("huanxue", TAG + "----getAllCollMusic----" + qukuItem.getName() + "----qukuItem.id:" + qukuItem.getId());
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.getReadableDatabase();
                String selection = CurrentStoreColumns.BASEQUKUITEM_ID + "=?";
                String[] selectionAgrs = new String[]{String.valueOf(qukuItem.getId())};
                Cursor cursor = database.query(CurrentStoreColumns.TABLE_NAME, null, selection, selectionAgrs, null, null, ORDER_ID_ASC, null);
                List<Music> musicList = new ArrayList<>();
                if (cursor.getCount() > 0) {
                    try {
                        while (cursor.moveToNext()) {
                            Music music = new Music();
                            String name = cursor.getString(cursor.getColumnIndex(CurrentStoreColumns.Music_Name));
                            String singer = cursor.getString(cursor.getColumnIndex(CurrentStoreColumns.Music_singer));
                            String rid = cursor.getString(cursor.getColumnIndex(CurrentStoreColumns.Music_rid));
                            music.name = name;
                            music.artist = singer;
                            music.rid = Long.valueOf(rid);
                            musicList.add(music);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                KuWoCallback.getInstance().callBackChargeMusic(musicList, new ArrayList<MusicChargeType>(), qukuItem.getId());
                if (needPlay) {
                    KuWoMemoryData.getInstance().setCurrentShowList(musicList);
                    KuWoSdk.getInstance().playMusics(0, KuWoConstants.MAX_PLAYLIST_DEFAULT);
                }
                cursor.close();
            }
        });

    }


    private interface CurrentStoreColumns {

        String TABLE_NAME = "coll_musics";
        String ID = "_id";
        String BASEQUKUITEM_ID = "item_id";
        String Music_Name = "music_name";
        String Music_singer = "music_singer";
        String Music_rid = "music_rid";
    }
}
