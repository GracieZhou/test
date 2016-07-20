
package com.android.settings.network.wifi;

import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.ListView;
import com.android.settings.R;

public class WifiSettingsFragment extends PreferenceFragment {

    protected static final String TAG = "WifiSettingsFragment";

    private Handler mHandler;

    private WifiSettingsHolder wifiSettingsHolder;

    private WifiSettingsLogic wifiSettingsLogic;

    private static final String EXTRA_IS_FIRST_RUN = "firstRun";

    private boolean mSetupWizardMode;

    public void onCreate(Bundle bundle) {
        mSetupWizardMode = getActivity().getIntent().getBooleanExtra(EXTRA_IS_FIRST_RUN, false);
        super.onCreate(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wifiSettingsLogic.getWifiEnabler() != null) {
            wifiSettingsLogic.getWifiEnabler().resume();
        }
        wifiSettingsLogic.registerReceiver();
        wifiSettingsLogic.updateAccessPoints();

        // getPreferenceScreen().addPreference(new MyPreference(getActivity()));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (wifiSettingsLogic.getWifiEnabler() != null) {
            wifiSettingsLogic.getWifiEnabler().pause();
        }
        wifiSettingsLogic.unregisterReceiver();
        wifiSettingsLogic.scannerPause();
    }

    @Override
    public void onActivityCreated(Bundle arg0) {

        super.onActivityCreated(arg0);

        wifiSettingsHolder = new WifiSettingsHolder(getActivity());

        wifiSettingsLogic = new WifiSettingsLogic(getActivity(), this, mHandler, mSetupWizardMode);

        // Remove listview divider.
        ListView listView = (android.widget.ListView) getView().findViewById(android.R.id.list);

        if (listView != null) {
            listView.setDivider(null);
            listView.setSelector(R.drawable.wifi_outter_focus);
            listView.setVerticalScrollBarEnabled(false);
        }

        addPreferencesFromResource(R.xml.wifi_settings);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (wifiSettingsLogic.onPreferenceTreeClick(preferenceScreen, preference)) {
            return true;
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

}
