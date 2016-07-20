
package com.eostek.hotkey;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.eostek.hotkey.util.AnimatedSelector;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.AudioManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

public class EosInputActivity extends Activity {
    private GridView input_gv;

    private final static int UPDATEIMG = 0x01;

    private final static int TODIMISSUI = 0x02;

    private static final int TODIMISSDELAYTIME = 10 * 1000;

    private boolean isMoving = false;

    private MyHandler handler;

    private AudioManager am = TvManager.getInstance().getAudioManager();

    private int[] mInputImgsHasTuner = {
            R.drawable.tv_signal_analog, R.drawable.tv_signal_digital, R.drawable.tv_signal_hdmi1,
            R.drawable.tv_signal_hdmi2, R.drawable.tv_signal_hdmi3, R.drawable.tv_signal_av,
            R.drawable.tv_signal_ypbpr, R.drawable.tv_signal_vga
    };

    private int[] mInputImgsNoTuner = {
            R.drawable.tv_signal_hdmi1, R.drawable.tv_signal_hdmi2, R.drawable.tv_signal_hdmi3,
            R.drawable.tv_signal_av, R.drawable.tv_signal_ypbpr, R.drawable.tv_signal_vga
    };

    private InputAdapter adapter;

    private int curIndex = 0;

    private View focusView;

    private boolean hasTuner = true;

    private int hasTunerSourceCount = 0;

    private int noTunerSourceCount = 0;

    private AnimatedSelector animatedSelector;

    private boolean isDismiss = false;

    private final static String FACTORYKEYCODES = String.valueOf(KeyEvent.KEYCODE_2)
            + String.valueOf(KeyEvent.KEYCODE_5) + String.valueOf(KeyEvent.KEYCODE_8)
            + String.valueOf(KeyEvent.KEYCODE_0);

    // Press source+NUM 5522 Reset operation done directly, respond to software
    // written into the preset value( reset factory.db)
    private final static String RESTORE_FCTY_CODES = String.valueOf(KeyEvent.KEYCODE_5)
            + String.valueOf(KeyEvent.KEYCODE_5) + String.valueOf(KeyEvent.KEYCODE_2)
            + String.valueOf(KeyEvent.KEYCODE_2);

    // Press source+NUM 5500 Reset operation done directly (Not reset
    // factory.db)
    private final static String RESTORE_CODES = String.valueOf(KeyEvent.KEYCODE_5) + String.valueOf(KeyEvent.KEYCODE_5)
            + String.valueOf(KeyEvent.KEYCODE_0) + String.valueOf(KeyEvent.KEYCODE_0);

    private final static String WHITE_BALANCE_CODES = String.valueOf(KeyEvent.KEYCODE_8)
            + String.valueOf(KeyEvent.KEYCODE_8) + String.valueOf(KeyEvent.KEYCODE_8)
            + String.valueOf(KeyEvent.KEYCODE_8);

