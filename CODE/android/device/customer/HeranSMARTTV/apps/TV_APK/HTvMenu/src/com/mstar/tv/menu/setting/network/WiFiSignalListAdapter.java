
package com.mstar.tv.menu.setting.network;

import java.util.List;

import com.mstar.tv.menu.R;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WiFiSignalListAdapter extends BaseAdapter {

    private static final int SECURITY_NONE = 0;

    private static final int SECURITY_WEP = 1;

    private static final int SECURITY_PSK = 2;

    private static final int SECURITY_EAP = 3;

    private static final int[][] WIFI_SIGNAL_IMG = {
            {
                    R.drawable.wifi_lock_signal_0, R.drawable.wifi_lock_signal_1, R.drawable.wifi_lock_signal_2,
                    R.drawable.wifi_lock_signal_3
            },
            {
                    R.drawable.wifi_signal_0, R.drawable.wifi_signal_1, R.drawable.wifi_signal_2,
                    R.drawable.wifi_signal_3
            }
    };

    private Context mContext;

    private List<ScanResult> mScanResultList;

    private ViewHolder viewHolder = null;

    // security level
    private int mSecurity;

    private String mSsid = "";

    private boolean mConnected;

    public WiFiSignalListAdapter(Context context, List<ScanResult> list) {
        this.mContext = context;
        this.mScanResultList = list;
    }

    public void updateConnectedSsid(String ssid, boolean connected) {
        mSsid = ssid;
        mConnected = connected;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mScanResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return mScanResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View view = null;

        if (convertView == null || convertView.getTag() == null) {
            view = factory.inflate(R.layout.wifi_list_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScanResult result = mScanResultList.get(position);

        viewHolder.ssid.setText(result.SSID);
        String tmp = mSsid.replaceAll("\"", "");
        if (result.SSID.equals(tmp)) {
            if (mConnected) {
                viewHolder.connect.setText(R.string.wifi_display_status_connected);
            }
            // else {
            // viewHolder.connect.setText(R.string.wifi_display_status_connecting);
            // }
        } else {
            viewHolder.connect.setText("");
        }

        viewHolder.level.setText(getLevel(result.level) + "");
        try {
            mSecurity = getSecurity(result);
        } catch (Exception e) {
            mSecurity = SECURITY_NONE;
            // TODO: handle exception
        }

        setBackground(getLevel(result.level));

        return view;
    }

    /**
     * Calculates the level of the signal.
     */
    int getLevel(int level) {
        if (level == Integer.MAX_VALUE) {
            return -1;
        }

        return WifiManager.calculateSignalLevel(level, 4);
    }

    /**
     * set the drawable to show the wifi signal level.
     */
    private void setBackground(int level) {
        if (level == Integer.MAX_VALUE) {
            viewHolder.icon.setImageDrawable(null);
        } else {
            int index = (mSecurity != SECURITY_NONE) ? 0 : 1;
            if (level < 0 || 3 < level)
                level = 0;
            viewHolder.icon.setImageResource(WIFI_SIGNAL_IMG[index][level]);
        }
    }

    /**
     * get the security level.
     */
    private int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }

        return SECURITY_NONE;
    }

    class ViewHolder {
        TextView ssid;

        TextView level;

        TextView connect;

        ImageView icon;

        public ViewHolder(View view) {
            this.ssid = (TextView) view.findViewById(R.id.ssid);
            this.level = (TextView) view.findViewById(R.id.level);
            this.connect = (TextView) view.findViewById(R.id.connect_hint);
            this.icon = (ImageView) view.findViewById(R.id.iv);
        }
    }

}
