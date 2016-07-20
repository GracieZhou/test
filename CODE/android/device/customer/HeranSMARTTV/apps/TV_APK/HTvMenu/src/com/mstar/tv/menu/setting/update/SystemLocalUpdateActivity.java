package com.mstar.tv.menu.setting.update;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RecoverySystem.ProgressListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mstar.android.storage.MStorageManager;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.setting.util.Tools;

public class SystemLocalUpdateActivity extends Activity {

    private final static String TAG = "MSettings.SystemLocalUpdateActivity";

    private static final int CHECK_STORAGE = 0;

    private static final int CHECK_NEW_VERSION = 1;

    private static final int CHECK_UPDATE_ERROR = 2;

    private static final int UPDATE_SUCCESS = 3;

    private static final int UPDATE_PROGRESS = 4;

    protected static final int MSG_SELECT_UPDATE = 5;

    private static final int CHECK_STORAGE_COUNT = 10;

    private Button mUpdateButton;

    private TextView mUpdateInfoText;

    private String mUpdateInfo;

    private int mRetryCount;

    private boolean mHasNewVerison = false;

    private boolean mIsUpdating = false;

    protected File mUpdateFile;

    private ProgressBar mProgressBar;

    private TextView mCurrentProgressText;

    private LinearLayout mLinearLayout;

    private int mCurrentProgress;

    private StorageManager mStorageManager;

    protected List<Map<String, String>> mMountedVolumes = new ArrayList<Map<String, String>>();

    protected List<File> mScanFiles = new ArrayList<File>();

    protected boolean mSelectedFlag = false;

    protected Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CHECK_STORAGE:
                    checkStorage();
                    break;
                case CHECK_NEW_VERSION:
                    mUpdateInfo += getString(R.string.check_new_version) + "\n";
                    mUpdateInfoText.setText(mUpdateInfo);
                    scanUpdateFile();
                    break;
                case CHECK_UPDATE_ERROR:
                    mUpdateInfo += getString(R.string.check_failure) + "\n";
                    mUpdateButton.setEnabled(true);
                    mUpdateButton.setText(getString(R.string.exit));
                    mUpdateButton.setBackgroundResource(R.drawable.left_bg);
                    mUpdateInfoText.setText(mUpdateInfo);
                    break;
                case UPDATE_SUCCESS:
                    mUpdateInfo += getString(R.string.updated);
                    mUpdateInfoText.setText(mUpdateInfo);
                    break;
                case UPDATE_PROGRESS:
                    mCurrentProgressText.setText(mCurrentProgress + "%");
                    mProgressBar.setProgress(mCurrentProgress);
                    if (mCurrentProgress == 100) {
                        mUpdateInfo += getString(R.string.check_success);
                        mUpdateInfoText.setText(mUpdateInfo);
                        mIsUpdating = false;
                    }
                    break;
                case MSG_SELECT_UPDATE:
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mIsUpdating = true;
                    mUpdateButton.setEnabled(false);
                    mUpdateButton.setBackgroundResource(R.drawable.one_px);
                    mUpdateInfo += getString(R.string.check_package) + "\n";
                    mUpdateInfoText.setText(mUpdateInfo);

                    // start to upgrade
                    new UpdateSystemThread().start();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_local_update);
        mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        // init control
        findViews();
        //
