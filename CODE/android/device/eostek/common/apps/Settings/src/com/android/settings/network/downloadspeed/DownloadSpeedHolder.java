
package com.android.settings.network.downloadspeed;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.network.connectivity.NetworkUtil;
import com.android.settings.widget.TitleWidget;
import com.android.settings.R;

public class DownloadSpeedHolder {
    private static final String TAG = "NetworkDiag";

    private DownloadSpeedActivity mDownloadSpeedActivity;

    private DownloadSpeedLogic mDownloadSpeedLogic;

    private NetworkUtil mNetworkUtil;

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
        this.mDownloadSpeedActivity = downloadSpeedActivity;
        this.mDownloadSpeedLogic = downloadSpeedLogic;
        mDownloadSpeedActivity.setContentView(R.layout.download_speed_begin);
        findViews(LAYOUT_DOWNLOADSPEED_BEGIN);
        registerListener();
        showStaticInfo();
    }

    private void findViews(int layout) {
        Log.i(TAG, ">>>>>Enter into findViews");
        Log.i(TAG, ">>>>>layout = " + layout);

        setTitleWidget();

        if (layout == LAYOUT_DOWNLOADSPEED_BEGIN) {
            mIpAddressTextView = (TextView) mDownloadSpeedActivity.findViewById(R.id.tv_ip_address);
            mAccessWayTextView = (TextView) mDownloadSpeedActivity.findViewById(R.id.tv_access_way);
            mGatewayAddressTextView = (TextView) mDownloadSpeedActivity.findViewById(R.id.tv_gateway_address);
            mBeginDownloadSpeedBtn = (Button) mDownloadSpeedActivity.findViewById(R.id.btn_begin_download_speed);
        } else if (layout == LAYOUT_DOWNLOADSPEED_PROGRESS) {
            mMaxDownloadSpeedTextView = (TextView) mDownloadSpeedActivity.findViewById(R.id.tv_max_speed);
            mMinDownloadSpeedTextView = (TextView) mDownloadSpeedActivity.findViewById(R.id.tv_min_speed);
            mAvgDownloadSpeedTextView = (TextView) mDownloadSpeedActivity.findViewById(R.id.tv_average_speed);
            mProgressDownloadSpeedTextView = (TextView) mDownloadSpeedActivity
                    .findViewById(R.id.tv_test_speed_progress);
            mDownloadSpeedProgressBar = (DownloadSpeedProgressbar) mDownloadSpeedActivity
                    .findViewById(R.id.progressbar);
            mDownloadSpeedTips = (TextView) mDownloadSpeedActivity.findViewById(R.id.tv_downloadspeed_tips);
            mTestSpeedReturnBtn = (Button) mDownloadSpeedActivity.findViewById(R.id.btn_test_speed_return);
        }
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) mDownloadSpeedActivity.findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setMainTitleText(mDownloadSpeedActivity.getString(R.string.action_settings));
            tw.setFirstSubTitleText(mDownloadSpeedActivity.getString(R.string.network_settings), false);
            tw.setSecondSubTitleText(mDownloadSpeedActivity.getString(R.string.download_speed));
        }
    }

    private void showStaticInfo() {
        Log.i(TAG, ">>>>>Enter into showStaticInfo");
        mNetworkUtil = NetworkUtil.getInstance(mDownloadSpeedActivity);
        Log.i(TAG, ">>>>>The network connection status is : = " + mNetworkUtil.isNetworkConnected());
        if (mNetworkUtil.isNetworkConnected()) {
            mIpAddressTextView.setText(mNetworkUtil.getIpAddress());
            mAccessWayTextView.setText(mNetworkUtil.getGateWay());
            mGatewayAddressTextView.setText(mNetworkUtil.getIpAssignment());
        } else {
            mNetworkUtil = NetworkUtil.getInstance(mDownloadSpeedActivity);
            mIpAddressTextView.setText("0.0.0.0");
            mAccessWayTextView.setText("255.255.255.255");
            mGatewayAddressTextView.setText("0.0.0.0");
        }
    }

    public void registerListener() {
        mBeginDownloadSpeedBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mNetworkUtil.isNetworkConnected()) {
                    mDownloadSpeedActivity.setContentView(R.layout.download_speed_progress);
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
                            mDownloadSpeedActivity.finish();
                        }
                    });
                    mDownloadSpeedLogic.startCheckDownloadSpeed(new DownloadSpeedListener() {

                        @Override
                        public void onDownloadSpeedChanged(int max, int min, int avg, boolean complete) {
                            Log.i(TAG, ">>>>>Enter into onDownloadSpeedChanged");
                            refreshDownloadSpeed(max, min, avg);
                            if (complete) {
                                String downloadCompeleteTips = mDownloadSpeedActivity.getResources().getString(
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
                    Toast.makeText(mDownloadSpeedActivity, mDownloadSpeedActivity.getString(R.string.connection_fail),
                            Toast.LENGTH_SHORT).show();
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
        if(progress>100){
            progress=100;
        }
        mProgressDownloadSpeedTextView.setText(progress + "%");
        mDownloadSpeedProgressBar.setProgress(progress);
    }
}
