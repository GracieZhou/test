
package com.bq.tv.task.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.bq.tv.task.CircleBar;
import com.bq.tv.task.device.StorageDeviceManager;
import com.bq.tv.task.device.StorageItem;
import com.bq.tv.task.ui.animation.UIAnimationUtil;
import com.bq.tv.task.ui.dialog.DeleteMessageDialog;
import com.bq.tv.task.ui.functionitem.*;
import com.eos.notificationcenter.R;
import com.eos.notificationcenter.utils.MemoryCalculator;
import com.eos.notificationcenter.utils.Util;

/**
 * A class to realize functions of the block.
 */
public class FunctionBlockContainer {

    /**
     * Tag used to show in logcat.
     */
    public static String TAG = "FunctionBlockContainer";

    private Context mContext;
    
    private Dialog mDialog;

    private StorageDeviceManager sdManager;

    private DeleteMessageDialog mRemoveDialog;

    private CircleBar mProgressBar;

    private LinearLayout mCleanBlock;  

    private LinearLayout mRemoveBlock;

    private ViewGroup mFunctionBlock;

    FunctionBlockClickListener mFunctionBlockClickListener;

    private final static String ALL_APP_PACKAGE_NAME = "eos.intent.action.ALLACTIVITY";

    private final static String HISTORY_PACKAGE_NAME = "com.eostek.history";

    private final static String SETTINGS_ACTION = "android.settings.SETTINGS";

    private final static String MEDIA_BROWSER_PACKAGE_NAME = "com.eostek.mediabrowser";

    private final static String VIDEO_QUALITY_PACKAGE_NAME = "com.eostek.tv.threedimensions";

    private final static String VOICE_CONTROLLER_SERVICE = "com.eostek.sciflyui.voicecontroller.service.IVoiceController";

    private final static String REPORT_BUG_ACTION = "android.settings.action.BUGREPORT";
    
    /**
     * Constructor of FunctionBlockContainer class.
     * 
     * @param context
     */
    public FunctionBlockContainer(Context context, Dialog dialog) {
        mContext = context;
        mDialog = dialog;

        mProgressBar = (CircleBar) mDialog.findViewById(R.id.memo_percent_progress);

        mCleanBlock = (LinearLayout) mDialog.findViewById(R.id.clean_block);

        mRemoveBlock = (LinearLayout) mDialog.findViewById(R.id.remove_block);

        mFunctionBlock = (ViewGroup) mDialog.findViewById(R.id.function_block_scroll);
        
        addItem(R.id.report_bug, REPORT_BUG_ACTION, "Action");
            
        addItem(R.id.setting_function, SETTINGS_ACTION, "Action");
        
        addItem(R.id.history_function, HISTORY_PACKAGE_NAME, "PackageName");
        
        addItem(R.id.media_function, MEDIA_BROWSER_PACKAGE_NAME, "PackageName");
        
        addItem(R.id.voice_function,VOICE_CONTROLLER_SERVICE,"Service");
        
        addItem(R.id.video_function,VIDEO_QUALITY_PACKAGE_NAME,"Video");
        
        addItem(R.id.all_app_function,ALL_APP_PACKAGE_NAME,"AllApp");
        
        setListener();

        refreshCircleBar();

        refreshCleanBlock();

        refreshRemoveBlock();

    }

    private void addItem(int id, String intentStr, String type) {

        FunctionItem funItem=null;

        if (type.equals("PackageName")) {
            funItem = new PackageNameItem(mContext, mDialog, id, intentStr);

        } else if (type.equals("Action")) {
            funItem = new ActionItem(mContext, mDialog, id, intentStr);

        } else if (type.equals("Service")) {
            funItem = new VoiceControllerItem(mContext, mDialog, id, intentStr);

        } else if (type.equals("Video")) {
            funItem = new VideoQualityItem(mContext, mDialog, id, intentStr);
            
        } else if (type.equals("AllApp")) {
            funItem = new AllAppItem(mContext, mDialog, id, intentStr);
        }
        
        if (funItem != null) {
            funItem.setOnClickListener();
        }
    }
    
