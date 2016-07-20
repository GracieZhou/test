/*
 * Copyright (C) 2008 The Android Open Source Project
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

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.deviceinfo.DeviceInfoActivity;
import com.android.settings.util.Utils;
import com.google.android.collect.Lists;

/**
 * Panel showing storage usage on disk for known {@link StorageVolume} returned
 * by {@link StorageManager}. Calculates and displays usage of data types.
 */
public class StorageInfoFragment extends PreferenceFragment {

    private static final String TAG = "StorageInfoFragment";

    private static final String TAG_CONFIRM_CLEAR_CACHE = "confirmClearCache";

    private static final int DLG_CONFIRM_UNMOUNT = 1;

    private static final int DLG_ERROR_UNMOUNT = 2;

    // The mountToggle Preference that has last been clicked.
    // Assumes no two successive unmount event on 2 different volumes are
    // performed before the first
    // one's preference is disabled
    private static Preference sLastClickedMountToggle;

    private static String sClickedMountPoint;

    // MStar Android Patch Begin
    private static String sClickedMountTitle;

    // MStar Android Patch End
    // private IMountService mMountService;
    private IMountService mMountService;

    private UsbManager mUsbManager;

    private StorageManager mStorageManager;

    // may be multiply storage
    private List<StorageVolumePreferenceCategory> mCategories = Lists.newArrayList();

    private DeviceInfoActivity mActivity;

