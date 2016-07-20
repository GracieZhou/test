
package com.eostek.scifly.messagecenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scifly.provider.SciflyStore.Messages;
import scifly.provider.metadata.Msg;
import android.content.res.Resources.NotFoundException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.scifly.messagecenter.logic.ClickEventDispatcher;
import com.eostek.scifly.messagecenter.logic.MessageDataCache;
import com.eostek.scifly.messagecenter.logic.MessageDisplayer;
import com.eostek.scifly.messagecenter.model.MessageSender;
import com.eostek.scifly.messagecenter.ui.MessageBaseAdapter;
import com.eostek.scifly.messagecenter.ui.MessageBaseAdapter.OnNoMessageLeftListener;
import com.eostek.scifly.messagecenter.ui.animation.UIAnimationUtil;
import com.eostek.scifly.messagecenter.ui.dialog.DeleteMessageDialog;
import com.eostek.scifly.messagecenter.util.Constants;
import com.eostek.scifly.messagecenter.util.Util;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayAdapterView.OnItemSelectedListener;
import com.jess.ui.TwoWayGridView;

/**
 * Listener of MessageCenter Home Activity.
 */
public class MessageCenterListener {

    /** Log messages identifier. */
    public static final String TAG = "MessageCenterListener";

    private MessageCenterHolder mHolder;

    private ClickEventDispatcher mDispatcher;

    private static final int ENTER_PRESSED_RESUME = 0x01;

    private static final int OPERATION_MODE_DISPLAY = 0x02;

    private static final int OPERATION_MODE_DELETE = 0x03;

    private static final int EXIT_APP = 0x04;

    private static final int VIEW_HIDE = 0x05;

    private static final int VIEW_HIDE_ALL = 0x06;

    private static int OPERATION_MODE = OPERATION_MODE_DISPLAY;

    private DeleteMessageDialog mDeleteMsgDialog;

    private MainActivity mContext;

    private MessageDisplayer mDisplayer;

    private MessageDataCache mDataCache;

    /**
     * Constructor.
     * 
     * @param mContext
     * @param mHolder
     */
    public MessageCenterListener(MainActivity mContext, MessageCenterHolder mHolder) {
        this.mContext = mContext;
        this.mHolder = mHolder;
        mDataCache = mContext.getDataCacheManager();
        mDisplayer = new MessageDisplayer(mContext);
        mDispatcher = mContext.getDispatcher();
    }

    /** set listener. */
    public void setListener() {

        /** set MainActivity listener */
        mContext.setBackPressedListener(new MainActivityBackPressed());

        /** set message gridview listener */
        mHolder.getMessageGridView().setOnItemSelectedListener(new MessageItemSelectedListener());

        mHolder.getMessageGridView().setOnKeyListener(new MessageKeyListener());

        mHolder.getMessageGridView().setOnItemClickListener(new MessageItemClickListener());

        mHolder.getMessageGridView().setOnTouchListener(new MessageTouchListener());

        mHolder.getMessageGridView().setOnFocusChangeListener(new MessageFocusChangeListener());

        /** set delete button listener */
        mHolder.getDeleteLayout().setOnClickListener(new MessageDeleteClickListener());

        mHolder.getShieldLayout().setOnClickListener(new MessageShieldClickListener());

    }

