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

class MediaRendererDeviceImpl implements MediaRendererDevice {
	public void SetListener(MediaRendererDeviceListener listener) {
		JNI_DMR_SetListener(listener);
	}

	public void SetProtocolInfo(ArrayList<ProtocolInfo> protocol_info) {
		JNI_DMR_SetProtocolInfo(protocol_info);
	}

	public void SetMediaInfo(int instance_id, MediaInfo media_info) {
		JNI_DMR_SetMediaInfo(instance_id, media_info);
	}

	public void SetTransportInfo(int instance_id, TransportInfo transport_info) {
		JNI_DMR_SetTransportInfo(instance_id, transport_info);
	}

	public void SetPositionInfo(int instance_id, PositionInfo position_info) {
		JNI_DMR_SetPositionInfo(instance_id, position_info);
	}

	public void SetDeviceCapabilities(int instance_id,
			DeviceCapabilities device_capabilities) {
		JNI_DMR_SetDeviceCapabilities(instance_id, device_capabilities);
	}

	public void SetTransportSettings(int instance_id,
			TransportSettings transport_settings) {
		JNI_DMR_SetTransportSettings(instance_id, transport_settings);
	}

	public void SetVolume(int instance_id, VolumeInfo volume_info) {
		JNI_DMR_SetVolume(instance_id, volume_info);
	}

	public DeviceInfo GetDeviceInfo() {
		return JNI_DMR_GetDeviceInfo();
	}

	public void SetDeviceInfo(DeviceInfo device_info) {
		JNI_DMR_SetDeviceInfo(device_info);
	}
	//[2014-8-25 shiyl] 设置app列表
	public void SetAppInfo(int instance_id, ArrayList<AppInfo> app_info) {
		JNI_DMR_SetAppInfo(instance_id, app_info);
	}
	
	//[2014-8-25 shiyl] 设置app列表
	public void SetAppStatusInfo(int instance_id, ArrayList<AppStatusInfo> status_info) {
		JNI_DMR_SetAppStatusInfo(instance_id, status_info);
	}
	
	//[2014-8-27 shiyl] 设置app列表
	public void SetVoiceSearchState(int instance_id, VoiceSearchState state) {
		JNI_DMR_SetVoiceSearchState(instance_id, state);
	}
	
	//[shiyl 2014-09-03] 获取最大音量值
	public void SetMaxVolume(int instance_id, VolumeInfo volume_info) {
		JNI_DMR_SetMaxVolume(instance_id, volume_info);
	}
	
	private native synchronized void JNI_DMR_SetListener(
			MediaRendererDeviceListener listener);

	private native synchronized void JNI_DMR_SetProtocolInfo(
			ArrayList<ProtocolInfo> protocol_info);

	private native synchronized void JNI_DMR_SetMediaInfo(int instance_id,
			MediaInfo media_info);

	private native synchronized void JNI_DMR_SetTransportInfo(int instance_id,
			TransportInfo transport_info);

	private native synchronized void JNI_DMR_SetTransportSettings(
			int instance_id, TransportSettings transport_settings);

	private native synchronized void JNI_DMR_SetPositionInfo(int instance_id,
			PositionInfo position_info);

	private native synchronized void JNI_DMR_SetDeviceCapabilities(
			int instance_id, DeviceCapabilities device_capabilities);

	private native synchronized void JNI_DMR_SetVolume(int instance_id,
			VolumeInfo volume_info);

	private native synchronized DeviceInfo JNI_DMR_GetDeviceInfo();

	private native synchronized void JNI_DMR_SetDeviceInfo(
			DeviceInfo device_info);
	//[2014-8-25 shiyl] 设置app列表
	private native synchronized void JNI_DMR_SetAppInfo(int instance_id,
			ArrayList<AppInfo> app_info);
			
	private native synchronized void JNI_DMR_SetAppStatusInfo(int instance_id,
			ArrayList<AppStatusInfo> status_info);
			
	private native synchronized void JNI_DMR_SetVoiceSearchState(int instance_id,
			VoiceSearchState state);
	
	//[shiyl 2014-09-03] 获取最大音量值
	private native synchronized void JNI_DMR_SetMaxVolume(int instance_id,
			VolumeInfo volume_info);
}

class MediaServerDeviceImpl implements MediaServerDevice {
	public void SetListener(MediaServerDeviceListener listener) {
		JNI_DMS_SetListener(listener);
	}

	public void SetProtocolInfo(ArrayList<ProtocolInfo> protocol_info) {
		JNI_DMS_SetProtocolInfo(protocol_info);
	}

