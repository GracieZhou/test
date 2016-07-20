include $(SWCFGPATH_DAILEO)/config.mk
include $(SWCFGPATH_DAILEO)/default_config.mk

EXTRALIBS += releaseDFB
EXTRALIBS += releaseJPEG
EXTRALIBS += releasePNG
EXTRALIBS += releaseFREETYPE
EXTRALIBS += releaseZLIB
EXTRALIBS += releaseICONV
EXTRALIBS += releaseCURL
EXTRALIBS += releaseOPENSSL
EXTRALIBS += releaseCARES
EXTRALIBS += releaseXML2
EXTRALIBS += releaseNEON
ifeq ($(STB_ENABLE), 1)
	EXTRALIBS += releaseWIFIUTLI
	EXTRALIBS += releaseWPAUTLI
endif

ifeq ($(MSTAR_TVOS), 0)
	EXTRALIBS += releaseGLIB
	EXTRALIBS += releaseICU
	ifneq ($(STB_ENABLE), 1)
		EXTRALIBS += releaseOGG
		EXTRALIBS += releaseSOUP
		EXTRALIBS += releaseMMS
	endif
endif

ifeq ($(AACENCODER_ENABLE), 1)
	EXTRALIBS += releaseAACENCODER
endif

# =================================
# Only for WebUI develop use
# =================================
ifeq ($(MSTAR_WEBUI), 1)
	EXTRALIBS += releaseSQLITE
	EXTRALIBS += releaseMSTREAMER
	EXTRALIBS += releaseWEBUI_QTWEBKIT
	EXTRALIBS += releaseWEBUI_QT
	EXTRALIBS += releaseWEBSOCKET
	EXTRALIBS += releaseJSON
endif

ifneq (,$(filter 1,$(HBBTV_ENABLE) $(GINGA_MWB_ENABLE)))
	ifeq ($(MSTAR_TVOS), 1)
		EXTRALIBS += releaseGLIB
		EXTRALIBS += releaseICU
	endif
	EXTRALIBS += releasePANGO
endif

ifeq ($(MSTREAMER_ENABLE), 1)
	EXTRALIBS += releaseCAIRO
	EXTRALIBS += releaseFONTCONFIG
	EXTRALIBS += releasePIXMAN
	EXTRALIBS += releasePANGO
	EXTRALIBS += releaseSOUP
	EXTRALIBS += releaseMAD
	ifeq ($(MSTAR_TVOS), 0)
		ifeq ($(STB_ENABLE), 1)
			EXTRALIBS += releaseMMS
		endif
	endif
	EXTRALIBS += releaseMSTREAMER
endif

ifeq ($(VVOIP_ENABLE),1)
	EXTRALIBS += releaseVVOIP
endif

ifeq ($(ENABLE_NETREADY), 1)
	EXTRALIBS += releaseNETREADY
endif

ifeq ($(ALSA_ENABLE),1)
	EXTRALIBS += releaseALSALIB
endif

ifeq ($(HTTP_REPORT_TVINFO_ENABLE),1)
	EXTRALIBS += releaseMINI_HTTPD
endif

ifeq ($(WEBKIT_ENABLE),1)
	EXTRALIBS += releaseCAIRO
	EXTRALIBS += releaseFONTCONFIG
	EXTRALIBS += releaseZIP
	EXTRALIBS += releasePIXMAN
	EXTRALIBS += releaseSQLITE
	EXTRALIBS += releasePANGO
	EXTRALIBS += releaseWEBKIT
endif

ifeq ($(QT_BROWSER_ENABLE),1)
	EXTRALIBS += releaseCAIRO
	EXTRALIBS += releasePIXMAN
	EXTRALIBS += releasePANGO
	EXTRALIBS += releaseQT
	EXTRALIBS += releaseQTWEBKIT
	EXTRALIBS += releaseFONTCONFIG
	EXTRALIBS += releaseSQLITE
	EXTRALIBS += releaseTVSERVICE
	EXTRALIBS += releaseMBAL
	EXTRALIBS += releaseMWBSERVICE
	EXTRALIBS += releaseHBBTVLAUNCHER
#This is not verified yet for MstarLauncher.
	EXTRALIBS += releaseMSTARLAUNCHER
endif

ifeq ($(QTWEBKIT_ENABLE),1)
	EXTRALIBS += releaseZIP
	EXTRALIBS += releaseSQLITE
	EXTRALIBS += releaseQT
	EXTRALIBS += releaseQTWEBKIT
	EXTRALIBS += releaseTVSERVICE
        EXTRALIBS += releaseMBAL
	ifeq ($(HBBTV_MWB_ENABLE), 1)
		EXTRALIBS += releaseDOWNLOAD
	endif
	EXTRALIBS += releaseHBBTVLAUNCHER
	EXTRALIBS += releaseMWBSERVICE
	EXTRALIBS += releaseMSTARLAUNCHER
endif

ifeq ($(SQL_DB_ENABLE),1)
	EXTRALIBS += releaseSQLITE
endif

ifeq ($(HSL_IFILEGEN),1)
	EXTRALIBS += releaseSIGDBINC
endif

ifeq ($(DRMAGENT_ENABLE),1)
	EXTRALIBS += releaseDRMAGENT
endif

ifeq ($(GINGA_ENABLE),1)
	EXTRALIBS += releaseGINGA
endif

ifeq ($(WIFI_ENABLE), 1)
ifeq ($(WIFI_80211CFG_ENABLE),1)
	EXTRALIBS += releaseNL
endif
endif

ifeq ($(HDCP_ENABLE),1)
	EXTRALIBS += releaseHDCP
endif

ifeq ($(MULTI_SCREEN_TX_ENABLE),1)
	EXTRALIBS += releaseMULTISCREENTX
endif

ifeq ($(RVU_ENABLE),1)
	EXTRALIBS += releaseRVU
endif

