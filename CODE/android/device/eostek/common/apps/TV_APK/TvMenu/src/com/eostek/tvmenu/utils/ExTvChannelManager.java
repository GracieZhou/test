//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2013 MStar Semiconductor, Inc. All rights reserved.
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

package com.eostek.tvmenu.utils;

import android.util.Log;

import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvCountry;
import com.mstar.android.tvapi.atv.AtvManager;
import com.mstar.android.tvapi.atv.vo.EnumAtvManualTuneMode;
import com.mstar.android.tvapi.atv.vo.EnumGetProgramCtrl;
import com.mstar.android.tvapi.atv.vo.EnumGetProgramInfo;
import com.mstar.android.tvapi.atv.vo.EnumSetProgramInfo;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.AtvSystemStandard.EnumAtvSystemStandard;
import com.mstar.android.tvapi.common.vo.EnumAvdVideoStandardType;
import com.mstar.android.tvapi.common.vo.EnumProgramAttribute;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumCountry;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.TvTypeInfo;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.dvb.dvbc.vo.EnumCabConstelType;
import com.mstar.android.tvapi.dtv.dvb.vo.DvbMuxInfo;

public class ExTvChannelManager {

    private static ExTvChannelManager mexTvChannelManager;

    private short route = 0;

    // / Define first service type
    public final static int max_atv_count = 255;

    public final static int max_dtv_count = 1000;

    public enum TV_TS_STATUS // TS = TUNINGSERVICE
    {
        E_TS_NONE,
        // channel tuning is in atv manual tuning
        E_TS_ATV_MANU_TUNING_LEFT,
        // channel tuning is in atv manual tuning
        E_TS_ATV_MANU_TUNING_RIGHT,
        // channel tuning is in atv auto tuning
        E_TS_ATV_AUTO_TUNING,
        // channel tuning is in atv scan pausing
        E_TS_ATV_SCAN_PAUSING,
        // channel tuning is in dtv scan manual pausing
        E_TS_DTV_MANU_TUNING,
        // channel tuning is in dtv scan auto tuning
        E_TS_DTV_AUTO_TUNING,
        // channel tuning is in dtv scan full tuning
        E_TS_DTV_FULL_TUNING,
        // channel tuning is in dtv scan pausing
        E_TS_DTV_SCAN_PAUSING,
    };

    private TV_TS_STATUS tv_tuning_status = TV_TS_STATUS.E_TS_NONE;

    public static ExTvChannelManager getInstance() {
        if (mexTvChannelManager == null) {
            mexTvChannelManager = new ExTvChannelManager();
        }
        return mexTvChannelManager;
    }

    public static enum EN_ANTENNA_TYPE {
        E_ROUTE_DTMB, E_ROUTE_DVBC, E_ROUTE_DVBT, E_ROUTE_MAX,
    }

    public ProgramInfo getProgramInfoByIndex(int programIndex) {
        ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
        qc.queryIndex = programIndex;
        ProgramInfo pi = TvChannelManager.getInstance().getProgramInfo(qc,
                EnumProgramInfoType.E_INFO_DATABASE_INDEX);
        return pi;
    }

    public ProgramInfo getCurrProgramInfo() {
        ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
        return TvChannelManager.getInstance()
                .getProgramInfo(qc, EnumProgramInfoType.E_INFO_CURRENT);
    }

