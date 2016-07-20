
package com.heran.launcher2.util.db;

import java.util.ArrayList;
import java.util.List;

import com.heran.launcher2.advert.MyAD;
import com.heran.launcher2.apps.AppInfoBean;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/*
 * projectName： EosLauncher
 * moduleName： DBManager.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-12-9 下午5:19:54
 * @Copyright © 2014 Eos Inc.
 */

public class DBManager {

    private static final String TAG = DBManager.class.getSimpleName();

    private static DBManager mDbManager;

    private static DBHelper mDBHelper;

    private DBManager(Context context) {
        mDBHelper = new DBHelper(context);
    }

    /**
     * get DBManager instance
     * 
     * @param context context object
     * @return DBManager object
     */
    public static DBManager getDBInstance(Context context) {
        if (mDbManager == null) {
            synchronized (DBManager.class) {
                if (mDbManager == null) {
                    mDbManager = new DBManager(context);
                }
            }
        }
        // mDBHelper.openDB(context);
        return mDbManager;
    }

    /**
     * get the ad data from database
     * 
     * @param type advertisement type,now support 4 kind :1)the ad show in home
     *            fragment {@link DBHelper#HOME_AD} ; 2)the ad show in app
     *            fragment {@link DBHelper#APP_STORE_AD} ; 3)the ad show in all
     *            app activity {@link DBHelper#ALL_APP_AD} ; 4)the text ad show
     *            in home fragment {@link DBHelper#TEXT_AD}
     * @return List<MyAD> object
     */
    public List<MyAD> getAds(String type) {
        List<MyAD> ads = new ArrayList<MyAD>();
        String selections = DBHelper.ADVERTISEMENT_COLUMN_TYPE + "=?";
        String[] selectionArgs = new String[] {
                type
        };
        Cursor cursor = mDBHelper.query(DBHelper.ADVERTISEMENT_TABLE_NAME, null, selections, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MyAD myAD = new MyAD();
                myAD.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_TABLE_COLUMN_ID)));
                myAD.setTi(cursor.getString(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_TITLE)));
                myAD.setCpi(cursor.getString(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_CP)));
                myAD.setDit(cursor.getInt(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_SHOW_TIME)));
                myAD.setDsr(cursor.getString(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_DESCRIPTION)));
                myAD.setGln(cursor.getString(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_LINK)));
                myAD.setPic(cursor.getString(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_PICTURE_URL)));
                myAD.setPlt(cursor.getInt(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_ANIMATION)));
                myAD.setSiz(cursor.getString(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_SIZE)));
                myAD.setType(cursor.getString(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_TYPE)));
                myAD.setUpd(cursor.getString(cursor.getColumnIndex(DBHelper.ADVERTISEMENT_COLUMN_UPDATE_TIME)));
                ads.add(myAD);
            }
            cursor.close();
        }
        return ads;
    }

    /**
     * udpate database data in table {@link DBHelper#ADVERTISEMENT_TABLE_NAME}
     * 
     * @param type The advertisement type
     * @param myADs The data to insert to database
     * @return the count number of insert data
     */
    public long updateAds(String type, List<MyAD> myADs) {
        long result = 0;
        if (myADs == null || myADs.isEmpty()) {
            return 0;
        }
        String selections = DBHelper.ADVERTISEMENT_COLUMN_TYPE + "=?";
        String[] selectionArgs = new String[] {
                type
        };
        result = mDBHelper.delete(DBHelper.ADVERTISEMENT_TABLE_NAME, selections, selectionArgs);
        Log.i(TAG, "delete number is " + result);
        result = 0;
        for (MyAD ad : myADs) {
            mDBHelper.insertAdvertisement(ad);
            result++;
        }
        Log.i(TAG, "insert number is " + result);
        return result;
    }

    /**
     * update record in database
     * 
     * @param myAD
     * @return
     */
    public long updateAds(MyAD myAD) {
        return mDBHelper.udpateAdvertisement(myAD);
    }

    /**
     * insert a record to database
     * 
     * @param myAD
     * @return
     */
    public long insertAd(MyAD myAD) {
        return mDBHelper.insertAdvertisement(myAD);
    }

    /**
     * get all application infomation which store in database
     */
    public List<AppInfoBean> getAppInfoBeans() {
        List<AppInfoBean> list = new ArrayList<AppInfoBean>();
        Cursor cursor = mDBHelper.query(DBHelper.APPLICATION_TABLE_NAME, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AppInfoBean infoBean = new AppInfoBean();
                infoBean.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.APPLICATION_COLUMN_ID)));
                infoBean.setClassName(cursor.getString(cursor.getColumnIndex(DBHelper.APPLICATION_COLUMN_CLASS_NAME)));
                infoBean.setDownloadUrl(
                        cursor.getString(cursor.getColumnIndex(DBHelper.APPLICATION_COLUMN_DOWNLOAD_URL)));
                infoBean.setPackageName(
                        cursor.getString(cursor.getColumnIndex(DBHelper.APPLICATION_COLUMN_PACKAGE_NAME)));
                infoBean.setPictureUrl(cursor.getString(cursor.getColumnIndex(DBHelper.APPLICATION_COLUMN_PIC_URL)));
                infoBean.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.APPLICATION_COLUMN_TITLE)));
                list.add(infoBean);
            }
            cursor.close();
        }
        return list;
    }

    /**
     * udpate database data in table {@link DBHelper#APPLICATION_TABLE_NAME}
     * 
     * @param list The data to update
     * @return the count number of insert data
     */
    public long updateApplications(List<AppInfoBean> list) {
        long result = 0;
        if (list == null || list.isEmpty()) {
            return 0;
        }
        result = mDBHelper.delete(DBHelper.APPLICATION_TABLE_NAME, null, null);
        Log.i(TAG, "delete number is " + result);
        result = 0;
        for (AppInfoBean appInfoBean : list) {
            mDBHelper.insertApplication(appInfoBean);
            result++;
        }
        Log.i(TAG, "insert number is " + result);
        return result;
    }

    /**
     * insert a record to database
     * 
     * @param infoBean
     * @return
     */
    public long insertApp(AppInfoBean infoBean) {
        return mDBHelper.insertApplication(infoBean);
    }

    /**
     * update the record in database
     * 
     * @param infoBean
     * @return
     */
    public long updateApp(AppInfoBean infoBean) {
        return mDBHelper.updateApplication(infoBean);
    }

}
