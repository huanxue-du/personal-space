package com.autolink.radio55.view;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.ViewGroup;

import com.autolink.radio55.app.AppDataUtils;
import com.autolink.radio55.utils.ELog;
import com.autolink.refutils.ViewBaseManager;
import com.autolink.refutils.ViewOptManagerEnum;

/**
 * 主控制视图类
 *
 * @author Administrator
 */
public class RadioRemoteMainView extends ViewBaseManager {
    private Activity mActivityContext;
    private Context mContext;

    @Override
    public View onCreateView(Application application, Context context, Context contexts, Object... objects) {
        super.onCreateView(application, context, contexts, objects);

        try {
            mContext = context.createPackageContext("com.autolink.radio55", Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ELog.i("onCreateView----");
        mActivityContext = (Activity) context;
        AppDataUtils.getInstance().setRegisterMvc(context, application);

        return (ViewGroup) RadioMainView.getInstance(mContext, mActivityContext).onCreateView();
    }

    private void onActivityCreate() {
        RadioMainView.getInstance(mContext, mActivityContext).onActivityCreate();
    }

    private void onLoseFocus() {
        AppDataUtils.getInstance().isOnRadioView(false);
    }

    //获得焦点太早，无法持有上下文
    private void onFocus() {
        AppDataUtils.getInstance().isOnRadioView(true);
    }

    private void onPause() {
        RadioMainView.getInstance(mContext, mActivityContext).onPause();

    }

    private void onStart() {
        RadioMainView.getInstance(mContext, mActivityContext).onStart();
    }

    private void onResume() {
        AppDataUtils.getInstance().isOnRadioView(true);
        RadioMainView.getInstance(mContext, mActivityContext).onResume();
    }

    private void onStop() {
        RadioMainView.getInstance(mContext, mActivityContext).onStop();

    }

    private void onDestroyView() {
        RadioMainView.getInstance(mContext, mActivityContext).onDestroyView();
//        AppDataUtils.getInstance().isOnRadioView(false);
    }

    private void onDestroy() {
    }

    @Override
    public void onActivityContext(Context activity, Dialog dialog) {
        mActivityContext = (Activity) activity;
        AppDataUtils.getInstance().isOnRadioView(true);
        ELog.i("onActivityContext----");
    }

    @Override
    public void onViewOptMethod(int arg0, Object... arg1) {
        // TODO Auto-generated method stub
        switch (arg0) {
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_FIRST:
                //                AppDataUtils.getInstance().updateFirstStart();
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_DESTROY:
                onDestroy();
                ELog.i("onDestroy----");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_RESUME:
                onResume();
                ELog.i("onResume----");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_PAUSE:
                onPause();
                ELog.i("onPause----");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_STOP:
                onStop();
                ELog.i("onStop----");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_FOCUS:
                onFocus();
                ELog.i("onFocus----");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_LOSEFOCUS:
                onLoseFocus();
                ELog.i("onLoseFocus----");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_START:
                onStart();
                ELog.i("onStart----");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_CREATE_VIEW:
                onActivityCreate();
                ELog.i("onActivityCreate----");
                break;
            case ViewOptManagerEnum.VIEW_OPT_METHOD_ON_DESTROY_VIEW:
                onDestroyView();
                ELog.i("onDestroyView----");
                break;
            default:
                break;
        }
    }

}
