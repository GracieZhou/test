
package com.mstar.tv.menu.setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import scifly.device.Device;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.os.SystemProperties;

import com.mstar.android.tv.TvCecManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.CecSetting;
import com.mstar.android.tvapi.common.vo.EnumSleepTimeState;
import com.mstar.android.tvapi.factory.vo.EnumAcOnPowerOnMode;
import com.mstar.tv.FocusView;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.setting.restore.SystemRestoreFactoryActivity;
import com.mstar.tv.menu.setting.update.SystemLocalUpdateActivity;
import com.mstar.tv.menu.setting.util.Tools;
import com.mstar.tv.menu.ui.EosCustomSettingActivity;

public class AdvanceSettingFragment extends PublicFragement {

    private final static String TAG = "AdvanceSettingFragment";

    private final static int LANGUSGE_SET = 0;

    private final static int FACTORY_RESET = 1;

    private final static int LOCAL_UPDATE = 2;

    private final static int NET_UPDATE = 3;

    private final static int SLEEP_MODE = 4;

    private final static int SAVE_MODE = 5;

    private final static int AUTO_POWERON = 6;

    private final static int CCREAT_CHANGEPASSWORD = 7;

    private final static int HOTKEY_1 = 8;

    private final static int HOTKEY_2 = 9;

    private final static int HDMICES_STATUS = 10;

    private final static int HDMIARC_STATUS = 11;

    private final static int TRADITIONAL_CHINESE = 0;

    private final static int ENGLISH = 1;

    private final static int SIMPLE_CHINESE = 2;

    private EosSettingItem languageSelectItem = null;

    private EosSettingItem resetItem = null;

    private EosSettingItem localUpdateItem = null;

    private EosSettingItem networkUpdateItem = null;

    private EosSettingItem sleepModeItem = null;

    private EosSettingItem saveModeItem = null;

    private EosSettingItem hdmiCecItem = null;

    private EosSettingItem hdmiArcItem = null;

    private EosSettingItem autoPowerOnItem = null;

    private EosSettingItem resetPwdItem = null;

    private EosSettingItem hotkey1Item = null;

    private EosSettingItem hotkey2Item = null;

    private EosSettingItem versionItem = null;

    private String[] advanceSetting_title;

    private String mLocaleCountry;

    private Locale mCurrentLocale;

    private TvCecManager mTvCecManager = null;

    private CecSetting hdmicecstatus = null;

    private CecSetting cecSetting = null;
    
    private boolean isVIP = false;
    
    private boolean hasTuner = false;

