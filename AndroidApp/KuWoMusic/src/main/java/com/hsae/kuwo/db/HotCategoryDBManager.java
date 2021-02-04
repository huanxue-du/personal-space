package com.hsae.kuwo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hsae.kuwo.utils.KuWoCallback;
import com.hsae.kuwo.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.BaseQukuItemList;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.9.14.
 */
public class HotCategoryDBManager {

    public static final String ORDER_ID_DESC = "_id" + " DESC "; // 按_id字段降序排列
    public static final String ORDER_ID_ASC = "_id" + " ASC "; // 按_id字段升序排列
    private static final String TAG = "HistorySearchDBManager";
    private static final String SQL_CREATE_TABLE_CATEGORY =
            "CREATE TABLE IF NOT EXISTS " + CurrentStoreColumns.TABLE_NAME + "(" + CurrentStoreColumns.ID + " integer primary key autoincrement, " // id
                    + CurrentStoreColumns.BASEQUKUITEM_NAME + " varchar, " // item_name
                    + CurrentStoreColumns.BASEQUKUITEM_ID + " varchar " // item_id
                    + ")";
    private static HotCategoryDBManager mInstance;
    private KuWoMusicDB mDatabase = null;

    public HotCategoryDBManager() {
        mDatabase = KuWoMusicDB.getInstance();
    }

    public static HotCategoryDBManager getInstance() {
        if (null == mInstance) {
            mInstance = new HotCategoryDBManager();
        }
        return mInstance;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CATEGORY);
    }

    /**
     * 操作数据库进行词条添加
     *
     * @param keyWord 分类标签
     */
    private synchronized void insertDB(final BaseQukuItem keyWord) {
        if (keyWord == null) {
            return;
        }
        Log.v("huanxue", TAG + "----insertDB----" + keyWord.getName());
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.openWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(CurrentStoreColumns.BASEQUKUITEM_NAME, keyWord.getName());
                values.put(CurrentStoreColumns.BASEQUKUITEM_ID, keyWord.getId());
                database.insert(CurrentStoreColumns.TABLE_NAME, null, values);
            }
        });

    }


    /**
     * 添加默认分类数据
     *
     * @param songList 分类标签
     */
    public synchronized void insertDefaultCategory(final List<BaseQukuItem> songList) {
        if (songList == null) {
            return;
        }
        Log.v("huanxue", TAG + "----insertDefaultCategory----:" + songList.size());
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                //先清空当前数据库数据再添加默认数据
                SQLiteDatabase database = mDatabase.openWritableDatabase();
                database.delete(HotCategoryDBManager.CurrentStoreColumns.TABLE_NAME, null, null);

                for (BaseQukuItem item : songList) {
                    ContentValues values = new ContentValues();
                    values.put(CurrentStoreColumns.BASEQUKUITEM_NAME, item.getName());
                    values.put(CurrentStoreColumns.BASEQUKUITEM_ID, item.getId());
                    database.insert(CurrentStoreColumns.TABLE_NAME, null, values);
                }
            }
        });
    }


    /**
     * 更新用户主动点击的热门分类标签
     *
     * @param keyWord 分类标签
     */
    public synchronized void updateHotCategory(final BaseQukuItem keyWord) {
        Log.v("huanxue", TAG + "---updateHotCategory---" + keyWord.getName());
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.getReadableDatabase();
                int count = database
                        .delete(CurrentStoreColumns.TABLE_NAME, CurrentStoreColumns.BASEQUKUITEM_NAME + "=?", new String[]{keyWord.getName()});
                if (count == 0) {//未删除指定分类标签，则删除_id值最小的
                    database.execSQL(
                            "delete from " + CurrentStoreColumns.TABLE_NAME + " where _id=(select min(_id) from " + CurrentStoreColumns.TABLE_NAME + ")");
                }
                insertDB(keyWord);
            }
        });
    }


    /**
     * 获取数据库中所有的分类标签
     */
    public synchronized void getAllCategory() {
        Log.v("huanxue", TAG + "---getAllCategory---");
        SQLiteDatabase database = mDatabase.getReadableDatabase();
        Cursor searches = database.query(CurrentStoreColumns.TABLE_NAME, null, null, null, null, null, ORDER_ID_DESC, null);
        ArrayList<BaseQukuItem> results = new ArrayList<BaseQukuItem>();
        try {
            while (searches.moveToNext()) {
                BaseQukuItem item = new BaseQukuItemList();
                String stringName = searches.getString(searches.getColumnIndex(CurrentStoreColumns.BASEQUKUITEM_NAME));
                String stringId = searches.getString(searches.getColumnIndex(CurrentStoreColumns.BASEQUKUITEM_ID));
                item.setName(stringName);
                item.setId(stringId);
                results.add(item);
            }
        } finally {
            if (searches != null) {
                searches.close();
            }
        }
        KuWoCallback.getInstance().callBackHotCategoriesList(results);
    }

    public boolean getHasCategory(final BaseQukuItem keyWord) {
        SQLiteDatabase database = mDatabase.getReadableDatabase();
        return false;
    }

    private interface CurrentStoreColumns {

        String TABLE_NAME = "hot_category";
        String ID = "_id";
        String BASEQUKUITEM_ID = "item_id";
        String BASEQUKUITEM_NAME = "item_name";
    }
}
