LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_JAVA_LIBRARIES := \
		scifly.android \
		com.mstar.android
			
LOCAL_PACKAGE_NAME := HLauncher
LOCAL_CERTIFICATE := platform
#LOCAL_PRIVILEGED_MODULE := true
LOCAL_OVERRIDES_PACKAGES := Launcher3 Gallery Gallery2 Galaxy4 SoundRecorder Email DeskClock Calendar Calculator \
    HTMLViewer MLocalMM2 MTvTest Music MWidiDemo MLatinIME LatinIME PinyinIME DMR DMP PhotoTable BasicDreams Exchange2 VideoEditor QuickSearchBox \
    HoloSpiralWallpaper LiveWallpapers LiveWallpapersPicker MagicSmokeWallpapers NoiseField MSetupWizard \
    PacProcessor PhaseBeam PhotoTable TelephonyProvider VisualizationWallpapers Camera2 MTvPlayer MTvHotkey MTvMisc  Settings\
    WAPPushManager BackupRestoreConfirmation CalendarProvider Contacts ContactsProvider TeleService WallpaperCropper DMS MTvBoot\
    MDummyAPK
    
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_DEX_PREOPT := false

LOCAL_STATIC_JAVA_LIBRARIES := \
	Universalimageloader \
	ExtreamaxAPI \
	Gson \
	HttpClient \
	HttpCore \
	HttpMime \
	GooglePlayServices \
	
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
        Universalimageloader:libs/universalimageloader.jar \
	ExtreamaxAPI:libs/ExtreamaxAPI-1.1.3.jar \
	Gson:libs/gson-2.3.1.jar \
	HttpClient:libs/httpclient-4.3.5.jar \
	HttpCore:libs/httpcore-4.3.2.jar \
	HttpMime:libs/httpmime-4.3.5.jar \
	GooglePlayServices:libs/google-play-services.jar
	
include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))

