
package com.eostek.scifly.messagecenter.logic;

import static com.eostek.scifly.messagecenter.util.Constants.FIRST_MESSAGE;

import java.net.URLDecoder;
import java.util.List;

import scifly.provider.metadata.Msg;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.eostek.scifly.messagecenter.MainActivity;
import com.eostek.scifly.messagecenter.MessageCenterHolder;
import com.eostek.scifly.messagecenter.ui.MessageBaseAdapter;
import com.eostek.scifly.messagecenter.ui.animation.UIAnimationUtil;

/**
 * the Helper for swicth user.
 */
public class UserSwitchHelper {

    private static final String TAG = "UserSwitchHelper";

    private MainActivity mContext;

    private MessageCenterHolder mHolder;

    private MessageDisplayer mDisplayer;

    private String mCurrentUserId = "";

    private String mCurrentUserName = "";

    /**
     * get current userName.
     */
    public String getCurrentUserName() {
        return mCurrentUserName;
    }

    /**
     * set current userName
     */
    public void setCurrentUserName(String mCurrentUserName) {
        this.mCurrentUserName = mCurrentUserName;
    }

    /**
     * get current userId
     */
    public String getCurrentUserId() {
        return mCurrentUserId;
    }

    /**
     * set current userId
     */
    public void setCurrentUserId(String mCurrentUserId) {
        this.mCurrentUserId = mCurrentUserId;
    }

    /**
     * the constructor.
     * 
     * @param mContext application context.
     */
    public UserSwitchHelper(MainActivity mContext) {
        this.mContext = mContext;
        mHolder = mContext.getHolder();
        mDisplayer = new MessageDisplayer(mContext);
    }

    /**
     * switch to show certain messages own by certain user.
     * 
     * @param userId switch to show messages own by this userId.
     * @param userName user's name
     */
    @SuppressWarnings("deprecation")
    public void switch2User(String userId, String userName) {

        setCurrentUserId(userId);
        try {
            setCurrentUserName(URLDecoder.decode(userName));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            setCurrentUserName(userName);
        }

        final List<Msg> msgs = mContext.getCenterManager().getMessageBySenderCategory("" + userId);

        final MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();

        mContext.getCenterManager().readMsgsBySender(userId);

        adapter.getLoadHelper().loadNewUserMessages(msgs);

        mHolder.getMessageGridView().setLayoutAnimation(UIAnimationUtil.getLayoutShowAnimation(0));

        mHolder.initBlockView();

        adapter.notifyDataSetChanged();

        final ViewTreeObserver observer = mHolder.getMessageGridView().getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (observer.isAlive()) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    ViewTreeObserver observer = mHolder.getMessageGridView().getViewTreeObserver();
                    observer.removeOnGlobalLayoutListener(this);
                }

                if (mHolder.getMessageGridView().getCount() > 0) {
                    mHolder.getMessageGridView().requestFocusFromTouch();
                    mHolder.getMessageGridView().setSelection(FIRST_MESSAGE);
                    adapter.setLastSelectedPosition(FIRST_MESSAGE);
                }
                mDisplayer.clearPlayLayout();
                View view = mHolder.getMessageGridView().getChildAt(FIRST_MESSAGE);
                if (!adapter.isDeleteMode() && view != null && !view.isSelected()) {
                    mDisplayer.enablePlayLayout(FIRST_MESSAGE);
                }
            }
        });

    }

    /**
     * delete messages own by certain user.
     * 
     * @param userId the userId
     * @return whether delete successed
     */
    public boolean deleteUser(String userId) {
        return mContext.getCenterManager().delMessageBySender("" + userId);
    }

    /**
     * delete messages own by current user whose messages are showing.
     * 
     * @param userId the userId
     * @return whether delete successed
     */
    public boolean deleteCurrentUser() {
        return deleteUser(mCurrentUserId);
    }

}
