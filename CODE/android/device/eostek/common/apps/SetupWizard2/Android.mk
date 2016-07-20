LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := \
android-support-v4


LOCAL_SRC_FILES := \
    $(call all-subdir-java-files) \
    $(call all-subdir-Iaidl-files) \
    $(call all-java-files-under,src/scifly/middleware/network)

LOCAL_JAVA_LIBRARIES := scifly.android
LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    commonUtils

ifneq ($(BUILD_MSTARTV),)
    LOCAL_JAVA_LIBRARIES += com.mstar.android
endif

ifeq ($(TARGET_BOARD_PLATFORM),madison)
$(warning *** TARGET_BOARD_PLATFORM "madison")
FILTER_OUT_SRC_FILES := $(call all-java-files-under,src/scifly/middleware/network/mstar/lollipop) \
                        $(call all-java-files-under,src/scifly/middleware/network/amlogic/kitkat)
LOCAL_SRC_FILES := $(filter-out $(FILTER_OUT_SRC_FILES),$(LOCAL_SRC_FILES))

else ifeq ($(TARGET_BOARD_PLATFORM),muji)
$(warning *** TARGET_BOARD_PLATFORM "muji")
FILTER_OUT_SRC_FILES := $(call all-java-files-under,src/scifly/middleware/network/mstar/kitkat) \
                        $(call all-java-files-under,src/scifly/middleware/network/amlogic/kitkat)
LOCAL_SRC_FILES := $(filter-out $(FILTER_OUT_SRC_FILES),$(LOCAL_SRC_FILES))

else ifeq ($(TARGET_BOARD_PLATFORM),meson8)
$(warning *** TARGET_BOARD_PLATFORM "dongle")
FILTER_OUT_SRC_FILES := $(call all-java-files-under,src/scifly/middleware/network/mstar) 
LOCAL_SRC_FILES := $(filter-out $(FILTER_OUT_SRC_FILES),$(LOCAL_SRC_FILES))

else ifeq ($(TARGET_BOARD_PLATFORM),monet)
$(warning *** TARGET_BOARD_PLATFORM "monet")
FILTER_OUT_SRC_FILES := $(call all-java-files-under,src/scifly/middleware/network/mstar/kitkat) \
                        $(call all-java-files-under,src/scifly/middleware/network/amlogic/kitkat)
LOCAL_SRC_FILES := $(filter-out $(FILTER_OUT_SRC_FILES),$(LOCAL_SRC_FILES))
endif

LOCAL_PACKAGE_NAME := SetupWizard2
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_DEX_PREOPT := false
LOCAL_OVERRIDES_PACKAGES := MSetupWizard SetupWizard
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_32_BIT_ONLY := true
LOCAL_JNI_SHARED_LIBRARIES_ABI := arm
LOCAL_PREBUILT_JNI_LIBS := libs/libSetMeUpServer.so

include $(BUILD_PACKAGE)

##### build static jar
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    commonUtils:./libs/commonUtilsV_05_15.jar
include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))