    @Override
    protected void initItems() {
        setTag("advance");
        isVIP = Device.isVipMode(getActivity());
        mItems = new ArrayList<EosSettingItem>();
        advanceSetting_title = getActivity().getResources().getStringArray(R.array.setting_advance);

        versionItem = new EosSettingItem(this, advanceSetting_title[10], getResources().getStringArray(
                R.array.versionerror), 0, MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(versionItem);
        languageSelectItem = new EosSettingItem(this, advanceSetting_title[0], getResources().getStringArray(
                R.array.language), 0, MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(languageSelectItem);
        resetItem = new EosSettingItem(this, advanceSetting_title[1], MenuConstants.ITEMTYPE_BUTTON, true);
        mItems.add(resetItem);
        localUpdateItem = new EosSettingItem(this, advanceSetting_title[2], MenuConstants.ITEMTYPE_BUTTON, true);
        mItems.add(localUpdateItem);
        networkUpdateItem = new EosSettingItem(this, advanceSetting_title[3], MenuConstants.ITEMTYPE_BUTTON, true);
        if (isVIP) {
            mItems.add(networkUpdateItem);
        }
        sleepModeItem = new EosSettingItem(this, advanceSetting_title[4], getResources().getStringArray(
                R.array.time_setting_sleep_time), 0, MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(sleepModeItem);
        saveModeItem = new EosSettingItem(this, advanceSetting_title[5], getResources().getStringArray(
                R.array.turnon_off), 0, MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(saveModeItem);
        autoPowerOnItem = new EosSettingItem(this, advanceSetting_title[6], getResources().getStringArray(
                R.array.turnon_off), 0, MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(autoPowerOnItem);
        resetPwdItem = new EosSettingItem(this, advanceSetting_title[7], MenuConstants.ITEMTYPE_BUTTON, true);
        mItems.add(resetPwdItem);
        try {
            hasTuner = TvManager.getInstance().getFactoryManager().getTunerStatus();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if(hasTuner){
            hotkey1Item = new EosSettingItem(this, advanceSetting_title[8], getResources().getStringArray(
                    R.array.hotkeysource), 0, MenuConstants.ITEMTYPE_ENUM, true);
            hotkey2Item = new EosSettingItem(this, advanceSetting_title[9], getResources().getStringArray(
                    R.array.hotkeysource), 0, MenuConstants.ITEMTYPE_ENUM, true);
        }else{
            hotkey1Item = new EosSettingItem(this, advanceSetting_title[8], getResources().getStringArray(
                    R.array.hotkeysource_no_tuner), 0, MenuConstants.ITEMTYPE_ENUM, true);
            hotkey2Item = new EosSettingItem(this, advanceSetting_title[9], getResources().getStringArray(
                    R.array.hotkeysource_no_tuner), 0, MenuConstants.ITEMTYPE_ENUM, true);
        }
        mItems.add(hotkey1Item);
        mItems.add(hotkey2Item);
        int source = TvCommonManager.getInstance().getCurrentTvInputSource();
        if (source == TvCommonManager.INPUT_SOURCE_HDMI || source == TvCommonManager.INPUT_SOURCE_HDMI2
                || source == TvCommonManager.INPUT_SOURCE_HDMI3) {
            mTvCecManager = TvCecManager.getInstance();
            hdmicecstatus = mTvCecManager.getCecConfiguration();
            hdmiCecItem = new EosSettingItem(this, advanceSetting_title[11], getResources().getStringArray(
                    R.array.turnon_off), hdmicecstatus.cecStatus, MenuConstants.ITEMTYPE_ENUM, true);
            mItems.add(hdmiCecItem);
            cecSetting = TvCecManager.getInstance().getCecConfiguration();
            hdmiArcItem = new EosSettingItem(this, advanceSetting_title[12], getResources().getStringArray(
                    R.array.turnon_off), cecSetting.arcStatus, MenuConstants.ITEMTYPE_ENUM, hdmicecstatus.cecStatus == 0 ? false : true);
            mItems.add(hdmiArcItem);
        }
        animatedSelector.setTopOffset(getActivity().getResources().getInteger(
                R.integer.advance_setting_fragment_top_off));
        initDate();
    }

    private void callBackSimple(int resultValue, int position) {
        switch (position) {
            case LANGUSGE_SET: {
                switch (resultValue) {
                    case TRADITIONAL_CHINESE: {
                        setLanguage("zh_TW");
                        break;
                    }
                    case ENGLISH: {
                        setLanguage("en_US");
                        break;
                    }
                    case SIMPLE_CHINESE: {
                        setLanguage("zh_CN");
                        break;
                    }
                    default:
                        break;
                }
            }
                break;
            case 3:
                TvTimerManager.getInstance().setSleepMode(EnumSleepTimeState.values()[resultValue]);
                break;

            case 4:
                Settings.System.putInt(getActivity().getContentResolver(), "savemode", resultValue);
                if (resultValue == 1 && !TvChannelManager.getInstance().isSignalStabled()) {
                    Intent intentStandby = new Intent("com.eostek.tv.player.startstandyby");
                    getActivity().sendBroadcast(intentStandby);
                }
                break;
            case 5:
                if (resultValue == 0) {
                    TvFactoryManager.getInstance().setPowerOnMode(EnumAcOnPowerOnMode.E_ACON_POWERON_SECONDARY);
                } else if (resultValue == 1) {
                    TvFactoryManager.getInstance().setPowerOnMode(EnumAcOnPowerOnMode.E_ACON_POWERON_DIRECT);
                }
                break;
            case 7:
                Settings.System.putInt(getActivity().getContentResolver(), "hotkey1", resultValue);
                break;
            case 8:
                Settings.System.putInt(getActivity().getContentResolver(), "hotkey2", resultValue);
                break;
            case 9:
                hdmicecstatus.cecStatus = (short) resultValue;
                mTvCecManager.setCecConfiguration(hdmicecstatus);
                break;
            default:
                break;
        }
    }
    
    private void callBackVIP(int resultValue, int position) {
        switch (position) {
            case LANGUSGE_SET: {
                switch (resultValue) {
                    case TRADITIONAL_CHINESE: {
                        setLanguage("zh_TW");
                        break;
                    }
                    case ENGLISH: {
                        setLanguage("en_US");
                        break;
                    }
                    case SIMPLE_CHINESE: {
                        setLanguage("zh_CN");
                        break;
                    }
                    default:
                        break;
                }
            }
                break;
            case SLEEP_MODE:
                TvTimerManager.getInstance().setSleepMode(EnumSleepTimeState.values()[resultValue]);
                break;

            case SAVE_MODE:
                Settings.System.putInt(getActivity().getContentResolver(), "savemode", resultValue);
                if (resultValue == 1 && !TvChannelManager.getInstance().isSignalStabled()) {
                    Intent intentStandby = new Intent("com.eostek.tv.player.startstandyby");
                    getActivity().sendBroadcast(intentStandby);
                }
                // fix Man 0033464,Laird add
                break;
            case AUTO_POWERON:
                if (resultValue == 0) {
                    TvFactoryManager.getInstance().setPowerOnMode(EnumAcOnPowerOnMode.E_ACON_POWERON_SECONDARY);
                } else if (resultValue == 1) {
                    TvFactoryManager.getInstance().setPowerOnMode(EnumAcOnPowerOnMode.E_ACON_POWERON_DIRECT);
                }
                break;
            case HOTKEY_1:
                Settings.System.putInt(getActivity().getContentResolver(), "hotkey1", resultValue);
                break;
            case HOTKEY_2:
                Settings.System.putInt(getActivity().getContentResolver(), "hotkey2", resultValue);
                break;
            case HDMICES_STATUS:
                Log.i("lucky", "resultValue=====" + resultValue);
                hdmicecstatus.cecStatus = (short) resultValue;
                hdmicecstatus.autoStandby = (short) resultValue;
                if (resultValue == 0) {
                    hdmiArcItem.setFocusable(false);
                } else {
                    hdmiArcItem.setFocusable(true);
                }
                TvCommonManager.getInstance().setSourceIdentState(resultValue);
                TvCommonManager.getInstance().setSourceSwitchState(resultValue);
                mTvCecManager.setCecConfiguration(hdmicecstatus);
                break;
            case HDMIARC_STATUS:
                cecSetting.arcStatus = (short) resultValue;
                mTvCecManager.setCecConfiguration(cecSetting);
                break;
            default:
                break;
        }
    }

    @Override
    void callBack(int resultValue, int position) {
        if (isVIP) {
            callBackVIP(resultValue, position);
        } else {
            callBackSimple(resultValue, position);
        }
    }

    private void setLanguage(String counLanguage) {
        ((EosCustomSettingActivity) getActivity()).getHandler().removeMessages(EosCustomSettingActivity.DELAYFINISH);
        String language = counLanguage.substring(0, 2);
        String country = counLanguage.substring(3, 5);
        Locale locale = new Locale(language, country);
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            config.locale = locale;
            com.android.internal.app.LocalePicker.updateLocale(locale);
            // indicate this isn't some passing default - the user wants this
            // remembered
            config.userSetLocale = true;
            am.updateConfiguration(config);
            // Trigger the dirty bit for the Settings Provider.
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    void callBack(Boolean resultValue, int position) {
    }

    private void callBackSimple(int position) {
        switch (position) {
            case FACTORY_RESET:
                Tools.intentForward(getActivity(), SystemRestoreFactoryActivity.class);
                getActivity().finish();
                break;
            case LOCAL_UPDATE:
                Tools.intentForward(getActivity(), SystemLocalUpdateActivity.class);
                getActivity().finish();
                break;
            case 6:
                creatChangePasswordDialog();
                break;
            default:
                break;
        }
    }

    private void callBackVIP(int position) {
        switch (position) {
            case FACTORY_RESET: {
                Tools.intentForward(getActivity(), SystemRestoreFactoryActivity.class);
                getActivity().finish();
                break;
            }
            case LOCAL_UPDATE: {
                Tools.intentForward(getActivity(), SystemLocalUpdateActivity.class);
                getActivity().finish();
                break;
            }
            case NET_UPDATE: {
                // EthernetManager ethernetManager =
                // EthernetManager.getInstance();
                EthernetManager ethernetManager = (EthernetManager) this.getActivity().getSystemService(
                        Context.ETHERNET_SERVICE);
                WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled() || isNetworkConnected()) {
                    // Tools.intentForward(getActivity(),
                    // SystemNetUpdateActivity.class);
                    Intent intent = new Intent("android.settings.action.NETUPDATE");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.not_network), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case CCREAT_CHANGEPASSWORD: {
                creatChangePasswordDialog();
                break;
            }
            default:
                break;
        }
    }

    @Override
    void callBack(int position) {
        if (isVIP) {
            callBackVIP(position);
        } else {
            callBackSimple(position);
        }
    }

    // =====================================================================================
    private boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(
                Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (info.isConnected()) {
            return true;
        }

        return false;
    }

    // ======================================================================================

    private void creatChangePasswordDialog() {
        ((EosCustomSettingActivity) getActivity()).getHandler().removeMessages(EosCustomSettingActivity.DELAYFINISH);
        final Dialog passwordChangeDialog = new Dialog(getActivity(), R.style.dialog);
        passwordChangeDialog.setContentView(R.layout.eos_passwordchange);

        final LinearLayout oldPasswordLayout = (LinearLayout) passwordChangeDialog
                .findViewById(R.id.old_password_layout);
        final LinearLayout newPasswordLayout = (LinearLayout) passwordChangeDialog
                .findViewById(R.id.new_password_layout);
        final LinearLayout confirmPasswordLayout = (LinearLayout) passwordChangeDialog
                .findViewById(R.id.confirm_password_layout);
        final FocusView focusView = (FocusView) passwordChangeDialog.findViewById(R.id.focus_selector);
        final EditText oldPasswordEdt = (EditText) passwordChangeDialog.findViewById(R.id.old_password);
        final EditText newPasswordEdt = (EditText) passwordChangeDialog.findViewById(R.id.new_password);
        final EditText confirmPasswordEdt = (EditText) passwordChangeDialog.findViewById(R.id.confirm_password);

        Button sureBtn = (Button) passwordChangeDialog.findViewById(R.id.sure_reset_btn);
        Button cancleBtn = (Button) passwordChangeDialog.findViewById(R.id.cancle_reset_btn);
        Button cleanBtn = (Button) passwordChangeDialog.findViewById(R.id.clean_reset_btn);

        class FocusChangeListener implements OnFocusChangeListener {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Rect rect = new Rect();
                    v.getGlobalVisibleRect(rect);
                    focusView.startAnimation(v);
                }
            }
        }

        FocusChangeListener focusChangeListener = new FocusChangeListener();
        oldPasswordLayout.setOnFocusChangeListener(focusChangeListener);
        newPasswordLayout.setOnFocusChangeListener(focusChangeListener);
        confirmPasswordLayout.setOnFocusChangeListener(focusChangeListener);
        sureBtn.setOnFocusChangeListener(focusChangeListener);
        cancleBtn.setOnFocusChangeListener(focusChangeListener);
        cleanBtn.setOnFocusChangeListener(focusChangeListener);
        sureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int curPassword = TvManager.getInstance().getParentalcontrolManager().GetParentalPassword();
                String curInputStr = oldPasswordEdt.getText().toString();
                String newInputStr = newPasswordEdt.getText().toString();
                String confirmInputStr = confirmPasswordEdt.getText().toString();
                if (curInputStr == null || curInputStr.length() < 4) {
                    Toast.makeText(getActivity(), R.string.oldpasswordnullorlessthansix, Toast.LENGTH_LONG).show();
                } else if (newInputStr == null || newInputStr.length() < 4) {
                    Toast.makeText(getActivity(), R.string.newpasswordnullorlessthansix, Toast.LENGTH_LONG).show();
                } else if (confirmInputStr == null || confirmInputStr.length() < 4) {
                    Toast.makeText(getActivity(), R.string.confirmpasswordnullorlessthansix, Toast.LENGTH_LONG).show();
                } else if (!(Integer.parseInt(curInputStr) == curPassword)) {
                    Toast.makeText(getActivity(), R.string.originalpassworderr, Toast.LENGTH_LONG).show();
                } else if (!(newInputStr.equals(confirmInputStr))) {
                    Toast.makeText(getActivity(), R.string.newconfirmpassworderr, Toast.LENGTH_LONG).show();
                } else if ((Integer.parseInt(curInputStr) == curPassword) && newInputStr.equals(confirmInputStr)) {
                    TvManager.getInstance().getParentalcontrolManager()
                            .setParentalPassword(Integer.parseInt(newInputStr));
                    Toast.makeText(getActivity(), R.string.resetsuccess, Toast.LENGTH_LONG).show();
                    passwordChangeDialog.dismiss();
                    getActivity().findViewById(R.id.main).setVisibility(View.VISIBLE);
                    ((ListView) getActivity().findViewById(R.id.context_lst)).requestFocus();
                    ((EosCustomSettingActivity) getActivity()).getHandler().sendEmptyMessageDelayed(
                            EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
                }
            }
        });
        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordChangeDialog.dismiss();
                getActivity().findViewById(R.id.main).setVisibility(View.VISIBLE);
                ((ListView) getActivity().findViewById(R.id.context_lst)).requestFocus();
                ((EosCustomSettingActivity) getActivity()).getHandler().sendEmptyMessageDelayed(
                        EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
            }
        });
        cleanBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPasswordEdt.setText("");
                newPasswordEdt.setText("");
                confirmPasswordEdt.setText("");
            }
        });
        passwordChangeDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keycode, KeyEvent keyEvent) {
                switch (keycode) {
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_MENU:
                        passwordChangeDialog.dismiss();
                        getActivity().findViewById(R.id.main).setVisibility(View.VISIBLE);
                        ((ListView) getActivity().findViewById(R.id.context_lst)).requestFocus();
                        ((EosCustomSettingActivity) getActivity()).getHandler().sendEmptyMessageDelayed(
                                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
                        break;
                    case KeyEvent.KEYCODE_0:
                    case KeyEvent.KEYCODE_1:
                    case KeyEvent.KEYCODE_2:
                    case KeyEvent.KEYCODE_3:
                    case KeyEvent.KEYCODE_4:
                    case KeyEvent.KEYCODE_5:
                    case KeyEvent.KEYCODE_6:
                    case KeyEvent.KEYCODE_7:
                    case KeyEvent.KEYCODE_8:
                    case KeyEvent.KEYCODE_9:
                        ((EosCustomSettingActivity) getActivity()).getHandler().removeMessages(
                                EosCustomSettingActivity.DELAYFINISH);
                        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            if (oldPasswordLayout.hasFocus()) {
                                oldPasswordEdt.setText(oldPasswordEdt.getText().toString()
                                        + (keycode - KeyEvent.KEYCODE_0));
                            } else if (newPasswordLayout.hasFocus()) {
                                newPasswordEdt.setText(newPasswordEdt.getText().toString()
                                        + (keycode - KeyEvent.KEYCODE_0));
                            } else if (confirmPasswordLayout.hasFocus()) {
                                confirmPasswordEdt.setText(confirmPasswordEdt.getText().toString()
                                        + (keycode - KeyEvent.KEYCODE_0));
                            }
                        }
                        break;
                }
                return false;
            }
        });
        getActivity().findViewById(R.id.main).setVisibility(View.GONE);
        passwordChangeDialog.show();
    }

    @Override
    protected void initDate() {
        Configuration conf = getActivity().getResources().getConfiguration();
        mCurrentLocale = conf.locale;
        mLocaleCountry = mCurrentLocale.getLanguage();
        if (mLocaleCountry.equals("zh")) {
            languageSelectItem.setCurValue(0);
        } else if (mLocaleCountry.equals("en")) {
            languageSelectItem.setCurValue(1);
        }
        sleepModeItem.setCurValue(TvTimerManager.getInstance().getSleepMode().ordinal());
        if (TvFactoryManager.getInstance().getPowerOnMode() == EnumAcOnPowerOnMode.E_ACON_POWERON_DIRECT) {
            autoPowerOnItem.setCurValue(1);
        } else {
            autoPowerOnItem.setCurValue(0);
        }
        saveModeItem.setCurValue(Settings.System.getInt(getActivity().getContentResolver(), "savemode", 0));
        //savemode focus is from low blue mode.
        int status = android.provider.Settings.System.getInt(getActivity().getContentResolver(), MenuConstants.MODE, 0);
        saveModeItem.setFocusable(status == 0);
        hotkey1Item.setCurValue(Settings.System.getInt(getActivity().getContentResolver(), "hotkey1", 0));
        hotkey2Item.setCurValue(Settings.System.getInt(getActivity().getContentResolver(), "hotkey2", 0));
        versionItem.setValues(new String[] {
            readSysIni()
        });
        mAdapter.setHasShowValue(true);
    }

    @Override
    boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
        return false;
    }

    private String readSysIni() {
        String versionCode = Settings.System.getString(getActivity().getContentResolver(), "LauncherVersionCode");
        String line = null;
        String panel_version = "";
        try {
            FileInputStream mStream = new FileInputStream(new File("config/sys.ini"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(mStream));
            while ((line = reader.readLine()) != null) {
                // System.out.println(line);
                if (line.startsWith("project_panel_version")) {
                    int position = line.indexOf(";");
                    String[] tmpStrings = line.subSequence(0, position).toString().split("=");
                    panel_version = tmpStrings[1].trim();
                }
            }
            reader.close();
            mStream.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (panel_version.equals("")) {
            return getResources().getStringArray(R.array.versionerror)[0];
        } else {
            String version = SystemProperties.get("ro.scifly.version.alias", "");
            if (version.startsWith("v")) {
                version = version.split("v")[1];
            }
            panel_version += "-" + version;
            if (versionCode != null && !versionCode.isEmpty()) {
                panel_version += versionCode;
            }
        }
        return panel_version;
    }
}
