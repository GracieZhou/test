//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package mstar.tvsetting.factory.ui.factorymenu;

import mstar.factorymenu.ui.R;
import mstar.tvsetting.factory.desk.FactoryDB;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;
import mstar.tvsetting.factory.ui.designmenu.OtherOptionAdjustViewHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.mstar.android.tvapi.dtv.vo.DtvEventScan;
import com.mstar.android.tvapi.atv.vo.AtvEventScan;
import com.mstar.android.tvapi.common.vo.HbbtvEventInfo;
import com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener;
import com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvChannelManager;

public class FactoryMenuActivity extends Activity {
    private static final String TAG = "FactoryMenuActivity";

    private ViewFlipper factoryViewFlipper;

    private FactoryMenuViewHolder holder;

    private int mstarCurrentPage = Mstar_MAIN_PAGE;

    private final static int Mstar_MAIN_PAGE = 0;

    private final static int Mstar_ADC_PAGE = 1;

    private final static int Mstar_WB_PAGE = 2;

    private final static int Mstar_OVERSCAN_PAGE = 3;

    private final static int Mstar_OTHER_PAGE = 4;

    private final static int Mstar_INFO_PAGE = 5;

    private final int SIGNAL_LOCK = 1;

    private final int SIGNAL_UNLOCK = 0;

    private OnDtvPlayerEventListener mDtvPlayerEventListener = null;

    private OnAtvPlayerEventListener mAtvPlayerEventListener = null;

    private OnTvPlayerEventListener mTvPlayerEventListener = null;

    private IFactoryDesk factoryDesk;

    private boolean isFirst = true;

    private ADCAdjustViewHolder adcViewHolder;

    private WBAdjustViewHolder wbViewHolder;

    private OverScanAdjustViewHolder overScanViewHolder;

    private OtherOptionForFactory otherOptionViewHolder;

    private InfoViewHolder infoHolder;

    private class DtvPlayerEventListener implements OnDtvPlayerEventListener {
        @Override
        public boolean onDtvChannelNameReady(int what) {
            return false;
        }

        @Override
        public boolean onDtvAutoTuningScanInfo(int what, DtvEventScan extra) {
            return false;
        }

        @Override
        public boolean onDtvProgramInfoReady(int what) {
            return false;
        }

        @Override
        public boolean onCiLoadCredentialFail(int what) {
            return false;
        }

