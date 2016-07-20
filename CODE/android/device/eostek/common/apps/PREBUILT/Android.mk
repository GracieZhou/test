LOCAL_PATH := $(call my-dir)

define PREBUILT_template
  LOCAL_MODULE := $(1)
  LOCAL_MODULE_CLASS := APPS
  LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
  LOCAL_CERTIFICATE := platform
  LOCAL_SRC_FILES := $$(LOCAL_MODULE)$(COMMON_ANDROID_PACKAGE_SUFFIX)
  LOCAL_REQUIRED_MODULES := $(2)
  include $(BUILD_PREBUILT)
endef

define PREBUILT_APP_template
  include $(CLEAR_VARS)
  LOCAL_MODULE_TAGS := optional
  $(call PREBUILT_template, $(1), $(2))
endef

prebuilt_apps := \
    FileFly \
    SciflyVideo \
    AnalyzerService \
    Mta \
    wps \
    FileBrowser \
    91Q 

$(foreach app,$(prebuilt_apps), \
    $(eval $(call PREBUILT_APP_template, $(app),)))
