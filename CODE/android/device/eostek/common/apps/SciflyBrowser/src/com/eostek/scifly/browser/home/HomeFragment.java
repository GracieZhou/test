package com.eostek.scifly.browser.home;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.modle.UrlModle;
import com.jess.ui.TwoWayGridView;

/**
 * projectName： Browser moduleName： HomeFragment.java
 * 
 * @author Shirley.jiang & Ahri.chen
 * @time 2016-1-27 
 */
public class HomeFragment extends Fragment{

    private final String TAG = "HomeFragment";

    private BrowserActivity mActivity;

    private EditText mSearchInput;

    private Button mSearchBtn;

    private TwoWayGridView mGridView;

    private List<UrlModle> mList;

    private HomeLogic mLogic;

    private HomeHolder mHolder;

    public HomeFragment() {
    }

    public HomeFragment(BrowserActivity activity) {
        mActivity = activity;
        mLogic = new HomeLogic(mActivity);
        mHolder = new HomeHolder(mActivity);
        mHolder.setLogic(mLogic);
        mLogic.setHolder(mHolder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView...");
        View view = inflater.inflate(R.layout.home_layout, container, false);
        mHolder.findView(view);

        mLogic.registerReceiver();
        mLogic.initData();
        mHolder.setListener();
        return view;
    }

    public HomeLogic getLogic() {
        return mLogic;
    }
}
