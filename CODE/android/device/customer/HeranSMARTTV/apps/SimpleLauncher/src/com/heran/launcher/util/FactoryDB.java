package com.heran.launcher.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import com.heran.launcher.util.IFactoryDesk.EN_VD_SIGNALTYPE;
import com.heran.launcher.util.IFactoryDesk.MAPI_VIDEO_ARC_Type;
import com.heran.launcher.util.IFactoryDesk.MAX_DTV_Resolution_Info;
import com.heran.launcher.util.IFactoryDesk.MAX_HDMI_Resolution_Info;
import com.heran.launcher.util.IFactoryDesk.MAX_YPbPr_Resolution_Info;
import com.heran.launcher.util.IFactoryDesk.ST_MAPI_VIDEO_WINDOW_INFO;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

public class FactoryDB {
    private static FactoryDB instance;

    private Context mContext = null;

    private ContentResolver cr = null;

    public static FactoryDB getInstance(Context context) {
        if (instance == null) {
            instance = new FactoryDB(context);
        }
        return instance;
    }

    private FactoryDB(Context context) {
        mContext = context;
    }

    public void openDB() {
    }

    public void closeDB() {
    }

    private ContentResolver getContentResolver() {
        if (cr == null) {
            cr = mContext.getContentResolver();
        }
        return cr;
    }

