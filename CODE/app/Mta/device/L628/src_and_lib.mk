LOCAL_SRC_FILES += \
	$(call all-java-files-under, src/com/utsmta/mstar)

LOCAL_JAVA_LIBRARIES := \
    services \
    com.mstar.android \
	scifly.android
