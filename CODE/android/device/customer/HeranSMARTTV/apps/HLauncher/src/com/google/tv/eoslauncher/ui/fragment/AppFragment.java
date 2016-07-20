
package com.google.tv.eoslauncher.ui.fragment;

import java.io.File;
import java.util.ArrayList;

import scifly.provider.SciflyStatistics;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
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
import android.widget.ViewFlipper;

import com.google.tv.eoslauncher.HomeActivity;
import com.google.tv.eoslauncher.HomeApplication;
import com.google.tv.eoslauncher.MainViewHolder;
import com.google.tv.eoslauncher.R;
import com.google.tv.eoslauncher.business.DownloadManager;
import com.google.tv.eoslauncher.model.AppInfoBean;
import com.google.tv.eoslauncher.model.DownloadInfo;
import com.google.tv.eoslauncher.model.MyAD;
import com.google.tv.eoslauncher.model.ViewBean;
import com.google.tv.eoslauncher.ui.BlurActivity;
import com.google.tv.eoslauncher.ui.app.AllAppListActivity;
import com.google.tv.eoslauncher.ui.app.AppStoreActivity;
import com.google.tv.eoslauncher.ui.app.LocaleAppAdapter;
import com.google.tv.eoslauncher.ui.app.PopGridAdapter;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.FlipperAnimation;
import com.google.tv.eoslauncher.util.FocusView;
import com.google.tv.eoslauncher.util.GoogleAnalyticsUtil;
import com.google.tv.eoslauncher.util.HistoryRec;
import com.google.tv.eoslauncher.util.UIUtil;
import com.google.tv.eoslauncher.util.Utils;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * projectName： EosLauncher moduleName： AppFragment.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2013-12-18 上午11:33:00
 * @Copyright © 2013 Eos Inc.
 */

public class AppFragment extends PublicFragment {

    private final static String TAG = "AppFragment";

    // Focus View for mFlipper
    private ImageButton mFlipperFocusView;

    // Setting button
    private ImageButton mSettingView;

    // advertisement buttons
    private ImageButton mAdButton;

    private MyOnClickListener mClickListener;

    private MyOnFocusChangeListener mFocusChangeListener;

    private MyOnKeyListener mKeyListener;

    private MyPopOnItemClickListener myPopOnItemClickListener;

    private MyAppOnItemClickListener myAppOnItemClickListener;

    private MyOnItemSelectedListener myOnItemSelectedListener;

    private HomeActivity mContext;

    private MainViewHolder mHolder;

    private Handler mHandler;

    // the view show mAppStoreView and ads
    private ViewFlipper mFlipper;

    private ViewBean mViewBean;

    private GridView mMyAppgv;

    private GridView mPopAppgv;

    private PopGridAdapter mPopGridAdapter;

    private LocaleAppAdapter mLocaleAppAdapter;

    ArrayList<ResolveInfo> myAppInfos;

    private ArrayList<AppInfoBean> mPopBeans;

    private FlipperAnimation mFlipperAnimation;

    private int animType; // animation type

    private int animTime;

    private boolean mClearFocus = true; // the flag whether clear focus

    private static final String SCIFLY_VIDEO_PKG = "com.eostek.scifly.video";

    private static final String SCIFLY_VIDEO_CLS = "com.eostek.scifly.video.home.HomeActivity";

    private static final String SCIFLY_VIDEO_ADDRESS = "/system/media/SciflyVideo.apk";
    
    private static final String BABAOFAN_PKG = "com.jrm.babao.babaofan";
    
    private static final String BABAOFAN_CLS = "com.jrm.babao.babaofan.activity.main.BabaofanMainActivity";

    private ProgressDialog progressDialog;

    private static final int MSG_MDISMISS_DIALOG = 0x3000;
    
    private String recData;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public AppFragment() {
        super();
        Log.v(TAG, "public appfragment()");
    }

