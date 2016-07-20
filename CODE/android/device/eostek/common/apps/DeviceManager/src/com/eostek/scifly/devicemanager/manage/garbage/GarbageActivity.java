
package com.eostek.scifly.devicemanager.manage.garbage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.DeviceManager;
import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.manage.appuninstall.AppUninstallActivity;
import com.eostek.scifly.devicemanager.manage.garbage.BigFileActivity;
import com.eostek.scifly.devicemanager.manage.garbage.listener.ClearGarbageTaskListener;
import com.eostek.scifly.devicemanager.manage.garbage.listener.ScanAdsTaskListener;
import com.eostek.scifly.devicemanager.manage.garbage.listener.ScanApkTaskListener;
import com.eostek.scifly.devicemanager.manage.garbage.listener.ScanBigFileTaskListener;
import com.eostek.scifly.devicemanager.manage.garbage.listener.ScanCacheTaskListener;
import com.eostek.scifly.devicemanager.manage.garbage.listener.ScanResidueTaskListener;
import com.eostek.scifly.devicemanager.manage.garbage.task.ClearGarbageTask;
import com.eostek.scifly.devicemanager.manage.garbage.task.ScanAdsTask;
import com.eostek.scifly.devicemanager.manage.garbage.task.ScanApkTask;
import com.eostek.scifly.devicemanager.manage.garbage.task.ScanBigFileTask;
import com.eostek.scifly.devicemanager.manage.garbage.task.ScanCacheTask;
import com.eostek.scifly.devicemanager.manage.garbage.task.ScanResidueTask;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.Util;

import java.util.ArrayList;
import java.util.List;


public class GarbageActivity extends Activity implements OnClickListener{

    private static final String TAG = GarbageActivity.class.getSimpleName();

    public static final int EVENT_TYPE_SCAN_RUNNING = 0;
    public static final int EVENT_TYPE_SCAN_NO_FOUND = 1;
    public static final int EVENT_TYPE_CLEAN_READY = 2;
    public static final int EVENT_TYPE_CLEAN_RUNNING = 3;
    public static final int EVENT_TYPE_CLEAN_DONE = 4;
    
    private int mEventType = EVENT_TYPE_SCAN_RUNNING;

    private ProgressBar mPbIcon;
    private ImageView mIvIcon;
    private TextView mTvAction;
    private Button mBtnAction;
    private TextView mTvStatus;
    private LinearLayout mLlAction;
    
    private long totalscansize;
    
    //Cache Files
    private TextView mTvCache;
    private List<String> cachefiles;
    private Long cacheSize;
    private boolean scanCacheDone;

    //Residue Files
    private TextView mTvResidue;
    private List<String> residueFiles;
    private Long residueSize;
    private boolean scanResidueDone;

    //Ad Files
    private TextView mTvAdFile;
    private List<String> adsfiles;
    private Long adsFileSize;
    private boolean scanAdsFileDone;

    //Useless APK
    private CheckBox mCbApkFile;
    private TextView mTvApkFile;
    private List<String> apkfiles;
    private Long apkFileSize;
    private boolean scanApkFileDone;
    private LinearLayout mLayoutApkFile;

    // Big Files
    private CheckBox mCbBigFile;
    private TextView mTvBigFile;
    private List<String> bigFiles;
    private Long bigFileSize;
    private boolean scanBigFileDone;
    private LinearLayout mLayoutBigFile;
    

    // Unused APP
    private TextView mTvUnusedApp;
    private Long unusedAppSize;
    private LinearLayout mLayoutUnsued;
    
    //Garbage Task
    private int task1;
    private int task2;
    private int task3;
    private int task4;
    private int task5;
    private int cleartask;
    
    private boolean isScanDone;
    
    private FileCollection mFileCollection = new FileCollection();
    
    private List<String> mListPkgName = new ArrayList<String>();
    
    private ArrayList<Integer> keyQueue;

