/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef _VOLUMEMANAGER_H
#define _VOLUMEMANAGER_H

#include <pthread.h>

#ifdef __cplusplus
// MStar Android Patch Begin
#include <utils/List.h>
#include <utils/threads.h>
#include <sysutils/SocketListener.h>

#include "Volume.h"

using namespace::android;

/* The length of an MD5 hash when encoded into ASCII hex characters */
#define MD5_ASCII_LENGTH_PLUS_NULL ((MD5_DIGEST_LENGTH*2)+1)
#define DISK_MAJOR 8
#define DISK_EXTEND_MAJOR 259
#define SD_MAJOR 179

typedef enum { ASEC, OBB, ISO } container_type_t;
// MStar Android Patch End

class ContainerData {
public:
    ContainerData(char* _id, container_type_t _type)
            : id(_id)
            , type(_type)
    {}

    ~ContainerData() {
        if (id != NULL) {
            free(id);
            id = NULL;
        }
    }

    char *id;
    container_type_t type;
};

typedef android::List<ContainerData*> AsecIdCollection;

class VolumeManager {
private:
    static VolumeManager *sInstance;

private:
    SocketListener        *mBroadcaster;

    VolumeCollection      *mVolumes;
    AsecIdCollection      *mActiveContainers;
    bool                   mDebug;

    // for adjusting /proc/sys/vm/dirty_ratio when UMS is active
    int                    mUmsSharingCount;
    int                    mSavedDirtyRatio;
    int                    mUmsDirtyRatio;
    int                    mVolManagerDisabled;

    // MStar Android Patch Begin
    Mutex                   mVolumesLock;
    Mutex                   mActiveContainersLock;
    // MStar Android Patch End

    // EosTek Patch Begin
    char                   *mUsbAsecDir;
    char                   *mUsbSecureDir;
    char                   *mUsbSecureAsecDir;
    // EosTek Patch End
public:
    virtual ~VolumeManager();

    int start();
    int stop();

    void handleBlockEvent(NetlinkEvent *evt);

    int addVolume(Volume *v);

    int listVolumes(SocketClient *cli, bool broadcast);
    int mountVolume(const char *label);
    // EosTek Patch Begin
    int updateUsbAsec(const char *label);
    // EosTek Patch End
    int unmountVolume(const char *label, bool force, bool revert);
    int shareVolume(const char *label, const char *method);
    int unshareVolume(const char *label, const char *method);
    int shareEnabled(const char *path, const char *method, bool *enabled);
    int formatVolume(const char *label, bool wipe);
    void disableVolumeManager(void) { mVolManagerDisabled = 1; }
    // MStar Android Patch Begin
    int getVolumeLabel(SocketClient *cli, const char *label);
    void lockActiveContainers();
    void unlockActiveContainers();
    int getVolumeUuid(SocketClient *cli, const char *pathStr);
    void refreshVolumeUUIDAfterFormat(const char *pathStr);
    // MStar Android Patch End

    /* ASEC */
    int findAsec(const char *id, char *asecPath = NULL, size_t asecPathLen = 0,
            const char **directory = NULL) const;
    // EosTek Patch Begin
    int createAsec(const char *id, unsigned numSectors, const char *fstype,
                   const char *key, const int ownerUid, bool isExternal, bool isUsb);
    int resizeAsec(const char *id, unsigned numSectors, const char *key);
    int finalizeAsec(const char *id, bool isUsb);

    /**
     * Fixes ASEC permissions on a filesystem that has owners and permissions.
     * This currently means EXT4-based ASEC containers.
     *
     * There is a single file that can be marked as "private" and will not have
     * world-readable permission. The group for that file will be set to the gid
     * supplied.
     *
     * Returns 0 on success.
     */
    int fixupAsecPermissions(const char *id, gid_t gid, const char* privateFilename, bool isUsb);
    int destroyAsec(const char *id, bool force, bool isUsb);
    int mountAsec(const char *id, const char *key, int ownerUid, bool readOnly, bool isUsb);
    int unmountAsec(const char *id, bool force, bool isUsb);
    int renameAsec(const char *id1, const char *id2, bool isUsb);
    int getAsecMountPath(const char *id, char *buffer, int maxlen, bool isUsb);
    const char *getUsbAsecDir() { return mUsbAsecDir; }
    const char *getUsbSecureDir() { return mUsbSecureDir; }
    const char *getUsbSecureAsecDir() { return mUsbSecureAsecDir; }
    // EosTek Patch End
    int getAsecFilesystemPath(const char *id, char *buffer, int maxlen);

    /* Loopback images */
    int listMountedObbs(SocketClient* cli);
    int mountObb(const char *fileName, const char *key, int ownerUid);
    int unmountObb(const char *fileName, bool force);
    int getObbMountPath(const char *id, char *buffer, int maxlen);

    Volume* getVolumeForFile(const char *fileName);

    // MStar Android Patch Begin
    /* ISO images */
    int listMountedISOs(SocketClient* cli);
    int mountISO(const char *fileName);
    int unmountISO(const char *fileName, bool force);
    int getISOMountPath(const char *id, char *buffer, int maxlen);

    /* Samba */
    int mountSamba(const char *host, const char *shareDir, const char *mountPoint,
                   const char *userName, const char *password, bool ro, bool executable);
    int unmountSamba(const char *mountPoint, bool force);
    // MStar Android Patch End

    /* Shared between ASEC and Loopback images */
    int unmountLoopImage(const char *containerId, const char *loopId,
            const char *fileName, const char *mountPoint, bool force);

    void setDebug(bool enable);

    // XXX: Post froyo this should be moved and cleaned up
    int cleanupAsec(Volume *v, bool force);
    // MStar Android Patch Begin
    int cleanupISO(Volume *v, bool force);
    // MStar Android Patch End

    void setBroadcaster(SocketListener *sl) { mBroadcaster = sl; }
    SocketListener *getBroadcaster() { return mBroadcaster; }

    static VolumeManager *Instance();

    static char *asecHash(const char *id, char *buffer, size_t len);

    Volume *lookupVolume(const char *label);
    int getNumDirectVolumes(void);
    int getDirectVolumeList(struct volume_info *vol_list);
    // EosTek Patch Begin
    int unmountAllAsecsInDir(const char *directory, bool isUsb);
    // EosTek Patch End
    /*
     * Ensure that all directories along given path exist, creating parent
     * directories as needed.  Validates that given path is absolute and that
     * it contains no relative "." or ".." paths or symlinks.  Last path segment
     * is treated as filename and ignored, unless the path ends with "/".  Also
     * ensures that path belongs to a volume managed by vold.
     */
    int mkdirs(char* path);

private:
    VolumeManager();
    void readInitialState();
    bool isMountpointMounted(const char *mp);
    bool isAsecInDirectory(const char *dir, const char *asec) const;
    bool isLegalAsecId(const char *id) const;
};

extern "C" {
#endif /* __cplusplus */
#define UNMOUNT_NOT_MOUNTED_ERR -2
    int vold_disableVol(const char *label);
    int vold_getNumDirectVolumes(void);
    int vold_getDirectVolumeList(struct volume_info *v);
    int vold_unmountAllAsecs(void);
#ifdef __cplusplus
}
#endif

#endif
