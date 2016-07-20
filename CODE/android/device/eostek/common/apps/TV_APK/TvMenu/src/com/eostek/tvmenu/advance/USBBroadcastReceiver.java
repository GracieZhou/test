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


package com.eostek.tvmenu.advance;

import java.io.File;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StatFs;
import android.util.Log;

public class USBBroadcastReceiver extends BroadcastReceiver {

    private static int count = 0;
    private static HashMap<String, Integer> diskDesc = new HashMap<String, Integer>();
    private static final String TAG = "USBBroadcastReceiver";

    public static synchronized void addDiskDesc(String key, Integer value) {
        diskDesc.put(key, value);
    }

    public static synchronized void removeDiskDesc(String key) {
        diskDesc.remove(key);
    }

    public static String formatKey(String value) {
        if (value == null) {
            return null;
        }
        if (!value.startsWith("/")) {
            value = "/" + value;
        }
        if (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
            return formatKey(value);
        } else {
            return value;
        }
    }

    /**
     * @param source
     * @return
     */
    public static String splitHeadAndTail(String source) {
        if (source == null) {
            return null;
        }
        if (source.startsWith("/")) {
            source = source.substring(1);
        }
        if (source.endsWith("/")) {
            source = source.substring(0, source.length() - 1);
        }
        return source;
    }

    private static synchronized void initDiskDesc() {
        String parent = "/mnt/usb/";
        File pFile = new File(parent);
        File[] children = pFile.listFiles();
        if (children != null) {
            String str = null;
            for (File file : children) {
                str = file.getName();
                if (str != null) {
                    Log.e(TAG, "find disk " + str);
                    addDiskDesc(parent + str, 0);
                }
            }
        }
    }

    public USBBroadcastReceiver() {
        Log.d(TAG, "USBBroadcastReceiver constructor!!!");
        if (count == 0) {
            initDiskDesc();
            count++;
        }

    }

    /**
     * @return disk count
     */
    public static synchronized int getDiskCount() {
        if (diskDesc != null) {
            return diskDesc.size();
        } else {
            return 0;
        }
    }

    /**
     * return disk capacity percent by path
     *
     * @param key
     * @return
     */
    public static synchronized int getDiskCapacityPercent(String key) {
        Integer tmp = diskDesc.get(key);
        if (tmp == null) {
            return -1;
        } else {
            return tmp;
        }
    }

    public static synchronized boolean isDiskExisted(String path) {
        path = formatKey(path);
        if (diskDesc.containsKey(path)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_EJECT)) {
            Uri uri = intent.getData();
            final String path = uri.getPath();

            Log.w(TAG, "path:" + path);

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                Log.w(TAG, "ACTION_MEDIA_MOUNTED:" + path);
                addDiskDesc(path, 0);
                new Thread() {
                    public void run() {
                        try {
                            StatFs sf = new StatFs(path);
                            final int percent = (int) (100 - ((sf.getFreeBlocks() * 100)/ sf.getBlockCount()));
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Error : ", e);
                        }

                    };
                }.start();
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                Log.w(TAG, "ACTION_MEDIA_UNMOUNTED:" + path);
            } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {

                Log.w(TAG, "ACTION_MEDIA_EJECT:" + path);
                removeDiskDesc(path);

            }

        }

    }

}
