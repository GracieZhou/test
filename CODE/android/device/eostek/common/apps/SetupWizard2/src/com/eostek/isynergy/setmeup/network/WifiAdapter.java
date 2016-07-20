
package com.eostek.isynergy.setmeup.network;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.utils.Utils;

public class WifiAdapter extends BaseAdapter {
    private static final String TAG = WifiAdapter.class.getSimpleName();

    private Context mContext;

    private List<ScanResult> mWifiResultList;

    private LayoutInflater mInflater;

    public WifiAdapter(Context context, List<ScanResult> wifiResultList) {
        this.mContext = context;
        this.mWifiResultList = wifiResultList;

        mInflater = LayoutInflater.from(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mWifiResultList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mWifiResultList.get(arg0);
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
            convertView = mInflater.inflate(R.layout.listview_item_wifi, null);
            view.stateImage = (ImageView) convertView.findViewById(R.id.listImg1);
            view.ssidTxt = (TextView) convertView.findViewById(R.id.tv_wifi_item);
            view.levelImage = (ImageView) convertView.findViewById(R.id.listImg2);

            convertView.setTag(view);
        } else {
            view = (ListItemView) convertView.getTag();
        }

        ScanResult currentItem = mWifiResultList.get(position);

        view.stateImage.setImageResource(R.drawable.p_wifi_lock);
        view.ssidTxt.setText(currentItem.SSID);

        int level = Utils.calculateSignalLevel(currentItem.level);
        switch (level) {
            case Utils.WIFI_LEVEL_ERROR:
                view.levelImage.setImageResource(R.drawable.wifi_level_none);
                break;
            case Utils.WIFI_LEVEL_WEAK:
                view.levelImage.setImageResource(R.drawable.wifi_level_weak);
                break;
            case Utils.WIFI_LEVEL_MIDDLE:
                view.levelImage.setImageResource(R.drawable.wifi_level_middle);
                break;
            case Utils.WIFI_LEVEL_STRONG:
                view.levelImage.setImageResource(R.drawable.wifi_level_strong);
                break;
            default:
                break;
        }

        return convertView;
    }

    public final class ListItemView {
        public ImageView stateImage;

        public TextView ssidTxt;

        public ImageView levelImage;
    }

}
