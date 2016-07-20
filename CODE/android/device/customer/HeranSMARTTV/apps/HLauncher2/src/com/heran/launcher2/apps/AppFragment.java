
package com.heran.launcher2.apps;

import java.io.File;
import java.util.ArrayList;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.others.DownloadInfo;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.DownloadManager;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.GoogleAnalyticsUtil;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.CustomAlertDialog;
import com.heran.launcher2.widget.ViewBean;
import com.mstar.android.tv.TvCommonManager;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * projectName： EosLauncher moduleName： AppFragment.java
 * 
 * @author junling.hou
 * @version 1.0.0
 * @data 2016-3-31 上午11:33:00
 * @Copyright 2013 Eos Inc.
 */

public class AppFragment extends PublicFragment {

    private final static String TAG = "AppFragment";

    // Setting button
    private ImageButton mSettingView;

    private MyOnClickListener mClickListener;

    private MyOnFocusChangeListener mFocusChangeListener;

    private MyOnKeyListener mKeyListener;

    private MyPopOnItemClickListener myPopOnItemClickListener;

    private MyAppOnItemClickListener myAppOnItemClickListener;

    private MyOnItemSelectedListener myOnItemSelectedListener;

    private final HomeActivity mContext;

    private final ViewBean mViewBean;

    private GridView mMyAppgv;

    private GridView mPopAppgv;

    private PopGridAdapter mPopGridAdapter;

    private LocaleAppAdapter mLocaleAppAdapter;

    private ArrayList<ResolveInfo> myAppInfos;

    private ArrayList<AppInfoBean> mPopBeans;

    /*
     * fragment button
     */
    public ImageButton fragmentBtn;

    private FocusView mFocusView;

    private MHandler mMHandler;

    // the flag whether clear focus
    private boolean mClearFocus = true;

    private StringBuffer recData = new StringBuffer();

    private static final String SCIFLY_VIDEO_PKG = "com.eostek.scifly.video";

    private static final String SCIFLY_VIDEO_CLS = "com.eostek.scifly.video.home.HomeActivity";

    private ProgressDialog progressDialog;
    
    MainViewHolder mHolder ;
	
    boolean popfrist = true ;
    
    public Handler getAppHander(){
        return mMHandler;
    }
    
    public ImageButton getAppSetting(){
    	return mSettingView ;
    }
    
    public GridView getPopApp(){
    	return mPopAppgv ;
    }

    /*
     * get mViewBean
     */
    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public AppFragment(HomeActivity context) {
        super();
        mContext = context;
        mViewBean = new ViewBean(null, null);
        mHolder = mContext.mviewHolder;
    }

