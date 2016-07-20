
package com.eostek.tvmenu.network;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.eostek.tvmenu.R;


public class NetWorkFragment extends Fragment {
    
    public NetworkHolder mHolder;
    public NetworkLogic mLogic;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.network_setting_fragment, null);
        
        mHolder = new NetworkHolder(this);
        mLogic = new NetworkLogic(this);
        mHolder.initView(view);
        mHolder.setListener();
        return view;
    }
	
}
