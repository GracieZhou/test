
package com.eostek.isynergy.setmeup.language;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.model.LanguageModel;

public class LanguageAdapter extends BaseAdapter {
    private static final String TAG = LanguageAdapter.class.getSimpleName();

    private Context mContext;

    private List<LanguageModel> mLanguages = new ArrayList<LanguageModel>();

    private LayoutInflater mInflater;

    public LanguageAdapter(Context context, List<LanguageModel> languages) {
        this.mContext = context;
        this.mLanguages = languages;
        mInflater = LayoutInflater.from(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mLanguages.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListItemView view = null;
        if (convertView == null) {
            view = new ListItemView();
            convertView = mInflater.inflate(R.layout.listview_item_language, null);
            view.image = (ImageView) convertView.findViewById(R.id.iv_language);
            view.text = (TextView) convertView.findViewById(R.id.tv_language);

            convertView.setTag(view);
        } else {
            view = (ListItemView) convertView.getTag();
        }

        LanguageModel currentItem = mLanguages.get(position);

        String currentLanguage = mContext.getResources().getConfiguration().locale + "";
        String language = currentItem.getDisplayName();

        view.text.setText(language);
        if (currentLanguage.equals(currentItem.getOrigalLocale())) {
            view.image.setVisibility(View.VISIBLE);
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
