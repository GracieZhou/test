
package com.bq.tv.task.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bq.tv.task.TaskItem;
import com.eos.notificationcenter.R;

/**
 * Adapter about item of message.
 * 
 * @author Admin
 */
public class TaskBaseAdapter extends BaseAdapter {
    /** Log messages identifier. */
    public static String TAG = "TaskBaseAdapter";

    /** Data source list. */
    private List<TaskItem> mTaskList = new ArrayList<TaskItem>();

    private Context mContext;

    private LayoutInflater mInflater;

    private int mLastSelection;

    /**
     * Constructor of TaskBaseAdapter.
     * @param context
     * @param items
     */
    public TaskBaseAdapter(Context context, List<TaskItem> items) {

        mContext = context;

        mInflater = LayoutInflater.from(mContext);

        if (items != null) {
            mTaskList = items;
        }

    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mTaskList.size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mTaskList.get(position);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Remove item.
     * @param position
     * @return
     */
    public boolean removeItem(int position) {

        if (position < 0 || position >= getCount()) {
            return false;
        }

        mTaskList.remove(position);

        return true;
    }

    /** view cache. */
    public static class TaskGridViewHolder {
        public TextView exit_text;

        public ImageView arrow_img;

        public ImageView icon_img;

        public ImageView thumbnail_img;

        public TextView name_text;

    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TaskGridViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new TaskGridViewHolder();

            convertView = mInflater.inflate(R.layout.task_item, null);

            viewHolder.exit_text = (TextView) convertView.findViewById(R.id.exit_tip);
            viewHolder.arrow_img = (ImageView) convertView.findViewById(R.id.exit_icon);
            viewHolder.icon_img = (ImageView) convertView.findViewById(R.id.app_icon);
            viewHolder.thumbnail_img = (ImageView) convertView.findViewById(R.id.app_thumbnail);
            viewHolder.name_text = (TextView) convertView.findViewById(R.id.app_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TaskGridViewHolder) convertView.getTag();
        }

        TaskItem item = mTaskList.get(position);

        processView(viewHolder, item);

        return convertView;
    }

    private void processView(TaskGridViewHolder viewHolder, TaskItem item) {

        viewHolder.name_text.setText("" + item.getName());
        viewHolder.icon_img.setImageDrawable(item.getAppIcon());
        viewHolder.thumbnail_img.setImageBitmap(item.getThumbnail());
    }

    /**
     * Get the last selection.
     * @return
     */
    public int getLastSelection() {
        return mLastSelection;
    }

    /**
     * Set the last selection.
     * @param mLastSelection
     */
    public void setLastSelection(int mLastSelection) {
        this.mLastSelection = mLastSelection;
    }

    /**
     * Set task list.
     * @param mTaskList
     */
    public void setTaskList(List<TaskItem> mTaskList) {
        this.mTaskList = mTaskList;
    }

}
