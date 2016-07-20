#!/bin/bash

source ./build/envsetup.sh

function get-configs()
{
    if [ -z "$TARGET_PRODUCT" ]; then
        lunch
    fi

    TARGET_DEVICE=$(get_build_var TARGET_DEVICE)
    PRODUCT_BRAND=$(tr '[A-Z]' '[a-z]' <<<"$(get_build_var PRODUCT_BRAND)")

    echo ""
    echo -e "\033[41;37mTarget product: $TARGET_PRODUCT\033[0m"
    echo -e "\033[41;37mTarget device: $TARGET_DEVICE\033[0m"
    echo -e "\033[41;37mProduct brand: $PRODUCT_BRAND\033[0m"
    TARGET_DEVICE_DIR=$ANDROID_BUILD_TOP/device/$PRODUCT_BRAND/$TARGET_DEVICE
    RELEASE_OUT=$ANDROID_BUILD_TOP/../images/lollipop/$TARGET_DEVICE
    PRODUCT_OUT=$ANDROID_BUILD_TOP/out/target/product/$TARGET_DEVICE
    AUTO_UPDATE_SCRIPT=$RELEASE_OUT/auto_update.txt

    SET_PARTITION_SCRIPT=$RELEASE_OUT/scripts/set_partition

    BOARDCONFIG_MK=$TARGET_DEVICE_DIR/BoardConfig.mk
    if [ ! -e "$BOARDCONFIG_MK" ]; then
        echo "The $BOARDCONFIG_MK is NOT exist in your system."
        exit 1
    fi

    local TARGET_USERIMAGES_USE_EXT2=$(get_build_var TARGET_USERIMAGES_USE_EXT2)
    local TARGET_USERIMAGES_USE_EXT3=$(get_build_var TARGET_USERIMAGES_USE_EXT3)
    local TARGET_USERIMAGES_USE_EXT4=$(get_build_var TARGET_USERIMAGES_USE_EXT4)
    if [ "$TARGET_USERIMAGES_USE_EXT2" == "true" ]; then
        INTERNAL_USERIMAGES_USE_EXT=true
        INTERNAL_USERIMAGES_FILE_SYSTEM_TYPE=ext2
    elif [ "$TARGET_USERIMAGES_USE_EXT3" == "true" ]; then
        INTERNAL_USERIMAGES_USE_EXT=true
        INTERNAL_USERIMAGES_FILE_SYSTEM_TYPE=ext3
    elif [ "$TARGET_USERIMAGES_USE_EXT4" == "true" ]; then
        INTERNAL_USERIMAGES_USE_EXT=true
        INTERNAL_USERIMAGES_FILE_SYSTEM_TYPE=ext4
    fi

    if [ "$TARGET_USERIMAGES_SPARSE_EXT_DISABLED" == "true" ]; then
        INTERNAL_USERIMAGES_SPARSE_EXT_FLAG=-s
    fi

    BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE=$(get_build_var BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE)
    if [ -z "$BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE" ]; then
        echo "No cache file system type."
        exit 1
    fi

    BOARD_SYSTEMBACKUPIMAGE=$(get_build_var BOARD_SYSTEMBACKUPIMAGE)
    if [ "$BOARD_SYSTEMBACKUPIMAGE" == "true" ]; then
        BOARD_SYSTEMBACKUPIMAGE_FILE_SYSTEM_TYPE=$(get_build_var BOARD_SYSTEMBACKUPIMAGE_FILE_SYSTEM_TYPE)
        if [ -z "$BOARD_SYSTEMBACKUPIMAGE_FILE_SYSTEM_TYPE" ]; then
            echo "No systembackup file system type."
            exit 1
        fi
    fi

    BUILD_WITH_SECURE_BOOT=$(get_build_var BUILD_WITH_SECURE_BOOT)

    BOARD_MBOOTIMAGE=$(get_build_var BOARD_MBOOTIMAGE)
    BOARD_CERTIFICATIONIMAGE=$(get_build_var BOARD_CERTIFICATIONIMAGE)
    BOARD_TEEIMAGE=$(get_build_var BOARD_TEEIMAGE)
    BOARD_RTPMIMAGE=$(get_build_var BOARD_RTPMIMAGE)
    BOARD_DTBIMAGE=$(get_build_var BOARD_DTBIMAGE)
	# EosTek Patch Begin
    BOARD_NO_SDCARD=$(get_build_var BOARD_NO_SDCARD)
	# EosTek Patch End
    BOARD_FRCIMAGE=$(get_build_var BOARD_FRCIMAGE)

    BOARD_RECOVERYIMAGE_PARTITION_SIZE=$(get_build_var BOARD_RECOVERYIMAGE_PARTITION_SIZE)
    BOARD_BOOTIMAGE_PARTITION_SIZE=$(get_build_var BOARD_BOOTIMAGE_PARTITION_SIZE)
    BOARD_SYSTEMIMAGE_PARTITION_SIZE=$(get_build_var BOARD_SYSTEMIMAGE_PARTITION_SIZE)
    BOARD_USERDATAIMAGE_PARTITION_SIZE=$(get_build_var BOARD_USERDATAIMAGE_PARTITION_SIZE)
    BOARD_CACHEIMAGE_PARTITION_SIZE=$(get_build_var BOARD_CACHEIMAGE_PARTITION_SIZE)
    BOARD_TVSERVICEIMAGE_PARTITION_SIZE=$(get_build_var BOARD_TVSERVICEIMAGE_PARTITION_SIZE)
    BOARD_TVCONFIGIMAGE_PARTITION_SIZE=$(get_build_var BOARD_TVCONFIGIMAGE_PARTITION_SIZE)
    BOARD_TVDATABASEIMAGE_PARTITION_SIZE=$(get_build_var BOARD_TVDATABASEIMAGE_PARTITION_SIZE)
    BOARD_TVCUSTOMERIMAGE_PARTITION_SIZE=$(get_build_var BOARD_TVCUSTOMERIMAGE_PARTITION_SIZE)
    [ "$BOARD_SYSTEMBACKUPIMAGE" == "true" ] && BOARD_SYSTEMBACKUPIMAGE_PARTITION_SIZE=$(get_build_var BOARD_SYSTEMBACKUPIMAGE_PARTITION_SIZE)
    [ "$BOARD_CERTIFICATIONIMAGE" == "true" ] && BOARD_CERTIFICATEIMAGE_PARTITION_SIZE=$(get_build_var BOARD_CERTIFICATEIMAGE_PARTITION_SIZE)
    [ "$BOARD_TEEIMAGE" == "true" ] && BOARD_TEEIMAGE_PARTITION_SIZE=$(get_build_var BOARD_TEEIMAGE_PARTITION_SIZE)
    [ "$BOARD_RTPMIMAGE" == "true" ] && BOARD_RTPMIMAGE_PARTITION_SIZE=$(get_build_var BOARD_RTPMIMAGE_PARTITION_SIZE)
    [ "$BOARD_DTBIMAGE" == "true" ] && BOARD_DTBIMAGE_PARTITION_SIZE=$(get_build_var BOARD_DTBIMAGE_PARTITION_SIZE)
	# EosTek Patch Begin
    [ "$BOARD_NO_SDCARD" == "false" ] && BOARD_VRSDCARD_PARTITION_SIZE=$(get_build_var BOARD_VRSDCARD_PARTITION_SIZE)
	# EosTek Patch End
    [ "$BOARD_FRCIMAGE" == "true" ] && BOARD_FRCIMAGE_PARTITION_SIZE=$(get_build_var BOARD_FRCIMAGE_PARTITION_SIZE)

    if [ -z "$BOARD_RECOVERYIMAGE_PARTITION_SIZE" ]; then
        echo "No recovery partition size."
        exit 1
    fi
    if [ -z "$BOARD_BOOTIMAGE_PARTITION_SIZE" ]; then
        echo "No boot partition size."
        exit 1
    fi
    if [ -z "$BOARD_SYSTEMIMAGE_PARTITION_SIZE" ]; then
        echo "No systen partition size."
        exit 1
    fi
    if [ -z "$BOARD_USERDATAIMAGE_PARTITION_SIZE" ]; then
        echo "No userdata partition size."
        exit 1
    fi
    if [ -z "$BOARD_CACHEIMAGE_PARTITION_SIZE" ]; then
        echo "No cache partition size."
        exit 1
    fi
    if [ -z "$BOARD_TVSERVICEIMAGE_PARTITION_SIZE" ]; then
        echo "No tvservice partition size."
        exit 1
    fi
    if [ -z "$BOARD_TVCONFIGIMAGE_PARTITION_SIZE" ]; then
        echo "No tvconfig partition size."
        exit 1
    fi
    if [ -z "$BOARD_TVDATABASEIMAGE_PARTITION_SIZE" ]; then
        echo "No tvdatabase partition size."
        exit 1
    fi
    if [ -z "$BOARD_TVCUSTOMERIMAGE_PARTITION_SIZE" ]; then
        echo "No tvcustomer partition size."
        exit 1
    fi
    if [ "$BOARD_SYSTEMBACKUPIMAGE" == "true" ]; then
        if [ -z "$BOARD_SYSTEMBACKUPIMAGE_PARTITION_SIZE" ]; then
            echo "No systembackup partition size."
            exit 1
        fi
    fi
    if [ "$BOARD_CERTIFICATIONIMAGE" == "true" ]; then
        if [ -z "$BOARD_CERTIFICATEIMAGE_PARTITION_SIZE" ]; then
            echo "No certification partition size."
            exit 1
        fi
    fi
    if [ "$BOARD_TEEIMAGE" == "true" ]; then
        if [ -z "$BOARD_TEEIMAGE_PARTITION_SIZE" ]; then
            echo "No tee partition size."
            exit 1
        fi
    fi
    if [ "$BOARD_RTPMIMAGE" == "true" ]; then
        if [ -z "$BOARD_RTPMIMAGE_PARTITION_SIZE" ]; then
            echo "No RTPM partition size."
            exit 1
        fi
    fi
    if [ "$BOARD_DTBIMAGE" == "true" ]; then
        if [ -z "$BOARD_DTBIMAGE_PARTITION_SIZE" ]; then
            echo "No dtb partition size."
            exit 1
        fi
    fi
	# EosTek Patch Begin
    if [ "$BOARD_NO_SDCARD" == "false" ]; then
        if [ -z "$BOARD_VRSDCARD_PARTITION_SIZE" ]; then
            echo "No VRSDCARD partition size."
            exit 1
        fi
    fi
	# EosTek Patch End
	
    if [ "$BOARD_FRCIMAGE" == "true" ]; then
        if [ -z "$BOARD_FRCIMAGE_PARTITION_SIZE" ]; then
            echo "No frc partition size."
            exit 1
        fi
    fi

    BOARD_DTB_BASE=$(get_build_var BOARD_DTB_BASE)
    #if [ -z "$BOARD_DTB_BASE" ]; then
    #    echo "No dtb base address."
    #fi

    BOARD_BOOTM_BASE=$(get_build_var BOARD_BOOTM_BASE)
    if [ -z "$BOARD_BOOTM_BASE" ]; then
        echo "No bootm base address."
    fi

    BOARD_KERNEL_BASE=$(get_build_var BOARD_KERNEL_BASE)
    if [ -z "$BOARD_KERNEL_BASE" ]; then
        echo "No kernel base address."
    fi

    BOARD_TFTP_ADDRESS=$(get_build_var BOARD_TFTP_ADDRESS)
    if [ -z "$BOARD_TFTP_ADDRESS" ]; then
        echo "No tftp address."
    fi
}

