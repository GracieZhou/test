
package com.eostek.tv.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eostek.tv.advertisement.AdInfo;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

public class TvDBManager {

    private final String TAG = TvDBManager.class.getSimpleName();

    private static TvDBManager mDbManager;

    private SQLiteDatabase mDB = null;

    private TvDBManager(Context context) {
        mDB = new TvDBHelper(context).getWritableDatabase();
    }

    public static TvDBManager getInstance(Context context) {
        if (mDbManager == null) {
            mDbManager = new TvDBManager(context);
        }
        return mDbManager;
    }

    /**
     * insert one entry to table adinfo
     * 
     * @param AdInfo
     * @return
     */
    public synchronized void insertAdInfo(AdInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(TvDBHelper.AD_COLUMN_TITLE, info.getTitle());
        cv.put(TvDBHelper.AD_COLUMN_UPDATEDATE, info.getUpdate_date());
        cv.put(TvDBHelper.AD_COLUMN_UPDATETIME, info.getUpdate_time());
        
        cv.put(TvDBHelper.AD_COLUMN_PICTURE_URL, info.getPic_url());
        cv.put(TvDBHelper.AD_COLUMN_DESCRITPTION, info.getDescription());
        cv.put(TvDBHelper.AD_COLUMN_DISMISS_TIME, info.getDismiss_time());
        cv.put(TvDBHelper.AD_COLUMN_PROGRAMME_ID, info.getProgramme_id());
        cv.put(TvDBHelper.AD_COLUMN_SOURCE, info.getSource());
        
        cv.put(TvDBHelper.AD_COLUMN_WEBVIEW_URL, info.getWebview_url());
        cv.put(TvDBHelper.AD_COLUMN_POSITION_X, info.getPos_x());
        cv.put(TvDBHelper.AD_COLUMN_POSITION_Y, info.getPos_y());
        mDB.insert(TvDBHelper.AD_TABLE_NAME, null, cv);
    }

    /**
     * query a list of AdInfo at certain condition
     * 
     * @param selections
     * @param whereArgs
     * @return
     */
    public synchronized List<AdInfo> getAdInfos(String selections, String[] whereArgs) {
        List<AdInfo> list = new ArrayList<AdInfo>();
        Cursor cursor = mDB.query(TvDBHelper.AD_TABLE_NAME, null, selections, whereArgs, null, null, null);
        if (cursor != null) {
            Log.i(TAG,"cursor count : " + cursor.getCount());
            while (cursor.moveToNext()) {
                AdInfo info = new AdInfo();

                info.setTitle(cursor.getString(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_TITLE)));
                info.setUpdate_date(cursor.getString(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_UPDATEDATE)));
                info.setUpdate_time(cursor.getString(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_UPDATETIME)));
                info.setPic_url(cursor.getString(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_PICTURE_URL)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_DESCRITPTION)));
                info.setDismiss_time(cursor.getInt(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_DISMISS_TIME)));
                info.setProgramme_id(cursor.getInt(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_PROGRAMME_ID)));
                info.setSource(cursor.getString(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_SOURCE)));
                info.setWebview_url(cursor.getString(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_WEBVIEW_URL)));
                info.setPos_x(cursor.getInt(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_POSITION_X)));
                info.setPos_y(cursor.getInt(cursor.getColumnIndex(TvDBHelper.AD_COLUMN_POSITION_Y)));
                
                list.add(info);
            }
            cursor.close();
            cursor = null;
        }
        return list;
    }

    /**
     * get a list of current adinfos at specified source
     * 
     * @param source : current source
     * @param info : current programme info, only used when source is DTV/ATV, set null in other case
     * @return
     */
    public List<AdInfo> getCurrentAdInfo(int source, ProgramInfo info){
        String[] whereArgs = new String[2];
        String selections = TvDBHelper.AD_COLUMN_SOURCE + "=? AND " + TvDBHelper.AD_COLUMN_PROGRAMME_ID + "=?";
        switch (source) {
            case TvCommonManager.INPUT_SOURCE_ATV:
                if (info == null)
                    return null;
                whereArgs[0] = "ATV";
                whereArgs[1] = String.valueOf(info.number);
                Log.i(TAG, "atv info number : " + info.number);
                break;
            case TvCommonManager.INPUT_SOURCE_DTV:
                if (info == null)
                    return null;
                whereArgs[0] = "DTV";
                whereArgs[1] = String.valueOf(info.serviceId);
                Log.i(TAG, "dtv info serviceId : " + info.serviceId);
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI:
                whereArgs[0] = "HDMI";
                whereArgs[1] = "0";
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI2:
                whereArgs[0] = "HDMI2";
                whereArgs[1] = "0";
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI3:
                whereArgs[0] = "HDMI3";
                whereArgs[1] = "0";
                break;
            case TvCommonManager.INPUT_SOURCE_CVBS:
                whereArgs[0] = "AV";
                whereArgs[1] = "0";
                break;
            case TvCommonManager.INPUT_SOURCE_YPBPR:
                whereArgs[0] = "YPBPR";
                whereArgs[1] = "0";
                break;
            case TvCommonManager.INPUT_SOURCE_VGA:
                whereArgs[0] = "VGA";
                whereArgs[1] = "0";
                break;
            default:
                break;
        }
        return getAdInfos(selections, whereArgs);
    }
    
    /**
     * empty data
     * 
     * @param tablename
     * @return
     */
    public synchronized long emptyData(String tablename) {
        return mDB.delete(tablename, null, null);
    }
    
    /**
     * arbitrary sql
     * 
     * @param sql statement
     * @return
     */
    public synchronized void execSQL(String sql) {
        mDB.execSQL(sql);
    }
}
