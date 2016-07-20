
package com.android.settings.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocalePicker.LocaleInfo;
import com.android.settings.R;
import com.android.settings.userbackup.BackUpData;
import com.android.settings.util.Utils;

public class LanguageSettingsLogic {
    public static final String TAG = "LanguageSettingsLogic";

    private LanguageSettingsActivity mActivity;

    private int mCurrentLanguage = 0;

    private LocaleInfo[] mLocaleInfos;

    private Configuration conf;

    public LanguageSettingsLogic(LanguageSettingsActivity activity) {
        this.mActivity = activity;
        // here must init List first ,currentLanguage'initialization depends on
        // the list
        initLanguageList();
        initCurrentLanguage();
    }

    public void updateLanguageSettings(int languageId) {
        conf.locale = mLocaleInfos[languageId].getLocale();
        // update device configuration
        LocalePicker.updateLocale(conf.locale);
        BackUpData.updateData("locale", "my_locale", conf.locale.getLanguage() + conf.locale.getCountry());
        mCurrentLanguage = languageId;
        Log.i(TAG, "set language " + languageId);
    }

    /**
     * add the languages to ArrayList for saving displayName "English(US)" and
     * the locales to LocaleList for saving locales
     */
    public void initLanguageList() {
        ArrayList<String> localeList = new ArrayList<String>(Arrays.asList(mActivity.getAssets().getLocales()));
        String[] localeStrings = new String[localeList.size()];
        localeStrings = localeList.toArray(localeStrings);
        final String[] specialLocaleCodes = mActivity.getResources().getStringArray(R.array.special_locale_codes);
        final String[] specialLocaleNames = mActivity.getResources().getStringArray(R.array.special_locale_names);

        Arrays.sort(localeStrings);
        final int origSize = localeStrings.length;

        final LocaleInfo[] preprocess = new LocaleInfo[origSize];

        int finalSize = 0;
        for (int i = 0; i < origSize; i++) {
            final String s = localeStrings[i];
            final int len = s.length();
            if (len == 5) {
                String language = s.substring(0, 2);
                String country = s.substring(3, 5);
                final Locale l = new Locale(language, country);
                if (finalSize == 0) {

                    Log.v(TAG, "adding initial " + toTitleCase(l.getDisplayLanguage(l)));

                    preprocess[finalSize++] = new LocaleInfo(toTitleCase(l.getDisplayLanguage(l)), l);
                } else {
                    // check previous entry:
                    // same lang and a country -> upgrade to full name and
                    // insert ours with full name
                    // diff lang -> insert ours with lang-only name
                    if (preprocess[finalSize - 1].getLocale().getLanguage().equals(language)
                            && !preprocess[finalSize - 1].getLocale().getLanguage().equals("zz")) {
                        Log.v(TAG,
                                "backing up and fixing "
                                        + preprocess[finalSize - 1].getLabel()
                                        + " to "
                                        + getDisplayName(preprocess[finalSize - 1].getLocale(), specialLocaleCodes,
                                                specialLocaleNames));
                        Locale loc = preprocess[finalSize - 1].getLocale();
                        preprocess[finalSize - 1] = new LocaleInfo(toTitleCase(getDisplayName(
                                preprocess[finalSize - 1].getLocale(), specialLocaleCodes, specialLocaleNames)), loc);
                        Log.v(TAG,
                                "  and adding "
                                        + toTitleCase(getDisplayName(l, specialLocaleCodes, specialLocaleNames)));
                        preprocess[finalSize++] = new LocaleInfo(toTitleCase(getDisplayName(l, specialLocaleCodes,
                                specialLocaleNames)), l);
                    } else {
                        String displayName;
                        if (s.equals("zz_ZZ")) {
                            displayName = "[Developer] Accented English";
                        } else if (s.equals("zz_ZY")) {
                            displayName = "[Developer] Fake Bi-Directional";
                        } else {
                            displayName = toTitleCase(l.getDisplayLanguage(l));
                            Log.v(TAG, "adding " + displayName);
                            preprocess[finalSize++] = new LocaleInfo(displayName, l);
                        }
                    }
                }
            }
        }
        mLocaleInfos = new LocaleInfo[finalSize];
        for (int i = 0; i < finalSize; i++) {
            mLocaleInfos[i] = preprocess[i];
        }
    }

    public void initCurrentLanguage() {
        final String[] specialLocaleNames = mActivity.getResources().getStringArray(R.array.special_locale_names);
        conf = mActivity.getResources().getConfiguration();
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
                localeString = "[Developer] Accented English";
            } else if (country.equals("ZY")) {
                localeString = "[Developer] Fake Bi-Directional";
            } else {
                localeString = "";
            }
        } else if (Utils.hasOnlyOneLanguageInstance(language, Resources.getSystem().getAssets().getLocales())) {
            localeString = conf.locale.getDisplayLanguage(conf.locale);
        } else {
            localeString = conf.locale.getDisplayName(conf.locale);
        }
        if (localeString.length() > 1) {
            localeString = Character.toUpperCase(localeString.charAt(0)) + localeString.substring(1);
            if (localeString.equals(mActivity.getResources().getString(R.string.zh_cn))) {
                localeString = specialLocaleNames[0];
            } else if (localeString.equals(mActivity.getResources().getString(R.string.zh_tw))) {
                localeString = specialLocaleNames[1];
            }

            String defaultLanguage = localeString;
            int count = 0;
            for (LocaleInfo info : mLocaleInfos) {
                if (info.getLabel().equals(defaultLanguage)) {
                    break;
                }
                count++;
            }
            mCurrentLanguage = count;
        }

    }

    public LocaleInfo[] getLanguageList() {

        return mLocaleInfos;
    }

    public int getCurrentLanguage() {
        return mCurrentLanguage;
    }

    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String getDisplayName(Locale l, String[] specialLocaleCodes, String[] specialLocaleNames) {
        String code = l.toString();

        for (int i = 0; i < specialLocaleCodes.length; i++) {
            if (specialLocaleCodes[i].equals(code)) {
                return specialLocaleNames[i];
            }
        }

        return l.getDisplayName(l);
    }

}
