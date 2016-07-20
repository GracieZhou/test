LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src) 

LOCAL_JAVA_LIBRARIES := \
    scifly.android

LOCAL_STATIC_JAVA_LIBRARIES := \
	android-support-v4 \
	download \
	sciflydatacache  

LOCAL_PACKAGE_NAME := SciflyTheme

LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)



##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    download:./libs/download.jar

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
