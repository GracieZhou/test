//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
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

package com.mstar.tv.menu.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.atv.vo.EnumAtvManualTuneMode;
import com.mstar.android.tvapi.atv.vo.EnumGetProgramCtrl;
import com.mstar.android.tvapi.atv.vo.EnumGetProgramInfo;
import com.mstar.android.tvapi.atv.vo.EnumSetProgramInfo;
import com.mstar.android.tvapi.common.vo.AtvSystemStandard.EnumAtvSystemStandard;
import com.mstar.android.tvapi.common.vo.EnumAvdVideoStandardType;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceInputType;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceType;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.tv.ExTvChannelManager;
import com.mstar.tv.FocusView;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.ui.EosCustomSettingActivity;

@SuppressLint("HandlerLeak")
public class AtvManualTuningDialog extends Dialog {

    private int colorsystemindex = 0;

    private int soundsystemindex = 0;

    private Handler mhandle;

    private int currentCH;

    private Runnable runAtvtuning;

    private final int WAIT_EXPIRE_TIME = 1000;

    private int inputChannelNumber = -1;

    private EditText mChannelNumTxt;

    private TextView mColorSystemTxt;

    private TextView mSoundSystemTxt;

    private TextView mFreqencyTxt;

    private LinearLayout mChannelNumLin;

    private LinearLayout mSearchLin;

    private FocusView focusView;

    private int minCH;

    private int maxCH;

    private TvCommonManager tvCommonMgr = null;

    private TvChannelManager tvChannelMgr = null;

    private static String[] atvcolorsystem = {
            "PAL", "NTSC_M", "SECAM", "NTSC_44", "PAL_M", "PAL_N", "PAL_60", "NO_STAND", "AUTO"
    };

    private static String[] atvsoundsystem = {
            "BG", "DK", "I", "L", "M", "BTSC"
    };

    private final static class AvdType {
        // color system
        private static final int PAL = EnumAvdVideoStandardType.PAL_BGHI.ordinal();

        private static final int NTSC_M = EnumAvdVideoStandardType.NTSC_M.ordinal();

        private static final int SECAM = EnumAvdVideoStandardType.SECAM.ordinal();

        private static final int NTSC_44 = EnumAvdVideoStandardType.NTSC_44.ordinal();

        private static final int PAL_M = EnumAvdVideoStandardType.PAL_M.ordinal();

        private static final int PAL_N = EnumAvdVideoStandardType.PAL_N.ordinal();

        private static final int PAL_60 = EnumAvdVideoStandardType.PAL_60.ordinal();

        private static final int NO_STAND = EnumAvdVideoStandardType.NOTSTANDARD.ordinal();

        private static final int AUTO = EnumAvdVideoStandardType.AUTO.ordinal();

        private static final int[] colortypes = {
                PAL, NTSC_M, SECAM, NTSC_44, PAL_M, PAL_N, PAL_60, NO_STAND, AUTO
        };

        protected static int get(int index) {
            if (index < 0 || index >= colortypes.length)
                return colortypes[0];
            return colortypes[index];
        }

    }

    private int curChannelNumber = 0;

    private Activity mContext;

    protected AtvManualTuningDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(EosCustomSettingActivity.DELAYFINISH);
        setContentView(R.layout.atvmanualtuning);
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(EosCustomSettingActivity.DELAYFINISH);
        ExTvChannelManager.getInstance().setSystemCountry();
        findViews();
        setListener();
        mhandle = new Handler();
        tvCommonMgr = TvCommonManager.getInstance();
        tvChannelMgr = TvChannelManager.getInstance();
        tvCommonMgr.setInputSource(EnumInputSource.E_INPUT_SOURCE_ATV);
        tvChannelMgr
                .changeToFirstService(EnumFirstServiceInputType.E_FIRST_SERVICE_ATV, EnumFirstServiceType.E_DEFAULT);

