
package com.eostek.scifly.devicemanager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.service.DownloadService;
import com.eostek.scifly.devicemanager.manage.AppManagerFragment;
import com.eostek.scifly.devicemanager.recommend.AppRecommendFragment;
import com.eostek.scifly.devicemanager.util.Debug;

import java.util.ArrayList;



public class DeviceManagerActivity extends FragmentActivity implements OnFocusChangeListener,OnPageChangeListener,OnClickListener{

    private static final String TAG = DeviceManagerActivity.class.getSimpleName();

    private ArrayList<Integer> keyQueue;
    
    private static final String toMTA = String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT) + String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP) + String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT) + String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP);
    
    private FragmentManager mFragmentManager;
    private ViewPager mViewPager;
    private HomePageAdapter mHomePageAdapter;
    private TextView mTvManage;
    private TextView mTvRecommend;
    private ImageView mIvManage;
    private ImageView mIvRecommend;
    
    private ArrayList<Fragment> mFunFragments;
    private Fragment mAppManagerFragment;
    private Fragment mAppRecommendFragment;
    
    private int mCurrentPageIndex = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        initFragments();
        initValue();
        initViews();
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Debug.d(TAG, "onResume");
    }
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Debug.d(TAG, "onStart");
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Debug.d(TAG, "onStop");
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Debug.d(TAG, "onDestroy");
    }
    
    private void initValue() {
        Intent mIntent = new Intent(this, DownloadService.class);
        startService(mIntent);
    }
    
    private void initViews() {
        mFragmentManager = getSupportFragmentManager();
        
        mTvManage = (TextView)findViewById(R.id.activity_main_title_manager);
        mIvManage = (ImageView)findViewById(R.id.act_main_title_manage_bg_2);
        mTvRecommend = (TextView)findViewById(R.id.activity_main_title_recommend);
        mIvRecommend = (ImageView)findViewById(R.id.act_main_title_recommend_bg_2);
        
        mViewPager = (ViewPager)findViewById(R.id.activity_main_viewpager);
        mHomePageAdapter = new HomePageAdapter(mFragmentManager);
        mViewPager.setAdapter(mHomePageAdapter);
        mViewPager.setOnPageChangeListener(this);
        
        mIvManage.setVisibility(View.INVISIBLE);
        mIvRecommend.setVisibility(View.INVISIBLE);
        
        mTvManage.setOnFocusChangeListener(this);
        mTvRecommend.setOnFocusChangeListener(this);
        
        mTvManage.setOnClickListener(this);
        mTvRecommend.setOnClickListener(this);
    }
    
    private void initFragments() {
        mFunFragments = new ArrayList<Fragment>();
        if(mAppManagerFragment == null) {
            mAppManagerFragment = new AppManagerFragment(this);
            mFunFragments.add(mAppManagerFragment);
        }
        
        if(mAppRecommendFragment == null) {
            mAppRecommendFragment = new AppRecommendFragment(this);
            mFunFragments.add(mAppRecommendFragment);
        }
    }

    private void setFragments(int index) {
        mCurrentPageIndex = index;
        if (mViewPager != null && index >= 0) {
            try {
                mViewPager.setCurrentItem(index);
            } catch (Exception e) {
                Debug.d(TAG, "Exception:" + e);
            }
        }
    }
    
    public void reuqestFocus(int index) {
        if (index == 0) {
            mTvManage.requestFocus();
        } else if (index == 1) {
            mTvRecommend.requestFocus();
        }
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        if(this.isFinishing()) {
            Debug.d(TAG, "isFinishing");
            return;
        }
        switch (arg0.getId()) {
            case R.id.activity_main_title_manager:
                Debug.d(TAG, "onFocusChange:manager  :" + arg1);
                if(arg1 == true) {
                    setFragments(0);
                    mIvManage.setVisibility(View.INVISIBLE);
                    mIvRecommend.setVisibility(View.INVISIBLE);
                } else {
                    if(mCurrentPageIndex == 0) {
                        mIvManage.setVisibility(View.VISIBLE);
                    }
                }
                break;
                
            case R.id.activity_main_title_recommend:
                Debug.d(TAG, "onFocusChange:recommend:" + arg1);
                if(arg1 == true) {
                    setFragments(1);
                    mIvManage.setVisibility(View.INVISIBLE);
                    mIvRecommend.setVisibility(View.INVISIBLE);
                } else {
                    if(mCurrentPageIndex == 1) {
                        mIvRecommend.setVisibility(View.VISIBLE);
                    }
                }
                break;

            default:
                break;
        }
    }
    
    @Override
    public void onPageScrollStateChanged(int arg0) {
        
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        
    }

    @Override
    public void onPageSelected(int arg0) {
        Debug.d(TAG, "onPageSelected: mCurrentPageIndex = " + arg0);
        mCurrentPageIndex = arg0;
        switch (arg0) {
            case 0:
                if(!mTvManage.isFocused()) {
                    mIvManage.setVisibility(View.VISIBLE);
                    mIvRecommend.setVisibility(View.INVISIBLE);
                }
                break;
            
            case 1:
                if(!mTvRecommend.isFocused()) {
                    mIvRecommend.setVisibility(View.VISIBLE);
                    mIvManage.setVisibility(View.INVISIBLE);
                }
                break;
            
            default:
                break;
        }
    }

    class HomePageAdapter extends FragmentPagerAdapter {

        public HomePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFunFragments.get(arg0 % mFunFragments.size());
        }

        @Override
        public int getCount() {
            return mFunFragments.size();
        }
        
    }
    
    private String intArrayListToString(ArrayList<Integer> al) {
        String str = "";
        for (int i = 0; i < al.size(); ++i) {
            str += al.get(i).toString();
        }
        return str;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
     // 开机启动3分钟内才有效果
        if (SystemClock.elapsedRealtime() > 180000) {
            return super.onKeyDown(keyCode, event);
        } else {
            if (keyQueue == null) {
                keyQueue = new ArrayList<Integer>();
            }
        }
        
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                keyQueue.add(event.getKeyCode());
                if(keyQueue.size() == 8){
                    if (intArrayListToString(keyQueue).equals(toMTA)) {
                        keyQueue.clear();
                        PackageManager pm = getPackageManager();
                        ComponentName name = new ComponentName("com.utsmta.app", "com.utsmta.app.MainActivity");
                        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                        Debug.d(TAG, "Entering MTA MainActivity");
                        
                        Intent intent = new Intent();
                        intent.setAction("com.utsmta.app.MainActivity");
                        intent.putExtra("fromLauncher", true);
                        startActivity(intent);
                    }
                    else{
                        keyQueue.remove(0);
                    }
                }
                break;
                
            default:
                keyQueue.clear();
                break;
        } 
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.activity_main_title_manager:
                setFragments(0);
                mIvManage.setVisibility(View.INVISIBLE);
                mIvRecommend.setVisibility(View.INVISIBLE);
                break;
            case R.id.activity_main_title_recommend:
                setFragments(1);
                mIvManage.setVisibility(View.INVISIBLE);
                mIvRecommend.setVisibility(View.INVISIBLE);
                break;    
            default:   
                break;
        }
    }
}

