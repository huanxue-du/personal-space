package com.sz.huanxue.androidapp.data.local.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * 操作数据库的sql方法实现
 * 设计：仅提供基础的CRUD方法，复杂逻辑交由外部复合逻辑及方法调用
 * @author huanxue
 * Created by HSAE_DCY on 2021.1.4.
 */
@Dao
public interface UserDAO {
    /**
     * 1. OnConflictStrategy.REPLACE：冲突策略是取代旧数据同时继续事务。
     * 2. OnConflictStrategy.ROLLBACK：冲突策略是回滚事务。
     * 3. OnConflictStrategy.ABORT：冲突策略是终止事务。
     * 4. OnConflictStrategy.FAIL：冲突策略是事务失败。
     * 5. OnConflictStrategy.IGNORE：冲突策略是忽略冲突。
     *
     * @param entity
     * @return 当@Insert注解的方法只有一个参数的时候，这个方法也可以返回一个long，
     * 当@Insert注解的方法有多个参数的时候则可以返回long[]或者r List<Long>。long都是表示插入的rowId。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity entity);

    /**
     * @param entity
     * @return @Delete对应的方法也是可以设置int返回值来表示删除了多少行
     * (通过参数里面的primary key找到要删除的行)
     */
    @Delete()
    int deleteUsers(UserEntity entity);

    /**
     * @param entity
     * @return @Update注解的方法也可以返回int变量。表示更新了多少行
     * (通过参数里面的primary key找到要删除的行)
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateUsers(UserEntity entity);

    /**
     * 根据userId更新该账户的密码
     *
     * @param userId 账户的唯一标示
     * @param vin vin码
     */
    @Query("update " + UserEntity.TABLE_NAME + " set vin = :vin WHERE userId = :userId")
    int updateVin(String userId, String vin);

    /**
     * 更新表中的部分字段
     *
     * @param entity
     * @return
     */
    @Update(entity = UserEntity.class)
    int updateUsers(UserEntity.OneEntity entity);


    @Update(entity = UserEntity.class)
    int updateUser111(UserEntity.TwoEntity twoEntity);

    /**
     * 查询整张表的数据
     *
     * @return
     */
    @Query("SELECT * FROM " + UserEntity.TABLE_NAME)
    List<UserEntity> loadAllUserInfo();

    /**
     * 获取个人对应的所有本地数据；
     *
     * @param userId
     * @return 返回结果可以是数组，也可以是List
     */
    @Query("SELECT * FROM  " + UserEntity.TABLE_NAME + " WHERE userId = :userId")
    List<UserEntity> queryByUserId(String userId);

    /**
     * 查询id值属性在minID与maxID之间的所有数据
     *
     * @param minID 最小id值
     * @param maxID 最大id值
     * @return 所有符合查询条件的结果
     */
    @Query("SELECT * FROM " + UserEntity.TABLE_NAME + " WHERE id BETWEEN :minID AND :maxID")
    List<UserEntity> loadUsers(int minID, int maxID);

    /**
     * 根据userId 查询该账户的refreshToken
     *
     * @param userId 账户的唯一标示
     * @return accrssToken
     */
    @Query("select accrssToken from  " + UserEntity.TABLE_NAME + " where userId = :userId")
    String queryToken(String userId);

}
