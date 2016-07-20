
package com.eostek.tv.player.pvr;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.eostek.tv.player.R;
import com.mstar.android.tv.TvPvrManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

public class PVRMenuInfoActivity extends Activity {
    private String[] data = new String[] {};

    private ArrayAdapter<String> adapter = null;

    private ListView listView = null;

    private static boolean isDiskSelectPage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_pvr_menu_info);
        listView = (ListView) findViewById(R.id.pvr_menu_info_list_view);
        TextView title = (TextView) findViewById(R.id.pvr_menu_info_subtitle);
        adapter = new ArrayAdapter<String>(this, R.layout.eos_pvr_menu_info_list_view_item, data);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        String titleStr = savedInstanceState.getString("mstar.tvsetting.ui.PVRMenuInfoActivity");
        title.setText(titleStr);
        if (titleStr.equals("Select Disk")) {
            try {
                updateUsbDeviceAndPartitionInfoList();
            } catch (TvCommonException e) {

                e.printStackTrace();
            }
            isDiskSelectPage = true;
        } else if (titleStr.equals("Time Shift Size")) {
            // no list
            adapter.clear();
            isDiskSelectPage = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_L:
                if (isDiskSelectPage) {
                    try {
                        selectUsbDisk(true);
                    } catch (TvCommonException e) {

                        e.printStackTrace();
                    }
                } else {
                    // change time shift size for recorder
                    try {
                        selectTimeShiftSize(true);
                    } catch (TvCommonException e) {

                        e.printStackTrace();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_R:
                if (isDiskSelectPage) {
                    try {
                        selectUsbDisk(false);
                    } catch (TvCommonException e) {

                        e.printStackTrace();
                    }
                } else {
                    // change time shift size for recorder
                    try {
                        selectTimeShiftSize(false);
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void updateUsbDeviceAndPartitionInfoList() throws TvCommonException {
        TvPvrManager pvr = TvPvrManager.getInstance();
        // clear all
        adapter.clear();
        int idx = 0;
        int partitionCount = 0;
        String tmpStr = null;
        // usb device name, store at index 0
        tmpStr = "USB " + pvr.getUsbDeviceIndex();
        adapter.insert(tmpStr, 0);
        // usb partition name, store from index 1
        partitionCount = pvr.getUsbPartitionNumber();
        for (idx = 0; idx < partitionCount; idx++) {
            tmpStr = "DISK " + idx;
            adapter.insert(tmpStr, (idx + 1));
        }
        // invalidate page
        adapter.notifyDataSetChanged();
        listView.invalidate();
    }

    public void selectUsbDisk(boolean isLeftKey) throws TvCommonException {
        int curDeviceIndex = 0;
        int totalDeviceCount = 0;
        TvPvrManager pvr = TvPvrManager.getInstance();
        curDeviceIndex = pvr.getUsbDeviceIndex();
        totalDeviceCount = pvr.getUsbDeviceNumber();
        if (isLeftKey == true) {
            if (curDeviceIndex > 0) {
                curDeviceIndex--;
            } else {
                curDeviceIndex = pvr.getUsbDeviceNumber() - 1;
            }
        } else {
            if (curDeviceIndex < (totalDeviceCount - 1)) {
                curDeviceIndex++;
            } else {
                curDeviceIndex = 0;
            }
        }
        pvr.changeDevice((short) curDeviceIndex);
        updateUsbDeviceAndPartitionInfoList();
    }

    public void selectTimeShiftSize(boolean isLeftKey) throws TvCommonException {
        // clear all
        adapter.clear();
        // select time shift size!!
        // invalidate page
        adapter.notifyDataSetChanged();
        listView.invalidate();
    }
}
