package com.autolink.radio55.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.view.ArcRadioSeekBar;
import com.android.view.ScaleBarViewRH5;
import com.android.view.ScaleBarViewRH5.CurrentChangeListener;
import com.autolink.radio55.R;
import com.autolink.radio55.adapter.CallBackAll;
import com.autolink.radio55.adapter.CallBackColl;
import com.autolink.radio55.adapter.RadioAllAdapter;
import com.autolink.radio55.adapter.RadioCollAdapter;
import com.autolink.radio55.adapter.RadioEntity;
import com.autolink.radio55.app.AppDataUtils;
import com.autolink.radio55.app.CallBackAppData;
import com.autolink.radio55.utils.ELog;
import com.autolink.radio55.utils.RadioDataUtils;
import com.autolink.radio55.widget.ObjViewAvn;
import com.autolink.serial.mcu.manager.radio.RadioManager;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * mainview视图控制类
 *
 * @author Administrator
 */
public class RadioMainView extends FrameLayout implements OnClickListener, CallBackAppData, CallBackAll, CallBackColl {

    public static final int MSG_SHOW_MYDIALOG = 0X000019;
    public static final int SET_MAINFREQ = 650245;
    private static final int ANIM_DURATION = 400;//动画时间
    private static final int REMOVE_WINDOWS = 0X000001;
    private static final int RADIO_UPDATA_PINDIAN_INFO = 0X000002;
    private static final int SET_COLL_LIST_ICON = 0X000004;
    private static final int UPDATE_PREDATE_PROGRESS = 0X000007;
    private static final int ON_CLICK_AM = 0X000011;
    private static final int ON_CLICK_FM = 0X000012;
    private static final int MSG_DELECT_DB = 0X000015;
    private static final int MSG_SHOW_LIST = 0X000016;
    private static final int MSG_SET_PRE_RADIO = 0X000021;
    private static final int MSG_SET_NEXT_RADIO = 0X000022;
    private static final int SCAND_AGAIN = 83172;
    private static WeakReference<RadioMainView> instance;
    public ArcRadioSeekBar mCircleSeekBar;
    /**
     * fragment是否处于活动状态
     */
    protected boolean isOnResume = false;
    View mainView, am_fm_list_layout, mDialogView;
    RadioAllAdapter adapterAM;
    RadioAllAdapter adapterFM;
    RadioCollAdapter adapterFmColl;
    RadioCollAdapter adapterAmColl;
    TranslateAnimation mShowAction, mHiddenAction;
    Toast mToast;
    WindowManager mWindowManager;
    Button radio_break_onclick_id;
    boolean windowOnclick = false;
    View windowView;
    private Activity mActivity;
    private Context mContext;
    private ScaleBarViewRH5 mScaleBarViewRH5;
    private ListView radio_coll_fm_listview, radio_coll_am_listview, radio_fm_listview, radio_am_listview;
    private Button radio_scand_again, FM_switch_btn, AM_switch_btn;
    private TextView radio_scan_tital_show, radio_show_progres_pete_text, radio_main_freq_show_tv, radio_band_tv;
    private Button radio_all_radio_list, radio_coll_list, am_fm_list_layout_main_but;
    private ImageButton switch_radio_pre, radio_coll_tv_bt_id, switch_radio_next, switch_radio_play_model;
    private ImageButton radio_brack_list, list_radio_show;
    private ImageView search_btn;
    private View radio_main_ui_scan, radio_fmam_list_img_vi, radio_coll_list_img_vi;
    private TextView mainFreq_tv;
    private int processT = 0;
    private Dialog mDialog;
    /**
     * 是否可以展示扫描页面
     */
    private boolean isShowScand = false;
    /**
     * handler判断是否继续执行
     */
    private boolean hasStopScand = true;
    private BroadcasetRadio broadcasetRadio;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_PRE_RADIO:
                    AppDataUtils.getInstance().setPreCollect();
                    break;

                case MSG_SET_NEXT_RADIO:
                    AppDataUtils.getInstance().setNextCollect();
                    break;
                case MSG_DELECT_DB:
                    AppDataUtils.getInstance().deleteDB();
                    break;
                case MSG_SHOW_MYDIALOG:
                    showDialog();
                    break;
                case MSG_SHOW_LIST:
                    onClick(list_radio_show);
                    onClick(radio_coll_list);
                    break;
                case ON_CLICK_AM:
                    onclickAM();
                    break;
                case ON_CLICK_FM:
                    onclickFM();
                    break;
                case SCAND_AGAIN:

