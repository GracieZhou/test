#<MStar Software>
#******************************************************************************
# MStar Software
# Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
# All software, firmware and related documentation herein ("MStar Software") are
# intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
# law, including, but not limited to, copyright law and international treaties.
# Any use, modification, reproduction, retransmission, or republication of all
# or part of MStar Software is expressly prohibited, unless prior written
# permission has been granted by MStar.
#
# By accessing, browsing and/or using MStar Software, you acknowledge that you
# have read, understood, and agree, to be bound by below terms ("Terms") and to
# comply with all applicable laws and regulations:
#
# 1. MStar shall retain any and all right, ownership and interest to MStar
#    Software and any modification/derivatives thereof.
#    No right, ownership, or interest to MStar Software and any
#    modification/derivatives thereof is transferred to you under Terms.
#
# 2. You understand that MStar Software might include, incorporate or be
#    supplied together with third party's software and the use of MStar
#    Software may require additional licenses from third parties.
#    Therefore, you hereby agree it is your sole responsibility to separately
#    obtain any and all third party right and license necessary for your use of
#    such third party's software.
#
# 3. MStar Software and any modification/derivatives thereof shall be deemed as
#    MStar's confidential information and you agree to keep MStar's
#    confidential information in strictest confidence and not disclose to any
#    third party.
#
# 4. MStar Software is provided on an "AS IS" basis without warranties of any
#    kind. Any warranties are hereby expressly disclaimed by MStar, including
#    without limitation, any warranties of merchantability, non-infringement of
#    intellectual property rights, fitness for a particular purpose, error free
#    and in conformity with any international standard.  You agree to waive any
#    claim against MStar for any loss, damage, cost or expense that you may
#    incur related to your use of MStar Software.
#    In no event shall MStar be liable for any direct, indirect, incidental or
#    consequential damages, including without limitation, lost of profit or
#    revenues, lost or damage of data, and unauthorized system use.
#    You agree that this Section 4 shall still apply without being affected
#    even if MStar Software has been modified by MStar in accordance with your
#    request or instruction for your use, except otherwise agreed by both
#    parties in writing.
#
# 5. If requested, MStar may from time to time provide technical supports or
#    services in relation with MStar Software to you for your use of
#    MStar Software in conjunction with your or your customer's product
#    ("Services").
#    You understand and agree that, except otherwise agreed by both parties in
#    writing, Services are provided on an "AS IS" basis and the warranty
#    disclaimer set forth in Section 4 above shall apply.
#
# 6. Nothing contained herein shall be construed as by implication, estoppels
#    or otherwise:
#    (a) conferring any license or right to use MStar name, trademark, service
#        mark, symbol or any other identification;
#    (b) obligating MStar or any of its affiliates to furnish any person,
#        including without limitation, you and your customers, any assistance
#        of any kind whatsoever, or any information; or
#    (c) conferring any license or right under any intellectual property right.
#
# 7. These terms shall be governed by and construed in accordance with the laws
#    of Taiwan, R.O.C., excluding its conflict of law rules.
#    Any and all dispute arising out hereof or related hereto shall be finally
#    settled by arbitration referred to the Chinese Arbitration Association,
#    Taipei in accordance with the ROC Arbitration Law and the Arbitration
#    Rules of the Association by three (3) arbitrators appointed in accordance
#    with the said Rules.
#    The place of arbitration shall be in Taipei, Taiwan and the language shall
#    be English.
#    The arbitration award shall be final and binding to both parties.
#
#******************************************************************************
#<MStar Software>

ifeq ($(BUILD_WITH_SUPERNOVA),true)
SUPERNOVA_BUILD_TOP := $(ANDROID_BUILD_TOP)/vendor/mstar/supernova
SUPERNOVA_PROJECT_TOP := $(SUPERNOVA_BUILD_TOP)/projects

ifeq "aosp_guava" "$(TARGET_PRODUCT)"
ifneq ($(filter 21 22, $(PLATFORM_SDK_VERSION)),)
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monaco_068D_ROM_EMMC_TVOS_TEE.sh
else
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monaco_068D_ROM_EMMC_TVOS_TEE.sh
endif
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.monaco/images/ext4

else ifneq ($(filter aosp_pitaya aosp_pitayaoas aosp_pitaya32_64 aosp_pitaya32,$(TARGET_PRODUCT)),)
    SUPERNOVA_BUILDSETTING := buildsettings/build_Muji_072B_ROM_EMMC_TVOS_DTMB_URSA6_CMA.sh
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.muji/images/ext4/

else ifeq "aosp_grapefruit" "$(TARGET_PRODUCT)"
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monaco_068D_ES_ASIA_ROM_EMMC_TVOS_4K2K.sh
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.monaco/images/ext4

else ifeq "aosp_pomelo" "$(TARGET_PRODUCT)"
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monaco_068D_ROM_EMMC_TVOS_4K2K_RAPTORS.sh
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.monaco/images/ext4
	
#eostek patch begin
# comment : set other product's default build settings the same as aosp_arbutus 
else ifeq "I_6A_638" "$(TARGET_PRODUCT)"
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monet_082B_ROM_EMMC_TVOS_ES_ASIA_NTSC_ATV_URSA6_CMA_TEE.sh
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.monet/images/ext4
	
else ifeq "Heran_arbutus" "$(TARGET_PRODUCT)"
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monet_082B_ROM_EMMC_TVOS_ES_ASIA_NTSC_ATV_URSA6_CMA_TEE.sh
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.monet/images/ext4
#eostek patch end

