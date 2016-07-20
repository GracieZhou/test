//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
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

package com.android.settings.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

/**
 * ��������ʵ����.
 * 
 * @author wangchao
 * @since 1.0
 * @date 2011-11-18
 */
public class UpgradeTask implements Runnable {

    private static final String TAG = "upgrade";

    private static final String DOWNLOAD_ADDRESS = "url";

    private static final String VERSION = "version";

    private static final String NAME = "share_pres";

    private final Timer timer = new Timer();

    private static final int DOWNLOAD_ERROR = 3;
	/* 锟窖撅拷锟斤拷锟截的达拷小 */
	private long downloadedSize = 0;
	/* �ļ��ܴ�С */
	private long totalSize;
	/* �ٷֱ� */
	private int percent;
	// �����仯������
	private UpdateListener mDPListener;
	// ���°��ŵı��ص�ַ
	private String mLocalPath;
	// ���µ�URL�ĵ�ַ
	private String mUpgradeURL;
	// �汾��
	private String mVersion;

	private Context mContext;
	private Handler mHandler;
	private BufferedInputStream bis;
	
	private TimerTask timerTask;
	

	public UpgradeTask(Context context, String upgradeURL, String localPath, String version,
			UpdateListener dpListener, Handler handler) {
		this.mDPListener = dpListener;
		this.mLocalPath = localPath;
		this.mUpgradeURL = upgradeURL;
		this.mVersion = version;
		this.mContext = context;
		this.mHandler = handler;
    }

	/**
	 * �����İ汾�Ƿ���ȷ�������ȷ����Ҫ���жϵ���ֱ��ɾ��.
	 * 
	 * @return
	 */
	protected void prepare() {

		File file = new File(mLocalPath);

		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		String versionString = getStringData(VERSION);

		if (versionString.equals(mVersion)) {
			mUpgradeURL = getStringData(DOWNLOAD_ADDRESS);
		} else {
			System.out.println("delete..............");
			file.delete();
		}
	}

	/*
	 * @see com.jrm.core.container.cmps.upgrade.task.BaseUpgradeTask#onDownload()
	 */
	protected boolean download() {

		/* ��ȡ֮ǰ�����صĴ�С */
		File file = new File(mLocalPath);
		if (file.exists()) {
			downloadedSize = file.length();
		} else {
			downloadedSize = 0;
		}

		System.out.println("mUpgradeURL:" + mUpgradeURL);

		System.out.println("downloadedSize:" + downloadedSize);

		try {
			URL url = new URL(mUpgradeURL);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

			totalSize = httpConnection.getContentLength();

			System.out.println("totalSize:" + totalSize);

			if (downloadedSize == totalSize && this.mDPListener != null) {
				mDPListener.onDownloadSizeChange(100);
				return true;
			} else if (downloadedSize > totalSize) {
				if (!file.delete()) {
					return false;
				}
			}

			httpConnection.disconnect();

			/* �������ز��� */
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("Accept", "image/gif, " + "image/jpeg, " + "image/pjpeg, " + "image/pjpeg, "
							+ "application/x-shockwave-flash, " + "application/xaml+xml, "
							+ "application/vnd.ms-xpsdocument, " + "application/x-ms-xbap, "
							+ "application/x-ms-application, " + "application/vnd.ms-excel, "
							+ "application/vnd.ms-powerpoint, " + "application/msword, " + "*/*");
			httpConnection.setRequestProperty("Accept-Language", "zh-CN");
			httpConnection.setRequestProperty("Referer", mUpgradeURL);
			httpConnection.setRequestProperty("Charset", "UTF-8");
			httpConnection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");// ���û�ȡʵ����ݵķ�Χ

			httpConnection.setRequestProperty("Connection", "Keep-Alive");

			/* ׼������ */
			InputStream inStream = httpConnection.getInputStream();
			byte[] buffer = new byte[5120];

			/* �ļ��洢 */
			File saveFile = new File(mLocalPath);// task.getLocalUrl()
//			bis = new BufferedInputStream(inStream);  
			RandomAccessFile threadfile = new RandomAccessFile(saveFile, "rwd");
			threadfile.seek(downloadedSize);
			int offset = 0;
			int count = 0;
			int perUnit = (int) totalSize / 5120 / 100;

			try {
				while ((offset = inStream.read(buffer, 0, 5120)) != -1) {
					threadfile.write(buffer, 0, offset);

					count++;
					if (count == perUnit && downloadedSize < totalSize) {
						percent = (int) (downloadedSize * 100 / totalSize);

						if (this.mDPListener != null) {
							mDPListener.onDownloadSizeChange(percent);
						}

						count = 0;
					}else if(count==1){
					    percent = (int) (downloadedSize * 100 / totalSize);
					    
					    if (this.mDPListener != null) {
                            mDPListener.onDownloadSizeChange(percent);
                        }
					}
					if(timerTask==null){
					    timerTask=new TimerTask() {
					        
					        @Override
					        public void run() {
					            Log.i(TAG, "timer-----------"+percent);
					                Intent intent=new Intent("download_percent");
					                intent.putExtra("percent", percent);
					                mContext.sendBroadcast(intent);
					        }
					    };
					    timer.schedule(timerTask, 0, 1000);
					}
					downloadedSize += offset;
				}
			} finally {
				threadfile.close();
				inStream.close();
			}

			if (downloadedSize == totalSize && this.mDPListener != null) {
			    timer.cancel();
				mDPListener.onDownloadSizeChange(100);
			}

			Log.d(TAG, "download finished.");
			return true;
		} catch (MalformedURLException e) {
		    Intent intent=new Intent("download_exception");
            mContext.sendBroadcast(intent);
		    timer.cancel();
			e.printStackTrace();
			Log.e(TAG, "MSG1:" + e.getMessage());
			return false;
		} catch (IOException e) {
		    Intent intent=new Intent("download_exception");
            mContext.sendBroadcast(intent);
		    timer.cancel();
			e.printStackTrace();
			Log.e(TAG, "MSG2:" + e.getMessage());
			return false;
		} catch (Throwable th) {
		    Intent intent=new Intent("download_exception");
            mContext.sendBroadcast(intent);
		    timer.cancel();
			th.printStackTrace();
			Log.e(TAG, "MSG3:" + th.getMessage());
			return false;
		}
	}

	@Override
	public void run() {
		prepare();
		if (!download()) {
			System.out.println("download failed...");
			mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
		}
	};

	/**
	 * ��SharePreference�л�ȡ�־û������
	 * 
	 * @param key
	 * @return
	 */
	private String getStringData(String key) {
		SharedPreferences preference = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preference.getString(key, "");
	}
}