	public void AddMediaFile(String path, MediaMetaData metadata) {
		JNI_DMS_AddMediaFile(path, metadata);
	}

	public void RemoveMediaFile(String path) {
		JNI_DMS_RemoveMediaFile(path);
	}

	public ShareItem Browse(String path) {
		return JNI_DMS_Browse(path);
	}

	public int EnableUpload() {
		return DMSEnableUpload();
	}

	public int DisableUpload() {
		return DMSDisableUpload();
	}

	public int SetUploadItemSaveDir(String dir_path) {
		return DMSSetUploadItemSaveDir(dir_path);
	}

	public DeviceInfo GetDeviceInfo() {
		return JNI_DMS_GetDeviceInfo();
	}

	public void SetDeviceInfo(DeviceInfo device_info) {
		JNI_DMS_SetDeviceInfo(device_info);
	}

	public void SetPassword(String pw) {
		JNI_DMS_SetPassword(pw);
	}

	private native synchronized void JNI_DMS_SetListener(
			MediaServerDeviceListener listener);

	private native synchronized void JNI_DMS_SetProtocolInfo(
			ArrayList<ProtocolInfo> protocol_info);

	private native synchronized void JNI_DMS_AddMediaFile(String path,
			MediaMetaData metadata);

	private native synchronized void JNI_DMS_RemoveMediaFile(String path);

	private native synchronized ShareItem JNI_DMS_Browse(String path);

	private native synchronized int DMSEnableUpload();

	private native synchronized int DMSDisableUpload();

	private native synchronized int DMSSetUploadItemSaveDir(String dir_path);

	private native synchronized DeviceInfo JNI_DMS_GetDeviceInfo();

	private native synchronized void JNI_DMS_SetDeviceInfo(
			DeviceInfo device_info);

	private native synchronized void JNI_DMS_SetPassword(String pw);
}

class MediaServerControllerImpl implements MediaServerController {
	public ArrayList<ProtocolInfo> GetProtocolInfo() {
		return JNI_DMSC_GetProtocolInfo();
	}

	public ArrayList<ShareObject> Browse(String id, String filter,
			int start_id, int requested_count, String sort_criteria) {
		return JNI_DMSC_Browse(id, filter, start_id, requested_count,
				sort_criteria);
	}

	public BrowseResult Browse_Ex(String id, BrowseFlag flag, String filter,
			int start_id, int requested_count, String sort_criteria)
			throws ActionUnsupportedException, HostUnreachableException,
			MissingAuthenticationException {
		return JNI_DMSC_Browse_Ex(id, flag, filter, start_id, requested_count,
				sort_criteria);
	}

	public ShareItem CreateObject(String container_id, String title) {
		return DMSCCreateObject(container_id, title);
	}

	public int TransferFile(String export_uri, String import_uri) {
		return DMSCTransferFile(export_uri, import_uri);
	}

	public DeviceInfo GetDeviceInfo() {
		return JNI_DMSC_GetDeviceInfo();
	}

	public void Authenticate(String pw) {
		JNI_DMSC_Authenticate(pw);
	}

	private native synchronized ArrayList<ProtocolInfo> JNI_DMSC_GetProtocolInfo();

	private native synchronized ArrayList<ShareObject> JNI_DMSC_Browse(
			String id, String filter, int start_id, int requested_count,
			String sort_criteria);

	private native synchronized BrowseResult JNI_DMSC_Browse_Ex(String id,
			BrowseFlag flag, String filter, int start_id, int requested_count,
			String sort_criteria);

	private native synchronized ShareItem DMSCCreateObject(String container_id,
			String title);

	private native synchronized int DMSCTransferFile(String export_uri,
			String import_uri);

	private native synchronized DeviceInfo JNI_DMSC_GetDeviceInfo();

	private native synchronized void JNI_DMSC_Authenticate(String pw);

	private int handle;
}

class MediaRendererControllerImpl implements MediaRendererController {
	public MediaRendererControllerImpl() {
		handle = 0;
	}

	public DeviceInfo GetDeviceInfo() {
		return JNI_DMRC_GetDeviceInfo();
	}

	public ArrayList<ProtocolInfo> GetProtocolInfo() {
		return JNI_DMRC_GetProtocolInfo();
	}

	public MediaInfo GetMediaInfo(int instance_id) {
		return JNI_DMRC_GetMediaInfo(instance_id);
	}

	public DeviceCapabilities GetDeviceCapabilities(int instance_id) {
		return JNI_DMRC_GetDeviceCapabilities(instance_id);
	}

