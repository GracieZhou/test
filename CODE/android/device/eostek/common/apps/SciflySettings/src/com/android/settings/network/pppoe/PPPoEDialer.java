//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.android.settings.network.pppoe;

import java.io.File;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.settings.R;
import com.mstar.android.pppoe.PPPOE_STA;
import com.mstar.android.pppoe.PppoeManager;

public class PPPoEDialer {

    private static final String TAG = "PPPoEDialer";

    private PppoeManager mPppoeManager = null;

    private Context mContext;

    private Handler mHandler;

    public static final int PPPOE_STATE_NONE = -1;

    public static final int PPPOE_STATE_CONNECT = 0;

    public static final int PPPOE_STATE_DISCONNECT = 1;

    public static final int PPPOE_STATE_CONNECTING = 2;

    public static final int PPPOE_STATE_AUTHFAILED = 3;

    public static final int PPPOE_STATE_FAILED = 4;

    public PPPoEDialer(Context context, Handler handler) {
        super();
        mContext = context;
        mHandler = handler;
        try {
            mPppoeManager = PppoeManager.getInstance(context);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        creteFile();
    }

    private void creteFile() {
        File file = new File("/data/misc/ppp/ipaddr");
        if (!file.exists()) {
            try {
                file.createNewFile();
                String command = "chmod 777 " + "/data/misc/ppp/ipaddr";
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PPPoEDialer(Context context) {
        super();
        mContext = context;
        mPppoeManager = PppoeManager.getInstance(context);
    }

    public void exit() {
        mContext.unregisterReceiver(mPppoeReceiver);
    }

    public void dial(String user, String passwd) {
        if (mPppoeManager == null) {
            try {
                mPppoeManager = PppoeManager.getInstance(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        if (PPPOE_STA.CONNECTING == mPppoeManager.PppoeGetStatus()) {
            Log.d(TAG, "CONNECTING.....");
            return;
        }

        // check username and password
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(passwd)) {
            return;
        } else {
            Log.d(TAG, "wire pppoe");
            mPppoeManager.PppoeSetInterface("eth0");
            mPppoeManager.PppoeSetUser(user);
            mPppoeManager.PppoeSetPW(passwd);
            mPppoeManager.PppoeDialup();
        }
    }

    public String getPppoeStatus() {
        if (mPppoeManager.getPppoeStatus().equals(mPppoeManager.PPPOE_STATE_CONNECT))
            return mContext.getResources().getString(R.string.pppoe_connected);
        else if (mPppoeManager.getPppoeStatus().equals(mPppoeManager.PPPOE_STATE_DISCONNECTING))
            return mContext.getResources().getString(R.string.pppoe_disconnected);
        else
            return mContext.getResources().getString(R.string.pppoe_failed);

    }

    public String getUser() {
        return mPppoeManager.PppoeGetUser();
    }

    public String getPasswd() {
        return mPppoeManager.PppoeGetPW();
    }

    public void setUser(String user) {
        mPppoeManager.PppoeSetUser(user);
    }

    public void setPasswd(String passwd) {
        mPppoeManager.PppoeSetPW(passwd);
    }

    public void hangup() {
        mPppoeManager.PppoeHangUp();
        Log.d(TAG, "ppppoe hang up");
    }

    private BroadcastReceiver mPppoeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action, " + action);
            if (!action.equals("com.mstar.android.pppoe.PPPOE_STATE_ACTION")) {
                return;
            }

            String status = intent.getStringExtra(PppoeManager.PPPOE_STATE_STATUE);
            Log.d(TAG, "#pppoestatus=" + status);
            if (null == status)
                return;

            Message message = new Message();
            message.what = PPPOE_STATE_NONE;
            if (status.equals(PppoeManager.PPPOE_STATE_CONNECT)) {
                Log.d(TAG, "@pppoe_connect");
                android.provider.Settings.System.putString(mContext.getContentResolver(), "PPPoE", "1");
                message.what = PPPOE_STATE_CONNECT;
            } else if (status.equals(PppoeManager.PPPOE_STATE_DISCONNECT)) {
                Log.d(TAG, "@pppoe_disconnect");
                android.provider.Settings.System.putString(mContext.getContentResolver(), "PPPoE", "0");
                message.what = PPPOE_STATE_DISCONNECT;
            } else if (status.equals(PppoeManager.PPPOE_STATE_CONNECTING)) {
                Log.d(TAG, "@pppoe_connecting");
                message.what = PPPOE_STATE_CONNECTING;
            } else if (status.equals(PppoeManager.PPPOE_STATE_AUTHFAILED)) {
                Log.d(TAG, "@pppoe_authfailed");
                message.what = PPPOE_STATE_AUTHFAILED;
            } else if (status.equals(PppoeManager.PPPOE_STATE_FAILED)) {
                Log.d(TAG, "@pppoe_failed");
                message.what = PPPOE_STATE_FAILED;
            }
            mHandler.sendMessage(message);
        }
    };

    public void registerPPPoEReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PppoeManager.PPPOE_STATE_ACTION);
        mContext.registerReceiver(mPppoeReceiver, intentFilter);
    }

    // Eostek Begin
    /**
     * @param netmask
     * @return the length of netmask
     */
    private int netmaskToPrefixLength(String netmask) {
        if (TextUtils.isEmpty(netmask)) {
            return -1;
        }

        String[] tmp = netmask.split("\\.");
        int cnt = 0;
        for (String cell : tmp) {
            int i = Integer.parseInt(cell);
            cnt += Integer.bitCount(i);
        }

        return cnt;
    }
    // Eostek End
    public boolean isConnected() {
        if(mPppoeManager.getPppoeStatus().equals(mPppoeManager.PPPOE_STATE_CONNECT))
            return true;
        else
            return false;
    }
}
