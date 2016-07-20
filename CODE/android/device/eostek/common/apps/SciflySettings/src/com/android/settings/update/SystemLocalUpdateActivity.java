//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.android.settings.update;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import scifly.storage.StorageManagerExtra;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.RecoverySystem.ProgressListener;
import android.os.SystemProperties;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class SystemLocalUpdateActivity extends Activity {

    private final static String TAG = "SystemLocalUpdateActivity";

    private static final int CHECK_STORAGE = 0;

    private static final int CHECK_NEW_VERSION = 1;

    private static final int CHECK_UPDATE_ERROR = 2;

    private static final int UPDATE_SUCCESS = 3;

    private static final int UPDATE_PROGRESS = 4;

    protected static final int MSG_SELECT_UPDATE = 5;

    private static final int CHECK_STORAGE_COUNT = 10;

    private TextView mTvCurrentVersion;

    private TextView mUpdateInfoText;

    private Button mUpdateButton;

    private int mRetryCount;

    private boolean mHasNewVerison = false;

    private boolean mIsUpdating = false;

    protected File mUpdateFile;

    private TextView mCurrentProgressText;

    private int mCurrentProgress;

    private SelectUpdateDialog mSelectUpdateDialog;

    protected List<Map<String, String>> mMountedVolumes = new ArrayList<Map<String, String>>();

    protected List<File> mScanFiles = new ArrayList<File>();

    protected boolean mSelectedFlag = false;

    private static final String LOCAL_FACVER = SystemProperties.get("ro.scifly.version.alias", "---------------");

    private static final String LOCAL_ROMVER = SystemProperties.get("ro.build.version.incremental", "---------------");

    @SuppressLint("HandlerLeak")
    protected Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CHECK_STORAGE:
                    checkStorage();
                    break;
                case CHECK_NEW_VERSION:
                    appendText(R.string.check_new_version);
                    scanUpdateFile();
                    break;
                case CHECK_UPDATE_ERROR:
                    appendText(R.string.check_failure);
                    mUpdateButton.setText(getString(R.string.exit));
                    if (!mUpdateButton.isEnabled()) {
                        mUpdateButton.setEnabled(true);
                    }
                    mUpdateButton.requestFocus();
                    mUpdateButton.setFocusableInTouchMode(true);
                    mUpdateButton.requestFocusFromTouch();
                    break;
                case UPDATE_SUCCESS:
                    appendText(R.string.updated);
                    break;
                case UPDATE_PROGRESS:
                    StringBuilder progressStr = new StringBuilder();
                    progressStr.append(getString(R.string.progress));
                    progressStr.append(mCurrentProgress);
                    progressStr.append("%");
                    mCurrentProgressText.setText(progressStr);
                    if (!mCurrentProgressText.isShown()) {
                        mCurrentProgressText.setVisibility(View.VISIBLE);
                    }
                    break;
                case MSG_SELECT_UPDATE:
                    if (!mCurrentProgressText.isShown()) {
                        mCurrentProgressText.setVisibility(View.VISIBLE);
                    }
                    mIsUpdating = true;
                    if (mUpdateButton.isEnabled()) {
                        mUpdateButton.setEnabled(false);
                    }
                    appendText(R.string.check_package);

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

        // init control
        setTitleWidget();
        findViews();

        // register control event
        registerListeners();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        // register storage change event
        registerReceiver(storageChangeReceiver, intentFilter);

        Message msg = myHandler.obtainMessage(CHECK_STORAGE);
        myHandler.sendMessage(msg);
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setSubTitleText(getString(R.string.system_update), getString(R.string.system_local_update));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mIsUpdating) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(storageChangeReceiver);
        super.onDestroy();
    }

    private void findViews() {
        mTvCurrentVersion = (TextView) findViewById(R.id.local_current_version);
        StringBuilder currentVersion = new StringBuilder();
        currentVersion.append(getString(R.string.current_version));
         String currVer = LOCAL_FACVER;
        if("---------------".equals(currVer)) {
            currVer = LOCAL_ROMVER;
        }
        currentVersion.append(currVer);
        mTvCurrentVersion.setText(currentVersion.toString());

        mUpdateInfoText = (TextView) findViewById(R.id.local_update_info);
        mUpdateInfoText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mUpdateInfoText.setScrollbarFadingEnabled(false);
        mUpdateInfoText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mUpdateInfoText.setBackgroundResource(R.drawable.update_display_version);
                } else {
                    mUpdateInfoText.setBackgroundResource(R.drawable.update_not_display);
                }
            }
        });
        mUpdateInfoText.setText(getString(R.string.ntfs_unsupported));
        mUpdateInfoText.append("\n");
        mUpdateInfoText.append(getString(R.string.sdcard));

        mCurrentProgressText = (TextView) findViewById(R.id.local_current_progress);
        mUpdateButton = (Button) findViewById(R.id.local_immediate);
        mUpdateButton.setEnabled(false);
    }

    private void appendText(int... resId) {
        for (int id : resId) {
            String str = getString(id);
            if (!TextUtils.isEmpty(str)) {
                mUpdateInfoText.append("\n");
                mUpdateInfoText.append(str);
                if (mUpdateInfoText.getGravity() != (Gravity.LEFT | Gravity.BOTTOM)) {
                    int lineHeight = mUpdateInfoText.getLineHeight();
                    int lineCount = mUpdateInfoText.getLineCount();
                    int height = mUpdateInfoText.getHeight();
                    if (lineHeight * lineCount > height) {
                        mUpdateInfoText.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                    }
                }
            }
        }
    }

    private void registerListeners() {
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHasNewVerison) {
                    if (mScanFiles.size() == 1) {
                        mUpdateFile = mScanFiles.get(0);
                        if (!mCurrentProgressText.isShown()) {
                            mCurrentProgressText.setVisibility(View.VISIBLE);
                        }
                        mIsUpdating = true;
                        if (mUpdateButton.isEnabled()) {
                            mUpdateButton.setEnabled(false);
                        }
                        appendText(R.string.check_package);
                        new UpdateSystemThread().start();
                    } else if (mScanFiles.size() > 1) {
                        mSelectUpdateDialog = new SelectUpdateDialog(SystemLocalUpdateActivity.this);
                        mSelectUpdateDialog.show();
                    }
                } else {
                    finish();
                    overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
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
                appendText(R.string.no_sdcard);
                mUpdateButton.setText(getString(R.string.exit));
                if (!mUpdateButton.isEnabled()) {
                    mUpdateButton.setEnabled(true);
                }
                mUpdateButton.requestFocus();
                mUpdateButton.setFocusableInTouchMode(true);
                mUpdateButton.requestFocusFromTouch();
            }
        }
    }

    private  boolean hasStorageDevice() {
        mMountedVolumes.clear();
        StorageManagerExtra sm = StorageManagerExtra.getInstance(this);
        for (StorageVolume volume : sm.getVolumeList()) {
            String path = volume.getPath();
            String fsType = sm.getVolumeFsType(path);

            boolean isNtfsFs = false;
            if(!TextUtils.isEmpty(fsType) && fsType.contains("ntfs")) {
                isNtfsFs = true;
            }

            if (Environment.MEDIA_MOUNTED.equals(sm.getVolumeState(path)) && !isNtfsFs) {
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
            appendText(R.string.check_updatezip);
            mUpdateButton.setText(getString(R.string.update));
        } else {
            mHasNewVerison = false;
            appendText(R.string.no_update_file);
            mUpdateButton.setText(getString(R.string.exit));
        }
        if (!mUpdateButton.isEnabled()) {
            mUpdateButton.setEnabled(true);
        }
        mUpdateButton.requestFocus();
        mUpdateButton.setFocusableInTouchMode(true);
        mUpdateButton.requestFocusFromTouch();
    }

    private File mRecoveryDir = new File(Constants.RECOVERY_DIR);

    private File mCommandFile = new File(mRecoveryDir, Constants.COMMAND_FILE_NAME);

    @SuppressLint("NewApi")
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

            String platform = SystemProperties.get("ro.scifly.platform", "dongle");
            Log.e(TAG, "platform:" + platform);
            if ("dongle".equalsIgnoreCase(platform)) {// dongle.
                if (!mRecoveryDir.exists()) {
                    mRecoveryDir.mkdir();
                }
                if (mCommandFile.exists()) {
                    mCommandFile.delete();
                }
                /*
                 * write command to file /cache/recovery/command
                 * ===============================================
                 * --update_package=/cache/v2.3.2.16452.zip
                 */
                FileWriter commandFile = new FileWriter(mCommandFile);
                try {
                    StringBuilder b = new StringBuilder();
                    b.append("--update_package=");
                    b.append(mUpdateFile);
                    b.append("\n");
                    b.append("--locale=");
                    b.append(Locale.getDefault().toString());
                    b.append("\n");
                    commandFile.write(b.toString());
                } finally {
                    commandFile.close();
                }

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                pm.reboot("recovery");

            } else {
                RecoverySystem.installPackage(this, mUpdateFile);

            }

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
                appendText(R.string.sdcard_insert);

                hasStorageDevice();
                scanUpdateFile();
            } else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
                if (mSelectUpdateDialog != null) {
                    mSelectUpdateDialog.dismiss();
                }
                appendText(R.string.sdcard_remove);

                hasStorageDevice();
                scanUpdateFile();
                if (mScanFiles.size() < 1) {
                    mUpdateButton.setText(getString(R.string.exit));
                } else {
                    mUpdateButton.setText(getString(R.string.update));
                }
                if (!mUpdateButton.isEnabled()) {
                    mUpdateButton.setEnabled(true);
                }
                mUpdateButton.requestFocus();
                mUpdateButton.setFocusableInTouchMode(true);
                mUpdateButton.requestFocusFromTouch();
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
