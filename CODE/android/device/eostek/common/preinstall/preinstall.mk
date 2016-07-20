LOCAL_PATH := device/eostek/common/preinstall

# cpe
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/cpe/diagnostics.xsl:system/etc/cpe/diagnostics.xsl \
    $(LOCAL_PATH)/cpe/full_diagnostics.xml:system/etc/cpe/full_diagnostics.xml \
    $(LOCAL_PATH)/cpe/init_data.xml:system/etc/cpe/init_data.xml \
    $(LOCAL_PATH)/cpe/jrm-date.xml:system/etc/cpe/jrm-date.xml

# whitelist
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/whitelist/wm.xml:/data/system/whitelist/wm.xml \
    $(LOCAL_PATH)/whitelist/am.xml:/data/system/whitelist/am.xml \
    $(LOCAL_PATH)/whitelist/nm.xml:/data/system/whitelist/nm.xml

# defalut theme
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/theme/defaultTheme.zip:system/media/theme/defaultTheme.zip
    
# file-fly
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/upnp/container_config.properties:system/etc/upnp/container_config.properties \
    $(LOCAL_PATH)/upnp/tvos_ir_keys.xml:system/etc/upnp/tvos_ir_keys.xml \
	$(LOCAL_PATH)/upnp/dlna/AppManageControl.xml:system/etc/upnp/dlna/AppManageControl.xml \
    $(LOCAL_PATH)/upnp/dlna/AVTransport.xml:system/etc/upnp/dlna/AVTransport.xml \
    $(LOCAL_PATH)/upnp/dlna/BaBaoConnectionManagerExtension.xml:system/etc/upnp/dlna/BaBaoConnectionManagerExtension.xml \
    $(LOCAL_PATH)/upnp/dlna/BaBaoRenderingControlExtension.xml:system/etc/upnp/dlna/BaBaoRenderingControlExtension.xml \
    $(LOCAL_PATH)/upnp/dlna/ConnectionManager.xml:system/etc/upnp/dlna/ConnectionManager.xml \
    $(LOCAL_PATH)/upnp/dlna/ContentDirectory.xml:system/etc/upnp/dlna/ContentDirectory.xml \
    $(LOCAL_PATH)/upnp/dlna/DLNA.xml:system/etc/upnp/dlna/DLNA.xml \
    $(LOCAL_PATH)/upnp/dlna/DMR.xml:system/etc/upnp/dlna/DMR.xml \
    $(LOCAL_PATH)/upnp/dlna/DMS.xml:system/etc/upnp/dlna/DMS.xml \
    $(LOCAL_PATH)/upnp/dlna/RenderingControl.xml:system/etc/upnp/dlna/RenderingControl.xml \
    $(LOCAL_PATH)/upnp/dlna/VoiceSearchControl.xml:system/etc/upnp/dlna/VoiceSearchControl.xml \
    $(LOCAL_PATH)/upnp/web/TvAccelerometerInput.xml:system/etc/upnp/web/TvAccelerometerInput.xml \
    $(LOCAL_PATH)/upnp/web/TvDevice.xml:system/etc/upnp/web/TvDevice.xml \
    $(LOCAL_PATH)/upnp/web/TvKeyboardInput.xml:system/etc/upnp/web/TvKeyboardInput.xml \
    $(LOCAL_PATH)/upnp/web/TvKeyControl.xml:system/etc/upnp/web/TvKeyControl.xml \
    $(LOCAL_PATH)/upnp/web/TvMouseControl.xml:system/etc/upnp/web/TvMouseControl.xml \
    $(LOCAL_PATH)/upnp/web/TvMouseInput.xml:system/etc/upnp/web/TvMouseInput.xml \
    $(LOCAL_PATH)/upnp/web/TvOrientationInput.xml:system/etc/upnp/web/TvOrientationInput.xml \
    $(LOCAL_PATH)/upnp/web/TvTouchInput.xml:system/etc/upnp/web/TvTouchInput.xml \
    $(LOCAL_PATH)/upnp/iSynergy.idc:system/usr/idc/iSynergy.idc \
    $(LOCAL_PATH)/upnp/iSynergy.kl:system/usr/keylayout/iSynergy.kl \
    $(LOCAL_PATH)/upnp/iSynergy.kcm:system/usr/keychars/iSynergy.kcm \
    $(LOCAL_PATH)/upnp/dlna/libdlnajni.so:system/lib/libdlnajni.so

#snplus
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/snplus/snplus_config.ini:system/etc/snplus_config.ini

# Voice Control
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/libraries/VoiceControl/libmsc.so:system/lib/libmsc.so \
    $(LOCAL_PATH)/libraries/VoiceControl/libvoicesearch.so:system/lib/libvoicesearch.so
