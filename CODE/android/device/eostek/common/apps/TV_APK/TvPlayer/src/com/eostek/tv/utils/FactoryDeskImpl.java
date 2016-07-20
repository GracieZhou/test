
package com.eostek.tv.utils;

import android.content.Context;

import com.eostek.tv.utils.IFactoryDesk.EN_VD_SIGNALTYPE;
import com.eostek.tv.utils.IFactoryDesk.MAPI_AVD_VideoStandardType;
import com.eostek.tv.utils.IFactoryDesk.ST_MAPI_VIDEO_WINDOW_INFO;
import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.TvPlayer;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.factory.FactoryManager;

@SuppressWarnings("deprecation")
public class FactoryDeskImpl {
    private static FactoryDeskImpl instance;

    private FactoryDB factoryDB;

    private ST_MAPI_VIDEO_WINDOW_INFO[][] factoryDTVOverscanSet;

    private ST_MAPI_VIDEO_WINDOW_INFO[][] factoryHDMIOverscanSet;

    private ST_MAPI_VIDEO_WINDOW_INFO[][] factoryYPbPrOverscanSet;

    private ST_MAPI_VIDEO_WINDOW_INFO[][] factoryVDOverscanSet;

    private ST_MAPI_VIDEO_WINDOW_INFO[][] factoryATVOverscanSet;

    private FactoryManager fm = TvManager.getInstance().getFactoryManager();

    private PictureManager pm = TvManager.getInstance().getPictureManager();

    private TvPlayer mtvplayer = TvManager.getInstance().getPlayerManager();

    private FactoryDeskImpl(Context context) {
        factoryDB = FactoryDB.getInstance(context);
        factoryDTVOverscanSet = factoryDB.queryOverscanAdjusts(0);
        factoryHDMIOverscanSet = factoryDB.queryOverscanAdjusts(1);
        factoryYPbPrOverscanSet = factoryDB.queryOverscanAdjusts(2);
        factoryVDOverscanSet = factoryDB.queryOverscanAdjusts(3);
        factoryATVOverscanSet = factoryDB.queryOverscanAdjusts(4);
    }

    public static FactoryDeskImpl getInstance(Context context) {
        if (instance == null) {
            instance = new FactoryDeskImpl(context);
        }
        return instance;
    }

    private EnumInputSource overSacnSourceType = EnumInputSource.E_INPUT_SOURCE_ATV;

