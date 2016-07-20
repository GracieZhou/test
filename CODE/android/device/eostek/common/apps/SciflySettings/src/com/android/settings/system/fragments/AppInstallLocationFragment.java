
package com.android.settings.system.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import scifly.storage.StorageManagerExtra;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.system.SystemSettingsActivity;
import com.android.settings.system.fragments.adapter.AppInstallLocationAdapter;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.Environment;

public class AppInstallLocationFragment extends Fragment {
    public static final int INSTALL_LOCATION_AUTO = 0;

    public static final int INSTALL_LOCATION_UDISK = 3;

    public static final String TAG = "InstallLocationHolder";

    public static final String USB_PREFIX = "USB - ";

    private SystemSettingsActivity mActivity;

    private ListView mLocations;

    private String[] locationStrings;

    private AppInstallLocationAdapter mAdapter;

    private ArrayList<HashMap<String, Object>> data = null;

    private HashMap<String, String> labelMap = new HashMap<String, String>();

    private boolean isNTFS = false;

    public static final int USB_STATE_ON = 0x00021;

    public static final int USB_STATE_OFF = 0x00022;

    private UsbStateReceiver UsbReceiver = new UsbStateReceiver();
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case USB_STATE_OFF:
                    Log.i(TAG, "USB_STATE_OFF-->USB_STATE_OFF");
                    break;
                case USB_STATE_ON:
                    Log.i(TAG, "USB_STATE_ON-->USB_STATE_ON");
                    break;
                default:
                    break;
            }
            registerAdapter();
            mAdapter.notifyDataSetChanged();
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.system_setting_layout, container, false);
        mLocations = (ListView) root.findViewById(R.id.list_view);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = (SystemSettingsActivity) getActivity();
        mActivity.setSubTitle(R.string.install_location);

        getUsbFormat();
        registerListener();
        registerAdapter();
        registerUSBReceiver();
    }

    public boolean getUsbFormat() {
        StorageManagerExtra sm = StorageManagerExtra.getInstance(getActivity().getApplicationContext());
        for (StorageVolume volume : sm.getVolumeList()) {
            String path = volume.getPath();
            String fsType = sm.getVolumeFsType(path);

            if (!TextUtils.isEmpty(fsType) && fsType.contains("ntfs")) {
                isNTFS = true;

            }

        }
        return isNTFS;
    }

    public void registerAdapter() {
        locationStrings = new String[] {
                mActivity.getString(R.string.install_location_auto), USB_PREFIX
        };
        if (data == null) {
            data = new ArrayList<HashMap<String, Object>>();
        }
        data.clear();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("image", R.drawable.circle);
        map.put("text", locationStrings[0]);
        data.add(map);

        StorageManagerExtra sm = StorageManagerExtra.getInstance(mActivity);
        final StorageManager storage = StorageManager.from(mActivity);
        StorageVolume[] volumes = storage.getVolumeList();
        for (StorageVolume volume : volumes) {
            if (!volume.isPrimary() && Environment.MEDIA_MOUNTED.equals(volume.getState())) {
                String label = sm.getVolumeLabel(volume.getPath());
                if(TextUtils.isEmpty(label)) {
                    label = "NONE";
                }
                if (volume.getPath().contains("sdcard1") || volume.getPath().contains("external_sd")) {

                } else {
                map = new HashMap<String, Object>();
                map.put("image", R.drawable.circle);
                map.put("text", locationStrings[1] + label);
                data.add(map);
                labelMap.put(volume.getPath(), locationStrings[1] + label);
                }
            }
        }

        mAdapter = new AppInstallLocationAdapter(getActivity(), data);
        mAdapter.setCurrentAppInstallLocation(getDefaultInstallLocationId());
        mLocations.setAdapter(mAdapter);

    }

    public void registerListener() {
        mLocations.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isNTFS) {

                    if (position == 1) {
                        setDefaultInstallLocation(0);
                        View childAtImfirst = mLocations.getChildAt(0);
                        mAdapter.setCurrentAppInstallLocation(0);
                        ImageView imfirst = (ImageView) childAtImfirst.findViewById(R.id.system_settings_inputmethodIV);

                        imfirst.setVisibility(View.VISIBLE);

                        View childAtImsecond = mLocations.getChildAt(1);
                        ImageView imsecond = (ImageView) childAtImsecond
                                .findViewById(R.id.system_settings_inputmethodIV);

                        imsecond.setVisibility(View.INVISIBLE);
                    } else if (position == 0) {
                        // do nothing
                        setDefaultInstallLocation(position);
                        mAdapter.setCurrentAppInstallLocation(position);
                    }
                } else {
                    setDefaultInstallLocation(position);
                    mAdapter.setCurrentAppInstallLocation(position);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mLocations.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isNTFS) {
                    if (position == 1) {
                        Toast makeText = Toast.makeText(getActivity().getApplicationContext(),
                                mActivity.getString(R.string.install_location_toast), Toast.LENGTH_SHORT);
                        makeText.show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void registerUSBReceiver() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addDataScheme("file");
        getActivity().registerReceiver(UsbReceiver, filter);
    }

    public int getDefaultInstallLocationId() {
        int location = Settings.Global.getInt(mActivity.getContentResolver(), Global.DEFAULT_INSTALL_LOCATION,
                INSTALL_LOCATION_AUTO);

        if (location == INSTALL_LOCATION_UDISK) {
            String mountPoint = android.os.SystemProperties.get("persist.sys.install.usb");

            if (!TextUtils.isEmpty(mountPoint)) {
                String label = labelMap.get(mountPoint);
                Log.d(TAG, "lable:" + label);
                location = 0;
                for (HashMap<String, Object> cell : data) {

                    if (cell.get("text").equals(label)) {
                        Log.d(TAG, "match with usb:" + label + ", location : " + location);
                        return location;
                    }
                    location++;
                }
            }
        }
        return location;
    }

    private String getUsbMountPoint(String label) {
        if (labelMap.isEmpty()) {
            return "";
        }

        for (Map.Entry<String, String> entry : labelMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.equals(label)) {
                return key;
            }
        }
        return "";
    }

    public boolean setDefaultInstallLocation(int installLoation) {

        String text = (String)data.get(installLoation).get("text");
        if (text.startsWith(USB_PREFIX)) {
            android.os.SystemProperties.set("persist.sys.install.usb", getUsbMountPoint(text));
            return Settings.Global.putInt(mActivity.getContentResolver(), Global.DEFAULT_INSTALL_LOCATION,
                    INSTALL_LOCATION_UDISK);
        }
        return Settings.Global.putInt(mActivity.getContentResolver(), Global.DEFAULT_INSTALL_LOCATION, installLoation);
    }

    public class UsbStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (handler == null) {
                return;
            }
            Message msg = new Message();

            if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
                    || intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
                msg.arg1 = USB_STATE_ON;
            } else {
                msg.arg1 = USB_STATE_OFF;
            }
            handler.sendEmptyMessage(msg.arg1);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler = null;
        getActivity().unregisterReceiver(UsbReceiver);
    }

}
