
package com.android.settings.display;

import android.os.Bundle;
import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.display.fragments.DisplayAndSoundFragment;

public class DisplayAndSoundActivity extends BaseSettingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager.beginTransaction().replace(R.id.fragment_content, new DisplayAndSoundFragment()).commit();
    }

    public void setSubTitle() {
        mTitle.setSubTitleText(getString(R.string.display_and_sound),"");
    }
    
    public void setSubTitle(int resId) {
        setSubTitle(getString(R.string.display_and_sound), getString(resId));
    }
}
