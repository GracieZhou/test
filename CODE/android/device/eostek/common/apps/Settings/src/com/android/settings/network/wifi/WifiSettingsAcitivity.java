
package com.android.settings.network.wifi;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import com.android.settings.userbackup.WifiInfoBackUp;
import com.android.settings.widget.TitleWidget;
import com.android.settings.R;

public class WifiSettingsAcitivity extends Activity {

    private WifiSettingsFragment mWifiSettingsFragment;
    private static WifiInfoBackUp mWifiInfoBackUp=null;

    @Override
    protected void onCreate(Bundle bundle) {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(bundle);
        setContentView(R.layout.wifi_setting_main_layout);

        mWifiInfoBackUp=new WifiInfoBackUp(getApplicationContext());
        mWifiSettingsFragment = new WifiSettingsFragment();

        changeFrag(mWifiSettingsFragment);
    }

    public void changeFrag(Fragment f) {
        if (f == null) {
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_view, f);
        ft.commit();

        setTitleWidget();
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setMainTitleText(getString(R.string.action_settings));
            tw.setFirstSubTitleText(getString(R.string.network_settings), false);
            tw.setSecondSubTitleText(getString(R.string.wifi_settings));
        }
    }

    private static final String RESOURCE_THEME_LIGHT = "Theme.Settings";

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        resid = getResources().getIdentifier(RESOURCE_THEME_LIGHT, "style", getPackageName());
        super.onApplyThemeResource(theme, resid, first);
    }

}
