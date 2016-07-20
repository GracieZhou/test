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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <fts.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/mount.h>
#include <sys/ioctl.h>
#include <dirent.h>

#include <linux/kdev_t.h>

#define LOG_TAG "Vold"

#include <openssl/md5.h>

#include <cutils/fs.h>
#include <cutils/log.h>

#include <selinux/android.h>

#include <sysutils/NetlinkEvent.h>

#include <private/android_filesystem_config.h>
// MStar Android Patch Begin
#include <linux/msdos_fs.h>
#include <ext2fs/ext2_fs.h>
#include <inttypes.h>

#include "unicode/ucnv.h"
#include "blkid/blkid.h"
// MStar Android Patch End

#include "VolumeManager.h"
#include "DirectVolume.h"
#include "ResponseCode.h"
#include "Loop.h"
#include "Ext4.h"
#include "Fat.h"
// MStar Android Patch Begin
#include "Iso.h"
#include "Cifs.h"
// MStar Android Patch End
#include "Devmapper.h"
#include "Process.h"
#include "Asec.h"
#include "cryptfs.h"

#define MASS_STORAGE_FILE_PATH  "/sys/class/android_usb/android0/f_mass_storage/lun/file"

#define ROUND_UP_POWER_OF_2(number, po2) (((!!(number & ((1U << po2) - 1))) << po2)\
                                         + (number & (~((1U << po2) - 1))))

/* writes superblock at end of file or device given by name */
static int writeSuperBlock(const char* name, struct asec_superblock *sb, unsigned int numImgSectors) {
    int sbfd = open(name, O_RDWR);
    if (sbfd < 0) {
        SLOGE("Failed to open %s for superblock write (%s)", name, strerror(errno));
        return -1;
    }

    if (lseek(sbfd, (numImgSectors * 512), SEEK_SET) < 0) {
        SLOGE("Failed to lseek for superblock (%s)", strerror(errno));
        close(sbfd);
        return -1;
    }

    if (write(sbfd, sb, sizeof(struct asec_superblock)) != sizeof(struct asec_superblock)) {
        SLOGE("Failed to write superblock (%s)", strerror(errno));
        close(sbfd);
        return -1;
    }
    close(sbfd);
    return 0;
}

static int adjustSectorNumExt4(unsigned numSectors) {
    // Ext4 started to reserve 2% or 4096 clusters, whichever is smaller for
    // preventing costly operations or unexpected ENOSPC error.
    // Ext4::format() uses default block size without clustering.
    unsigned clusterSectors = 4096 / 512;
    unsigned reservedSectors = (numSectors * 2)/100 + (numSectors % 50 > 0);
    numSectors += reservedSectors > (4096 * clusterSectors) ? (4096 * clusterSectors) : reservedSectors;
    return ROUND_UP_POWER_OF_2(numSectors, 3);
}

static int adjustSectorNumFAT(unsigned numSectors) {
    /*
    * Add some headroom
    */
    unsigned fatSize = (((numSectors * 4) / 512) + 1) * 2;
    numSectors += fatSize + 2;
    /*
    * FAT is aligned to 32 kb with 512b sectors.
    */
    return ROUND_UP_POWER_OF_2(numSectors, 6);
}

static int setupLoopDevice(char* buffer, size_t len, const char* asecFileName, const char* idHash, bool debug) {
    if (Loop::lookupActive(idHash, buffer, len)) {
        if (Loop::create(idHash, asecFileName, buffer, len)) {
            SLOGE("ASEC loop device creation failed for %s (%s)", asecFileName, strerror(errno));
            return -1;
        }
        if (debug) {
            SLOGD("New loop device created at %s", buffer);
        }
    } else {
        if (debug) {
            SLOGD("Found active loopback for %s at %s", asecFileName, buffer);
        }
    }
    return 0;
}

static int setupDevMapperDevice(char* buffer, size_t len, const char* loopDevice, const char* asecFileName, const char* key, const char* idHash , int numImgSectors, bool* createdDMDevice, bool debug) {
    if (strcmp(key, "none")) {
        if (Devmapper::lookupActive(idHash, buffer, len)) {
            if (Devmapper::create(idHash, loopDevice, key, numImgSectors,
                                  buffer, len)) {
                SLOGE("ASEC device mapping failed for %s (%s)", asecFileName, strerror(errno));
                return -1;
            }
            if (debug) {
                SLOGD("New devmapper instance created at %s", buffer);
            }
        } else {
            if (debug) {
                SLOGD("Found active devmapper for %s at %s", asecFileName, buffer);
            }
        }
        *createdDMDevice = true;
    } else {
        strcpy(buffer, loopDevice);
        *createdDMDevice = false;
    }
    return 0;
}

static void waitForDevMapper(const char *dmDevice) {
    /*
     * Wait for the device mapper node to be created. Sometimes it takes a
     * while. Wait for up to 1 second. We could also inspect incoming uevents,
     * but that would take more effort.
     */
    int tries = 25;
    while (tries--) {
        if (!access(dmDevice, F_OK) || errno != ENOENT) {
            break;
        }
        usleep(40 * 1000);
    }
}

VolumeManager *VolumeManager::sInstance = NULL;

VolumeManager *VolumeManager::Instance() {
    if (!sInstance)
        sInstance = new VolumeManager();
    return sInstance;
}

VolumeManager::VolumeManager() {
    mDebug = false;
    mVolumes = new VolumeCollection();
    mActiveContainers = new AsecIdCollection();
    mBroadcaster = NULL;
    mUmsSharingCount = 0;
    mSavedDirtyRatio = -1;
    // set dirty ratio to 0 when UMS is active
    mUmsDirtyRatio = 0;
    mVolManagerDisabled = 0;
    // EosTek Patch Begin
    mUsbAsecDir = new char[255];
    snprintf(mUsbAsecDir, 255, "/mnt/usb/sda1/.asec");
    mUsbSecureDir = new char[255];
    snprintf(mUsbSecureDir, 255, "/mnt/usb/sda1/.secure");
    mUsbSecureAsecDir = new char[255];
    snprintf(mUsbSecureAsecDir, 255, "/mnt/usb/sda1/.secure/asec");
    // EosTek Patch End
}

VolumeManager::~VolumeManager() {
    delete mVolumes;
    delete mActiveContainers;
    // EosTek Patch Begin
    delete mUsbAsecDir;
    delete mUsbSecureDir;
    delete mUsbSecureAsecDir;
    // EosTek Patch End
}

char *VolumeManager::asecHash(const char *id, char *buffer, size_t len) {
    static const char* digits = "0123456789abcdef";

    unsigned char sig[MD5_DIGEST_LENGTH];

    if (buffer == NULL) {
        SLOGE("Destination buffer is NULL");
        errno = ESPIPE;
        return NULL;
    } else if (id == NULL) {
        SLOGE("Source buffer is NULL");
        errno = ESPIPE;
        return NULL;
    } else if (len < MD5_ASCII_LENGTH_PLUS_NULL) {
        SLOGE("Target hash buffer size < %d bytes (%zu)",
                MD5_ASCII_LENGTH_PLUS_NULL, len);
        errno = ESPIPE;
        return NULL;
    }

    MD5(reinterpret_cast<const unsigned char*>(id), strlen(id), sig);

    char *p = buffer;
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++) {
        *p++ = digits[sig[i] >> 4];
        *p++ = digits[sig[i] & 0x0F];
    }
    *p = '\0';

    return buffer;
}

void VolumeManager::setDebug(bool enable) {
    mDebug = enable;
    VolumeCollection::iterator it;
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End

    for (it = mVolumes->begin(); it != mVolumes->end(); ++it) {
        (*it)->setDebug(enable);
    }
}

int VolumeManager::start() {
    return 0;
}

int VolumeManager::stop() {
    return 0;
}

int VolumeManager::addVolume(Volume *v) {
    // MStar Android Patch Begin
#ifdef VRSDCARD
    mVolumes->push_front(v);
#else
    mVolumes->push_back(v);
#endif
    // MStar Android Patch End
    return 0;
}

// MStar Android Patch Begin
static bool getVolumeUUID(const char* path, char* buf, int size) {
    blkid_cache cache = NULL;
    blkid_dev dev = NULL;
    blkid_tag_iterate iter = NULL;
    const char *type = NULL, *value = NULL;
    bool ret = false;

    if (path == NULL || buf == NULL || size < 0) {
        return false;
    }

    if (blkid_get_cache(&cache, NULL) < 0) {
        return false;
    }

    dev = blkid_get_dev(cache, path, BLKID_DEV_NORMAL);
    if (dev == NULL) {
        return false;
    }
    iter = blkid_tag_iterate_begin(dev);
    while (blkid_tag_next(iter, &type, &value) == 0) {
        if (strcmp(type,"UUID") == 0) {
            int n = strlen(value);
            if (n > size) {
                n = size;
            }
            ret = true;
            strncpy(buf,value,n);
            buf[n] = '\0';
        }
    }
    blkid_tag_iterate_end(iter);
    blkid_put_cache(cache);
    return ret;
}

class UUIDCache {
public:
    struct Entry {
        char *device;
        char *uuid;
    };

    UUIDCache() {
        uuidCache = new EntryCollection();
    }

    ~UUIDCache() {
        delete uuidCache;
    }

    void addEntry(const char *device, const char *uuid) {
        Entry *entry = NULL;
        if (device == NULL || uuid == NULL) {
            return;
        }

        entry = searchEntry(device);
        if (entry == NULL) {
            entry = new Entry();
            entry->device = strdup(device);
            uuidCache->push_back(entry);
        } else {
            if (entry->uuid != NULL) {
                free(entry->uuid);
            }
        }
        entry->uuid = strdup(uuid);
    }

    Entry *searchEntry(const char *device) {
        EntryCollection::iterator it;
        for (it = uuidCache->begin(); it != uuidCache->end(); ++it) {
            if ((*it)->device != NULL && strcmp((*it)->device, device) == 0) {
                return(*it);
            }
        }
        return NULL;
    }

private:
    typedef android::List<Entry *> EntryCollection;
    EntryCollection *uuidCache;
};

