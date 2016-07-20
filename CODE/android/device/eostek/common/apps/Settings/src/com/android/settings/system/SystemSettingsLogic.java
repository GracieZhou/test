
package com.android.settings.system;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.android.settings.R;
import com.android.settings.util.Utils;

public class SystemSettingsLogic {
    public static final String TAG = "SystemSettingsLogic";

    private static final int INSTALL_LOCATION_AUTO = 0;

    private static final int INSTALL_LOCATION_INTERNAL = 1;

    private static final int INSTALL_LOCATION_SDCARD = 2;

    private SystemSettingsActivity mActivity;

    private Configuration conf;

    private String mCurrentInputMethod = "";

    private String mCurrentLanguage = "";


    public SystemSettingsLogic(SystemSettingsActivity activity) {
        this.mActivity = activity;
        conf = mActivity.getResources().getConfiguration();
    }

    public String getCurrentLanguage() {
        final String[] specialLocaleNames = mActivity.getResources().getStringArray(R.array.special_locale_names);
        Configuration conf = mActivity.getResources().getConfiguration();
        String language = conf.locale.getLanguage();
        String localeString;
        //  This is not an accurate way to display the locale, as it is
        // just working around the fact that we support limited dialects
        // and want to pretend that the language is valid for all locales.
        // We need a way to support languages that aren't tied to a particular
        // locale instead of hiding the locale qualifier.
        if (language.equals("zz")) {
            String country = conf.locale.getCountry();
            if (country.equals("ZZ")) {
                localeString = "[Developer] Accented English (zz_ZZ)";
            } else if (country.equals("ZY")) {
                localeString = "[Developer] Fake Bi-Directional (zz_ZY)";
            } else {
                localeString = "";
            }
        } else if (Utils.hasOnlyOneLanguageInstance(language, Resources.getSystem().getAssets().getLocales())) {
            localeString = conf.locale.getDisplayLanguage(conf.locale);
        } else {
            localeString = conf.locale.getDisplayName(conf.locale);
        }
        if (localeString.length() > 1) {
            localeString = Character.toUpperCase(localeString.charAt(0))
                    + localeString.substring(1);
            if(localeString.equals(mActivity.getResources().getString(R.string.zh_cn))){
                localeString = specialLocaleNames[0];
            }else if(localeString.equals( mActivity.getResources().getString(R.string.zh_tw))){
                localeString =specialLocaleNames[1] ;
            }
            mCurrentLanguage=localeString;
        }
        return mCurrentLanguage;
    }

    public String getCurrentInputMethodName() {
        String lastInputMethodId = Settings.Secure.getString(mActivity.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        InputMethodManager mImm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = mImm.getInputMethodList();
        for (int i = 0; i < mInputMethodProperties.size(); i++) {
            if (lastInputMethodId.equals(mInputMethodProperties.get(i).getId())) {
                mCurrentInputMethod = mInputMethodProperties.get(i).loadLabel(mActivity.getPackageManager()).toString();
                break;
            }
        }
        return mCurrentInputMethod;
    }

    @SuppressLint("NewApi")
    public String getInstallLocation() {
        int installLocationPreference = Settings.Global.getInt(mActivity.getContentResolver(),
                Settings.Global.DEFAULT_INSTALL_LOCATION, INSTALL_LOCATION_AUTO);
        String appInstallLocation = null;
        switch (installLocationPreference) {
            case INSTALL_LOCATION_AUTO:
                appInstallLocation = mActivity.getString(R.string.install_location_auto);
                break;
            case INSTALL_LOCATION_INTERNAL:
                appInstallLocation = mActivity.getString(R.string.install_location_internal);
                break;
            case INSTALL_LOCATION_SDCARD:
                appInstallLocation = mActivity.getString(R.string.install_location_sdcard);
                break;
        }
        return appInstallLocation;
    }

}
