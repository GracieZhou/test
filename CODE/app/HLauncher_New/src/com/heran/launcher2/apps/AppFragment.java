
package com.heran.launcher2.apps;

import java.io.File;
import java.util.ArrayList;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.others.DownloadInfo;
import com.heran.launcher2.util.DownloadManager;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.GoogleAnalyticsUtil;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.ViewBean;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2013-12-18 上午11:33:00
 * @Copyright © 2013 Eos Inc.
 */

public class AppFragment extends PublicFragment {

    private final static String TAG = "AppFragment";

    // Focus View for mFlipper
    // private ImageButton mFlipperFocusView;

    // Setting button
    private ImageButton mSettingView;

    // advertisement buttons
    // private ImageButton mAdButton;

    private MyOnClickListener mClickListener;

    private MyOnFocusChangeListener mFocusChangeListener;

    private MyOnKeyListener mKeyListener;

    private MyPopOnItemClickListener myPopOnItemClickListener;

    private MyAppOnItemClickListener myAppOnItemClickListener;

    private MyOnItemSelectedListener myOnItemSelectedListener;

    private HomeActivity mContext;

    private ViewBean mViewBean;

    private GridView mMyAppgv;

    private GridView mPopAppgv;

    private PopGridAdapter mPopGridAdapter;

    private LocaleAppAdapter mLocaleAppAdapter;

    private ArrayList<ResolveInfo> myAppInfos;

    private ArrayList<AppInfoBean> mPopBeans;

    // private FlipperAnimation mFlipperAnimation;

    public ImageButton fragmentBtn;

    public ImageButton AD;

    private FocusView mFocusView;

    private MHandler mMHandler;

    private boolean mClearFocus = true; // the flag whether clear focus

    private String recData = "";

    private static final String SCIFLY_VIDEO_PKG = "com.eostek.scifly.video";

    private static final String SCIFLY_VIDEO_CLS = "com.eostek.scifly.video.home.HomeActivity";

