
package com.android.settings.network.wifi;

import scifly.middleware.network.WifiConfig;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiEnterpriseConfig.Phase2;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.settings.R;
/**
 * the dialog of input password.
 *
 */
public class PasswordInputDialog extends AlertDialog {

    private static final String TAG = "PasswordInputDialog";

    private Activity mContext;

    private View mMainView;

    private TextView mTitleTv;

    private EditText mInputText;

    private EditText mSsidText;

    private Spinner mWifiSecuritySp;

    private LinearLayout mSsidLl;

    private LinearLayout mPswLl;

    private LinearLayout mShowPswLl;

    private CheckBox mShowPassword;

    private Button mConfirmButton;

    private AccessPoint mAccessPoint;

    private int mAccessPointSecurity;

    private boolean isAddWifiDialog;

    private boolean mPasswordInvalid;

    public PasswordInputDialog(Context context, AccessPoint accessPoint) {
        super(context);
        isAddWifiDialog = false;
        mContext = (Activity) context;
        mAccessPoint = accessPoint;
        mAccessPointSecurity = (accessPoint == null) ? AccessPoint.SECURITY_NONE : accessPoint.security;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMainView = getLayoutInflater().inflate(R.layout.dialog_wifi_password_input, null);

        setView(mMainView);
        super.onCreate(savedInstanceState);

//        getWindow().setLayout(
//                mContext.getResources().getDimensionPixelSize(
//                        R.dimen.WIFI_PASSWORD_DIALOG_WIDTH),
//                mContext.getResources().getDimensionPixelSize(
//                        R.dimen.WIFI_PASSWORD_DIALOG_HEIGHT));

        findViews();
        registerListener();

        setCheckBoxEnable();

        enableSubmitIfAppropriate();
    }

