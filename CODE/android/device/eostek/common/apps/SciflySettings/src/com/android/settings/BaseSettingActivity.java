
package com.android.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.settings.widget.TitleWidget;

/**
 * BaseSettingActivity.
 * 
 * @author Davis
 * @date 2015-8-19
 */
public abstract class BaseSettingActivity extends Activity {

    protected TitleWidget mTitle;

    protected FragmentManager mFragmentManager;
    
    public ImageView mUpgrade;

    public RelativeLayout mUpgradeParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);

        mTitle = (TitleWidget) findViewById(R.id.activity_system_settings_title);
        
        mUpgrade = (ImageView) findViewById(R.id.right_image);

        mUpgradeParentLayout = (RelativeLayout) findViewById(R.id.right_image_parent);

        mFragmentManager = getFragmentManager();
        // If the language changes, this activity will be destroyed and then on
        // create, but the back stack may have kept several fragments. We pop
        // the BackStack when activity is created, and then add the first
        // fragment.
        mFragmentManager.popBackStack();
    }

    public void addFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
        transaction.replace(R.id.fragment_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public abstract void setSubTitle();

    public abstract void setSubTitle(int resId);

    public void setSubTitle(String title, String title2) {
        mTitle.setSubTitleText(title, title2);
    }
}