//        Message msg = myHandler.obtainMessage();
//        msg.what = CHECK_STORAGE;
//        myHandler.sendMessage(msg);

        // register control event
        registerListeners();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        // register storage change event
        registerReceiver(storageChangeReceiver, intentFilter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mIsUpdating) {
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_TV_INPUT:
                mUpdateButton.performClick();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_CHANNEL_UP:
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        finish();
        unregisterReceiver(storageChangeReceiver);
        super.onDestroy();
    }

    private void findViews() {
        mUpdateButton = (Button) findViewById(R.id.local_immediate);
        mUpdateInfoText = (TextView) findViewById(R.id.local_update_info);
        TextView title = (TextView) findViewById(R.id.local_updateTitle);
        title.setText(getString(R.string.system_local_update));
        mUpdateInfo = getString(R.string.current_version) + Tools.getSystemVersion() + "\n";
        mUpdateInfoText.setText(mUpdateInfo);
        mUpdateButton.setEnabled(true);
        mUpdateButton.setBackgroundResource(R.drawable.left_bg);
        mUpdateButton.setText(getString(R.string.check_update));
        mCurrentProgressText = (TextView) findViewById(R.id.local_current_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLinearLayout = (LinearLayout) findViewById(R.id.local_show_progress);
    }

    private void registerListeners() {
        mUpdateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mUpdateButton.getText().equals(getString(R.string.check_update))) {
                    Message msg = myHandler.obtainMessage();
                    msg.what = CHECK_STORAGE;
                    myHandler.sendMessage(msg);
                    mUpdateInfo += getString(R.string.sdcard) + "\n";
                    mUpdateInfoText.setText(mUpdateInfo);
                    mUpdateButton.setText(getString(R.string.check_update_ing));
                    return ;
                }
                if (mHasNewVerison) {
                    if (mScanFiles.size() == 1) {
                        mUpdateFile = mScanFiles.get(0);
                        mLinearLayout.setVisibility(View.VISIBLE);
                        mIsUpdating = true;
                        mUpdateButton.setEnabled(false);
                        mUpdateButton.setBackgroundResource(R.drawable.one_px);
                        mUpdateInfo += getString(R.string.check_package) + "\n";
                        mUpdateInfoText.setText(mUpdateInfo);
                        new UpdateSystemThread().start();
                    } else if (mScanFiles.size() > 1) {
                        Log.d(TAG, "SelectedUpdateDialog");
//                        mSelectUpdateDialog = new SelectUpdateDialog(SystemLocalUpdateActivity.this);
//                        mSelectUpdateDialog.show();
                    }
                } else {
                    finish();
                }
            }
        });
    }

    private void checkStorage() {
        mRetryCount++;
        boolean hasStorage = hasStorageDevice();
        if (hasStorage) {
            myHandler.sendEmptyMessage(CHECK_NEW_VERSION);
        } else {
            if (mRetryCount < CHECK_STORAGE_COUNT) {
                myHandler.sendEmptyMessageDelayed(CHECK_STORAGE, 200);
            } else {
                mUpdateInfo += getString(R.string.no_sdcard) + "\n";
                mUpdateInfoText.setText(mUpdateInfo);
                mUpdateButton.setText(getString(R.string.exit));
                mUpdateButton.setEnabled(true);
                mUpdateButton.setBackgroundResource(R.drawable.left_bg);
            }
        }
    }

    private boolean hasStorageDevice() {
        mMountedVolumes.clear();
        MStorageManager sm = MStorageManager.getInstance(SystemLocalUpdateActivity.this);
        for (StorageVolume volume : mStorageManager.getVolumeList()) {
            String path =volume.getPath();
            if (Environment.MEDIA_MOUNTED.equals(sm.getVolumeState(path))) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("volume_path", path);

                String label = sm.getVolumeLabel(path);
                if (TextUtils.isEmpty(label)) {
                    if (Environment.getExternalStorageDirectory().getAbsolutePath().equals(path)) {
                        map.put("volume_lable", getString(R.string.sdcard_lable));
                    } else {
                        map.put("volume_lable", getString(R.string.mobile_stoarge_device));
                    }
                } else {
                    map.put("volume_lable", label);
                }

                mMountedVolumes.add(map);
            }
        }
        // do not have any storage
        if (mMountedVolumes.size() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    private void scanUpdateFile() {
        mScanFiles.clear();
        for (Map<String, String> map : mMountedVolumes) {
            mUpdateFile = new File(map.get("volume_path"), "update_signed.zip");
            if (mUpdateFile.exists()) {
                mScanFiles.add(mUpdateFile);
            }
        }

        if (mScanFiles.size() > 0) {
            mHasNewVerison = true;
            mUpdateInfo += getString(R.string.check_updatezip) + "\n";
            mUpdateInfoText.setText(mUpdateInfo);
            mUpdateButton.setText(getString(R.string.update));
        } else {
            mHasNewVerison = false;
            mUpdateInfo += getString(R.string.no_update_file) + "\n";
            mUpdateInfoText.setText(mUpdateInfo);
            mUpdateButton.setText(getString(R.string.exit));
        }
        mUpdateButton.setEnabled(true);
        mUpdateButton.setBackgroundResource(R.drawable.left_bg);
    }

    private boolean verifyPackage() {
        // recovery listener
        RecoverySystem.ProgressListener progressListener = new ProgressListener() {

            @Override
            public void onProgress(int progress) {
                mCurrentProgress = progress;
                myHandler.sendEmptyMessage(UPDATE_PROGRESS);
            }
        };

        // call system interface to update system
        try {
            RecoverySystem.verifyPackage(mUpdateFile, progressListener, null);
            RecoverySystem.installPackage(this, mUpdateFile);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "verifyPackage exception, " + e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    private void updateSystem() {
        mHasNewVerison = false;
        if (verifyPackage()) {
            myHandler.sendEmptyMessage(UPDATE_SUCCESS);
        } else {
            myHandler.sendEmptyMessage(CHECK_UPDATE_ERROR);
        }
    }

    private BroadcastReceiver storageChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action, " + action);
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                hasStorageDevice();
                scanUpdateFile();

                mUpdateInfo += getString(R.string.sdcard_insert) + "\n";
                mUpdateInfoText.setText(mUpdateInfo);
            } else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
//                if (mSelectUpdateDialog != null) {
//                    mSelectUpdateDialog.dismiss();
//                }
                mUpdateInfo += getString(R.string.sdcard_remove) + "\n";
                mUpdateInfoText.setText(mUpdateInfo);
                mUpdateButton.setEnabled(true);

                hasStorageDevice();
                scanUpdateFile();
                if (mScanFiles.size() < 1) {
                    mUpdateButton.setText(getString(R.string.exit));
                } else {
                    mUpdateButton.setText(getString(R.string.update));
                }
            }
        }
    };

    private class UpdateSystemThread extends Thread {

        @Override
        public void run() {
            super.run();

            updateSystem();
        }
    }

}
