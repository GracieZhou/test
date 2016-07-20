
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_REQUIRED_MODULES := \
    scifly.android

LOCAL_JAVA_LIBRARIES := scifly.android

LOCAL_STATIC_JAVA_LIBRARIES := BaiduLBS_Android

LOCAL_32_BIT_ONLY := true
LOCAL_JNI_SHARED_LIBRARIES_ABI := arm
LOCAL_PREBUILT_JNI_LIBS := libs/armeabi/liblocSDK5.so

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_PACKAGE_NAME := LocationService
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_MODULE_TAGS := optional
LOCAL_DEX_PREOPT := false
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := BaiduLBS_Android:libs/BaiduLBS_Android.jar

include $(BUILD_MULTI_PREBUILT)

# ==============================================================================
include $(call all-makefiles-under,$(LOCAL_PATH))
