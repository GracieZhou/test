
package com.eostek.tv.threedimensions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.EnumColorTemperature;
import com.mstar.android.tvapi.common.vo.EnumPictureMode;
import com.mstar.android.tvapi.common.vo.EnumVideoArcType;
import com.mstar.android.tvapi.common.vo.EnumVideoItem;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

/**
 * projectName： Eosmisc moduleName： PictureSettingActivity.java
 * 
 * @author swift.wan
 * @version 1.0.0
 * @time 2014-8-5 下午5:31:04
 * @Copyright © 2015 Eos Inc.
 */

public class PictureSettingActivity extends Activity {

    private final String TAG = PictureSettingActivity.class.getSimpleName();

    private ExecutorService mTvApiThreadExecutor;

    private TvPictureManager manager;

    private TextView mtitleTxt;

    private ListView mListView;

    private SettingAdapter adapter;

    private List<SettingItemInfo> items = null;

    private SettingItemInfo brightnessItem = null;

    private SettingItemInfo contrastItem = null;

    private SettingItemInfo backlightItem = null;

    private SettingItemInfo sharpnessItem = null;

    private SettingItemInfo HueItem = null;

    private SettingItemInfo SaturationItem = null;

    private SettingItemInfo RedItem = null;

    private SettingItemInfo GreenItem = null;

    private SettingItemInfo BlueItem = null;

    private SettingItemInfo itemZoomMode = null;

    private TvPictureManager mTvPictureManager = TvPictureManager.getInstance();

    private EnumInputSource curSourceType = EnumInputSource.E_INPUT_SOURCE_NONE;

    private String[] mZoomArray;

    private final static int COLORTEMP = 16;
    
