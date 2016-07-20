
package com.android.settings.deviceinfo.fragments;

import scifly.device.Device;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.deviceinfo.DeviceInfoActivity;
import com.android.settings.deviceinfo.business.DeviceInfoLogic;
import com.android.settings.deviceinfo.business.MoreInfoLogic;
import com.android.settings.util.Utils;

public class MoreInfoFragment extends Fragment {

    private DeviceInfoActivity mActivity;

    /**
     * cpu name
     */
    private TextView tv_about_more_cpu_value;

    /**
     * RAM value
     */
    private TextView tv_about_more_ram_value;

    /**
     * Flash value
     */
    private TextView tv_about_more_flash_value;

    /**
     * MAC value
     */
    private TextView tv_about_more_mac_value;

    /**
     * time after boot
     */
    private TextView tv_about_more_up_time_value;

    /**
     * system version
     */
    private TextView tv_about_more_version_value;

    /**
     * SciflyVersion
     */
    private TextView tv_about_more_up_version_real_value;

    private static final int EVENT_UPDATE_STATS = 500;

    private MoreInfoLogic mMoreInfoLogic;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_UPDATE_STATS:
                    tv_about_more_up_time_value.setText(mMoreInfoLogic.updateTimes());
                    sendEmptyMessageDelayed(EVENT_UPDATE_STATS, 1000);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_more_info, container, false);
        findViews(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (DeviceInfoActivity) getActivity();
        mActivity.setSubTitle(R.string.about_more_info);
        mMoreInfoLogic = new MoreInfoLogic(mActivity);
        initViews();
    }

    /**
     * @Title: findViews
     * @param: @param root
     * @return: void
     * @throws
     */
    private void findViews(View root) {
        tv_about_more_cpu_value = (TextView) root.findViewById(R.id.tv_about_more_cpu_value);
        tv_about_more_ram_value = (TextView) root.findViewById(R.id.tv_about_more_ram_value);
        tv_about_more_flash_value = (TextView) root.findViewById(R.id.tv_about_more_flash_value);
        tv_about_more_mac_value = (TextView) root.findViewById(R.id.tv_about_more_mac_value);
        tv_about_more_up_time_value = (TextView) root.findViewById(R.id.tv_about_more_up_time_value);
        tv_about_more_version_value = (TextView) root.findViewById(R.id.tv_about_more_version_value);
        tv_about_more_up_version_real_value = (TextView) root.findViewById(R.id.tv_about_more_up_version_real_value);
    }

    /**
     * @Title: initViews
     * @param:
     * @return: void
     * @throws
     */
    private void initViews() {
        tv_about_more_cpu_value.setText(mMoreInfoLogic.getCpuName());
        tv_about_more_ram_value.setText(mMoreInfoLogic.getTotalMemory());
        // To determine whether the virtual moun
        if (Environment.isExternalStorageEmulated()) {
            tv_about_more_flash_value.setText(Utils.formatStorageSizeLong(mActivity, mMoreInfoLogic.getRomTotalSize()
                    + mMoreInfoLogic.getSystemSpace()));
        } else {
            tv_about_more_flash_value.setText(Utils.formatStorageSizeLong(mActivity, mMoreInfoLogic.getRomTotalSize()
                    + mMoreInfoLogic.getExternalSDTotalSize() + mMoreInfoLogic.getSystemSpace()));
        }
        tv_about_more_mac_value.setText(mMoreInfoLogic.getLocalEthernetMacAddress());
        mHandler.sendEmptyMessage(EVENT_UPDATE_STATS);
        tv_about_more_version_value.setText(Build.VERSION.RELEASE);
        tv_about_more_up_version_real_value.setText(DeviceInfoLogic.getBuildNumber());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeMessages(EVENT_UPDATE_STATS);
    }

}
