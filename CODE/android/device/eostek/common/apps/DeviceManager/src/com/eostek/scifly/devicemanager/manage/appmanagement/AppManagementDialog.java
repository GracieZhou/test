package com.eostek.scifly.devicemanager.manage.appmanagement;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.Util;

public class AppManagementDialog extends Dialog implements android.view.View.OnClickListener{

    private static final String TAG = AppManagementDialog.class.getSimpleName();
    
    private Context mContext;
    private Handler mHandler;
    private AppManagementInfo mAppInfo;
    
    private ImageView mImageIcon;
    private TextView mTvAppName;
    private TextView mTvAppVersion;
    private TextView mTvDiskTotal;
    private TextView mTvDiskCode;
    private TextView mTvDiskData;
    private TextView mTvCache;
    private Button mBtnNormalClearData;
    private Button mBtnNormalClearCache;
    private Button mBtnNormalUninstall;
    private Button mBtnNormalUpdate;
    private Button mBtnSystemClearData;
    private Button mBtnSystemClearCache;
    private LinearLayout mLayoutNormal;
    private LinearLayout mLayoutSystem;
    
    private ClearCacheObserver mClearCacheObserver;
    private ClearUserDataObserver mClearDataObserver;
    
    private long mLongCacheSize = 0;
    private long mLongCodeSize = 0;
    private long mLongDataSize = 0;
    private long mLongTotalSize = 0;

    private OnDialogListener mOnDialogListener;
    
    public interface OnDialogListener {
        public void onUpdate();
    }
    
    public void setOnDialogListener(OnDialogListener onDialogListener) {
        this.mOnDialogListener = onDialogListener;
    }
    
    public AppManagementDialog(Context context, Handler handler, AppManagementInfo info) {
        super(context, R.style.Scifly_Dialog);
        setContentView(R.layout.activity_management_dialog);
        this.mContext = context;
        this.mHandler = handler;
        this.mAppInfo = info;
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        Debug.d(TAG, "AppManagementDialog");
    }
    
