package com.sz.huanxue.androidapp.ui.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.Gson;
import com.hsae.h5.business.userprofile.UserInfo;
import com.hsae.h5.business.userprofile.UserProfileProxy;
import com.sz.huanxue.androidapp.R;
import com.sz.huanxue.androidapp.data.remote.RetrofitManager;
import com.sz.huanxue.androidapp.data.remote.api.IServer;
import com.sz.huanxue.androidapp.data.remote.bean.OpenRecord;
import com.sz.huanxue.androidapp.data.remote.bean.QrBean;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author huanxue
 * Created by Administrator on 2019/7/16.
 */
public class SecondActivity extends AppCompatActivity implements OnClickListener {


    private Button mBtnStyle1;
    private Button mBtnStyle2;
    private Button mBtnStyle3;
    private Button mBtnStyle4;
    private Button mBtnStyle5;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("logcat", "SecondActivity------onCreate");
        setContentView(R.layout.activity_second);
        initView();


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("logcat", "SecondActivity------onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("logcat", "SecondActivity------onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("logcat", "SecondActivity------onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("logcat", "SecondActivity------onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("logcat", "SecondActivity------onResume");
    }

    private void initView() {
        mBtnStyle1 = (Button) findViewById(R.id.btn_style1);
        mBtnStyle2 = (Button) findViewById(R.id.btn_style2);
        mBtnStyle3 = (Button) findViewById(R.id.btn_style3);
        mBtnStyle4 = (Button) findViewById(R.id.btn_style4);
        mBtnStyle5 = (Button) findViewById(R.id.btn_style5);

        mBtnStyle1.setOnClickListener(this);
        mBtnStyle2.setOnClickListener(this);
        mBtnStyle3.setOnClickListener(this);
        mBtnStyle4.setOnClickListener(this);
        mBtnStyle5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_style1:
                Log.i("logcat", "SecondActivity-----style111111");
//                SkinCompatManager.getInstance().restoreDefaultTheme();
//                SoundPoolUtils.getInstance(this).playSound(1, 3);
                getRecord();
                break;
            case R.id.btn_style2:
                Log.i("logcat", "SecondActivity-----style2222222");
//                SkinCompatManager.getInstance().loadSkin("two", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
//                SkinCompatUserThemeManager.get().addColorState(R.color.colorPrimary, "#38F803");
//                SoundPoolUtils.getInstance(this).playSound(2, 1);
                getQR();
                break;
            case R.id.btn_style3:
                getRecordByRetrofit();
                break;
            case R.id.btn_style4:
                break;
            case R.id.btn_style5:
                break;
        }
    }

    /**
     * 获取对账服务开通状态
     */
    public void getRecord() {
        final UserInfo userInfo = UserProfileProxy.getInstance().getCurrentUserInfo();
        if (userInfo != null && "2".equals(userInfo.getUserId())) {
            Log.d("huanxue", "---getRecord---isUserLogon  userInfo is empty or 访客");
            return;
        }


        //请求服务器的地址
        String url = "https://fawivi-gw-public-uat.faw.cn:63443/cp-openingrecord/cpBill/billRecord";
        OkHttpClient okHttpClient = new OkHttpClient();
        final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        String text = monthFormat.format(new Date());
        Log.d("huanxue", "  getRecord  text:" + text);
        String jsonStr = "{\n" +
                "\"oauthProviderId\": \"kuwo\",\n" +
                "\"statusCode\": \"0\",\n" +
                "\"statusComment\": \"成功\",\n" +
                "\"openTime\": \"" + text + "\"\n" +
                "}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
   /*     HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);*/
        RequestBody body = RequestBody.create(jsonStr, JSON);
        Request request = new Request.Builder().url(url)
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .post(body).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsondata = response.body().string();
                    Log.d("huanxue", "--getRecord--onResponse:" + jsondata);
                    Gson gson = new Gson();
                    OpenRecord record = gson.fromJson(jsondata, OpenRecord.class);
                    if ("0".equals(record.getStatusCode())) {
                        //请求对账服务成功
                    }

                }
            }
        });

    }

    private void getQR() {
        RetrofitManager.self()
                .getService(IServer.class)
                .reqQrUrl("LFPH6BCP3L2L00031", "11")
                .compose(apply())
                .subscribe(new Observer<QrBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull QrBean qrBean) {
                        Log.i("GOOD_DA", "onNext-->");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("GOOD_DA", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * rxJava网络请求公共操作符，并回调到主线程
     */
    protected <T> ObservableTransformer<T, T> apply() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> compositeDisposable.add(disposable));

    }

    public void getRecordByRetrofit(){
        final UserInfo userInfo = UserProfileProxy.getInstance().getCurrentUserInfo();
        final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        String text = monthFormat.format(new Date());
        RetrofitManager.self().getService(IServer.class)
                .getRecord("Bearer "+userInfo.getAccessToken(),"kuwo","0","成功",text)
                .compose(apply())
                .subscribe(new Observer<OpenRecord>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        
                    }

                    @Override
                    public void onNext(@NonNull OpenRecord openRecord) {
                        Log.i("huanxue", "getRecord  onNext   success");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        
                    }


                });
    }
}