    /**
     * Listener for key touch event in the message gridview.
     */
    private class MessageTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            mDisplayer.clearPlayLayout();
            return false;
        }
    }

    /**
     * Listener for key pressed event in the message gridview ,such as selector
     * moves left or right.
     */
    private class MessageKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (KeyEvent.ACTION_DOWN == event.getAction()) {
                TwoWayGridView mGridView = mHolder.getMessageGridView();

                int positionOnScreen = mGridView.getSelectedItemPosition() - mGridView.getFirstVisiblePosition();

                switch (keyCode) {

                    case KeyEvent.KEYCODE_DPAD_LEFT:

                        mDisplayer.keyLeftPressed();

                        // mHandler.sendEmptyMessage(SCROLL_LEFT);

                        if (mGridView.getSelectedItemPosition() > 0) {
                            if (OPERATION_MODE == OPERATION_MODE_DISPLAY) {
                                mDisplayer.clearPlayLayout();
                                mDisplayer.enablePlayLayout(positionOnScreen - 1);
                            }
                        }
                        return false;

                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        mDisplayer.keyRightPressed();

                        if (mGridView.getSelectedItemPosition() < mGridView.getCount() - 1) {
                            if (OPERATION_MODE == OPERATION_MODE_DISPLAY) {
                                mDisplayer.clearPlayLayout();
                                mDisplayer.enablePlayLayout(positionOnScreen + 1);
                            }
                        }
                        return false;
                    default:
                        break;

                }
            }
            return false;
        }
    }

    /** Listener for "删除消息" button click event. */
    private class MessageDeleteClickListener implements OnClickListener {
        public void onClick(View v) {
            final MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();

            if (adapter.getCount() <= 0) {
                return;
            }

            /** set message gridview's adapter listener */
            if (adapter.getOnNoMessageLeftListener() == null) {
                adapter.setOnNoMessageLeftListener(new AdapterNoMessageListener());
            }

            if (OPERATION_MODE == OPERATION_MODE_DISPLAY) {
                Log.i(TAG, "enter deleting  mode");

                OPERATION_MODE = OPERATION_MODE_DELETE;
                mDisplayer.UI2DeleteMode();
                adapter.setDeleteMode(true);
                mDisplayer.enableDeleteCover(true);
                mDisplayer.clearPlayLayout();

            } else if (OPERATION_MODE == OPERATION_MODE_DELETE) {
                mDeleteMsgDialog = new DeleteMessageDialog(mContext);

                if (mDeleteMsgDialog.isShowing()) {
                    return;
                }

                mDeleteMsgDialog.show(mContext.getSwitchHelper().getCurrentUserName());
                mDeleteMsgDialog.setDeleteListener(new OnClickListener() {
                    public void onClick(View v) {
                        Log.i(TAG, "delete all user's messages");
                        View view;
                        for (int i = 0; i <= mHolder.getItemNumPerScreen(); i++) {
                            view = mHolder.getMessageGridView().getChildAt(i);
                            if (view != null) {
                                view.startAnimation(UIAnimationUtil.getViewScaleHideAnimation(200));
                            }
                        }

                        /** clear cache */
                        Map<String, String> urls = new HashMap<String, String>();
                        addUserCacheUrls(urls, adapter.getMessages2show());
                        mDataCache.clearCache(urls);

                        Message msg = mHandler.obtainMessage();
                        msg.what = VIEW_HIDE_ALL;
                        mHandler.sendMessageDelayed(msg, 100);
                        mDeleteMsgDialog.dismiss();
                    }
                });
                mDeleteMsgDialog.setDeleteCancelListener(new OnClickListener() {
                    public void onClick(View v) {
                        Log.i(TAG, "cancel delete all user's messages");
                        if (mDeleteMsgDialog.isShowing()) {
                            mDeleteMsgDialog.dismiss();
                        }
                    }
                });
            }
        }
    }

    /** Listener for "屏蔽当前用户消息" button click event. */
    private class MessageShieldClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            boolean isCpe = false;
            MessageSender currentsender = mHolder.getSenderGridView().getCurrentSender();
            print("originalState:" + currentsender);

            if (currentsender.getSource() == Messages.SOURCE_CPE) {
                isCpe = true;
            }

            if (!isCpe) {
                if (!currentsender.isblocked()) {
                    mContext.getCenterManager().add2BlackList(currentsender);
                } else {
                    mContext.getCenterManager().deleteFromBlackList(currentsender);
                }

            }
        }

    }

    public void updateShieldStatus() {
        MessageSender currentSender = mHolder.getSenderGridView().getCurrentSender();
        print("currentStatus:" + currentSender.toString());

        updateShieldTip(currentSender.isblocked());
        mDisplayer.UI2BlockMode(currentSender.isblocked());

        // 如果是取消屏蔽,先判断消息是否为空,为空的话刷新用户列表.
        MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();
        List<Msg> msgs = adapter.getMessages2show();
        print("message lefted:" + msgs.size());
        if (msgs.size() == 0 && !currentSender.isblocked()) {
            print("remove sender");
            int position = mHolder.getSenderGridView().getSelectedItem();
            print("selectposition:" + position);
            mHolder.getSenderGridView().setChildSelected(position);
        }

    }

    // public void unShield() {
    // }
    //
    // public void refreshBlackListq() {
    // MessageSender currentSender =
    // mHolder.getSenderGridView().getCurrentSender();
    // boolean blocked = currentSender.isblocked();
    // currentSender.setBlocked(!blocked);
    // mHolder.getSenderGridView().setCurrentSender(currentSender);
    // Log.e("test", "refreshBkList:" + currentSender.toString());
    // updateShieldTip();
    // mDisplayer.UI2BlockMode();
    // }

    // private void updateBlackList(MessageSender currentsender) {
    //
    // if (!currentsender.isblocked()) {
    // // return
    // //
    // mContext.getCenterManager().add2BlackList(currentsender.getSenderId());
    // mContext.getCenterManager().add2BlackList(currentsender);
    // } else {
    // // return
    // //
    // mContext.getCenterManager().removeFromBlackList(currentsender.getSenderId());
    // mContext.getCenterManager().deleteFromBlackList(currentsender);
    // }
    // }

    /**
     * Callback method for no Message left in current gridview adapter.
     */
    private class AdapterNoMessageListener implements OnNoMessageLeftListener {

        public void onNoMessage(boolean isDelAll) {
            Log.i(TAG, "no message left");

            int currentPosition = mHolder.getSenderGridView().getSelectedItem();
            boolean isBlocked = mHolder.getSenderGridView().getCurrentSender().isblocked();

            print("currentPosition:" + currentPosition);

            if (!isDelAll && !isBlocked) {
                mHolder.getSenderGridView().removeSender(currentPosition);
            }

            if (OPERATION_MODE == OPERATION_MODE_DELETE) {

                // exit deleting mode begin:
                OPERATION_MODE = OPERATION_MODE_DISPLAY;
                MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();

                mHolder.getMessageGridView().requestFocus();
                mHolder.getMessageGridView().setSelection(adapter.getLastSelectedPostion());
                mDisplayer.UI2DisplayMode();
                adapter.setDeleteMode(false);
                mDisplayer.disableDeleteCover(true);
                // exit deleting mode end.
            }

        }
    }

    /** Listener for message item selected event. */
    private class MessageItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(TwoWayAdapterView<?> parent, View view, int position, long id) {
            MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();

            Log.i(TAG, " selected " + position);
            try {
                if (view != null && OPERATION_MODE == OPERATION_MODE_DISPLAY) {
                    // view.findViewById(R.id.message_play_layout).setVisibility(View.VISIBLE);
                    Msg msg = (Msg) parent.getAdapter().getItem(position);
                    if (msg.mCategory == Messages.CATEGORY_APK) {
                        TextView textView = (TextView) view.findViewById(R.id.message_play);
                        if (Util.isInstalled(msg, mContext)) {
                            textView.setText(R.string.open_resource);
                        } else {
                            textView.setText(R.string.install_resource);
                        }
                    }
                } else if (view != null && OPERATION_MODE == OPERATION_MODE_DELETE) {
                    view.findViewById(R.id.message_play_layout).setVisibility(View.INVISIBLE);
                }
            } catch (NotFoundException e) {
                Log.i(TAG, "resource can not be found");
                e.printStackTrace();
            }
            /** remember the last selected item position */
            print("remember :" + position);
            adapter.setLastSelectedPosition(position);
        }

        @Override
        public void onNothingSelected(TwoWayAdapterView<?> parent) {

        }

    }

    private long mLastClickTime = -1;

    /**
     * Listener for message item click event,it will open different app to deal
     * with the message.
     */
    private class MessageItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "Item Clicked --position :" + position);
            Log.i(TAG,
                    "selected --position :"
                            + ((MessageBaseAdapter) mHolder.getMessageGridView().getAdapter()).getLastSelectedPostion());

            MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();
            int a = parent.getFirstVisiblePosition();
            int b = position;
            Log.i(TAG, "" + String.format("a =%d b =%d b-a =%d  view size=%d", a, b, b - a, view.getWidth()));

            int positionOnScreen = position - parent.getFirstVisiblePosition();

            Msg msg = (Msg) parent.getAdapter().getItem(position);