    private void initViews() {
        mImageIcon = (ImageView)findViewById(R.id.act_management_dlg_icon);
        mTvAppName = (TextView)findViewById(R.id.act_management_dlg_name);
        mTvAppVersion = (TextView)findViewById(R.id.act_management_dlg_version);
        
        mTvDiskTotal = (TextView)findViewById(R.id.act_management_dlg_tv_disk_total);
        mTvDiskCode = (TextView)findViewById(R.id.act_management_dlg_tv_disk_app);
        mTvDiskData = (TextView)findViewById(R.id.act_management_dlg_tv_disk_data);
        mTvCache = (TextView)findViewById(R.id.act_management_dlg_tv_cache);
        
        mBtnNormalClearData = (Button)findViewById(R.id.act_management_dlg_btn_normal_app_clear_data);
        mBtnNormalClearCache = (Button)findViewById(R.id.act_management_dlg_btn_normal_app_clear_cache);
        mBtnNormalUninstall = (Button)findViewById(R.id.act_management_dlg_btn_normal_app_uninstall);
        mBtnNormalUpdate = (Button)findViewById(R.id.act_management_dlg_btn_normal_app_update);
        mBtnSystemClearData = (Button)findViewById(R.id.act_management_dlg_btn_system_app_clear_data);
        mBtnSystemClearCache = (Button)findViewById(R.id.act_management_dlg_btn_system_app_clear_cache);
    
        mLayoutNormal = (LinearLayout)findViewById(R.id.act_management_dlg_ll_normal_app);
        mLayoutSystem = (LinearLayout)findViewById(R.id.act_management_dlg_ll_system_app);
        
        if(mAppInfo.getmDrawable() != null)
            mImageIcon.setImageDrawable(mAppInfo.getmDrawable());
        else
            mImageIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_icon));
        
        if(mAppInfo.getmName() != null)
            mTvAppName.setText(mAppInfo.getmName());
            
        if(mAppInfo.getmVersionName() != null)
            mTvAppVersion.setText(String.format(mContext.getString(R.string.act_management_dlg_tv_version), mAppInfo.getmVersionName()));
        
        if(mAppInfo.getmSystemApp()) {
            mLayoutNormal.setVisibility(View.GONE);
            mLayoutSystem.setVisibility(View.VISIBLE);
        } else {
            if(mAppInfo.getmUpdateFlag()) {
                mBtnNormalUpdate.setVisibility(View.VISIBLE);
            } else {
                mBtnNormalUpdate.setVisibility(View.GONE);
            }
            mLayoutNormal.setVisibility(View.VISIBLE);
            mLayoutSystem.setVisibility(View.GONE);
        }
        
        mContext.getPackageManager().getPackageSizeInfo(mAppInfo.getmPkgName(), new IPackageStatsObserver.Stub() {
            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                mLongCacheSize = stats.cacheSize;
                mLongCodeSize = stats.codeSize;
                mLongDataSize = stats.dataSize;
                mLongTotalSize = stats.codeSize + stats.dataSize;
                
                mTvDiskCode.setText(Util.sizeToString(mLongCodeSize));
                mTvDiskData.setText(Util.sizeToString(mLongDataSize));
                mTvDiskTotal.setText(Util.sizeToString(mLongTotalSize));
                mTvCache.setText(Util.sizeToString(mLongCacheSize));
            }
        });
        
        mBtnNormalClearData.setOnClickListener(this);
        mBtnNormalClearCache.setOnClickListener(this);
        mBtnNormalUninstall.setOnClickListener(this);
        mBtnNormalUpdate.setOnClickListener(this);
        mBtnSystemClearData.setOnClickListener(this);
        mBtnSystemClearCache.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.d(TAG, "onCreate");
        initViews();        
    }
    
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.act_management_dlg_btn_normal_app_clear_cache:
            case R.id.act_management_dlg_btn_system_app_clear_cache:
                clearApplicationUserCache();
                break;
                
            case R.id.act_management_dlg_btn_normal_app_clear_data:
            case R.id.act_management_dlg_btn_system_app_clear_data:
                clearApplicationUserData();
                break;
                
            case R.id.act_management_dlg_btn_normal_app_uninstall:
                Uri uri = Uri.parse("package:" + mAppInfo.getmPkgName());
                Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                mContext.startActivity(intent);
                this.dismiss();
                break;
            case R.id.act_management_dlg_btn_normal_app_update:
                if(mOnDialogListener != null) {
                    mOnDialogListener.onUpdate();
                }
                break;
                
                default:
                    break;
        }
    }
    
    private void clearApplicationUserCache() {
        if(mClearCacheObserver == null) {
            mClearCacheObserver = new ClearCacheObserver();
        }
        mContext.getPackageManager().deleteApplicationCacheFiles(mAppInfo.getmPkgName(), mClearCacheObserver);
        mLongCacheSize = 0;
        mTvCache.setText(Util.sizeToString(mLongCacheSize));
    }
    
    private void clearApplicationUserData() {
        if(mClearDataObserver  == null) {
            mClearDataObserver = new ClearUserDataObserver();
        }
        ActivityManager am = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        boolean res = am.clearApplicationUserData(mAppInfo.getmPkgName(), mClearDataObserver);
        mLongCacheSize = 0;
        mLongDataSize = 0;
        mLongTotalSize = mLongCodeSize + mLongDataSize;
        mTvCache.setText(Util.sizeToString(mLongCacheSize));
        mTvDiskData.setText(Util.sizeToString(mLongDataSize));
        mTvDiskTotal.setText(Util.sizeToString(mLongTotalSize));
    }

    class ClearCacheObserver extends IPackageDataObserver.Stub {
        @Override
        public void onRemoveCompleted(final String packageName, final boolean succeeded) throws RemoteException {
            Debug.d(TAG, "ClearCacheObserver:onRemoveCompleted");
            
        }
    }
    
    class ClearUserDataObserver extends IPackageDataObserver.Stub {
        @Override
        public void onRemoveCompleted(final String packageName, final boolean succeeded) throws RemoteException {
            Debug.d(TAG, "ClearUserDataObserver:onRemoveCompleted");
            
            
        }
    }

}
