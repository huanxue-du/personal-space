package com.sz.huanxue.androidapp.utils;

import com.sz.huanxue.androidapp.net.RxWeatherService;
import com.sz.huanxue.androidapp.ui.entity.RadioEntity;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.3.23.
 */
public class NetUtils {

    private static final NetUtils sInstances = new NetUtils();

    public static NetUtils getInstance() {
        return sInstances;
    }

    private void doRequestByRxRetrofit() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("")//基础URL 建议以 / 结尾
                .addConverterFactory(GsonConverterFactory.create())//设置 Json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//RxJava 适配器
                .build();
        RxWeatherService rxjavaService = retrofit.create(RxWeatherService.class);
        rxjavaService.getMessage("北京").subscribeOn(Schedulers.io())//IO线程加载数据
                .observeOn(AndroidSchedulers.mainThread())//主线程显示数据
                .subscribe(new Subscriber<RadioEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RadioEntity radioEntity) {
                        //拿到数据后的处理
                    }
                });
    }
}
