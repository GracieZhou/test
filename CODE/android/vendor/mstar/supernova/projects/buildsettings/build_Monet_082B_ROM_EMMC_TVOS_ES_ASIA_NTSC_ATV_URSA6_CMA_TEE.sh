#!/bin/bash

#//============================================================================
#//env.cfg
PREPARE_BUILD_FILE=./prepare_build_env.sh
source ${PREPARE_BUILD_FILE} $BASH_SOURCE
echo $BASH_SOURCE > ./buildsettings/buildsetting
ENV_CFG_FILE=../out/buildenv/env.cfg
sed -i 's/EXT4=.*/EXT4=true/g' ${ENV_CFG_FILE}
sed -i 's/TARGET_CPU=.*/TARGET_CPU=arm/g' ${ENV_CFG_FILE}
sed -i 's/TOOLCHAIN=.*/TOOLCHAIN=arm-gnueabi/g' ${ENV_CFG_FILE}
sed -i 's/CHIP=.*/CHIP=monet/g' ${ENV_CFG_FILE}
sed -i 's/BOARD=.*/BOARD=082B_TVOS_ES_ASIA_NTSC_ATV/g' ${ENV_CFG_FILE}
sed -i 's/PROJ_MODE=.*/PROJ_MODE=dvb/g' ${ENV_CFG_FILE}
sed -i 's/CUSTOMER_MMAP=.*/CUSTOMER_MMAP=y/g' ${ENV_CFG_FILE}
sed -i 's/MMAP_TYPE=.*/MMAP_TYPE=SN_MMAP_ANDROID_ES_ASIA_512_512_CMA.h/g' ${ENV_CFG_FILE}
sed -i 's/LINK_TYPE=.*/LINK_TYPE=static/g' ${ENV_CFG_FILE}
sed -i 's/CUSTOMER_PQ=.*/CUSTOMER_PQ=y/g' ${ENV_CFG_FILE}
sed -i 's/CUSTOMER_PQ_Folder_Name=.*/CUSTOMER_PQ_Folder_Name=monet_sz/g' ${ENV_CFG_FILE}
source ${ENV_CFG_FILE}

#//============================================================================
#//buildenv.mk

#//============================================================================
#//config.mk

SW_CFG_FILE=../out/buildenv/config/sw_cfg/tvsystem/dtv/dvb.mk

sed -i 's/HBBTV_ENABLE =.*/HBBTV_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/DVBT_SYSTEM_ENABLE =.*/DVBT_SYSTEM_ENABLE = 1/g' ${SW_CFG_FILE}
sed -i 's/DVBC_SYSTEM_ENABLE =.*/DVBC_SYSTEM_ENABLE = 1/g' ${SW_CFG_FILE}
sed -i 's/DVBS_SYSTEM_ENABLE =.*/DVBS_SYSTEM_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/DTMB_SYSTEM_ENABLE =.*/DTMB_SYSTEM_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/MHEG5_ENABLE =.*/MHEG5_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/CI_ENABLE =.*/CI_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/CI_PLUS_ENABLE =.*/CI_PLUS_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/CIPLUS_PVR_ENABLE =.*/CIPLUS_PVR_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/ALSA_ENABLE =.*/ALSA_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/USB_SUPER_SPEED_ENABLE =.*/USB_SUPER_SPEED_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/MULTIPLE_SERVICE_NAME_ENABLE =.*/MULTIPLE_SERVICE_NAME_ENABLE = 1/g' ${SW_CFG_FILE}

#===================================================================#
SW_ATV_CFG_FILE=../out/buildenv/config/sw_cfg/tvsystem/atv/ntsc.mk
sed -i 's/ESASIA_NTSC_SYSTEM_ENABLE =.*/ESASIA_NTSC_SYSTEM_ENABLE = 1/g' ${SW_ATV_CFG_FILE}
sed -i 's/NTSC_CC_ENABLE =.*/NTSC_CC_ENABLE = 1/g' ${SW_ATV_CFG_FILE}
sed -i 's/KARAOKE_ENABLE =.*/KARAOKE_ENABLE = 1/g' ${SW_ATV_CFG_FILE}


#==================== board::Customer_Module.ini ====================#
MODULE_INI_FILE=../out/buildenv/board/INI/module/Customer_Module.ini

