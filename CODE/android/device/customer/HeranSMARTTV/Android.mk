ifneq ($(filter $(HERAN_DEVICE),$(TARGET_DEVICE)),)
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_COPY_HEADERS := \
    keypad.h \
    spi_config.h
include $(BUILD_COPY_HEADERS)

# ==============================================================================
include $(CLEAR_VARS)
LOCAL_MODULE := remove_unused_apps

LOCAL_MODULE_CLASS := FAKE
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_OVERRIDES_PACKAGES := \
    OpenWnn \
    Settings \
    WAPPushManager \
    TelephonyProvider \
    Telecom \
    MmsService \
    DMS

include $(BUILD_SYSTEM)/base_rules.mk

$(LOCAL_BUILT_MODULE):
	$(hide) echo "Fake: $@"
	$(hide) mkdir -p $(dir $@)
	$(hide) touch $@

PACKAGES.$(LOCAL_MODULE).OVERRIDES := $(strip $(LOCAL_OVERRIDES_PACKAGES))

# ==============================================================================
include $(call all-makefiles-under,$(LOCAL_PATH))
include $(call all-makefiles-under,device/mstar/arbutus)
endif