    public int queryArcMode(int inputSrcType) {
        int value = -1;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType), null, null, null,
                null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enARCType"));

        }
        cursor.close();
        return value;
    }

    // for OverscanAdjust
    public ST_MAPI_VIDEO_WINDOW_INFO[][] queryOverscanAdjusts(int FactoryOverScanType) {
        ST_MAPI_VIDEO_WINDOW_INFO[][] model;
        switch (FactoryOverScanType) {
        // in DB
            case 0:
                Cursor cursor = getContentResolver().query(Uri.parse("content://mstar.tv.factory/dtvoverscansetting"),
                        null, null, null, "ResolutionTypeNum");
                int maxDTV1 = MAX_DTV_Resolution_Info.E_DTV_MAX.ordinal();
                int maxDTV2 = MAPI_VIDEO_ARC_Type.E_AR_MAX.ordinal();
                model = new ST_MAPI_VIDEO_WINDOW_INFO[maxDTV1][maxDTV2];
                for (int i = 0; i < maxDTV1; i++) {
                    for (int j = 0; j < maxDTV2; j++) {
                        ST_MAPI_VIDEO_WINDOW_INFO item = new ST_MAPI_VIDEO_WINDOW_INFO();
                        if (cursor.moveToNext()) {
                            item.u16H_CapStart = cursor.getInt(cursor.getColumnIndex("u16H_CapStart"));
                            item.u16V_CapStart = cursor.getInt(cursor.getColumnIndex("u16V_CapStart"));
                            item.u8HCrop_Left = (short) cursor.getInt(cursor.getColumnIndex("u8HCrop_Left"));
                            item.u8HCrop_Right = (short) cursor.getInt(cursor.getColumnIndex("u8HCrop_Right"));
                            item.u8VCrop_Up = (short) cursor.getInt(cursor.getColumnIndex("u8VCrop_Up"));
                            item.u8VCrop_Down = (short) cursor.getInt(cursor.getColumnIndex("u8VCrop_Down"));
                        }
                        model[i][j] = item;
                    }
                }
                cursor.close();
                break;
            case 1:
                Cursor cursor1 = getContentResolver().query(
                        Uri.parse("content://mstar.tv.factory/hdmioverscansetting"), null, null, null,
                        "ResolutionTypeNum");
                int maxHDMI1 = MAX_HDMI_Resolution_Info.E_HDMI_MAX.ordinal();
                int maxHDMI2 = MAPI_VIDEO_ARC_Type.E_AR_MAX.ordinal();
                model = new ST_MAPI_VIDEO_WINDOW_INFO[maxHDMI1][maxHDMI2];
                for (int i = 0; i < maxHDMI1; i++) {
                    for (int j = 0; j < maxHDMI2; j++) {
                        ST_MAPI_VIDEO_WINDOW_INFO item = new ST_MAPI_VIDEO_WINDOW_INFO();
                        if (cursor1.moveToNext()) {

                            item.u16H_CapStart = cursor1.getInt(cursor1.getColumnIndex("u16H_CapStart"));
                            item.u16V_CapStart = cursor1.getInt(cursor1.getColumnIndex("u16V_CapStart"));
                            item.u8HCrop_Left = (short) cursor1.getInt(cursor1.getColumnIndex("u8HCrop_Left"));
                            item.u8HCrop_Right = (short) cursor1.getInt(cursor1.getColumnIndex("u8HCrop_Right"));
                            item.u8VCrop_Up = (short) cursor1.getInt(cursor1.getColumnIndex("u8VCrop_Up"));
                            item.u8VCrop_Down = (short) cursor1.getInt(cursor1.getColumnIndex("u8VCrop_Down"));

                        }
                        model[i][j] = item;
                    }
                }
                cursor1.close();
                break;
            case 2:
                Cursor cursor2 = getContentResolver().query(
                        Uri.parse("content://mstar.tv.factory/ypbproverscansetting"), null, null, null,
                        "ResolutionTypeNum");
                int maxYPbPr1 = MAX_YPbPr_Resolution_Info.E_YPbPr_MAX.ordinal();
                int maxYPbPr2 = MAPI_VIDEO_ARC_Type.E_AR_MAX.ordinal();
                model = new ST_MAPI_VIDEO_WINDOW_INFO[maxYPbPr1][maxYPbPr2];
                for (int i = 0; i < maxYPbPr1; i++) {
                    for (int j = 0; j < maxYPbPr2; j++) {
                        ST_MAPI_VIDEO_WINDOW_INFO item = new ST_MAPI_VIDEO_WINDOW_INFO();
                        if (cursor2.moveToNext()) {

                            item.u16H_CapStart = cursor2.getInt(cursor2.getColumnIndex("u16H_CapStart"));
                            item.u16V_CapStart = cursor2.getInt(cursor2.getColumnIndex("u16V_CapStart"));
                            item.u8HCrop_Left = (short) cursor2.getInt(cursor2.getColumnIndex("u8HCrop_Left"));
                            item.u8HCrop_Right = (short) cursor2.getInt(cursor2.getColumnIndex("u8HCrop_Right"));
                            item.u8VCrop_Up = (short) cursor2.getInt(cursor2.getColumnIndex("u8VCrop_Up"));
                            item.u8VCrop_Down = (short) cursor2.getInt(cursor2.getColumnIndex("u8VCrop_Down"));

                        }
                        model[i][j] = item;
                    }
                }
                cursor2.close();
                break;
            case 3:
                Cursor cursor3 = getContentResolver().query(Uri.parse("content://mstar.tv.factory/overscanadjust"),
                        null, null, null, "FactoryOverScanType");
                int maxVD1 = EN_VD_SIGNALTYPE.SIG_NUMS.ordinal();
                int maxVD2 = MAPI_VIDEO_ARC_Type.E_AR_MAX.ordinal();
                model = new ST_MAPI_VIDEO_WINDOW_INFO[maxVD1][maxVD2];
                for (int i = 0; i < maxVD1; i++) {
                    for (int j = 0; j < maxVD2; j++) {
                        ST_MAPI_VIDEO_WINDOW_INFO item = new ST_MAPI_VIDEO_WINDOW_INFO();
                        if (cursor3.moveToNext()) {
                            item.u16H_CapStart = cursor3.getInt(cursor3.getColumnIndex("u16H_CapStart"));
                            item.u16V_CapStart = cursor3.getInt(cursor3.getColumnIndex("u16V_CapStart"));
                            item.u8HCrop_Left = (short) cursor3.getInt(cursor3.getColumnIndex("u8HCrop_Left"));
                            item.u8HCrop_Right = (short) cursor3.getInt(cursor3.getColumnIndex("u8HCrop_Right"));
                            item.u8VCrop_Up = (short) cursor3.getInt(cursor3.getColumnIndex("u8VCrop_Up"));
                            item.u8VCrop_Down = (short) cursor3.getInt(cursor3.getColumnIndex("u8VCrop_Down"));
                        }
                        model[i][j] = item;
                    }
                }
                cursor3.close();
                break;
            case 4:
                Cursor cursor4 = getContentResolver().query(Uri.parse("content://mstar.tv.factory/atvoverscansetting"),
                        null, null, null, "ResolutionTypeNum");

                int maxVD3 = EN_VD_SIGNALTYPE.SIG_NUMS.ordinal();
                int maxVD4 = MAPI_VIDEO_ARC_Type.E_AR_MAX.ordinal();
                model = new ST_MAPI_VIDEO_WINDOW_INFO[maxVD3][maxVD4];
                for (int i = 0; i < maxVD3; i++) {
                    for (int j = 0; j < maxVD4; j++) {
                        ST_MAPI_VIDEO_WINDOW_INFO item = new ST_MAPI_VIDEO_WINDOW_INFO();
                        if (cursor4.moveToNext()) {
                            item.u16H_CapStart = cursor4.getInt(cursor4.getColumnIndex("u16H_CapStart"));
                            item.u16V_CapStart = cursor4.getInt(cursor4.getColumnIndex("u16V_CapStart"));
                            item.u8HCrop_Left = (short) cursor4.getInt(cursor4.getColumnIndex("u8HCrop_Left"));
                            item.u8HCrop_Right = (short) cursor4.getInt(cursor4.getColumnIndex("u8HCrop_Right"));
                            item.u8VCrop_Up = (short) cursor4.getInt(cursor4.getColumnIndex("u8VCrop_Up"));
                            item.u8VCrop_Down = (short) cursor4.getInt(cursor4.getColumnIndex("u8VCrop_Down"));
                        }
                        model[i][j] = item;
                    }
                }
                cursor4.close();
                break;
            default:
                return null;
        }
        return model;
    }

    public void updateOverscanAdjust(int FactoryOverScanType, int arcMode, ST_MAPI_VIDEO_WINDOW_INFO[][] model) {
        ContentValues vals = new ContentValues();

        vals.put("u16H_CapStart", model[FactoryOverScanType][arcMode].u16H_CapStart);
        vals.put("u16V_CapStart", model[FactoryOverScanType][arcMode].u16V_CapStart);
        vals.put("u8HCrop_Left", model[FactoryOverScanType][arcMode].u8HCrop_Left);
        vals.put("u8HCrop_Right", model[FactoryOverScanType][arcMode].u8HCrop_Right);
        vals.put("u8VCrop_Up", model[FactoryOverScanType][arcMode].u8VCrop_Up);
        vals.put("u8VCrop_Down", model[FactoryOverScanType][arcMode].u8VCrop_Down);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/overscanadjust/factoryoverscantype/" + FactoryOverScanType
                            + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IFactoryDesk.T_OverscanAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    public void updateYPbPrOverscanAdjust(int FactoryOverScanType, int arcMode, ST_MAPI_VIDEO_WINDOW_INFO[][] model) {
        ContentValues vals = new ContentValues();

        vals.put("u16H_CapStart", model[FactoryOverScanType][arcMode].u16H_CapStart);
        vals.put("u16V_CapStart", model[FactoryOverScanType][arcMode].u16V_CapStart);
        vals.put("u8HCrop_Left", model[FactoryOverScanType][arcMode].u8HCrop_Left);
        vals.put("u8HCrop_Right", model[FactoryOverScanType][arcMode].u8HCrop_Right);
        vals.put("u8VCrop_Up", model[FactoryOverScanType][arcMode].u8VCrop_Up);
        vals.put("u8VCrop_Down", model[FactoryOverScanType][arcMode].u8VCrop_Down);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/ypbproverscansetting/resolutiontypenum/"
                            + FactoryOverScanType + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IFactoryDesk.T_YPbPrOverscanSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    public void updateHDMIOverscanAdjust(int FactoryOverScanType, int arcMode, ST_MAPI_VIDEO_WINDOW_INFO[][] model) {
        ContentValues vals = new ContentValues();

        vals.put("u16H_CapStart", model[FactoryOverScanType][arcMode].u16H_CapStart);
        vals.put("u16V_CapStart", model[FactoryOverScanType][arcMode].u16V_CapStart);
        vals.put("u8HCrop_Left", model[FactoryOverScanType][arcMode].u8HCrop_Left);
        vals.put("u8HCrop_Right", model[FactoryOverScanType][arcMode].u8HCrop_Right);
        vals.put("u8VCrop_Up", model[FactoryOverScanType][arcMode].u8VCrop_Up);
        vals.put("u8VCrop_Down", model[FactoryOverScanType][arcMode].u8VCrop_Down);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/hdmioverscansetting/resolutiontypenum/" + FactoryOverScanType
                            + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IFactoryDesk.T_HDMIOverscanSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    public void updateDTVOverscanAdjust(int FactoryOverScanType, int arcMode, ST_MAPI_VIDEO_WINDOW_INFO[][] model) {
        ContentValues vals = new ContentValues();

        vals.put("u16H_CapStart", model[FactoryOverScanType][arcMode].u16H_CapStart);
        vals.put("u16V_CapStart", model[FactoryOverScanType][arcMode].u16V_CapStart);
        vals.put("u8HCrop_Left", model[FactoryOverScanType][arcMode].u8HCrop_Left);
        vals.put("u8HCrop_Right", model[FactoryOverScanType][arcMode].u8HCrop_Right);
        vals.put("u8VCrop_Up", model[FactoryOverScanType][arcMode].u8VCrop_Up);
        vals.put("u8VCrop_Down", model[FactoryOverScanType][arcMode].u8VCrop_Down);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/dtvoverscansetting/resolutiontypenum/" + FactoryOverScanType
                            + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IFactoryDesk.T_DTVOverscanSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    public void updateATVOverscanAdjust(int FactoryOverScanType, int arcMode, ST_MAPI_VIDEO_WINDOW_INFO[][] model) {
        ContentValues vals = new ContentValues();

        vals.put("u16H_CapStart", model[FactoryOverScanType][arcMode].u16H_CapStart);
        vals.put("u16V_CapStart", model[FactoryOverScanType][arcMode].u16V_CapStart);
        vals.put("u8HCrop_Left", model[FactoryOverScanType][arcMode].u8HCrop_Left);
        vals.put("u8HCrop_Right", model[FactoryOverScanType][arcMode].u8HCrop_Right);
        vals.put("u8VCrop_Up", model[FactoryOverScanType][arcMode].u8VCrop_Up);
        vals.put("u8VCrop_Down", model[FactoryOverScanType][arcMode].u8VCrop_Down);

        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/atvoverscansetting/resolutiontypenum/" + FactoryOverScanType
                            + "/_id/" + arcMode), vals, null, null);

        } catch (SQLException e) {
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IFactoryDesk.T_ATVOverscanSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    public int queryCurInputSrc() {
        int value = 0;
        Cursor cursor = getContentResolver().query(Uri.parse("content://mstar.tv.usersetting/systemsetting"), null,
                null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        cursor.close();
        return value;
    }
}
