/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.fragments;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityThread;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.text.format.Formatter;
import android.util.Log;

import com.android.internal.os.storage.ExternalStorageFormatter;
import com.android.settings.R;
import com.android.settings.deviceinfo.fragments.StorageMeasurement.MeasurementDetails;
import com.android.settings.deviceinfo.fragments.StorageMeasurement.MeasurementReceiver;
import com.android.settings.util.Utils;
import com.google.android.collect.Lists;

public class StorageVolumePreferenceCategory extends PreferenceCategory {

    private static final String TAG = "StorageVolumePreferenceCategory";

    public static final String KEY_CACHE = "cache";

    private static final int ORDER_USAGE_BAR = -2;

    private static final int ORDER_STORAGE_LOW = -1;

    /** Physical volume being measured, or {@code null} for internal. */
    private final StorageVolume mVolume;

    private final StorageMeasurement mMeasure;

    private final StorageManager mStorageManager;

    private final Resources mResources;

    private UsageBarPreference mUsageBarPreference;

    private StorageItemPreference mStorageLow;

    private StorageItemPreference mMountTogglePreference;

    private StorageItemPreference mFormatPreference;

    private StorageItemPreference mItemTotal;

    private StorageItemPreference mItemAvailable;

    private StorageItemPreference mItemSystemUsed;

    private StorageItemPreference mExtensionAvailable;

    private StorageItemPreference mItemApps;

    private StorageItemPreference mItemDcim;

    private StorageItemPreference mItemMusic;

    private StorageItemPreference mItemDownloads;

    private StorageItemPreference mItemCache;

    private StorageItemPreference mItemMisc;

    private List<StorageItemPreference> mItemUsers = Lists.newArrayList();

    private Context mContext;

    private long mTotalSize;

    private static final int MSG_UI_UPDATE_APPROXIMATE = 1;

    private static final int MSG_UI_UPDATE_DETAILS = 2;

    private boolean mUsbConnected;

    private String mUsbFunction;

    private long mOtherSpace = 0;

