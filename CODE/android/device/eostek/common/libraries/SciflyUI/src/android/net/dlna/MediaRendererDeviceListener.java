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

public interface MediaRendererDeviceListener {
	/**
	 * set URI callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @param media
	 *            URI
	 * @param URI
	 *            of media metadata
	 * @return true:Accept the URL set锟斤拷false:Inform caller stop
	 * */
	public boolean OnSetAVTransportURI(int instance_id, String uri,
			ShareObject share_object);
	
	/**
	 * set URI of next AV transport
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @param media
	 *            URI
	 * @param URI
	 *            of media metadata
	 * @return true:Accept the URL set锟斤拷false:Inform caller stop
	 * */
	public boolean OnSetNextAVTransportURI(int instance_id, String uri,
			ShareObject share_object);

	/**
	 * get protocol information callback
	 * 
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnGetProtocolInfo();
	
	/**
	 * play callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @param speed
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnPlay(int instance_id, String speed);

	/**
	 * stop callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnStop(int instance_id);

	/**
	 * pause callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnPause(int instance_id);

	/**
	 * seek callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @param seek
	 *            mode
	 * @param seek
	 *            target
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnSeek(int instance_id, SeekMode mode, String target);

	/**
	 * next callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnNext(int instance_id);

	/**
	 * previous callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnPrevious(int instance_id);

	/**
	 * get media information callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnGetMediaInfo(int instance_id);

	/**
	 * get transport information callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnGetTransportInfo(int instance_id);

	/**
	 * Get position infomation callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnGetPositionInfo(int instance_id);

	/**
	 * Get device capabilities callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnGetDeviceCapabilities(int instance_id);

	/**
	 * get transport settings callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnGetTransportSettings(int instance_id);

	/**
	 * zoom in callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnZoomIn(int instance_id);

	/**
	 * zoom out callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnZoomOut(int instance_id);

	/**
	 * image Rotate Clockwise callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnRotateClockwise(int instance_id);

	/**
	 * image Rotate counterClockwise callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnRotateCounterClockwise(int instance_id);

	public boolean OnSelectPreset(int instance_id);

	/**
	 * get volume callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnGetVolume(int instance_id);

	/**
	 * set volume callback
	 * 
	 * @param instance
	 *            id锟斤拷default id:0
	 * @return true:succes锟斤拷false:Inform caller stop
	 * */
	public boolean OnSetVolume(int instance_id, VolumeInfo volume_info);

	/**
	 * The old version of mobile phone terminal:keep alive
	 * 
	 * @param instance_id
	 * @return true
	 **/
	public boolean OnKeepAlive(int instance_id);
	
		//[2014-7-14 neddy] 安装应用
	public boolean OnInstallApp(int instance_id, String uri);
	
	public boolean OnOpenApp(int instance_id, String pkg, String activity);
  	
  	public boolean OnDeleteApp(int instance_id, String pkg);
	
	//[2014-8-25 shiyl] 获取应用列表
	/**
	 * get AppInfo information callback
	 * 
	 * @return true:succes false:Inform caller stop
	 * */
	public boolean OnGetAppInfo(int instance_id);
	
	public boolean OnGetAppStatusInfo(int instance_id);
	
	//[2014-8-25 shiyl] 语音搜索列表
	public boolean OnVoiceSearchOnOff(int instance_id, int OnOff);
	
	public boolean OnVoiceSearchSendText(int instance_id,  String text);
	
	public boolean OnGetVoiceSearchState(int instance_id);
	
	//[shiyl 2014-09-03] 获取音量最大值
	public boolean OnGetMaxVolume(int instance_id);
}
