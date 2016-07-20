
package com.android.settings.network.downloadspeed;

import scifly.middleware.network.IpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;
import scifly.middleware.network.StaticIpConfig;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.settings.widget.TitleWidget;

public class DownloadSpeedHolder {
    private static final String TAG = "NetworkDiag";

    private DownloadSpeedActivity mActivity;

    private DownloadSpeedLogic mDownloadSpeedLogic;

    private TextView mIpAddressTextView;

    private TextView mAccessWayTextView;

    private TextView mGatewayAddressTextView;

    private Button mBeginDownloadSpeedBtn;

    private TextView mMaxDownloadSpeedTextView;

    private TextView mMinDownloadSpeedTextView;

    private TextView mAvgDownloadSpeedTextView;

    private TextView mProgressDownloadSpeedTextView;

    private DownloadSpeedProgressbar mDownloadSpeedProgressBar;

    private TextView mDownloadSpeedTips;

    private Button mTestSpeedReturnBtn;

    private static final int LAYOUT_DOWNLOADSPEED_BEGIN = 0;

    private static final int LAYOUT_DOWNLOADSPEED_PROGRESS = 1;

    public DownloadSpeedHolder(DownloadSpeedActivity downloadSpeedActivity, DownloadSpeedLogic downloadSpeedLogic) {
        this.mActivity = downloadSpeedActivity;
        this.mDownloadSpeedLogic = downloadSpeedLogic;
        mActivity.setContentView(R.layout.download_speed_begin);
        findViews(LAYOUT_DOWNLOADSPEED_BEGIN);
        registerListener();
        showStaticInfo();
    }

    private void findViews(int layout) {
        Log.i(TAG, ">>>>>Enter into findViews");
        Log.i(TAG, ">>>>>layout = " + layout);

        setTitleWidget();

        if (layout == LAYOUT_DOWNLOADSPEED_BEGIN) {
            mIpAddressTextView = (TextView) mActivity.findViewById(R.id.tv_ip_address);
            mAccessWayTextView = (TextView) mActivity.findViewById(R.id.tv_access_way);
            mGatewayAddressTextView = (TextView) mActivity.findViewById(R.id.tv_gateway_address);
            mBeginDownloadSpeedBtn = (Button) mActivity.findViewById(R.id.btn_begin_download_speed);
        } else if (layout == LAYOUT_DOWNLOADSPEED_PROGRESS) {
            mMaxDownloadSpeedTextView = (TextView) mActivity.findViewById(R.id.tv_max_speed);
            mMinDownloadSpeedTextView = (TextView) mActivity.findViewById(R.id.tv_min_speed);
            mAvgDownloadSpeedTextView = (TextView) mActivity.findViewById(R.id.tv_average_speed);
            mProgressDownloadSpeedTextView = (TextView) mActivity.findViewById(R.id.tv_test_speed_progress);
            mDownloadSpeedProgressBar = (DownloadSpeedProgressbar) mActivity.findViewById(R.id.progressbar);
            mDownloadSpeedTips = (TextView) mActivity.findViewById(R.id.tv_downloadspeed_tips);
            mTestSpeedReturnBtn = (Button) mActivity.findViewById(R.id.btn_test_speed_return);
        }
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) mActivity.findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setSubTitleText(mActivity.getString(R.string.network_settings),
                    mActivity.getString(R.string.download_speed));
        }
    }

    private void showStaticInfo() {
        IpConfig ipConfig = mDownloadSpeedLogic.getIpConfig();
        StaticIpConfig staticIpConfig = ipConfig == null ? null : ipConfig.getStaticIpConfig();
        if (staticIpConfig != null) {
            if (staticIpConfig.ipAddress != null) {
                mIpAddressTextView.setText(staticIpConfig.ipAddress.getAddress().getHostAddress());
            }
            if (staticIpConfig.gateway != null) {
                mGatewayAddressTextView.setText(staticIpConfig.gateway.getHostAddress());
            }

            mAccessWayTextView.setText(ipConfig.ipAssignment == IpAssignment.DHCP ? "DHCP" : "STATIC");
        } else {
            mIpAddressTextView.setText("0.0.0.0");
            mGatewayAddressTextView.setText("0.0.0.0");
            mAccessWayTextView.setText(mActivity.getResources().getString(R.string.unassigned));
        }
    }

    public void registerListener() {
        mBeginDownloadSpeedBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mDownloadSpeedLogic.isNetworkConnected()) {
                    mActivity.setContentView(R.layout.download_speed_progress);
                    findViews(LAYOUT_DOWNLOADSPEED_PROGRESS);
                    // EosTek Patch Begin
                    // comment : fix focus problem
                    mTestSpeedReturnBtn.requestFocus();
                    mTestSpeedReturnBtn.setFocusable(true);
                    // EosTek Patch End
                    refreshDownloadSpeed(0, 0, 0);
                    refreshProgressBar(0);
                    mTestSpeedReturnBtn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            if (mDownloadSpeedLogic.mDonwloadCountTimer != null) {
                                Log.i("mDownloadSpeedLogic", "mDownloadSpeedLogic: " + mDownloadSpeedLogic);
                                mDownloadSpeedLogic.stopDownload();
                                mDownloadSpeedLogic.mDonwloadCountTimer.cancel();
                            }
                            mActivity.finish();
                            mActivity.overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
                        }
                    });
                    mDownloadSpeedLogic.startCheckDownloadSpeed(new DownloadSpeedListener() {

                        @Override
                        public void onDownloadSpeedChanged(int max, int min, int avg, boolean complete) {
                            Log.i(TAG, ">>>>>Enter into onDownloadSpeedChanged");
                            refreshDownloadSpeed(max, min, avg);
                            if (complete) {
                                String downloadCompeleteTips = mActivity.getResources().getString(
                                        R.string.download_speed_complete);
                                mDownloadSpeedTips.setText(downloadCompeleteTips);
                                mDownloadSpeedTips.setTextColor(Color.rgb(255, 116, 00));
                            }
                        }

                        @Override
                        public void onDownloadProgress(int progress) {
                            Log.i(TAG, ">>>>>Enter into onDownloadProgress");
                            refreshProgressBar(progress);
                        }
                    });
                } else {
                    Utils.showToast(mActivity, R.string.connection_fail);
                }
            }
        });
    }

    private void refreshDownloadSpeed(int max, int min, int avg) {
        mMaxDownloadSpeedTextView.setText(max / 1024 + "KB/s");
        mMinDownloadSpeedTextView.setText(min / 1024 + "KB/s");
        mAvgDownloadSpeedTextView.setText(avg / 1024 + "KB/s");
    }

    private void refreshProgressBar(int progress) {
        if (progress > 100) {
            progress = 100;
        }
        mProgressDownloadSpeedTextView.setText(progress + "%");
        mDownloadSpeedProgressBar.setProgress(progress);
    }
}
