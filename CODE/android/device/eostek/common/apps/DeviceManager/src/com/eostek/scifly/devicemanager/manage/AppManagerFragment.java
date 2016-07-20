package com.eostek.scifly.devicemanager.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.eostek.scifly.devicemanager.DeviceManagerActivity;
import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.manage.autostart.AutoStartActivity;

import com.eostek.scifly.devicemanager.manage.appmanagement.AppManagementActivity;
import com.eostek.scifly.devicemanager.manage.garbage.GarbageActivity;
import com.eostek.scifly.devicemanager.manage.process.ProcessActivity;
import com.eostek.scifly.devicemanager.util.Debug;

public class AppManagerFragment extends Fragment implements OnFocusChangeListener,OnClickListener, OnKeyListener{

    public static final String TAG = AppManagerFragment.class.getSimpleName();
    
    private DeviceManagerActivity mActivity;
    
    private RelativeLayout mLayoutProcess;
    private RelativeLayout mLayoutGarbage;
    private RelativeLayout mLayoutManager;
    private RelativeLayout mLayoutAutoStart;
    
    private RelativeLayout mLayoutProcessBg;
    private RelativeLayout mLayoutGarbageBg;
    private RelativeLayout mLayoutManagerBg;
    private RelativeLayout mLayoutAutoStartBg;
    
    public AppManagerFragment(DeviceManagerActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.d(TAG, "onCreate");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_manager, container, false);
        initViews(view);
        
        Debug.d(TAG, "onCreateView");
        return view;
    }
    
    @Override
    public void onResume() {
        Debug.d(TAG, "onResume");
        super.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Debug.d(TAG, "onPause");
    }
    
    
    @Override
    public void onStart() {
        Debug.d(TAG, "onStart");
        super.onStart();
    }
    
    @Override
    public void onStop() {
        Debug.d(TAG, "onStop");
        super.onStop();
    }
    
    @Override
    public void onDestroy() {
        Debug.d(TAG, "onDestroy");
        super.onDestroy();
    }
    
    private void initViews(View view) {
        mLayoutProcess = (RelativeLayout)view.findViewById(R.id.fragment_manager_rl_process);
        mLayoutGarbage = (RelativeLayout)view.findViewById(R.id.fragment_manager_rl_garbage);
        mLayoutManager = (RelativeLayout)view.findViewById(R.id.fragment_manager_rl_appmanager);
        mLayoutAutoStart = (RelativeLayout)view.findViewById(R.id.fragment_manager_rl_autostart);
        
        mLayoutProcessBg = (RelativeLayout)view.findViewById(R.id.fragment_manager_rl_process_bg);
        mLayoutGarbageBg = (RelativeLayout)view.findViewById(R.id.fragment_manager_rl_garbage_bg);
        mLayoutManagerBg = (RelativeLayout)view.findViewById(R.id.fragment_manager_rl_appmanager_bg);
        mLayoutAutoStartBg = (RelativeLayout)view.findViewById(R.id.fragment_manager_rl_autostart_bg);
        
        mLayoutProcess.setOnClickListener(this);
        mLayoutGarbage.setOnClickListener(this);
        mLayoutManager.setOnClickListener(this);
        mLayoutAutoStart.setOnClickListener(this);
        
        mLayoutProcess.setOnFocusChangeListener(this);
        mLayoutGarbage.setOnFocusChangeListener(this);
        mLayoutManager.setOnFocusChangeListener(this);
        mLayoutAutoStart.setOnFocusChangeListener(this);
        
        mLayoutProcess.setOnKeyListener(this);
        mLayoutGarbage.setOnKeyListener(this);
        mLayoutManager.setOnKeyListener(this);
        mLayoutAutoStart.setOnKeyListener(this);
    }

    private void viewScaleUp(View view, float xValue, float yValue) {

        ScaleAnimation inAnimation = new ScaleAnimation(1.0f, xValue, 1.0f, yValue, ScaleAnimation.RELATIVE_TO_SELF,
                0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        inAnimation.setDuration(200);
        inAnimation.setFillAfter(true);
        view.startAnimation(inAnimation);
    }

    private void viewScaleDown(View view, float xValue, float yValue) {
        ScaleAnimation inAnimation = new ScaleAnimation(xValue, 1.0f, yValue, 1.0f, ScaleAnimation.RELATIVE_TO_SELF,
                0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        inAnimation.setDuration(300);
        inAnimation.setFillAfter(true);
        view.startAnimation(inAnimation);
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        if(arg1) {
            arg0.bringToFront();
            switch (arg0.getId()) {
                case R.id.fragment_manager_rl_process:
                    mLayoutProcessBg.setBackgroundResource(R.drawable.fragment_manager_bg);
                    break;
                case R.id.fragment_manager_rl_garbage:
                    mLayoutGarbageBg.setBackgroundResource(R.drawable.fragment_manager_bg);
                    break;
                case R.id.fragment_manager_rl_appmanager:
                    mLayoutManagerBg.setBackgroundResource(R.drawable.fragment_manager_bg);
                    break;
                case R.id.fragment_manager_rl_autostart:
                    mLayoutAutoStartBg.setBackgroundResource(R.drawable.fragment_manager_bg);
                    break;
    
                default:
                    break;
            }
            viewScaleUp(arg0, 1.1f, 1.1f);
        } else {
            switch (arg0.getId()) {
                case R.id.fragment_manager_rl_process:
                    mLayoutProcessBg.setBackground(null);
                    break;
                case R.id.fragment_manager_rl_garbage:
                    mLayoutGarbageBg.setBackground(null);
                    break;
                case R.id.fragment_manager_rl_appmanager:
                    mLayoutManagerBg.setBackground(null);
                    break;
                case R.id.fragment_manager_rl_autostart:
                    mLayoutAutoStartBg.setBackground(null);
                    break;
    
                default:
                    break;
            }
            viewScaleDown(arg0, 1.0f, 1.0f);
        }
    }
    
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.fragment_manager_rl_process:
                startActivity(new Intent(mActivity, ProcessActivity.class));
                break;
            case R.id.fragment_manager_rl_garbage:
                startActivity(new Intent(mActivity, GarbageActivity.class));
                break;
            case R.id.fragment_manager_rl_appmanager:
                startActivity(new Intent(mActivity, AppManagementActivity.class));
                break;
            case R.id.fragment_manager_rl_autostart:
                startActivity(new Intent(mActivity, AutoStartActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                mActivity.reuqestFocus(0);
                return true;
            } else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT && mLayoutProcess.isFocused()) {
                return true;
            } else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && mLayoutAutoStart.isFocused()) {
                return true;
            }
        }
        return false;
    }

}
