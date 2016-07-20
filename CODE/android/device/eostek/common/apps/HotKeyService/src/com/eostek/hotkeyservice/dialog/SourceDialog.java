package com.eostek.hotkeyservice.dialog;

import java.util.ArrayList;
import java.util.List;

import com.eostek.hotkeyservice.HotKeyService;
import com.eostek.hotkeyservice.R;
import com.eostek.hotkeyservice.util.AnimatedSelector;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvCommonManager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class SourceDialog extends Dialog{
    
    HotKeyService mContext;

    public SourceDialog(HotKeyService context, int theme) {
        super(context, theme);
        mContext = context;
    }
    
    private final static int MSG_UPDATE_IMG = 0x01;

    private final static int MSG_TODIMISS_UI = 0x02;

    private static final int MSG_TODIMISS_DELAY_TIME = 10 * 1000;

    private final int[] mInputImgs = {
            R.drawable.tv_signal_analog, R.drawable.tv_signal_digital, R.drawable.tv_signal_hdmi1,
            R.drawable.tv_signal_hdmi2, R.drawable.tv_signal_hdmi3, R.drawable.tv_signal_av,
            R.drawable.tv_signal_ypbpr, R.drawable.tv_signal_vga
    };

    private GridView mSourceGridView;

    private View mCurFocusView;

    private AnimatedSelector mAnimatedSelector;

    private InputAdapter mInputAdapter;

    private boolean mIsMoving = false;

    private int mCurIndex = 0;

    private MyHandler mHandler = new MyHandler();

    private final static String GOODKEYCODES = String.valueOf(KeyEvent.KEYCODE_2) + String.valueOf(KeyEvent.KEYCODE_5)
            + String.valueOf(KeyEvent.KEYCODE_8) + String.valueOf(KeyEvent.KEYCODE_0);

    private ArrayList<Integer> keyQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.eos_input);
        
        getWindow().setGravity(Gravity.BOTTOM);

        mSourceGridView = (GridView) findViewById(R.id.input_gv);
        mInputAdapter = new InputAdapter();
        mSourceGridView.setAdapter(mInputAdapter);
        getCurInputSourceImg();
        mSourceGridView.setSelection(mCurIndex);

        View selector = findViewById(R.id.selector);
        mAnimatedSelector = new AnimatedSelector(selector, mSourceGridView.getSelector());
        mAnimatedSelector.setLeftOffset(mContext.getResources().getInteger(R.integer.input_select_left_off));
        mAnimatedSelector.setTopOffset(mContext.getResources().getInteger(R.integer.input_selector_margintop));
        mSourceGridView.setSelector(mAnimatedSelector);

        setListener();
        
        showInOutAnimation(true);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        showInOutAnimation(true);
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showSelector(hasFocus);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        dismiss();
//    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mHandler.removeMessages(MSG_TODIMISS_UI);
        mHandler.sendEmptyMessageDelayed(MSG_TODIMISS_UI, MSG_TODIMISS_DELAY_TIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_TV_INPUT:
                    ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                    List<RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
                    RunningTaskInfo currentActivity;
                    currentActivity = forGroundActivity.get(0);
//                    String activityName = currentActivity.topActivity.getClassName();
//                    if (activityName.equals(EosInputActivity.class.getName())) {
//                        showAnimation(findViewById(R.id.input_rl), R.anim.eoshotkey_anim_out);
//                        mHandler.removeMessages(MSG_TODIMISS_UI);
//                        dismiss();
//                        return true;
//                    }
                case KeyEvent.KEYCODE_VOLUME_UP:
                    mSourceGridView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    mSourceGridView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    mSourceGridView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    mSourceGridView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                    return true;
                case MKeyEvent.KEYCODE_MSTAR_REVEAL:
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
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
                intent = new Intent("mstar..factory.intent.action.MainmenuActivity");
                mContext.startActivity(intent);
                mHandler.removeMessages(MSG_TODIMISS_UI);
                dismiss();
                return true;
            } else {
                keyQueue.remove(0);
            }
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                mHandler.removeMessages(MSG_TODIMISS_UI);
                showInOutAnimation(false);
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setListener() {
        mSourceGridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
                setImageSource(0, v);
            }
        });

        mSourceGridView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAnimatedSelector.ensureViewVisible();
                mCurIndex = position;
                mCurFocusView = view;
                if (!mIsMoving) {
                    mInputAdapter.notifyDataSetChanged();
                }
                mIsMoving = true;
                mHandler.removeMessages(MSG_UPDATE_IMG);
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMG,300);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAnimatedSelector.hideView();
            }
        });

        mSourceGridView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !mSourceGridView.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        mAnimatedSelector.hideView();
                    }
                }
                return false;
            }
        });

        mSourceGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                setInputSource(position);
            }
        });
    }

    private String intArrayListToString(ArrayList<Integer> al) {
        String str = "";
        for (int i = 0; i < al.size(); ++i) {
            str += al.get(i).toString();
        }
        return str;
    }

    private void showInOutAnimation(boolean isInAnimation) {
        View view = findViewById(R.id.input_rl);
        if (isInAnimation) {
            showAnimation(view, R.anim.eoshotkey_anim_in);
            mHandler.sendEmptyMessageDelayed(MSG_TODIMISS_UI, MSG_TODIMISS_DELAY_TIME);
        } else {
            showAnimation(view, R.anim.eoshotkey_anim_out);
            mHandler.removeMessages(MSG_TODIMISS_UI);
            mHandler.sendEmptyMessageDelayed(MSG_TODIMISS_UI, 300);
        }
    }

    private void showAnimation(View view, int id) {
        Animation animation = AnimationUtils.loadAnimation(mContext, id);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    private class InputAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mInputImgs.length;
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
            LayoutInflater inflater = LayoutInflater.from(mContext);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.eos_input_item, null);
            }
            ImageView inputItem_img = (ImageView) convertView.findViewById(R.id.inputitem_img);
            inputItem_img.setImageResource(mInputImgs[position]);
            return convertView;
        }
    }

    /**
     * to init the image array when show the ui.
     */
    private void getCurInputSourceImg() {
        int curSourceId = TvCommonManager.getInstance().getCurrentTvInputSource();
        switch (curSourceId) {
            case TvCommonManager.INPUT_SOURCE_ATV:
                mInputImgs[0] = R.drawable.tv_signal_analog_selected;
                mCurIndex = 0;
                break;
            case TvCommonManager.INPUT_SOURCE_DTV:
                mInputImgs[1] = R.drawable.tv_signal_digital_selected;
                mCurIndex = 1;
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI:
                mInputImgs[2] = R.drawable.tv_signal_hdmi1_selected;
                mCurIndex = 2;
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI2:
                mInputImgs[3] = R.drawable.tv_signal_hdmi2_selected;
                mCurIndex = 3;
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI3:
                mInputImgs[4] = R.drawable.tv_signal_hdmi3_selected;
                mCurIndex = 4;
                break;
            case TvCommonManager.INPUT_SOURCE_CVBS:
                mInputImgs[5] = R.drawable.tv_signal_av_selected;
                mCurIndex = 5;
                break;
            case TvCommonManager.INPUT_SOURCE_YPBPR:
                mInputImgs[6] = R.drawable.tv_signal_ypbpr_selected;
                mCurIndex = 6;
                break;
            case TvCommonManager.INPUT_SOURCE_VGA:
                mInputImgs[7] = R.drawable.tv_signal_vga_selected;
                mCurIndex = 7;
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
    private void setImageSource(int index, View view) {
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

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_IMG:
                    setImageSource(mCurIndex, mCurFocusView);
                    mIsMoving = false;
                    break;
                case MSG_TODIMISS_UI:
                   dismiss();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void showSelector(boolean bShow) {
        if (mAnimatedSelector == null)
            return;
        if (bShow) {
            mAnimatedSelector.ensureViewVisible();
        } else {
            mAnimatedSelector.hideView();
        }
    }

    private void setInputSource(int position) {
        showInOutAnimation(false);
        int curSource;
        switch (position) {
            case 0:
                curSource = TvCommonManager.INPUT_SOURCE_ATV;
                break;
            case 1:
                curSource = TvCommonManager.INPUT_SOURCE_DTV;
                break;
            case 2:
                curSource = TvCommonManager.INPUT_SOURCE_HDMI;
                break;
            case 3:
                curSource = TvCommonManager.INPUT_SOURCE_HDMI2;
                break;
            case 4:
                curSource = TvCommonManager.INPUT_SOURCE_HDMI3;
                break;
            case 5:
                curSource = TvCommonManager.INPUT_SOURCE_CVBS;
                break;
            case 6:
                curSource = TvCommonManager.INPUT_SOURCE_YPBPR;
                break;
            case 7:
                curSource = TvCommonManager.INPUT_SOURCE_VGA;
                break;
            default:
                curSource = TvCommonManager.INPUT_SOURCE_NONE;
                break;
        }
        if(TvCommonManager.getInstance().getCurrentTvInputSource() != curSource){
            TvCommonManager.getInstance().setInputSource(curSource);
        }
        // start tv player
        Intent intent = new Intent("com.eostek.action.tvplayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mContext.startActivity(intent);
    }


}