        @Override
        public boolean onEpgTimerSimulcast(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onHbbtvStatusMode(int what, boolean arg1) {
            return false;
        }

        @Override
        public boolean onMheg5StatusMode(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onMheg5ReturnKey(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onOadHandler(int what, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onOadDownload(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onDtvAutoUpdateScan(int what) {
            return false;
        }

        @Override
        public boolean onTsChange(int what) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogLossSignal(int what) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogNewMultiplex(int what) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogFrequencyChange(int what) {
            return false;
        }

        @Override
        public boolean onRctPresence(int what) {
            return false;
        }

        @Override
        public boolean onChangeTtxStatus(int what, boolean arg1) {
            return false;
        }

        @Override
        public boolean onDtvPriComponentMissing(int what) {
            return false;
        }

        @Override
        public boolean onAudioModeChange(int what, boolean arg1) {
            return false;
        }

        @Override
        public boolean onMheg5EventHandler(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onOadTimeout(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onGingaStatusMode(int what, boolean arg1) {
            return false;
        }

        @Override
        public boolean onSignalLock(int what) {
            mSignalLockHandler.sendEmptyMessage(SIGNAL_LOCK);
            return true;
        }

        @Override
        public boolean onSignalUnLock(int what) {
            mSignalLockHandler.sendEmptyMessage(SIGNAL_UNLOCK);
            return true;
        }

        @Override
        public boolean onUiOPRefreshQuery(int what) {
            return false;
        }

        @Override
        public boolean onUiOPServiceList(int what) {
            return false;
        }

        @Override
        public boolean onUiOPExitServiceList(int what) {
            return false;
        }
    }

    private class AtvPlayerEventListener implements OnAtvPlayerEventListener {

        @Override
        public boolean onAtvAutoTuningScanInfo(int what, AtvEventScan extra) {
            return false;
        }

        @Override
        public boolean onAtvManualTuningScanInfo(int what, AtvEventScan extra) {
            return false;
        }

        @Override
        public boolean onSignalLock(int what) {
            mSignalLockHandler.sendEmptyMessage(SIGNAL_LOCK);
            return true;
        }

        @Override
        public boolean onSignalUnLock(int what) {
            mSignalLockHandler.sendEmptyMessage(SIGNAL_UNLOCK);
            return true;
        }

        @Override
        public boolean onAtvProgramInfoReady(int what) {
            return false;
        }
    }

    private class TvPlayerEventListener implements OnTvPlayerEventListener {

        @Override
        public boolean onScreenSaverMode(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onHbbtvUiEvent(int what, HbbtvEventInfo eventInfo) {
            return false;
        }

        @Override
        public boolean onPopupDialog(int what, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onPvrNotifyPlaybackTime(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyPlaybackSpeedChange(int what) {
            return false;
        }

        @Override
        public boolean onPvrNotifyRecordTime(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyRecordSize(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyRecordStop(int what) {
            return false;
        }

        @Override
        public boolean onPvrNotifyPlaybackStop(int what) {
            return false;
        }

        @Override
        public boolean onPvrNotifyPlaybackBegin(int what) {
            return false;
        }

        @Override
        public boolean onPvrNotifyTimeShiftOverwritesBefore(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyTimeShiftOverwritesAfter(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyOverRun(int what) {
            return false;
        }

        @Override
        public boolean onPvrNotifyUsbRemoved(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyCiPlusProtection(int what) {
            return false;
        }

        @Override
        public boolean onPvrNotifyParentalControl(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyAlwaysTimeShiftProgramReady(int what) {
            return false;
        }

        @Override
        public boolean onPvrNotifyAlwaysTimeShiftProgramNotReady(int what) {
            return false;
        }

        @Override
        public boolean onPvrNotifyCiPlusRetentionLimitUpdate(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onTvProgramInfoReady(int what) {
            return false;
        }

        @Override
        public boolean onSignalLock(int what) {
            mSignalLockHandler.sendEmptyMessage(SIGNAL_LOCK);
            return true;
        }

        @Override
        public boolean onSignalUnLock(int what) {
            mSignalLockHandler.sendEmptyMessage(SIGNAL_UNLOCK);
            return true;
        }

        @Override
        public boolean onEpgUpdateList(int what, int arg1) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePip(int what, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePop(int what, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableDualView(int what, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableTravelingMode(int what, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onDtvPsipTsUpdate(int what, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onEmerencyAlert(int what, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onDtvChannelInfoUpdate(int what, int info, int arg2) {
            return false;
        }
    }

    private Handler mSignalLockHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (overScanViewHolder != null) {
                if (SIGNAL_LOCK == msg.what) {
                    overScanViewHolder.updateUi();
                } else if (SIGNAL_UNLOCK == msg.what) {
                    overScanViewHolder.updateUi();
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mstarmenu);
        factoryDesk = FactoryDeskImpl.getInstance(this);
        FactoryDB.getInstance(this).openDB();
        factoryDesk.loadEssentialDataFromDB();
        factoryViewFlipper = (ViewFlipper) findViewById(R.id.mstarfactory_view_flipper);
        holder = new FactoryMenuViewHolder(this);
        holder.findView();
        registerListeners();
        isFirst = false;
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action != null && action.equals("mstar.tvsetting.factory.intent.action.FactorymenuActivity.adcAdjust")){
            mstarCurrentPage = Mstar_ADC_PAGE;
            factoryViewFlipper.setDisplayedChild(Mstar_ADC_PAGE);
            adcViewHolder = new ADCAdjustViewHolder(FactoryMenuActivity.this, factoryDesk);
            adcViewHolder.findView();
            adcViewHolder.onCreate();
        }
    }

    @Override
    protected void onResume() {
        mDtvPlayerEventListener = new DtvPlayerEventListener();
        TvChannelManager.getInstance().registerOnDtvPlayerEventListener(mDtvPlayerEventListener);
        mTvPlayerEventListener = new TvPlayerEventListener();
        TvChannelManager.getInstance().registerOnTvPlayerEventListener(mTvPlayerEventListener);
        mAtvPlayerEventListener = new AtvPlayerEventListener();
        TvChannelManager.getInstance().registerOnAtvPlayerEventListener(mAtvPlayerEventListener);

        if (!isFirst) {
            FactoryDB.getInstance(this).openDB();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        TvChannelManager.getInstance().unregisterOnAtvPlayerEventListener(mAtvPlayerEventListener);
        mAtvPlayerEventListener = null;
        TvChannelManager.getInstance().unregisterOnDtvPlayerEventListener(mDtvPlayerEventListener);
        mDtvPlayerEventListener = null;
        TvChannelManager.getInstance().unregisterOnTvPlayerEventListener(mTvPlayerEventListener);
        mTvPlayerEventListener = null;

        if (factoryDesk != null) {
            FactoryDB.getInstance(this).closeDB();
        }
        Intent intent = new Intent("factoryDirty");
        sendBroadcast(intent);
        super.onPause();
    }

    void registerListeners() {
        holder.adcAdjustLinerLayout.setOnClickListener(listener);
        holder.wbLinerLayout.setOnClickListener(listener);
        holder.overScanLinerLayout.setOnClickListener(listener);
        holder.otherLinerLayout.setOnClickListener(listener);
        holder.infoLinerLayout.setOnClickListener(listener);
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.linearlayout_factory_adc:
                    mstarCurrentPage = Mstar_ADC_PAGE;
                    factoryViewFlipper.setDisplayedChild(Mstar_ADC_PAGE);
                    adcViewHolder = new ADCAdjustViewHolder(FactoryMenuActivity.this, factoryDesk);
                    adcViewHolder.findView();
                    adcViewHolder.onCreate();
                    break;
                case R.id.linearlayout_factory_whitebalance:
                    mstarCurrentPage = Mstar_WB_PAGE;
                    factoryViewFlipper.setDisplayedChild(Mstar_WB_PAGE);
                    wbViewHolder = new WBAdjustViewHolder(FactoryMenuActivity.this, factoryDesk);
                    wbViewHolder.findView();
                    wbViewHolder.onCreate();
                    break;
                case R.id.linearlayout_factory_overscan:
                    mstarCurrentPage = Mstar_OVERSCAN_PAGE;
                    factoryViewFlipper.setDisplayedChild(Mstar_OVERSCAN_PAGE);
                    overScanViewHolder = new OverScanAdjustViewHolder(FactoryMenuActivity.this,
                            factoryDesk);
                    overScanViewHolder.findView();
                    overScanViewHolder.onCreate();
                    break;
                case R.id.linearlayout_factory_otheroption:
                    mstarCurrentPage = Mstar_OTHER_PAGE;
                    factoryViewFlipper.setDisplayedChild(Mstar_OTHER_PAGE);
                    otherOptionViewHolder = new OtherOptionForFactory(FactoryMenuActivity.this);
                    Log.d(TAG, "otherOptionViewHolder..." + otherOptionViewHolder);
                    otherOptionViewHolder.onCreate();
                    otherOptionViewHolder.findView();
                    break;
                case R.id.linearlayout_factory_info:
                    mstarCurrentPage = Mstar_INFO_PAGE;
                    factoryViewFlipper.setDisplayedChild(Mstar_INFO_PAGE);
                    infoHolder = new InfoViewHolder(FactoryMenuActivity.this);
                    infoHolder.findView();
                    infoHolder.onCreate();
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean bRet = false;
        Log.d(TAG, "mstarCurrentPage..." + mstarCurrentPage);
        switch (mstarCurrentPage) {
            case Mstar_ADC_PAGE:
                bRet = adcViewHolder.onKeyDown(keyCode, event);
                break;
            case Mstar_WB_PAGE:
                bRet = wbViewHolder.onKeyDown(keyCode, event);
                break;
            case Mstar_OVERSCAN_PAGE:
                bRet = overScanViewHolder.onKeyDown(keyCode, event);
                break;
            case Mstar_OTHER_PAGE:
                bRet = otherOptionViewHolder.onKeyDown(keyCode, event);
                break;
            case Mstar_INFO_PAGE:
                bRet = infoHolder.onKeyDown(keyCode, event);
                break;
            default:
                break;
        }
        if (bRet == false) {
            bRet = super.onKeyDown(keyCode, event);
        }
        return bRet;
    }

    public void returnRoot(int pageIdx) {
        mstarCurrentPage = Mstar_MAIN_PAGE;
        factoryViewFlipper.setDisplayedChild(Mstar_MAIN_PAGE);
        LinearLayout container = (LinearLayout) findViewById(R.id.linearlayout_mstarfactory_menu);
        LinearLayout focusedLayout = (LinearLayout) container.getChildAt(pageIdx);
        focusedLayout.setFocusable(true);
        focusedLayout.requestFocus();
        focusedLayout.setFocusableInTouchMode(true);
    }
}
