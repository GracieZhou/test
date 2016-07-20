LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-subdir-java-files) \
    src/com/android/packageinstaller/EventLogTags.logtags

LOCAL_STATIC_JAVA_LIBRARIES += android-support-v4

LOCAL_PACKAGE_NAME := SciflyPackageInstaller
LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES += scifly.android
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_OVERRIDES_PACKAGES :=PackageInstaller

include $(BUILD_PACKAGE)