    public AppFragment(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        this.mHolder = mHolder;
        mHandler = new MyHandler();
        mViewBean = new ViewBean(null, null);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.app_main, container, false);
        initView(mview);
        setRetainInstance(true);
        return mview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mClearFocus) {
            clearGridViewFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mClearFocus = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClearFocus = true;
        mHandler.removeMessages(Constants.APPADUPDATE);
        mFlipper.removeAllViews();
        System.gc();
        Log.v(TAG, "appfragment----onDestroy() ");
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.APPADUPDATE: // update ads picture
                    // mFlipper.showNext();
                    mFlipperAnimation.loadFlipperAnimation(animType);
                    mHandler.sendEmptyMessageDelayed(Constants.APPADUPDATE, animTime);
                    break;
                case MSG_MDISMISS_DIALOG:
                    progressDialog.dismiss();
                    Utils.setIs91QInstall(mContext, true);
                    Log.v(TAG, "finish fake install,set the click flag to true");
                default:
                    break;
            }
        }
    }

    /**
     * init views and add listeners for views
     * 
     * @param mview The view inflate from xml
     */
    private void initView(View mview) {
        mClickListener = new MyOnClickListener();
        mFocusChangeListener = new MyOnFocusChangeListener();
        mKeyListener = new MyOnKeyListener();
        myPopOnItemClickListener = new MyPopOnItemClickListener();
        myOnItemSelectedListener = new MyOnItemSelectedListener();
        myAppOnItemClickListener = new MyAppOnItemClickListener();

        FocusView mFocusView = (FocusView) mview.findViewById(R.id.app_selector);
        // mAppStoreView = (ImageButton)
        // mview.findViewById(R.id.iamgeview_appstore);
        // mMyAppView = (ImageButton) mview.findViewById(R.id.imageview_myapp);
        mSettingView = (ImageButton) mview.findViewById(R.id.imageview_setting);
        mMyAppgv = (GridView) mview.findViewById(R.id.myapp_gv);
        mPopAppgv = (GridView) mview.findViewById(R.id.app_store_gv);

        myAppInfos = (ArrayList<ResolveInfo>) mContext.defaultAppInfos;
        if (mLocaleAppAdapter == null) {
            mLocaleAppAdapter = new LocaleAppAdapter(mContext, myAppInfos);
        }
        mMyAppgv.setAdapter(mLocaleAppAdapter);

        mPopBeans = (ArrayList<AppInfoBean>) mContext.downAppInfos;
        mPopGridAdapter = new PopGridAdapter(mContext, mPopBeans);
        mPopAppgv.setAdapter(mPopGridAdapter);

        initFlipper(mview);
        mFlipper.setDisplayedChild(0);
        // Add Related listeners
        addViewGlobalLayoutListener(mFlipperFocusView, mViewBean);
        setButtonListeners(mSettingView);
        setGridViewListener(mPopAppgv, mPopAppgv.getId());
        setGridViewListener(mMyAppgv, mMyAppgv.getId());

        mViewBean.setmFocusObject(mFocusView);
        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(mFlipperFocusView);
        } else if (mViewBean.getmCurFocusView() == mHolder.home_content) {
            // if curFoucesView is the mark set in restoreFragmentFocus,set the
            // curFoucesView to the left view
            mViewBean.setmCurFocusView(mSettingView);
        }
    }

    private void initFlipper(View mview) {
        mFlipper = (ViewFlipper) mview.findViewById(R.id.app_ad_filpper);
        mFlipperAnimation = new FlipperAnimation(mContext, mFlipper);
        // add ads view to mFlipper
        for (int i = 0; i < mContext.appSotreAdInfoList.size(); i++) {
            View mTempView = LayoutInflater.from(mContext).inflate(R.layout.app_ad_item, null);
            mFlipper.addView(mTempView);
            mAdButton = (ImageButton) mTempView.findViewById(R.id.ad_1);
            // mAdButton.setImageBitmap(mContext.appSotreAdInfoList.get(i).getBt());
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true)
                    .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).showImageOnLoading(R.drawable.a1)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
            HomeApplication.getInstance().displayImage(mContext.appSotreAdInfoList.get(i).getPic(), options, mAdButton);
            mAdButton.setOnFocusChangeListener(mFocusChangeListener);
            mAdButton.setOnKeyListener(mKeyListener);
            mAdButton.setOnClickListener(mClickListener);
            if (i == 0) {
                // init mFlipperFocusView for the focus object to draw
                mFlipperFocusView = mAdButton;
            }
        }
        // init the anim type and duration
        animType = mContext.appSotreAdInfoList.get(0).getPlt();
        animTime = mContext.appSotreAdInfoList.get(0).getDit();
        // start animation
        mHandler.sendEmptyMessageDelayed(Constants.APPADUPDATE, animTime);
    }

    /**
     * set listener for view
     * 
     * @param mView
     */
    private void setButtonListeners(View mView) {
        mView.setOnClickListener(mClickListener);
        mView.setOnFocusChangeListener(mFocusChangeListener);
        mView.setOnKeyListener(mKeyListener);
        // add OnGlobalLayoutListener for the view
        addViewGlobalLayoutListener(mView, mViewBean);
    }

    /**
     * add GridView Listeners
     * 
     * @param mView
     */
    private void setGridViewListener(GridView mView, int mark) {
        mView.setOnKeyListener(mKeyListener);
        mView.setOnFocusChangeListener(mFocusChangeListener);
        if (mark == mPopAppgv.getId()) {
            mView.setOnItemClickListener(myPopOnItemClickListener);
        } else if (mark == mMyAppgv.getId()) {
            mView.setOnItemClickListener(myAppOnItemClickListener);
        }
        mView.setOnItemSelectedListener(myOnItemSelectedListener);
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
                    GoogleAnalyticsUtil.sendEvent("com.eostek.scifly.devicemanager",
                            "com.eostek.scifly.devicemanager.DeviceManagerActivity", true, null);
                    
                    recData = HistoryRec.block[1] + ',' + HistoryRec.block2Action[4] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    
                    // start setting appsetting
                    mContext.startApk("com.eostek.scifly.devicemanager",
                            "com.eostek.scifly.devicemanager.DeviceManagerActivity", null);
                    break;
                case R.id.ad_1:
                    int currpage = mFlipper.getDisplayedChild();
                    MyAD adinfo = mContext.appSotreAdInfoList.get(currpage);

                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_APPS, adinfo, 0);
                    
                    recData = HistoryRec.block[1] + ',' + HistoryRec.block2Action[0] + ',' + adinfo.getGln() + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";

                    Intent intent = new Intent(mContext, AppStoreActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", adinfo.getGln());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "adClickRecord", "adUrl",
                            adinfo.getGln());
                    Log.v(TAG, "AppFragment mFlipper.getDisplayedChild " + mFlipper.getDisplayedChild());
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
                Log.v(TAG, "appfragment---onfocuschange " + v.getId());
            }
            switch (v.getId()) {
                case R.id.ad_1:
                    // if the views in Flipper have focus,stop display ads
                    if (hasFocus) {
                        mViewBean.setmCurFocusView(mFlipperFocusView);
                        mHandler.removeMessages(Constants.APPADUPDATE);
                        // draw focus view
                        mViewBean.getmFocusObject().startAnimation(v);
                    }
                    break;
                case R.id.imageview_setting:
                    if (hasFocus) { // when has focus,draw focus view
                        mViewBean.setmCurFocusView(mSettingView);
                        mViewBean.getmFocusObject().startAnimation(v);
                        // when the views in Flipper lost focus,start display
                        // ads
                        mHandler.removeMessages(Constants.APPADUPDATE);
                        mHandler.sendEmptyMessageDelayed(Constants.APPADUPDATE, Constants.DELAYDCHANGEPIC);
                    }
                    break;
                case R.id.app_store_gv:
                    if (hasFocus) {
                        mViewBean.getmFocusObject().startAnimation(mPopAppgv.getSelectedView());
                        mHandler.removeMessages(Constants.APPADUPDATE);
                        mHandler.sendEmptyMessageDelayed(Constants.APPADUPDATE, Constants.DELAYDCHANGEPIC);
                    }
                    break;
                case R.id.myapp_gv:
                    if (hasFocus) {
                        mViewBean.getmFocusObject().startAnimation(mMyAppgv.getSelectedView());
                        mHandler.removeMessages(Constants.APPADUPDATE);
                        mHandler.sendEmptyMessageDelayed(Constants.APPADUPDATE, Constants.DELAYDCHANGEPIC);
                    }
                    break;
                default:
                    break;
            }// end of switch
        }
    }

    /**
     * Handle onKey event
     */
    class MyOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (isRunning) {
                // if the Fragment animation is running,Ignore key event
                return true;
            }
            switch (v.getId()) {
                case R.id.ad_1:
                    // iamgeview_appstore receive left click,switch to HOMEVIEW
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                        mViewBean.setmCurFocusView(mFlipperFocusView);
                        clearGridViewFocus();
                        mContext.showViews(Constants.HOMEVIEW);
                        return true;
                    }
                    // handler the right key click event,move the focus to
                    // mSettingView
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mSettingView.requestFocus();
                        return true;
                    }
                    // handler the down key click event,move the focus to
                    // mPopAppgv
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mPopAppgv.requestFocus();
                        return true;
                    }
                    break;
                case R.id.imageview_setting:
                    // imageview_setting receive right click,switch to MEDIAVIEW
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mViewBean.setmCurFocusView(mSettingView);
                        clearGridViewFocus();
                        Log.d("AppFragment", "sam onCreate Constants.kok_device " + Constants.kok_device);
                        mContext.showViews(Constants.MEDIAVIEW);
                        return true;
                    }
                    // focus go to mMyAppgv when recevie down click
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mMyAppgv.requestFocus();
                        return true;
                    }
                    break;
                case R.id.app_store_gv:
                    // go to HOMEVIEW when receive left click and the gridview
                    // selection is left most
                    if ((mPopAppgv.getSelectedItemPosition() == 0) && (event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                        mViewBean.setmCurFocusView(mFlipperFocusView);
                        clearGridViewFocus();
                        mContext.showViews(Constants.HOMEVIEW);
                        return true;
                    }
                    // focus go to mPopAppgv when recevie right click and the
                    // gridview selection is right most
                    if ((mPopAppgv.getSelectedItemPosition() == 4) && (event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mSettingView.requestFocus();
                        return true;
                    }
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
                        if (mFlipper != null) {
                            mFlipper.getCurrentView().requestFocus();
                        }
                        return true;
                    }
                    break;
                case R.id.myapp_gv:
                    // go to MEDIAVIEW when receive right click and the gridview
                    // selection is right most
                    if ((mMyAppgv.getSelectedItemPosition() == 2 || mMyAppgv.getSelectedItemPosition() == 5)
                            && (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mViewBean.setmCurFocusView(mSettingView);
                        clearGridViewFocus();
                        Log.d("AppFragment", "sam onCreate Constants.kok_device " + Constants.kok_device);
                        mContext.showViews(Constants.MEDIAVIEW);
                        return true;
                    }
                    break;
                default:
                    break;
            }// end of switch
            return false;
        }
    }

    class MyAppOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == (myAppInfos.size() - 1)) {
                // when click the all app icon,start AllAppListActivity
            	
            	 recData = HistoryRec.block[1] + ',' + HistoryRec.block2Action[2] + ',' + "all app" + ',' + HistoryRec.getCurrentDateTime();
                 Log.d("rec", "tempData:" + recData);
                 HistoryRec.writeToFile(recData);
                 recData = "";
                 
                Intent mIntent = new Intent();
                mIntent.setClass(mContext, AllAppListActivity.class);
                getActivity().startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.photo_zoom_enter, 0);
            }  else {
                ResolveInfo appInfo = (ResolveInfo) parent.getItemAtPosition(position);

                GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_APPS, appInfo.activityInfo.packageName);

                recData = HistoryRec.block[1] + ',' + HistoryRec.block2Action[2] + ','
                        + appInfo.activityInfo.packageName + ',' + HistoryRec.getCurrentDateTime();
                Log.d("rec", "tempData:" + recData);
                HistoryRec.writeToFile(recData);
                recData = "";

                startApk(mContext, appInfo.activityInfo.packageName, appInfo.activityInfo.name);

            }
        }

        private void createDialog() {
            AlertDialog.Builder builder = new Builder(mContext);
            builder.setTitle(R.string.scifly_video_dialog_title);
            builder.setMessage(R.string.scifly_video_dialog_message);
            builder.setPositiveButton(R.string.scifly_video_dialog_positive_button,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // Utils.install(mContext, SCIFLY_VIDEO_ADDRESS);
                            // Environment.getSystemSecureDirectory();
                            showFakeInstallUI();
                        }
                    });
            builder.setNegativeButton(R.string.scifly_video_dialog_negative_button, null);
            builder.create().show();
        }

    }

    private void showFakeInstallUI() {
        progressDialog = ProgressDialog.show(mContext, mContext.getString(R.string.install_title),
                mContext.getString(R.string.install_info), true, false);
        mHandler.sendEmptyMessageDelayed(MSG_MDISMISS_DIALOG, 3 * 1000);
    }

    public void createBaBaoFanDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.babaofan_dialog_title);
        builder.setMessage(R.string.babaofan_dialog_message);
        builder.setPositiveButton(R.string.scifly_video_dialog_positive_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                startApk(mContext, BABAOFAN_PKG, BABAOFAN_CLS);
            }
        });
        builder.setNegativeButton(R.string.scifly_video_dialog_negative_button, null);
        builder.create().show();

    }

    /**
     * handler Pop GridView OnItemClickListener event
     */
    class MyPopOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean isExist = Utils.isAppInstalled(mContext, mPopBeans.get(position).getPackageName());
            Log.v(TAG, "isAppInstalled = " + isExist + " " + mPopBeans.get(position).getPackageName());
            
            recData = HistoryRec.block[1] + ',' + HistoryRec.block2Action[1] + ',' + mPopBeans.get(position).getPackageName() + ',' + HistoryRec.getCurrentDateTime();
            Log.d("rec", "tempData:" + recData);
            HistoryRec.writeToFile(recData);
            recData = "";
            
            if (!isExist) {
                String url = mPopBeans.get(position).getDownloadUrl();

                AppInfoBean appInfo = mPopBeans.get(position);
                GoogleAnalyticsUtil.sendEvent(appInfo.getPackageName(), appInfo.getClassName(), false,
                        appInfo.getDownloadUrl());

                if (TextUtils.isEmpty(url) || Utils.isNetworkState == false) {
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
                    DownloadManager dm = DownloadManager.getDownloadManagerInstance(mContext);
                    DownloadInfo downloadInfo = dm.getDownloadInfo(url);
                    if (!file.exists() && !file2.exists() && downloadInfo == null) {
                        // if file does not exist, then start download
                        Intent intent = new Intent(mContext, BlurActivity.class);
                        intent.putExtra("DownloadUrl", url);
                        mContext.startActivity(intent);
                    } else {
                        // file exists, that means has downloaded apk or
                        // downloading apk.
                        Log.i(TAG, "current_bytes  " + downloadInfo.getPresentBytes());
                        Log.i(TAG, "total_bytes  " + downloadInfo.getTotalBytes());
                        if (downloadInfo.getPresentBytes() != downloadInfo.getTotalBytes()) {
                            // is downloading
                            String str = mContext.getResources().getString(R.string.apk_downloading);
                            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
                            dm.restartDownload(downloadInfo);
                        } else if (downloadInfo.getPresentBytes() == downloadInfo.getTotalBytes()) {
                            // downloaded
                            Utils.install(mContext, filePath);
                        }
                    }
                }
            } else {
                AppInfoBean appInfo = (AppInfoBean) parent.getItemAtPosition(position);

                GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_APPS, appInfo.getPackageName());

                startApk(mContext, appInfo.getPackageName(), appInfo.getClassName());
            }
        }
    }

    /**
     * handler GridView OnItemSelectedListener event
     */
    class MyOnItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mViewBean.getmFocusObject().startAnimation(view);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void startApk(Context context, String packageName, String className) {
        Intent mIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                UIUtil.updateHistory(mContext, packageName, className);
                mContext.startActivity(mIntent);
                mContext.setToChangeInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
            } catch (ActivityNotFoundException anf) {
                UIUtil.toastShow(R.string.app_run_error, getActivity());
            }
        } else {
            UIUtil.toastShow(R.string.app_run_error, getActivity());
        }
    }

}
