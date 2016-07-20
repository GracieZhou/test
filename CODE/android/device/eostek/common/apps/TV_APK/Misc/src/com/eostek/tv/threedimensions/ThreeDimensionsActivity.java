
package com.eostek.tv.threedimensions;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvS3DManager;
import com.mstar.android.tvapi.common.ThreeDimensionManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.Enum3dType;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideo3DTo2D;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideoDisplayFormat;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideoLrViewSwitch;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideoSelfAdaptiveDetect;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

public class ThreeDimensionsActivity extends Activity {

    private static final int LOAD_DATA_TO_UI = 111;

    private static final String TAG = "ThreeDimensionsActivity";

    private ListView mLst;

    private TextView title;

    protected int mSelfDetect = 0;

    protected int mSelfConversion = 0;

    protected int mSelf3Dto2D = 0;

    protected short m3DDepth = 0;

    protected short m3DOffset = 0;

    protected int mSelfLRSwitch = 0;

    protected Thread mInitialThread = null;

    private MyHandler myHandler = null;

    private TvS3DManager mTvS3DManager;

    private TvCommonManager mTvCommonManager;

    private ThreeDimensionManager mThreeDimensionManager;

    private ThreeDimensionsAdapter mAdapter;

    private String[] mValues;

    private boolean[] mClickable;

    private final static String GOODKEYCODES = String.valueOf(KeyEvent.KEYCODE_2)
            + String.valueOf(KeyEvent.KEYCODE_5) + String.valueOf(KeyEvent.KEYCODE_8)
            + String.valueOf(KeyEvent.KEYCODE_0);

    private final static String FACTORYEYCODES = String.valueOf(KeyEvent.KEYCODE_1)
            + String.valueOf(KeyEvent.KEYCODE_9) + String.valueOf(KeyEvent.KEYCODE_7)
            + String.valueOf(KeyEvent.KEYCODE_9);

    private final static String SPIDFCODES = String.valueOf(KeyEvent.KEYCODE_1)
            + String.valueOf(KeyEvent.KEYCODE_2) + String.valueOf(KeyEvent.KEYCODE_3)
            + String.valueOf(KeyEvent.KEYCODE_4);

