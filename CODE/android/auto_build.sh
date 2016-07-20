#!/bin/bash
#build path
ANDROID_TOP_DIR=$(pwd)
SVN_PATCH_DIR=$ANDROID_TOP_DIR/../svn-patch
ANDROID_PATCH_PATH=$SVN_PATCH_DIR/android
OUTPUT_POLICY_6A828_PATH=$SVN_PATCH_DIR/6A828
OUTPUT_POLICY_SCIFLY_PATH=$SVN_PATCH_DIR/scifly
UPDATE_OUTPUT_POLICY=No
UPDATE_BUILD_NUM=No
UPDATE_VERSION_MAJOR=

# svn urls
ANDROID_PATCH_URL=https://172.23.65.223:8000/svn/project/C_H_6A638_01/TRUNK/CODE/android
OUTPUT_POLICY_6A828_URL=https://172.23.65.223:8000/svn/project/I_6A828_01/DOC/OutputPolicy
OUTPUT_POLICY_SCIFLY_URL=https://172.23.65.223:8000/svn/project/I_Scifly_UI_02/DOC/06%20Output%20Policy

#base package
I_6A828_A15_BASE_PACKAGE_PATH=/home/H828_A15/code/I_6A828_A15.2015.07.15.tar.bz2

#build log
BUILD_LOG_PATH=$ANDROID_TOP_DIR/[$(date +%T-%Y-%m-%d)]-build.log

# build settings
DEFAULT_PRODUCT=
DEFAULT_BRAND=
DEFAULT_DEVICE=
DEFAULT_LUNCH=
DEFAULT_OUT_DIR=

# BUILD_NUM
OLD_BUILD_NUM=
NEW_BUILD_NUM=
VERSION_CONTROL_FILE=
OTA_FROM_VERSION_FILE=

function init_build_settings() {
    echo "=========================================================================";
    echo "Please notice the three setps"
    echo "1. choose the device you will build"
    echo "2. choose update output policy or not"
    echo "3. choose update the BUILD_NUMBER to svn or not"
    echo "=========================================================================";
    echo "";

    DEFAULT_PRODUCT=Heran_arbutus
    DEFAULT_BRAND=customer
    DEFAULT_DEVICE=HeranSMARTTV
    DEFAULT_LUNCH=Heran_arbutus-userdebug
    
    DEFAULT_OUT_DIR=$ANDROID_TOP_DIR/out/target/product/$DEFAULT_DEVICE
    
    read -p "Step2: Update output policy ?  [y/n] :" tmp
    if [ "$tmp" == "y" ]; then
        UPDATE_OUTPUT_POLICY=Yes
        VERSION_CONTROL_FILE=$ANDROID_PATCH_PATH/device/$DEFAULT_BRAND/$DEFAULT_DEVICE/device.mk
        OTA_FROM_VERSION_FILE=$ANDROID_PATCH_PATH/device/$DEFAULT_BRAND/$DEFAULT_DEVICE/BoardConfig.mk
    fi
    
    read -p "Step3: Update  BUILD_NUMBER to svn ?  [y/n] :" tmp
    if [ "$tmp" == "y" ]; then
        UPDATE_BUILD_NUM=Yes
        read -p "Please input the major build version[eg:2.5.9] :" tmp1
        if [ -n "$tmp1" ]; then
            UPDATE_VERSION_MAJOR=$tmp1
        else
            echo "Do not update major version in svn."
        fi
    fi
    
}

