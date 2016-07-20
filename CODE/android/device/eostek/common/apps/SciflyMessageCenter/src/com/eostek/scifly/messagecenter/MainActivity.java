
package com.eostek.scifly.messagecenter;

import static com.eostek.scifly.messagecenter.util.Constants.CACHE_PATH;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Messages;
import scifly.provider.metadata.IMsgManager;
import scifly.provider.metadata.Msg;
import scifly.provider.metadata.MsgService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eostek.scifly.messagecenter.datacenter.IStatusListener;
import com.eostek.scifly.messagecenter.datacenter.MsgCenterManager;
import com.eostek.scifly.messagecenter.logic.ClickEventDispatcher;
import com.eostek.scifly.messagecenter.logic.MessageDataCache;
import com.eostek.scifly.messagecenter.logic.MessageDisplayer;
import com.eostek.scifly.messagecenter.logic.SimpleAudioPlayer;
import com.eostek.scifly.messagecenter.logic.UserSwitchHelper;
import com.eostek.scifly.messagecenter.model.MessageSender;
import com.eostek.scifly.messagecenter.ui.MessageBaseAdapter;
import com.eostek.scifly.messagecenter.ui.SenderContainer.OnUserPressedListener;

/**
 * Activity for MessageCenter home page.
 */
public class MainActivity extends Activity {
    /** Log messages identifier */
    public static final String TAG = "MainActivity";

    private static final String AUTHORITY = "com.eostek.scifly.provider";

    private static final String TABLE_MESSAGE = "message";

    private static final String TABLE_MSGUSER = "msguser";

    private static final Uri CONTENT_URI_MESSAGE = Uri.parse("content://" + AUTHORITY + "/" + TABLE_MESSAGE);

    private static final Uri CONTENT_URI_MSGUSER = Uri.parse("content://" + AUTHORITY + "/" + TABLE_MSGUSER);

    private static final int UPDATE_USER_MESSAGE = 0x01;

    private static final int UPDATE_SENDERS = 0x02;
    private static final int UPDATE_BLKLIST = 0x03;
    private static final int UPDATE_REFRESH_SENDERS = 0x04;

    private MessageCenterHolder mHolder;

    private MessageCenterListener mListener;

    private List<MessageSender> mSenders = new ArrayList<MessageSender>();

    private BackPressedListener mBackPressedListener;

    private MsgCenterManager mCenterManager;

    private UserSwitchHelper mSwitchHelper;

    private MessageDisplayer mDisplayer;

    private IMsgManager mService = null;

    private List<Msg> mMessageList = new ArrayList<Msg>();

    private MessageDataCache mDataCache;

    private SimpleAudioPlayer mAudioPlayer;

    private ClickEventDispatcher mDispatcher;

