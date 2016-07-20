
package com.android.settings.network.ethernet;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;

public class EthernetSettingFragment extends Fragment {

    private static final String TAG = "EthernetSettingFragment";

    private View root;

    private EthernetSettingLogic mLogic;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        mLogic = new EthernetSettingLogic(this, root);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.ethernet_link_property_config_layout, null);
        Log.i(TAG, "onCreateView");
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (mLogic != null) {
            mLogic.saveEthernetConfig();
        }

        super.onDestroy();
    }
}
