package com.sz.huanxue.androidapp.data.remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.12.29.
 */
public class RetrofitManager {
    private Retrofit mRetrofit;

    private RetrofitManager() {

    }

    public static RetrofitManager self() {
        return RetrofitMangerHolder.mInstance;
    }

    public <T> T getService(Class<T> service) {
        return mRetrofit.create(service);
    }

    public void init(Config config) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
                .readTimeout(config.readTimeout, TimeUnit.SECONDS)
                .writeTimeout(config.writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        //默认的okhttp打印日志的方式
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(config.baseUrl)
                .build();
    }

    private static class RetrofitMangerHolder {
        private static final RetrofitManager mInstance = new RetrofitManager();
    }

    public static class Config {
        int connectTimeout;
        int readTimeout;
        int writeTimeout;
        String baseUrl;


        public Config setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Config setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Config setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Config setWriteTimeout(int writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

    }

}
