
# This file includes all definitions that apply to all
# arbutus devices, include inherit-devices.
#
# This cleanly combines a set of device-specific configuration.
LOCAL_PATH := device/customer/$(HERAN_DEVICE)

# dummy definitions to use += in later parts
PRODUCT_COPY_FILES :=
PRODUCT_PROPERTY_OVERRIDES :=
PRODUCT_PACKAGES :=


BUILD_NUMBER := v2.5.28.57363

# -----------------------------------------------------------------------
# Change on this file should go above this line

PRODUCT_COPY_FILES += \
     $(LOCAL_PATH)/init.monet.rc:root/init.monet.rc \
     $(LOCAL_PATH)/fstab.monet:root/fstab.monet \
     $(LOCAL_PATH)/kernel:kernel

PRODUCT_PACKAGE_OVERLAYS += $(LOCAL_PATH)/overlay
PRODUCT_DEFAULT_DEV_CERTIFICATE := device/eostek/scifly/signature/testkey

override PRODUCT_LOCALES := en_US zh_TW

PRODUCT_PROPERTY_OVERRIDES := \
    persist.sys.language=zh \
    persist.sys.country=TW \
    persist.sys.timezone=Asia/Taipei
    
PRODUCT_PROPERTY_OVERRIDES += \
    ro.sf.lcd_density=240    
	
#ads
PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.ads.switch=0 \
    persist.sys.ads.cacheCompleted=0

#boot video report url
PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.service.url=http://tvosapp.babao.com:8081/wigAdmin/interface/bootVideoReport.jsp
    
#GA
PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.ga.property.id=UA-62177441-12

PRODUCT_PROPERTY_OVERRIDES += \
    ro.eostek.tv=MST828 \
    ro.build.scifly=1 \
    ro.scifly.platform=tv \
    ro.scifly.forcelandscape=1 \
    ro.scifly.whitelist.enable=1 \
    ro.scifly.service.url=http://app.heran.babao.com/interface/clientService.jsp

# Add by frank.zhang @2015-11-10 for Installing apk on USB
PRODUCT_PROPERTY_OVERRIDES += \
    ro.scifly.ApkOnUsb.disable=false

# STR
PRODUCT_PROPERTY_OVERRIDES += \
    mstar.str.enable=false

#Scifly Modify Version
PRODUCT_PROPERTY_OVERRIDES += \
    ro.scifly.version.alias = v2.5.28.0
# jar
PRODUCT_PACKAGES += \
  scifly.android \
  scifly-service
    
# library
PRODUCT_PACKAGES += \
    libAuthorizeNative20 \
    libstreamnet_jni \
    libdlnajni \
    libjingle_soapclient_jni \
    libvlcjni \
    libiomx-ics \
    libmsc \
    libvoicesearch \
    libext2_uuid \
    libairplay_jni \
    libbrowser3util_jni

# executables
PRODUCT_PACKAGES += \
    eshell \
    sqlite3 \
    upnp_tv_receiver \
    preinstall    

# apk
PRODUCT_PACKAGES += \
    SciflyVideo \
    SciflyEPGProvider \
    FileFly_Service \
    MediaBrowser \
    MediaBrowserBak \
    SciflyVoiceController \
    VoiceSearch \
    StreamNetPlusService \
    DeviceManager \
    SciflyMessageCenter \
    SciflyProvider \
    SciflyHistory \
    SciflyTaskSwitch \
    SciflyIme \
    SciflySystemUI \
    mta \
    eosWB \
    AnalyzerService \
    StatisticsManager \
    SciflyAdvertising \
    EostekDocumentUI 

# tv apk
PRODUCT_PACKAGES += \
    HTvPlayer \
    HHotKey \
    HTvMenu \
    HMisc \
    LocationService \
    SciflyPackageInstaller \
    SciflySettings \
    MKeyEventService

# heran apks
PRODUCT_PACKAGES += \
    iSynergy-IM \
    FileFly-H \
    HLauncher2 \
	SimpleLauncher \
    FaceDetection \
    HBabaofan \
    pandora \
    GoogleLoginService \
    GoogleServicesFramework \
    Phonesky \
    PrebuiltGmsCore \
    Instruction \
    RemoteControl \
    kok \
    miscupgrade

# Media
PRODUCT_PROPERTY_OVERRIDES += \
    mstar.mstplayer.bd=1 \
    testing.mediascanner.skiplist=/mnt/sdcard/play/,/mnt/sdcard/Android/data/

PRODUCT_PROPERTY_OVERRIDES += \
    mstar.forcelandscape=1	    
    
# ensure procrank added in TARGET_BUILD_VARIANT=user
PRODUCT_PACKAGES += \
	libpagemap \
	procrank \
    	sqlite3 \
    	su      
	  
#ensure debug is avaliable.
ADDITIONAL_DEFAULT_PROPERTIES += ro.debuggable=1


# inherit mstar arbutus device
$(call inherit-product-if-exists, $(LOCAL_PATH)/preinstall/preinstall.mk)
$(call inherit-product, device/mstar/arbutus/device.mk)
$(call inherit-product, device/eostek/common/preinstall/preinstall.mk)
