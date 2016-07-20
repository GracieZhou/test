DROP TABLE IF EXISTS "SQLITEADMIN_QUERIES";
CREATE TABLE SQLITEADMIN_QUERIES(ID INTEGER PRIMARY KEY,NAME VARCHAR(100),SQL TEXT);
INSERT INTO "SQLITEADMIN_QUERIES" VALUES(1,'insert','insert into tbl_UserPCModeSetting (_id,u16HorizontalStart,u16VerticalStart,u16HorizontalTotal,u8ModeIndex,u16Phase,u8AutoSign,u8Order,u16UI_HorizontalStart,u16UI_VorizontalStart)
values(9,0,0,0,0,0,0,0,0,0);');
INSERT INTO "SQLITEADMIN_QUERIES" VALUES(2,'update','update MS_USER_COLORTEMP_EX set _Name="SVIDEO" where InputSrcType=3;');
DROP TABLE IF EXISTS "tbl_3DInfo";
CREATE TABLE [tbl_3DInfo] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[bEnable3D] INTEGER NULL,
[enInput3DMode] INTEGER NULL,
[enOutput3DMode] INTEGER NULL
);
INSERT INTO "tbl_3DInfo" VALUES(0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DSetting";
CREATE TABLE [tbl_3DSetting] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[enDisplayMode] INTEGER  NULL,
[en3DFormat] INTEGER  NULL,
[en2DFormat] INTEGER  NULL,
[enAutoStart] INTEGER  NULL,
[en3DTimerPeriod] INTEGER  NULL
);
INSERT INTO "tbl_3DSetting" VALUES(0,0,0,0,2,0);
DROP TABLE IF EXISTS "tbl_BlockSysSetting";
CREATE TABLE [tbl_BlockSysSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u8BlockSysLockMode] INTEGER NULL,
[u8UnratedLoack] INTEGER NULL,
[u8VideoBlockMode] INTEGER NULL,
[u8BlockSysPWSetStatus] INTEGER NULL,
[u8ParentalControl] INTEGER NULL,
[u8ParentalObjectiveContent] INTEGER NULL,
[u8EnterLockPage] INTEGER NULL,
[u16BlockSysPassword] INTEGER NULL
);
INSERT INTO "tbl_BlockSysSetting" VALUES(0,0,0,0,0,0,0,0,0);
DROP TABLE IF EXISTS "tbl_CECSetting";
CREATE TABLE [tbl_CECSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u8CECStatus] INTEGER NULL,
[u8AutoStandby] INTEGER NULL,
[u8ARCStatus] INTEGER NULL,
[u8AudioModeStatus] INTEGER NULL
);
INSERT INTO "tbl_CECSetting" VALUES(0,1,0,0,0);
DROP TABLE IF EXISTS "tbl_CISetting";
CREATE TABLE [tbl_CISetting] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[u8AKH] TEXT  NULL,
[u8DHSKTail] TEXT  NULL,
[u8CamID] TEXT  NULL,
[u16CamBrandID] INTEGER  NULL,
[u8ScramblerMode] INTEGER  NULL,
[u8SystemMJDUTC] TEXT  NULL
);
INSERT INTO "tbl_CISetting" VALUES(0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,',0,0,'0x0,0x0,0x0,0x0,0x0,');
INSERT INTO "tbl_CISetting" VALUES(1,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,',0,0,'0x0,0x0,0x0,0x0,0x0,');
INSERT INTO "tbl_CISetting" VALUES(2,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,',0,0,'0x0,0x0,0x0,0x0,0x0,');
INSERT INTO "tbl_CISetting" VALUES(3,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,',0,0,'0x0,0x0,0x0,0x0,0x0,');
INSERT INTO "tbl_CISetting" VALUES(4,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,','0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,',0,0,'0x0,0x0,0x0,0x0,0x0,');
DROP TABLE IF EXISTS "tbl_ChinaDVBCSetting";
CREATE TABLE [tbl_ChinaDVBCSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[eDVBCRegion] INTEGER  NULL,
[u32NITFreq] INTEGER  NULL,
[u32NITFreq2] INTEGER  NULL,
[u8LocalNITVersion] INTEGER  NULL
);
INSERT INTO "tbl_ChinaDVBCSetting" VALUES(0,40,474000,371000,0);
DROP TABLE IF EXISTS "tbl_DB_VERSION";
CREATE TABLE [tbl_DB_VERSION] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u8Version] INTEGER NULL,
[u32SystemDBSize] INTEGER NULL
);
INSERT INTO "tbl_DB_VERSION" VALUES(0,17,0);
DROP TABLE IF EXISTS "tbl_DvbtPresetting";
CREATE TABLE [tbl_DvbtPresetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u32Frequency] INTEGER NULL,
[enBandWidth] INTEGER NULL
);
INSERT INTO "tbl_DvbtPresetting" VALUES(0,0,0);
DROP TABLE IF EXISTS "tbl_EpgTimer";
CREATE TABLE [tbl_EpgTimer] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[enTimerType] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[enRepeatMode] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[u32StartTime] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[u32DurationTime] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[u8ServiceType] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[u16ServiceNumber] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[u16EventID] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[u16MajorNumber] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[u16MinorNumber] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[bIsEndTimeBeforeStart] INTEGER DEFAULT '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''0''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''' NULL,
[sServiceName] TEXT  NULL,
[sEventName] TEXT  NULL,
[bPinCodeCached] INTEGER  NULL,
[u16CachedPinCode] INTEGER  NULL,
[u32TimerId] INTEGER  NULL,
[u16Onid] INTEGER  NULL,
[u16Tsid] INTEGER  NULL,
[u16Sid] INTEGER  NULL,
[u8DtvRoute] INTEGER  NULL
);
INSERT INTO "tbl_EpgTimer" VALUES(0,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(1,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(2,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(3,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(4,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(5,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(6,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(7,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(8,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(9,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(10,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(11,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(12,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(13,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(14,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(15,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(16,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(17,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(18,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(19,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(20,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(21,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(22,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(23,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
INSERT INTO "tbl_EpgTimer" VALUES(24,0,0,0,0,0,0,0,0,0,0,NULL,NULL,0,0,0,0,0,0,0);
DROP TABLE IF EXISTS "tbl_FavTypeName";
CREATE TABLE [tbl_FavTypeName] (
[TypeId] INTEGER  NOT NULL PRIMARY KEY,
[TypeName] TEXT  NULL
);
INSERT INTO "tbl_FavTypeName" VALUES(0,'Fav1');
INSERT INTO "tbl_FavTypeName" VALUES(1,'Fav2');
INSERT INTO "tbl_FavTypeName" VALUES(2,'Fav3');
INSERT INTO "tbl_FavTypeName" VALUES(3,'Fav4');
INSERT INTO "tbl_FavTypeName" VALUES(4,'Fav5');
INSERT INTO "tbl_FavTypeName" VALUES(5,'Fav6');
INSERT INTO "tbl_FavTypeName" VALUES(6,'Fav7');
INSERT INTO "tbl_FavTypeName" VALUES(7,'Fav8');
DROP TABLE IF EXISTS "tbl_InputSource_Type";
CREATE TABLE [tbl_InputSource_Type] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[_Name] TEXT  NULL,
[_Value] INTEGER  NULL,
[_Remark] TEXT  NULL
);
INSERT INTO "tbl_InputSource_Type" VALUES(0,'MAPI_INPUT_SOURCE_VGA',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(1,'MAPI_INPUT_SOURCE_ATV',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(2,'MAPI_INPUT_SOURCE_CVBS',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(3,'MAPI_INPUT_SOURCE_CVBS2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(4,'MAPI_INPUT_SOURCE_CVBS3',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(5,'MAPI_INPUT_SOURCE_CVBS4',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(6,'MAPI_INPUT_SOURCE_CVBS5',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(7,'MAPI_INPUT_SOURCE_CVBS6',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(8,'MAPI_INPUT_SOURCE_CVBS7',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(9,'MAPI_INPUT_SOURCE_CVBS8',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(10,'MAPI_INPUT_SOURCE_CVBS_MAX',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(11,'MAPI_INPUT_SOURCE_SVIDEO',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(12,'MAPI_INPUT_SOURCE_SVIDEO2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(13,'MAPI_INPUT_SOURCE_SVIDEO3',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(14,'MAPI_INPUT_SOURCE_SVIDEO4',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(15,'MAPI_INPUT_SOURCE_SVIDEO_MAX',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(16,'MAPI_INPUT_SOURCE_YPBPR',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(17,'MAPI_INPUT_SOURCE_YPBPR2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(18,'MAPI_INPUT_SOURCE_YPBPR3',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(19,'MAPI_INPUT_SOURCE_YPBPR_MAX',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(20,'MAPI_INPUT_SOURCE_SCART',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(21,'MAPI_INPUT_SOURCE_SCART2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(22,'MAPI_INPUT_SOURCE_SCART_MAX',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(23,'MAPI_INPUT_SOURCE_HDMI',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(24,'MAPI_INPUT_SOURCE_HDMI2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(25,'MAPI_INPUT_SOURCE_HDMI3',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(26,'MAPI_INPUT_SOURCE_HDMI4',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(27,'MAPI_INPUT_SOURCE_HDMI_MAX',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(28,'MAPI_INPUT_SOURCE_DTV',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(29,'MAPI_INPUT_SOURCE_DVI',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(30,'MAPI_INPUT_SOURCE_DVI2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(31,'MAPI_INPUT_SOURCE_DVI3',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(32,'MAPI_INPUT_SOURCE_DVI4',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(33,'MAPI_INPUT_SOURCE_DVI_MAX',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(34,'MAPI_INPUT_SOURCE_STORAGE',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(35,'MAPI_INPUT_SOURCE_KTV',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(36,'MAPI_INPUT_SOURCE_JPEG',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(37,'MAPI_INPUT_SOURCE_DTV2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(38,'MAPI_INPUT_SOURCE_STORAGE2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(39,'MAPI_INPUT_SOURCE_DTV3',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(40,'MAPI_INPUT_SOURCE_SCALER_OP',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(41,'MAPI_INPUT_SOURCE_RVU',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(42,'MAPI_INPUT_SOURCE_VGA2',0,'');
INSERT INTO "tbl_InputSource_Type" VALUES(43,'MAPI_INPUT_SOURCE_VGA3',0,'');
DROP TABLE IF EXISTS "tbl_IsdbSysSetting";
CREATE TABLE [tbl_IsdbSysSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[enAirChannelType] INTEGER NULL
);
INSERT INTO "tbl_IsdbSysSetting" VALUES(0,0);
DROP TABLE IF EXISTS "tbl_IsdbUserSetting";
CREATE TABLE [tbl_IsdbUserSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[b_GingaMode] INTEGER NULL,
[u8ATVCCMode] INTEGER NULL,
[u8DTVCCMode] INTEGER NULL,
[enScanOpt] INTEGER NULL
);
INSERT INTO "tbl_IsdbUserSetting" VALUES(0,1,0,0,1);
DROP TABLE IF EXISTS "tbl_MediumSetting";
CREATE TABLE [tbl_MediumSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[AntennaType] INTEGER NULL,
[CableSystem] INTEGER NULL,
[fAntennaPower] INTEGER NULL
);
INSERT INTO "tbl_MediumSetting" VALUES(0,1,0,0);
DROP TABLE IF EXISTS "tbl_MfcMode";
CREATE TABLE [tbl_MfcMode] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[eMFC] INTEGER NULL
);
INSERT INTO "tbl_MfcMode" VALUES(0,2);
DROP TABLE IF EXISTS "tbl_NRMode";
CREATE TABLE [tbl_NRMode] (
[NRMode] INTEGER  NULL,
[InputSrcType] INTEGER  NULL,
[eNR] INTEGER DEFAULT '''0''' NOT NULL,
[eMPEG_NR] INTEGER DEFAULT '''0''' NOT NULL,
PRIMARY KEY ([NRMode],[InputSrcType])
);
INSERT INTO "tbl_NRMode" VALUES(0,0,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,0,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,0,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,0,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,1,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,1,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,1,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,1,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,2,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,2,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,2,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,2,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,3,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,3,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,3,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,3,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,4,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,4,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,4,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,4,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,5,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,5,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,5,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,5,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,6,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,6,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,6,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,6,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,7,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,7,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,7,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,7,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,8,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,8,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,8,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,8,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,9,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,9,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,9,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,9,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,10,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,10,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,10,0,0);
INSERT INTO "tbl_NRMode" VALUES(2,10,2,1);
INSERT INTO "tbl_NRMode" VALUES(0,11,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,11,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,11,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,11,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,12,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,12,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,12,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,12,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,13,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,13,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,13,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,13,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,14,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,14,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,14,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,14,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,15,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,15,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,15,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,15,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,16,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,16,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,16,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,16,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,17,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,17,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,17,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,17,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,18,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,18,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,18,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,18,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,19,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,19,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,19,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,19,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,20,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,20,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,20,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,20,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,21,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,21,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,21,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,21,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,22,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,22,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,22,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,22,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,23,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,23,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,23,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,23,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,24,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,24,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,24,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,24,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,25,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,25,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,25,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,25,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,26,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,26,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,26,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,26,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,27,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,27,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,27,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,27,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,28,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,28,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,28,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,28,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,29,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,29,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,29,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,29,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,30,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,30,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,30,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,30,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,31,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,31,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,31,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,31,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,32,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,32,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,32,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,32,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,33,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,33,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,33,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,33,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,34,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,34,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,34,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,34,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,35,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,35,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,35,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,35,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,36,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,36,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,36,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,36,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,37,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,37,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,37,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,37,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,38,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,38,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,38,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,38,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,39,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,39,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,39,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,39,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,40,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,40,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,40,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,40,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,41,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,41,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,41,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,41,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,42,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,42,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,42,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,42,0,0);
INSERT INTO "tbl_NRMode" VALUES(0,43,2,1);
INSERT INTO "tbl_NRMode" VALUES(1,43,2,1);
INSERT INTO "tbl_NRMode" VALUES(2,43,2,1);
INSERT INTO "tbl_NRMode" VALUES(3,43,0,0);
DROP TABLE IF EXISTS "tbl_NitInfo";
CREATE TABLE [tbl_NitInfo] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u16ONID] INTEGER DEFAULT '0' NOT NULL,
[u16NetworkID] INTEGER DEFAULT '0' NOT NULL,
[u8TSNumber] INTEGER DEFAULT '0' NOT NULL
);
INSERT INTO "tbl_NitInfo" VALUES(0,65535,0,0);
INSERT INTO "tbl_NitInfo" VALUES(1,65535,0,0);
INSERT INTO "tbl_NitInfo" VALUES(2,65535,0,0);
INSERT INTO "tbl_NitInfo" VALUES(3,65535,0,0);
INSERT INTO "tbl_NitInfo" VALUES(4,65535,0,0);
DROP TABLE IF EXISTS "tbl_Nit_TSInfo";
CREATE TABLE [tbl_Nit_TSInfo] (
[_id] INTEGER  NOT NULL,
[_NIT_id] INTEGER  NOT NULL,
[wTransportStream_ID] INTEGER  NOT NULL,
[u32CentreFrequency] INTEGER  NOT NULL,
[au32FrequencyList] TEXT  NULL,
PRIMARY KEY ([_id],[_NIT_id])
);
INSERT INTO "tbl_Nit_TSInfo" VALUES(0,0,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(1,0,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(2,0,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(3,0,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(4,0,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(5,0,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(6,0,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(7,0,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(0,1,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(1,1,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(2,1,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(3,1,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(4,1,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(5,1,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(6,1,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(7,1,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(0,2,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(1,2,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(2,2,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(3,2,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(4,2,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(5,2,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(6,2,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(7,2,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(0,3,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(1,3,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(2,3,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(3,3,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(4,3,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(5,3,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(6,3,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(7,3,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(0,4,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(1,4,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(2,4,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(3,4,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(4,4,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(5,4,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(6,4,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
INSERT INTO "tbl_Nit_TSInfo" VALUES(7,4,65535,0,'0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0');
DROP TABLE IF EXISTS "tbl_OADInfo";
CREATE TABLE [tbl_OADInfo] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[DL_StartTime] INTEGER  NULL,
[DL_EndTime] INTEGER  NULL,
[enMonitorState] INTEGER  NULL,
[ServiceInfo_u16Oad_PID] INTEGER  NULL,
[ServiceInfo_u16ServiceId] INTEGER  NULL,
[ServiceInfo_u16Transport_stream_id] INTEGER  NULL,
[ServiceInfo_u16Original_network_id] INTEGER  NULL,
[CheckStatus_u8ScheduleOn] INTEGER  NULL
);
INSERT INTO "tbl_OADInfo" VALUES(0,0,0,15,8191,0,65535,65535,0);
DROP TABLE IF EXISTS "tbl_OADInfo_UntDescriptor";
CREATE TABLE [tbl_OADInfo_UntDescriptor] (
[_id] INTEGER  PRIMARY KEY NULL,
[untLocation_association_tag] INTEGER  NULL,
[untSchedule_u32StartTime] INTEGER  NULL,
[untSchedule_u32EndTime] INTEGER  NULL,
[untSchedule_u8FinalAvail] INTEGER  NULL,
[untSchedule_u8PeriodFlag] INTEGER  NULL,
[untSchedule_u8PeriodUnit] INTEGER  NULL,
[untSchedule_u8DurationUnit] INTEGER  NULL,
[untSchedule_u8EstimateUnit] INTEGER  NULL,
[untSchedule_u8Period] INTEGER  NULL,
[untSchedule_u8Duration] INTEGER  NULL,
[untSchedule_u8Estimate] INTEGER  NULL
);
INSERT INTO "tbl_OADInfo_UntDescriptor" VALUES(0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_OADInfo_UntDescriptor" VALUES(1,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_OADInfo_UntDescriptor" VALUES(2,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_OADInfo_UntDescriptor" VALUES(3,0,0,0,0,0,0,0,0,0,0,0);
DROP TABLE IF EXISTS "tbl_OADWakeUpInfo";
CREATE TABLE [tbl_OADWakeUpInfo] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u8ScheduleOn] INTEGER NULL,
[u32WakeUpTime] INTEGER NULL
);
INSERT INTO "tbl_OADWakeUpInfo" VALUES(0,0,0);
DROP TABLE IF EXISTS "tbl_PicMode_Setting";
CREATE TABLE [tbl_PicMode_Setting] (
[InputSrcType] INTEGER  NULL,
[PictureModeType] INTEGER  NULL,
[u8Backlight] INTEGER  NULL,
[u8Contrast] INTEGER  NULL,
[u8Brightness] INTEGER  NULL,
[u8Saturation] INTEGER  NULL,
[u8Sharpness] INTEGER  NULL,
[u8Hue] INTEGER  NULL,
[eColorTemp] INTEGER  NULL,
[eVibrantColour] INTEGER  NULL,
[ePerfectClear] INTEGER  NULL,
[eDynamicContrast] INTEGER  NULL,
[eDynamicBacklight] INTEGER  NULL,
[eAutoBrightnessSensor] INTEGER  NULL,
[eActiveBackLight] INTEGER  NULL,
[_Remark] TEXT  NULL,
PRIMARY KEY ([InputSrcType],[PictureModeType])
);
INSERT INTO "tbl_PicMode_Setting" VALUES(1,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(1,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(0,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(2,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(11,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,3,100,50,50,50,75,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(16,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(20,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(28,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(29,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,0,100,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,1, 80,75,50,50,20,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,4,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,5,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,6,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(34,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(23,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(3,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(4,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(5,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(6,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(7,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(8,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(9,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(10,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(12,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(13,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(14,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(15,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(17,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(18,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(19,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(21,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(22,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(24,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(25,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(26,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(27,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(30,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(31,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(32,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(33,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,0,100,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,1, 80,75,50,50,20,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,4,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,5,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,6,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(35,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,0,100,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,1, 80,75,50,50,20,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,4,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,5,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,6,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(36,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(37,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,0,100,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,1, 80,75,50,50,20,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,4,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,5,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,6,100,50,50,50,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(38,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(39,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(40,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(41,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,1, 80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,2, 75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(42,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,0,100,75,50,50,70,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,1,80,60,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,2,75,55,45,40,50,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,3,100,50,50,50,75,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,4,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,5,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,6,100,50,50,50,50,50,1,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,7,100,70,48,70,60,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,8,100,80,45,80,30,50,0,0,0,0,0,0,0,'');
INSERT INTO "tbl_PicMode_Setting" VALUES(43,9,100,80,46,80,30,50,0,0,0,0,0,0,0,'');
DROP TABLE IF EXISTS "tbl_PipSetting";
CREATE TABLE [tbl_PipSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[enPipMode] INTEGER  NULL,
[enSubInputSourceType] INTEGER  NULL,
[enPipSize] INTEGER  NULL,
[enPipPosition] INTEGER  NULL,
[bBolderEnable] INTEGER  NULL,
[enPipSoundSrc] INTEGER  NULL,
[u8BorderWidth] INTEGER  NULL,
[bPipEnable] INTEGER  NULL
);
INSERT INTO "tbl_PipSetting" VALUES(0,3,0,0,3,0,0,1,1);
DROP TABLE IF EXISTS "tbl_SNConfig";
CREATE TABLE [tbl_SNConfig] (
[_id] INTEGER  NULL,
[Dirty_flag] INTEGER  NULL,
[PIP_ENABLE] INTEGER  NULL,
[ENABLE_DTMB] INTEGER  NULL,
[DVB_ENABLE] INTEGER  NULL,
[CHINA_ATV_ENABLE] INTEGER  NULL,
[CI_ENABLE] INTEGER  NULL,
[CI_PLUS_ENABLE] INTEGER  NULL,
[MHEG5_ENABLE] INTEGER  NULL,
[EPG_ENABLE] INTEGER  NULL,
[EPG_EED_ENABLE] INTEGER  NULL,
[PVR_ENABLE] INTEGER  NULL,
[VCHIP_ENABLE] INTEGER  NULL,
[SUBTITLE_ENABLE] INTEGER  NULL,
[TTX_ENABLE] INTEGER  NULL,
[ATSC_CC_ENABLE] INTEGER  NULL,
[ISDB_CC_ENABLE] INTEGER  NULL,
[NTSC_CC_ENABLE] INTEGER  NULL,
[OAD_ENABLE] INTEGER  NULL,
[HBBTV_ENABLE] INTEGER  NULL,
[GINGA_ENABLE] INTEGER  NULL,
[SUPPORT_EURO_HDTV] INTEGER  NULL,
[STB_ENABLE] INTEGER  NULL,
[CEC_ENABLE] INTEGER  NULL,
[STR_ENABLE] INTEGER  NULL,
[STEREO_3D_ENABLE] INTEGER  NULL,
[ENABLE_6M30] INTEGER  NULL,
[SSC_ENABLE] INTEGER  NULL,
[CHINA_ENABLE] INTEGER  NULL,
[OFL_DET] INTEGER  NULL,
[ISDB_SYSTEM_ENABLE] INTEGER  NULL,
[TV_SYSTEM] INTEGER  NULL,
[ENABLE_MVC] INTEGER  NULL,
[ENABLE_ISO_MVC] INTEGER  NULL,
[CVBSOUT_ENABLE] INTEGER  NULL,
[VIF_ASIA_SIGNAL_OPTION] INTEGER  NULL,
[A3_STB_ENABLE] INTEGER  NULL,
[ENABLE_DIVX_PLUS] INTEGER  NULL,
[ENABLE_4K2K] INTEGER  NULL
);
INSERT INTO "tbl_SNConfig" VALUES(0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
DROP TABLE IF EXISTS "tbl_SoundModeSetting";
CREATE TABLE [tbl_SoundModeSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[Bass] INTEGER  NULL,
[Treble] INTEGER  NULL,
[EqBand1] INTEGER  NULL,
[EqBand2] FLOAT  NULL,
[EqBand3] INTEGER  NULL,
[EqBand4] INTEGER  NULL,
[EqBand5] INTEGER  NULL,
[EqBand6] INTEGER  NULL,
[EqBand7] INTEGER  NULL,
[UserMode] INTEGER  NULL,
[Balance] INTEGER  NULL,
[enSoundAudioChannel] INTEGER  NULL
);
INSERT INTO "tbl_SoundModeSetting" VALUES(0,50,50,50,50.0,50,50,50,50,50,0,50,0);
INSERT INTO "tbl_SoundModeSetting" VALUES(1,75,75,91,50.0,26,58,91,50,50,0,50,0);
INSERT INTO "tbl_SoundModeSetting" VALUES(2,90,75,75,26.0,26,91,75,50,50,0,50,0);
INSERT INTO "tbl_SoundModeSetting" VALUES(3,35,35,26,26.0,83,83,50,50,50,0,50,0);
INSERT INTO "tbl_SoundModeSetting" VALUES(4,55,45,50,50.0,50,50,50,50,50,0,50,0);
INSERT INTO "tbl_SoundModeSetting" VALUES(5,55,56,50,50.0,50,50,50,50,50,0,50,0);
INSERT INTO "tbl_SoundModeSetting" VALUES(6,55,57,50,50.0,50,50,50,50,50,0,50,0);
DROP TABLE IF EXISTS "tbl_SoundSetting";
CREATE TABLE [tbl_SoundSetting] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[SoundMode] INTEGER  NULL,
[AudysseyDynamicVolume] INTEGER  NULL,
[AudysseyEQ] INTEGER  NULL,
[SurroundSoundMode] INTEGER  NULL,
[Surround] INTEGER  NULL,
[bEnableAVC] INTEGER  NULL,
[Volume] INTEGER  NULL,
[HPVolume] INTEGER  NULL,
[Balance] INTEGER  NULL,
[Primary_Flag] INTEGER  NULL,
[enSoundAudioLan1] INTEGER  NULL,
[enSoundAudioLan2] INTEGER  NULL,
[MUTE_Flag] INTEGER  NULL,
[enSoundAudioChannel] INTEGER  NULL,
[bEnableAD] INTEGER  NULL,
[ADVolume] INTEGER  NULL,
[ADOutput] INTEGER  NULL,
[SPDIF_Delay] INTEGER  NULL,
[Speaker_Delay] INTEGER  NULL,
[SpeakerPreScale] TEXT  NULL,
[HeadPhonePreScale] TEXT  NULL,
[LineOutPreScale] TEXT  NULL,
[SCART1PreScale] TEXT  NULL,
[SCART2PreScale] TEXT  NULL,
[bEnableHI] INTEGER  NULL,
[bEnableHeavyBass] INTEGER  NULL,
[HeavyBassVolume] INTEGER  NULL,
[WallMusic] INTEGER  NULL,
[DGClarity] INTEGER  NULL,
[TrueBass] INTEGER  NULL,
[bEnableDRC] INTEGER  NULL,
[hdmi1AudioSource] INTEGER DEFAULT '0' NULL,
[hdmi2AudioSource] INTEGER DEFAULT '0' NULL,
[hdmi3AudioSource] INTEGER DEFAULT '0' NULL,
[hdmi4AudioSource] INTEGER DEFAULT '0' NULL,
[MicVal] INTEGER  NULL,
[MicEchoVal] INTEGER  NULL
);
INSERT INTO "tbl_SoundSetting" VALUES(0,0,0,0,0,0,1,20,20,50,0,6,6,0,0,0,20,0,0,0,'0x00,0x89,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x71,0x71,0x22,0x71,0x71,0x71,0x71,0x27,0x71,0x29,0x30,0x31,0x32,0x33,0x65,0x65,0x36,0x71,0x65,0x71,0x40,0x41,0x42,0x71,0x44,0x45,','0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x40,0x41,','0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,','0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,','0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,0x19,',0,0,0,0,0,0,0,0,0,0,0,50,0);
DROP TABLE IF EXISTS "tbl_SubtitleSetting";
CREATE TABLE [tbl_SubtitleSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[SubtitleDefaultLanguage] INTEGER NULL,
[SubtitleDefaultLanguage_2] INTEGER NULL,
[fHardOfHearing] INTEGER NULL,
[fEnableSubTitle] INTEGER NULL
);
INSERT INTO "tbl_SubtitleSetting" VALUES(0,6,6,0,0);
DROP TABLE IF EXISTS "tbl_SystemSetting";
CREATE TABLE [tbl_SystemSetting] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[fRunInstallationGuide] INTEGER  NULL,
[fNoChannel] INTEGER  NULL,
[bDisableSiAutoUpdate] INTEGER  NULL,
[bDisableDynamicRescan] INTEGER  NULL,
[enInputSourceType] INTEGER  NULL,
[Country] INTEGER  NULL,
[enCableOperators] INTEGER  NULL,
[enSatellitePlatform] INTEGER  NULL,
[u16NetworkId] INTEGER  NULL,
[Language] INTEGER  NULL,
[en3DARC] INTEGER  NULL,
[enSPDIFMODE] INTEGER  NULL,
[fSoftwareUpdate] INTEGER  NULL,
[U8OADTime] INTEGER  NULL,
[fOADScanAfterWakeup] INTEGER  NULL,
[fAutoVolume] INTEGER  NULL,
[fDcPowerOFFMode] INTEGER  NULL,
[DtvRoute] INTEGER  NULL,
[ScartOutRGB] INTEGER  NULL,
[U8Transparency] INTEGER  NULL,
[u32MenuTimeOut] INTEGER  NULL,
[AudioOnly] INTEGER  NULL,
[bEnableWDT] INTEGER  NULL,
[u8FavoriteRegion] INTEGER  NULL,
[u8Bandwidth] INTEGER  NULL,
[u8TimeShiftSizeType] INTEGER  NULL,
[fOadScan] INTEGER  NULL,
[bEnablePVRRecordAll] INTEGER  NULL,
[u8ColorRangeMode] INTEGER  NULL,
[u8HDMIAudioSource] INTEGER  NULL,
[bEnableAlwaysTimeshift] INTEGER  NULL,
[eSUPER] INTEGER  NULL,
[bUartBus] INTEGER  NULL,
[m_AutoZoom] INTEGER  NULL,
[bOverScan] INTEGER  NULL,
[m_u8BrazilVideoStandardType] INTEGER  NULL,
[m_u8SoftwareUpdateMode] INTEGER  NULL,
[OSD_Active_Time] INTEGER  NULL,
[m_MessageBoxExist] INTEGER  NULL,
[u16LastOADVersion] INTEGER  NULL,
[bEnableAutoChannelUpdate] INTEGER  NULL,
[u32PvrSettingId] INTEGER  NULL,
[enForcedInputSourceType] INTEGER  NULL,
[enLocalDimm] INTEGER  NULL,
[bATVChSwitchFreeze] INTEGER  NULL,
[bSourceDetectEnable] INTEGER  NULL,
[bAutoSourceSwitch] INTEGER  NULL,
[u32MsrvTimerCounter] INTEGER  NULL,
[standbyNoOperation] INTEGER DEFAULT '''''''0''''''' NULL,
[standbyNoSignal] BOOLEAN DEFAULT '''0''' NULL,
[screenSaveMode] BOOLEAN DEFAULT '''''''0''''''' NULL,
[bAutoMHLSwitch] BOOLEAN DEFAULT '''0''' NULL,
[bViewerPrompt] BOOLEAN DEFAULT '''0''' NULL,
[u8OpMode] INTEGER  NULL,
[u32CicamIdentifier] INTEGER  NULL,
[bEnableHbbtv] INTEGER  NULL,
[bWOLEnable] INTEGER  NULL,
[u32StrPowerMode] INTEGER  NULL,
[bEnableACR] INTEGER  NULL,
[bSourcePreview] BOOLEAN DEFAULT '''0''' NULL,
[bMonitorITC] INTEGER DEFAULT '0' NULL,
[bxvYCCOnOff] BOOLEAN DEFAULT '''0''' NULL,
[bEnableStoreCookies] BOOLEAN DEFAULT '''0''' NULL,
[bServiceListNeedRearrange] INTEGER  NULL,
[bCiOccupiedTuner] INTEGER DEFAULT '0' NULL,
[u16CiPinCode] INTEGER DEFAULT '65535' NULL,
[u16HdmidEdidVersion] INTEGER DEFAULT '0' NULL,
[bMainAutoDetectHdrLevel] BOOLEAN DEFAULT '''1''' NULL,
[bSubAutoDetectHdrLevel] BOOLEAN DEFAULT '''1''' NULL,
[u8MainHdrLevel] INTEGER DEFAULT '1' NULL,
[u8SubHdrLevel] INTEGER DEFAULT '1' NULL,
[bMainHdrOn] BOOLEAN DEFAULT '''1''' NULL,
[bSubHdrOn] BOOLEAN DEFAULT '''1''' NULL
);
INSERT INTO "tbl_SystemSetting" VALUES(0,0,0,0,0,1,50,0,0,0,6,0,0,0,4,0,0,0,0,0,3,5000,0,1,0,8,0,0,0,2,0,0,1,0,0,1,4,0,0,0,0,0,0,0,3,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,65535,0,1,1,1,1,1,1);
DROP TABLE IF EXISTS "tbl_ThreeDVideoMode";
CREATE TABLE [tbl_ThreeDVideoMode] (
[InputSrcType] INTEGER  PRIMARY KEY NOT NULL,
[eThreeDVideo] INTEGER DEFAULT '''''''0''''''' NOT NULL,
[eThreeDVideoDisplayMode] INTEGER DEFAULT '''''''0''''''' NOT NULL,
[eThreeDVideoDisplayFormat] INTEGER DEFAULT '''''''0''''''' NOT NULL,
[eThreeDVideo3DDepth] INTEGER DEFAULT '''''''0''''''' NOT NULL,
[eThreeDVideoAutoStart] INTEGER DEFAULT '''''''0''''''' NOT NULL,
[eThreeDVideo3DOutputAspect] INTEGER DEFAULT '''''''0''''''' NOT NULL,
[eThreeDVideoLRViewSwitch] INTEGER DEFAULT '''''''0''''''' NOT NULL,
[eThreeDVideo3DOffset] INTEGER DEFAULT '''''''0''''''' NULL,
[eThreeDVideoSelfAdaptiveDetect] INTEGER DEFAULT '''''''0''''''' NULL,
[eThreeDVideo3DTo2D] INTEGER DEFAULT '''''''0''''''' NULL,
[eThreeDVideoSelfAdaptiveLevel] INTEGER DEFAULT '''''''0''''''' NULL
);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(0,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(1,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(2,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(3,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(4,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(5,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(6,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(7,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(8,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(9,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(10,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(11,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(12,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(13,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(14,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(15,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(16,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(17,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(18,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(19,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(20,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(21,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(22,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(23,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(24,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(25,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(26,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(27,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(28,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(29,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(30,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(31,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(32,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(33,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(34,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(35,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(36,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(37,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(38,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(39,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(40,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(41,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(42,0,0,0,15,0,0,0,30,0,0,0);
INSERT INTO "tbl_ThreeDVideoMode" VALUES(43,0,0,0,15,0,0,0,30,0,0,0);
DROP TABLE IF EXISTS "tbl_ThreeDVideoRouterSetting";
CREATE TABLE [tbl_ThreeDVideoRouterSetting] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(0,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(1,1,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(2,2,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(3,3,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(4,4,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(5,5,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(6,6,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(7,7,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(8,8,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(9,9,9,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(10,2,5,0,5);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(11,11,5,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(12,12,12,12,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(13,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DTo2DVideoRouterSetting";
CREATE TABLE [tbl_3DTo2DVideoRouterSetting] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(0,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(1,1,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(2,2,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(3,3,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(4,4,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(5,5,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(6,6,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(7,7,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(8,8,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(9,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(10,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(11,11,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(12,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(13,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K3DVideoRouterSetting";
CREATE TABLE [tbl_4K2K3DVideoRouterSetting] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(1,2,3,4,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(2,5,6,7,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(3,3,13,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(4,4,13,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(5,5,13,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(6,6,13,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(7,7,13,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(8,8,13,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(9,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(10,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(11,11,13,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(12,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_TimeSetting";
CREATE TABLE [tbl_TimeSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[bOnTimeFlag] INTEGER NULL,
[bOffTimeFlag] INTEGER NULL,
[enOffTimeState] INTEGER NULL,
[u8OffTimer_Info_Hour] INTEGER NULL,
[u8OffTimer_Info_Min] INTEGER NULL,
[enOnTimeState] INTEGER NULL,
[u8OnTimer_Info_Hour] INTEGER NULL,
[u8OnTimer_Info_Min] INTEGER NULL,
[cOnTimerChannel] INTEGER NULL,
[cOnTimeTVSrc] INTEGER NULL,
[cOnTimeAntennaType] INTEGER NULL,
[cOnTimerVolume] INTEGER NULL,
[eTimeZoneInfo] INTEGER NULL,
[bIs12Hour] INTEGER NULL,
[bIsAutoSync] INTEGER NULL,
[bClockMode] INTEGER NULL,
[bAutoSleepFlag] INTEGER NULL,
[bIsDaylightsaving] INTEGER NULL,
[enTimerBootMode] INTEGER NULL,
[s32OffsetTime] INTEGER NULL,
[enDaylightSavingMode] INTEGER NULL,
[enLinuxTimeSource] INTEGER NULL,
[u32NtpTime] INTEGER NULL,
[u32OldRtc] INTEGER NULL
);
INSERT INTO "tbl_TimeSetting" VALUES(0,0,0,0,0,0,0,12,0,0,0,0,30,50,0,0,1,0,0,0,28800,0,1,0,0);
DROP TABLE IF EXISTS "tbl_UserColorTemp";
CREATE TABLE [tbl_UserColorTemp] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[u8RedGain] INTEGER DEFAULT '128' NULL,
[u8GreenGain] INTEGER DEFAULT '128' NULL,
[u8BlueGain] INTEGER DEFAULT '128' NULL,
[u8RedOffset] INTEGER DEFAULT '128' NULL,
[u8GreenOffset] INTEGER DEFAULT '128' NULL,
[u8BlueOffset] INTEGER DEFAULT '128' NULL
);
INSERT INTO "tbl_UserColorTemp" VALUES(0,128,128,128,128,128,128);
DROP TABLE IF EXISTS "tbl_UserColorTempEx";
CREATE TABLE [tbl_UserColorTempEx] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[u16RedGain] INTEGER DEFAULT '1024' NULL,
[u16GreenGain] INTEGER DEFAULT '1024' NULL,
[u16BlueGain] INTEGER DEFAULT '1024' NULL,
[u16RedOffset] INTEGER DEFAULT '1024' NULL,
[u16GreenOffset] INTEGER DEFAULT '1024' NULL,
[u16BlueOffset] INTEGER DEFAULT '1024' NULL
);
INSERT INTO "tbl_UserColorTempEx" VALUES(0,1024,1024,1024,1024,1024,1024);
INSERT INTO "tbl_UserColorTempEx" VALUES(1,1024,1024,1024,1024,1024,1024);
INSERT INTO "tbl_UserColorTempEx" VALUES(2,1024,1024,1024,1024,1024,1024);
INSERT INTO "tbl_UserColorTempEx" VALUES(3,1024,1024,1024,1024,1024,1024);
INSERT INTO "tbl_UserColorTempEx" VALUES(4,1024,1024,1024,1024,1024,1024);
INSERT INTO "tbl_UserColorTempEx" VALUES(5,1024,1024,1024,1024,1024,1024);
INSERT INTO "tbl_UserColorTempEx" VALUES(6,1024,1024,1024,1024,1024,1024);
INSERT INTO "tbl_UserColorTempEx" VALUES(7,1024,1024,1024,1024,1024,1024);
INSERT INTO "tbl_UserColorTempEx" VALUES(8,1024,1024,1024,1024,1024,1024);
DROP TABLE IF EXISTS "tbl_UserLocationSetting";
CREATE TABLE [tbl_UserLocationSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u16LocationNo] INTEGER  NULL,
[s16ManualLongitude] INTEGER  NULL,
[s16ManualLatitude] INTEGER  NULL
);
INSERT INTO "tbl_UserLocationSetting" VALUES(0,0,0,0);
DROP TABLE IF EXISTS "tbl_UserMMSetting";
CREATE TABLE [tbl_UserMMSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u8SubtitleSpecific] INTEGER NULL,
[u8SubtitleBGColor] INTEGER NULL,
[u8SubtitleFontColor] INTEGER NULL,
[u8SlideShowTime] INTEGER NULL,
[u8SlideShowMode] INTEGER NULL,
[fPreviewOn] INTEGER NULL,
[fResumePlay] INTEGER NULL
);
INSERT INTO "tbl_UserMMSetting" VALUES(0,0,0,0,0,0,0,0);
DROP TABLE IF EXISTS "tbl_UserOverScanMode";
CREATE TABLE [tbl_UserOverScanMode] (
[InputSrcType] INTEGER  PRIMARY KEY NOT NULL,
[OverScanHposition] INTEGER DEFAULT '''0''' NULL,
[OverScanVposition] INTEGER DEFAULT '''0''' NULL,
[OverScanHRatio] INTEGER DEFAULT '''0''' NULL,
[OverScanVRatio] INTEGER DEFAULT '''0''' NULL
);
INSERT INTO "tbl_UserOverScanMode" VALUES(0,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(1,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(2,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(3,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(4,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(5,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(6,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(7,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(8,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(9,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(10,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(11,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(12,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(13,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(14,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(15,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(16,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(17,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(18,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(19,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(20,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(21,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(22,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(23,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(24,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(25,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(26,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(27,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(28,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(29,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(30,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(31,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(32,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(33,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(34,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(35,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(36,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(37,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(38,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(39,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(40,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(41,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(42,0,0,0,0);
INSERT INTO "tbl_UserOverScanMode" VALUES(43,0,0,0,0);
DROP TABLE IF EXISTS "tbl_UserPCModeSetting";
CREATE TABLE [tbl_UserPCModeSetting] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[u16HorizontalStart] INTEGER DEFAULT '''''''0''''''' NULL,
[u16VerticalStart] INTEGER DEFAULT '''''''0''''''' NULL,
[u16HorizontalTotal] INTEGER DEFAULT '''''''0''''''' NULL,
[u8ModeIndex] INTEGER DEFAULT '''''''0''''''' NULL,
[u16Phase] INTEGER DEFAULT '''''''0''''''' NULL,
[u8AutoSign] INTEGER DEFAULT '''''''0''''''' NULL,
[u8Order] INTEGER DEFAULT '''''''0''''''' NULL,
[u16UI_HorizontalStart] INTEGER DEFAULT '''''''0''''''' NULL,
[u16UI_VorizontalStart] INTEGER DEFAULT '''''''0''''''' NULL,
[u16UI_Clock] INTEGER DEFAULT '''''''0''''''' NULL,
[u16UI_Phase] INTEGER DEFAULT '''''''0''''''' NULL,
[u32SyncPolarity] INTEGER DEFAULT '''''''0''''''' NULL,
[u32HSyncStart] INTEGER DEFAULT '''''''0''''''' NULL,
[u32HSyncEnd] INTEGER DEFAULT '''''''0''''''' NULL
);
INSERT INTO "tbl_UserPCModeSetting" VALUES(0,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(1,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(2,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(3,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(4,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(5,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(6,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(7,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(8,0,0,0,0,0,0,0,0,0,'''''''0''''''','''''''0''''''',0,0,0);
INSERT INTO "tbl_UserPCModeSetting" VALUES(9,0,0,0,0,0,0,0,50,60,50,50,0,0,0);
DROP TABLE IF EXISTS "tbl_VideoSetting";
CREATE TABLE [tbl_VideoSetting] (
[InputSrcType] INTEGER  PRIMARY KEY NOT NULL,
[ePicture] INTEGER DEFAULT '1' NULL,
[u8SubBrightness] INTEGER DEFAULT '128' NULL,
[u8SubContrast] INTEGER DEFAULT '128' NULL,
[enARCType] INTEGER DEFAULT '0' NULL,
[fOutput_RES] INTEGER DEFAULT '0' NULL,
[tvsys] INTEGER DEFAULT '0' NULL,
[LastVideoStandardMode] INTEGER DEFAULT '0' NULL,
[LastAudioStandardMode] INTEGER DEFAULT '0' NULL,
[eDynamic_Contrast] INTEGER DEFAULT '0' NULL,
[eFilm] INTEGER DEFAULT '0' NULL,
[eTvFormat] INTEGER DEFAULT '0' NULL,
[enGameModeARCType] INTEGER DEFAULT '1' NULL,
[enAutoModeARCType] INTEGER DEFAULT '1' NULL,
[enPcModeARCType] INTEGER DEFAULT '1' NULL,
[bIsPcMode] INTEGER DEFAULT '0' NULL
);
INSERT INTO "tbl_VideoSetting" VALUES(0,5,128,128,1,12,1,0,1,1,1,2,1,1,1,1);
INSERT INTO "tbl_VideoSetting" VALUES(1,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(2,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(3,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(4,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(5,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(6,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(7,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(8,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(9,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(10,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(11,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(12,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(13,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(14,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(15,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(16,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(17,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(18,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(19,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(20,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(21,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(22,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(23,5,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(24,5,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(25,5,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(26,5,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(27,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(28,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(29,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(30,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(31,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(32,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(33,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(34,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(35,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(36,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(37,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(38,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(39,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(40,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(41,1,128,128,1,12,1,0,1,1,1,2,1,1,1,0);
INSERT INTO "tbl_VideoSetting" VALUES(42,5,128,128,1,12,1,0,1,1,1,2,1,1,1,1);
INSERT INTO "tbl_VideoSetting" VALUES(43,5,128,128,1,12,1,0,1,1,1,2,1,1,1,1);
DROP TABLE IF EXISTS "tbl_VChipMpaaItem";
CREATE TABLE [tbl_VChipMpaaItem] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[Rating] INTEGER NULL,
[bIsNR] INTEGER NULL
);
INSERT INTO "tbl_VChipMpaaItem" VALUES(0,0,0);
DROP TABLE IF EXISTS "tbl_RR5RatingPair";
CREATE TABLE [tbl_RR5RatingPair] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[stRR5RatingPair_id_0] INTEGER NULL,
[stRR5RatingPair_id_1] INTEGER NULL,
[stRR5RatingPair_id_2] INTEGER NULL,
[stRR5RatingPair_id_3] INTEGER NULL,
[stRR5RatingPair_id_4] INTEGER NULL,
[stRR5RatingPair_id_5] INTEGER NULL,
[stRR5RatingPair_id_6] INTEGER NULL,
[stRR5RatingPair_id_7] INTEGER NULL,
[stRR5RatingPair_id_8] INTEGER NULL,
[stRR5RatingPair_id_9] INTEGER NULL,
[stRR5RatingPair_id_10] INTEGER NULL,
[stRR5RatingPair_id_11] INTEGER NULL,
[stRR5RatingPair_id_12] INTEGER NULL,
[stRR5RatingPair_id_13] INTEGER NULL,
[stRR5RatingPair_id_14] INTEGER NULL
);
INSERT INTO "tbl_RR5RatingPair" VALUES(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(11,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(12,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(13,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(14,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(15,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(16,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(17,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(18,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
INSERT INTO "tbl_RR5RatingPair" VALUES(19,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
DROP TABLE IF EXISTS "tbl_VChipSetting";
CREATE TABLE [tbl_VChipSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[stVChipTVItem_NONE] INTEGER NULL,
[stVChipTVItem_Y] INTEGER NULL,
[stVChipTVItem_Y7] INTEGER NULL,
[stVChipTVItem_G] INTEGER NULL,
[stVChipTVItem_PG] INTEGER NULL,
[stVChipTVItem_14] INTEGER NULL,
[stVChipTVItem_MA] INTEGER NULL,
[u8VChipCEItem] INTEGER NULL,
[u8VChipCFItem] INTEGER NULL,
[u16InputBlockItem] INTEGER NULL,
[u16InputBlockItem_Loop] INTEGER NULL,
[u16u16InputBlocFlag] INTEGER NULL,
[u8SelectedRegion] INTEGER NULL
);
INSERT INTO "tbl_VChipSetting" VALUES(0,0,0,0,0,0,0,0,0,0,0,0,0,0);
DROP TABLE IF EXISTS "tbl_VChipRatingInfo";
CREATE TABLE [tbl_VChipRatingInfo] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u8VersionNo] INTEGER NULL,
[u8RegionNo] INTEGER NULL,
[u8Region5Name] TEXT NULL,
[u8NoDimension] INTEGER NULL
);
INSERT INTO "tbl_VChipRatingInfo" VALUES(0,255,0,'default',0);
DROP TABLE IF EXISTS "tbl_Regin5DimensionInfo";
CREATE TABLE [tbl_Regin5DimensionInfo] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u8DimensionName] TEXT NULL,
[u8Values_Defined] INTEGER NULL,
[u16Graduated_Scale] INTEGER NULL
);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(0,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(1,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(2,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(3,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(4,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(5,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(6,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(7,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(8,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(9,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(10,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(11,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(12,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(13,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(14,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(15,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(16,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(17,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(18,'default',0,0);
INSERT INTO "tbl_Regin5DimensionInfo" VALUES(19,'default',0,0);
DROP TABLE IF EXISTS "tbl_AbbRatingText";
CREATE TABLE [tbl_AbbRatingText] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[Regin5Dimension_index] INTEGER NULL,
[stAbbRatingText] TEXT NULL
);
INSERT INTO "tbl_AbbRatingText" VALUES(0,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(1,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(2,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(3,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(4,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(5,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(6,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(7,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(8,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(9,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(10,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(11,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(12,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(13,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(14,0,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(15,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(16,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(17,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(18,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(19,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(20,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(21,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(22,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(23,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(24,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(25,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(26,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(27,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(28,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(29,1,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(30,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(31,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(32,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(33,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(34,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(35,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(36,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(37,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(38,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(39,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(40,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(41,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(42,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(43,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(44,2,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(45,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(46,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(47,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(48,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(49,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(50,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(51,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(52,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(53,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(54,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(55,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(56,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(57,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(58,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(59,3,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(60,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(61,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(62,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(63,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(64,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(65,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(66,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(67,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(68,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(69,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(70,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(71,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(72,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(73,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(74,4,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(75,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(76,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(77,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(78,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(79,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(80,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(81,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(82,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(83,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(84,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(85,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(86,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(87,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(88,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(89,5,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(90,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(91,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(92,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(93,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(94,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(95,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(96,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(97,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(98,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(99,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(100,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(101,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(102,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(103,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(104,6,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(105,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(106,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(107,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(108,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(109,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(110,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(111,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(112,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(113,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(114,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(115,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(116,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(117,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(118,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(119,7,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(120,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(121,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(122,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(123,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(124,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(125,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(126,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(127,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(128,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(129,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(130,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(131,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(132,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(133,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(134,8,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(135,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(136,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(137,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(138,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(139,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(140,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(141,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(142,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(143,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(144,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(145,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(146,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(147,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(148,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(149,9,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(150,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(151,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(152,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(153,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(154,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(155,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(156,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(157,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(158,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(159,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(160,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(161,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(162,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(163,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(164,10,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(165,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(166,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(167,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(168,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(169,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(170,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(171,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(172,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(173,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(174,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(175,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(176,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(177,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(178,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(179,11,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(180,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(181,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(182,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(183,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(184,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(185,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(186,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(187,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(188,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(189,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(190,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(191,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(192,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(193,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(194,12,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(195,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(196,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(197,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(198,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(199,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(200,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(201,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(202,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(203,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(204,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(205,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(206,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(207,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(208,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(209,13,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(210,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(211,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(212,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(213,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(214,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(215,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(216,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(217,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(218,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(219,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(220,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(221,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(222,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(223,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(224,14,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(225,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(226,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(227,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(228,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(229,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(230,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(231,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(232,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(233,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(234,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(235,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(236,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(237,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(238,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(239,15,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(240,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(241,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(242,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(243,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(244,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(245,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(246,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(247,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(248,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(249,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(250,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(251,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(252,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(253,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(254,16,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(255,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(256,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(257,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(258,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(259,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(260,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(261,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(262,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(263,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(264,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(265,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(266,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(267,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(268,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(269,17,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(270,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(271,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(272,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(273,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(274,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(275,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(276,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(277,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(278,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(279,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(280,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(281,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(282,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(283,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(284,18,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(285,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(286,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(287,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(288,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(289,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(290,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(291,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(292,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(293,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(294,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(295,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(296,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(297,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(298,19,'default');
INSERT INTO "tbl_AbbRatingText" VALUES(299,19,'default');
DROP TABLE IF EXISTS "tbl_MiscSetting";
CREATE TABLE [tbl_MiscSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[MTSSetting] INTEGER NULL,
[BlockUnratedTV] INTEGER NULL,
[CurrentTVtype] INTEGER NULL
);
INSERT INTO "tbl_MiscSetting" VALUES(0,1,0,0);
DROP TABLE IF EXISTS "tbl_mstCECSetting";
CREATE TABLE [tbl_mstCECSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[u8CECStatus] INTEGER NULL,
[u8AutoStandby] INTEGER NULL,
[u8ARCStatus] INTEGER NULL,
[u8AudioModeStatus] INTEGER NULL,
[u8TvAutpPowerOn] INTEGER NULL,
[u8AmplifierControl] INTEGER NULL,
[u8SpearkerPreference] INTEGER NULL,
[u8QuickMenuSpeakerPreference] INTEGER NULL
);
INSERT INTO "tbl_mstCECSetting" VALUES(0,1,0,1,1,1,1,1,1);
DROP TABLE IF EXISTS "tbl_CCSetting";
CREATE TABLE [tbl_CCSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[OnOffMode] INTEGER NULL,
[BasicMode] INTEGER NULL,
[AdvancedMode] INTEGER NULL
);
INSERT INTO "tbl_CCSetting" VALUES(0,1,1,1);
DROP TABLE IF EXISTS "tbl_CCAdvancedSetting";
CREATE TABLE [tbl_CCAdvancedSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[bIsDefault] INTEGER NULL,
[FontStyle] INTEGER NULL,
[FontSize] INTEGER NULL,
[FontEdgeStyle] INTEGER NULL,
[FontEdgeColor] INTEGER NULL,
[FGColor] INTEGER NULL,
[BGColor] INTEGER NULL,
[FGOpacity] INTEGER NULL,
[BGOpacity] INTEGER NULL,
[ItalicsAttr] INTEGER NULL,
[UnderlineAttr] INTEGER NULL,
[WinColor] INTEGER NULL,
[WinOpacity] INTEGER NULL
);
INSERT INTO "tbl_CCAdvancedSetting" VALUES(0,1,0,0,0,0,7,0,0,0,0,0,0,3);
INSERT INTO "tbl_CCAdvancedSetting" VALUES(1,1,0,0,0,0,7,0,0,0,0,0,0,3);
INSERT INTO "tbl_CCAdvancedSetting" VALUES(2,1,0,0,0,0,7,0,0,0,0,0,0,3);
INSERT INTO "tbl_CCAdvancedSetting" VALUES(3,1,0,0,0,0,7,0,0,0,0,0,0,3);
INSERT INTO "tbl_CCAdvancedSetting" VALUES(4,1,0,0,0,0,7,0,0,0,0,0,0,3);
INSERT INTO "tbl_CCAdvancedSetting" VALUES(5,1,0,0,0,0,7,0,0,0,0,0,0,3);
INSERT INTO "tbl_CCAdvancedSetting" VALUES(6,1,0,0,0,0,7,0,0,0,0,0,0,3);
INSERT INTO "tbl_CCAdvancedSetting" VALUES(7,1,0,0,0,0,7,0,0,0,0,0,0,3);
DROP TABLE IF EXISTS "tbl_AndroidConfig";
CREATE TABLE [tbl_AndroidConfig] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[PIP_ENABLE] INTEGER NULL,
[OFL_DET] INTEGER NULL,
[PREVIEW_MODE_ENABLE] INTEGER NULL,
[PVR_ENABLE] INTEGER NULL,
[ATSC_CC_ENABLE] INTEGER NULL,
[ISDB_CC_ENABLE] INTEGER NULL,
[NTSC_CC_ENABLE] INTEGER NULL,
[KOREAN_CC_ENABLE] INTEGER NULL,
[ATV_MANUAL_TUNING_ENABLE] INTEGER NULL,
[AUTO_HOH_ENABLE] INTEGER NULL,
[AUDIO_DESCRIPTION_ENABLE] INTEGER NULL,
[THREED_DEPTH_ENABLE] INTEGER NULL,
[SELF_DETECT_ENABLE] INTEGER NULL,
[THREED_CONVERSION_TWODTOTHREED] INTEGER NULL,
[THREED_CONVERSION_AUTO] INTEGER NULL,
[THREED_CONVERSION_PIXEL_ALTERNATIVE] INTEGER NULL,
[THREED_CONVERSION_FRAME_ALTERNATIVE] INTEGER NULL,
[THREED_CONVERSION_CHECK_BOARD] INTEGER NULL,
[THREED_TWOD_AUTO] INTEGER NULL,
[THREED_TWOD_PIXEL_ALTERNATIVE] INTEGER NULL,
[THREED_TWOD_FRAME_ALTERNATIVE] INTEGER NULL,
[THREED_TWOD_CHECK_BOARD] INTEGER NULL
);
INSERT INTO "tbl_AndroidConfig" VALUES(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K60Hz3DVideoRouterSetting";
CREATE TABLE [tbl_4K2K60Hz3DVideoRouterSetting] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(1,1,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(2,2,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(3,3,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(4,4,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(5,5,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(6,6,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(7,7,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(8,8,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(9,9,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(10,0,0,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(11,11,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(12,12,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_BootSetting";
CREATE TABLE [tbl_BootSetting] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[enMuteColor] INTEGER  NULL,
[enBackendColor] INTEGER  NULL,
[enFrameColor] INTEGER  NULL
);
INSERT INTO "tbl_BootSetting" VALUES(0,0,0,0);
DROP TABLE IF EXISTS "tbl_DisplayModeRouterSetting";
CREATE TABLE [tbl_DisplayModeRouterSetting] (
[e3DType] INTEGER PRIMARY KEY NOT NULL,
[e3DMode] INTEGER NULL,
[eDualviewMode] INTEGER NULL
);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(0,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(1,1,1);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(2,2,2);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(3,13,13);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(4,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(5,0,5);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(6,12,5);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(7,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(8,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(9,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(10,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(11,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(12,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(13,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(14,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting" VALUES(15,20,0);
DROP TABLE IF EXISTS "tbl_DvbUserSetting";
CREATE TABLE [tbl_DvbUserSetting] (
[_id] INTEGER  NOT NULL PRIMARY KEY,
[bEnableUHF7M] INTEGER NULL,
[u32LocationCode] INTEGER NULL
);
INSERT INTO "tbl_DvbUserSetting" VALUES(0,0,0);
DROP TABLE IF EXISTS "tbl_InputSourceLockSetting";
CREATE TABLE [tbl_InputSourceLockSetting] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[bStatus] BOOLEAN  NULL
);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(0,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(1,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(2,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(3,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(4,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(5,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(6,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(7,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(8,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(9,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(10,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(11,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(12,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(13,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(14,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(15,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(16,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(17,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(18,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(19,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(20,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(21,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(22,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(23,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(24,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(25,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(26,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(27,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(28,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(29,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(30,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(31,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(32,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(33,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(34,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(35,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(36,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(37,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(38,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(39,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(40,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(41,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(42,0);
INSERT INTO "tbl_InputSourceLockSetting" VALUES(43,0);
DROP TABLE IF EXISTS "tbl_HdmiEdidInfo";
CREATE TABLE [tbl_HdmiEdidInfo] (
[_id] INTEGER  PRIMARY KEY NOT NULL,
[u16HdmiEdidVersion] INTEGER NULL
);
INSERT INTO "tbl_HdmiEdidInfo" VALUES(0,0);
INSERT INTO "tbl_HdmiEdidInfo" VALUES(1,0);
INSERT INTO "tbl_HdmiEdidInfo" VALUES(2,0);
INSERT INTO "tbl_HdmiEdidInfo" VALUES(3,0);
DROP TABLE IF EXISTS "tbl_ThreeDVideoRouterSetting_URSA6";
CREATE TABLE [tbl_ThreeDVideoRouterSetting_URSA6] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(0,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(1,1,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(2,2,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(3,3,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(4,4,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(5,5,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(6,6,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(7,7,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(8,8,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(9,9,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(10,0,0,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(11,11,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(12,12,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(13,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA6" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DTo2DVideoRouterSetting_URSA6";
CREATE TABLE [tbl_3DTo2DVideoRouterSetting_URSA6] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(0,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(1,1,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(2,2,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(3,3,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(4,4,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(5,5,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(6,6,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(7,7,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(8,8,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(9,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(10,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(11,11,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(12,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(13,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA6" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K3DVideoRouterSetting_URSA6";
CREATE TABLE [tbl_4K2K3DVideoRouterSetting_URSA6] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(1,1,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(2,2,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(3,3,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(4,4,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(5,5,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(6,6,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(7,7,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(8,8,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(9,9,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(10,0,0,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(11,11,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(12,12,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA6" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K60Hz3DVideoRouterSetting_URSA6";
CREATE TABLE [tbl_4K2K60Hz3DVideoRouterSetting_URSA6] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(1,1,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(2,2,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(3,3,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(4,4,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(5,5,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(6,6,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(7,7,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(8,8,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(9,9,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(10,0,0,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(11,11,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(12,12,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA6" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_DisplayModeRouterSetting_URSA6";
CREATE TABLE [tbl_DisplayModeRouterSetting_URSA6] (
[e3DType] INTEGER PRIMARY KEY NOT NULL,
[e3DMode] INTEGER NULL,
[eDualviewMode] INTEGER NULL
);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(0,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(1,1,1);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(2,2,2);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(3,13,13);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(4,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(5,0,5);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(6,12,5);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(7,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(8,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(9,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(10,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(11,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(12,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(13,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(14,0,0);
INSERT INTO "tbl_DisplayModeRouterSetting_URSA6" VALUES(15,20,0);
DROP TABLE IF EXISTS "tbl_ThreeDVideoRouterSetting_URSA9";
CREATE TABLE [tbl_ThreeDVideoRouterSetting_URSA9] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(0,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(1,1,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(2,2,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(3,3,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(4,4,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(5,5,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(6,6,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(7,7,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(8,8,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(9,9,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(10,0,0,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(11,11,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(12,12,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(13,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA9" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DTo2DVideoRouterSetting_URSA9";
CREATE TABLE [tbl_3DTo2DVideoRouterSetting_URSA9] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(0,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(1,1,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(2,2,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(3,3,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(4,4,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(5,5,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(6,6,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(7,7,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(8,8,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(9,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(10,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(11,11,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(12,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(13,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA9" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K3DVideoRouterSetting_URSA9";
CREATE TABLE [tbl_4K2K3DVideoRouterSetting_URSA9] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(1,1,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(2,2,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(3,3,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(4,4,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(5,5,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(6,6,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(7,7,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(8,8,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(9,9,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(10,0,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(11,11,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(12,12,2,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA9" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K60Hz3DVideoRouterSetting_URSA9";
CREATE TABLE [tbl_4K2K60Hz3DVideoRouterSetting_URSA9] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(1,1,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(2,2,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(3,3,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(4,4,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(5,5,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(6,6,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(7,7,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(8,8,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(9,9,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(10,0,0,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(11,11,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(12,12,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA9" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_ThreeDVideoRouterSetting_URSA11";
CREATE TABLE [tbl_ThreeDVideoRouterSetting_URSA11] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(0,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(1,1,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(2,2,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(3,3,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(4,4,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(5,5,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(6,6,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(7,7,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(8,8,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(9,9,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(10,0,0,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(11,11,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(12,12,2,2,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(13,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_URSA11" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DTo2DVideoRouterSetting_URSA11";
CREATE TABLE [tbl_3DTo2DVideoRouterSetting_URSA11] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(0,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(1,1,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(2,2,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(3,3,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(4,4,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(5,5,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(6,6,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(7,7,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(8,8,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(9,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(10,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(11,11,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(12,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(13,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_URSA11" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K3DVideoRouterSetting_URSA11";
CREATE TABLE [tbl_4K2K3DVideoRouterSetting_URSA11] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(1,1,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(2,2,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(3,3,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(4,4,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(5,5,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(6,6,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(7,7,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(8,8,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(9,9,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(10,0,0,2,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(11,11,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(12,12,6,6,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_URSA11" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K60Hz3DVideoRouterSetting_URSA11";
CREATE TABLE [tbl_4K2K60Hz3DVideoRouterSetting_URSA11] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(1,1,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(2,2,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(3,3,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(4,4,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(5,5,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(6,6,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(7,7,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(8,8,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(9,9,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(10,0,0,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(11,11,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(12,12,2,2,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_URSA11" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_ThreeDVideoRouterSetting_CSOT";
CREATE TABLE [tbl_ThreeDVideoRouterSetting_CSOT] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(0,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(1,1,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(2,2,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(3,3,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(4,4,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(5,5,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(6,6,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(7,7,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(8,8,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(9,9,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(10,2,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(11,11,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(12,12,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(13,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DTo2DVideoRouterSetting_CSOT";
CREATE TABLE [tbl_3DTo2DVideoRouterSetting_CSOT] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(0,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(1,1,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(2,2,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(3,3,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(4,4,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(5,5,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(6,6,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(7,7,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(8,8,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(9,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(10,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(11,11,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(12,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(13,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K3DVideoRouterSetting_CSOT";
CREATE TABLE [tbl_4K2K3DVideoRouterSetting_CSOT] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(1,1,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(2,2,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(3,3,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(4,4,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(5,5,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(6,6,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(7,7,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(8,8,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(9,9,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(10,2,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(11,11,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(12,12,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K60Hz3DVideoRouterSetting_CSOT";
CREATE TABLE [tbl_4K2K60Hz3DVideoRouterSetting_CSOT] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(1,1,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(2,2,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(3,3,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(4,4,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(5,5,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(6,6,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(7,7,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(8,8,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(9,9,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(10,2,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(11,11,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(12,12,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_ThreeDVideoRouterSetting_NOVA";
CREATE TABLE [tbl_ThreeDVideoRouterSetting_NOVA] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(0,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(1,1,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(2,2,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(3,3,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(4,4,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(5,5,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(6,6,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(7,7,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(8,8,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(9,9,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(10,0,0,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(11,11,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(12,12,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(13,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_NOVA" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DTo2DVideoRouterSetting_NOVA";
CREATE TABLE [tbl_3DTo2DVideoRouterSetting_NOVA] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(0,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(1,1,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(2,2,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(3,3,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(4,4,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(5,5,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(6,6,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(7,7,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(8,8,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(9,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(10,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(11,11,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(12,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(13,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_NOVA" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K3DVideoRouterSetting_NOVA";
CREATE TABLE [tbl_4K2K3DVideoRouterSetting_NOVA] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(1,1,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(2,2,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(3,3,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(4,4,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(5,5,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(6,6,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(7,7,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(8,8,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(9,9,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(10,0,0,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(11,11,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(12,12,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_NOVA" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K60Hz3DVideoRouterSetting_NOVA";
CREATE TABLE [tbl_4K2K60Hz3DVideoRouterSetting_NOVA] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(1,1,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(2,2,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(3,3,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(4,4,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(5,5,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(6,6,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(7,7,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(8,8,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(9,9,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(10,0,0,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(11,11,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(12,12,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_NOVA" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_ThreeDVideoRouterSetting_CSOT_4K540";
CREATE TABLE [tbl_ThreeDVideoRouterSetting_CSOT_4K540] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(0,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(1,1,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(2,2,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(3,3,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(4,4,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(5,5,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(6,6,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(7,7,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(8,8,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(9,9,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(10,2,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(11,11,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(12,12,6,0,6);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(13,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_CSOT_4K540" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DTo2DVideoRouterSetting_CSOT_4K540";
CREATE TABLE [tbl_3DTo2DVideoRouterSetting_CSOT_4K540] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(0,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(1,1,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(2,2,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(3,3,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(4,4,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(5,5,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(6,6,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(7,7,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(8,8,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(9,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(10,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(11,11,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(12,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(13,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_CSOT_4K540" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K3DVideoRouterSetting_CSOT_4K540";
CREATE TABLE [tbl_4K2K3DVideoRouterSetting_CSOT_4K540] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(1,1,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(2,2,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(3,3,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(4,4,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(5,5,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(6,6,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(7,7,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(8,8,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(9,9,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(10,2,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(11,11,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(12,12,6,0,6);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_CSOT_4K540" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540";
CREATE TABLE [tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(1,1,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(2,2,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(3,3,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(4,4,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(5,5,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(6,6,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(7,7,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(8,8,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(9,9,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(10,2,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(11,11,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(12,12,6,0,6);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_CSOT_4K540" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_ThreeDVideoRouterSetting_ROCKET2";
CREATE TABLE [tbl_ThreeDVideoRouterSetting_ROCKET2] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(0,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(1,1,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(2,2,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(3,3,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(4,4,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(5,5,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(6,6,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(7,7,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(8,8,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(9,9,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(10,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(11,11,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(12,12,2,0,2);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(13,0,0,0,0);
INSERT INTO "tbl_ThreeDVideoRouterSetting_ROCKET2" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_3DTo2DVideoRouterSetting_ROCKET2";
CREATE TABLE [tbl_3DTo2DVideoRouterSetting_ROCKET2] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(0,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(1,1,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(2,2,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(3,3,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(4,4,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(5,5,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(6,6,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(7,7,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(8,8,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(9,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(10,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(11,11,13,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(12,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(13,0,0,0,0);
INSERT INTO "tbl_3DTo2DVideoRouterSetting_ROCKET2" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K3DVideoRouterSetting_ROCKET2";
CREATE TABLE [tbl_4K2K3DVideoRouterSetting_ROCKET2] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(1,1,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(2,2,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(3,3,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(4,4,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(5,5,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(6,6,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(7,7,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(8,8,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(9,9,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(10,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(11,11,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(12,12,2,0,2);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K3DVideoRouterSetting_ROCKET2" VALUES(14,0,0,0,0);
DROP TABLE IF EXISTS "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2";
CREATE TABLE [tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2] (
[e3DType] INTEGER  PRIMARY KEY NOT NULL,
[eScalerInput] INTEGER  NULL,
[eScalerOutput] INTEGER  NULL,
[eUrsaInput] INTEGER  NULL,
[ePanelInput] INTEGER  NULL
);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(0,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(1,1,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(2,2,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(3,3,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(4,4,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(5,5,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(6,6,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(7,7,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(8,8,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(9,9,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(10,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(11,11,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(12,12,2,0,2);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(13,0,0,0,0);
INSERT INTO "tbl_4K2K60Hz3DVideoRouterSetting_ROCKET2" VALUES(14,0,0,0,0);