
package com.eostek.sciflyui.thememanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.eostek.sciflyui.thememanager.task.ThemeModel;
import com.eostek.sciflyui.thememanager.ui.DeleteThemeDialog;
import com.eostek.sciflyui.thememanager.ui.GridViewBaseAdapter;
import com.eostek.sciflyui.thememanager.util.Constant;
import com.eostek.sciflyui.thememanager.util.ThemeManagerUtils;
import com.eostek.sciflyui.thememanager.util.Utils;

/**
 * @author admin
 */
public class ThemeDisplayAct extends Activity {
    /**
     * TAG.
     */
    protected static final String TAG = "ThemeDisplayAct";

    // protected static final String LOCAL_THEME_DESC =
    // "/mnt/sdcard/eostek/description.xml";
    /**
     * LOCAL_THEME_DESC.
     */
    protected static final String LOCAL_THEME_DESC = "/data/eostek/description.xml";

    /**
     * LOCAL_WALLPAPER_DESC.
     */
    protected static final String LOCAL_WALLPAPER_DESC = "/data/eostek/wallpaper";

    /**
     * INITIALIZE_UI.
     */
    protected static final int INITIALIZE_UI = 1;

    /**
     * UPGRADE_GRIDVIEW.
     */
    protected static final int UPGRADE_GRIDVIEW = 2;

    /**
     * RESTART_SCIFLY_VIDEO.
     */
    protected static final int RESTART_SCIFLY_VIDEO = 3;

    /**
     * NO_THEMES.
     */
    protected static final int NO_THEMES = 4;

    private Map<String, String> mThemeNames = new HashMap<String, String>();

    List<ThemeModel> mThemes = new ArrayList<ThemeModel>();

    /**
     * mCurrentThemeModel.
     */
    public ThemeModel mCurrentThemeModel;

    /**
     * mCurrentView.
     */
    public View mCurrentView;

    private ThemeListener mListener;

    private ThemeHolder mHolder;

    DeleteThemeDialog mDeleteThemeDialog;

    int mSelected = 0;

    private View mHoveredView;

    private boolean mIsThreadAlive = false;

    private static String server_tvos_url = "";

    private static final String SERVER_URL_PROPERTY = "ro.scifly.service.url";
    
    private static final String DEFAULT_SERVER_URL = "http://tvosapp.babao.com/interface/clientService.jsp";

    /**
     * @param hoveredView hoveredView
     */
    public void setHoveredView(View hoveredView) {
        this.mHoveredView = hoveredView;
    }

    /**
     * @param selected selected
     */
    public void setSelected(int selected) {
        this.mSelected = selected;
    }

    /**
     * @return ThemeHolder
     */
    public ThemeHolder getHolder() {
        return mHolder;
    }