                    if (isShowScand) {
                        onClick(search_btn);
                    }
                    break;
                case SET_MAINFREQ:
                    AppDataUtils.getInstance().setMainPreq(msg.arg2);
                    break;
                case REMOVE_WINDOWS:
                    if (mWindowManager != null && windowView != null) {
                        windowView.setVisibility(GONE);
                        mWindowManager.removeView(windowView);
                        windowView = null;
                    }
                    setVisibilityByView(radio_main_ui_scan, View.GONE);
                    break;
                case UPDATE_PREDATE_PROGRESS:


                    if (hasStopScand) {//不在搜索时打断
                        break;
                    }

                    if (AppDataUtils.getInstance().getBand() == RadioManager.VALUE_BAND_AM) {
                        if ((AppDataUtils.getInstance().getAllRadioData(RadioManager.VALUE_BAND_AM).size() < 1)) {// 没有搜索到电台
                            radio_scan_tital_show.setText(R.string.radio_loading_end_on_info);
                            if (radio_show_progres_pete_text != null) {
                                radio_show_progres_pete_text.setText(R.string.radio_loading_end_on_info_try_again);
                            }
                            setVisibilityByView(radio_main_ui_scan, View.VISIBLE);
                            setVisibilityByView(radio_scand_again, View.VISIBLE);
                            if (radio_scand_again != null) {
                                radio_scand_again.setEnabled(true);
                            }
                            setVisibilityByView(mCircleSeekBar, View.GONE);
                            setVisibilityByView(radio_show_progres_pete_text, View.GONE);
                        } else {// 搜索到
                            AppDataUtils.getInstance().callbackList();
                            mHandler.sendEmptyMessage(REMOVE_WINDOWS);
                            onClick(list_radio_show);
                            onClick(radio_all_radio_list);
                        }
                    } else if (AppDataUtils.getInstance().getBand() == RadioManager.VALUE_BAND_FM) {
                        if ((AppDataUtils.getInstance().getAllRadioData(RadioManager.VALUE_BAND_FM).size() < 1)) {
                            if (radio_scan_tital_show != null) {
                                radio_scan_tital_show.setText(R.string.radio_loading_end_on_info);
                            }
                            if (radio_show_progres_pete_text != null) {
                                radio_show_progres_pete_text.setText(R.string.radio_loading_end_on_info_try_again);
                            }
                            setVisibilityByView(radio_main_ui_scan, View.VISIBLE);
                            setVisibilityByView(radio_scand_again, View.VISIBLE);
                            if (radio_scand_again != null) {
                                radio_scand_again.setEnabled(true);
                            }

                            setVisibilityByView(mCircleSeekBar, View.GONE);
                            setVisibilityByView(radio_show_progres_pete_text, View.GONE);
                        } else {
                            AppDataUtils.getInstance().callbackList();
                            mHandler.sendEmptyMessage(REMOVE_WINDOWS);
                            onClick(list_radio_show);
                            onClick(radio_all_radio_list);
                        }
                    }
                    break;
                case RADIO_UPDATA_PINDIAN_INFO:
                    setCollIcon();
                    setSwitchButtonIcon();
                    radio_main_freq_show_tv.setText(RadioDataUtils.getFregp(AppDataUtils.getInstance().getMainFreq(), AppDataUtils.getInstance().getBand()));
                    break;
                case SET_COLL_LIST_ICON:
                    setCollListIcon();
                    break;

            }
            return false;
        }
    });


    public RadioMainView(Context context, Activity activity) {
        super(context, null);
        this.mContext = context;
        this.mActivity = activity;

        mDialog = new Dialog(mActivity, R.style.dialog_style);
    }

    /**
     * 生成RadioMainView
     */
    public static RadioMainView getInstance(Context context, Activity activityContext) {
        if (instance == null || instance.get() == null || instance.get().mActivity == null || instance.get().mActivity != activityContext || instance.get().mContext == null || instance.get().mContext != context) {
            RadioMainView radio = new RadioMainView(context, activityContext);
            instance = new WeakReference<RadioMainView>(radio);
        }
        return instance.get();
    }

    /**
     * 提供给外部使用
     *
     * @return
     */
    public static RadioMainView getInstance() {
        return instance.get();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id != R.id.search_btn) {// 控制再次搜索
            if (id != R.id.radio_coll_list && id != R.id.radio_all_radio_list && id != R.id.list_radio_show && id != R.id.radio_brack_list && am_fm_list_layout != null && radio_brack_list != null && am_fm_list_layout.getVisibility() == View.VISIBLE) {

                if (id == R.id.radio_scand_again) {
                    mHandler.sendEmptyMessage(SCAND_AGAIN);
                } else {
                    onClick(radio_brack_list);
                }
                return;
            }
        }

        switch (id) {
            case R.id.FM_switch_btn:
                if (AppDataUtils.getInstance().isNormalClick()) {
                    mHandler.removeMessages(ON_CLICK_FM);
                    mHandler.removeMessages(ON_CLICK_AM);
                    mHandler.sendEmptyMessageDelayed(ON_CLICK_FM, 50);
                }
                break;
            case R.id.AM_switch_btn:
                if (AppDataUtils.getInstance().isNormalClick()) {
                    mHandler.removeMessages(ON_CLICK_FM);
                    mHandler.removeMessages(ON_CLICK_AM);
                    mHandler.sendEmptyMessageDelayed(ON_CLICK_AM, 50);
                }
                break;
            case R.id.switch_radio_pre:
                if (AppDataUtils.getInstance().isNormalClick()) {
                    if (AppDataUtils.getInstance().getScanType() != 0) {
                        AppDataUtils.getInstance().stopScandFmOrAm();
                        return;
                    }
                    mHandler.removeMessages(MSG_SET_PRE_RADIO);
                    mHandler.sendEmptyMessageDelayed(MSG_SET_PRE_RADIO, 50);
                }
                break;
            case R.id.switch_radio_next:
                if (AppDataUtils.getInstance().isNormalClick()) {
                    if (AppDataUtils.getInstance().getScanType() != 0) {
                        AppDataUtils.getInstance().stopScandFmOrAm();
                        return;
                    }
                    mHandler.removeMessages(MSG_SET_NEXT_RADIO);
                    mHandler.sendEmptyMessageDelayed(MSG_SET_NEXT_RADIO, 50);
                }
                break;
            case R.id.switch_radio_play_model://mainview上控制列表展示icon
                if (!AppDataUtils.getInstance().getIsOnColl()) {
                    onClick(radio_coll_list);
                } else {
                    onClick(radio_all_radio_list);
                }
                mHandler.removeMessages(SET_COLL_LIST_ICON);
                mHandler.sendEmptyMessageDelayed(SET_COLL_LIST_ICON, 20);
                break;
            case R.id.radio_scand_again:
                mHandler.sendEmptyMessage(SCAND_AGAIN);
                break;
            case R.id.search_btn:
                if (AppDataUtils.getInstance().isNormalClick()) {
                    if (AppDataUtils.getInstance().getScanType() != 0) {
                        AppDataUtils.getInstance().stopScandFmOrAm();
                        return;
                    }
                    setVisibilityByView(mCircleSeekBar, View.VISIBLE);
                    setVisibilityByView(radio_scand_again, View.INVISIBLE);
                    setVisibilityByView(radio_main_ui_scan, View.VISIBLE);
                    setVisibilityByView(radio_show_progres_pete_text, View.VISIBLE);
                    if (AppDataUtils.getInstance().getScanType() < 1) {
                        AppDataUtils.getInstance().setScandFMOrAM();
                        createWindows();
                        mHandler.sendEmptyMessage(MSG_DELECT_DB);
                    }
                }
                break;
            case R.id.list_radio_show://打开列表Icon
                am_fm_list_layout_main_but.setVisibility(View.VISIBLE);

                if (am_fm_list_layout.getVisibility() != View.VISIBLE) {
                    am_fm_list_layout.startAnimation(mShowAction);
                    am_fm_list_layout.setVisibility(View.VISIBLE);
                }
                if (AppDataUtils.getInstance().getIsOnColl()) {
                    onClick(radio_coll_list);
                    if (AppDataUtils.getInstance().getBand() == RadioManager.VALUE_BAND_FM) {
                        adapterFmColl.setSelectItem(RadioManager.VALUE_BAND_FM);
                    } else {
                        adapterAmColl.setSelectItem(RadioManager.VALUE_BAND_AM);
                    }

                } else {
                    onClick(radio_all_radio_list);
                    if (AppDataUtils.getInstance().getBand() == RadioManager.VALUE_BAND_FM) {
                        adapterFM.setSelectItem(RadioManager.VALUE_BAND_FM);
                    } else {
                        adapterAM.setSelectItem(RadioManager.VALUE_BAND_AM);
                    }
                }
                break;
            case R.id.radio_brack_list:
                radio_all_radio_list.setEnabled(true);
                AppDataUtils.getInstance().isReplaceColl = false;
                if (am_fm_list_layout.getVisibility() != View.GONE) {
                    am_fm_list_layout.startAnimation(mHiddenAction);
                    am_fm_list_layout.setVisibility(View.GONE);
                    am_fm_list_layout_main_but.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_coll_tv_bt_id:
                String key = AppDataUtils.getInstance().getMainFreq() + "";
                AppDataUtils.getInstance().collection(key);

                break;
            case R.id.radio_coll_list:
                AppDataUtils.getInstance().setIsOnColl(true, AppDataUtils.getInstance().getBand());
                setVisibilityByView(radio_coll_list_img_vi, View.VISIBLE);
                setVisibilityByView(radio_fmam_list_img_vi, View.INVISIBLE);
                radio_coll_list.setSelected(true);
                radio_all_radio_list.setSelected(false);
                if (AppDataUtils.getInstance().getBand() == RadioManager.VALUE_BAND_AM) {
                    setVisibilityByView(radio_coll_am_listview, View.VISIBLE);
                    setVisibilityByView(radio_coll_fm_listview, View.GONE);
                } else if (AppDataUtils.getInstance().getBand() == RadioManager.VALUE_BAND_FM) {
                    setVisibilityByView(radio_coll_fm_listview, View.VISIBLE);
                    setVisibilityByView(radio_coll_am_listview, View.GONE);
                }
                setVisibilityByView(radio_am_listview, View.GONE);
                setVisibilityByView(radio_fm_listview, View.GONE);
                mHandler.removeMessages(SET_COLL_LIST_ICON);
                mHandler.sendEmptyMessageDelayed(SET_COLL_LIST_ICON, 20);
                break;
            case R.id.radio_all_radio_list:

                radio_coll_list.setSelected(false);
                radio_all_radio_list.setSelected(true);
                AppDataUtils.getInstance().setIsOnColl(false, AppDataUtils.getInstance().getBand());
                setVisibilityByView(radio_coll_list_img_vi, View.INVISIBLE);
                setVisibilityByView(radio_fmam_list_img_vi, View.VISIBLE);
                setVisibilityByView(radio_coll_fm_listview, View.GONE);
                setVisibilityByView(radio_coll_am_listview, View.GONE);
                if (AppDataUtils.getInstance().getBand() == RadioManager.VALUE_BAND_FM) {

                    setVisibilityByView(radio_am_listview, View.GONE);
                    setVisibilityByView(radio_fm_listview, View.VISIBLE);
                } else if (AppDataUtils.getInstance().getBand() == RadioManager.VALUE_BAND_AM) {

                    setVisibilityByView(radio_am_listview, View.VISIBLE);
                    setVisibilityByView(radio_fm_listview, View.GONE);
                } else {
                    setVisibilityByView(radio_am_listview, View.GONE);
                    setVisibilityByView(radio_fm_listview, View.GONE);
                }
                mHandler.removeMessages(SET_COLL_LIST_ICON);
                mHandler.sendEmptyMessageDelayed(SET_COLL_LIST_ICON, 20);
                break;
            case R.id.am_fm_list_layout_main_but:
                onClick(radio_brack_list);
                break;
            default:
                ELog.w("onClick:---unknow--id:" + id);
        }


    }

    private Activity getActivity() {
        return mActivity;
    }

    public View onCreateView() {
        AppDataUtils.getInstance().setRadioViewProxy(this);
        AppDataUtils.getInstance().setUiHandle(mHandler);

        mainView = View.inflate(mContext, R.layout.radio_view_mainview, null);
        AppDataUtils.getInstance().readSP();//ACC记忆功能
        initView(mainView);
        return mainView;
    }

    public void onActivityCreate() {//进行UI交互操作
        if (AppDataUtils.getInstance().getMainFreq() >= RadioDataUtils.MIN_AM_FREQ) {//最小频点数531
            radio_main_freq_show_tv.setText(RadioDataUtils.getFregp(AppDataUtils.getInstance().getMainFreq(), AppDataUtils.getInstance().getBand()));
        }

        if (AppDataUtils.getInstance().getMainFreq() >= RadioDataUtils.MIN_FM_FREQ) {// FM
            mScaleBarViewRH5.initViewParam(AppDataUtils.getInstance().getMainFreq(), ScaleBarViewRH5.MOD_TYPE_FM);
            radio_band_tv.setText(RadioDataUtils.FM_MHZ);
        } else if (AppDataUtils.getInstance().getMainFreq() >= RadioDataUtils.MIN_AM_FREQ) {// AM
            mScaleBarViewRH5.initViewParam(AppDataUtils.getInstance().getMainFreq(), ScaleBarViewRH5.MOD_TYPE_AM);
            radio_band_tv.setText(RadioDataUtils.AM_KHZ);
        }

        mScaleBarViewRH5.setCurrentChangeListener(new CurrentChangeListener() {

            @Override
            public void callbackCurrentChange(float value) {
                // TODO Auto-generated method stub
                int num = (int) value;
                radio_main_freq_show_tv.setText(RadioDataUtils.getFregp(num, AppDataUtils.getInstance().getBand()));
                mHandler.removeMessages(SET_MAINFREQ);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(SET_MAINFREQ, 0, num), 200);
            }
        });

        setCollListIcon();
        setSwitchButtonIcon();
        setCollIcon();
    }

    public void onStart() {
        mShowAction = new TranslateAnimation(0, 600f, 0, 0, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(ANIM_DURATION);
        mHiddenAction = new TranslateAnimation(0, Animation.RELATIVE_TO_SELF, 0, 600f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(ANIM_DURATION);
    }

    public void onResume() {
        isOnResume = true;
        isShowScand = true;

    }

    public void onPause() {
        isShowScand = false;
        isOnResume = false;
        hasStopScand = true;

        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mToast != null) {
            mToast.cancel();
        }
        if (mWindowManager != null && windowView != null) {//直接调用，不使用handler.handler处理比主线程慢，会出现错误
            windowView.setVisibility(GONE);
            mWindowManager.removeView(windowView);
            windowView = null;
        }
        setVisibilityByView(radio_main_ui_scan, View.GONE);
        AppDataUtils.getInstance().stopScandFmOrAm();
    }

    public void onStop() {
        if (am_fm_list_layout.getVisibility() == View.VISIBLE) {
            onClick(radio_brack_list);
            am_fm_list_layout.clearAnimation();
        }

    }

    public void onDestroyView() {
        clearData();
        if (mHandler != null) {
            mHandler = null;
        }
    }

    private void initView(View view) {

        mScaleBarViewRH5 = (ScaleBarViewRH5) view.findViewById(R.id.scalebar_main_view_id);
        am_fm_list_layout_main_but = (Button) view.findViewById(R.id.am_fm_list_layout_main_but);
        radio_fmam_list_img_vi = view.findViewById(R.id.radio_fmam_list_img_vi);
        radio_coll_list_img_vi = view.findViewById(R.id.radio_coll_list_img_vi);
        FM_switch_btn = (Button) view.findViewById(R.id.FM_switch_btn);
        AM_switch_btn = (Button) view.findViewById(R.id.AM_switch_btn);
        radio_main_freq_show_tv = (TextView) view.findViewById(R.id.radio_main_freq_show_tv);
        radio_band_tv = (TextView) view.findViewById(R.id.radio_band);
        switch_radio_pre = (ImageButton) view.findViewById(R.id.switch_radio_pre);
        radio_coll_tv_bt_id = (ImageButton) view.findViewById(R.id.radio_coll_tv_bt_id);
        switch_radio_play_model = (ImageButton) view.findViewById(R.id.switch_radio_play_model);
        switch_radio_next = (ImageButton) view.findViewById(R.id.switch_radio_next);
        radio_brack_list = (ImageButton) view.findViewById(R.id.radio_brack_list);
        radio_all_radio_list = (Button) view.findViewById(R.id.radio_all_radio_list);
        radio_coll_list = (Button) view.findViewById(R.id.radio_coll_list);
        search_btn = (ImageView) view.findViewById(R.id.search_btn);
        list_radio_show = (ImageButton) view.findViewById(R.id.list_radio_show);
        radio_coll_fm_listview = (ListView) view.findViewById(R.id.radio_coll_fmam_listview);
        radio_coll_am_listview = (ListView) view.findViewById(R.id.radio_coll_am_listview);
        radio_am_listview = (ListView) view.findViewById(R.id.radio_am_listview);
        radio_fm_listview = (ListView) view.findViewById(R.id.radio_fm_listview);
        am_fm_list_layout = view.findViewById(R.id.am_fm_list_layout);

        switch_radio_play_model.setOnClickListener(this);
        radio_brack_list.setOnClickListener(this);
        radio_coll_tv_bt_id.setOnClickListener(this);
        radio_coll_list.setOnClickListener(this);
        am_fm_list_layout_main_but.setOnClickListener(this);
        radio_all_radio_list.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        FM_switch_btn.setOnClickListener(this);
        AM_switch_btn.setOnClickListener(this);
        switch_radio_pre.setOnClickListener(this);
        switch_radio_next.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        list_radio_show.setOnClickListener(this);

        adapterFmColl = new RadioCollAdapter(mContext, RadioManager.VALUE_BAND_FM);
        adapterFmColl.registerRadioCollAdapterCallBack(this);
        radio_coll_fm_listview.setAdapter(adapterFmColl);
        adapterAmColl = new RadioCollAdapter(mContext, RadioManager.VALUE_BAND_AM);
        adapterAmColl.registerRadioCollAdapterCallBack(this);
        radio_coll_am_listview.setAdapter(adapterAmColl);
        adapterAM = new RadioAllAdapter(mContext, RadioManager.VALUE_BAND_AM);
        adapterAM.regedistCallBack(this);
        radio_am_listview.setAdapter(adapterAM);
        adapterFM = new RadioAllAdapter(mContext, RadioManager.VALUE_BAND_FM);
        adapterFM.regedistCallBack(this);
        radio_fm_listview.setAdapter(adapterFM);

        broadcasetRadio = new BroadcasetRadio();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppDataUtils.ACTION_CARUI_SWITCH_BAND);
        intentFilter.addAction(ObjViewAvn.ACTION_LONG_NEXT);
        intentFilter.addAction(ObjViewAvn.ACTION_LONG_PRE);
        this.mContext.registerReceiver(broadcasetRadio, intentFilter);


    }

    public void createWindows() {
        hasStopScand = false;
        processT = 0;
        if (mWindowManager != null && windowView != null) {//再次搜索时判断

            return;
        }
        WindowManager.LayoutParams smallWindowParams = new WindowManager.LayoutParams();
        smallWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        smallWindowParams.format = PixelFormat.RGBA_8888;
        smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        smallWindowParams.gravity = Gravity.CENTER;
        smallWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        smallWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;// 190
        smallWindowParams.x = 0;
        smallWindowParams.y = 0;

        windowView = View.inflate(mContext, R.layout.radio_view_scand, null);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(windowView, smallWindowParams);
        windowOnclick = true;
        mCircleSeekBar = (ArcRadioSeekBar) windowView.findViewById(R.id.radio_cir_id);

        radio_main_ui_scan = windowView.findViewById(R.id.radio_main_ui_scan);
        radio_scand_again = (Button) windowView.findViewById(R.id.radio_scand_again);
        radio_scand_again.setOnClickListener(this);
        radio_show_progres_pete_text = (TextView) windowView.findViewById(R.id.radio_show_progres_pete_text);
        radio_scan_tital_show = (TextView) windowView.findViewById(R.id.radio_scan_tital_show);
        radio_break_onclick_id = (Button) windowView.findViewById(R.id.radio_break_onclick_id);
        radio_scan_tital_show.setText(R.string.radio_loading_on);


        radio_break_onclick_id.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (windowOnclick) {
                    hasStopScand = true;
                    setVisibilityByView(radio_break_onclick_id, View.INVISIBLE);
                    mHandler.sendEmptyMessage(REMOVE_WINDOWS);
                    mHandler.removeMessages(UPDATE_PREDATE_PROGRESS);//移除后续操作
                    AppDataUtils.getInstance().stopScandFmOrAm();
                    windowOnclick = false;
                }
            }
        });
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public void CallBackCollFreq(String freq) {
        AppDataUtils.getInstance().collectCallBackCollFreq(freq);
    }

    @Override
    public void CallBackSelectionIndex(int position, int adapter) {//adapter用来区分要选中哪一个列表电台
        switch (adapter) {
            case AppDataUtils.ADAPTER_AM:
                radio_am_listview.setAdapter(adapterAM);
                radio_am_listview.setSelection(position);
                break;
            case AppDataUtils.ADAPTER_FM:
                radio_fm_listview.setAdapter(adapterFM);
                radio_fm_listview.setSelection(position);
                break;
            case AppDataUtils.ADAPTER_COLL_AM:
                radio_coll_am_listview.setAdapter(adapterAmColl);
                radio_coll_am_listview.setSelection(position);
                break;
            case AppDataUtils.ADAPTER_COLL_FM:
                radio_coll_fm_listview.setAdapter(adapterFmColl);
                radio_coll_fm_listview.setSelection(position);
                break;
        }

    }

    @Override
    public void CallBackOnclickItem(int freq, int band) {
        AppDataUtils.getInstance().collectCallBackOnclickItem(freq, band);
    }

    @Override
    public void CallBackDeleteFreq(String freq) {//从收藏列表中点击删除按钮，实际是更新数据库标识
        //通知数据库处理
        AppDataUtils.getInstance().collectCallBackDeleteFreq(freq);

    }

    @Override
    public void CallBackReplaceColl(RadioEntity radioEntity) {//
        if (!radio_all_radio_list.isEnabled()) {
            if (mDialog != null && !mDialog.isShowing() && radioEntity != null) {
                mDialog.show();
                mainFreq_tv.setText(R.string.radio_if_coll_before);
                mainFreq_tv.setGravity(Gravity.CENTER);
                mainFreq_tv.setTag(radioEntity);
            }
        }
    }

    @Override
    public void callBackGetMainFreqFromApp(int freq) {

        if (freq >= 8750) {// FM
            mScaleBarViewRH5.initViewParam(freq, ScaleBarViewRH5.MOD_TYPE_FM);

        } else {// AM
            mScaleBarViewRH5.initViewParam(freq, ScaleBarViewRH5.MOD_TYPE_AM);

        }
        if (AppDataUtils.getInstance().getScanType() == 0) {
            AppDataUtils.getInstance().callbackList();
        }
        mHandler.sendMessage(mHandler.obtainMessage(RADIO_UPDATA_PINDIAN_INFO));
    }

    @Override
    public void callBackGetBandFromApp(int band) {

        mHandler.sendEmptyMessage(REMOVE_WINDOWS);
        setCollListIcon();

        if (band == RadioManager.VALUE_BAND_AM) {
            radio_band_tv.setText(RadioDataUtils.AM_KHZ);
        } else if (band == RadioManager.VALUE_BAND_FM) {
            radio_band_tv.setText(RadioDataUtils.FM_MHZ);
        }
    }

    @Override
    public void callBackIsOnRadioStatus(boolean isOnRadio) {
    }

    @Override
    public void callBackCollPrestoreListFromApp(final int band) {//不能保证该回调一定会在主线程，需强制切换回主线程
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (band == RadioManager.VALUE_BAND_AM) {
                    adapterAM.getData(RadioManager.VALUE_BAND_AM);
                    adapterAmColl.getData(RadioManager.VALUE_BAND_AM);
                } else if (band == RadioManager.VALUE_BAND_FM) {
                    adapterFM.getData(RadioManager.VALUE_BAND_FM);
                    adapterFmColl.getData(RadioManager.VALUE_BAND_FM);
                }
                setCollIcon();
            }
        });

    }

    @Override
    public void callBackScanTypeFromApp(int type) {
        if (type == 5) {
            mHandler.removeMessages(SET_MAINFREQ);
            radio_show_progres_pete_text.setText("0%");
            radio_scand_again.setEnabled(false);
            radio_scan_tital_show.setText(R.string.radio_loading_on);
        }

        if (type != 0) {
            onClick(radio_brack_list);
        }
    }

    @Override
    public void callBackPercentageFromApp(int process) {
        if (mCircleSeekBar != null) {
            if (processT > process || process > 100) {
                return;
            }
            processT = process;

            radio_show_progres_pete_text.setText(processT + "%");
            mCircleSeekBar.setProgress(processT);

        }
        mHandler.removeMessages(UPDATE_PREDATE_PROGRESS);
        mHandler.sendEmptyMessageDelayed(UPDATE_PREDATE_PROGRESS, 1000);

    }

    @Override
    public void callBackBindServiceFromApp(boolean isOk) {

    }

    /**
     * 设置收藏图标的显示状态
     */
    private void setCollIcon() {
        if (radio_coll_tv_bt_id != null) {
            String key = AppDataUtils.getInstance().getMainFreq() + "";
            if (AppDataUtils.getInstance().getCollData(key)) {
                radio_coll_tv_bt_id.setImageResource(R.drawable.btn_coll_p);
            } else {
                radio_coll_tv_bt_id.setImageResource(R.drawable.btn_coll_n);
            }
        }
    }

    /**
     * 设置左侧收藏列表图标显示状态
     */
    private void setCollListIcon() {
        boolean isOnCollList = AppDataUtils.getInstance().getIsOnColl();
        if (isOnCollList) {
            AppDataUtils.getInstance().writeSP(AppDataUtils.sp_iscoll);
            switch_radio_play_model.setImageResource(R.drawable.btn_icon_list_coll_selector);
        } else {
            AppDataUtils.getInstance().writeSP(AppDataUtils.sp_isall);
            switch_radio_play_model.setImageResource(R.drawable.btn_icon_list_all_selector);
        }
    }

    /**
     * 弹框
     */
    private void showDialog() {
        Button delete_bnt, cancel_bnt;
        if (mDialog == null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mDialogView = inflater.inflate(R.layout.radio_dialog_collect, null);
        mainFreq_tv = (TextView) mDialogView.findViewById(R.id.title);
        mainFreq_tv.setText(R.string.dialog_content1);
        delete_bnt = (Button) mDialogView.findViewById(R.id.yes);
        delete_bnt.setText(R.string.dialog_yes_btn);
        cancel_bnt = (Button) mDialogView.findViewById(R.id.no);
        cancel_bnt.setText(R.string.dialog_no_btn);
        mDialog.setCancelable(true);
        mDialog.setContentView(mDialogView);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = 481;
        lp.height = 401;
        dialogWindow.setAttributes(lp);
        delete_bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!radio_all_radio_list.isEnabled()) {// 第二次弹框删除
                    //取消选中电台的收藏标识并且加入新的电台
                    AppDataUtils.getInstance().collectReplaceFreq((RadioEntity) mainFreq_tv.getTag());
                    radio_all_radio_list.setEnabled(true);
                    AppDataUtils.getInstance().isReplaceColl = false;

                } else {// 可以点击的,第一次弹框
                    radio_all_radio_list.setEnabled(false);
                    AppDataUtils.getInstance().isReplaceColl = true;
                    mHandler.sendEmptyMessageDelayed(MSG_SHOW_LIST, 20);
                }
                mDialog.dismiss();
            }
        });
        cancel_bnt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                radio_all_radio_list.setEnabled(true);
                AppDataUtils.getInstance().isReplaceColl = false;
                mDialog.dismiss();
            }
        });

        if (!(getActivity()).isFinishing()) {
            // show dialog
            mDialog.show();
        }
    }

    /**
     * 自定义吐司的显示
     *
     * @param
     */
    public void showMyToast() {
        mToast = new Toast(mActivity);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View toast_view = inflater.inflate(R.layout.radio_view_toast, null);
        mToast.setView(toast_view);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        showMyToast2(mToast, 2000);
        mToast.show();
    }

    /**
     * toast的时间设置
     *
     * @param toast
     * @param cnt
     */
    public void showMyToast2(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

    /**
     * 设置FMAM按键图标
     */
    private void setSwitchButtonIcon() {
        int freqs = AppDataUtils.getInstance().getMainFreq();
        if (FM_switch_btn != null && AM_switch_btn != null) {
            if (freqs >= RadioDataUtils.MIN_FM_FREQ) {
                FM_switch_btn.setSelected(true);
                AM_switch_btn.setSelected(false);
            } else {
                FM_switch_btn.setSelected(false);
                AM_switch_btn.setSelected(true);
            }
        }
    }

    /**
     * 动态注册，动态解除
     */
    public void clearData() {
        if (broadcasetRadio != null) {
            this.mContext.unregisterReceiver(broadcasetRadio);
            broadcasetRadio = null;
        }
        AppDataUtils.getInstance().unRegisterAppDataUtils();

    }

    private void onclickFM() {
        AppDataUtils.getInstance().setBandByType(RadioManager.VALUE_BAND_FM);
        if (am_fm_list_layout.getVisibility() == View.VISIBLE) {
            radio_am_listview.setVisibility(View.GONE);
            radio_fm_listview.setVisibility(View.VISIBLE);
        }

    }

    private void onclickAM() {
        AppDataUtils.getInstance().setBandByType(RadioManager.VALUE_BAND_AM);
        if (am_fm_list_layout.getVisibility() == View.VISIBLE) {
            radio_am_listview.setVisibility(View.VISIBLE);
            radio_fm_listview.setVisibility(View.GONE);
        }

    }

    /**
     * 设置view的显示状态
     *
     * @param view
     * @param visibilityType
     */
    public void setVisibilityByView(View view, int visibilityType) {
        if (view != null) {
            if (view.getVisibility() == visibilityType) {
                return;
            } else {
                view.setVisibility(visibilityType);
            }
        }
    }

    class BroadcasetRadio extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppDataUtils.ACTION_CARUI_SWITCH_BAND.equals(intent.getAction())) {
                AppDataUtils.getInstance().setBand();
            } else if (ObjViewAvn.ACTION_LONG_PRE.equals(intent.getAction())) {
                AppDataUtils.getInstance().setPreCollect();
            } else if (ObjViewAvn.ACTION_LONG_NEXT.equals(intent.getAction())) {
                AppDataUtils.getInstance().setNextCollect();
            }
        }
    }


}
