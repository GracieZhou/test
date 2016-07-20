
package com.eostek.scifly.messagecenter.ui;

import java.util.ArrayList;
import java.util.List;

import scifly.provider.SciflyStore.Messages;
import scifly.provider.metadata.Msg;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.scifly.messagecenter.MainActivity;
import com.eostek.scifly.messagecenter.R;
import com.eostek.scifly.messagecenter.util.Util;

/**
 * Adapter about item of message.
 * 
 * @author Admin
 */
public class MessageBaseAdapter extends BaseAdapter {
    /** Log messages identifier. */
    public static final String TAG = "MessageBaseAdapter";

    /** Data source list. */
    private List<Msg> mMessages2show;

    private List<Msg> mMessagesWait2show_pre;

    private List<Msg> mMessagesWait2show_next;

    private MainActivity mContext;

    private LayoutInflater mInflater;

    private int mLastSelectedPosition = -1;

    private OnNoMessageLeftListener mOnNoMessageLeftListener;

    private LazyLoadingHelper mLoadHelper;

    private UpdateStatu mUpdateStatu;

    private MessageProcesser mProcesser;

    private boolean isDeleteMode = false;

    /**
     * get instance of MessageProcesser.
     * 
     * @return
     */
    public MessageProcesser getProcesser() {
        return mProcesser;
    }

    /**
     * get the messages which is to be show.
     * 
     * @return
     */
    public List<Msg> getMessages2show() {
        return mMessages2show;
    }

