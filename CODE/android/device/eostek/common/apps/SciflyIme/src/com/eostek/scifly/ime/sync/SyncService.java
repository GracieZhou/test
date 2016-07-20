
package com.eostek.scifly.ime.sync;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.eostek.scifly.ime.sync.sensors.ISensorSyncManager;
import com.eostek.scifly.ime.sync.words.IWordsSyncManager;
import com.eostek.scifly.ime.util.Constans;

/**
 * service of synchronization.
 * 
 * @author frank.zhang
 * @date 2014-12-2.
 */
public class SyncService extends Service {
    private static final String TAG = "SyncService";

    private SyncServiceBinder mSyncServiceBinder;

    private static final boolean DBG = true;

    private ServerSocket mServerSocket = null;

    private AcceptThread mAcceptThread = null;

    private boolean mAllowAccept = true;

    private ISensorSyncManager mSensorSyncManager = null;

    private IWordsSyncManager mWordsSyncManager = null;

    private static final int SERVER_PORT = 29807;

    @Override
    public IBinder onBind(Intent arg0) {
        return mSyncServiceBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Constans.print(TAG, "onCreate");
        mSyncServiceBinder = new SyncServiceBinder(this);

        init();
        startSyncServer();
    }

    private void init() {
        Constans.print(TAG, "init");
        mSensorSyncManager = mSyncServiceBinder.getSensorSyncManager();
        mWordsSyncManager = mSyncServiceBinder.getWordsSyncManager();
        mAcceptThread = new AcceptThread();

        try {
            mServerSocket = new ServerSocket(SERVER_PORT);

        } catch (IOException e) {
            Constans.printE(TAG, e.getMessage());
        }
    }

    private void stopSyncServer() {
        Constans.print(TAG, "stopSyncServer");
        try {
            mAllowAccept = false;
            mAcceptThread.join();
            if (null != mServerSocket) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            Constans.printE(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Constans.printE(TAG, e.getMessage());
        }
    }

    private void startSyncServer() {
        Constans.print(TAG, "startSyncServer");
        if (null != mServerSocket) {
            mAcceptThread.start();
        } else {
            Constans.printE(TAG, "start sync server failed!");
        }

    }

    private ArrayList<Socket> mClientList = new ArrayList<Socket>();

    class AcceptThread extends Thread {

        @Override
        public void run() {
            super.run();

            while (mAllowAccept) {
                try {
                    final Socket client = mServerSocket.accept();

                    synchronized (this) {
                        mClientList.add(client);
                        if (DBG) {
                            Constans.printE(TAG, "accept socket=" + client.toString());
                        }

                        try {
                            mWordsSyncManager.onClientConnect(client.getInetAddress().toString().substring(1));
                        } catch (RemoteException e) {
                            Constans.printE(TAG, e.getMessage());
                        }
                    }
                    new EventHandleThread(client).start();
                } catch (IOException e) {
                    Constans.printE(TAG, e.getMessage());
                }
            }

            if (false == mAllowAccept) {
                try {
                    for (Socket client : mClientList) {
                        client.close();
                    }
                    mClientList.clear();
                } catch (IOException e) {
                    Constans.printE(TAG, e.getMessage());
                }
            }
        }
    }

    class EventHandleThread extends Thread {

        private Socket mClient = null;

        private SyncEventReader mReader;

        EventHandleThread(Socket client) {
            mClient = client;
        }

        @Override
        public void run() {
            super.run();
            mReader = new SyncEventReader(mClient);

            while (mClient.isConnected()) {

                try {
                    if (mReader.readReply()) {
                        switch (mReader.getType()) {
                            case SyncEvent.TYPE_SENSOR:
                                synchronized (this) {
                                    mSensorSyncManager.onDataAvailable(mReader.getData());
                                }
                                break;

                            case SyncEvent.TYPE_WORDS:
                                synchronized (this) {
                                    mWordsSyncManager.onDataAvailable(mReader.getData());
                                }
                                break;

                            default:
                                break;
                        }

                    } else {

                        try {

                            synchronized (this) {
                                mClientList.remove(mClient);

                                mWordsSyncManager.onClientDisconnnect(mClient.getInetAddress().toString().substring(1));

                                mClient.close();
                            }
                        } catch (IOException e) {
                            Constans.printE(TAG, e.getMessage());
                        }

                        break;
                    }
                } catch (RemoteException e) {
                    Constans.printE(TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopSyncServer();

    }

}