    private ArrayList<Integer> keyQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_input);
        hasTunerSourceCount = mInputImgsHasTuner.length;
        noTunerSourceCount = mInputImgsNoTuner.length;
        input_gv = (GridView) findViewById(R.id.input_gv);

        try {
            hasTuner = TvManager.getInstance().getFactoryManager().getTunerStatus();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (hasTuner) {
            input_gv.setNumColumns(hasTunerSourceCount);
            getCurInputSourceImgHasTuner();
        } else {
            input_gv.setNumColumns(noTunerSourceCount);
            getCurInputSourceImgNoTuner();
        }

        View selector = findViewById(R.id.selector);
        animatedSelector = new AnimatedSelector(selector, input_gv.getSelector());
        animatedSelector.setLeftOffset(getResources().getInteger(R.integer.input_select_left_off));
        animatedSelector.setTopOffset(getResources().getInteger(R.integer.input_selector_margintop));
        input_gv.setSelector(animatedSelector);
        adapter = new InputAdapter();
        input_gv.setAdapter(adapter);
        input_gv.setSelection(curIndex);
        handler = new MyHandler();

        input_gv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
                if (hasTuner) {
                    setImageSourceHasTuner(0, v);
                } else {
                    setImageSourceNoTuner(0, v);
                }
            }
        });

        input_gv.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                animatedSelector.ensureViewVisible();
                curIndex = position;
                focusView = view;
                if (!isMoving) {
                    adapter.notifyDataSetChanged();
                }
                isMoving = true;
                handler.removeMessages(UPDATEIMG);
                handler.sendEmptyMessageDelayed(UPDATEIMG, 200);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                animatedSelector.hideView();
            }
        });

        input_gv.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !input_gv.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        animatedSelector.hideView();
                    }
                }
                return false;
            }
        });
        input_gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (hasTuner) {
                    setInputSourceHasTuner(position);
                } else {
                    setInputSourceNoTuner(position);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Animation menu_AnimIn = AnimationUtils.loadAnimation(this, R.anim.eoshotkey_anim_in);
        menu_AnimIn.setFillAfter(true);
        findViewById(R.id.input_rl).startAnimation(menu_AnimIn);
        handler.sendEmptyMessageDelayed(TODIMISSUI, TODIMISSDELAYTIME);
        super.onResume();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        handler.removeMessages(TODIMISSUI);
        handler.sendEmptyMessageDelayed(TODIMISSUI, TODIMISSDELAYTIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
            // case KeyEvent.KEYCODE_TV_INPUT:
            /*
             * ActivityManager activityManager = (ActivityManager)
             * getSystemService(Context.ACTIVITY_SERVICE); List<RunningTaskInfo>
             * forGroundActivity = activityManager.getRunningTasks(1);
             * RunningTaskInfo currentActivity; currentActivity =
             * forGroundActivity.get(0); String activityName =
             * currentActivity.topActivity.getClassName(); if
             * (activityName.equals("com.eostek.hotkey.EosInputActivity")) {
             * Animation menu_Animout = AnimationUtils.loadAnimation(this,
             * R.anim.eoshotkey_anim_out); menu_Animout.setFillAfter(true);
             * findViewById(R.id.input_rl).startAnimation(menu_Animout);
             * handler.removeMessages(TODIMISSUI);
             * setInputSource(input_gv.getSelectedItemPosition()); finish();
             * return true; } if (isDismiss) { return true; }
             * input_gv.performItemClick(input_gv.getSelectedView(),
             * input_gv.getSelectedItemPosition(), input_gv
             * .getSelectedView().getId()); dismiss(); isDismiss = true;
             */
            // return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    input_gv.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    input_gv.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    input_gv.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    input_gv.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                    return true;
                case MKeyEvent.KEYCODE_MSTAR_REVEAL:
                    return true;
                case KeyEvent.KEYCODE_MENU:
                case KeyEvent.KEYCODE_TV_INPUT:
                    // input_gv.dispatchKeyEvent(new
                    // KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    if (hasTuner) {
                        setInputSourceHasTuner(curIndex);
                    } else {
                        setInputSourceNoTuner(curIndex);
                    }

                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
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
            if (keystr.equals(FACTORYKEYCODES)) {
                keyQueue.clear();
                intent = new Intent("mstar.tvsetting.factory.intent.action.MainmenuActivity");
                startActivity(intent);
                handler.removeMessages(TODIMISSUI);
                finish();
                return true;
            } else if (keystr.equals(RESTORE_FCTY_CODES)) {
                keyQueue.clear();
                intent = new Intent();
                intent.setClassName("com.mstar.tv.menu",
                        "com.mstar.tv.menu.setting.restore.SystemRestoreFactoryActivity");
                intent.putExtra("isResetFactoryDB", true);
                startActivity(intent);
                handler.removeMessages(TODIMISSUI);
                finish();
                return true;
            } else if (keystr.equals(RESTORE_CODES)) {
                keyQueue.clear();
                intent = new Intent();
                intent.setClassName("com.mstar.tv.menu",
                        "com.mstar.tv.menu.setting.restore.SystemRestoreFactoryActivity");
                intent.putExtra("isResetFactoryDB", false);
                startActivity(intent);
                handler.removeMessages(TODIMISSUI);
                finish();
                return true;
            } else if (keystr.equals(WHITE_BALANCE_CODES)) {
                keyQueue.clear();
                intent = new Intent();
                intent.setClassName("org.wb.app", "org.wb.app.SurfaceViewPlayVideo");
                startActivity(intent);
                handler.removeMessages(TODIMISSUI);
                finish();
                return true;
            } else {
                keyQueue.remove(0);
            }
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                handler.removeMessages(TODIMISSUI);
                dismiss();
            default:
                break;
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

    private void dismiss() {
        Animation menu_Animout = AnimationUtils.loadAnimation(this, R.anim.eoshotkey_anim_out);
        menu_Animout.setFillAfter(true);
        findViewById(R.id.input_rl).startAnimation(menu_Animout);
        handler.removeMessages(TODIMISSUI);
        handler.sendEmptyMessageDelayed(TODIMISSUI, 300);
    }

    private class InputAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (hasTuner) {
                return hasTunerSourceCount;
            } else {
                return noTunerSourceCount;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(EosInputActivity.this);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.eos_input_item, null);
            }
            ImageView inputItem_img = (ImageView) convertView.findViewById(R.id.inputitem_img);
            if (hasTuner) {
                inputItem_img.setImageResource(mInputImgsHasTuner[position]);
            } else {
                inputItem_img.setImageResource(mInputImgsNoTuner[position]);
            }
            return convertView;
        }
    }

    /**
     * to init the image array when show the ui.
     */
    private void getCurInputSourceImgHasTuner() {
        int curSourceId = TvCommonManager.getInstance().getCurrentTvInputSource();
        EnumInputSource curSource = EnumInputSource.values()[curSourceId];
        switch (curSource) {
            case E_INPUT_SOURCE_ATV:
                mInputImgsHasTuner[0] = R.drawable.tv_signal_analog_selected;
                curIndex = 0;
                break;
            case E_INPUT_SOURCE_DTV:
                mInputImgsHasTuner[1] = R.drawable.tv_signal_digital_selected;
                curIndex = 1;
                break;
            case E_INPUT_SOURCE_HDMI:
                mInputImgsHasTuner[2] = R.drawable.tv_signal_hdmi1_selected;
                curIndex = 2;
                break;
            case E_INPUT_SOURCE_HDMI2:
                mInputImgsHasTuner[3] = R.drawable.tv_signal_hdmi2_selected;
                curIndex = 3;
                break;
            case E_INPUT_SOURCE_HDMI3:
                mInputImgsHasTuner[4] = R.drawable.tv_signal_hdmi3_selected;
                curIndex = 4;
                break;
            case E_INPUT_SOURCE_CVBS:
                mInputImgsHasTuner[5] = R.drawable.tv_signal_av_selected;
                curIndex = 5;
                break;
            case E_INPUT_SOURCE_YPBPR:
                mInputImgsHasTuner[6] = R.drawable.tv_signal_ypbpr_selected;
                curIndex = 6;
                break;
            case E_INPUT_SOURCE_VGA:
                mInputImgsHasTuner[7] = R.drawable.tv_signal_vga_selected;
                curIndex = 7;
                break;
            default:
                break;
        }
    }

    private void getCurInputSourceImgNoTuner() {
        int curSourceId = TvCommonManager.getInstance().getCurrentTvInputSource();
        EnumInputSource curSource = EnumInputSource.values()[curSourceId];
        switch (curSource) {
            case E_INPUT_SOURCE_HDMI:
                mInputImgsNoTuner[0] = R.drawable.tv_signal_hdmi1_selected;
                curIndex = 0;
                break;
            case E_INPUT_SOURCE_HDMI2:
                mInputImgsNoTuner[1] = R.drawable.tv_signal_hdmi2_selected;
                curIndex = 1;
                break;
            case E_INPUT_SOURCE_HDMI3:
                mInputImgsNoTuner[2] = R.drawable.tv_signal_hdmi3_selected;
                curIndex = 2;
                break;
            case E_INPUT_SOURCE_CVBS:
                mInputImgsNoTuner[3] = R.drawable.tv_signal_av_selected;
                curIndex = 3;
                break;
            case E_INPUT_SOURCE_YPBPR:
                mInputImgsNoTuner[4] = R.drawable.tv_signal_ypbpr_selected;
                curIndex = 4;
                break;
            case E_INPUT_SOURCE_VGA:
                mInputImgsNoTuner[5] = R.drawable.tv_signal_vga_selected;
                curIndex = 5;
                break;
            default:
                break;
        }
    }

    /**
     * to update the image.
     * 
     * @param index
     * @param view
     */
    private void setImageSourceHasTuner(int index, View view) {
        ImageView img = (ImageView) view.findViewById(R.id.inputitem_img);
        ScaleAnimation inAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        inAnimation.setDuration(200);
        inAnimation.setFillAfter(true);
        img.startAnimation(inAnimation);
        switch (index) {
            case 0:
                img.setImageResource(R.drawable.tv_signal_analog_focus);
                break;
            case 1:
                img.setImageResource(R.drawable.tv_signal_digital_focus);
                break;
            case 2:
                img.setImageResource(R.drawable.tv_signal_hdmi1_focus);
                break;
            case 3:
                img.setImageResource(R.drawable.tv_signal_hdmi2_focus);
                break;
            case 4:
                img.setImageResource(R.drawable.tv_signal_hdmi3_focus);
                break;
            case 5:
                img.setImageResource(R.drawable.tv_signal_av_focus);
                break;
            case 6:
                img.setImageResource(R.drawable.tv_signal_ypbpr_focus);
                break;
            case 7:
                img.setImageResource(R.drawable.tv_signal_vga_focus);
                break;
            default:
                break;
        }
    }

    private void setImageSourceNoTuner(int index, View view) {
        ImageView img = (ImageView) view.findViewById(R.id.inputitem_img);
        ScaleAnimation inAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        inAnimation.setDuration(200);
        inAnimation.setFillAfter(true);
        img.startAnimation(inAnimation);
        switch (index) {
            case 0:
                img.setImageResource(R.drawable.tv_signal_hdmi1_focus);
                break;
            case 1:
                img.setImageResource(R.drawable.tv_signal_hdmi2_focus);
                break;
            case 2:
                img.setImageResource(R.drawable.tv_signal_hdmi3_focus);
                break;
            case 3:
                img.setImageResource(R.drawable.tv_signal_av_focus);
                break;
            case 4:
                img.setImageResource(R.drawable.tv_signal_ypbpr_focus);
                break;
            case 5:
                img.setImageResource(R.drawable.tv_signal_vga_focus);
                break;
            default:
                break;
        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATEIMG:
                    if (hasTuner) {
                        setImageSourceHasTuner(curIndex, focusView);
                    } else {
                        setImageSourceNoTuner(curIndex, focusView);
                    }
                    isMoving = false;
                    break;
                case TODIMISSUI:
                    finish();
                    isDismiss = false;
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void showSelector(boolean bShow) {
        if (animatedSelector == null)
            return;
        if (bShow) {
            animatedSelector.ensureViewVisible();
        } else {
            animatedSelector.hideView();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        showSelector(hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    private void setInputSourceHasTuner(int position) {
        dismiss();
        boolean isSourceChange = false;
        boolean isChangeChannel = false;
        EnumInputSource curSource = TvCommonManager.getInstance().getCurrentInputSource();
        switch (position) {
            case 0:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_ATV) {
                    isSourceChange = true;
                    isChangeChannel = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_ATV);
                }
                break;
            case 1:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_DTV) {
                    isSourceChange = true;
                    isChangeChannel = true;
                    try {
                        am.enableMute(EnumMuteType.E_MUTE_PERMANENT);
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_DTV);
                    handlertv.postDelayed(mute_thread, 500);
                }
                break;
            case 2:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_HDMI) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_HDMI);
                }
                break;
            case 3:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_HDMI2) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_HDMI2);
                }
                break;
            case 4:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_HDMI3) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_HDMI3);
                }
                break;
            case 5:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_CVBS) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_CVBS);
                }
                break;
            case 6:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_YPBPR) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_YPBPR);
                }
                break;
            case 7:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_VGA) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_VGA);
                    break;
                }
                break;
            default:
                break;
        }
        int isLanucher = 0;
        try {
            isLanucher = System.getInt(getContentResolver(), "tvapp");
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (isLanucher == 1) {
            Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
            mHomeIntent.addCategory(Intent.CATEGORY_HOME);
            mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            mHomeIntent.putExtra("isSourceChange", isSourceChange);
            mHomeIntent.putExtra("isChangeChannel", isChangeChannel);
            startActivity(mHomeIntent);
        } else if (curSource == EnumInputSource.E_INPUT_SOURCE_STORAGE || (isLanucher == 2)) {
            Intent localIntent = new Intent();
            localIntent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
            startActivity(localIntent);
        }else{
	    Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
            mHomeIntent.addCategory(Intent.CATEGORY_HOME);
            mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            mHomeIntent.putExtra("isSourceChange", isSourceChange);
            mHomeIntent.putExtra("isChangeChannel", isChangeChannel);
            startActivity(mHomeIntent);
	}
        finish();
    }

    private void setInputSourceNoTuner(int position) {
        dismiss();
        boolean isSourceChange = false;
        boolean isChangeChannel = false;
        EnumInputSource curSource = TvCommonManager.getInstance().getCurrentInputSource();
        switch (position) {
            case 0:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_HDMI) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_HDMI);
                }
                break;
            case 1:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_HDMI2) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_HDMI2);
                }
                break;
            case 2:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_HDMI3) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_HDMI3);
                }
                break;
            case 3:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_CVBS) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_CVBS);
                }
                break;
            case 4:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_YPBPR) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_YPBPR);
                }
                break;
            case 5:
                if (curSource != EnumInputSource.E_INPUT_SOURCE_VGA) {
                    isSourceChange = true;
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_VGA);
                    break;
                }
                break;
            default:
                break;
        }
        int isLanucher = 0;
        try {
            isLanucher = System.getInt(getContentResolver(), "tvapp");
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (isLanucher == 1) {
            Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
            mHomeIntent.addCategory(Intent.CATEGORY_HOME);
            mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            mHomeIntent.putExtra("isSourceChange", isSourceChange);
            mHomeIntent.putExtra("isChangeChannel", isChangeChannel);
            startActivity(mHomeIntent);
        } else if (curSource == EnumInputSource.E_INPUT_SOURCE_STORAGE || (isLanucher == 2)) {
            Intent localIntent = new Intent();
            localIntent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
            startActivity(localIntent);
        }
        finish();
    }

    Handler handlertv = new Handler();

    Runnable mute_thread = new Runnable() {
        @Override
        public void run() {
            try {
                if (am != null) {
                    am.disableMute(EnumMuteType.E_MUTE_PERMANENT);
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            handlertv.removeCallbacks(mute_thread);
        }
    };
}
