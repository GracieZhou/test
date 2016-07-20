
package com.eostek.tv.launcher.ui;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.business.database.DBManager;
import com.eostek.tv.launcher.business.receiver.PackageReceiver;
import com.eostek.tv.launcher.model.MetroInfo;
import com.eostek.tv.launcher.ui.adapter.AllAppAdapter;
import com.eostek.tv.launcher.util.FastBlur;
import com.eostek.tv.launcher.util.GoogleAnalyticsUtil;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.TvUtils;
import com.eostek.tv.launcher.util.UIUtil;

/*
 * projectName： TVLauncher
 * moduleName： AllAppActivity.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-24 下午2:09:31
 * @Copyright © 2014 Eos Inc.
 */
/**
 * the activity to show all application installed except the package in
 * blacklist,in different platform,the blacklist is different
 **/
public class AllAppActivity extends Activity {

    private final String TAG = AllAppActivity.class.getSimpleName();

    private GridView appGridView;

    private List<ResolveInfo> listAllApps;

    private AllAppAdapter mAllAppAdapter;

    private PackageReceiver mPackageReceiver;

    private RelativeLayout relativeLayout;

    private Bitmap backgroundBitmap;

    // the apk list which would not show in all app
    private List<String> blackList;

    // the apk list from launcher
    private List<String> blackListLauncher;

    private DBManager mDbManager;

    public final String MTAKEYCODES = String.valueOf(KeyEvent.KEYCODE_7) + String.valueOf(KeyEvent.KEYCODE_8)
            + String.valueOf(KeyEvent.KEYCODE_9) + String.valueOf(KeyEvent.KEYCODE_2);

