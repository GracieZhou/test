
package com.android.settings.system;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class AppInstallLocationHolder {
    public static final String TAG = "InstallLocationHolder";

    private AppInstallLocationActivity mActivity;

    private TitleWidget mTitle;

    private ListView mLocations;

    private String[] locationStrings;

    private AppInstallLocationAdapter myAdapter;

    public AppInstallLocationHolder(AppInstallLocationActivity activity) {
        this.mActivity = activity;

    }

    public void findViews() {
        mTitle = (TitleWidget) mActivity.findViewById(R.id.activity_system_settings_inputmethod_title);
        mLocations = (ListView) mActivity.findViewById(R.id.system_settings_inputmethod_list);
        mTitle.setMainTitleText(mActivity.getString(R.string.action_settings));
        mTitle.setFirstSubTitleText(mActivity.getString(R.string.system_settings), false);
        mTitle.setSecondSubTitleText(mActivity.getString(R.string.install_location));
    }

    public void registerAdapter() {
        locationStrings = new String[] {
                mActivity.getString(R.string.install_location_auto),
                mActivity.getString(R.string.install_location_internal),
                mActivity.getString(R.string.install_location_sdcard)
        };
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < locationStrings.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.language_focus);
            map.put("text", locationStrings[i]);
            data.add(map);
        }
        myAdapter = new AppInstallLocationAdapter(mActivity, data);
        mLocations.setAdapter(myAdapter);

    }

    public void registerListener() {
        mLocations.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.getmLogic().setDefaultInstallLocation(position);
                myAdapter.notifyDataSetChanged();
            }
        });
    }
}
