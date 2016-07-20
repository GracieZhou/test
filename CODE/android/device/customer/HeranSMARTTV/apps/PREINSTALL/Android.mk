
LOCAL_PATH := $(call my-dir)

# ==============================================================================
define PRESIGNED_preinstall_template
  LOCAL_MODULE := $(1)
  LOCAL_MODULE_CLASS := APPS
  LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
  LOCAL_CERTIFICATE := PRESIGNED
  LOCAL_MODULE_PATH := $(TARGET_OUT)/preinstall/
  LOCAL_SRC_FILES := $$(LOCAL_MODULE)$(COMMON_ANDROID_PACKAGE_SUFFIX)
  LOCAL_REQUIRED_MODULES := $(2)
  include $(BUILD_PREBUILT)
endef

define PRESIGNED_APP_preinstall_template
  include $(CLEAR_VARS)
  LOCAL_MODULE_TAGS := optional
  $(call PRESIGNED_preinstall_template, $(1), $(2))
endef

preinstall_apps := \
        #OTTKalaok
    
$(foreach app,$(preinstall_apps), \
	$(eval $(call PRESIGNED_APP_preinstall_template, $(app),)))

# ==============================================================================
include $(call all-makefiles-under,$(LOCAL_PATH))