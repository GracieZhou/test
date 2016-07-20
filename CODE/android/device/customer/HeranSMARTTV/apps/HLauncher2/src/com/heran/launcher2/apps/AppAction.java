
package com.heran.launcher2.apps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.ServiceJson;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.db.DBManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

public class AppAction extends ServiceJson {
    private final static String TAG = "AppAction";

    private static final String XML_TAG_BEGIN = "item";

    private final Context mContext;

    private final Handler mHandler;

    private final Object isUpdate = new Object();

    private final DBManager mDbManager;

    private final Object cntLock = new Object();

    private int count = 0;

    // down App
    private List<AppInfoBean> downAppInfoList = new ArrayList<AppInfoBean>();
    List<AppInfoBean> downAppInfoLists = new ArrayList<AppInfoBean>();
    public List<AppInfoBean> getDownAppInfoList() {
        downAppInfoLists.clear();
        for(AppInfoBean mBean:downAppInfoList){
            if(!TextUtils.isEmpty(mBean.getTitle())&&!TextUtils.isEmpty(mBean.getPackageName())){
                downAppInfoLists.add(mBean);
            }
        }
        return downAppInfoLists;
    }

    private final int[] icons = {
            R.drawable.appfeed_icon_facebook, R.drawable.ic_logo_youtube, R.drawable.flickr_logo,
            R.drawable.pasacas_icon, R.drawable.history_ic_launcher,
    };

