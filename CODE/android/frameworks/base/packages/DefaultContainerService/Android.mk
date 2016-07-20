LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)
# EosTek Patch Begin
LOCAL_JAVA_LIBRARIES := \
    scifly.android
# EosTek Patch End
LOCAL_PACKAGE_NAME := DefaultContainerService

LOCAL_JNI_SHARED_LIBRARIES := libdefcontainer_jni

LOCAL_CERTIFICATE := platform

LOCAL_PRIVILEGED_MODULE := true

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
