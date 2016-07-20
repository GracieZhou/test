
package com.eostek.isynergy.setmeup.network;

import java.util.List;

import scifly.middleware.network.AccessPoint;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.utils.Utils;

public class NetworkHolder {
    private LinearLayout mWifiPswLl;

    private EditText mWifiPwdEt;

    private CheckBox mShowPwdCb;

    private Button mPwdEnterBtn;

    private TextView mPwdErrTv;

    LinearLayout mWaitingLl;

    TextView mConnectingTv;

    private LinearLayout mConnResultLl;

    private ImageView mResultIv;

    private TextView mResultTv;

    private ListView mWifiLv;

    private NetworkFragment mNetworkFragment;

    private Context mContext;

    private View mNetworkLayout;

    public AccessPoint mSelectAccessPoint;

    public final static int ETHERNET_CONN_FAIL = 2;

    public final static int ETHERNET_SCANNING = 3;

    private static final int SHOW_VIEW_WAITING = 1;

    private static final int SHOW_VIEW_WIFILIST = 2;

    private static final int SHOW_VIEW_PSW = 3;

    private static final int SHOW_VIEW_RESULT = 4;

    private static final int SHOW_VIEW_CONNECTING = 5;

    static final int SHOW_VIEW_WIFI_CONNECT_FAIL = 6;

    private static final int SHOW_VIEW_WIFI_CONNECT_SUCCESS = 7;

    static final int SHOW_VIEW_CONNECT_FAIL = 8;

    static final int SHOW_SCAN_WIFI_TV = 9;

    static final int SHOW_OPEN_WIFI_FAILED_TV = 10;

