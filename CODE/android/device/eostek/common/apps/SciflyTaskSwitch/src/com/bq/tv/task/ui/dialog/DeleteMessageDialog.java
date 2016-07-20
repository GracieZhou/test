
package com.bq.tv.task.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bq.tv.task.device.StorageDeviceManager;
import com.bq.tv.task.device.StorageItem;
import com.eos.notificationcenter.R;
import com.eos.notificationcenter.utils.Util;

/**
 * Dialog to show mounted storage devices, the devices can be removed in this
 * dialog safely.
 */
public class DeleteMessageDialog implements OnItemClickListener {
	
	private AlertDialog mAlertDialog;
	
	private Context mContext;

    private ListView mDeviceListView;

    private Button mCancelBtn;

    private List<StorageItem> mDeviceItems;

    private ArrayList<HashMap<String, Object>> mDeviceInfo;

    private SimpleAdapter mSimpleAdapter;
    
    private OnDismissListener mOnDismissListener;

    public DeleteMessageDialog(Context context) {
        this.mContext = context;
        mDeviceItems = new ArrayList<StorageItem>();
        mDeviceInfo = new ArrayList<HashMap<String, Object>>();
    }

    private void initViews(Window window) {
        mSimpleAdapter = new SimpleAdapter(mContext, mDeviceInfo, R.layout.device_item, new String[] {
            "device_name"
        }, new int[] {
            R.id.device_name
        });

        mDeviceListView = (ListView) window.findViewById(R.id.device_list);
        mDeviceListView.setAdapter(mSimpleAdapter);
        mDeviceListView.setOnItemClickListener(this);

        mCancelBtn = (Button) window.findViewById(R.id.deleteMsgButton_cancel);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isShowing()) {
                	dismiss();
                }
            }
        });
    }

    public void dismiss() {
        mDeviceInfo.clear();
        mAlertDialog.dismiss();
        if(mOnDismissListener != null)
        	mOnDismissListener.onDismiss(null);
    }
    
    public boolean isShowing() {
    	if(mAlertDialog != null) {
    		return mAlertDialog.isShowing();
    	} else {
    		return false;
    	}
    }
    
    public void setOnDismissListener(OnDismissListener l) {
    	this.mOnDismissListener = l;
    }
    
    public void show() {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Translucent);
        mAlertDialog = builder.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAlertDialog.show();
        mAlertDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
            	
            }
        });
        
        Window window = mAlertDialog.getWindow();
        window.setContentView(R.layout.delete_message_dialog);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Util.getDiemnsionPixelSize(mContext, R.dimen.DELETE_DIALOG_WIDTH);
		lp.height  = Util.getDiemnsionPixelSize(mContext, R.dimen.DELETE_DIALOG_HEIGHT);
		window.setAttributes(lp);
		
		initViews(window);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        StorageItem item = mDeviceItems.get(position);
        if (item == null || item.getCurrentMountStatu() == StorageItem.UNMOUNTED)
            return;

        // Unmount the device selected.
        StorageDeviceManager sdm = new StorageDeviceManager(mContext);
        try {
            sdm.doUnmount(item.getStoragePath(), false);
            item.setCurrentMountStatu(StorageItem.UNMOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Notify users the device has unmounted.
        TextView deviceName = (TextView) view.findViewById(R.id.device_name);
        if (deviceName != null) {
            deviceName.setText(item.getLabel() + " (" + mContext.getString(R.string.unmounted) + ") ");
        }
    }

    /**
     * Set devices items.
     * 
     * @param items
     */
    public void setDevicesItems(List<StorageItem> items) {

        if (items != null) {
            mDeviceItems = items;
        }

        for (StorageItem item : mDeviceItems) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("device_name", "" + item.getLabel());
            mDeviceInfo.add(map);

        }
        mSimpleAdapter.notifyDataSetChanged();
    }
}