        runAtvtuning = new Runnable() {

            @Override
            public void run() {
                inputChannelNumber = -1;
                if (currentCH >= minCH && currentCH <= maxCH) {
                    ExTvChannelManager.getInstance().setChannel(currentCH - 1, false);
                    updateAtvManualtuningComponents();
                } else {
                    Toast.makeText(mContext, "CH is illegality!!", Toast.LENGTH_SHORT).show();
                    currentCH = tvChannelMgr.getCurrentChannelNumber();
                    updateAtvManualtuningComponents();
                }
            }
        };
        minCH = tvChannelMgr.getProgramCtrl(EnumGetProgramCtrl.E_GET_CHANNEL_MIN, 0, 0);
        maxCH = tvChannelMgr.getProgramCtrl(EnumGetProgramCtrl.E_GET_CHANNEL_MAX, 0, 0);
        updateAtvManualtuningComponents();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(EosCustomSettingActivity.DELAYFINISH);
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
        return super.dispatchKeyEvent(event);
    }

    private void findViews() {
        focusView = (FocusView) findViewById(R.id.focus_selector);
        focusView.setTopOffset(-56);
        mChannelNumTxt = (EditText) findViewById(R.id.atv_manualtuning_channelnum_val);
        mColorSystemTxt = (TextView) findViewById(R.id.atv_manualtuning_colorsystem_val);
        mSoundSystemTxt = (TextView) findViewById(R.id.atv_manualtuning_soundsystem_val);
        mFreqencyTxt = (TextView) findViewById(R.id.atv_manualtuning_frequency_val);
        mChannelNumLin = (LinearLayout) findViewById(R.id.atv_manualtuning_channelnum);
        mSearchLin = (LinearLayout) findViewById(R.id.atv_manualtuning_start);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getCurrentFocus() == null) {
            return true;
        }
        int currentid = getCurrentFocus().getId();
        int nCurrentFrequency = tvChannelMgr.getAtvCurrentFrequency();
        curChannelNumber = tvChannelMgr.getCurrentChannelNumber();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                switch (currentid) {
                    case R.id.atv_manualtuning_fre:
                        tvChannelMgr.startAtvManualTuning(3 * 1000, nCurrentFrequency,
                                EnumAtvManualTuneMode.E_MANUAL_TUNE_MODE_FINE_TUNE_UP);
                        updateAtvManualtuningfreq();
                        ExTvChannelManager.getInstance().saveAtvProgram(curChannelNumber);
                        break;
                    case R.id.atv_manualtuning_channelnum:
                        curChannelNumber++;
                        ExTvChannelManager.getInstance().setChannel(curChannelNumber, false);
                        updateAtvManualtuningComponents();
                        break;
                    case R.id.atv_manualtuning_colorsystem:
                        colorsystemindex = (colorsystemindex + 1) % (atvcolorsystem.length);
                        ExTvChannelManager.getInstance().forceVideoStandard(AvdType.get(colorsystemindex));
                        ExTvChannelManager.getInstance().setAtvProgramInfo(
                                EnumSetProgramInfo.E_SET_VIDEO_STANDARD_OF_PROGRAM.ordinal(),
                                tvChannelMgr.getCurrentChannelNumber(), AvdType.get(colorsystemindex));
                        mColorSystemTxt.setText(atvcolorsystem[colorsystemindex]);
                        ExTvChannelManager.getInstance().saveAtvProgram(curChannelNumber);
                        break;
                    case R.id.atv_manualtuning_soundsystem:
                        soundsystemindex = (soundsystemindex + 1) % (atvsoundsystem.length);
                        ExTvChannelManager.getInstance().atvSetForceSoundSystem(
                                EnumAtvSystemStandard.values()[soundsystemindex]);
                        mSoundSystemTxt.setText(atvsoundsystem[soundsystemindex]);
                        ExTvChannelManager.getInstance().saveAtvProgram(curChannelNumber);
                        break;
                    default:
                        break;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                switch (currentid) {
                    case R.id.atv_manualtuning_fre:
                        tvChannelMgr.startAtvManualTuning(3 * 1000, nCurrentFrequency,
                                EnumAtvManualTuneMode.E_MANUAL_TUNE_MODE_FINE_TUNE_DOWN);
                        updateAtvManualtuningfreq();
                        ExTvChannelManager.getInstance().saveAtvProgram(curChannelNumber);
                        break;
                    case R.id.atv_manualtuning_channelnum:
                        curChannelNumber--;
                        if (curChannelNumber + 1 < minCH) {
                            curChannelNumber = maxCH - 1;
                        }
                        ExTvChannelManager.getInstance().setChannel(curChannelNumber, false);
                        updateAtvManualtuningComponents();
                        break;
                    case R.id.atv_manualtuning_colorsystem:
                        colorsystemindex = (colorsystemindex + atvcolorsystem.length - 1) % (atvcolorsystem.length);

                        ExTvChannelManager.getInstance().forceVideoStandard(AvdType.get(colorsystemindex));
                        ExTvChannelManager.getInstance().setAtvProgramInfo(
                                EnumSetProgramInfo.E_SET_VIDEO_STANDARD_OF_PROGRAM.ordinal(),
                                tvChannelMgr.getCurrentChannelNumber(), AvdType.get(colorsystemindex));
                        ExTvChannelManager.getInstance().saveAtvProgram(curChannelNumber);
                        mColorSystemTxt.setText(atvcolorsystem[colorsystemindex]);

                        break;
                    case R.id.atv_manualtuning_soundsystem:
                        soundsystemindex = (soundsystemindex + atvsoundsystem.length - 1) % (atvsoundsystem.length);
                        ExTvChannelManager.getInstance().atvSetForceSoundSystem(
                                EnumAtvSystemStandard.values()[soundsystemindex]);
                        ExTvChannelManager.getInstance().saveAtvProgram(curChannelNumber);
                        mSoundSystemTxt.setText(atvsoundsystem[soundsystemindex]);

                    default:
                        break;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                TvChannelManager.getInstance().stopAtvManualTuning();
                mhandle.removeCallbacks(runAtvtuning);
                dismiss();
                mContext.findViewById(R.id.main).setVisibility(View.VISIBLE);
                ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                        EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
                ((ListView) mContext.findViewById(R.id.context_lst)).requestFocus();
                return true;
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                if (R.id.atv_manualtuning_channelnum == currentid) {
                    // InputChannelNumber is -1,means that this is the first
                    // letter input.
                    if (-1 == inputChannelNumber) {
                        // Just ignore input when first input is 0.
                        if (KeyEvent.KEYCODE_0 == keyCode) {
                            return true;
                        }
                        inputChannelNumber = (keyCode - KeyEvent.KEYCODE_0);
                    }
                    // Or,current inputChannelNumer >= 10,means that has already
                    // two letters, clear the current value and re-input(MAX
                    // Channel Number is less than 100).
                    else if (inputChannelNumber >= 10) {
                        inputChannelNumber = (keyCode - KeyEvent.KEYCODE_0);
                    } else {
                        inputChannelNumber = inputChannelNumber * 10 + (keyCode - KeyEvent.KEYCODE_0);
                    }

                    currentCH = inputChannelNumber;
                    mChannelNumTxt.setText(String.valueOf(currentCH));
                    refreshTimer();
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                if (currentid == R.id.atv_manualtuning_start) {
                    tvChannelMgr.stopAtvManualTuning();
                    tvChannelMgr.startAtvManualTuning(5 * 1000, nCurrentFrequency,
                            EnumAtvManualTuneMode.E_MANUAL_TUNE_MODE_FINE_TUNE_UP);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (currentid == R.id.atv_manualtuning_start) {
                    mChannelNumLin.requestFocus();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (currentid == R.id.atv_manualtuning_channelnum) {
                    mSearchLin.requestFocus();
                    return true;
                }
                break;
            default:
                break;
        }
        if (keyCode < KeyEvent.KEYCODE_0 || keyCode > KeyEvent.KEYCODE_9) {
            inputChannelNumber = -1;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refreshTimer() {
        mhandle.removeCallbacks(runAtvtuning);
        mhandle.postDelayed(runAtvtuning, WAIT_EXPIRE_TIME);
    }

    private void updateAtvManualtuningfreq() {
        String str_val;
        int freqKhz = tvChannelMgr.getAtvCurrentFrequency();
        int minteger = freqKhz / 1000;
        int mfraction = (freqKhz % 1000) / 10; // 0.25M not

        if (mfraction <= 5) {
            mfraction = 0;
        } else if ((mfraction >= 20) && (mfraction <= 30)) {
            mfraction = 25;
        } else if ((mfraction >= 45) && (mfraction <= 55)) {
            mfraction = 50;
        } else if ((mfraction >= 70) && (mfraction <= 80)) {
            mfraction = 75;
        }

        str_val = Integer.toString(minteger) + "." + Integer.toString(mfraction);
        mFreqencyTxt.setText(str_val);
    }

    private void updateAtvManualtuningComponents() {
        String channelNum_val;
        int curChannelNum = tvChannelMgr.getCurrentChannelNumber();
        // 0.250M
        if (curChannelNum > 999 || curChannelNum < 0) {
            curChannelNum = 1;
            ExTvChannelManager.getInstance().setChannel(1, false);
            channelNum_val = curChannelNum + "";
        } else {
            channelNum_val = Integer.toString(curChannelNum + 1);
        }

        colorsystemindex = TvChannelManager.getInstance().getAtvProgramInfo(
                EnumGetProgramInfo.E_GET_VIDEO_STANDARD_OF_PROGRAM.ordinal(), curChannelNum);

        // get video standard
        EnumAvdVideoStandardType vst = EnumAvdVideoStandardType.values()[colorsystemindex];
        switch (vst) {
            case PAL_BGHI:
            case PAL_M:
            case PAL_N:
            case PAL_60:
            case SECAM:
                colorsystemindex = 1;
                break;
            case NTSC_44:
            case NTSC_M:
                colorsystemindex = 0;
                break;
            default:
                colorsystemindex = 0;
        }
        mChannelNumTxt.setText(channelNum_val);
        soundsystemindex = ExTvChannelManager.getInstance().atvGetSoundSystem().ordinal();
        mColorSystemTxt.setText(atvcolorsystem[colorsystemindex]);
        mSoundSystemTxt.setText(atvsoundsystem[soundsystemindex]);
        updateAtvManualtuningfreq();
    }

    @Override
    public void dismiss() {
        if (mContext != null && !mContext.isFinishing()) {
            super.dismiss();
        }
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(EosCustomSettingActivity.DELAYFINISH);
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
    }

    private void setListener() {
        FocusChangeListener focusChangeListener = new FocusChangeListener();
        mChannelNumLin.setOnFocusChangeListener(focusChangeListener);
        mSearchLin.setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.atv_manualtuning_colorsystem).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.atv_manualtuning_soundsystem).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.atv_manualtuning_fre).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.atv_manualtuning_start).setOnFocusChangeListener(focusChangeListener);
    }

    class FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Rect rect = new Rect();
                v.getGlobalVisibleRect(rect);
                focusView.startAnimation(v);
            }
        }
    }
}
