package com.hsae.kuwo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 酷我音乐数据库
 */
public class KuWoMusicDB extends SQLiteOpenHelper {

    private static final String TAG = "KuWoMusicDB";
    private static final String DATA_BASE_NAME = "kuwo_music.db";
    private static final int FIRST_VERSION = 1;
    private static KuWoMusicDB sInstance = null;
    private SQLiteDatabase mSqLiteDatabase;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    public KuWoMusicDB(Context context) {
        super(context, DATA_BASE_NAME, null, FIRST_VERSION);
    }

    public static synchronized KuWoMusicDB getInstance() {
        return sInstance;
    }

    public static void initContext(Context context) {
        sInstance = new KuWoMusicDB(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        HistorySearchDBManager.getInstance().onCreate(db);
        HotCategoryDBManager.getInstance().onCreate(db);
        CollAlbumsDbManager.getInstance().onCreate(db);
        CollMusicsDbManager.getInstance().onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized SQLiteDatabase openWritableDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mSqLiteDatabase = sInstance.getWritableDatabase();
        }
        return mSqLiteDatabase;
    }

    public synchronized SQLiteDatabase openReadableDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mSqLiteDatabase = sInstance.getReadableDatabase();
        }
        return mSqLiteDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mSqLiteDatabase.close();
        }
    }
}
