package com.autolink.radio55.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;

import com.autolink.radio.model.action.ViewActionDefine;
import com.autolink.radio.model.view.IRadioViewProxy;
import com.autolink.radio.model.view.RadioViewProxy;
import com.autolink.radio55.adapter.RadioEntity;
import com.autolink.radio55.datas.RadioDataBases;
import com.autolink.radio55.datas.RadioDataBases.RADIO_DATA_TYPE;
import com.autolink.radio55.utils.ELog;
import com.autolink.radio55.utils.RadioDataUtils;
import com.autolink.radio55.utils.SortDatasUtil;
import com.autolink.radio55.view.RadioMainView;
import com.autolink.radio55.widget.ObjViewAvn;
import com.autolink.serial.mcu.manager.applogic.ApplogicManager;
import com.autolink.serial.mcu.manager.radio.RadioManager;
import com.autolink.thread.MyHandlerThread.Callback;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * 基础功能实现类
 *
 * @author Administrator
 */
public class AppDataUtils implements IRadioViewProxy {
    public static final int ADAPTER_AM = 1;
    public static final int ADAPTER_FM = 2;
    public static final int ADAPTER_COLL_AM = 3;
    public static final int ADAPTER_COLL_FM = 4;
    public static final String ACTION_UPDATE_LANGUAGE = "android.update.language";
    public static final String ACTION_CARUI_SWITCH_BAND = "com.autolink.switch.band";
    /**
     * 将列表显示状态存储于SP文件中，11表示收藏，22表示全部
     */
    public static final int sp_iscoll = 11;
    public static final int sp_isall = 22;
    public static final String ON_RADIO_ACTION = "com.radio.widget.on.radio";
    public static final String ON_RADIO_BOOLEAN = "ON_RADIO_BOOLEAN";
    private static final int MAX_NUMBER_COLL = 50;//收藏最大电台数目
    private static final int UPDATE_AM_LIST = 5482;
    private static final int UPDATE_FM_LIST = 5483;
    private static final int MIN_CLICK_DELAY_TIME = 500;//最快可点击时间间隔
    private static final String AM_STATUS = "am_status";
    private static final String FM_STATUS = "fm_status";
    private static final String LIST_STATUS = "LIST_STATUS";
    private static final int MIN_REQUEST_DELAY_TIME = 200;//最快可请求刷新时间间隔
    private static WeakReference<AppDataUtils> instances;
    public boolean isShowingAvn = false;
    public boolean isReplaceColl = false;//判断是否触发替换电台
    RadioEntity entityForColl;//收藏操作前记忆的电台entity
    SharedPreferences sp;
    private boolean isFirstStart = false;// 断B+后第一次启动
    private boolean cycleForColl_AM = false;// am模式下是否在收藏列表
    private boolean cycleForColl_FM = false;// fm模式下是否在收藏列表
    private int frequency = -1, amFrequency = 531, fmFrequency = 8750;// 频点值，FM是8750-10800，AM是531-1602
    private int fmOrAm_band = -1;// 波段值，0表示FM，1表示AM
    private int ScanType = 0;// 当前状态值，0表示没有任何状态，1表示向下搜索，2表示向上搜索，3表示单步调频，4表示浏览，5表示搜索电台列表。
    private int playIndex = 0;// 角标
    private LinkedList<RadioEntity> radioFmEntities, radioAmEntities;//储存MCU+DB电台数据
    private LinkedList<RadioEntity> radioAmCollEntities, radioFmCollEntities;// 储存数据库已收藏状态数据
    private LinkedList<RadioEntity> radioFmCollEntitiesOrderById;// 用于储存查询已收藏电台个数
    private LinkedList<RadioEntity> radioAmCollEntitiesOrderById;
    private Map<String, RadioEntity> radioAmMap, radioFmMap;//不同的集合存放MCU+DB电台数据
    private Map<String, RadioEntity> radioAmCollMap, radioFmCollMap;
    private CallBackAppData mCallBackAppData;
    private RadioThreadForDB mRadioThreadForDB;//子线程跟随application存在
    private boolean isCreateMVC = false;
    private Context mContext;
    private ApplogicManager mApplogicManager;// 中间件对象
    private Callback mCallback = new Callback() {

        @Override
        public boolean handleMessage(Message arg0) {
            // TODO Auto-generated method stub

            return false;
        }
    };
    private Map<Integer, Integer> callBackPrestoreListFmMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> callBackPrestoreListAmMap = new HashMap<Integer, Integer>();//内存值储存MCU电台
    private Handler threadHandler2;//更新数据库
    private WeakReference<Handler> mHandlers;
    private long lastClickTime;
    private long lastAMRequestTime = 0;
    private long lastFMRequestTime = 0;
    private Handler threadHandler1 = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Map<String, RadioEntity> mapsCollAB = new HashMap<String, RadioEntity>();
            switch (msg.what) {
                case UPDATE_AM_LIST:
                    if (!requestNotifyDataChanged(RadioManager.VALUE_BAND_AM)) {
                        break;
                    }
                    if (getScanType() != 0) {
                        break;
                    }


                    Map<Integer, Integer> mapsA = new HashMap<Integer, Integer>();
                    mapsCollAB.clear();
                    mapsCollAB.putAll(radioAmCollMap);

                    if (callBackPrestoreListAmMap.size() > 0) {//MCU电台数据被转换
                        mapsA.putAll(callBackPrestoreListAmMap);
                    }

                    if (mapsCollAB.size() > 0) {
                        boolean doMap = false;
                        if (mapsA.size() == 0) {
                            mapsA.clear();
                            doMap = true;
                        }
                        for (String key : mapsCollAB.keySet()) {//数据库数据被转换
                            if (!mapsA.containsValue(Integer.parseInt(key))) {
                                if (doMap) {
                                    mapsA.put(mapsA.size(), Integer.parseInt(key));
                                } else {
                                    mapsA.put(mapsA.size() + 1, Integer.parseInt(key));
                                }
                            }
                        }
                    }


                    boolean hasSize = mapsA.size() > 0;
                    int index = 0;
                    if (hasSize) {
                        radioAmMap.clear();
                        radioAmEntities.clear();


                        for (Integer key : mapsA.keySet()) {
                            RadioEntity entity = new RadioEntity();
                            entity.setId(key);
                            entity.setIndex(index);
                            entity.setType(RadioDataUtils.AM);
                            entity.setFrequency(mapsA.get(key) + "");
                            entity.setFrequencyType(RadioDataUtils.getMKHZ(RadioManager.VALUE_BAND_AM));
                            index = index + 1;
                            radioAmEntities.add(entity);
                        }

                        if (radioAmEntities.size() > 0) {
                            SortDatasUtil.sotrDatas(radioAmEntities);//已经是全数据，排序
                            for (int i = 0; i < radioAmEntities.size(); i++) {
                                radioAmMap.put(radioAmEntities.get(i).getFrequency(), radioAmEntities.get(i));
                            }
                        }


                    }

                    break;

                case UPDATE_FM_LIST:
                    if (!requestNotifyDataChanged(RadioManager.VALUE_BAND_FM)) {
                        break;
                    }
                    if (getScanType() != 0) {
                        break;
                    }

                    Map<Integer, Integer> mapsF = new HashMap<Integer, Integer>();
                    mapsCollAB.clear();
                    mapsCollAB.putAll(radioFmCollMap);

                    if (callBackPrestoreListFmMap.size() > 0) {
                        mapsF.putAll(callBackPrestoreListFmMap);
                    }

                    if (mapsCollAB.size() > 0) {
                        boolean doMap = false;
                        if (mapsF.size() == 0) {
                            mapsF.clear();
                            doMap = true;
                        }
                        for (String key : mapsCollAB.keySet()) {
                            if (!mapsF.containsValue(Integer.parseInt(key))) {
                                if (doMap) {
                                    mapsF.put(mapsF.size(), Integer.parseInt(key));
                                } else {
                                    mapsF.put(mapsF.size() + 1, Integer.parseInt(key));
                                }

                            }
                        }
                    }


                    boolean hasSize2 = mapsF.size() > 0;
                    int index2 = 0;

                    if (hasSize2) {
                        radioFmMap.clear();
                        radioFmEntities.clear();

                        for (Integer key : mapsF.keySet()) {
                            RadioEntity entity = new RadioEntity();
                            entity.setId(key);
                            entity.setIndex(index2);
                            entity.setType(RadioDataUtils.FM);
                            entity.setFrequency(mapsF.get(key) + "");
                            entity.setFrequencyType(RadioDataUtils.getMKHZ(RadioManager.VALUE_BAND_FM));
                            radioFmEntities.add(entity);
                            index2 = index2 + 1;
                        }

                        if (radioFmEntities.size() > 0) {
                            SortDatasUtil.sotrDatas(radioFmEntities);
                            for (int i = 0; i < radioFmEntities.size(); i++) {
                                radioFmMap.put(radioFmEntities.get(i).getFrequency(), radioFmEntities.get(i));
                            }
                        }


                    }


                    break;
            }
            return false;
        }
    });

    private AppDataUtils() {

        mRadioThreadForDB = new RadioThreadForDB("AppDataUtils_2");
        mRadioThreadForDB.start();

    }

    public static AppDataUtils getInstance() {
        if (instances == null || instances.get() == null) {
            AppDataUtils instance = new AppDataUtils();
            instances = new WeakReference<AppDataUtils>(instance);
        }
        return instances.get();
    }

    /**
     * 绑定MVC，提供给外部调用
     *
     * @param context
     */
    public void setRegisterMvc(Context context, Application app) {
        // TODO Auto-generated method stub

        setMvcAppDataUtils(context, app);

    }

    private ApplogicManager getApplogicManager(Context context) {
        if (mApplogicManager == null) {
            mApplogicManager = new ApplogicManager(context, mCallback);
            mApplogicManager.start();
        }

        return mApplogicManager;
    }

    private void setMvcAppDataUtils(Context context, Application app) {
        this.mContext = context.getApplicationContext();


        if (isCreateMVC) {
            return;
        }

        ELog.i("setMvcAppDataUtils");
        RadioDataBases.getInstance(this.mContext);
        getApplogicManager(this.mContext);
        if (radioFmEntities == null) {
            radioFmEntities = new LinkedList<RadioEntity>();
        }

        if (radioAmEntities == null) {
            radioAmEntities = new LinkedList<RadioEntity>();
        }

        if (radioFmCollMap == null) {
            radioFmCollMap = new HashMap<String, RadioEntity>();
        }

        if (radioAmCollMap == null) {
            radioAmCollMap = new HashMap<String, RadioEntity>();
        }

        if (radioFmMap == null) {
            radioFmMap = new HashMap<String, RadioEntity>();
        }

        if (radioAmMap == null) {
            radioAmMap = new HashMap<String, RadioEntity>();
        }

        isCreateMVC = RadioViewProxy.getInstance().createRadioProxy(app);

        try {
            RadioViewProxy.getInstance().registerProxyView((IRadioViewProxy) this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 动态注册，动态解除
     */
    public void unRegisterAppDataUtils() {

        if (mCallBackAppData != null) {
            this.mCallBackAppData = null;
        }


    }

    /**
     * 设置是否在收藏列表
     *
     * @param isOnColl
     */
    public void setIsOnColl(boolean isOnColl, int band) {
        if (band == RadioManager.VALUE_BAND_AM) {
            cycleForColl_AM = isOnColl;
        } else if (band == RadioManager.VALUE_BAND_FM) {
            cycleForColl_FM = isOnColl;
        }

    }

    /**
     * 得到是否在收藏的boolean值
     *
     * @return
     */
    public boolean getIsOnColl() {
        if (fmOrAm_band == RadioManager.VALUE_BAND_AM) {
            return cycleForColl_AM;
        } else {
            return cycleForColl_FM;
        }
    }

    /**
     * 设置向下步进一个单位
     */
    public void setSingleStepNext() {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }

        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SINGLE_STEP_NEXT);

    }

    /**
     * 设置向上步进一个单位
     */
    public void setSingleStepPre() {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }
        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SINGLE_STEP_PREV);

    }

    /**
     * 设置向下搜索
     */
    public void setScreachPrev() {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }
        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SREACH_PREV);
    }

    /**
     * 设置向上搜索
     */
    public void setScreachNext() {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }
        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SCREACH_NEXT);
    }

    public void setMainFreqForAmFmList(int frep, int band) {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }
        if (ScanType != 0) {
            return;
        }
        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SET_MAIN_PREQ, frep, band);

    }

    public void setMainPreq(int arg0) {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }
        if (ScanType != 0) {
            return;
        }
        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SET_MAIN_PREQ, arg0, fmOrAm_band);
    }

    /**
     * 设置波段
     *
     * @param band
     */
    public void setBandByType(int band) {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }
        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SET_BAND_BY_TYPE, band);
    }

    public void setBand() {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }
        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SET_BAND);
    }

    /**
     * 设置全局搜索
     */
    public synchronized void setScandFMOrAM() {
        if (RadioViewProxy.getInstance() == null) {
            return;
        }


        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_AUTO_STORE_FREQ);
    }

    /**
     * 设置停止搜索
     *
     * @return
     */
    public synchronized boolean stopScandFmOrAm() {
        ELog.i("stopScandFmOrAm:" + getScanType());
        if (ScanType != 0 && ScanType > 0) {

            RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SET_MAIN_PREQ, getMainFreq(), getBand());
            ScanType = 0;
            return true;
        }
        return false;
    }

    /**
     * 低内存注销MVC
     */
    public void onTerminate() {


        if (RadioViewProxy.getInstance() != null) {
            try {
                RadioViewProxy.getInstance().unRegisterProxyView(mContext);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    /**
     * 回调
     */
    @Override
    public void callBackGetMainFreq(int freq) {// 频点更新回调
        ELog.i("callBackGetMainFreq:" + freq);
        if (freq < 531) {
            return;
        } else if (freq > 1602 && freq < 8750) {
            return;
        } else if (freq > 10800) {
            return;
        }
        frequency = freq;

        if (mCallBackAppData != null) {
            mCallBackAppData.callBackGetMainFreqFromApp(freq);
        }
        if (isShowingAvn) {
            sendBroadForWidget();
        }


    }

    @Override
    public void callBackGetBand(int band) {// 波段更新回调
        fmOrAm_band = band;
        if (mCallBackAppData != null) {
            mCallBackAppData.callBackGetBandFromApp(band);
        }

    }

    @Override
    public void callBackFmPrestoreList(Map<Integer, Integer> fmMap) {// MCU
        // FM电台更新回调
        callBackPrestoreListFmMap.clear();
        if (fmMap != null) {
            callBackPrestoreListFmMap.putAll(fmMap);
        }


    }

    @Override
    public void callBackAmPrestoreList(Map<Integer, Integer> amMap) {// MCU
        // AM电台更新回调
        callBackPrestoreListAmMap.clear();
        if (amMap != null) {
            callBackPrestoreListAmMap.putAll(amMap);
        }


    }

    @Override
    public void callBackScanType(int type) {// 状态更新回调
        if (type == 0 && ScanType == 5) {//搜索停止
            if (getBand() == 0) {
                threadHandler1.sendMessageDelayed(threadHandler1.obtainMessage(UPDATE_FM_LIST, callBackPrestoreListFmMap), 10);
            } else if (getBand() == 1) {
                threadHandler1.sendMessageDelayed(threadHandler1.obtainMessage(UPDATE_AM_LIST, callBackPrestoreListAmMap), 10);
            }
            callbackList();
        }
        ELog.i("callBackScanType:" + type);
        ScanType = type;


        if (mCallBackAppData != null) {
            mCallBackAppData.callBackScanTypeFromApp(type);
        }


    }

    @Override
    public void callRadioPauseState(boolean arg0) {// 收音状态回调
        // TODO Auto-generated method stub
    }

    @Override
    public void callBackPercentage(int process) {// 搜索进度更新回调
        //        ELog.i("callBackPercentage:"+process);
        if (mCallBackAppData != null) {
            mCallBackAppData.callBackPercentageFromApp(process);
        }

    }

    @Override
    public void callBackBindService(boolean isOk) {// 服务绑定更新回调
        if (mCallBackAppData != null) {
            mCallBackAppData.callBackBindServiceFromApp(isOk);
        }

    }

    @Override
    public void callBackClearFm() {// 搜索开始时清除MCU电台FM内存值回调

        callBackPrestoreListFmMap.clear();
        if (radioFmEntities != null) {
            radioFmEntities.clear();
        }
        if (radioFmMap != null) {
            radioFmMap.clear();
        }
        callbackList();
    }


    @Override
    public void callBackClearAm() {// 搜索开始时清除MCU电台AM内存值回调

        callBackPrestoreListAmMap.clear();
        if (radioAmEntities != null) {
            radioAmEntities.clear();
        }
        if (radioAmMap != null) {
            radioAmMap.clear();
        }
        callbackList();
    }

    @Override
    public void callBackMainFreqState(int i) {
    }

    public void callbackList() {//通知主线程更新列表
        if (mCallBackAppData != null) {
            mCallBackAppData.callBackCollPrestoreListFromApp(getBand());
        }
    }

    /**
     * 是否在收音(需要具体定义是否在收音机该接口)
     *
     * @param isOnRadio
     */
    public void isOnRadioView(boolean isOnRadio) {

        if (mCallBackAppData != null) {
            mCallBackAppData.callBackIsOnRadioStatus(isOnRadio);
        }

        if (mContext != null) {

            Intent intent = new Intent(ON_RADIO_ACTION);
            intent.putExtra(ON_RADIO_BOOLEAN, isOnRadio);
            mContext.sendBroadcast(intent);
        }
    }

    public void setRadioViewProxy(CallBackAppData mCallBackAppData) {
        this.mCallBackAppData = mCallBackAppData;
    }

    public void UpdateAmCollList() {//更新数据库中最新已收藏数据
        if (radioAmCollEntities != null) {
            radioAmCollEntities.clear();
        }
        if (radioAmCollEntitiesOrderById != null) {
            radioAmCollEntitiesOrderById.clear();
        }

        if (radioAmCollMap != null) {
            radioAmCollMap.clear();
        }

        LinkedList<RadioEntity> amcoll_db = new LinkedList<RadioEntity>();
        // 修改查询方式，根据收藏标识
        radioAmCollEntities = RadioDataBases.getInstance(mContext).queryAllCollByNum(RadioDataUtils.AM_DB, RadioDataUtils.ASC);
        radioAmCollEntitiesOrderById = RadioDataBases.getInstance(mContext).queryAllCollByNum(RadioDataUtils.AM_DB, RadioDataUtils.ASC);
        amcoll_db = RadioDataBases.getInstance(mContext).queryAllColl(RadioDataUtils.AM_DB, RadioDataUtils.ASC);
        SortDatasUtil.sotrDatas(radioAmCollEntities);

        if (amcoll_db != null) {
            for (int i = 0; i < amcoll_db.size(); i++) {
                amcoll_db.get(i).setIndex(i);
                radioAmCollMap.put(amcoll_db.get(i).getFrequency(), amcoll_db.get(i));
            }
        }

        //通知全部电台列表更新
        threadHandler1.removeMessages(UPDATE_AM_LIST);
        threadHandler1.sendMessageDelayed(threadHandler1.obtainMessage(UPDATE_AM_LIST, callBackPrestoreListAmMap), 50);

    }


    public void UpdateFmCollList() {
        if (radioFmCollEntities != null) {
            radioFmCollEntities.clear();
        }
        if (radioFmCollEntitiesOrderById != null) {
            radioFmCollEntitiesOrderById.clear();
        }
        if (radioFmCollMap != null) {
            radioFmCollMap.clear();
        }

        LinkedList<RadioEntity> fmcoll_db = new LinkedList<RadioEntity>();
        radioFmCollEntities = RadioDataBases.getInstance(mContext).queryAllCollByNum(RadioDataUtils.FM_DB, RadioDataUtils.ASC);
        radioFmCollEntitiesOrderById = RadioDataBases.getInstance(mContext).queryAllCollByNum(RadioDataUtils.FM_DB, RadioDataUtils.ASC);
        fmcoll_db = RadioDataBases.getInstance(mContext).queryAllColl(RadioDataUtils.FM_DB, RadioDataUtils.ASC);
        SortDatasUtil.sotrDatas(radioFmCollEntities);

        for (int i = 0; i < fmcoll_db.size(); i++) {
            fmcoll_db.get(i).setIndex(i);
            radioFmCollMap.put(fmcoll_db.get(i).getFrequency(), fmcoll_db.get(i));
        }
        threadHandler1.removeMessages(UPDATE_FM_LIST);
        threadHandler1.sendMessageDelayed(threadHandler1.obtainMessage(UPDATE_FM_LIST, callBackPrestoreListFmMap), 50);
    }

    /**
     * 根据band来查询不同波段的MCU+DB数据(全部电台)
     *
     * @param
     * @return
     */
    public LinkedList<RadioEntity> getAllRadioData(int band) {
        if (band == RadioManager.VALUE_BAND_AM) {// 查询MCU传过来的AM列表
            return radioAmEntities;
        } else if (band == RadioManager.VALUE_BAND_FM) {// 查询MCU传过来的FM列表
            return radioFmEntities;
        }

        return new LinkedList<RadioEntity>();
    }

    /**
     * 查询收藏电台列表
     *
     * @return
     */
    public LinkedList<RadioEntity> getCollRadiosData() {

        if (getBand() == RadioManager.VALUE_BAND_FM) {
            return radioFmCollEntities == null ? new LinkedList<RadioEntity>() : radioFmCollEntities;
        } else if (getBand() == RadioManager.VALUE_BAND_AM) {
            return radioAmCollEntities == null ? new LinkedList<RadioEntity>() : radioAmCollEntities;
        }
        return new LinkedList<RadioEntity>();
    }

    /**
     * 根据是否在收藏查询显示列表
     *
     * @param isOnCollList 是否在收藏
     * @return
     */
    private LinkedList<RadioEntity> queryFmOrAmOrCollRadios(boolean isOnCollList) {
        if (!isOnCollList) {
            if (fmOrAm_band == RadioManager.VALUE_BAND_AM) {
                return radioAmEntities == null ? new LinkedList<RadioEntity>() : radioAmEntities;
            } else if (fmOrAm_band == RadioManager.VALUE_BAND_FM) {
                return radioFmEntities == null ? new LinkedList<RadioEntity>() : radioFmEntities;
            }
        } else {
            return getCollRadiosData();
        }
        return new LinkedList<RadioEntity>();
    }

    /**
     * 查询集合中是否存在当前频点对象
     *
     * @param lIType
     * @return
     */
    public RadioEntity getFocusRadioEntity(RADIO_LIST_TYPE lIType) {
        switch (lIType) {

            case LIST_AM_FM:
                if (fmOrAm_band == RadioManager.VALUE_BAND_AM) {
                    if (radioAmMap != null && radioAmMap.containsKey(frequency + "")) {
                        return radioAmMap.get(frequency + "");
                    }
                } else if (fmOrAm_band == RadioManager.VALUE_BAND_FM) {
                    if (radioFmMap != null && radioFmMap.containsKey(frequency + "")) {
                        return radioFmMap.get(frequency + "");
                    }
                }
                break;
            case COLL_LIST:
                if (fmOrAm_band == RadioManager.VALUE_BAND_AM) {
                    if (radioAmCollMap != null && radioAmCollMap.containsKey(frequency + "")) {
                        return radioAmCollMap.get(frequency + "");
                    }
                } else if (fmOrAm_band == RadioManager.VALUE_BAND_FM) {
                    if (radioFmCollMap != null && radioFmCollMap.containsKey(frequency + "")) {
                        return radioFmCollMap.get(frequency + "");
                    }
                }
                break;

        }

        return new RadioEntity();
    }

    /**
     * 根据波段清除数据库非收藏数据
     */
    public void deleteDB() {

        // 删除数据后更新
        if (getBand() == 0) {
            RadioDataBases.getInstance(mContext).deleteAllByIsColl(RadioDataUtils.FM_DB);

        } else if (getBand() == 1) {
            RadioDataBases.getInstance(mContext).deleteAllByIsColl(RadioDataUtils.AM_DB);

        }
    }

    /**
     * 断B+后清除数据库数据
     */
    public void updateFirstStart() {
        if (mApplogicManager.getFirstStartOnOff() == 1) {
            delectAllDB();
        } else {
            isFirstStart = false;
        }
    }

    /**
     * 清除全部数据库数据
     */
    private void delectAllDB() {
        isFirstStart = true;
        RadioDataBases.getInstance(mContext).deleteAll();

    }

    /**
     * 查询数据库已收藏状态数据
     *
     * @return
     */
    public LinkedList<RadioEntity> queryAmCollRadiosFromData() {

        return radioAmCollEntities == null ? new LinkedList<RadioEntity>() : radioAmCollEntities;
    }

    public LinkedList<RadioEntity> queryFmCollRadiosFromData() {

        return radioFmCollEntities == null ? new LinkedList<RadioEntity>() : radioFmCollEntities;
    }

    /**
     * 查询数据库中已收藏状态电台个数，用于限制已收藏电台列表
     *
     * @return
     */
    public LinkedList<RadioEntity> getRdioAMFMCollEntitisById() {
        if (fmOrAm_band == RadioManager.VALUE_BAND_AM) {
            return radioAmCollEntitiesOrderById == null ? new LinkedList<RadioEntity>() : radioAmCollEntitiesOrderById;
        } else if (fmOrAm_band == RadioManager.VALUE_BAND_FM) {
            return radioFmCollEntitiesOrderById == null ? new LinkedList<RadioEntity>() : radioFmCollEntitiesOrderById;
        }
        return new LinkedList<RadioEntity>();

    }

    /**
     * @return 主线程RadioMainView的handler
     */

    @Override
    public Handler getHandler() {
        if (mHandlers != null && mHandlers.get() != null) {
            return mHandlers.get();
        }
        return null;
    }

    /**
     * 获得频点值
     *
     * @return
     */
    public int getMainFreq() {
        return frequency;
    }

    /**
     * 获得波段值
     *
     * @return
     */
    public int getBand() {
        return fmOrAm_band;
    }

    /**
     * 获得状态值
     *
     * @return
     */
    public int getScanType() {
        return ScanType;
    }

    /**
     * 根据频点查询数据库
     *
     * @param key
     * @return
     */
    public boolean getCollData(String key) {
        if (RadioDataBases.getInstance(mContext).showRadioColl(key)) {
            return true;
        }
        return false;
    }

    /**
     * 获得一个可以给主线程发数据的Handler(发送回调消息给到收音机)
     *
     * @param mHamHandler
     */
    public void setUiHandle(Handler mHamHandler) {
        mHandlers = new WeakReference<Handler>(mHamHandler);
    }

    /**
     * 设置上一个电台,0代表向上，1表示向下
     */
    public void setPreCollect() {
        onclickPreOrNext(0);
    }

    /**
     * 设置下一个电台,0代表向上，1表示向下
     */
    public void setNextCollect() {
        onclickPreOrNext(1);
    }

    private void onclickPreOrNext(int num) {
        RadioEntity mSelectorEntity;// 切换选中电台
        LinkedList<RadioEntity> mLinkList;
        mLinkList = AppDataUtils.getInstance().queryFmOrAmOrCollRadios(AppDataUtils.getInstance().getIsOnColl());

        int size = mLinkList.size();
        // 得到列表的大小
        if (size <= 0) {
            RadioMainView.getInstance().showMyToast();
            return;
        }
        if (size == 1) {
            String string = mLinkList.get(0).getFrequency();
            int freqs = Integer.valueOf(string);
            if (frequency == freqs) {
                RadioMainView.getInstance().showMyToast();
                return;
            }
        }

        onclickForGetIndex(mLinkList, size, num);

        mSelectorEntity = mLinkList.get(playIndex);
        if (mSelectorEntity != null) {
            int str = Integer.parseInt(mSelectorEntity.getFrequency());
            int band = RadioDataUtils.getFmAmTypeByStr(mSelectorEntity.getType());
            AppDataUtils.getInstance().setMainFreqForAmFmList(str, band);

        }
    }

    /**
     * 得到角标值
     *
     * @param size
     * @return
     */
    private int onclickForGetIndex(LinkedList<RadioEntity> mLinkList, int size, int num) {

        String freq = frequency + "";
        mLinkList = AppDataUtils.getInstance().queryFmOrAmOrCollRadios(AppDataUtils.getInstance().getIsOnColl());

        if (mLinkList != null && size > 0) {
            // 判断角标值
            for (int i = 0; i <= size - 1; i++) {

                if (mLinkList.get(i) != null && mLinkList.get(i).getFrequency() != null && mLinkList.get(i).getFrequency().equals(freq)) {
                    playIndex = i;
                    return doForIndex(size, num, playIndex);
                } else {
                    playIndex = 0;
                }
            }

        } else {
            playIndex = 0;
        }

        return playIndex;
    }

    /**
     * 计算最终角标值
     *
     * @return 0表示减法，1表示加法
     */
    private int doForIndex(int size, int num, int index) {
        if (num == 0) {
            playIndex = index <= 0 ? size - 1 : index - 1;
        } else if (num == 1) {
            playIndex = index >= size - 1 ? 0 : index + 1;
        }
        return playIndex;

    }

    public void setSwitchLoc() {

        RadioViewProxy.getInstance().notifyMessage(ViewActionDefine.TO_MODEL_SET_SWITCHLOC);
    }

    public void sendBroadForWidget() {
        Intent intent = new Intent();
        intent.setAction(ObjViewAvn.ACTION_AVN);
        intent.putExtra("freq", getMainFreq());
        intent.putExtra("band", getBand());
        this.mContext.sendBroadcast(intent);
    }

    /***
     * 防止快速点击
     * @return
     */
    public boolean isNormalClick() {
        boolean flag = true;
        long curClickTime = SystemClock.elapsedRealtime();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
            lastClickTime = curClickTime;
        } else {
            flag = false;
        }

        return flag;
    }

    private void collect(String key) {

        Message mSMessageForColl;
        //先存储该entityForColl是为了记忆需要添加进收藏的电台
        entityForColl = new RadioEntity();
        entityForColl.setType(RadioDataUtils.getFmAmStr(AppDataUtils.getInstance().getBand()));
        entityForColl.setFrequency(key);
        entityForColl.setIsColl(RadioDataUtils.ISCOLL_TRUE);
        entityForColl.setFrequencyType(RadioDataUtils.getMKHZ(AppDataUtils.getInstance().getBand()));
        mSMessageForColl = threadHandler2.obtainMessage();
        mSMessageForColl.what = RadioThreadForDB.DATA_FOR_INSERT;
        mSMessageForColl.obj = entityForColl;

        if (AppDataUtils.getInstance().getRdioAMFMCollEntitisById().size() >= MAX_NUMBER_COLL) {// 显示弹框
            getHandler().sendEmptyMessageDelayed(RadioMainView.MSG_SHOW_MYDIALOG, 30);

            return;
        }
        if (RadioDataBases.getInstance(mContext).getRadioData(key)) {
            //保证添加操作只执行一次
            return;
        }
        threadHandler2.sendMessageDelayed(mSMessageForColl, 30);

    }

    public void collection(String key) {
        threadHandler2.sendMessage(threadHandler2.obtainMessage(RadioThreadForDB.DATA_FOR_ONCLICK_COLL, key));
    }

    public void collectCallBackCollFreq(String freq) {
        threadHandler2.sendMessage(threadHandler2.obtainMessage(RadioThreadForDB.DATA_FOR_ONCLICK_COLL, freq));
    }

    public void collectCallBackOnclickItem(int freq, int band) {
        if (freq != AppDataUtils.getInstance().getMainFreq()) {
            threadHandler2.sendMessageDelayed(threadHandler2.obtainMessage(RadioThreadForDB.DATA_LIST_MAINFREP, freq, band), 10);

        }
    }

    public void collectCallBackDeleteFreq(String freq) {
        Message message = threadHandler2.obtainMessage();
        message.what = RadioThreadForDB.DATA_FOR_UPDATE;
        message.obj = freq;
        threadHandler2.sendMessage(message);
    }

    public void collectReplaceFreq(RadioEntity mEntiry) {
        threadHandler2.sendMessageDelayed(threadHandler2.obtainMessage(RadioThreadForDB.DATA_FOR_UPDATE, mEntiry.getFrequency()), 30);
        threadHandler2.sendMessageDelayed(threadHandler2.obtainMessage(RadioThreadForDB.DATA_FOR_ONCLICK_COLL, entityForColl.getFrequency()), 100);
    }

    public void writeSP(int num) {
        sp = mContext.getSharedPreferences(LIST_STATUS, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (getBand() == RadioManager.VALUE_BAND_AM) {
            editor.putInt(AM_STATUS, num);
        } else if (getBand() == RadioManager.VALUE_BAND_FM) {
            editor.putInt(FM_STATUS, num);
        }
        editor.apply();
    }

    private int getListStatusSP(String string) {
        sp = mContext.getSharedPreferences(LIST_STATUS, mContext.MODE_PRIVATE);

        return sp.getInt(string, 0);
    }

    public void readSP() {//此时无法获取系统band，需要读取SP文件

        readSP(AM_STATUS, RadioManager.VALUE_BAND_AM);
        readSP(FM_STATUS, RadioManager.VALUE_BAND_FM);

    }

    private void readSP(String string, int band) {//如何判断该赋值给谁
        int lst_status = getListStatusSP(string);
        if (lst_status == 11) {
            setIsOnColl(true, band);
        } else {
            setIsOnColl(false, band);
        }
    }

    private boolean requestNotifyDataChanged(int band) {
        long time = SystemClock.elapsedRealtime();
        boolean isRequest = false;
        if (band == RadioManager.VALUE_BAND_AM) {
            if (time - lastAMRequestTime >= MIN_REQUEST_DELAY_TIME) {
                lastAMRequestTime = time;
                isRequest = true;
            } else {
                ELog.w("requestNotifyDataChanged:--AM--time  is  not  enouth");
                isRequest = false;
            }
        } else if (band == RadioManager.VALUE_BAND_FM) {
            if (time - lastFMRequestTime >= MIN_REQUEST_DELAY_TIME) {
                lastFMRequestTime = time;
                isRequest = true;
            } else {
                ELog.w("requestNotifyDataChanged:--FM--time  is  not  enouth");
                isRequest = false;
            }
        }

        return isRequest;

    }


    public enum RADIO_LIST_TYPE {
        COLL_LIST, LIST_AM_FM
    }

    private class RadioThreadForDB extends HandlerThread {
        private static final int SET_AMFM_MAIN_FREP = 0X0000014;
        private static final int DATA_FOR_DELETE = 0X000020;
        private static final int DATA_LIST_MAINFREP = 0X0000010;
        private static final int DATA_FOR_INSERT = 0X000008;
        private static final int DATA_FOR_UPDATE = 0X000024;
        private static final int DATA_FOR_ONCLICK_COLL = 0X000030;

        public RadioThreadForDB(String name) {
            super(name);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            threadHandler2 = new Handler(getLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case DATA_LIST_MAINFREP:
                            setMainFreqForAmFmList(msg.arg1, msg.arg2);
                            break;

                        case 1111:
                            RadioMainView.getInstance().mCircleSeekBar.setProgress((Integer) msg.obj);
                            break;
                        case DATA_FOR_INSERT:
                            if (msg.obj != null) {
                                boolean isok = RadioDataBases.getInstance(mContext).insertRadioData((RadioEntity) msg.obj, RADIO_DATA_TYPE.COLL_AM_FM);
                            }
                            break;
                        case DATA_FOR_DELETE:
                            if (msg.obj != null) {
                                boolean isok = RadioDataBases.getInstance(mContext).deleteByRadioEntity((RadioEntity) msg.obj, RADIO_DATA_TYPE.COLL_AM_FM);
                            }
                            break;
                        case DATA_FOR_UPDATE:
                            String str = (String) msg.obj;
                            RadioDataBases.getInstance(mContext).updateRadioData(str, RADIO_DATA_TYPE.COLL_AM_FM, RadioDataUtils.ISCOLL_FALSE);
                            break;
                        case DATA_FOR_ONCLICK_COLL:

                            String freq = (String) msg.obj;
                            if (RadioDataBases.getInstance(mContext).getRadioData(freq)) {// 数据库中存在该频点
                                if (RadioDataBases.getInstance(mContext).showRadioColl(freq)) {// 显示
                                    RadioDataBases.getInstance(mContext).updateRadioData(freq, RADIO_DATA_TYPE.COLL_AM_FM, RadioDataUtils.ISCOLL_FALSE);
                                } else {// 不显示
                                    if (AppDataUtils.getInstance().getRdioAMFMCollEntitisById().size() >= MAX_NUMBER_COLL) {// 显示弹框
                                        collect(freq);
                                    } else {
                                        RadioDataBases.getInstance(mContext).updateRadioData(freq, RADIO_DATA_TYPE.COLL_AM_FM, RadioDataUtils.ISCOLL_TRUE);
                                    }
                                }
                            } else {// 不存在
                                collect(freq);
                            }
                            break;
                    }
                }

            };
        }
    }

}