#####################################################################################
if [ $# -eq "0" ]; then
    init_build_settings;
else
	echo $0 $*
	# [-b build number update y/n] [-d device] 
    # [-o output policy update y/n] [-m major version]
    while getopts ":b:d:o:m:" opt
    do
	    case $opt in
		    m) 
			    if [ "$UPDATE_BUILD_NUM" == "Yes" ]; then
			        UPDATE_VERSION_MAJOR=$OPTARG
			    fi
			    ;;
		    d) 
			    if [ "$OPTARG" == "pitaya" ]; then
                    DEFAULT_PRODUCT=aosp_pitaya
                    DEFAULT_BRAND=mstar
                    DEFAULT_DEVICE=pitaya
                    DEFAULT_LUNCH=aosp_pitaya-userdebug
                elif [ "$OPTARG" == "scifly" ]; then
                    DEFAULT_PRODUCT=scifly_pitaya
                    DEFAULT_BRAND=eostek
                    DEFAULT_DEVICE=scifly
                    DEFAULT_LUNCH=scifly_pitaya-userdebug
                elif [ "$temp" == "leader" ]; then
                    DEFAULT_PRODUCT=Leader_pitaya
                    DEFAULT_BRAND=customer
                    DEFAULT_DEVICE=LeaderSMARTTV
                    DEFAULT_LUNCH=Leader_pitaya-userdebug
                elif [ "$temp" == "heran" ]; then
                    DEFAULT_PRODUCT=Heran_arbutus
                    DEFAULT_BRAND=customer
                    DEFAULT_DEVICE=HeranSMARTTV
                    DEFAULT_LUNCH=Heran_arbutus-userdebug
                else
                    echo "You must choose a Device to finish build settings."
                    exit -1
                fi
                DEFAULT_OUT_DIR=$ANDROID_TOP_DIR/out/target/product/$DEFAULT_DEVICE
                
			    ;;
		    b) 
			    if [ "$OPTARG" == "y" ]; then
			        UPDATE_BUILD_NUM=Yes
			    fi
			    ;;
		    o) 
			    if [ "$OPTARG" == "y" ]; then
			        UPDATE_OUTPUT_POLICY=Yes
			        VERSION_CONTROL_FILE=$ANDROID_PATCH_PATH/device/$DEFAULT_BRAND/$DEFAULT_DEVICE/device.mk
                    OTA_FROM_VERSION_FILE=$ANDROID_PATCH_PATH/device/$DEFAULT_BRAND/$DEFAULT_DEVICE/BoardConfig.mk			        
			    fi
			    ;;
		    ?) 
			    echo "Unimplemented option chosen.";;

	    esac
done
	
fi

echo "";
echo "*************************************************************************";
echo "Your choice:"
echo "Brand: $DEFAULT_BRAND"
echo "Device: $DEFAULT_DEVICE"
echo "lunch: $DEFAULT_LUNCH"
echo "out: $DEFAULT_OUT_DIR"
echo "Update output policy: $UPDATE_OUTPUT_POLICY"
echo "Update BUILD_NUMBER to svn: $UPDATE_BUILD_NUM"
if [ -n "$UPDATE_VERSION_MAJOR" ]; then
        echo "Update major version to: $UPDATE_VERSION_MAJOR"
fi
echo "*************************************************************************";
echo "";
echo "";
###################################################################################


function prepare_patch() {
    echo "Prepare Patch: $2 to $1"
    mkdir -p $1
    svn checkout $2 $1 > /dev/null;
}

