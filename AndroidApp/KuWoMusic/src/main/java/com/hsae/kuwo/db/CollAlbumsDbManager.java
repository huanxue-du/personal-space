package com.hsae.kuwo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import com.hsae.kuwo.utils.KuWoCallback;
import com.hsae.kuwo.utils.ThreadPoolManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author huanxue
 * Created by HSAE_DCY on 2020.9.17.
 */
public class CollAlbumsDbManager {

    public static final String ORDER_ID_ASC = "_id" + " ASC "; // 按_id字段升序排列
    private static final String TAG = "CollAlbumsDbManager";
    private static final String SQL_CREATE_TABLE_CATEGORY =
        "CREATE TABLE IF NOT EXISTS " + CurrentStoreColumns.TABLE_NAME + "(" + CurrentStoreColumns.ID + " integer primary key autoincrement, " // id
            + CurrentStoreColumns.BASEQUKUITEM_NAME + " varchar, " // item_name
            + CurrentStoreColumns.BASEQUKUITEM_ID + " varchar, " // item_id
            + CurrentStoreColumns.BASEQUKUITEM_IMAGE_URL + " varchar " // item_id
            + ")";
    private static CollAlbumsDbManager mInstance;
    private KuWoMusicDB mDatabase = null;

    public CollAlbumsDbManager() {
        mDatabase = KuWoMusicDB.getInstance();
    }

    public static CollAlbumsDbManager getInstance() {
        if (null == mInstance) {
            mInstance = new CollAlbumsDbManager();
        }
        return mInstance;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CATEGORY);
    }

    /**
     * 操作数据库进行专辑添加
     *
     * @param qukuItem 专辑
     */
    public synchronized void insertAlbum(final BaseQukuItem qukuItem) {
        if (qukuItem == null) {
            return;
        }
        Log.v("huanxue", TAG + "----insertAlbum----" + qukuItem.getName());
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.openWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(CurrentStoreColumns.BASEQUKUITEM_NAME, qukuItem.getName());
                values.put(CurrentStoreColumns.BASEQUKUITEM_ID, qukuItem.getId());
                values.put(CurrentStoreColumns.BASEQUKUITEM_IMAGE_URL, qukuItem.getImageUrl());
                database.insert(CurrentStoreColumns.TABLE_NAME, null, values);
                getAllAlbums();
            }
        });

    }

    /**
     * 操作数据库进行专辑删除
     *
     * @param qukuItem 专辑
     */
    public synchronized void deleteAlbum(final BaseQukuItem qukuItem) {
        if (qukuItem == null) {
            return;
        }
        Log.v("huanxue", TAG + "----deleteAlbum----" + qukuItem.getName());
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.getReadableDatabase();
                database.delete(CurrentStoreColumns.TABLE_NAME, CurrentStoreColumns.BASEQUKUITEM_ID + "=?",
                    new String[]{String.valueOf(qukuItem.getId())});
                getAllAlbums();
            }
        });
    }

    /**
     * 查询数据库中是否包含该专辑,根据专辑ID查询
     *
     * @param qukuItem 专辑
     * @return 1代表已收藏 0代表未收藏
     */
    public synchronized int seleteAlbum(final BaseQukuItem qukuItem) {
        if (qukuItem == null) {
            return 0;
        }
        Log.v("huanxue", TAG + "----seleteAlbum----" + qukuItem.getName());
        SQLiteDatabase database = mDatabase.getReadableDatabase();
        String selection = CurrentStoreColumns.BASEQUKUITEM_ID + "=?";
        String[] selectionAgrs = new String[]{String.valueOf(qukuItem.getId())};
        Cursor cursor = database.query(CurrentStoreColumns.TABLE_NAME, null, selection, selectionAgrs, null, null, ORDER_ID_ASC, null);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count > 0 ? 1 : 0;
    }

    /**
     * 获取数据库中全部专辑数据
     */
    public void getAllAlbums() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.getReadableDatabase();
                Cursor cursor = database.query(CurrentStoreColumns.TABLE_NAME, null, null, null, null, null, ORDER_ID_ASC, null);
                List<BaseQukuItem> qukuItemList = new ArrayList<>();
                if (cursor.getCount() > 0) {
                    try {
                        while (cursor.moveToNext()) {
                            BaseQukuItem qukuItem = new BaseQukuItem();
                            String id = cursor.getString(cursor.getColumnIndex(CurrentStoreColumns.BASEQUKUITEM_ID));
                            String name = cursor.getString(cursor.getColumnIndex(CurrentStoreColumns.BASEQUKUITEM_NAME));
                            String imageUrl = cursor.getString(cursor.getColumnIndex(CurrentStoreColumns.BASEQUKUITEM_IMAGE_URL));
                            qukuItem.setId(id);
                            qukuItem.setName(name);
                            qukuItem.setImageUrl(imageUrl);
                            qukuItemList.add(qukuItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //回调给客户端进行UI刷新
                KuWoCallback.getInstance().callBackUpdateMyAlbums(qukuItemList);
                cursor.close();
            }
        });
    }

    private interface CurrentStoreColumns {

        String TABLE_NAME = "coll_albums";
        String ID = "_id";
        String BASEQUKUITEM_ID = "item_id";
        String BASEQUKUITEM_NAME = "item_name";
        String BASEQUKUITEM_IMAGE_URL = "item_image_url";
    }
}
