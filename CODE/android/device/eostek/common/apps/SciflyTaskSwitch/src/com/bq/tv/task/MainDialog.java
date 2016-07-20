
package com.bq.tv.task;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bq.tv.task.ui.FunctionBlockContainer.FunctionBlockClickListener;
import com.bq.tv.task.ui.TaskBaseAdapter;
import com.eos.notificationcenter.R;

public class MainDialog extends Dialog {

    private Context mContext;
    
    private static MainDialog mMainDialog;
    
    private TaskSwitchHolder mHolder;

    private TaskSwitchListener mListener;

    public static final String TAG = MainDialog.class.getSimpleName();

    private static final int REFRESH_MEMO_INFO = 0x01;

    private static final int AUTO_CLOSE_WINDOW = 0x02;

    private static final int AUTO_EXIT_ACTIVITY = 0x03;

    private static final int AUTO_CLOSE_WINDOW_DELAY = 4000;

    public static final String SCIFLY_PLATFORM_TV = "tv";
    
    private boolean isEnterAnimationPlayed = false;

    public MainDialog(Context context) {
        super(context, R.style.style_dialog);
        this.mContext = context;
        setContentView(R.layout.main_layout);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        hideViewByplatform();

        mHolder = new TaskSwitchHolder(mContext, this);
        mHolder.getViews();

        mListener = new TaskSwitchListener(mContext, this, mHolder);
        mListener.setListener();

        mHandler.sendEmptyMessageDelayed(REFRESH_MEMO_INFO, 2000);

        setListener();

        Log.i(TAG, "MainDialog");
    }
    
    public static synchronized MainDialog getInstance(Context context) {
        if(mMainDialog == null) {
            mMainDialog = new MainDialog(context);
        }
        return mMainDialog;
    }
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.i(TAG, "onCreate");
    }

    private void hideViewByplatform() {
    	
    	String board_platform = SystemProperties.get("ro.board.platform");
        String scifly_platform = SystemProperties.get("ro.scifly.platform");
        String product_device = SystemProperties.get("ro.product.device");
        
        Log.d(TAG, "ro.board.platform = " + board_platform);
        Log.d(TAG, "ro.scifly.platform = " + scifly_platform);
        Log.d(TAG, "ro.product.device = " + product_device);
        Log.d(TAG, "Build.DEVICE = " + Build.DEVICE);
        Log.d(TAG, "Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
        
        // dongle has no video_function
        if(scifly_platform.equals("dongle")) {
        	LinearLayout video_function = (LinearLayout) findViewById(R.id.video_function);
            video_function.setVisibility(View.GONE);
        }

        // 828 and 638 has no report_bug and voice_function
        if(scifly_platform.equals("muji") || scifly_platform.equals("muji")) {
        	LinearLayout voice_controller = (LinearLayout) findViewById(R.id.voice_function);
            voice_controller.setVisibility(View.GONE); 
            LinearLayout report_controller = (LinearLayout) findViewById(R.id.report_bug);
            report_controller.setVisibility(View.GONE); 
        }
        
        /*
        if (!Build.DEVICE.equals("heran_dvb")) {
            if (!scifly_platform.equals(SCIFLY_PLATFORM_TV) || Build.DEVICE.equals("LeaderSMARTTV") || Build.DEVICE.equals("LeaderSMARTTV32G")) {
                LinearLayout video_function = (LinearLayout) findViewById(R.id.video_function);
                video_function.setVisibility(View.GONE);
            }
        }
        
        if(product_device.equals("Leader")){
           LinearLayout voice_controller = (LinearLayout) findViewById(R.id.voice_function);
           voice_controller.setVisibility(View.GONE); 
        }
        */
    }

    private void setListener() {

        mHolder.setFunctionBlockClickListener(new FunctionBlockClickListener() {
            @Override
            public void onBlockClick(View view) {
                sendCloseWindowMsg();
            }
        });

        mHolder.setRemoveDialogDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                sendCloseWindowMsg();
            }
        });
    }

    

    @Override
    protected void onStart() {
        super.onStart();
        broadcastRegister();
        if (!isEnterAnimationPlayed) {
            mHolder.playEnterAnimation();
            isEnterAnimationPlayed = true;
        }

        sendCloseWindowMsg();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeMessages(AUTO_CLOSE_WINDOW);
        mHandler.removeMessages(AUTO_EXIT_ACTIVITY);
        broadcastUnRegister();
        Log.i(TAG, "onStop");
    }
    
    private void broadcastRegister() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");

        mContext.registerReceiver(usbReceiver, filter);
    }
    
    private void broadcastUnRegister() {
        mContext.unregisterReceiver(usbReceiver);
    }
    
    public void showDialog() {
        if(isShowing()) {
            Log.i(TAG, "isShowing");
            return;
        }
        show();
    }
    
    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            mHolder.refreshRemoveBlock();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent arg1) {

        Log.i(TAG, "onKeyDown " + keyCode);
        sendCloseWindowMsg();

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mHolder.getTaskGridView().isFocused()) {
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mHolder.getTaskGridView().isFocused()) {
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!mHolder.getTaskGridView().isFocused()) {
                    TaskBaseAdapter adapter = (TaskBaseAdapter) mHolder.getTaskGridView().getAdapter();
                    if (adapter.getCount() == 0) {
                    } else {
                        mHolder.getTaskContainer().doWhenGetFocusAgain();
                        Log.i(TAG, "up to task gridview!!!");
                    }
                    return true;
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mHolder.getTaskGridView().isFocused()) {
                    mHolder.getFunctionContainer().getFocus(mHolder.getFunctionContainer().getCleanBlock());
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_BACK:
                mHandler.removeMessages(AUTO_CLOSE_WINDOW);
                mHandler.removeMessages(AUTO_EXIT_ACTIVITY);
                mHandler.sendEmptyMessage(AUTO_CLOSE_WINDOW);
                return true;
        }

        return super.onKeyDown(keyCode, arg1);
    }

    /**
     * Send a message to close window.
     */
    protected void sendCloseWindowMsg() {
        Log.i(TAG, "sendCloseWindowMsg");
        mHandler.removeMessages(AUTO_CLOSE_WINDOW);
        mHandler.removeMessages(AUTO_EXIT_ACTIVITY);
        mHandler.sendEmptyMessageDelayed(AUTO_CLOSE_WINDOW, AUTO_CLOSE_WINDOW_DELAY);
    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_MEMO_INFO:
                    mHolder.refreshCircleBar();
                    mHolder.refreshCleanBlock();
                    mHandler.sendEmptyMessageDelayed(REFRESH_MEMO_INFO, 2000);
                    break;
                case AUTO_CLOSE_WINDOW:

                    // window will not close when remove dialog is showing.
                    if (mHolder.isRemoveDialogShowing()) {
                        return;
                    }

                    mHolder.playExitAnimation();
                    mHandler.sendEmptyMessageDelayed(AUTO_EXIT_ACTIVITY, 600);
                    break;
                case AUTO_EXIT_ACTIVITY:
                    Log.i(TAG, "TaskSwitch Auto Closed");
                    System.exit(0);
                    break;
                default:
                    break;
            }

        }
    };
}
