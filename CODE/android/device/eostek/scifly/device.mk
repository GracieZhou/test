
# This file includes all definitions that apply to all
# arbutus devices, include inherit-devices.
#
# This cleanly combines a set of device-specific configuration.


# dummy definitions to use += in later parts
PRODUCT_COPY_FILES :=
PRODUCT_PROPERTY_OVERRIDES :=
PRODUCT_PACKAGES :=


BUILD_NUMBER := v2.5.20.55954

# -----------------------------------------------------------------------
# Change on this file should go above this line

PRODUCT_COPY_FILES += \
    device/eostek/scifly/init.monet.rc:root/init.monet.rc \
    device/eostek/scifly/fstab.monet:root/fstab.monet

PRODUCT_PACKAGE_OVERLAYS += device/eostek/scifly/overlay
PRODUCT_DEFAULT_DEV_CERTIFICATE := device/eostek/scifly/signature/testkey

override PRODUCT_LOCALES := zh_CN en_US zh_TW fr_FR

PRODUCT_PROPERTY_OVERRIDES := \
    persist.sys.language=en \
    persist.sys.country=US \
    persist.sys.timezone=Asia/Shanghai
    
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
    persist.sys.ga.property.id=UA-62177441-9

PRODUCT_PROPERTY_OVERRIDES += \
    ro.eostek.tv=MST828 \
    ro.build.scifly=1 \
    ro.scifly.platform=tv \
    ro.scifly.forcelandscape=1 \
    ro.scifly.whitelist.enable=1 \
    ro.scifly.service.url=http://tvosapp.babao.com/interface/clientService.jsp

# Add by frank.zhang @2015-11-10 for Installing apk on USB
PRODUCT_PROPERTY_OVERRIDES += \
    ro.scifly.ApkOnUsb.disable=false


#Scifly Modify Version
PRODUCT_PROPERTY_OVERRIDES += \
    ro.scifly.version.alias=v2.5.11.51862
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

# executables of scifly
PRODUCT_PACKAGES += \
    eshell \
    upnp_tv_receiver    

# apk
PRODUCT_PACKAGES += \
    SciflyVideo \
    FileFly_Service \
    iSynergy-IM \
    Babaofan \
    MediaBrowser \
    wpsoffice \
    FileFly \
    SciflyVoiceController \
    VoiceSearch \
    StreamNetPlusService \
    Mta \
    DeviceManager \
    SetupWizard2 \
    AnalyzerService \
    SciflyLauncher \
    SciflyMessageCenter \
    SciflyProvider \
    SciflyHistory \
    SciflyWidgetHost \
    SciflyTheme \
    SciflyTaskSwitch \
    SciflyIme \
    SciflySystemUI \
    AnalyzerService \
    StatisticsManager \
    wps \
    SciflyAdvertising \
    OTTKalaok \
    SciflyCloudPhotoAlbum \
    91Q \
    FileBrowser \
    GoogleLoginService \
    GoogleServicesFramework \
    Phonesky \
    PrebuiltGmsCore \
    EostekDocumentUI \
    SciflyBrowser
    

# tv apk
PRODUCT_PACKAGES += \
    TvPlayer \
    HotKey \
    TvMenu \
    LocationService \
    SciflyPackageInstaller \
    SciflySettings \
    Misc
    
# Media
PRODUCT_PROPERTY_OVERRIDES += \
    mstar.mstplayer.bd=1 \
    testing.mediascanner.skiplist=/mnt/sdcard/play/,/mnt/sdcard/Android/data/
    
# ensure procrank added in TARGET_BUILD_VARIANT=user
PRODUCT_PACKAGES += \
	libpagemap \
	procrank \
    	sqlite3 \
    	su      
	  
#ensure debug is avaliable.
ADDITIONAL_DEFAULT_PROPERTIES += ro.debuggable=1


# inherit mstar arbutus device
$(call inherit-product, device/mstar/arbutus/device.mk)
$(call inherit-product-if-exists, device/eostek/scifly/preinstall/preinstall.mk)
$(call inherit-product, device/eostek/common/preinstall/preinstall.mk)
