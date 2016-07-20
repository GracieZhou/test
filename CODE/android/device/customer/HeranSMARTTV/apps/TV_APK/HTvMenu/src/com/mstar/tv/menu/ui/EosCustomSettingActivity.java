
package com.mstar.tv.menu.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.android.MIntent;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.setting.AdvanceSettingFragment;
import com.mstar.tv.menu.setting.AudioSettingFragment;
import com.mstar.tv.menu.setting.ChannelManagerFragment;
import com.mstar.tv.menu.setting.NetWorkFragment;
import com.mstar.tv.menu.setting.PCImageAdjustFragment;
import com.mstar.tv.menu.setting.PictureSettingFragment;

/*
 * @projectName： EOSTVMenu
 * @moduleName： EosCustomSettingActivity.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time  2013-12-18
 * @Copyright © 2013 EOSTEK, Inc.
 */
@SuppressLint("ValidFragment")
public class EosCustomSettingActivity extends Activity {

    private final static String TAG = EosCustomSettingActivity.class.getSimpleName();

    private GridView titles_gv;

    private String[] dataList;

    private final static int UPDATEFRAGMENT = 0x01;

    private final static int DELAYUPDATEFRAGEMENT = 150;

    public static final int DELAYFINISH = 0x02;

    public static final int TODIMISSDELAYTIME = 10 * 1000;

    private PictureSettingFragment picture = new PictureSettingFragment();

    private AudioSettingFragment audio = new AudioSettingFragment();

    private ChannelManagerFragment channel = new ChannelManagerFragment();

    private PCImageAdjustFragment pcImage = new PCImageAdjustFragment();

    private NetWorkFragment netWork = new NetWorkFragment();

    private AdvanceSettingFragment advanceSetting = new AdvanceSettingFragment();

    private boolean isMoving = true;

    private int mLastPosition = -1;

    private int mCurPosition = 0;

    private TitlesAdapter adapter = null;