TAR_FILE = $(wildcard ./*.tar.gz)
# =====================================
# modile version define
# =====================================
ifeq ($(TOOLCHAIN), arm-gnueabi)
SQLITE_NAME=sqlite-3.7.11
else
SQLITE_NAME=sqlite-3.6.22
endif

# =====================================
# file name define
# =====================================
DFB_TAR=directfb-1.4.2m.tar.gz
FREETYPE_TAR=freetype-2.5.3.tar.gz
ZLIB_TAR=zlib-1.2.8.tar.gz
PNG_TAR=libpng-1.2.32.tar.gz
JPEG_TAR=jpeg-6b.tar.gz
ICONV_TAR=libiconv-1.13.1.tar.gz
CURL_TAR=libcurl-7.21.3.tar.gz
SSL_TAR=libssl-0.9.8.tar.gz
CRYPTO_TAR=libcrypto-0.9.8.tar.gz
WIFIUTLI_TAR=libwireless-29.tar.gz
WPAUTLI_TAR=libwpa_supplicant.tar.gz
CAIRO_TAR=cairo-1.10.2.tar.gz
FONTCONFIG_TAR=fontconfig-2.7.1.tar.gz
GLIB_TAR=glib-2.24.2.tar.gz
ICU_TAR=icu-4.2.1.tar.gz
XML2_TAR=libxml2-2.7.7.tar.gz
XML_TAR=libxml.tar.gz
ZIP_TAR=libzip-0.9.3.tar.gz
OPENSSL_TAR=openssl-0.9.8o.tar.gz
PIXMAN_TAR=pixman-0.19.6.tar.gz
SQLITE_TAR=$(SQLITE_NAME).tar.gz
SIGDBINC_TAR=sigdb-inc-1.0.tar.gz
PANGO_TAR=pango-1.28.1.tar.gz
ifeq ($(MSTAR_TVOS), 1)
QTWEBKIT_TAR=qtwebkit-4.9.0_tvos.tar.gz
QTWEBKIT_BASEDIR_NAME=qtwebkit-4.9.0_tvos
HBBTVLAUNCHER_TAR=libhbbtvlauncher-1.1_tvos.tar.gz
else
QTWEBKIT_TAR=qtwebkit-4.9.0.tar.gz
QTWEBKIT_BASEDIR_NAME=qtwebkit-4.9.0
HBBTVLAUNCHER_TAR=libhbbtvlauncher-1.1.tar.gz
endif
ifeq ($(WIFI_ENABLE), 1)
ifeq ($(WIFI_80211CFG_ENABLE),1)
NL_TAR=nl-3.2.24.tar.gz
endif
endif
ifeq ($(HBBTV_MWB_ENABLE), 1)
MBAL_TAR=libmbal-1.30.tar.gz
else
#libmbal is a bridge bewteen TV and MWB, whereas none_libmal is a set
#of corresponding APIs of mbal that do nothing.
#In the ATSC (non-hbbtv) config, we need to use none_libmbal to keep API 
#compatible to make MWB correctly run.
MBAL_TAR=none_libmbal-1.30.tar.gz
endif
TVSERVICE_TAR=libtvservice-1.22.tar.gz
DOWNLOAD_TAR=libdownload.tar.gz
MINI_HTTPD_TAR=mini_httpd-1.19.tar.gz
MSTARLAUNCHER_TAR=MstarLauncher-1.0.tar.gz
MWBSERVICE_TAR=libmwbservice-1.1.tar.gz
ifeq ($(AACENCODER_ENABLE), 1)
AACENCODER_TAR=libEmAacLcEncLib-1.0.2.tar.gz
endif

# =====================================
# Only for WebUI develop use
# =====================================
WEBSOCKET_TAR=libwebsockets.tar.gz
JSON_TAR=libjson.tar.gz
WEBUI_QT_TAR=qt-webui.tar.gz
WEBUI_QTWEBKIT_TAR=qtwebkit-webui.tar.gz

ifeq ($(MSTAR_TVOS), 1)
QT_TAR=qt-4.8.1_tvos.tar.gz
else
QT_TAR=qt-4.8.1.tar.gz
endif

ifeq ($(HBBTV_ENABLE), 1)
WEBKIT_TAR=webkit.tar.gz
else
ifeq ($(WEBKIT_ENABLE), 1)
WEBKIT_TAR=webkit.tar.gz
endif
ifeq ($(GALAXY_ENABLE), 1)
WEBKIT_TAR=webkit_china.tar.gz
endif
ifeq ($(GALAXY_SKY_ENABLE), 1)
WEBKIT_TAR=webkit_china.tar.gz
endif
ifeq ($(NCL_WEBKIT_ENABLE), 1)
WEBKIT_TAR=webkit_NCL.tar.gz
endif
endif

NEON_TAR=neon-0.29.3.tar.gz
SOUP_TAR=libsoup-2.32.0.tar.gz
MSTREAMER_TAR=mstreamer.tar.gz
VUDU_TAR=libVudu-3.0.0.tar.gz
CARES_TAR=libcares.tar.gz
ifeq ($(MSTAR_TVOS), 1)
DRMAGENT_TAR=libDrmAgent_tvos.tar.gz
else
DRMAGENT_TAR=libDrmAgent.tar.gz
endif
NETREADY_TAR=libutv-agent.tar.gz
HDCP_TAR=libhdcp.tar.gz
MULTISCREENTX_TAR=libMultiScreenTX.tar.gz
RVU_TAR=RVU.tar.gz

ifeq ($(LX_KRL_VER), 2.6.28.9)
	ALSALIB_TAR=alsa-lib-1.0.18rc3.tar.gz
else
	ifeq ($(LX_KRL_VER), 2.6.32.15)
		ALSALIB_TAR=alsa-lib-1.0.21a.tar.gz
	else
		ifeq ($(LX_KRL_VER), 2.6.35.8)
			ALSALIB_TAR=alsa-lib-1.0.23.tar.gz
		else
			ifeq ($(LX_KRL_VER), 2.6.35.11)
				ALSALIB_TAR=alsa-lib-1.0.23.tar.gz
			else
				ifeq ($(LX_KRL_VER), 3.1.10)
					ALSALIB_TAR=alsa-lib-1.0.23.tar.gz
				else
					ALSALIB_TAR=alsa-lib-1.0.23.tar.gz
				endif
			endif
		endif
	endif
endif

VVOIP_TAR=lib_vvoip.tar.gz
GINGA_TAR=ginga.tar.gz
MAD_TAR=mad-0.15.1b.tar.gz
# temp dir to untar file
TMP_DIR=./_release.$(CHIP)

ifeq ($(TOOLCHAIN), )
$(error ERROR USAGE - Please select the correct target cpu for TOOLCHAIN)
endif

ifeq ($(FPU_ENABLE), 1)
FLOATING_TYPE=hardfloat
else
FLOATING_TYPE=softfloat
endif

TARFILE_DIR=../$(TOOLCHAIN)/$(FLOATING_TYPE)
CUR_TARFILE_DIR=./$(TOOLCHAIN)/$(FLOATING_TYPE)
EXTRALIBS_EXPANSION:=$(foreach value, $(patsubst release%,%_TAR,$(EXTRALIBS)),$($(value)))

# **********************************************
# Release shared libs path
# **********************************************
DYNAMIC_LIBPATH = $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/mslib
SHARE_PATH = $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/share

DEVELOP_LIBPATH =  $(DEVETARGET_DIRPATH)/$(CHIP)
DFB_TARGET_INC=$(INCLUDE_PATH)/dfb/target
DFB_X86_INC=$(INCLUDE_PATH)/dfb/x86
DFB_GFX_PATH=$(DYNAMIC_LIBPATH)/directfb-1.4-0/gfxdrivers
DFB_INPUTDRIVERS_PATH=$(DYNAMIC_LIBPATH)/directfb-1.4-0/inputdrivers

# **********************************************
# Remove file list of MStreamer
# **********************************************
filelist = \
	libgstdebugutils.so \
	libgstsubparse.so \
	libgstmsubrender.so \
	libgsticydemux.so \
	libgstasfdemux.so \
	libgstjpeg.so \
	libgstfludash.so \
	libgstmdmpes.so \
	libgstsouphttpsrc.so \
	libgstisomp4.so \
	libgstdrmplugin.so \
	libgstdrmas.so \
	libgstrtp.so \
	libgstrtpmanager.so \
	libgstrtsp.so \
	libgstudp.so

# =====================================
# Rules
# =====================================
.PHONY: all release

all: release

release: $(EXTRALIBS_EXPANSION)

$(DFB_TAR): $(CUR_TARFILE_DIR)/$(DFB_TAR)
#	@echo "############################################################"
	@echo "Release directfb....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(DFB_TAR)
#copy header file
	@[ -d $(DFB_TARGET_INC)/directfb ] ||  mkdir -p $(DFB_TARGET_INC)/directfb
	@[ -d $(DFB_TARGET_INC)/directfb-internal ] ||  mkdir -p $(DFB_TARGET_INC)/directfb-internal
	@cp -rf $(TMP_DIR).$@/directfb-1.4.2m/include/directfb/* $(DFB_TARGET_INC)/directfb
	@cp -rf $(TMP_DIR).$@/directfb-1.4.2m/include/directfb-internal/* $(DFB_TARGET_INC)/directfb-internal
#copy libs
	@find $(TMP_DIR).$@/directfb-1.4.2m/lib -maxdepth 1 -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@cp -rf $(TMP_DIR).$@/directfb-1.4.2m/lib/directfb-1.4-0 $(DYNAMIC_LIBPATH)
	@rm $(DFB_GFX_PATH)/libdirectfb_mstar.*

	@rm -rf $(TMP_DIR).$@

$(MINI_HTTPD_TAR): $(CUR_TARFILE_DIR)/$(MINI_HTTPD_TAR)
#       @echo "############################################################"
	@echo "Release mini_httpd....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(MINI_HTTP_TAR)
#copy files

	@[ -d $(TARGET_DIRPATH)/mini_httpd ] || mkdir -p $(TARGET_DIRPATH)/mini_httpd
	@cp -rf $(TMP_DIR).$@/* $(TARGET_DIRPATH)/mini_httpd

	@rm -rf $(TMP_DIR).$@

$(JPEG_TAR): $(CUR_TARFILE_DIR)/$(JPEG_TAR)
#	@echo "############################################################"
	@echo "Release jpeg....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(JPEG_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/jpeg ] || mkdir -p $(INCLUDE_PATH)/jpeg
	@cp -rf $(TMP_DIR).$@/jpeg-6b/include/* $(INCLUDE_PATH)/jpeg

	@rm -rf $(TMP_DIR).$@

$(PNG_TAR): $(CUR_TARFILE_DIR)/$(PNG_TAR)
#	@echo "############################################################"
	@echo "Release png....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(PNG_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/png ] || mkdir -p $(INCLUDE_PATH)/png
	@cp -rf $(TMP_DIR).$@/libpng-1.2.32/include/* $(INCLUDE_PATH)/png

	@rm -rf $(TMP_DIR).$@

$(FREETYPE_TAR): %:$(CUR_TARFILE_DIR)/$(FREETYPE_TAR)
#	@echo "############################################################"
	@echo "Release freetype....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(FREETYPE_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/freetype ] || mkdir -p $(INCLUDE_PATH)/freetype
	@cp -rf $(TMP_DIR).$@/freetype-2.5.3/include/* $(INCLUDE_PATH)/freetype

	@rm -rf $(TMP_DIR).$@

$(ZLIB_TAR): $(CUR_TARFILE_DIR)/$(ZLIB_TAR)
#	@echo "############################################################"
	@echo "Release zlib....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(ZLIB_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/zlib ] || mkdir -p $(INCLUDE_PATH)/zlib
	@cp -rf $(TMP_DIR).$@/zlib-1.2.8/include/* $(INCLUDE_PATH)/zlib

	@rm -rf $(TMP_DIR).$@


$(NL_TAR): $(CUR_TARFILE_DIR)/$(NL_TAR)
#       @echo "############################################################"
	@echo "Release NL....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(NL_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/nl ] || mkdir -p $(INCLUDE_PATH)/nl
	@cp -rf $(TMP_DIR).$@/nl-3.2.24/include/* $(INCLUDE_PATH)/nl

	@rm -rf $(TMP_DIR).$@


$(ICONV_TAR): $(CUR_TARFILE_DIR)/$(ICONV_TAR)
#	@echo "############################################################"
	@echo "Release iconv....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(ICONV_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/iconv ] || mkdir -p $(INCLUDE_PATH)/iconv
	@cp -rf $(TMP_DIR).$@/libiconv-1.13.1/include/* $(INCLUDE_PATH)/iconv

	@rm -rf $(TMP_DIR).$@

$(CURL_TAR): $(CUR_TARFILE_DIR)/$(CURL_TAR)
#	@echo "############################################################"
	@echo "Release libcurl....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(CURL_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/curl ] || mkdir -p $(INCLUDE_PATH)/curl
	@cp -rf $(TMP_DIR).$@/libcurl-7.21.3/include/* $(INCLUDE_PATH)

	@rm -rf $(TMP_DIR).$@

$(SSL_TAR): $(CUR_TARFILE_DIR)/$(SSL_TAR)
#       @echo "############################################################"
	@echo "Release libssl....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(SSL_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/ssl ] || mkdir -p $(INCLUDE_PATH)/ssl
	@cp -rf $(TMP_DIR).$@/libssl-0.9.8/include/* $(INCLUDE_PATH)/ssl
	@rm -rf $(TMP_DIR).$@

$(CRYPTO_TAR): $(CUR_TARFILE_DIR)/$(CRYPTO_TAR)
#       @echo "############################################################"
	@echo "Release libcrypto....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(CRYPTO_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
#	@[ '-d $(INCLUDE_PATH)/crypto' ] || mkdir -p $(INCLUDE_PATH)/crypto
#	@cp -rf $(TMP_DIR).$@/libcrypto-0.9.8/include/* $(INCLUDE_PATH)/crypto
	@rm -rf $(TMP_DIR).$@

$(WIFIUTLI_TAR): $(CUR_TARFILE_DIR)/$(WIFIUTLI_TAR)
#       @echo "############################################################"
	@echo "Release libwireless utility....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(WIFIUTLI_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
#	@[ '-d $(INCLUDE_PATH)/wireless_tool' ] && rm -rf $(INCLUDE_PATH)/wireless_tool
#	@cp -rf $(TMP_DIR).$@/libwireless/include $(INCLUDE_PATH)/wireless_tool
	@rm -rf $(TMP_DIR).$@

$(WPAUTLI_TAR): $(CUR_TARFILE_DIR)/$(WPAUTLI_TAR)
#       @echo "############################################################"
	@echo "Release wpa_supplicant....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(WPAUTLI_TAR)
#copy libs
	@mkdir -p $(SHARE_PATH)/wpa_supplicant/
	@cp -rf $(TMP_DIR).$@/libwpa_supplicant/bin/wpa_supplicant $(SHARE_PATH)/wpa_supplicant/
#copy header file
	@rm -rf $(TMP_DIR).$@

$(CAIRO_TAR): $(CUR_TARFILE_DIR)/$(CAIRO_TAR)
#	@echo "############################################################"
	@echo "Release cairo....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(CAIRO_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/cairo ] || mkdir -p $(INCLUDE_PATH)/cairo
	@cp -rf $(TMP_DIR).$@/cairo-1.10.2/include/cairo $(INCLUDE_PATH)

	@rm -rf $(TMP_DIR).$@

$(FONTCONFIG_TAR): $(CUR_TARFILE_DIR)/$(FONTCONFIG_TAR)
#	@echo "############################################################"
	@echo "Release fontconfig....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(FONTCONFIG_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/fontconfig ] || mkdir -p $(INCLUDE_PATH)/fontconfig
	@cp -rf $(TMP_DIR).$@/fontconfig-2.7.1/include/fontconfig $(INCLUDE_PATH)

	@rm -rf $(TMP_DIR).$@

$(GLIB_TAR): $(CUR_TARFILE_DIR)/$(GLIB_TAR)
#	@echo "############################################################"
	@echo "Release glib....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(GLIB_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/glib ] || mkdir -p $(INCLUDE_PATH)/glib
	@cp -rf $(TMP_DIR).$@/glib-2.24.2/include/* $(INCLUDE_PATH)/glib
	@cp -rf $(TMP_DIR).$@/glib-2.24.2/lib/glib-2.0/include/* $(INCLUDE_PATH)/glib

	@rm -rf $(TMP_DIR).$@

$(ICU_TAR): $(CUR_TARFILE_DIR)/$(ICU_TAR)
#	@echo "############################################################"
	@echo "Release icu....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(ICU_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/icu ] || mkdir -p $(INCLUDE_PATH)/icu
	@cp -rf $(TMP_DIR).$@/icu-4.2.1/include/* $(INCLUDE_PATH)/icu

	@rm -rf $(TMP_DIR).$@

$(XML2_TAR): $(CUR_TARFILE_DIR)/$(XML2_TAR)
#	@echo "############################################################"
	@echo "Release xml2....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(XML2_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/xml2 ] || mkdir -p $(INCLUDE_PATH)/xml2
	@cp -rf $(TMP_DIR).$@/libxml2-2.7.7/include/libxml2/* $(INCLUDE_PATH)/xml2

	@rm -rf $(TMP_DIR).$@

$(XML_TAR): $(CUR_TARFILE_DIR)/$(XML_TAR)
#	@echo "############################################################"
	@echo "Release xml....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(XML_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/xml ] || mkdir -p $(INCLUDE_PATH)/xml
	@cp -rf $(TMP_DIR).$@/libxml/*h $(INCLUDE_PATH)/xml

	@rm -rf $(TMP_DIR).$@

$(ZIP_TAR): $(CUR_TARFILE_DIR)/$(ZIP_TAR)
#       @echo "############################################################"
	@echo "Release libzip....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(ZIP_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/zip ] || mkdir -p $(INCLUDE_PATH)/zip
	@cp -rf $(TMP_DIR).$@/libzip-0.9.3/include/* $(INCLUDE_PATH)/zip
	@rm -rf $(TMP_DIR).$@

$(OPENSSL_TAR): $(CUR_TARFILE_DIR)/$(OPENSSL_TAR)
#	@echo "############################################################"
	@echo "Release openssl....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(OPENSSL_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/openssl ] || mkdir -p $(INCLUDE_PATH)/openssl
	@cp -rf $(TMP_DIR).$@/openssl-0.9.8o/include/openssl/* $(INCLUDE_PATH)/openssl

	@rm -rf $(TMP_DIR).$@

$(PIXMAN_TAR): $(CUR_TARFILE_DIR)/$(PIXMAN_TAR)
#	@echo "############################################################"
	@echo "Release pixman....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(PIXMAN_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/pixman ] || mkdir -p $(INCLUDE_PATH)/pixman-1
	@cp -rf $(TMP_DIR).$@/pixman-0.19.6/include/pixman-1/* $(INCLUDE_PATH)/pixman-1

	@rm -rf $(TMP_DIR).$@

$(SQLITE_TAR): $(CUR_TARFILE_DIR)/$(SQLITE_TAR)
#       @echo "############################################################"
	@echo "Release sqlite....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(SQLITE_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/sqlite ] || mkdir -p $(INCLUDE_PATH)/sqlite
	@cp -rf $(TMP_DIR).$@/$(SQLITE_NAME)/include/* $(INCLUDE_PATH)/sqlite

	@rm -rf $(TMP_DIR).$@

$(WEBKIT_TAR): $(CUR_TARFILE_DIR)/$(WEBKIT_TAR)
ifeq ($(WEBKIT_ENABLE),1)
#       @echo "############################################################"
	@echo "Release webkit....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(WEBKIT_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/webkit ] || mkdir -p $(INCLUDE_PATH)/webkit
	@cp -rf $(TMP_DIR).$@/webkit/include/* $(INCLUDE_PATH)/webkit
#copy Resouce
	@[ -d  $(PHOTOSPHERE_ROOT)/projects/ui/Apps/OWB/OWB/Resources ] || mkdir -p $(PHOTOSPHERE_ROOT)/projects/ui/Apps/OWB/OWB/Resources
	@cp -rf $(TMP_DIR).$@/webkit/Resources/* $(PHOTOSPHERE_ROOT)/projects/ui/Apps/OWB/OWB/Resources
	@rm -rf $(TMP_DIR).$@
endif

$(NEON_TAR): $(CUR_TARFILE_DIR)/$(NEON_TAR)
#       @echo "############################################################"
	@echo "Release neon....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(NEON_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/neon ] || mkdir -p $(INCLUDE_PATH)/neon
	@cp -rf $(TMP_DIR).$@/neon-0.29.3/include/* $(INCLUDE_PATH)/neon

	@rm -rf $(TMP_DIR).$@

$(SOUP_TAR): $(CUR_TARFILE_DIR)/$(SOUP_TAR)
#       @echo "############################################################"
	@echo "Release soup....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(SOUP_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/soup ] || mkdir -p $(INCLUDE_PATH)/soup
	@cp -rf $(TMP_DIR).$@/libsoup-2.32.0/include/libsoup-2.4/* $(INCLUDE_PATH)/soup

	@rm -rf $(TMP_DIR).$@

$(MSTREAMER_TAR): $(CUR_TARFILE_DIR)/$(MSTREAMER_TAR)
#       @echo "############################################################"
	@echo "Release mstreamer....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(MSTREAMER_TAR)
#copy libs
	@if [ -d $(DYNAMIC_LIBPATH)/gstreamer-0.10 ]; then rm -rf $(DYNAMIC_LIBPATH)/gstreamer-0.10 ; fi
	@find $(TMP_DIR).$@/mstreamer -name '*.a' -exec rm -f {} \;
	@find $(TMP_DIR).$@/mstreamer -name *.la -exec rm -f {} \;

ifeq ($(ENABLE_LITE_SN), 1)
	for filename in $(filelist) ; do \
		rm -f $(TMP_DIR).$@/mstreamer/lib/gstreamer-0.10/$$filename ; \
	done
endif

	@cp -rf $(TMP_DIR).$@/mstreamer/bin $(TMP_DIR).$@/mstreamer/lib/gstreamer-0.10
	@cp -f $(TMP_DIR).$@/mstreamer/libexec/gstreamer-0.10/* $(TMP_DIR).$@/mstreamer/lib/gstreamer-0.10/bin
	@ln -s /applications/rc $(TMP_DIR).$@/mstreamer/lib/gstreamer-0.10/rc
	@mv -f $(TMP_DIR).$@/mstreamer/lib/gstreamer-0.10 $(DYNAMIC_LIBPATH)
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/mstreamer ] || mkdir -p $(INCLUDE_PATH)/mstreamer
	@cp -rf $(TMP_DIR).$@/mstreamer/include/* $(INCLUDE_PATH)/mstreamer

	@rm -rf $(TMP_DIR).$@

$(VVOIP_TAR): $(CUR_TARFILE_DIR)/$(VVOIP_TAR)
#       @echo "############################################################"
	@echo "Release lib_vvoip....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(VVOIP_TAR)
#copy libs
	@[ -d $(DYNAMIC_LIBPATH)/vvoip ] && rm -rf $(DYNAMIC_LIBPATH)/vvoip ; mkdir $(DYNAMIC_LIBPATH)/vvoip
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH)/vvoip \;
#copy header file
	@[ '-d $(INCLUDE_PATH)/lib_vvoip' ] && rm -rf $(INCLUDE_PATH)/vvoip
	@cp -rf $(TMP_DIR).$@/lib_vvoip/include $(INCLUDE_PATH)/vvoip

$(VUDU_TAR): $(CUR_TARFILE_DIR)/$(VUDU_TAR)
ifeq ($(VUDU_ENABLE),1)
#       @echo "############################################################"
	@echo "Release libVudu....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(VUDU_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	#@[ '-d $(INCLUDE_PATH)/skype' ] && rm -rf $(INCLUDE_PATH)/skype
	#@cp -rf $(TMP_DIR).$@/libSkype/include $(INCLUDE_PATH)/skype
	@rm -rf $(TMP_DIR).$@
endif

$(CARES_TAR): $(CUR_TARFILE_DIR)/$(CARES_TAR)
#       @echo "############################################################"
	@echo "Release libCares....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(CARES_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/c-ares ] || mkdir -p $(INCLUDE_PATH)/c-ares
	@cp -rf $(TMP_DIR).$@/c-ares/include/* $(INCLUDE_PATH)/c-ares
	@rm -rf $(TMP_DIR).$@

$(ALSALIB_TAR): $(CUR_TARFILE_DIR)/$(ALSALIB_TAR)
#	@echo "############################################################"
	@echo "Release alsa library....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(ALSALIB_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/alsa ] || mkdir -p $(INCLUDE_PATH)/alsa
	@cp -rf $(TMP_DIR).$@/alsa-lib/include/* $(INCLUDE_PATH)/alsa

	@rm -rf $(TMP_DIR).$@

$(GINGA_TAR): $(CUR_TARFILE_DIR)/$(GINGA_TAR)
#       @echo "############################################################"
	  @echo "Release Ginga....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(GINGA_TAR)
#copy libs
ifeq ($(LIFIA_PACKAGE_ENABLE), 1)
	@echo "Release Ginga of Lifia ....."
	@if [ -d $(DYNAMIC_LIBPATH)/ginga ]; then rm -rf $(DYNAMIC_LIBPATH)/ginga ; fi
	@cp -f $(TMP_DIR).$@/vendor/lib/liblua.so $(DYNAMIC_LIBPATH)
	@cp -f $(TMP_DIR).$@/ginga/lib/* $(DYNAMIC_LIBPATH)
	@mkdir $(DYNAMIC_LIBPATH)/ginga
	@rm -rf $(TMP_DIR).$@
else
	@if [ -d $(DYNAMIC_LIBPATH)/ginga ]; then rm -rf $(DYNAMIC_LIBPATH)/ginga ; fi
	@cp -rf $(TMP_DIR).$@/vendor/lib/* $(DYNAMIC_LIBPATH)
	@cp -f $(TMP_DIR).$@/ginga/lib/* $(DYNAMIC_LIBPATH)
	@cp -f $(TMP_DIR).$@/nclFormatter.sh $(DYNAMIC_LIBPATH)/ginga
	@cp -rf $(TMP_DIR).$@/jvm $(DYNAMIC_LIBPATH)/ginga
ifeq ($(GINGA_MWB_ENABLE), 1)
	@cp -f $(TMP_DIR).$@/browser/mwb/libnclbrowser.so $(DYNAMIC_LIBPATH)/ginga/lib/ginga
	@cp -f $(TMP_DIR).$@/browser/mwb/ginga_ncl $(DYNAMIC_LIBPATH)/ginga/sbin
endif
	@rm -rf $(TMP_DIR).$@
endif

$(MAD_TAR): $(CUR_TARFILE_DIR)/$(MAD_TAR)
#       @echo "############################################################"
	@echo "Release Mad....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(MAD_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/mad ] || mkdir -p $(INCLUDE_PATH)/mad
	@cp -rf $(TMP_DIR).$@/mad-0.15.1b/include/* $(INCLUDE_PATH)/mad
	@rm -rf $(TMP_DIR).$@

$(SIGDBINC_TAR): $(CUR_TARFILE_DIR)/$(SIGDBINC_TAR)
#       @echo "############################################################"
	@echo "Release sigdb includes....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(SIGDBINC_TAR)
#copy header file
	@[ -d $(INCLUDE_PATH)/sigdb-inc ] || mkdir -p $(INCLUDE_PATH)/sigdb-inc
	@cp -rf $(TMP_DIR).$@/tools $(INCLUDE_PATH)/sigdb-inc/tools
	@rm -rf $(TMP_DIR).$@

$(PANGO_TAR): $(CUR_TARFILE_DIR)/$(PANGO_TAR)
#       @echo "############################################################"
	@echo "Release pango....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(PANGO_TAR)
#copy libs
	@mkdir -p $(DYNAMIC_LIBPATH)/pango/1.6.0/modules/
	@find $(TMP_DIR).$@/pango-1.28.1/lib/* -name 'libpango*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/pango-1.28.1/lib/* -name 'pango*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH)/pango/1.6.0/modules/ \;
#copy header file
	@[ -d $(INCLUDE_PATH)/pango ] || mkdir -p $(INCLUDE_PATH)/pango
	@cp -rf $(TMP_DIR).$@/pango-1.28.1/include/* $(INCLUDE_PATH)/pango
	@rm -rf $(TMP_DIR).$@

$(NETREADY_TAR): $(CUR_TARFILE_DIR)/$(NETREADY_TAR)
#       @echo "############################################################"
	@echo "Release Netready Service....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(NETREADY_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ '-d $(INCLUDE_PATH)/skype' ] && rm -rf $(INCLUDE_PATH)/netready
	@cp -rf $(TMP_DIR).$@/libutv-agent/include $(INCLUDE_PATH)/netready
	@rm -rf $(TMP_DIR).$@

$(QT_TAR): $(CUR_TARFILE_DIR)/$(QT_TAR)
#       @echo "############################################################"
	@echo "Release qt....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(QT_TAR)
#copy libs
	@mkdir -p $(DYNAMIC_LIBPATH)/qt/plugins
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtCore.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtGui.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtNetwork.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtSql.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtXml.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtXmlPatterns.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@cp -rf $(TMP_DIR).$@/qt-4.8.1/lib/plugins/* $(DYNAMIC_LIBPATH)/qt/plugins
#copy header file
	@[ -d $(INCLUDE_PATH)/qt ] || mkdir -p $(INCLUDE_PATH)/qt
	@cp -rf $(TMP_DIR).$@/qt-4.8.1/include/* $(INCLUDE_PATH)/qt
	@rm -rf $(TMP_DIR).$@

$(QTWEBKIT_TAR): $(CUR_TARFILE_DIR)/$(QTWEBKIT_TAR)
#       @echo "############################################################"
	@echo "Release qtwebkit....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(QTWEBKIT_TAR)
#copy libs
	@find $(TMP_DIR).$@/$(QTWEBKIT_BASEDIR_NAME)/lib/* -name 'libQtWebKit.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
ifeq ($(GINGA_MWB_ENABLE),1)
	@find $(TMP_DIR).$@/$(QTWEBKIT_BASEDIR_NAME)/lib/* -name 'libNCLBrowserWrapper.so' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
endif
#copy header file
	@[ -d $(INCLUDE_PATH)/qtwebkit ] || mkdir -p $(INCLUDE_PATH)/qtwebkit
	@cp -rf $(TMP_DIR).$@/$(QTWEBKIT_BASEDIR_NAME)/include/* $(INCLUDE_PATH)/qtwebkit
ifeq ($(GINGA_MWB_ENABLE),1)
	@cp -rf $(TMP_DIR).$@/$(QTWEBKIT_BASEDIR_NAME)/include/NCLBrowserWrapper/* $(INCLUDE_PATH)/qtwebkit
endif
	@rm -rf $(TMP_DIR).$@

$(MSTARLAUNCHER_TAR): $(CUR_TARFILE_DIR)/$(MSTARLAUNCHER_TAR)
#       @echo "############################################################"
	@echo "Release MstarLauncher....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(MSTARLAUNCHER_TAR)

#copy binary file
#	@echo $(TOOLCHAIN)
	@cp -f $(TMP_DIR).$@/MstarLauncher-1.0/bin/MstarLauncher $(PHOTOSPHERE_ROOT)/projects/ui/Apps/MWB/$(TOOLCHAIN)/$(FLOATING_TYPE)/bin

#copy libs
#	@echo $(TMP_DIR)
	@find $(TMP_DIR).$@/MstarLauncher-1.0/lib/* -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;

#copy header file
	@[ ! -d $(TMP_DIR).$@/MstarLauncher-1.0/include/apm ] || cp -rf $(TMP_DIR).$@/MstarLauncher-1.0/include/apm/* $(PHOTOSPHERE_ROOT)/projects/ipc_svc/webappmgr/exported_api
	@cp -rf $(TMP_DIR).$@/MstarLauncher-1.0/include/* $(INCLUDE_PATH)
	@rm -rf $(TMP_DIR).$@

$(MBAL_TAR): $(CUR_TARFILE_DIR)/$(MBAL_TAR)
#       @echo "############################################################"
	@echo "Release mbal....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(MBAL_TAR)*
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/mbal ] || mkdir -p $(INCLUDE_PATH)/mbal
	@cp -rf $(TMP_DIR).$@/*/include/* $(INCLUDE_PATH)/mbal

	@rm -rf $(TMP_DIR).$@

$(TVSERVICE_TAR): $(CUR_TARFILE_DIR)/$(TVSERVICE_TAR)
#       @echo "############################################################"
	@echo "Release tvservice....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(TVSERVICE_TAR)*
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/mbal ] || mkdir -p $(INCLUDE_PATH)/mbal
	@cp -rf $(TMP_DIR).$@/*/include/* $(INCLUDE_PATH)/tvservice
	@rm -rf $(TMP_DIR).$@

$(DOWNLOAD_TAR): $(CUR_TARFILE_DIR)/$(DOWNLOAD_TAR)
#       @echo "############################################################"
	@echo "Release download....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(DOWNLOAD_TAR)*
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/download ] || mkdir -p $(INCLUDE_PATH)/download
	@cp -rf $(TMP_DIR).$@/*/include/* $(INCLUDE_PATH)/download
	@cp -f $(TMP_DIR).$@/libdownload/bin/downloadmanager $(PHOTOSPHERE_ROOT)/projects/ui/Apps/MWB/MWB/downloadmanager
	@cp -f $(TMP_DIR).$@/libdownload/downloadmanager.ini $(PHOTOSPHERE_ROOT)/projects/ui/Apps/MWB/MWB/downloadmanager.ini
	@rm -rf $(TMP_DIR).$@

$(MWBSERVICE_TAR): $(CUR_TARFILE_DIR)/$(MWBSERVICE_TAR)
#       @echo "############################################################"
	@echo "Release MWBService....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(MWBSERVICE_TAR)*
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/mwbservice ] || mkdir -p $(INCLUDE_PATH)/mwbservice
	@cp -rf $(TMP_DIR).$@/include/mwbservice/* $(INCLUDE_PATH)/mwbservice
	@rm -rf $(TMP_DIR).$@

$(HBBTVLAUNCHER_TAR): $(CUR_TARFILE_DIR)/$(HBBTVLAUNCHER_TAR)
#       @echo "############################################################"
ifeq ($(MSTAR_TVOS), 1)
	@echo "Release hbbtvlauncher for TVOS project"
else
	@echo "Release hbbtvlauncher....."
endif
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(HBBTVLAUNCHER_TAR)*
ifeq ($(MSTAR_TVOS), 1)
#copy Binary
	@cp -f $(TMP_DIR).$@/MWB $(PHOTOSPHERE_ROOT)/projects/ui/Apps/MWB/$(TOOLCHAIN)/$(FLOATING_TYPE)/bin
else
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/hbbtvlauncher ] || mkdir -p $(INCLUDE_PATH)/hbbtvlauncher
	@cp -rf $(TMP_DIR).$@/*.h $(INCLUDE_PATH)/hbbtvlauncher
endif
	@rm -rf $(TMP_DIR).$@

$(DRMAGENT_TAR): $(CUR_TARFILE_DIR)/$(DRMAGENT_TAR)
#       @echo "############################################################"
	@echo "Release drmagent....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(DRMAGENT_TAR)
#copy libs
ifeq ($(TEE_ENABLE), 1)
	@cp $(TMP_DIR).$@/libDrmAgent/lib/tee/*.so $(DYNAMIC_LIBPATH)
	@cp -f $(PHOTOSPHERE_ROOT)/tee/Apps/install/PlayReady25/libs/$(TARGET_CPU)/hard_float/*.so $(DYNAMIC_LIBPATH)
else
	@cp $(TMP_DIR).$@/libDrmAgent/lib/normal/*.so $(DYNAMIC_LIBPATH)
endif
#copy header file
	@[ -d $(INCLUDE_PATH)/drmagent ] || mkdir -p $(INCLUDE_PATH)/drmagent
	@cp -rf $(TMP_DIR).$@/libDrmAgent/include/* $(INCLUDE_PATH)/drmagent
	@cp -f $(TMP_DIR).$@/libDrmAgent/drminfo.ini $(INCLUDE_PATH)/drmagent
	@rm -rf $(TMP_DIR).$@

#releaseHDCP:HDCP_TAR
$(HDCP_TAR): $(CUR_TARFILE_DIR)/$(HDCP_TAR)
#       @echo "############################################################"
#   	@echo "Release libhdcp....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(HDCP_TAR)
#copy libs
ifeq ($(TEE_ENABLE),1)
	@echo "Release libhdcp (TEE version)....."
	@mv -f $(TMP_DIR).$@/libhdcp/lib/tee/* $(DYNAMIC_LIBPATH)
else
	@echo "Release libhdcp (non-TEE version)....."
	@mv -f $(TMP_DIR).$@/libhdcp/lib/normal/* $(DYNAMIC_LIBPATH)
endif
#copy hdcp configuration file
	@echo "mkdir -p $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/config/hdcp"
	@mkdir -p $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/config/hdcp
	@mv -f $(TMP_DIR).$@/libhdcp/hdcp2.ini $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/config/hdcp
# EosTek Patch Begin
	@sed -i 's/HDCP_RX_KEY_PATH=.*/HDCP_RX_KEY_PATH=\/Customer\/hdcp2_key.bin/g' $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/config/hdcp/hdcp2.ini
# EosTek Patch Begin
#copy header file
	@[ -d $(INCLUDE_PATH)/libhdcp ] || mkdir -p $(INCLUDE_PATH)/hdcp
	@mv -f $(TMP_DIR).$@/libhdcp/include/* $(INCLUDE_PATH)/hdcp
	@rm -rf $(TMP_DIR).$@


$(MULTISCREENTX_TAR): $(CUR_TARFILE_DIR)/$(MULTISCREENTX_TAR)
#       @echo "############################################################"
	@echo "Release MultiScreenTX....."
#       @echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(MULTISCREENTX_TAR)
#copy libs
	@cp -rf $(TMP_DIR).$@/libMultiScreenTX/lib/* $(DYNAMIC_LIBPATH)
#copy configuration file
	@mkdir -p $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/config/MultiScreenTX
	@cp -rf $(TMP_DIR).$@/libMultiScreenTX/conf/common/* $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/config/MultiScreenTX
ifeq ($(WIFI_80211CFG_ENABLE),1)
	@cp -rf $(TMP_DIR).$@/libMultiScreenTX/conf/network/nl80211/* $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/config/MultiScreenTX
else
	@cp -rf $(TMP_DIR).$@/libMultiScreenTX/conf/network/wext/* $(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/config/MultiScreenTX
endif
#copy header file
	@[ -d $(INCLUDE_PATH)/libMultiScreenTX ] || mkdir -p $(INCLUDE_PATH)/MultiScreenTX
	@cp -rf $(TMP_DIR).$@/libMultiScreenTX/include/* $(INCLUDE_PATH)/MultiScreenTX
	@rm -rf $(TMP_DIR).$@

$(WEBSOCKET_TAR): $(CUR_TARFILE_DIR)/$(WEBSOCKET_TAR)
#	@echo "############################################################"
	@echo "Release libwebsocket....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(WEBSOCKET_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/libwebsockets ] || mkdir -p $(INCLUDE_PATH)/libwebsockets
	@cp -rf $(TMP_DIR).$@/libwebsockets/include/* $(INCLUDE_PATH)/libwebsockets

	@rm -rf $(TMP_DIR).$@

$(JSON_TAR): $(CUR_TARFILE_DIR)/$(JSON_TAR)
#	@echo "############################################################"
	@echo "Release libjson....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(JSON_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/libjson ] || mkdir -p $(INCLUDE_PATH)/libjson
	@cp -rf $(TMP_DIR).$@/libjson/include/* $(INCLUDE_PATH)/libjson

	@rm -rf $(TMP_DIR).$@

$(WEBUI_QTWEBKIT_TAR): $(CUR_TARFILE_DIR)/$(WEBUI_QTWEBKIT_TAR)
#	@echo "############################################################"
	@echo "Release WEBUI_QTWEBKIT....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar page file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@rm -rf $(PHOTOSPHERE_ROOT)/projects/ui/Apps/WebUI
	@mkdir $(PHOTOSPHERE_ROOT)/projects/ui/Apps/WebUI
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(WEBUI_QTWEBKIT_TAR)
#copy files
	#qtwebkit lib
	@find $(TMP_DIR).$@/$(QTWEBKIT_BASEDIR_NAME)/lib/* -name 'libQtWebKit.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	#qtwebkit header
	@[ -d $(INCLUDE_PATH)/qtwebkit ] || mkdir -p $(INCLUDE_PATH)/qtwebkit
	@cp -rf $(TMP_DIR).$@/$(QTWEBKIT_BASEDIR_NAME)/include/QtWebKit/* $(INCLUDE_PATH)/qtwebkit
	@cp -rf $(TMP_DIR).$@/$(QTWEBKIT_BASEDIR_NAME)/bin/* $(PHOTOSPHERE_ROOT)/projects/ui/Apps/WebUI

	@rm -rf $(TMP_DIR).$@

$(WEBUI_QT_TAR): $(CUR_TARFILE_DIR)/$(WEBUI_QT_TAR)
#	@echo "############################################################"
	@echo "Release WEBUI_QT....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar page file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(WEBUI_QT_TAR)
#copy files
	#qt lib
	@mkdir -p $(DYNAMIC_LIBPATH)/qt/plugins
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtCore.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtGui.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtNetwork.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtSql.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtXml.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@find $(TMP_DIR).$@/qt-4.8.1/lib/* -name 'libQtXmlPatterns.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
	@cp -rf $(TMP_DIR).$@/qt-4.8.1/lib/plugins/* $(DYNAMIC_LIBPATH)/qt/plugins
	#qt header
	@[ -d $(INCLUDE_PATH)/qt ] || mkdir -p $(INCLUDE_PATH)/qt
	@cp -rf $(TMP_DIR).$@/qt-4.8.1/include/* $(INCLUDE_PATH)/qt

	@rm -rf $(TMP_DIR).$@

$(RVU_TAR): $(CUR_TARFILE_DIR)/$(RVU_TAR)
#       echo "############################################################"
#   	echo "Release RVU....."
#       echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(RVU_TAR)
#copy libs
	@echo "Release RVU....."
	@[ -d $(DYNAMIC_LIBPATH)/RVU/ ] || mkdir -p $(DYNAMIC_LIBPATH)/RVU/
	@cp -f $(TMP_DIR).$@/rvu/lib/* $(DYNAMIC_LIBPATH)/RVU/

	@[ -d $(PHOTOSPHERE_ROOT)/projects/ui/Apps/RVU/RVU/ ] || mkdir -p $(PHOTOSPHERE_ROOT)/projects/ui/Apps/RVU/RVU/
#copy player binary
	@cp -f $(TMP_DIR).$@/rvu/bin/* $(PHOTOSPHERE_ROOT)/projects/ui/Apps/RVU/RVU/
#copy RVU configuration file
	@cp -f $(TMP_DIR).$@/rvu/cfg/* $(PHOTOSPHERE_ROOT)/projects/ui/Apps/RVU/RVU/

#copy header file
	@[ -d $(INCLUDE_PATH)/RVU ] || mkdir -p $(INCLUDE_PATH)/RVU
	@cp -f $(TMP_DIR).$@/rvu/include/* $(INCLUDE_PATH)/RVU
	@rm -rf $(TMP_DIR).$@

$(AACENCODER_TAR): $(CUR_TARFILE_DIR)/$(AACENCODER_TAR)
#	@echo "############################################################"
	@echo "Release aacencoder....."
#	@echo "############################################################"
#keep file last update time
	@cp -f $< $@
#untar file
	@[ -d $(TMP_DIR).$@ ] && rm -rf $(TMP_DIR).$@ ; mkdir $(TMP_DIR).$@
	@cd $(TMP_DIR).$@;tar zxf $(TARFILE_DIR)/$(AACENCODER_TAR)
#copy libs
	@find $(TMP_DIR).$@ -name '*.so*' -exec cp -f -d {} $(DYNAMIC_LIBPATH) \;
#copy header file
	@[ -d $(INCLUDE_PATH)/aacencoder ] || mkdir -p $(INCLUDE_PATH)/aacencoder
	@cp -rf $(TMP_DIR).$@/libEmAacLcEncLib-1.0.2/include/* $(INCLUDE_PATH)/aacencoder

	@rm -rf $(TMP_DIR).$@

clean:
	@echo "clean up temp files"
	@rm -rf *.tar.gz
	@echo "clean up headers"
	@rm -rf $(DFB_TARGET_INC)/directfb
	@rm -rf $(DFB_TARGET_INC)/directfb-internal
	@rm -rf $(INCLUDE_PATH)/jpeg
	@rm -rf $(INCLUDE_PATH)/png
	@rm -rf $(INCLUDE_PATH)/freetype
	@rm -rf $(INCLUDE_PATH)/zlib
	@rm -rf $(INCLUDE_PATH)/nl
	@rm -rf $(INCLUDE_PATH)/iconv
	@rm -rf $(INCLUDE_PATH)/curl
	@rm -rf $(INCLUDE_PATH)/ssl
	@rm -rf $(INCLUDE_PATH)/cairo
	@rm -rf $(INCLUDE_PATH)/fontconfig
	@rm -rf $(INCLUDE_PATH)/glib
	@rm -rf $(INCLUDE_PATH)/icu
	@rm -rf $(INCLUDE_PATH)/xml2
	@rm -rf $(INCLUDE_PATH)/xml
	@rm -rf $(INCLUDE_PATH)/zip
	@rm -rf $(INCLUDE_PATH)/openssl
	@rm -rf $(INCLUDE_PATH)/pixman-1
	@rm -rf $(INCLUDE_PATH)/sqlite
	@rm -rf $(INCLUDE_PATH)/webkit
	@rm -rf $(INCLUDE_PATH)/neon
	@rm -rf $(INCLUDE_PATH)/soup
	@rm -rf $(INCLUDE_PATH)/mstreamer
	@rm -rf $(INCLUDE_PATH)/vvoip
	@rm -rf $(INCLUDE_PATH)/c-ares
	@rm -rf $(INCLUDE_PATH)/alsa
	@rm -rf $(PHOTOSPHERE_ROOT)/projects/ui/Apps/OWB/OWB/Resources
	@rm -rf $(INCLUDE_PATH)/sigdb-inc
	@rm -rf $(INCLUDE_PATH)/pango
	@rm -rf $(INCLUDE_PATH)/qt
	@rm -rf $(INCLUDE_PATH)/qtwebkit
	@rm -rf $(INCLUDE_PATH)/mbinder
	@rm -rf $(INCLUDE_PATH)/mbal
	@rm -rf $(INCLUDE_PATH)/tvservice
	@rm -rf $(INCLUDE_PATH)/drmagent
	@rm -rf $(INCLUDE_PATH)/netready
	@rm -rf $(INCLUDE_PATH)/hdcp
	@rm -rf $(INCLUDE_PATH)/MultiScreenTX
	@rm -rf $(INCLUDE_PATH)/RVU
	@rm -rf $(INCLUDE_PATH)/LauncherUtil
	@rm -rf $(INCLUDE_PATH)/mwblauncherservice
	@rm -rf $(INCLUDE_PATH)/mwbservice
	@rm -rf $(INCLUDE_PATH)/download
	@rm -f $(PHOTOSPHERE_ROOT)/projects/ui/Apps/RVU/RVU/*.txt
	@rm -f $(PHOTOSPHERE_ROOT)/projects/ui/Apps/RVU/RVU/*.ini
	@rm -f $(PHOTOSPHERE_ROOT)/projects/ui/Apps/RVU/RVU/GStreamerPlayer
	@rm -rf $(INCLUDE_PATH)/aacencoder
	@rm -rf $(INCLUDE_PATH)/mad