static UUIDCache uuidCache;
// EosTek Patch Begin
#define SD_MOUNT_PATH "/mnt/sdcard"
#define SD_FORMAT_FILE_PARENT "/cache/recovery"
#define SD_FORMAT_FILE_PATH "/cache/recovery/last_sdcard"
// EosTek Patch End
void VolumeManager::handleBlockEvent(NetlinkEvent *evt) {
    const char *devpath = evt->findParam("DEVPATH");
    const char *devtype = evt->findParam("DEVTYPE");
    const char *dn = evt->findParam("DEVNAME");
    int major = -1;
    int minor = -1;
    bool isRawDisk = false;
    bool isPartition = false;
    int partIdx = -1;
    bool isSDCard = false;
    char device[255];
    char uuid[255];
    VolumeCollection::iterator it;
    bool hit = false;
    static bool preDiskChangeEvent = false;

#ifdef VRSDCARD
    static bool isVirtualSDCardAllocated = false;
    static int npartscount = 0;
    SLOGD("@@@npartscount1: %i@@@", npartscount);
#endif

#define NETLINK_DEBUG

    /* Determine what block device can be allowed*/
    major = atoi(evt->findParam("MAJOR"));
    minor = atoi(evt->findParam("MINOR"));
    if (major == DISK_MAJOR) {
    } else if (major == DISK_EXTEND_MAJOR) {
        // EosTek Patch Begin
        // comment : fix vrsdcard mount bug
        const char *part_no = evt->findParam("PARTN");
        SLOGI("devpath:%s, devtype:%s, dn:%s, part_no:%s\n", devpath, devtype, dn, part_no);    
        if(part_no && MMCBLK_AS_SDCARD == atoi(part_no)) {
            SLOGI("Found sdcard !\n");
            isSDCard = true;
            if(access(SD_FORMAT_FILE_PATH, F_OK)) {
                sprintf(device, "/dev/block/mmcblk0p%d",MMCBLK_AS_SDCARD);
                SLOGI("Starting format sdcard from ext4 to vfat.\n");
                time_t t1 = time(NULL);
                if (Fat::format(device, 0, false)) {
                    SLOGE("Failed to format (%s)", strerror(errno));
                }
                time_t t2 = time(NULL);
                SLOGI("Formatting sdcard spent %d s\n",(int)(t2 - t1));
                memset(device, 0, sizeof(device));
                
                if(!mkdir(SD_FORMAT_FILE_PARENT,0770)) {
                    if(chown(SD_FORMAT_FILE_PARENT, AID_SYSTEM, AID_CACHE)) {
                        SLOGE("Failed to chown (%s)", strerror(errno));
                    } 
                } else {
                    SLOGE("Failed to mkdir (%s)", strerror(errno));
                }
                
                if(creat(SD_FORMAT_FILE_PATH, 0600) == -1) {
                    SLOGE("Failed to creat (%s)", strerror(errno));
                }
            }
        }
        // EosTek Patch End
    } else if (major == SD_MAJOR) {
        if (strncmp(dn,"mmcblk0",7) == 0) {
#ifndef MMCBLK_AS_SDCARD
            return;
#endif
        }
        // EosTek Patch Begin
        if (strncmp(devpath, "/devices/platform/ms_sdmmc.0/mmc_host/mmc1",
                strlen("/devices/platform/ms_sdmmc.0/mmc_host/mmc1"))) {
             isSDCard = true;
        }
        // isSDCard = true;
        // EosTek Patch End
       
    } else {
        return;
    }

    snprintf(device,255,"/dev/block/vold/%d:%d",major,minor);

    if (strcmp(devtype,"disk") == 0) {
        const char *nparts = evt->findParam("NPARTS");
        if (nparts) {

#ifdef VRSDCARD
            npartscount = atoi(nparts);
            SLOGD("@@@npartscount2: %i@@@", npartscount);
#endif

            int diskNumParts = atoi(nparts);
            isRawDisk = (diskNumParts == 0);
        } else {
            return ;
        }
    } else {
        const char *tmp = evt->findParam("PARTN");
        if (tmp == NULL) {
            return;
        }

#ifdef VRSDCARD
        if (evt->getAction() == NetlinkEvent::NlActionAdd) {
            npartscount--;
            SLOGD("@@@npartscount3: %i@@@", npartscount);
        }
#endif

        partIdx = atoi(tmp);
        isPartition = true;
    }

    if (isRawDisk || isPartition) {
        //first find uuid from cache
        UUIDCache::Entry *entry = uuidCache.searchEntry(device);

        if (evt->getAction() == NetlinkEvent::NlActionAdd) {
            mode_t mode = 0660 | S_IFBLK;
            dev_t dev = (major << 8) | minor;

            //if device has been now added, not add again
            if (entry != NULL && entry->uuid != NULL) {
                return;
            }

            if (mknod(device, mode, dev) < 0) {
                if (errno != EEXIST) {
                    return ;
                }
            }
            if (!getVolumeUUID(device, uuid, 255)) {
        #ifdef NETLINK_DEBUG
                SLOGD("can not get the uuid of %s when device add",device);
        #endif
                return ;
            }
        #ifdef NETLINK_DEBUG
            SLOGD("get the uuid %s of %s when device add",uuid, device);
        #endif
        } else if (evt->getAction() == NetlinkEvent::NlActionRemove) {
            //if device has been now removed, not revome again
            if (entry == NULL || entry->uuid == NULL) {
            #ifdef NETLINK_DEBUG
                SLOGD("can not get the uuid of %s when device remove",device);
            #endif
                return;
            }
            //get the uuid of the device
            strcpy(uuid,entry->uuid);
            //clear the cache entry
            free(entry->uuid);
            entry->uuid = NULL;

        #ifdef NETLINK_DEBUG
            SLOGD("get the uuid %s of %s when device remove",uuid, device);
        #endif
        } else if (evt->getAction() == NetlinkEvent::NlActionChange) {
            if (preDiskChangeEvent == false) {
                preDiskChangeEvent = true;
            }
        #ifdef NETLINK_DEBUG
            SLOGD("receive a disk change event");
        #endif
            return;
        } else {
        #ifdef NETLINK_DEBUG
            SLOGD("unknown disk event");
        #endif
            return;
        }

        /* Lookup a volume to handle this device */
        for (it = mVolumes->begin(); it != mVolumes->end(); ++it) {
            if (strcmp(uuid, (*it)->getLabel()) == 0) {
                /* When SD Card is insert into a USB SD Reader , it will be a
                * USB disk. So, we must handle this situation.
                */
                if (isSDCard) {
                    if ( strcmp(SD_MOUNT_PATH, (*it)->getMountpoint()) != 0 &&
                        strncmp("/mnt/usb/mmcblk",(*it)->getMountpoint(),15) != 0) {
                        continue;
                    }
                }

#ifdef VRSDCARD
                // The USB disk already has a record, skip the record when it should be treated as a SD Card
                if (evt->getAction() == NetlinkEvent::NlActionAdd) {
                    if ((isVirtualSDCardAllocated == false)&&(npartscount ==0)){
                        if ( strcmp(SD_MOUNT_PATH, (*it)->getMountpoint()) != 0 ) {
                            SLOGD("isVirtualSDCardAllocated == false, continue");
                            continue;
                        }
                    }
                }
#endif

                // When adding disk, skip the record which has the same uuid but has the different device path
                if (evt->getAction() == NetlinkEvent::NlActionAdd) {
                    if ((*it)->getDevicePath() != NULL) {
                        continue;
                    }
                }

                // When removing disk, make sure that the record has the same uuid and has the same device path
                if (evt->getAction() == NetlinkEvent::NlActionRemove) {
                    if ((*it)->getDevicePath() == NULL) {
                        continue;
                    } else if(strcmp(device,(*it)->getDevicePath()) != 0) {
                        continue;
                    }
                }

                (*it)->handleBlockEvent(evt);
            #ifdef NETLINK_DEBUG
                SLOGD("Device '%s' event handled by volume %s\n", devpath, (*it)->getLabel());
            #endif
                hit = true;
                if (evt->getAction() == NetlinkEvent::NlActionAdd) {
                    //if device was added at before, so cache its uuid and device path again
                    uuidCache.addEntry(device,uuid);
                    (*it)->setDevicePath(device);

                    if (preDiskChangeEvent) {
                        preDiskChangeEvent = false;
                    }
                } else {
                    (*it)->setDevicePath(NULL);
#ifdef VRSDCARD
                    isSDCard = !(strcmp(SD_MOUNT_PATH, (*it)->getMountpoint())) ;
#endif
                    if (isSDCard) {
                        Mutex::Autolock lock(mVolumesLock);
                        delete (*it);
                        mVolumes->erase(it);
#ifdef VRSDCARD
                        isVirtualSDCardAllocated = false;
                        SLOGD("@@@set isVirtualSDCardAllocated: %i@@@", isVirtualSDCardAllocated);
#endif
                    }
                    
                    // EosTek Patch Begin
                    else {
                        SLOGI("handlePartitionRemoved,ready to unlink: %s\n",device);
                        if ( 0 != unlink(device) ) {
                            SLOGE("Failed to unlink %s\n",device);
                        }
                        
                        if (!strncmp("/mnt/usb",(*it)->getMountpoint(),8)) {
                            if(rmdir((*it)->getMountpoint())) {
                                SLOGE("Failed to rmdir %s\n",(*it)->getMountpoint());
                            }
                        }
                        
                        {
                             Mutex::Autolock lock(mVolumesLock);
                             delete (*it);
                             mVolumes->erase(it);
                        }
                    }
                    // EosTek Patch End
                }

                break;
            } else {
            #ifdef NETLINK_DEBUG
                SLOGD("device name mismatch : (%s, %s %i)", uuid, (*it)->getLabel(),(*it)->getState());
            #endif
            }
        }
    } else {
        preDiskChangeEvent = true;
        return;
    }

    if (!hit) {
        static char index = 'a'-1;
        char * mountPoint = NULL;
        const char *dp = NULL;
        Volume *volume = NULL;

    #ifdef NETLINK_DEBUG
        SLOGW("No volumes handled block event for '%s'", devpath);
    #endif
        if (evt->getAction() != NetlinkEvent::NlActionAdd) {
            return;
        }

        dp = evt->findParam("DEVPATH");
        if (dp == NULL) {
            return;
        }
        
        // EosTek Patch Begin
        // comment : fix vrsdcard mount bug
        const char *part_no = evt->findParam("PARTN");
        if(part_no && MMCBLK_AS_SDCARD == atoi(part_no)) {
            SLOGI("force set sdcard flag !\n");
            isSDCard = true;
        }
        // EosTek Patch End

        /* Determine its mount point */
        if (isSDCard) {
            if (isRawDisk) {
                asprintf(&mountPoint, SD_MOUNT_PATH);
            } else if (isPartition) {
#ifdef MMCBLK_AS_SDCARD
                if (partIdx == MMCBLK_AS_SDCARD) {
                    asprintf(&mountPoint, SD_MOUNT_PATH);
                }
#else
                if (partIdx == 1) {
                    asprintf(&mountPoint, SD_MOUNT_PATH);
                } else {
                    asprintf(&mountPoint,"/mnt/usb/%s",dn);
                }
#endif
            }
        } else {
            if (!isPartition) {
                ++index;
                if (preDiskChangeEvent) {
                    preDiskChangeEvent = false;
                }
            } else {
                if (preDiskChangeEvent) {
                    ++index;
                    preDiskChangeEvent = false;
                }
            }

            if (index > 'z') {
                index = 'A';
            }
            if ( index < 'a' && index > 'Z') {
                return;
            }

#ifdef VRSDCARD
            if (isRawDisk) {
                if (isVirtualSDCardAllocated) {
                    asprintf(&mountPoint,"/mnt/usb/sd%c1",index);
                } else {
                    asprintf(&mountPoint, SD_MOUNT_PATH);
                    isVirtualSDCardAllocated = true;
                }
            }
#else
            if (isRawDisk) {
                asprintf(&mountPoint,"/mnt/usb/sd%c1",index);
            }
#endif
#ifdef VRSDCARD
            else if (isPartition && npartscount !=0) {
                asprintf(&mountPoint,"/mnt/usb/sd%c%i",index,partIdx);
            } else if (isPartition && npartscount ==0) {
                if (isVirtualSDCardAllocated == true) {
                    asprintf(&mountPoint,"/mnt/usb/sd%c%i",index,partIdx);
                } else {
                    asprintf(&mountPoint, SD_MOUNT_PATH);
                    isVirtualSDCardAllocated = true;
                    SLOGD("@@@allocate last partition /mnt/sdcard@@@");
                }
            }
#else
            else if (isPartition) {
                asprintf(&mountPoint,"/mnt/usb/sd%c%i",index,partIdx);
            }
#endif
        }
        if (mountPoint == NULL) {
            return;
        }

        // EosTek Patch Begin
        if(!strncmp(devpath,"/devices/platform/ms_sdmmc.0/mmc_host/mmc1",strlen("/devices/platform/ms_sdmmc.0/mmc_host/mmc1"))) {
            free(mountPoint);
            mountPoint = strdup("/mnt/external_sd");
        }
        // EosTek Patch End

    #ifdef NETLINK_DEBUG
        SLOGD("Allocate mount point '%s' for '%s'", mountPoint, device);
    #endif
        struct fstab_rec rec;
        int flags;
        rec.label = uuid;
        rec.mount_point = mountPoint;
        rec.partnum = partIdx;
        flags = !strcmp(mountPoint, SD_MOUNT_PATH) ? VOL_PROVIDES_ASEC : 0;
        // if we get the volume by the mount point, it means that the uuid of
        // the volume is changed. refresh the uuid of that volume
        // it is no need to consider the SD card, because uuid will change
        // when SD card changes
        volume = lookupVolume(mountPoint);
        if (volume != NULL) {
        #ifdef NETLINK_DEBUG
            SLOGD("The uuid of %s changes from %s to %s'", mountPoint, volume->getLabel(), uuid);
        #endif
            volume->setLabel(uuid);
            // EosTek Patch Begin
            if(isSDCard) {
                volume->setPartIdx(partIdx);
                volume->setFlags(flags);
            }
            // EosTek Patch End
        } else {
             volume = new DirectVolume(this, &rec, flags);
            addVolume(volume);
        }

        free(mountPoint);
        //cache the device path of device
        volume->setDevicePath(device);

        //cache the uuid of device
        uuidCache.addEntry(device, uuid);

        if ( volume->handleBlockEvent(evt) !=0 ) {
            SLOGD("New add volume fail to handle the event of %s",devpath);
        }
    }
}
// MStar Android Patch End

