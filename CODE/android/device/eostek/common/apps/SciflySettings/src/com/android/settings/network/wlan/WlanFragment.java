
package com.android.settings.network.wlan;

import scifly.device.Device;
import scifly.middleware.network.EthernetManagerGlobal;

import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.network.NetworkSettingActivity;
import com.android.settings.util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class WlanFragment extends PreferenceFragment implements OnPreferenceClickListener, OnPreferenceChangeListener {

    private final static String TAG = "WlanFragment";

    private NetworkSettingActivity mContext;

    private InputDialog dialog;

    private static final String WLAN_KEY = "enable_wlan";

    private static final String SSID = "ssid";

    private static final String SECURITY = "security";

    private static final String PASSWORD = "password";

    private SettingPreference mWlanPreference;

    private SettingPreference mSSIDPreference;

    private SettingPreference mSecurityPreference;

    private SettingPreference mPasswordPreference;

    private WifiManager mWifiManager;

    private int mSecurityTypeIndex;

    public static final int OPEN_INDEX = 0;

    public static final int WPA_INDEX = 1;

    public static final int WPA2_INDEX = 2;

    private WifiConfiguration mWifiConfig = null;

    private String mSsid;

    private String mPwd;

    private final String HOT_SPOT_OPEN_STRING = "is_hot_spot_opened";

    private final String HOT_SPOT_SECURITY_STRING = "hot_spot_security";

    private final String HOT_SPOT_SSID_KEY_STRING = "hot_spot_ssid_key";

    private final String HOT_SPOT_SSID_PASSWORD_STRING = "hot_spot_ssid_password";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_wlan);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mContext = (NetworkSettingActivity) getActivity();
        mContext.setSubTitle(R.string.wlan);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        // find preference and set clickListener
        mWlanPreference = (SettingPreference) findPreference(WLAN_KEY);
        mSSIDPreference = (SettingPreference) findPreference(SSID);
        mSecurityPreference = (SettingPreference) findPreference(SECURITY);
        mPasswordPreference = (SettingPreference) findPreference(PASSWORD);
        mWlanPreference.setOnPreferenceClickListener(this);
        mSecurityPreference.setOnPreferenceChangeListener(this);
        mPasswordPreference.setOnPreferenceClickListener(this);
        boolean is_hot_spot_opened = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(
                HOT_SPOT_OPEN_STRING, false);
        mWlanPreference.setChecked(is_hot_spot_opened);
        isRemovePreference(!is_hot_spot_opened);

        isHidePassword();
        mSsid = Device.getDeviceName(getActivity());
        mSSIDPreference.setRightText(mSsid);
        mPwd = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).getString(HOT_SPOT_SSID_PASSWORD_STRING,
                "12345678");
        mPasswordPreference.setRightText(mPwd);

    }

    /**
     * @Title: startProvisioningIfNecessary.
     * @Description:open or close wlan.
     * @param: @param isChecked.
     * @return: void.
     * @throws
     */
    private void startProvisioningIfNecessary(boolean isChecked) {
        Log.d(TAG, "<<<<<<<startProvisioningIfNecessary<<<<<<<");
        EthernetManagerGlobal mEthernetManagerGlobal = new EthernetManagerGlobal(getActivity());
        if (isChecked) {
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
                mEthernetManagerGlobal.setEnabled(true);
            }
            Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.WIFI_SAVED_STATE, 1);
            mWifiConfig = getConfig();

            if (mWifiConfig != null) {
                mWifiManager.setWifiApEnabled(null, false);
                mWifiManager.setWifiApEnabled(mWifiConfig, true);
            }
        } else {
            mWifiManager.setWifiApEnabled(null, false);
            Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.WIFI_SAVED_STATE, 0);
        }
    }

    /**
     * @Title: getConfig.
     * @Description: get wifi config.
     * @param: @return.
     * @return: WifiConfiguration.To let SSID this column can not be clicked.
     * @throws
     */
    private WifiConfiguration getConfig() {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = mSsid;
        switch (mSecurityTypeIndex) {
            case OPEN_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                return config;
            case WPA_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.preSharedKey = mPwd;
                return config;
            case WPA2_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.preSharedKey = mPwd;
                return config;
        }
        return null;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (WLAN_KEY.equals(key)) {
            mWlanPreference.toggleButton();
            boolean isChecked = mWlanPreference.isChecked();
            mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                    .putBoolean(HOT_SPOT_OPEN_STRING, isChecked).commit();
            isRemovePreference(!isChecked);
            isHidePassword();
            startProvisioningIfNecessary(isChecked);
        } else if (PASSWORD.equals(key)) {
            dialog = new InputDialog(mContext);
            dialog.show();
        }
        return false;
    }

    // show or hide ssid,security,password preference.
    public void isRemovePreference(boolean b) {
        if (b) {
            getPreferenceScreen().removePreference(mSSIDPreference);
            getPreferenceScreen().removePreference(mSecurityPreference);
            getPreferenceScreen().removePreference(mPasswordPreference);
        } else {
            getPreferenceScreen().addPreference(mSSIDPreference);
            getPreferenceScreen().addPreference(mSecurityPreference);
            getPreferenceScreen().addPreference(mPasswordPreference);
        }
    }

    public void isHidePassword() {
        mSecurityTypeIndex = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt(
                HOT_SPOT_SECURITY_STRING, 0);
        switch (mSecurityTypeIndex) {
            case OPEN_INDEX:
                mSecurityPreference.setRightText("CLOSE");
                this.getPreferenceScreen().removePreference(mPasswordPreference);
                break;
            case WPA_INDEX:
                mSecurityPreference.setRightText("WPA PSK");
                break;
            case WPA2_INDEX:
                mSecurityPreference.setRightText("WPA2 PSK");
                break;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences sp = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        Editor spEditor = sp.edit();
        if (newValue.equals("CLOSE")) {
            mSecurityTypeIndex = OPEN_INDEX;
            this.getPreferenceScreen().removePreference(mPasswordPreference);
        } else if (newValue.equals("WPA PSK")) {
            mSecurityTypeIndex = WPA_INDEX;
            this.getPreferenceScreen().addPreference(mPasswordPreference);
        } else if (newValue.equals("WPA2 PSK")) {
            mSecurityTypeIndex = WPA2_INDEX;
            this.getPreferenceScreen().addPreference(mPasswordPreference);
        }
        spEditor.putInt(HOT_SPOT_SECURITY_STRING, mSecurityTypeIndex).commit();
        boolean is_hot_spot_opened = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(
                HOT_SPOT_OPEN_STRING, false);
        startProvisioningIfNecessary(is_hot_spot_opened);
        return true;
    }

    class InputDialog extends AlertDialog implements android.view.View.OnClickListener {

        private View mMainView;

        private Button mBtn;

        private EditText mInputText;

        private CheckBox mShowPassword;

        protected InputDialog(Context context) {
            super(context);
            mContext = (NetworkSettingActivity) context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            mMainView = getLayoutInflater().inflate(R.layout.dialog_wifi_password_input, null);
            setView(mMainView);
            super.onCreate(savedInstanceState);
            findViews();
            registerListener();
        }

        // find dialog view
        private void findViews() {

            mBtn = (Button) mMainView.findViewById(R.id.confirm_button);
            mInputText = (EditText) mMainView.findViewById(R.id.wifi_password);
            mShowPassword = (CheckBox) mMainView.findViewById(R.id.showpassword_check);
            mBtn.setText(mContext.getResources().getString(R.string.button_yes));
            mInputText.setText(mPwd);
            mBtn.setOnClickListener(this);
            mInputText.setOnEditorActionListener(new OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
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
                    updatePasswordVisibility(checked);
                }
            });
        }

        private void updatePasswordVisibility(boolean checked) {
            int pos = 0;
            if (!TextUtils.isEmpty(mInputText.getText())) {
                pos = mInputText.getText().length();
            }
            mInputText.setInputType(InputType.TYPE_CLASS_TEXT
                    | (checked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            : InputType.TYPE_TEXT_VARIATION_PASSWORD));
            if (pos >= 0) {
                mInputText.setSelection(pos);
            }
        }

        public String getPassword() {
            String password = mInputText.getText().toString();
            return password;
        }

        @Override
        public void onClick(View v) {
            if (dialog != null && dialog.isShowing()) {
                String password = "";
                password = dialog.getPassword();
                SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("settings",
                        Context.MODE_PRIVATE);
                Editor spEditor = sp.edit();
                if (password.length() >= 8) {
                    mPasswordPreference.setRightText(password);
                    mPwd = password;
                    spEditor.putString(HOT_SPOT_SSID_PASSWORD_STRING, mPwd);
                    dialog.dismiss();
                } else {
                    Utils.showToast(getActivity(), R.string.password_too_short);
                }
                spEditor.commit();
                startProvisioningIfNecessary(mWlanPreference.isChecked());
            }
        }
    }

}
