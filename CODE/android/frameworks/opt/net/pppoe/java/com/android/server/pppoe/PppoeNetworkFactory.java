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
import android.net.ConnectivityManager;
import android.net.DhcpResults;
import android.net.InterfaceConfiguration;
import android.net.NetworkUtils;
import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkAgent;
import android.net.NetworkCapabilities;
import android.net.NetworkFactory;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkRequest;
import com.mstar.android.pppoe.IPppoeManager;
import com.mstar.android.pppoe.PppoeManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;

import com.android.internal.util.IndentingPrintWriter;
import com.android.server.net.BaseNetworkObserver;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.RouteInfo;
import android.net.LinkAddress;
import java.net.InetAddress;
import android.content.BroadcastReceiver;
import java.net.UnknownHostException;


/**
 * Manages connectivity for an Pppoe interface.
 *
 * Pppoe Interfaces may be present at boot time or appear after boot (e.g.,
 * for Pppoe adapters connected over USB). This class currently supports
 * only one interface. When an interface appears on the system (or is present
 * at boot time) this class will start tracking it and bring it up, and will
 * attempt to connect when requested. Any other interfaces that subsequently
 * appear will be ignored until the tracked interface disappears. Only
 * interfaces whose names match the <code>config_ethernet_iface_regex</code>
 * regular expression are tracked.
 *
 * This class reports a static network score of 70 when it is tracking an
 * interface and that interface's link is up, and a score of 0 otherwise.
 *
 * @hide
 */
class PppoeNetworkFactory {

    private static final String NETWORK_TYPE = "Pppoe";
    private static final String TAG = "PppoeNetworkFactory";
    private static final int NETWORK_SCORE = 70;
    private static final boolean DBG = true;

    /** For static IP configuration */
    private PppoeManager mPppoeManager;

    /** To set link state and configure IP addresses. */
    private INetworkManagementService mNMService;

    /* To communicate with ConnectivityManager */
    private NetworkCapabilities mNetworkCapabilities;
    private NetworkAgent mNetworkAgent;
    private LocalNetworkFactory mFactory;
    private Context mContext;
    /** Data members. All accesses to these must be synchronized(this). */

    private static boolean mLinkUp;
    private NetworkInfo mNetworkInfo;
    private LinkProperties mLinkProperties;
    private BroadcastReceiver mPppoeStateReceiver;
    private int mPrefixLength;

    PppoeNetworkFactory() {
        mNetworkInfo = new NetworkInfo(ConnectivityManager.TYPE_PPPOE, 0, NETWORK_TYPE, "");
        mNetworkInfo.setIsAvailable(false);
        mLinkProperties = new LinkProperties();
        initNetworkCapabilities();
        mPppoeManager = null;
    }

