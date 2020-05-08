package com.autolink.radio55.datas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.autolink.radio55.adapter.RadioEntity;
import com.autolink.radio55.app.AppDataUtils;
import com.autolink.radio55.utils.RadioDataUtils;

import java.util.LinkedList;

/**
 * 数据库操作类
 *
 * @author Administrator
 */
public class RadioDataBases extends SQLiteOpenHelper {

    public static RadioDataBases mInstance;

    private final static int DATABASE_VERSION = 5;
    private final static String DB_NAME = "autolink_radio.db";
    private final static String RADIO_TABLE_COLL = "radio_coll_table";


    /**
     * 如果mInstance为null,则根据context创建新的对象并返回
     *
     * @param context
     * @return
     */
    public static RadioDataBases getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RadioDataBases(context.getApplicationContext());
        }
        return mInstance;
    }

    private RadioDataBases(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }


    private SQLiteDatabase db = null;// 执行onCreate后对象db就不再为空，跟随表radio_coll_table的存在而存在,但需要获取权限

    @Override
    public void onCreate(SQLiteDatabase db1) {
        String TABLE_RADIO_COLL_CREATE = "create table if not exists " + RADIO_TABLE_COLL + " ( " + "id INTEGER ," + "type varchar(50), " + "frequency INTEGER, " + "iscoll INTEGER, " + "frequency_Type varchar(50) )";
        db1.execSQL(TABLE_RADIO_COLL_CREATE);
        this.db = db1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion) {
        String sql = " drop table " + RADIO_TABLE_COLL;
        db2.execSQL(sql);
        onCreate(db2);
    }


    private SQLiteDatabase getDatabaseWrit() {//获取数据库操作权限
        if (db == null) {
            db = getWritableDatabase();
        }
        return db;
    }

    public enum RADIO_DATA_TYPE {
        FM, AM, COLL_AM_FM
    }

    /**
     * 根据type类型删除对应表中的entity
     *
     * @param entity
     * @param type
     * @return
     */
    public boolean deleteByRadioEntity(RadioEntity entity, RADIO_DATA_TYPE type) {
        boolean re = false;
        getDatabaseWrit();
        int resul = 0;
        switch (type) {
            case AM:

                break;
            case FM:

                break;
            case COLL_AM_FM:
                resul = db.delete(RADIO_TABLE_COLL, RadioEntity.FREQUENCY + "=" + entity.getFrequency(), null);
                break;
        }
        if (resul > 0) {
            re = true;
        }
        AppDataUtils.getInstance().callbackList();
        return re;
    }


    /**
     * 根据band删除数据库非收藏状态数据，5代表非收藏状态
     *
     * @param band
     */
    public void deleteAllByIsColl(String band) {
        getDatabaseWrit();
        db.delete(RADIO_TABLE_COLL, "type =" + band + " and iscoll =5", null);

        AppDataUtils.getInstance().callbackList();
    }

    /**
     * 清除全部数据库
     *
     * @return boolean
     */
    public void deleteAll() {
        getDatabaseWrit();
        db.delete(RADIO_TABLE_COLL, null, null);
        AppDataUtils.getInstance().callbackList();
    }


    /**
     * 根据波段类型查询数据库数据
     *
     * @param band
     * @param orderbyString
     * @return
     */
    public LinkedList<RadioEntity> queryAllColl(String band, String orderbyString) {
        LinkedList<RadioEntity> list = new LinkedList<RadioEntity>();
        getDatabaseWrit();
        if (orderbyString == null) {
            orderbyString = RadioDataUtils.ASC;
        }
        Cursor cursor = db.query(RADIO_TABLE_COLL, null, "type =" + band, null, null, null, orderbyString);


        int id = 0;
        while (cursor.moveToNext()) {
            RadioEntity entity = new RadioEntity();
            entity.setId(cursor.getInt(cursor.getColumnIndex(RadioEntity.ID)));
            entity.setIndex(id);
            entity.setType(cursor.getString(cursor.getColumnIndex(RadioEntity.TYPE)));
            entity.setFrequency(cursor.getString(cursor.getColumnIndex(RadioEntity.FREQUENCY)));
            entity.setFrequencyType(cursor.getString(cursor.getColumnIndex(RadioEntity.FREQUENCY_TYPE)));
            list.add(entity);
            id++;
        }

        cursor.close();


        return list;
    }

    /**
     * 根据标识判断显示收藏数据
     *
     * @param band
     * @param orderbyString
     * @return
     */
    public LinkedList<RadioEntity> queryAllCollByNum(String band, String orderbyString) {
        LinkedList<RadioEntity> list = new LinkedList<RadioEntity>();
        getDatabaseWrit();
        if (orderbyString == null) {
            orderbyString = RadioDataUtils.ASC;
        }
        Cursor cursor = db.query(RADIO_TABLE_COLL, null, "type =" + band + " and iscoll = 0", null, null, null, orderbyString);

        int id = 0;

        while (cursor.moveToNext()) {
            RadioEntity entity = new RadioEntity();
            entity.setId(cursor.getInt(cursor.getColumnIndex(RadioEntity.ID)));
            entity.setIndex(id);
            entity.setType(cursor.getString(cursor.getColumnIndex(RadioEntity.TYPE)));
            entity.setFrequency(cursor.getString(cursor.getColumnIndex(RadioEntity.FREQUENCY)));
            entity.setFrequencyType(cursor.getString(cursor.getColumnIndex(RadioEntity.FREQUENCY_TYPE)));
            list.add(entity);
            id++;
        }

        cursor.close();


        return list;

    }


    /**
     * 添加一个RadioEntity到数据库中
     *
     * @param mRadioEntity
     * @param type
     * @return
     */
    public boolean insertRadioData(RadioEntity mRadioEntity, RADIO_DATA_TYPE type) {
        long returnID = -1;
        getDatabaseWrit();
        ContentValues initValues = new ContentValues();
        initValues.put(RadioEntity.ID, mRadioEntity.getId());
        initValues.put(RadioEntity.TYPE, mRadioEntity.getType());
        initValues.put(RadioEntity.FREQUENCY, mRadioEntity.getFrequency());
        initValues.put(RadioEntity.ISCOLL, mRadioEntity.getIsColl());
        initValues.put(RadioEntity.FREQUENCY_TYPE, mRadioEntity.getFrequencyType());

        switch (type) {
            case AM:
                break;
            case FM:
                break;
            case COLL_AM_FM:
                returnID = db.insert(RADIO_TABLE_COLL, null, initValues);
                break;
        }

        AppDataUtils.getInstance().callbackList();

        return returnID != -1;
    }


    /**
     * 根据频点更新数据库中数据的收藏状态
     *
     * @param freq
     * @param num
     */
    public void updateRadioData(String freq, RADIO_DATA_TYPE type, int num) {
        getDatabaseWrit();
        ContentValues contentValues = new ContentValues();
        contentValues.put("iscoll", num);
        switch (type) {
            case AM:
                break;
            case FM:
                break;
            case COLL_AM_FM:
                db.update(RADIO_TABLE_COLL, contentValues, " frequency = " + freq, null);
                break;
        }
        AppDataUtils.getInstance().callbackList();

    }

    /**
     * 查询数据库是否存在该频点
     *
     * @param string
     * @return
     */
    public boolean getRadioData(String string) {
        String sql = "select * from  " + RADIO_TABLE_COLL + "   where frequency  = " + "'" + string + "'";
        getDatabaseWrit();
        int number = 0;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            number = cursor.getCount();
        }
        cursor.close();
        return number != 0;
    }

    /**
     * 数据库中该频点是否显示收藏，0表示已收藏，非0表示未收藏
     */
    public boolean showRadioColl(String string) {
        String sql = "select iscoll from  " + RADIO_TABLE_COLL + "   where frequency  = " + "'" + string + "'";
        getDatabaseWrit();
        int number = 9;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            int a = cursor.getInt(cursor.getColumnIndex(RadioEntity.ISCOLL));
            if (a == RadioDataUtils.ISCOLL_TRUE) {
                number = RadioDataUtils.ISCOLL_TRUE;
            }
        }
        cursor.close();
        return number != 9;
    }
}
