
package com.eostek.tvmenu.picture;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuApplication;

public class PictureSettingFragment extends Fragment {
    
    PictureSettingHolder mHolder;
    
    PictureSettingLogic mLogic;
    
    TvMenuApplication mApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_setting_fragment, null);
        mApplication = (TvMenuApplication)getActivity().getApplication();
        
        mHolder = new PictureSettingHolder(this);
        mLogic = new PictureSettingLogic(this);
        
        mHolder.initView(view);
        mHolder.initItems();
        mHolder.setListener();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Seekbar.setProgress is of no effect when invoked in Fragment's onCreateView.
        mLogic.initData();
    }

}