function generate-userimage-prop-dictionary()
{
    rm -rf $1

    if [ -n "$INTERNAL_USERIMAGES_FILE_SYSTEM_TYPE" ]; then
        echo "fs_type=$INTERNAL_USERIMAGES_FILE_SYSTEM_TYPE" >> $1
    fi

    if [ -n "$BOARD_SYSTEMIMAGE_PARTITION_SIZE" ]; then
        echo "system_size=$BOARD_SYSTEMIMAGE_PARTITION_SIZE" >> $1
    fi

    if [ -n "$BOARD_USERDATAIMAGE_PARTITION_SIZE" ]; then
        echo "userdata_size=$BOARD_USERDATAIMAGE_PARTITION_SIZE" >> $1
    fi

    if [ -n "$BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE" ]; then
        echo "cache_fs_type=$BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE" >> $1
    fi

    if [ -n "$BOARD_CACHEIMAGE_PARTITION_SIZE" ]; then
        echo "cache_size=$BOARD_CACHEIMAGE_PARTITION_SIZE" >> $1
    fi

    if [ -n "$BOARD_SYSTEMBACKUPIMAGE_FILE_SYSTEM_TYPE" ]; then
        echo "systembackup_fs_type=$BOARD_SYSTEMBACKUPIMAGE_FILE_SYSTEM_TYPE" >> $1
    fi

    if [ -n "$BOARD_SYSTEMBACKUPIMAGE_PARTITION_SIZE" ]; then
        echo "systembackup_size=$BOARD_SYSTEMBACKUPIMAGE_PARTITION_SIZE" >> $1
    fi

    if [ -n "$INTERNAL_USERIMAGES_SPARSE_EXT_FLAG" ]; then
        echo "extfs_sparse_flag=$INTERNAL_USERIMAGES_SPARSE_EXT_FLAG" >> $1
    fi

    if [ -n "$mkyaffs2_extra_flags" ]; then
        echo "mkyaffs2_extra_flags=$mkyaffs2_extra_flags" >> $1
    fi

    echo "selinux_fc=$PRODUCT_OUT/root/file_contexts" >> $1
}

