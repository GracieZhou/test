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

package android.net.dlna;

import java.util.ArrayList;

import android.net.dlna.ActionFailedException;
import android.net.dlna.ActionUnsupportedException;
import android.net.dlna.HostUnreachableException;

public interface MediaRendererController {
	/**
	 * get device information
	 * 
	 * @return DeviceInfo
	 * */
	public DeviceInfo GetDeviceInfo() throws ActionUnsupportedException,
			HostUnreachableException;

	/**
	 * get protocol information
	 * 
	 * @return ProtocolInfo
	 * */
	public ArrayList<ProtocolInfo> GetProtocolInfo()
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * get media information
	 * 
	 * @return MediaInfo
	 * */
	public MediaInfo GetMediaInfo(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * get device capabilities
	 * 
	 * @param instance
	 *            id��default id:0
	 * @return DeviceCapabilities
	 * */
	public DeviceCapabilities GetDeviceCapabilities(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * get transport information
	 * 
	 * @param instance
	 *            id��default id:0
	 * @return TransportInfo
	 * */
	public TransportInfo GetTransportInfo(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * get position
	 * 
	 * @param instance
	 *            id��default id:0
	 * @return PositionInfo
	 * */
	public PositionInfo GetPositionInfo(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * get transport parameter
	 * 
	 * @param instance
	 *            id��default id:0
	 * @return transport parameter
	 * */
	public TransportSettings GetTransportSettings(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * set transport parameter
	 * 
	 * @param instance
	 *            id��default id:0
	 * @param transport
	 *            parameter
	 * 
	 * */
	public void SetAVTransportURI(int instance_id, String uri, String metadata)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException;

	/**
	 * stop
	 * 
	 * @param instance
	 *            id��default id:0
	 * 
	 * */
	public void Stop(int instance_id) throws ActionUnsupportedException,
			HostUnreachableException;

	/**
	 * play
	 * 
	 * @param instance
	 *            id��default id:0
	 * 
	 * */
	public void Play(int instance_id, String speed)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * pause
	 * 
	 * @param instance
	 *            id��default id:0
	 * 
	 * */
	public void Pause(int instance_id) throws ActionUnsupportedException,
			HostUnreachableException;

	/**
	 * seek
	 * 
	 * @param instance
	 *            id��default id:0
	 * @param seek
	 *            mode
	 * @param seek
	 *            target
	 * 
	 * */
	public void Seek(int instance_id, SeekMode mode, String target)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * next
	 * 
	 * @param instance
	 *            id��default id:0
	 * 
	 * */
	public void Next(int instance_id) throws ActionUnsupportedException,
			HostUnreachableException;

	/**
	 * previous
	 * 
	 * @param instance_id
	 *            instance id��default id:0
	 * */
	public void Previous(int instance_id) throws ActionUnsupportedException,
			HostUnreachableException;

	/**
	 * get Volume
	 * 
	 * @param instance_id
	 *            instance id��default id:0
	 * */
	public int GetVolume(int instance_id, Channel channel)
			throws ActionUnsupportedException, HostUnreachableException;

	public void SetVolume(int instance_id, VolumeInfo info)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * zoom in
	 * 
	 * @param instance
	 *            id��default id:0
	 * */
	public void ZoomIn(int instance_id) throws ActionUnsupportedException,
			HostUnreachableException;

	/**
	 * zoom out
	 * 
	 * @param instance
	 *            id��default id:0
	 * */
	public void ZoomOut(int instance_id) throws ActionUnsupportedException,
			HostUnreachableException;

	/**
	 * Image rotation clockwise
	 * 
	 * @param instance
	 *            id��default id:0
	 * */
	public void RotateClockwise(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException;

	/**
	 * Image rotate counterclockwise
	 * 
	 * @param instance
	 *            id��default id:0
	 * */
	public void RotateCounterClockwise(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException;

	// [2014-7-14 neddy] 
	public int InstallApp(int instance_id, String url)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException;
	
	/**
	 * open app
	 * 
	 * @param instance_id
	 * @param pkg  package name
	 * @param activity activity name for start app
	 * @return
	 * @throws ActionUnsupportedException
	 * @throws HostUnreachableException
	 * @throws ActionFailedException
	 */
	public int openApp(int instance_id, String pkg, String activity)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException;
	/**
	 *
	 * @param instance_id
	 * @param pkg
	 * @return
	 * @throws ActionUnsupportedException
	 * @throws HostUnreachableException
	 * @throws ActionFailedException
	 */
	public int deleteApp(int instance_id, String pkg)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException;
	
	//[2014-8-25 shiyl] 获取app列表
	public ArrayList<AppInfo> GetAppInfo(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException ;
	
	//[2014-8-28 shiyl] 获取app状态列表
	public ArrayList<AppStatusInfo> GetAppStatusInfo(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException;

	//[2014-8-27 shiyl] 语音搜索接口
	public  int VoiceSearchOnOff(int instance_id, int OnOff/*0 or 1*/)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException;
			
	//[2014-8-27 shiyl] 语音搜索文字发送
	public int VoiceSearchSendText(int instance_id, String text)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException;
			
	//[2014-8-27 shiyl] 语音搜索状态获取
	public VoiceSearchState GetVoiceSearchState(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException;
			
	//[shiyl 2014-09-03] 获取最大音量值
	public int GetMaxVolume(int instance_id, Channel channel)
		throws ActionUnsupportedException, HostUnreachableException;
}