    public boolean programSel(int u32Number, int u8ServiceType) {
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getChannelManager()
                        .selectProgram(u32Number, (short) u8ServiceType, 0x00);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dvbcsetScanParam(short u16SymbolRate, EnumCabConstelType enConstellation,
            int u32nitFrequency, int u32EndFrequncy, short u16NetworkID) {
        try {
            DtvManager.getDvbcScanManager().setScanParam(u16SymbolRate, enConstellation,
                    u32nitFrequency, u32EndFrequncy, u16NetworkID, false);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dtvManualScanFreq(int FrequencyKHz) {
        Log.d("TuningService", "dtvManualScanFreq:" + FrequencyKHz);
        try {
            DtvManager.getDvbPlayerManager().setManualTuneByFreq(FrequencyKHz);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

    public EN_ANTENNA_TYPE dtvGetAntennaType() {
        // must read form msrv tv system not from init
        if (TvManager.getInstance() != null) {
            try {
                route = (short) TvManager.getInstance().getCurrentDtvRoute();
            } catch (TvCommonException e) {
                e.printStackTrace();
            }

        }

        return EN_ANTENNA_TYPE.values()[route];
    }

    public boolean saveAtvProgram(int currentProgramNo) {
        try {
            //return AtvManager.getAtvPlayerManager().saveAtvProgram(currentProgramNo - 1);
            return AtvManager.getAtvPlayerManager().saveAtvProgram(currentProgramNo);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * set inputsource to dtv
     */
    private void makeSourceDtv() {
        if (TvCommonManager.getInstance().getCurrentInputSource() != EnumInputSource.E_INPUT_SOURCE_DTV) {
            TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_DTV);
        }
    }

    public int setChannel(int u16channelnum, boolean bcheckblock) {
        int ret = -1;
        try {
            ret = TvManager.getInstance().setChannel(u16channelnum, bcheckblock);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean setAtvProgramInfo(int command, int programNo, int param3) {
        boolean ret = false;
        try {
            ret = AtvManager.getAtvScanManager().setAtvProgramInfo(
                    EnumSetProgramInfo.values()[command], programNo, param3);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void forceVideoStandard(int videoStandardType) {
        try {
            AtvManager.getAtvPlayerManager().forceVideoStandard(
                    EnumAvdVideoStandardType.values()[videoStandardType]);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    public boolean setManualTuningStart(int eEventIntervalMs, int frequency, int eMode) {
        boolean ret = false;
        try {
            ret = AtvManager.getAtvScanManager().setManualTuningStart(eEventIntervalMs, frequency,
                    EnumAtvManualTuneMode.values()[eMode]);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int getProgramControl(int command, int programNo, int param3) {
        int ret = -1;
        try {
            ret = AtvManager.getAtvScanManager().getProgramControl(
                    EnumGetProgramCtrl.values()[command], programNo, param3);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * set inputsource to atv
     */
    private void makeSourceAtv() {
        if (TvCommonManager.getInstance().getCurrentInputSource() != EnumInputSource.E_INPUT_SOURCE_ATV) {
            TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_ATV);
        }
    }

    public void setProgramAttribute(EnumProgramAttribute enpa, int programNo, short programType,
            int programId, boolean bv) {
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getChannelManager()
                        .setProgramAttribute(enpa, programNo, programType, programId, bv);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    public boolean SetColorRanger(short value) {
        boolean colorRange0_255 = false;
        try {
            if (value == 2) {
                if (TvManager.getInstance() != null) {
                    TvManager.getInstance().getPictureManager().autoHDMIColorRange();
                }
            } else {
                if (value != 0) {
                    colorRange0_255 = false;
                } else {
                    colorRange0_255 = true;
                }

                if (TvManager.getInstance() != null) {
                    TvManager.getInstance().getPictureManager().setColorRange(colorRange0_255);
                }
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

    public DvbMuxInfo getCurrentMuxInfo() {
        try {
            return DtvManager.getDvbPlayerManager().getCurrentMuxInfo();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean atvSetForceSoundSystem(EnumAtvSystemStandard eSoundSystem) {
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getAudioManager().setAtvSoundSystem(eSoundSystem);
            }

        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

    public EnumAtvSystemStandard atvGetSoundSystem() {
        int soundindx = 0;
        int index = 0;
        int curNum = getCurrentChannelNumber();
        try {
            // soundindx =
            // TvManager.getInstance().getAudioManager().getAtvSoundSystem().ordinal();
            soundindx = AtvManager.getAtvScanManager().getAtvProgramInfo(
                    EnumGetProgramInfo.E_GET_AUDIO_STANDARD, curNum);
            index = EnumAtvSystemStandard.getOrdinalThroughValue(soundindx);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return EnumAtvSystemStandard.values()[(index == -1 ? 0 : index)];
    }

    public int getCurrentChannelNumber() {
        int res = 0;
        try {
            if (TvManager.getInstance() != null) {
                res = TvManager.getInstance().getChannelManager().getCurrChannelNumber();
            }
            if (TvCommonManager.getInstance().getCurrentInputSource() == EnumInputSource.E_INPUT_SOURCE_ATV) {
                if (res > max_atv_count || res < 0) {
                    Log.d("Mapp", "getatvCurrentChannelNumber error:" + res);
                    res = max_atv_count;
                }
            } else {
                if (res > max_dtv_count || res < 0) {
                    Log.d("Mapp", "getdtvCurrentChannelNumber error:" + res);
                    res = max_dtv_count;
                }
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return res;
    }

    public enum EN_SCAN_RET_STATUS {
        // None
        STATUS_SCAN_NONE,
        // auto tuning process
        STATUS_AUTOTUNING_PROGRESS,
        // signal quality
        STATUS_SIGNAL_QUALITY,
        // get programes
        STATUS_GET_PROGRAMS,
        // set region
        STATUS_SET_REGION,
        // favorite region
        STATUS_SET_FAVORITE_REGION,
        // exit to OAD download
        STATUS_EXIT_TO_DL,
        // LCN conflict
        STATUS_LCN_CONFLICT,
        // end of scan
        STATUS_SCAN_END,
        // Scan end and rearrange done to set first prog. done
        STATUS_SCAN_SETFIRSTPROG_DONE
    }

    public void setSystemCountry() {
        TvChannelManager.getInstance().setSystemCountryId(TvCountry.TAIWAN);
    }
    
    public boolean startManualScan() {
        try {//startAutoUpdateScan();
            DtvManager.getDtvScanManager().startManualScan();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public int judgeAntennaCategory() {
        TvChannelManager mTvChannelManager = TvChannelManager.getInstance();
        TvTypeInfo tvinfo = TvCommonManager.getInstance().getTvInfo();
        int currentRouteIndex = mTvChannelManager.getCurrentDtvRouteIndex();
        int mCurrentRoute = tvinfo.routePath[currentRouteIndex];
        return mCurrentRoute;
    }
}
