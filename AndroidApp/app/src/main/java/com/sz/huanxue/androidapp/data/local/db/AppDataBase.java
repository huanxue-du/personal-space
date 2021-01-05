package com.sz.huanxue.androidapp.data.local.db;

import com.sz.huanxue.androidapp.HuanXueApp;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.4.
 */
@Database(entities = {UserEntity.class}, version = 1, exportSchema = true)
public abstract class AppDataBase extends RoomDatabase {
    /**
     * 数据库版本 1->2 user表格新增了age列
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE User ADD COLUMN age integer");
        }
    };
    private static AppDataBase instance;

    public static AppDataBase getInstance() {
        if (instance == null) {
            synchronized (AppDataBase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(HuanXueApp.getContext(), AppDataBase.class, "huanxueApp.db")
                            //设置是否允许在主线程做查询操作
                            .allowMainThreadQueries()
                            // 设置迁移数据库如果发生错误，将会重新创建数据库(丢失原有数据)，而不是发生崩溃
                            .fallbackToDestructiveMigration()
                            //正确迁移数据库的方式，不丢失原有数据.多次数据库版本变更，需要添加多次Migration
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract UserDAO userDao();
}