function merge() {
    echo "=========================================================================";
    echo "merge patch now ..."
    
    rm -rf $ANDROID_TOP_DIR/device/$DEFAULT_BRAND
    
    if [ -d "$ANDROID_TOP_DIR/device/customer" ]; then
        rm -rf $ANDROID_TOP_DIR/device/customer
    fi
	
	if [ -d "$ANDROID_TOP_DIR/device/eostek" ]; then
        rm -rf $ANDROID_TOP_DIR/device/eostek
    fi
    
    find $SVN_PATCH_DIR -name "*.svn"  | xargs rm -rf 
    cp -rf $ANDROID_PATCH_PATH/* $ANDROID_TOP_DIR
    
    echo "merge patch done ..."
    echo "=========================================================================";
    echo "";
}

function update_output_policy() {
    echo "=========================================================================";
    echo "updage output policy now ..."
    prepare_patch $OUTPUT_POLICY_6A828_PATH $OUTPUT_POLICY_6A828_URL;
    prepare_patch $OUTPUT_POLICY_SCIFLY_PATH $OUTPUT_POLICY_SCIFLY_URL;

    # CPE
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/CPE/libraries/cpe_jar.jar \
           $ANDROID_PATCH_PATH/device/eostek/common/libraries/SciflyUI/libs/cpe/cpe_jar.jar

    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/CPE/preinstall/jrm-date.xml \
           $ANDROID_PATCH_PATH/device/eostek/common/preinstall/cpe/jrm-date.xml
    
	cp -rf $OUTPUT_POLICY_SCIFLY_PATH/CPE/preinstall/init_data.xml \
           $ANDROID_PATCH_PATH/device/eostek/common/preinstall/cpe/init_data.xml
		   
	# EosPlayer
	cp -rf $OUTPUT_POLICY_SCIFLY_PATH/EosMedia/libraries_mstar/EosPlayer.jar \
           $ANDROID_PATCH_PATH/device/eostek/common/libraries/SciflyUI/libs/media/EosPlayer.jar
	

    # Babaofan
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/AppStore/apps/Babaofan.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/Babaofan/Babaofan.apk
    
    # FileFly-RK3066
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/apps/FileFly.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/PREBUILT/FileFly.apk
		   
	 # FileFly-H
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/H828-apps/FileFly-H.apk \
           $ANDROID_PATCH_PATH/device/customer/HeranSMARTTV/apps/PREBUILT/FileFly-H.apk	   
		   
    # SciflyVideo
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/SciflyVideo/apps/SciflyVideo.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/PREBUILT/SciflyVideo.apk
		   
	 # SciflyEPGProvider
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/SciflyEpgProvider/SciflyEPGProvider.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/PREBUILT/SciflyEPGProvider.apk
           
    # AnalyzerService
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/AnalyzerService/apps/AnalyzerService.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/PREBUILT/AnalyzerService.apk
    
    # FileFly_Service
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/apps/FileFly_Service.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/FileFly_Service/FileFly_Service.apk
	
    # iSynergy-IM       
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/app_noicon/iSynergy-IM.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/iSynergy-IM/iSynergy-IM.apk
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/libraries/libjingle_soapclient_jni.so \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/iSynergy-IM/libs/libjingle_soapclient_jni.so
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/libraries/libcaptetown.so \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/iSynergy-IM/libs/libcaptetown.so
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/libraries/libchinalink.so \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/iSynergy-IM/libs/libchinalink.so
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/libraries/libsystem.so \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/iSynergy-IM/libs/libsystem.so
    
    # MediaBrowser
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/MediaBrowser/apps/MediaBrowser.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/MediaBrowser/MediaBrowser.apk
           
    # StreamNet+
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/StreamNet+/apps/StreamNetPlusService.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/StreamNetPlusService/StreamNetPlusService.apk
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/StreamNet+/libraries/libstreamnet_jni.so \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/StreamNetPlusService/libs/libstreamnet_jni.so
    
    # libdlnajni
    cp -rf $OUTPUT_POLICY_SCIFLY_PATH/iSynergy/libraries/libdlnajni.so \
           $ANDROID_PATCH_PATH/device/eostek/common/preinstall/upnp/dlna/libdlnajni.so
    
    # 828 output policy       
    cp -rf $OUTPUT_POLICY_6A828_PATH/iSynergy/exe/upnp_tv_receiver \
           $ANDROID_PATCH_PATH/device/eostek/common/executables/upnp/upnp_tv_receiver
    cp -rf $OUTPUT_POLICY_6A828_PATH/iSynergy/so/libairplay_jni.so \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/FileFly_Service/libs/libairplay_jni.so
           
    # SciflyWidgetHost
    cp -rf $OUTPUT_POLICY_6A828_PATH/SciflyWidgetHost/apps/SciflyWidgetHost.apk \
           $ANDROID_PATCH_PATH/device/eostek/common/apps/PREBUILT/SciflyWidgetHost.apk
    
    # unpn config       
    cp -rf $OUTPUT_POLICY_6A828_PATH/iSynergy/config_H828/iSynergy.idc \
           $ANDROID_PATCH_PATH/device/eostek/common/preinstall/upnp/iSynergy.idc
    cp -rf $OUTPUT_POLICY_6A828_PATH/iSynergy/config_H828/iSynergy.kcm \
           $ANDROID_PATCH_PATH/device/eostek/common/preinstall/upnp/iSynergy.kcm
    cp -rf $OUTPUT_POLICY_6A828_PATH/iSynergy/config_H828/iSynergy.kl \
           $ANDROID_PATCH_PATH/device/eostek/common/preinstall/upnp/iSynergy.kl
    cp -rf $OUTPUT_POLICY_6A828_PATH/iSynergy/config_H828/tvos_ir_keys.xml \
           $ANDROID_PATCH_PATH/device/eostek/common/preinstall/upnp/tvos_ir_keys.xml
	cp -rf $OUTPUT_POLICY_6A828_PATH/iSynergy/config_H828/TvKeyControl.xml \
           $ANDROID_PATCH_PATH/device/eostek/common/preinstall/upnp/web/TvKeyControl.xml
    
    # commit android patch to svn
    cd $ANDROID_PATCH_PATH
    local curr_time=$(date +%T\ %Y-%m-%d)
    svn commit -m "[Auto commit output policy]==> $curr_time" >> version.log
    echo "###########################################" >> version.log
    
    OLD_BUILD_NUM=$(grep BUILD_NUMBER $VERSION_CONTROL_FILE| sed 's/BUILD_NUMBER := v//')
    local ver_major_prefix=$(grep BUILD_NUMBER $VERSION_CONTROL_FILE| sed 's/BUILD_NUMBER := v//' | sed 's/\./x/3'|sed 's/x.*/./')
    local ver_minor=$(svn info $ANDROID_PATCH_URL| sed -n 's/Revision: //p')
    
    if [ -n "$UPDATE_VERSION_MAJOR" ]; then
        NEW_BUILD_NUM=$UPDATE_VERSION_MAJOR.$ver_minor
    else
        NEW_BUILD_NUM=$ver_major_prefix$ver_minor 
    fi
    
    # change the version in local files
    sed -i "s/BUILD_NUMBER.*/BUILD_NUMBER := v$NEW_BUILD_NUM/g" $VERSION_CONTROL_FILE
	sed -i "s/ro.scifly.version.alias.*/ro.scifly.version.alias = v$UPDATE_VERSION_MAJOR.0/g" $VERSION_CONTROL_FILE
    sed -i "s/target_files-v.*/target_files-v$OLD_BUILD_NUM.zip/g" $OTA_FROM_VERSION_FILE
    
    if [ "$UPDATE_BUILD_NUM" == "Yes" ]; then
        echo "Upgrade from v$OLD_BUILD_NUM to v$NEW_BUILD_NUM ..."
        svn commit -m "[Auto commit version control]==>$NEW_BUILD_NUM" >> version.log
    fi
    echo $NEW_BUILD_NUM
    echo "updage output policy done ..."
    echo "=========================================================================";
    echo "";
}