    /**
     * @return List<ThemeModel>
     */
    public List<ThemeModel> getmThemes() {
        return mThemes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Name:SciflyTheme, Version:2.4.38, Date:2015-09-02, Publisher:Youpeng.Wan,Shirley.Jiang, REV:40782");

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(br, filter);
        // registerReceiver(receiver, filter);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_main);

            mHolder = new ThemeHolder(this);
            mHolder.getViews();

            mListener = new ThemeListener(this, mHolder);
            mListener.setListeners();
            mListener.setmContext(getApplicationContext());
            initData(INITIALIZE_UI);

            changeBackgound();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.app_exception, Toast.LENGTH_SHORT).show();
            finish();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.low_memory, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Throwable t) {
            Toast.makeText(this, R.string.app_exception, Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mListener.mWarning != null) {
            mListener.mWarning.dismiss();
        }

        if (mListener.getChangeThemeWaittingDialog() != null && mListener.getChangeThemeWaittingDialog().isShowing()) {
            mListener.getChangeThemeWaittingDialog().dismiss();
        }

        android.os.Process.killProcess(android.os.Process.myPid());

        // recycle bitmap.
        // if (mAdapter != null) {
        // // mAdapter.recycleMemory();
        // }

        unregisterReceiver(br);
        // unregisterReceiver( receiver );

    }

    void changeBackgound() {

        // try {
        // Drawable wallPaper =
        // WallpaperManager.getInstance(ThemeDisplayAct.this).getDrawable();
        //
        // if (wallPaper != null) {
        // RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_layout);
        // rl.setBackground(wallPaper);
        //
        // }
        // } catch (Throwable t) {
        // t.printStackTrace();
        // }

        // Bitmap bitmap = null;
        //
        // File file = new File(LOCAL_WALLPAPER_DESC);
        //
        // if (!file.exists() || !file.isDirectory()) {
        // return;
        // }
        // File[] children = file.listFiles();
        //
        // for (int i = 0; i < children.length; i++) {
        // File child = children[i];
        // if (child.getName().startsWith("wallpaper")
        // && ThemeManagerUtils.checkExtension(child.getName(), new String[] {
        // ".jpg", ".png"
        // })) {
        // bitmap = BitmapFactory.decodeFile(child.getPath());
        // if (bitmap == null) {
        // continue;
        // }
        //
        // Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
        //
        // break;
        // }
        // }
        //
        // // File file = new
        // File("/mnt/sdcard/wallpaper_Home_wallpaper6p.jpg");

    }

    protected void initData(final int what) {

        if (mIsThreadAlive) {
            return;
        } else {
            mIsThreadAlive = true;

            mThemeNames.clear();
            new Thread(new Runnable() {

                @Override
                public void run() {

                    toSureDescriptionExist();
                    // Unpack the current theme.
                    String currentThemeStr = ThemeManagerUtils.readLocalFile(Constant.LOCAL_THEME_DESC);
                    Log.i(TAG, "currentThemeStr:" + currentThemeStr);

                    mCurrentThemeModel = ThemeManagerUtils.parseLocalTheme(currentThemeStr);
                    // Unpack the built-in theme.
                    try {
                        mThemes = ThemeManagerUtils.getLocalThemesByFolder(Constant.SYSTEM_DEFAULT_PATH, mThemeNames);
                    } catch (ZipException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(what);

                    try {
                        // Unpack the downloaded theme
                        mThemes.addAll(ThemeManagerUtils.getLocalThemesByFolder(Constant.CACHE_PATH, mThemeNames));
                    } catch (ZipException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(what);

                    try {
                        // get onlline themes.
                        server_tvos_url = SystemProperties.get(SERVER_URL_PROPERTY);
                        Log.i(TAG, "theme server url :: " + server_tvos_url);
                        server_tvos_url = TextUtils.isEmpty(server_tvos_url) ? DEFAULT_SERVER_URL : server_tvos_url;
                        List<ThemeModel> mOnlineThemes = ThemeManagerUtils.parseOnlineTheme(20, mThemeNames, mThemes,
                                server_tvos_url);
                        mThemes.addAll(mOnlineThemes);

                        /**
                         * add informations of local file which downloaded from
                         * server to database
                         */

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mThemes.size() == 0) {
                        mHandler.sendEmptyMessage(NO_THEMES);
                    }
                    mHandler.sendEmptyMessage(what);
                    mIsThreadAlive = false;
                }
            }).start();

        }
    }

    /**
     * mHandler.
     */
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == INITIALIZE_UI) {
                initUI();
            } else if (msg.what == UPGRADE_GRIDVIEW) {
                upgradeGridView();
            } else if (msg.what == RESTART_SCIFLY_VIDEO) {
                mActivityManager = (ActivityManager) ThemeDisplayAct.this.getSystemService(Context.ACTIVITY_SERVICE);
                exitAppByName(SCIFLY_VIDEO_PCKNAME);
            } else if (msg.what == NO_THEMES) {
                Toast.makeText(ThemeDisplayAct.this, R.string.get_theme_failed, Toast.LENGTH_SHORT).show();
            }
        }

    };

    ActivityManager mActivityManager;

    final String mActivityMngClass = "android.app.ActivityManager";

    final String mMethodName = "forceStopPackage";

    final String SCIFLY_VIDEO_PCKNAME = "com.eostek.scifly.video";

    private void exitAppByName(String pkgName) {
        try {
            Method method = Class.forName(mActivityMngClass).getMethod(mMethodName, String.class);
            method.invoke(mActivityManager, pkgName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    // public int itemWidth;

    private GridViewBaseAdapter mAdapter;

    // private AnimatedSelector animatedSelector;

    View selector;

    /**
     * @return ScreenMetrics
     */
    public float getScreenMetrics() {
        // 得到像素密度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        float density = outMetrics.density; // 像素密度
        return density;
    }

    /**
     * initUI.
     */
    public void initUI() {

        mAdapter = new GridViewBaseAdapter(ThemeDisplayAct.this, mThemes);

        mHolder.getGirdview().setAdapter(mAdapter);

        mListener.setItemHoverListener();

        // itemWidth = (int) (225 * getScreenMetrics());

        mHolder.getGirdview().setSelection(mSelected);
        // set event listener
    }

    /**
     * upgradeGridView.
     */
    public void upgradeGridView() {

        mAdapter.setUpdateAll(false);
        mAdapter.setUpdateSelected(mSelected);
        mAdapter.notifyDataSetChanged();
        // mAdapter = new GridViewBaseAdapter(ThemeDisplayAct.this, mThemes);
        //
        // mHolder.girdview.setAdapter(mAdapter);
        //
        // mHolder.girdview.setSelection(selected);

        Log.i(TAG, "selected " + mSelected);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:

                int selectedPosition = mHolder.getGirdview().getSelectedItemPosition();

                if (selectedPosition == -1) {
                    selectedPosition = getPositionByView();
                    if (selectedPosition == -1) {
                        return false;
                    }
                }
                Log.i(TAG, "" + selectedPosition);

                ThemeModel selectedModel = mAdapter.getmThemes().get(selectedPosition);

                // Log.i(TAG,"" + mCurrentTheme.hashCode());
                Log.i(TAG, "" + selectedModel.hashCode());

                if (selectedModel.mType == ThemeModel.TYPE.ONLINE) {
                    Log.i(TAG, "trying to delete online theme");
                    Utils.showToast(this, getString(R.string.deleteOnlineTheme));
                    return false;
                }

                if (selectedModel.mType == ThemeModel.TYPE.DEFAULT) {
                    Log.i(TAG, "trying to delete default theme");
                    Utils.showToast(this, getString(R.string.deleteDefaultTheme));
                    return false;
                }

                if (selectedModel.equals(mCurrentThemeModel)) {
                    Log.i(TAG, "trying to delete current theme");
                    Utils.showToast(this, getString(R.string.deleteCurrentTheme));
                    return false;
                }

                mListener.setSelectedPosition(selectedPosition);

                mDeleteThemeDialog = new DeleteThemeDialog(ThemeDisplayAct.this);
                mDeleteThemeDialog.show(selectedPosition);
                mDeleteThemeDialog.setDeleteListener(mListener.deleteThemeListener);
                mDeleteThemeDialog.setDeleteCancelListener(mListener.deleteThemeCancelListener);

                break;
            case KeyEvent.KEYCODE_1:
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    private int getPositionByView() {
        int position = -1;
        if (mHoveredView != null) {
            try {
                position = mHolder.getGirdview().getPositionForView(mHoveredView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "view position =" + position);
            return position;
        }

        return -1;
    }

    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.i("test", "The net is changed");

            String action = intent.getAction();
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            // NetworkInfo wifiInfo =
            // mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                // The net is changed
                initData(INITIALIZE_UI);
                /*
                 * if (wifiInfo != null) { if (wifiInfo.isConnected() &&
                 * wifiInfo.isAvailable()) { Toast.makeText(context,
                 * R.string.netconnectsuccess, Toast.LENGTH_SHORT).show(); }
                 * else { Toast.makeText(context, R.string.netconnectfail,
                 * Toast.LENGTH_SHORT).show(); // The download is not completed.
                 * if (mListener.getmPercent() > 0 && mListener.getmPercent() <
                 * 100) {
                 * mListener.getmManager().removeTask(mListener.getmTask()
                 * .getTaskId()); } } }
                 */
                if (info != null) {

                    if (!info.isConnected() || !info.isAvailable()) {
                        Toast.makeText(context, R.string.netconnectfail, Toast.LENGTH_SHORT).show();
                        // The download is not completed.
                        if (mListener.getmPercent() > 0 && mListener.getmPercent() < 100) {
                            mListener.getmManager().removeTask(mListener.getmTask().getTaskId());
                        }
                    }

                } else {
                    Toast.makeText(context, R.string.netconnectfail, Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    private void toSureDescriptionExist() {

        File fileDir = new File("/data/eostek/");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file_desc = new File(Constant.LOCAL_THEME_DESC);
        if (!file_desc.exists()) {
            File file = new File(Constant.SYSTEM_DEFAULT_PATH);
            if (file.isDirectory()) {
                File files[] = file.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.getName().endsWith(".zip") || pathname.getName().endsWith(".stz")) {
                            return true;
                        }
                        return false;
                    }
                });
                if (files != null) {
                    for (File f : files) {

                        String description;
                        BufferedWriter br = null;
                        try {
                            br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_desc)));
                            description = ThemeManagerUtils.unzipDescription(f.getAbsolutePath());
                            br.write(description);
                            br.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        } finally {
                            if (br != null) {
                                try {
                                    br.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;// FIXME : update me latter
                    }
                }
            }
        }
    }
}
