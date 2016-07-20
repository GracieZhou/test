
package com.android.settings.system.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.settings.R;
import com.android.settings.system.SystemSettingsActivity;
import com.android.settings.system.business.InputMethodSettingsLogic;
import com.android.settings.system.fragments.adapter.InputMethodSettingsAdapter;

public class InputMethodSettingFragment extends Fragment {

    private InputMethodSettingsLogic mLogic;

    private SystemSettingsActivity mActivity;

    private ListView mInputmethodListView;

    private ArrayList<String> inputmethodStrings;

    private InputMethodSettingsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.system_setting_layout, container, false);
        mInputmethodListView = (ListView) root.findViewById(R.id.list_view);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = (SystemSettingsActivity) getActivity();
        mActivity.setSubTitle(R.string.inputmethod_settings);
        mLogic = mActivity.getInputMethodSettingsLogic();
        registerListener();
        registerAdapter();
    }

    public void registerAdapter()  {
        inputmethodStrings = mLogic.getInputMethodLabelList();
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < inputmethodStrings.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.circle);
            map.put("text", inputmethodStrings.get(i));
            data.add(map);
        }
        mAdapter = new InputMethodSettingsAdapter(getActivity(), data);
        mInputmethodListView.setAdapter(mAdapter);
        mAdapter.setCurrentIME(mLogic.getCurrentInputMethodId());
        if (!mInputmethodListView.isFocused()) {
            mInputmethodListView.requestFocus();
            mInputmethodListView.setSelection(mLogic.getCurrentInputMethodId());
        }

    }

    public void registerListener() {
        mInputmethodListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String inputMethodLabel = ((TextView) view.findViewById(R.id.system_settings_inputmethodTV)).getText()
                        .toString();
                mLogic.switchInputMethod(inputMethodLabel);
                mAdapter.setCurrentIME(mLogic.getCurrentInputMethodId());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

}