sed -i 's/F_BACKEND_ENABLE_4K2K =.*/F_BACKEND_ENABLE_4K2K = 0\;/g' ${MODULE_INI_FILE}
sed -i 's/F_BACKEND_ENABLE_4K2K_FRC =.*/F_BACKEND_ENABLE_4K2K_FRC = 0\;/g' ${MODULE_INI_FILE}
sed -i 's/F_BACKEND_ENABLE_4K2K_NAPOLI =.*/F_BACKEND_ENABLE_4K2K_NAPOLI = 0\;/g' ${MODULE_INI_FILE}
sed -i 's/F_BACKEND_ENABLE_OSDC =.*/F_BACKEND_ENABLE_OSDC = 0\;/g' ${MODULE_INI_FILE}
sed -i 's/F_CEC_ARC_HDMI_PORT =.*/F_CEC_ARC_HDMI_PORT = 24\;/g' ${MODULE_INI_FILE}
sed -i 's/F_PANEL_ENABLE_VB1 =.*/F_PANEL_ENABLE_VB1 = 1\;/g' ${MODULE_INI_FILE}
sed -i 's/M_SYSTEM_BASIC_DACOUT_PATH =.*/M_SYSTEM_BASIC_DACOUT_PATH = \"\/config\/panel\"\;/g' ${MODULE_INI_FILE}
sed -i 's/F_TRANSFORM =.*/F_TRANSFORM = \"monet_ursa6\"\;/g' ${MODULE_INI_FILE}
sed -i 's/M_SYSTEM_BASIC_SOC_MUTE =.*/M_SYSTEM_BASIC_SOC_MUTE = 0\;/g' ${MODULE_INI_FILE}
sed -i 's/F_FIXED_DS =.*/F_FIXED_DS = 0\;/g' ${MODULE_INI_FILE}

#//============================================================================
#//pcb.mk
PCB_MK_FILE=../out/buildenv/config/chips/${CHIP}/pcb.mk

sed -i 's/FPU_ENABLE =.*/FPU_ENABLE = 1/g' ${PCB_MK_FILE}

#//============================================================================
#//pcb_config.mk
PCB_CONFIG_MK_FILE=../out/buildenv/config/chips/${CHIP}/MST082B_10AJQ_TVOS_ES_ASIA_NTSC_ATV/pcb_config.mk

sed -i 's/EXT4.*=.*/EXT4=true/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/BOOT_FROM_ROM.*=.*/BOOT_FROM_ROM = 1/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/ENABLE_BOOT_FROM_MASK_ROM.*=.*/ENABLE_BOOT_FROM_MASK_ROM = 1/g' ${PCB_CONFIG_MK_FILE}
# DISABLE STR
sed -i 's/STR_ENABLE.*=.*/STR_ENABLE = 0/g' ${PCB_CONFIG_MK_FILE}

sed -i 's/CHINA_ATV_ENABLE =.*/CHINA_ATV_ENABLE = 0/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/CHINA_ENABLE =.*/CHINA_ENABLE = 0/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/SECURE_BOOTING_ENABLE =.*/SECURE_BOOTING_ENABLE = 1/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/MHL_ENABLE =.*/MHL_ENABLE = 1/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/MHL_ENABLE_BY_STANDBY_MODE =.*/MHL_ENABLE_BY_STANDBY_MODE = 0/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/ENABLE_VB1 =.*/ENABLE_VB1 = 0/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/ENABLE_AUDIO_HANDLER =.*/ENABLE_AUDIO_HANDLER = 1/g' ${PCB_CONFIG_MK_FILE}

sed -i 's/ENABLE_BACKEND.*=.*/ENABLE_BACKEND = 1/g' ${PCB_CONFIG_MK_FILE}
sed -i 's/OFL_DET.*=.*/OFL_DET = 1/g' ${PCB_CONFIG_MK_FILE}

#//============================================================================
#//DFBRC.ini
DFBRC_FILE=../out/buildenv/board/${CHIP}/MST082B_10AJQ_TVOS_ES_ASIA_NTSC_ATV/SN_DFBRC_Monet_MST082B_10AJQ.ini
sed -i 's/DFBRC_MST_GOP_COUNTS .*/DFBRC_MST_GOP_COUNTS = 1;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_MST_GOP_AVAILABLE_0 .*/DFBRC_MST_GOP_AVAILABLE_0 =1;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_MST_GOP_AVAILABLE_2 .*/DFBRC_MST_GOP_AVAILABLE_2 =1;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_MUXCOUNTS .*/DFBRC_MUXCOUNTS =5;/g' ${DFBRC_FILE}

sed -i 's/DFBRC_MUX0_GOPINDEX .*/DFBRC_MUX0_GOPINDEX = 1;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_MUX1_GOPINDEX .*/DFBRC_MUX1_GOPINDEX = 3;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_MUX2_GOPINDEX .*/DFBRC_MUX2_GOPINDEX = 0;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_MUX3_GOPINDEX .*/DFBRC_MUX3_GOPINDEX = 2;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_MUX4_GOPINDEX .*/DFBRC_MUX4_GOPINDEX = 4;/g' ${DFBRC_FILE}

