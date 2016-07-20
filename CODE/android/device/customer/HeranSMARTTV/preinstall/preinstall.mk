LOCAL_PATH := device/customer/$(HERAN_DEVICE)/preinstall

# cpe bbfct
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/bbfct/bbfct.bin:system/etc/upnp/bbfct.bin \
    $(LOCAL_PATH)/bbfct/cpe_init.cfg:system/etc/cpe/cpe_init.cfg

	# virtual_mac
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/virtual_mac:data/local/virtual_mac

	# Predict BootVideo
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/bootVideo/video_predict:data/video/video_predict

#video.ts
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/bootVideo/video.ts:system/media/video/video.ts
	
# bootanimation
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/bootanimation/bootanimation.zip:system/media/bootanimation.zip \
    $(LOCAL_PATH)/bootanimation/firstbootanimation.zip:system/media/firstbootanimation.zip

# Mta
#PRODUCT_COPY_FILES += \
    #$(LOCAL_PATH)/mta/mta.properties:data/app/mta.properties

# ir ko
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/modules/rc-heran-tv.ko:system/lib/modules/rc-keymap-tv.ko \
    $(LOCAL_PATH)/modules/mdrv-ir-heran.ko:system/lib/modules/mdrv-ir.ko