    /*
     * handler the msg 0
     */
    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Bundle b = msg.getData();
            long downing = b.getLong("download");
            if (downing > 0) {
                mPopGridAdapter.notifyDataSetChanged();
            }
            switch (msg.what) {
                case 0:
                    try {
						  drawFocus(mPopAppgv);
                        mPopAppgv.requestFocus();
                        mPopAppgv.setSelection(0);
                        // mPopAppgv.getSelectedView().requestFocus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    mPopGridAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    drawFocus(mPopAppgv.getSelectedView());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View mview = inflater.inflate(R.layout.app_main, container, false);
        initView(mview);
        setRetainInstance(true);
        return mview;
    }

    /**
     * init views and add listeners for views
     * 
     * @param mview The view inflate from xml
     */
    private void initView(View mview) {
        Log.d(TAG, "initView");
        mClickListener = new MyOnClickListener();
        mFocusChangeListener = new MyOnFocusChangeListener();
        mKeyListener = new MyOnKeyListener();
        mMHandler = new MHandler();
        myPopOnItemClickListener = new MyPopOnItemClickListener();
        myOnItemSelectedListener = new MyOnItemSelectedListener();
        myAppOnItemClickListener = new MyAppOnItemClickListener();

        mSettingView = (ImageButton) mview.findViewById(R.id.imageview_setting);
        mMyAppgv = (GridView) mview.findViewById(R.id.myapp_gv);
        mPopAppgv = (GridView) mview.findViewById(R.id.app_store_gv);
        fragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
        mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        myAppInfos = (ArrayList<ResolveInfo>) mContext.mainLogic.defaultAppInfos;
        if (mLocaleAppAdapter == null) {
            mLocaleAppAdapter = new LocaleAppAdapter(mContext, myAppInfos);
        }
        mMyAppgv.setAdapter(mLocaleAppAdapter);

        mPopBeans = (ArrayList<AppInfoBean>) mContext.mainLogic.downAppInfos;
        mPopGridAdapter = new PopGridAdapter(mContext, mPopBeans);
        mPopAppgv.setAdapter(mPopGridAdapter);

        mSettingView.setOnClickListener(mClickListener);
        mSettingView.setOnKeyListener(mKeyListener);
        mSettingView.setOnFocusChangeListener(mFocusChangeListener);

        mMyAppgv.setOnItemClickListener(myAppOnItemClickListener);
        mMyAppgv.setOnKeyListener(mKeyListener);
        mMyAppgv.setOnFocusChangeListener(mFocusChangeListener);
        mMyAppgv.setOnItemSelectedListener(myOnItemSelectedListener);

        mPopAppgv.setOnItemClickListener(myPopOnItemClickListener);
        mPopAppgv.setOnKeyListener(mKeyListener);
        mPopAppgv.setOnFocusChangeListener(mFocusChangeListener);
        mPopAppgv.setOnItemSelectedListener(myOnItemSelectedListener);

        addViewGlobalLayoutListener(mSettingView, mViewBean);
        addViewGlobalLayoutListener(mPopAppgv, mViewBean);
        addViewGlobalLayoutListener(mMyAppgv, mViewBean);

        mMHandler.removeMessages(0);
        mMHandler.sendEmptyMessage(0);
    }

    private void drawFocus(View view) {
        mViewBean.setmCurFocusView(view);
        mViewBean.getmFocusObject().startAnimation(view);
    }

    /**
     * clear GridView Focus,to avoid back to AppFragment call onItemSelected
     */
    private void clearGridViewFocus() {
        mMyAppgv.setSelection(0);
        mPopAppgv.setSelection(0);
    }

    /**
     * Handle key click event
     */
    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageview_setting:
                    // start setting appsetting
                    recData.append(HistoryRec.block[1] + ',' + HistoryRec.block2Action[4] + ',' + "" + ',' + "" + ','
                            + "" + ',' + HistoryRec.getCurrentDateTime());
                    HistoryRec.writeToFile(recData.toString());
                    recData.delete(0, recData.length());
                    mContext.mainLogic.startApk("com.eostek.scifly.devicemanager",
                            "com.eostek.scifly.devicemanager.DeviceManagerActivity", null);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Handle focus change
     */
    class MyOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {

                switch (v.getId()) {
                    case R.id.app_store_gv: // 上                
                        if(!popfrist){
                            if(mPopAppgv.getSelectedItemPosition()!=0){
                                mPopAppgv.setSelection(0);
                                mViewBean.setmCurFocusView(mPopAppgv.getSelectedView());
                            }else{
                                mViewBean.getmFocusObject().startAnimation(mPopAppgv.getSelectedView());
                            }
                        }else{ 
                            mPopAppgv.setSelection(0);
                            drawFocus(mPopAppgv.getSelectedView());
                            popfrist = false ;
                        }                                   
                        Log.d(TAG, "focus app_store_gv");
                        break;
                    case R.id.myapp_gv: // 下
                        Log.d(TAG, "focus myapp_gv");                                 
                        	if(mMyAppgv.getSelectedItemPosition()!=0){
                        		mMyAppgv.setSelection(0);
                        		mViewBean.setmCurFocusView(mMyAppgv.getSelectedView());
                        	}else{
                        		mViewBean.getmFocusObject().startAnimation(mMyAppgv.getSelectedView());
                        	}                   
                        break;
                    case R.id.imageview_setting:
                        Log.d(TAG, "focus mSettingView");
                        mViewBean.getmFocusObject().startAnimation(mSettingView);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Handle onKey event
     */
    class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return false;
            }
            switch (v.getId()) {
                case R.id.imageview_setting:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                        mMyAppgv.requestFocus();
                        return true;
                    }
                    // focus go to mMyAppgv when recevie down click
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        fragmentBtn.requestFocus();
//                        drawFocus(fragmentBtn);
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        mPopAppgv.requestFocus();
                        return true;
                    }
                    break;
                case R.id.app_store_gv:
                    // focus go to mPopAppgv when recevie right click and the
                    // gridview selection is right most
                    if ((mPopAppgv.getSelectedItemPosition() == 6 || mPopAppgv.getSelectedItemPosition() == 13
                            || mPopAppgv.getSelectedItemPosition() == 20) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                        mContext.mviewHolder.setHomeBtnFocus(0);
                        return true;
                    }
                    if ((mPopAppgv.getChildCount() >= 14 && mPopAppgv.getSelectedItemPosition() >= 14)) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                            mSettingView.requestFocus();
                            return true;
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            if (mPopAppgv.getSelectedItemPosition() == mPopAppgv.getChildCount() - 1) {
                                mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                                mContext.mviewHolder.setHomeBtnFocus(0);
                                return true;
                            }
                        }
                    }
                    if ((mPopAppgv.getChildCount() < 14 && mPopAppgv.getSelectedItemPosition() >= 7)) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                            mSettingView.requestFocus();
                            return true;
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            if (mPopAppgv.getSelectedItemPosition() == mPopAppgv.getChildCount() - 1) {
                                mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                                mContext.mviewHolder.setHomeBtnFocus(0);
                                return true;
                            }
                        }

                    }
                    if ((mPopAppgv.getChildCount() <= 7 && mPopAppgv.getSelectedItemPosition() < 7)) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                            mSettingView.requestFocus();
                            return true;
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            if (mPopAppgv.getSelectedItemPosition() == mPopAppgv.getChildCount() - 1) {
                                mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                                mContext.mviewHolder.setHomeBtnFocus(0);
                                return true;
                            }
                        }

                    }
                    if ((mPopAppgv.getSelectedItemPosition() < 7) && (keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_UP);
                        return true;
                    }
                    if ((mPopAppgv.getSelectedItemPosition() == 0 || mPopAppgv.getSelectedItemPosition() == 7
                            || mPopAppgv.getSelectedItemPosition() == 14) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_UP);
                        return true;
                    }
                    break;
                case R.id.myapp_gv:
                    // go to MEDIAVIEW when receive right click and the gridview
                    // selection is right most
                    if ((mMyAppgv.getSelectedItemPosition() == 4) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                        mContext.mviewHolder.setHomeBtnFocus(0);
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        fragmentBtn.requestFocus();
//                        drawFocus(fragmentBtn);
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_UP);
                        mPopAppgv.requestFocus();
                        return true;
                    }
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) && (mMyAppgv.getSelectedItemPosition() == 0)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_LEFT);
                        mSettingView.requestFocus();
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    /*
     * handler click for local app
     */
    class MyAppOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == (myAppInfos.size() - 1)) {
                // when click the all app icon,start AllAppListActivity

                recData.append(HistoryRec.block[1] + ',' + HistoryRec.block2Action[2] + ',' + "all app" + ',' + "" + ','
                        + "" + ',' + HistoryRec.getCurrentDateTime());
                HistoryRec.writeToFile(recData.toString());
                recData.delete(0, recData.length());

                Intent mIntent = new Intent();
                mIntent.setClass(mContext, AllAppListActivity.class);
                getActivity().startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.photo_zoom_enter, 0);
            } else {
                ResolveInfo appInfo = (ResolveInfo) parent.getItemAtPosition(position);

                recData.append(
                        HistoryRec.block[1] + ',' + HistoryRec.block2Action[2] + ',' + appInfo.activityInfo.packageName
                                + ',' + "" + ',' + "" + ',' + HistoryRec.getCurrentDateTime());
                HistoryRec.writeToFile(recData.toString());
                recData.delete(0, recData.length());

                startApk(mContext, appInfo.activityInfo.packageName, appInfo.activityInfo.name);
                Log.d(TAG, "startApk :" + mContext + "," + appInfo.activityInfo.packageName + ","
                        + appInfo.activityInfo.name);
            }
        }
    }

    private void showFakeInstallUI() {
        progressDialog = ProgressDialog.show(mContext, mContext.getString(R.string.install_title),
                mContext.getString(R.string.install_info), true, false);
        Utils.setIs91QInstall(mContext, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }
            }
        }).start();
    }

    /**
     * handler Pop GridView OnItemClickListener event
     */
    class MyPopOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean isExist = Utils.isAppInstalled(mContext, mPopBeans.get(position).getPackageName());
            Log.v(TAG, "isAppInstalled = " + isExist + " " + mPopBeans.get(position).getPackageName());
            if (isExist) {
                String get91qname = mPopBeans.get(position).getTitle();

                if (mContext.getResources().getString(R.string.Qvideo).equals(get91qname)) {
                    if (Utils.getIs91QInstall(mContext)) {
                        startApk(mContext, SCIFLY_VIDEO_PKG, SCIFLY_VIDEO_CLS);
                    } else {
                        Log.v(TAG, "first time click,show info to use whether install 91Q");
                        createDialog();
                        GoogleAnalyticsUtil.sendEvent(SCIFLY_VIDEO_PKG, SCIFLY_VIDEO_CLS, false, null);
                    }
                } else {
                    AppInfoBean appInfo = (AppInfoBean) parent.getItemAtPosition(position);
                    Log.i(TAG, "appInfo.getPackageName()==" + appInfo.getPackageName() + "," + appInfo.getClassName());
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_APPS, appInfo.getPackageName());
                    startApk(mContext, appInfo.getPackageName(), appInfo.getClassName());
                }
            } else {
                String url = mPopBeans.get(position).getDownloadUrl();

                AppInfoBean appInfo = mPopBeans.get(position);
                GoogleAnalyticsUtil.sendEvent(appInfo.getPackageName(), appInfo.getClassName(), false,
                        appInfo.getDownloadUrl());

                if (TextUtils.isEmpty(url) ||  Utils.isNetworkState == false) {
                    // if the path is empty, then send toast msg.
                    String str = mContext.getResources().getString(R.string.download_illegal_url);
                    Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
                } else {
                    String fileName = mPopBeans.get(position).getPackageName();
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .getAbsolutePath() + File.separator + fileName;
                    Log.i(TAG, "filePath = " + filePath);
                    File file = new File(filePath);
                    File file2 = new File(filePath + ".apk");
                    if (!file.exists() && !file2.exists()) {
                        createDowlodDialog(url, mPopBeans.get(position));
                    } else {
                        // file exists, that means has downloaded apk or
                        // downloading apk.
                        DownloadManager dm = DownloadManager.getDownloadManagerInstance(mContext, mMHandler);
                        DownloadInfo downloadInfo = dm.getDownloadInfo(url);
                        if (downloadInfo == null) {
                            Log.e(TAG, "Database has no this info. The uri is " + url);
                            file.delete();
                            createDowlodDialog(url, mPopBeans.get(position));
                            return;
                        }
                        Log.i(TAG, "current_bytes  " + downloadInfo.getPresentBytes());
                        Log.i(TAG, "total_bytes  " + downloadInfo.getTotalBytes());
                        if (downloadInfo.getPresentBytes() == downloadInfo.getTotalBytes()) {
                            // downloaded
                            Utils.install(mContext, filePath);
                        } else {
                            // is downloading
                            String str = mContext.getResources().getString(R.string.apk_downloading);
                            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
                            dm.restartDownload(downloadInfo, mPopBeans.get(position));
                            mPopBeans.get(position).setDownloadFlag(true);
                            mMHandler.removeMessages(1);
                            mMHandler.sendEmptyMessage(1);
                            mMHandler.removeMessages(2);
                            mMHandler.sendEmptyMessageDelayed(2, 200);
                        }
                    }
                }
            }
        }

        private void createDialog() {
            CustomAlertDialog mCustomAlertDialog = new CustomAlertDialog(mContext);
            mCustomAlertDialog.setTitle(R.string.scifly_video_dialog_title);
            mCustomAlertDialog.setMessage(R.string.scifly_video_dialog_message);
            mCustomAlertDialog.setPositiveButton(R.string.scifly_video_dialog_positive_button, new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    showFakeInstallUI();
                }
            });
            mCustomAlertDialog.setNegativeButton(R.string.scifly_video_dialog_negative_button, null);
        }

        private void createDowlodDialog(final String downloadUrl, final AppInfoBean mAppInfoBean) {
            final Resources resource = mContext.getResources();
            final CustomAlertDialog mCustomAlertDialog = new CustomAlertDialog(mContext);
            mCustomAlertDialog.setTitle(resource.getString(R.string.download_tips));
            mCustomAlertDialog.setMessage(resource.getString(R.string.download_msg));
            mCustomAlertDialog.setPositiveButton(resource.getString(R.string.download_ok), new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    String str;
                    if (downloadUrl == null || downloadUrl.isEmpty()) {
                        Log.e(TAG, "illeagle download url " + downloadUrl);
                        str = resource.getString(R.string.download_illegal_url);
                    } else {
                        DownloadManager dManager = DownloadManager.getDownloadManagerInstance(mContext, mMHandler);
                        dManager.startDownload(downloadUrl, mAppInfoBean);
                        mAppInfoBean.setDownloadFlag(true);
                        mMHandler.removeMessages(1);
                        mMHandler.sendEmptyMessage(1);
                        mMHandler.removeMessages(2);
                        mMHandler.sendEmptyMessageDelayed(2, 200);
                        str = resource.getString(R.string.download_start_downloading);
                        Log.v(TAG, "Download url = " + downloadUrl);
                    }
                    Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
                    if (mCustomAlertDialog != null) {
                        mCustomAlertDialog.dismiss();
                    }

                }
            });
            mCustomAlertDialog.setNegativeButton(resource.getString(R.string.download_cancel), new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (mCustomAlertDialog != null) {
                        mCustomAlertDialog.dismiss();
                    }
                }
            });
        }
    }

    /**
     * handler GridView OnItemSelectedListener event
     */
    class MyOnItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            drawFocus(view);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void startApk(Context context, String packageName, String className) {
        Intent mIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent == null) {
            UIUtil.toastShow(R.string.app_run_error, getActivity());
        } else {
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                mContext.startActivity(mIntent);
                mContext.setToChangeInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
            } catch (ActivityNotFoundException anf) {
                UIUtil.toastShow(R.string.app_run_error, getActivity());
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        mHolder.updatePIFragment(Constants.CITYSELECT_CLOSE,2);
        if (mClearFocus) {
            clearGridViewFocus();
        }

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.v(TAG, "onPause");
        mClearFocus = false;
        popfrist = true ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClearFocus = true;
        Log.v(TAG, "appfragment----onDestroy() ");
    }

    @Override
    public void onStop() {
        Log.v(TAG, "appfragment----onDestroy() ");
        mMHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

}
