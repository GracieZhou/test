
package com.android.settings.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;

public class LanguageSettingsAdapter extends BaseAdapter {
    private LanguageSettingsActivity mActivity;

    private List<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();

    private LayoutInflater mInflater;

    public LanguageSettingsAdapter(LanguageSettingsActivity activity, ArrayList<HashMap<String, Object>> list) {
        this.mActivity = activity;
        this.mList = list;
        mInflater = LayoutInflater.from(activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public HashMap<String, Object> getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    private ImageView originCheckedIV;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView view = null;
        if (convertView == null) {
            view = new ListItemView();
            convertView = mInflater.inflate(R.layout.list_item_system_settings_inputmethod, null);
            view.image = (ImageView) convertView.findViewById(R.id.system_settings_inputmethodIV);
            view.text = (TextView) convertView.findViewById(R.id.system_settings_inputmethodTV);

            convertView.setTag(view);
        } else {
            view = (ListItemView) convertView.getTag();
        }

        view.image.setBackgroundResource((Integer) mList.get(position).get("image"));
        view.text.setText((String) mList.get(position).get("text"));
        int languageId = mActivity.getmLogic().getCurrentLanguage();
        if (languageId == position) {
            view.image.setVisibility(View.VISIBLE);
            originCheckedIV = view.image;

        } else {
            view.image.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public final class ListItemView {
        public ImageView image;

        public TextView text;
    }

}