sed -i 's/DFBRC_LAYERCOUNTS .*/DFBRC_LAYERCOUNTS =5;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_LAYER0_GOPINDEX .*/DFBRC_LAYER0_GOPINDEX = 1;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_LAYER1_GOPINDEX .*/DFBRC_LAYER1_GOPINDEX = 0;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_LAYER2_GOPINDEX .*/DFBRC_LAYER2_GOPINDEX = 2;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_LAYER3_GOPINDEX .*/DFBRC_LAYER3_GOPINDEX = 3;/g' ${DFBRC_FILE}
sed -i 's/DFBRC_LAYER4_GOPINDEX .*/DFBRC_LAYER4_GOPINDEX = 4;/g' ${DFBRC_FILE}

#//============================================================================
#//default_setting.mk
DEFAULT_SETTING_FILE=../out/buildenv/target/default_setting.mk
sed -i 's/STORAGE_TYPE =.*/STORAGE_TYPE = emmc/g' ${DEFAULT_SETTING_FILE}

SYS_INI=../out/buildenv/board/${CHIP}/MST082B_10AJQ_TVOS_ES_ASIA_NTSC_ATV/sys.ini
sed -i 's#gModelName =.*#gModelName ="/config/model/Customer_1.ini";#' ${SYS_INI}

#//============================================================================
#//device_option.mk
DEVICE_OPTION_MK_FILE=../out/buildenv/config/devices/device_option.mk
sed -i 's/=.*/= 0/g' ${DEVICE_OPTION_MK_FILE}
#Tuner
sed -i 's/TUNER_NXP_TDA18275 =.*/TUNER_NXP_TDA18275 = 1/g' ${DEVICE_OPTION_MK_FILE}
sed -i 's/TUNER_AV2012 =.*/TUNER_AV2012 = 1/g' ${DEVICE_OPTION_MK_FILE}

#//============================================================================
#//Tuner
sed -i 's/TUNER_MXL661 =.*/TUNER_MXL661 = 1/g' ${DEVICE_OPTION_MK_FILE}

#//============================================================================
#//Demodulator
sed -i 's/DEMOD_monet =.*/DEMOD_monet = 1/g' ${DEVICE_OPTION_MK_FILE}
sed -i 's/DEMOD_EXTEND_msb1240 =.*/DEMOD_EXTEND_msb1240 = 1/g' ${DEVICE_OPTION_MK_FILE}
sed -i 's/DEMOD_EXTEND_atbm885x =.*/DEMOD_EXTEND_atbm885x = 0/g' ${DEVICE_OPTION_MK_FILE}
sed -i 's/DEMOD_EXTEND2_example =.*/DEMOD_EXTEND2_example = 1/g' ${DEVICE_OPTION_MK_FILE}
#audio
sed -i 's/AUDIO_AMP_MSH9010 =.*/AUDIO_AMP_MSH9010 = 1/g' ${DEVICE_OPTION_MK_FILE}

#//============================================================================
#//Dish
sed -i 's/DISH_DUMMY =.*/DISH_DUMMY = 1/g' ${DEVICE_OPTION_MK_FILE}

#//============================================================================
#//URSA
sed -i 's/URSA_TYPE =.*/URSA_TYPE = 6/g' ${DEVICE_OPTION_MK_FILE}

#//============================================================================
#//monet_device.mk
MONET_DEVICE_MK_FILE=../out/buildenv/config/devices/monet_device.mk
sed -i 's/LOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ENABLE =.*/LOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ENABLE = 1/g' ${MONET_DEVICE_MK_FILE}

#=================== Customer_1.ini======================#
CUSTOMER_FILE=../out/buildenv/board/${CHIP}/MST082B_10AJQ_TVOS_ES_ASIA_NTSC_ATV/model/Customer_1.ini
sed -i 's/m_pPanelName .*=.*/m_pPanelName = \"\/config\/panel\/UD_VB1_8LANE.ini\"\;/g' ${CUSTOMER_FILE}
sed -i 's/m_p4K1KPanelName .*=.*/#m_p4K1KPanelName = \"\"\;/g' ${CUSTOMER_FILE}
sed -i 's/m_p4K2KPanelName .*=.*/#m_p4K2KPanelName = \"\"\;/g' ${CUSTOMER_FILE}
sed -i 's/UrsaSelect .*=.*/UrsaSelect = 6\;/g' ${CUSTOMER_FILE}
sed -i 's/m_pDBName .*=.*/m_pDBName     = \"\/config\/DBPath\/DBPath_URSA6.ini\"\;/g' ${CUSTOMER_FILE}
sed -i 's/TunerSelectNo1 =.*/TunerSelectNo1 = \"TUNER_MXL661\"\;/g' ${CUSTOMER_FILE}
sed -i 's/TunerSelectNo2 =.*/TunerSelectNo2 = \"TUNER_AV2012\"\;/g' ${CUSTOMER_FILE}
sed -i 's/pHDCPFileName .*=.*/pHDCPFileName = \"\/Customer\/hdcp_key.bin\"\;/g' ${CUSTOMER_FILE}

