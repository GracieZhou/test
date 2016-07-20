/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.pppoe;

import android.content.Context;
import android.content.pm.PackageManager;
import com.android.internal.util.IndentingPrintWriter;
import android.net.ConnectivityManager;

import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.LinkAddress;
import android.net.NetworkAgent;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.os.Binder;
import android.os.IBinder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.PrintWriterPrinter;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mstar.android.pppoe.IPppoeManager;
import com.mstar.android.pppoe.PppoeManager;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.SystemProperties;
import android.content.Intent;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/**
 * PppoeServiceImpl handles remote Pppoe operation requests by implementing
 * the IPppoeManager interface.
 *
 * @hide
 */
public class PppoeServiceImpl extends IPppoeManager.Stub {
    private static final String TAG = "PppoeServiceImpl";
    private static final int LINK_UP = 30;
    private static final int LINK_DOWN = 31;
    private static final int LINK_AUTH_FAIL = 32;
    private static final int LINK_TIME_OUT = 33;
    private static final int LINK_PPP_FAIL = 34;
    private static final int LINK_DISCONNECTING = 35;
    private static final int LINK_CONNECTING = 36;
    private String mPppoeState = null;
    private Thread mSocketThread = null;
    private final Context mContext;
    //private final PppoeConfigStore mPppoeConfigStore;
    private final INetworkManagementService mNMService;
    private final AtomicBoolean mStarted = new AtomicBoolean(false);
    private IpConfiguration mIpConfiguration;
    private ConnectivityManager mCM;
    private Handler mHandler;
    private NetworkInfo mNetworkInfo;
    private final PppoeNetworkFactory mTracker;
    public PppoeServiceImpl(Context context) {
        mContext = context;
        Log.i(TAG, "Creating PppoeConfigStore");
        IBinder b = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
        mNMService = INetworkManagementService.Stub.asInterface(b);
        mTracker = new PppoeNetworkFactory();
        mPppoeState = "disconnect";
    }
    public void start() {
        startMonitorThread();
        Log.i(TAG, "Starting Pppoe service");
        mCM = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        HandlerThread handlerThread = new HandlerThread("PppoeServiceThread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        mTracker.start(mContext, mHandler);
    }
     /**
     *function:set pppoe status
     */
    public void setPppoeStatus(String status, boolean sendBroadcast) {
        mPppoeState = status;
        if (sendBroadcast) {
            sendPppBroadcast(mPppoeState);
            // Kill all pppoe processes
            if (status != null && status.equals(PppoeManager.PPPOE_STATE_DISCONNECT)) {
                SystemProperties.set("ctl.start", "pppoe-stop");
            }
        }
    }

    /**
     *function:get pppoe status
     */
    public String getPppoeStatus() {
        return mPppoeState;
    }

     /**
     *function:send ppp status
     */
    private void sendPppBroadcast(String pppState) {
        Intent intent = new Intent();
        intent.setAction("com.mstar.android.pppoe.PPPOE_STATE_ACTION");
        intent.putExtra("PppoeStatus", pppState);
        mContext.sendBroadcast(intent);
    }

    private void startMonitorThread() {
        mSocketThread = new Thread("pppoe_monitor_thread") {
            public void run() {
                try {
                    LocalServerSocket server2 = new LocalServerSocket("pppoe.localsocket");
                    LocalSocket receiver2 = null;
                    InputStream in = null;
                    //DataInputStream din = null;
                    BufferedReader br = null;
                    while (true) {
                        if (receiver2 != null) {
                            receiver2.close();
                            receiver2 = null;
                        }
                        if (in != null) {
                            in.close();
                            in = null;
                        }
                        if (br != null) {
                            br.close();
                            br = null;
                        }

                        receiver2 = server2.accept();
                        if (receiver2 == null) {
                            Log.e(TAG, "Can not accept socket");
                            continue;
                        }
                        in = receiver2.getInputStream();
                        //din = new DataInputStream(in);
                        InputStreamReader isr = new InputStreamReader(in);
                        br = new BufferedReader(isr);
                        if (receiver2 != null) {
                            String event = br.readLine();
                            handleEvent(event);
                            Log.i(TAG, "socket event = " + event);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mSocketThread.start();
    }
    /**
     *function:handleEvent
     */
    private void handleEvent(String event) {
        if (null == event) {
            return;
        }

        String [] eventInfo = event.split(":");
        if (eventInfo.length != 2) {
            Log.e(TAG, "pppoe event error");
            return;
        }

        int eventCommand = Integer.parseInt(eventInfo[1]);
        int message = PppoeManager.MSG_PPPOE_DISCONNECT;
        switch (eventCommand) {
            case LINK_UP:
                message = PppoeManager.MSG_PPPOE_CONNECT;
                break;
            case LINK_DOWN:
                message = PppoeManager.MSG_PPPOE_DISCONNECT;
                break;
            case LINK_AUTH_FAIL:
                message = PppoeManager.MSG_PPPOE_AUTH_FAILED;
                break;
        }

        String pppStatus = getStateFromMsg(message);
        if (false == (pppStatus.equals(PppoeManager.PPPOE_STATE_AUTHFAILED))) {
            setPppoeStatus(pppStatus, true);
        } else {
            setPppoeStatus(pppStatus, false);
        }
    }

    /**
     *fuction:getStateFromMsg
     */
    private String getStateFromMsg(int msg) {
        String pppState = PppoeManager.PPPOE_STATE_DISCONNECT;
        switch (msg) {
            case PppoeManager.MSG_PPPOE_CONNECT:
                pppState = PppoeManager.PPPOE_STATE_CONNECT;
                break;
            case PppoeManager.MSG_PPPOE_AUTH_FAILED:
                pppState = PppoeManager.PPPOE_STATE_AUTHFAILED;
                break;
            case PppoeManager.MSG_PPPOE_DISCONNECTING:
                pppState = PppoeManager.PPPOE_STATE_DISCONNECTING;
                break;
            case PppoeManager.MSG_PPPOE_CONNECTING:
                pppState = PppoeManager.PPPOE_STATE_CONNECTING;
                break;
            case PppoeManager.MSG_PPPOE_TIME_OUT:
                pppState = PppoeManager.PPPOE_STATE_LINKTIMEOUT;
                break;
            case PppoeManager.MSG_PPPOE_FAILED:
                pppState = PppoeManager.PPPOE_STATE_FAILED;
                break;
        }
        return pppState;
    }
}
