
package scifly.storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.storage.IMountService;
import android.os.storage.StorageVolume;
import android.util.Log;
import scifly.device.Device;
import android.text.TextUtils;

public class StorageManagerExtra {

    private static final String TAG = "StorageManagerExtra";

    private static final boolean DBG = true;

    static final Object mObject = new Object();

    private static StorageManagerExtra mInstance = null;

    IMountService mService = null;

    Looper mLooper;
    
    private String mPlatform = null;

    private StorageManagerExtra(IMountService service, Looper looper) {
        mService = service;
        mLooper = looper;
        mPlatform = SystemProperties.get("ro.scifly.platform", "");
    }

    public static StorageManagerExtra getInstance(Context context) {
        if (mInstance == null) {
            synchronized (mObject) {
                if (mInstance == null) {
                    IBinder b = ServiceManager.getService("mount");
                    mInstance = new StorageManagerExtra(IMountService.Stub.asInterface(b), context.getMainLooper());
                }
            }
        }

        return mInstance;
    }

    /**
     * Gets the state of a volume via its mountpoint.
     * 
     * @param mountPoint point for volume
     * @return the state of the volume
     */
    public String getVolumeState(String mountPoint) {
        if (mService == null)
            return Environment.MEDIA_REMOVED;

        try {
            return mService.getVolumeState(mountPoint);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get volume state", e);
            return "";
        }
    }

    /**
     * Returns list of all mountable volumes.
     * 
     * @hide
     */
    public StorageVolume[] getVolumeList() {
        if (mService == null) {
            return new StorageVolume[0];
        }

        try {
            Parcelable[] list = mService.getVolumeList();
            if (list == null) {
                return new StorageVolume[0];
            }

            int length = list.length;
            StorageVolume[] result = new StorageVolume[length];
            for (int i = 0; i < length; i++) {
                result[i] = (StorageVolume) list[i];
            }
            return result;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get volume list", e);
            return null;
        }
    }

    /**
     * Returns list of paths for all mountable volumes.
     * 
     * @return list of paths for all mountable volumes
     */
    public String[] getVolumePaths() {
        StorageVolume[] volumes = getVolumeList();
        if (volumes == null) {
            return null;
        }

        int count = volumes.length;
        String[] paths = new String[count];
        for (int i = 0; i < count; i++) {
            paths[i] = volumes[i].getPath();
        }

        return paths;
    }

    /**
     * Returns the file system volume label for the volume mounted at the given
     * mount point, or null for failure
     * 
     * @param mountPoint point for volume
     * @return volume label or null
     */
    public String getVolumeLabel(String mountPoint) {

        try {
            return mService.getVolumeLabel(mountPoint);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get volume label", e);
            return "";
        }
    }

    /**
     * Returns the file system volume uuid for the volume mounted at the given
     * mount point, or null for failure
     * 
     * @param mountPoint point for volume
     * @return volume uuid or null
     */
    public String getVolumeUUID(String mountPoint) {
        try {
            return mService.getVolumeUUID(mountPoint);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get volume uuid", e);
            return "";
        }
    }

    /**
     * Returns the path of Extrenal Sdcard.
     * 
     * @return external sdcard path or null
     * @since API 2.2
     */
    public String getExternalSdcardPath() {
        StorageVolume[] volumes = getVolumeList();
        if (volumes == null) {
            return null;
        }
        
        String assumeExternalSdRoot = "/mnt/external_sd";
        
        if(Device.SCIFLY_PLATFORM_DONGLE.equals(mPlatform)) {
            assumeExternalSdRoot = "/storage/external_storage/sdcard1";
        }

        for (StorageVolume volume : volumes) {
            String path = volume.getPath();
            if (DBG) {
                Log.d(TAG, "volume path :" + path);
            }

            // FIXME
            
            if (path.equals(assumeExternalSdRoot) && Environment.MEDIA_MOUNTED.equals(volume.getState())) {
                return path;
            }
        }

        return "";
    }


    /**
     * Gets the path list of mounted Udisk .
     * 
     * @return UdiskPaths which was or were mounted, if no udisk was mounted ,
     *         then return (new String[0]) which length is zero;
     * @since API 2.0
     * @author frank.zhang
     */
    public String[] getUdiskPaths() {
        ArrayList<String> udiskPaths = new ArrayList<String>();
        String oneLine = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/mounts")));
            while ((oneLine = br.readLine()) != null) {

                if (oneLine.startsWith("/dev/block/vold/8")) {
                    String[] splits = oneLine.split(" ");
                    if (splits.length > 2) {
                        udiskPaths.add(splits[1].replace("\\040", " "));
                    }
                }
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        int size = udiskPaths.size();
        return (String[]) udiskPaths.toArray(new String[size]);
    }

    public String getVolumeFsType(String mountPoint) {
        if(TextUtils.isEmpty(mountPoint)) {
            return null;
        }
        String oneLine = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/mounts")));
            while ((oneLine = br.readLine()) != null) {
                    String[] parts = oneLine.split(" ");
                    if (parts.length > 3) {
                        String mp = parts[1].replace("\\040", " ");
                        if(mountPoint.equals(mp)) {
                            return parts[2];
                        }
                    }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return null;
    }
}
