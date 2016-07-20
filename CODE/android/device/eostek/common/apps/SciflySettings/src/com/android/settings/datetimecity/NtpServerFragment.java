
package com.android.settings.datetimecity;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.settings.R;

/**
 * NtpseverFragment
 */
public class NtpServerFragment extends Fragment {

    public static final String TAG = "NtpServerFragment";

    private DateTimeCitySettingsActivity mActivity;

    public static final int NTPSERVER_CHINA = 0;

    public static final int NTPSERVER_TAIWAN = 1;

    public static final int NTPSERVER_USA = 2;

    private ListView mNtpServers;
    
    private NtpServerAdapter mAdapter;

    private String[] ntpservers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.system_setting_layout, container, false);
        mNtpServers = (ListView) root.findViewById(R.id.list_view);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mActivity = (DateTimeCitySettingsActivity) this.getActivity();
        super.onActivityCreated(savedInstanceState);
        mActivity.setTitle(R.string.server);
        registerListener();
        registerAdapter();
    }

    
    public void registerAdapter() {
        ntpservers = new String[] {
                mActivity.getString(R.string.ntpserver_china),
                mActivity.getString(R.string.ntpserver_taiwan),
                mActivity.getString(R.string.ntpserver_american)
        };
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < ntpservers.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.circle);
            map.put("text", ntpservers[i]);
            data.add(map);
        }
        mAdapter = new NtpServerAdapter(getActivity(), data);
        mNtpServers.setAdapter(mAdapter);
        mAdapter.setCurrentNtpserver(getNtpServer());
        if (!mNtpServers.isFocused()) {
            mNtpServers.requestFocus();
            mNtpServers.setSelection(getNtpServer());
        }

    }

    public void registerListener() {
        mNtpServers.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sp = mActivity.getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                Editor spEditor = sp.edit();
                spEditor.putInt("ntp_server_id", position);
                spEditor.commit();
                Log.d(TAG, "<<<<<<ntp_server_id<<<<<<<<<"+position);
                mAdapter.setCurrentNtpserver(position);
                ((DateTimeCitySettingsActivity) getActivity()).getFragmentManager().popBackStack();
                mAdapter.notifyDataSetChanged();
                mActivity.mLogic.startTimeSync();
            }
        });
    }

    /**
     * return current ntpserver.
     * 
     */
    private int getNtpServer() {
        Log.d(TAG, "<<<<<<<<getNtpServer<<<<<<<<<<<<<<<<"+getActivity().getApplicationContext().getSharedPreferences("settings", 0)
                .getInt("ntp_server_id",0));
        return getActivity().getApplicationContext().getSharedPreferences("settings", 0)
                .getInt("ntp_server_id", 0);
    }
}
