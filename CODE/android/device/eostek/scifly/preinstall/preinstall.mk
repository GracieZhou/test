LOCAL_PATH := device/eostek/scifly/preinstall

# cpe bbfct
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/bbfct/bbfct.bin:system/etc/upnp/bbfct.bin \
    $(LOCAL_PATH)/bbfct/cpe_init.cfg:system/etc/cpe/cpe_init.cfg

# Predict BootVideo
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/bootVideo/video_predict:data/video/video_predict
