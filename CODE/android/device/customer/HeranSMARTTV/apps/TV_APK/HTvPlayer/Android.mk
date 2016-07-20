LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    $(call all-subdir-java-files) \
    $(call all-subdir-Iaidl-files)

LOCAL_JAVA_LIBRARIES := \
    com.mstar.android \
    scifly.android
	
LOCAL_STATIC_JAVA_LIBRARIES := \
    ImageLoad

LOCAL_PACKAGE_NAME := HTvPlayer
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_DEX_PREOPT := false

LOCAL_OVERRIDES_PACKAGES := TvPlayer MTvPlayer

include $(BUILD_PACKAGE)


################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    ImageLoad:libs/universal-image-loader.jar
	
include $(BUILD_MULTI_PREBUILT)

################################################
include $(call all-makefiles-under,$(LOCAL_PATH))
