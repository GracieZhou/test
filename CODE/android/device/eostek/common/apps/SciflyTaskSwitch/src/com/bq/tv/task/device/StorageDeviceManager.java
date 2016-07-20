
package com.bq.tv.task.device;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import scifly.storage.StorageManagerExtra;
import com.eos.notificationcenter.R;
import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.text.TextUtils;
import android.util.Log;

/**
 *  Manage storage device.
 */
public class StorageDeviceManager {

    /**
     * Tag used to show in logcat.
     */
    public static String TAG = "FunctionBlockContainer";

    Context mContext;

    static final String INTERNAL_SDCARD_PATH = "/mnt/internal_sd";

    static final String INTERNAL_SDCARD_PATH2 = "/mnt/sdcard";
    
    static final String INTERNAL_SDCARD_PATH_805 = Environment.getExternalStorageDirectory().getAbsolutePath();

    private IMountService mMountService = null;

    /**
     * Constructor of StorageDeviceManager.
     * @param mContext context.
     */
    public StorageDeviceManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Get all mounted volume.
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public List<StorageItem> getAllMountedVolume() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        StorageManagerExtra storageManager = StorageManagerExtra.getInstance(mContext);
        String[] storagePaths = storageManager.getVolumePaths();
        if (storagePaths == null) {
            return null;
        }

        List<StorageItem> temp = new ArrayList<StorageItem>();
        for (int i = 0; i < storagePaths.length; ++i) {
            String path = storagePaths[i];
            String state = storageManager.getVolumeState(path);

            /** ignore internal sdcard */
            if (path.equals(INTERNAL_SDCARD_PATH) || path.equals(INTERNAL_SDCARD_PATH2)
                    || path.equals(INTERNAL_SDCARD_PATH_805)) {
                continue;
            }

            if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
                String label = getMountedVolumeLabel(mContext, path);
                StorageItem storageItem = new StorageItem();
                storageItem.setStoragePath(path);
                storageItem.setLabel(label);
                temp.add(storageItem);
            }
        }
        return temp;
    }

    /**
     * Get the label of mounted volume.
     * @param c context.
     * @param path path.
     * @return
     */
    public String getMountedVolumeLabel(Context c, String path) {
        StorageManagerExtra storageManagerExtra = StorageManagerExtra.getInstance(mContext);
        String label = null;
        String udiskPath = null;
        String externalPath = storageManagerExtra.getExternalSdcardPath();
        
        if (!TextUtils.isEmpty(externalPath) && externalPath.equals(path)) {
            label = storageManagerExtra.getVolumeLabel(externalPath);
            if (TextUtils.isEmpty(label)) {
                label = mContext.getString(R.string.remove_external_sdcard_default);
            }
            return label;
        }
        
        String[] udisks = storageManagerExtra.getUdiskPaths();
        if (udisks.length > 0) {
            
            for (int i = 0; i < udisks.length; i++) {
                if (udisks[i].contains(path)) {
                    udiskPath = udisks[i];
                    break;
                }
            }
            if (!TextUtils.isEmpty(udiskPath)) {
                label = storageManagerExtra.getVolumeLabel(udiskPath);
                if (TextUtils.isEmpty(label)) {
                    label = mContext.getString(R.string.remove_disk_default);
                }
            }
            return label;
        } 
        
        label = mContext.getString(R.string.remove_unknown_storage_default);
        return label;
    }

    /**
     * Unmount volume.
     * @param extStoragePath external storage path.
     * @param force boolean value.
     */
    public void doUnmount(String extStoragePath, boolean force) {
        Log.i(TAG, "doUnmount()");

        IMountService mountService = getMountService();

        try {
            mountService.unmountVolume(extStoragePath, force, false);
        } catch (RemoteException e) {
        }
    }

    private synchronized IMountService getMountService()
    {
        Log.i(TAG, "getMountService()");
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

}
