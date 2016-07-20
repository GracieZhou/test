
package com.eostek.tvmenu.tune;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.Constants;
import com.eostek.tvmenu.utils.Tools;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvTypeInfo;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

import android.content.res.Resources;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChannelManagerHolder {
    private LinearLayout mItemChannelEidtLl;

    private LinearLayout mItemDtvAutoTuningLl;

    private LinearLayout mItemAtvAutoTuningLl;

    private LinearLayout mItemDtvManualTuningLl;

    private LinearLayout mItemFineTuningLl;

    private LinearLayout mItemAntennaTypeLl;

    private TextView mChannelEditTitleTxt;

    private TextView mDtvAutoTuningTitleTxt;

    private TextView mAtvAutoTuningTitleTxt;

    private TextView mDtvManualTuningTitleTxt;

    private TextView mFineTuningTitleTxt;

    private TextView mAntennaTypeTitleTxt;

    private TextView mAntennaTypeContentTxt;

    private TvTypeInfo tvinfo;

    private int currentRouteIndex;

    private int mCurrentRoute;

    private TvChannelManager mTvChannelManager;

    private String[] mTitleChannelManagerStr;

    private String[] mAntennaTypeStr;

    private EnumInputSource mCurSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    public static final String DIALOGID_AUTOTUNING = "DTV_AUTOTUNING";

    public static final String DIALOGID_MANUALTUNING = "DTV_MANUALTUNING";

    private String platform = "";

    private ChannelManagerFragment mFragment;
    
    Resources mR;

    public ChannelManagerHolder(ChannelManagerFragment f) {
        mFragment = f;
        mR = mFragment.getActivity().getResources();
    }

    /**
     * if the input source is Atv,init AtvView
     * 
     * @param mView
     */
    protected void initAtvView(View mView) {
        mTitleChannelManagerStr = mFragment.getActivity().getResources()
                .getStringArray(R.array.channelmanager_atv_vals);

        //AutoTuning
        mItemAtvAutoTuningLl = (LinearLayout) mView.findViewById(R.id.item_atv_auto_tuning_ll);
        mAtvAutoTuningTitleTxt = (TextView) mItemAtvAutoTuningLl.findViewById(R.id.title_txt);

        //FineTuning
        mItemFineTuningLl = (LinearLayout) mView.findViewById(R.id.item_fine_tuning_ll);
        mFineTuningTitleTxt = (TextView) mItemFineTuningLl.findViewById(R.id.title_txt);

    }

    /**
     * if the input source is Dtv,init AtvView
     * 
     * @param mView
     */
    protected void initDtvView(View mView) {
        mTitleChannelManagerStr = mFragment.getActivity().getResources()
                .getStringArray(R.array.channelmanager_dtv_vals);

        //ChannelEidt
        mItemChannelEidtLl = (LinearLayout) mView.findViewById(R.id.item_channel_eidt_ll);
        mChannelEditTitleTxt = (TextView) mItemChannelEidtLl.findViewById(R.id.title_txt);

        //AutoTuning
        mItemDtvAutoTuningLl = (LinearLayout) mView.findViewById(R.id.item_dtv_auto_tuning_ll);
        mDtvAutoTuningTitleTxt = (TextView) mItemDtvAutoTuningLl.findViewById(R.id.title_txt);

        //ManualTuning
        mItemDtvManualTuningLl = (LinearLayout) mView.findViewById(R.id.item_dtv_manual_tuning_ll);
        mDtvManualTuningTitleTxt = (TextView) mItemDtvManualTuningLl.findViewById(R.id.title_txt);

        //AntennaType
        mItemAntennaTypeLl = (LinearLayout) mView.findViewById(R.id.item_antenna_type_ll);
        mAntennaTypeTitleTxt = (TextView) mItemAntennaTypeLl.findViewById(R.id.title_txt);
        mAntennaTypeContentTxt = (TextView) mItemAntennaTypeLl.findViewById(R.id.value);

        //check current platform
        platform = Tools.get(mFragment.getActivity(), "ro.eostek.tv");
        Log.e("chensen", "platform = " + platform);
        int idArray = 0;
        if ("S".equals(platform)) {
            idArray = R.array.str_arr_cha_antannatype_platform_s;
            mAntennaTypeStr = mFragment.getResources().getStringArray(idArray);
        } else if ("L".equals(platform)) {
            // idArray = R.array.str_arr_cha_antannatype_platform_l;
            // TvChannelManager.getInstance().switchMSrvDtvRouteCmd((short) 0);
            // mAntennaTypeStr = getResources().getStringArray(idArray);
        } else {
            idArray = R.array.str_arr_cha_antannatype_platform_s;
            mAntennaTypeStr = mFragment.getResources().getStringArray(idArray);
        }

        //switch TV Antenna ROUTE
        mTvChannelManager = TvChannelManager.getInstance();
        tvinfo = TvCommonManager.getInstance().getTvInfo();
        currentRouteIndex = mTvChannelManager.getCurrentDtvRouteIndex();
        mCurrentRoute = tvinfo.routePath[currentRouteIndex];
        if (TvChannelManager.TV_ROUTE_DVBT == mCurrentRoute || TvChannelManager.TV_ROUTE_DVBT2 == mCurrentRoute) {
            mAntennaTypeContentTxt.setText(mAntennaTypeStr[Constants.TV_ROUTE_DVBT]);
        } else {
            mAntennaTypeContentTxt.setText(mAntennaTypeStr[Constants.TV_ROUTE_DTMB]);
            mTvChannelManager.switchMSrvDtvRouteCmd(mTvChannelManager
                    .getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DTMB));
        }
    }

    /**
   	 * init the DtvData to each item
   	 */
    protected void initDtvData() {
        mChannelEditTitleTxt.setText(mTitleChannelManagerStr[Constants.CHANNEL_EDIT_TITLE]);
        mDtvAutoTuningTitleTxt.setText(mTitleChannelManagerStr[Constants.DTV_AUTO_TUNING_TITLE]);
        mDtvManualTuningTitleTxt.setText(mTitleChannelManagerStr[Constants.DTV_MANUAL_TUNING_TITLE]);
        mAntennaTypeTitleTxt.setText(mTitleChannelManagerStr[Constants.ANTENNA_TYPE_TITLE]);
    }

    /**
   	 * init the AtvData to each item
   	 */
    protected void initAtvData() {
        mAtvAutoTuningTitleTxt.setText(mTitleChannelManagerStr[Constants.ATV_AUTO_TUNING_TITLE]);
        mFineTuningTitleTxt.setText(mTitleChannelManagerStr[Constants.FINE_TUNING_TITLE]);
    }

    /**
	 * set OnKeyListener and OnFocusListener for Dtv
	 */
    protected void setDtvListener() {

        OnKeyListener OnKeyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                switch (view.getId()) {

                    case R.id.item_channel_eidt_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    return false;
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    mFragment.mLogic.showChannelEditDialog();
                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_dtv_auto_tuning_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    return false;
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    if (mFragment.mLogic.getLockChannelCount() > 0) {
                                        Toast.makeText(mFragment.getActivity(), R.string.channellocktip,
                                                Toast.LENGTH_LONG).show();
                                        mFragment.mLogic.showPasswordCheckDialog(mFragment.DIALOGID_AUTOTUNING);
                                    } else {
                                        mFragment.mLogic.startDTVAutoTunning();
                                    }
                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_dtv_manual_tuning_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    return false;
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    if (mFragment.mLogic.getLockChannelCount() > 0) {
                                        Toast.makeText(mFragment.getActivity(), R.string.channellocktip,
                                                Toast.LENGTH_LONG).show();
                                        mFragment.mLogic.showPasswordCheckDialog(mFragment.DIALOGID_MANUALTUNING);
                                    } else {
                                        currentRouteIndex = mTvChannelManager.getCurrentDtvRouteIndex();
                                        mCurrentRoute = tvinfo.routePath[currentRouteIndex];
                                        if (mCurrentRoute == TvChannelManager.TV_ROUTE_DTMB) {
                                            Toast.makeText(mFragment.getActivity(), R.string.toast_no_manual_tuning, Toast.LENGTH_LONG).show();
                                        } else {
                                            mFragment.mLogic.showDtvManulTuningDialog();
                                        }
                                    }
                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_antenna_type_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    return false;
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
//                                    currentRouteIndex = mTvChannelManager.getCurrentDtvRouteIndex();
//                                    mCurrentRoute = tvinfo.routePath[currentRouteIndex];
//                                    if (TvChannelManager.TV_ROUTE_DVBT == mCurrentRoute
//                                            || TvChannelManager.TV_ROUTE_DVBT2 == mCurrentRoute) {
//                                        mCurrentRoute = TvChannelManager.TV_ROUTE_DTMB;
//                                        mAntennaTypeContentTxt.setText(mAntennaTypeStr[Constants.TV_ROUTE_DTMB]);
//                                        mTvChannelManager.switchMSrvDtvRouteCmd(mTvChannelManager
//                                                .getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DTMB));
//                                    } else {
//                                        mCurrentRoute = TvChannelManager.TV_ROUTE_DVBT;
//                                        mAntennaTypeContentTxt.setText(mAntennaTypeStr[Constants.TV_ROUTE_DVBT]);
//                                        mTvChannelManager.switchMSrvDtvRouteCmd(mTvChannelManager
//                                                .getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DVBT));
//                                    }

                                }
                                    break;

                            }
                        }
                    }
                        break;

                }
                return false;
            }
        };

      //set Items OnKeyListener
        mItemChannelEidtLl.setOnKeyListener(OnKeyListener);
        mItemDtvAutoTuningLl.setOnKeyListener(OnKeyListener);
        mItemDtvManualTuningLl.setOnKeyListener(OnKeyListener);
        mItemAntennaTypeLl.setOnKeyListener(OnKeyListener);

        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean haiFocus) {

                switch (view.getId()) {
                    case R.id.item_channel_eidt_ll: {
                        if (haiFocus) {
                        	//if Item has focused,change ui
                            buttomItemFocused(view, mChannelEditTitleTxt);
                        } else {
                        	//if Item does not have focused,change ui
                            buttomItemUnfocused(view, mChannelEditTitleTxt);
                        }
                    }
                        break;

                    case R.id.item_dtv_auto_tuning_ll: {
                        if (haiFocus) {
                            buttomItemFocused(view, mDtvAutoTuningTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mDtvAutoTuningTitleTxt);
                        }
                    }
                        break;

                    case R.id.item_dtv_manual_tuning_ll: {
                        if (haiFocus) {
                            buttomItemFocused(view, mDtvManualTuningTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mDtvManualTuningTitleTxt);
                        }
                    }
                        break;

                    case R.id.item_antenna_type_ll: {
                        if (haiFocus) {
                            enumItemFocused(view, mAntennaTypeTitleTxt);
                        } else {
                            enumItemUnfocused(view, mAntennaTypeTitleTxt);
                        }
                    }
                        break;

                }
            }
        };

      //set Items OnFocusChangeListener
        mItemChannelEidtLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemDtvAutoTuningLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemDtvManualTuningLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemAntennaTypeLl.setOnFocusChangeListener(onFocusChangeListener);
    }

    /**
	 * set OnKeyListener and OnFocusListener for Atv
	 */
    protected void setAtvListener() {

        OnKeyListener OnKeyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                switch (view.getId()) {

                    case R.id.item_atv_auto_tuning_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    return false;
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    mFragment.mLogic.startATVAutoTunning();
                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_fine_tuning_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    return false;
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    mFragment.mLogic.showFineTuningDialog();
                                }
                                    break;
                            }
                        }
                    }
                        break;

                }
                return false;
            }
        };

      //set Items OnKeyListener
        mItemAtvAutoTuningLl.setOnKeyListener(OnKeyListener);
        mItemFineTuningLl.setOnKeyListener(OnKeyListener);

        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean haiFocus) {

                switch (view.getId()) {
                    case R.id.item_atv_auto_tuning_ll: {
                        if (haiFocus) {
                            buttomItemFocused(view, mAtvAutoTuningTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mAtvAutoTuningTitleTxt);
                        }
                    }
                        break;

                    case R.id.item_fine_tuning_ll: {
                        if (haiFocus) {
                            buttomItemFocused(view, mFineTuningTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mFineTuningTitleTxt);
                        }
                    }
                        break;

                }
            }
        };

        
      //set Items OnFocusChangeListener
        mItemAtvAutoTuningLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemFineTuningLl.setOnFocusChangeListener(onFocusChangeListener);
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

    /**
     * change the UI when EnumItem dosen't have focused
     * 
     * @param view
     * @param titleTxt
     */
    private void enumItemUnfocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(android.graphics.Color.WHITE);
        view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
    }

    /**
     * change the UI when EnumItem has focused
     * 
     * @param view
     * @param titleTxt
     */
    private void enumItemFocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(mR.getColor(R.color.cyan));
        view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
    }

}
