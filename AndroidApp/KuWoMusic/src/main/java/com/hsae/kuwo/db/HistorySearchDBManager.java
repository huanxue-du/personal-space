package com.hsae.kuwo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.hsae.kuwo.utils.KuWoConstants;
import com.hsae.kuwo.utils.KuWoCallback;
import com.hsae.kuwo.utils.ThreadPoolManager;
import java.util.LinkedList;

/**
 * 历史搜索
 */
public class HistorySearchDBManager {//搜索历史返回之前需要重新排序

    private static final String TAG = "HistorySearchDBManager";
    private static HistorySearchDBManager mInstance;
    private KuWoMusicDB mDatabase = null;
    private LinkedList<String> mLinkedList;

    public HistorySearchDBManager() {
        mDatabase = KuWoMusicDB.getInstance();
    }

    public static HistorySearchDBManager getInstance() {
        if (null == mInstance) {
            mInstance = new HistorySearchDBManager();
        }
        return mInstance;
    }

    public void getAllHistoryList() {
        if (mLinkedList == null) {
            getAllKeyWordsDB();
        }
        KuWoCallback.getInstance().callBackSearchHistoryKeywords(mLinkedList);
    }

    private void setLinkedList(LinkedList<String> linkedList) {
        Log.i("huanxue", TAG + "----setLinkedList----");
        mLinkedList = linkedList;
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CurrentStoreColumns.TABLE_NAME + " (" + CurrentStoreColumns.KEY_WORDS + " TEXT);");
    }

    /**
     * 添加历史记录
     *
     * @param keyWord 历史记录词条
     */
    public void insert(String keyWord) {
        Log.v("huanxue", TAG + "----insert----" + keyWord);
        String delete = null;
        if (mLinkedList.contains(keyWord)) {
            delete = keyWord;
            mLinkedList.remove(keyWord);
        }
        if (mLinkedList.size() >= KuWoConstants.MAX_KEY_WORDS) {//删除最早的一个，并添加最新的
            delete = mLinkedList.get(0);
            mLinkedList.remove(0);
        }
        if (delete != null) {
            //操作数据库进行数据删除
            deleteDB(delete);
        }
        mLinkedList.add(keyWord);
        insertDB(keyWord);
    }

    /**
     * 清空全部搜索历史记录
     */
    public void deleteAll() {
        Log.v("huanxue", TAG + "----deleteAll----");
        mLinkedList.clear();
        deleteAllDB();
    }

    /**
     * 操作数据库进行词条添加
     *
     * @param keyWords 搜索词条
     */
    private synchronized void insertDB(final String keyWords) {
        Log.v("huanxue", TAG + "----insertDB----" + keyWords);
        KuWoCallback.getInstance().callBackSearchHistoryKeywords(mLinkedList);
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.openWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(CurrentStoreColumns.KEY_WORDS, keyWords);
                database.insert(CurrentStoreColumns.TABLE_NAME, null, values);
            }
        });
    }

    /**
     * 操作数据库进行词条删除
     *
     * @param keyWord 搜索词条
     */
    private void deleteDB(final String keyWord) {
        Log.v("huanxue", TAG + "----deleteDB----" + keyWord);
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.openWritableDatabase();
                database.delete(CurrentStoreColumns.TABLE_NAME, CurrentStoreColumns.KEY_WORDS + "=?", new String[]{keyWord});

            }
        });
    }

    /**
     * 操作数据库进行全部数据删除
     */
    private void deleteAllDB() {
        Log.v("huanxue", TAG + "----deleteAllDB----");
        KuWoCallback.getInstance().callBackSearchHistoryKeywords(mLinkedList);
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = mDatabase.openWritableDatabase();
                database.delete(CurrentStoreColumns.TABLE_NAME, null, null);

            }
        });
    }

    /**
     * 获取数据库中所有的历史记录
     */
    private synchronized void getAllKeyWordsDB() {
        SQLiteDatabase database = mDatabase.getReadableDatabase();
        Cursor searches = database.query(CurrentStoreColumns.TABLE_NAME, null, null, null, null, null, null, null);
        LinkedList<String> results = new LinkedList<String>();
        try {
            while (searches.moveToNext()) {
                String string = searches.getString(searches.getColumnIndex(CurrentStoreColumns.KEY_WORDS));
                results.add(string);
            }
        } finally {
            if (searches != null) {
                searches.close();
                searches = null;
            }
        }
        setLinkedList(results);
    }


    private interface CurrentStoreColumns {

        String TABLE_NAME = "history_search";
        String KEY_WORDS = "key_words";
    }
}
