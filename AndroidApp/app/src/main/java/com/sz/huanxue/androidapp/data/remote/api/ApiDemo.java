package com.sz.huanxue.androidapp.data.remote.api;



import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * created
 * 此类为 retrofit2主要注解使用demo类，供参考；共二十多个注解，根据功能大概分为三类：
 * <p>
 * 一、请求方法类
 * GET、POST、PUT、DELETE、PATCH、HEAD、OPTIONS、HTTP
 * <p>
 * 二、标记类
 * FormUrlEncoded、（结合Field、FieldMap使用）
 * Multipart、（结合、Part、PartMap使用）
 * Streaming
 * <p>
 * 三、参数类
 * Headers、Header、Body、Field、FieldMap、Part、PartMap、Query、QueryMap、Path、URL
 * <p>
 * Query，QueryMap使用在get请求时设置请求参数：
 * Body，Field， FieldMap， Part，PartMap 用在Post方法中设置参数
 *
 * @author Daikin.Da
 */
public interface ApiDemo {


    /**
     * 请求方法类注解（除掉HTTP注解的演示使用），以get为例说明：
     * （）内参数代表URL地址，有以下几种使用方法：
     * baseUrl =="http://www.baidu.com/api"
     * 1、“/user”：表示 "http://www.baidu.com/user";
     * 2、“user”：表示 "http://www.baidu.com/user";
     * baseUrl =="http://www.baidu.com/api/"
     * 3、“/user”：表示 "http://www.baidu.com/user";
     * 4、“user”：表示 "http://www.baidu.com/api/user";
     * </b>
     * “version”为占位符号 ，可以在方法参数中通过Path注解传入；
     *
     * @return
     * @params 参考 {@link #getHeader(String, String)}Query，QueryMap使用在get请求时设置请求参数：
     */
    @GET("{version}/user")
    Observable<Object> get(@Path("version") String version, @QueryMap Map<String, String> params);


    /**
     * HTTP 可以设置为请求方法类 其他几个任意方法（通过method制定）。
     * HTTP 内部它包含三个参数，方法(method)，路径(path)，和是否有body(hasBody)。
     *
     * @return
     */
    @HTTP(method = "get", path = "{version}/user", hasBody = false)
    Observable<Object> http(@Path("version") String version);

    /**
     * 正常情况下默认头部都会同意在retrofit初始化时在烂机器中配置
     * Headers可以给该方法添加消息头部；
     * Query，QueryMap使用在get请求时设置请求参数：
     *
     * @return
     */
    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: RetrofitBean-Sample-App",
    })
    @GET("{version}/user")
    Observable<Object> getHeader(@Path("version") String version, @Query("id") String id);

    /**
     * FormUrlEncoded 表示请求体为Form表单  对应：Content-Type:application/x-www-form-urlencoded
     * <p>
     * 数据是为键值对的方式以表单提交，对应的数据用@ Field，@ FieldMap这个来设置
     *
     * @param version
     * @return
     */
    @FormUrlEncoded
    @POST("{version}/register")
    Observable<Object> post(@Path("version") String version, @Field("name") String name, @Field("pwd") String pwd);

    /**
     * 参考{@link #post(String, String, String)}
     *
     * @param version
     * @return
     */
    @FormUrlEncoded
    @POST("{version}/register")
    Observable<Object> post(@Path("version") String version, @FieldMap Map<String, String> maps);

    /**
     * Multipart 表示支持文件上传的Form表单请求  对应文件上传的：Content-Type:multipart/form-data
     * 提交表单数据。对应的参数注释解为@ Part，@ PartMap。
     * 另外还有json数据，对应参数注释解@Body。
     * <p>
     * <p>
     * RequestBody name = RequestBody.create(MediaType.parse("image/*"), "name");
     * File picture= new File(path);
     * RequestBody requestFile = RequestBody.create(MediaType.parse(AppConstants.CONTENT_TYPE_FILE), picture);
     * MultipartBody.Part picture = MultipartBody.Part.createFormData("picture", picture.getName(), requestFile);
     *
     * @return
     */
    @Multipart
    @POST("upload/head")
    Observable<ResponseBody> post(@Part("name") RequestBody name, @Part MultipartBody.Part picture);

    /**
     * RequestBody requestFile = RequestBody.create(MediaType.parse(AppConstants.CONTENT_TYPE_FILE), picture);
     * Map<String, RequestBody> params = new HashMap<>();
     * params.put("picture\"; filename=\"" + picture.getName() + "", requestFile);
     *
     * @param name
     * @param pictures
     * @return
     */
    @Multipart
    @POST("upload/heads")
    Observable<ResponseBody> post(@Part("name") RequestBody name, @PartMap Map<String, RequestBody> pictures);


    /**
     * 下载使用Streaming做下载，响应体的数据用流的形式返回
     * 未使用该注解，默认会把数据全部载入内存，之后通过流获取数据也是读取内存中数据，所以返回数据较大时，需要使用该注解。
     *
     * @return
     */
    @Streaming
    @GET("file/file1")
    Observable<ResponseBody> download();


    /**
     * 参考{@link #post(String, String, String)}
     * Body为非表单提交方式；一般为自定义实体；使用body时不需要使用 @FormUrlEncoded
     *
     * @param version
     * @return
     */
    @POST("{version}/register")
    Observable<Object> post(@Path("version") String version, @Body Object o);

    /**
     * @param url
     * @return
     */
    @GET
    Observable<Object> post(@Url String url);

}