    private ArrayList<Integer> keyQueue = new ArrayList<Integer>();

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LConstants.PACKAGE_ADDED:
                case LConstants.PACKAGE_REMOVED:
                    initGridView();
                    break;
                case LConstants.LOCAL_CHANGE:
                    AllAppActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.allapp_layout);

        mDbManager = DBManager.getDbManagerInstance(HomeApplication.getInstance());
        Set<String> listSet = mDbManager.getMetroPackages();
        if (blackListLauncher == null) {
            blackListLauncher = new ArrayList<String>();
        } else {
            blackListLauncher.clear();
        }
        for (String string : listSet) {
            blackListLauncher.add(string);
        }

        relativeLayout = (RelativeLayout) findViewById(R.id.all_app_rl);
        appGridView = (GridView) findViewById(R.id.appgridview);
        initGridView();

        // register application install and uninstall
        mPackageReceiver = new PackageReceiver(this, mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter2.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter2.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter2.addDataScheme("package");
        registerReceiver(mPackageReceiver, intentFilter2);

        TvUtils.setInputToStorage(TAG);
        // applyBlur();
    }

    private void initGridView() {
        final PackageManager packageManager = getPackageManager();
        final Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // Get all the applications installed on the system
        listAllApps = new ArrayList<ResolveInfo>();
        List<ResolveInfo> tempInfos = packageManager.queryIntentActivities(mIntent, 0);
        blackList = getBlckList();
        for (ResolveInfo resolveInfo : tempInfos) {
            if (!shouldHide(blackList, resolveInfo.activityInfo.packageName)) {
                listAllApps.add(resolveInfo);
            } else {
                Log.v(TAG, "blackList : " + resolveInfo.activityInfo.packageName);
            }
        }
        Collections.sort(listAllApps, new ChineseCharComp());
        mAllAppAdapter = new AllAppAdapter(this, listAllApps);
        appGridView.setAdapter(mAllAppAdapter);
        if (HomeApplication.getFocusType() == LConstants.FOCUS_TYPE_STATIC) {
            appGridView.setSelector(getResources().getDrawable(R.drawable.imagebutton_focus_border));
        } else if (HomeApplication.getFocusType() == LConstants.FOCUS_TYPE_DYNAMIC) {
            appGridView.setSelector(getResources().getDrawable(R.drawable.home_focus));
        }
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIUtil.setDefaultBackground(this, relativeLayout);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        keyQueue.add(keyCode);
        if (keyQueue.size() == 4) {
            String keystr = intArrayListToString(keyQueue);
            if (keystr.equals(MTAKEYCODES)) {
                keyQueue.clear();
                startApk("com.utsmta.app", "com.utsmta.app.MainActivity");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private String intArrayListToString(ArrayList<Integer> al) {
        String str = "";
        for (int i = 0; i < al.size(); ++i) {
            str += al.get(i).toString();
        }
        return str;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPackageReceiver != null) {
            unregisterReceiver(mPackageReceiver);
        }
    }

    private void setListener() {
        appGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResolveInfo appInfo = (ResolveInfo) parent.getItemAtPosition(position);
                Intent mIntent = AllAppActivity.this.getPackageManager().getLaunchIntentForPackage(
                        appInfo.activityInfo.packageName);
                if (mIntent != null) {
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {

                        MetroInfo metroInfo = new MetroInfo();
                        metroInfo.setPkgName(appInfo.activityInfo.packageName);
                        metroInfo.setTitle(appInfo.loadLabel(getPackageManager()).toString());
                        GoogleAnalyticsUtil.sendEvent(metroInfo, true, true);

                        AllAppActivity.this.startActivity(mIntent);
                    } catch (ActivityNotFoundException anf) {
                        Toast.makeText(AllAppActivity.this, "apprunerror", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AllAppActivity.this, "apprunerror", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private class ChineseCharComp implements Comparator<ResolveInfo> {

        @Override
        public int compare(ResolveInfo lhs, ResolveInfo rhs) {
            ResolveInfo info1 = lhs;
            ResolveInfo info2 = rhs;
            Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
            if (myCollator.compare(info1.loadLabel(getPackageManager()), info2.loadLabel(getPackageManager())) < 0) {
                return -1;
            } else if (myCollator.compare(info1.loadLabel(getPackageManager()), info2.loadLabel(getPackageManager())) > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /*
     * start an application
     * @param pckName PackageName
     * @param clsName ClassName
     */
    private void startApk(String pckName, String clsName) {
        ComponentName componentName = new ComponentName(pckName, clsName);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        try {
            AllAppActivity.this.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to start MTA !");
        }
    }

    /**
     * set the blured picture as background
     */
    public void applyBlur() {
        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                long time = System.currentTimeMillis();
                // backgroundBitmap = HomeActivity.getCurrentBackground();
                blur(backgroundBitmap, relativeLayout);
                Log.v(TAG, "onGlobalLayout applyBlur time = " + (System.currentTimeMillis() - time) + "; id = "
                        + backgroundBitmap.toString());
            }
        });
    }

    /**
     * create blur bitmap for the given view
     * 
     * @param bkg
     * @param view
     */
    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        // the value set to blur bitmap
        float scaleFactor = 11;
        float radius = 7;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
        Log.v(TAG, "blur time = " + (System.currentTimeMillis() - startMs + "ms"));
    }

    /**
     * get black list which will not show in all app,the list include the data
     * from both from xml and from launcher
     * 
     * @return
     */
    private List<String> getBlckList() {
        List<String> tmpList = getBlackListFromXml();
        // for L,just use the xml blacklist
        if (!HomeApplication.isHasTVModule()) {
            // add the laucnher black list to xml blacklist
            if (blackListLauncher != null && !blackListLauncher.isEmpty()) {
                tmpList.addAll(blackListLauncher);
            }
        }
        return tmpList;
    }

    /**
     * the xml file is something like this <packages> <app
     * pkg="com.google.android.voicesearch"/>
     * 
     * @return
     */
    private List<String> getBlackListFromXml() {
        List<String> list = new ArrayList<String>();
        XmlPullParser parser;
        if (HomeApplication.isHasTVModule()) {
            parser = getResources().getXml(R.xml.blacklist_l);
        } else {
            parser = getResources().getXml(R.xml.blacklist_dangle);
        }
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equals("app")) {
                        String pkgName = parser.getAttributeValue(null, "pkg");
                        list.add(pkgName);
                        // Log.v(TAG, "pkg = " + pkgName);
                    }
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * whether hide the application in all app
     * 
     * @param pkgName
     * @return true hide the application,else false
     */
    private boolean shouldHide(List<String> list, String pkgName) {
        boolean hide = list.contains(pkgName);
        // Log.v(TAG, pkgName + " shouldHide " + hide);
        return hide;
    }

}
