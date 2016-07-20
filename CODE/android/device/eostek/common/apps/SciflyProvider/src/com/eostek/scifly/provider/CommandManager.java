
package com.eostek.scifly.provider;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Messages;
import scifly.provider.metadata.IMsgManager;
import scifly.provider.metadata.IMsgStateListener;
import scifly.provider.metadata.Msg;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.eostek.scifly.provider.SciflyProvider.Message;

/**
 * Manager the command of MessageCenter from server.
 * 
 * @author charles.tai
 * @since 2014-08-15
 */
public class CommandManager {

    private static final String TAG = "SciflyProvider";

    private static final String SERVICE_ACTION = "scifly.provider.metadata.MsgService";

    private static final boolean DBG = true;

    private static final int MESSAGE_READ = 1;

    private Context mContext;

    private Queue mQueue;

    private MessageThread mThread;

    private IMsgManager mService;

    private Msg mMessage;

    private boolean mFlag = false;

    protected boolean mConnected = false;

    /**
     * @param context {@link Context}
     */
    public CommandManager(Context context) {
        mContext = context;
        mContext.bindService(new Intent(SERVICE_ACTION), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * put the command of message into the Queue.
     * 
     * @param message {@link Msg}
     */
    public void putCommand(Msg message) {
        if (message.mCategory == Messages.CATEGORY_BROADCAST
                || message.mCategory == Messages.CATEGORY_UPDATE_RESULT
                || message.mCategory == Messages.CATEGORY_TEXT
                || message.mCategory == Messages.CATEGORY_UPLOAD_LOG
                || message.mCategory == Messages.CATEGORY_UPLOAD_SCREEN_SHOT
                || message.mCategory == Messages.CATEGORY_BOOT_LOGO
                || message.mCategory == Messages.CATEGORY_APK_INSTALL
                || message.mCategory == Messages.CATEGORY_APK_UNINSTALL
                || message.mCategory == Messages.CATEGORY_APK_STARTUP
                || ((message.mCategory == Messages.CATEGORY_URL) && (message.mSource == Messages.SOURCE_CPE))
                || ((message.mCategory == Messages.CATEGORY_IMAGE) && (message.mSource == Messages.SOURCE_CPE))
                || message.mCategory == Messages.CATEGORY_ADS_SWITCH
                || message.mCategory == Messages.CATEGORY_BOOT_VIDEO
                || message.mCategory == Messages.CATEGORY_BOOT_VIDEO_RECOVERY
                || message.mCategory == Messages.CATEGORY_UPDATE_CLOUD_PUSH) {
            Log.d(TAG, "putCommandMessage Msg.mCategory : " + message.mCategory);
            if (null == mMessage) {
                mMessage = new Msg();
            }
            mMessage = message;
            if (null == mQueue) {
                mQueue = new Queue();
            }
            mQueue.add(message);
            mFlag = true;
            if (null == mThread) {
                mThread = new MessageThread();
                mThread.start();
            }
            mThread.run();
        }
    }

    /**
     * Thread to handler the Msgservice.
     */
    private class MessageThread extends Thread {
        @Override
        public void run() {
            while (mFlag) {
                if (mQueue.isEmpty()) {
                    mFlag = false;
                } else {
                    synchronized (this) {
                        mMessage = mQueue.poll();
                        try {
                            // send the cmd to the MessageCenter by the service.
                            if (null != mService) {
                                Log.d(TAG, "Service is Connected ? " + mConnected);
                                if (mConnected) {
                                    mService.putCommand(mMessage);
                                } else {
                                    if (mServiceConnection == null) {
                                        mContext.bindService(new Intent(SERVICE_ACTION), mServiceConnection,
                                                Context.BIND_AUTO_CREATE);
                                    }
                                    mService.putCommand(mMessage);
                                }
                            } else {
                                Log.w(TAG, "MsgService is null ? ");
                                return;
                            }
                        } catch (RemoteException e) {
                            Log.e(TAG, "Can not get the status of message service." + e.getMessage());
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.e(TAG, "Put Command Exception." + e.getMessage());
                            e.printStackTrace();
                        }
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /**
         * notify the current thread the cmd has handled.
         */
        private void notifyCommand() {
            synchronized (this) {
                notify();
            }
        }
    }

    /**
     * Listener to change the state of message.
     */
    private class MsgStateListener extends IMsgStateListener.Stub {

        @Override
        public void stateChanged(Msg message) throws RemoteException {
            Log.d(TAG, "stateChanged");
            mThread.notifyCommand();
            ContentValues values = new ContentValues();
            if (message.mCategory == Messages.CATEGORY_BROADCAST
                    || message.mCategory == Messages.CATEGORY_UPDATE_RESULT
                    || message.mCategory == Messages.CATEGORY_UPLOAD_LOG
                    || message.mCategory == Messages.CATEGORY_UPLOAD_SCREEN_SHOT
                    || message.mCategory == Messages.CATEGORY_BOOT_LOGO
                    || message.mCategory == Messages.CATEGORY_APK_INSTALL
                    || message.mCategory == Messages.CATEGORY_APK_STARTUP
                    || message.mCategory == Messages.CATEGORY_APK_UNINSTALL) {
                values.put(Message.STATUS, MESSAGE_READ);
                values.put(Message.CATEGORY, message.mCategory);
                SciflyStore.Messages.updateMessage(mContext.getContentResolver(), values, "time=?", new String[] {
                    Long.toString(message.mTime)
                });
            }
        }
    }

    private MsgServiceConnectedCallBack mMsgServiceConnectedCallBack;

    public void setMsgServiceConnectedCallBack(MsgServiceConnectedCallBack callBack) {
        mMsgServiceConnectedCallBack = callBack;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (DBG) {
                Log.d(TAG, "MessageService is disconnected");
            }
            mConnected = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (DBG) {
                Log.d(TAG, "MessageService is connected");
            }
            mConnected = true;

            mService = IMsgManager.Stub.asInterface(service);
            try {
                mService.setMsgStateChangeListener(new MsgStateListener());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (mMsgServiceConnectedCallBack != null) {
                mMsgServiceConnectedCallBack.onServiceConnected();
            }

        }
    };

    public interface MsgServiceConnectedCallBack {
        void onServiceConnected();
    }

    /**
     * save the Message into LinkedList by Queue.
     */
    private class Queue {

        private LinkedList<Msg> mLinkedList = new LinkedList<Msg>();

        private boolean add(Msg message) {
            return mLinkedList.add(message);
        }

        private Msg poll() {
            if (mLinkedList.size() == 0) {
                Log.e(TAG, "the size of mLinedList is 0.");
                return mMessage;
            }
            return mLinkedList.removeFirst();
        }

        private boolean isEmpty() {
            return mLinkedList.isEmpty();
        }

        @SuppressWarnings("unused")
        private int size() {
            return mLinkedList.size();
        }
    }

}