    /** whether message center is in delete mode. */
    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    /** set message center mode. */
    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
    }

    /**
     * * get load helper ,notice that you should not get the dat set
     * directly,but use load helper to change data set,or message center will
     * not detect data set getting empty event.
     * 
     * @return
     */
    public LazyLoadingHelper getLoadHelper() {
        return mLoadHelper;
    }

    /**
     * get update status
     * 
     * @return
     */
    public UpdateStatu getUpdateStatu() {
        return mUpdateStatu;
    }

    /**
     * Constructor.
     * 
     * @param context
     * @param msgs
     */
    public MessageBaseAdapter(Context context, List<Msg> msgs) {
        mContext = (MainActivity) context;
        // mMessages = msgs;
        this.mInflater = LayoutInflater.from(mContext);
        mProcesser = new MessageProcesser(mContext);
        mLoadHelper = new LazyLoadingHelper();
        mUpdateStatu = new UpdateStatu();

        mMessages2show = new ArrayList<Msg>();
        mMessagesWait2show_pre = new ArrayList<Msg>();
        mMessagesWait2show_next = new ArrayList<Msg>();
        if (msgs != null && msgs.size() > 0) {
            mMessagesWait2show_next = msgs;
        }
        mLoadHelper.loadMorePage(1);

        isDeleteMode = false;
    }

    @Override
    public int getCount() {
        return mMessages2show.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages2show.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * get last selected position.
     * 
     * @return
     */
    public int getLastSelectedPostion() {
        return mLastSelectedPosition;
    }

    /**
     * set last selected position.
     * 
     * @param lastSelectedPosition
     */
    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.mLastSelectedPosition = lastSelectedPosition;
    }

    /**
     * set OnNoMessageLeft listener.
     * 
     * @param l OnNoMessageLeft listener;
     */
    public void setOnNoMessageLeftListener(OnNoMessageLeftListener l) {
        this.mOnNoMessageLeftListener = l;
    }

    /**
     * get OnNoMessageLeft listener.
     * 
     * @return OnNoMessageLeft listener;
     */
    public OnNoMessageLeftListener getOnNoMessageLeftListener() {
        return mOnNoMessageLeftListener;
    }

    /** view cache. */
    public static class MessageGridViewHolder {

        /** timeLabel. */
        public TextView timeLabel;

        /** message_content_type. */
        public TextView message_content_type;

        /** message_img. */
        public ImageView message_img;

        /** content_description. */
        public TextView content_description;

        /** pgm_index. */
        public TextView pgm_index;

        /** message_play. */
        public TextView message_play;

        /** message_play_layout. */
        public LinearLayout message_play_layout;

        /** type. */
        public int type;

        /** isUpdate. */
        public boolean isUpdate = false;
    }

    /**
     * update the status for the message.
     * 
     * @author charles.tai
     */
    private class UpdateStatu {

        private boolean mIsUpdate = false;

        private int mUpdatePosition = -1;

        private int mLastUpdatePosition = -1;

        public int getLastUpdatePosition() {
            return mLastUpdatePosition;
        }

        public void setLastUpdatePosition(int mLastUpdatePosition) {
            this.mLastUpdatePosition = mLastUpdatePosition;
        }

        public boolean isUpdate() {
            return mIsUpdate;
        }

        public void setIsUpdate(boolean mIsUpdate) {
            this.mIsUpdate = mIsUpdate;
        }

        public int getUpdatePosition() {
            return mUpdatePosition;
        }

        public void setUpdatePosition(int mUpdatePosition) {
            this.mUpdatePosition = mUpdatePosition;
        }

        public void setAll(boolean mIsUpdate, int mUpdatePosition, int mLastUpdatePosition) {
            setIsUpdate(mIsUpdate);
            setUpdatePosition(mUpdatePosition);
            setLastUpdatePosition(mLastUpdatePosition);
        }
    }

    /**
     * when update items ,not to load image in asyn way but sync way.
     * 
     * @param startPosition TODO
     */
    public void notifyDataSetUpdate(int startPosition) {
        mUpdateStatu.setAll(true, startPosition, -1);
        notifyDataSetChanged();
        // isUpdate = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Msg msg = (Msg) getItem(position);
        MessageGridViewHolder viewHolder = null;
        if (convertView == null || msg.mCategory == Messages.CATEGORY_VOICE) {
            viewHolder = new MessageGridViewHolder();

            convertView = mInflater.inflate(R.layout.message_item, null);

            viewHolder.timeLabel = (TextView) convertView.findViewById(R.id.timeLable);
            viewHolder.message_content_type = (TextView) convertView.findViewById(R.id.message_content_type);
            viewHolder.message_img = (ImageView) convertView.findViewById(R.id.message_img);
            viewHolder.content_description = (TextView) convertView.findViewById(R.id.content_description);
            viewHolder.pgm_index = (TextView) convertView.findViewById(R.id.pgm_index);
            viewHolder.message_play = (TextView) convertView.findViewById(R.id.message_play);
            viewHolder.message_play_layout = (LinearLayout) convertView.findViewById(R.id.message_play_layout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MessageGridViewHolder) convertView.getTag();
        }

        // Log.i(TAG, "getView() position = " + position + " isUpdate +" +
        // mUpdateStatu.isUpdate());
        viewHolder.isUpdate = mUpdateStatu.isUpdate();

        processView(viewHolder, (Msg) getItem(position));

        /**
         * enabling delete cover here to avoid screen flash ,enbling delete
         * cover after getView() will lead to screen flash
         */
        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.delete_cover);
        if (ll != null) {
            if (isDeleteMode) {
                ll.setVisibility(View.VISIBLE);
            } else {
                ll.setVisibility(View.INVISIBLE);
            }
        }

        convertView.findViewById(R.id.message_play_layout).setVisibility(View.INVISIBLE);

        /**
         * special time line display :the first one is a little different from
         * others
         */
        // TextView timeline = (TextView)
        // convertView.findViewById(R.id.timeLable);
        if (viewHolder.timeLabel != null) {

            if (position == 0 && convertView != null) {
                viewHolder.timeLabel.setBackgroundResource(R.drawable.timeline_bg_start);
            } else {
                viewHolder.timeLabel.setBackgroundResource(R.drawable.timeline_bg);
            }

        }

        if (mUpdateStatu.getLastUpdatePosition() == -1) {
            mUpdateStatu.setLastUpdatePosition(position);
        }

        if (mUpdateStatu.getLastUpdatePosition() > position) {
            if (position >= mUpdateStatu.getUpdatePosition() + mLoadHelper.numPerScreen - 1) {
                mUpdateStatu.setAll(false, -1, -1);
            }
        } else if (mUpdateStatu.getLastUpdatePosition() < position) {
            if (position == 0) {
                mUpdateStatu.setAll(false, -1, -1);
            }
        }

        // Log.i(TAG, "" + position + " >= " + (mUpdateStatu.getUpdatePosition()
        // + mLoadHelper.numPerScreen - 1));

        return convertView;
    }

    /**
     * this method is to produce view of item used to display different kind of
     * messages,such as epg,image etc.
     * 
     * @param viewHolder the original view
     * @param msg the data source
     * @return the processed view used to display different kind of messages
     */
    private void processView(MessageGridViewHolder viewHolder, Msg msg) {

        if (viewHolder.type == Messages.CATEGORY_TEXT || viewHolder.type == Messages.CATEGORY_URL) {
            // viewHolder.content_description.setText("");

            viewHolder.content_description.setBackgroundResource(0);
            viewHolder.content_description.setPadding(0, 0, 0, 0);
            // viewHolder.content_description.setTextSize(Util.getDiemnsionPixelSize(mContext,
            // R.dimen.content_description_textsize));
            viewHolder.content_description.setSingleLine(true);
            viewHolder.content_description.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
            viewHolder.content_description.getLayoutParams().height = Util.getDiemnsionPixelSize(mContext,
                    R.dimen.content_description_textsize);
            viewHolder.content_description.setTextColor(mContext.getResources().getColor(android.R.color.white));
            viewHolder.message_img.setVisibility(View.VISIBLE);
        } else {
            viewHolder.message_img.setImageResource(0);
            viewHolder.message_img.setScaleType(ScaleType.FIT_START);
            viewHolder.message_img.setVisibility(View.VISIBLE);
        }

        viewHolder.content_description.setTextSize(Util.getDiemnsionPixelSize(mContext, R.dimen.TEXT_SIZE_DEFAULT));
        viewHolder.pgm_index.setVisibility(View.INVISIBLE);
        Log.i(TAG, "description text size = " + Util.getDiemnsionPixelSize(mContext, R.dimen.TEXT_SIZE_DEFAULT));
        viewHolder.message_play_layout.setBackgroundColor(mContext.getResources().getColor(
                R.color.tanslucent_half_balck));
        ImageView img = (ImageView) viewHolder.message_play_layout.findViewById(R.id.message_play_layout_icon);
        img.setVisibility(View.VISIBLE);

        if (viewHolder.type == Messages.CATEGORY_VOICE) {
            int padding = Util.getDiemnsionPixelSize(mContext, R.dimen.imageview_padding);
            viewHolder.message_img.setBackgroundResource(R.drawable.bg_border_img);
            viewHolder.message_img.setPadding(padding, padding, padding, padding);
        }
        switch (msg.mCategory) {
            case Messages.CATEGORY_EPG:
                mProcesser.processEpg(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_EPG;
                break;
            case Messages.CATEGORY_EPG_CACHE:
                mProcesser.processEpg(viewHolder, msg);
                viewHolder.message_content_type.setText(R.string.type_epg_cache);
                viewHolder.type = Messages.CATEGORY_EPG_CACHE;
                break;
            case Messages.CATEGORY_MSG_LIVE:
                mProcesser.processLive(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_MSG_LIVE;
                break;
            case Messages.CATEGORY_IMAGE:
                mProcesser.processImage(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_IMAGE;
                break;
            case Messages.CATEGORY_TEXT:
                mProcesser.processText(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_TEXT;
                break;
            case Messages.CATEGORY_URL:
                mProcesser.processText(viewHolder, msg);
                viewHolder.message_content_type.setText(R.string.type_url);
                viewHolder.type = Messages.CATEGORY_URL;
                break;
            case Messages.CATEGORY_VIDEO:
                mProcesser.processVideo(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_VIDEO;
                break;
            case Messages.CATEGORY_APK:
                mProcesser.processApk(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_APK;
                break;
            case Messages.CATEGORY_VOICE:
                mProcesser.processVoice(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_VOICE;
                break;
            case Messages.CATEGORY_MUSIC:
                mProcesser.processMusic(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_MUSIC;
                break;
            case Messages.CATEGORY_DOCUMENT:
                mProcesser.processDocument(viewHolder, msg);
                viewHolder.type = Messages.CATEGORY_DOCUMENT;
                break;
            default:
                break;
        }

    }

    /**
     * the helper for loading message info.
     */
    public class LazyLoadingHelper {

        private int mLoadedPage = 0;

        private int numPerPage = 200;

        private int numPerScreen = 6;

        /**
         * get number of per screen.
         * 
         * @return
         */
        public int getNumPerScreen() {
            return numPerScreen;
        }

        /**
         * get number of per screen.
         * 
         * @param numPerScreen
         */
        public void setNumPerScreen(int numPerScreen) {
            this.numPerScreen = numPerScreen;
        }

        /** get loaded page. */
        public int getLoadedPage() {
            return mLoadedPage;
        }

        /** get number of item per page when loaded . */
        public int getNumPerPage() {
            return numPerPage;
        }

        /**
         * if has next page.
         * 
         * @return
         */
        public boolean hasNextPage() {
            return mMessagesWait2show_next.size() > 0;
        }

        /**
         * set number of item per page when loaded.
         * 
         * @param numPerPage number of item per page
         */
        public void setNumPerPage(int numPerPage) {
            this.numPerPage = numPerPage;
        }

        /**
         * load more pages of item, it will load MorePage*numPerPage item.
         * 
         * @param MorePage load how many pages of item
         * @return whether have more pages of message
         */
        public boolean loadMorePage(int MorePage) {
            Log.i(TAG, "before show = " + mMessages2show.size() + " wait2show = " + mMessagesWait2show_next.size());

            boolean flg = true;

            int i = MorePage * numPerPage;

            if (mMessagesWait2show_next.size() == 0) {
                flg = false;
            }

            while (i-- > 0 && mMessagesWait2show_next.size() > 0) {
                mMessages2show.add(mMessagesWait2show_next.get(0));
                mMessagesWait2show_next.remove(0);
            }
            mLoadedPage = (int) Math.ceil(mMessages2show.size() / (double) numPerPage);

            View view = mContext.findViewById(R.id.no_message_tip);
            if (mMessages2show.size() == 0) {
                isDeleteMode = false;
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }
            return flg;
        }

        /**
         * load more items, it will load msgs.size() item.
         * 
         * @param msgs the data source needs to be added
         */
        public void loadMoreMessages2Start(List<Msg> msgs) {
            if (msgs == null) {
                return;
            }

            mMessages2show.addAll(0, msgs);
            msgs.clear();

            View view = mContext.findViewById(R.id.no_message_tip);
            if (mMessages2show.size() == 0) {
                isDeleteMode = false;
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }

        }

        /** clear data set. */
        public void clearMessages() {
            mMessagesWait2show_next.clear();
            mMessagesWait2show_pre.clear();
            mMessages2show.clear();
        }

        /**
         * load user's data, it will load msgs.size() items.
         * 
         * @param msgs the data source needs to be loaded
         */
        public void loadNewUserMessages(List<Msg> msgs) {
            clearMessages();
            mMessagesWait2show_next = msgs;
            this.loadMorePage(1);
        }

        /**
         * you can't remove messages by getting the List,but you can use this
         * method ,in order to trigger the OnNoMessageLeft event.
         * 
         * @param position the position of message you want to remove in the
         *            list
         */
        public void removeMessage(int position) {
            if (mMessages2show == null) {
                return;
            }
            mMessages2show.remove(position);

            // while (mMessages2show.size() < numPerPage &&
            // mMessagesWait2show_next.size() > 0) {
            // mMessages2show.add(mMessagesWait2show_next.get(0));
            // mMessagesWait2show_next.remove(0);
            // }

            View view = mContext.findViewById(R.id.no_message_tip);
            if (mMessages2show.size() == 0) {

                setDeleteMode(false);
                view.setVisibility(View.VISIBLE);

                if (mOnNoMessageLeftListener != null) {
                    mOnNoMessageLeftListener.onNoMessage(false);
                }
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }

        /** remove all message. */
        public synchronized void removeAllMessage() {
            if (mMessages2show == null) {
                return;
            }

            clearMessages();

            View view = mContext.findViewById(R.id.no_message_tip);
            if (mMessages2show.size() == 0) {

                setDeleteMode(false);
                view.setVisibility(View.VISIBLE);

                if (mOnNoMessageLeftListener != null) {
                    mOnNoMessageLeftListener.onNoMessage(true);
                }
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * swap to Next Page.
         * 
         * @return
         */
        public boolean swap2NextPage() {

            if (mMessagesWait2show_next.size() <= 0) {
                return false;
            } else {
                for (int k = 0; k < mMessagesWait2show_next.size(); k++) {
                    Log.i(TAG, "next k = " + k + " text = " + mMessagesWait2show_next.get(k).mTitle);
                }

                mMessagesWait2show_pre.addAll(mMessages2show);
                mMessages2show.clear();
            }

            while (mMessages2show.size() < numPerPage && mMessagesWait2show_next.size() > 0) {
                mMessages2show.add(mMessagesWait2show_next.get(0));
                mMessagesWait2show_next.remove(0);
            }
            return true;
        }

        /**
         * if swap to per page.
         * 
         * @return
         */
        public boolean swap2PrePage() {

            if (mMessagesWait2show_pre.size() <= 0) {
                return false;
            } else {
                mMessagesWait2show_next.addAll(0, mMessages2show);
                mMessages2show.clear();

                for (int k = 0; k < mMessagesWait2show_next.size(); k++) {
                    Log.i(TAG, "next k = " + k + " text = " + mMessagesWait2show_next.get(k).mTitle);
                }
            }

            while (mMessages2show.size() < numPerPage && mMessagesWait2show_pre.size() > 0) {
                mMessages2show.add(0, mMessagesWait2show_pre.get(mMessagesWait2show_pre.size() - 1));
                mMessagesWait2show_pre.remove(mMessagesWait2show_pre.size() - 1);
            }
            return true;
        }

    }

    /** gridview data set reduced to empty listener. */
    public interface OnNoMessageLeftListener {
        /** do something when no message left. */
        void onNoMessage(boolean isDelAll);
    }

}
