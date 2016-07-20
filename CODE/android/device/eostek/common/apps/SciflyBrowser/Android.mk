LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := SciflyBrowser
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_OVERRIDES_PACKAGES := Browser MBrowser2 MBrowser3

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    src/com/android/browser/EventLogTags.logtags \
    src/com/android/localmmservice/ILocalMediaService.aidl \
    src/com/android/localmmservice/ILocalMediaStatusListener.aidl 
    

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-common \
    guava \
    android-support-v13 \
    android-support-v4 \
    sciflydatacache \
    qrcore \
    mstar-browser

LOCAL_JAVA_LIBRARIES := \
    android.policy \
    scifly.android

ifeq ($(PLATFORM_VERSION), $(filter $(PLATFORM_VERSION), 5.0 5.0.1 5.1.1)) 
    LOCAL_JNI_SHARED_LIBRARIES := libbrowser3util_jni
else
    LOCAL_JNI_SHARED_LIBRARIES := libbrowser2util_jni
endif

LOCAL_DEX_PREOPT := false

LOCAL_EMMA_COVERAGE_FILTER := *,-com.android.common.*

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    qrcore:libs/core.jar \
    mstar-browser:libs/mstar.jar

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under, $(LOCAL_PATH))