    static final int SHOW_SCAN_RESULT = 11;


    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            resetAllViews();
            switch (msg.what) {
                case SHOW_VIEW_WAITING:
                    showWaitingView();
                    break;
                case SHOW_VIEW_WIFILIST:
                    showWifiListView();
                    break;
                case SHOW_VIEW_PSW:
                    showPasswordView();
                    break;
                case SHOW_VIEW_RESULT:
                    showConnectResultView();
                    break;
                case SHOW_VIEW_CONNECTING:
                    showConnectingView();
                    break;
                case SHOW_VIEW_WIFI_CONNECT_FAIL:
                    showWifiConnectFailView();
                    break;
                case SHOW_VIEW_WIFI_CONNECT_SUCCESS:
                    showWifiConnectSuccessView();
                    break;
                case SHOW_VIEW_CONNECT_FAIL:
                    showConnectFailView();
                    break;
                case SHOW_SCAN_WIFI_TV:{
                    mWaitingLl.setVisibility(View.VISIBLE);
                    mConnectingTv.setText(R.string.scan_wifi);
                }
                break;
                case SHOW_OPEN_WIFI_FAILED_TV:{
                    mWaitingLl.setVisibility(View.VISIBLE);
                    mConnectingTv.setText(R.string.failed_open_wifi);
                }
                break;
                case SHOW_SCAN_RESULT:{
                    mNetworkFragment.showWifiList();
                }
                break;
                default:
                    break;
            }
        }

    };

    private static final String TAG = NetworkHolder.class.getSimpleName();

    public NetworkHolder(NetworkFragment networkFragment, View view) {
        this.mNetworkFragment = networkFragment;
        this.mContext = networkFragment.getActivity();
        this.mNetworkLayout = view;

        findViews();
    }

    private void findViews() {
        mWaitingLl = (LinearLayout) mNetworkLayout.findViewById(R.id.ll_progress);
        mConnectingTv = (TextView) mNetworkLayout.findViewById(R.id.tv_connecting);

        mConnResultLl = (LinearLayout) mNetworkLayout.findViewById(R.id.ll_conn_result);
        mResultIv = (ImageView) mConnResultLl.findViewById(R.id.iv_conn_result);
        mResultTv = (TextView) mConnResultLl.findViewById(R.id.tv_conn_result);
        mWifiPswLl = (LinearLayout) mNetworkLayout.findViewById(R.id.ll_wifi_psw);
        mWifiPwdEt = (EditText) mWifiPswLl.findViewById(R.id.et_psw);
        mShowPwdCb = (CheckBox) mWifiPswLl.findViewById(R.id.cb_show_psw);
        mPwdEnterBtn = (Button) mWifiPswLl.findViewById(R.id.btn_wifi_psw);
        mPwdErrTv = (TextView) mWifiPswLl.findViewById(R.id.tv_error_psw);
        mWifiLv = (ListView) mNetworkLayout.findViewById(R.id.lv_wifi);

        mWifiLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                mNetworkFragment.autoScan = false;
                print(String.valueOf(position));
                String ssid = ((ScanResult) (mWifiLv.getAdapter().getItem(position))).SSID;
                print("ssid=" + ssid);
                mNetworkFragment.mSelectAccessPoint = mNetworkFragment.getAccessPointBySSID(ssid);
                mHandler.sendEmptyMessage(SHOW_VIEW_PSW);
                mWifiPwdEt.requestFocus();
            }
        });
        mPwdEnterBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(mWifiPwdEt.length() < 8){
                    Toast.makeText(mNetworkFragment.getActivity().getApplicationContext(), mNetworkFragment.getActivity().getString(R.string.input_right_psw),  
                            Toast.LENGTH_SHORT).show(); 
                }else{
                    print("connect");
                    mNetworkFragment.autoScan = false;
                    mNetworkFragment.connect(mWifiPwdEt.getText().toString());
                    mHandler.sendEmptyMessage(SHOW_VIEW_CONNECTING);
                    mHandler.sendEmptyMessageDelayed(SHOW_VIEW_WIFI_CONNECT_FAIL, 25000);
                }

            }

        });
        mShowPwdCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg0.isChecked()) {
                    mWifiPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mWifiPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        mWifiPwdEt.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i(TAG, "" + actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    closeIME();
                    findNextFoucs();
                    return true;
                } else {
                    return false;
                }
            }

            private void closeIME() {
                View view = mNetworkFragment.getActivity().getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) mNetworkFragment.getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            private void findNextFoucs() {
                mShowPwdCb.requestFocus();
                mShowPwdCb.requestFocusFromTouch();
            }
        });

        mWifiPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //                mPwdEnterBtn.setEnabled(mWifiPwdEt.length() >= 8);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void resetAllViews() {
        mWifiPswLl.setVisibility(View.GONE);
        mConnResultLl.setVisibility(View.GONE);
        mWifiLv.setVisibility(View.GONE);
        mWaitingLl.setVisibility(View.GONE);
    }

    public void showWaitingView() {
        // 显示缓冲条.
        mWaitingLl.setVisibility(View.VISIBLE);
        mConnectingTv.setText(mContext.getResources().getString(R.string.detect_network));

    }

    private void showWifiListView() {
        mWifiLv.setVisibility(View.VISIBLE);
    }

    private void showPasswordView() {
        mWifiPwdEt.setText("");
        mPwdErrTv.setText("");
        mWifiPswLl.setVisibility(View.VISIBLE);
    }

    private void showConnectResultView() {
        mConnResultLl.setVisibility(View.VISIBLE);
    }

    public boolean onBackPressed() {
        Utils.print(TAG, "onBackPressed.");
        //        if (isVisiable(mWaitingLl) || isVisiable(mWifiLv)) {
        if(isVisiable(mWifiLv)) {
            return false;
        }
        if (isVisiable(mWifiPswLl)) {
            mNetworkFragment.autoScan = true;
            mHandler.sendEmptyMessage(SHOW_VIEW_WIFILIST);
            return true;
        }
        if (isVisiable(mWaitingLl)) {
            if(mNetworkFragment.autoScan){
                return false;
            }else{
                NetworkFragment.autoScan = true;
                mHandler.sendEmptyMessage(SHOW_VIEW_WIFILIST);
                return true;
            }
        }
        if (isVisiable(mConnResultLl)) {
            //            mHandler.sendEmptyMessage(SHOW_VIEW_RESULT);
            //            return true;
            return false;
        }

        return true;
    }

    private boolean isVisiable(View view) {
        if (view == null) {
            return false;
        }
        return view.getVisibility() == View.VISIBLE ? true : false;
    }

    public void dismissProcessing() {
        // 显示缓冲条.
        mWaitingLl.setVisibility(View.INVISIBLE);
        mConnResultLl.setVisibility(View.INVISIBLE);
        mConnectingTv.setText(mContext.getResources().getString(R.string.detect_network));

    }

    public void etheNetConnected() {
        mWaitingLl.setVisibility(View.INVISIBLE);
        mConnResultLl.setVisibility(View.VISIBLE);
        mResultIv.setImageResource(R.drawable.conn_success);
        mResultTv.setText(mContext.getResources().getString(R.string.wire_success));
    }

    public void wifiConnected() {
        mWaitingLl.setVisibility(View.INVISIBLE);
        mConnResultLl.setVisibility(View.VISIBLE);
        mResultIv.setImageResource(R.drawable.conn_success);
        mResultTv.setText(mContext.getResources().getString(R.string.wifi_success));
    }

    public void showAllNetowks(List<ScanResult> mWifiResultList) {
        //        if (mNetworkFragment.bConnecting
        //                || mWifiPswLl.getVisibility() == View.VISIBLE
        //                || (mResultTv.getVisibility() == View.VISIBLE && mResultTv.getText().toString()
        //                        .equals(mContext.getResources().getString(R.string.wifi_success)))) {
        if (mWifiPswLl.getVisibility() == View.VISIBLE
                || (mResultTv.getVisibility() == View.VISIBLE && mResultTv.getText().toString()
                .equals(mContext.getResources().getString(R.string.wifi_success)))) {
            return;
        }
        dismissProcessing();
        showWifiListView();
        Utils.print("Net fragment", "showAllNetowks");
        WifiAdapter wifiAdapter = new WifiAdapter(mContext, mWifiResultList);
        mWifiLv.setAdapter(wifiAdapter);
        mWifiLv.requestFocus();
        mWifiLv.smoothScrollToPosition(0);
    }

    private void print(String str) {
        Utils.print(TAG, str);
    }

    public void wifiConnectFail() {
        mHandler.sendEmptyMessage(SHOW_VIEW_WIFI_CONNECT_FAIL);
    }

    public void wifiConnectSuccess() {
        mHandler.sendEmptyMessage(SHOW_VIEW_WIFI_CONNECT_SUCCESS);
    }

    private void showConnectingView() {
        mWaitingLl.setVisibility(View.VISIBLE);
        mConnectingTv.setText(mContext.getResources().getString(R.string.wifi_connecting));

    }

    private void showWifiConnectFailView() {
        mWaitingLl.setVisibility(View.INVISIBLE);
        mWifiPswLl.setVisibility(View.VISIBLE);
//        mPwdErrTv.setText(mContext.getResources().getString(R.string.error_psw));
        mPwdErrTv.setText(mContext.getResources().getString(R.string.wifi_disabled_password_failure));
        mPwdErrTv.setVisibility(View.VISIBLE);
    }

    private void showWifiConnectSuccessView() {
        mWifiPswLl.setVisibility(View.INVISIBLE);
        mWaitingLl.setVisibility(View.INVISIBLE);
        mConnResultLl.setVisibility(View.VISIBLE);
        mResultIv.setImageResource(R.drawable.conn_success);
        mResultTv.setText(mContext.getResources().getString(R.string.wifi_success));
    }

    public void connectFail() {
        mHandler.sendEmptyMessage(SHOW_VIEW_CONNECT_FAIL);
    }

    private void showConnectFailView() {
        mWaitingLl.setVisibility(View.INVISIBLE);
        mConnResultLl.setVisibility(View.VISIBLE);
        mResultIv.setImageResource(R.drawable.conn_fail);
        mResultTv.setTextColor(android.graphics.Color.RED);
        mResultTv.setText(mContext.getResources().getString(R.string.network_conn_fail));
    }

}