    private void setCheckBoxEnable() {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("wifi_password_config",
                Activity.MODE_PRIVATE);
        boolean checked = sharedPreferences.getBoolean("password_visibility", true);
        mShowPassword.setChecked(checked);
        updatePasswordVisibility(checked);
        Log.i(TAG, "setCheckBoxEnable::checked:" + checked);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    private void findViews() {
        mWifiSecuritySp = (Spinner) mMainView
                .findViewById(R.id.sp_wifi_security);
        mShowPswLl = (LinearLayout) mMainView.findViewById(R.id.ll_show_psw);
        mPswLl = (LinearLayout) mMainView.findViewById(R.id.ll_password);
        mSsidText = (EditText) mMainView.findViewById(R.id.wifi_ssid);
        mTitleTv = (TextView) mMainView.findViewById(R.id.title);
        mSsidLl = (LinearLayout) mMainView.findViewById(R.id.ll_ssid);
        mInputText = (EditText) mMainView.findViewById(R.id.wifi_password);
        mShowPassword = (CheckBox) mMainView
                .findViewById(R.id.showpassword_check);
        mConfirmButton = (Button) mMainView.findViewById(R.id.confirm_button);

        mInputText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
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
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) mContext
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            private void findNextFoucs() {
                mShowPassword.requestFocus();
                mShowPassword.requestFocusFromTouch();
            }
        });
    }

    private void registerListener() {
        mShowPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean checked) {
                Log.i(TAG, "onCheckedChanged::checked:" + checked);

                updatePasswordVisibility(checked);

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("wifi_password_config",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("password_visibility", checked);
                editor.commit();

            }
        });

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfAppropriate();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mWifiSecuritySp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                switch (arg2) {
                case AccessPoint.SECURITY_NONE:
                    mPasswordInvalid = false;
                    mAccessPointSecurity = AccessPoint.SECURITY_NONE;
                    mPswLl.setVisibility(View.INVISIBLE);
                    mShowPswLl.setVisibility(View.INVISIBLE);
                    break;
                case AccessPoint.SECURITY_WEP:
                    mPasswordInvalid = true;
                    mAccessPointSecurity = AccessPoint.SECURITY_WEP;
                    mPswLl.setVisibility(View.VISIBLE);
                    mShowPswLl.setVisibility(View.VISIBLE);
                    enableSubmitIfAppropriate();
                    break;
                case AccessPoint.SECURITY_PSK:
                    mPasswordInvalid = true;
                    mAccessPointSecurity = AccessPoint.SECURITY_PSK;
                    mPswLl.setVisibility(View.VISIBLE);
                    mShowPswLl.setVisibility(View.VISIBLE);
                    enableSubmitIfAppropriate();
                    break;
                case AccessPoint.SECURITY_EAP:
                    mPasswordInvalid = true;
                    mAccessPointSecurity = AccessPoint.SECURITY_EAP;
                    mPswLl.setVisibility(View.VISIBLE);
                    mShowPswLl.setVisibility(View.VISIBLE);
                    enableSubmitIfAppropriate();
                    break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                mAccessPointSecurity = AccessPoint.SECURITY_NONE;
                mPswLl.setVisibility(View.INVISIBLE);
                mShowPswLl.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void updatePasswordVisibility(boolean checked) {
        int pos = mInputText.getSelectionEnd();
        mInputText.setInputType(InputType.TYPE_CLASS_TEXT
                | (checked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD));
        if (pos >= 0) {
            mInputText.setSelection(pos);
        }
    }

    void enableSubmitIfAppropriate() {
        mPasswordInvalid = false;

        if ((mAccessPointSecurity == AccessPoint.SECURITY_WEP && mInputText
                .length() == 0)
                || (mAccessPointSecurity == AccessPoint.SECURITY_PSK && mInputText
                        .length() < 8)) {
            mPasswordInvalid = true;
        }
        mConfirmButton.setEnabled(!mPasswordInvalid);
        if (mConfirmButton.isEnabled()) {
            mConfirmButton.setTextColor(mContext.getResources().getColor(android.R.color.black));
        } else {
            mConfirmButton.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
        }
    }

    public WifiConfig getConfig() {
        WifiConfig config;
        if (mAccessPoint.getConfig() == null) {
            config = mAccessPoint.buildConfigFromResult(false);
        } else {
            config = mAccessPoint.getConfig();
        }
        Log.d(TAG, "config : " + config == null ? null : config.toString());

        switch (mAccessPointSecurity) {
            case AccessPoint.SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;
            case AccessPoint.SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (mInputText.length() != 0) {
                    int length = mInputText.length();
                    String password = mInputText.getText().toString();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;
            case AccessPoint.SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (mInputText.length() != 0) {
                    String password = mInputText.getText().toString();
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;
            case AccessPoint.SECURITY_EAP:
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
                config.enterpriseConfig = new WifiEnterpriseConfig();
                config.enterpriseConfig.setPhase2Method(Phase2.NONE);
                break;
        }

        return config;
    }

    public EditText getInputText() {
        return mInputText;
    }

    public CheckBox getShowPassword() {
        return mShowPassword;
    }

    public Button getConfirmButton() {
        return mConfirmButton;
    }

    public void changeUItoAddWifi() {
        isAddWifiDialog = true;
        mTitleTv.setText(mContext.getResources().getString(
                R.string.wifi_add_network));
        mSsidLl.setVisibility(View.VISIBLE);
        mInputText.setHint(mContext.getResources().getString(
                R.string.wifi_password));
        mSsidText
                .setHint(mContext.getResources().getString(R.string.wifi_ssid));
        mConfirmButton.setText(mContext.getResources().getString(
                R.string.wifi_add));
        mConfirmButton.setEnabled(true);
        mConfirmButton.setTextColor(mContext.getResources().getColor(android.R.color.black));
    }

    public void setOnConfirmClickListener(View.OnClickListener l) {
        mConfirmButton.setOnClickListener(l);
    }
/**
 * get the type of dialog,ADD_WIFI and INPUT_PASSWORD.
 * @return
 */
    public String getDialogType() {
        String DialogType = null;
        if (isAddWifiDialog) {
            DialogType = "ADD_WIFI";
        } else {
            DialogType = "INPUT_PASSWORD";
        }
        return DialogType;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password,
            int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        if (Type == AccessPoint.SECURITY_NONE) {
             config.hiddenSSID = true; 
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (Type == AccessPoint.SECURITY_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == AccessPoint.SECURITY_PSK) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    public String getSsid() {
        String ssid = mSsidText.getText().toString();
        return ssid;
    }

    public int getSecurity() {
        return mAccessPointSecurity;
    }

    public String getPassword() {
        String password = mInputText.getText().toString();
        return password;
    }

}
