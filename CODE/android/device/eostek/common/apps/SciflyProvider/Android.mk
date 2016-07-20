LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-subdir-java-files) \
    $(call all-subdir-Iaidl-files)

LOCAL_PACKAGE_NAME := SciflyProvider
LOCAL_CERTIFICATE := platform

LOCAL_JAVA_LIBRARIES := \
    scifly.android \
    services

include $(BUILD_PACKAGE)

########################
include $(call all-makefiles-under,$(LOCAL_PATH))
