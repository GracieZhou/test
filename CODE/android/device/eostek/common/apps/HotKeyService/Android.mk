LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := HotKey2
LOCAL_MODULE_TAGS := optional

LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_JAVA_LIBRARIES := \
		com.mstar.android 

# Necessary to get BIND_APP_WIDGET permission
LOCAL_CERTIFICATE := shared

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS)

include $(BUILD_MULTI_PREBUILT)

# Build the tests as well.
include $(call all-makefiles-under, $(LOCAL_PATH))
