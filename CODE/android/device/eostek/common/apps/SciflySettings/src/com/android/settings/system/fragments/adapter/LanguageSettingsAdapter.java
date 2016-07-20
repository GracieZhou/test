
package com.android.settings.system.fragments.adapter;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.util.ViewHolder;

/**
 * @ClassName: LanguageSettingsAdapter.
 * @author: lucky.li.
 * @date: 2015-9-12 上午10:18:22.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class LanguageSettingsAdapter extends BaseAdapter {
    private List<String> mList = null;

    private LayoutInflater mInflater;

    private int mCurrentLanguage;

    public LanguageSettingsAdapter(Activity activity, List<String> list) {
        this.mList = list;
        mInflater = LayoutInflater.from(activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public String getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_system_settings_inputmethod, null);
        }
        ImageView iconView = ViewHolder.get(convertView, R.id.system_settings_inputmethodIV);
        TextView textView = ViewHolder.get(convertView, R.id.system_settings_inputmethodTV);
        textView.setText(getItem(position));
        if (mCurrentLanguage == position) {
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void setCurrentLanguage(int currentLanguage) {
        mCurrentLanguage = currentLanguage;
        Log.i("lucky", "mCurrentLanguage=="+mCurrentLanguage);
        this.notifyDataSetChanged();
    }
}
