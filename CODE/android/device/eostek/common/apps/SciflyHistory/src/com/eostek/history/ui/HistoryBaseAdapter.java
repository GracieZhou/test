
package com.eostek.history.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.history.R;
import com.eostek.history.model.HistoryCategory;
import com.eostek.history.model.HistoryItem;
import com.eostek.history.model.MoreItem;
import com.eostek.history.util.Constants;

/**
 * Adapter about item of history
 * 
 * @author Mars
 */
public class HistoryBaseAdapter extends BaseAdapter {

    /** Log messages identifier */
    public static String TAG = "HistoryBaseAdapter";

    private Context mContext;

    public static final int GET_MORE_COUNT = Constants.GET_MORE_COUNT;

    // private String

    /** Data source list */
    private List<HistoryItem> mHistoryItemsPart;

    private List<HistoryItem> mHistoryItemsFull;

    private LayoutInflater mInflater;

    private int mLastSelectedPosition;

    private List<HistoryItem> mDataSource = new ArrayList<HistoryItem>();

    private HistoryCategory mHistoryCategory;

    /**
     * Constructor of HistoryBaseAdapter.
     * 
     * @param context
     */
    public HistoryBaseAdapter(Context context) {

        mContext = context;

        mInflater = LayoutInflater.from(mContext);

        mHistoryItemsPart = new ArrayList<HistoryItem>();
    }

    /**
     * Constructor of HistoryBaseAdapter.
     * 
     * @param context
     * @param hc
     */
    public HistoryBaseAdapter(Context context, HistoryCategory hc) {

        mContext = context;

        mInflater = LayoutInflater.from(mContext);

        if (hc.getChildren() != null) {

            mHistoryItemsPart = getItemsPart(hc.getChildren());

            mHistoryItemsFull = hc.getChildren();

        }

    }

    /** use this constructor will use all the item */
    public HistoryBaseAdapter(Context context, List<HistoryItem> children) {

        mContext = context;

        mInflater = LayoutInflater.from(mContext);

        if (children != null) {

            mHistoryItemsPart = children;

            mHistoryItemsFull = children;
        }

    }

    private List<HistoryItem> getItemsPart(List<HistoryItem> children) {

        List<HistoryItem> itemsPart = new ArrayList<HistoryItem>();

        if (hasMoreItem(children)) {

            for (int i = 0; i < GET_MORE_COUNT; i++) {
                itemsPart.add(children.get(i));
            }
            itemsPart.add(children.get(children.size() - 1));
            children.remove(children.size() - 1);
        } else {
            itemsPart = new ArrayList<HistoryItem>(children);
        }

        return itemsPart;
    }

    private boolean hasMoreItem(List<HistoryItem> children) {

        boolean hasMoreItem = children.get(children.size() - 1) instanceof MoreItem;

        int itemCount = children.size();

        if (hasMoreItem && itemCount >= GET_MORE_COUNT + 1) {
            return true;
        }
        return false;
    }

    @Override
    public int getCount() {
        return mHistoryItemsPart.size();
    }

    @Override
    public Object getItem(int position) {
        return mHistoryItemsPart.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * get last selected position.
     */
    public int getLastSelectedPostion() {
        return mLastSelectedPosition;
    }

    /**
     * set last selected position.
     */
    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.mLastSelectedPosition = lastSelectedPosition;
    }

    /**
     * Get full history item.
     * 
     * @return
     */
    public List<HistoryItem> getHistoryItemsFull() {
        return mHistoryItemsFull;
    }

    /** view cache */
    public static class GridViewHolder {
        public TextView timeLabel;

        public TextView description;

        public ImageView thumbnail;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridViewHolder viewHolder = null;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.history_item, null);

            viewHolder = new GridViewHolder();
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.history_item_thumb);
            viewHolder.timeLabel = (TextView) convertView.findViewById(R.id.history_item_time);
            viewHolder.description = (TextView) convertView.findViewById(R.id.history_item_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GridViewHolder) convertView.getTag();
        }

        processView(viewHolder, (HistoryItem) getItem(position));

        return convertView;
    }

    private void processView(GridViewHolder viewHolder, HistoryItem historyItem) {

        if (historyItem.getSmallImage() != null) {
            viewHolder.thumbnail.setImageBitmap(historyItem.getSmallImage());
        } else {
            Drawable drawble = historyItem.loadDrawable(mContext);
            if (drawble != null) {
                viewHolder.thumbnail.setImageDrawable(drawble);
            }
        }

        viewHolder.timeLabel.setText("" + historyItem.getTimeLabel());
        viewHolder.description.setText("" + historyItem.getName());

    }
}