    private final Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            synchronized (isUpdate) {
                serverUrl = Constants.serverUrl_app;
                Log.d(TAG, "AppAction().parsePgmJson()");
                String parameter = "{\"ns\": \"ad\",\"nm\": \"GetAd\",\"op\": 1,\"bd\": {\"pos\":\"appApk_638.21\",\"typ\": 1,\"siz\": \"300X400\"}}";
                int state = UIUtil.getRespStatus(serverUrl);
                if (state == 404 || state == -1) {
                    Log.d(TAG, "state = " + String.valueOf(state));
                    mHandler.sendEmptyMessageDelayed(Constants.UPDATE_APP_INFO, 3*60 * 1000);
                    return;
                }
                try {
                    JSONObject jsonObject = getJSONObject(serverUrl, parameter, true);
                    if (jsonObject != null) {
                        JSONObject bdObject = jsonObject.getJSONObject("bd");
                        if (bdObject != null) {
                            JSONArray itsJSONArray = bdObject.optJSONArray("its");
                            Log.d(TAG, "its:" + itsJSONArray);
                            parseSearchConditionsJson(itsJSONArray);
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }
    };

    public AppAction(Context myContext, Handler myHandler) {
        mContext = myContext;
        mHandler = myHandler;
        mDbManager = DBManager.getDBInstance(mContext);
        downAppInfoList = mDbManager.getAppInfoBeans();
        Log.v(TAG, "" + (downAppInfoList == null || downAppInfoList.isEmpty()));
        if (downAppInfoList == null || downAppInfoList.isEmpty()) {
            String[] popStrings = mContext.getResources().getStringArray(R.array.pop_app);
            String[] packages = mContext.getResources().getStringArray(R.array.pop_app_package);
            String[] classs = mContext.getResources().getStringArray(R.array.pop_app_class);
            String[] url = mContext.getResources().getStringArray(R.array.pop_app_url);
            Log.d(TAG, "popStrings.length:"+popStrings.length);
            for (int i = 0; i < popStrings.length; i++) {
                AppInfoBean mTmpBean = new AppInfoBean();
                mTmpBean.setDownloadUrl(url[i]);
                mTmpBean.setTitle(popStrings[i]);
                if(i<icons.length){
                    mTmpBean.setPictureUrl(String.valueOf(icons[i]));
                }
                mTmpBean.setPackageName(packages[i]);
                mTmpBean.setClassName(classs[i]);
                downAppInfoList.add(mTmpBean);
                int id = (int) mDbManager.insertApp(mTmpBean);
                mTmpBean.setId(id);
            }
        }else if(downAppInfoList.size()<21){
            mDbManager.deleteAllAppInfo();
        }

    }

    /**
     * parse ad json
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public void parsePgmJson() {
        HomeApplication.getInstance().addNetworkTask(mRunnable);
    }

    private void parseSearchConditionsJson(JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "jsonObject.length ::" + jsonArray.length());
        int app_count = icons.length;
        if(jsonArray.length() > 0){            
            app_count = jsonArray.length();
        }
        for (int i = 0; i < app_count; i++) {
            AppInfoBean appInfo = downAppInfoList.get(i);
            JSONObject jsonobj = jsonArray.getJSONObject(i);
            appInfo.setTitle(jsonobj.optString("ti"));
            appInfo.setDownloadUrl(jsonobj.optString("gln"));
            if (appInfo.getDownloadUrl().indexOf("packageName") > 0) {
                String pkname = appInfo.getDownloadUrl().substring(appInfo.getDownloadUrl().lastIndexOf("=") + 1,
                        appInfo.getDownloadUrl().length());
                appInfo.setPackageName(pkname);
            }
            String http = jsonobj.optString("pic");
            appInfo.setPictureUrl(http);
            Log.w(TAG, "http :" + http);
            startGlideDownlodImgae(appInfo, app_count);
        }
    }

    /*
     * load Images
     */
    private void startGlideDownlodImgae(final AppInfoBean appInfo, final int size) {
        FutureTarget<File> future = Glide.with(mContext).load(appInfo.getPictureUrl()).downloadOnly(80, 80);
        try {
            File cacheFile = future.get();
            if (cacheFile != null) {
                Log.v(TAG, "GlideDownlodImgae apps = " + size + "---path:" + cacheFile.getPath());
                synchronized (cntLock) {
                    count++;
                    Log.v(TAG, "onLoadingComplete count = " + count);
                    // when all picture download finish
                    if (count == size) {
                        // update database data
                        mDbManager.updateApplications(downAppInfoList);
                        // send message to udpate ui
                        mHandler.sendEmptyMessage(Constants.APPUPDATE);
                        count = 0;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * get local recommendations applications, read from local xml data
     * 
     * @param void
     * @return List<ResolveInfo>
     * @throws JSONException
     */
    public List<ResolveInfo> shouDefault() {
        Log.i(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<----------shouDefault");
        XmlResourceParser parser = mContext.getResources().getXml(R.xml.iteminfo);
        List<ResolveInfo> defaultAppList = new ArrayList<ResolveInfo>();
        List<AppInfoBean> tmpDef = new ArrayList<AppInfoBean>();
        int position;
        try {
            while ((position = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (position != XmlResourceParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (XML_TAG_BEGIN.equals(name)) {
                    AppInfoBean iteminfo = new AppInfoBean();
                    iteminfo.setTitle(parser.getAttributeValue(null, "title"));
                    iteminfo.setPackageName(parser.getAttributeValue(null, "packageName"));
                    iteminfo.setClassName(parser.getAttributeValue(null, "className"));
                    Log.i(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<----------from xml  title : " + iteminfo.getTitle()
                            + "  clsname : " + iteminfo.getClassName());
                    tmpDef.add(iteminfo);
                }
            }
            Log.i(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<----------tmpDef size = " + tmpDef.size());

            PackageManager pm = mContext.getPackageManager();
            final Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
            mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> listAllApps = pm.queryIntentActivities(mIntent, 0);
            for (int i = 0; i < tmpDef.size(); i++) {
                AppInfoBean tmp = tmpDef.get(i);
                for (int j = 0, len = listAllApps.size(); j < len; j++) {
                    ResolveInfo ri = listAllApps.get(j);
                    if ((tmp.getClassName()).equals(ri.activityInfo.name)
                            && (tmp.getPackageName()).equals(ri.activityInfo.packageName)) {
                        // if find the app,break the inner loop
                        defaultAppList.add(ri);
                        break;
                    }
                }
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // add a empty ResolveInfo for the all App
        ResolveInfo mTmpInfo = new ResolveInfo();
        defaultAppList.add(mTmpInfo);
        return defaultAppList;
    }

}
