
package com.eostek.isynergy.setmeup.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;

import com.eostek.isynergy.setmeup.model.LanguageModel;
import com.eostek.isynergy.setmeup.utils.Utils;

public class LanguageLogic {
    private static final String TAG = LanguageLogic.class.getSimpleName();

    private static final int CORRECT_LENGTH = 5;

    private Context mContext;

    public LanguageLogic(Context context) {
        this.mContext = context;
    }

    /**
     * get all support language
     * @return LanguageModel List
     */
    public List<LanguageModel> getAllLanguages() {
        List<LanguageModel> languages = new ArrayList<LanguageModel>();

        String[] locals = mContext.getAssets().getLocales();
        if (locals == null || locals.length <= 0) {
            return languages;
        }

        for (String local : locals) {
            Utils.print(TAG, "local:" + local.toString() + ";");
            if (CORRECT_LENGTH != local.length()) {
                continue;
            }
            if (VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                local = local.replace("-", "_");
            }
            String[] splits = local.split("_");

            if (splits != null && splits.length == 2) {

                Locale locale = new Locale(splits[0], splits[1]);

                LanguageModel language = new LanguageModel(local, locale);
                Utils.print(TAG, "language:" + language.getLocale());
                languages.add(language);
            }

        }
        Utils.print(TAG, "languages size:" + languages.size());
        return languages;
    }
}
