LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
    src/com/android/systemui/EventLogTags.logtags

LOCAL_STATIC_JAVA_LIBRARIES := Keyguard
LOCAL_JAVA_LIBRARIES := telephony-common

# EosTek Patch Begin
LOCAL_PACKAGE_NAME := SciflySystemUI
LOCAL_OVERRIDES_PACKAGES := SystemUI
LOCAL_JAVA_LIBRARIES += scifly.android
# EosTek Patch End
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_RESOURCE_DIR := \
    frameworks/base/packages/Keyguard/res \
    $(LOCAL_PATH)/res
LOCAL_AAPT_FLAGS := --auto-add-overlay --extra-packages com.android.keyguard

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