# Generate auto_update/set_partition/[[cis script
function gernerate-script-start()
{
    local SET_CONFIG_SCRIPT=$RELEASE_OUT/scripts/set_config
    local CIS_SCRIPT=$RELEASE_OUT/scripts/[[cis

    # Generate auto_update script
    rm -rf $AUTO_UPDATE_SCRIPT
    echo "mstar scripts/set_partition" >> $AUTO_UPDATE_SCRIPT

    # Generate set_partition script
    rm -rf $SET_PARTITION_SCRIPT
    echo "mmc slc 0 1" >> $SET_PARTITION_SCRIPT
    echo "mmc rmgpt" >> $SET_PARTITION_SCRIPT
    echo "mmc create misc 0x00080000" >> $SET_PARTITION_SCRIPT
}

# Generate auto_update/set_partition/set_config script
function gernerate-script-end()
{
    echo "mstar scripts/set_config" >> $AUTO_UPDATE_SCRIPT
    echo "reset" >> $AUTO_UPDATE_SCRIPT
    # Generate set_config script
    # TODO...

    # Generate set_partition script
}

# Generate [[mboot/[[recovery/[[boot/[[tee/[[RTPM script
function gernerate-raw-script()
{
    local MBOOT_SCRIPT=$RELEASE_OUT/scripts/[[mboot
    local RECOVERY_SCRIPT=$RELEASE_OUT/scripts/[[recovery
    local BOOT_SCRIPT=$RELEASE_OUT/scripts/[[boot
    local TEE_SCRIPT=$RELEASE_OUT/scripts/[[tee
    local RTPM_SCRIPT=$RELEASE_OUT/scripts/[[RT_PM
    local DTB_SCRIPT=$RELEASE_OUT/scripts/[[dtb
    local FRC_SCRIPT=$RELEASE_OUT/scripts/[[frc

    # Update auto_update/set_partition script
    [ "$BOARD_MBOOTIMAGE" != "0" ] && echo "mstar scripts/[[mboot" >> $AUTO_UPDATE_SCRIPT
    echo "mstar scripts/[[recovery" >> $AUTO_UPDATE_SCRIPT
    echo "mstar scripts/[[boot" >> $AUTO_UPDATE_SCRIPT
    [ "$BOARD_TEEIMAGE" == "true" ] && echo "mstar scripts/[[tee" >> $AUTO_UPDATE_SCRIPT
    [ "$BOARD_RTPMIMAGE" == "true" ] && echo "mstar scripts/[[RT_PM" >> $AUTO_UPDATE_SCRIPT
    [ "$BOARD_DTBIMAGE" == "true" ] && echo "mstar scripts/[[dtb" >> $AUTO_UPDATE_SCRIPT
    [ "$BOARD_FRCIMAGE" == "true" ] && echo "mstar scripts/[[frc" >> $AUTO_UPDATE_SCRIPT

    echo "mmc create recovery $BOARD_RECOVERYIMAGE_PARTITION_SIZE" >> $SET_PARTITION_SCRIPT
    echo "mmc create boot $BOARD_BOOTIMAGE_PARTITION_SIZE" >> $SET_PARTITION_SCRIPT
    [ "$BOARD_TEEIMAGE" == "true" ] && echo "mmc create tee $BOARD_TEEIMAGE_PARTITION_SIZE" >> $SET_PARTITION_SCRIPT
    [ "$BOARD_RTPMIMAGE" == "true" ] && echo "mmc create RTPM $BOARD_RTPMIMAGE_PARTITION_SIZE" >> $SET_PARTITION_SCRIPT
    [ "$BOARD_DTBIMAGE" == "true" ] && echo "mmc create dtb $BOARD_DTBIMAGE_PARTITION_SIZE" >> $SET_PARTITION_SCRIPT
    [ "$BOARD_FRCIMAGE" == "true" ] && echo "mmc create frc $BOARD_FRCIMAGE_PARTITION_SIZE" >> $SET_PARTITION_SCRIPT

    rm -rf $MBOOT_SCRIPT
    rm -rf $RECOVERY_SCRIPT
    rm -rf $BOOT_SCRIPT
    rm -rf $TEE_SCRIPT
    rm -rf $RTPM_SCRIPT
    rm -rf $DTB_SCRIPT
    rm -rf $FRC_SCRIPT
    #1: mboot in SPI ; 2: mboot in EMMC ; 3: mboot in EMMC ,SPI(pm)
    if [ "$BOARD_MBOOTIMAGE" == "1" ]; then
        echo "tftp $BOARD_TFTP_ADDRESS mboot.bin" >> $MBOOT_SCRIPT
        echo "spi wrc $BOARD_TFTP_ADDRESS 0 \$(filesize)" >> $MBOOT_SCRIPT
    elif [ "$BOARD_MBOOTIMAGE" == "2" ]; then
        echo "tftp $BOARD_TFTP_ADDRESS rom_emmc_boot.bin" >> $MBOOT_SCRIPT
        echo "mmc write.boot 1 $BOARD_TFTP_ADDRESS 0 \$(filesize)" >> $MBOOT_SCRIPT
        echo "tftp $BOARD_TFTP_ADDRESS mboot.bin" >> $MBOOT_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS MBOOT \$(filesize)" >> $MBOOT_SCRIPT
    elif [ "$BOARD_MBOOTIMAGE" == "3" ]; then
        echo "tftp $BOARD_TFTP_ADDRESS rom_emmc_boot.bin" >> $MBOOT_SCRIPT
        echo "mmc write.boot 1 $BOARD_TFTP_ADDRESS 0 \$(filesize)" >> $MBOOT_SCRIPT
        echo "tftp $BOARD_TFTP_ADDRESS mboot.bin" >> $MBOOT_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS MBOOT \$(filesize)" >> $MBOOT_SCRIPT
        echo "tftp $BOARD_TFTP_ADDRESS PM51.bin" >> $MBOOT_SCRIPT
        echo "spi wrc $BOARD_TFTP_ADDRESS 0 \$(filesize)" >> $MBOOT_SCRIPT
    fi

    if [ "$BUILD_WITH_SECURE_BOOT" == "true" ]; then
        echo "tftp $BOARD_TFTP_ADDRESS recovery.img.aes" >> $RECOVERY_SCRIPT
        echo "mmc erase.p misc" >> $RECOVERY_SCRIPT
        echo "mmc erase.p recovery" >> $RECOVERY_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS recovery \$(filesize) 1" >> $RECOVERY_SCRIPT
        echo "tftp $BOARD_TFTP_ADDRESS secure_info_recovery.bin" >> $RECOVERY_SCRIPT
        echo "store_secure_info recoverySign $BOARD_TFTP_ADDRESS" >> $RECOVERY_SCRIPT
        if [ "$BOARD_DTBIMAGE" == "true" ]; then
            echo "setenv recoverycmd mmc read.p $BOARD_DTB_BASE dtb $BOARD_DTBIMAGE_PARTITION_SIZE\; mmc read.p $BOARD_BOOTM_BASE recovery $BOARD_RECOVERYIMAGE_PARTITION_SIZE\; authenticateAN $BOARD_BOOTM_BASE\; bootm $BOARD_BOOTM_BASE" >> $RECOVERY_SCRIPT
        else
            echo "setenv recoverycmd mmc read.p $BOARD_BOOTM_BASE recovery $BOARD_RECOVERYIMAGE_PARTITION_SIZE\; authenticateAN $BOARD_BOOTM_BASE\; bootm $BOARD_BOOTM_BASE" >> $RECOVERY_SCRIPT
        fi
        echo "saveenv" >> $RECOVERY_SCRIPT
    else
        echo "tftp $BOARD_TFTP_ADDRESS recovery.img" >> $RECOVERY_SCRIPT
        echo "mmc erase.p misc" >> $RECOVERY_SCRIPT
        echo "mmc erase.p recovery" >> $RECOVERY_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS recovery \$(filesize) 1" >> $RECOVERY_SCRIPT
        if [ "$BOARD_DTBIMAGE" == "true" ]; then
            echo "setenv recoverycmd mmc read.p $BOARD_DTB_BASE dtb $BOARD_DTBIMAGE_PARTITION_SIZE\; mmc read.p $BOARD_BOOTM_BASE recovery $BOARD_RECOVERYIMAGE_PARTITION_SIZE\; bootm $BOARD_BOOTM_BASE" >> $RECOVERY_SCRIPT
        else
            echo "setenv recoverycmd mmc read.p $BOARD_BOOTM_BASE recovery $BOARD_RECOVERYIMAGE_PARTITION_SIZE\; bootm $BOARD_BOOTM_BASE" >> $RECOVERY_SCRIPT
        fi
        echo "saveenv" >> $RECOVERY_SCRIPT
    fi

    if [ "$BUILD_WITH_SECURE_BOOT" == "true" ]; then
        echo "tftp $BOARD_TFTP_ADDRESS boot.img.aes" >> $BOOT_SCRIPT
        echo "mmc erase.p boot" >> $BOOT_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS boot \$(filesize) 1" >> $BOOT_SCRIPT
        echo "tftp $BOARD_TFTP_ADDRESS secure_info_boot.bin" >> $BOOT_SCRIPT
        echo "store_secure_info bootSign $BOARD_TFTP_ADDRESS" >> $BOOT_SCRIPT
        echo "setenv LOAD_KERNEL mmc read.p $BOARD_BOOTM_BASE boot $BOARD_BOOTIMAGE_PARTITION_SIZE\; authenticateAN $BOARD_BOOTM_BASE" >> $BOOT_SCRIPT
        echo "setenv BOOT_KERNEL bootm $BOARD_BOOTM_BASE" >> $BOOT_SCRIPT
        echo "saveenv" >> $BOOT_SCRIPT
    else
        echo "tftp $BOARD_TFTP_ADDRESS boot.img" >> $BOOT_SCRIPT
        echo "mmc erase.p boot" >> $BOOT_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS boot \$(filesize) 1" >> $BOOT_SCRIPT
        if [ "$BOARD_TEEIMAGE" == "true" ]; then
            echo "setenv LOAD_KERNEL mmc read.p $BOARD_BOOTM_BASE boot $BOARD_BOOTIMAGE_PARTITION_SIZE" >> $BOOT_SCRIPT
            echo "setenv BOOT_KERNEL bootm $BOARD_BOOTM_BASE" >> $BOOT_SCRIPT
        else
            if [ "$BOARD_DTBIMAGE" == "true" ]; then
                echo "setenv bootcmd mmc read.p $BOARD_DTB_BASE dtb $BOARD_DTBIMAGE_PARTITION_SIZE\; mmc read.p $BOARD_BOOTM_BASE boot $BOARD_BOOTIMAGE_PARTITION_SIZE\; bootm $BOARD_BOOTM_BASE" >> $BOOT_SCRIPT
            else
                echo "setenv bootcmd mmc read.p $BOARD_BOOTM_BASE boot $BOARD_BOOTIMAGE_PARTITION_SIZE\; bootm $BOARD_BOOTM_BASE" >> $BOOT_SCRIPT
            fi
        fi
        echo "saveenv" >> $BOOT_SCRIPT
    fi

    if [ "$BOARD_TEEIMAGE" == "true" ]; then
        echo -e "\033[31mGernerate raw tee script...\033[0m"
        if [ "$BUILD_WITH_SECURE_BOOT" == "true" ]; then
            echo "tftp $BOARD_TFTP_ADDRESS tee.aes" >> $TEE_SCRIPT
            echo "mmc erase.part tee" >> $TEE_SCRIPT
            echo "mmc write.p $BOARD_TFTP_ADDRESS tee \$(filesize)" >> $TEE_SCRIPT
            echo "setenv LOAD_NUTTX mmc read.p 0x5EC70000 tee 0x600000" >> $TEE_SCRIPT
            echo "setenv BOOT_NUTTX bootNuttx 0x1EC70000" >> $TEE_SCRIPT
            echo "saveenv" >> $TEE_SCRIPT
            echo "tftp $BOARD_TFTP_ADDRESS secure_info_tee.bin" >> $TEE_SCRIPT
            echo "store_secure_info teeSign $BOARD_TFTP_ADDRESS" >> $TEE_SCRIPT
            echo "tftp $BOARD_TFTP_ADDRESS nuttx_config.bin" >> $TEE_SCRIPT
            echo "store_nuttx_config NuttxConfig $BOARD_TFTP_ADDRESS" >> $TEE_SCRIPT
        else
            echo "tftp $BOARD_TFTP_ADDRESS tee.bin" >> $TEE_SCRIPT
            echo "mmc erase.p tee" >> $TEE_SCRIPT
            echo "mmc write.p $BOARD_TFTP_ADDRESS tee \$(filesize) 1" >> $TEE_SCRIPT
            echo "setenv LOAD_NUTTX mmc read.p 0x5EC70000 tee 0x600000" >> $TEE_SCRIPT
            echo "setenv BOOT_NUTTX bootNuttx 0x1EC70000" >> $TEE_SCRIPT
            echo "saveenv" >> $TEE_SCRIPT
            echo "tftp $BOARD_TFTP_ADDRESS nuttx_config.bin" >> $TEE_SCRIPT
            echo "store_nuttx_config NuttxConfig $BOARD_TFTP_ADDRESS" >> $TEE_SCRIPT
        fi
    fi

    if [ "$BOARD_RTPMIMAGE" == "true" ]; then
        echo -e "\033[31mGernerate RTPM script...\033[0m"
        echo "tftp $BOARD_TFTP_ADDRESS RT_PM.bin" >> $RTPM_SCRIPT
        echo "mmc erase.p RTPM" >> $RTPM_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS RTPM \$(filesize) 1" >> $RTPM_SCRIPT
    fi

    if [ "$BOARD_DTBIMAGE" == "true" ]; then
        echo -e "\033[31mGernerate dtb script...\033[0m"
        echo "tftp $BOARD_TFTP_ADDRESS dtb.bin" >> $DTB_SCRIPT
        echo "mmc erase.p dtb" >> $DTB_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS dtb \$(filesize) 1" >> $DTB_SCRIPT
    fi
    if [ "$BOARD_FRCIMAGE" == "true" ]; then
        echo -e "\033[31mGernerate frc script...\033[0m"
        echo "tftp $BOARD_TFTP_ADDRESS FRC-R2.bin" >> $FRC_SCRIPT
        echo "mmc erase.p frc" >> $FRC_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS frc \$(filesize)" >> $FRC_SCRIPT
    fi
}

# Generate [[system/[[userdate/[[cache/... script
function gernerate-fs-script()
{
    local PARTITION_NAME=$1
    local PARTITION_SIZE=$2
    local PARTITION_LZO=$3
    local PARTITION_SCRIPT=$RELEASE_OUT/scripts/[[$PARTITION_NAME

    # Update auto_update/set_partition script
    echo "mstar scripts/[[$PARTITION_NAME" >> $AUTO_UPDATE_SCRIPT

    echo "mmc create $PARTITION_NAME $PARTITION_SIZE" >> $SET_PARTITION_SCRIPT

    rm -rf $PARTITION_SCRIPT
    if [ "$PARTITION_LZO" == "true" ]; then
        echo "tftp $BOARD_TFTP_ADDRESS $PARTITION_NAME.img.lzo" >> $PARTITION_SCRIPT
        echo "mmc erase.p $PARTITION_NAME" >> $PARTITION_SCRIPT
        echo "mmc unlzo $BOARD_TFTP_ADDRESS \$(filesize) $PARTITION_NAME 1" >> $PARTITION_SCRIPT
    else
        echo "tftp $BOARD_TFTP_ADDRESS $PARTITION_NAME.img" >> $PARTITION_SCRIPT
        echo "mmc erase.p $PARTITION_NAME" >> $PARTITION_SCRIPT
        echo "mmc write.p $BOARD_TFTP_ADDRESS $PARTITION_NAME \$(filesize) 1" >> $PARTITION_SCRIPT
    fi
}

# Generate system/userdate/cache/... image
function gernerate-fs-image()
{
    local PARTITION_NAME=$1
    local PARTITION_SIZE=$2
    local PARTITION_LZO=$3

    if [ "$PARTITION_NAME" == "userdata" ]; then
        local MOUNT_POINT=data
    else
        local MOUNT_POINT=$PARTITION_NAME
    fi
# EosTek Patch Begin
    if [ "$PARTITION_NAME" == "vrsdcard" ]; then
    	echo "make_ext4fs $PRODUCT_OUT/$PARTITION_NAME.img" PARTITION_SIZE=$PARTITION_SIZE
    	make_ext4fs -l $PARTITION_SIZE -a $MOUNT_POINT $PRODUCT_OUT/$PARTITION_NAME.img $TARGET_OUT
        return
    fi
# EosTek Patch End
    local TARGET_OUT=$PRODUCT_OUT/$MOUNT_POINT
    if [ ! -d "$TARGET_OUT" ]; then
        echo "No $TARGET_OUT directory to gernerate image."
        return
    fi

    if [ "$PARTITION_NAME" == "system" -o "$PARTITION_NAME" == "userdata" -o "$PARTITION_NAME" == "cache" -o "$PARTITION_NAME" == "systembackup" ]; then
        if [  $PARTITION_NAME == "system" ]; then
            local IMAGE_INFO=$PRODUCT_OUT/obj/PACKAGING/systemimage_intermediates/system_image_info.txt
        else
            local IMAGE_INFO=$PRODUCT_OUT/obj/PACKAGING/"$PARTITION_NAME"_intermediates/"$PARTITION_NAME"_image_info.txt
        fi

        generate-userimage-prop-dictionary $IMAGE_INFO
        ./build/tools/releasetools/build_image.py $TARGET_OUT $IMAGE_INFO $PRODUCT_OUT/$PARTITION_NAME.img
    else
        make_ext4fs -S $PRODUCT_OUT/root/file_contexts -l $PARTITION_SIZE -a $MOUNT_POINT $PRODUCT_OUT/$PARTITION_NAME.img $TARGET_OUT
    fi
}

# Compress system/userdate/cache/... image
function compress-image()
{
    local PARTITION_NAME=$1
    local PARTITION_LZO=$2

    if [ "$PARTITION_LZO" == "false" ]; then
        return
    fi

    if [ ! -f "$PRODUCT_OUT/$PARTITION_NAME.img" ]; then
        echo "No $PRODUCT_OUT/$PARTITION_NAME.img to compress."
        return
    fi

    lzop -f -o $PRODUCT_OUT/$PARTITION_NAME.img.lzo $PRODUCT_OUT/$PARTITION_NAME.img
}

# Copy system/userdate/cache/... image
function copy-image()
{
    local PARTITION_NAME=$1
    local PARTITION_LZO=$2

    if [ ! -f "$PRODUCT_OUT/$PARTITION_NAME.img" ]; then
        echo "No $PRODUCT_OUT/$PARTITION_NAME.img to copy."
        return
    fi

    cp -f $PRODUCT_OUT/$PARTITION_NAME.img$([ $PARTITION_LZO == "true" ] && echo ".lzo") $RELEASE_OUT
}



function make-raw-partition()
{
    if [ "$BUILD_WITH_SECURE_BOOT" == "true" ]; then
        echo -e "\033[31mMake raw secure recovery/boot partition...\033[0m"
    else
        echo -e "\033[31mMake raw recovery/boot partition...\033[0m"
    fi

    # Generate image
    mkbootfs $PRODUCT_OUT/recovery/root | minigzip > $PRODUCT_OUT/ramdisk-recovery.img
    mkimage -A arm -O linux -T multi -C none -a $BOARD_KERNEL_BASE -e $BOARD_KERNEL_BASE -n 'MStar-linux(recovery)' -d $PRODUCT_OUT/kernel:$PRODUCT_OUT/ramdisk-recovery.img $PRODUCT_OUT/recovery.img
    mkbootfs $PRODUCT_OUT/root | minigzip > $PRODUCT_OUT/ramdisk.img
    mkimage -A arm -O linux -T multi -C none -a $BOARD_KERNEL_BASE -e $BOARD_KERNEL_BASE -n 'MStar-linux' -d $PRODUCT_OUT/kernel:$PRODUCT_OUT/ramdisk.img $PRODUCT_OUT/boot.img
    if [ "$BUILD_WITH_SECURE_BOOT" == "true" ]; then
        local ALIGNMENT=$(get_build_var ALIGNMENT)
        local SUBSECUREINFOGEN=$(get_build_var SUBSECUREINFOGEN)
        local AESCRYPT2=$(get_build_var AESCRYPT2)
        local HOST_OUT_EXECUTABLES=$(get_build_var HOST_OUT_EXECUTABLES)
        local SECURE_BOOT_AES_KEY=$TARGET_DEVICE_DIR/security/AESboot.bin
        local SECURE_BOOT_RSA_PRIVATE_KEY=$TARGET_DEVICE_DIR/security/RSAimage_priv.txt
        local SECURE_BOOT_RSA_PUBLIC_KEY=$TARGET_DEVICE_DIR/security/RSAimage_pub.txt

        cp $PRODUCT_OUT/boot.img $PRODUCT_OUT/boot.img.tmp
        $ALIGNMENT $PRODUCT_OUT/boot.img.tmp
        $SUBSECUREINFOGEN $PRODUCT_OUT/secure_info_boot.bin $PRODUCT_OUT/boot.img.tmp $SECURE_BOOT_RSA_PRIVATE_KEY $SECURE_BOOT_RSA_PUBLIC_KEY 0  8 1 2097152 0 $HOST_OUT_EXECUTABLES
        $AESCRYPT2 0 $PRODUCT_OUT/boot.img.tmp $PRODUCT_OUT/boot.img.aes $SECURE_BOOT_AES_KEY
        rm $PRODUCT_OUT/boot.img.tmp

        cp $PRODUCT_OUT/recovery.img $PRODUCT_OUT/recovery.img.tmp
        $ALIGNMENT $PRODUCT_OUT/recovery.img.tmp
        $SUBSECUREINFOGEN $PRODUCT_OUT/secure_info_recovery.bin $PRODUCT_OUT/recovery.img.tmp $SECURE_BOOT_RSA_PRIVATE_KEY $SECURE_BOOT_RSA_PUBLIC_KEY 0  8 1 2097152 0 $HOST_OUT_EXECUTABLES
        $AESCRYPT2 0 $PRODUCT_OUT/recovery.img.tmp $PRODUCT_OUT/recovery.img.aes $SECURE_BOOT_AES_KEY
        rm $PRODUCT_OUT/recovery.img.tmp
    fi

    release-raw-partition
}

function release-raw-partition()
{
    if [ "$BUILD_WITH_SECURE_BOOT" == "true" ]; then
        echo -e "\033[31mRelease raw secure recovery/boot partition...\033[0m"
    else
        echo -e "\033[31mRelease raw recovery/boot partition...\033[0m"
    fi

    # Copy image
    #if [ "$BOARD_MBOOTIMAGE" == "1" ]; then
    #    cp -f $PRODUCT_OUT/mboot.bin $RELEASE_OUT
    #elif [ "$BOARD_MBOOTIMAGE" == "2" ]; then
    #    cp -f $PRODUCT_OUT/rom_emmc_boot.bin $RELEASE_OUT
    #    cp -f $PRODUCT_OUT/mboot.bin $RELEASE_OUT
    #elif [ "$BOARD_MBOOTIMAGE" == "3" ]; then
    #    cp -f $PRODUCT_OUT/rom_emmc_boot.bin $RELEASE_OUT
    #    cp -f $PRODUCT_OUT/mboot.bin $RELEASE_OUT
    #    cp -f $PRODUCT_OUT/PM51.bin $RELEASE_OUT
    #fi
    if [ "$BUILD_WITH_SECURE_BOOT" == "true" ]; then
        cp -f $PRODUCT_OUT/recovery.img.aes $RELEASE_OUT
        cp -f $PRODUCT_OUT/boot.img.aes $RELEASE_OUT
        cp -f $PRODUCT_OUT/secure_info_boot.bin $RELEASE_OUT
        cp -f $PRODUCT_OUT/secure_info_recovery.bin $RELEASE_OUT
    else
        cp -f $PRODUCT_OUT/recovery.img $RELEASE_OUT
        cp -f $PRODUCT_OUT/boot.img $RELEASE_OUT
    fi
    if [ "$BOARD_TEEIMAGE" == "true" ]; then
        if [ "$BUILD_WITH_SECURE_BOOT" == "true" ]; then
            cp -f $PRODUCT_OUT/tee.aes $RELEASE_OUT
            cp -f $PRODUCT_OUT/secure_info_tee.bin $RELEASE_OUT
        else
            cp -f $PRODUCT_OUT/tee.bin $RELEASE_OUT
        fi
        cp -f $PRODUCT_OUT/nuttx_config.bin $RELEASE_OUT
    fi
    #if [ "$BOARD_TEEIMAGE" == "true" ]; then
    #    cp -f $PRODUCT_OUT/RT_PM.bin $RELEASE_OUT
    #fi

    # Generate script
    gernerate-raw-script
}

#make-fs-partition $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO
function make-fs-partition()
{
    echo -e "\033[31mMake fs $1 partition...\033[0m"
    local PARTITION_NAME=$1
    local PARTITION_SIZE=$2
    local PARTITION_LZO=$3

    # Generate image
    gernerate-fs-image $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO

    release-fs-partition $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO
}

#release-fs-partition $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO [$PREBUILT]
function release-fs-partition()
{
    echo -e "\033[31mRelease fs $1 partition...\033[0m"
    local PARTITION_NAME=$1
    local PARTITION_SIZE=$2
    local PARTITION_LZO=$3
    local PREBUILT=$4

    if [ "$PREBUILT" != "true" ]; then
        # Compress image
        compress-image $PARTITION_NAME $PARTITION_LZO

        # Copy image
        copy-image $PARTITION_NAME $PARTITION_LZO
    fi

    # Generate script
    gernerate-fs-script $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO
}

#split-fs-partition $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO
function split-fs-partition()
{
    echo -e "\033[31mSplit fs $1 partition...\033[0m"
    local PARTITION_NAME=$1
    local PARTITION_SIZE=$2
    local PARTITION_LZO=$3
    # We only have about 200MB free memory for TFTP, and the compression ratio of lzo is > 75%.
    # Split size is 150MB better.
    local SPLIT_SIZE=157286400
    local PARTITION_SCRIPT=$RELEASE_OUT/scripts/[[$PARTITION_NAME
    local count=$((($(stat -c %s $PRODUCT_OUT/$PARTITION_NAME.img)-1)/$SPLIT_SIZE+1))

    if [ $count -eq 1 ]; then
        # Compress image
        compress-image $PARTITION_NAME $PARTITION_LZO
        # Copy image
        copy-image $PARTITION_NAME $PARTITION_LZO
        # Generate script
        gernerate-fs-script $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO
        return
    fi

    # Split image
    # this parameter 150m is related to SPLIT_SIZE, please modify both of them is you want to modify splited image size
    split -b 150m $PRODUCT_OUT/$PARTITION_NAME.img $PRODUCT_OUT/$PARTITION_NAME.img

    # Compress & Copy image
    local str="a b c d e f g h i j k l m n o p q r s t u v w x y z"
    local i=0
    for j in $str
    do
        for k in $str
        do
            if [ $i -ge $count ]; then
                break
            fi

            if [ "$PARTITION_LZO" == "true" ]; then
                lzop -f -o $PRODUCT_OUT/$PARTITION_NAME.img$j$k.lzo $PRODUCT_OUT/$PARTITION_NAME.img$j$k
            fi

            cp -f $PRODUCT_OUT/$PARTITION_NAME.img$j$k$([ $PARTITION_LZO == "true" ] && echo ".lzo") $RELEASE_OUT
            i=`expr $i + 1`
        done
    done

    # Generate script
    echo "mstar scripts/[[$PARTITION_NAME" >> $AUTO_UPDATE_SCRIPT

    echo "mmc create $PARTITION_NAME $PARTITION_SIZE" >> $SET_PARTITION_SCRIPT

    rm -rf $PARTITION_SCRIPT
    echo "mmc erase.p $PARTITION_NAME" >> $PARTITION_SCRIPT

    local offset=0
    local size=0
    i=0
    for j in $str
    do
        for k in $str
        do
            if [ $i -ge $count ]; then
                break
            fi

            offset=$(($offset+$size/512))
            if [ $(($PARTITION_SIZE-$i*$SPLIT_SIZE)) -gt $SPLIT_SIZE ]; then
                size=$SPLIT_SIZE
            else
                size=$(($PARTITION_SIZE-$i*$SPLIT_SIZE))
            fi

            offset_16=0x$(echo "obase=16; $offset" | bc)
            size_16=0x$(echo "obase=16; $size" | bc)

            if [ "$PARTITION_LZO" == "true" ]; then
                echo "tftp $BOARD_TFTP_ADDRESS $PARTITION_NAME.img$j$k.lzo" >> $PARTITION_SCRIPT
                if [ $i -eq 0 ]; then
                    echo "mmc unlzo $BOARD_TFTP_ADDRESS \$(filesize) $PARTITION_NAME 1" >> $PARTITION_SCRIPT
                else
                    echo "mmc unlzo.cont $BOARD_TFTP_ADDRESS \$(filesize) $PARTITION_NAME 1" >> $PARTITION_SCRIPT
                fi
            else
                echo "tftp $BOARD_TFTP_ADDRESS $PARTITION_NAME.img$j$k" >> $PARTITION_SCRIPT
                if [ $i -eq 0 ]; then
                    echo "mmc write.p $BOARD_TFTP_ADDRESS $PARTITION_NAME \$(filesize) 1" >> $PARTITION_SCRIPT
                else
                    echo "mmc write.p.continue $BOARD_TFTP_ADDRESS $PARTITION_NAME $offset_16 $size_16 1" >> $PARTITION_SCRIPT
                fi
            fi
            i=`expr $i + 1`
        done
    done
}

#make-split-fs-partition $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO
function make-split-fs-partition()
{
    echo -e "\033[31mMake fs $1 partition...\033[0m"
    local PARTITION_NAME=$1
    local PARTITION_SIZE=$2
    local PARTITION_LZO=$3

    # Generate image
    gernerate-fs-image $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO

    split-fs-partition $PARTITION_NAME $PARTITION_SIZE $PARTITION_LZO
}