    public static Map isBlocked = new HashMap();

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_USER_MESSAGE:
                    new Thread(new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    List<Msg> audioMsg = new ArrayList<Msg>();
                                    for (Msg msg : mMessageList) {
                                        if (msg.mCategory == Messages.CATEGORY_VOICE) {
                                            audioMsg.add(msg);
                                        }
                                    }

                                    mDisplayer.updateUserMessage(MainActivity.this.getCenterManager(), mSwitchHelper,
                                            mMessageList);

                                    /** voice message auto play */
                                    // for (int i = audioMsg.size() - 1; i >= 0;
                                    // i--) {
                                    // mDispatcher.simulateAutoClick(audioMsg.get(i));
                                    // }
                                }
                            });
                        }
                    }).start();
                    break;
                case UPDATE_SENDERS:
                    mHolder.getSenderGridView().addNewSenders(mSenders);
                    break;
                case UPDATE_BLKLIST:
                    mListener.updateShieldStatus();
                    break;
                case UPDATE_REFRESH_SENDERS:
                    initData();
                    break;
                default:
                    break;
            }
        }

    };

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "MsgService is disconnected...");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "MsgService is connected...");
            mService = IMsgManager.Stub.asInterface(service);
        }
    };

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {

        public void onChange(boolean selfChange) {
            refreshUserMessage();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        };
    };

    private IStatusListener mStatusListener = new IStatusListener() {

        @Override
        public void onDelResult(boolean isSucceed, String msg) {
            if (isSucceed) {
                mHandler.sendEmptyMessage(UPDATE_BLKLIST);
            } else {
                Log.e("test","delete failed.");
            }
        }

        @Override
        public void onAddResult(boolean isSucceed, String msg) {
            if (isSucceed) {
                mHandler.sendEmptyMessage(UPDATE_BLKLIST);
            }else{
                Log.e("test","add failed.");
            }
        }

        @Override
        public void getBlackList(List<MessageSender> senders) {
            mHandler.sendEmptyMessage(UPDATE_BLKLIST);
        }
    };

    public ContentObserver getContentObserver() {
        return mContentObserver;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // setBackground();
        getWindow().setBackgroundDrawable(null);

        MsgCenterManager.getInstance(this).getBlackListFromServer();
        MsgCenterManager.getInstance(this).setListener(mStatusListener);

        bindService();

        registerForContextMenu(this.findViewById(R.id.message_center));

        initData();
        // Codes for test:
        View v = findViewById(R.id.message_center);
        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String pkgName = getTopActivity(MainActivity.this);
                Log.e(TAG, "pkgName:" + pkgName);
            }
        });
        // end.

        Log.i("SciflyMessageCenter",
                "Name:SciflyMessageCenter, Version:2.4.1, Date:2015-09-02, Publisher:Kevin.Duan,Youpeng.Wan,Shirley.Jiang, REV:40668");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log.e("youp", "onResume...");
        // mHolder.getMessageGridView().requestFocusFromTouch();

        String pkgName = getTopActivity(this);
        Log.e(TAG, "pkgName:" + pkgName);

        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (getTaskId() >= 0) {
            am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME, null);

            Log.e(TAG, "moveTaskToFront");
        }

    }

    private String getTopActivity(Activity context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            RunningTaskInfo taskInfo = runningTaskInfos.get(0);
            String str = (taskInfo.topActivity).toString();
            ComponentName baseAct = taskInfo.baseActivity;
            Log.e(TAG, "baseAct:" + baseAct.getPackageName());
            return str;

        } else {
            return null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart...");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        try {
            this.getDispatcher().stopMessage();
        } catch (Exception e) {
            //FIXME
            Log.d(TAG, e.toString());
        }
    }

    private void initData() {
        mSenders = MsgCenterManager.getInstance(this).getAllSenders();

        getContentResolver().registerContentObserver(CONTENT_URI_MESSAGE, true, mContentObserver);

        getContentResolver().registerContentObserver(CONTENT_URI_MSGUSER, true, mContentObserver);

        mCenterManager = MsgCenterManager.getInstance(this);
        if (!mCenterManager.hasUnReadMsg()) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(R.drawable.icon_messages);
            Log.i(TAG, "dismiss notification icon_messages.");
        }

        mDataCache = MessageDataCache.getCacheLoader(this);

        mAudioPlayer = new SimpleAudioPlayer(this);

        mHolder = new MessageCenterHolder(this);
        mHolder.getViews();

        mSwitchHelper = new UserSwitchHelper(this);

        mDisplayer = new MessageDisplayer(this);

        mHolder.initSenderGirdView(mSenders);
        mHolder.initMessageGirdView();
        // mHolder.getSenderGridView().requestFocus();

        mDispatcher = new ClickEventDispatcher(mHolder, this);

        mListener = new MessageCenterListener(this, mHolder);
        mListener.setListener();

        mHolder.setSenderGridOnItemClickListener(new OnUserPressedListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onUserPressed(int position, MessageSender user) {
                try {
                    if (user != null) {
                        String id = user.getSenderId();
                        mSwitchHelper.switch2User(id, user.getName());
                        System.out.println("pressedItem:" + URLDecoder.decode(user.getName()));
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });
        Log.i(TAG, "size +++++++++" + mSenders.size());
        if (mSenders.size() > 0) {
            String id = mSenders.get(0).getSenderId();
            mSwitchHelper.switch2User(id, mSenders.get(0).getName());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(0, 6, Menu.NONE, "给当前用户发送一条消息");
        menu.add(0, 1, Menu.NONE, "给当前用户发送一条消息2");
        menu.add(0, 10, Menu.NONE, "给当前用户发送一百条消息");
        menu.add(0, 3, Menu.NONE, "删除当前用户");
        menu.add(0, 9, Menu.NONE, "AddData");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 3:
                mSwitchHelper.deleteCurrentUser();
                break;
            case 1:
                addMessage(2);
                break;
            case 6:
                addMessage(0);
                break;
            case 9:
                adduser();
                break;
            case 10:
                int i,
                j;
                i = j = 200;
                while (i-- > 0) {
                    addMessage(j - i);
                }
            default:
                break;

        }

        return super.onContextItemSelected(item);
    }

    /**
     * listener for press back.
     */
    @Override
    public void onBackPressed() {
        if (mBackPressedListener != null) {
            mBackPressedListener.backPressed();
        }
    }

    /**
     * set back key pressed listener.
     * 
     * @param backPressedListener
     */
    public void setBackPressedListener(BackPressedListener backPressedListener) {
        this.mBackPressedListener = backPressedListener;
    }

    /**
     * interface of back pressed.
     */
    public interface BackPressedListener {
        /** MainActivity key "back" pressed event */
        public void backPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            switch (keyCode) {
                case KeyEvent.KEYCODE_0:
                    break;
                // case 25:
                // addMessage(0);
                // break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mHolder == null || mHolder.getMessageGridView() == null) {
                        return super.onKeyDown(keyCode, event);
                    }
                    if (mHolder.getMessageGridView().isFocused()) {
                        mHolder.getDeleteLayout().requestFocus();
                        Log.i(TAG, "down to delete!!!");
                    } else if (mHolder.getSenderGridView().isFocused()) {
                        Log.i(TAG, "down to message gridview!!!");
                        mHolder.getMessageGridView().requestFocus();
                        View lastChild;
                        lastChild = mHolder.getMessageGridView().getSelectedView();
                        if (lastChild != null) {
                            View lastChildLayout = (LinearLayout) lastChild.findViewById(R.id.message_play_layout);
                            lastChildLayout.setVisibility(View.VISIBLE);
                        }
                        Log.i(TAG, "down to message gridview!!!");
                    }

                    return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mHolder == null || mHolder.getMessageGridView() == null) {
                        return super.onKeyDown(keyCode, event);
                    }
                    if (mHolder.getMessageGridView().isFocused()) {
                        mHolder.getSenderGridView().requestFocus();
                        Log.i(TAG, "up to userlist!!!");
                    } else if (mHolder.getDeleteLayout().isFocused()) {
                        MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();
                        if (adapter.getCount() == 0) {
                            mHolder.getSenderGridView().requestFocus();
                        } else {
                            mDisplayer.doWhenGetFocusAgain();
                            Log.i(TAG, "up to message gridview!!!");
                        }

                    } else if (mHolder.getShieldLayout().isFocused()) {
                        int senderCount = mHolder.getSenderGridView().getChildCount();
                        if (mHolder.getSenderGridView().getChildCount() > 0) {
                            mHolder.getSenderGridView().requestFocus();
                        } else {
                            mDisplayer.doWhenGetFocusAgain();
                        }
                    }

                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mHolder == null || mHolder.getMessageGridView() == null) {
                        return super.onKeyDown(keyCode, event);
                    }
                    if (mHolder.getMessageGridView().isFocused() || mHolder.getDeleteLayout().isFocused()) {
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    /*
                     * if (mHolder.getMessageGridView().isFocused() ||
                     * mHolder.getDeleteLayout().isFocused()) { return true; }
                     */
                    if (mHolder == null || mHolder.getMessageGridView() == null) {
                        return super.onKeyDown(keyCode, event);
                    }
                    if (mHolder.getMessageGridView().isFocused()) {
                        return true;
                    } else if (mHolder.getDeleteLayout().isFocused()) {
                        mHolder.getShieldLayout().requestFocus();
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void bindService() {
        Intent intent = new Intent(this, MsgService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void addMessage(int i) {

        Calendar calendar = Calendar.getInstance();

        switch (i) {
            case 0:

                Msg msg_epg_2 = new Msg();
                msg_epg_2.mTitle = " ";
                msg_epg_2.mUserId = "" + mSwitchHelper.getCurrentUserId();
                msg_epg_2.mUserInfo = "" + mSwitchHelper.getCurrentUserName();
                msg_epg_2.mImgUrl = "http://172.23.65.182:8080/MessageCenter/face1.jpg";
                // msg_epg_2.mData =
                // "http://172.23.65.182:8080/MessageCenter/video1.mp4";// url
                // msg_epg_2.mData =
                // "http://172.23.65.182:8080/MessageCenter/audio_expired.ogg";//
                // url
                msg_epg_2.mData = "http://172.23.65.182:8080/MessageCenter/thumb1.png";// url
                msg_epg_2.mThumb = "http://172.23.65.182:8080/MessageCenter/thumb1.png";
                msg_epg_2.mSource = Messages.SOURCE_ISYNERGY;
                msg_epg_2.mCategory = Messages.CATEGORY_IMAGE;
                msg_epg_2.mTime = System.currentTimeMillis();

                Messages.putMessage(getContentResolver(), msg_epg_2);
                break;
            case 1:
                Msg msg_epg_3 = new Msg();
                msg_epg_3.mTitle = "嗨！2014 id=" + i;
                msg_epg_3.mUserId = "" + mSwitchHelper.getCurrentUserId();
                msg_epg_3.mUserInfo = "" + mSwitchHelper.getCurrentUserName();
                msg_epg_3.mImgUrl = "http://172.23.65.182:8080/MessageCenter/face1.jpg";
                // msg_epg_2.mData =
                // "http://172.23.65.182:8080/MessageCenter/video1.mp4";// url
                msg_epg_3.mData = "http://172.23.65.182:8080/MessageCenter/audio2.ogg";// url
                msg_epg_3.mThumb = "http://172.23.65.182:8080/MessageCenter/thumb1.png";
                msg_epg_3.mSource = Messages.SOURCE_ISYNERGY;
                msg_epg_3.mCategory = Messages.CATEGORY_VOICE;
                msg_epg_3.mTime = System.currentTimeMillis();

                Messages.putMessage(getContentResolver(), msg_epg_3);
                break;

            case 2:

                String dataString = "美丽的雪花飞舞起来了。我已经有三年不曾见着它。"
                        + "去年在福建，仿佛比现在更迟一点，也曾见过雪。"
                        + "但那是远处山顶的积雪，可不是飞舞的雪花。在平原上，它只是偶然的随着雨点洒下来几颗，没有落到地面的时候。"
                        + "它的颜色是灰的，不是白色；它的重量像是雨点，并不会飞舞。一到地面，它立刻融成了水，没有痕迹，也未尝跳跃，"
                        + "也未尝发出唏嘘的声音，像江浙一带下雪时的模样。这样的雪，在四十年来第一次看见它的老年的福建人，"
                        + "诚然能感到特别的意味，谈得津津有味，但在我，却总觉得索然。"
                        + "\"福建下过雪\"，我可没有这样想过。"
                        + "我喜欢眼前飞舞着的上海的雪花。"
                        + "它才是\"雪白\"的白色，也才是花一样的美丽。它好像比空气还轻，并不从半空里落下来，"
                        + "而是被空气从地面卷起来的。然而它又像是活的生物，像夏天黄昏时候的成群的蚊蚋(ruì)，"
                        + "像春天酿蜜时期的蜜蜂，它的忙碌的飞翔，或上或下，或快或慢，或粘着人身，或拥入窗隙，"
                        + "仿佛自有它自己的意志和目的。它静默无声。但在它飞舞的时候，我们似乎听见了千百万人马的呼号和脚步声，"
                        + "大海汹涌的波涛声，森林的狂吼声，有时又似乎听见了儿女的窃窃私语声，礼拜堂的平静的晚祷声，花园里的欢乐的鸟歌声"
                        + "……它所带来的是阴沉与严寒。但在它的飞舞的姿态中，我们看见了慈善的母亲，活泼的孩子，微笑的花儿，和暖的太阳，静默的晚霞……它没有气息。但当它扑到我们面上的时候，我们似乎闻到了旷野间鲜洁的空气的气息，"
                        + "山谷中幽雅的兰花的气息，花园里浓郁的玫瑰的气息，清淡的茉莉花的气息……在白天，它做出千百种婀娜的姿态；夜间，它发出银色的光辉，"
                        + "照耀着我们行路的人，又在我们的玻璃窗上扎扎地绘就了各式各样的花卉和树木，斜的，直的，弯的，倒的。还有那河流，那天上的云";

                Msg msg_epg_4 = new Msg();
                msg_epg_4.mTitle = "嗨！2014 id=" + i;
                msg_epg_4.mUserId = "" + mSwitchHelper.getCurrentUserId();
                msg_epg_4.mUserInfo = "" + mSwitchHelper.getCurrentUserName();
                msg_epg_4.mImgUrl = "http://172.23.65.182:8080/MessageCenter/face1.jpg";
                // msg_epg_2.mData =
                // "http://172.23.65.182:8080/MessageCenter/video1.mp4";// url
                msg_epg_4.mData = dataString;// url
                // msg_epg_4.mThumb =
                // "http://172.23.65.182:8080/MessageCenter/thumb1.png";
                msg_epg_4.mSource = Messages.SOURCE_ISYNERGY;
                msg_epg_4.mCategory = Messages.CATEGORY_TEXT;
                msg_epg_4.mTime = System.currentTimeMillis();

                Messages.putMessage(getContentResolver(), msg_epg_4);
                break;
            default:
                break;

        }
    }

    private void refreshUserMessage() {

        new Thread() {

            @Override
            public void run() {
                Log.i(TAG, "refreshUserMessage");
                mSenders = mCenterManager.getAllSenders();

                if (!mCenterManager.hasUnReadMsg()) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(R.drawable.icon_messages);
                    Log.i(TAG, "dismiss notification icon_messages.");
                }

                mHandler.sendEmptyMessage(UPDATE_SENDERS);

                mMessageList = mCenterManager.getUnReadMsg(mSwitchHelper.getCurrentUserId());
                if (mMessageList.size() == 0) {
                    return;
                }

                mHandler.removeMessages(UPDATE_USER_MESSAGE);
                mHandler.sendEmptyMessage(UPDATE_USER_MESSAGE);
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        MessageBaseAdapter adapter = (MessageBaseAdapter) mHolder.getMessageGridView().getAdapter();
        getContentResolver().unregisterContentObserver(mContentObserver);

        if (adapter != null) {
            adapter.getLoadHelper().clearMessages();
            // MessageImageLoader.getImageLoader(this).destory();
        }

        if (mAudioPlayer != null) {
            mAudioPlayer.release();
            mAudioPlayer = null;
        }

        unbindService(serviceConnection);
    }

    private void setBackground() {

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable dr = wallpaperManager.getDrawable();

        View v = findViewById(R.id.message_center_main);
        if (dr != null) {
            v.setBackground(dr);
        }

    }

    private void adduser() {

        Msg msg_epg_1 = new Msg();
        msg_epg_1.mTitle = "SpiderMan";
        msg_epg_1.mUserId = Messages.USERID_CPE;
        msg_epg_1.mUserInfo = "EPG";
        msg_epg_1.mImgUrl = "http://172.23.65.179:8080/msgCenter/system.jpg";
        msg_epg_1.mData = "系统消息";// url
        msg_epg_1.mThumb = "http://172.23.65.179:8080/msgCenter/spiderman.jpg";
        msg_epg_1.mSource = Messages.SOURCE_CPE;
        msg_epg_1.mCategory = Messages.CATEGORY_EPG;
        msg_epg_1.mTime = 0;
        msg_epg_1.mExtra = "扩展字段";
        msg_epg_1.mReserve = "保留字段";
        Messages.putMessage(getContentResolver(), msg_epg_1);

        Msg msg_image_1 = new Msg();
        msg_image_1.mTitle = "Test Message";
        msg_image_1.mUserId = "id002";
        msg_image_1.mUserInfo = "图片";
        msg_image_1.mImgUrl = "http://172.23.65.179:8080/msgCenter/wechat.jpg";
        msg_image_1.mData = "微信消息";// url
        msg_image_1.mThumb = "http://172.23.65.179:8080/msgCenter/Thumb.jpg";
        msg_image_1.mSource = Messages.SOURCE_WECHAT;
        msg_image_1.mCategory = Messages.CATEGORY_IMAGE;
        msg_image_1.mExtra = "扩展字段";
        msg_image_1.mReserve = "保留字段";
        msg_image_1.mTime = 0;
        Messages.putMessage(getContentResolver(), msg_image_1);

        Msg msg_text_1 = new Msg();
        msg_text_1.mTitle = "Test Message";
        msg_text_1.mUserId = "id003";
        msg_text_1.mUserInfo = "文字";
        msg_text_1.mImgUrl = "http://172.23.65.179:8080/msgCenter/wechat.jpg";
        msg_text_1.mData = "一二三四五六七八九十xxxxxxx00000000000000000jaoyioghgahehgg九八七六五四三二一零一二三四五六七八九十九八七六五四三二一零一二三四五六七八九十九八七六五四三二一零一二三四五六七八九十九八七六五四三二一零一二三四五六七八九十九八七六五四三二一零一二三四五六七八九十九八七六五四三二一零";// text
                                                                                                                                                                                             // data
        msg_text_1.mThumb = "http://172.23.65.179:8080/msgCenter/Thumb.jpg";
        msg_text_1.mSource = Messages.SOURCE_WECHAT;
        msg_text_1.mCategory = Messages.CATEGORY_TEXT;
        msg_text_1.mExtra = "扩展字段";
        msg_text_1.mReserve = "保留字段";
        msg_text_1.mTime = 0;
        Messages.putMessage(getContentResolver(), msg_text_1);

        Msg msg_voice_1 = new Msg();
        msg_voice_1.mTitle = "3'21''";
        msg_voice_1.mUserId = "id004";
        msg_voice_1.mUserInfo = "语音";
        msg_voice_1.mImgUrl = "http://172.23.65.179:8080/msgCenter/wechat.jpg";
        msg_voice_1.mData = "http://172.23.65.182:8080/MessageCenter/827b385f3c829c05c1656ce24afae909.ogg";// voice
                                                                                                           // data
                                                                                                           // url
        msg_voice_1.mThumb = "http://172.23.65.179:8080/msgCenter/Thumb.jpg";
        msg_voice_1.mSource = Messages.SOURCE_WECHAT;
        msg_voice_1.mCategory = Messages.CATEGORY_VOICE;
        msg_voice_1.mExtra = "扩展字段";
        msg_voice_1.mReserve = "保留字段";
        msg_voice_1.mTime = 0;
        Messages.putMessage(getContentResolver(), msg_voice_1);

        Msg msg_video_1 = new Msg();
        msg_video_1.mTitle = "4'25''";
        msg_video_1.mUserId = "id005";
        msg_video_1.mUserInfo = "视频";
        msg_video_1.mImgUrl = "http://172.23.65.179:8080/msgCenter/wechat.jpg";
        msg_video_1.mData = "微信消息";// voice data url
        msg_video_1.mThumb = "";// url
        msg_video_1.mSource = Messages.SOURCE_WECHAT;
        msg_video_1.mCategory = Messages.CATEGORY_VIDEO;
        msg_video_1.mExtra = "扩展字段";
        msg_video_1.mReserve = "保留字段";
        msg_video_1.mTime = 0;
        Messages.putMessage(getContentResolver(), msg_video_1);

        Msg msg_apk_1 = new Msg();
        msg_apk_1.mTitle = "愤怒的小鸟";
        msg_apk_1.mUserId = "id006";
        msg_apk_1.mUserInfo = "apk";
        msg_apk_1.mImgUrl = "http://172.23.65.179:8080/msgCenter/wechat.jpg";
        msg_apk_1.mData = "微信消息";// apk data url
        msg_apk_1.mThumb = "http://172.23.65.179:8080/msgCenter/angrybird.png";// url
        msg_apk_1.mSource = Messages.SOURCE_WECHAT;
        msg_apk_1.mCategory = Messages.CATEGORY_APK;
        msg_apk_1.mExtra = "扩展字段";
        msg_apk_1.mReserve = "保留字段";
        msg_apk_1.mTime = 0;
        Messages.putMessage(getContentResolver(), msg_apk_1);

        Msg msg_epg_2 = new Msg();
        msg_epg_2.mTitle = "Transformers";
        msg_epg_2.mUserId = "id007";
        msg_epg_2.mUserInfo = "EPG";
        msg_epg_2.mImgUrl = "http://172.23.65.179:8080/msgCenter/wechat.jpg";
        msg_epg_2.mData = "微信消息";// url
        msg_epg_2.mThumb = "http://172.23.65.179:8080/msgCenter/transformers.jpg";
        msg_epg_2.mSource = Messages.SOURCE_WECHAT;
        msg_epg_2.mCategory = Messages.CATEGORY_EPG;
        msg_epg_2.mTime = 0;
        msg_epg_2.mExtra = "扩展字段";
        msg_epg_2.mReserve = "保留字段";
        Messages.putMessage(getContentResolver(), msg_epg_2);

    }

    /**
     * print message log.
     * 
     * @param t
     */
    public <T> void print(T t) {
        Log.i(TAG, "" + t.toString());
    }

    /**
     * get instance of MessageDataCache.
     * 
     * @return MessageDataCache
     */
    public MessageDataCache getDataCacheManager() {
        return mDataCache;
    }

    /**
     * get instance of MessageDisplayer.
     * 
     * @return MessageDisplayer
     */
    public MessageDisplayer getDisplayer() {
        return mDisplayer;
    }

    /**
     * get instance of UserSwitchHelper.
     * 
     * @return UserSwitchHelper
     */
    public UserSwitchHelper getSwitchHelper() {
        return mSwitchHelper;
    }

    /**
     * get instance of MessageCenterHolder.
     * 
     * @return MessageCenterHolder
     */
    public MessageCenterHolder getHolder() {
        return mHolder;
    }

    /**
     * get instance of MsgCenterManager.
     * 
     * @return MsgCenterManager
     */
    public MsgCenterManager getCenterManager() {
        return mCenterManager;
    }

    /**
     * get instance of SimpleAudioPlayer.
     * 
     * @return SimpleAudioPlayer
     */
    public SimpleAudioPlayer getAudioPlayer() {
        return mAudioPlayer;
    }

    /**
     * get instance of ClickEventDispatcher.
     * 
     * @return ClickEventDispatcher
     */
    public ClickEventDispatcher getDispatcher() {
        return mDispatcher;
    }

    /**
     * get senders
     * 
     * @return
     */
    public List<MessageSender> getSenders() {
        return mSenders;
    }

    public void registerContentObservers() {
        getContentResolver().registerContentObserver(CONTENT_URI_MESSAGE, true, mContentObserver);
        getContentResolver().registerContentObserver(CONTENT_URI_MSGUSER, true, mContentObserver);
    }

    public void unregisteContentObservers() {
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

}
