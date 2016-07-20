
package com.eostek.tv.launcher.ui;

import java.util.ArrayList;
import java.util.List;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.model.SettingItemInfo;
import com.eostek.tv.launcher.ui.adapter.SettingAdapter;
import com.eostek.tv.launcher.util.LConstants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;

/*
 * projectName： TVLauncher
 * moduleName： Settings.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-8-5 下午5:31:04
 * @Copyright © 2014 Eos Inc.
 */

public class GeneralSettings extends Activity {

    private final String TAG = GeneralSettings.class.getSimpleName();

    private final String SETTING_PREFERENCE = "general_setting";

    private final String SHARPNESS = "sharpness";

    private final String ASPECT_RATIO = "aspect_ratio";

    private final String SKIP_START_END = "skip_start_end";

    private ListView mListView;

    private SettingAdapter adapter;

    private SharedPreferences preferences;

    private Editor editor;

    private List<SettingItemInfo> items = null;

    private SettingItemInfo lightItem = null;

    private SettingItemInfo scaleItem = null;

    private SettingItemInfo skipStartAndEndItem = null;

    private SettingItemInfo refreshPageItem = null;

    private SettingItemInfo checkNewVersionItem = null;

    private SettingItemInfo resetAppItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_settings);

        initItem();
        mListView = (ListView) findViewById(R.id.listview);
        adapter = new SettingAdapter(this, items);
        mListView.setAdapter(adapter);
        setListViewListener();
        // preferences = getSharedPreferences(SETTING_PREFERENCE,
        // Context.MODE_PRIVATE);
        // editor = preferences.edit();
        // mListView = (ListView) findViewById(R.id.listview);
        // adapter = new SettingAdapter(this,preferences);
        // mListView.setAdapter(adapter);
        // setListViewListener();

        // shareness = preferences.getInt(SHARPNESS, 0);
        // aspectRate = preferences.getInt(ASPECT_RATIO, 0);
        // skipStartEnd = preferences.getInt(SKIP_START_END, 0);
        //
        // contentList.add(getResources().getStringArray(R.array.sharpness_array)[shareness]);
        // contentList.add(getResources().getStringArray(R.array.aspect_ratio_array)[aspectRate]);
        // contentList.add(getResources().getStringArray(R.array.skip_start_end_array)[skipStartEnd]);
        // contentList.add(getResources().getString(R.string.get_lastest_launcher_data));
        // contentList.add("V" + UIUtil.getVersionName(this));
        // contentList.add(getResources().getString(R.string.reset_application));
        // for (int i = 0; i < adapter.getCount(); i++) {
        // adapter.setTextContent(i, contentList.get(i));
        // }
    }

    private void initItem() {
        items = new ArrayList<SettingItemInfo>();
        String[] title = getResources().getStringArray(R.array.setting_item_title);

        String[] lights = getResources().getStringArray(R.array.sublight_item);
        String[] scales = getResources().getStringArray(R.array.subscale_item);
        String[] skips = getResources().getStringArray(R.array.subskip_item);
        String[] refresh = new String[] {
            getResources().getString(R.string.obtain_new_page)
        };
        String[] version = new String[] {
            getResources().getString(R.string.check_new_version)
        };
        String[] reset = new String[] {
            getResources().getString(R.string.reset_app)
        };

        lightItem = new SettingItemInfo(this, title[0], lights, 0, LConstants.ITEM_ENUM);
        scaleItem = new SettingItemInfo(this, title[1], scales, 0, LConstants.ITEM_ENUM);
        skipStartAndEndItem = new SettingItemInfo(this, title[2], skips, 0, LConstants.ITEM_ENUM);
        refreshPageItem = new SettingItemInfo(this, title[3], refresh, LConstants.ITEM_BUTTON);
        checkNewVersionItem = new SettingItemInfo(this, title[4], version, LConstants.ITEM_BUTTON);
        resetAppItem = new SettingItemInfo(this, title[5], reset, LConstants.ITEM_BUTTON);

        items.add(lightItem);
        items.add(scaleItem);
        items.add(skipStartAndEndItem);
        items.add(refreshPageItem);
        items.add(checkNewVersionItem);
        items.add(resetAppItem);
        initValues();
    }

    private void initValues() {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setListViewListener() {
        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "onItemClick position = " + position);
                switch (position) {
                    case LConstants.SHARPNESS_ITEM:

                        break;
                    case LConstants.ASPECT_RATIO_ITEM:

                        break;
                    case LConstants.SKIP_START_END_ITEM:

                        break;
                    case LConstants.REFRESH_HOME_ITEM:

                        break;
                    case LConstants.CHECK_UPDATE_ITEM:

                        break;
                    case LConstants.RESET_ITEM:

                        break;

                    default:
                        break;
                }
            }
        });

        mListView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            int position = mListView.getSelectedItemPosition();
                            items.get(position).onKeyDown(keyCode, event, position);
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    public List<SettingItemInfo> getItems() {
        return items;
    }

    /**
     * update the view infomation
     * 
     * @param position The position to update
     */
    public void updateView(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SettingItemInfo item = items.get(position);
                View view = mListView.getSelectedView();
                if (item.getItemType() == LConstants.ITEM_ENUM) {
                    TextView content = (TextView) view.findViewById(R.id.content);
                    content.setText(item.getValues()[item.getCurValue()]);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * item click event
     * 
     * @param position The click item
     */
    public void itemClick(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                break;
        }
    }

}
