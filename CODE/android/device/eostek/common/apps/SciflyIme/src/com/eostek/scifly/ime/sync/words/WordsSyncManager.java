
package com.eostek.scifly.ime.sync.words;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;

import com.eostek.scifly.ime.sync.SyncEvent;
import com.eostek.scifly.ime.sync.SyncEventReader;
import com.eostek.scifly.ime.util.Constans;

/**
 * implementation of IWordsSyncManager.
 */
public class WordsSyncManager extends IWordsSyncManager.Stub {

    private static final String TAG = "WordsSyncManager";

    private static final boolean DBG = true;

    private static IWordsSyncManager _sInstance = null;

    private List<IWordsChangedListener> mListeners = new ArrayList<IWordsChangedListener>();

    // IP_ADDRESS;
    private String mISynergyHostIP = null;

    // PORT
    private int mISynergyServerPort = 0;

    // SOCKET
    private Socket mISynergySock = null;

    private boolean mHasClient = false;

    private Handler mHandler = null;

    private SyncEventReader mEventWriter = null;

    private static final int ACTIONE_CONNECT_TO_SERVER = 100;

    private static final int ACTIONE_SEND_WORDS_TO_SERVER = 101;

    private static final int ACTIONE_DISPATCH_WORDS_TO_LISTENER = 102;

    /**
     * The singleton pattern
     * 
     * @param context
     * @return IWordsSyncManager instance
     */
    public static IWordsSyncManager getInstance(Context context) {
        if (null == _sInstance) {
            _sInstance = new WordsSyncManager(context);
        }

        return _sInstance;
    }

    /**
     * @param context service.
     */
    private WordsSyncManager(Context context) {

        if (null == mHandler) {
            HandlerThread ht = new HandlerThread("wordsfly thread");
            ht.start();
            mHandler = new Handler(ht.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case ACTIONE_CONNECT_TO_SERVER:
                            mISynergyServerPort = Integer.parseInt((String) msg.obj);
                            try {
                                mISynergySock = new Socket(mISynergyHostIP, mISynergyServerPort);

                                if (mISynergySock.isConnected()) {
                                    mEventWriter = new SyncEventReader(mISynergySock);
                                    if (DBG) {
                                        Constans.printE(TAG, "Connected to iSynergy at port:" + mISynergyServerPort
                                                + ", host:" + mISynergyHostIP);
                                    }

                                    mHasClient = true;

                                    setSockStatusToListeners(true);
                                }

                            } catch (UnknownHostException e) {
                                Constans.printE(TAG, e.getMessage());
                                mHasClient = false;
                            } catch (IOException e) {
                                Constans.printE(TAG, e.getMessage());
                                mHasClient = false;
                            }

                            break;

                        case ACTIONE_SEND_WORDS_TO_SERVER:
                            if (mHasClient && (null != mEventWriter)) {
                                mEventWriter.writeSyncEvent(new SyncEvent(SyncEvent.TYPE_WORDS, (String) msg.obj));
                            }
                            break;

                        case ACTIONE_DISPATCH_WORDS_TO_LISTENER:
                            dispatchWords((String) msg.obj);

                        default:
                            break;
                    }
                }
            };
        }

        if (DBG) {
            Constans.print(TAG, "Constructed.");
        }

    }

    private void dispatchWords(String receivedWords) {

        if (mListeners != null && mListeners.size() > 0) {
            for (IWordsChangedListener listener : mListeners) {
                try {
                    listener.onWordsChanged(Constans.TYPE_WORDS, receivedWords);
                } catch (RemoteException e) {
                    Constans.printE(TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean registerWordsChangedListener(IWordsChangedListener wordsChangedListener) throws RemoteException {

        for (IWordsChangedListener listener : mListeners) {
            if (listener.equals(wordsChangedListener)) {
                return false;
            }
        }
        mListeners.add(wordsChangedListener);

        if (DBG) {
            Constans.print(TAG, "add a new listener, size:" + mListeners.size());
        }

        return true;
    }

    @Override
    public void unregisterWordsChangedListener(IWordsChangedListener wordsChangedListener) throws RemoteException {
        if (wordsChangedListener != null) {
            mListeners.remove(wordsChangedListener);
        }
    }

    @Override
    public void sendBySocket(int type, String words) throws RemoteException {
        if (DBG) {
            Constans.printE(TAG, "type:" + type + ", words:" + words);
        }
        sendWordsBySocket(type, words);
    }

    @Override
    public boolean isSocketAvailable() throws RemoteException {

        return mHasClient;
    }

    private void sendWordsBySocket(int type, String s) {
        if ((null != s) && (Constans.TYPE_WORDS == type)) {
            Message msg = mHandler.obtainMessage(ACTIONE_SEND_WORDS_TO_SERVER, "0," + s);
            msg.sendToTarget();
        }
    }

    @Override
    public void release() throws RemoteException {

    }

    @Override
    public void onDataAvailable(byte[] data) {

        // the format of the words message:
        // first char is the type, maybe cmd or words;
        // other chars are the message body, so the first 4 bytes of the data
        // are
        // the type, any message must longer then 4 bytes.
        if (null != data && data.length > 0) {
            String raw = new String(data);

            if (DBG) {
                Constans.printE(TAG, "onDataAvailable:" + raw);
            }

            switch (Integer.parseInt(raw.substring(0, 1))) {
                case Constans.TYPE_WORDS:
                    Message msg = mHandler.obtainMessage(ACTIONE_DISPATCH_WORDS_TO_LISTENER, unpack(raw));
                    msg.sendToTarget();
                    break;

                case Constans.TYPE_PORT:
                    Message msg1 = mHandler.obtainMessage(ACTIONE_CONNECT_TO_SERVER, unpack(raw));
                    msg1.sendToTarget();
                    break;

                default:
                    break;
            }
        }
    }

    private String unpack(String raw) {
        return raw.substring(2);
    }

    @Override
    public void onClientConnect(String clientIP) throws RemoteException {
        if (DBG) {
            Constans.printE(TAG, "client connected: " + clientIP);
        }

        if (null == mISynergyHostIP) {
            mISynergyHostIP = clientIP;
        }
    }

    @Override
    public void onClientDisconnnect(String clientIP) throws RemoteException {
        if (DBG) {
            Constans.printE(TAG, "client disconnected: " + clientIP);
        }
        if (null != mISynergyHostIP && mISynergyHostIP.equals(clientIP)) {
            mISynergyHostIP = null;
            mHasClient = false;

            setSockStatusToListeners(false);

            try {
                if (null != mISynergySock) {
                    mISynergySock.close();
                }
            } catch (IOException e) {
                Constans.printE(TAG, e.getMessage());
            }

        }
    }

    private void setSockStatusToListeners(boolean isAvailable) {
        if (mListeners != null && mListeners.size() > 0) {
            for (IWordsChangedListener listener : mListeners) {
                try {
                    if (null != listener) {
                        listener.socketStatus(isAvailable);
                    }
                } catch (RemoteException e) {
                    Constans.printE(TAG, e.getMessage());
                }
            }
        }
    }
}