	public TransportInfo GetTransportInfo(int instance_id) {
		return JNI_DMRC_GetTransportInfo(instance_id);
	}

	public PositionInfo GetPositionInfo(int instance_id) {
		return JNI_DMRC_GetPositionInfo(instance_id);
	}

	public TransportSettings GetTransportSettings(int instance_id) {
		return JNI_DMRC_GetTransportSettings(instance_id);
	}

	public void SetAVTransportURI(int instance_id, String uri, String metadata) {
		JNI_DMRC_SetAVTransportURI(instance_id, uri, metadata);
	}

	public void Stop(int instance_id) {
		JNI_DMRC_Stop(instance_id);
	}

	public void Play(int instance_id, String speed) {
		JNI_DMRC_Play(instance_id, speed);
	}

	public void Pause(int instance_id) {
		JNI_DMRC_Pause(instance_id);
	}

	public void Seek(int instance_id, SeekMode mode, String target) {
		JNI_DMRC_Seek(instance_id, mode, target);
	}

	public void Next(int instance_id) {
		JNI_DMRC_Next(instance_id);
	}

	public void Previous(int instance_id) {
		JNI_DMRC_Previous(instance_id);
	}

	public void ZoomIn(int instance_id) {
		JNI_DMRC_ZoomIn(instance_id);
	}

	public void ZoomOut(int instance_id) {
		JNI_DMRC_ZoomOut(instance_id);
	}

	public void RotateClockwise(int instance_id) {
		JNI_DMRC_RotateClockwise(instance_id);
	}

	public void RotateCounterClockwise(int instance_id) {
		JNI_DMRC_RotateCounterClockwise(instance_id);
	}

	public int GetVolume(int instance_id, Channel channel) {
		return JNI_DMRC_GetVolume(instance_id, channel);
	}

	public void SetVolume(int instance_id, VolumeInfo info) {
		JNI_DMRC_SetVolume(instance_id, info);
	}
	
	//[2014-7-14 neddy] 安装应用
	public int InstallApp(int instance_id, String url) {
		return JNI_DMRC_InstallApp(instance_id, url);
	}
	
	public int openApp(int instance_id, String pkg, String activity)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException {
		return JNI_DMRC_OpenApp(instance_id, pkg, activity);
	}
	
