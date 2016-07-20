
package com.android.settings.network.pppoe;

import java.net.Inet4Address;
import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.network.NetworkSettingActivity;
import com.android.settings.util.Utils;
import com.mstar.android.pppoe.PppoeManager;

public class PppoeFragment extends PreferenceFragment implements View.OnClickListener {

    private static final String PPPOE_SHOW_PASSWORD = "pppoe_show_password";

    private static final String PPPOE_SAVE_ACCOUNT_AND_PASSWORD = "pppoe_save_account_and_password";

    private static final int CHECKED = 1;

    private static final String TAG = null;

    private EditText mPassword;

    private EditText mUsername;

    private CheckBox mShowPassword;

    private CheckBox mSaveAccountPasswd;

    private Button mBtnDialerHangUp;

    private Button mBtnDialerOk;

    private TextView mTvDialerStatus;

    private PPPoEDialer mPPPoEDialer;

    private Context mContext;

    private View mView;

    private NetworkSettingActivity mActivity;

    private Handler mPppoeHandler = new Handler();

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @SuppressWarnings("static-access")
        public void handleMessage(Message msg) {
            if (msg.what == mPPPoEDialer.PPPOE_STATE_CONNECT) {
                mTvDialerStatus.setText(getString(R.string.pppoe_connected));
                savePppoeConfiguration();
            } else if (msg.what == mPPPoEDialer.PPPOE_STATE_DISCONNECT)
                mTvDialerStatus.setText(getString(R.string.pppoe_disconnected));
            else if (msg.what == mPPPoEDialer.PPPOE_STATE_CONNECTING)
                mTvDialerStatus.setText(getString(R.string.pppoe_dialing));
            else if (msg.what == mPPPoEDialer.PPPOE_STATE_AUTHFAILED)
                mTvDialerStatus.setText(getString(R.string.pppoe_authfailed));
            else if (msg.what == mPPPoEDialer.PPPOE_STATE_FAILED)
                mTvDialerStatus.setText(getString(R.string.pppoe_failed));
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.pppoe_setting, container, false);
        mActivity = (NetworkSettingActivity) this.getActivity();
        mActivity.setSubTitle(R.string.pppoe_settings_title);
        mContext = mView.getContext();
        initView(mView);
        initData();
        initEvent(savedInstanceState);
        return mView;
    }

    /**
     * Save PPPOE IpConfig
     */
    private void savePppoeConfiguration() {
        EthernetManager mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        PppoeManager mPppoeManager = PppoeManager.getInstance(mContext);
        if (mPppoeManager != null && mPppoeManager.getPppoeStatus().equals(mPppoeManager.PPPOE_STATE_CONNECT)) {
            IpConfiguration ipConfiguration = new IpConfiguration();
            ipConfiguration.ipAssignment = android.net.IpConfiguration.IpAssignment.DHCP;
            StaticIpConfiguration staticIpConfiguration = new StaticIpConfiguration();
            ipConfiguration.setStaticIpConfiguration(staticIpConfiguration);
            String ipAddr = mPppoeManager.getIpaddr();
            Inet4Address inetAddr = null;
            try {
                inetAddr = (Inet4Address) NetworkUtils.numericToInetAddress(ipAddr);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            int networkPrefixLength = netmaskToPrefixLength(mPppoeManager.getMask());

            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                Log.w(TAG, "illegal argument netmask");
                // return;
            }
            try {
                // process ip & netmask
                staticIpConfiguration.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            // process gateway
            String gateway = mPppoeManager.getRoute();
            if (!TextUtils.isEmpty(gateway)) {
                try {
                    staticIpConfiguration.gateway = (Inet4Address) NetworkUtils.numericToInetAddress(gateway);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            // process dns
            String dns1 = mPppoeManager.getDns1();
            if (!TextUtils.isEmpty(dns1)) {
                try {
                    InetAddress numericToInetAddress = NetworkUtils.numericToInetAddress(dns1);
                    staticIpConfiguration.dnsServers.add(numericToInetAddress);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            String dns2 = mPppoeManager.getDns2();
            if (!TextUtils.isEmpty(dns1)) {
                try {
                    staticIpConfiguration.dnsServers.add((Inet4Address) NetworkUtils.numericToInetAddress(dns2));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            mEthernetManager.setConfiguration(ipConfiguration);
        }
    }

    private void initData() {
        mPPPoEDialer = new PPPoEDialer(mContext, mHandler);

        new PPPoESettingsHolder(mView);
        if (Utils.isPppoeConnected(mContext))
            mTvDialerStatus.setText(getString(R.string.pppoe_connected));
        else
            mTvDialerStatus.setText(getString(R.string.pppoe_disconnected));
        // mTvDialerStatus.setText(mPPPoEDialer.getPppoeStatus());
    }

    private void initEvent(Bundle savedInstanceState) {
        if (Utils.isPppoeConnected(mContext)) {
            mUsername.setText(mPPPoEDialer.getUser());
            mPassword.setText(mPPPoEDialer.getPasswd());
            mUsername.setEnabled(false);
            mPassword.setEnabled(false);
        } else {
            if (Settings.Global.getInt(mContext.getContentResolver(), PPPOE_SAVE_ACCOUNT_AND_PASSWORD, 0) == CHECKED) {
                mUsername.setText(mPPPoEDialer.getUser());
                mPassword.setText(mPPPoEDialer.getPasswd());
            }
            // mUsername.addTextChangedListener(this);
            // mPassword.addTextChangedListener(this);
        }

        mSaveAccountPasswd.setOnClickListener(this);
        mShowPassword.setOnClickListener(this);
        if (Settings.Global.getInt(mContext.getContentResolver(), PPPOE_SAVE_ACCOUNT_AND_PASSWORD, 0) == CHECKED) {
            mSaveAccountPasswd.setChecked(true);
        }

        if (Settings.Global.getInt(mContext.getContentResolver(), PPPOE_SHOW_PASSWORD, 0) == CHECKED) {
            mShowPassword.setChecked(true);
            mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    private void initView(View view) {
        mUsername = (EditText) view.findViewById(R.id.username_et);
        mPassword = (EditText) view.findViewById(R.id.password_et);
        mSaveAccountPasswd = (CheckBox) view.findViewById(R.id.auto_dialer_cb);
        mShowPassword = (CheckBox) view.findViewById(R.id.show_password);
        mBtnDialerHangUp = (Button) view.findViewById(R.id.dialer_hangup);
        mBtnDialerOk = (Button) view.findViewById(R.id.dialer_ok);
        mTvDialerStatus = (TextView) view.findViewById(R.id.dialer_status);

        mBtnDialerHangUp.setOnClickListener(this);
        mBtnDialerOk.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPPPoEDialer.registerPPPoEReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPPPoEDialer.exit();
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.show_password:
                mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                        | (((CheckBox) view).isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                : InputType.TYPE_TEXT_VARIATION_PASSWORD));
                Settings.Global.putInt(mContext.getContentResolver(), PPPOE_SHOW_PASSWORD,
                        (((CheckBox) view).isChecked() ? 1 : 0));
                break;
            case R.id.auto_dialer_cb:
                Settings.Global.putInt(mContext.getContentResolver(), PPPOE_SAVE_ACCOUNT_AND_PASSWORD,
                        (((CheckBox) view).isChecked() ? 1 : 0));
                if (((CheckBox) view).isChecked()) {
                    mPPPoEDialer.setUser(mUsername.getText().toString().trim());
                    mPPPoEDialer.setPasswd(mPassword.getText().toString().trim());
                }
                break;
            case R.id.dialer_hangup:
                mPassword.setEnabled(true);
                mUsername.setEnabled(true);
                if (mPassword.length() > 0 && mUsername.length() > 0) {
                    String dialerStatus = mTvDialerStatus.getText().toString();
                    Log.i("life", "dialerStatus=" + dialerStatus);
                    if (!getString(R.string.pppoe_disconnected).equals(dialerStatus)) {
                        mPPPoEDialer.hangup();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.not_account_empty),
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.dialer_ok:
                if (Utils.isPppoeConnected(mContext)) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.please_pppoe_disconnect),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mPassword.length() > 0 && mUsername.length() > 0) {
                        mPppoeHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String user = mUsername.getText().toString().trim();
                                String passwd = mPassword.getText().toString().trim();
                                mPPPoEDialer.dial(user, passwd);
                            }
                        });
                        mPassword.setEnabled(false);
                        mUsername.setEnabled(false);
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.not_account_empty),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private int netmaskToPrefixLength(String netmask) {
        if (TextUtils.isEmpty(netmask)) {
            return -1;
        }

        String[] tmp = netmask.split("\\.");
        int cnt = 0;
        for (String cell : tmp) {
            int i = Integer.parseInt(cell);
            cnt += Integer.bitCount(i);
        }

        return cnt;
    }
}
