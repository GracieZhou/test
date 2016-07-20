
package com.android.settings.network.screendisplay;

import scifly.device.Device;

import com.android.settings.R;
import com.android.settings.network.NetworkSettingActivity;
import com.android.settings.network.downloadspeed.DownloadSpeedActivity;
import com.android.settings.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;

public class ScreenDisplayFragment extends Fragment {

    public static final String TAG = "ScreenDisplay";

    private TextView DeviceID;

    private TextView connectStatus;

    private Button mBtn;

    private ImageView mLeftImage;

    private ImageView mCenterImage;

    private ImageView mRightImage;

    private WifiManager mWifiManager;

    private final String PLATFORM = SystemProperties.get("ro.board.platform", "");

    // 638
    private final String PLATFORM_MONET = "monet";

    // 828
    private final String PLATFORM_MUJI = "muji";

    // 628
    private static final String PLATFORM_MADISON = "madison";

    // 805
    private static final String PLATFORM_MESON8 = "meson8";

    private final String LISTEN_ON = "mcast_listen";

    private final String ENABLE_LISTEN = "net.wfd.enable";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_screen_display, container, false);
        ((NetworkSettingActivity) getActivity()).setSubTitle(R.string.screen_display);

        if (PLATFORM.equals(PLATFORM_MUJI) || PLATFORM.equals(PLATFORM_MONET)) {
            Settings.Global.putInt(getActivity().getContentResolver(), LISTEN_ON, 1);
            SystemProperties.set(ENABLE_LISTEN, "1");
        } else {
            Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.WIFI_DISPLAY_ON, 1);
        }

        DeviceID = (TextView) root.findViewById(R.id.id);

        mLeftImage = (ImageView) root.findViewById(R.id.mcast_left_image);
        mCenterImage = (ImageView) root.findViewById(R.id.mcast_center_image);
        mRightImage = (ImageView) root.findViewById(R.id.mcast_right_image);
        setImage();

        DeviceID.setText(Device.getDeviceName(getActivity()));
        DeviceID.setTextColor(getActivity().getResources().getColor(R.color.green));
        connectStatus = (TextView) root.findViewById(R.id.connect_information);
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            connectStatus.setText(getActivity().getResources().getString(R.string.connected));
        } else {
            connectStatus.setText(getActivity().getResources().getString(R.string.waiting));
            showWifiDialog();
        }
        mBtn = (Button) root.findViewById(R.id.help);
        registerListener();
        return root;
    }

    /**
     * Whether Open WiFi
     */
    private void showWifiDialog() {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_wifi_state_screen_display, null);
        Button ok = (Button) dialogView.findViewById(R.id.wifi_button_ok);
        Button cancle = (Button) dialogView.findViewById(R.id.wifi_button_cancle);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView).create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 700;
        params.height = 320;
        dialog.getWindow().setAttributes(params);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mWifiManager.setWifiEnabled(true);
                connectStatus.setText(getActivity().getResources().getString(R.string.connected));
            }
        });

        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                connectStatus.setText(getActivity().getResources().getString(R.string.connecting));
            }
        });
    }

    public void registerListener() {
        mBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.intentForward(getActivity(), HelpActivity.class);
            }
        });
        mBtn.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    mBtn.setTextColor(getActivity().getResources().getColor(R.color.white));
                } else {
                    mBtn.setTextColor(getActivity().getResources().getColor(R.color.dark));
                }
            }
        });
    }

    private void setImage() {
        if ("CN".equals(getCurrentLanguage())) {
            mLeftImage.setBackgroundResource(R.drawable.mricast_cn_left);
            mCenterImage.setBackgroundResource(R.drawable.mricast_cn_center);
            mRightImage.setBackgroundResource(R.drawable.mricast_cn_right);
        } else if ("TW".equals(getCurrentLanguage())) {
            mLeftImage.setBackgroundResource(R.drawable.mricast_tw_left);
            mCenterImage.setBackgroundResource(R.drawable.mricast_tw_center);
            mRightImage.setBackgroundResource(R.drawable.mricast_tw_right);
        } else {
            mLeftImage.setBackgroundResource(R.drawable.mricast_en_left);
            mCenterImage.setBackgroundResource(R.drawable.mricast_en_center);
            mRightImage.setBackgroundResource(R.drawable.mricast_en_right);
        }
    }

    /**
     * @param mActivity
     * @return the current language
     */
    private String getCurrentLanguage() {
        Configuration conf = getActivity().getResources().getConfiguration();
        String language = conf.locale.getCountry();
        return language;
    }
}