    private ProgressDialog progressDialog;

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
        // mHandler = new MyHandler();
        mViewBean = new ViewBean(null, null);
    }

    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        mPopAppgv.requestFocus();
                        mPopAppgv.setSelection(0);
                        // mPopAppgv.getSelectedView().requestFocus();
                    } catch (Exception e) {
                        Log.d(TAG, "onResume error : " + e.toString());
                    }
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

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClearFocus = true;
        Log.v(TAG, "appfragment----onDestroy() ");
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
        fragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
        mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        AD = (ImageButton) getActivity().findViewById(R.id.bt1);
        mSettingView = (ImageButton) mview.findViewById(R.id.imageview_setting);
        mMyAppgv = (GridView) mview.findViewById(R.id.myapp_gv);
        mPopAppgv = (GridView) mview.findViewById(R.id.app_store_gv);

        myAppInfos = (ArrayList<ResolveInfo>) mContext.mainLogic.defaultAppInfos;
        if (mLocaleAppAdapter == null) {
            mLocaleAppAdapter = new LocaleAppAdapter(mContext, myAppInfos);
        }
        mMyAppgv.setAdapter(mLocaleAppAdapter);

        mPopBeans = (ArrayList<AppInfoBean>) mContext.mainLogic.downAppInfos;
        mPopGridAdapter = new PopGridAdapter(mContext, mPopBeans);
        mPopAppgv.setAdapter(mPopGridAdapter);

        // setButtonListeners(mSettingView);
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
        // setGridViewListener(mMyAppgv, mMyAppgv.getId());
        // setGridViewListener(mPopAppgv, mPopAppgv.getId());
        addViewGlobalLayoutListener(mSettingView, mViewBean);
        addViewGlobalLayoutListener(mPopAppgv, mViewBean);
        addViewGlobalLayoutListener(mMyAppgv, mViewBean);

        mMHandler.removeMessages(0);
        mMHandler.sendEmptyMessage(0);
    }

    /**
     * set listener for view
     * 
     * @param mView
     */
    // private void setButtonListeners(View mView) {
    // mView.setOnClickListener(mClickListener);
    // mView.setOnFocusChangeListener(mFocusChangeListener);
    // mView.setOnKeyListener(mKeyListener);
    // // add OnGlobalLayoutListener for the view
    // addViewGlobalLayoutListener(mView, mViewBean);
    // }

    /**
     * add GridView Listeners
     * 
     * @param mView
     */
    // private void setGridViewListener(GridView mView, int mark) {
    // mView.setOnKeyListener(mKeyListener);
    // mView.setOnFocusChangeListener(mFocusChangeListener);
    // if (mark == mPopAppgv.getId()) {
    // mView.setOnItemClickListener(myPopOnItemClickListener);
    // } else if (mark == mMyAppgv.getId()) {
    // mView.setOnItemClickListener(myAppOnItemClickListener);
    // }
    // mView.setOnItemSelectedListener(myOnItemSelectedListener);
    // }

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
                    recData = HistoryRec.block[1] + ',' + HistoryRec.block2Action[4] + ',' + "" + ',' + "" + ',' + ""
                            + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
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
                        drawFocus(mPopAppgv.getSelectedView());
                        Log.d("test", "focus app_store_gv");
                        // mViewBean.getmFocusObject().startAnimation(mPopAppgv.getSelectedView());

                        break;
                    case R.id.myapp_gv: // 下
                        // drawFocus(mMyAppgv.getSelectedView());
                        Log.d("test", "focus myapp_gv");
                        mViewBean.getmFocusObject().startAnimation(mMyAppgv.getSelectedView());
                        break;
                    case R.id.imageview_setting:
                        // drawFocus(mSettingView);
                        Log.d("test", "focus mSettingView");
                        mViewBean.getmFocusObject().startAnimation(mSettingView);
                        break;

                    default:
                        break;
                }

            } // end of switch
        }
    }

    /**
     * Handle onKey event
     */
    class MyOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            switch (v.getId()) {
                //
                case R.id.imageview_setting:

                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                        // mMyAppgv.setSelection(0);
                        mMyAppgv.requestFocus();
                        return true;
                    }
                    // focus go to mMyAppgv when recevie down click
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        fragmentBtn.requestFocus();
                        drawFocus(fragmentBtn);
                        return true;
                    }
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        // mPopAppgv.setSelection(0);
                        mPopAppgv.requestFocus();
                        return true;
                    }
                    break;
                case R.id.app_store_gv:
                    // focus go to mPopAppgv when recevie right click and the
                    // gridview selection is right most
                    if ((mPopAppgv.getSelectedItemPosition() == 6 || mPopAppgv.getSelectedItemPosition() == 13
                            || mPopAppgv.getSelectedItemPosition() == 20) && (event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                        AD.requestFocus();
                        drawFocus(AD);
                        return true;
                    }
                    if ((mPopAppgv.getChildCount() >= 14 && mPopAppgv.getSelectedItemPosition() >= 14)
                            && (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        mSettingView.requestFocus();
                        return true;
                    }
                    if ((mPopAppgv.getChildCount() < 14 && mPopAppgv.getSelectedItemPosition() >= 7)
                            && (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        mSettingView.requestFocus();
                        return true;
                    }
                    if ((mPopAppgv.getChildCount() <= 7 && mPopAppgv.getSelectedItemPosition() < 7)
                            && (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        mSettingView.requestFocus();
                        return true;
                    }
                    if ((mPopAppgv.getSelectedItemPosition() < 7) && (event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_UP);

                        return true;
                    }

                    if ((mPopAppgv.getSelectedItemPosition() == 0 || mPopAppgv.getSelectedItemPosition() == 7
                            || mPopAppgv.getSelectedItemPosition() == 14) && (event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_UP);

                        return true;
                    }

                    break;

                case R.id.myapp_gv:
                    // Toast.makeText(getActivity(),
                    // "OnKeyListener==R.id.myapp_gv",Toast.LENGTH_SHORT).show();
                    // go to MEDIAVIEW when receive right click and the gridview
                    // selection is right most
                    if ((mMyAppgv.getSelectedItemPosition() == 4) && (event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                        AD.requestFocus();
                        return true;
                    }

                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        fragmentBtn.requestFocus();
                        drawFocus(fragmentBtn);
                        return true;
                    }

                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_UP);
                        // mPopAppgv.setSelection(0);
                        mPopAppgv.requestFocus();
                        return true;
                    }

                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                            && (mMyAppgv.getSelectedItemPosition() == 0)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_LEFT);
                        mSettingView.requestFocus();
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

                recData = HistoryRec.block[1] + ',' + HistoryRec.block2Action[2] + ',' + "all app" + ',' + "" + ',' + ""
                        + ',' + HistoryRec.getCurrentDateTime();
                HistoryRec.writeToFile(recData);
                recData = "";

                Intent mIntent = new Intent();
                mIntent.setClass(mContext, AllAppListActivity.class);
                getActivity().startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.photo_zoom_enter, 0);
            }

            else {
                ResolveInfo appInfo = (ResolveInfo) parent.getItemAtPosition(position);

                recData = HistoryRec.block[1] + ',' + HistoryRec.block2Action[2] + ','
                        + appInfo.activityInfo.packageName + ',' + "" + ',' + "" + ','
                        + HistoryRec.getCurrentDateTime();
                HistoryRec.writeToFile(recData);
                recData = "";

                startApk(mContext, appInfo.activityInfo.packageName, appInfo.activityInfo.name);
                Log.d(TAG, "startApk :" + mContext + "," + appInfo.activityInfo.packageName + ","
                        + appInfo.activityInfo.name);
                // // ----------- add by Jason
                // //
                // --------------------------------------------------------------------
                // hRecString = hRecString + HistoryRec.block2Action[2] + ',' +
                // // appInfo.activityInfo.packageName + ',';
                // hRecString = hRecString + HistoryRec.getCurrentDateTime();
                // HistoryRec.writeToFile(hRecBlock + hRecString);
                // hRecString = "";
                // //
                // ---------------------------------------------------------------------------------------------

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
                    if (!file.exists() && !file2.exists()) {
                        // if file does not exist, then start download
                        // Intent intent = new Intent(mContext,
                        // BlurActivity.class);
                        // intent.putExtra("DownloadUrl", url);
                        // mContext.startActivity(intent);
                        createDowlodDialog(url);
                    } else {
                        // file exists, that means has downloaded apk or
                        // downloading apk.
                        DownloadManager dm = DownloadManager.getDownloadManagerInstance(mContext);
                        DownloadInfo downloadInfo = dm.getDownloadInfo(url);
                        if (downloadInfo == null) {
                            Log.e(TAG, "Database has no this info. The uri is " + url);
                            return;
                        }
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
                String get91qname = mPopBeans.get(position).getTitle();
                if (get91qname.equals(R.string.Qvideo)) {
                    if (Utils.getIs91QInstall(mContext)) {

                        startApk(mContext, SCIFLY_VIDEO_PKG, SCIFLY_VIDEO_CLS);
                    } else {
                        Log.v(TAG, "first time click,show info to use whether install 91Q");
                        createDialog();
                        GoogleAnalyticsUtil.sendEvent(SCIFLY_VIDEO_PKG, SCIFLY_VIDEO_CLS, false, null);
                    }
                    // recData = HistoryRec.block[1] + ',' +
                    // HistoryRec.block2Action[2] + ',' + "91q app" + ',' + "" +
                    // ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    // HistoryRec.writeToFile(recData);
                    // recData = "";
                } else {
                    AppInfoBean appInfo = (AppInfoBean) parent.getItemAtPosition(position);
                    Log.i(TAG, "appInfo.getPackageName()==" + appInfo.getPackageName() + "," + appInfo.getClassName());
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_APPS, appInfo.getPackageName());
                    startApk(mContext, appInfo.getPackageName(), appInfo.getClassName());
                }
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

        private void createDowlodDialog(final String downloadUrl) {
            final Resources resource = mContext.getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(resource.getString(R.string.download_tips));
            builder.setMessage(resource.getString(R.string.download_msg));
            builder.setCancelable(false);
            builder.setNegativeButton(resource.getString(R.string.download_ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str;
                    if (downloadUrl == null || downloadUrl.isEmpty()) {
                        Log.e(TAG, "illeagle download url " + downloadUrl);
                        str = resource.getString(R.string.download_illegal_url);
                    } else {
                        DownloadManager dManager = DownloadManager.getDownloadManagerInstance(mContext);
                        dManager.startDownload(downloadUrl);
                        str = resource.getString(R.string.download_start_downloading);
                        Log.v(TAG, "Download url = " + downloadUrl);
                    }
                    Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
                }
            });
            builder.setPositiveButton(resource.getString(R.string.download_cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
            builder.create().show();
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
