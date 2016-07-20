
package com.eostek.documentui.ui;

import scifly.storage.StorageManagerExtra;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.eostek.documentui.Constants;
import com.eostek.documentui.DocumentApplication;
import com.eostek.documentui.R;
import com.eostek.documentui.data.DataProxy;
import com.eostek.documentui.data.SettingInfor;
import com.eostek.documentui.util.Utils;
import com.eostek.documentui.view.TextSelectorWidget;
import com.eostek.documentui.view.ValueChangeListener;

/**
 * @ClassName: DownloadSettingsActivity.
 * @Description:DownloadSettingsActivity.
 * @author: lucky.li.
 * @date: Nov 11, 2015 11:42:25 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class DownloadSettingsActivity extends Activity {
    private final String TAG = "DownloadSettingsActivity";

    private TextSelectorWidget mDownloadTaskNumView;

    private TextSelectorWidget mDownloadLimitSpeedView;

    private TextSelectorWidget mStorageDirectoryView;

    private String[] limitSpeedItems;

    private String[] storageDirectoryItems;

    private DataProxy mDataProxy;

    private SettingInfor mSettingInfo;

    /**
     * No speed limit
     */
    private final int NO_SPEED_LIMIT = 100000;

    /**
     * 50Kb/s
     */
    private final int SPEED_LIMIT_500 = 50;

    /**
     * 100KB/s
     */
    private final int SPEED_LIMIT_1M = 100;

    /**
     * 200KB/s
     */
    private final int SPEED_LIMIT_2M = 200;

    /**
     * 500KB/s
     */
    private final int SPEED_LIMIT_5M = 500;

    /**
     * 1MB/s
     */
    private final int SPEED_LIMIT_10M = 1000;

    /**
     * 2Mb/s
     */
    private final int SPEED_LIMIT_20M = 2000;

    /**
     * 5Mb/s
     */
    private final int SPEED_LIMIT_50M = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_download_settings_layout);
        findViews();
    }

    private void findViews() {
        mDataProxy = new DataProxy(this);
        mSettingInfo = mDataProxy.getSetting();
        if (Constants.isDebug) {
            Log.i(TAG, "settingInfo==" + mSettingInfo.toString());
        }
        mDownloadTaskNumView = (TextSelectorWidget) findViewById(R.id.download_task_num);
        mDownloadTaskNumView.setItemName(getString(R.string.download_task_num));
        mDownloadTaskNumView.setValueRange(1, 5);
        mDownloadTaskNumView.setCurrentValue(mSettingInfo.downloadNumber);
        mDownloadTaskNumView.setArrowResource(TextSelectorWidget.SELECT_ENABLE);

        mDownloadLimitSpeedView = (TextSelectorWidget) findViewById(R.id.download_limit_speed);
        mDownloadLimitSpeedView.setItemName(getString(R.string.download_limit_speed));
        limitSpeedItems = getResources().getStringArray(R.array.download_limit_speed_items);
        mDownloadLimitSpeedView.setValueItems(limitSpeedItems);
        mDownloadLimitSpeedView.setCurrentValue(getCurrentSpeedIndex(mSettingInfo.downloadSpeedLimite));
        mDownloadLimitSpeedView.setArrowResource(TextSelectorWidget.SELECT_ENABLE);
        mStorageDirectoryView = (TextSelectorWidget) findViewById(R.id.storage_directory);
        mStorageDirectoryView.setItemName(getString(R.string.storage_directory));
        storageDirectoryItems = getResources().getStringArray(R.array.storage_directory_items);
        mStorageDirectoryView.setValueItems(storageDirectoryItems);
        if (mSettingInfo.bDataCache) {
            mStorageDirectoryView.setCurrentValue(0);
            if (!Utils.isHasUDisk(this)) {
                mStorageDirectoryView.setArrowResource(TextSelectorWidget.SELECT_UNENABLE);
                mStorageDirectoryView.setFlag(TextSelectorWidget.STORAGE_DIRECTORY_FLAG);
            }else{
                mStorageDirectoryView.setArrowResource(TextSelectorWidget.SELECT_ENABLE);
            }
        } else {
            if (!Utils.isHasUDisk(this)) {
                mStorageDirectoryView.setArrowResource(TextSelectorWidget.SELECT_UNENABLE);
                mStorageDirectoryView.setCurrentValue(0);
                mStorageDirectoryView.setFlag(TextSelectorWidget.STORAGE_DIRECTORY_FLAG);
                mSettingInfo.saveDir = Constants.DOWNLOAD_INNER_LOCATION;
                mSettingInfo.bDataCache = true;
                DocumentApplication.getInstance().addTask(new Runnable() {

                    @Override
                    public void run() {
                        mDataProxy.updateSetting(mSettingInfo);
                    }
                });
            } else {
                mStorageDirectoryView.setArrowResource(TextSelectorWidget.SELECT_ENABLE);
                mStorageDirectoryView.setCurrentValue(1);
            }
        }
        registerListener();
    }

    /**
     * @Title: getCurrentSpeedIndex.
     * @Description: getCurrentSpeedIndex.
     * @param: @param value
     * @param: @return.
     * @return: int.
     * @throws
     */
    private int getCurrentSpeedIndex(int value) {
        int index = 0;
        switch (value) {
            case NO_SPEED_LIMIT:
                index = 0;
                break;
            case SPEED_LIMIT_500:
                index = 1;
                break;
            case SPEED_LIMIT_1M:
                index = 2;
                break;
            case SPEED_LIMIT_2M:
                index = 3;
                break;
            case SPEED_LIMIT_5M:
                index = 4;
                break;
            case SPEED_LIMIT_10M:
                index = 5;
                break;
            case SPEED_LIMIT_20M:
                index = 6;
                break;
            case SPEED_LIMIT_50M:
                index = 7;
                break;
            default:
                index = 0;
                break;
        }
        return index;
    }

    private void registerListener() {
        mDownloadTaskNumView.setValueChangeListener(new ValueChangeListener() {

            @Override
            public void onValueChanged(int value) {
                mSettingInfo.downloadNumber = value;
                DocumentApplication.getInstance().addTask(new Runnable() {

                    @Override
                    public void run() {
                        mDataProxy.updateSetting(mSettingInfo);
                    }
                });
            }
        });
        mDownloadLimitSpeedView.setValueChangeListener(new ValueChangeListener() {

            @Override
            public void onValueChanged(int value) {
                mSettingInfo.downloadSpeedLimite = getLimitSpeedValue(value);
                DocumentApplication.getInstance().addTask(new Runnable() {

                    @Override
                    public void run() {
                        mDataProxy.updateSetting(mSettingInfo);
                    }
                });
            }
        });
        mStorageDirectoryView.setValueChangeListener(new ValueChangeListener() {

            @Override
            public void onValueChanged(int value) {
                if (value == 1) {
                    if (Utils.isHasUDisk(DownloadSettingsActivity.this)) {
                        StorageManagerExtra storageManager = StorageManagerExtra
                                .getInstance(DownloadSettingsActivity.this);
                        String[] uDiskPaths = storageManager.getUdiskPaths();
                        mSettingInfo.saveDir = uDiskPaths[0];
                        mSettingInfo.bDataCache = false;
                    }
                } else if (value == 0) {
                    mSettingInfo.saveDir = Constants.DOWNLOAD_INNER_LOCATION;
                    mSettingInfo.bDataCache = true;
                }
                DocumentApplication.getInstance().addTask(new Runnable() {

                    @Override
                    public void run() {
                        mDataProxy.updateSetting(mSettingInfo);
                    }
                });
            }
        });
    }

    private int getLimitSpeedValue(int value) {
        int speed = NO_SPEED_LIMIT;
        switch (value) {
            case 0:
                speed = NO_SPEED_LIMIT;
                break;
            case 1:
                speed = SPEED_LIMIT_500;
                break;
            case 2:
                speed = SPEED_LIMIT_1M;
                break;
            case 3:
                speed = SPEED_LIMIT_2M;
                break;
            case 4:
                speed = SPEED_LIMIT_5M;
                break;
            case 5:
                speed = SPEED_LIMIT_10M;
                break;
            case 6:
                speed = SPEED_LIMIT_20M;
                break;
            case 7:
                speed = SPEED_LIMIT_50M;
                break;
            default:
                speed = NO_SPEED_LIMIT;
                break;
        }
        return speed;
    }
}
