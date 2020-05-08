package com.sz.huanxue.androidapp.net;

import com.sz.huanxue.androidapp.ui.entity.RadioEntity;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.3.23.
 */
public interface RxWeatherService {
    @GET("weather_mini")
    Observable<RadioEntity> getMessage(@Query("city") String city);

}