#=================== UD_VB1_8LANE.ini======================#
PANEL_FILE=../out/buildenv/board/INI/panel/UD_VB1_8LANE.ini
sed -i 's/osdWidth.*=.*/osdWidth = 1920/g' ${PANEL_FILE}
sed -i 's/osdHeight.*=.*/osdHeight = 1080/g' ${PANEL_FILE}
#===================================================================#
APP_CFG_FILE=../out/buildenv/config/sw_cfg/app/app.mk
sed -i 's/DRMAGENT_ENABLE =.*/DRMAGENT_ENABLE = 0/g' ${APP_CFG_FILE}
sed -i 's/INTEL_WIDI_ENABLE =.*/INTEL_WIDI_ENABLE = 0/g' ${APP_CFG_FILE}

#===================================================================#
PLATFORM_MK_FILE=../out/buildenv/config/sw_cfg/platform/platform.mk
sed -i 's/MSTAR_TVOS =.*/MSTAR_TVOS = 1/g' ${PLATFORM_MK_FILE}
sed -i 's/BINDER_64BIT =.*/BINDER_64BIT = 1/g' ${PLATFORM_MK_FILE}

#===================================================================#
COMMON_FEATURE_MK_FILE=../out/buildenv/config/sw_cfg/common_feature/common_feature.mk
sed -i 's/AUTO_TEST =.*/AUTO_TEST = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/PRELINK_ENABLE =.*/PRELINK_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/EPG_ENABLE =.*/EPG_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/EPG_EED_ENABLE =.*/EPG_EED_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/PVR_ENABLE =.*/PVR_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/PWS_ENABLE =.*/PWS_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/ENABLE_DIVX_PLUS =.*/ENABLE_DIVX_PLUS = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/^OAD_ENABLE =.*/OAD_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/RELEASE_BINDER_TEST =.*/RELEASE_BINDER_TEST = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/PIP_ENABLE =.*/PIP_ENABLE = 0/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/MSPI_ENABLE =.*/MSPI_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/TRAVELING_ENABLE =.*/TRAVELING_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/STEREO_3D_ENABLE =.*/STEREO_3D_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/WOL_ENABLE =.*/WOL_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/PREVIEW_MODE_ENABLE =.*/PREVIEW_MODE_ENABLE = 0/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE =.*/CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/SEAMLESS_ZOOMING_ENABLE =.*/SEAMLESS_ZOOMING_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/LOCAL_DIMMING =.*/LOCAL_DIMMING = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/ENABLE_DIVX =.*/ENABLE_DIVX = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/TTX_ENABLE =.*/TTX_ENABLE = 0/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/TTX_USING_VECTOR_FONT =.*/TTX_USING_VECTOR_FONT = 0/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/SECURE_ENABLE =.*/SECURE_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/ACTIVE_STANDBY_MODE_ENABLE =.*/ACTIVE_STANDBY_MODE_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/SUPPORT_EURO_HDTV =.*/SUPPORT_EURO_HDTV = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/AD_SWITCH_ENABLE =.*/AD_SWITCH_ENABLE = 0/g' ${SW_CFG_FILE}
sed -i 's/^VE_ENABLE =.*/VE_ENABLE = 0/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/ADVERT_BOOTING_ENABLE =.*/ADVERT_BOOTING_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/HDMI_HDCP22_ENABLE =.*/HDMI_HDCP22_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/ENABLE_MBACKTRACE =.*/ENABLE_MBACKTRACE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/VP9_ENABLE =.*/VP9_ENABLE = 0/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/ENABLE_CMA =.*/ENABLE_CMA = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/XVYCC_ENABLE =.*/XVYCC_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/TEE_ENABLE =.*/TEE_ENABLE = 1/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/TVOS_AN_VERSION_MAJOR =.*/TVOS_AN_VERSION_MAJOR = 5/g' ${COMMON_FEATURE_MK_FILE}
sed -i 's/TVOS_AN_VERSION_MINOR =.*/TVOS_AN_VERSION_MINOR = 1/g' ${COMMON_FEATURE_MK_FILE}
