
package com.android.settings.network.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.LinkProperties;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
//import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
//import android.net.wifi.WifiConfiguration.ProxySettings;
//
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
//
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.settings.R;

public class PasswordInputDialog extends AlertDialog {

    private static final String TAG = "PasswordInputDialog";

    private Activity mContext;

    private View mMainView;

    private EditText mInputText;

    private CheckBox mShowPassword;

    private Button mConfirmButton;

    private AccessPoint mAccessPoint;

    private int mAccessPointSecurity;

    private IpAssignment mIpAssignment = IpAssignment.UNASSIGNED;

    private LinkProperties mLinkProperties = new LinkProperties();

    public PasswordInputDialog(Context context, AccessPoint accessPoint) {
        super(context);
        mContext = (Activity) context;
        mAccessPoint = accessPoint;
        mAccessPointSecurity = (accessPoint == null) ? AccessPoint.SECURITY_NONE : accessPoint.security;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mMainView = getLayoutInflater().inflate(R.layout.wifi_password_input_dialog, null);

        setView(mMainView);
        super.onCreate(savedInstanceState);

//        getWindow().setLayout(mContext.getResources().getDimensionPixelSize(R.dimen.WIFI_PASSWORD_DIALOG_WIDTH),
//                mContext.getResources().getDimensionPixelSize(R.dimen.WIFI_PASSWORD_DIALOG_HEIGHT));

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

    private void findViews() {
        mInputText = (EditText) mMainView.findViewById(R.id.wifi_password);
        mShowPassword = (CheckBox) mMainView.findViewById(R.id.showpassword_check);
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
        boolean passwordInvalid = false;

        if ((mAccessPointSecurity == AccessPoint.SECURITY_WEP && mInputText.length() == 0)
                || (mAccessPointSecurity == AccessPoint.SECURITY_PSK && mInputText.length() < 8)) {
            passwordInvalid = true;
        }
        mConfirmButton.setEnabled(!passwordInvalid);
        if (mConfirmButton.isEnabled()) {
            mConfirmButton.setTextColor(mContext.getResources().getColor(android.R.color.black));
        } else {
            mConfirmButton.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
        }
        try {
//            Log.i(TAG, "" + mAccessPoint.getConfig().linkProperties.toString());
        } catch (Exception e) {
        }
    }

    public WifiConfiguration getConfig() {
        WifiConfiguration config;
        if (mAccessPoint.getConfig() == null) {
            config = mAccessPoint.buildConfigFromResult(false);
        } else {
            config = mAccessPoint.getConfig();
        }

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

//        config.proxySettings = ProxySettings.NONE;
//        config.ipAssignment = mIpAssignment;
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

    public void setOnConfirmClickListener(View.OnClickListener l) {
        mConfirmButton.setOnClickListener(l);
    }

    public String getPassword() {
        String password = mInputText.getText().toString();
        return password;
    }

}
