
package com.bq.tv.task.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.PackageManagerExtra;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bq.tv.task.TaskItem;
import com.bq.tv.task.TaskSwitchHolder;
import com.bq.tv.task.ui.animation.UIAnimationUtil;
import com.eos.notificationcenter.R;
import com.eos.notificationcenter.utils.Constants;
import com.eos.notificationcenter.utils.Util;
import com.jess.ui.TwoWayGridView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;

/**
 *  A container of task grid view.
 */
public class TaskGridViewContainer {

    /**
     * Tag used to show in logcat.
     */
    public static final String TAG = "TaskGridViewContainer";

    static final int FIRST_TASK = 0;

    private Context mContext;
    
    private Dialog mDialog;

    //TaskSwitchHolder mHolder;

    private TwoWayGridView mGridView;

    private TaskBaseAdapter adapter;

    private PackageManager mPackageManager;

    private ActivityManager mActivityManager;

    private static final String SCHEME = "package";

    static final int UID_SYSTEM = 1000;
    
    private static final String TV_PLAYER_PACKAGE = "com.eostek.tv.player";

    /**
     * Constructor of TaskGridViewContainer.
     * @param context
     * @param mHolder
     */
    public TaskGridViewContainer(Context context, TaskSwitchHolder mHolder) {
        this.mContext = context;
        this.mGridView = mHolder.getTaskGridView();
        this.adapter = (TaskBaseAdapter) mGridView.getAdapter();
        this.mPackageManager = mContext.getPackageManager();
        this.mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * Update task item.
     * @param taskItems
     */
    public void updateTaskItems(List<TaskItem> taskItems) {
        TaskBaseAdapter adapter = (TaskBaseAdapter) mGridView.getAdapter();
        adapter.setTaskList(taskItems);
        adapter.notifyDataSetChanged();
    }

    /**
     * Get Item.
     * @param position
     * @return
     */
    public TaskItem getItem(int position) {
        TaskBaseAdapter adapter = (TaskBaseAdapter) mGridView.getAdapter();
        return (TaskItem) adapter.getItem(position);
    }

    private boolean removeItem(int position) {

        TaskBaseAdapter adapter = (TaskBaseAdapter) mGridView.getAdapter();

        boolean result = adapter.removeItem(position);
        adapter.notifyDataSetChanged();
        return result;
    }

    /**
     * Set initial position.
     * @param pos
     */
    public void setInitialPosition(final int pos) {

        if (pos < 0 || pos >= mGridView.getCount()) {
            return;
        }

        final ViewTreeObserver observer = mGridView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (observer.isAlive()) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    ViewTreeObserver observer = mGridView.getViewTreeObserver();
                    observer.removeOnGlobalLayoutListener(this);
                }

                if (adapter == null) {
                    adapter = (TaskBaseAdapter) mGridView.getAdapter();
                }

                mGridView.requestFocusFromTouch();
                mGridView.setSelection(pos);
                adapter.setLastSelection(pos);
                clearUpArrow();
                updateUpArrow(pos);

                Log.i(TAG, "selected pos = " + mGridView.getSelectedItemPosition());
            }
        });

    }
    boolean isDeleting = false;

    /**
     * 退出应用.
     * 
     * @param position position of item
     */
    public void exitSelectedApp(final int position) {

        if (position < 0 || position >= mGridView.getCount() || isDeleting) {
            return;
        }

        isDeleting = true;

        final TaskItem item = getItem(position);

        final int positionOnScreen = getPositionOnScreen(position);

        if (item == null) {
            return;
        }

        Log.i(TAG, "position = " + position + " position on screen = " + positionOnScreen);

        playExitAnimationBefore(positionOnScreen).setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removeItem(position, item, positionOnScreen);
                isDeleting = false;
            }
        });

    }

    private void removeItem(int position, TaskItem item, int positionOnScreen) {
        removeItem(position);

        playExitAnimationAfter(positionOnScreen);

        clearUpArrow();

        if (positionOnScreen >= mGridView.getCount()) {
            setInitialPosition(positionOnScreen - 1);
        } else {
            setInitialPosition(positionOnScreen);
        }

        String pckName = item.getPackagename();

        if (isSystemApp(pckName)) {
            Log.i(TAG, "system app " + pckName);
            // EosTek Patch Begin
            forceStop(item);
            //mActivityManager.killBackgroundProcesses(pckName);
            // EosTek Patch End
        } else {
            Log.i(TAG, "user app " + pckName);
            forceStop(item);
        }
    }

    private AnimationSet playExitAnimationBefore(int positionOnScreen) {
        View view = mGridView.getChildAt(positionOnScreen);
        AnimationSet animation = UIAnimationUtil.getViewMoveAnimation(0, 0, 0, -1.0f, 300);
        animation.addAnimation(UIAnimationUtil.getViewScaleHideAnimation(300));
        animation.setFillAfter(false);
        if (view != null) {
            Log.i(TAG, "playExitAnimationBefore");
            view.setAnimation(animation);
            mGridView.invalidate();
        }
        return animation;
    }

    private boolean isSystemApp(String pck) {

        PackageManagerExtra pme = PackageManagerExtra.getInstance();

        return pme.isSystemApp(pck);
    }

    private void forceStop(TaskItem item) {
        try {
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            String pkgName = (item).getPackagename();
            method.invoke(mActivityManager, pkgName);
            
            //Android 4.4 、5.0等等
            //mActivityManager.removeTask(item.getTaskId(), ActivityManager.REMOVE_TASK_KILL_PROCESS);
            if(Build.VERSION.SDK_INT < 22) {
            	Method removeTask = Class.forName("android.app.ActivityManager").getMethod("removeTask", int.class, int.class);
            	int taskId = (item).getTaskId();
            	Object flag = Class.forName("android.app.ActivityManager").getField("REMOVE_TASK_KILL_PROCESS").get("REMOVE_TASK_KILL_PROCESS");
            	//int flag = ActivityManager.REMOVE_TASK_KILL_PROCESS;
            	removeTask.invoke(mActivityManager, taskId, flag);
            }
            //Android 5.1
            //mActivityManager.removeTask(item.getTaskId());
            else if(Build.VERSION.SDK_INT >= 22) {
            	Method removeTask = Class.forName("android.app.ActivityManager").getMethod("removeTask", int.class);
            	int taskId = (item).getTaskId();
            	removeTask.invoke(mActivityManager, taskId);
            }  
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.i(TAG, "InvocationTargetException");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 展示应用详情. */
    public void showInstalledAppDetails(String packageName) {

        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        Uri uri = Uri.fromParts(SCHEME, packageName, null);

        intent.setData(uri);

        try {

            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.i(TAG, "not found " + intent.getDataString());
            e.printStackTrace();
        }
    }

    private void playExitAnimationAfter(int position) {

        final int tempPosition = position;

        final ViewTreeObserver observer = mGridView.getViewTreeObserver();
        observer.addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                List<Animator> resultList = new LinkedList<Animator>();
                int oldPosition = tempPosition;
                int endPosition = (int) (Constants.TASK_ITEM_NUM_PER_SCREEN + 1.5);
                for (int pos = oldPosition; pos < endPosition; pos++) {
                    View view = mGridView.getChildAt(pos);
                    if (view != null) {
                        resultList.add(UIAnimationUtil.createTranslationAnimations(view, view.getWidth(), 0, 0, 0));
                    }
                }
                AnimatorSet resultSet = new AnimatorSet();
                resultSet.playTogether(resultList);
                resultSet.setDuration(200);
                resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
                resultSet.start();
                return true;
            }
        });
    }

    /**
     * Called when item is clicked.
     * @param position
     */
    public void itemClicked(int position) {

        TaskItem item = getItem(position);

        if (item == null) {
            return;
        }

        if (mPackageManager == null) {
            mPackageManager = mContext.getPackageManager();
        }

        Intent intent = item.getIntent();

        final String pck = item.getPackagename();
        Log.i(TAG, "package name = " + pck);
        
        // switch source while package is not dtv.
        if (!pck.equals(TV_PLAYER_PACKAGE)) {
            new Thread(new Runnable() {
                public void run() {
                    Util.switchSource(mContext);
                }
            }).start();
        }

        try {

            if (intent != null) {

                Log.i(TAG, "start by intent from item pck " + item.getIntent().getComponent().getPackageName()
                        + " clsName " + item.getIntent().getComponent().getClassName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                intent = mPackageManager.getLaunchIntentForPackage(pck);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else {
                    Log.i(TAG, "start by action " + pck);
                    intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(pck);
                    mContext.startActivity(intent);
                }
            }

        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "faild to start application.", Toast.LENGTH_LONG).show();
        }
    }

    /** update up arrow and exit app textview. */
    public void updateUpArrow(int position, boolean isShown) {

        View view = mGridView.getChildAt(getPositionOnScreen(position));

        try {
            View child = view.findViewById(R.id.item_header);

            if (isShown) {
                child.setVisibility(View.VISIBLE);
            } else {
                child.setVisibility(View.INVISIBLE);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update up arrow.
     * @param position
     */
    public void updateUpArrow(int position) {
        updateUpArrow(position, true);
    }

    /**
     * Get the position on screen.
     * @param position
     * @return
     */
    public int getPositionOnScreen(int position) {

        if (position < 0 || position > mGridView.getCount()) {
            return -1;
        }

        return position - mGridView.getFirstVisiblePosition();
    }

    /**
     * Clear up arrow.
     */
    public void clearUpArrow() {
        View view;
        for (int i = 0; i <= Constants.TASK_ITEM_NUM_PER_SCREEN; i++) {
            view = mGridView.getChildAt(i);
            if (view != null) {
                try {
                    view.findViewById(R.id.item_header).setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Called when get focus again.
     */
    public void doWhenGetFocusAgain() {

        Log.i(TAG, "get focus");

        if (adapter == null) {
            adapter = (TaskBaseAdapter) mGridView.getAdapter();
        }
        mGridView.requestFocusFromTouch();
        mGridView.setSelection(adapter.getLastSelection());
        View lastChild;
        lastChild = mGridView.getSelectedView();
        if (lastChild != null) {
            View lastChildLayout = (LinearLayout) lastChild.findViewById(R.id.item_header);
            lastChildLayout.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Get last selection.
     * @return
     */
    public int getLastSelection() {
        if (adapter == null) {
            adapter = (TaskBaseAdapter) mGridView.getAdapter();
        }
        return adapter.getLastSelection();
    }

    /**
     * Set last selection.
     * @param pos
     */
    public void setLastSelection(int pos) {
        if (adapter == null) {
            adapter = (TaskBaseAdapter) mGridView.getAdapter();
        }
        adapter.setLastSelection(pos);
    }

}
