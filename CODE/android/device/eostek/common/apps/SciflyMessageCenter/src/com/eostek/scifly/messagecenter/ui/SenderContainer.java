
package com.eostek.scifly.messagecenter.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scifly.provider.SciflyStore.Messages;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.scifly.messagecenter.MainActivity;
import com.eostek.scifly.messagecenter.MessageCenterHolder;
import com.eostek.scifly.messagecenter.R;
import com.eostek.scifly.messagecenter.logic.MessageDataCache;
import com.eostek.scifly.messagecenter.model.MessageSender;
import com.jess.ui.TwoWayGridView;

/**
 * GridLayout for Sender list.
 * 
 * @author Youpeng
 */
public class SenderContainer extends GridLayout {

    /**
     * views of a sender.
     * 
     * @author Youpeng
     */
    protected class ItemHolder {
        private int position;

        private ImageView mUserImage;

        private TextView mUnreadTxt;

        private ImageView mBlockImg;

        private MarqueeTextView mNickName;

        private LinearLayout mUserInfoLayout;

        private LinearLayout mUserInfoBkgroundLayout;

        private TextView mDescriptionTxt;

        private ImageView mPointImg;

        private LinearLayout mMainLayout;

        private LinearLayout mSlaveLayout;

        /**
         * show info.
         * 
         * @param show
         */
        public void showInfo(boolean show) {
            if (show) {
                mUserInfoLayout.setVisibility(View.VISIBLE);
            } else {
                mUserInfoLayout.setVisibility(View.GONE);
            }
        }

        /**
         * replace background when changed.
         * 
         * @param change
         */
        public void replaceBackground(boolean change) {
            if (change) {
                mUserInfoLayout.setBackgroundColor(Color.argb(00, 111, 111, 44));
                mUserInfoBkgroundLayout.setBackgroundColor(Color.argb(00, 111, 111, 44));
            } else {
                mUserInfoLayout.setBackgroundColor(Color.rgb(255, 255, 255));
                mUserInfoBkgroundLayout.setBackgroundColor(Color.rgb(255, 128, 0));
            }

        }

        /**
         * show pointer.
         * 
         * @param show
         */
        public void showPointer(boolean show) {
            if (show) {
                mPointImg.setVisibility(View.VISIBLE);
            } else {
                mPointImg.setVisibility(View.INVISIBLE);
            }
        }

        public void showBlockImg(boolean isBlock) {
            if (isBlock) {
                mBlockImg.setVisibility(View.VISIBLE);
            } else {
                mBlockImg.setVisibility(View.GONE);
            }
        }
    }

    private static final int UN_FOCUSED = -1;

    private int mLastFocusedPosition = -1;

    private int mCurrentFucusedPosition = 0;

    private int mViewCount = 0;

    private int mCurrentPressedItem = 0;

    private MessageDataCache mImageLoader;

    private Map<String, String> mSenderMaps = new HashMap<String, String>();

    private List<MessageSender> mSenders = new ArrayList<MessageSender>();

    private MessageSender mCurrentSender;

    /**
     * the interface of ItemPressedListener.
     */
    public interface OnItemPressedListener {
        /**
         * the listener of message item pressed.
         * 
         * @param pressedView
         * @param position
         */
        void onItemPressed(View pressedView, int position);
    }

    /**
     * the interface of UserPressedListener
     */
    public interface OnUserPressedListener {
        /**
         * the listener of user pressed.
         * 
         * @param position
         * @param userId
         */
        void onUserPressed(int position, MessageSender userId);
    }

    /**
     * Implementation.
     */
    public OnItemPressedListener onItemPressedListener = new OnItemPressedListener() {

        @Override
        public void onItemPressed(View pressedView, int position) {
            Log.e(TAG, "need to process....:" + position);
            if (mSenders == null || mSenders.size() <= 0 || position < 0) {
                return;
            }
            mCurrentPressedItem = position;
            mCurrentSender = mSenders.get(mCurrentPressedItem);

            Integer lastPosition = (Integer) SenderContainer.this.getTag();
            if (lastPosition == null) {
                lastPosition = 0;
            }
            View oldItemView = SenderContainer.this.getChildAt(lastPosition);
            if (oldItemView != null) {
                ItemHolder holder = (ItemHolder) oldItemView.getTag();
                if (holder != null) {
                    holder.showInfo(false);
                    holder.showPointer(false);
                }
            }
            View newItemView = SenderContainer.this.getChildAt(position);
            if (newItemView != null) {
                ItemHolder holder = (ItemHolder) newItemView.getTag();
                if (holder != null) {
                    holder.showInfo(true);
                    holder.showPointer(true);
                }
            }

            // ItemHolder currentHolder = pressedView.getTag();

            SenderContainer.this.setTag(position);

            if (mOnItemClicked != null) {
                mOnItemClicked.onUserPressed(position, mSenders.get(position));
            }
            LinearLayout shieldLayout = ((MainActivity) mContext).getHolder().getShieldLayout();
            TextView shield_message_tip = (TextView) shieldLayout.findViewById(R.id.shield_message_tip);
            MessageSender currentsender = mSenders.get(position);

            if (currentsender.getSource() != Messages.SOURCE_CPE) {

                shieldLayout.setEnabled(true);
                shieldLayout.setVisibility(View.VISIBLE);
                if (currentsender.isblocked()) {
                    shield_message_tip.setText(R.string.cancle_blockInfo);
                } else {
                    shield_message_tip.setText(R.string.blockInfo);
                }

            } else {
                shieldLayout.setEnabled(false);
                shieldLayout.setVisibility(View.GONE);

            }

        }
    };

