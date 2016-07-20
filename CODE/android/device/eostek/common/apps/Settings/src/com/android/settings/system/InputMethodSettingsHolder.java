
package com.android.settings.system;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class InputMethodSettingsHolder {
    private InputMethodSettingsActivity mActivity;

    private TitleWidget mTitle;

    private ListView mInputmethods;

    private ArrayList<String> inputmethodStrings;

    private InputMethodSettingsAdapter myAdapter;

    public InputMethodSettingsHolder(InputMethodSettingsActivity activity) {
        this.mActivity = activity;

    }

    public void findView() {
        mTitle = (TitleWidget) mActivity.findViewById(R.id.activity_system_settings_inputmethod_title);
        mInputmethods = (ListView) mActivity.findViewById(R.id.system_settings_inputmethod_list);
        mTitle.setMainTitleText(mActivity.getString(R.string.action_settings));
        mTitle.setFirstSubTitleText(mActivity.getString(R.string.system_settings), false);
        mTitle.setSecondSubTitleText(mActivity.getString(R.string.inputmethod_settings));
    }

    public void registerAdapter()  {
        inputmethodStrings = mActivity.getmLogic().getInputMethodLabelList();
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < inputmethodStrings.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.language_focus);
            map.put("text", inputmethodStrings.get(i));
            data.add(map);
        }
        myAdapter = new InputMethodSettingsAdapter(mActivity, data);
        mInputmethods.setAdapter(myAdapter);

    }

    public void registerListener() {
        mInputmethods.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String inputMethodLabel = ((TextView) view.findViewById(R.id.system_settings_inputmethodTV)).getText()
                        .toString();
                mActivity.getmLogic().switchInputMethod(inputMethodLabel);
                myAdapter.notifyDataSetChanged();
            }
        });
    }
}
