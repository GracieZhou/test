
package com.eostek.scifly.ime.sync.sensors;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.eostek.scifly.ime.util.Constans;

/**
 * implementation of ISensorSyncManager.
 */
public class SensorSyncManager extends ISensorSyncManager.Stub {

    private static final String TAG = "SensorSyncManager";

    private static final boolean DBG = false;

    private static SensorSyncManager _sInstance = null;

    private Context mContext = null;

    private static Handler mDataIncomingHandler = null;

    private static final int ACTION_PROCESS_DATA = 1;

    private SensorSyncManager(Context ctx) {
        if (null == mContext) {
            mContext = ctx;
        }
        if (null == mDataIncomingHandler) {
            HandlerThread ht = new HandlerThread("sync thread");
            ht.start();
            mDataIncomingHandler = new Handler(ht.getLooper()) {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case ACTION_PROCESS_DATA:
                            handleDataIncoming((byte[]) msg.obj);
                            break;

                        default:
                            break;
                    }
                }
            };
        }

        startRemoteControlSensorServer();
    }

    public static ISensorSyncManager getInstance(Context ctx) {
        if (_sInstance == null) {
            _sInstance = new SensorSyncManager(ctx);
        }

        return _sInstance;
    }

    private SensorEventBuilder builder = new SensorEventBuilder();

    @Override
    public void onDataAvailable(byte[] data) {
        if ((null != data) && (data.length >= 17)) {

            if (DBG) {
                builder.decode(data);
                StringBuilder sb = new StringBuilder();
                switch (builder.getSensorType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        sb.append("ACCELEROMETER-->");
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        sb.append("GYROSCOPE-->");
                        break;
                    default:
                        break;
                }
                sb.append("accuracy:" + builder.getAccuracy() + ", timestamp:" + System.currentTimeMillis() + ", x:"
                        + builder.getValues()[0] + ", y:" + builder.getValues()[1] + ", z:" + builder.getValues()[2]);
                Log.d(TAG, sb.toString());
            }

            if (null != mDataIncomingHandler) {
                Message msg = mDataIncomingHandler.obtainMessage(ACTION_PROCESS_DATA, data);
                msg.sendToTarget();
            }
        }
    }

    private OutputStream mOut = null;

    private byte[] buf = new byte[2];

    private synchronized void handleDataIncoming(byte[] data) {
        if (!mClientManager.isEmpty()) {
            Iterator<LocalSocket> clientIterator = mClientManager.iterator();
            while (clientIterator.hasNext()) {
                LocalSocket client = clientIterator.next();
                try {
                    int len = data.length;
                    buf[0] = (byte) (len & 0xff);
                    buf[1] = (byte) ((len >> 8) & 0xff);

                    mOut = new DataOutputStream(client.getOutputStream());
                    mOut.write(buf, 0, 2);
                    mOut.write(data, 0, len);
                    mOut.flush();
                } catch (IOException e) {
                    Log.e(TAG, "dispatch exception: " + e.getMessage());
                    try {
                        mOut.close();
                        client.close();
                    } catch (IOException e1) {
                        Log.e(TAG, "disconnect exception: " + e1.getMessage());
                    } finally {
                        clientIterator.remove();
                    }
                }
            }
        }
    }

    private static final String SOCKET_ADDRESS = "eostek_rc_sensor";

    private LocalServerSocket mServerSocket = null;

    private AcceptThread mAcceptThread = null;

    private boolean mAllowAccept = true;

    private List<LocalSocket> mClientManager = Collections.synchronizedList(new ArrayList<LocalSocket>());

    private void startRemoteControlSensorServer() {
        Constans.print(TAG, "====startRemoteControlSensorServer");
        try {
            mServerSocket = new LocalServerSocket(SOCKET_ADDRESS);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
    }

    private void stopRemoteControlSensorServer() {
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

    class AcceptThread extends Thread {

        @Override
        public void run() {
            super.run();

            while (mAllowAccept) {
                try {
                    final LocalSocket client = mServerSocket.accept();
                    mClientManager.add(client);
                    Constans.print(TAG, "mClient count:" + mClientManager.size());
                    Log.i(TAG, "accept socket=" + client.toString());

                    mContext.sendBroadcast(new Intent("com.eostek.intent.action.iSyngerSensorPresent"));
                } catch (IOException e) {
                    Constans.printE(TAG, e.getMessage());
                }
            }

            if (false == mAllowAccept) {
                try {
                    for (LocalSocket client : mClientManager) {
                        client.close();
                    }
                    mClientManager.clear();
                    stopRemoteControlSensorServer();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

}
