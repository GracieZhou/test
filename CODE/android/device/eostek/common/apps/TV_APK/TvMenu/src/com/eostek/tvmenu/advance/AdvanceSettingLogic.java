
package com.eostek.tvmenu.advance;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.RemoteException;
import android.widget.Toast;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuActivity;
import com.eostek.tvmenu.TvMenuHolder;
import com.eostek.tvmenu.utils.Constants;
import com.eostek.tvmenu.utils.Tools;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvParentalControlManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumLanguage;


public class AdvanceSettingLogic {

    AdvanceSettingFragment mFragment;

    public AdvanceSettingHolder mHolder;

    public AdvanceSettingLogic(AdvanceSettingFragment f) {
        // TODO Auto-generated constructor stub
        mFragment = f;
    }
    
    /**
     * start FactoryResetActivity , LocalUpdateActivity
     */
    protected void update(Class<?> classname) {
        Tools.intentForward(mFragment.getActivity(), classname);
        mFragment.getActivity().finish();
    }

    /**
     * start NetworkUpdate activity
     */
    protected void startNetUpdate() {
        EthernetManager ethernetManager = (EthernetManager) (mFragment.getActivity()).getSystemService(Context.ETHERNET_SERVICE);
        WifiManager wifiManager = (WifiManager) mFragment.getActivity().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled() || ethernetManager.isEnabled()) {
        	TvCommonManager.getInstance().setInputSource(Constants.E_INPUT_SOURCE_STORAGE);
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.update.SystemUpdateActivity");
            mFragment.getActivity().startActivity(intent);
            mFragment.getActivity().finish();
        } else {
            Toast.makeText(mFragment.getActivity(), mFragment.getString(R.string.not_network), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * start SettingActivity
     */
    protected void startSettingActivity(){
        TvCommonManager.getInstance().setInputSource(Constants.E_INPUT_SOURCE_STORAGE);
        
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.datetimecity.TimeZoneSettingActivity");
        mFragment.getActivity().startActivity(intent);
        mFragment.getActivity().finish();

    }
    
    /**
     * start PVROptionActivity
     */
    protected void startPVROptionActivity(){
        Intent intent = new Intent(mFragment.getActivity(), com.eostek.tvmenu.advance.PVROptionActivity.class);
        mFragment.startActivity(intent);
        mFragment.getActivity().finish();
    }
    
    /**
     * To set parent content lock rating
     *
     * @param rating
     * @see #PARENTAL_CONTENT_NONE
     * @see #PARENTAL_CONTENT_DRUGS
     * @see #PARENTAL_CONTENT_VIOLENCE
     * @see #PARENTAL_CONTENT_VIOLENCE_DRUGS
     * @see #PARENTAL_CONTENT_SEX
     * @see #PARENTAL_CONTENT_SEX_DRUGS
     * @see #PARENTAL_CONTENT_VIOLENCE_SEX
     * @see #PARENTAL_CONTENT_VIOLENCE_SEX_DRUGS
     */
    public void SetParentalControlRating(int ParentalGuidanceVal) {
        TvParentalControlManager.getInstance().setParentalControlRating(ParentalGuidanceVal);
    }
    
    /**
     * To get parent content lock rating
     *
     * @see #PARENTAL_CONTENT_NONE
     * @see #PARENTAL_CONTENT_DRUGS
     * @see #PARENTAL_CONTENT_VIOLENCE
     * @see #PARENTAL_CONTENT_VIOLENCE_DRUGS
     * @see #PARENTAL_CONTENT_SEX
     * @see #PARENTAL_CONTENT_SEX_DRUGS
     * @see #PARENTAL_CONTENT_VIOLENCE_SEX
     * @see #PARENTAL_CONTENT_VIOLENCE_SEX_DRUGS
     * @return int (Rating of parent content lock)
     */
    public int getParentalControlRating() {
        int parentalGuidanceVal = 0;
        parentalGuidanceVal = TvParentalControlManager.getInstance().getParentalControlRating();
        return parentalGuidanceVal;
    }
    
    /**
     * set the language
     * 
     * @param counLanguage
     */
    protected void setLanguage(String counLanguage) {
        ((TvMenuActivity) mFragment.getActivity()).getHandler().removeMessages(TvMenuHolder.FINISH);
        String language = counLanguage.substring(0, 2);
        String country = counLanguage.substring(3, 5);
        Locale locale = new Locale(language, country);
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            config.locale = locale;
            // com.android.internal.app.LocalePicker.updateLocale(locale);
            config.userSetLocale = true;
            am.updateConfiguration(config);
            // Trigger the dirty bit for the Settings Provider.
            BackupManager.dataChanged(Constants.BACKUP_MANAGER_DATA_CHANGED);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the vision from system config file
     * 
     * @return
     */
    protected String readSysIni() {
        String line = null;
        String panel_version = "";
        try {
            FileInputStream mStream = new FileInputStream(new File("config/sys.ini"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(mStream));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(Constants.PROJECT_PANEL_VERSION)) {
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
            return mFragment.getResources().getStringArray(R.array.versionerror)[0];
        } else {
            panel_version += "-" + Build.VERSION.INCREMENTAL;
        }
        return panel_version;
    }

}
