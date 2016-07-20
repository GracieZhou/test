
package com.eostek.tvmenu.network;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.Constants;

import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NetworkHolder {

    private NetWorkFragment mFragment;

    private LinearLayout mItemWiredNetwork = null;

    private LinearLayout mItemWirelessNetwork = null;

    private LinearLayout mItemExpertSettings = null;

    private TextView mWiredNetworkTitleTxt;

    private TextView mWirelessNetworkTitleTxt;

    private TextView mExpertSettingsTitleTxt;

    private String[] mTitleNetworkSettingStr;

    Resources mR;
    
    public NetworkHolder(NetWorkFragment f) {
        mFragment = f;
        mR = mFragment.getActivity().getResources();
    }

    /**
	 * init the view of all items.item_titles and item_values
	 * 
	 * @param view
	 */
    protected void initView(View view) {
        mTitleNetworkSettingStr = mFragment.getActivity().getResources().getStringArray(R.array.network);

        //WiredNetwork
        mItemWiredNetwork = (LinearLayout) view.findViewById(R.id.item_wired_network);
        mWiredNetworkTitleTxt = (TextView) mItemWiredNetwork.findViewById(R.id.title_txt);

        //WirelessNetwork
        mItemWirelessNetwork = (LinearLayout) view.findViewById(R.id.item_wireless_network);
        mWirelessNetworkTitleTxt = (TextView) mItemWirelessNetwork.findViewById(R.id.title_txt);

        //ExpertSettings
        mItemExpertSettings = (LinearLayout) view.findViewById(R.id.item_expert_settings);
        mExpertSettingsTitleTxt = (TextView) mItemExpertSettings.findViewById(R.id.title_txt);
        
        //init title text
        mWiredNetworkTitleTxt.setText(mTitleNetworkSettingStr[0]);
        mWirelessNetworkTitleTxt.setText(mTitleNetworkSettingStr[1]);
        mExpertSettingsTitleTxt.setText(mTitleNetworkSettingStr[2]);
    }

    /**
	 * set OnKeyListener and OnFocusListener
	 */
    protected void setListener() {

        OnKeyListener OnKeyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER: {
                            if (!view.isFocusable()) {
                                return true;
                            }

                            switch (view.getId()) {

                                case R.id.item_wired_network: {
                                    mFragment.mLogic.startNetworkSetting(Constants.WIRED_NETWORK_SETTING);
                                }
                                    break;
                                case R.id.item_wireless_network: {
                                    mFragment.mLogic.startNetworkSetting(Constants.WIREDLESS_NETWORK_SETTING);
                                }
                                    break;
                                case R.id.item_expert_settings: {
                                    mFragment.mLogic.startExpertSetting();
                                }
                            }

                        }
                        case KeyEvent.KEYCODE_DPAD_RIGHT: 
                        	return true;
                    }
                    
                }
                return false;
            }
        };

        //set Items OnKeyListener
        mItemWiredNetwork.setOnKeyListener(OnKeyListener);
        mItemWirelessNetwork.setOnKeyListener(OnKeyListener);
        mItemExpertSettings.setOnKeyListener(OnKeyListener);

        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean haiFocus) {

                switch (view.getId()) {
                    case R.id.item_wired_network: {
                        if (haiFocus) {
                            buttomItemFocused(view, mWiredNetworkTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mWiredNetworkTitleTxt);
                        }
                    }
                        break;

                    case R.id.item_wireless_network: {
                        if (haiFocus) {
                            buttomItemFocused(view, mWirelessNetworkTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mWirelessNetworkTitleTxt);
                        }
                    }
                        break;

                    case R.id.item_expert_settings: {
                        if (haiFocus) {
                            buttomItemFocused(view, mExpertSettingsTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mExpertSettingsTitleTxt);
                        }
                    }
                        break;

                }
            }
        };

      //set Items OnFocusChangeListener
        mItemWiredNetwork.setOnFocusChangeListener(onFocusChangeListener);
        mItemWirelessNetwork.setOnFocusChangeListener(onFocusChangeListener);
        mItemExpertSettings.setOnFocusChangeListener(onFocusChangeListener);
    }

    /**
     * change the UI when buttomItem dosen't has focused
     * 
     * @param view
     * @param titleTxt
     */
    private void buttomItemUnfocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(android.graphics.Color.WHITE);
        view.findViewById(R.id.button_context).setBackgroundResource(R.drawable.bar_bg_btn_grey);
    }

    /**
     * change the UI when buttomItem has focused
     * 
     * @param view
     * @param titleTxt
     */
    private void buttomItemFocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(mR.getColor(R.color.cyan));
        view.findViewById(R.id.button_context).setBackgroundResource(R.drawable.bar_bg_btn_cyan);
    }

}
