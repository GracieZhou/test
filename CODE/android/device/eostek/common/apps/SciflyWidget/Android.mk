LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-subdir-java-files) \
    $(call all-subdir-Iaidl-files)

LOCAL_PACKAGE_NAME := SciflyWidgetHost
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform

LOCAL_JAVA_LIBRARIES := \
    scifly.android

LOCAL_STATIC_JAVA_LIBRARIES := \
    sciflydatacache

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under, $(LOCAL_PATH))