    public boolean setOverScanSourceType() {
        try {
            EnumInputSource sourceType = EnumInputSource.values()[factoryDB.queryCurInputSrc()];
            TvManager.getInstance().setInputSource(sourceType);
            overSacnSourceType = sourceType;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean setOverScan(short vPosition, short vSize, short hSize, short hPosition) {
        int inputSrc = factoryDB.queryCurInputSrc();
        int arcMode = factoryDB.queryArcMode(inputSrc);
        int currentResolution = GetResolutionMapping(overSacnSourceType);
        int videoStandard = 0;
        try {
            videoStandard = mtvplayer.getVideoStandard().ordinal();
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }

        MAPI_AVD_VideoStandardType lastVideoStandardMode = MAPI_AVD_VideoStandardType.values()[videoStandard];
        switch (overSacnSourceType) {
            case E_INPUT_SOURCE_VGA:
            case E_INPUT_SOURCE_CVBS:
            case E_INPUT_SOURCE_CVBS2:
            case E_INPUT_SOURCE_CVBS3:
            case E_INPUT_SOURCE_CVBS4:
            case E_INPUT_SOURCE_CVBS5:
            case E_INPUT_SOURCE_CVBS6:
            case E_INPUT_SOURCE_CVBS7:
            case E_INPUT_SOURCE_CVBS8:
            case E_INPUT_SOURCE_SVIDEO:
            case E_INPUT_SOURCE_SVIDEO2:
            case E_INPUT_SOURCE_SVIDEO3:
            case E_INPUT_SOURCE_SVIDEO4:
            case E_INPUT_SOURCE_SCART:
            case E_INPUT_SOURCE_SCART2: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down = vPosition;
                factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up = vSize;
                factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right = hSize;
                factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left = hPosition;
                factoryDB.updateOverscanAdjust(eVDSinType.ordinal(), arcMode, factoryVDOverscanSet);
                try {
                    pm.setOverscan(
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_ATV: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down = vPosition;
                factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up = vSize;
                factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right = hSize;
                factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left = hPosition;
                factoryDB.updateATVOverscanAdjust(eVDSinType.ordinal(), arcMode,
                        factoryATVOverscanSet);
                try {
                    pm.setOverscan(
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_YPBPR:
            case E_INPUT_SOURCE_YPBPR2:
            case E_INPUT_SOURCE_YPBPR3: {
                factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Down = vPosition;
                factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Up = vSize;
                factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Right = hSize;
                factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Left = hPosition;
                factoryDB.updateYPbPrOverscanAdjust(currentResolution, arcMode,
                        factoryYPbPrOverscanSet);
                try {
                    pm.setOverscan(
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4: {
                factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Down = vPosition;
                factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Up = vSize;
                factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Right = hSize;
                factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Left = hPosition;
                factoryDB.updateHDMIOverscanAdjust(currentResolution, arcMode,
                        factoryHDMIOverscanSet);
                try {
                    pm.setOverscan(factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_DTV: {
                factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Down = vPosition;
                factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Up = vSize;
                factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Right = hSize;
                factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Left = hPosition;
                factoryDB
                        .updateDTVOverscanAdjust(currentResolution, arcMode, factoryDTVOverscanSet);
                try {
                    pm.setOverscan(factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            default:
                break;
        }
        return false;
    }

    public boolean setOverScanHsize(short hSize) {
        int inputSrc = factoryDB.queryCurInputSrc();
        int arcMode = factoryDB.queryArcMode(inputSrc);
        int currentResolution = GetResolutionMapping(overSacnSourceType);
        int videoStandard = 0;
        try {
            videoStandard = mtvplayer.getVideoStandard().ordinal();
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }
        MAPI_AVD_VideoStandardType lastVideoStandardMode = MAPI_AVD_VideoStandardType.values()[videoStandard];
        switch (overSacnSourceType) {
            case E_INPUT_SOURCE_VGA:
            case E_INPUT_SOURCE_CVBS:
            case E_INPUT_SOURCE_CVBS2:
            case E_INPUT_SOURCE_CVBS3:
            case E_INPUT_SOURCE_CVBS4:
            case E_INPUT_SOURCE_CVBS5:
            case E_INPUT_SOURCE_CVBS6:
            case E_INPUT_SOURCE_CVBS7:
            case E_INPUT_SOURCE_CVBS8:
            case E_INPUT_SOURCE_SVIDEO:
            case E_INPUT_SOURCE_SVIDEO2:
            case E_INPUT_SOURCE_SVIDEO3:
            case E_INPUT_SOURCE_SVIDEO4:
            case E_INPUT_SOURCE_SCART:
            case E_INPUT_SOURCE_SCART2: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right = hSize;
                factoryDB.updateOverscanAdjust(eVDSinType.ordinal(), arcMode, factoryVDOverscanSet);
                try {
                    pm.setOverscan(
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case E_INPUT_SOURCE_ATV:

                EN_VD_SIGNALTYPE eVDSinType;
                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right = hSize;
                factoryDB.updateATVOverscanAdjust(eVDSinType.ordinal(), arcMode,
                        factoryATVOverscanSet);
                try {
                    pm.setOverscan(
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                return true;

            case E_INPUT_SOURCE_YPBPR:
            case E_INPUT_SOURCE_YPBPR2:
            case E_INPUT_SOURCE_YPBPR3: {
                factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Right = hSize;
                factoryDB.updateYPbPrOverscanAdjust(currentResolution, arcMode,
                        factoryYPbPrOverscanSet);
                try {
                    pm.setOverscan(
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4: {
                factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Right = hSize;
                factoryDB.updateHDMIOverscanAdjust(currentResolution, arcMode,
                        factoryHDMIOverscanSet);
                try {
                    pm.setOverscan(factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case E_INPUT_SOURCE_DTV: {
                factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Right = hSize;
                factoryDB
                        .updateDTVOverscanAdjust(currentResolution, arcMode, factoryDTVOverscanSet);
                try {
                    pm.setOverscan(factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            default:
                break;
        }
        return false;
    }

    public boolean setOverScanHposition(short hPosition) {
        int inputSrc = factoryDB.queryCurInputSrc();
        int arcMode = factoryDB.queryArcMode(inputSrc);
        int currentResolution = GetResolutionMapping(overSacnSourceType);
        int videoStandard = 0;
        try {
            videoStandard = mtvplayer.getVideoStandard().ordinal();
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }

        MAPI_AVD_VideoStandardType lastVideoStandardMode = MAPI_AVD_VideoStandardType.values()[videoStandard];
        switch (overSacnSourceType) {
            case E_INPUT_SOURCE_VGA:
            case E_INPUT_SOURCE_CVBS:
            case E_INPUT_SOURCE_CVBS2:
            case E_INPUT_SOURCE_CVBS3:
            case E_INPUT_SOURCE_CVBS4:
            case E_INPUT_SOURCE_CVBS5:
            case E_INPUT_SOURCE_CVBS6:
            case E_INPUT_SOURCE_CVBS7:
            case E_INPUT_SOURCE_CVBS8:
            case E_INPUT_SOURCE_SVIDEO:
            case E_INPUT_SOURCE_SVIDEO2:
            case E_INPUT_SOURCE_SVIDEO3:
            case E_INPUT_SOURCE_SVIDEO4:
            case E_INPUT_SOURCE_SCART:
            case E_INPUT_SOURCE_SCART2: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left = hPosition;
                factoryDB.updateOverscanAdjust(eVDSinType.ordinal(), arcMode, factoryVDOverscanSet);
                try {
                    pm.setOverscan(
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case E_INPUT_SOURCE_ATV: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left = hPosition;
                factoryDB.updateATVOverscanAdjust(eVDSinType.ordinal(), arcMode,
                        factoryATVOverscanSet);
                try {
                    pm.setOverscan(
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case E_INPUT_SOURCE_YPBPR:
            case E_INPUT_SOURCE_YPBPR2:
            case E_INPUT_SOURCE_YPBPR3: {
                factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Left = hPosition;
                factoryDB.updateYPbPrOverscanAdjust(currentResolution, arcMode,
                        factoryYPbPrOverscanSet);
                try {
                    pm.setOverscan(
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4: {
                factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Left = hPosition;
                factoryDB.updateHDMIOverscanAdjust(currentResolution, arcMode,
                        factoryHDMIOverscanSet);
                try {
                    pm.setOverscan(factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_DTV: {
                factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Left = hPosition;
                factoryDB
                        .updateDTVOverscanAdjust(currentResolution, arcMode, factoryDTVOverscanSet);
                try {
                    pm.setOverscan(factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            default:
                break;
        }
        return false;
    }

    public boolean setOverScanVsize(short vSize) {
        int inputSrc = factoryDB.queryCurInputSrc();
        int arcMode = factoryDB.queryArcMode(inputSrc);
        int currentResolution = GetResolutionMapping(overSacnSourceType);
        int videoStandard = 0;
        try {
            videoStandard = mtvplayer.getVideoStandard().ordinal();
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }

        MAPI_AVD_VideoStandardType lastVideoStandardMode = MAPI_AVD_VideoStandardType.values()[videoStandard];
        switch (overSacnSourceType) {
            case E_INPUT_SOURCE_VGA:
            case E_INPUT_SOURCE_CVBS:
            case E_INPUT_SOURCE_CVBS2:
            case E_INPUT_SOURCE_CVBS3:
            case E_INPUT_SOURCE_CVBS4:
            case E_INPUT_SOURCE_CVBS5:
            case E_INPUT_SOURCE_CVBS6:
            case E_INPUT_SOURCE_CVBS7:
            case E_INPUT_SOURCE_CVBS8:
            case E_INPUT_SOURCE_SVIDEO:
            case E_INPUT_SOURCE_SVIDEO2:
            case E_INPUT_SOURCE_SVIDEO3:
            case E_INPUT_SOURCE_SVIDEO4:
            case E_INPUT_SOURCE_SCART:
            case E_INPUT_SOURCE_SCART2: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up = vSize;
                factoryDB.updateOverscanAdjust(eVDSinType.ordinal(), arcMode, factoryVDOverscanSet);
                try {
                    pm.setOverscan(
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_ATV: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up = vSize;
                factoryDB.updateATVOverscanAdjust(eVDSinType.ordinal(), arcMode,
                        factoryATVOverscanSet);
                try {
                    pm.setOverscan(
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_YPBPR:
            case E_INPUT_SOURCE_YPBPR2:
            case E_INPUT_SOURCE_YPBPR3: {
                factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Up = vSize;
                factoryDB.updateYPbPrOverscanAdjust(currentResolution, arcMode,
                        factoryYPbPrOverscanSet);
                try {
                    pm.setOverscan(
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4: {
                factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Up = vSize;
                factoryDB.updateHDMIOverscanAdjust(currentResolution, arcMode,
                        factoryHDMIOverscanSet);
                try {
                    pm.setOverscan(factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case E_INPUT_SOURCE_DTV: {
                factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Up = vSize;
                factoryDB
                        .updateDTVOverscanAdjust(currentResolution, arcMode, factoryDTVOverscanSet);
                try {
                    pm.setOverscan(factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            default:
                break;
        }
        return false;
    }

    public boolean setOverScanVposition(short vPosition) {
        int inputSrc = factoryDB.queryCurInputSrc();
        int arcMode = factoryDB.queryArcMode(inputSrc);
        int currentResolution = GetResolutionMapping(overSacnSourceType);
        int videoStandard = 0;
        try {
            videoStandard = mtvplayer.getVideoStandard().ordinal();
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }

        MAPI_AVD_VideoStandardType lastVideoStandardMode = MAPI_AVD_VideoStandardType.values()[videoStandard];
        switch (overSacnSourceType) {
            case E_INPUT_SOURCE_VGA:
            case E_INPUT_SOURCE_CVBS:
            case E_INPUT_SOURCE_CVBS2:
            case E_INPUT_SOURCE_CVBS3:
            case E_INPUT_SOURCE_CVBS4:
            case E_INPUT_SOURCE_CVBS5:
            case E_INPUT_SOURCE_CVBS6:
            case E_INPUT_SOURCE_CVBS7:
            case E_INPUT_SOURCE_CVBS8:
            case E_INPUT_SOURCE_SVIDEO:
            case E_INPUT_SOURCE_SVIDEO2:
            case E_INPUT_SOURCE_SVIDEO3:
            case E_INPUT_SOURCE_SVIDEO4:
            case E_INPUT_SOURCE_SCART:
            case E_INPUT_SOURCE_SCART2: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down = vPosition;
                factoryDB.updateOverscanAdjust(eVDSinType.ordinal(), arcMode, factoryVDOverscanSet);
                try {
                    pm.setOverscan(
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryVDOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_ATV: {
                EN_VD_SIGNALTYPE eVDSinType;

                switch (lastVideoStandardMode) {
                    case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_SECAM:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_SECAM;
                        break;
                    case E_MAPI_VIDEOSTANDARD_NTSC_44:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_M:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_M;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_N:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL_NC;
                        break;
                    case E_MAPI_VIDEOSTANDARD_PAL_60:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_NTSC_443;
                        break;
                    default:
                        eVDSinType = EN_VD_SIGNALTYPE.SIG_PAL;
                        break;
                }
                factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down = vPosition;
                factoryDB.updateATVOverscanAdjust(eVDSinType.ordinal(), arcMode,
                        factoryATVOverscanSet);
                try {
                    pm.setOverscan(
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Down,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8VCrop_Up,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Right,
                            factoryATVOverscanSet[eVDSinType.ordinal()][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_YPBPR:
            case E_INPUT_SOURCE_YPBPR2:
            case E_INPUT_SOURCE_YPBPR3: {
                factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Down = vPosition;
                factoryDB.updateYPbPrOverscanAdjust(currentResolution, arcMode,
                        factoryYPbPrOverscanSet);
                try {
                    pm.setOverscan(
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryYPbPrOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4: {
                factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Down = vPosition;
                factoryDB.updateHDMIOverscanAdjust(currentResolution, arcMode,
                        factoryHDMIOverscanSet);
                try {
                    pm.setOverscan(factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryHDMIOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case E_INPUT_SOURCE_DTV: {
                factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Down = vPosition;
                factoryDB
                        .updateDTVOverscanAdjust(currentResolution, arcMode, factoryDTVOverscanSet);
                try {
                    pm.setOverscan(factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Down,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8VCrop_Up,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Right,
                            factoryDTVOverscanSet[currentResolution][arcMode].u8HCrop_Left);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }

                return true;
            }
            default:
                break;
        }
        return false;
    }

    private int GetResolutionMapping(EnumInputSource enCurrentInputType) {
        int currentResolution = 0;

        try {
            currentResolution = fm.getResolutionMappingIndex(enCurrentInputType);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        return currentResolution;
    }
}
