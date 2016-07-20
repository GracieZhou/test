/*
 * Copyright (C) 2010 Google Inc.
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

package com.android.systemui.usb;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;

import com.android.systemui.SystemUI;
// EosTek Patch Begin
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.StatFs;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import scifly.storage.StorageManagerExtra;
import com.android.systemui.R;
// EosTek Patch End

public class StorageNotification extends SystemUI {
    private static final String TAG = "StorageNotification";
    private static final boolean DEBUG = true;

    private static final boolean POP_UMS_ACTIVITY_ON_CONNECT = true;

    /**
     * The notification that is shown when a USB mass storage host
     * is connected.
     * <p>
     * This is lazily created, so use {@link #setUsbStorageNotification()}.
     */
    private Notification mUsbStorageNotification;

    /**
     * The notification that is shown when the following media events occur:
     *     - Media is being checked
     *     - Media is blank (or unknown filesystem)
     *     - Media is corrupt
     *     - Media is safe to unmount
     *     - Media is missing
     * <p>
     * This is lazily created, so use {@link #setMediaStorageNotification()}.
     */
    private Notification   mMediaStorageNotification;
    private boolean        mUmsAvailable;
    private StorageManager mStorageManager;

    private Handler        mAsyncEventHandler;
	private final int mUsbIconNotificationId = R.drawable.status_sys_notify_storage_usb;
	private DelayCancelNotification mDelayCancelNotification;

    private class StorageNotificationEventListener extends StorageEventListener {
        public void onUsbMassStorageConnectionChanged(final boolean connected) {
            mAsyncEventHandler.post(new Runnable() {
                @Override
                public void run() {
                    onUsbMassStorageConnectionChangedAsync(connected);
                }
            });
        }
        public void onStorageStateChanged(final String path,
                final String oldState, final String newState) {
            mAsyncEventHandler.post(new Runnable() {
                @Override
                public void run() {
                    onStorageStateChangedAsync(path, oldState, newState);
                }
            });
        }
    }

    @Override
    public void start() {
        mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        final boolean connected = mStorageManager.isUsbMassStorageConnected();
        if (DEBUG) Log.d(TAG, String.format( "Startup with UMS connection %s (media state %s)",
                mUmsAvailable, Environment.getExternalStorageState()));

        HandlerThread thr = new HandlerThread("SystemUI StorageNotification");
        thr.start();
        mAsyncEventHandler = new Handler(thr.getLooper());

        StorageNotificationEventListener listener = new StorageNotificationEventListener();
        listener.onUsbMassStorageConnectionChanged(connected);
        mStorageManager.registerListener(listener);
        // EosTek Patch Begin
        // update the icon of TF card every 10 seconds, show warning icon when
        // the rest volume is less than 10%
        mMonitorSdcardRunnable = new MonitorSdcardRunnable();
		mDelayCancelNotification = new DelayCancelNotification();
        mContext.registerReceiver(mBootReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        // EosTek Patch End
    }

    private void onUsbMassStorageConnectionChangedAsync(boolean connected) {
        mUmsAvailable = connected;
        /*
         * Even though we may have a UMS host connected, we the SD card
         * may not be in a state for export.
         */
        String st = Environment.getExternalStorageState();

        if (DEBUG) Log.i(TAG, String.format("UMS connection changed to %s (media state %s)",
                connected, st));

        if (connected && (st.equals(
                Environment.MEDIA_REMOVED) || st.equals(Environment.MEDIA_CHECKING))) {
            /*
             * No card or card being checked = don't display
             */
            connected = false;
        }
        // EosTek Patch Begin
        // updateUsbMassStorageNotification(connected);
        // EosTek Patch End
    }

    private synchronized void onStorageStateChangedAsync(String path, String oldState, String newState) {
        if (DEBUG) Log.i(TAG, String.format(
                "Media {%s} state changed from {%s} -> {%s}", path, oldState, newState));
        // EosTek Patch Begin
        // Null check
        if (TextUtils.isEmpty(path)) {
            return;
        }

        // adjust the type of the current storage
        boolean sdcard = false;
        if (path.startsWith(MST_MEDIA_STORAGE_PATH) || path.startsWith(RK_MEDIA_STORAGE_PATH)
                || path.startsWith(MLOGIC_MEDIA_STORAGE_PATH)) {
            sdcard = true;
        } else if (path.startsWith(MST_USB_STORAGE_PATH) || path.startsWith(RK_USB_STORAGE_PATH)
                || path.startsWith(MLOGIC_USB_STORAGE_PATH)) {
            sdcard = false;
        } else {
            Log.w(TAG, String.format("Ignoring unknown path {%s}", path));
            return;
        }

        int titleId = 0, messageId = 0, iconId = 0;
        boolean badStorage = false;
        if (newState.equals(Environment.MEDIA_MOUNTED)) {
            // check read only
            final String state = mStorageManager.getVolumeState(path);
            if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                badStorage = true;
                mStorageList.put(path, true);
            } else {
                mStorageList.put(path, false);
            }
            if (sdcard) {
                titleId = messageId = com.android.internal.R.string.ext_sdcard_storage_notification_title;
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard;
            } else {
                titleId = messageId = com.android.internal.R.string.ext_usb_storage_notification_title;
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb;
            }
        } else if (newState.equals(Environment.MEDIA_UNMOUNTED)) {
            // abnormal storage
            if (oldState.equals(Environment.MEDIA_CHECKING)) {
                // abnormal storage
                mStorageList.put(path, true);
                badStorage = true;
                // sdcard
                if (sdcard) {
                    titleId = messageId = com.android.internal.R.string.ext_sdcard_media_state_abnormal_notification_title;
                    iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard_abnormal;
                } else {
                    titleId = messageId = com.android.internal.R.string.ext_usb_media_state_abnormal_notification_title;
                    iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb_abnormal;
                }

                // normal unmounted
            } else if (oldState.equals(Environment.MEDIA_MOUNTED)) {
                // make sure the path is in the hash map
                if (!mStorageList.containsKey(path)) {
                    return;
                }
                mStorageList.remove(path);
                if (sdcard) {
                    titleId = messageId = com.android.internal.R.string.ext_sdcard_media_nomedia_notification_title;
                    iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard;
                } else {
                    titleId = messageId = com.android.internal.R.string.ext_usb_media_nomedia_notification_title;
                    iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb;
                }
            } else if (oldState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                // bad state storage
                if (mStorageList.get(path)) {
                    badStorage = true;
                }
                mStorageList.remove(path);
                if (sdcard) {
                    titleId = messageId = com.android.internal.R.string.ext_sdcard_media_nomedia_notification_title;
                    iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard;
                } else {
                    titleId = messageId = com.android.internal.R.string.ext_usb_media_nomedia_notification_title;
                    iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb;
                }
            } else {
                return;
            }
        } else if (newState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            mStorageList.put(path, true);
            badStorage = true;
            if (sdcard) {
                titleId = messageId = com.android.internal.R.string.ext_sdcard_media_state_ro_notification_title;
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard_abnormal;
            } else {
                titleId = messageId = com.android.internal.R.string.ext_usb_media_state_ro_notification_title;
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb_abnormal;
            }
        } else if (newState.equals(Environment.MEDIA_NOFS) || newState.equals(Environment.MEDIA_UNKNOWN)) {
            mStorageList.put(path, true);
            badStorage = true;
            if (sdcard) {
                titleId = messageId = com.android.internal.R.string.ext_sdcard_media_state_abnormal_notification_title;
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard_abnormal;
            } else {
                titleId = messageId = com.android.internal.R.string.ext_usb_media_state_abnormal_notification_title;
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb_abnormal;
            }
        } else if (newState.equals(Environment.MEDIA_REMOVED)) {
            // make sure the path is in the hash map
            if (!mStorageList.containsKey(path)) {
                return;
            }
            // bad state storage
            if (mStorageList.get(path)) {
                badStorage = true;
            }
            mStorageList.remove(path);
            if (sdcard) {
                titleId = messageId = com.android.internal.R.string.ext_sdcard_media_nomedia_notification_title;
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard;
            } else {
                titleId = messageId = com.android.internal.R.string.ext_usb_media_nomedia_notification_title;
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb;
            }
        } else if (newState.equals(Environment.MEDIA_BAD_REMOVAL)) {
            titleId = messageId = com.android.internal.R.string.ext_sdcard_media_badremoval_notification_title;
            iconId = com.android.internal.R.drawable.stat_sys_warning;
        } else {
            Log.w(TAG, String.format("Ignoring state {%s}", newState));
            return;
        }

        if (sdcard) {
            setMediaStorageNotification(titleId, messageId, iconId, true, true, null);
            if (newState.equals(Environment.MEDIA_MOUNTED)) {
                mAsyncEventHandler.removeCallbacks(mMonitorSdcardRunnable);
                mAsyncEventHandler.postDelayed(mMonitorSdcardRunnable, DELAY_MS);
            }
        } else {
            if (badStorage) {
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb_abnormal;
            } else {
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb;
            }
            // usb storage mounted/unmounted
            setUsbStorageNotification(titleId, messageId, iconId, true, true, null);
        }

        // update storage icon on system bar
        if (haveBadUsbStorage()) {
            iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb_abnormal;
        }else if(sdcard){ 
            iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard;
        }
        else {
            iconId = com.android.internal.R.drawable.status_sys_notify_storage_usb;
        }
        if( iconId != com.android.internal.R.drawable.status_sys_notify_storage_sdcard){
            setUsbStorageNotification(titleId, messageId, iconId, true, true, null);
        }

        // remove all storage icons
        mAsyncEventHandler.postDelayed(mDelayCancelNotification, 2000);
        // EosTek Patch End
    }

    /**
     * Update the state of the USB mass storage notification
     */
    void updateUsbMassStorageNotification(boolean available) {

        if (available) {
            Intent intent = new Intent();
            intent.setClass(mContext, com.android.systemui.usb.UsbStorageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
            setUsbStorageNotification(
                    com.android.internal.R.string.usb_storage_notification_title,
                    com.android.internal.R.string.usb_storage_notification_message,
                    com.android.internal.R.drawable.stat_sys_data_usb,
                    false, true, pi);
        } else {
            setUsbStorageNotification(0, 0, 0, false, false, null);
        }
    }

    /**
     * Sets the USB storage notification.
     */
    private synchronized void setUsbStorageNotification(int titleId, int messageId, int icon,
            boolean sound, boolean visible, PendingIntent pi) {

        if (!visible && mUsbStorageNotification == null) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        if (visible) {
            Resources r = Resources.getSystem();
            CharSequence title = r.getText(titleId);
            // EosTek Patch Begin
            // FIXME make sure the title different from old before
            final int count = mStorageList.size();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < count; i++) {
                sb.append(" ");
            }
            title = title + sb.toString();
            // EosTek Patch End
            CharSequence message = r.getText(messageId);

            if (mUsbStorageNotification == null) {
                mUsbStorageNotification = new Notification();
                mUsbStorageNotification.icon = icon;
                mUsbStorageNotification.when = 0;
            }
            // EosTek Patch Begin
            else {
                mUsbStorageNotification.icon = icon;
                mUsbStorageNotification.when = System.currentTimeMillis();
            }
            // EosTek Patch End

            if (sound) {
                mUsbStorageNotification.defaults |= Notification.DEFAULT_SOUND;
            } else {
                mUsbStorageNotification.defaults &= ~Notification.DEFAULT_SOUND;
            }

            mUsbStorageNotification.flags = Notification.FLAG_ONGOING_EVENT;

            mUsbStorageNotification.tickerText = title;
            if (pi == null) {
                Intent intent = new Intent();
                pi = PendingIntent.getBroadcastAsUser(mContext, 0, intent, 0,
                        UserHandle.CURRENT);
            }
            mUsbStorageNotification.color = mContext.getResources().getColor(
                    com.android.internal.R.color.system_notification_accent_color);
            mUsbStorageNotification.setLatestEventInfo(mContext, title, message, pi);
            mUsbStorageNotification.visibility = Notification.VISIBILITY_PUBLIC;
            mUsbStorageNotification.category = Notification.CATEGORY_SYSTEM;

            final boolean adbOn = 1 == Settings.Global.getInt(
                mContext.getContentResolver(),
                Settings.Global.ADB_ENABLED,
                0);

            if (POP_UMS_ACTIVITY_ON_CONNECT && !adbOn) {
                // Pop up a full-screen alert to coach the user through enabling UMS. The average
                // user has attached the device to USB either to charge the phone (in which case
                // this is harmless) or transfer files, and in the latter case this alert saves
                // several steps (as well as subtly indicates that you shouldn't mix UMS with other
                // activities on the device).
                //
                // If ADB is enabled, however, we suppress this dialog (under the assumption that a
                // developer (a) knows how to enable UMS, and (b) is probably using USB to install
                // builds or use adb commands.
                // EosTek Patch Begin
                // mUsbStorageNotification.fullScreenIntent = pi;
                // EosTek Patch End
            }
        }

        final int notificationId = mUsbStorageNotification.icon;
        if (visible) {
            // EosTek Patch Begin
            // update the icon if exist
            notificationManager.notify(null, mUsbIconNotificationId, mUsbStorageNotification);
            // EosTek Patch End
        } else {
            // EosTek Patch Begin
            notificationManager.cancel(mUsbIconNotificationId);
            // EosTek Patch End
        }
    }

    private synchronized boolean getMediaStorageNotificationDismissable() {
        if ((mMediaStorageNotification != null) &&
            ((mMediaStorageNotification.flags & Notification.FLAG_AUTO_CANCEL) ==
                    Notification.FLAG_AUTO_CANCEL))
            return true;

        return false;
    }

    /**
     * Sets the media storage notification.
     */
    private synchronized void setMediaStorageNotification(int titleId, int messageId, int icon, boolean visible,
                                                          boolean dismissable, PendingIntent pi) {

        if (!visible && mMediaStorageNotification == null) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        if (mMediaStorageNotification != null && visible) {
            /*
             * Dismiss the previous notification - we're about to
             * re-use it.
             */
            final int notificationId = mMediaStorageNotification.icon;
            notificationManager.cancel(notificationId);
        }

        if (visible) {
            Resources r = Resources.getSystem();
            CharSequence title = r.getText(titleId);
            CharSequence message = r.getText(messageId);

            if (mMediaStorageNotification == null) {
                mMediaStorageNotification = new Notification();
                mMediaStorageNotification.when = 0;
            }

            mMediaStorageNotification.defaults &= ~Notification.DEFAULT_SOUND;

            if (dismissable) {
                mMediaStorageNotification.flags = Notification.FLAG_AUTO_CANCEL;
            } else {
                mMediaStorageNotification.flags = Notification.FLAG_ONGOING_EVENT;
            }

            mMediaStorageNotification.tickerText = title;
            if (pi == null) {
                Intent intent = new Intent();
                pi = PendingIntent.getBroadcastAsUser(mContext, 0, intent, 0,
                        UserHandle.CURRENT);
            }

            mMediaStorageNotification.icon = icon;
            mMediaStorageNotification.color = mContext.getResources().getColor(
                    com.android.internal.R.color.system_notification_accent_color);
            mMediaStorageNotification.setLatestEventInfo(mContext, title, message, pi);
            mMediaStorageNotification.visibility = Notification.VISIBILITY_PUBLIC;
            mMediaStorageNotification.category = Notification.CATEGORY_SYSTEM;
        }

        final int notificationId = mMediaStorageNotification.icon;
        // EosTek Patch Begin
        if (visible) {
            // update the icon if exist
            notificationManager.notify(null, mSdCardIconNotificationId, mMediaStorageNotification);
        } else {
            notificationManager.cancel(mSdCardIconNotificationId);
        }
        // EosTek Patch End
    }
    // EosTek Patch Begin
    private Notification mSdCardIconNotification;
    private final int mSdCardIconNotificationId = R.drawable.status_sys_notify_tfcard_is_out_of_memory;
    private MonitorSdcardRunnable mMonitorSdcardRunnable;
    private static final int DELAY_MS = 10 * 1000; 

    // key is path and value is flag for normal or abnormal
    private Map<String, Boolean> mStorageList = new HashMap<String, Boolean>();

    private static final String MST_USB_STORAGE_PATH = "/mnt/usb/";
    private static final String MST_MEDIA_STORAGE_PATH = "/mnt/external_sd";
    private static final String RK_USB_STORAGE_PATH = "/mnt/usb_storage/";
    private static final String RK_MEDIA_STORAGE_PATH = "/mnt/external_sd";
    private static final String MLOGIC_USB_STORAGE_PATH = "/storage/external_storage/";
    private static final String MLOGIC_MEDIA_STORAGE_PATH = "/storage/external_storage/sdcard";

    private boolean haveMeidaStorage() {
        for (String key : mStorageList.keySet()) {
            if (key.startsWith(RK_MEDIA_STORAGE_PATH) || key.startsWith(MST_MEDIA_STORAGE_PATH)
                    || key.startsWith(MLOGIC_MEDIA_STORAGE_PATH)) {
                Log.d(TAG, "sdcard path : " + key);
                return true;
            }
        }

        return false;
    }

    private boolean haveUsbStorage() {
        for (String key : mStorageList.keySet()) {
            if (key.startsWith(RK_USB_STORAGE_PATH) || key.startsWith(MST_USB_STORAGE_PATH)
                    || (key.startsWith(MLOGIC_USB_STORAGE_PATH)) && !key.startsWith(MLOGIC_MEDIA_STORAGE_PATH)) {
                Log.d(TAG, "usb path : " + key);
                return true;
            }
        }

        return false;
    }

    private boolean haveBadUsbStorage() {
        for (String key : mStorageList.keySet()) {
            if (key.startsWith(RK_USB_STORAGE_PATH) || key.startsWith(MST_USB_STORAGE_PATH)
                    || (key.startsWith(MLOGIC_USB_STORAGE_PATH)) && !key.startsWith(MLOGIC_MEDIA_STORAGE_PATH)) {
                if (mStorageList.get(key)) {
                    Log.d(TAG, "key : " + key + " value : " + mStorageList.get(key));
                    return true;
                }
            }
        }

        return false;
    }

    private void removeStorageIcons() {
        // remove all icon
        if (mStorageList.isEmpty()) {
			// make sure monitor removed
            mAsyncEventHandler.removeCallbacks(mMonitorSdcardRunnable);
            setUsbStorageNotification(0, 0, 0, false, false, null);
            setMediaStorageNotification(0, 0, 0, false, false, null);

            return;
        }

        // make sure no media storage icon
        if (!haveMeidaStorage()) {
            // make sure monitor removed
            mAsyncEventHandler.removeCallbacks(mMonitorSdcardRunnable);
            setMediaStorageNotification(0, 0, 0, false, false, null);
        }
        // make sure no usb storage icons
        if (!haveUsbStorage()) {
            setUsbStorageNotification(0, 0, 0, false, false, null);
        }
    }

    class MonitorSdcardRunnable implements Runnable {

        public void run() {
            updateSdcardIcon();
            // update every 10 seconds
            mAsyncEventHandler.removeCallbacks(mMonitorSdcardRunnable);
            mAsyncEventHandler.postDelayed(mMonitorSdcardRunnable, DELAY_MS);
        }

        private void updateSdcardIcon() {
            String path = StorageManagerExtra.getInstance(mContext).getExternalSdcardPath();
            if (TextUtils.isEmpty(path)) {
                NotificationManager notificationManager = (NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager == null) {
                    return;
                }
                // cancel sdcard icon
                notificationManager.cancel(mSdCardIconNotificationId);
                return;
            }

            // to avoid card is unmounted during the calculation, use try catch.
            boolean isLowStorage = false;
            try {
                StatFs sf = new StatFs(path);
                long freeBytes = sf.getFreeBytes();
                long totalBytes = sf.getTotalBytes();

                if (false) {
                    Log.d(TAG, "free : " + freeBytes + " total : " + totalBytes);
                }

                // no happened
                if (totalBytes == 0) {
                    Log.w(TAG, "total size is zero...");
                    return;
                }
                isLowStorage = (freeBytes * 100 / totalBytes <= 10) ? true : false;
                if (false) {
                    Log.w(TAG, "isLowStorage : " + isLowStorage);
                }
            } catch (Exception e) {
                Log.e(TAG, "error while update sdcard icon. ", e);
                return;
            }

            int titleId, messageId, iconId;
            if (isLowStorage) {
                iconId = R.drawable.status_sys_notify_tfcard_is_out_of_memory;
                titleId = messageId = com.android.internal.R.string.ext_sdcard_media_low_storage_notification_title;
                setMediaStorageNotification(titleId, messageId, iconId, true, true, null);
            } else {
                iconId = com.android.internal.R.drawable.status_sys_notify_storage_sdcard;
                titleId = messageId = com.android.internal.R.string.ext_sdcard_storage_notification_title;
                setMediaStorageNotification(titleId, messageId, iconId, true, true, null);
            }
        }
    }
	
	    class DelayCancelNotification implements Runnable {

        @Override
        public void run() {
            removeStorageIcons();
        }
    }
    // EosTek Patch End

    private Runnable mBootDoneCheckRunnable = new Runnable() {

        @Override
        public void run() {
            String externalSdcardPath = StorageManagerExtra.getInstance(mContext).getExternalSdcardPath();
            String[] udiskPaths = StorageManagerExtra.getInstance(mContext).getUdiskPaths();

            if (!TextUtils.isEmpty(externalSdcardPath)) {
                onStorageStateChangedAsync(externalSdcardPath, Environment.MEDIA_CHECKING,
                        mStorageManager.getVolumeState(externalSdcardPath));
            }

            if (null != udiskPaths && udiskPaths.length > 0) {
                onStorageStateChangedAsync(udiskPaths[0], Environment.MEDIA_CHECKING,
                        mStorageManager.getVolumeState(udiskPaths[0]));
            }
        }
    };

    private BroadcastReceiver mBootReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            mAsyncEventHandler.postDelayed(mBootDoneCheckRunnable, 1000);
        }
    };
    // EosTek Patch End
}
