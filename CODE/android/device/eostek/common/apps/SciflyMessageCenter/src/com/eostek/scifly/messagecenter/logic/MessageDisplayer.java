
package com.eostek.scifly.messagecenter.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import scifly.provider.metadata.Msg;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.scifly.messagecenter.MainActivity;
import com.eostek.scifly.messagecenter.MessageCenterHolder;
import com.eostek.scifly.messagecenter.R;
import com.eostek.scifly.messagecenter.datacenter.MsgCenterManager;
import com.eostek.scifly.messagecenter.model.MessageSender;
import com.eostek.scifly.messagecenter.ui.MessageBaseAdapter;
import com.eostek.scifly.messagecenter.ui.animation.UIAnimationUtil;
import com.eostek.scifly.messagecenter.ui.dialog.TransparentDialog;
import com.jess.ui.TwoWayGridView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;

/**
 * The Displayer of Message.
 */
public class MessageDisplayer {

    private static final String TAG = "MessageDisplayer";

    private static final int LOAD_MORE_MESSAGE = 0x01;

    /** no except position. */
    public static final int NO_EXCEPT_POSITION = -1;

    private MainActivity mContext;

    private TwoWayGridView mGridView;

    private MessageCenterHolder mHolder;

    private MessageBaseAdapter adapter;

    private List<OnNewMessageListener> mOnNewMessageListeners = new ArrayList<MessageDisplayer.OnNewMessageListener>();

    private TransparentDialog mDialog;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_MORE_MESSAGE:
                    adapter.getLoadHelper().loadMorePage(1);
                    adapter.notifyDataSetUpdate(mGridView.getFirstVisiblePosition());

