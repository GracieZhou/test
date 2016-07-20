LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-subdir-java-files) \
    $(call all-subdir-Iaidl-files)

LOCAL_JAVA_LIBRARIES := \
    services \
    scifly.android

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    guava 
    
LOCAL_PACKAGE_NAME := SciflyHistory
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

##### build static jar
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \

include $(BUILD_MULTI_PREBUILT)
