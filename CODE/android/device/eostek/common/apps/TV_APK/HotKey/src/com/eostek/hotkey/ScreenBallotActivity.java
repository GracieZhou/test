
package com.eostek.hotkey;

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
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.eostek.hotkey.util.AnimatedSelector;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

public class ScreenBallotActivity extends Activity {

    private GridView interface_gv;

    private final static int UPDATEIMG = 0x01;

    private final static int TODIMISSUI = 0x02;

    private static final int TODIMISSDELAYTIME = 10 * 1000;

    private boolean isMoving = false;

    private MyHandler handler;

    private int[] screenBallot_img = {
            R.drawable.home, R.drawable.app, R.drawable.data, R.drawable.heran,
            R.drawable.web_browser
    };

    private InputAdapter adapter;

    private int curIndex = 0;

    private View focusView;

    private int sourceCount = 0;

    private AnimatedSelector animatedSelector;

    private boolean isDismiss = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_screenballot);
        sourceCount = screenBallot_img.length;
        interface_gv = (GridView) findViewById(R.id.screenballot__gv);
        View selector = findViewById(R.id.selector);
        animatedSelector = new AnimatedSelector(selector, interface_gv.getSelector());
        if (screenBallot_img.length == 5) {
            RelativeLayout.LayoutParams params = (LayoutParams) interface_gv.getLayoutParams();
            params.setMargins(getResources().getInteger(R.integer.screen_select_left_off), 0, 0, 0);
            interface_gv.setLayoutParams(params);
            animatedSelector.setLeftOffset(getResources().getInteger(R.integer.screen_select_left_off));
        }
        animatedSelector.setTopOffset(getResources().getInteger(
                R.integer.screenballot_selector_margintop));
        interface_gv.setSelector(animatedSelector);
        adapter = new InputAdapter();
        interface_gv.setAdapter(adapter);
        handler = new MyHandler();

        interface_gv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
                setImageSource(0, v);
            }
        });

        interface_gv.setOnItemSelectedListener(new OnItemSelectedListener() {
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

        interface_gv.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !interface_gv.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        animatedSelector.hideView();
                    }
                }
                return false;
            }
        });
        interface_gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                startActivityByIndex(position);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        handler.removeMessages(TODIMISSUI);
        handler.sendEmptyMessageDelayed(TODIMISSUI, TODIMISSDELAYTIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case MKeyEvent.KEYCODE_MSTAR_REVEAL:
                    ActivityManager activityManager = (ActivityManager)getSystemService(
                            Context.ACTIVITY_SERVICE);
                    List<RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
                    RunningTaskInfo currentActivity;
                    currentActivity = forGroundActivity.get(0);
                    String activityName = currentActivity.topActivity.getClassName();
                    if (activityName.equals("com.eostek.hotkey.ScreenBallotActivity")) {
                        Animation menu_Animout = AnimationUtils.loadAnimation(this, R.anim.eoshotkey_anim_out);
                        menu_Animout.setFillAfter(true);
                        findViewById(R.id.screenballot_rl).startAnimation(menu_Animout);
                        finish();
                        return true;
                    }
                    if (isDismiss) {
                        return true;
                    }
                    dismiss();
                    isDismiss = true;
                    return true;
                case KeyEvent.KEYCODE_TV_INPUT:
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    protected void startActivityByIndex(int position) {
        dismiss();
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra("gotolanucher", "home");
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra("gotolanucher", "app");
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra("gotolanucher", "media");
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra("gotolanucher", "heran");
                startActivity(intent);
                break;
            case 4:
                TvCommonManager.getInstance()
                        .setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
                intent = new Intent();
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        Animation menu_AnimIn = AnimationUtils.loadAnimation(this, R.anim.eoshotkey_anim_in);
        menu_AnimIn.setFillAfter(true);
        findViewById(R.id.screenballot_rl).startAnimation(menu_AnimIn);
        handler.sendEmptyMessageDelayed(TODIMISSUI, TODIMISSDELAYTIME);
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(TODIMISSUI);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                handler.removeMessages(TODIMISSUI);
                dismiss();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void dismiss() {
        Animation menu_Animout = AnimationUtils.loadAnimation(this, R.anim.eoshotkey_anim_out);
        menu_Animout.setFillAfter(true);
        findViewById(R.id.screenballot_rl).startAnimation(menu_Animout);
        handler.removeMessages(TODIMISSUI);
        handler.sendEmptyMessageDelayed(TODIMISSUI, 300);
    }

    private class InputAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sourceCount;
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
            LayoutInflater inflater = LayoutInflater.from(ScreenBallotActivity.this);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.eos_screenballot_item, null);
            }
            ImageView inputItem_img = (ImageView) convertView
                    .findViewById(R.id.screenballot_item_img);
            inputItem_img.setImageResource(screenBallot_img[position]);
            return convertView;
        }
    }

    /**
     * to update the image.
     * 
     * @param index
     * @param view
     */
    private void setImageSource(int index, View view) {
        ImageView img = (ImageView) view.findViewById(R.id.screenballot_item_img);
        ScaleAnimation inAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        inAnimation.setDuration(200);
        inAnimation.setFillAfter(true);
        img.startAnimation(inAnimation);
        switch (index) {
            case 0:
                img.setImageResource(R.drawable.home_selected);
                break;
            case 1:
                img.setImageResource(R.drawable.app_selected);
                break;
            case 2:
                img.setImageResource(R.drawable.data_selected);
                break;
            case 3:
                img.setImageResource(R.drawable.heran_selected);
                break;
            case 4:
                img.setImageResource(R.drawable.web_browser_selected);
                break;
            default:
                break;
        }
        ((ImageView) interface_gv.getChildAt(
                index - 1 > -1 ? (index - 1) % sourceCount : screenBallot_img.length - 1)
                .findViewById(R.id.screenballot_item_img))
                .setImageResource(screenBallot_img[index - 1 > -1 ? (index - 1) % sourceCount
                        : screenBallot_img.length - 1]);
        ((ImageView) interface_gv.getChildAt((index + 1) % sourceCount).findViewById(
                R.id.screenballot_item_img)).setImageResource(screenBallot_img[(index + 1)
                % sourceCount]);
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATEIMG:
                    setImageSource(curIndex, focusView);
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
}
