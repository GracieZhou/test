
package com.eostek.mkeyeventservice.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.eostek.mkeyeventservice.Constants;
import com.eostek.mkeyeventservice.R;
import com.eostek.mkeyeventservice.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import scifly.view.KeyEventExtra;

public class DataDisplayDialog extends Dialog {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.DISMISS:
                    dismiss();
            }
        }
    };

    private Context mcontext;

    private ListView mDataListView;

    public DataDisplayDialog(Context context, int theme) {
        super(context, theme);
        mcontext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_layout);
        findViews();
        initData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        mHandler.removeMessages(Constants.DISMISS);
        mHandler.sendEmptyMessageDelayed(Constants.DISMISS, 10000);

        switch (keyCode) {
            case KeyEventExtra.KEYCODE_MSTAR_DATA_DISPLAY:
                dismiss();
                return true;
            default:
                mHandler.sendEmptyMessage(Constants.DISMISS);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void findViews() {
        mDataListView = (ListView) findViewById(R.id.data_listview);
    }

    private void initData() {
        SimpleAdapter adapter = new SimpleAdapter(mcontext, getData(), R.layout.data_list_item, new String[] {
                "name", "content"
        }, new int[] {
                R.id.name, R.id.content
        });
        mDataListView.setAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {

        String IPAddress = Utils.getLocalHostIp();
        String MAC = Utils.getLocalMac();
        String Language = "English";
        String singal = "";
        if (isZh()) {
            Language = "中文";
        }
        final String HdcpKey = Utils.getHdcpKey();
        if (HdcpKey.length() >= 289) {
            singal = "√";
        } else {
            singal = "ㄨ";
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "IP");
        map.put("content", IPAddress);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "MAC");
        map.put("content", MAC);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", mcontext.getResources().getString(R.string.language));
        map.put("content", Language);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "HDCP1.4");
        map.put("content", singal);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", mcontext.getResources().getString(R.string.system));
        map.put("content", Utils.getVersion());
        list.add(map);

        return list;

    }

    private boolean isZh() {
        Locale locale = mcontext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
