
package com.android.settings.deviceinfo;

import android.content.Intent;
import android.os.Bundle;

import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.deviceinfo.fragments.DeviceInfoFragment;
import com.android.settings.deviceinfo.fragments.StorageInfoFragment;

/**
 * @ClassName: DeviceInfoActivity
 * @Description:DeviceInfo UI
 * @author: lucky.li
 * @date: 2015-8-27 下午4:26:41
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
 */
public class DeviceInfoActivity extends BaseSettingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager.beginTransaction().replace(R.id.fragment_content, new DeviceInfoFragment()).commit();
        Intent intent = getIntent();
        String fragmentName = intent.getStringExtra("fragment_name");
        if ("StorageInfoFragment".equals(fragmentName)) {
            this.addFragment(new StorageInfoFragment());
        }
    }

    @Override
    public void setSubTitle() {
        mTitle.setSubTitleText(getString(R.string.about), "");
    }

    @Override
    public void setSubTitle(int resId) {
        mTitle.setSubTitleText(getString(R.string.about), getString(resId));
    }
}
