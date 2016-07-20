
package com.heran.launcher2.apps;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.R;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.mstar.android.tv.TvCommonManager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

public class AllAppListActivity extends Activity {
    private static final String TAG = "AllAppListActivity";

    private GridView appGridView;

    private List<ResolveInfo> listAllApps;

    private AllAppAdapter mAllAppAdapter;

    private ImageButton mAdButton;

    private AllAppAction mAllAppAction;

    private StringBuffer recData = new StringBuffer();

    private static final String SCIFLY_VIDEO_PKG = "com.eostek.scifly.video";

    /*
     * get the string mtaKeyCodes
     */
    public static String mtaKeyCodes = String.valueOf(KeyEvent.KEYCODE_7) + String.valueOf(KeyEvent.KEYCODE_8)
            + String.valueOf(KeyEvent.KEYCODE_9) + String.valueOf(KeyEvent.KEYCODE_2);

    private final ArrayList<Integer> keyQueue = new ArrayList<Integer>();

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.ALLAPPUPDATE:
                    try {
                        glideLoadGif(AllAppListActivity.this, mAllAppAction.getmAd().getPic(), mAdButton);
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void glideLoadGif(Context mContext, String mString, ImageButton mButton) {
        Log.d(TAG, "------req image:" + mString);
        if (mString == null) {
            return;
        }
        if (HomeApplication.isNumeric(mString)) {
            Glide.with(mContext).load(Integer.valueOf(mString)).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mButton);
        } else {
            Glide.with(mContext).load(mString).error(R.drawable.ad).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mButton);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.allapplication);
        appGridView = (GridView) findViewById(R.id.appgridview);
        mAdButton = (ImageButton) findViewById(R.id.allapp_ad_img);
        mAllAppAction = new AllAppAction(this, mHandler);
        TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);

        if (Utils.isNetworkState) {
            Runnable parsePgmJsonRunnable = new Runnable() {

                @Override
                public void run() {
                    mAllAppAction.parsePgmJson();
                }
            };
            HomeApplication.getInstance().addNetworkTask(parsePgmJsonRunnable);
        }
    }

    private void initGridView() {
        final PackageManager packageManager = getPackageManager();
        final Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // Get all the applications installed on the system
        listAllApps = packageManager.queryIntentActivities(mIntent, 0);
        // if 91Q do not click to install,do not show in all app
        if (!Utils.getIs91QInstall(this)) {
            for (ResolveInfo info : listAllApps) {
                if (SCIFLY_VIDEO_PKG.equals(info.activityInfo.packageName)) {
                    listAllApps.remove(info);
                    Log.v(TAG, "do not 91Q in all app!");
                    break;
                }
            }
        }
        Collections.sort(listAllApps, new ChineseCharComp());
        mAllAppAdapter = new AllAppAdapter(this, listAllApps);
        appGridView.setAdapter(mAllAppAdapter);

        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGridView();
        HomeApplication.getInstance().glideLoadGif(AllAppListActivity.this, mAllAppAction.getmAd().getPic(), mAdButton);
        appGridView.requestFocus();
        // default the first one has focus
        appGridView.setSelection(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Cycle moving left and right buttons
        int position = appGridView.getSelectedItemPosition();
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (findViewById(R.id.back_btn).hasFocus() || mAdButton.hasFocus()) {
                appGridView.requestFocus();
                appGridView.setSelection(position);
                return true;
            }
        }
        keyQueue.add(keyCode);
        if (keyQueue.size() == 4) {
            String keystr = intArrayListToString(keyQueue);
            if (keystr.equals(mtaKeyCodes)) {
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
        startActivity(intent);
    }

    private void setListener() {
        mAdButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(AllAppListActivity.this, MyWebViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("URL", mAllAppAction.getmAd().getGln());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        appGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResolveInfo appInfo = (ResolveInfo) parent.getItemAtPosition(position);
                Intent mIntent = getPackageManager().getLaunchIntentForPackage(appInfo.activityInfo.packageName);
                Log.d(TAG, "startApk :" + appInfo.activityInfo.packageName + "," + appInfo.activityInfo.name);
                if (mIntent != null) {
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        recData = new StringBuffer();
                        recData.append(recData + HistoryRec.block2Action[3] + ',' + appInfo.activityInfo.packageName
                                + ',' + HistoryRec.getCurrentDateTime());
                        HistoryRec.writeToFile(recData.toString());
                        recData.delete(0, recData.length());
                        startActivity(mIntent);
                    } catch (ActivityNotFoundException anf) {
                        UIUtil.showToast(AllAppListActivity.this, "apprunerror");
                    }
                } else {
                    UIUtil.showToast(AllAppListActivity.this, "apprunerror");
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
            } else if (myCollator.compare(info1.loadLabel(getPackageManager()),
                    info2.loadLabel(getPackageManager())) > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