    private void setListener() {

        mCleanBlock.setOnClickListener(new CleanAndRemoveClickListener());
        mRemoveBlock.setOnClickListener(new CleanAndRemoveClickListener());
        
        mProgressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onclick mProgressBar");
                refreshMemoWithAnim();
            }
        });
    }

    /** function block click event listener. */
    public interface FunctionBlockClickListener {
        public void onBlockClick(View view);
    }

    /**
     * Set FunctionBlock click listener.
     * 
     * @param l
     */
    public void setFunctionBlockClickListener(FunctionBlockClickListener l) {
        if (l != null) {
            mFunctionBlockClickListener = l;
        }
    }

    private void refreshMemoWithAnim() {
        int newProgress = (int) getMemoProgress();
        mProgressBar.playAnimation(mProgressBar.getProgress(), newProgress >= 0 ? newProgress : 0);
    }

    /**
     * Make the view group get focus.
     * 
     * @param v
     */
    public void getFocus(ViewGroup v) {

        if (v != null && v.getVisibility() == View.VISIBLE) {
            v.requestFocus();
        }

    }

    private class CleanAndRemoveClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {

            if (mFunctionBlockClickListener != null) {
                mFunctionBlockClickListener.onBlockClick(view);
            }

            if (view.equals(mCleanBlock)){
                // Log.i(TAG, "try to kill processes");
                Util.killProcesses();
                refreshCleanBlock();
                // refreshCircleBar();
                refreshMemoWithAnim();
            }
            
            if (view.equals(mRemoveBlock)){
             // Log.i(TAG, "try to remove devices");
                if (mRemoveDialog == null) {
                    mRemoveDialog = new DeleteMessageDialog(mContext);
                }
                mRemoveDialog.show();

                if (sdManager == null) {
                    sdManager = new StorageDeviceManager(mContext);
                }

                try {
                    List<StorageItem> items = sdManager.getAllMountedVolume();
                    for (StorageItem item : items) {
                        Log.i(TAG, "storage item name : " + item.getLabel());
                    }
                    mRemoveDialog.setDevicesItems(items);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                
            }
        }
    }

    /** is remove storage device dialog showing or not. */
    public boolean isRemoveDialogShowing() {

        if (mRemoveDialog != null && mRemoveDialog.isShowing()) {
            return true;
        }
        return false;
    }
    
    // circle bar
    long progress = 1;

    /** refresh cicle bar. */
    public void refreshCircleBar() {

        MemoryCalculator mc = new MemoryCalculator(mContext);

        progress = mc.getProcess();

        mProgressBar.setProgress((int) progress);
    }

    /**
     * Get the memory progress.
     * 
     * @return
     */
    public long getMemoProgress() {
        MemoryCalculator mc = new MemoryCalculator(mContext);
        return mc.getProcess();
    }

    // clean block

    /**
     * Refresh clean block.
     */
    public void refreshCleanBlock() {
        TextView tx = (TextView) mCleanBlock.findViewById(R.id.bkground_count);
        Integer count = Util.getRunningThreadCount(mContext);
        tx.setText("" + String.format(mContext.getResources().getString(R.string.bkground_count), count));
        // Log.i(TAG, "count = " + Util.getRunningThreadCount(mContext));
    }

    /**
     * Get clean block.
     * 
     * @return
     */
    public LinearLayout getCleanBlock() {
        return mCleanBlock;
    }

    // remove block

    /**
     * Refresh remove block.
     */
    public void refreshRemoveBlock() {

        if (sdManager == null) {
            sdManager = new StorageDeviceManager(mContext);
        }

        try {

            List<StorageItem> items = sdManager.getAllMountedVolume();

            int count = items.size();

            if (count == 0) {

                ViewGroup removeBlockOutter = (ViewGroup) mDialog.findViewById(R.id.remove_block_outter);
                removeBlockOutter.setVisibility(View.GONE);

                mFunctionBlock.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3));
            } else {

                ViewGroup removeBlockOutter = (ViewGroup) mDialog.findViewById(R.id.remove_block_outter);

                if (removeBlockOutter.getVisibility() == View.GONE) {
                    removeBlockOutter.setVisibility(View.VISIBLE);
                }
                mFunctionBlock.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));

                TextView txt = (TextView) mRemoveBlock.findViewById(R.id.remove_text_count);
                txt.setText("" + String.format(mContext.getResources().getString(R.string.remove_usb_count), count));
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    /**
     * Play entering animation.
     */
    public void playEnterAnim() {
        View view = mDialog.findViewById(R.id.bottom);
        if (view != null) {
            view.startAnimation(UIAnimationUtil.getViewMoveAnimation(0, 0, 1.0f, 0));
        }

        if (mProgressBar != null) {
            mProgressBar.startAnimation(UIAnimationUtil.getViewMoveAnimation(0, 0, -1.0f, 0));
        }

    }

    /**
     * play exiting animation.
     */
    public void playExitAnim() {

        View view = mDialog.findViewById(R.id.bottom);
        if (view != null) {
            view.startAnimation(UIAnimationUtil.getViewMoveAnimation(0, 0, 0, 1.0f));
        }

        if (mProgressBar != null) {
            mProgressBar.startAnimation(UIAnimationUtil.getViewMoveAnimation(0, 0, 0, -1.0f));
        }
    }

    /**
     * Set remove dialog dismiss listener.
     * 
     * @param l
     */
    public void setRemoveDialogDismissListener(OnDismissListener l) {

        if (mRemoveDialog == null) {
            mRemoveDialog = new DeleteMessageDialog(mContext);
        }

        mRemoveDialog.setOnDismissListener(l);
    }

}