function prepare_low_target_file() {
    if [ -d "$ANDROID_TOP_DIR/../$OLD_BUILD_NUM" ]; then
        cp $ANDROID_TOP_DIR/../$OLD_BUILD_NUM/${DEFAULT_PRODUCT}-target_files-v${OLD_BUILD_NUM}.zip \
            $ANDROID_TOP_DIR/../images/lollipop/$DEFAULT_DEVICE/
    fi
}

function build_target() {
    echo "=========================================================================";
    echo "starting build ..."
    
    cd $ANDROID_TOP_DIR
    set -e;
    source ./build/envsetup.sh && lunch $DEFAULT_LUNCH;
	make clean -j24  > /dev/null 2>$BUILD_LOG_PATH;
	make update-api -j32  > /dev/null 2>$BUILD_LOG_PATH;
    make -j32  > /dev/null 2>$BUILD_LOG_PATH;
	./development/scripts/releaseimage.sh;
	make otapackage -j32  > /dev/null 2>$BUILD_LOG_PATH;
	./development/scripts/make_usb_upgrade_auto.sh;
    #prepare_low_target_file;
    #make incrementalotapackage -j24  > /dev/null 2>$BUILD_LOG_PATH;
    set +e;
    
    echo "build done ..."
    echo "=========================================================================";
    echo "";
}

