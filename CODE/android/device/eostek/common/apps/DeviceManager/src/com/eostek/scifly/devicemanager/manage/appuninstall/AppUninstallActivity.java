
package com.eostek.scifly.devicemanager.manage.appuninstall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.receiver.MessageListenerBase;
import com.eostek.scifly.devicemanager.receiver.MessageReceiver;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.Util;

public class AppUninstallActivity extends Activity implements OnItemClickListener {

    private static final String TAG = AppUninstallActivity.class.getSimpleName();

    private final static int QUREY_APK_LIST_SYNC = 0;
    
    private TextView mTvUninstallTip;

    private GridView mGridView;

    private AppUninstallAdapter mAdapter;

    private List<AppUninstallInfo> mInstalledAppInfoList = new ArrayList<AppUninstallInfo>();

    private PackageManager mPackageManager;

    private BroadcastReceiverListener mBroadcastReceiverListener;
    
    private long mApkSize;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.act_manage_uninstall);
        initViews();
        
        mBroadcastReceiverListener = new BroadcastReceiverListener();
        MessageReceiver.addMessageListener(mBroadcastReceiverListener);
        
        initValues();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageReceiver.removeMessageListener(mBroadcastReceiverListener);
    }

    private void initViews() {
        mGridView = (GridView) findViewById(R.id.gridview);
        mTvUninstallTip = (TextView)findViewById(R.id.tv_no_app_uninstall);
        mGridView.setSmoothScrollbarEnabled(true);
        mGridView.setSelector(android.R.color.transparent);
        mGridView.setOnItemClickListener(this);
    }

    private void initValues() {
        mPackageManager = getPackageManager();
        
        getInstalledAppInfo();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        AppUninstallInfo info = mInstalledAppInfoList.get(arg2);
        String packageName = info.getmPkgName();
        // uninstall apk
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        startActivity(intent);
    }

    private void getInstalledAppInfo() {

        // complete query unused apk .
        List<ApplicationInfo> listAppcations = mPackageManager
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo info : listAppcations) {

            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // system app, skip it!
                continue;
            }
            if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                // used to be a system app, skip it!
                continue;
            }
            if (info.packageName.equals(this.getPackageName())) {
                continue;
            }
            final String appName = mPackageManager.getApplicationLabel(info).toString();
            final String packageName = info.packageName;
            final Drawable icon = mPackageManager.getApplicationIcon(info);
            mPackageManager.getPackageSizeInfo(packageName, new IPackageStatsObserver.Stub() {
                public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                    Debug.d(TAG,  "packageName:" + packageName + " codeSize:" + stats.codeSize + " dataSize:" + stats.dataSize + " cacheSize:" + stats.cacheSize);
                    mApkSize = stats.codeSize + stats.dataSize + stats.cacheSize;
                    
                    AppUninstallInfo unusedInfo = new AppUninstallInfo();
                    unusedInfo.setmDrawable(icon);
                    unusedInfo.setmName(appName);
                    unusedInfo.setmPkgName(packageName);
                    unusedInfo.setmSize(Util.sizeToString(mApkSize));
                    mInstalledAppInfoList.add(unusedInfo);
                    
                    mHandler.sendEmptyMessage(QUREY_APK_LIST_SYNC);
                }
            });
        }
    }
    
    private void adapterNotifyDataSetChanged() {
        if(mAdapter == null) {
            mAdapter = new AppUninstallAdapter(AppUninstallActivity.this, mInstalledAppInfoList);
            mGridView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
    }

    private class BroadcastReceiverListener extends MessageListenerBase {
        @Override
        public void onPackageRemoved(String packName) {
            super.onPackageRemoved(packName);
            
            for(AppUninstallInfo item : mInstalledAppInfoList) {
                if(item.getmPkgName().equals(packName)) {
                    mInstalledAppInfoList.remove(item);
                    break;
                }
            }

            adapterNotifyDataSetChanged();
            
            if(mInstalledAppInfoList.size() == 0)
                mTvUninstallTip.setVisibility(View.VISIBLE);
            else
                mTvUninstallTip.setVisibility(View.GONE);
        }
    }
    
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case QUREY_APK_LIST_SYNC:
                    mGridView.setVisibility(View.VISIBLE);
                    
                    adapterNotifyDataSetChanged();
                    
                    if(mInstalledAppInfoList.size() == 0)
                        mTvUninstallTip.setVisibility(View.VISIBLE);
                    else
                        mTvUninstallTip.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        };
    };
}

