
package com.eostek.scifly.browser.settool;

import java.util.ArrayList;
import java.util.List;

import com.android.browser.BrowserSettings;
import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClearAdapter extends BaseAdapter {

    private BrowserActivity mActivity;

    public ArrayList mClearList = new ArrayList();

    private static final int CLEAR_HIS_ITEM = 0;

    private static final int CLEAR_CRASH_DATA_ITEM = 1;

    private static final int CLEAR_LIST_DATA_ITEM = 2;

    private static final int CLEAR_PSW_ITEM = 3;

    private static final int CLEAR_COOKIE_ITEM = 4;

    private static final int CLEAR_POSITION_PER_ITEM = 5;

    public ClearAdapter(String[] mClearStr, BrowserActivity activity) {

        for (int i = 0; i < mClearStr.length; i++) {
            mClearList.add(mClearStr[i]);
        }

        this.mActivity = activity;
    }

    @Override
    public int getCount() {

        if (mClearList == null) {
            return 0;
        } else {
            return mClearList.size();
        }
    }

    @Override
    public Object getItem(int position) {

        return mClearList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.clear_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.clear_title);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.clear_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text.setText(mClearList.get(position).toString());

        BrowserSettings settings = BrowserSettings.getInstance(mActivity);
        switch (position) {
            case CLEAR_HIS_ITEM:
                if (settings.isClearHistory()) {
                    viewHolder.image.setBackgroundResource(R.drawable.check_on);
                } else {
                    viewHolder.image.setBackgroundResource(R.drawable.check_off);
                }
                break;

            case CLEAR_CRASH_DATA_ITEM:
                if (settings.isClearCache()) {
                    viewHolder.image.setBackgroundResource(R.drawable.check_on);
                } else {
                    viewHolder.image.setBackgroundResource(R.drawable.check_off);
                }
                break;

            case CLEAR_LIST_DATA_ITEM:
                if (settings.isClearFormData()) {
                    viewHolder.image.setBackgroundResource(R.drawable.check_on);
                } else {
                    viewHolder.image.setBackgroundResource(R.drawable.check_off);
                }
                break;

            case CLEAR_PSW_ITEM:
                if (settings.isClearPassword()) {
                    viewHolder.image.setBackgroundResource(R.drawable.check_on);
                } else {
                    viewHolder.image.setBackgroundResource(R.drawable.check_off);
                }
                break;

            case CLEAR_COOKIE_ITEM:
                if (settings.isClearCookie()) {
                    viewHolder.image.setBackgroundResource(R.drawable.check_on);
                } else {
                    viewHolder.image.setBackgroundResource(R.drawable.check_off);
                }
                break;

            case CLEAR_POSITION_PER_ITEM:
                if (settings.isCancleLocatPermission()) {
                    viewHolder.image.setBackgroundResource(R.drawable.check_on);
                } else {
                    viewHolder.image.setBackgroundResource(R.drawable.check_off);
                }
                break;

            default:
                break;
        }

        return convertView;
    }

    class ViewHolder {
        public TextView text;

        public ImageView image;

    }

}
