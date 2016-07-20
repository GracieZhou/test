
package com.android.settings.system.fragments;

import java.util.ArrayList;
import java.util.List;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.android.internal.app.LocalePicker.LocaleInfo;
import com.android.settings.R;
import com.android.settings.system.SystemSettingsActivity;
import com.android.settings.system.business.LanguageSettingsLogic;
import com.android.settings.system.fragments.adapter.LanguageSettingsAdapter;

/**
 * @ClassName: LanguageSettingFragment.
 * @author: lucky.li.
 * @date: 2015-9-12 上午9:49:46.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class LanguageSettingFragment extends Fragment {

    private final String TAG = "LanguageSettingFragment";

    private LanguageSettingsLogic mLogic;

    private ListView mLanguageList;

    private LanguageSettingsAdapter mAdapter;

    private SystemSettingsActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.system_setting_layout, container, false);
        mLanguageList = (ListView) root.findViewById(R.id.list_view);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (SystemSettingsActivity) getActivity();
        mActivity.setSubTitle(R.string.language_settings);
        mLogic = mActivity.getLanguageSettingsLogic();
        registerListener();
        registerAdapter();
    }

    public void registerAdapter() {
        LocaleInfo[] infos = mLogic.getLanguageList();
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < infos.length; i++) {
            data.add(infos[i].getLabel());
        }
        mAdapter = new LanguageSettingsAdapter(getActivity(), data);
        mLanguageList.setAdapter(mAdapter);
        mAdapter.setCurrentLanguage(mLogic.getCurrentLanguage());
        if (!mLanguageList.isFocused()) {
            mLanguageList.requestFocus();
            mLanguageList.setSelection(mLogic.getCurrentLanguage());
        }
    }

    public void registerListener() {
        mLanguageList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLogic.updateLanguageSettings(position);
                mAdapter.setCurrentLanguage(position);
            }
        });
    }

}