    private class PppoeStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkPppoeManager(context);
            String action = intent.getAction();
            if (action.equals(PppoeManager.PPPOE_STATE_ACTION)) {
                String pppState = intent.getStringExtra(PppoeManager.PPPOE_STATE_STATUE);
                updateInterfaceState(true);
                notifyStateChange(pppState);
            }
        }

    }
     /**
     * Updates interface state variables.
     * Called on link state changes or on startup.
     */
    private void updateInterfaceState(boolean up) {
        Log.d(TAG," link " + (up ? "up" : "down"));

        synchronized(this) {
            mLinkUp = up;
            mNetworkInfo.setIsAvailable(up);
            if (!up) {
                // Tell the agent we're disconnected. It will call disconnect().
                mNetworkInfo.setDetailedState(DetailedState.DISCONNECTED, null, null);
            }
            updateAgent();
            // set our score lower than any network could go
            // so we get dropped.  TODO - just unregister the factory
            // when link goes down.
            mFactory.setScoreFilter(up ? NETWORK_SCORE : -1);
        }
    }

    private void checkPppoeManager(Context context) {
        if (mPppoeManager == null) {
            mPppoeManager  = PppoeManager.getInstance(context);
        }
    }

    private void notifyStateChange(String status) {
        if (status.equals(PppoeManager.PPPOE_STATE_CONNECT)
            || status.equals(PppoeManager.PPPOE_STATE_DISCONNECT)) {
            if (status.equals(PppoeManager.PPPOE_STATE_CONNECT)) {
                updateLinkProperties();
            } else if (status.equals(PppoeManager.PPPOE_STATE_DISCONNECT)) {
                mNetworkInfo.setDetailedState(DetailedState.DISCONNECTED,null,null);
                mNetworkInfo.setIsAvailable(false);
            }
        }
    }
    private LinkAddress makeLinkAddress() {
        String ipaddr = mPppoeManager.getIpaddr();
        if (TextUtils.isEmpty(ipaddr)) {
            Log.d(TAG, "pppoe ip is null");
            return null;
        }
        return new LinkAddress(NetworkUtils.numericToInetAddress(ipaddr), mPrefixLength);
    }

    private void updateLinkProperties() {
        mLinkProperties.clear();
        LinkAddress linkAddress = makeLinkAddress();
        mLinkProperties.addLinkAddress(linkAddress);

        try {
            //InetAddress ia = InetAddress.getByName(mPppoeManager.getRoute());
            InetAddress pppoeGateway = InetAddress.getByName(mPppoeManager.getIpaddr());
            RouteInfo routeInfo = new RouteInfo(linkAddress, pppoeGateway);
            mLinkProperties.addRoute(routeInfo);
        } catch (UnknownHostException e) {
            Log.d(TAG, "failed to add route");
        }

        String dns1 = mPppoeManager.getDns1();
        if (TextUtils.isEmpty(dns1) == false) {
            mLinkProperties.addDnsServer(NetworkUtils.numericToInetAddress(dns1));
        } else {
            Log.d(TAG, "dns1 is empty");
        }

        String dns2 = mPppoeManager.getDns2();
        if (TextUtils.isEmpty(dns2) == false) {
            mLinkProperties.addDnsServer(NetworkUtils.numericToInetAddress(dns2));
        } else {
            Log.d(TAG, "dns2 is empty");
        }

        mLinkProperties.setInterfaceName(mPppoeManager.getInterfaceName());

        Log.d(TAG, "print linkproperties of pppoe:");
        Log.d(TAG, mLinkProperties.toString());
              String tcpBufferSizes = mContext.getResources().getString(
                      com.android.internal.R.string.config_ethernet_tcp_buffers);
              if (TextUtils.isEmpty(tcpBufferSizes) == false) {
                      mLinkProperties.setTcpBufferSizes(tcpBufferSizes);
        }
        synchronized(PppoeNetworkFactory.this) {
                    if (mNetworkAgent != null) {
                        Log.e(TAG, "Already have a NetworkAgent - aborting new request");
                        return;
                    }
                    mNetworkInfo.setIsAvailable(true);
                    mNetworkInfo.setDetailedState(DetailedState.CONNECTED, null, null);
                    // Create our NetworkAgent.
                    mNetworkAgent = new NetworkAgent(mFactory.getLooper(), mContext,
                            NETWORK_TYPE, mNetworkInfo, mNetworkCapabilities, mLinkProperties,
                            NETWORK_SCORE) {
                        public void unwanted() {
                            synchronized(PppoeNetworkFactory.this) {
                                if (this == mNetworkAgent) {
                                    mLinkProperties.clear();
                                    mNetworkInfo.setDetailedState(DetailedState.DISCONNECTED, null,null);
                                    updateAgent();
                                    mNetworkAgent = null;
                                } else {
                                    Log.d(TAG, "Ignoring unwanted as we have a more modern " +
                                            "instance");
                                }
                            }
                        };
                    };
                }
    }
     private class LocalNetworkFactory extends NetworkFactory {
        LocalNetworkFactory(String name, Context context, Looper looper) {
            super(looper, context, name, new NetworkCapabilities());
        }

     protected void startNetwork() {
             // TODO
            // Enter association mode.
            Log.d(TAG,"Enter association mode");
        }

     protected void stopNetwork() {
        }
    }

    public void updateAgent() {
        synchronized (PppoeNetworkFactory.this) {
            if (mNetworkAgent == null) return;
            if (DBG) {
                Log.i(TAG, "Updating mNetworkAgent with: " +
                      mNetworkCapabilities + ", " +
                      mNetworkInfo + ", " +
                      mLinkProperties);
            }
            mNetworkAgent.sendNetworkCapabilities(mNetworkCapabilities);
            mNetworkAgent.sendNetworkInfo(mNetworkInfo);
            mNetworkAgent.sendLinkProperties(mLinkProperties);
            // never set the network score below 0.
            mNetworkAgent.sendNetworkScore(mLinkUp? NETWORK_SCORE : 0);
        }
    }

    /**
     * Begin monitoring connectivity
     */
    public synchronized void start(Context context, Handler target) {
        // The services we use.
        IBinder b = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
        mNMService = INetworkManagementService.Stub.asInterface(b);
        mPppoeManager = (PppoeManager) context.getSystemService(Context.PPPOE_SERVICE);
        // Create and register our NetworkFactory
        mFactory = new LocalNetworkFactory(NETWORK_TYPE, context, target.getLooper());
        mFactory.setCapabilityFilter(mNetworkCapabilities);
        mFactory.setScoreFilter(-1); // this set high when we have an iface
        mFactory.register();
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(PppoeManager.PPPOE_STATE_ACTION);
        mPppoeStateReceiver = new PppoeStateReceiver();
        mContext.registerReceiver(mPppoeStateReceiver, filter);
        mNetworkAgent = null;
        updateInterfaceState(true);
      }

    public synchronized void stop() {
        // ConnectivityService will only forget our NetworkAgent if we send it a NetworkInfo object
        // with a state of DISCONNECTED or SUSPENDED. So we can't simply clear our NetworkInfo here:
        // that sets the state to IDLE, and ConnectivityService will still think we're connected.
        //
        // TODO: stop using explicit comparisons to DISCONNECTED / SUSPENDED in ConnectivityService,
        // and instead use isConnectedOrConnecting().
        mNetworkInfo.setDetailedState(DetailedState.DISCONNECTED, null, null);
        mLinkUp = false;
        updateAgent();
        mLinkProperties = new LinkProperties();
        mNetworkAgent = null;
        mNetworkInfo = new NetworkInfo(ConnectivityManager.TYPE_PPPOE, 0, NETWORK_TYPE, "");
        mFactory.unregister();
    }

    private void initNetworkCapabilities() {
        mNetworkCapabilities = new NetworkCapabilities();
        mNetworkCapabilities.addTransportType(NetworkCapabilities.TRANSPORT_PPPOE);
        mNetworkCapabilities.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        mNetworkCapabilities.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
        // We have no useful data on bandwidth. Say 100M up and 100M down. :-(
        mNetworkCapabilities.setLinkUpstreamBandwidthKbps(100 * 1000);
        mNetworkCapabilities.setLinkDownstreamBandwidthKbps(100 * 1000);
    }

    synchronized void dump(FileDescriptor fd, IndentingPrintWriter pw, String[] args) {
        pw.println();
        pw.println("NetworkInfo: " + mNetworkInfo);
        pw.println("LinkProperties: " + mLinkProperties);
        pw.println("NetworkAgent: " + mNetworkAgent);
    }
}
