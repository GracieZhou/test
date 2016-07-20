
package com.bq.tv.task;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;

import com.bq.tv.task.ui.TaskBaseAdapter;
import com.bq.tv.task.ui.TaskGridViewContainer;
import com.eos.notificationcenter.R;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayAdapterView.OnItemSelectedListener;
import com.jess.ui.TwoWayGridView;

/**
 *  Task switch listener.
 */
public class TaskSwitchListener {

    /**
     * Tag used to show in logcat.
     */
    public static final String TAG = "TaskSwitchListener";

    private Context mContext;
    
    private Dialog mDialog;

    private TaskSwitchHolder mHolder;

    private TaskGridViewContainer mTaskContainer;

    /**
     * Constructor of TaskSwitchListener.
     * @param mContext
     * @param mHolder
     */
    public TaskSwitchListener(Context mContext, Dialog dialog, TaskSwitchHolder mHolder) {
        this.mContext = mContext;
        this.mDialog = dialog;
        this.mHolder = mHolder;
        mTaskContainer = mHolder.getTaskContainer();
    }

    /** set listener. */
    public void setListener() {

        mHolder.getTaskGridView().setOnKeyListener(new TaskOnKeyListener());

        mHolder.getTaskGridView().setOnItemSelectedListener(new TaskItemSelectedListener());

        mHolder.getTaskGridView().setOnItemClickListener(new TaskItemClickListener());

        mHolder.getTaskGridView().setOnFocusChangeListener(new TaskFocusChangeListener());

    }

    private class TaskFocusChangeListener implements OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus) {

            View lastChild;
            lastChild = mHolder.getTaskGridView().getSelectedView();
            if (lastChild != null && !hasFocus) {
                View lastChildLayout = (LinearLayout) lastChild.findViewById(R.id.item_header);
                lastChildLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class TaskItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {

            TaskBaseAdapter adapter = (TaskBaseAdapter) mHolder.getTaskGridView().getAdapter();

            Log.i(TAG, "item click position = " + position);

            mTaskContainer.itemClicked(position);

            /** Select the clicked item */
            if (!mHolder.getTaskGridView().isFocused()) {
                mHolder.getTaskGridView().requestFocusFromTouch();
                mHolder.getTaskGridView().setSelection(position);
                adapter.setLastSelection(position);
            }

            mDialog.dismiss();

        }

    }

    private long mLastTime = -1;

    private class TaskOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            TwoWayGridView mGridView = mHolder.getTaskGridView();

            int position = mGridView.getSelectedItemPosition();

            Message message = mHandler.obtainMessage();

            long currentTime = System.currentTimeMillis();

            if (mLastTime == -1) {
                mLastTime = currentTime;
            }

            if (KeyEvent.ACTION_DOWN == event.getAction()) {

                switch (keyCode) {

                    case KeyEvent.KEYCODE_DPAD_LEFT:

                        if (mGridView.getSelectedItemPosition() > 0) {
                            mTaskContainer.clearUpArrow();
                            mTaskContainer.updateUpArrow(position - 1);
                        }
                        return false;

                    case KeyEvent.KEYCODE_DPAD_RIGHT:

                        if (mGridView.getSelectedItemPosition() < mGridView.getCount() - 1) {
                            mTaskContainer.clearUpArrow();
                            mTaskContainer.updateUpArrow(position + 1);
                        }
                        return false;
                }

            } else if (KeyEvent.ACTION_UP == event.getAction()) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:

                        if (mLastTime < 0)
                            /** key event is not from gridview then return */
                            return false;

                        if (currentTime - mLastTime >= 500) {
                            // already handled
                        } else {
                            message.arg1 = position;
                            message.what = REMOVE_TASK;
                            mHandler.sendMessage(message);
                        }
                        break;
                    default:
                        break;
                }

                mLastTime = -1;
            }
            return false;
        }
    }

    private class TaskItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(TwoWayAdapterView<?> parent, View view, int position, long id) {

            TaskBaseAdapter adapter = (TaskBaseAdapter) mHolder.getTaskGridView().getAdapter();
            adapter.setLastSelection(position);
        }

        @Override
        public void onNothingSelected(TwoWayAdapterView<?> parent) {

        }

    }

    static final int REMOVE_TASK = 0x01;

    static final int SHOW_DETAILS = 0x02;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case REMOVE_TASK:

                    mTaskContainer.exitSelectedApp(msg.arg1);

                    break;
                case SHOW_DETAILS:

                    mTaskContainer.showInstalledAppDetails(msg.obj.toString());

                    mDialog.dismiss();

                    break;
                default:
                    break;
            }

        }

    };

}