                    final ViewTreeObserver observer = mHolder.getMessageGridView().getViewTreeObserver();
                    observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            observer.removeOnGlobalLayoutListener(this);
                            if (!adapter.isDeleteMode()) {
                                enablePlayLayout(getCurrentPositionOnScreen());
                            }
                        }
                    });

                    mDialog.dismiss();
                    break;
                default:
                    break;
            }

        }

    };

    /**
     * Constructor.
     * 
     * @param mContext
     */
    public MessageDisplayer(MainActivity mContext) {
        this.mContext = mContext;
        mHolder = mContext.getHolder();
        mGridView = mContext.getHolder().getMessageGridView();
        adapter = (MessageBaseAdapter) mGridView.getAdapter();
        mDialog = new TransparentDialog(mContext);
    }

    /**
     * key right pressed
     * 
     * @return
     */
    public boolean keyRightPressed() {
        final int itemPerScreen = (int) mHolder.getItemNumPerScreen();
        final ViewTreeObserver observer = mHolder.getMessageGridView().getViewTreeObserver();
        if (mGridView.getSelectedItemPosition() >= mGridView.getCount() - itemPerScreen / 2 - 1
                && adapter.getLoadHelper().hasNextPage()) {
            Log.i(TAG, "keyRightPressed");
            mDialog.show();
            mHandler.sendEmptyMessageDelayed(LOAD_MORE_MESSAGE, 500);
            return true;
        }
        return false;
    }

    /**
     * key left pressed
     * 
     * @return
     */
    public boolean keyLeftPressed() {
        if (mGridView.getSelectedItemPosition() <= (int) (mGridView.getCount() - mHolder.getItemNumPerScreen() / 2) - 1
                && mGridView.getSelectedItemPosition() > (int) (mHolder.getItemNumPerScreen() / 2)) {
            Log.i(TAG, "keyLeftPressed");
        }
        return false;
    }

    /**
     * do when get focus again
     */
    public void doWhenGetFocusAgain() {
        Log.i(TAG, "get focus");
        adapter = (MessageBaseAdapter) mGridView.getAdapter();
        mGridView.requestFocusFromTouch();
        mGridView.setSelection(adapter.getLastSelectedPostion());
        if (adapter.isDeleteMode()) {
            return;
        }
        View lastChild;
        lastChild = mGridView.getSelectedView();
        if (lastChild != null) {
            View lastChildLayout = (LinearLayout) lastChild.findViewById(R.id.message_play_layout);
            lastChildLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * delete message
     * 
     * @param position
     */
    public void deleteMessage(int position) {
        try {
            View view = mGridView.getChildAt(position);
            if (view != null) {
                final MessageBaseAdapter adapter = (MessageBaseAdapter) mGridView.getAdapter();
                adapter.setDeleteMode(true);
                adapter.getLoadHelper().removeMessage(mGridView.getPositionForView(view));
                adapter.notifyDataSetUpdate(position);

                final int tempPosition = position;

                final ViewTreeObserver observer = mHolder.getMessageGridView().getViewTreeObserver();
                observer.addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        observer.removeOnPreDrawListener(this);
                        List<Animator> resultList = new LinkedList<Animator>();
                        int oldPosition = tempPosition;
                        int endPosition = (int) (mHolder.getItemNumPerScreen() + 1.5);
                        for (int pos = oldPosition; pos < endPosition; pos++) {
                            View view = mHolder.getMessageGridView().getChildAt(pos);
                            if (view != null) {
                                resultList.add(UIAnimationUtil.createTranslationAnimations(view, view.getWidth(), 0, 0,
                                        0));
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

                // mHolder.MessageGridViewResize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * delete message from ui and database.
     */
    public void deleteAllMessage() {

        adapter.getLoadHelper().removeAllMessage();
        adapter.notifyDataSetChanged();

        mContext.getSwitchHelper().deleteCurrentUser();
    }

    /**
     * enter pressed resume
     * 
     * @param position
     */
    public void enterPressedResume(int position) {
        try {
            for (int i = 0; i <= mHolder.getItemNumPerScreen(); i++) {
                View view = mHolder.getMessageGridView().getChildAt(i).findViewById(R.id.message_main_content);
                if (view != null) {
                    view.setBackgroundResource(R.drawable.message_inner_focus);
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();

        }
    }

    /**
     * clear play layout
     */
    public void clearPlayLayout() {
        View view;
        for (int i = 0; i <= mHolder.getItemNumPerScreen(); i++) {
            view = mGridView.getChildAt(i);
            if (view != null) {
                try {
                    view.findViewById(R.id.message_play_layout).setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * enable play layout
     * 
     * @param position
     */
    public void enablePlayLayout(int position) {
        final MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();
        View view = mGridView.getChildAt(position);
        if (view != null) {
            try {
                Log.i(TAG, "enable +" + position);
                Log.i(TAG, "ad c" + adapter.getCount());
                view.findViewById(R.id.message_play_layout).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearMessageBackgroud(int exceptPosition) {
        View view;
        for (int i = 0; i <= mHolder.getItemNumPerScreen(); i++) {
            view = mGridView.getChildAt(i);
            if (view != null) {
                try {
                    if (i != exceptPosition) {
                        view.findViewById(R.id.message_main_content).setBackgroundResource(
                                R.drawable.message_outter_focus);
                        view.findViewById(R.id.message_main_content_border).setBackgroundResource(
                                R.drawable.message_outter_focus);
                    } else {
                        view.findViewById(R.id.message_main_content).setBackgroundResource(
                                R.drawable.list_selector_background_focus);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void recoverMessageBackgroud() {
        View view;
        for (int i = 0; i <= mHolder.getItemNumPerScreen(); i++) {
            view = mGridView.getChildAt(i);
            if (view != null) {
                try {
                    view.findViewById(R.id.message_main_content).setBackgroundResource(R.drawable.message_inner_focus);
                    view.findViewById(R.id.message_main_content_border).setBackgroundResource(
                            R.drawable.selected_white_border);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** enable the cover in front of the item. */
    public void enableDeleteCover(final boolean isAnimationShow) {

        new Thread(new Runnable() {
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        View view;
                        View cover;
                        for (int i = 0; i < mHolder.getMessageGridView().getCount(); i++) {
                            try {
                                view = mHolder.getMessageGridView().getChildAt(i);
                                cover = view.findViewById(R.id.delete_cover);
                                if (isAnimationShow) {
                                    cover.startAnimation(UIAnimationUtil.getViewShowAnimation());
                                }

                                cover.setVisibility(View.VISIBLE);
                            } catch (NullPointerException e) {
                                break;
                            }

                        }
                        view = mHolder.getMessageGridView().getChildAt(
                                mHolder.getMessageGridView().getSelectedItemPosition());
                        if (view != null) {
                            view.findViewById(R.id.message_play_layout).setVisibility(View.INVISIBLE);
                        }

                    }
                });
            }
        }).start();
    }

    /**
     * disable the cover in front of the item.
     * 
     * @param isAnimationShow
     */
    public void disableDeleteCover(final boolean isAnimationShow) {

        new Thread(new Runnable() {
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        View view;
                        View cover;

                        view = mHolder.getMessageGridView().getChildAt(
                                mHolder.getMessageGridView().getSelectedItemPosition());
                        if (view != null) {
                            view.findViewById(R.id.message_play_layout).setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < mHolder.getMessageGridView().getCount(); i++) {
                            try {
                                view = mHolder.getMessageGridView().getChildAt(i);
                                cover = view.findViewById(R.id.delete_cover);
                                if (isAnimationShow) {
                                    cover.startAnimation(UIAnimationUtil.getViewAlphaHideAnimation());
                                }
                                cover.setVisibility(View.INVISIBLE);
                            } catch (NullPointerException e) {
                                break;
                            }
                        }

                    }
                });
            }
        }).start();
    }

    /**
     * UI switch to DeleteMode
     */
    public void UI2DeleteMode() {
        TextView tip = (TextView) mHolder.getDeleteLayout().findViewById(R.id.delete_message_tip);
        tip.setText(mContext.getResources().getString(R.string.delete_all));
    }

    /**
     * UI switch to displayMode
     */
    public void UI2DisplayMode() {
        TextView tip = (TextView) mHolder.getDeleteLayout().findViewById(R.id.delete_message_tip);
        tip.setText(mContext.getResources().getString(R.string.deleteInfo));
    }

    /**
     * UI switch to BlockMode
     */
    public void UI2BlockMode(boolean blocked) {
        int currentSenderIndex = mHolder.getSenderGridView().getSelectedItem();

        ImageView shield_user_icon = (ImageView) mHolder.getSenderGridView().getChildAt(currentSenderIndex)
                .findViewById(R.id.block_user_icon);
        TextView shield_message_tip = (TextView) mHolder.getShieldLayout().findViewById(R.id.shield_message_tip);

        if (blocked) {
            shield_user_icon.setVisibility(View.VISIBLE);
            shield_message_tip.setText(mContext.getResources().getString(R.string.cancle_blockInfo));

        } else {
            shield_user_icon.setVisibility(View.GONE);
            shield_message_tip.setText(mContext.getResources().getString(R.string.blockInfo));
        }
    }

    /**
     * update user message
     * 
     * @param centerManager
     * @param switchHelper
     * @param mMessageList
     * @return
     */
    public int updateUserMessage(MsgCenterManager centerManager, UserSwitchHelper switchHelper,
            final List<Msg> mMessageList) {

        Log.d(TAG, ">>> DataBaseHasChaneged <<<");

        final MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();
        final int count = mMessageList.size();
        Log.i(TAG, "count " + count);
        if (count == 0) {
            return count;
        }

        centerManager.readMsgsBySender(switchHelper.getCurrentUserId());
        adapter.getLoadHelper().loadMoreMessages2Start(mMessageList);
        adapter.notifyDataSetUpdate(mGridView.getFirstVisiblePosition() + 1);

        final ViewTreeObserver observer = mHolder.getMessageGridView().getViewTreeObserver();
        if (mHolder.getMessageGridView().getFirstVisiblePosition() == 0) {
            Log.i(TAG, "first one at original position");

            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    observer.removeOnGlobalLayoutListener(this);
                    if (adapter.getLastSelectedPostion() == -1) {
                        adapter.setLastSelectedPosition(0);
                    }

                    mHolder.getMessageGridView().requestFocusFromTouch();
                    mHolder.getMessageGridView().setSelection(adapter.getLastSelectedPostion());

                    clearPlayLayout();
                    if (!adapter.isDeleteMode()) {
                        enablePlayLayout(getCurrentPositionOnScreen());
                    }

                    Log.i(TAG, "mOnNewMessageListeners.size() = " + mOnNewMessageListeners.size());
                    if (mOnNewMessageListeners.size() > 0) {
                        for (OnNewMessageListener l : mOnNewMessageListeners) {
                            l.onNewMessage(mMessageList);
                        }
                    }
                }
            });

            final int tempPosition = 0;
            observer.addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    List<Animator> resultList = new LinkedList<Animator>();
                    int oldPosition = tempPosition;
                    int endPosition = (int) (mHolder.getItemNumPerScreen() + 1.5);
                    for (int pos = oldPosition; pos < endPosition; pos++) {
                        View view = mHolder.getMessageGridView().getChildAt(pos);
                        if (view != null) {
                            resultList.add(UIAnimationUtil.createTranslationAnimations(view, -view.getWidth(), 0, 0, 0));
                        }
                    }
                    AnimatorSet resultSet = new AnimatorSet();
                    resultSet.playTogether(resultList);
                    resultSet.setDuration(500);
                    resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    resultSet.start();
                    return true;
                }
            });
        } else {
            Log.i(TAG, "first one not at original position");
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    observer.removeOnGlobalLayoutListener(this);
                    clearPlayLayout();
                    if (!adapter.isDeleteMode()) {
                        enablePlayLayout(getCurrentPositionOnScreen());
                    }
                }
            });
            // clearPlayLayout();
            mHolder.getMessageGridView().setSelection(adapter.getLastSelectedPostion() + count);
            adapter.setLastSelectedPosition(adapter.getLastSelectedPostion() + count);
            int a = adapter.getLastSelectedPostion() + count;
            Log.i(TAG, "p o s out" + getPositionOnScreen(a));
        }
        return count;
    }

    private int getPositionOnScreen(int position) {

        if (position < 0 || position > mGridView.getCount()) {
            return -1;
        }

        return position - mGridView.getFirstVisiblePosition();
    }

    /**
     * get current position on screen.
     * 
     * @return
     */
    public int getCurrentPositionOnScreen() {
        if (adapter == null) {
            adapter = (MessageBaseAdapter) mGridView.getAdapter();
        }
        return getPositionOnScreen(adapter.getLastSelectedPostion());
    }

    /**
     * interface of new message.
     * 
     * @author charles.tai
     */
    private interface OnNewMessageListener {
        void onNewMessage(List<Msg> msg);
    }

    private void addOnNewMessageListener(OnNewMessageListener l) {
        if (!mOnNewMessageListeners.contains(l)) {
            mOnNewMessageListeners.add(l);
        }
    }

    private void removeOnNewMessageListener(OnNewMessageListener l) {
        mOnNewMessageListeners.remove(l);
    }
}
