#!/bin/bash

#################################### part-1 ####################################
# show some tips
#
#================================= User guide ==================================
echo "-------------------------------------------------------------------------";
echo "Please notice the three setps"
echo "1. execute the script in ANDROID_BUILD_TOP"
echo "2. all updated image files in ANDROID_BUILD_TOP/../images"
echo "3. you can change crc by 'export PATH', default in ANDROID_BUILD_TOP/prebuilts/tools/linux-x86/crc"
echo "-------------------------------------------------------------------------";
echo "";


#################################### part-2 ####################################
# in order to show different Android version , we define argument AN
#============================ Select Android version ===========================
AN=lollipop
printf "Android : $AN\n";


#################################### part-3 ####################################
# define platform chip
# magic string(8 bytes) for checking information
#================================ Select Chip ==================================
#read -p "Chip?  1)monaco 2)muji 3)monet :" temp
temp=2
if [ "$temp" == "1" ]; then
    CHIP=monaco
elif [ "$temp" == "2" ]; then
    CHIP=muji
elif [ "$temp" == "3" ]; then
    CHIP=monet
fi
printf "CHIP: $CHIP\n"
MAGIC_STRING=12345678
printf "MAGIC_STRING: $MAGIC_STRING\n"


#################################### part-4 ####################################
# select DEVICE according to CHIP
#=============================== Select Board&Device ===========================
if [ "$CHIP" == "monaco" ]; then
    read -p "Device? 1)guava 2)grapefruit 3)pomelo :" temp
    if [ "$temp" == "1" ]; then
        DEVICE=guava
    elif [ "$temp" == "2" ]; then
        DEVICE=grapefruit
    elif [ "$temp" == "3" ]; then
        DEVICE=pomelo
    fi
elif [ "$CHIP" == "muji" ]; then
    #read -p "Device? 1)pitaya 2)coconut 3)tomato 4)scifly 5)leader 6)heran: " temp
    temp=6
    if [ "$temp" == "1" ]; then
        DEVICE=pitaya
    elif [ "$temp" == "2" ]; then
        DEVICE=coconut
    elif [ "$temp" == "3" ]; then
        DEVICE=tomato
	elif [ "$temp" == "4" ]; then
        DEVICE=scifly
    elif [ "$temp" == "5" ]; then
        DEVICE=LeaderSMARTTV
    elif [ "$temp" == "6" ]; then
        DEVICE=HeranSMARTTV
    fi
elif [ "$CHIP" == "monet" ]; then
    read -p "Device? 1)arbutus :" temp
    if [ "$temp" == "1" ]; then
        DEVICE=arbutus
    fi
fi
printf "DEVICE: $DEVICE\n"


#################################### part-5 ####################################
# config DRAM address in UBoot to load data
# the address my be different.we can refer to address in tftp upgrade script file
# and board MIU config
#======================= config DRAM address to load data ======================
DRAM_FATLOAD_BUF_ADDR=0x20200000


#################################### part-6 ####################################
# select function we want. If we select secureboot , boot.img and recovery.img
# will be encrypted , and need to upgrade their SecureInfo together
#=============================== function select ===============================
# ----------------------enable secureboot-----------------------
#read -p "Enable secureboot? (y/n)" temp
temp=y
if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
   ENABLE_SECUREBOOT=1
else
   ENABLE_SECUREBOOT=0
fi
printf "ENABLE_SECUREBOOT=$ENABLE_SECUREBOOT\n"


#################################### part-7 ####################################
# select different flash type . if EMMC , set filesystem EXT4, and if NAND , set
# filesystem UBIFS . NAND flash also has two types: MLC and SLC
#=============================== Select Filesystem =============================
FS_FORMAT="EXT4"
printf "FS_FORMAT: $FS_FORMAT\n"


#################################### part-8 ####################################
# select upgrade method . "usb" is default
# READBACK_VERIFY decide whether we need to do crc verification
#============================= Select Upgrade Method ===========================
# update soruce "usb" or "network"
UPDAT_METHOD="usb"
READBACK_VERIFY=1


#################################### part-9 ####################################
# If EXT4 filesystem , we use mmc command to write and erase partition
# else , we use nand command
#============================ Config flash erase cmd ===========================
# erase command
if [ "$FS_FORMAT" == "EXT4" ]; then
    CMD_ERASE_WHOLE="mmc rmgpt"
    CMD_ERASE_PART="mmc erase.p"
else
    CMD_ERASE_WHOLE="nand erase.chip"
    CMD_ERASE_PART="nand erase.part"
fi


#################################### part-10 ###################################
# define functions . These functions will be called in part-11.
# if you want to upgrade a new partition , please refer to func_upgrade_recovery
# or func_upgrade_system , and write a new function
#============================ Function definition ==============================

# set common argument
function func_init_common_env(){
    if [ -z "$TARGET_PRODUCT" ]; then
        ANDROID_BUILD_TOP=`pwd`
        export PATH=$PATH:$ANDROID_BUILD_TOP/tools/crc
    fi
    if [ ! -d "$SCRIPT_DIR" ]; then
        SCRIPT_DIR=$ANDROID_BUILD_TOP/images/scripts
        IMAGE_DIR=$ANDROID_BUILD_TOP/images
    fi
    
    UPDAT_METHOD=usb
    if [ "$UPDAT_METHOD" == "network" ]; then
        OUTPUT_IMG="netUpdate.bin"
    elif [ "$UPDAT_METHOD" == "usb" ]; then
        OUTPUT_IMG="/usbflash/638Upgrade.bin"
    fi

    IMAGE_EXT=".img"
    FILE_PART_READ_CMD="filepartload"
    #USB_IMG=$IMAGE_DIR/$OUTPUT_IMG
    USB_IMG=$ANDROID_BUILD_TOP/$OUTPUT_IMG
    SCRIPT_FILE=$IMAGE_DIR/usb_upgrade.txt
    SCRIPT_BUF_SIZE=0x4000 #16KB
    PAD_DUMMY_SIZE=10240 #10KB

    # image offset in /usbflash/638Upgrade.bin , we need this argument to load image
    # data from /usbflash/638Upgrade.bin when upgrade in UBoot
    IMAGE_OFFSET_IN_USB_IMG=$SCRIPT_BUF_SIZE

    TEMP_IMG=$IMAGE_DIR/usb_temp.bin
    PADDED_BIN=$IMAGE_DIR/padded.bin
    PAD_DUMMY_BIN=$IMAGE_DIR/dummy_pad.
}

# rm some exist files, create PAD_DUMMY_BIN
function func_pre_process(){
    echo "Creating USB upgrade Image..."
    if [ -f "$TEMP_IMG" ]; then
        rm $TEMP_IMG
    fi
    if [ -f "$USB_IMG" ]; then
        rm $USB_IMG
    fi
    if [ -f "$SCRIPT_FILE" ]; then
        rm $SCRIPT_FILE
    fi

    #PAD_DUMMY_BIN=10KB
    printf "\xff" >$PAD_DUMMY_BIN
    for ((i=1; i<$PAD_DUMMY_SIZE; i++))
    do
        printf "\xff" >>$PAD_DUMMY_BIN
    done
}

# write MAGIC_STRING to /usbflash/638Upgrade.bin
# write script file to /usbflash/638Upgrade.bin , and caculate crc value of script file
# write crc value to /usbflash/638Upgrade.bin
function func_post_process(){
    cat $SCRIPT_FILE >>$USB_IMG
    cat $TEMP_IMG >>$USB_IMG
    rm $TEMP_IMG
    rm $PAD_DUMMY_BIN
    TMP_SCRIPT_FILE=$IMAGE_DIR/tmp_usb_upgrade.txt
    printf "%s" $MAGIC_STRING > $TMP_SCRIPT_FILE
    cat $TMP_SCRIPT_FILE >>$USB_IMG
    rm $TMP_SCRIPT_FILE
    if [ "$READBACK_VERIFY" = "1" ]; then
        cp $SCRIPT_FILE $SCRIPT_FILE.temp
        # $CRC_BIN -a $SCRIPT_FILE.temp
        # will caculate crc value of $SCRIPT_FILE.temp , and add the value to the last of file
        # so last 4 bytes of $SCRIPT_FILE.temp is the crc value
        CRC_VALUE=`crc -a $SCRIPT_FILE.temp | grep "CRC32" | awk '{print $3;}'`
        split -d -a 2 -b "16384" $SCRIPT_FILE.temp $SCRIPT_FILE.temp.
        printf "script file crc %s\n" $CRC_VALUE
        cat $SCRIPT_FILE.temp.01 >> $USB_IMG
        rm -f $SCRIPT_FILE.temp
        rm -f $SCRIPT_FILE.temp.*
    fi
    crc -a $USB_IMG
    echo "/usbflash/638Upgrade.bin done"
}

# pad script file to SCRIPT_FILE_SIZE
function func_pad_script(){
    SCRIPT_FILE_SIZE=$(stat -c%s $SCRIPT_FILE)
    PADDED_SIZE=$(($SCRIPT_BUF_SIZE-$SCRIPT_FILE_SIZE))

    while [ $PADDED_SIZE -gt $PAD_DUMMY_SIZE ]
    do
        cat $PAD_DUMMY_BIN >> $SCRIPT_FILE
        PADDED_SIZE=$(($PADDED_SIZE-$PAD_DUMMY_SIZE))
    done

    if [ $PADDED_SIZE != 0 ]; then
        printf "\xff" >$PADDED_BIN
        for ((i=1; i<$PADDED_SIZE; i++))
        do
            printf "\xff" >>$PADDED_BIN
        done

        cat $PADDED_BIN >> $SCRIPT_FILE
        rm $PADDED_BIN
    fi
}

function func_finish_script(){
    if [ "$UPDAT_METHOD" == "network" ]; then
        printf "setenv netUpdate_complete 1\n" >> $SCRIPT_FILE
    elif [ "$UPDAT_METHOD" == "usb" ]; then
        printf "setenv MstarUpgrade_complete 1\n" >> $SCRIPT_FILE
        #printf "setenv force_upgrade 0x00\n" >> $SCRIPT_FILE
    fi

    printf "saveenv\n" >> $SCRIPT_FILE
    printf "printenv\n" >> $SCRIPT_FILE
    printf "%% <-this is end of file symbol\n" >> $SCRIPT_FILE
}

