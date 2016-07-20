LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := SciflyAdvertising
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := \
    scifly.android

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    sciflydatacache

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS)

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))