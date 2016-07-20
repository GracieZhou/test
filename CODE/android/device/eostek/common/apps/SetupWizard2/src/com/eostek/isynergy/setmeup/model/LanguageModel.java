
package com.eostek.isynergy.setmeup.model;

import java.util.Locale;

public class LanguageModel {
    private Locale mLocale;

    private String mOrigalLocale;

    private String mCountry;

    private String mDisplayName;

    public LanguageModel(String original, Locale locale) {
        this.mLocale = locale;
        this.mOrigalLocale = original;
        this.mCountry = locale.getCountry();
        this.mDisplayName = locale.getDisplayName();

    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String lanName) {
        this.mDisplayName = lanName;
    }

    public String getOrigalLocale() {
        return mOrigalLocale;
    }

    public void setOrigalLocale(String origalLocale) {
        this.mOrigalLocale = origalLocale;
    }

    public Locale getLocale() {
        return mLocale;
    }

    public void setLocale(Locale locale) {
        this.mLocale = locale;
    }
}
