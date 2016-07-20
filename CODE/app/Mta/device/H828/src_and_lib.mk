$(shell cp $(LOCAL_PATH)/device/$(_DEVICE_)/amta_config.xml $(LOCAL_PATH)/assets/)

LOCAL_SRC_FILES += \
	$(call all-java-files-under, src/com/utsmta/mstar)

LOCAL_JAVA_LIBRARIES := \
    services \
    com.mstar.android \
	scifly.android