    @SuppressLint("HandlerLeak")
    private Handler mUpdateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UI_UPDATE_APPROXIMATE: {
                    final long[] size = (long[]) msg.obj;
                    updateApproximate(size[0], size[1]);
                    break;
                }
                case MSG_UI_UPDATE_DETAILS: {
                    final MeasurementDetails details = (MeasurementDetails) msg.obj;
                    updateDetails(details);
                    break;
                }
            }
        }
    };

    /**
     * Build category to summarize internal storage, including any emulated
     * {@link StorageVolume}.
     */
    public static StorageVolumePreferenceCategory buildForInternal(Context context) {
        return new StorageVolumePreferenceCategory(context, null);
    }

    /**
     * Build category to summarize specific physical {@link StorageVolume}.
     */
    public static StorageVolumePreferenceCategory buildForPhysical(Context context, StorageVolume volume) {
        return new StorageVolumePreferenceCategory(context, volume);
    }

    private StorageVolumePreferenceCategory(Context context, StorageVolume volume) {
        super(context);
        mContext = context;
        mVolume = volume;
        mMeasure = StorageMeasurement.getInstance(context, volume);

        mResources = context.getResources();
        mStorageManager = StorageManager.from(context);
        if (volume != null) {
            Log.d(TAG, "volume-------getPath:" + volume.getPath());
            Log.d(TAG, "Environment-------getExternalStorageDirectory():"
                    + Environment.getExternalStorageDirectory().getPath());
			Log.d(TAG, "volume-------getDescription:" + volume.getDescription(context));		
            if (volume.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                setTitle(context.getText(R.string.device_memory));
            } else {				
				if("/mnt/external_sd".equals(volume.getPath())){					
					setTitle(context.getText(R.string.install_location_sdcard));
				}else{
					setTitle(volume.getDescription(context));	
				}
               
            }
        } else {
            setTitle(context.getText(R.string.internal_storage));
        }
    }

    private StorageItemPreference buildItem(int titleRes, int colorRes) {
        return new StorageItemPreference(getContext(), titleRes, colorRes);
    }

    public void init() {
        final Context context = getContext();
        removeAll();
        if (mVolume != null && mVolume.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
            mUsageBarPreference = new UsageBarPreference(context, true);
        } else {
            mUsageBarPreference = new UsageBarPreference(context, false);
        }
        mUsageBarPreference.setOrder(ORDER_USAGE_BAR);
        addPreference(mUsageBarPreference);

        mItemTotal = buildItem(R.string.memory_size, 0);
        mItemAvailable = buildItem(R.string.memory_available, R.color.memory_avail);
        mExtensionAvailable = buildItem(R.string.extension_memory_available, R.color.extension_memory_avail);
        addPreference(mItemTotal);
        if (mVolume != null && mVolume.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
            addPreference(mExtensionAvailable);
        } else {
            addPreference(mItemAvailable);
        }
        mItemSystemUsed = buildItem(R.string.system_usage, R.color.memory_used);
        mItemApps = buildItem(R.string.memory_apps_usage, R.color.memory_apps_usage);
        mItemDcim = buildItem(R.string.memory_dcim_usage, R.color.memory_dcim);
        mItemMusic = buildItem(R.string.memory_music_usage, R.color.memory_music);
        mItemDownloads = buildItem(R.string.memory_downloads_usage, R.color.memory_downloads);
        mItemCache = buildItem(R.string.memory_media_cache_usage, R.color.memory_cache);
        mItemMisc = buildItem(R.string.memory_media_misc_usage, R.color.memory_misc);

        mItemCache.setKey(KEY_CACHE);

        final boolean showDetails = mVolume == null || mVolume.isPrimary();
        if (showDetails) {
            addPreference(mItemSystemUsed);
            addPreference(mItemApps);
            addPreference(mItemDcim);
            addPreference(mItemMusic);
            addPreference(mItemDownloads);
            addPreference(mItemCache);
            addPreference(mItemMisc);
        }

        final boolean isRemovable = mVolume != null ? mVolume.isRemovable() : false;
        // Always create the preference since many code rely on it existing
        Log.d(TAG, " isRemovable in init :" + isRemovable);
        mMountTogglePreference = buildItem(R.string.choose_mount, 0);
        if (isRemovable) {
            // MStar Android Patch Begin
            mMountTogglePreference.updateTitle((mResources.getString(R.string.sd_eject).toString().replace(mResources
                    .getString(R.string.sd_card_settings_label).toString(), getTitle())));
            mMountTogglePreference.updateSize(mResources.getString(R.string.sd_eject_summary).toString()
                    .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
            // MStar Android Patch End
            // addPreference(mMountTogglePreference);
        }

        boolean allowFormat = false;
        if (mVolume != null && !mVolume.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
            allowFormat = true;
        }
        Log.d(TAG, "allowFormat:" + allowFormat);
        if (allowFormat) {
            mFormatPreference = buildItem(R.string.clear_data, 0);
            // MStar Android Patch Begin
            mFormatPreference.updateTitle(mResources.getString(R.string.sd_format).toString()
                    .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
            mFormatPreference.updateSize(mResources.getString(R.string.sd_format_summary).toString()
                    .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
            // MStar Android Patch End
            addPreference(mFormatPreference);
        }

        final IPackageManager pm = ActivityThread.getPackageManager();
        try {
            if (pm.isStorageLow() && mVolume == null) {
                mStorageLow = buildItem(R.string.storage_low_title, 0);
                mStorageLow.setOrder(ORDER_STORAGE_LOW);
                mStorageLow.updateTitle(mResources.getString(R.string.storage_low_title).toString());
                mStorageLow.updateSize(mResources.getString(R.string.storage_low_summary));
                addPreference(mStorageLow);
            } else if (mStorageLow != null) {
                removePreference(mStorageLow);
                mStorageLow = null;
            }
        } catch (RemoteException e) {
        }
    }

    public StorageVolume getStorageVolume() {
        return mVolume;
    }

    public boolean mountToggleClicked(Preference preference) {
        return preference == mMountTogglePreference;
    }

    private void updatePreferencesFromState() {
        // Only update for physical volumes
        if (mVolume == null) {
            return;
        }

        mMountTogglePreference.setEnabled(true);

        final String state = mStorageManager.getVolumeState(mVolume.getPath());

        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mItemAvailable.setTitle(R.string.memory_available_read_only);
        } else {
            mItemAvailable.setTitle(R.string.memory_available);
        }
        Log.d(TAG, "state :" + state);
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            if (mVolume != null) {
                if (mVolume.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                    setTitle(mContext.getText(R.string.device_memory));
                } else {					
					if("/mnt/external_sd".equals(mVolume.getPath())){					
						setTitle(mContext.getText(R.string.install_location_sdcard));
					}else{
						setTitle(mVolume.getDescription(mContext));	
					}
                 
                }
            }
            mMountTogglePreference.setEnabled(true);
            // MStar Android Patch Begin
            mMountTogglePreference.updateTitle(mResources.getString(R.string.sd_eject).toString()
                    .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
            mMountTogglePreference.updateSize(mResources.getString(R.string.sd_eject_summary).toString()
                    .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
            // MStar Android Patch End
        } else {
            if (Environment.MEDIA_UNMOUNTED.equals(state) || Environment.MEDIA_NOFS.equals(state)
                    || Environment.MEDIA_UNMOUNTABLE.equals(state)) {
                mMountTogglePreference.setEnabled(true);
                // MStar Android Patch Begin
                mMountTogglePreference.updateTitle(mResources.getString(R.string.sd_mount).toString()
                        .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
                mMountTogglePreference.updateSize(mResources.getString(R.string.sd_mount_summary).toString()
                        .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
                // MStar Android Patch End
            } else {
                mMountTogglePreference.setEnabled(false);
                // MStar Android Patch Begin
                mMountTogglePreference.updateTitle(mResources.getString(R.string.sd_mount).toString()
                        .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
                mMountTogglePreference.updateSize(mResources.getString(R.string.sd_insert_summary).toString()
                        .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
                // MStar Android Patch End
            }

            removePreference(mUsageBarPreference);
            removePreference(mItemTotal);
            removePreference(mItemAvailable);
            removePreference(mFormatPreference);
            setTitle("");
        }

        if (mUsbConnected
                && (UsbManager.USB_FUNCTION_MTP.equals(mUsbFunction) || UsbManager.USB_FUNCTION_PTP
                        .equals(mUsbFunction))) {
            mMountTogglePreference.setEnabled(false);
            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                mMountTogglePreference.updateSize(mResources.getString(R.string.mtp_ptp_mode_summary));
            }

            if (mFormatPreference != null) {
                mFormatPreference.setEnabled(false);
                mFormatPreference.updateSize(mResources.getString(R.string.mtp_ptp_mode_summary));
            }
        } else if (mFormatPreference != null) {
            mFormatPreference.setEnabled(true);
            // MStar Android Patch Begin
            mFormatPreference.updateSize(mResources.getString(R.string.sd_format_summary).toString()
                    .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
            // MStar Android Patch End
        }
    }

    public void updateApproximate(long totalSize, long availSize) {
        // EosTek Patch Begin
        if ((this.getStorageVolume() == null) && (this.getTitle() == getContext().getText(R.string.internal_storage))) {
            mTotalSize = getSystemStorageSize();
            mItemTotal.updateSize(formatSize(mTotalSize));
            mItemAvailable.updateSize(formatSize(getRomAvailableSize()));
        } else {
            mTotalSize = totalSize;
            removePreference(mItemSystemUsed);
            mItemTotal.updateSize((formatSize(totalSize)));
            mItemAvailable.updateSize(formatSize(availSize));
            mExtensionAvailable.updateSize(formatSize(availSize));
        }
        Log.d(TAG, "updateApproximate totalSize:" + formatSize(totalSize));

        final long usedSize = totalSize - availSize;

        mUsageBarPreference.clear();
        mUsageBarPreference.addEntry(0, usedSize / (float) totalSize, android.graphics.Color.GRAY);
        mUsageBarPreference.commit();

        updatePreferencesFromState();
    }

    private static long totalValues(HashMap<String, Long> map, String... keys) {
        long total = 0;
        if (map == null)
            return 0;
        for (String key : keys) {
            if (map.containsKey(key)) {
                total += map.get(key);
            }
        }
        return total;
    }

    public void updateDetails(MeasurementDetails details) {
        final boolean showDetails = mVolume == null || mVolume.isPrimary();
        if (!showDetails)
            return;

        Log.d(TAG, "updateDetails totalSize:" + formatSize(details.totalSize));
        if ((this.getStorageVolume() == null) && (this.getTitle() == getContext().getText(R.string.internal_storage))) {
            mTotalSize = getSystemStorageSize();
            mItemTotal.updateSize(formatSize(mTotalSize));
            mItemAvailable.updateSize(formatSize(getRomAvailableSize()));
        } else {
            removePreference(mItemSystemUsed);
            mTotalSize = details.totalSize;
            mItemTotal.updateSize((formatSize(details.totalSize)));
            mItemAvailable.updateSize(formatSize(details.availSize));
            mExtensionAvailable.updateSize(formatSize(details.availSize));
        }

        mUsageBarPreference.clear();

        if ((this.getStorageVolume() == null) && (this.getTitle() == getContext().getText(R.string.internal_storage))) {
            updatePreference(mItemSystemUsed, getSystemSpace() + getCacheSpace() + mOtherSpace);
            updatePreference(mItemApps, getRomTotalSize() - getRomAvailableSize());
        } else {
            updatePreference(mItemApps, details.appsSize);
        }

        final long dcimSize = totalValues(details.mediaSize, Environment.DIRECTORY_DCIM, Environment.DIRECTORY_MOVIES,
                Environment.DIRECTORY_PICTURES);
        updatePreference(mItemDcim, dcimSize);

        final long musicSize = totalValues(details.mediaSize, Environment.DIRECTORY_MUSIC,
                Environment.DIRECTORY_ALARMS, Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_RINGTONES,
                Environment.DIRECTORY_PODCASTS);
        updatePreference(mItemMusic, musicSize);

        final long downloadsSize = totalValues(details.mediaSize, Environment.DIRECTORY_DOWNLOADS);
        updatePreference(mItemDownloads, downloadsSize);
        updatePreference(mItemCache, details.cacheSize);
        updatePreference(mItemMisc, details.miscSize);
        for (StorageItemPreference userPref : mItemUsers) {
            final long userSize = details.usersSize.get(userPref.mUserHandle);
            updatePreference(userPref, userSize);
        }
        mUsageBarPreference.commit();
    }

    private void updatePreference(StorageItemPreference pref, long size) {
        Log.d(TAG, "updatePreference, " + " pref : " + pref + " size : " + size);
        if (size > 0) {
            pref.updateSize(formatSize(size));
            final int order = pref.getOrder();
            mUsageBarPreference.addEntry(order, size / (float) mTotalSize, pref.mColor);
        } else {
            removePreference(pref);
        }
    }

    private void measure() {
        mMeasure.invalidate();
        mMeasure.measure();
    }

    public void onResume() {
        mMeasure.setReceiver(mReceiver);
        measure();
    }

    public void onStorageStateChanged() {
        init();
        measure();
    }

    public void onUsbStateChanged(boolean isUsbConnected, String usbFunction) {
        measure();
        mUsbConnected = isUsbConnected;
        mUsbFunction = usbFunction;
    }

    public void onMediaScannerFinished() {
        measure();
    }

    public void onCacheCleared() {
        measure();
    }

    public void onPause() {
        mMeasure.cleanUp();
    }

    private String formatSize(long size) {
        return Formatter.formatFileSize(getContext(), size);
    }

    private MeasurementReceiver mReceiver = new MeasurementReceiver() {
        @Override
        public void updateApproximate(StorageMeasurement meas, long totalSize, long availSize) {
            mUpdateHandler.obtainMessage(MSG_UI_UPDATE_APPROXIMATE, new long[] {
                    totalSize, availSize
            }).sendToTarget();
        }

        @Override
        public void updateDetails(StorageMeasurement meas, MeasurementDetails details) {
            mUpdateHandler.obtainMessage(MSG_UI_UPDATE_DETAILS, details).sendToTarget();
        }
    };

    public Intent intentForClick(Preference pref) {
        Intent intent = null;
        if (pref == mFormatPreference) {
            Log.d(TAG, "click mFormat");

            ConfirmClearUSBCacheDialog();
        } else if (pref == mItemApps) {
            // intent = new Intent(Intent.ACTION_MANAGE_PACKAGE_STORAGE);
            // intent.setClass(getContext(),
            // com.android.settings.Settings.ManageApplicationsActivity.class);
        } else if (pref == mItemDownloads) {
            // intent = new
            // Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).putExtra(
            // DownloadManager.INTENT_EXTRAS_SORT_BY_SIZE, true);
        } else if (pref == mItemMusic) {
            // intent = new Intent(Intent.ACTION_GET_CONTENT);
            // intent.setType("audio/mp3");
        } else if (pref == mItemDcim) {
            // intent = new Intent(Intent.ACTION_VIEW);
            // intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            // MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            // intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else if (pref == mItemMisc) {
            // Context context = getContext().getApplicationContext();
            // intent = new Intent(context, MiscFilesHandler.class);
            // intent.putExtra(StorageVolume.EXTRA_STORAGE_VOLUME, mVolume);
        }
        return intent;
    }

    public static class PreferenceHeader extends Preference {
        public PreferenceHeader(Context context, int titleRes) {
            super(context, null, com.android.internal.R.attr.preferenceCategoryStyle);
            setTitle(titleRes);
        }

        public PreferenceHeader(Context context, CharSequence title) {
            super(context, null, com.android.internal.R.attr.preferenceCategoryStyle);
            setTitle(title);
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    public long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return blockSize * totalBlocks;
    }

    public static long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    public long getSystemSpace() {
        File path = Environment.getRootDirectory();
        long fileTotalLength = path.getTotalSpace();
        return fileTotalLength;
    }

    public long getCacheSpace() {
        File path = Environment.getDownloadCacheDirectory();
        long fileTotalLength = path.getTotalSpace();
        return fileTotalLength;
    }

    public long getExternalSDTotalSize() {
        if (Environment.isExternalStorageEmulated()) {
            return 0;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return blockSize * totalBlocks;
    }

    public long getSystemStorageSize() {
        String totalSpaceString = Utils.formatStorageSizeLong(getContext(), getRomTotalSize() + getSystemSpace()
                + getExternalSDTotalSize());
        long totalSize = 0;
        if (totalSpaceString.contains("GB") || totalSpaceString.contains("G")) {
            String totalSpaceValue = totalSpaceString.substring(0, totalSpaceString.lastIndexOf("G") - 1);
            totalSize = (long) (Double.parseDouble(totalSpaceValue) * 1024 * 1024 * 1024);
        } else if (totalSpaceString.contains("MB") || totalSpaceString.contains("M")) {
            String totalSpaceValue = totalSpaceString.substring(0, totalSpaceString.lastIndexOf("M") - 1);
            totalSize = (long) (Double.parseDouble(totalSpaceValue) * 1024 * 1024);
        }
        mOtherSpace = totalSize - getRomTotalSize() - getSystemSpace() - getCacheSpace() - getExternalSDTotalSize();
        return totalSize - getExternalSDTotalSize();
    }

    /**
     * Dialog to request user confirmation before clearing usb data.
     */
    private void ConfirmClearUSBCacheDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // MStar Android Patch End
        builder.setTitle(mResources.getString(R.string.media_format_title).toString()
                .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
        builder.setMessage(mResources.getString(R.string.media_format_desc).toString()
                .replace(mResources.getString(R.string.sd_card_settings_label).toString(), getTitle()));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isMonkeyRunning()) {
                    return;
                }
                Intent intent = new Intent(ExternalStorageFormatter.FORMAT_ONLY);
                intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
                // Transfer the storage volume to the new intent
                // MStar Android Patch Begin
                intent.putExtra(StorageVolume.EXTRA_STORAGE_VOLUME, mVolume);
                // MStar Android Patch End
                Log.d(TAG, "mVolume: " + mVolume.getDescription(mContext));
                mContext.startService(intent);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create();
        builder.show();
        return;
    }

}