    private final BroadcastReceiver mMediaScannerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "mMediaScannerReceiver action: " + action);
            if (action.equals(UsbManager.ACTION_USB_STATE)) {
                boolean isUsbConnected = intent.getBooleanExtra(UsbManager.USB_CONNECTED, false);
                String usbFunction = mUsbManager.getDefaultFunction();
                Log.d(TAG, "isUsbConnected: " + isUsbConnected);
                Log.d(TAG, "usbFunction: " + usbFunction);
                for (StorageVolumePreferenceCategory category : mCategories) {
                    category.onUsbStateChanged(isUsbConnected, usbFunction);
                }
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                for (StorageVolumePreferenceCategory category : mCategories) {
                    category.onMediaScannerFinished();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        final Context context = getActivity();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        mStorageManager = StorageManager.from(context);
        mStorageManager.registerListener(mStorageListener);

        addPreferencesFromResource(R.xml.device_info_memory);
        addCategory(StorageVolumePreferenceCategory.buildForInternal(getActivity()));

        final StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
        for (StorageVolume volume : storageVolumes) {
            Log.d(TAG, "volume-------getState:" + volume.getState());
            Log.d(TAG, "volume-------getPath:" + volume.getPath());
            Log.d(TAG, "volume-------getDescription:" + volume.getDescription(getActivity()));
            // EosTek Patch Begin
            if (!volume.isEmulated() && volume.getPath().indexOf("vrsdcard") < 0) {
                addCategory(StorageVolumePreferenceCategory.buildForPhysical(getActivity(), volume));
            }
            // EosTek Patch End
        }

        setHasOptionsMenu(true);
    }

    StorageEventListener mStorageListener = new StorageEventListener() {
        @Override
        public void onStorageStateChanged(String path, String oldState, String newState) {
            Log.i(TAG, "Received storage state changed notification that " + path + " changed state from " + oldState
                    + " to " + newState);
            for (StorageVolumePreferenceCategory category : mCategories) {
                final StorageVolume volume = category.getStorageVolume();
                if (volume != null && path.equals(volume.getPath())) {
                    category.onStorageStateChanged();
                    break;
                }
            }
        }
    };

    /**
     * Returns the specified system service from the owning Activity.
     */
    protected Object getSystemService(final String name) {
        return getActivity().getSystemService(name);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        mActivity = (DeviceInfoActivity) getActivity();
        mActivity.setSubTitle(R.string.about_storage_info);
    }

    private void addCategory(StorageVolumePreferenceCategory category) {
        mCategories.add(category);
        getPreferenceScreen().addPreference(category);
        category.init();
    }

    @SuppressWarnings("unused")
    private boolean isMassStorageEnabled() {
        // Mass storage is enabled if primary volume supports it
        final StorageVolume[] volumes = mStorageManager.getVolumeList();
        final StorageVolume primary = StorageManager.getPrimaryVolume(volumes);
        return primary != null && primary.allowMassStorage();
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        getActivity().registerReceiver(mMediaScannerReceiver, intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_STATE);
        getActivity().registerReceiver(mMediaScannerReceiver, intentFilter);

        for (StorageVolumePreferenceCategory category : mCategories) {
            category.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mMediaScannerReceiver);
        // pause all
        for (StorageVolumePreferenceCategory category : mCategories) {
            category.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mStorageManager != null && mStorageListener != null) {
            mStorageManager.unregisterListener(mStorageListener);
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (StorageVolumePreferenceCategory.KEY_CACHE.equals(preference.getKey())) {
            ConfirmClearCacheFragment.show(this);
            return true;
        }

        for (StorageVolumePreferenceCategory category : mCategories) {
            Intent intent = category.intentForClick(preference);
            if (intent != null) {
                // Don't go across app boundary if monkey is running
                if (!Utils.isMonkeyRunning()) {
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException anfe) {
                        Log.w(TAG, "No activity found for intent " + intent);
                    }
                }
                return true;
            }
            final StorageVolume volume = category.getStorageVolume();
            if (volume != null && category.mountToggleClicked(preference)) {
                sLastClickedMountToggle = preference;
                sClickedMountPoint = volume.getPath();
                // MStar Android Patch Begin
                sClickedMountTitle = volume.getDescription(getActivity());
                // MStar Android Patch End
                String state = mStorageManager.getVolumeState(volume.getPath());
                if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    unmount();
                } else {
                    mount();
                }
            }
        }
        return false;
    }

    private void unmount() {
        // Check if external media is in use.
        try {
            if (hasAppsAccessingStorage()) {
                // Present dialog to user
                showDialogInner(DLG_CONFIRM_UNMOUNT);
            } else {
                doUnmount();
            }
        } catch (RemoteException e) {
            // Very unlikely. But present an error dialog anyway
            Log.e(TAG, "Is MountService running?");
            showDialogInner(DLG_ERROR_UNMOUNT);
        }
    }

    private void mount() {
        IMountService mountService = getMountService();
        try {
            if (mountService != null) {
                mountService.mountVolume(sClickedMountPoint);
            } else {
                Log.e(TAG, "Mount service is null, can't mount");
            }
        } catch (RemoteException ex) {
            // Not much can be done
        }
    }

    private void doUnmount() {
        // Present a toast here
        Toast.makeText(getActivity(), R.string.unmount_inform_text, Toast.LENGTH_SHORT).show();
        IMountService mountService = getMountService();
        try {
            sLastClickedMountToggle.setEnabled(false);
            // MStar Android Patch Begin
            sLastClickedMountToggle.setTitle(getString(R.string.sd_ejecting_title).toString().replace(
                    getString(R.string.sd_card_settings_label).toString(), sClickedMountTitle));
            sLastClickedMountToggle.setSummary(getString(R.string.sd_ejecting_summary).toString().replace(
                    getString(R.string.sd_card_settings_label).toString(), sClickedMountTitle));
            // MStar Android Patch End
            mountService.unmountVolume(sClickedMountPoint, true, false);
        } catch (RemoteException e) {
            // Informative dialog to user that unmount failed.
            showDialogInner(DLG_ERROR_UNMOUNT);
        }
    }

    private boolean hasAppsAccessingStorage() throws RemoteException {
        IMountService mountService = getMountService();
        int stUsers[] = mountService.getStorageUsers(sClickedMountPoint);
        if (stUsers != null && stUsers.length > 0) {
            return true;
        }
        // TODO FIXME Parameterize with mountPoint and uncomment.
        // On HC-MR2, no apps can be installed on sd and the emulated internal
        // storage is not
        // removable: application cannot interfere with unmount
        /*
         * ActivityManager am =
         * (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
         * List<ApplicationInfo> list = am.getRunningExternalApplications(); if
         * (list != null && list.size() > 0) { return true; }
         */
        // Better safe than sorry. Assume the storage is used to ask for
        // confirmation.
        return true;
    }

    private void showDialogInner(int id) {
        // removeDialog(id);
        // showDialog(id);
    }

    // public boolean onOptionsItemSelected(MenuItem item) {
    // switch (item.getItemId()) {
    // case R.id.storage_usb:
    // if (getActivity() instanceof PreferenceActivity) {
    // ((PreferenceActivity) getActivity()).startPreferencePanel(
    // UsbSettings.class.getCanonicalName(),
    // null,
    // R.string.storage_title_usb, null,
    // this, 0);
    // } else {
    // startFragment(this, UsbSettings.class.getCanonicalName(), -1, null);
    // }
    // return true;
    // }
    // return super.onOptionsItemSelected(item);
    // }
    // public boolean startFragment(
    // Fragment caller, String fragmentClass, int requestCode, Bundle extras) {
    // if (getActivity() instanceof PreferenceActivity) {
    // PreferenceActivity preferenceActivity =
    // (PreferenceActivity)getActivity();
    // preferenceActivity.startPreferencePanel(fragmentClass, extras,
    // R.string.lock_settings_picker_title, null, caller, requestCode);
    // return true;
    // } else {
    // Log.w(TAG,
    // "Parent isn't PreferenceActivity, thus there's no way to launch the "
    // + "given Fragment (name: " + fragmentClass + ", requestCode: " +
    // requestCode
    // + ")");
    // return false;
    // }
    // }
    private synchronized IMountService getMountService() {
        if (mMountService == null) {
            IBinder service = ServiceManager.getService("mount");
            if (service != null) {
                mMountService = IMountService.Stub.asInterface(service);
            } else {
                Log.e(TAG, "Can't get mount service");
            }
        }
        return mMountService;
    }

    private void onCacheCleared() {
        for (StorageVolumePreferenceCategory category : mCategories) {
            category.onCacheCleared();
        }
    }

    private static class ClearCacheObserver extends IPackageDataObserver.Stub {

        private final StorageInfoFragment mTarget;

        private int mRemaining;

        public ClearCacheObserver(StorageInfoFragment target, int remaining) {
            mTarget = target;
            mRemaining = remaining;
        }

        @Override
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            synchronized (this) {
                if (--mRemaining == 0) {
                    mTarget.onCacheCleared();
                }
            }
        }
    }

    /**
     * Dialog to request user confirmation before clearing all cache data.
     */
    public static class ConfirmClearCacheFragment extends DialogFragment {

        public static void show(StorageInfoFragment parent) {
            if (!parent.isAdded())
                return;

            final ConfirmClearCacheFragment dialog = new ConfirmClearCacheFragment();
            dialog.setTargetFragment(parent, 0);
            dialog.show(parent.getFragmentManager(), TAG_CONFIRM_CLEAR_CACHE);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Context context = getActivity();
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.memory_clear_cache_title);
            builder.setMessage(getString(R.string.memory_clear_cache_message));

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final StorageInfoFragment target = (StorageInfoFragment) getTargetFragment();
                    final PackageManager pm = context.getPackageManager();
                    final List<PackageInfo> infos = pm.getInstalledPackages(0);
                    final ClearCacheObserver observer = new ClearCacheObserver(target, infos.size());
                    for (PackageInfo info : infos) {
                        pm.deleteApplicationCacheFiles(info.packageName, observer);
                    }
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);

            return builder.create();
        }
    }
}
