
package com.eostek.scifly.devicemanager.recommend;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.eostek.scifly.devicemanager.DeviceManagerActivity;
import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.service.DownloadService;
import com.eostek.scifly.devicemanager.service.DownloadService.DownlaodServiceBinder;
import com.eostek.scifly.devicemanager.service.DownloadService.OnServiceListener;
import com.eostek.scifly.devicemanager.receiver.MessageListenerBase;
import com.eostek.scifly.devicemanager.receiver.MessageReceiver;
import com.eostek.scifly.devicemanager.recommend.AppRecommendThread.OnThreadListener;
import com.eostek.scifly.devicemanager.ui.DownloadProcessView;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.SciflyToast;
import com.eostek.scifly.devicemanager.util.Util;

public class AppRecommendFragment extends Fragment implements OnItemClickListener {

    public static final String TAG = AppRecommendFragment.class.getSimpleName();

    private final static int MSG_TYPE_EVT_QUREY_FAILURE = 0;
    private final static int MSG_TYPE_EVT_QUREY_SUCCESS = 1;
    private final static int MSG_TYPE_EVT_SYNC_INFOLIST = 2;
    private final static int MSG_TYPE_EVT_TOAST_RUNNING = 3;
    private final static int MSG_TYPE_EVT_INSTALL_SUCCESS = 4;
    private final static int MSG_TYPE_EVT_INSTALL_FAILURE = 5;
    private final static int MSG_TYPE_EVT_PACKAGE_DEL = 6;
    private final static int MSG_TYPE_EVT_TOAST_DOWNLOAD_START = 7;
    
    private DeviceManagerActivity mActivity;
    
    private GridView mGridView;
    private View mWaitView;
    
    private AppRecommendThread mAppRecommendThread;
    private AppRecommendAdapter mAppRecommendAdapter;
    private AppRecommendHandler mRecommendHandler = new AppRecommendHandler();
    private BroadcastReceiverListener mReceiverListener;
    private DownloadServiceConnection mDownloadServiceConnection;
    private DownloadServiceListener mDownloadServiceListener;
    private DownloadService mDownloadService;
    
    private final static int DEFAULT_QUERY_PAGE_INDEX = 1;
    private int mQueryPageIndex = DEFAULT_QUERY_PAGE_INDEX;
    
    private void showToast(String toastString) {
        SciflyToast.showShortToast(mActivity, toastString);
    }
    
    private void queryRecommend(int pageIndex) {
        mAppRecommendThread = new AppRecommendThread(mActivity, pageIndex);
        mAppRecommendThread.setOnThreadListener(new OnThreadListener() {
            @Override
            public void onSyncInfo(AppRecommendInfo info) {
                mRecommendHandler.obtainMessage(MSG_TYPE_EVT_SYNC_INFOLIST).sendToTarget();
            }
            
            @Override
            public void onQueryInfoSuccess() {
                mRecommendHandler.obtainMessage(MSG_TYPE_EVT_QUREY_SUCCESS).sendToTarget();
            }
            
            @Override
            public void onQueryInfoFailure() {
                mRecommendHandler.obtainMessage(MSG_TYPE_EVT_QUREY_FAILURE).sendToTarget();
            }
        });
        mAppRecommendThread.start();
    }
    
    public AppRecommendFragment(DeviceManagerActivity activity) {
        mActivity = activity;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    
        mReceiverListener = new BroadcastReceiverListener();
        MessageReceiver.addMessageListener(mReceiverListener);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.d(TAG, "onCreate");
        queryRecommend(DEFAULT_QUERY_PAGE_INDEX);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        initViews(view);
        Debug.d(TAG, "onCreateView");
        
        return view;
    }
    
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Debug.d(TAG, "onStart");
        
        Intent mIntent = new Intent(mActivity, DownloadService.class);
        mDownloadServiceConnection = new DownloadServiceConnection();
        mActivity.bindService(mIntent, mDownloadServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Debug.d(TAG, "onResume");
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Debug.d(TAG, "onPause");
    }
    
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Debug.d(TAG, "onStop");
        
        if(mDownloadServiceConnection != null) {
            mActivity.unbindService(mDownloadServiceConnection);
            mDownloadServiceConnection = null;
        }
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Debug.d(TAG, "onDestroy");
        