function clean_out() {
    echo "=========================================================================";
    echo "clean project now ..."
    
    rm -rf $SVN_PATCH_DIR
    
    if [ -d "$DEFAULT_OUT_DIR/system" ]; then
        cd $DEFAULT_OUT_DIR
        rm -rf *.img* *.bin tv* root recovery tee.* system data cache fake_packages *.txt
        rm -rf obj/APPS obj/PACKAGING obj/JAVA_LIBRARIES
    fi
    
    if [ -d "$DEFAULT_OUT_DIR/../../common/obj" ]; then
        cd $DEFAULT_OUT_DIR/../../common
        rm -rf *
        cd $ANDROID_TOP_DIR
    fi
    
    if [ -d "$ANDROID_TOP_DIR/../images/lollipop/$DEFAULT_DEVICE" ]; then
        rm -rf $ANDROID_TOP_DIR/../images/lollipop/${DEFAULT_DEVICE}.old
        mv $ANDROID_TOP_DIR/../images/lollipop/$DEFAULT_DEVICE $ANDROID_TOP_DIR/../images/lollipop/${DEFAULT_DEVICE}.old
    fi
    
    echo "clean project done ..."
    echo "=========================================================================";
    echo "";
}

function release_image() {
    echo "=========================================================================";
    echo "release image now ..."

    mkdir -p $ANDROID_TOP_DIR/../$NEW_BUILD_NUM
    cp -rf $ANDROID_TOP_DIR/../images/lollipop/$DEFAULT_DEVICE/* $ANDROID_TOP_DIR/../$NEW_BUILD_NUM
    cp -rf $DEFAULT_OUT_DIR/obj/PACKAGING/target_files_intermediates/${DEFAULT_PRODUCT}-target_files-v${NEW_BUILD_NUM}.zip \
            $ANDROID_TOP_DIR/../$NEW_BUILD_NUM
    cp -rf $DEFAULT_OUT_DIR/${DEFAULT_PRODUCT}-ota-v${NEW_BUILD_NUM}.zip $ANDROID_TOP_DIR/../$NEW_BUILD_NUM
    cp -rf $DEFAULT_OUT_DIR/incremental-${DEFAULT_PRODUCT}-ota-v${NEW_BUILD_NUM}.zip $ANDROID_TOP_DIR/../$NEW_BUILD_NUM
    
    cd $ANDROID_TOP_DIR/..
    zip ${DEFAULT_PRODUCT}-${NEW_BUILD_NUM}.zip -r $NEW_BUILD_NUM
    mv ${DEFAULT_PRODUCT}-${NEW_BUILD_NUM}.zip $NEW_BUILD_NUM
    
    echo "release image done ..."
    echo "=========================================================================";
    echo "";
}

function main() {
    #init_build_settings;
    
    if [ "$DEFAULT_DEVICE" == "pitaya" ]; then
        clean_out;
        build_target;
    elif [ "$DEFAULT_DEVICE" == "scifly" ]; then
        clean_out;
        prepare_patch $ANDROID_PATCH_PATH $ANDROID_PATCH_URL;
        
        if [ "$UPDATE_OUTPUT_POLICY" == "Yes" ]; then
            update_output_policy;
        fi
        
        merge;
        build_target;
        release_image;
    elif [ "$DEFAULT_DEVICE" == "LeaderSMARTTV" ]; then
        clean_out;
        prepare_patch $ANDROID_PATCH_PATH $ANDROID_PATCH_URL;
        
        if [ "$UPDATE_OUTPUT_POLICY" == "Yes" ]; then
            update_output_policy;
        fi
        
        merge;
        build_target;
        release_image;
    elif [ "$DEFAULT_DEVICE" == "HeranSMARTTV" ]; then
        clean_out;
        prepare_patch $ANDROID_PATCH_PATH $ANDROID_PATCH_URL;
        
        if [ "$UPDATE_OUTPUT_POLICY" == "Yes" ]; then
            update_output_policy;
        fi
        
        merge;
        build_target;
        release_image;
    else
        echo "[main] You must choose a Device to finish build settings."
        exit -1
    fi
}

######################################################
main;
######################################################
