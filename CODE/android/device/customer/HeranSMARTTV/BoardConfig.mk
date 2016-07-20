# This file includes all definitions that apply to all
# arbutus devices, include inherit-devices.
#
# This cleanly combines a set of device-specific configuration.
LOCAL_PATH := device/customer/$(HERAN_DEVICE)

include $(LOCAL_PATH)/BoardConfigCommon.mk

# Change on this file should go below this line
# -----------------------------------------------------------------------

TARGET_BOOTLOADER_BOARD_NAME := arbutus

# mmc part
# :  type name      (size)
#1.  misc           (512K)
#2.  recovery       (19M)
#3.  boot           (15M)
#4.  tee            (6M)
#5.  rtpm           (256K)
#6.  dtb            (1M)
#7.  system         (1024M)
#8.  userdata       (4G)
#9.  cache          (200M)
#10. tvservice      (160M)
#11. tvconfig       (10M)
#12. tvdatabase     (8M)
#13. tvcustomer     (16M)
#16. vrsdcard       (1.6G)
BOARD_RECOVERYIMAGE_PARTITION_SIZE   := 0x01300000
BOARD_BOOTIMAGE_PARTITION_SIZE       := 0x00F00000
BOARD_TEEIMAGE_PARTITION_SIZE        := 0x00600000
BOARD_RTPMIMAGE_PARTITION_SIZE       := 0x00040000
BOARD_DTBIMAGE_PARTITION_SIZE        := 0x00100000
BOARD_SYSTEMIMAGE_PARTITION_SIZE     := 0x40000000
BOARD_USERDATAIMAGE_PARTITION_SIZE   := 0x100000000
BOARD_CACHEIMAGE_PARTITION_SIZE      := 0x0C800000
BOARD_TVSERVICEIMAGE_PARTITION_SIZE  := 0x0A000000
BOARD_TVCONFIGIMAGE_PARTITION_SIZE   := 0x00A00000
BOARD_TVDATABASEIMAGE_PARTITION_SIZE := 0x00800000
BOARD_TVCUSTOMERIMAGE_PARTITION_SIZE := 0x01000000
BOARD_VRSDCARD_PARTITION_SIZE        := 0x60000000

BOARD_FLASH_BLOCK_SIZE := 512

# In eng/others-build, dexpreopt disable, but we can enable in here.

WITH_DEXPREOPT := true
#DONT_DEXPREOPT_PREBUILTS := true

# OTA config
OTA_INCREMENTAL_FROM := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/$(TARGET_PRODUCT)-target_files-v2.5.28.57358.zip
OTA_MBOOT_IMAGE := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/mboot.bin
OTA_MBOOT_EMMC_ROM_IMAGE := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/rom_emmc_boot.bin
OTA_TEE_IMAGE := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/tee.bin
OTA_TEE_AES_IMAGE := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/tee.aes
OTA_TEE_SECUREINFO_IMAGE := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/secure_info_tee.bin
OTA_NUTTX_IMAGE := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/nuttx_config.bin
OTA_RTPM_IMAGE := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/RT_PM.bin
OTA_DTB_IMAGE := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/dtb.bin
OTA_TV_IMAGE_PATH := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/
OTA_TV_IMAGE_LIST := tvservice,tvcustomer,tvdatabase
OTA_TVCONFIG_IMAGE_PATH := $(TOP)/../images/lollipop/$(TARGET_DEVICE)/
OTA_TVCONFIG_IMAGE_LIST :=
OTA_TVCONFIG_DELETE_LIST :=
