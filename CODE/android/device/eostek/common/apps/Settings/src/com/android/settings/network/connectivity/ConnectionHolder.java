
package com.android.settings.network.connectivity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class ConnectionHolder {
    private ConnectionActivity mConnectionActivity;

    private ConnectionLogic mConnectionLogic;

    private Button mBeginDiagBtn;

    private Button mErrDetailReturnBtn;

    private Button mErrorDetailBtn;

    private Button mConnReturnBtn;

    private ImageView mGatewayIv;

    private ImageView mCenterNetworkIv;

    private ImageView mTimeServerIv;

    private ProgressBar mConnGatewayPb;

    private ProgressBar mConnCenterNetworkPb;

    private ProgressBar mConnServerPb;

    private ProgressBar mGatewayRingPb;

    private ProgressBar mCenterNetworkRingPb;

    private ProgressBar mServerRingPb;

    private TextView mIpAddressTv;

    private TextView mErrDescriptionTv;

    private TextView mGatewayAddressTv;

    private TextView mAccessWayTv;

    private TextView mErrCodeTv;

    private TextView mConnSuccessTv;

    private TextView mConnFailTv;

    private static final int LAYOUT_CONN_BEGIN = 1;

    private static final int LAYOUT_CONN_PROGRESS = 2;

    private static final int LAYOUT_CONN_ERROR = 3;

    private static final int LOCAL_CONN_SUCCESS = 1;

    private static final int PING_GATEWAY_SUCCESS = 2;

    private static final int PING_CENTER_NETWORK_SUCCESS = 3;

    private static final int LOCAL_CONN_FAIL = 10;

    private static final int PING_GATEWAY_FAIL = 20;

    private static final int PING_CENTER_NETWORK_FAIL = 30;

    private static final int PING_SERVER_FAIL = 40;

    private static final int SUCCESS = 100;

    private static int mErrorCode;

    protected static final int NORMAL = 0;

    protected static final int NO_ETHERNET_ERR_CODE = 10000;

    protected static final int NO_WIFI_ERR_CODE = 10001;

    protected static final int LOCAL_NETWORK_CONN_ERR_CODE = 10002;

    protected static final int GATEWAY_ERR_CODE = 93;

    protected static final int CENTER_NETWORK_ERR_CODE = 10003;

    protected static final int TIME_SERVER_ERR_CODE = 10004;

    protected static final int UNKNOW_NETWORK_CONN_ERR_CODE = 10005;

    protected static final int TIME_SYNC_FAIL_ERR_CODE = 10006;

    protected static final int TIME_SYNC_WAIT_OVER_TIME_ERR_CODE = 10007;

    protected static final int CONN_CDN_SERVER_FAIL_ERR_CODE = 10008;

    protected static final int DOWNLOAD_FAIL_ERR_CODE = 10009;

    protected static final int NO_REQUEST_PAGE_ERR_CODE = 100010;

    protected static final int CANNOT_IDENTIFY_PAGE_ERR_CODE = 10011;

    public ConnectionHolder(ConnectionActivity connectionActivity, ConnectionLogic connectionLogic) {
        this.mConnectionActivity = connectionActivity;
        this.mConnectionLogic = connectionLogic;
        findViews(LAYOUT_CONN_BEGIN);
        registerListener();
        showStaticInfo();
    }

    private void findViews(int XML_NUMBER) {

        setTitleWidget();

        if (XML_NUMBER == LAYOUT_CONN_BEGIN) {
            mBeginDiagBtn = (Button) mConnectionActivity.findViewById(R.id.btn_begin_diag);
            mIpAddressTv = (TextView) mConnectionActivity.findViewById(R.id.tv_ip_address);
            mGatewayAddressTv = (TextView) mConnectionActivity.findViewById(R.id.tv_gateway_address);
            mAccessWayTv = (TextView) mConnectionActivity.findViewById(R.id.tv_accessway);
        }
        if (XML_NUMBER == LAYOUT_CONN_PROGRESS) {
            mErrorDetailBtn = (Button) mConnectionActivity.findViewById(R.id.btn_error_detail);
            mGatewayIv = (ImageView) mConnectionActivity.findViewById(R.id.iv_gateway);
            mCenterNetworkIv = (ImageView) mConnectionActivity.findViewById(R.id.iv_center_network);
            mTimeServerIv = (ImageView) mConnectionActivity.findViewById(R.id.iv_time_server);
            mConnGatewayPb = (ProgressBar) mConnectionActivity.findViewById(R.id.pb_conn_gateway);
            mConnGatewayPb.setMax(100);
            mConnGatewayPb.setProgress(0);
            mConnCenterNetworkPb = (ProgressBar) mConnectionActivity.findViewById(R.id.pb_conn_center_network);
            mConnCenterNetworkPb.setMax(100);
            mConnCenterNetworkPb.setProgress(0);
            mConnServerPb = (ProgressBar) mConnectionActivity.findViewById(R.id.pb_conn_server);
            mConnServerPb.setMax(100);
            mConnServerPb.setProgress(0);
            mGatewayRingPb = (ProgressBar) mConnectionActivity.findViewById(R.id.pb_ring_gateway);
            mCenterNetworkRingPb = (ProgressBar) mConnectionActivity.findViewById(R.id.pb_ring_center_network);
            mServerRingPb = (ProgressBar) mConnectionActivity.findViewById(R.id.pb_ring_server);
            mConnReturnBtn = (Button) mConnectionActivity.findViewById(R.id.btn_return);
            mConnSuccessTv = (TextView) mConnectionActivity.findViewById(R.id.tv_conn_success);
            mConnFailTv = (TextView) mConnectionActivity.findViewById(R.id.tv_conn_fail);
        }
        if (XML_NUMBER == LAYOUT_CONN_ERROR) {
            mErrDetailReturnBtn = (Button) mConnectionActivity.findViewById(R.id.btn_err_detail_return);
            mErrDescriptionTv = (TextView) mConnectionActivity.findViewById(R.id.tv_err_description);
            mErrCodeTv = (TextView) mConnectionActivity.findViewById(R.id.tv_err_code);
        }
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) mConnectionActivity.findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setMainTitleText(mConnectionActivity.getString(R.string.action_settings));
            tw.setFirstSubTitleText(mConnectionActivity.getString(R.string.network_settings), false);
            tw.setSecondSubTitleText(mConnectionActivity.getString(R.string.network_diag));
        }
    }

    private void showStaticInfo() {
        NetworkUtil networkUtil = NetworkUtil.getInstance(mConnectionActivity);
        if (networkUtil.isNetworkConnected()) {
            mIpAddressTv.setText(networkUtil.getIpAddress());
            mGatewayAddressTv.setText(networkUtil.getGateWay());
            mAccessWayTv.setText(networkUtil.getIpAssignment());
        }
    }

    public void registerListener() {

        mBeginDiagBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mConnectionActivity.setContentView(R.layout.connection_progress);
                findViews(LAYOUT_CONN_PROGRESS);

                // mErrorDetailBtn.setClickable(false);
                mErrorDetailBtn.setEnabled(false);

                mConnReturnBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mConnectionActivity.finish();
                    }
                });
                mErrorDetailBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showErrorDetails();
                    }
                });

                mErrorDetailBtn.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View arg0, boolean hasFocus) {
                        if (hasFocus) {
                        	mErrorDetailBtn.setTextColor(Color.rgb(255, 255, 255));
                        } else {
                        	mErrorDetailBtn.setTextColor(Color.rgb(0, 0, 0));
                        }
                    }
                });
                
                mConnReturnBtn.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View arg0, boolean hasFocus) {
                        if (hasFocus) {
                        	mConnReturnBtn.setTextColor(Color.rgb(255, 255, 255));
                        } else {
                        	mConnReturnBtn.setTextColor(Color.rgb(0, 0, 0));
                        }
                    }
                });
                
                
                mConnectionLogic.startCheckConnection(new ConnectionListener() {

                    public void onStatusChanged(int status, boolean success, int errorCode) {
                        if (success) {
                            refreshUI(SUCCESS);
                        } else {
                            if (errorCode == NORMAL) {
                                refreshUI(status);
                            } else {
                                mErrorDetailBtn.setEnabled(true);
                                mConnFailTv.setVisibility(View.VISIBLE);
                                mErrorCode = errorCode;
                            }
                        }
                    }
                });
            }
        });
    }

    private void showErrorDetails() {
        mConnectionActivity.setContentView(R.layout.connection_error);
        findViews(LAYOUT_CONN_ERROR);
        // EosTek Patch Begin
        // comment : fix focus problem
        mErrDetailReturnBtn.requestFocus();
        mErrDetailReturnBtn.setFocusable(true);
        // EosTek Patch End
        mErrCodeTv.setText("" + mErrorCode);
        switch (mErrorCode) {
            case LOCAL_NETWORK_CONN_ERR_CODE:
                mErrDescriptionTv.setText(R.string.local_network_conn_error);
                break;
            case GATEWAY_ERR_CODE:
                mErrDescriptionTv.setText(R.string.gateway_error);
                break;
            case CENTER_NETWORK_ERR_CODE:
                mErrDescriptionTv.setText(R.string.center_network_error);
                break;
            case TIME_SERVER_ERR_CODE:
                mErrDescriptionTv.setText(R.string.server_error);
                break;
        }
        mErrDetailReturnBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mConnectionActivity.finish();
            }
        });
    }

    Handler updateCenterNetworkBarHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mConnCenterNetworkPb.setProgress(msg.arg1);
            updateCenterNetworkBarHandler.post(CenterNetworkBarThread);
        }
    };

    Runnable CenterNetworkBarThread = new Runnable() {
        int Progress = 0;

        @Override
        public void run() {
            Progress = Progress + 2;
            Message msg = updateCenterNetworkBarHandler.obtainMessage();
            msg.arg1 = Progress;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateCenterNetworkBarHandler.sendMessage(msg);
            if (Progress == 100) {
                mCenterNetworkRingPb.setVisibility(View.VISIBLE);
                mConnectionLogic.isProgressFinish(true);
                updateCenterNetworkBarHandler.removeCallbacks(CenterNetworkBarThread);
            }
        }
    };

    Handler updateGatewayBarHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mConnGatewayPb.setProgress(msg.arg1);
            updateGatewayBarHandler.post(GatewayBarThread);
        }

    };

    Runnable GatewayBarThread = new Runnable() {
        int Progress = 0;

        @Override
        public void run() {
            Progress = Progress + 2;
            Message msg = updateGatewayBarHandler.obtainMessage();
            msg.arg1 = Progress;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateGatewayBarHandler.sendMessage(msg);
            if (Progress == 100) {
                mConnectionLogic.isProgressFinish(true);
                mGatewayRingPb.setVisibility(View.VISIBLE);
                updateGatewayBarHandler.removeCallbacks(GatewayBarThread);
            }
        }
    };

    Handler updateServerBarHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mConnServerPb.setProgress(msg.arg1);
            updateServerBarHandler.post(ServerBarThread);
        }

    };

    Runnable ServerBarThread = new Runnable() {
        int Progress = 0;

        @Override
        public void run() {
            Progress = Progress + 2;
            Message msg = updateServerBarHandler.obtainMessage();
            msg.arg1 = Progress;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateServerBarHandler.sendMessage(msg);
            if (Progress == 100) {
                mServerRingPb.setVisibility(View.VISIBLE);
                mConnectionLogic.isProgressFinish(true);
                updateServerBarHandler.removeCallbacks(ServerBarThread);
            }
        }
    };

    private void refreshUI(int status) {
        switch (status) {
            case LOCAL_CONN_SUCCESS:
                mConnGatewayPb.setVisibility(View.VISIBLE);
                updateGatewayBarHandler.post(GatewayBarThread);
                break;
            case PING_GATEWAY_SUCCESS:
                mGatewayRingPb.setVisibility(View.INVISIBLE);
                mGatewayIv.setVisibility(View.VISIBLE);
                mConnCenterNetworkPb.setVisibility(View.VISIBLE);
                updateCenterNetworkBarHandler.post(CenterNetworkBarThread);
                break;
            case PING_CENTER_NETWORK_SUCCESS:
                mCenterNetworkRingPb.setVisibility(View.INVISIBLE);
                mCenterNetworkIv.setVisibility(View.VISIBLE);
                mConnServerPb.setVisibility(View.VISIBLE);
                updateServerBarHandler.post(ServerBarThread);
                break;
            case SUCCESS:
                mServerRingPb.setVisibility(View.INVISIBLE);
                mTimeServerIv.setVisibility(View.VISIBLE);
                mConnSuccessTv.setVisibility(View.VISIBLE);
                // EosTek Patch Begin
                // comment : add focus
                mConnReturnBtn.requestFocus();
                mConnReturnBtn.setFocusable(true);
                // EosTek Patch End
                break;
            case LOCAL_CONN_FAIL:
                mErrorDetailBtn.setClickable(true);
                mConnFailTv.setVisibility(View.VISIBLE);
                break;
            case PING_GATEWAY_FAIL:
                mErrorDetailBtn.setClickable(true);
                mConnFailTv.setVisibility(View.VISIBLE);
                break;
            case PING_CENTER_NETWORK_FAIL:
                mErrorDetailBtn.setClickable(true);
                mConnFailTv.setVisibility(View.VISIBLE);
                break;
            case PING_SERVER_FAIL:
                mErrorDetailBtn.setClickable(true);
                mConnFailTv.setVisibility(View.VISIBLE);
                break;
        }
    }
}
