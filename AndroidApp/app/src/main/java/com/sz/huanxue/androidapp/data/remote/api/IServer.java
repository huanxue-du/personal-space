package com.sz.huanxue.androidapp.data.remote.api;

import com.sz.huanxue.androidapp.data.remote.bean.OpenRecord;
import com.sz.huanxue.androidapp.data.remote.bean.QrBean;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.12.30.
 */
public interface IServer {
    /**
     * @Query，@QueryMap Query主要用于Get请求数据，用于拼接在拼接在Url路径后面的查询参数，一个@Query相当于拼接一个参数，多个参数中间用，隔开。
     * @Field，@FieldMap Field主要用于Post请求数据。如果请求为post实现，那么最好传递参数时使用@Field、@FieldMap和@FormUrlEncoded。
     * 因为@Query和或QueryMap都是将参数拼接在url后面的，而@Field或@FieldMap传递的参数时放在请求体的
     * @FormUrlEncoded 在post请求中使用@Field和@FieldMap后必须添加该注解，否则程序会抛出异常
     * @Path 主要用于Get请求，用于替换Url路径中的变量字符。
     * @Url 动态的Url请求数据的注解。
     */
    /**
     * token请求头的key
     */
    String AUTHORIZATION = "Authorization";
    /**
     * 指明的json类型
     */
    MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * 验证accessToken是否失效
     *
     * @param accessToken token
     * @return 返回的数据
     */
    @GET("/auth/token/accessToken")
    Observable<Object> verifyAccessToken(@Header(AUTHORIZATION) String accessToken);

    /**
     * 获取扫码登录二维码
     *
     * @param deviceId deviceId
     * @param deviceId Vin码
     * @param deviceType 登录设备上屏幕的序号，值恒等于11 主驾
     * @return 返回的数据
     */
    @GET("auth/qrRandomCode/{deviceId}/{deviceType}")
    Observable<QrBean> reqQrUrl(@Path("deviceId") String deviceId, @Path("deviceType") String deviceType);

    /**
     * 登录
     *
     * @param deviceId deviceId
     * @param deviceType deviceType
     * @param password 密码
     * @param loginId 登录id
     * @param loginMode 登录模式
     * @param loginType 登录类型
     * @param pdsn pdsn
     * @param sign 签名
     * @param ts 时间戳
     * @return 返回的数据
     */
    @POST("/auth/userLogin")
    @FormUrlEncoded
    Observable<Object> login(@Field("loginId") String loginId, @Field("loginType") String loginType, @Field("password") String password, @Field("deviceId") String deviceId, @Field("deviceType") String deviceType, @Field("pdsn") String pdsn, @Field("sign") String sign, @Field("loginMode") String loginMode, @Field("ts") long ts);

    /**
     * 表单的请求方式
     *
     * @param accessToken
     * @param oauthProviderId
     * @param statusCode
     * @param statusComment
     * @param openTime
     * @return
     */
    @FormUrlEncoded
    @POST("/cp-openingrecord/cpBill/billRecord")
    Observable<OpenRecord> getRecord(@Header(AUTHORIZATION) String accessToken, @Field("oauthProviderId") String oauthProviderId, @Field("statusCode") String statusCode, @Field("statusComment") String statusComment, @Field("openTime") String openTime);

    /**
     * json的请求方式，必须使用RequestBody
     *
     * @param accessToken
     * @param body
     * @return
     */
    @POST("/cp-openingrecord/cpBill/billRecord")
    Observable<OpenRecord> getRecord(@Header(AUTHORIZATION) String accessToken, @Body RequestBody body);

}