    private OnUserPressedListener mOnItemClicked;

    /**
     * setOnUserPressedListener
     * 
     * @param onItemClicked
     */
    public void setOnUserPressedListener(OnUserPressedListener onItemClicked) {
        this.mOnItemClicked = onItemClicked;
    }

    private Context mContext;

    private LayoutInflater mInflater;

    private static final boolean DEBUG = true;

    private static final int FIRST_MARGIN_LEFT = 60;

    private static final int MARGIN_LEFT = 10;

    private static final String TAG = "SenderContainer";

    /**
     * Constructor.
     * 
     * @param context
     */
    public SenderContainer(Context context) {
        super(context);
        this.mContext = context;
        mImageLoader = MessageDataCache.getCacheLoader(mContext);
    }

    /**
     * Constructor.
     * 
     * @param context
     * @param attrs
     */
    public SenderContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mImageLoader = MessageDataCache.getCacheLoader(mContext);
    }

    /**
     * Constructor.
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SenderContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mImageLoader = MessageDataCache.getCacheLoader(mContext);
    }

    /**
     * initialize sender view.
     */
    public void setGridLayoutAdapter(List<MessageSender> senders) {
        this.removeAllViews();
        this.mSenders = senders;
        mViewCount = senders.size();
        Log.i(TAG, "setGridLayoutAdapter.currentSender:" + mCurrentSender);

        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int insertPosition = 0; insertPosition < mViewCount; insertPosition++) {

            MessageSender sender = senders.get(insertPosition);
            if (sender.equals(mCurrentSender)) {
                mCurrentPressedItem = insertPosition;
            }
            addView(sender, insertPosition);
        }

    }

    /**
     * add new senders
     * 
     * @param senders
     */
    public void addNewSenders(List<MessageSender> senders) {
        setGridLayoutAdapter(senders);
    }

    private void updateUnreadCount(MessageSender sender) {
        if (sender == null) {
            return;
        }
        for (int i = 0; i < mSenders.size(); i++) {
            if (mSenders.get(i).equals(sender)) {
                View convertView = this.getChildAt(i);
                TextView unReadText = (TextView) convertView.findViewById(R.id.unread_msg_count);
                int unreadCount = sender.getUnReadCount();
                if (unReadText != null) {
                    if (unreadCount == 0) {
                        unReadText.setVisibility(View.INVISIBLE);
                    } else {
                        unReadText.setVisibility(View.VISIBLE);
                        unReadText.setText(unreadCount + "");
                    }
                }
                continue;
            }
        }
    }

    private int getFocusedPosition() {
        int count = SenderContainer.this.getChildCount();
        for (int index = 0; index < count; index++) {
            View view = SenderContainer.this.getChildAt(index);
            if (view.isFocused()) {
                return index;
            }
        }
        return -1;
    }

    private void addView(MessageSender sender, int position) {
        mSenderMaps.put(sender.getSenderId() + sender.getName(), sender.getSenderId() + sender.getName());

        View convertView = mInflater.inflate(R.layout.user_item, null);

        ItemHolder holder = new ItemHolder();
        holder.position = position;
        holder.mUserImage = (ImageView) convertView.findViewById(R.id.user_face);
        holder.mUnreadTxt = (TextView) convertView.findViewById(R.id.unread_msg_count);
        holder.mBlockImg = (ImageView) convertView.findViewById(R.id.block_user_icon);

        holder.mPointImg = (ImageView) convertView.findViewById(R.id.bg_arrow);
        holder.mNickName = (MarqueeTextView) convertView.findViewById(R.id.user_nickname);
        holder.mDescriptionTxt = (TextView) convertView.findViewById(R.id.user_message_type);
        holder.mUserInfoLayout = (LinearLayout) convertView.findViewById(R.id.info_scifly);
        holder.mUserInfoBkgroundLayout = (LinearLayout) convertView.findViewById(R.id.info_bkground);
        holder.mMainLayout = (LinearLayout) convertView.findViewById(R.id.first_layout);
        holder.mSlaveLayout = (LinearLayout) convertView.findViewById(R.id.second_layout);
        holder.mBlockImg = (ImageView) convertView.findViewById(R.id.block_user_icon);

        registListeners(convertView, holder);

        if (position == 0) {
            // holder.showInfo(true);
            // holder.showPointer(true);
            convertView.setLayoutParams(getGridPatams(FIRST_MARGIN_LEFT));
        } else {
            convertView.setLayoutParams(getGridPatams(MARGIN_LEFT));

        }

        if (Messages.USERID_CPE.equals(sender.getSenderId())) {
            holder.mUserImage.setImageResource(R.drawable.system_userface);
        } else {
            mImageLoader.loadImage(sender.getImgURL(), holder.mUserImage, R.drawable.msg_photo_img,
                    R.drawable.msg_photo_img, R.drawable.msg_photo_img);
        }

        String nickName;
        try {
            nickName = URLDecoder.decode(sender.getName());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            nickName = sender.getName();
        }
        holder.mNickName.setText(nickName);
        switch (sender.getSource()) {
            case Messages.SOURCE_CPE:
                holder.mNickName.setText(mContext.getString(R.string.msg_from_cpe_desc));
                holder.mDescriptionTxt.setText(R.string.msg_from_cpe);
                break;
            case Messages.SOURCE_ISYNERGY:
                holder.mDescriptionTxt.setText(R.string.msg_from_isynergy);
                break;
            case Messages.SOURCE_WECHAT:
                holder.mDescriptionTxt.setText(R.string.msg_from_wechat);
                break;
            case Messages.SOURCE_SCIFLYKU:
                holder.mDescriptionTxt.setText(R.string.msg_from_scifly);
                break;
            default:
                if (Messages.USERID_CPE.equals(sender.getSenderId())) {
                    holder.mNickName.setText(mContext.getString(R.string.msg_from_cpe_desc));
                    holder.mDescriptionTxt.setText(R.string.msg_from_cpe);
                } else {
                    holder.mDescriptionTxt.setText(R.string.msg_from_null);
                }
                break;
        }
        // unread msg:
        if (sender.getUnReadCount() > 0) {
            holder.mUnreadTxt.setVisibility(View.VISIBLE);
            holder.mUnreadTxt.setText(sender.getUnReadCount() + "");
        }

        convertView.setTag(holder);

        if (mCurrentSender == null && position == 0) {
            holder.showInfo(true);
            holder.replaceBackground(true);
            holder.showPointer(true);
            mCurrentSender = sender;
        } else if (sender.equals(mCurrentSender)) {
            mCurrentFucusedPosition = position;
            holder.showInfo(true);
            holder.replaceBackground(true);
            holder.showPointer(true);
            // holder.showShieldImg(sender.isblocked());
        }
        if (sender.isblocked()) {
            holder.mBlockImg.setVisibility(View.VISIBLE);
        }
        this.addView(convertView, position);
    }

    private void registListeners(View convertView, final ItemHolder holder) {

        convertView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ItemHolder holder = (ItemHolder) v.getTag();

                if (hasFocus) {
                    print("focused:" + hasFocus + " ;lastPosition:" + mLastFocusedPosition + "; currentPosition:"
                            + mCurrentFucusedPosition);
                    if (mLastFocusedPosition == UN_FOCUSED) {// refocused.
                        View view = SenderContainer.this.getChildAt(mCurrentFucusedPosition);
                        if (view != null) {
                            view.requestFocus();
                        }
                        print("refocused...");
                        holder.replaceBackground(false);
                    } else {
                        mCurrentFucusedPosition = holder.position;
                        holder.showInfo(true);
                        holder.replaceBackground(false);
                        mCurrentSender = mSenders.get(mCurrentFucusedPosition);
                    }

                } else {
                    mLastFocusedPosition = getFocusedPosition();
                    if (mLastFocusedPosition != UN_FOCUSED) {
                        holder.showInfo(false);
                    } else {
                        print("focus escaped.");
                        holder.replaceBackground(true);
                    }
                }

            }
        });

        holder.mUserImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // print("1111");
                onItemPressedListener.onItemPressed(v, holder.position);
            }
        });
        holder.mUserInfoLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // print("2222");
                onItemPressedListener.onItemPressed(v, holder.position);
            }
        });
        holder.mSlaveLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // print("3333");
                onItemPressedListener.onItemPressed(v, holder.position);
            }
        });
        holder.mMainLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // print("11411--->" + holder.position);
                holder.showInfo(true);
                try {
                    onItemPressedListener.onItemPressed(v, holder.position);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.mMainLayout.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(TAG, "" + "holder.first_layout.onKeyDown...");
                // ...............

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        onItemPressedListener.onItemPressed(v, holder.position);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        Log.i("sender", "sender down");
                        TwoWayGridView gridview = ((MainActivity) mContext).getHolder().getMessageGridView();
                        if (gridview.getCount() == 0) {
                            ((MainActivity) mContext).getHolder().getDeleteLayout().requestFocus();
                            return true;
                        }
                        ((MainActivity) mContext).getDisplayer().doWhenGetFocusAgain();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        // FIXME .
                        return false;
                    }

                }
                return false;
            }
        });

    }

    private GridLayout.LayoutParams getGridPatams(int marginLeft) {
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.setMargins(marginLeft, 0, 0, 0);
        lp.setGravity(Gravity.CENTER_HORIZONTAL);
        return lp;
    }

    /**
     * add Sender.
     * 
     * @param sender
     * @param index
     */
    public void addSender(MessageSender sender, int index) {
        addView(sender, index);
        mSenders.add(index, sender);
    }

    /**
     * remove Sender.
     * 
     * @param index
     */
    public void removeSender(int index) {
        // if (index == 0 || index >= this.getChildCount()) {
        // return;
        // }
        if (index >= this.getChildCount()) {
            return;
        }
        this.removeViewAt(index);

        setChildSelected(index);
        // }
    }

    public void setChildSelected(int index) {
        MessageSender sender = mSenders.get(index);
        mSenderMaps.remove(sender.getSenderId() + sender.getName());
        mSenders.remove(index);

        int totalCount = this.getChildCount();
        for (int i = index; i < totalCount; i++) {
            View convertView = this.getChildAt(i);
            if (convertView != null) {
                ItemHolder holder = (ItemHolder) convertView.getTag();
                if (holder != null) {
                    holder.position--;
                    print("removed.holder.position:" + holder.position);
                }
            }
        }

        // if (index > 0) {
        if (index == 0) {
            index = 0;
        } else {
            index--;
        }
        if (mSenders.size() == 0) {
            return;
        }

        if (mOnItemClicked != null) {
            mOnItemClicked.onUserPressed(index, mSenders.get(index));
            View view = this.getChildAt(index);
            if (view != null) {
                onItemPressedListener.onItemPressed(view, index);
                mCurrentFucusedPosition = index;

                View convertView = this.getChildAt(index);
                LinearLayout bgLayout = (LinearLayout) convertView.findViewById(R.id.info_scifly);
                LinearLayout infoLayout = (LinearLayout) convertView.findViewById(R.id.info_bkground);

                bgLayout.setBackgroundColor(Color.argb(00, 111, 111, 44));
                infoLayout.setBackgroundColor(Color.argb(00, 111, 111, 44));
            } else {
                print("preview is null;");
            }
        }
    }

    /**
     * test code.
     * 
     * @param show
     */
    public void test(boolean show) {
        View item = this.getChildAt(0);
        ItemHolder holder = (ItemHolder) item.getTag();
        if (holder != null) {
            if (show) {
                holder.showInfo(true);
            } else {
                holder.showInfo(false);
            }
        }
    }

    /**
     * get selected item of sender.
     * 
     * @return
     */
    public int getSelectedItem() {
        print("mSelectedItemPosition:" + mCurrentPressedItem);
        return mCurrentPressedItem;
    }

    /**
     * position holder
     */
    private class PositionHolder {
        private int mLastFocusedPosition = -1;

        private int mCurrentFucusedPosition = 0;

        private int mViewCount = 0;

        private int mCurrentPressedItem = 0;
    }

    private void print(String log) {
        if (DEBUG) {
            Log.i(TAG, log);
        }
    }

    public void setShieldTip(MessageCenterHolder holder) {

        TextView shield_message_tip = (TextView) holder.getShieldLayout().findViewById(R.id.shield_message_tip);
        LinearLayout shieldLayout = ((MainActivity) mContext).getHolder().getShieldLayout();
        if (mCurrentSender.getSource() != Messages.SOURCE_CPE) {
            shieldLayout.setEnabled(true);
            shieldLayout.setVisibility(View.VISIBLE);

            if (mCurrentSender.isblocked()) {
                shield_message_tip.setText(mContext.getResources().getString(R.string.cancle_blockInfo));
            } else {
                shield_message_tip.setText(mContext.getResources().getString(R.string.blockInfo));
            }

        } else {
            shieldLayout.setEnabled(false);
            shieldLayout.setVisibility(View.INVISIBLE);
        }

    }

    public MessageSender getCurrentSender() {
        return mCurrentSender;
    }

}