# set SecureInfo argument if enable secureboot
function func_secure_pre_set(){
    if [ "$ENABLE_SECUREBOOT" == "1" ]; then
        BOOT_SIGNATURE_BIN=$IMAGE_DIR/secure_info_boot.bin
        RECOVERY_SIGNATURE_BIN=$IMAGE_DIR/secure_info_recovery.bin
    fi
}

# print user configure information
function func_print_selection(){
    echo "--------------------------- User configure ---------------------------";
    printf "Android : $AN\n";
    printf "CHIP: $CHIP\n"
    printf "DEVICE: $DEVICE\n"
    printf "ENABLE_SECUREBOOT=$ENABLE_SECUREBOOT\n"
    printf "FS_FORMAT: $FS_FORMAT\n"
    echo "-------------------------------------------------------------------------";
    echo "";
}

# get partition information : partition name and partition size
function func_get_partition_information(){
    RECOVERY_PARTITION_NAME=recovery
    BOOT_PARTITION_NAME=boot
    SYSTEM_PARTITION_NAME=system
    USERDATA_PARTITION_NAME=userdata
    CACHE_PARTITION_NAME=cache
    TVSERVICE_PARTITION_NAME=tvservice
    TVCUSTOMER_PARTITION_NAME=tvcustomer
    TVDATABASE_PARTITION_NAME=tvdatabase
# EosTek Patch Begin    
    VRSDCARD_PARTITION_NAME=vrsdcard
# EosTek Patch End    

    # ----- first we should change file form dos format to unix format,
    # ----- if not ,we will get error data
    dos2unix $SCRIPT_DIR/* 2>/dev/null
    if [ "$FS_FORMAT" == "EXT4" ]; then
        RECOVERY_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "recovery" | awk '{print $4;}'`
        BOOT_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "boot" | awk '{print $4;}'`
        SYSTEM_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "system" | awk '{print $4;}'`
        USERDATA_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "userdata" | awk '{print $4;}'`
        CACHE_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "cache" | awk '{print $4;}'`
        TVSERVICE_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "tvservice" | awk '{print $4;}'`
        TVCUSTOMER_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "tvcustomer" | awk '{print $4;}'`
        TVDATABASE_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "tvdatabase" | awk '{print $4;}'`
# EosTek Patch Begin         
        VRSDCARD_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "vrsdcard" | awk '{print $4;}'`
# EosTek Patch End        
    else
        RECOVERY_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep dynpart | sed 's/.*misc..//g' | sed ' s/.recovery.*//g'`
        BOOT_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep dynpart | sed 's/.*recovery..//g' | sed ' s/.boot.*//g'`
        SYSTEM_PARTITION_SIZE=`cat $SCRIPT_DIR/[[system | grep -v "^ *#" | grep "ubi create" | awk '{print $4;}'`
        USERDATA_PARTITION_SIZE=`cat $SCRIPT_DIR/[[userdata | grep -v "^ *#" | grep "ubi create" | awk '{print $4;}'`
        CACHE_PARTITION_SIZE=`cat $SCRIPT_DIR/[[cache | grep -v "^ *#" | grep "ubi create" | awk '{print $4;}'`
        TVSERVICE_PARTITION_SIZE=`cat $SCRIPT_DIR/[[tvservice | grep -v "^ *#" | grep "ubi create" | awk '{print $4;}'`
        TVCUSTOMER_PARTITION_SIZE=`cat $SCRIPT_DIR/[[tvcustomer | grep -v "^ *#" | grep "ubi create" | awk '{print $4;}'`
        TVDATABASE_PARTITION_SIZE=`cat $SCRIPT_DIR/[[tvdatabase | grep -v "^ *#" | grep "ubi create" | awk '{print $4;}'`
# EosTek Patch Begin         
        VRSDCARD_PARTITION_SIZE=`cat $SCRIPT_DIR/[[vrsdcard | grep -v "^ *#" | grep "ubi create" | awk '{print $4;}'`
# EosTek Patch End        
    fi

    if [ "$BOOT_PARTITION_SIZE" == "" ]; then
        echo
        echo "Failed to get boot partition settings!"
        echo "Please check if the config file exists!"
        echo
        exit 1
    fi
}

# get image information : set image name , and get image size
function func_get_image_information(){
    if [ "$ENABLE_SECUREBOOT" == "1" ]; then
        RECOVERY_IMG=$IMAGE_DIR/recovery.img.aes
        BOOT_IMG=$IMAGE_DIR/boot.img.aes
    else
        RECOVERY_IMG=$IMAGE_DIR/recovery$IMAGE_EXT
        BOOT_IMG=$IMAGE_DIR/boot$IMAGE_EXT
    fi

    SYSTEM_IMG=$IMAGE_DIR/system$IMAGE_EXT
    SYSTEM_IMG_LZO=$IMAGE_DIR/system$IMAGE_EXT.lzo
    USERDATA_IMG=$IMAGE_DIR/userdata$IMAGE_EXT
    USERDATA_IMG_LZO=$IMAGE_DIR/userdata$IMAGE_EXT.lzo
    CACHE_IMG=$IMAGE_DIR/cache$IMAGE_EXT
    CACHE_IMG_LZO=$IMAGE_DIR/cache$IMAGE_EXT.lzo
    TVSERVICE_IMG=$IMAGE_DIR/tvservice$IMAGE_EXT
    TVCUSTOMER_IMG=$IMAGE_DIR/tvcustomer$IMAGE_EXT
    TVDATABASE_IMG=$IMAGE_DIR/tvdatabase$IMAGE_EXT
    USB_IMG=$IMAGE_DIR/$OUTPUT_IMG
# EosTek Patch Begin
    VRSDCARD_IMG=$IMAGE_DIR/vrsdcard$IMAGE_EXT
    VRSDCARD_IMG_LZO=$IMAGE_DIR/vrsdcard$IMAGE_EXT.lzo
# EosTek Patch End

    RECOVERY_IMG_SIZE=$(stat -c%s $RECOVERY_IMG )
    BOOT_IMG_SIZE=$(stat -c%s $BOOT_IMG )
    TVSERVICE_IMG_SIZE=$(stat -c%s $TVSERVICE_IMG )
    TVCUSTOMER_IMG_SIZE=$(stat -c%s $TVCUSTOMER_IMG )
    TVDATABASE_IMG_SIZE=$(stat -c%s $TVDATABASE_IMG )
}

# upgrade recovery partition . if enable secureboot , we alse need to upgrade SecureInfo of recovery
function func_upgrade_recovery(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        temp="Y"
    else
        read -p "Update recovery? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade recovery"
        cat $RECOVERY_IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($RECOVERY_IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$CMD_ERASE_PART misc\n" >> $SCRIPT_FILE
        printf "$CMD_ERASE_PART recovery\n" >> $SCRIPT_FILE
        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $RECOVERY_IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $RECOVERY_PARTITION_NAME $RECOVERY_IMG_SIZE >> $SCRIPT_FILE
        else
            if [ "$NAND_FLASH_TYPE" == "MLC" ]; then
                printf "nand write.e %x recovery %x\n" $DRAM_FATLOAD_BUF_ADDR $RECOVERY_IMG_SIZE >> $SCRIPT_FILE
            else
                printf "nand write.slc %x recovery %x\n" $DRAM_FATLOAD_BUF_ADDR $RECOVERY_IMG_SIZE >> $SCRIPT_FILE
            fi
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$RECOVERY_IMG_SIZE+$PADDED_SIZE))

        # ------------------ upgrade recovery secureinfo -----------------------
        if [ "$ENABLE_SECUREBOOT" == "1" ]; then
            cat $RECOVERY_SIGNATURE_BIN >>$TEMP_IMG
            SIGNATURE_IMG_SIZE=$(stat -c%s $RECOVERY_SIGNATURE_BIN )

            # align image to 0x1000(4K)
            PADDED_SIZE=0
            NOT_ALAIN_IMAGE_SIZE=$(($SIGNATURE_IMG_SIZE & 0xfff))
            if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                for ((i=0; i<$PADDED_SIZE; i++))
                do
                    printf "\xff" >>$PADDED_BIN
                done

                cat $PADDED_BIN >>$TEMP_IMG
                rm $PADDED_BIN
            fi

            printf "$RECOVERY_SIGNATURE_BIN size: 0x%x\n" $SIGNATURE_IMG_SIZE
            printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SIGNATURE_IMG_SIZE >> $SCRIPT_FILE
            printf "store_secure_info recoverySign %x \n" $DRAM_FATLOAD_BUF_ADDR >> $SCRIPT_FILE
            IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SIGNATURE_IMG_SIZE+$PADDED_SIZE))
        fi
        # ------------------ upgrade recovery secureinfo -----------------------
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade recovery"
    else
        echo "Your choice??"
    fi
}


# upgrade boot partition . if enable secureboot , we alse need to upgrade SecureInfo of boot
function func_upgrade_boot(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        temp="Y"
    else
        read -p "Update boot? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade boot"
        cat $BOOT_IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($BOOT_IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$CMD_ERASE_PART boot\n" >> $SCRIPT_FILE
        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $BOOT_IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $BOOT_PARTITION_NAME $BOOT_IMG_SIZE >> $SCRIPT_FILE
        else
            if [ "$NAND_FLASH_TYPE" == "MLC" ]; then
                printf "nand write.e %x boot %x\n" $DRAM_FATLOAD_BUF_ADDR $BOOT_IMG_SIZE >> $SCRIPT_FILE
            else
                printf "nand write.slc %x boot %x\n" $DRAM_FATLOAD_BUF_ADDR $BOOT_IMG_SIZE >> $SCRIPT_FILE
            fi
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$BOOT_IMG_SIZE+$PADDED_SIZE))

        # ------------------------ upgrade boot secureinfo ---------------------
        if [ "$ENABLE_SECUREBOOT" == "1" ]; then
            cat $BOOT_SIGNATURE_BIN >>$TEMP_IMG
            SIGNATURE_IMG_SIZE=$(stat -c%s $BOOT_SIGNATURE_BIN )

            # align image to 0x1000(4K)
            PADDED_SIZE=0
            NOT_ALAIN_IMAGE_SIZE=$(($SIGNATURE_IMG_SIZE & 0xfff))
            if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                for ((i=0; i<$PADDED_SIZE; i++))
                do
                    printf "\xff" >>$PADDED_BIN
                done

                cat $PADDED_BIN >>$TEMP_IMG
                rm $PADDED_BIN
            fi

            printf "$BOOT_SIGNATURE_BIN size: 0x%x\n" $SIGNATURE_IMG_SIZE
            printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SIGNATURE_IMG_SIZE >> $SCRIPT_FILE
            printf "store_secure_info bootSign %x \n" $DRAM_FATLOAD_BUF_ADDR >> $SCRIPT_FILE
            IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SIGNATURE_IMG_SIZE+$PADDED_SIZE))
        fi
        # ------------------------ upgrade boot secureinfo ---------------------
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade boot"
    else
        echo "Your choice??"
    fi
}


# upgrade system partition
function func_upgrade_system(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        temp="Y"
    else
        read -p "Update system? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade system"
        if [ "$FS_FORMAT" == "EXT4" ]; then
            # ----- ext4 filesystem

            # erase system partition
            printf "$CMD_ERASE_PART system\n" >> $SCRIPT_FILE

            # system partition can be upgraded in 3 formats.
            # 1 unlzo.cont : lzo format and has been spilt
            # 2 unlzo : lzo format and not spilt
            # 3 write.p : image not compressed by lzo , and just like boot/recovery
            SPILT_FLAG=0
            UNLZO_FLAG=0
            WRITE_FLAG=0

            # check if spilt lzo format
            outline=`cat $SCRIPT_DIR/[[system | grep -v "^ *#" | grep "mmc" | grep "system" | grep "unlzo\.cont" `
            if [ "$outline" != "" ]; then
                SPILT_FLAG=1
            fi

            # check if lzo format
            outline=`cat $SCRIPT_DIR/[[system | grep -v "^ *#" | grep "mmc" | grep "system" | grep "unlzo" `
            if [ "$outline" != "" ]; then
                UNLZO_FLAG=1
            fi

            # check if image format
            outline=`cat $SCRIPT_DIR/[[system | grep -v "^ *#" | grep "mmc" | grep "system" | grep "write\.p" `
            if [ "$outline" != "" ]; then
                WRITE_FLAG=1
            fi
            if [ "$SPILT_FLAG" = "1" ]; then
                # system image has been spilt
                SPILT_NUM=`cat $SCRIPT_DIR/[[system | grep -v "^ *#" | grep "mmc" | grep "unlzo" | wc | awk '{print $1;}' `
                # write system.imgxx.lzo to /usbflash/638Upgrade.bin
                for ((i=1; i<=$SPILT_NUM; i++))
                do
                    SYSTEM_IMAGE_PART=`cat $SCRIPT_DIR/[[system | grep -v "^ *#" | grep "tftp" | awk '{print $3;}' |awk 'NR=='$i''`
                    if [ -f $IMAGE_DIR/$SYSTEM_IMAGE_PART ]; then
                        echo "get $IMAGE_DIR/$SYSTEM_IMAGE_PART"
                        cat $IMAGE_DIR/$SYSTEM_IMAGE_PART >>$TEMP_IMG

                        # align image to 0x1000(4K)
                        SYSTEM_IMG_PART_SIZE=$(stat -c%s $IMAGE_DIR/$SYSTEM_IMAGE_PART )
                        PADDED_SIZE=0
                        NOT_ALAIN_IMAGE_SIZE=$(($SYSTEM_IMG_PART_SIZE & 0xfff))
                        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                            for ((j=0; j<$PADDED_SIZE; j++))
                            do
                                printf "\xff" >>$PADDED_BIN
                            done

                            cat $PADDED_BIN >>$TEMP_IMG
                            rm $PADDED_BIN
                        fi

                        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SYSTEM_IMG_PART_SIZE >> $SCRIPT_FILE
                        if [ "$i" = "1" ]; then
                            printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEM_IMG_PART_SIZE $SYSTEM_PARTITION_NAME>> $SCRIPT_FILE
                        else
                            printf "mmc unlzo.cont %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEM_IMG_PART_SIZE $SYSTEM_PARTITION_NAME>> $SCRIPT_FILE
                        fi
                        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SYSTEM_IMG_PART_SIZE+$PADDED_SIZE))
                    else
                        echo " Error , file : $IMAGE_DIR/$SYSTEM_IMAGE_PART not exist"
                        exit 0
                    fi
                done
            elif [ "$UNLZO_FLAG" = "1" ]; then
                # system upgrade by lzo format, and not spilt
                # check system.img.lzo if exist
                if [ -f $SYSTEM_IMG_LZO ]; then
                    echo "get $SYSTEM_IMG_LZO"
                    cat $SYSTEM_IMG_LZO >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    SYSTEM_IMG_LZO_SIZE=$(stat -c%s $SYSTEM_IMG_LZO )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($SYSTEM_IMG_LZO_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((j=0; j<$PADDED_SIZE; j++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SYSTEM_IMG_LZO_SIZE >> $SCRIPT_FILE
                    printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEM_IMG_LZO_SIZE $SYSTEM_PARTITION_NAME>> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SYSTEM_IMG_LZO_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $SYSTEM_IMG_LZO not exist"
                    exit 0
                fi
            elif [ "$WRITE_FLAG" = "1" ]; then
                # system upgrade by image format, like boot and recovery
                # check system.img if exist
                if [ -f $SYSTEM_IMG ]; then
                    echo "get $SYSTEM_IMG"
                    cat $SYSTEM_IMG >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    SYSTEM_IMG_SIZE=$(stat -c%s $SYSTEM_IMG )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($SYSTEM_IMG_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((i=0; i<$PADDED_SIZE; i++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SYSTEM_IMG_SIZE >> $SCRIPT_FILE
                    printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEM_PARTITION_NAME $SYSTEM_IMG_SIZE >> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SYSTEM_IMG_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $SYSTEM_IMG not exist"
                    exit 0
                fi
            else
                # unknow upgrade format
                echo " unknow system upgrade format"
                exit 1
            fi # if [ "$SPILT_FLAG" = "1" ]
        else # if [ "$FS_FORMAT" == "EXT4" ]
            # ubi filesystem
            if [ -f $SYSTEM_IMG ]; then
                echo "get $SYSTEM_IMG"
                cat $SYSTEM_IMG >>$TEMP_IMG

                # align image to 0x1000(4K)
                SYSTEM_IMG_SIZE=$(stat -c%s $SYSTEM_IMG )
                PADDED_SIZE=0
                NOT_ALAIN_IMAGE_SIZE=$(($SYSTEM_IMG_SIZE & 0xfff))
                if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                    PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                    for ((i=0; i<$PADDED_SIZE; i++))
                    do
                        printf "\xff" >>$PADDED_BIN
                    done

                    cat $PADDED_BIN >>$TEMP_IMG
                    rm $PADDED_BIN
                fi

                printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SYSTEM_IMG_SIZE >> $SCRIPT_FILE
                printf "ubi part ubi\n" >> $SCRIPT_FILE
                printf "ubi remove %s\n" $SYSTEM_PARTITION_NAME >> $SCRIPT_FILE
                printf "ubi create %s %x\n" $SYSTEM_PARTITION_NAME $SYSTEM_PARTITION_SIZE >> $SCRIPT_FILE
                printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEM_PARTITION_NAME $SYSTEM_IMG_SIZE >> $SCRIPT_FILE
                IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SYSTEM_IMG_SIZE+$PADDED_SIZE))
            else
                echo " Error , file : $SYSTEM_IMG not exist"
                exit 0
            fi
        fi # if [ "$FS_FORMAT" == "EXT4" ]
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade system"
    else
        echo "Your choice??"
    fi
}


# upgrade userdata partition , refer to upgrade code of system partition
function func_upgrade_userdata(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        temp="Y"
    else
        read -p "Update userdata? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade userdata"
        if [ "$FS_FORMAT" == "EXT4" ]; then
            # ----- ext4 filesystem

            # erase userdata partition
            printf "$CMD_ERASE_PART userdata\n" >> $SCRIPT_FILE

            SPILT_FLAG=0
            UNLZO_FLAG=0
            WRITE_FLAG=0

            # check if spilt lzo format
            outline=`cat $SCRIPT_DIR/[[userdata | grep -v "^ *#" | grep "mmc" | grep "userdata" | grep "unlzo\.cont" `
            if [ "$outline" != "" ]; then
                SPILT_FLAG=1
            fi

            # check if lzo format
            outline=`cat $SCRIPT_DIR/[[userdata | grep -v "^ *#" | grep "mmc" | grep "userdata" | grep "unlzo" `
            if [ "$outline" != "" ]; then
                UNLZO_FLAG=1
            fi

            # check if image format
            outline=`cat $SCRIPT_DIR/[[userdata | grep -v "^ *#" | grep "mmc" | grep "userdata" | grep "write\.p" `
            if [ "$outline" != "" ]; then
                WRITE_FLAG=1
            fi
            if [ "$SPILT_FLAG" = "1" ]; then
                # userdata image has been spilt
                SPILT_NUM=`cat $SCRIPT_DIR/[[userdata | grep -v "^ *#" | grep "mmc" | grep "unlzo" | wc | awk '{print $1;}' `
                # write userdata.imgxx.lzo to /usbflash/638Upgrade.bin
                for ((i=1; i<=$SPILT_NUM; i++))
                do
                    USERDATA_IMAGE_PART=`cat $SCRIPT_DIR/[[userdata | grep -v "^ *#" | grep "tftp" | awk '{print $3;}' |awk 'NR=='$i''`
                    if [ -f $IMAGE_DIR/$USERDATA_IMAGE_PART ]; then
                        echo "get $IMAGE_DIR/$USERDATA_IMAGE_PART"
                        cat $IMAGE_DIR/$USERDATA_IMAGE_PART >>$TEMP_IMG

                        # align image to 0x1000(4K)
                        USERDATA_IMG_PART_SIZE=$(stat -c%s $IMAGE_DIR/$USERDATA_IMAGE_PART )
                        PADDED_SIZE=0
                        NOT_ALAIN_IMAGE_SIZE=$(($USERDATA_IMG_PART_SIZE & 0xfff))
                        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                            for ((j=0; j<$PADDED_SIZE; j++))
                            do
                                printf "\xff" >>$PADDED_BIN
                            done

                            cat $PADDED_BIN >>$TEMP_IMG
                            rm $PADDED_BIN
                        fi

                        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $USERDATA_IMG_PART_SIZE >> $SCRIPT_FILE
                        if [ "$i" = "1" ]; then
                            printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $USERDATA_IMG_PART_SIZE $USERDATA_PARTITION_NAME>> $SCRIPT_FILE
                        else
                            printf "mmc unlzo.cont %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $USERDATA_IMG_PART_SIZE $USERDATA_PARTITION_NAME>> $SCRIPT_FILE
                        fi
                        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$USERDATA_IMG_PART_SIZE+$PADDED_SIZE))
                    else
                        echo " Error , file : $IMAGE_DIR/$USERDATA_IMAGE_PART not exist"
                        exit 0
                    fi
                done
            elif [ "$UNLZO_FLAG" = "1" ]; then
                # userdata upgrade by lzo format, and not spilt
                # check userdata.img.lzo if exist
                if [ -f $USERDATA_IMG_LZO ]; then
                    echo "get $USERDATA_IMG_LZO"
                    cat $USERDATA_IMG_LZO >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    USERDATA_IMG_LZO_SIZE=$(stat -c%s $USERDATA_IMG_LZO )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($USERDATA_IMG_LZO_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((j=0; j<$PADDED_SIZE; j++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $USERDATA_IMG_LZO_SIZE >> $SCRIPT_FILE
                    printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $USERDATA_IMG_LZO_SIZE $USERDATA_PARTITION_NAME>> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$USERDATA_IMG_LZO_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $USERDATA_IMG_LZO not exist"
                    exit 0
                fi
            elif [ "$WRITE_FLAG" = "1" ]; then
                # userdata upgrade by image format, like boot and recovery
                # check userdata.img if exist
                if [ -f $USERDATA_IMG ]; then
                    echo "get $USERDATA_IMG"
                    cat $USERDATA_IMG >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    USERDATA_IMG_SIZE=$(stat -c%s $USERDATA_IMG )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($USERDATA_IMG_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((i=0; i<$PADDED_SIZE; i++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $USERDATA_IMG_SIZE >> $SCRIPT_FILE
                    printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $USERDATA_PARTITION_NAME $USERDATA_IMG_SIZE >> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$USERDATA_IMG_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $USERDATA_IMG not exist"
                    exit 0
                fi
            else
                # unknow upgrade format
                echo " unknow userdata upgrade format"
                exit 1
            fi # if [ "$SPILT_FLAG" = "1" ]
        else
            # ubi filesystem
            if [ -f $USERDATA_IMG ]; then
                echo "get $USERDATA_IMG"
                cat $USERDATA_IMG >>$TEMP_IMG

                # align image to 0x1000(4K)
                USERDATA_IMG_SIZE=$(stat -c%s $USERDATA_IMG )
                PADDED_SIZE=0
                NOT_ALAIN_IMAGE_SIZE=$(($USERDATA_IMG_SIZE & 0xfff))
                if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                    PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                    for ((i=0; i<$PADDED_SIZE; i++))
                    do
                        printf "\xff" >>$PADDED_BIN
                    done

                    cat $PADDED_BIN >>$TEMP_IMG
                    rm $PADDED_BIN
                fi

                printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $USERDATA_IMG_SIZE >> $SCRIPT_FILE
                printf "ubi part ubi\n" >> $SCRIPT_FILE
                printf "ubi remove %s\n" $USERDATA_PARTITION_NAME >> $SCRIPT_FILE
                printf "ubi create %s %x\n" $USERDATA_PARTITION_NAME $USERDATA_PARTITION_SIZE >> $SCRIPT_FILE
                printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $USERDATA_PARTITION_NAME $USERDATA_IMG_SIZE >> $SCRIPT_FILE
                IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$USERDATA_IMG_SIZE+$PADDED_SIZE))
            else
                echo " Error , file : $USERDATA_IMG not exist"
                exit 0
            fi
        fi
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade userdata"
    else
        echo "Your choice??"
    fi
}

# upgrade cache partition , refer to upgrade code of system partition
function func_upgrade_cache(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        temp="Y"
    else
        read -p "Upgrade cache? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade cache"
        if [ "$FS_FORMAT" == "EXT4" ]; then
            # ----- ext4 filesystem

            # erase cache partition
            printf "$CMD_ERASE_PART cache\n" >> $SCRIPT_FILE

            SPILT_FLAG=0
            UNLZO_FLAG=0
            WRITE_FLAG=0

            # check if spilt lzo format
            outline=`cat $SCRIPT_DIR/[[cache | grep -v "^ *#" | grep "mmc" | grep "cache" | grep "unlzo\.cont" `
            if [ "$outline" != "" ]; then
                SPILT_FLAG=1
            fi

            # check if lzo format
            outline=`cat $SCRIPT_DIR/[[cache | grep -v "^ *#" | grep "mmc" | grep "cache" | grep "unlzo" `
            if [ "$outline" != "" ]; then
                UNLZO_FLAG=1
            fi

            # check if image format
            outline=`cat $SCRIPT_DIR/[[cache | grep -v "^ *#" | grep "mmc" | grep "cache" | grep "write\.p" `
            if [ "$outline" != "" ]; then
                WRITE_FLAG=1
            fi
            if [ "$SPILT_FLAG" = "1" ]; then
                # cache image has been spilt
                SPILT_NUM=`cat $SCRIPT_DIR/[[cache | grep -v "^ *#" | grep "mmc" | grep "unlzo" | wc | awk '{print $1;}' `
                # write cache.imgxx.lzo to /usbflash/638Upgrade.bin
                for ((i=1; i<=$SPILT_NUM; i++))
                do
                    CACHE_IMAGE_PART=`cat $SCRIPT_DIR/[[cache | grep -v "^ *#" | grep "tftp" | awk '{print $3;}' |awk 'NR=='$i''`
                    if [ -f $IMAGE_DIR/$CACHE_IMAGE_PART ]; then
                        echo "get $IMAGE_DIR/$CACHE_IMAGE_PART"
                        cat $IMAGE_DIR/$CACHE_IMAGE_PART >>$TEMP_IMG

                        # align image to 0x1000(4K)
                        CACHE_IMG_PART_SIZE=$(stat -c%s $IMAGE_DIR/$CACHE_IMAGE_PART )
                        PADDED_SIZE=0
                        NOT_ALAIN_IMAGE_SIZE=$(($CACHE_IMG_PART_SIZE & 0xfff))
                        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                            for ((j=0; j<$PADDED_SIZE; j++))
                            do
                                printf "\xff" >>$PADDED_BIN
                            done

                            cat $PADDED_BIN >>$TEMP_IMG
                            rm $PADDED_BIN
                        fi

                        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $CACHE_IMG_PART_SIZE >> $SCRIPT_FILE
                        if [ "$i" = "1" ]; then
                            printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $CACHE_IMG_PART_SIZE $CACHE_PARTITION_NAME>> $SCRIPT_FILE
                        else
                            printf "mmc unlzo.cont %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $CACHE_IMG_PART_SIZE $CACHE_PARTITION_NAME>> $SCRIPT_FILE
                        fi
                        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$CACHE_IMG_PART_SIZE+$PADDED_SIZE))
                    else
                        echo " Error , file : $IMAGE_DIR/$CACHE_IMAGE_PART not exist"
                        exit 0
                    fi
                done
            elif [ "$UNLZO_FLAG" = "1" ]; then
                # cache upgrade by lzo format, and not spilt
                # check cache.img.lzo if exist
                if [ -f $CACHE_IMG_LZO ]; then
                    echo "get $CACHE_IMG_LZO"
                    cat $CACHE_IMG_LZO >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    CACHE_IMG_LZO_SIZE=$(stat -c%s $CACHE_IMG_LZO )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($CACHE_IMG_LZO_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((j=0; j<$PADDED_SIZE; j++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $CACHE_IMG_LZO_SIZE >> $SCRIPT_FILE
                    printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $CACHE_IMG_LZO_SIZE $CACHE_PARTITION_NAME>> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$CACHE_IMG_LZO_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $CACHE_IMG_LZO not exist"
                    exit 0
                fi
            elif [ "$WRITE_FLAG" = "1" ]; then
                # cache upgrade by image format, like boot and recovery
                # check cache.img if exist
                if [ -f $CACHE_IMG ]; then
                    echo "get $CACHE_IMG"
                    cat $CACHE_IMG >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    CACHE_IMG_SIZE=$(stat -c%s $CACHE_IMG )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($CACHE_IMG_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((i=0; i<$PADDED_SIZE; i++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $CACHE_IMG_SIZE >> $SCRIPT_FILE
                    printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $CACHE_PARTITION_NAME $CACHE_IMG_SIZE >> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$CACHE_IMG_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $CACHE_IMG not exist"
                    exit 0
                fi
            else
                # unknow upgrade format
                echo " unknow cache upgrade format"
                exit 1
            fi # if [ "$SPILT_FLAG" = "1" ]
        else
            # ubi filesystem
            if [ -f $CACHE_IMG ]; then
                echo "get $CACHE_IMG"
                cat $CACHE_IMG >>$TEMP_IMG

                # align image to 0x1000(4K)
                CACHE_IMG_SIZE=$(stat -c%s $CACHE_IMG )
                PADDED_SIZE=0
                NOT_ALAIN_IMAGE_SIZE=$(($CACHE_IMG_SIZE & 0xfff))
                if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                    PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                    for ((i=0; i<$PADDED_SIZE; i++))
                    do
                        printf "\xff" >>$PADDED_BIN
                    done

                    cat $PADDED_BIN >>$TEMP_IMG
                    rm $PADDED_BIN
                fi

                printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $CACHE_IMG_SIZE >> $SCRIPT_FILE
                printf "ubi part ubi\n" >> $SCRIPT_FILE
                printf "ubi remove %s\n" $CACHE_PARTITION_NAME >> $SCRIPT_FILE
                printf "ubi create %s %x\n" $CACHE_PARTITION_NAME $CACHE_PARTITION_SIZE >> $SCRIPT_FILE
                printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $CACHE_PARTITION_NAME $CACHE_IMG_SIZE >> $SCRIPT_FILE
                IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$CACHE_IMG_SIZE+$PADDED_SIZE))
            else
                echo " Error , file : $CACHE_IMG not exist"
                exit 0
            fi
        fi
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade cache"
    else
        echo "Your choice??"
    fi
}

# upgrade tvservice partition
function func_upgrade_tvservice(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        temp="Y"
    else
        read -p "Update tvservice? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade tvservice"
        cat $TVSERVICE_IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($TVSERVICE_IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $TVSERVICE_IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "$CMD_ERASE_PART tvservice\n" >> $SCRIPT_FILE
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $TVSERVICE_PARTITION_NAME $TVSERVICE_IMG_SIZE >> $SCRIPT_FILE
        else
            printf "ubi part ubi\n" >> $SCRIPT_FILE
            printf "ubi remove %s\n" $TVSERVICE_PARTITION_NAME >> $SCRIPT_FILE
            printf "ubi create %s %x\n" $TVSERVICE_PARTITION_NAME $TVSERVICE_PARTITION_SIZE >> $SCRIPT_FILE
            printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $TVSERVICE_PARTITION_NAME $TVSERVICE_IMG_SIZE >> $SCRIPT_FILE
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$TVSERVICE_IMG_SIZE+$PADDED_SIZE))

    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade tvservice"
    else
        echo "Your choice??"
    fi
}

# upgrade tvconfig partition
function func_upgrade_tvconfig(){
    #read -p "Update tvconfig? (y/n)" temp
    temp=y
    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade tvconfig"
        TVCONFIG_PARTITION_NAME=tvconfig
        TVCONFIG_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "tvconfig" | awk '{print $4;}'`
        TCCONFIG_IMG=$IMAGE_DIR/tvconfig$IMAGE_EXT
        TVCONFIG_IMG_SIZE=$(stat -c%s $TCCONFIG_IMG )

        cat $TCCONFIG_IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($TVCONFIG_IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $TVCONFIG_IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "$CMD_ERASE_PART tvconfig\n" >> $SCRIPT_FILE
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $TVCONFIG_PARTITION_NAME $TVCONFIG_IMG_SIZE >> $SCRIPT_FILE
        else
            printf "ubi part ubi\n" >> $SCRIPT_FILE
            printf "ubi remove %s\n" $TVCONFIG_PARTITION_NAME >> $SCRIPT_FILE
            printf "ubi create %s %x\n" $TVCONFIG_PARTITION_NAME $TVCONFIG_PARTITION_SIZE >> $SCRIPT_FILE
            printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $TVCONFIG_PARTITION_NAME $TVCONFIG_IMG_SIZE >> $SCRIPT_FILE
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$TVCONFIG_IMG_SIZE+$PADDED_SIZE))
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade tvconfig"
    else
        echo "Your choice??"
    fi
}

# upgrade tvdatabase partition
function func_upgrade_tvdatabase(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        temp="Y"
    else
        read -p "Update tvdatabase? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade tvdatabase"
        cat $TVDATABASE_IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($TVDATABASE_IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $TVDATABASE_IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "$CMD_ERASE_PART tvdatabase\n" >> $SCRIPT_FILE
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $TVDATABASE_PARTITION_NAME $TVDATABASE_IMG_SIZE >> $SCRIPT_FILE
        else
            printf "ubi part ubi\n" >> $SCRIPT_FILE
            printf "ubi remove %s\n" $TVDATABASE_PARTITION_NAME >> $SCRIPT_FILE
            printf "ubi create %s %x\n" $TVDATABASE_PARTITION_NAME $TVDATABASE_PARTITION_SIZE >> $SCRIPT_FILE
            printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $TVDATABASE_PARTITION_NAME $TVDATABASE_IMG_SIZE >> $SCRIPT_FILE
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$TVDATABASE_IMG_SIZE+$PADDED_SIZE))

    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade tvdatabase"
    else
        echo "Your choice??"
    fi
}

# upgrade tvcustomer partition
function func_upgrade_tvcustomer(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        temp="Y"
    else
        read -p "Update tvcustomer? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade tvcustomer"
        cat $TVCUSTOMER_IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($TVCUSTOMER_IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $TVCUSTOMER_IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "$CMD_ERASE_PART tvcustomer\n" >> $SCRIPT_FILE
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $TVCUSTOMER_PARTITION_NAME $TVCUSTOMER_IMG_SIZE >> $SCRIPT_FILE
        else
            printf "ubi part ubi\n" >> $SCRIPT_FILE
            printf "ubi remove %s\n" $TVCUSTOMER_PARTITION_NAME >> $SCRIPT_FILE
            printf "ubi create %s %x\n" $TVCUSTOMER_PARTITION_NAME $TVCUSTOMER_PARTITION_SIZE >> $SCRIPT_FILE
            printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $TVCUSTOMER_PARTITION_NAME $TVCUSTOMER_IMG_SIZE >> $SCRIPT_FILE
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$TVCUSTOMER_IMG_SIZE+$PADDED_SIZE))

    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade tvcustomer"
    else
        echo "Your choice??"
    fi
}

# upgrade mboot . if mboot in SPI flash , we call spi wrc command to write data
# if mboot in EMMC flash , we call mmc write.p command to write data
function func_upgrade_mboot(){
    #read -p "Upgrade mboot? (y/n)" temp
    temp=y

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade mboot"

        #read -p "Mboot partition type 1)SPI; 2)EMMC :" temp
        temp=2
        if [ "$temp" == "1" ]; then
            MBOOT_PARTITION_TYPE=SPI
            MBOOT_PARTITION_SIZE=0x200000
        elif [ "$temp" == "2" ]; then
            MBOOT_PARTITION_TYPE=EMMC
            MBOOT_PARTITION_SIZE=0x300000
        fi

        printf "MBOOT_PARTITION_TYPE=$MBOOT_PARTITION_TYPE\n"
        printf "MBOOT_PARTITION_SIZE=$MBOOT_PARTITION_SIZE\n"

        MBOOT_BIN=$IMAGE_DIR/mboot.bin
        if [ "$MBOOT_PARTITION_TYPE" = "EMMC" ]; then
            MBOOT_ROM_BIN=$IMAGE_DIR/rom_emmc_boot.bin
        fi

        if [ ! -f $MBOOT_BIN ]; then
            echo "Error ,$MBOOT_BIN not exist!!!"
            exit 0
        fi

        MBOOT_BIN_SIZE=$(stat -c%s $MBOOT_BIN )
        if [ "$MBOOT_PARTITION_TYPE" = "EMMC" ]; then
            MBOOT_ROM_BIN_SIZE=$(stat -c%s $MBOOT_ROM_BIN )
        fi

        if [ "$MBOOT_PARTITION_TYPE" = "EMMC" ]; then
            cat $MBOOT_ROM_BIN >>$TEMP_IMG

            PADDED_SIZE_ROM=0
            NOT_ALAIN_IMAGE_SIZE=$(($MBOOT_ROM_BIN_SIZE & 0xfff))
            if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                PADDED_SIZE_ROM=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                for ((i=0; i<$PADDED_SIZE_ROM; i++))
                do
                    printf "\xff" >>$PADDED_BIN
                done

                cat $PADDED_BIN >>$TEMP_IMG
                rm $PADDED_BIN
            fi
        fi
        cat $MBOOT_BIN >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($MBOOT_BIN_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        if [ "$MBOOT_PARTITION_TYPE" == "EMMC" ]; then
            printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $MBOOT_ROM_BIN_SIZE >> $SCRIPT_FILE
            printf "mmc write.boot 1 %x 0 %x\n" $DRAM_FATLOAD_BUF_ADDR $MBOOT_ROM_BIN_SIZE >> $SCRIPT_FILE
            IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$MBOOT_ROM_BIN_SIZE+$PADDED_SIZE_ROM))
            printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $MBOOT_BIN_SIZE >> $SCRIPT_FILE
            printf "mmc write.p %x MBOOT %x\n" $DRAM_FATLOAD_BUF_ADDR $MBOOT_BIN_SIZE >> $SCRIPT_FILE
        elif [ "$MBOOT_PARTITION_TYPE" == "SPI" ]; then
            printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $MBOOT_BIN_SIZE >> $SCRIPT_FILE
            printf "spi wrc %x 0 %x\n" $DRAM_FATLOAD_BUF_ADDR $MBOOT_BIN_SIZE >> $SCRIPT_FILE
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$MBOOT_BIN_SIZE+$PADDED_SIZE))
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade mboot"
    else
        echo "Your choice??"
    fi
}

# upgrade pm . if pm in SPI flash , we call spi wrc command to write PM51.bin data
function func_upgrade_pm(){
    read -p "Upgrade pm(PM51.bin)? (y/n)" temp

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade pm(PM51.bin)"

        PM_FLASH_TYPE=SPI
        PM_PARTITION_SIZE=0x200000

        printf "PM_FLASH_TYPE=$PM_FLASH_TYPE\n"
        printf "PM_PARTITION_SIZE=$PM_PARTITION_SIZE\n"

        PM_BIN=$IMAGE_DIR/PM51.bin

        if [ ! -f $PM_BIN ]; then
            echo "Error ,$PM_BIN not exist!!!"
            exit 0
        fi

        PM_BIN_SIZE=$(stat -c%s $PM_BIN )

        cat $PM_BIN >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($PM_BIN_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi
        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $PM_BIN_SIZE >> $SCRIPT_FILE
        printf "spi wrc %x 0 %x\n" $DRAM_FATLOAD_BUF_ADDR $PM_BIN_SIZE >> $SCRIPT_FILE

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$PM_BIN_SIZE+$PADDED_SIZE))
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade pm(PM51.bin)"
    else
        echo "Your choice??"
    fi
}

# upgrade certificate partition
function func_upgrade_certificate(){
    read -p "Update certifacate? (y/n)" temp

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade certificate"
        CERTIFICATE_PARTITION_NAME=certificate
        CERTIFICATE_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "certificate" | awk '{print $4;}'`
        CERTIFICATE_IMG=$IMAGE_DIR/certificate$IMAGE_EXT
        CERTIFICATE_IMG_SIZE=$(stat -c%s $CERTIFICATE_IMG )

        cat $CERTIFICATE_IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($CERTIFICATE_IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $CERTIFICATE_IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "$CMD_ERASE_PART certificate\n" >> $SCRIPT_FILE
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $CERTIFICATE_PARTITION_NAME $CERTIFICATE_IMG_SIZE >> $SCRIPT_FILE
        else
            printf "ubi part ubi\n" >> $SCRIPT_FILE
            printf "ubi remove %s\n" $CERTIFICATE_PARTITION_NAME >> $SCRIPT_FILE
            printf "ubi create %s %x\n" $CERTIFICATE_PARTITION_NAME $CERTIFICATE_PARTITION_SIZE >> $SCRIPT_FILE
            printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $CERTIFICATE_PARTITION_NAME $CERTIFICATE_IMG_SIZE >> $SCRIPT_FILE
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$CERTIFICATE_IMG_SIZE+$PADDED_SIZE))
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade certifacate"
    else
        echo "Your choice??"
    fi
}

# upgrade tee partition
function func_upgrade_tee(){
    #read -p "Update tee? (y/n)" temp
    temp=y

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade tee"
        UPGRADE_TEE_=1
        TEE_PARTITION_NAME=tee
        if [ "$ENABLE_SECUREBOOT" = "1" ]; then
            TEE_BIN=$IMAGE_DIR/tee.aes
        else
            TEE_BIN=$IMAGE_DIR/tee.bin
        fi
        TEE_BIN_SIZE=$(stat -c%s $TEE_BIN )

        cat $TEE_BIN >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($TEE_BIN_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $TEE_BIN_SIZE >> $SCRIPT_FILE
        printf "$CMD_ERASE_PART tee\n" >> $SCRIPT_FILE
        printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $TEE_PARTITION_NAME $TEE_BIN_SIZE >> $SCRIPT_FILE

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$TEE_BIN_SIZE+$PADDED_SIZE))

        #---------------------- upgrade tee secureinfo ---------------------
        if [ "$ENABLE_SECUREBOOT" == "1" ]; then
            TEE_SIGNATURE_BIN=$IMAGE_DIR/secure_info_tee.bin
            cat $TEE_SIGNATURE_BIN >>$TEMP_IMG
            SIGNATURE_IMG_SIZE=$(stat -c%s $TEE_SIGNATURE_BIN )

            # align image to 0x1000(4K)
            PADDED_SIZE=0
            NOT_ALAIN_IMAGE_SIZE=$(($SIGNATURE_IMG_SIZE & 0xfff))
            if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                for ((i=0; i<$PADDED_SIZE; i++))
                do
                    printf "\xff" >>$PADDED_BIN
                done

                if [ $PADDED_SIZE != 0 ]; then
                    cat $PADDED_BIN >>$TEMP_IMG
                    rm $PADDED_BIN
                fi
            fi
            printf "$TEE_SIGNATURE_BIN size: 0x%x\n" $SIGNATURE_IMG_SIZE
            printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SIGNATURE_IMG_SIZE >> $SCRIPT_FILE
            printf "store_secure_info teeSign %x \n" $DRAM_FATLOAD_BUF_ADDR >> $SCRIPT_FILE
            IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SIGNATURE_IMG_SIZE+$PADDED_SIZE))
        fi
        #---------------------- upgrade tee secureinfo ---------------------

        NUTTX_CONFIG_BIN=$IMAGE_DIR/nuttx_config.bin
        cat $NUTTX_CONFIG_BIN >>$TEMP_IMG
        NUTTX_CONFIG_BIN_SIZE=$(stat -c%s $NUTTX_CONFIG_BIN )

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($NUTTX_CONFIG_BIN_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done
            if [ $PADDED_SIZE != 0 ]; then
                cat $PADDED_BIN >>$TEMP_IMG
                rm $PADDED_BIN
            fi
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $NUTTX_CONFIG_BIN_SIZE >> $SCRIPT_FILE
        printf "store_nuttx_config NuttxConfig %x \n" $DRAM_FATLOAD_BUF_ADDR >> $SCRIPT_FILE
        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$NUTTX_CONFIG_BIN_SIZE+$PADDED_SIZE))
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade tee"
    else
        echo "Your choice??"
    fi
}

# upgrade systembackup partition
function func_upgrade_systembackup(){
    read -p "Upgrade systembackup? (y/n)" temp

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade systembackup"
        SYSTEMBACKUP_PARTITION_NAME=systembackup
        SYSTEMBACKUP_IMG_LZO=systembackup.img.lzo
        SYSTEMBACKUP_IMG=systembackup.img

        if [ "$FS_FORMAT" == "EXT4" ]; then
            # ----- ext4 filesystem

            # erase systembackup partition
            printf "$CMD_ERASE_PART systembackup\n" >> $SCRIPT_FILE

            # systembackup partition can be upgraded in 3 formats.
            # 1 unlzo.cont : lzo format and has been spilt
            # 2 unlzo : lzo format and not spilt
            # 3 write.p : image not compressed by lzo , and just like boot/recovery
            SPILT_FLAG=0
            UNLZO_FLAG=0
            WRITE_FLAG=0

            # check if spilt lzo format
            outline=`cat $SCRIPT_DIR/[[systembackup | grep -v "^ *#" | grep "mmc" | grep "systembackup" | grep "unlzo\.cont" `
            if [ "$outline" != "" ]; then
                SPILT_FLAG=1
            fi

            # check if lzo format
            outline=`cat $SCRIPT_DIR/[[systembackup | grep -v "^ *#" | grep "mmc" | grep "systembackup" | grep "unlzo" `
            if [ "$outline" != "" ]; then
                UNLZO_FLAG=1
            fi

            # check if image format
            outline=`cat $SCRIPT_DIR/[[systembackup | grep -v "^ *#" | grep "mmc" | grep "systembackup" | grep "write\.p" `
            if [ "$outline" != "" ]; then
                WRITE_FLAG=1
            fi
            if [ "$SPILT_FLAG" = "1" ]; then
                # systembackup image has been spilt
                SPILT_NUM=`cat $SCRIPT_DIR/[[systembackup | grep -v "^ *#" | grep "mmc" | grep "unlzo" | wc | awk '{print $1;}' `
                # write systembackup.imgxx.lzo to /usbflash/638Upgrade.bin
                for ((i=1; i<=$SPILT_NUM; i++))
                do
                    SYSTEMBACKUP_IMAGE_PART=`cat $SCRIPT_DIR/[[systembackup | grep -v "^ *#" | grep "tftp" | awk '{print $3;}' |awk 'NR=='$i''`
                    if [ -f $IMAGE_DIR/$SYSTEMBACKUP_IMAGE_PART ]; then
                        echo "get $IMAGE_DIR/$SYSTEMBACKUP_IMAGE_PART"
                        cat $IMAGE_DIR/$SYSTEMBACKUP_IMAGE_PART >>$TEMP_IMG

                        # align image to 0x1000(4K)
                        SYSTEMBACKUP_IMG_PART_SIZE=$(stat -c%s $IMAGE_DIR/$SYSTEMBACKUP_IMAGE_PART )
                        PADDED_SIZE=0
                        NOT_ALAIN_IMAGE_SIZE=$(($SYSTEMBACKUP_IMG_PART_SIZE & 0xfff))
                        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                            for ((j=0; j<$PADDED_SIZE; j++))
                            do
                                printf "\xff" >>$PADDED_BIN
                            done

                            cat $PADDED_BIN >>$TEMP_IMG
                            rm $PADDED_BIN
                        fi

                        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SYSTEMBACKUP_IMG_PART_SIZE >> $SCRIPT_FILE
                        if [ "$i" = "1" ]; then
                            printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEMBACKUP_IMG_PART_SIZE $SYSTEMBACKUP_PARTITION_NAME>> $SCRIPT_FILE
                        else
                            printf "mmc unlzo.cont %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEMBACKUP_IMG_PART_SIZE $SYSTEMBACKUP_PARTITION_NAME>> $SCRIPT_FILE
                        fi
                        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SYSTEMBACKUP_IMG_PART_SIZE+$PADDED_SIZE))
                    else
                        echo " Error , file : $IMAGE_DIR/$SYSTEMBACKUP_IMAGE_PART not exist"
                        exit 0
                    fi
                done
            elif [ "$UNLZO_FLAG" = "1" ]; then
                # systembackup upgrade by lzo format, and not spilt
                # check systembackup.img.lzo if exist
                if [ -f $SYSTEMBACKUP_IMG_LZO ]; then
                    echo "get $SYSTEMBACKUP_IMG_LZO"
                    cat $SYSTEMBACKUP_IMG_LZO >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    SYSTEMBACKUP_IMG_LZO_SIZE=$(stat -c%s $SYSTEMBACKUP_IMG_LZO )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($SYSTEMBACKUP_IMG_LZO_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((j=0; j<$PADDED_SIZE; j++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SYSTEMBACKUP_IMG_LZO_SIZE >> $SCRIPT_FILE
                    printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEMBACKUP_IMG_LZO_SIZE $SYSTEMBACKUP_PARTITION_NAME>> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SYSTEMBACKUP_IMG_LZO_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $SYSTEMBACKUP_IMG_LZO not exist"
                    exit 0
                fi
            elif [ "$WRITE_FLAG" = "1" ]; then
                # systembackup upgrade by image format, like boot and recovery
                # check systembackup.img if exist
                if [ -f $SYSTEMBACKUP_IMG ]; then
                    echo "get $SYSTEMBACKUP_IMG"
                    cat $SYSTEMBACKUP_IMG >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    SYSTEMBACKUP_IMG_SIZE=$(stat -c%s $SYSTEMBACKUP_IMG )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($SYSTEMBACKUP_IMG_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((i=0; i<$PADDED_SIZE; i++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SYSTEMBACKUP_IMG_SIZE >> $SCRIPT_FILE
                    printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEMBACKUP_PARTITION_NAME $SYSTEMBACKUP_IMG_SIZE >> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SYSTEMBACKUP_IMG_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $SYSTEMBACKUP_IMG not exist"
                    exit 0
                fi
            else
                # unknow upgrade format
                echo " unknow systembackup upgrade format"
                exit 1
            fi # if [ "$SPILT_FLAG" = "1" ]
        else # if [ "$FS_FORMAT" == "EXT4" ]
            # ubi filesystem
            if [ -f $SYSTEMBACKUP_IMG ]; then
                echo "get $SYSTEMBACKUP_IMG"
                cat $SYSTEMBACKUP_IMG >>$TEMP_IMG

                # align image to 0x1000(4K)
                SYSTEMBACKUP_IMG_SIZE=$(stat -c%s $SYSTEMBACKUP_IMG )
                PADDED_SIZE=0
                NOT_ALAIN_IMAGE_SIZE=$(($SYSTEMBACKUP_IMG_SIZE & 0xfff))
                if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                    PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                    for ((i=0; i<$PADDED_SIZE; i++))
                    do
                        printf "\xff" >>$PADDED_BIN
                    done

                    cat $PADDED_BIN >>$TEMP_IMG
                    rm $PADDED_BIN
                fi
                SYSTEMBACKUP_PARTITION_SIZE=0x40000000 # 1G
                printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $SYSTEMBACKUP_IMG_SIZE >> $SCRIPT_FILE
                printf "ubi part ubi\n" >> $SCRIPT_FILE
                printf "ubi remove %s\n" $SYSTEMBACKUP_PARTITION_NAME >> $SCRIPT_FILE
                printf "ubi create %s %x\n" $SYSTEMBACKUP_PARTITION_NAME $SYSTEMBACKUP_PARTITION_SIZE >> $SCRIPT_FILE
                printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $SYSTEMBACKUP_PARTITION_NAME $SYSTEMBACKUP_IMG_SIZE >> $SCRIPT_FILE
                IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$SYSTEMBACKUP_IMG_SIZE+$PADDED_SIZE))
            else
                echo " Error , file : $SYSTEMBACKUP_IMG not exist"
                exit 0
            fi
        fi # if [ "$FS_FORMAT" == "EXT4" ]
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade systembackup"
    else
        echo "Your choice??"
    fi
}

# EosTek Patch Begin
# upgrade vrsdcard partition
function func_upgrade_vrsdcard(){
    #read -p "Upgrade vrsdcard? (y/n)" temp
    temp=y

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade vrsdcard"
        #VRSDCARD_PARTITION_NAME=vrsdcard
        #VRSDCARD_IMG_LZO=vrsdcard.img.lzo
        #VRSDCARD_IMG=vrsdcard.img

        if [ "$FS_FORMAT" == "EXT4" ]; then
            # ----- ext4 filesystem

            # erase vrsdcard partition
            printf "$CMD_ERASE_PART vrsdcard\n" >> $SCRIPT_FILE

            # vrsdcard partition can be upgraded in 3 formats.
            # 1 unlzo.cont : lzo format and has been spilt
            # 2 unlzo : lzo format and not spilt
            # 3 write.p : image not compressed by lzo , and just like boot/recovery
            SPILT_FLAG=0
            UNLZO_FLAG=0
            WRITE_FLAG=0

            # check if spilt lzo format
            outline=`cat $SCRIPT_DIR/[[vrsdcard | grep -v "^ *#" | grep "mmc" | grep "vrsdcard" | grep "unlzo\.cont" `
            if [ "$outline" != "" ]; then
                SPILT_FLAG=1
            fi

            # check if lzo format
            outline=`cat $SCRIPT_DIR/[[vrsdcard | grep -v "^ *#" | grep "mmc" | grep "vrsdcard" | grep "unlzo" `
            if [ "$outline" != "" ]; then
                UNLZO_FLAG=1
            fi

            # check if image format
            outline=`cat $SCRIPT_DIR/[[vrsdcard | grep -v "^ *#" | grep "mmc" | grep "vrsdcard" | grep "write\.p" `
            if [ "$outline" != "" ]; then
                WRITE_FLAG=1
            fi
            if [ "$SPILT_FLAG" = "1" ]; then
                # vrsdcard image has been spilt
                SPILT_NUM=`cat $SCRIPT_DIR/[[vrsdcard | grep -v "^ *#" | grep "mmc" | grep "unlzo" | wc | awk '{print $1;}' `
                # write vrsdcard.imgxx.lzo to /usbflash/638Upgrade.bin
                for ((i=1; i<=$SPILT_NUM; i++))
                do
                    VRSDCARD_IMAGE_PART=`cat $SCRIPT_DIR/[[vrsdcard | grep -v "^ *#" | grep "tftp" | awk '{print $3;}' |awk 'NR=='$i''`
                    if [ -f $IMAGE_DIR/$VRSDCARD_IMAGE_PART ]; then
                        echo "get $IMAGE_DIR/$VRSDCARD_IMAGE_PART"
                        cat $IMAGE_DIR/$VRSDCARD_IMAGE_PART >>$TEMP_IMG

                        # align image to 0x1000(4K)
                        VRSDCARD_IMG_PART_SIZE=$(stat -c%s $IMAGE_DIR/$VRSDCARD_IMAGE_PART )
                        PADDED_SIZE=0
                        NOT_ALAIN_IMAGE_SIZE=$(($VRSDCARD_IMG_PART_SIZE & 0xfff))
                        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                            for ((j=0; j<$PADDED_SIZE; j++))
                            do
                                printf "\xff" >>$PADDED_BIN
                            done

                            cat $PADDED_BIN >>$TEMP_IMG
                            rm $PADDED_BIN
                        fi

                        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $VRSDCARD_IMG_PART_SIZE >> $SCRIPT_FILE
                        if [ "$i" = "1" ]; then
                            printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $VRSDCARD_IMG_PART_SIZE $VRSDCARD_PARTITION_NAME>> $SCRIPT_FILE
                        else
                            printf "mmc unlzo.cont %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $VRSDCARD_IMG_PART_SIZE $VRSDCARD_PARTITION_NAME>> $SCRIPT_FILE
                        fi
                        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$VRSDCARD_IMG_PART_SIZE+$PADDED_SIZE))
                    else
                        echo " Error , file : $IMAGE_DIR/$VRSDCARD_IMAGE_PART not exist"
                        exit 0
                    fi
                done
            elif [ "$UNLZO_FLAG" = "1" ]; then
                # vrsdcard upgrade by lzo format, and not spilt
                # check vrsdcard.img.lzo if exist
                if [ -f $VRSDCARD_IMG_LZO ]; then
                    echo "get $VRSDCARD_IMG_LZO"
                    cat $VRSDCARD_IMG_LZO >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    VRSDCARD_IMG_LZO_SIZE=$(stat -c%s $VRSDCARD_IMG_LZO )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($VRSDCARD_IMG_LZO_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((j=0; j<$PADDED_SIZE; j++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $VRSDCARD_IMG_LZO_SIZE >> $SCRIPT_FILE
                    printf "mmc unlzo %x %x %s 1\n" $DRAM_FATLOAD_BUF_ADDR $VRSDCARD_IMG_LZO_SIZE $VRSDCARD_PARTITION_NAME>> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$VRSDCARD_IMG_LZO_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $VRSDCARD_IMG_LZO not exist"
                    exit 0
                fi
            elif [ "$WRITE_FLAG" = "1" ]; then
                # vrsdcard upgrade by image format, like boot and recovery
                # check vrsdcard.img if exist
                if [ -f $VRSDCARD_IMG ]; then
                    echo "get $VRSDCARD_IMG"
                    cat $VRSDCARD_IMG >>$TEMP_IMG

                    # align image to 0x1000(4K)
                    VRSDCARD_IMG_SIZE=$(stat -c%s $VRSDCARD_IMG )
                    PADDED_SIZE=0
                    NOT_ALAIN_IMAGE_SIZE=$(($VRSDCARD_IMG_SIZE & 0xfff))
                    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                        for ((i=0; i<$PADDED_SIZE; i++))
                        do
                            printf "\xff" >>$PADDED_BIN
                        done

                        cat $PADDED_BIN >>$TEMP_IMG
                        rm $PADDED_BIN
                    fi

                    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $VRSDCARD_IMG_SIZE >> $SCRIPT_FILE
                    printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $VRSDCARD_PARTITION_NAME $VRSDCARD_IMG_SIZE >> $SCRIPT_FILE
                    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$VRSDCARD_IMG_SIZE+$PADDED_SIZE))
                else
                    echo " Error , file : $VRSDCARD_IMG not exist"
                    exit 0
                fi
            else
                # unknow upgrade format
                echo " unknow vrsdcard upgrade format"
                exit 1
            fi # if [ "$SPILT_FLAG" = "1" ]
        else # if [ "$FS_FORMAT" == "EXT4" ]
            # ubi filesystem
            if [ -f $VRSDCARD_IMG ]; then
                echo "get $VRSDCARD_IMG"
                cat $VRSDCARD_IMG >>$TEMP_IMG

                # align image to 0x1000(4K)
                VRSDCARD_IMG_SIZE=$(stat -c%s $VRSDCARD_IMG )
                PADDED_SIZE=0
                NOT_ALAIN_IMAGE_SIZE=$(($VRSDCARD_IMG_SIZE & 0xfff))
                if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
                    PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
                    for ((i=0; i<$PADDED_SIZE; i++))
                    do
                        printf "\xff" >>$PADDED_BIN
                    done

                    cat $PADDED_BIN >>$TEMP_IMG
                    rm $PADDED_BIN
                fi

                printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $VRSDCARD_IMG_SIZE >> $SCRIPT_FILE
                printf "ubi part ubi\n" >> $SCRIPT_FILE
                printf "ubi remove %s\n" $VRSDCARD_PARTITION_NAME >> $SCRIPT_FILE
                printf "ubi create %s %x\n" $VRSDCARD_PARTITION_NAME $VRSDCARD_PARTITION_SIZE >> $SCRIPT_FILE
                printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $VRSDCARD_PARTITION_NAME $VRSDCARD_IMG_SIZE >> $SCRIPT_FILE
                IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$VRSDCARD_IMG_SIZE+$PADDED_SIZE))
            else
                echo " Error , file : $VRSDCARD_IMG not exist"
                exit 0
            fi
        fi # if [ "$FS_FORMAT" == "EXT4" ]
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade vrsdcard"
    else
        echo "Your choice??"
    fi
}
# EosTek Patch End

# upgrade RTPM partition
function func_upgrade_RTPM(){
    #read -p "Update RTPM? (y/n)" temp
    temp=n

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade RTPM"
        RTPM_PARTITION_NAME=RTPM
        RTPM_PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "RTPM" | awk '{print $4;}'`
        RTPM_IMG=$IMAGE_DIR/RT_PM.bin
        RTPM_IMG_SIZE=$(stat -c%s $RTPM_IMG )

        cat $RTPM_IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($RTPM_IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $RTPM_IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "$CMD_ERASE_PART RTPM\n" >> $SCRIPT_FILE
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $RTPM_PARTITION_NAME $RTPM_IMG_SIZE >> $SCRIPT_FILE
        else
            printf "ubi part ubi\n" >> $SCRIPT_FILE
            printf "ubi remove %s\n" $RTPM_PARTITION_NAME >> $SCRIPT_FILE
            printf "ubi create %s %x\n" $RTPM_PARTITION_NAME $RTPM_PARTITION_SIZE >> $SCRIPT_FILE
            printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $RTPM_PARTITION_NAME $RTPM_IMG_SIZE >> $SCRIPT_FILE
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$RTPM_IMG_SIZE+$PADDED_SIZE))
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade RTPM"
    else
        echo "Your choice??"
    fi
}

# upgrade dtb partition
function func_upgrade_dtb(){
    #read -p "Update dtb? (y/n)" temp
    temp=y

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
        echo "YES, upgrade dtb"
        PARTITION_NAME=dtb
        PARTITION_SIZE=`cat $SCRIPT_DIR/set_partition | grep -v "^ *#" | grep "mmc" | grep "dtb" | awk '{print $4;}'`
        IMG_NAME=`cat $SCRIPT_DIR/[[dtb | grep -v "^ *#" | grep "tftp" | awk '{print $3;}'`
        IMG=$IMAGE_DIR/$IMG_NAME
        IMG_SIZE=$(stat -c%s $IMG)

        cat $IMG >>$TEMP_IMG

        # align image to 0x1000(4K)
        PADDED_SIZE=0
        NOT_ALAIN_IMAGE_SIZE=$(($IMG_SIZE & 0xfff))
        if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
            PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
            for ((i=0; i<$PADDED_SIZE; i++))
            do
                printf "\xff" >>$PADDED_BIN
            done

            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi

        printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $DRAM_FATLOAD_BUF_ADDR $IMAGE_OFFSET_IN_USB_IMG $IMG_SIZE >> $SCRIPT_FILE
        if [ "$FS_FORMAT" == "EXT4" ]; then
            printf "$CMD_ERASE_PART %s\n" $PARTITION_NAME >> $SCRIPT_FILE
            printf "mmc write.p %x %s %x 1\n" $DRAM_FATLOAD_BUF_ADDR $PARTITION_NAME $IMG_SIZE >> $SCRIPT_FILE
        else
            printf "ubi part ubi\n" >> $SCRIPT_FILE
            printf "ubi remove %s\n" $PARTITION_NAME >> $SCRIPT_FILE
            printf "ubi create %s %x\n" $PARTITION_NAME $PARTITION_SIZE >> $SCRIPT_FILE
            printf "ubi write %x %s %x\n" $DRAM_FATLOAD_BUF_ADDR $PARTITION_NAME $IMG_SIZE >> $SCRIPT_FILE
        fi

        IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$IMG_SIZE+$PADDED_SIZE))
    elif [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
        echo "NO, not upgrade dtb"
    else
        echo "Your choice??"
    fi
}

# upgrade env. we need to set env from set_config ,[[boot and [[recovery script
function func_upgrade_env(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
	# EosTek Patch Begin    
        temp="Y"
    else
        read -p "Update env? (y/n)" temp
    fi

    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
	# EosTek Patch End
        # get env from [[boot
        dos2unix $SCRIPT_DIR/[[boot 2>/dev/null
        exec<"$SCRIPT_DIR/[[boot"
        while read line
        do
            output=`echo $line | grep -v \# | grep -v ^\% | grep -v \%$ | grep setenv`
            if [ "$output" != "" ]; then
                outputnew=`echo "$output" | grep ';'`
                if [ "$outputnew" != "" ]; then
                    output=`echo $outputnew | sed s/';'/'\\\;'/g`
                fi

                printf "setenv filesize %x\n" $BOOT_IMG_SIZE >> $SCRIPT_FILE
                printf "$output\n" >> $SCRIPT_FILE
            fi
        done

        # get env from [[recovery
        dos2unix $SCRIPT_DIR/[[recovery 2>/dev/null
        exec<"$SCRIPT_DIR/[[recovery"
        while read line
        do
            output=`echo $line | grep -v \# | grep -v ^\% | grep -v \%$ | grep setenv`
            if [ "$output" != "" ]; then
                outputnew=`echo "$output" | grep ';'`
                if [ "$outputnew" != "" ]; then
                    output=`echo $outputnew | sed s/';'/'\\\;'/g`
                fi
                #printf "setenv filesize %x\n" $KERNEL_IMG_SIZE >> $SCRIPT_FILE
                printf "$output\n" >> $SCRIPT_FILE
            fi
        done

        # get env from [[tee
        if [ "$UPGRADE_TEE_" == "1" ]; then
            dos2unix $SCRIPT_DIR/[[tee 2>/dev/null
            exec<"$SCRIPT_DIR/[[tee"
            while read line
            do
                output=`echo $line | grep -v \# | grep -v ^\% | grep -v \%$ | grep setenv`
                if [ "$output" != "" ]; then
                    outputnew=`echo "$output" | grep ';'`
                    if [ "$outputnew" != "" ]; then
                        output=`echo $outputnew | sed s/';'/'\\\;'/g`
                    fi
                    printf "$output\n" >> $SCRIPT_FILE
                fi
            done
        fi

        # get env from set_config
        dos2unix $SCRIPT_DIR/set_config 2>/dev/null
        exec<"$SCRIPT_DIR/set_config"

        while read line
        do
            output=`echo $line | grep -v \# | grep -v ^\% | grep -v \%$`
            if [ "$output" != "" ]; then
                # if output have ";",add \ to output
                outputnew=`echo "$output" |grep "setenv" | grep ';'`
                if [ "$outputnew" != "" ]; then
                    output=`echo $outputnew | sed s/';'/'\\\;'/g`
                fi
                echo "$output" >> $SCRIPT_FILE
            fi
        done
    fi
}

# do some typical things for nand flash
function func_upgrade_nand(){
    echo "YES, upgrade NANDINFO.nni and PAIRPAGEMAP_v2.ppm for nand flash"
    NANDINFO_BIN=$IMAGE_DIR/NANDINFO.nni
    PAIRPAGEMAP_BIN=$IMAGE_DIR/PAIRPAGEMAP_v2.ppm
    NANDINFO_DRAM_ADDR="0x50400000"
    PAIRPAGEMAP_DRAM_ADDR="0x50500000"

    # ------------------------------ NANDINFO.nni ------------------------------
    cat $NANDINFO_BIN >>$TEMP_IMG
    NANDINFO_BIN_SIZE=$(stat -c%s $NANDINFO_BIN )
    PADDED_SIZE=0
    NOT_ALAIN_IMAGE_SIZE=$(($NANDINFO_BIN_SIZE & 0xfff))
    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
        for ((i=0; i<$PADDED_SIZE; i++))
        do
            printf "\xff" >>$PADDED_BIN
        done

        if [ $PADDED_SIZE != 0 ]; then
            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi
    fi

    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $NANDINFO_DRAM_ADDR $IMAGE_OFFSET_IN_USB_IMG $NANDINFO_BIN_SIZE >> $SCRIPT_FILE
    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$NANDINFO_BIN_SIZE+$PADDED_SIZE))

    # ---------------------------- PAIRPAGEMAP_v2.ppm --------------------------
    cat $PAIRPAGEMAP_BIN >>$TEMP_IMG
    PAIRPAGEMAP_BIN_SIZE=$(stat -c%s $PAIRPAGEMAP_BIN )
    PADDED_SIZE=0
    NOT_ALAIN_IMAGE_SIZE=$(($PAIRPAGEMAP_BIN_SIZE & 0xfff))
    if [ $NOT_ALAIN_IMAGE_SIZE != 0 ]; then
        PADDED_SIZE=$((0x1000-$NOT_ALAIN_IMAGE_SIZE))
        for ((i=0; i<$PADDED_SIZE; i++))
        do
            printf "\xff" >>$PADDED_BIN
        done

        if [ $PADDED_SIZE != 0 ]; then
            cat $PADDED_BIN >>$TEMP_IMG
            rm $PADDED_BIN
        fi
    fi

    printf "$FILE_PART_READ_CMD %x $OUTPUT_IMG %x %x\n" $PAIRPAGEMAP_DRAM_ADDR $IMAGE_OFFSET_IN_USB_IMG $PAIRPAGEMAP_BIN_SIZE >> $SCRIPT_FILE
    IMAGE_OFFSET_IN_USB_IMG=$(($IMAGE_OFFSET_IN_USB_IMG+$PAIRPAGEMAP_BIN_SIZE+$PADDED_SIZE))
    printf "ncisbl %x %x\n" $NANDINFO_DRAM_ADDR $PAIRPAGEMAP_DRAM_ADDR>> $SCRIPT_FILE
}

# upgrade partition .write commands of set_partition to /usbflash/638Upgrade.bin
function func_upgrade_partition(){
    if [ "$FULL_USB_UPDATE" == "1" ]; then
        dos2unix $SCRIPT_DIR/set_partition 2>/dev/null
        exec 6<&0 # Link file descriptor #6 with stdin. Saves stdin.
        exec<"$SCRIPT_DIR/set_partition"

        while read line
        do
            output=`echo $line | grep -v \# | grep -v \%`
            if [ "$output" != "" ]; then
                printf "$output\n" >> $SCRIPT_FILE
            fi
        done

        exec 0<&6 6<&- #Now restore stdin from fd #6, where it had been saved and close fd #6 ( 6<&- ) to free it for other processes to use.
        # printf "mmc part\n" >> $SCRIPT_FILE
    fi
}


#################################### part-11 ###################################
# main code , we can select which parition we want to upgrade.
# call functions to upgrade partitions. If need to upgrade a new parition , call
# corresponding function here
#==================================== Main =====================================
FULL_USB_UPDATE=1
#while true
#do
#    read -p "Upgrade all? (y/n)" temp
#    if [ "$temp" == "Y" ] || [ "$temp" == "y" ]; then
#        FULL_USB_UPDATE=1
#        break
#    fi
#    if [ "$temp" == "N" ] || [ "$temp" == "n" ]; then
#        FULL_USB_UPDATE=0
#        break
#    fi
#done

# ----------------------call function----------------------
func_print_selection;
func_init_common_env;
func_pre_process;
func_secure_pre_set;
func_get_partition_information;
func_get_image_information;

# upgrade partition
if [ "$FS_FORMAT" != "EXT4" ]; then
    # do things in writecis.txt
    func_upgrade_nand;
fi
func_upgrade_partition;
func_upgrade_recovery;
func_upgrade_boot;
func_upgrade_tee;
func_upgrade_system;
func_upgrade_userdata;
func_upgrade_cache;
func_upgrade_tvservice;
func_upgrade_tvconfig;
func_upgrade_tvdatabase;
func_upgrade_tvcustomer;
# EosTek Patch Begin
func_upgrade_vrsdcard;
# EosTek Patch End
func_upgrade_RTPM;
func_upgrade_dtb;
func_upgrade_mboot;
# upgrade env
func_upgrade_env;


func_finish_script;
func_pad_script;
func_post_process;

# ------------------------ copy the first 16 bytes to last ---------------------
dd if=$IMAGE_DIR/$OUTPUT_IMG of=$IMAGE_DIR/out.bin bs=16 count=1;
cat $IMAGE_DIR/out.bin >>$IMAGE_DIR/$OUTPUT_IMG
rm -rf $IMAGE_DIR/out.bin
# ----------------------- copy the first 16 bytes to last end ------------------

exit 0