    private ColorTemperatureExData colorTempExData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_settings);
        mtitleTxt = (TextView) findViewById(R.id.title_txt);
        mtitleTxt.setText(R.string.media_title);
        mListView = (ListView) findViewById(R.id.listview);

        initItem();

        adapter = new SettingAdapter(this, items);
        mListView.setAdapter(adapter);

        setListViewListener();

        mTvApiThreadExecutor = Executors.newSingleThreadExecutor();
    }

    private void initItem() {
        manager = TvPictureManager.getInstance();

        curSourceType = TvCommonManager.getInstance().getCurrentInputSource();
        items = new ArrayList<SettingItemInfo>();
        String[] title = getResources().getStringArray(R.array.title);

        brightnessItem = new SettingItemInfo(this, title[Constants.BRIGHTNESS], 0, 100);
        items.add(brightnessItem);

        contrastItem = new SettingItemInfo(this, title[Constants.CONTRAST], 0, 100);
        items.add(contrastItem);

        backlightItem = new SettingItemInfo(this, title[Constants.BACKLIGHT], 0, 100);
        items.add(backlightItem);

        sharpnessItem = new SettingItemInfo(this, title[Constants.SHARPNESS], 0, 100);
        items.add(sharpnessItem);

        HueItem = new SettingItemInfo(this, title[Constants.HUE], 0, 100);
        items.add(HueItem);

        SaturationItem = new SettingItemInfo(this, title[Constants.SATUYRATION], 0, 100);
        items.add(SaturationItem);

        RedItem = new SettingItemInfo(this, title[Constants.RED], 0, 128);
        items.add(RedItem);

        GreenItem = new SettingItemInfo(this, title[Constants.GREEN], 0, 128);
        items.add(GreenItem);

        BlueItem = new SettingItemInfo(this, title[Constants.BLUE], 0, 128);
        items.add(BlueItem);

        mZoomArray = getResources().getStringArray(R.array.setting_zoommode_vals);
        itemZoomMode = new SettingItemInfo(this, title[Constants.Zoom], mZoomArray);
        items.add(itemZoomMode);

        initValues();
    }

    private void initValues() {

        brightnessItem.setCurValue(TvPictureManager.getInstance().getVideoItem(TvPictureManager.PICTURE_BRIGHTNESS));
        contrastItem.setCurValue(TvPictureManager.getInstance().getVideoItem(TvPictureManager.PICTURE_CONTRAST));
        HueItem.setCurValue(TvPictureManager.getInstance().getVideoItem(TvPictureManager.PICTURE_HUE));
        SaturationItem.setCurValue(TvPictureManager.getInstance().getVideoItem(TvPictureManager.PICTURE_SATURATION));
        backlightItem.setCurValue(TvPictureManager.getInstance().getVideoItem(TvPictureManager.PICTURE_BACKLIGHT));
        sharpnessItem.setCurValue(TvPictureManager.getInstance().getVideoItem(TvPictureManager.PICTURE_SHARPNESS));

        int initValue = manager.getColorTempIdx().getValue();
        colorTempExData = manager.getWbGainOffsetEx(initValue, curSourceType.ordinal());
        RedItem.setCurValue(colorTempExData.redGain / COLORTEMP);
        GreenItem.setCurValue(colorTempExData.greenGain / COLORTEMP);
        BlueItem.setCurValue(colorTempExData.blueGain / COLORTEMP);

        itemZoomMode.setCurValue(getVideoArcType());

    }

    /**
     * getVideoArcType of mZoomArray postion
     * 
     * @param void
     * @return int mZoomArray of postion.
     */
    private int getVideoArcType() {
        int initValue = 0;
        /* zoom mode */
        EnumVideoArcType type = manager.getVideoArc();
        switch (type) {
            case E_16x9:
                initValue = 0;
                break;
            case E_4x3:
                initValue = 1;
                break;
            case E_AUTO:
                initValue = 2;
                break;
            case E_Panorama:
                initValue = 3;
                break;
            case E_DEFAULT:
                if (curSourceType == EnumInputSource.E_INPUT_SOURCE_ATV
                        || curSourceType == EnumInputSource.E_INPUT_SOURCE_DTV) {
                    initValue = 0;
                } else {
                    initValue = 4;
                }
                break;
            default:
                initValue = 0;
                break;
        }
        return initValue;
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

        mListView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.v(TAG, "key down");
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            final int position = mListView.getSelectedItemPosition();
                            items.get(position).onKeyDown(keyCode, event);
                            if (items.get(position).getItemType() == Constants.ITEM_ENUM) {
                                updateView(position);
                            } else if (items.get(position).getItemType() == Constants.ITEM_DIGITAL) {
                                updateView(position);
                            }
                            break;
                        default:
                            break;
                    }
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    Log.v(TAG, "key up");
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            final int position = mListView.getSelectedItemPosition();
                            if (items.get(position).getItemType() == Constants.ITEM_ENUM) {
                                addTvApiTask(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        doSetVideoArc(position, items.get(position).getCurValue());
                                    }
                                });
                            } else if (items.get(position).getItemType() == Constants.ITEM_DIGITAL) {
                                addTvApiTask(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        doInBackGround(position, items.get(position).getCurValue());
                                    }
                                });
                            }
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
                if (item.getItemType() == Constants.ITEM_ENUM) {
                    TextView content = (TextView) view.findViewById(R.id.content);
                    content.setText(item.getValues()[item.getCurValue()]);
                } else if (item.getItemType() == Constants.ITEM_DIGITAL) {
                    TextView content = (TextView) view.findViewById(R.id.content);
                    content.setText("" + item.getCurValue());
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

    public void doInBackGround(int position, int value) {
        switch (position) {
            case 0:
                if (TvPictureManager.getInstance().getPictureMode() != TvPictureManager.PICTURE_MODE_USER) {
                    TvPictureManager.getInstance().setPictureMode(TvPictureManager.PICTURE_MODE_USER);
                }
                TvPictureManager.getInstance().setVideoItem(TvPictureManager.PICTURE_BRIGHTNESS, value);
                break;
            case 1:
                if (TvPictureManager.getInstance().getPictureMode() != TvPictureManager.PICTURE_MODE_USER) {
                    TvPictureManager.getInstance().setPictureMode(TvPictureManager.PICTURE_MODE_USER);
                }
                TvPictureManager.getInstance().setVideoItem(TvPictureManager.PICTURE_CONTRAST, value);
                break;
            case 2:
                if (TvPictureManager.getInstance().getPictureMode() != TvPictureManager.PICTURE_MODE_USER) {
                    TvPictureManager.getInstance().setPictureMode(TvPictureManager.PICTURE_MODE_USER);
                }
                TvPictureManager.getInstance().setVideoItem(TvPictureManager.PICTURE_BACKLIGHT, value);
                break;
            case 3:
                if (TvPictureManager.getInstance().getPictureMode() != TvPictureManager.PICTURE_MODE_USER) {
                    TvPictureManager.getInstance().setPictureMode(TvPictureManager.PICTURE_MODE_USER);
                }
                TvPictureManager.getInstance().setVideoItem(TvPictureManager.PICTURE_SHARPNESS, value);
                break;
            case 4:
                if (TvPictureManager.getInstance().getPictureMode() != TvPictureManager.PICTURE_MODE_USER) {
                    TvPictureManager.getInstance().setPictureMode(TvPictureManager.PICTURE_MODE_USER);
                }
                TvPictureManager.getInstance().setVideoItem(TvPictureManager.PICTURE_HUE, value);
                break;
            case 5:
                if (TvPictureManager.getInstance().getPictureMode() != TvPictureManager.PICTURE_MODE_USER) {
                    TvPictureManager.getInstance().setPictureMode(TvPictureManager.PICTURE_MODE_USER);
                }
                TvPictureManager.getInstance().setVideoItem(TvPictureManager.PICTURE_SATURATION, value);
                break;
            case 6:
//                if (TvPictureManager.getInstance().getColorTempratureIdx() != TvPictureManager.COLOR_TEMP_USER1) {
//                    TvPictureManager.getInstance().setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
//                }
                colorTempExData.redGain = value * COLORTEMP;
                manager.setWbGainOffsetEx(colorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        curSourceType.ordinal());
                break;
            case 7:
//                if (TvPictureManager.getInstance().getColorTempratureIdx() != TvPictureManager.COLOR_TEMP_USER1) {
//                    TvPictureManager.getInstance().setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
//                }
                colorTempExData.greenGain = value * COLORTEMP;
                manager.setWbGainOffsetEx(colorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        curSourceType.ordinal());
                break;
            case 8:
//                if (TvPictureManager.getInstance().getColorTempratureIdx() != TvPictureManager.COLOR_TEMP_USER1) {
//                    TvPictureManager.getInstance().setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
//                }
                colorTempExData.blueGain = value * COLORTEMP;
                manager.setWbGainOffsetEx(colorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        curSourceType.ordinal());
                break;
            default:
                break;
        }

    }

    /**
     * Set video Arc. The enum oridinal value bigger than E_AR_DotByDot
     * including E_AR_Subtitle,
     * 
     * @param position of mZoomArray,value of set
     * @return boolean TRUE:Success, or FALSE:failed.
     */
    public void doSetVideoArc(int position, int value) {
        Log.d(TAG, "position:" + position + ",value:" + value);
        switch (value) {
            case 0:
                manager.setVideoArc(EnumVideoArcType.E_16x9);
                break;
            case 1:
                manager.setVideoArc(EnumVideoArcType.E_4x3);
                break;
            case 2:
                manager.setVideoArc(EnumVideoArcType.E_AUTO);
                break;
            case 3:
                manager.setVideoArc(EnumVideoArcType.E_Panorama);
                break;
            case 4:
                manager.setVideoArc(EnumVideoArcType.E_DEFAULT);
                break;
            default:
                break;
        }
    }

    private void addTvApiTask(Runnable runnable) {
        if (mTvApiThreadExecutor == null) {
            mTvApiThreadExecutor = Executors.newSingleThreadExecutor();
        }
        mTvApiThreadExecutor.execute(runnable);
    }

}
