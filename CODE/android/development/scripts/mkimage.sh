#!/bin/bash

source ./development/scripts/build_image.sh

function make-images()
{
    mkdir -p $RELEASE_OUT
    mkdir -p $RELEASE_OUT/scripts

    gernerate-script-start

    # Raw partition must be first.
    make-raw-partition
    make-split-fs-partition system $BOARD_SYSTEMIMAGE_PARTITION_SIZE true
    make-fs-partition userdata $BOARD_USERDATAIMAGE_PARTITION_SIZE true
    make-fs-partition cache $BOARD_CACHEIMAGE_PARTITION_SIZE true
    make-fs-partition tvservice $BOARD_TVSERVICEIMAGE_PARTITION_SIZE false
    make-fs-partition tvconfig $BOARD_TVCONFIGIMAGE_PARTITION_SIZE false
    make-fs-partition tvdatabase $BOARD_TVDATABASEIMAGE_PARTITION_SIZE false
    make-fs-partition tvcustomer $BOARD_TVCUSTOMERIMAGE_PARTITION_SIZE false
    if [ "$BOARD_SYSTEMBACKUP" == "true" ]; then
        split-fs-partition systembackup $BOARD_SYSTEMBACKUPIMAGE_PARTITION_SIZE true
    fi
    if [ "$BOARD_CERTIFICATIONIMAGE" == "true" ]; then
        make-fs-partition certificate $BOARD_CERTIFICATEIMAGE_PARTITION_SIZE false
    fi
    # You can make-fs-partition() or make-split-fs-partition() here...
# EosTek Patch Begin
    if [ "$BOARD_NO_SDCARD" == "false" ]; then
        make-fs-partition vrsdcard $BOARD_VRSDCARD_PARTITION_SIZE true
    fi
# EosTek Patch End


    # Copy other images form device dir.
    if [ -d "$TARGET_DEVICE_DIR/images/prebuilts" ]; then
        echo -e "\033[31mCopy other images form device directory...\033[0m"
        cp -rf $TARGET_DEVICE_DIR/images/prebuilts/* $RELEASE_OUT
    fi

    gernerate-script-end
}


get-configs
make-images
echo -e "\033[31mRelease images to $RELEASE_OUT\033[0m"