    private ArrayList<Integer> keyQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_dimensions);
        mTvS3DManager = TvS3DManager.getInstance();
        mTvCommonManager = TvCommonManager.getInstance();
        mThreeDimensionManager = TvManager.getInstance().getThreeDimensionManager();
        if (mInitialThread == null) {
            mInitialThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateData();
                    mInitialThread = null;
                }
            });
            mInitialThread.start();
        }
        mClickable = new boolean[6];
        for (int i = 0; i < 6; i++)
            mClickable[i] = true;
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadDataToUI();
        setListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.three_dimensions, menu);
        return true;
    }

    public void updateData() {
        mSelfDetect = mTvS3DManager.getSelfAdaptiveDetect().ordinal();
        mSelfConversion = mTvS3DManager.getDisplayFormat().ordinal();
        mSelf3Dto2D = mTvS3DManager.getDisplay3DTo2DMode().ordinal();
        m3DDepth = (short) mTvS3DManager.get3DDepthMode();
        m3DOffset = (short) mTvS3DManager.get3DOffsetMode();
        mSelfLRSwitch = mTvS3DManager.getLrViewSwitch().ordinal();
    }

    private void findViews() {
        setFocusableFor3DConversion();
        setFocusableFor3DDepthandOffset();
        mLst = (ListView) findViewById(R.id.menu_lst);
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(R.string.title_3d);
        myHandler = new MyHandler();
        myHandler.sendEmptyMessage(LOAD_DATA_TO_UI);
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == LOAD_DATA_TO_UI) {
                LoadDataToUI();
            }
            super.handleMessage(msg);
        }
    }

    private boolean isHDMIIn(EnumInputSource input) {
        switch (input) {
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4:
                return true;
        }
        return false;
    }

    private void LoadDataToUI() {
        mValues = new String[6];
        mValues[0] = getResources().getStringArray(R.array.str_arr_3d_detect_vals)[mSelfDetect];
        mValues[1] = getResources().getStringArray(R.array.str_arr_3d_conversion_vals)[mSelfConversion];
        mValues[2] = getResources().getStringArray(R.array.str_arr_3d_3dto2d_vals)[mSelf3Dto2D];
        mValues[3] = Integer.toString(m3DDepth);
        mValues[4] = Integer.toString(m3DOffset);
        mValues[5] = getResources().getStringArray(R.array.str_arr_3d_3dofset_vals)[mSelfLRSwitch];

        setFocusableFor3DConversion();
        setFocusableFor3DDepthandOffset();

        mAdapter = new ThreeDimensionsAdapter(getResources().getStringArray(R.array.item_names),
                mValues, mClickable, this);
        mLst.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyQueue == null) {
            keyQueue = new ArrayList<Integer>();
        }

        keyQueue.add(keyCode);
        if (keyQueue.size() == 4) {
            String keystr = intArrayListToString(keyQueue);
            Intent intent = null;
            if (keystr.equals(GOODKEYCODES)) {
                keyQueue.clear();
                intent = new Intent("mstar.tvsetting.factory.intent.action.MainmenuActivity");
                startActivity(intent);
                finish();
                return true;
            } else if (keystr.equals(FACTORYEYCODES)) {
                keyQueue.clear();
                intent = new Intent("mstar.tvsetting.factory.intent.action.FactorymenuActivity");
                startActivity(intent);
                finish();
                return true;
            } else if (keystr.equals(SPIDFCODES)) {
                keyQueue.clear();
                intent = new Intent(this, SpdifOutPutActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else {
                keyQueue.remove(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setListener() {
        OnKeyListener listener = new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {
                int currentposition = mLst.getSelectedItemPosition();
                if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    switch (mLst.getSelectedItemPosition()) {
                        case 0:
                            mSelfDetect = (mSelfDetect + 1) % 3;
                            mTvS3DManager.setSelfAdaptiveDetect(EnumThreeDVideoSelfAdaptiveDetect
                                    .values()[mSelfDetect]);
                            break;
                        case 1:
                            mSelfConversion = (mSelfConversion + 1) % 10;
                            if (mSelfConversion == EnumThreeDVideoDisplayFormat.E_ThreeD_Video_DISPLAYFORMAT_AUTO
                                    .ordinal()
                                    && isHDMIIn(mTvCommonManager.getCurrentInputSource()))
                                mSelfConversion++;

                            mTvS3DManager
                                    .setDisplayFormat(EnumThreeDVideoDisplayFormat.values()[mSelfConversion]);
                            currentposition = 1;
                            break;
                        case 2:
                            mSelf3Dto2D = (mSelf3Dto2D + 1) % 6;
                            mTvS3DManager.set3DTo2D(EnumThreeDVideo3DTo2D.values()[mSelf3Dto2D]);
                            currentposition = 2;
                            break;
                        case 3:
                            if (m3DDepth < 31)
                                m3DDepth++;
                            mTvS3DManager.set3DDepthMode(m3DDepth);
                            currentposition = 3;
                            break;
                        case 4:
                            if (m3DOffset < 31)
                                m3DOffset++;
                            mTvS3DManager.set3DOffsetMode(m3DOffset);
                            currentposition = 4;
                            break;
                        case 5:
                            mSelfLRSwitch = (mSelfLRSwitch + 1) % 2;
                            mTvS3DManager
                                    .setLrViewSwitch(EnumThreeDVideoLrViewSwitch.values()[mSelfLRSwitch]);
                            currentposition = 5;
                            break;
                    }
                    LoadDataToUI();
                    mLst.setSelection(currentposition);
                    Log.i(TAG, "set over");
                    return true;
                } else if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    switch (mLst.getSelectedItemPosition()) {
                        case 0:
                            if (mSelfDetect == 0) {
                                mSelfDetect = 2;
                            } else {
                                mSelfDetect = (mSelfDetect - 1) % 3;
                            }
                            mTvS3DManager.setSelfAdaptiveDetect(EnumThreeDVideoSelfAdaptiveDetect
                                    .values()[mSelfDetect]);
                            break;
                        case 1:
                            if (mSelfConversion == 0) {
                                mSelfConversion = 9;
                            } else {
                                mSelfConversion = (mSelfConversion - 1) % 10;
                            }

                            if (mSelfConversion == EnumThreeDVideoDisplayFormat.E_ThreeD_Video_DISPLAYFORMAT_AUTO
                                    .ordinal()
                                    && isHDMIIn(mTvCommonManager.getCurrentInputSource()))
                                mSelfConversion--;

                            mTvS3DManager
                                    .setDisplayFormat(EnumThreeDVideoDisplayFormat.values()[mSelfConversion]);
                            currentposition = 1;
                            break;
                        case 2:
                            if (mSelf3Dto2D == 0) {
                                mSelf3Dto2D = 5;
                            } else {
                                mSelf3Dto2D = (mSelf3Dto2D - 1) % 6;
                            }
                            mTvS3DManager.set3DTo2D(EnumThreeDVideo3DTo2D.values()[mSelf3Dto2D]);
                            currentposition = 2;
                            break;
                        case 3:
                            if (m3DDepth > 1)
                                m3DDepth--;
                            mTvS3DManager.set3DDepthMode(m3DDepth);
                            currentposition = 3;
                            break;
                        case 4:
                            if (m3DOffset > 1)
                                m3DOffset--;
                            mTvS3DManager.set3DOffsetMode(m3DOffset);
                            currentposition = 4;
                            break;
                        case 5:
                            if (mSelfLRSwitch == 0) {
                                mSelfLRSwitch = 1;
                            } else {
                                mSelfLRSwitch = (mSelfLRSwitch + 1) % 2;
                            }
                            mTvS3DManager
                                    .setLrViewSwitch(EnumThreeDVideoLrViewSwitch.values()[mSelfLRSwitch]);
                            currentposition = 5;
                            break;
                    }
                    LoadDataToUI();
                    mLst.setSelection(currentposition);
                    Log.i(TAG, "set over");
                    return true;
                } else if ((keyCode == KeyEvent.KEYCODE_DPAD_UP)
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    Log.i(TAG, "up:" + currentposition);
                    if (currentposition <= 0) {
                        currentposition = 5;
                    } else {
                        currentposition--;
                    }
                    for (int i = 0; i < 6; i++) {
                        if (mClickable[currentposition]) {
                            break;
                        } else {
                            currentposition = (currentposition - 1 + mClickable.length)
                                    % mClickable.length;
                        }
                    }
                    mLst.setSelection(currentposition);
                    return true;
                } else if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    Log.i(TAG, "down:" + currentposition);
                    if (currentposition >= 5) {
                        currentposition = 0;
                    } else {
                        currentposition++;
                    }
                    for (int i = 0; i < 6; i++) {
                        if (mClickable[currentposition]) {
                            break;
                        } else {
                            currentposition = (currentposition + 1) % mClickable.length;
                        }
                    }
                    mLst.setSelection(currentposition);
                    return true;
                }
                return false;
            }
        };
        mLst.setOnKeyListener(listener);
    }

    private void setFocusableFor3DDepthandOffset() {
        if (mTvS3DManager.getDisplayFormat() == EnumThreeDVideoDisplayFormat.E_ThreeD_Video_DISPLAYFORMAT_NONE) {
            mClickable[3] = false;
            mClickable[4] = false;
        } else {
            if (mTvS3DManager.getDisplayFormat() == EnumThreeDVideoDisplayFormat.E_ThreeD_Video_DISPLAYFORMAT_2DTO3D) {
                mClickable[3] = true;
                mClickable[4] = true;
            } else {
                mClickable[3] = true;
                mClickable[4] = false;
            }
        }
    }

    private void setFocusableFor3DConversion() {
        try {
            if (mTvCommonManager.getCurrentInputSource() == EnumInputSource.E_INPUT_SOURCE_STORAGE
                    && mThreeDimensionManager.getCurrent3dFormat() == Enum3dType.EN_3D_FRAME_PACKING_720P) {
                mClickable[0] = false;
                mClickable[1] = false;
            } else if (mTvS3DManager.getSelfAdaptiveDetect() != EnumThreeDVideoSelfAdaptiveDetect.E_ThreeD_Video_SELF_ADAPTIVE_DETECT_OFF) {
                mClickable[1] = false;
            } else {
                mClickable[0] = true;
                mClickable[1] = true;
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    private String intArrayListToString(ArrayList<Integer> al) {
        String str = "";
        for (int i = 0; i < al.size(); ++i) {
            str += al.get(i).toString();
        }
        return str;
    }
}
