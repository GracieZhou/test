
package com.android.settings.system;

import scifly.storage.StorageManagerExtra;
import android.os.Bundle;

import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.system.business.InputMethodSettingsLogic;
import com.android.settings.system.business.LanguageSettingsLogic;
import com.android.settings.system.fragments.SystemSettingFragment;

/**
 * SystemSettingsActivity.
 * 
 * @author Davis
 * @Date 2015-8-18
 */
public class SystemSettingsActivity extends BaseSettingActivity {

    private LanguageSettingsLogic mLanguageSettingsLogic;

    private InputMethodSettingsLogic mInputMethodSettingsLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager.beginTransaction().replace(R.id.fragment_content, new SystemSettingFragment()).commit();
    }

    public void setSubTitle() {
        mTitle.setSubTitleText(getString(R.string.system_settings), "");
    }

    @Override
    public void setSubTitle(int resId) {
        setSubTitle(getString(R.string.system_settings), getString(resId));
    }

    public LanguageSettingsLogic getLanguageSettingsLogic() {
        if (mLanguageSettingsLogic == null) {
            mLanguageSettingsLogic = new LanguageSettingsLogic(this);
        }
        return mLanguageSettingsLogic;
    }

    public InputMethodSettingsLogic getInputMethodSettingsLogic() {
        if (mInputMethodSettingsLogic == null) {
            mInputMethodSettingsLogic = new InputMethodSettingsLogic(this);
        }
        return mInputMethodSettingsLogic;
    }

    /**
     * @Title: isHasUDisk.
     * @Description: isHasUDisk
     * @param: @return.
     * @return: boolean.
     * @throws
     */
    public boolean isHasUDisk() {
        StorageManagerExtra storageManager = StorageManagerExtra.getInstance(this);
        String[] uDiskPaths = storageManager.getUdiskPaths();
        if (uDiskPaths == null || uDiskPaths.length == 0) {
            return false;
        } else {
            return true;
        }
    }
}
