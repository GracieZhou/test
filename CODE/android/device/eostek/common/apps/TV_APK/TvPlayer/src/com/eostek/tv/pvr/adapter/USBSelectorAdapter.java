
package com.eostek.tv.pvr.adapter;

import java.util.ArrayList;

import com.eostek.tv.R;
import com.eostek.tv.utils.UtilsTools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/*
 * projectName： Tv
 * moduleName： USBSelectorAdapter.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-7-28 下午6:14:18
 * @Copyright © 2014 Eos Inc.
 */

public class USBSelectorAdapter extends BaseAdapter {

    // usb driver list
    private ArrayList<String> mUsbDriverPath;

    private Context mContext;

    public USBSelectorAdapter(Context context, ArrayList<String> path) {
        this.mContext = context;
        this.mUsbDriverPath = path;
    }

    @Override
    public int getCount() {
        if (mUsbDriverPath != null) {
            return mUsbDriverPath.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mUsbDriverPath.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layout.inflate(R.layout.eos_usb_driver_item, parent, false);
        }
        TextView itemName = (TextView) view.findViewById(R.id.usbItemName);
        itemName.setText(mUsbDriverPath.get(position));

        ProgressBar diskInfo = (ProgressBar) view.findViewById(R.id.usbItemSpace);
        diskInfo.setMax(100);
        ProgressBar tip = (ProgressBar) view.findViewById(R.id.tip);

        diskInfo.setProgress(UtilsTools.getAvailablePercent(mUsbDriverPath.get(position)));
        tip.setVisibility(View.INVISIBLE);

        return view;
    }

}