else ifeq "aosp_arbutus" "$(TARGET_PRODUCT)"
ifneq ($(filter 21 22, $(PLATFORM_SDK_VERSION)),)
ifeq ($(BUILD_WITH_TEE),true)
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monet_082B_ROM_EMMC_TVOS_DTMB_URSA6_CMA_TEE.sh
else
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monet_082B_ROM_EMMC_TVOS_DTMB_URSA6_CMA.sh
endif
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.monet/images/ext4

else
ifeq ($(BUILD_WITH_TEE),true)
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monet_087B_ROM_EMMC_TVOS_DTMB_URSA6_TEE_KitKat.sh
else
    SUPERNOVA_BUILDSETTING := buildsettings/build_Monet_081B_ROM_EMMC_TVOS_DTMB_URSA6.sh
endif
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.monet/images/ext4
endif

else ifeq "aosp_strawberry" "$(TARGET_PRODUCT)"
ifeq ($(BUILD_WITH_TEE),true)
    SUPERNOVA_BUILDSETTING := buildsettings/build_Manhattan_079B_ROM_EMMC_TVOS_DTMB_URSA6_TEE.sh
else
    SUPERNOVA_BUILDSETTING := buildsettings/build_Manhattan_079B_ROM_EMMC_TVOS_DTMB.sh
endif
    SUPERNOVA_TARGET_OUT := $(SUPERNOVA_BUILD_TOP)/target/dvb.manhattan/images/ext4

endif

.PHONY: FORCE

supernova: FORCE
	cd $(SUPERNOVA_PROJECT_TOP);source $(SUPERNOVA_BUILDSETTING);make all;make image_all
	@cp $(SUPERNOVA_TARGET_OUT)/tvservice.img $(PRODUCT_OUT)
	@cp $(SUPERNOVA_TARGET_OUT)/tvconfig.img $(PRODUCT_OUT)
	@cp $(SUPERNOVA_TARGET_OUT)/tvdatabase.img $(PRODUCT_OUT)
	@cp $(SUPERNOVA_TARGET_OUT)/tvcustomer.img $(PRODUCT_OUT)
	@if [ -f $(SUPERNOVA_TARGET_OUT)/RT_PM.bin ]; then cp $(SUPERNOVA_TARGET_OUT)/RT_PM.bin $(PRODUCT_OUT); fi
	@if [ -f $(SUPERNOVA_TARGET_OUT)/tee.aes ]; then cp $(SUPERNOVA_TARGET_OUT)/tee.aes $(PRODUCT_OUT); fi
	@if [ -f $(SUPERNOVA_TARGET_OUT)/tee.bin ]; then cp $(SUPERNOVA_TARGET_OUT)/tee.bin $(PRODUCT_OUT); fi
	@if [ -f $(SUPERNOVA_TARGET_OUT)/nuttx_config.bin ]; then cp $(SUPERNOVA_TARGET_OUT)/nuttx_config.bin $(PRODUCT_OUT); fi
	@if [ -f $(SUPERNOVA_TARGET_OUT)/secure_info_tee.bin ]; then cp $(SUPERNOVA_TARGET_OUT)/secure_info_tee.bin $(PRODUCT_OUT); fi
	@cp -rf $(SUPERNOVA_TARGET_OUT)/../../tmp_image/* $(PRODUCT_OUT)
	@cp -rf $(SUPERNOVA_PROJECT_TOP)/symbols $(PRODUCT_OUT)

supernova_clean:
	cd $(SUPERNOVA_PROJECT_TOP);source $(SUPERNOVA_BUILDSETTING);make clean
	@rm -f $(PRODUCT_OUT)/tvservice.img
	@rm -f $(PRODUCT_OUT)/tvconfig.img
	@rm -f $(PRODUCT_OUT)/tvdatabase.img
	@rm -f $(PRODUCT_OUT)/tvcustomer.img
	@rm -f $(PRODUCT_OUT)/tee.aes
	@rm -f $(PRODUCT_OUT)/tee.bin
	@rm -f $(PRODUCT_OUT)/nuttx_config.bin
	@rm -f $(PRODUCT_OUT)/secure_info_tee.bin
	@rm -rf $(PRODUCT_OUT)/tvservice
	@rm -rf $(PRODUCT_OUT)/tvconfig
	@rm -rf $(PRODUCT_OUT)/tvdatabase
	@rm -rf $(PRODUCT_OUT)/tvcustomer
	@rm -rf $(PRODUCT_OUT)/symbols/applications
	@rm -rf $(PRODUCT_OUT)/symbols/lib
	@rm -rf $(PRODUCT_OUT)/symbols/mslib
	@rm -rf $(PRODUCT_OUT)/symbols/vendor

droidcore: supernova

else
supernova_clean:
	@rm -f $(PRODUCT_OUT)/tvservice.img
	@rm -f $(PRODUCT_OUT)/tvconfig.img
	@rm -f $(PRODUCT_OUT)/tvdatabase.img
	@rm -f $(PRODUCT_OUT)/tvcustomer.img
	@rm -f $(PRODUCT_OUT)/tee.aes
	@rm -f $(PRODUCT_OUT)/tee.bin
	@rm -f $(PRODUCT_OUT)/nuttx_config.bin
	@rm -f $(PRODUCT_OUT)/secure_info_tee.bin
	@rm -rf $(PRODUCT_OUT)/tvservice
	@rm -rf $(PRODUCT_OUT)/tvconfig
	@rm -rf $(PRODUCT_OUT)/tvdatabase
	@rm -rf $(PRODUCT_OUT)/tvcustomer
	@rm -rf $(PRODUCT_OUT)/symbols/applications
	@rm -rf $(PRODUCT_OUT)/symbols/lib
	@rm -rf $(PRODUCT_OUT)/symbols/mslib
	@rm -rf $(PRODUCT_OUT)/symbols/vendor
endif

# ==============================================================================
include $(call all-subdir-makefiles)
