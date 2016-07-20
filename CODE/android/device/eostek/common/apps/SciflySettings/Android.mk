
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := bouncycastle conscrypt telephony-common
LOCAL_STATIC_JAVA_LIBRARIES := \
android-support-v4 android-support-v13 jsr305 gson volley \
nineoldanimation

LOCAL_JAVA_LIBRARIES = scifly.android

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
	src/com/android/settings/androidbug/AndroidBug5497Workaround.java \
	src/com/android/settings/SimpleSettingsUpgradeActivity.java \
    src/com/android/settings/BaseSettingActivity.java \
    src/com/android/settings/SettingsActivity.java \
    src/com/android/settings/DisplayAndSoundActivity.java \
    src/com/android/settings/SettingsApplication.java \
    src/com/android/settings/SettingsHolder.java \
    src/com/android/settings/SettingsPreferenceFragment.java \
    src/com/android/settings/DialogCreatable.java \
    src/com/android/settings/SettingPreference.java \
    src/com/android/settings/SimpleSettingsActivity.java \
    src/com/android/settings/SimpleSettingsFragment.java \
    $(call all-java-files-under,src/com/android/settings/display) \
    $(call all-java-files-under,src/com/android/settings/update) \
    $(call all-java-files-under,src/com/android/settings/screensaver) \
    $(call all-java-files-under,src/com/android/settings/application) \
    $(call all-java-files-under,src/com/android/settings/bugreport) \
    $(call all-java-files-under,src/com/android/settings/datetimecity) \
    $(call all-java-files-under,src/com/android/settings/deviceinfo) \
    $(call all-java-files-under,src/com/android/settings/sound) \
    $(call all-java-files-under,src/com/android/settings/system) \
    $(call all-java-files-under,src/com/android/settings/widget) \
    $(call all-java-files-under,src/com/android/settings/network) \
    $(call all-java-files-under,src/com/android/settings/util) \
    $(call all-java-files-under,src/com/android/settings/userbackup) \
    $(call all-java-files-under,src/com/android/settings/miscupgrade) \
    $(call all-java-files-under,src/com/eostek/streamnetplusservice/service) \
    $(call all-java-files-under,src/scifly/middleware/network)

ifneq ($(BUILD_MSTARTV),)
    LOCAL_JAVA_LIBRARIES += com.mstar.android
    ifeq ($(TARGET_ARCH), arm64)
        LOCAL_SRC_FILES += $(call all-java-files-under,src/com/android/settings/bluetooth/)
    else
        LOCAL_SRC_FILES += $(call all-java-files-under,src/com/android/settings/mstar/) \
                       $(call all-java-files-under,src/com/android/settings/bluetooth/) 
   endif
endif


ifneq ($(BOARD_SENSOR_AMLOGIC),)
    LOCAL_SRC_FILES += $(call all-java-files-under,src/scifly/middleware/display)
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

LOCAL_PACKAGE_NAME := SciflySettings
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_OVERRIDES_PACKAGES := TvSettings
LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled

LOCAL_AAPT_FLAGS += -c zz_ZZ

#ADD FOR BOOTLOADER
LOCAL_JNI_SHARED_LIBRARIES := libjni_bootloader_scifly
LOCAL_REQUIRED_MODULES := libjni_bootloader_scifly

include $(BUILD_PACKAGE)

##### build static jar
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    gson:./libs/gson-2.2.2.jar

include $(BUILD_MULTI_PREBUILT)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))

