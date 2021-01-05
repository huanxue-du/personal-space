package com.sz.huanxue.androidapp.data.local.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.sz.huanxue.androidapp.data.local.db.UserEntity.TABLE_NAME;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.4.
 */
@Entity(tableName = TABLE_NAME)
public class UserEntity {
    public static final String TABLE_NAME = "UserEntity";
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id;
    @ColumnInfo(name = "userId")
    public String userId;
    @ColumnInfo(name = "point")
    public int point;
    @ColumnInfo(name = "accrssToken")
    public String accrssToken;
    @ColumnInfo(name = "vin")
    public String vin;
    @ColumnInfo(name = "voice")
    public int voice;
    @ColumnInfo(name = "aId")
    public String aId;

    public UserEntity() {
    }

    public UserEntity(String userId, int point, String accrssToken, String vin, int voice, String aId) {
        this.userId = userId;
        this.point = point;
        this.accrssToken = accrssToken;
        this.vin = vin;
        this.voice = voice;
        this.aId = aId;
    }

    public static class OneEntity {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        public Integer id;
        @ColumnInfo(name = "userId")
        public String userId;
        @ColumnInfo(name = "vin")
        public String vin;
    }

    public static class TwoEntity {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        public Integer id;
        @ColumnInfo(name = "userId")
        public String userId;
        @ColumnInfo(name = "vin")
        public String vin;
        @ColumnInfo(name = "voice")
        public Integer voice;
        @ColumnInfo(name = "aId")
        public String aId;
    }

}
