
package com.android.settings.system;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.internal.app.LocalePicker.LocaleInfo;
import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class LanguageSettingsHolder {
    public static final String TAG = "LanguageSettingsHolder";

    private LanguageSettingsActivity mActivity;

    private TitleWidget mTitle;

    private ListView mLanguages;

    private LanguageSettingsAdapter myAdapter;

    public LanguageSettingsHolder(LanguageSettingsActivity activity) {
        this.mActivity = activity;
    }

    public void findViews() {
        mTitle = (TitleWidget) mActivity.findViewById(R.id.activity_system_settings_inputmethod_title);
        mLanguages = (ListView) mActivity.findViewById(R.id.system_settings_inputmethod_list);
    }

    public void initViews() {
        mTitle.setMainTitleText(mActivity.getString(R.string.action_settings));
        mTitle.setFirstSubTitleText(mActivity.getString(R.string.system_settings), false);
        mTitle.setSecondSubTitleText(mActivity.getString(R.string.language_settings));
    }

    public void registerAdapter() {
        LocaleInfo[] infos = mActivity.getmLogic().getLanguageList();
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < infos.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.language_focus);
            map.put("text", infos[i].getLabel());
            data.add(map);
        }
        myAdapter = new LanguageSettingsAdapter(mActivity, data);

        mLanguages.setAdapter(myAdapter);
    }

    public void registerListener() {
        mLanguages.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.getmLogic().updateLanguageSettings(position);

                myAdapter.notifyDataSetChanged();
            }
        });

    }

}
