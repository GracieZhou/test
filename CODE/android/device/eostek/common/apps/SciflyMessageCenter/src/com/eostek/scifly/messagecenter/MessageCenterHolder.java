
package com.eostek.scifly.messagecenter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.eostek.scifly.messagecenter.model.MessageSender;
import com.eostek.scifly.messagecenter.ui.MessageBaseAdapter;
import com.eostek.scifly.messagecenter.ui.SenderContainer;
import com.eostek.scifly.messagecenter.ui.SenderContainer.OnUserPressedListener;
import com.eostek.scifly.messagecenter.ui.animation.UIAnimationUtil;
import com.jess.ui.TwoWayGridView;

/**
 * Holder of MessageCenter Home Activity.
 */
public class MessageCenterHolder {

    /**
     * Tag used to show in logcat.
     */
    public static final String TAG = "MessageCenterHolder";

    private MainActivity mContext;

    private SenderContainer mSenderGridView;

    private TwoWayGridView mMessageGridView;

    private HorizontalScrollView mSenderScrollView;

    private LinearLayout mDeleteLayout;

    private LinearLayout mShieldLayout;

    private float mDensity;

    private int mWindowWitdh;

    private int mWindowHeight;

    private int mSenderColumnWidth = 170;

    private int mItemNumPerScreen = 6;

    /**
     * get delete layout.
     * 
     * @return
     */
    public LinearLayout getDeleteLayout() {
        return mDeleteLayout;
    }

    /**
     * get block layout
     * 
     * @return
     */
    public LinearLayout getShieldLayout() {
        return mShieldLayout;
    }

    /**
     * get item num of perscreen.
     * 
     * @return
     */
    public int getItemNumPerScreen() {
        return mItemNumPerScreen;
    }

    /**
     * get window witdh.
     * 
     * @return
     */
    public int getWindowWitdh() {
        return mWindowWitdh;
    }

    /**
     * Get context.
     * 
     * @return
     */
    public Activity getContext() {
        return mContext;
    }

    /**
     * Get SenderGridView.
     * 
     * @return
     */
    public SenderContainer getSenderGridView() {
        return mSenderGridView;
    }

    /**
     * set sender grid on item click listener
     * 
     * @param onItemClicked
     */
    public void setSenderGridOnItemClickListener(OnUserPressedListener onItemClicked) {
        mSenderGridView.setOnUserPressedListener(onItemClicked);
    }

    /**
     * Get MessageGridView.
     * 
     * @return
     */
    public TwoWayGridView getMessageGridView() {
        return mMessageGridView;
    }

    /**
     * get sender scrollview
     * 
     * @return
     */
    public HorizontalScrollView getSenderScrollView() {
        return mSenderScrollView;
    }

    /**
     * Get Density.
     * 
     * @return
     */
    public float getDensity() {
        return mDensity;
    }

    /**
     * Get SenderColumnWidth.
     * 
     * @return
     */
    public int getSenderColumnWidth() {
        return mSenderColumnWidth;
    }

    /**
     * the constructor
     * 
     * @param context the context of application.
     */
    public MessageCenterHolder(Context context) {
        mContext = (MainActivity) context;
    }

    /**
     * do the find views work.
     */
    public void getViews() {

        getWindowProperty();

        // find views
        mDeleteLayout = (LinearLayout) mContext.findViewById(R.id.delete_message);
        mShieldLayout = (LinearLayout) mContext.findViewById(R.id.shield_message);

        mMessageGridView = (TwoWayGridView) mContext.findViewById(R.id.gridview);
        mMessageGridView.setSmoothScrollbarEnabled(true);

        mSenderScrollView = (HorizontalScrollView) mContext.findViewById(R.id.user_scroll_view);
        mSenderGridView = (SenderContainer) mContext.findViewById(R.id.sender_gridview);

    }

    /**
     * add grid view of sender.
     * 
     * @param sender
     * @param index
     */
    public void addSenderView(MessageSender sender, int index) {
        mSenderGridView.addSender(sender, index);
    }

    /**
     * initialize sender view.
     */
    public void initSenderGirdView(List<MessageSender> mSenders) {
        mSenderGridView.setGridLayoutAdapter(mSenders);

    }

    /**
     * initialize MessageScrollView.
     */
    public void initMessageGirdView() {

        // initialize message view
        mMessageGridView.setAdapter(new MessageBaseAdapter(mContext, null));

        mMessageGridView.setNumRows(1);
        mMessageGridView.setNumColumns((int) getItemNumPerScreen());
        mMessageGridView.setSmoothScrollbarEnabled(true);
        mMessageGridView.setSelector(mContext.getResources().getDrawable(R.drawable.message_outter_focus));

        mMessageGridView.setLayoutAnimation(UIAnimationUtil.getLayoutShowAnimation(300));

    }

    private void getWindowProperty() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mDensity = outMetrics.density;
        mWindowWitdh = outMetrics.widthPixels;
        mWindowHeight = outMetrics.heightPixels;
        Log.i(TAG, "windowWidth = " + mWindowWitdh + " windowHeight = " + mWindowHeight + " mDensity = " + mDensity
                + " densityDpi = " + outMetrics.densityDpi);
    }

    /**
     * initialize BlockView Tip.
     */
    public void initBlockView() {
        mSenderGridView.setShieldTip(this);
    }
}
