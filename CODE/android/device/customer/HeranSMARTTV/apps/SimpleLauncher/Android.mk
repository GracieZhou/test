LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    $(call all-subdir-java-files) \
    $(call all-subdir-Iaidl-files)

LOCAL_JAVA_LIBRARIES := \
    com.mstar.android \
    scifly.android
	
LOCAL_PACKAGE_NAME := SimpleLauncher
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
LOCAL_PROGUARD_ENABLED := disabled
#LOCAL_DEX_PREOPT := false

LOCAL_STATIC_JAVA_LIBRARIES := \
	ExtreamaxAPIH628s \
	GjsonH628s \
	QueryH628s 

include $(BUILD_PACKAGE)


################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	ExtreamaxAPIH628s:jars/ExtreamaxAPI-1.1.3.jar \
	GjsonH628s:jars/Gjson-2.1.jar \
	QueryH628s:jars/android-query-full.0.25.10.jar


include $(BUILD_MULTI_PREBUILT)

################################################
include $(call all-makefiles-under,$(LOCAL_PATH))