int VolumeManager::listVolumes(SocketClient *cli, bool broadcast) {
    VolumeCollection::iterator i;
    char msg[256];
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        char *buffer;
        asprintf(&buffer, "%s %s %d",
                 (*i)->getLabel(), (*i)->getFuseMountpoint(),
                 (*i)->getState());
        cli->sendMsg(ResponseCode::VolumeListResult, buffer, false);
        free(buffer);
        if (broadcast) {
            if((*i)->getUuid()) {
                snprintf(msg, sizeof(msg), "%s %s \"%s\"", (*i)->getLabel(),
                    (*i)->getFuseMountpoint(), (*i)->getUuid());
                mBroadcaster->sendBroadcast(ResponseCode::VolumeUuidChange,
                    msg, false);
            }
            if((*i)->getUserLabel()) {
                snprintf(msg, sizeof(msg), "%s %s \"%s\"", (*i)->getLabel(),
                    (*i)->getFuseMountpoint(), (*i)->getUserLabel());
                mBroadcaster->sendBroadcast(ResponseCode::VolumeUserLabelChange,
                    msg, false);
            }
        }
    }
    cli->sendMsg(ResponseCode::CommandOkay, "Volumes listed.", false);
    return 0;
}

int VolumeManager::formatVolume(const char *label, bool wipe) {
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    if (mVolManagerDisabled) {
        errno = EBUSY;
        return -1;
    }

    return v->formatVol(wipe);
}

// MStar Android Patch Begin
static bool getDeviceMountInfo(const char* path, char* buf, int size) {
    char device[256];
    char mount_path[256];
    char rest[256];
    FILE *fp;
    char line[1024];

    if (path == NULL || buf == NULL || size < 0) {
        return false;
    }

    if (!(fp = fopen("/proc/mounts", "r"))) {
        SLOGE("Error opening /proc/mounts (%s)", strerror(errno));
        return false;
    }

    while (fgets(line, sizeof(line), fp)) {
        line[strlen(line)-1] = '\0';
        sscanf(line, "%255s %255s %255s\n", device, mount_path, rest);
        if (!strcmp(mount_path, path)) {
            fclose(fp);
            strncpy(buf, line, size);
            return true;
        }
    }

    fclose(fp);
    return false;
}

#define IOCTL_GET_VOLUME_LABEL _IOR('r', 0x13, __u32)

static bool getFatVolumeLabel(const char* devPath, char *label, int len, int* label_len) {
    int fd;
    int ret;
    char tmp[512];
    int size = sizeof(tmp);
    int i;

    if (devPath == NULL || label == NULL || len < 0 || label_len == NULL) {
        return false;
    }

    fd = open(devPath,O_RDONLY);
    if (fd < 0) {
        SLOGE("Can not open device %s",devPath);
        return false;
    }

    memset(tmp,0,size);
    ret = ioctl(fd,IOCTL_GET_VOLUME_LABEL,tmp);
    if (ret == -1) {
        SLOGE("Can not get the FAT label");
        close(fd);
        return false;
    }

    close(fd);

    i = 0;
    while (i < size && tmp[i]) {
        ++i;
    }
    if (i > len) {
        i = len;
    }
    *label_len = i;

    strncpy(label,tmp,i);
    close(fd);
    return true;
}

static bool getNTFSVolumeLabel(const char* devPath, char *label, int len, int* label_len) {
    int fd;
    int ret;
    char tmp[512];
    int size = sizeof(tmp);
    int i;

    if (devPath == NULL || label == NULL || len < 0 || label_len == NULL) {
        return false;
    }

    fd = open(devPath,O_RDONLY);
    if (fd < 0) {
        SLOGE("Can not open device %s",devPath);
        return false;
    }

    memset(tmp,0,size);
    ret = ioctl(fd,IOCTL_GET_VOLUME_LABEL,tmp);
    if (ret == -1) {
        SLOGE("Can not get the NTFS label");
        close(fd);
        return false;
    }

    close(fd);

    i = 0;
    while (i < size && tmp[i]) {
        ++i;
    }
    if (i > len) {
        i = len;
    }
    *label_len = i;

    strncpy(label,tmp,i);
    close(fd);
    return true;
}

static bool getExtVolumeLabel(const char* devPath, char *label, int len, int* label_len) {
    int fd;
    int ret;
    struct ext2_super_block super_block;
    int i;

    if (devPath == NULL || label == NULL || len < 0 || label_len == NULL) {
        return false;
    }

    fd = open(devPath,O_RDONLY);
    if (fd < 0) {
        SLOGE("Can not open device %s",devPath);
        return false;
    }

    ret = lseek(fd,1024,0);
    if (ret < 0) {
        SLOGE("Can not seek to ext2 super block");
        close(fd);
        return false;
    }

    ret = read(fd,&super_block,sizeof(super_block));
    if (ret < 0) {
        SLOGE("Can not read ext2 super block");
        close(fd);
        return false;
    }

    if (__le16_to_cpu(super_block.s_magic) != EXT2_SUPER_MAGIC) {
        SLOGE("Ext2 super block has error magic");
        close(fd);
        return false;
    }
    close(fd);

    i = strlen(super_block.s_volume_name);
    if (i > len) {
        i = len;
    }
    *label_len = i;
    strncpy(label,super_block.s_volume_name,i);
    return true;
}

static bool getExfatVolumeLabel(const char* devPath, char *label, int len, int* label_len) {
    int fd;
    int ret;
    char tmp[512];
    int size = sizeof(tmp);
    int i;

    if (devPath == NULL || label == NULL || len < 0 || label_len == NULL) {
        return false;
    }

    fd = open(devPath,O_RDONLY);
    if (fd < 0) {
        SLOGE("Can not open device %s",devPath);
        return false;
    }

    memset(tmp,0,size);
    ret = ioctl(fd,IOCTL_GET_VOLUME_LABEL,tmp);
    if (ret == -1) {
        SLOGE("Can not get the Exfat label");
        close(fd);
        return false;
    }

    close(fd);

    i = 0;
    while (i < size && tmp[i]) {
        ++i;
    }
    if (i > len) {
        i = len;
    }
    *label_len = i;

    strncpy(label,tmp,i);
    close(fd);
    return true;
}

static const char* getLabelCodeType(char* str, int size) {
    int i;
    int j;
    unsigned char* buf = (unsigned char*)str;

    if (size > 3 && buf[0] ==0xef && buf[1] ==0xbb && buf[2] ==0xbf) {
        buf += 3;
        size -= 3;
    }
    /* Fat volume label from windows is considered as GBK. Other volume
     * labels are considered as utf-8.
     */
    for (i = 0; i < size; ++i) {
        if ((buf[i] & 0x80) == 0) {
            continue;
        } else if ((buf[i] & 0x40) == 0) {
            return "GBK";
        } else {
            int following;

            if ((buf[i] & 0x20) == 0) {
                following = 1;
            } else if ((buf[i] & 0x10) == 0) {
                following = 2;
            } else if ((buf[i] & 0x08) == 0) {
                following = 3;
            } else if ((buf[i] & 0x04) == 0) {
                following = 4;
            } else if ((buf[i] & 0x02) == 0) {
                following = 5;
            } else
                return "GBK";

            /* ASCII in utf-8 is always like 0xxxxxxx.
             * Chineses in utf-8 is always like 1110xxxx 10xxxxxx 10xxxxxx.
             * So, if we find "110xxxxx 10xxxxxx", consider it as GBK.
             */
            if (following == 1) {
                return "GBK";
            }

            for (j = 0; j < following; j++) {
                i++;
                if (i >= size)
                    goto done;

                if ((buf[i] & 0x80) == 0 || (buf[i] & 0x40))
                    return "GBK";
            }
        }
    }
done:
    return "UTF-8";
}

int VolumeManager::getVolumeLabel(SocketClient *cli, const char *pathStr) {
    char mountInfo[1024];
    char device[256];
    char mountPath[256];
    char fsType[256];
    char rest[256];
    bool bRet = false;
    char label[512];
    int size = sizeof(label);
    int label_len = 0;
    UErrorCode ErrorCode = U_ZERO_ERROR;
    const char* externalStorage = getenv("EXTERNAL_STORAGE");

    if (externalStorage == NULL) {
        externalStorage = "/mnt/sdcard";
    }

    if (strcmp(externalStorage, pathStr) == 0) {
        pathStr = SD_MOUNT_PATH;
    }

    if (getDeviceMountInfo(pathStr,mountInfo,1024)) {
        sscanf(mountInfo, "%255s %255s %255s %255s\n", device, mountPath, fsType, rest);

        if (strncmp(fsType,"vfat",4) == 0) {
            bRet = getFatVolumeLabel(pathStr,label,size,&label_len);
        } else if (strncmp(fsType,"ntfs",4) == 0) {
            bRet = getNTFSVolumeLabel(pathStr,label,size,&label_len);
        } else if (strncmp(fsType,"ext",3) == 0) {
            bRet = getExtVolumeLabel(device,label,size,&label_len);
        } if (strncmp(fsType,"exfat",5) == 0) {
            bRet = getExfatVolumeLabel(pathStr,label,size,&label_len);
        }
    }

    if (bRet) {
        char target[1024+1];
        int target_len = sizeof(target) - 1;
        const char* codeType = getLabelCodeType(label,label_len);
        int ret = 0;

        ret = ucnv_convert("UTF-8",codeType,target,target_len,label,label_len,&ErrorCode);
        if (ErrorCode == U_BUFFER_OVERFLOW_ERROR) {
            SLOGE("Too long volume label of %s",pathStr);
            return -1;
        } else if (ErrorCode == U_STRING_NOT_TERMINATED_WARNING) {
            target[ret] = '\0';
        }

        cli->sendMsg(ResponseCode::VolumeListResult,target,false);
        return 0;
    }

    return -1;
}

int VolumeManager::getVolumeUuid(SocketClient *cli, const char *pathStr) {
    Mutex::Autolock lock(mVolumesLock);
    Volume *v = lookupVolume(pathStr);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    cli->sendMsg(ResponseCode::VolumeListResult,v->getLabel(),false);
    return 0;
}

void VolumeManager::refreshVolumeUUIDAfterFormat(const char *pathStr) {
    char devicePath[255];
    char uuid[255];

    Volume *v = lookupVolume(pathStr);

    if (!v) {
        return ;
    }

    dev_t diskNode = v->getDiskDevice();
#if defined(__LP64__)
    sprintf(devicePath, "/dev/block/vold/%" PRIu64 ":%" PRIu64,
            MAJOR(diskNode), MINOR(diskNode));
#else
    sprintf(devicePath, "/dev/block/vold/%d:%d",
            MAJOR(diskNode), MINOR(diskNode));
#endif

    if (!getVolumeUUID(devicePath, uuid, 255)) {
        SLOGD("can not get the uuid of %s after device format",devicePath);
    } else {
        SLOGD("get the uuid %s of %s after device format",uuid, devicePath);
        uuidCache.addEntry(devicePath, uuid);
        v->setLabel(uuid);
    }

    return ;
}
// MStar Android Patch End

