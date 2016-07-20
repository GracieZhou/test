
package com.android.settings.network.ethernet;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class EthernetSettingActivity extends Activity {

    private EthernetSettingFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ethernet_setting_main_layout);
        mFragment = new EthernetSettingFragment();
        changeFrag(mFragment);
    }

    public void changeFrag(Fragment f) {
        if (f == null) {
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_view, f);
        ft.commit();

        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setMainTitleText(getString(R.string.action_settings));
            tw.setFirstSubTitleText(getString(R.string.network_settings), false);
            tw.setSecondSubTitleText(getString(R.string.ethernet_setting));
        }
    }
}