    private EnumInputSource curSource = null;

    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATEFRAGMENT:
                    if (EosCustomSettingActivity.this.isFinishing()) {
                        return;
                    }
                    updateFragment(msg.arg1);
                    break;
                case DELAYFINISH:
                    finish();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_menu);
        titles_gv = (GridView) findViewById(R.id.titles_gv);
        curSource = TvCommonManager.getInstance().getCurrentInputSource();
        if (curSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
            curSource = EnumInputSource.values()[queryCurInputSrc()];
            Log.v(TAG, "Source is storage,queryCurInputSrc ,curSource = " + curSource);
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titles_gv.getLayoutParams();
        if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV || curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
            dataList = getResources().getStringArray(R.array.menu_setting_title_tv);
            params.setMargins(10, 10, 10, 0);
        } else if (curSource == EnumInputSource.E_INPUT_SOURCE_VGA) {
            dataList = getResources().getStringArray(R.array.menu_setting_title_vga);
            params.setMargins(10, 10, 10, 0);
        } else {
            dataList = getResources().getStringArray(R.array.menu_setting_title);
            params.setMargins(30, 10, 30, 0);
        }
        titles_gv.setLayoutParams(params);
        titles_gv.setNumColumns(dataList.length);
        adapter = new TitlesAdapter();
        titles_gv.setAdapter(adapter);
        titles_gv.setSelection(0);
        setListener();

        if (getIntent().getBooleanExtra("gotoadvance", false)) {
            Toast.makeText(this, R.string.nohotkeytip, Toast.LENGTH_LONG).show();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_setting, advanceSetting);
            ft.commitAllowingStateLoss();
            titles_gv.setSelection(dataList.length - 1);
        } else {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_setting, picture);
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myHandler.sendEmptyMessageDelayed(DELAYFINISH, TODIMISSDELAYTIME);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        myHandler.removeMessages(DELAYFINISH);
        myHandler.sendEmptyMessageDelayed(DELAYFINISH, TODIMISSDELAYTIME);
        if (!isMoving && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MENU:
                    if (titles_gv.hasFocus()) {
                        finish();
                    } else {
                        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    }
                    return true;
                case KeyEvent.KEYCODE_TV_INPUT:
                    Intent intent = new Intent(MIntent.ACTION_TV_INPUT_BUTTON);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    if (findViewById(R.id.context_lst).hasFocus()
                            && ((ListView) findViewById(R.id.context_lst)).getSelectedItemPosition() == 0) {
                        titles_gv.requestFocus();
                    } else {
                        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                    }
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    if (!findViewById(R.id.context_lst).hasFocus()) {
                        findViewById(R.id.context_lst).requestFocus();
                    } else {
                        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                    }
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * update the fragment when press the key right or left.You do the different
     * animation.
     * 
     * @param position
     */
    private void updateFragment(int position) {
        isMoving = true;
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        FragmentTransaction ft = fm.beginTransaction();
        if (position > mLastPosition) {
            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
        } else {
            ft.setCustomAnimations(R.anim.eos_menu_anim_leftin, R.anim.eos_menu_anim_rightout);
        }
        mLastPosition = position;
        if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV || curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
            clickForTv(ft, position);
        } else if (curSource == EnumInputSource.E_INPUT_SOURCE_VGA) {
            clickForVGA(ft, position);
        } else {
            clickForOthers(ft, position);
        }
    }

    /**
     * to do the different action for tv. If the source is tv,we have the
     * channel manager.
     * 
     * @param ft
     * @param position
     */
    private void clickForTv(FragmentTransaction ft, int position) {
        switch (position) {
            case 0:
                ft.remove(picture);
                ft.replace(R.id.content_setting, picture);
                ft.commit();
                break;
            case 1:
                ft.remove(audio);
                ft.replace(R.id.content_setting, audio);
                ft.commit();
                break;
            case 2:
                ft.remove(channel);
                ft.replace(R.id.content_setting, channel);
                ft.commit();
                break;
            case 3: {
                ft.remove(netWork);
                ft.replace(R.id.content_setting, netWork);
                ft.commit();
                break;
            }
            case 4: {
                ft.remove(advanceSetting);
                ft.replace(R.id.content_setting, advanceSetting);
                ft.commit();
                break;
            }
            default:
                break;
        }

    }

    /**
     * to do the different action for vga. If the source is tv,we have the pc
     * image.
     * 
     * @param ft
     * @param position
     */
    private void clickForVGA(FragmentTransaction ft, int position) {
        switch (position) {
            case 0:
                ft.remove(picture);
                ft.replace(R.id.content_setting, picture);
                ft.commit();
                break;
            case 1:
                ft.remove(audio);
                ft.replace(R.id.content_setting, audio);
                ft.commit();
                break;
            case 2: {
                ft.remove(pcImage);
                ft.replace(R.id.content_setting, pcImage);
                ft.commit();
                break;
            }
            case 3: {
                ft.remove(netWork);
                ft.replace(R.id.content_setting, netWork);
                ft.commit();
                break;
            }
            case 4: {
                ft.remove(advanceSetting);
                ft.replace(R.id.content_setting, advanceSetting);
                ft.commit();
                break;
            }
            default:
                break;
        }

    }

    /**
     * the title doesn't have the channel Manager if the source isn't tv.
     * 
     * @param ft
     * @param position
     */
    private void clickForOthers(FragmentTransaction ft, int position) {
        switch (position) {
            case 0:
                ft.remove(picture);
                ft.replace(R.id.content_setting, picture);
                ft.commit();
                break;
            case 1:
                ft.remove(audio);
                ft.replace(R.id.content_setting, audio);
                ft.commit();
                break;
            case 2:
                ft.remove(netWork);
                ft.replace(R.id.content_setting, netWork);
                ft.commit();
                break;
            case 3: {
                ft.remove(advanceSetting);
                ft.replace(R.id.content_setting, advanceSetting);
                ft.commit();
                break;
            }
            default:
                break;
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                return true;
            case KeyEvent.KEYCODE_BACK:
                myHandler.removeMessages(DELAYFINISH);
                finish();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (titles_gv.getSelectedItemPosition() == dataList.length - 1) {
                    titles_gv.setSelection(0);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (titles_gv.getSelectedItemPosition() == 0) {
                    titles_gv.setSelection(dataList.length - 1);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class TitlesAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return dataList.length;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(EosCustomSettingActivity.this);
            if (view == null) {
                view = inflater.inflate(R.layout.eos_menu_title_item, null);
            }
            TextView txt = (TextView) view.findViewById(R.id.menuitem_title_txt);
            txt.setText(dataList[i]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
    }

    /**
     * to change the background when the focus change.
     * 
     * @param bShow
     */
    private void showSelector(boolean bShow) {
        if (bShow && titles_gv.hasFocus()) {
            ((ImageView) findViewById(R.id.selector)).setBackgroundResource(R.drawable.setbar_focus);
        } else {
            // if the title hasn't focus,we should change the background.
            ((ImageView) findViewById(R.id.selector)).setBackgroundResource(R.drawable.setting_title_select);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        showSelector(hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * to recovery the UI if the item isn't focus.
     */
    private void clearFocus() {
        // TextView tv = (TextView)
        // titles_gv.getChildAt(mCurPosition).findViewById(
        // R.id.menuitem_title_txt);
        // final ScaleAnimation outAnimation = new ScaleAnimation(1.2f, 1.0f,
        // 1.2f, 1.0f,
        // ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
        // ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        // tv.clearAnimation();
        // outAnimation.setDuration(300);
        // outAnimation.setFillAfter(true);
        // tv.startAnimation(outAnimation);
    }

    private void setListener() {
        titles_gv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
            }
        });

        titles_gv.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearFocus();
                mCurPosition = position;
                isMoving = false;
                myHandler.removeMessages(UPDATEFRAGMENT);
                Message msg = myHandler.obtainMessage();
                msg.arg1 = position;
                msg.what = UPDATEFRAGMENT;
                myHandler.sendMessageDelayed(msg, DELAYUPDATEFRAGEMENT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        titles_gv.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !titles_gv.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                }
                return false;
            }
        });
    }

    public Handler getHandler() {
        return myHandler;
    }

    /**
     * query the current input source
     * 
     * @return InputSourceType
     */
    public int queryCurInputSrc() {
        int value = 0;
        Cursor cursor = getContentResolver().query(Uri.parse("content://mstar.tv.usersetting/systemsetting"), null,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        if (cursor != null) {
            cursor.close();
        }
        return value;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (titles_gv != null) {
            titles_gv.removeAllViewsInLayout();
            titles_gv = null;
        }
        if (myHandler != null) {
            myHandler.removeMessages(UPDATEFRAGMENT);
            myHandler.removeMessages(DELAYFINISH);
            myHandler = null;
        }
        if (adapter != null) {
            adapter = null;
        }
    }
}
