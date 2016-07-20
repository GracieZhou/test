
BUILDTYPE = lib

############################################################ final target name
LIBNAME   = libfactorymanagerservice

############################################################ source files.
SRC      += FactoryManagerService.cpp

VPATH = ./

############################################################ depnedent header files.
INCS     += -I./
INCS     += -I../../include/common
INCS     += -I../../include/factorymanager
INCS     += -I$(INCLUDE_PATH)/mdroid/frameworks/native/include
INCS     += -I$(INCLUDE_PATH)/mdroid/system/core/include
INCS     += -I$(INCLUDE_PATH)/mdroid/bionic/libc/include
INCS     += -I$(INCLUDE_PATH)/mdroid/bionic/libc/kernel/uapi
INCS     += -I$(INCLUDE_PATH)/mdroid/
INCS     += -I$(INCLUDE_PATH)/muf
INCS     += -I$(INCLUDE_PATH)/muf/base
INCS     += -I$(INCLUDE_PATH)/dfb/target/directfb
INCS     += -I$(INCLUDE_PATH)/MStarSDK
INCS     += -I$(INCLUDE_PATH)/sqlite
INCS     += -I$(INCLUDE_PATH)/utopia/$(CHIP)
INCS     += -I$(INCLUDE_PATH)/misc
INCS     += -I$(INCLUDE_PATH)/systeminfo
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/system_database/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/multimedia/mmplayer/include
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/control/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/common/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/common/middleware/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/module_rel/network_control/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/dvb/dvbt/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/dvb/base/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/atsc/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/msrv/atv/inc
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/develop/include/muf
INCS     += -I$(PHOTOSPHERE_ROOT)/projects/tvos/include/factorymanager

############################################################ dependent libraries
LIBS     += -lpthread
LIBS     += -lfactorymanager
LIBS     += -lmdroid
LIBS     += -L$(TARGET_DIRPATH)/$(PROJ_MODE).$(CHIP)/mslib

INC_REL_PATH = $(INCLUDE_PATH)/tvos/factorymanager
REL_INCS += ./FactoryManagerService.h

SVC_NAME = factorymanager

include ../../services.mk
