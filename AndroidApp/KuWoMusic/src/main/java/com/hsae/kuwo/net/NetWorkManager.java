package com.hsae.kuwo.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hsae.kuwo.utils.KuWoMemoryData;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author huanxue
 * Created by HSAE_DCY on 2020.7.9.
 */
public class NetWorkManager {
    private static final NetWorkManager sInstances = new NetWorkManager();
    /**
     * WiFi连接状态
     */
    private final AtomicBoolean wifiStatus = new AtomicBoolean(false);
    /**
     * 数据流量开关状态
     */
    private final AtomicBoolean lteStatus = new AtomicBoolean(false);
    /**
     * ping网络后的状态
     */
    private final AtomicBoolean pingStatus = new AtomicBoolean(false);
    /**
     * Tbox连接状态
     */
    private final AtomicBoolean tboxStatus = new AtomicBoolean(false);

    NetWorkManager() {

    }

    public static NetWorkManager getInstance() {
        return sInstances;
    }

    /**
     * 网络是否可用
     *
     * @return false 网络不可用 true 网络可用
     */
    public boolean isNetworkConnected() {
        Log.d("NetWorkManager", "  tboxStatus:" + tboxStatus.get() + "  pingStatus:" + pingStatus.get());
        if (tboxStatus.get()) {
            return pingStatus.get();
        }
        return getSystemNetwork();
    }

    /**
     * tbox未连接时时，判断当前网络是否可用
     *
     * @return false 网络不可用 true 网络可用
     */
    private boolean getSystemNetwork() {
        Context context = KuWoMemoryData.getInstance().getContext();
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }


    /**
     * tbox连接后，当wifi或lte状态发生变化时，更新网络是否可用
     */
/*    private void updateTboxNetwork(final boolean wifiSwitch, final boolean lteSwitch) {
        Log.d("NetWorkManager", " updateTboxNetwork  wifiSwitch:" + wifiSwitch + "  lteSwitch:" + lteSwitch);
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                //tbox连接时
                if (tboxStatus.get()) {
                    //wifi与lte状态未发生变化
                    if (wifiStatus.get() == wifiSwitch && lteStatus.get() == lteSwitch) {
                        return;
                    }
                    //wifi状态变化
                    if (wifiStatus.get() != wifiSwitch) {
                        wifiStatus.set(wifiSwitch);
                        if (wifiSwitch) {
                            pingStatus.set(getPingNetwork());
                            return;
                        }
                    }
                    //lte状态变化
                    if (lteStatus.get() != lteSwitch) {
                        lteStatus.set(lteSwitch);
                    }
                    if (lteSwitch) {
                        pingStatus.set(getPingNetwork());
                        return;
                    }
                }
                pingStatus.set(false);
            }
        });
    }*/


}
