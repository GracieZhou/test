
package com.mstar.tv.menu.setting;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;

/**
 * @projectName： EOSTVMenu
 * @moduleName： PCImageAdjustFragment.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-14
 * @Copyright © 2013 EOSTEK, Inc.
 */
@SuppressLint("ValidFragment")
public class PCImageAdjustFragment extends PublicFragement {
    private EosSettingItem pcModeClockItem = null;

    private EosSettingItem pcModePhaseItem = null;

    private EosSettingItem pcModeHPositionItem = null;

    private EosSettingItem pcModeVPositionItem = null;

    private EosSettingItem pcModeAutoTuneItem = null;

    private String[] pcImage_title;

    private TvPictureManager manager;

    private static final int AUTO_TUNE_START = 1;

    private static final int AUTO_TUNE_SUCCESS = 2;

    private static final int AUTO_TUNE_FAIL = 3;

    private static final int DELAYINITDATA = 4;

    private static final int DELAYINITDATATIME = 600;

    private boolean bAutoTuning = false;

    private ProgressDialog autoTuneProgressDialog;

    @SuppressLint("myHandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == AUTO_TUNE_START) {
                autoTuneProgressDialog = ProgressDialog.show(getActivity(), null, getActivity()
                        .getResources().getString(R.string.picadjust), true);
                new Thread() {
                    public void run() {
                        boolean bAutoTuneSuccess = false;
                        try {
                            if (TvManager.getInstance() != null) {
                                bAutoTuneSuccess = TvManager.getInstance().getPlayerManager()
                                        .startPcModeAtuoTune();
                            }
                        } catch (TvCommonException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (bAutoTuneSuccess) {
                            myHandler.sendEmptyMessage(AUTO_TUNE_SUCCESS);
                        } else {
                            myHandler.sendEmptyMessage(AUTO_TUNE_FAIL);
                        }
                    }
                }.start();
            } else if (msg.what == AUTO_TUNE_SUCCESS) {
                initDate();
                autoTuneProgressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.picadjustsuccess, Toast.LENGTH_SHORT).show();
                bAutoTuning = false;
            } else if (msg.what == AUTO_TUNE_FAIL) {
                initDate();
                autoTuneProgressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.picadjustfail, Toast.LENGTH_SHORT).show();
                bAutoTuning = false;
            } else if (msg.what == DELAYINITDATA) {
                initDate();
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void initItems() {
        setTag("pcimage");
        manager = TvPictureManager.getInstance();
        mItems = new ArrayList<EosSettingItem>();
        pcImage_title = getActivity().getResources().getStringArray(R.array.pc_adjust);
        /* clock */
        pcModeClockItem = new EosSettingItem(this, pcImage_title[0], 0, 100, 0,
                MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(pcModeClockItem);
        /* phase */
        pcModePhaseItem = new EosSettingItem(this, pcImage_title[1], 0, 100, 0,
                MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(pcModePhaseItem);
        /* horizontal position */
        pcModeHPositionItem = new EosSettingItem(this, pcImage_title[2], 0, 100, 0,
                MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(pcModeHPositionItem);
        /* vertical position */
        pcModeVPositionItem = new EosSettingItem(this, pcImage_title[3], 0, 100, 0,
                MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(pcModeVPositionItem);
        pcModeAutoTuneItem = new EosSettingItem(this, pcImage_title[4],
                MenuConstants.ITEMTYPE_BUTTON, true);
        mItems.add(pcModeAutoTuneItem);

        if (TvChannelManager.getInstance().isSignalStabled()) {
            mAdapter.setHasShowValue(true);
            myHandler.sendEmptyMessageDelayed(DELAYINITDATA, DELAYINITDATATIME);
        } else {
            mAdapter.setHasShowValue(true);
        }
    }

    @Override
    void callBack(int resultValue, int position) {
        switch (position) {
            case 0:
                manager.setPCClock(resultValue);
                break;
            case 1:
                manager.setPCPhase(resultValue);
                break;
            case 2:
                manager.setPCHPos(resultValue);
                break;
            case 3:
                manager.setPCVPos(resultValue);
                break;
            default:
                break;
        }
    }

    @Override
    void callBack(Boolean resultValue, int position) {

    }

    @Override
    void callBack(int position) {
        if (position == 4 && bAutoTuning == false) {
            bAutoTuning = true;
            myHandler.sendEmptyMessage(AUTO_TUNE_START);
        }
    }

    @Override
    protected void initDate() {
        int[] values = manager.getPCImage();

        if (values[0] < 0) {
            pcModeClockItem.setCurValue(0);
        } else {
            pcModeClockItem.setCurValue(values[0]);
        }
        if (values[1] < 0) {
            pcModePhaseItem.setCurValue(0);
        } else {
            pcModePhaseItem.setCurValue(values[1]);
        }
        if (values[2] < 0) {
            pcModeHPositionItem.setCurValue(0);
        } else {
            pcModeHPositionItem.setCurValue(values[2]);
        }
        if (values[3] < 0) {
            pcModeVPositionItem.setCurValue(0);
        } else {
            pcModeVPositionItem.setCurValue(values[3]);
        }

        mAdapter.setHasShowValue(true);
    }

    @Override
    boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
        return false;
    }
}