	public int deleteApp(int instance_id, String pkg)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException {
		return JNI_DMRC_DeleteApp(instance_id, pkg);
	}
	//[2014-8-25 shiyl] 获取app列表
	public ArrayList<AppInfo> GetAppInfo(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException {
		return JNI_DMRC_GetAppInfo(instance_id);
	}
	
	//[2014-8-28 shiyl] 获取app状态列表
	public ArrayList<AppStatusInfo> GetAppStatusInfo(int instance_id)
			throws ActionUnsupportedException, HostUnreachableException,
			ActionFailedException {
		return JNI_DMRC_GetAppStatusInfo(instance_id);
	}

	//[2014-8-27 shiyl] 语音搜索接口
	public  int VoiceSearchOnOff(int instance_id, int OnOff/*0 or 1*/){
		return JNI_DMRC_VoiceSearchOnOff(instance_id, OnOff);
	}
	
	public int VoiceSearchSendText(int instance_id, String text){
		return JNI_DMRC_VoiceSearchSendText(instance_id, text);
	}
	
	public VoiceSearchState GetVoiceSearchState(int instance_id){
		return JNI_DMRC_GetVoiceSearchState(instance_id);
	}
	
	//[shiyl 2014-09-03] 获取最大音量值
	public int GetMaxVolume(int instance_id, Channel channel) {
		return JNI_DMRC_GetMaxVolume(instance_id, channel);
	}
	
	private native synchronized DeviceInfo JNI_DMRC_GetDeviceInfo();

	private native synchronized ArrayList<ProtocolInfo> JNI_DMRC_GetProtocolInfo();

	private native synchronized MediaInfo JNI_DMRC_GetMediaInfo(int instance_id);

	private native synchronized DeviceCapabilities JNI_DMRC_GetDeviceCapabilities(
			int instance_id);

	private native synchronized TransportInfo JNI_DMRC_GetTransportInfo(
			int instance_id);

	private native synchronized PositionInfo JNI_DMRC_GetPositionInfo(
			int instance_id);

	private native synchronized TransportSettings JNI_DMRC_GetTransportSettings(
			int instance_id);

	private native synchronized void JNI_DMRC_SetAVTransportURI(
			int instance_id, String uri, String metadata);

	private native synchronized void JNI_DMRC_Stop(int instance_id);

	private native synchronized void JNI_DMRC_Play(int instance_id, String speed);

	private native synchronized void JNI_DMRC_Pause(int instance_id);

	private native synchronized void JNI_DMRC_Seek(int instance_id,
			SeekMode mode, String target);

	private native synchronized void JNI_DMRC_Next(int instance_id);

	private native synchronized void JNI_DMRC_Previous(int instance_id);

	private native synchronized int JNI_DMRC_GetVolume(int instance_id,
			Channel channel);

	private native synchronized void JNI_DMRC_SetVolume(int instance_id,
			VolumeInfo info);

	private native synchronized void JNI_DMRC_ZoomIn(int instance_id);

	private native synchronized void JNI_DMRC_ZoomOut(int instance_id);

	private native synchronized void JNI_DMRC_RotateClockwise(int instance_id);

	private native synchronized void JNI_DMRC_RotateCounterClockwise(
			int instance_id);
	
	private native synchronized int JNI_DMRC_InstallApp(int instance_id, String url);

	private native synchronized int JNI_DMRC_OpenApp(int instance_id,
			String pkg, String activity);
	
	private native synchronized int JNI_DMRC_DeleteApp(int instance_id,
			String pkg);
	
	//[2014-8-25 shiyl] 获取app列表
	private native synchronized ArrayList<AppInfo> JNI_DMRC_GetAppInfo(int instance_id);
	
	//[2014-8-29 shiyl] 获取app状态列表
	private native synchronized ArrayList<AppStatusInfo> JNI_DMRC_GetAppStatusInfo(int instance_id);

	//[2014-8-25 shiyl] 语音搜索接口
	private native synchronized int JNI_DMRC_VoiceSearchOnOff(int instance_id, int OnOff);
	
	private native synchronized int JNI_DMRC_VoiceSearchSendText(int instance_id, String text);
	
	private native synchronized VoiceSearchState JNI_DMRC_GetVoiceSearchState(int instance_id);
	
	//[shiyl 2014-09-03] 获取最大音量值
	private native synchronized int JNI_DMRC_GetMaxVolume(int instance_id, Channel channel);
	
	private int handle;
}

class DLNAImpl implements DLNA {
	public boolean Initialize(String ip, int port) {
		return JNI_DLNA_Initialize(ip, port);
	}

	public void SetDescriptionFile(String xml) {
		JNI_DLNA_SetDescriptionFile(xml);
	}

	public void Finalize() {
		JNI_DLNA_Finalize();
	}

	public MediaRendererDevice CreateMediaRendererDevice() {
		return new MediaRendererDeviceImpl();
	}

	public MediaServerDevice CreateMediaServerDevice() {
		return new MediaServerDeviceImpl();
	}

	public void SetDeviceListener(DeviceListener listener) {
		JNI_DLNA_SetDeviceListener(listener);
	}

	public void AsyncSearchDevice() {
		JNI_DLNA_AsyncSearchDevice();
	}
	
	/**
     * Async find device by ip
     */
    public int AsyncFindDeviceByIP(String ip){
        return JNI_DLNA_AsyncFindDeviceByIP(ip);
    }
	
	public ArrayList<MediaRendererController> GetMediaRendererControllerList() {
		ArrayList<MediaRendererController> l = new ArrayList<MediaRendererController>();
		return l;
	}

	public ArrayList<MediaServerController> GetMediaServerControllerList() {
		ArrayList<MediaServerController> l = new ArrayList<MediaServerController>();
		return l;
	}

	public String GetVersion() {
		return JNI_DLNA_GetVersion();
	}

	public int GetPort() {
		return JNI_DLNA_GetPort();
	}

	public String GetIP() {
		return JNI_DLNA_GetIP();
	}

	static {
		System.loadLibrary("dlnajni");
	}

	private native String JNI_DLNA_GetVersion();

	private native synchronized boolean JNI_DLNA_Initialize(String ip, int port);

	private native synchronized void JNI_DLNA_Finalize();

	private native synchronized void JNI_DLNA_SetDescriptionFile(String xml);

	private native synchronized int JNI_DLNA_GetPort();

	private native synchronized String JNI_DLNA_GetIP();

	private native synchronized void JNI_DLNA_AsyncSearchDevice();

	private native synchronized void JNI_DLNA_SetDeviceListener(DeviceListener listener);
	
	private native synchronized int JNI_DLNA_AsyncFindDeviceByIP(String ip);
}