    private static final String toSystemMonitor = String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT) + String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP) + String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT) + String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP);

    @Override
    protected void onCreate(Bundle arg0) {
        Debug.d(TAG, "onCreate");
        super.onCreate(arg0);
        setContentView(R.layout.act_manage_garbage);
        findView();
        setListener();
    }
    
    @Override
    protected void onResume() {
        mEventType = EVENT_TYPE_SCAN_RUNNING;
        setFuncView();
        super.onResume();
        
    }

    private void findView() {
        mBtnAction = (Button) findViewById(R.id.act_manage_garbage_btn_clean);
        mPbIcon = (ProgressBar)findViewById(R.id.act_manage_garbage_pb_icon);
        mIvIcon = (ImageView)findViewById(R.id.act_manage_garbage_iv_icon);
        mTvAction = (TextView)findViewById(R.id.act_manage_garbage_tv_clean_action);
        mTvStatus = (TextView)findViewById(R.id.act_manage_garbage_tv_clean_status);
        
        mLlAction = (LinearLayout)findViewById(R.id.act_manage_garbage_ll_clean_action);
        
        
        

        mTvCache = (TextView) findViewById(R.id.tv_summary_cache);
        mTvCache.setText(R.string.act_garbage_tv_scan_waiting);

        mTvResidue = (TextView) findViewById(R.id.tv_summary_uninstall);
        mTvResidue.setText(R.string.act_garbage_tv_scan_waiting);

        mTvAdFile = (TextView) findViewById(R.id.tv_summary_ads);
        mTvAdFile.setText(R.string.act_garbage_tv_scan_waiting);

        mCbApkFile = (CheckBox) findViewById(R.id.cb_apk_file);
        mTvApkFile = (TextView) findViewById(R.id.tv_summary_apk);
        mTvApkFile.setText(R.string.act_garbage_tv_scan_waiting);
        mLayoutApkFile = (LinearLayout)findViewById(R.id.act_manage_garbage_apk_layout);
        mLayoutApkFile.setOnClickListener(this);

        mCbBigFile = (CheckBox) findViewById(R.id.cb_big_file);
        mTvBigFile = (TextView) findViewById(R.id.tv_summary_big_file);
        mTvBigFile.setText(R.string.act_garbage_tv_scan_waiting);
        mLayoutBigFile = (LinearLayout)findViewById(R.id.act_manage_garbage_bigfile_layout);
        mLayoutBigFile.setOnClickListener(this);

        mTvUnusedApp = (TextView) findViewById(R.id.tv_summary_unused_apk);
        mTvUnusedApp.setText(R.string.act_garbage_tv_clean_no_need);
        mLayoutUnsued = (LinearLayout)findViewById(R.id.act_manage_garbage_unused_layout);

        isScanDone = false;
        scanAdsFileDone = false;
        scanApkFileDone = false;
        scanCacheDone = false;
        scanResidueDone = false;
        scanBigFileDone = false;
    }
    
    private void setListener() {
        mBtnAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View paramView) {
                switch (mEventType) {

                    case EVENT_TYPE_SCAN_NO_FOUND:
                        finish();
                        break;
                    case EVENT_TYPE_CLEAN_READY:
                        mEventType = EVENT_TYPE_CLEAN_RUNNING;
                        setFuncView();
                        break;
                    case EVENT_TYPE_CLEAN_DONE:
                        finish();
                        break;

                    default:
                        break;
                }
            }
        });
    }
    
    private void setFuncView() {
        if(mEventType == EVENT_TYPE_SCAN_RUNNING) {
            mTvStatus.setText(R.string.act_garbage_tv_scan_running);  
            
            mBtnAction.setVisibility(View.GONE);
            mPbIcon.setVisibility(View.VISIBLE);
            mIvIcon.setVisibility(View.GONE);
            mLlAction.setVisibility(View.GONE);
            mTvStatus.setVisibility(View.VISIBLE);
            
            mLayoutApkFile.setFocusable(false);
            mLayoutBigFile.setFocusable(false);
            mLayoutUnsued.setFocusable(false);
                                 
            task1 = DeviceManager.getInstance().startTask(new ScanCacheTask(new ScanCacheTaskListener(handler), this));
            task2 = DeviceManager.getInstance().startTask(new ScanResidueTask(new ScanResidueTaskListener(handler), this));
            task3 = DeviceManager.getInstance().startTask(new ScanAdsTask(new ScanAdsTaskListener(handler), this));
            task4 = DeviceManager.getInstance().startTask(new ScanApkTask(new ScanApkTaskListener(handler), this));
            task5 = DeviceManager.getInstance().startTask(new ScanBigFileTask(new ScanBigFileTaskListener(handler), this, Constants.HALF_A_HUNDRED_MB));
        
        } else if(mEventType == EVENT_TYPE_SCAN_NO_FOUND){
            mTvAction.setText(R.string.act_garbage_tv_scan_no_found);
            mBtnAction.setText(R.string.act_garbage_tv_confirm);
            
            mIvIcon.setImageResource(R.drawable.garbage_scan_no_found);
            
            mBtnAction.setVisibility(View.VISIBLE);
            mBtnAction.requestFocus();
            mLayoutApkFile.setFocusable(true);
            mLayoutBigFile.setFocusable(true);
            mLayoutUnsued.setFocusable(true);
            
            mPbIcon.setVisibility(View.GONE);
            mIvIcon.setVisibility(View.VISIBLE);
            mLlAction.setVisibility(View.VISIBLE);
            mTvStatus.setVisibility(View.GONE);
            
        } else if(mEventType == EVENT_TYPE_CLEAN_READY){
            mTvAction.setText(String.format(getString(R.string.act_garbage_tv_scan_result), Util.sizeToString(totalscansize)));
            mBtnAction.setText(R.string.act_garbage_tv_clean_now);
            
            mIvIcon.setImageResource(R.drawable.garbage_clean_ready);
            
            mBtnAction.setVisibility(View.VISIBLE);
            mBtnAction.requestFocus();
            mLayoutApkFile.setFocusable(true);
            mLayoutBigFile.setFocusable(true);
            mLayoutUnsued.setFocusable(true); 
            
            mPbIcon.setVisibility(View.GONE);
            mIvIcon.setVisibility(View.VISIBLE);
            mLlAction.setVisibility(View.VISIBLE);
            mTvStatus.setVisibility(View.GONE);

        } else if(mEventType == EVENT_TYPE_CLEAN_RUNNING){
            mTvAction.setText(R.string.act_garbage_tv_clean_running);  
            mIvIcon.setImageResource(R.drawable.garbage_clean_running);
            
            mBtnAction.setVisibility(View.GONE);
            mPbIcon.setVisibility(View.GONE);
            mIvIcon.setVisibility(View.VISIBLE);
            mLlAction.setVisibility(View.GONE);
            mTvStatus.setVisibility(View.VISIBLE);
            
            if (cachefiles != null && cacheSize > 0) {
                for (String f : cachefiles) {
                    mFileCollection.add(f);
                }
            }

            if (residueFiles != null && residueSize > 0) {
                for (String f : residueFiles) {
                    mFileCollection.add(f);
                }
            }
            if (adsfiles != null && adsFileSize > 0) {
                for (String f : adsfiles) {
                    mFileCollection.add(f);
                }
            }
            if (apkfiles != null && apkFileSize > 0 && mCbApkFile.isChecked()) {
                for (String f : apkfiles) {
                    mFileCollection.add(f);
                }
            }
            if (bigFiles != null && bigFileSize > 0 && mCbBigFile.isChecked()) {
                for (String f : bigFiles) {
                    mFileCollection.add(f);
                }
            }
            
            ClearGarbageTask ct = new ClearGarbageTask(new ClearGarbageTaskListener(handler), GarbageActivity.this);
            ct.setCollection(mFileCollection);
            cleartask = DeviceManager.getInstance().startTask(ct);
            
        } else if(mEventType == EVENT_TYPE_CLEAN_DONE){
            long cleanSize = 0;
            if(mCbBigFile.isChecked()) {
                cleanSize = totalscansize;
            } else {
                cleanSize = totalscansize - bigFileSize;
            }
            if(mCbApkFile.isChecked()) {
                
            } else {
                cleanSize = cleanSize - apkFileSize;
            }
            mTvAction.setText(String.format(getString(R.string.act_garbage_tv_clean_result), Util.sizeToString(cleanSize)));
            mBtnAction.setText(R.string.act_garbage_tv_confirm);
            
            if (cachefiles != null && cacheSize > 0) {
                mTvCache.setText(R.string.act_garbage_tv_clean_complete);
            }
            if (residueFiles != null && residueSize > 0) {
                mTvResidue.setText(R.string.act_garbage_tv_clean_complete);
            }
            if (adsfiles != null && adsFileSize > 0) {
                mTvAdFile.setText(R.string.act_garbage_tv_clean_complete);
            }
            if (apkfiles != null && apkFileSize > 0 && mCbApkFile.isChecked()) {
                mTvApkFile.setText(R.string.act_garbage_tv_clean_complete);
            }
            if (bigFiles != null && bigFileSize > 0 && mCbBigFile.isChecked()) {
                mTvBigFile.setText(R.string.act_garbage_tv_clean_complete);
            }
            
            mIvIcon.setImageResource(R.drawable.garbage_clean_success);
            
            mBtnAction.setVisibility(View.VISIBLE);
            mBtnAction.requestFocus();
            mLayoutBigFile.setFocusable(true);
            mLayoutUnsued.setFocusable(true); 
            
            mPbIcon.setVisibility(View.GONE);
            mIvIcon.setVisibility(View.VISIBLE);
            mLlAction.setVisibility(View.VISIBLE);
            mTvStatus.setVisibility(View.GONE);
        }
    }
    
    private void nextEvent() {
        isScanDone = scanCacheDone && scanResidueDone && scanAdsFileDone && scanApkFileDone && scanBigFileDone;
        if (isScanDone) {
            totalscansize = cacheSize + residueSize + adsFileSize + apkFileSize  + bigFileSize;
            if (totalscansize == 0) {
                mEventType = EVENT_TYPE_SCAN_NO_FOUND;
                setFuncView();
            } else {
                mEventType = EVENT_TYPE_CLEAN_READY;
                setFuncView();
            }
        }
    }
    
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constants.GARBAGE_MSG_CACHE_AVAILABLE:              
                    cacheSize = msg.getData().getLong("size");                  
                    cachefiles = msg.getData().getStringArrayList("files");     
                    if (cacheSize == 0) {
                        mTvCache.setText(R.string.act_garbage_tv_clean_no_need);
                    } else {
                        mTvCache.setText(Util.sizeToString(cacheSize));
                    }
                    scanCacheDone = true;
                    nextEvent();
                    break;
                    
                case Constants.GARBAGE_MSG_UNINSTALL_AVAILABLE:          
                    residueSize = msg.getData().getLong("size");              
                    residueFiles = msg.getData().getStringArrayList("files"); 
                    if (residueSize == 0) {
                        mTvResidue.setText(R.string.act_garbage_tv_clean_no_need);
                    } else {
                        mTvResidue.setText(Util.sizeToString(residueSize));
                    }
                    scanResidueDone = true;
                    nextEvent();
                    break;
                    
                case Constants.GARBAGE_MSG_ADS_AVAILABLE:
                    adsFileSize = msg.getData().getLong("size");
                    adsfiles = msg.getData().getStringArrayList("files");
                    if (adsFileSize == 0) {
                        mTvAdFile.setText(R.string.act_garbage_tv_clean_no_need);
                    } else {
                        mTvAdFile.setText(Util.sizeToString(adsFileSize));
                    }
                    scanAdsFileDone = true;
                    nextEvent();
                    break;
                    
                case Constants.GARBAGE_MSG_APK_AVAILABLE:
                    apkFileSize = msg.getData().getLong("size");                    // 2.apksize:
                    apkfiles = msg.getData().getStringArrayList("files");       // 3.apkfiles:
                    if (apkFileSize == 0) {
                        mTvApkFile.setText(R.string.act_garbage_tv_clean_no_need);
                    } else {
                        mTvApkFile.setText(Util.sizeToString(apkFileSize));
                    }
                    scanApkFileDone = true;
                    nextEvent();
                    break;
                    
                case Constants.GARBAGE_MSG_BIG_FILE_AVAILABLE:
                    bigFileSize = msg.getData().getLong("size");                // 2.apksize:
                    bigFiles = msg.getData().getStringArrayList("files");       // 3.apkfiles:
                    if (bigFileSize == 0) {
                        mTvBigFile.setText(R.string.act_garbage_tv_clean_no_need);
                    } else {
                        mTvBigFile.setText(Util.sizeToString(bigFileSize));
                    }
                    scanBigFileDone = true;
                    nextEvent();
                    break;
                    
                case Constants.GARBAGE_MSG_CLEAN_COMPLETED:
                    mEventType = EVENT_TYPE_CLEAN_DONE;
                    setFuncView();
                    break;

                    
                default:
                    break;
            }
        };
    };

    private String intArrayListToString(ArrayList<Integer> al) {
        String str = "";
        for (int i = 0; i < al.size(); ++i) {
            str += al.get(i).toString();
        }
        return str;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyQueue == null) {
            keyQueue = new ArrayList<Integer>();
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                keyQueue.add(event.getKeyCode());
                if(keyQueue.size() == 8){
                    if (intArrayListToString(keyQueue).equals(toSystemMonitor)) {
                        keyQueue.clear();
                        ComponentName component = new ComponentName("com.eostek.monitor", "com.eostek.monitor.MonitorActivity");
                        Intent intent = new Intent();
                        intent.setComponent(component);
                        intent.putExtra("fromLauncher", true);
                        startActivity(intent);
                    }
                    else{
                        keyQueue.remove(0);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(mLayoutApkFile.isFocused()) {
                    Intent mIntent = new Intent(GarbageActivity.this, ApkFileActivity.class);
                    startActivity(mIntent);
                }else if(mLayoutBigFile.isFocused()) {
                    Intent mIntent = new Intent(GarbageActivity.this, BigFileActivity.class);
                    startActivity(mIntent);
                } else if(mLayoutUnsued.isFocused()) {
                    Intent mIntent = new Intent(GarbageActivity.this, AppUninstallActivity.class);
                    startActivity(mIntent);
                } 
                break;
                
            default:
                keyQueue.clear();
                break;
        } 
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View arg0) {
        if(mLayoutBigFile.equals(arg0)) {
            if(mCbBigFile.isChecked()) {
                mCbBigFile.setChecked(false);
            } else {
                mCbBigFile.setChecked(true);
            }
        } else if(mLayoutApkFile.equals(arg0)) {
            if(mCbApkFile.isChecked()) {
                mCbApkFile.setChecked(false);
            } else {
                mCbApkFile.setChecked(true);
            }
        }
    }

}