int VolumeManager::getObbMountPath(const char *sourceFile, char *mountPath, int mountPathLen) {
    char idHash[33];
    if (!asecHash(sourceFile, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", sourceFile, strerror(errno));
        return -1;
    }

    memset(mountPath, 0, mountPathLen);
    int written = snprintf(mountPath, mountPathLen, "%s/%s", Volume::LOOPDIR, idHash);
    if ((written < 0) || (written >= mountPathLen)) {
        errno = EINVAL;
        return -1;
    }

    if (access(mountPath, F_OK)) {
        errno = ENOENT;
        return -1;
    }

    return 0;
}

int VolumeManager::getAsecMountPath(const char *id, char *buffer, int maxlen, bool isUsb) {
    char asecFileName[255];

    if (!isLegalAsecId(id)) {
        SLOGE("getAsecMountPath: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

 // EosTek Patch Begin
    if (isUsb) {
        snprintf(buffer, maxlen, "%s/%s", mUsbAsecDir, id);
    } else {
        snprintf(buffer, maxlen, "%s/%s", Volume::ASECDIR, id);
    }
    // EosTek Patch End

    return 0;
}

int VolumeManager::getAsecFilesystemPath(const char *id, char *buffer, int maxlen) {
    char asecFileName[255];

    if (!isLegalAsecId(id)) {
        SLOGE("getAsecFilesystemPath: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

    if (findAsec(id, asecFileName, sizeof(asecFileName))) {
        SLOGE("Couldn't find ASEC %s", id);
        return -1;
    }

    memset(buffer, 0, maxlen);
    if (access(asecFileName, F_OK)) {
        errno = ENOENT;
        return -1;
    }

    int written = snprintf(buffer, maxlen, "%s", asecFileName);
    if ((written < 0) || (written >= maxlen)) {
        errno = EINVAL;
        return -1;
    }

    return 0;
}

int VolumeManager::createAsec(const char *id, unsigned int numSectors, const char *fstype,
        const char *key, const int ownerUid, bool isExternal, bool isUsb) {
    struct asec_superblock sb;
    memset(&sb, 0, sizeof(sb));
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End

    if (!isLegalAsecId(id)) {
        SLOGE("createAsec: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }
	    // EosTek Patch Begin
    SLOGD("creatAsec %s, isUsb=%d", id, isUsb);
    if (isUsb) {
        int result = -1;
        if (access(mUsbSecureDir, F_OK)) {
            result = mkdir(mUsbSecureDir, 0600);
            SLOGD("mkdir: %s, result: %d", mUsbSecureDir, result);
        }
        if (access(mUsbSecureAsecDir, F_OK)) {
            result = mkdir(mUsbSecureAsecDir, 0600);
            SLOGD("mkdir: %s, result: %d", mUsbSecureAsecDir, result);
        }
    }
    // EosTek Patch End

    const bool wantFilesystem = strcmp(fstype, "none");
    bool usingExt4 = false;
    if (wantFilesystem) {
        usingExt4 = !strcmp(fstype, "ext4");
        if (usingExt4) {
            sb.c_opts |= ASEC_SB_C_OPTS_EXT4;
        } else if (strcmp(fstype, "fat")) {
            SLOGE("Invalid filesystem type %s", fstype);
            errno = EINVAL;
            return -1;
        }
    }

    sb.magic = ASEC_SB_MAGIC;
    sb.ver = ASEC_SB_VER;

    if (numSectors < ((1024*1024)/512)) {
        SLOGE("Invalid container size specified (%d sectors)", numSectors);
        errno = EINVAL;
        return -1;
    }

    if (lookupVolume(id)) {
        SLOGE("ASEC id '%s' currently exists", id);
        errno = EADDRINUSE;
        return -1;
    }

    char asecFileName[255];

    if (!findAsec(id, asecFileName, sizeof(asecFileName))) {
        SLOGE("ASEC file '%s' currently exists - destroy it first! (%s)",
                asecFileName, strerror(errno));
        errno = EADDRINUSE;
        return -1;
    }
    // EosTek Patch Begin
    const char *asecDir = isUsb ? mUsbSecureAsecDir: (isExternal ? Volume::SEC_ASECDIR_EXT : Volume::SEC_ASECDIR_INT);
    // EosTek Patch End
    int written = snprintf(asecFileName, sizeof(asecFileName), "%s/%s.asec", asecDir, id);
    if ((written < 0) || (size_t(written) >= sizeof(asecFileName))) {
        errno = EINVAL;
        return -1;
    }

    if (!access(asecFileName, F_OK)) {
        SLOGE("ASEC file '%s' currently exists - destroy it first! (%s)",
                asecFileName, strerror(errno));
        errno = EADDRINUSE;
        return -1;
    }

    unsigned numImgSectors;
    if (usingExt4)
        numImgSectors = adjustSectorNumExt4(numSectors);
    else
        numImgSectors = adjustSectorNumFAT(numSectors);

    // Add +1 for our superblock which is at the end
    if (Loop::createImageFile(asecFileName, numImgSectors + 1)) {
        SLOGE("ASEC image file creation failed (%s)", strerror(errno));
        return -1;
    }

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        unlink(asecFileName);
        return -1;
    }

    char loopDevice[255];
    if (Loop::create(idHash, asecFileName, loopDevice, sizeof(loopDevice))) {
        SLOGE("ASEC loop device creation failed (%s)", strerror(errno));
        unlink(asecFileName);
        return -1;
    }

    char dmDevice[255];
    bool cleanupDm = false;

    if (strcmp(key, "none")) {
        // XXX: This is all we support for now
        sb.c_cipher = ASEC_SB_C_CIPHER_TWOFISH;
        if (Devmapper::create(idHash, loopDevice, key, numImgSectors, dmDevice,
                             sizeof(dmDevice))) {
            SLOGE("ASEC device mapping failed (%s)", strerror(errno));
            Loop::destroyByDevice(loopDevice);
            unlink(asecFileName);
            return -1;
        }
        cleanupDm = true;
    } else {
        sb.c_cipher = ASEC_SB_C_CIPHER_NONE;
        strcpy(dmDevice, loopDevice);
    }

    /*
     * Drop down the superblock at the end of the file
     */
    if (writeSuperBlock(loopDevice, &sb, numImgSectors)) {
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        unlink(asecFileName);
        return -1;
    }

    if (wantFilesystem) {
        int formatStatus;
        char mountPoint[255];

        int written = snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
        if ((written < 0) || (size_t(written) >= sizeof(mountPoint))) {
            SLOGE("ASEC fs format failed: couldn't construct mountPoint");
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            unlink(asecFileName);
            return -1;
        }

        if (usingExt4) {
            formatStatus = Ext4::format(dmDevice, numImgSectors, mountPoint);
        } else {
            formatStatus = Fat::format(dmDevice, numImgSectors, 0);
        }

        if (formatStatus < 0) {
            SLOGE("ASEC fs format failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            unlink(asecFileName);
            return -1;
        }
        // EosTek Patch Begin
        if (isUsb) {
            if (access(mUsbAsecDir, F_OK)) {
                int result = mkdir(mUsbAsecDir, 0777);
                SLOGE("mkdir: %s, result: %d", mUsbAsecDir, result);
            }
            snprintf(mountPoint, sizeof(mountPoint), "%s/%s", mUsbAsecDir, id);
        } else {
            snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
        }
        // EosTek Patch End

        if (mkdir(mountPoint, 0000)) {
            if (errno != EEXIST) {
                SLOGE("Mountpoint creation failed (%s)", strerror(errno));
                if (cleanupDm) {
                    Devmapper::destroy(idHash);
                }
                Loop::destroyByDevice(loopDevice);
                unlink(asecFileName);
                return -1;
            }
        }

        int mountStatus;
        if (usingExt4) {
            mountStatus = Ext4::doMount(dmDevice, mountPoint, false, false, false);
        } else {
            mountStatus = Fat::doMount(dmDevice, mountPoint, false, false, false, ownerUid, 0, 0000,
                    false);
        }

        if (mountStatus) {
            SLOGE("ASEC FAT mount failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            unlink(asecFileName);
            return -1;
        }

        if (usingExt4) {
            int dirfd = open(mountPoint, O_DIRECTORY);
            if (dirfd >= 0) {
                if (fchown(dirfd, ownerUid, AID_SYSTEM)
                        || fchmod(dirfd, S_IRUSR | S_IWUSR | S_IXUSR | S_ISGID | S_IRGRP | S_IXGRP)) {
                    SLOGI("Cannot chown/chmod new ASEC mount point %s", mountPoint);
                }
                close(dirfd);
            }
        }
    } else {
        SLOGI("Created raw secure container %s (no filesystem)", id);
    }

    mActiveContainers->push_back(new ContainerData(strdup(id), ASEC));
    return 0;
}

int VolumeManager::resizeAsec(const char *id, unsigned numSectors, const char *key) {
    char asecFileName[255];
    char mountPoint[255];
    bool cleanupDm = false;

    if (!isLegalAsecId(id)) {
        SLOGE("resizeAsec: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

    if (findAsec(id, asecFileName, sizeof(asecFileName))) {
        SLOGE("Couldn't find ASEC %s", id);
        return -1;
    }

    int written = snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
    if ((written < 0) || (size_t(written) >= sizeof(mountPoint))) {
       SLOGE("ASEC resize failed for %s: couldn't construct mountpoint", id);
       return -1;
    }

    if (isMountpointMounted(mountPoint)) {
       SLOGE("ASEC %s mounted. Unmount before resizing", id);
       errno = EBUSY;
       return -1;
    }

    struct asec_superblock sb;
    int fd;
    unsigned int oldNumSec = 0;

    if ((fd = open(asecFileName, O_RDONLY)) < 0) {
        SLOGE("Failed to open ASEC file (%s)", strerror(errno));
        return -1;
    }

    struct stat info;
    if (fstat(fd, &info) < 0) {
        SLOGE("Failed to get file size (%s)", strerror(errno));
        close(fd);
        return -1;
    }

    oldNumSec = info.st_size / 512;

    unsigned numImgSectors;
    if (sb.c_opts & ASEC_SB_C_OPTS_EXT4)
        numImgSectors = adjustSectorNumExt4(numSectors);
    else
        numImgSectors = adjustSectorNumFAT(numSectors);
    /*
     *  add one block for the superblock
     */
    SLOGD("Resizing from %d sectors to %d sectors", oldNumSec, numImgSectors + 1);
    if (oldNumSec == numImgSectors + 1) {
        SLOGW("Size unchanged; ignoring resize request");
        return 0;
    } else if (oldNumSec > numImgSectors + 1) {
        SLOGE("Only growing is currently supported.");
        close(fd);
        return -1;
    }

    /*
     * Try to read superblock.
     */
    memset(&sb, 0, sizeof(struct asec_superblock));
    if (lseek(fd, ((oldNumSec - 1) * 512), SEEK_SET) < 0) {
        SLOGE("lseek failed (%s)", strerror(errno));
        close(fd);
        return -1;
    }
    if (read(fd, &sb, sizeof(struct asec_superblock)) != sizeof(struct asec_superblock)) {
        SLOGE("superblock read failed (%s)", strerror(errno));
        close(fd);
        return -1;
    }
    close(fd);

    if (mDebug) {
        SLOGD("Container sb magic/ver (%.8x/%.2x)", sb.magic, sb.ver);
    }
    if (sb.magic != ASEC_SB_MAGIC || sb.ver != ASEC_SB_VER) {
        SLOGE("Bad container magic/version (%.8x/%.2x)", sb.magic, sb.ver);
        errno = EMEDIUMTYPE;
        return -1;
    }

    if (!(sb.c_opts & ASEC_SB_C_OPTS_EXT4)) {
        SLOGE("Only ext4 partitions are supported for resize");
        errno = EINVAL;
        return -1;
    }

    if (Loop::resizeImageFile(asecFileName, numImgSectors + 1)) {
        SLOGE("Resize of ASEC image file failed. Could not resize %s", id);
        return -1;
    }

    /*
     * Drop down a copy of the superblock at the end of the file
     */
    if (writeSuperBlock(asecFileName, &sb, numImgSectors))
        goto fail;

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        goto fail;
    }

    char loopDevice[255];
    if (setupLoopDevice(loopDevice, sizeof(loopDevice), asecFileName, idHash, mDebug))
        goto fail;

    char dmDevice[255];

    if (setupDevMapperDevice(dmDevice, sizeof(dmDevice), loopDevice, asecFileName, key, idHash, numImgSectors, &cleanupDm, mDebug)) {
        Loop::destroyByDevice(loopDevice);
        goto fail;
    }

    /*
     * Wait for the device mapper node to be created.
     */
    waitForDevMapper(dmDevice);

    if (Ext4::resize(dmDevice, numImgSectors)) {
        SLOGE("Unable to resize %s (%s)", id, strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        goto fail;
    }

    return 0;
fail:
    Loop::resizeImageFile(asecFileName, oldNumSec);
    return -1;
}
// EosTek Patch Begin
int VolumeManager::finalizeAsec(const char *id, bool isUsb) {
// EosTek Patch End
    char asecFileName[255];
    char loopDevice[255];
    char mountPoint[255];

    if (!isLegalAsecId(id)) {
        SLOGE("finalizeAsec: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

    if (findAsec(id, asecFileName, sizeof(asecFileName))) {
        SLOGE("Couldn't find ASEC %s", id);
        return -1;
    }

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        return -1;
    }

    if (Loop::lookupActive(idHash, loopDevice, sizeof(loopDevice))) {
        SLOGE("Unable to finalize %s (%s)", id, strerror(errno));
        return -1;
    }

    unsigned int nr_sec = 0;
    struct asec_superblock sb;

    if (Loop::lookupInfo(loopDevice, &sb, &nr_sec)) {
        return -1;
    }
    // EosTek Patch Begin
    if (isUsb) {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", mUsbAsecDir, id);
    } else {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
    }
    // EosTek Patch End
    int result = 0;
    if (sb.c_opts & ASEC_SB_C_OPTS_EXT4) {
        result = Ext4::doMount(loopDevice, mountPoint, true, true, true);
    } else {
        result = Fat::doMount(loopDevice, mountPoint, true, true, true, 0, 0, 0227, false);
    }

    if (result) {
        SLOGE("ASEC finalize mount failed (%s)", strerror(errno));
        return -1;
    }

    if (mDebug) {
        SLOGD("ASEC %s finalized", id);
    }
    return 0;
}
// EosTek Patch Begin
int VolumeManager::fixupAsecPermissions(const char *id, gid_t gid, const char* filename, bool isUsb) {
// EosTek Patch End
    char asecFileName[255];
    char loopDevice[255];
    char mountPoint[255];

    if (gid < AID_APP) {
        SLOGE("Group ID is not in application range");
        return -1;
    }

    if (!isLegalAsecId(id)) {
        SLOGE("fixupAsecPermissions: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

    if (findAsec(id, asecFileName, sizeof(asecFileName))) {
        SLOGE("Couldn't find ASEC %s", id);
        return -1;
    }

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        return -1;
    }

    if (Loop::lookupActive(idHash, loopDevice, sizeof(loopDevice))) {
        SLOGE("Unable fix permissions during lookup on %s (%s)", id, strerror(errno));
        return -1;
    }

    unsigned int nr_sec = 0;
    struct asec_superblock sb;

    if (Loop::lookupInfo(loopDevice, &sb, &nr_sec)) {
        return -1;
    }
    // EosTek Patch Begin
    if (isUsb) {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", mUsbAsecDir, id);
    } else {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
    }
    // EosTek Patch End
    int result = 0;
    if ((sb.c_opts & ASEC_SB_C_OPTS_EXT4) == 0) {
        return 0;
    }

    SLOGI("---->fixupAsecPermissions: loopDevice:%s  mountPoint:%s\n", loopDevice, mountPoint);

    int ret = Ext4::doMount(loopDevice, mountPoint,
            false /* read-only */,
            true  /* remount */,
            false /* executable */);
    if (ret) {
        SLOGE("Unable remount to fix permissions for %s (%s)", id, strerror(errno));
        return -1;
    }

    char *paths[] = { mountPoint, NULL };

    FTS *fts = fts_open(paths, FTS_PHYSICAL | FTS_NOCHDIR | FTS_XDEV, NULL);
    if (fts) {
        // Traverse the entire hierarchy and chown to system UID.
        for (FTSENT *ftsent = fts_read(fts); ftsent != NULL; ftsent = fts_read(fts)) {
            // We don't care about the lost+found directory.
            if (!strcmp(ftsent->fts_name, "lost+found")) {
                continue;
            }

            /*
             * There can only be one file marked as private right now.
             * This should be more robust, but it satisfies the requirements
             * we have for right now.
             */
            const bool privateFile = !strcmp(ftsent->fts_name, filename);

            int fd = open(ftsent->fts_accpath, O_NOFOLLOW);
            if (fd < 0) {
                SLOGE("Couldn't open file %s: %s", ftsent->fts_accpath, strerror(errno));
                result = -1;
                continue;
            }

            result |= fchown(fd, AID_SYSTEM, privateFile? gid : AID_SYSTEM);

            if (ftsent->fts_info & FTS_D) {
                result |= fchmod(fd, 0755);
            } else if (ftsent->fts_info & FTS_F) {
                result |= fchmod(fd, privateFile ? 0640 : 0644);
            }

            if (selinux_android_restorecon(ftsent->fts_path, 0) < 0) {
                SLOGE("restorecon failed for %s: %s\n", ftsent->fts_path, strerror(errno));
                result |= -1;
            }

            close(fd);
        }
        fts_close(fts);

        // Finally make the directory readable by everyone.
        int dirfd = open(mountPoint, O_DIRECTORY);
        if (dirfd < 0 || fchmod(dirfd, 0755)) {
            SLOGE("Couldn't change owner of existing directory %s: %s", mountPoint, strerror(errno));
            result |= -1;
        }
        close(dirfd);
    } else {
        result |= -1;
    }

    result |= Ext4::doMount(loopDevice, mountPoint,
            true /* read-only */,
            true /* remount */,
            true /* execute */);

    if (result) {
        SLOGE("ASEC fix permissions failed (%s)", strerror(errno));
        return -1;
    }

    if (mDebug) {
        SLOGD("ASEC %s permissions fixed", id);
    }
    return 0;
}
// EosTek Patch Begin
int VolumeManager::renameAsec(const char *id1, const char *id2, bool isUsb) {
// EosTek Patch End
    char asecFilename1[255];
    char *asecFilename2;
    char mountPoint[255];

    const char *dir;

    if (!isLegalAsecId(id1)) {
        SLOGE("renameAsec: Invalid asec id1 \"%s\"", id1);
        errno = EINVAL;
        return -1;
    }

    if (!isLegalAsecId(id2)) {
        SLOGE("renameAsec: Invalid asec id2 \"%s\"", id2);
        errno = EINVAL;
        return -1;
    }

    if (findAsec(id1, asecFilename1, sizeof(asecFilename1), &dir)) {
        SLOGE("Couldn't find ASEC %s", id1);
        return -1;
    }

    asprintf(&asecFilename2, "%s/%s.asec", dir, id2);
    // EosTek Patch Begin
    if (isUsb) {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", mUsbAsecDir, id1);
    } else {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id1);
    }
    // EosTek Patch End
    if (isMountpointMounted(mountPoint)) {
        SLOGW("Rename attempt when src mounted");
        errno = EBUSY;
        goto out_err;
    }
    // EosTek Patch Begin
    if (isUsb) {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", mUsbAsecDir, id2);
    } else {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id2);
    }
    // EosTek Patch End
    if (isMountpointMounted(mountPoint)) {
        SLOGW("Rename attempt when dst mounted");
        errno = EBUSY;
        goto out_err;
    }

    if (!access(asecFilename2, F_OK)) {
        SLOGE("Rename attempt when dst exists");
        errno = EADDRINUSE;
        goto out_err;
    }

    if (rename(asecFilename1, asecFilename2)) {
        SLOGE("Rename of '%s' to '%s' failed (%s)", asecFilename1, asecFilename2, strerror(errno));
        goto out_err;
    }

    free(asecFilename2);
    return 0;

out_err:
    free(asecFilename2);
    return -1;
}

#define UNMOUNT_RETRIES 5
#define UNMOUNT_SLEEP_BETWEEN_RETRY_MS (1000 * 1000)
// EosTek Patch Begin
int VolumeManager::unmountAsec(const char *id, bool force, bool isUsb) {
// EosTek Patch End
    char asecFileName[255];
    char mountPoint[255];

    if (!isLegalAsecId(id)) {
        SLOGE("unmountAsec: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

    if (findAsec(id, asecFileName, sizeof(asecFileName))) {
        SLOGE("Couldn't find ASEC %s", id);
        return -1;
    }
    // EosTek Patch Begin
    if (isUsb) {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", mUsbAsecDir, id);
    } else {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
    }
    // EosTek Patch End
    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        return -1;
    }

    return unmountLoopImage(id, idHash, asecFileName, mountPoint, force);
}

int VolumeManager::unmountObb(const char *fileName, bool force) {
    char mountPoint[255];

    char idHash[33];
    if (!asecHash(fileName, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", fileName, strerror(errno));
        return -1;
    }

    int written = snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::LOOPDIR, idHash);
    if ((written < 0) || (size_t(written) >= sizeof(mountPoint))) {
        SLOGE("OBB unmount failed for %s: couldn't construct mountpoint", fileName);
        return -1;
    }

    return unmountLoopImage(fileName, idHash, fileName, mountPoint, force);
}

int VolumeManager::unmountLoopImage(const char *id, const char *idHash,
        const char *fileName, const char *mountPoint, bool force) {
    if (!isMountpointMounted(mountPoint)) {
        SLOGE("Unmount request for %s when not mounted", id);
        errno = ENOENT;
        return -1;
    }

    int i, rc;
    for (i = 1; i <= UNMOUNT_RETRIES; i++) {
        rc = umount(mountPoint);
        if (!rc) {
            break;
        }
        if (rc && (errno == EINVAL || errno == ENOENT)) {
            SLOGI("Container %s unmounted OK", id);
            rc = 0;
            break;
        }
        SLOGW("%s unmount attempt %d failed (%s)",
              id, i, strerror(errno));

        int action = 0; // default is to just complain

        if (force) {
            if (i > (UNMOUNT_RETRIES - 2))
                action = 2; // SIGKILL
            else if (i > (UNMOUNT_RETRIES - 3))
                action = 1; // SIGHUP
        }

        Process::killProcessesWithOpenFiles(mountPoint, action);
        usleep(UNMOUNT_SLEEP_BETWEEN_RETRY_MS);
    }

    if (rc) {
        errno = EBUSY;
        SLOGE("Failed to unmount container %s (%s)", id, strerror(errno));
        return -1;
    }

    int retries = 10;

    while(retries--) {
        // MStar Android Patch Begin
        if (!rmdir(mountPoint) || errno == ENOENT) {
            break;
        }
        // MStar Android Patch End

        SLOGW("Failed to rmdir %s (%s)", mountPoint, strerror(errno));
        usleep(UNMOUNT_SLEEP_BETWEEN_RETRY_MS);
    }

    if (!retries) {
        SLOGE("Timed out trying to rmdir %s (%s)", mountPoint, strerror(errno));
    }

    for (i=1; i <= UNMOUNT_RETRIES; i++) {
        if (Devmapper::destroy(idHash) && errno != ENXIO) {
            SLOGE("Failed to destroy devmapper instance (%s)", strerror(errno));
            usleep(UNMOUNT_SLEEP_BETWEEN_RETRY_MS);
            continue;
        } else {
          break;
        }
    }

    char loopDevice[255];
    if (!Loop::lookupActive(idHash, loopDevice, sizeof(loopDevice))) {
        Loop::destroyByDevice(loopDevice);
    } else {
        SLOGW("Failed to find loop device for {%s} (%s)", fileName, strerror(errno));
    }

    AsecIdCollection::iterator it;
    for (it = mActiveContainers->begin(); it != mActiveContainers->end(); ++it) {
        ContainerData* cd = *it;
        if (!strcmp(cd->id, id)) {
            free(*it);
            mActiveContainers->erase(it);
            break;
        }
    }
    if (it == mActiveContainers->end()) {
        SLOGW("mActiveContainers is inconsistent!");
    }
    return 0;
}
// EosTek Patch Begin
int VolumeManager::destroyAsec(const char *id, bool force, bool isUsb) {
// EosTek Patch End
    char asecFileName[255];
    char mountPoint[255];

    if (!isLegalAsecId(id)) {
        SLOGE("destroyAsec: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

    if (findAsec(id, asecFileName, sizeof(asecFileName))) {
        SLOGE("Couldn't find ASEC %s", id);
        return -1;
    }
    // EosTek Patch Begin
    if (isUsb) {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", mUsbAsecDir, id);
    } else {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
    }
    // EosTek Patch End
    if (isMountpointMounted(mountPoint)) {
        if (mDebug) {
            SLOGD("Unmounting container before destroy");
        }
        if (unmountAsec(id, force, isUsb)/*EosTek Patch*/) {
            SLOGE("Failed to unmount asec %s for destroy (%s)", id, strerror(errno));
            return -1;
        }
    }

    if (unlink(asecFileName)) {
        SLOGE("Failed to unlink asec '%s' (%s)", asecFileName, strerror(errno));
        return -1;
    }

    if (mDebug) {
        SLOGD("ASEC %s destroyed", id);
    }
    return 0;
}

/*
 * Legal ASEC ids consist of alphanumeric characters, '-',
 * '_', or '.'. ".." is not allowed. The first or last character
 * of the ASEC id cannot be '.' (dot).
 */
bool VolumeManager::isLegalAsecId(const char *id) const {
    size_t i;
    size_t len = strlen(id);

    if (len == 0) {
        return false;
    }
    if ((id[0] == '.') || (id[len - 1] == '.')) {
        return false;
    }

    for (i = 0; i < len; i++) {
        if (id[i] == '.') {
            // i=0 is guaranteed never to have a dot. See above.
            if (id[i-1] == '.') return false;
            continue;
        }
        if (id[i] == '_' || id[i] == '-') continue;
        if (id[i] >= 'a' && id[i] <= 'z') continue;
        if (id[i] >= 'A' && id[i] <= 'Z') continue;
        if (id[i] >= '0' && id[i] <= '9') continue;
        return false;
    }

    return true;
}

bool VolumeManager::isAsecInDirectory(const char *dir, const char *asecName) const {
    int dirfd = open(dir, O_DIRECTORY);
    if (dirfd < 0) {
        SLOGE("Couldn't open internal ASEC dir (%s)", strerror(errno));
        return false/*EosTek Patch*/;
    }

    bool ret = false;

    if (!faccessat(dirfd, asecName, F_OK, AT_SYMLINK_NOFOLLOW)) {
        ret = true;
    }

    close(dirfd);

    return ret;
}

int VolumeManager::findAsec(const char *id, char *asecPath, size_t asecPathLen,
        const char **directory) const {
    int dirfd, fd;
    const int idLen = strlen(id);
    char *asecName;

    if (!isLegalAsecId(id)) {
        SLOGE("findAsec: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

    if (asprintf(&asecName, "%s.asec", id) < 0) {
        SLOGE("Couldn't allocate string to write ASEC name");
        return -1;
    }

    const char *dir;
    if (isAsecInDirectory(Volume::SEC_ASECDIR_INT, asecName)) {
        dir = Volume::SEC_ASECDIR_INT;
    } else if (isAsecInDirectory(Volume::SEC_ASECDIR_EXT, asecName)) {
        dir = Volume::SEC_ASECDIR_EXT;
    // EosTek Patch Begin
    } else if (isAsecInDirectory(mUsbSecureAsecDir, asecName)) {
        dir = mUsbSecureAsecDir;
    // EosTek Patch End
    } else {
        free(asecName);
        return -1;
    }

    if (directory != NULL) {
        *directory = dir;
    }

    if (asecPath != NULL) {
        int written = snprintf(asecPath, asecPathLen, "%s/%s", dir, asecName);
        if ((written < 0) || (size_t(written) >= asecPathLen)) {
            SLOGE("findAsec failed for %s: couldn't construct ASEC path", id);
            free(asecName);
            return -1;
        }
    }

    free(asecName);
    return 0;
}
// EosTek Patch Begin
int VolumeManager::mountAsec(const char *id, const char *key, int ownerUid, bool readOnly, bool isUsb) {
// EosTek Patch End
    char asecFileName[255];
    char mountPoint[255];

    if (!isLegalAsecId(id)) {
        SLOGE("mountAsec: Invalid asec id \"%s\"", id);
        errno = EINVAL;
        return -1;
    }

    if (findAsec(id, asecFileName, sizeof(asecFileName))) {
        SLOGE("Couldn't find ASEC %s", id);
        return -1;
    }
    // EosTek Patch Begin
    if (isUsb) {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", mUsbAsecDir, id);
    } else {
        snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::ASECDIR, id);
    }
    // EosTek Patch End
    if (isMountpointMounted(mountPoint)) {
        SLOGE("ASEC %s already mounted", id);
        errno = EBUSY;
        return -1;
    }

    char idHash[33];
    if (!asecHash(id, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", id, strerror(errno));
        return -1;
    }

    char loopDevice[255];
    if (setupLoopDevice(loopDevice, sizeof(loopDevice), asecFileName, idHash, mDebug))
        return -1;

    char dmDevice[255];
    bool cleanupDm = false;
    int fd;
    unsigned int nr_sec = 0;
    struct asec_superblock sb;

    if (Loop::lookupInfo(loopDevice, &sb, &nr_sec)) {
        return -1;
    }

    if (mDebug) {
        SLOGD("Container sb magic/ver (%.8x/%.2x)", sb.magic, sb.ver);
    }
    if (sb.magic != ASEC_SB_MAGIC || sb.ver != ASEC_SB_VER) {
        SLOGE("Bad container magic/version (%.8x/%.2x)", sb.magic, sb.ver);
        Loop::destroyByDevice(loopDevice);
        errno = EMEDIUMTYPE;
        return -1;
    }
    nr_sec--; // We don't want the devmapping to extend onto our superblock

    if (setupDevMapperDevice(dmDevice, sizeof(dmDevice), loopDevice, asecFileName, key, idHash , nr_sec, &cleanupDm, mDebug)) {
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    if (mkdir(mountPoint, 0000)) {
        if (errno != EEXIST) {
            SLOGE("Mountpoint creation failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            return -1;
        }
    }

    /*
     * Wait for the device mapper node to be created.
     */
    waitForDevMapper(dmDevice);

    int result;
    if (sb.c_opts & ASEC_SB_C_OPTS_EXT4) {
        result = Ext4::doMount(dmDevice, mountPoint, readOnly, false, readOnly);
    } else {
        result = Fat::doMount(dmDevice, mountPoint, readOnly, false, readOnly, ownerUid, 0, 0222, false);
    }

    if (result) {
        SLOGE("ASEC mount failed (%s)", strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    mActiveContainers->push_back(new ContainerData(strdup(id), ASEC));
    if (mDebug) {
        SLOGD("ASEC %s mounted", id);
    }
    return 0;
}

Volume* VolumeManager::getVolumeForFile(const char *fileName) {
    VolumeCollection::iterator i;

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        const char* mountPoint = (*i)->getFuseMountpoint();
        if (!strncmp(fileName, mountPoint, strlen(mountPoint))) {
            return *i;
        }
    }

    return NULL;
}

/**
 * Mounts an image file <code>img</code>.
 */
int VolumeManager::mountObb(const char *img, const char *key, int ownerGid) {
    char mountPoint[255];

    char idHash[33];
    if (!asecHash(img, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", img, strerror(errno));
        return -1;
    }

    int written = snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::LOOPDIR, idHash);
    if ((written < 0) || (size_t(written) >= sizeof(mountPoint))) {
        SLOGE("OBB mount failed for %s: couldn't construct mountpoint", img);
        return -1;
    }

    if (isMountpointMounted(mountPoint)) {
        SLOGE("Image %s already mounted", img);
        errno = EBUSY;
        return -1;
    }

    char loopDevice[255];
    if (setupLoopDevice(loopDevice, sizeof(loopDevice), img, idHash, mDebug))
        return -1;

    char dmDevice[255];
    bool cleanupDm = false;
    int fd;
    unsigned int nr_sec = 0;

    if ((fd = open(loopDevice, O_RDWR)) < 0) {
        SLOGE("Failed to open loopdevice (%s)", strerror(errno));
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    if (ioctl(fd, BLKGETSIZE, &nr_sec)) {
        SLOGE("Failed to get loop size (%s)", strerror(errno));
        Loop::destroyByDevice(loopDevice);
        close(fd);
        return -1;
    }

    close(fd);

    if (setupDevMapperDevice(dmDevice, sizeof(loopDevice), loopDevice, img,key, idHash , nr_sec, &cleanupDm, mDebug)) {
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    if (mkdir(mountPoint, 0755)) {
        if (errno != EEXIST) {
            SLOGE("Mountpoint creation failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            return -1;
        }
    }

    if (Fat::doMount(dmDevice, mountPoint, true, false, true, 0, ownerGid,
                     0227, false)) {
        SLOGE("Image mount failed (%s)", strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    mActiveContainers->push_back(new ContainerData(strdup(img), OBB));
    if (mDebug) {
        SLOGD("Image %s mounted", img);
    }
    return 0;
}

int VolumeManager::mountVolume(const char *label) {
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }
    // EosTek Patch Begin
    int res = v->mountVol();
    if (!res) {
        // update usb path
        SLOGD("mountVolume, label=%s, mUsbAsecDir=%s", label, mUsbAsecDir);
        if (!strncmp(label, "/mnt/usb", 8) && strncmp(label, mUsbAsecDir, strlen(label))) {
            snprintf(mUsbAsecDir, 255, "%s/%s", label, ".asec");
            SLOGD("mountVolume, mUsbAsecDir=%s", mUsbAsecDir);
            snprintf(mUsbSecureDir, 255, "%s/%s", label, ".secure");
            SLOGD("mountVolume, mUsbSecureDir=%s", mUsbSecureDir);
            snprintf(mUsbSecureAsecDir, 255, "%s/%s", label, ".secure/asec");
            SLOGD("mountVolume, mUsbSecureAsecDir=%s", mUsbSecureAsecDir);
        }
    }

    return res;
    // EosTek Patch End
}

// EosTek Patch Begin
int VolumeManager::updateUsbAsec(const char *label) {
    SLOGD("updateUsbAsec : %s",label);
    if(!label) {
        SLOGE("mount point cannot be null !");
        return -1;
    }

    Mutex::Autolock lock(mVolumesLock);
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    // update usb asec path

    if (strncmp(label, mUsbAsecDir, strlen(label))) {
        SLOGD("updateUsbAsec:: --- before update UsbAsecDir=%s",mUsbAsecDir);
        snprintf(mUsbAsecDir, 255, "%s/%s", label, ".asec");
        snprintf(mUsbSecureDir, 255, "%s/%s", label, ".secure");
        snprintf(mUsbSecureAsecDir, 255, "%s/%s", label, ".secure/asec");

        SLOGD("updateUsbAsec:: +++ after update UsbAsecDir=%s",mUsbAsecDir);
        return 0;
    }

    return -1;
}
// EosTek Patch End

int VolumeManager::listMountedObbs(SocketClient* cli) {
    char device[256];
    char mount_path[256];
    char rest[256];
    FILE *fp;
    char line[1024];

    if (!(fp = fopen("/proc/mounts", "r"))) {
        SLOGE("Error opening /proc/mounts (%s)", strerror(errno));
        return -1;
    }

    // Create a string to compare against that has a trailing slash
    int loopDirLen = strlen(Volume::LOOPDIR);
    char loopDir[loopDirLen + 2];
    strcpy(loopDir, Volume::LOOPDIR);
    loopDir[loopDirLen++] = '/';
    loopDir[loopDirLen] = '\0';

    while(fgets(line, sizeof(line), fp)) {
        line[strlen(line)-1] = '\0';

        /*
         * Should look like:
         * /dev/block/loop0 /mnt/obb/fc99df1323fd36424f864dcb76b76d65 ...
         */
        sscanf(line, "%255s %255s %255s\n", device, mount_path, rest);

        if (!strncmp(mount_path, loopDir, loopDirLen)) {
            int fd = open(device, O_RDONLY);
            if (fd >= 0) {
                struct loop_info64 li;
                if (ioctl(fd, LOOP_GET_STATUS64, &li) >= 0) {
                    cli->sendMsg(ResponseCode::AsecListResult,
                            (const char*) li.lo_file_name, false);
                }
                close(fd);
            }
        }
    }

    fclose(fp);
    return 0;
}

// MStar Android Patch Begin
int VolumeManager::listMountedISOs(SocketClient* cli) {
    char device[256];
    char mount_path[256];
    char rest[256];
    FILE *fp;
    char line[1024];

    if (!(fp = fopen("/proc/mounts", "r"))) {
        SLOGE("Error opening /proc/mounts (%s)", strerror(errno));
        return -1;
    }

    // Create a string to compare against that has a trailing slash
    int loopDirLen = strlen(Volume::IOSDIR);
    char loopDir[loopDirLen + 2];
    strncpy(loopDir, Volume::IOSDIR, loopDirLen);
    loopDir[loopDirLen++] = '/';
    loopDir[loopDirLen] = '\0';

    while (fgets(line, sizeof(line), fp)) {
        line[strlen(line)-1] = '\0';

        /*
         * Should look like:
         * /dev/block/loop0 /mnt/ISO/fc99df1323fd36424f864dcb76b76d65 ...
         */
        sscanf(line, "%255s %255s %255s\n", device, mount_path, rest);

        if (!strncmp(mount_path, loopDir, loopDirLen)) {
            int fd = open(device, O_RDONLY);
            if (fd >= 0) {
                struct loop_info64 li;
                if (ioctl(fd, LOOP_GET_STATUS64, &li) >= 0) {
                    cli->sendMsg(ResponseCode::AsecListResult,
                                 (const char*) li.lo_file_name, false);
                }
                close(fd);
            }
        }
    }

    fclose(fp);
    return 0;
}

int VolumeManager::mountISO(const char *img) {
    const char *key = "none";
    char mountPoint[255];

    char idHash[33];
    if (!asecHash(img,idHash,sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", img, strerror(errno));
        return -1;
    }

    int written = snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::IOSDIR, idHash);
    if ((written < 0) || (size_t(written) >= sizeof(mountPoint))) {
        SLOGE("OBB mount failed: couldn't construct mountpoint", img);
        return -1;
    }

    if (isMountpointMounted(mountPoint)) {
        SLOGE("Image %s already mounted", img);
        errno = EBUSY;
        return -1;
    }

    char loopDevice[255];
    if (Loop::lookupActive(idHash,loopDevice,sizeof(loopDevice))) {
        if (Loop::create(idHash,img,loopDevice,sizeof(loopDevice))) {
            SLOGE("Image loop device creation failed (%s)", strerror(errno));
            return -1;
        }
        if (mDebug) {
            SLOGD("New loop device created at %s", loopDevice);
        }
    } else {
        if (mDebug) {
            SLOGD("Found active loopback for %s at %s", img, loopDevice);
        }
    }

    char dmDevice[255];
    bool cleanupDm = false;
    int fd;
    unsigned int nr_sec = 0;

    if ((fd = open(loopDevice,O_RDWR))<0) {
        SLOGE("Failed to open loopdevice (%s)", strerror(errno));
        Loop::destroyByDevice(loopDevice);
        return -1;
    }

    if (ioctl(fd,BLKGETSIZE,&nr_sec)) {
        SLOGE("Failed to get loop size (%s)", strerror(errno));
        Loop::destroyByDevice(loopDevice);
        close(fd);
        return -1;
    }

    printf("iso file nr_sec:%i %i\n",nr_sec,mDebug);
    close(fd);

    if (strcmp(key,"none")) {
        if (Devmapper::lookupActive(idHash,dmDevice,sizeof(dmDevice))) {
            if (Devmapper::create(idHash, loopDevice, key, nr_sec,
                                  dmDevice, sizeof(dmDevice))) {
                SLOGE("ASEC device mapping failed (%s)", strerror(errno));
                Loop::destroyByDevice(loopDevice);
                return -1;
            }
            if (mDebug) {
                SLOGD("New devmapper instance created at %s", dmDevice);
            }
        } else {
            if (mDebug) {
                SLOGD("Found active devmapper for %s at %s", img, dmDevice);
            }
        }
        cleanupDm = true;
    } else {
        strcpy(dmDevice, loopDevice);
    }

    if (mkdir(mountPoint, 0755)) {
        if (errno != EEXIST) {
            SLOGE("Mountpoint creation failed (%s)", strerror(errno));
            if (cleanupDm) {
                Devmapper::destroy(idHash);
            }
            Loop::destroyByDevice(loopDevice);
            return -1;
        }
    }

    if (Iso::doMount(dmDevice, mountPoint, true, false, true, 1000, 1023,
                     0, false)) {
        SLOGE("Image mount failed (%s)", strerror(errno));
        if (cleanupDm) {
            Devmapper::destroy(idHash);
        }
        Loop::destroyByDevice(loopDevice);
        remove(mountPoint);
        return -1;
    }

    mActiveContainers->push_back(new ContainerData(strdup(img), ISO));

    if (mDebug) {
        SLOGD("Image %s mounted", img);
    }
    return 0;
}

int VolumeManager::unmountISO(const char *fileName, bool force){
    char mountPoint[255];

    char idHash[33];
    if (!asecHash(fileName, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", fileName, strerror(errno));
        return -1;
    }

    snprintf(mountPoint, sizeof(mountPoint), "%s/%s", Volume::IOSDIR, idHash);

    return unmountLoopImage(fileName, idHash, fileName, mountPoint, force);
}

int VolumeManager::getISOMountPath(const char *sourceFile, char *mountPath, int mountPathLen){
    char idHash[33];
    if (!asecHash(sourceFile, idHash, sizeof(idHash))) {
        SLOGE("Hash of '%s' failed (%s)", sourceFile, strerror(errno));
        return -1;
    }

    memset(mountPath, 0, mountPathLen);
    snprintf(mountPath, mountPathLen, "%s/%s", Volume::IOSDIR, idHash);

    if (access(mountPath, F_OK)) {
        errno = ENOENT;
        return -1;
    }

    return 0;
}

static int mkDir(const char * path,mode_t mode){
    int i,j;
    char * str;
    char * p;

    if (path == NULL || path[0] != '/') {
        return -1;
    }

    str=strdup(path);
    if (str == NULL) {
        return -1;
    }

    if (chdir("/") == -1) {
        return -1;
    }

    p = strtok(str, "/");
    while (p != NULL) {
        if (mkdir(p,mode) == -1) {
            if (errno != EEXIST) {
                perror("mk dir error:");
                free(str);
                return -1;
            }
        }
        chdir(p);
        p = strtok(NULL, "/");
    }

    free(str);

    if (chdir("/") == -1) {
        return -1;
    }
    return 0;
}

int VolumeManager::mountSamba(const char *host, const char *shareDir, const char *mountPoint,
                              const char *userName, const char *password, bool ro, bool executable) {
    char mountPath[255];
    snprintf(mountPath,255,"%s/%s",Volume::SAMBADIR,mountPoint);

    if (mkDir(mountPath, 0777) != 0) {
        SLOGE("Mountpoint creation failed (%s)", strerror(errno));
        return -1;
    }

    if (Cifs::doMount(host,shareDir,mountPath,userName,password,ro,
                      false,executable,1000,1023,0)) {
        remove(mountPath);
        return -1;
    }

    return 0;
}

int VolumeManager::unmountSamba(const char *mountPoint, bool force) {
    char mountPath[255];
    snprintf(mountPath,255,"%s/%s",Volume::SAMBADIR,mountPoint);

    int i, rc;
    for (i = 1; i <= UNMOUNT_RETRIES; i++) {
        rc = umount(mountPath);
        if (!rc) {
            break;
        }
        if (rc && (errno == EINVAL || errno == ENOENT)) {
            SLOGI("Samba %s unmounted OK", mountPath);
            return 0;
        }
        SLOGW("%s unmount attempt %d failed (%s)",
              mountPath, i, strerror(errno));

        int action = 0; // default is to just complain

        if (force) {
            if (i > (UNMOUNT_RETRIES - 2))
                action = 2; // SIGKILL
            else if (i > (UNMOUNT_RETRIES - 3))
                action = 1; // SIGHUP
        }

        Process::killProcessesWithOpenFiles(mountPath, action);
        usleep(UNMOUNT_SLEEP_BETWEEN_RETRY_MS);
    }

    if (rc) {
        errno = EBUSY;
        SLOGE("Failed to unmount %s (%s)", mountPath, strerror(errno));
        return -1;
    }

    int retries = 10;

    while (retries--) {
        if (!rmdir(mountPath)) {
            break;
        }

        SLOGW("Failed to rmdir %s (%s)", mountPath, strerror(errno));
        usleep(UNMOUNT_SLEEP_BETWEEN_RETRY_MS);
    }

    if (!retries) {
        SLOGE("Timed out trying to rmdir %s (%s)", mountPath, strerror(errno));
    }

    return 0;
}
// MStar Android Patch End

int VolumeManager::shareEnabled(const char *label, const char *method, bool *enabled) {
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    if (strcmp(method, "ums")) {
        errno = ENOSYS;
        return -1;
    }

    if (v->getState() != Volume::State_Shared) {
        *enabled = false;
    } else {
        *enabled = true;
    }
    return 0;
}

int VolumeManager::shareVolume(const char *label, const char *method) {
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    /*
     * Eventually, we'll want to support additional share back-ends,
     * some of which may work while the media is mounted. For now,
     * we just support UMS
     */
    if (strcmp(method, "ums")) {
        errno = ENOSYS;
        return -1;
    }

    if (v->getState() == Volume::State_NoMedia) {
        errno = ENODEV;
        return -1;
    }

    if (v->getState() != Volume::State_Idle) {
        // You need to unmount manually befoe sharing
        errno = EBUSY;
        return -1;
    }

    if (mVolManagerDisabled) {
        errno = EBUSY;
        return -1;
    }

    dev_t d = v->getShareDevice();
    if ((MAJOR(d) == 0) && (MINOR(d) == 0)) {
        // This volume does not support raw disk access
        errno = EINVAL;
        return -1;
    }

    int fd;
    char nodepath[255];
    int written = snprintf(nodepath,
             sizeof(nodepath), "/dev/block/vold/%d:%d",
             major(d), minor(d));

    if ((written < 0) || (size_t(written) >= sizeof(nodepath))) {
        SLOGE("shareVolume failed: couldn't construct nodepath");
        return -1;
    }

    if ((fd = open(MASS_STORAGE_FILE_PATH, O_WRONLY)) < 0) {
        SLOGE("Unable to open ums lunfile (%s)", strerror(errno));
        return -1;
    }

    if (write(fd, nodepath, strlen(nodepath)) < 0) {
        SLOGE("Unable to write to ums lunfile (%s)", strerror(errno));
        close(fd);
        return -1;
    }

    close(fd);
    v->handleVolumeShared();
    if (mUmsSharingCount++ == 0) {
        FILE* fp;
        mSavedDirtyRatio = -1; // in case we fail
        if ((fp = fopen("/proc/sys/vm/dirty_ratio", "r+"))) {
            char line[16];
            if (fgets(line, sizeof(line), fp) && sscanf(line, "%d", &mSavedDirtyRatio)) {
                fprintf(fp, "%d\n", mUmsDirtyRatio);
            } else {
                SLOGE("Failed to read dirty_ratio (%s)", strerror(errno));
            }
            fclose(fp);
        } else {
            SLOGE("Failed to open /proc/sys/vm/dirty_ratio (%s)", strerror(errno));
        }
    }
    return 0;
}

int VolumeManager::unshareVolume(const char *label, const char *method) {
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    if (strcmp(method, "ums")) {
        errno = ENOSYS;
        return -1;
    }

    if (v->getState() != Volume::State_Shared) {
        errno = EINVAL;
        return -1;
    }

    int fd;
    if ((fd = open(MASS_STORAGE_FILE_PATH, O_WRONLY)) < 0) {
        SLOGE("Unable to open ums lunfile (%s)", strerror(errno));
        return -1;
    }

    char ch = 0;
    if (write(fd, &ch, 1) < 0) {
        SLOGE("Unable to write to ums lunfile (%s)", strerror(errno));
        close(fd);
        return -1;
    }

    close(fd);
    v->handleVolumeUnshared();
    if (--mUmsSharingCount == 0 && mSavedDirtyRatio != -1) {
        FILE* fp;
        if ((fp = fopen("/proc/sys/vm/dirty_ratio", "r+"))) {
            fprintf(fp, "%d\n", mSavedDirtyRatio);
            fclose(fp);
        } else {
            SLOGE("Failed to open /proc/sys/vm/dirty_ratio (%s)", strerror(errno));
        }
        mSavedDirtyRatio = -1;
    }
    return 0;
}

extern "C" int vold_disableVol(const char *label) {
    VolumeManager *vm = VolumeManager::Instance();
    vm->disableVolumeManager();
    vm->unshareVolume(label, "ums");
    return vm->unmountVolume(label, true, false);
}

extern "C" int vold_getNumDirectVolumes(void) {
    VolumeManager *vm = VolumeManager::Instance();
    return vm->getNumDirectVolumes();
}

int VolumeManager::getNumDirectVolumes(void) {
    VolumeCollection::iterator i;
    int n=0;
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        if ((*i)->getShareDevice() != (dev_t)0) {
            n++;
        }
    }
    return n;
}

extern "C" int vold_getDirectVolumeList(struct volume_info *vol_list) {
    VolumeManager *vm = VolumeManager::Instance();
    return vm->getDirectVolumeList(vol_list);
}

int VolumeManager::getDirectVolumeList(struct volume_info *vol_list) {
    VolumeCollection::iterator i;
    int n=0;
    dev_t d;
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        if ((d=(*i)->getShareDevice()) != (dev_t)0) {
            (*i)->getVolInfo(&vol_list[n]);
            snprintf(vol_list[n].blk_dev, sizeof(vol_list[n].blk_dev),
                     "/dev/block/vold/%d:%d", major(d), minor(d));
            n++;
        }
    }

    return 0;
}

int VolumeManager::unmountVolume(const char *label, bool force, bool revert) {
    // MStar Android Patch Begin
    Mutex::Autolock lock(mVolumesLock);
    // MStar Android Patch End
    Volume *v = lookupVolume(label);

    if (!v) {
        errno = ENOENT;
        return -1;
    }

    if (v->getState() == Volume::State_NoMedia) {
        errno = ENODEV;
        return -1;
    }

    if (v->getState() != Volume::State_Mounted) {
        SLOGW("Attempt to unmount volume which isn't mounted (%d)\n",
             v->getState());
        errno = EBUSY;
        return UNMOUNT_NOT_MOUNTED_ERR;
    }

    // MStar Android Patch Begin
    //cleanupAsec(v, force);
    // MStar Android Patch End

    return v->unmountVol(force, revert);
}

extern "C" int vold_unmountAllAsecs(void) {
    int rc;

    VolumeManager *vm = VolumeManager::Instance();
    // EosTek Patch Begin
    SLOGD("Unmount all asec in dir: %s", vm->getUsbSecureAsecDir());
    rc = vm->unmountAllAsecsInDir(vm->getUsbSecureAsecDir(), true);

    rc = vm->unmountAllAsecsInDir(Volume::SEC_ASECDIR_EXT, false);
    if (vm->unmountAllAsecsInDir(Volume::SEC_ASECDIR_INT, false)) {
    // EosTek Patch End
        rc = -1;
    }
    return rc;
}

#define ID_BUF_LEN 256
#define ASEC_SUFFIX ".asec"
#define ASEC_SUFFIX_LEN (sizeof(ASEC_SUFFIX) - 1)
// EosTek Patch Begin
int VolumeManager::unmountAllAsecsInDir(const char *directory, bool isUsb) {
// EosTek Patch End
    DIR *d = opendir(directory);
    int rc = 0;

    if (!d) {
        SLOGE("Could not open asec dir %s", directory);
        return -1;
    }

    size_t dirent_len = offsetof(struct dirent, d_name) +
            fpathconf(dirfd(d), _PC_NAME_MAX) + 1;

    struct dirent *dent = (struct dirent *) malloc(dirent_len);
    if (dent == NULL) {
        SLOGE("Failed to allocate memory for asec dir");
        return -1;
    }

    struct dirent *result;
    while (!readdir_r(d, dent, &result) && result != NULL) {
        if (dent->d_name[0] == '.')
            continue;
        if (dent->d_type != DT_REG)
            continue;
        size_t name_len = strlen(dent->d_name);
        if (name_len > 5 && name_len < (ID_BUF_LEN + ASEC_SUFFIX_LEN - 1) &&
                !strcmp(&dent->d_name[name_len - 5], ASEC_SUFFIX)) {
            char id[ID_BUF_LEN];
            strlcpy(id, dent->d_name, name_len - 4);
            if (unmountAsec(id, true, isUsb)/*EosTek Patch*/) {
                /* Register the error, but try to unmount more asecs */
                rc = -1;
            }
        }
    }
    closedir(d);

    free(dent);

    return rc;
}

/*
 * Looks up a volume by it's label or mount-point
 */
Volume *VolumeManager::lookupVolume(const char *label) {
    VolumeCollection::iterator i;

    for (i = mVolumes->begin(); i != mVolumes->end(); ++i) {
        if (label[0] == '/') {
            if (!strcmp(label, (*i)->getFuseMountpoint()))
                return (*i);
        } else {
            if (!strcmp(label, (*i)->getLabel()))
                return (*i);
        }
    }
    return NULL;
}

bool VolumeManager::isMountpointMounted(const char *mp)
{
    char device[256];
    char mount_path[256];
    char rest[256];
    FILE *fp;
    char line[1024];

    if (!(fp = fopen("/proc/mounts", "r"))) {
        SLOGE("Error opening /proc/mounts (%s)", strerror(errno));
        return false;
    }

    while(fgets(line, sizeof(line), fp)) {
        line[strlen(line)-1] = '\0';
        sscanf(line, "%255s %255s %255s\n", device, mount_path, rest);
        if (!strcmp(mount_path, mp)) {
            fclose(fp);
            return true;
        }
    }

    fclose(fp);
    return false;
}

// MStar Android Patch Begin
void VolumeManager::lockActiveContainers() {
    mActiveContainersLock.lock();
}

void VolumeManager::unlockActiveContainers() {
    mActiveContainersLock.unlock();
}
// MStar Android Patch End

int VolumeManager::cleanupAsec(Volume *v, bool force) {
    int rc = 0;
    // MStar Android Patch Begin
    Mutex::Autolock lock(mActiveContainersLock);
    const char* externalStorage = getenv("EXTERNAL_STORAGE");
    bool primaryStorage = externalStorage && !strcmp(v->getFuseMountpoint(), externalStorage);


    char asecFileName[255];

    AsecIdCollection removeAsec;
    AsecIdCollection removeObb;

    for (AsecIdCollection::iterator it = mActiveContainers->begin(); it != mActiveContainers->end();) {
        ContainerData* cd = *it;
        ++it;

        if (primaryStorage && cd->type == ASEC) {
            if (findAsec(cd->id, asecFileName, sizeof(asecFileName))) {
                SLOGE("Couldn't find ASEC %s; cleaning up", cd->id);
                removeAsec.push_back(cd);
            } else {
                SLOGD("Found ASEC at path %s", asecFileName);
                if (!strncmp(asecFileName, Volume::SEC_ASECDIR_EXT,
                        strlen(Volume::SEC_ASECDIR_EXT))) {
                    removeAsec.push_back(cd);
                }
            }
        } else if (cd->type == OBB) {
            if (v == getVolumeForFile(cd->id)) {
                removeObb.push_back(cd);
            }
        } else if (cd->type != ISO){
            SLOGE("Unknown container type %d!", cd->type);
        }
    }

    for (AsecIdCollection::iterator it = removeAsec.begin(); it != removeAsec.end(); ++it) {
        ContainerData *cd = *it;
        SLOGI("Unmounting ASEC %s (dependent on %s)", cd->id, v->getLabel());
        if (unmountAsec(cd->id, force, !primaryStorage/*EosTek Patch*/)) {
            SLOGE("Failed to unmount ASEC %s (%s)", cd->id, strerror(errno));
            rc = -1;
        }
    }

    for (AsecIdCollection::iterator it = removeObb.begin(); it != removeObb.end(); ++it) {
        ContainerData *cd = *it;
        SLOGI("Unmounting OBB %s (dependent on %s)", cd->id, v->getLabel());
        if (unmountObb(cd->id, force)) {
            SLOGE("Failed to unmount OBB %s (%s)", cd->id, strerror(errno));
            rc = -1;
        }
    }

    return rc;
    // MStar Android Patch End
}

// MStar Android Patch Begin
int VolumeManager::cleanupISO(Volume *v, bool force) {
    char mountPoint[255];
    char idHash[33];
    int len = 0;
    AsecIdCollection::iterator it;
    Mutex::Autolock lock(mActiveContainersLock);

    strncpy(mountPoint, v->getFuseMountpoint(), 255);
rescan:
    for (it = mActiveContainers->begin(); it != mActiveContainers->end(); ++it) {
        ContainerData* cd = *it;
        if (cd->type == ISO) {
            len = strlen(mountPoint);
            if(strncmp(mountPoint,cd->id,len) == 0) {
                cd->type = (container_type_t)-1;
            }
        }
    }

    for (it = mActiveContainers->begin(); it != mActiveContainers->end(); ++it) {
        ContainerData *cd = *it;
        if (cd->type == -1) {
            if (!asecHash(cd->id,idHash,sizeof(idHash))) {
                SLOGE("Hash of '%s' failed (%s)", cd->id, strerror(errno));
                return -1;
            }
            snprintf(mountPoint,sizeof(mountPoint),"%s/%s",Volume::IOSDIR,idHash);
            cd->type = (container_type_t)-2;
            goto rescan;
        }
    }

    it = mActiveContainers->end();
    while (it != mActiveContainers->begin()) {
        --it;
        ContainerData* cd = *it;
        if (cd->type == -2) {
            SLOGI("Unmounting ISO %s (dependant on %s)", cd->id, v->getFuseMountpoint());
            if (unmountISO(cd->id, force)) {
                SLOGE("Failed to unmount ISO %s (%s)", cd->id, strerror(errno));
                return -1;
            }
            it = mActiveContainers->end();
        }
    }

    return 0;
}
// MStar Android Patch End

int VolumeManager::mkdirs(char* path) {
    // Require that path lives under a volume we manage and is mounted
    const char* emulated_source = getenv("EMULATED_STORAGE_SOURCE");
    const char* root = NULL;
    if (emulated_source && !strncmp(path, emulated_source, strlen(emulated_source))) {
        root = emulated_source;
    } else {
        Volume* vol = getVolumeForFile(path);
        if (vol && vol->getState() == Volume::State_Mounted) {
            root = vol->getMountpoint();
        }
    }

    if (!root) {
        SLOGE("Failed to find mounted volume for %s", path);
        return -EINVAL;
    }

    /* fs_mkdirs() does symlink checking and relative path enforcement */
    return fs_mkdirs(path, 0700);
}