//FIXME
                switch (OPERATION_MODE) {
                    case OPERATION_MODE_DISPLAY:
                        try {
                            /**
                             * When press the remote cotorller "Enter" key , the
                             * background of the item view changed
                             */
                            LinearLayout messagePlayLayout = (LinearLayout) view.findViewById(R.id.message_main_content);
                            messagePlayLayout.setBackgroundResource(R.drawable.list_selector_background_pressed);

                            Message viewUpdateMsg = mHandler.obtainMessage();
                            viewUpdateMsg.arg1 = positionOnScreen;
                            viewUpdateMsg.what = ENTER_PRESSED_RESUME;
                            mHandler.sendMessageDelayed(viewUpdateMsg, 50);

                            /** Handle message item click event */
                            TextView desc = (TextView) parent.getChildAt(positionOnScreen).findViewById(
                                    R.id.content_description);
                            ImageView ci = (ImageView) parent.getChildAt(positionOnScreen).findViewById(R.id.message_img);

                            Log.i(TAG, "message_img width = " + ci.getWidth());
                            Log.i(TAG, "message_img height = " + ci.getHeight());
                            mDispatcher.clickEventDispatch(msg);

                            /** Select the clicked item */
                            if (!mHolder.getMessageGridView().isFocused()) {
                                mHolder.getMessageGridView().requestFocusFromTouch();
                                mHolder.getMessageGridView().setSelection(position);
                                adapter.setLastSelectedPosition(position);
                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        break;
                    case OPERATION_MODE_DELETE:

                        long systemTime = System.currentTimeMillis();
                        if (mLastClickTime != -1 && systemTime - mLastClickTime < 300) {
                            Log.i(TAG, "is deleting ,wait please!");
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.delete_wait_tip),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mLastClickTime = systemTime;

                        Msg selectedMsg = (Msg) parent.getAdapter().getItem(position);
                        Message message = mHandler.obtainMessage();

                        if (mHolder.getSenderGridView().getCurrentSender().isblocked()) {
                            mContext.getContentResolver().unregisterContentObserver(mContext.getContentObserver());
                            updateShieldTip();
                        }

                        /** delete record in database */
                        mContext.getCenterManager().delMessage(selectedMsg);

                        mContext.registerContentObservers();

                        /** clear cache on disk and in memory */
                        Map<String, String> urls = new HashMap<String, String>();
                        addCacheUrls(urls, msg);
                        mDataCache.clearCache(urls);

                        message.what = VIEW_HIDE;
                        message.arg1 = positionOnScreen;

                        mHandler.sendMessage(message);
                        break;
                    default:
                        break;
                }
                
             
        
        }

    }

    private void updateShieldTip() {

        TextView shield_message_tip = (TextView) mHolder.getShieldLayout().findViewById(R.id.shield_message_tip);
        MessageSender sender = mHolder.getSenderGridView().getCurrentSender();

        if (sender != null && sender.isblocked()) {
            shield_message_tip.setText(mContext.getResources().getString(R.string.cancle_blockInfo));
        } else {
            shield_message_tip.setText(mContext.getResources().getString(R.string.blockInfo));

        }
    }

    private void updateShieldTip(boolean blocked) {

        TextView shield_message_tip = (TextView) mHolder.getShieldLayout().findViewById(R.id.shield_message_tip);

        if (blocked) {
            shield_message_tip.setText(mContext.getResources().getString(R.string.cancle_blockInfo));
        } else {
            shield_message_tip.setText(mContext.getResources().getString(R.string.blockInfo));

        }
    }

    private void addUserCacheUrls(Map<String, String> urls, List<Msg> msgs) {
        for (Msg msg : msgs) {
            addCacheUrls(urls, msg);
        }
    }

    private void addCacheUrls(Map<String, String> urlMap, Msg msg) {

        urlMap.put(msg.mThumb, Constants.IMG_POSTFIX);
        switch (msg.mCategory) {
            case Messages.CATEGORY_EPG:
                break;
            case Messages.CATEGORY_EPG_CACHE:
                break;
            case Messages.CATEGORY_MSG_LIVE:
                break;
            case Messages.CATEGORY_IMAGE:
                urlMap.put(msg.mData, Constants.IMG_POSTFIX);
                break;
            case Messages.CATEGORY_TEXT:
                break;
            case Messages.CATEGORY_URL:
                break;
            case Messages.CATEGORY_VIDEO:
                urlMap.put(msg.mData, Constants.VIDEO_POSTFIX);
                break;
            case Messages.CATEGORY_APK:
                urlMap.put(msg.mData, Constants.APK_POSTFIX);
                break;
            case Messages.CATEGORY_VOICE:
                urlMap.put(msg.mData, Constants.VOICE_POSTFIX);
                break;
            default:
                break;
        }
    }

    /** Listener for message focus change event. */
    private class MessageFocusChangeListener implements OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus) {

            View lastChild;
            lastChild = mHolder.getMessageGridView().getSelectedView();
            if (lastChild != null && !hasFocus) {
                View lastChildLayout = (LinearLayout) lastChild.findViewById(R.id.message_play_layout);
                lastChildLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Callback method for "back" key pressed in MainActivity ,there are two
     * different operations 1.exit application 2.exit deleting mode.
     */
    private class MainActivityBackPressed implements MainActivity.BackPressedListener {
        public void backPressed() {
            if (OPERATION_MODE == OPERATION_MODE_DELETE) {
                Log.i(TAG, "exit deleting mode");
                OPERATION_MODE = OPERATION_MODE_DISPLAY;
                MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();
                mHolder.getMessageGridView().requestFocusFromTouch();
                mHolder.getMessageGridView().setSelection(adapter.getLastSelectedPostion());
                Log.i(TAG, "  " + adapter.getLastSelectedPostion());
                mDisplayer.UI2DisplayMode();
                adapter.setDeleteMode(false);
                mDisplayer.disableDeleteCover(true);
                mDisplayer.clearPlayLayout();
                mDisplayer.enablePlayLayout(mDisplayer.getCurrentPositionOnScreen());
            } else if (OPERATION_MODE == OPERATION_MODE_DISPLAY) {
                exit();
            }
        }
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENTER_PRESSED_RESUME:
                    mDisplayer.enterPressedResume(msg.arg1);
                    break;
                case VIEW_HIDE:
                    mDisplayer.deleteMessage(msg.arg1);
                    break;
                case VIEW_HIDE_ALL:
                    mDisplayer.deleteAllMessage();
                    mHolder.getSenderGridView().removeSender(mHolder.getSenderGridView().getSelectedItem());
                    break;
                case EXIT_APP:
                    isExit = false;
                    break;
                default:
                    break;
            }

        };
    };

    private boolean isExit = false;

    /** press 2 times in 2 seconds will exit app. */
    public void exit() {
        // if (!isExit) {
        // isExit = true;
        // Toast.makeText(mContext,
        // mContext.getResources().getString(R.string.exit_app),
        // Toast.LENGTH_SHORT).show();
        // Log.i(TAG, mContext.getResources().getString(R.string.exit_app));
        // mHandler.sendEmptyMessageDelayed(EXIT_APP, 2000);
        // } else {
        mContext.finish();
        // }
    }

    private void print(String s) {
        Log.i(TAG, "" + s);
    }

}