        MessageReceiver.removeMessageListener(mReceiverListener);
    }
  

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        AppRecommendInfo info = AppRecommendThread.getList().get(position);
        if (Util.checkApkExist(mActivity, info.getmPkgName())) {
            Intent intent = mActivity.getPackageManager().getLaunchIntentForPackage(info.getmPkgName());
            startActivity(intent);
        } else {
            if(mDownloadService != null) {
                mDownloadService.startDownloadItem(info.getmPath(), info.getmPkgName());
            }
        }
        mGridView.requestFocus();
    }
    
    private void initViews(View view) {
        mGridView = (GridView)view.findViewById(R.id.fragment_recommend_gridview);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean gridViewFocus) {
                int i = mGridView.getSelectedItemPosition();
                view = mGridView.getChildAt(i);
                if(view != null) {
                    RelativeLayout mLayout = (RelativeLayout) view.findViewById(R.id.fragment_recommend_gridview_item_layout);
                    if (gridViewFocus) {
                        mLayout.setBackgroundResource(R.drawable.bg_autostart_dark_list_selector);
                    } else {
                        mLayout.setBackgroundResource(R.drawable.pic_bg);
                    }
                }
            }
        });
        mGridView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int currentIndex = mGridView.getSelectedItemPosition();
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if((currentIndex == 4||currentIndex == 9)  && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                        if(AppRecommendThread.getList().size() == 10) {
                            mQueryPageIndex = mQueryPageIndex + 1;
                            mWaitView.setVisibility(View.VISIBLE);
                            Debug.d(TAG, "R:mQueryPageIndex=" + mQueryPageIndex);
                            queryRecommend(mQueryPageIndex);
                        }
                        return true;
                    }
                    if ((currentIndex <= 4 && currentIndex >= 0 ) && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        mActivity.reuqestFocus(1);
                        return true;
                    }
                    if ((currentIndex == 0 || currentIndex == 5) && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if(mQueryPageIndex > 1) {
                            mQueryPageIndex = mQueryPageIndex - 1;
                            mWaitView.setVisibility(View.VISIBLE);
                            Debug.d(TAG, "L:mQueryPageIndex=" + mQueryPageIndex);
                            queryRecommend(mQueryPageIndex);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        mWaitView = (View)view.findViewById(R.id.fragment_recommend_wait_view);
    }
    
    private void adapterNotifyDataSetChanged() {
        if(mAppRecommendAdapter == null) {
            mAppRecommendAdapter = new AppRecommendAdapter(mActivity, AppRecommendThread.getList());
            mGridView.setAdapter(mAppRecommendAdapter);
        }
        mAppRecommendAdapter.notifyDataSetChanged();
    }
    
    private class BroadcastReceiverListener extends MessageListenerBase {
        @Override
        public void onPackageRemoved(String packName) {
            super.onPackageRemoved(packName);
            mRecommendHandler.obtainMessage(MSG_TYPE_EVT_PACKAGE_DEL, packName).sendToTarget();
        }
    }
    
    private class DownloadServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Debug.d(TAG, "onServiceConnected");
            mDownloadService = ((DownlaodServiceBinder)service).getService();
            mDownloadServiceListener = new DownloadServiceListener();
            mDownloadService.registerListener(mDownloadServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Debug.d(TAG, "onServiceDisconnected");
        }
        
    }
    
    private class DownloadServiceListener implements OnServiceListener {

        @Override
        public void onToastDownloadRunning(String packname) {
            // TODO Auto-generated method stub
            Debug.d(TAG, "onToastDownloadRunning:" + packname);
            for(AppRecommendInfo info : AppRecommendThread.getList()) {
                if(info.getmPkgName().equals(packname)) {
                    mRecommendHandler.obtainMessage(MSG_TYPE_EVT_TOAST_RUNNING, info.getmAppName()).sendToTarget();
                    break;
                }
            }
        }

        @Override
        public void onToastInstallSuccess(String packname) {
            // TODO Auto-generated method stub
            Debug.d(TAG, "onToastInstallSuccess" + packname);
            for(AppRecommendInfo info : AppRecommendThread.getList()) {
                if(info.getmPkgName().equals(packname)) {
                    mRecommendHandler.obtainMessage(MSG_TYPE_EVT_INSTALL_SUCCESS, info).sendToTarget();
                    break;
                }
            }
        }

        @Override
        public void onToastInstallFailure(String packname) {
            // TODO Auto-generated method stub
            Debug.d(TAG, "onToastInstallFailure" + packname);
            for(AppRecommendInfo info : AppRecommendThread.getList()) {
                if(info.getmPkgName().equals(packname)) {
                    mRecommendHandler.obtainMessage(MSG_TYPE_EVT_INSTALL_FAILURE, info).sendToTarget();
                    break;
                }
            }
        }

        @Override
        public void onUpdateProcess(String packname, int status, int percent) {
            for(AppRecommendInfo info : AppRecommendThread.getList()) {
                //only uninstalled app can update process
                if(info.getmPkgName().equals(packname) && info.ismIsInstall() == false) {
                    View viewItem = mGridView.getChildAt(info.getmPosition());
                    if(viewItem != null) {
                        DownloadProcessView processView = (DownloadProcessView)viewItem.findViewById(R.id.fragment_recommend_gridview_item_process_view);
                        if(status == DownloadService.DOWNLOAD_STATUS_RUNNING) {
                            processView.setProgress(percent);
                        } else if(status == DownloadService.DOWNLOAD_STATUS_OVER) {
                            processView.setProgress(100);
                        } else if(status == DownloadService.DOWNLOAD_STATUS_PAUSE) {
                            processView.setProgress(percent);
                        } else if(status == DownloadService.DOWNLOAD_STATUS_FAILED) {
                            processView.setProgress(percent);
                        } else if(status == DownloadService.DOWNLOAD_STATUS_PENDING) {
                            processView.setProgress(percent);
                        }
                        //Debug.d(TAG, "packname:" + packname + " status:" + status + " percent:" + percent);
                        adapterNotifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onToastDownloadStart(String packname) {
            // TODO Auto-generated method stub
            for(AppRecommendInfo info : AppRecommendThread.getList()) {
                if(info.getmPkgName().equals(packname)) {
                    mRecommendHandler.obtainMessage(MSG_TYPE_EVT_TOAST_DOWNLOAD_START, info.getmAppName()).sendToTarget();
                    break;
                }
            }
        }   
    }
    
    private class AppRecommendHandler extends Handler {
        
        @Override
        public void handleMessage(android.os.Message msg) {
            String packname = null;
            String toastStr = null;
            AppRecommendInfo recommendInfo = null;
            View itemView = null;
            switch (msg.what) {
                case MSG_TYPE_EVT_QUREY_FAILURE:
                    mGridView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    adapterNotifyDataSetChanged();
                    toastStr = mActivity.getResources().getString(R.string.recommend_load_failure);
                    showToast(toastStr);
                    break;
                case MSG_TYPE_EVT_QUREY_SUCCESS:
                    mGridView.setVisibility(View.VISIBLE);
                    mWaitView.setVisibility(View.GONE);
                    adapterNotifyDataSetChanged();
                    break;
                case MSG_TYPE_EVT_SYNC_INFOLIST:
                    adapterNotifyDataSetChanged();
                    break;
                case MSG_TYPE_EVT_TOAST_RUNNING:
                    toastStr = (String)msg.obj + mActivity.getResources().getString(R.string.app_is_updating);
                    showToast(toastStr);
                    break;
                    
                case MSG_TYPE_EVT_TOAST_DOWNLOAD_START:
                    toastStr = (String)msg.obj + mActivity.getResources().getString(R.string.act_recommend_start_update);
                    showToast(toastStr);
                    break;
                    
                case MSG_TYPE_EVT_INSTALL_SUCCESS:
                    recommendInfo  = (AppRecommendInfo)msg.obj;
                    itemView = mGridView.getChildAt(recommendInfo.getmPosition());
                    if(itemView != null) {
                        DownloadProcessView processView = (DownloadProcessView)itemView.findViewById(R.id.fragment_recommend_gridview_item_process_view);
                        processView.setProgress(100);
                        adapterNotifyDataSetChanged();
                        toastStr = recommendInfo.getmAppName() + mActivity.getResources().getString(R.string.install_app_success);
                        showToast(toastStr);
                    }
                    break;
                
                case MSG_TYPE_EVT_INSTALL_FAILURE:
                    recommendInfo  = (AppRecommendInfo)msg.obj;
                    itemView = mGridView.getChildAt(recommendInfo.getmPosition());
                    if(itemView != null) {
                        DownloadProcessView processView = (DownloadProcessView)itemView.findViewById(R.id.fragment_recommend_gridview_item_process_view);
                        processView.setProgress(0);
                        adapterNotifyDataSetChanged();
                        toastStr = recommendInfo.getmAppName() + mActivity.getResources().getString(R.string.install_app_failure);
                        showToast(toastStr);
                    }
                    break;
                    
                case MSG_TYPE_EVT_PACKAGE_DEL:
                    packname = (String)msg.obj;
                    for(AppRecommendInfo info : AppRecommendThread.getList()) {
                        if(info.getmPkgName().equals(packname)) {
                            View viewItem = mGridView.getChildAt(info.getmPosition());
                            if(viewItem != null) {
                                DownloadProcessView processView = (DownloadProcessView)viewItem.findViewById(R.id.fragment_recommend_gridview_item_process_view);
                                processView.setProgress(0);
                                adapterNotifyDataSetChanged();
                            }
                        }
                    }
                    break;
            }
        }
    }
}


