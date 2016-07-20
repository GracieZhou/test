
package com.bq.tv.task;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.bq.tv.task.ui.FunctionBlockContainer;
import com.bq.tv.task.ui.FunctionBlockContainer.FunctionBlockClickListener;
import com.bq.tv.task.ui.TaskBaseAdapter;
import com.bq.tv.task.ui.TaskGridViewContainer;
import com.bq.tv.task.ui.animation.UIAnimationUtil;
import com.eos.notificationcenter.R;
import com.eos.notificationcenter.utils.Constants;
import com.eos.notificationcenter.utils.TaskItemBuildHelper;
import com.jess.ui.TwoWayGridView;

/**
 *  TaskSwitchHolder.
 */
public class TaskSwitchHolder {

    /**
     * Tag used to show in logcat.
     */
    public static String TAG = "TaskSwitchHolder";

    private Context mContext;
    
    private Dialog mDialog;

    private TaskGridViewContainer mTaskContainer;

    private FunctionBlockContainer mFunctionContainer;

    private TwoWayGridView mTaskGridView;

    /**
     * Constructor of TaskSwitchHolder.
     * @param context
     * @param view
     */
    public TaskSwitchHolder(Context context, Dialog dialog) {
        this.mContext = context;
        this.mDialog = dialog;
    }

    /**
     * Do the find views work.
     */
    public void getViews() {

        mTaskGridView = (TwoWayGridView) mDialog.findViewById(R.id.task_gridview);

        mTaskContainer = new TaskGridViewContainer(mContext, this);

        mFunctionContainer = new FunctionBlockContainer(mContext, mDialog);

        initMessageGirdView();

    }

    /**
     * Play animation when entering task switch.
     */
    public void playEnterAnimation() {
        Log.i(TAG, "playEnterAnimation");
        playBackgroundEnterAnim();
        playGridViewEnterAnim();
        mFunctionContainer.playEnterAnim();
    }

    /**
     * Play animation when exit.
     */
    public void playExitAnimation() {
        Log.i(TAG, "playExitAnimation");
        playBackgroundExitAnim();
        playGridViewExitAnim();
        mFunctionContainer.playExitAnim();
    }

    private void playGridViewExitAnim() {
        mTaskGridView.setLayoutAnimation(UIAnimationUtil.getLayoutShowAnimation(0));
    }

    private void playBackgroundExitAnim() {
        View view = mDialog.findViewById(R.id.main_layout_bg);
        if (view != null) {
            view.startAnimation(UIAnimationUtil.getViewAlphaHideAnimation());
        }
    }

    private void playGridViewEnterAnim() {
        mTaskGridView.setLayoutAnimation(UIAnimationUtil.getLayoutShowAnimation(0));
    }

    private void playBackgroundEnterAnim() {
        View view = mDialog.findViewById(R.id.main_layout_bg);
        if (view != null) {
            view.startAnimation(UIAnimationUtil.getViewShowAnimation());
        }
    }

    /**
     * Hide all views.
     */
    public void hideAllView() {
        View view = mDialog.findViewById(R.id.main_layout_bg);
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Initialize MessageScrollView.
     */
    public void initMessageGirdView() {

        // initialize message view
        mTaskGridView.setAdapter(new TaskBaseAdapter(mContext, null));
        Log.i(TAG, "mTaskGridView size " + mTaskGridView.getCount());

        mTaskGridView.setNumRows(1);
        mTaskGridView.setNumColumns(Constants.TASK_ITEM_NUM_PER_SCREEN);
        mTaskGridView.setSmoothScrollbarEnabled(true);
        mTaskGridView.setSelector(mContext.getResources().getDrawable(R.drawable.task_outter_focus));

        updateTaskItems(0);
    }

    /**
     * Refresh views.
     */
    public void refreshViews() {
        if (mTaskGridView.isFocused()) {
            mTaskContainer.clearUpArrow();
            mTaskContainer.setLastSelection(-1);
            mTaskGridView.setSelection(-1);
            // updateTaskItems(0);
            // initMessageGirdView();
        } else {
            updateTaskItems(-1);
        }
        refreshRemoveBlock();
    }

    /** refresh CircleBar. */
    public void refreshCircleBar() {
        mFunctionContainer.refreshCircleBar();
    }

    /** refresh CircleBar. */
    public void refreshCleanBlock() {
        mFunctionContainer.refreshCleanBlock();
    }

    /**
     * Refresh remove block.
     */
    public void refreshRemoveBlock() {
        mFunctionContainer.refreshRemoveBlock();
    }

    /**
     * Update TaskItems.
     * 
     * @param position TODO
     */
    public void updateTaskItems(final int position) {

        TaskItemBuildHelper helper = new TaskItemBuildHelper(mContext);
        getTaskContainer().updateTaskItems(helper.getTaskItems());

        final ViewTreeObserver observer = getTaskGridView().getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (observer.isAlive()) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    ViewTreeObserver observer = getTaskGridView().getViewTreeObserver();
                    observer.removeOnGlobalLayoutListener(this);
                }
                if (position >= 0)
                    setInitialPosition(position);

                Log.i(TAG, "selected pos = " + getTaskGridView().getSelectedItemPosition());
            }
        });

    }

    /**
     * Set listener to function block.
     * @param l
     */
    public void setFunctionBlockClickListener(FunctionBlockClickListener l) {
        mFunctionContainer.setFunctionBlockClickListener(l);
    }

    /**
     * Set the initial position.
     * @param pos
     */
    public void setInitialPosition(int pos) {
        mTaskContainer.setInitialPosition(pos);
    }

    /**
     * Get TaskGridView.
     * @return
     */
    public TwoWayGridView getTaskGridView() {
        return mTaskGridView;
    }

    /**
     * Get task container.
     * @return
     */
    public TaskGridViewContainer getTaskContainer() {
        return mTaskContainer;
    }

    /**
     * Get function container.
     * @return
     */
    public FunctionBlockContainer getFunctionContainer() {
        return mFunctionContainer;
    }

    /**
     * Determine whether show remove dialog.
     * @return
     */
    public boolean isRemoveDialogShowing() {
        return mFunctionContainer.isRemoveDialogShowing();
    }

    /**
     * Set RemoveDialogDismiss listener.
     * @param l
     */
    public void setRemoveDialogDismissListener(OnDismissListener l) {
        mFunctionContainer.setRemoveDialogDismissListener(l);
    }

}
