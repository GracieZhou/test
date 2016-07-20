/*
 * Copyright (C) 2007 The Android Open Source Project
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

#include <ctype.h>
#include <errno.h>
#include <fcntl.h>
#include <limits.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <unistd.h>
// MStar Android Patch Begin
#include <sys/mount.h>
#include <dirent.h>
#include "adb_install.h"
#include "bootloader.h"
// MStar Android Patch End
#include "common.h"
#include "install.h"
#include "mincrypt/rsa.h"
#include "minui/minui.h"
#include "minzip/SysUtil.h"
#include "minzip/Zip.h"
#include "mtdutils/mounts.h"
#include "mtdutils/mtdutils.h"
#include "roots.h"
#include "verifier.h"
#include "ui.h"
// MStar Android Patch Begin
#ifdef USE_MBOOT_UTILS
#include "mbootutils.h"
#endif
#include "minui/gb2313_unicode.h"
#ifdef BUILD_WITH_SECURE_BOOT
#include "secureboot/secure_updater.h"
#endif
#ifdef USE_URSA_UTILS
#include "ursautils.h"
#endif
// MStar Android Patch End

extern RecoveryUI* ui;

#define ASSUMED_UPDATE_BINARY_NAME  "META-INF/com/google/android/update-binary"
#define PUBLIC_KEYS_FILE "/res/keys"

// MStar Android Patch Begin
static int reload_env_enable = 0;
static int reload_panel_enable = 0;
// MStar Android Patch End

// Default allocation of progress bar segments to operations
static const int VERIFICATION_PROGRESS_TIME = 60;
static const float VERIFICATION_PROGRESS_FRACTION = 0.25;
static const float DEFAULT_FILES_PROGRESS_FRACTION = 0.4;
static const float DEFAULT_IMAGE_PROGRESS_FRACTION = 0.1;
// MStar Android Patch Begin
extern const char *dev_uuid;
extern const char *dev_label;
extern struct bootloader_message boot;

void dump(char *pdata, int length) {
    int i = 0;
    for (i = 0; i < length; i++) {
        printf("0x%X ", pdata[i]);
        if ((i + 1) % 16) {
            printf("\n");
        }
    }
    printf("\n");
}

static int
compare_label(char label[], const char *flag) {
    int ret = -1;
    int j = 0;
    char *tmp1;
    char *tmp2;
    char tmp[10];
    char exchange[128] = "\0";
    char name_label[128] = "\0";
    char ex_label[128] = "\0";

    if (NULL == (tmp1 = strchr(dev_label, 'u'))) {
        return ret;
    }
    LOGI("dev_label:#%s#,label:#%s#,flag:%S\n", dev_label, label, flag);
    while (NULL != (tmp2 = strstr(tmp1, "\\u"))) {
        memset(tmp, 0, 10);
        if ((strlen(tmp1) - strlen(tmp2)) == 5) {
            strncpy(tmp, tmp1 + 1, 4);
            strcat(ex_label, tmp);
        } else if ((strlen(tmp1) - strlen(tmp2)) == 2) {
            strncpy(tmp, tmp1 + 1, 1);
            strcat(ex_label, tmp);
        } else {
            strncpy(tmp, tmp1 + 5, 4);
            strcat(ex_label, tmp);
        }
        tmp1 = strchr(tmp2, 'u');
    }
    strcat(ex_label, tmp1 + 1);
    LOGI("ex_label:#%s#\n", ex_label);
    dump((char*)ex_label, sizeof(ex_label));
    if (0 == strcmp(flag, "GBK")) {
        while (*(label+j)) {
            memset(tmp, 0, 10);
            if (label[j] > 0x80) {
                tmp[0] = label[j];
                tmp[1] = label[j+1];
                memset(exchange, 0, 128);
                strGB2Unicode(label+j, (unsigned char*)exchange);
                sprintf(tmp, "%0.2x%0.2x", exchange[0], exchange[1]);
                strcat(name_label, tmp);
                j += 2;
            } else {
                strncpy(tmp, label + j, 1);
                strcat(name_label, tmp);
                j += 1;
            }
        }
    } else {
        wchar_t uniBuf[30];
        memset(uniBuf, 0, 30*sizeof(wchar_t));
        LOGI("label:#%s#\n", label);
        dump((char*)label, 128);
        UTF2Unicode(label, uniBuf);
        LOGI("uniBuf:#%s#\n", uniBuf);
        dump((char*)uniBuf, sizeof(uniBuf));
        wchar_t *tmp3 = uniBuf;
        while (*tmp3) {
            memset(exchange, 0, 128);
            LOGI("tmp3:\n");
            dump((char*)tmp3, sizeof(wchar_t));
            memcpy(exchange,tmp3,2);
            LOGI("exchange:\n");
            dump(exchange, 2);
            if (*tmp3 < (unsigned char)0x80) {
                strcat(name_label, exchange);
            } else {
                memset(tmp, 0, 10);
                sprintf(tmp, "%0.2x%0.2x", exchange[1], exchange[0]);
                strcat(name_label, tmp);
            }
            tmp3++;
        }
        LOGI("name_label:#%s#\n", name_label);
        dump((char*)name_label, sizeof(name_label));
    }

    if (0 == strncmp(ex_label, name_label, strlen(ex_label))) {
        LOGI("find label succeed:#%s# == #%s#\n", name_label, ex_label);
        ret = 0;
    } else {
        LOGE("find label fail:#%s# != #%s#\n", name_label, ex_label);
        ret = -1;
    }
    return ret;
}

static int
compareVolumeinfo(const char *path_uui, char *package_dev) {
    int ret = -1;
    char devname[128];
    char uuidname[128];
    DIR *dirp;
    struct dirent *dp;
    sleep(8);
    dirp = opendir("/dev/block");
    if(NULL == dirp) {
        LOGE("Can't open /dev/block\n");
        return ret;
    }

    char* p = (char*)malloc(128);
    if (NULL == p) {
        LOGE("malloc error\n");
    }
    char label[128];

    do {
        if (NULL == (dp = readdir(dirp))) {
            break;
        }
        memset(p, 0, 128);
        strncpy(p, "/dev/block/", strlen("/dev/block/"));
        strcat(p, dp->d_name);
        memset(uuidname, 0, 128);
        memset(label, 0, 128);
        memset(devname, 0, 128);
        if (0 == get_volume_info(p, NULL, uuidname, devname)) {
            char ex_dev[128] = "\0";
            //label from mboot
            snprintf(ex_dev, sizeof(ex_dev)-1, "%0.2X%0.2X%s%0.2X%0.2X", path_uui[3], path_uui[2], "-", path_uui[1], path_uui[0]);
            if ((0 == strncmp(uuidname, path_uui, strlen(uuidname)) || (0 == strncmp(uuidname, ex_dev, strlen(uuidname))))) {
                LOGI("find uuid succeed %s == %s or %s == %s\n", uuidname, path_uui, uuidname, ex_dev);
                get_volume_info(p, label, NULL, devname);
                if (0 == strncmp(label, dev_label, strlen(dev_label))) {
                    LOGI("find label succeed:%s == %s\n", label, dev_label);
                    ret = 0;
                } else {
                    const char* codeType = get_label_code_type(label, strlen(label));
                    LOGI("lable codeType: %s\n", codeType);
                    if (0 == compare_label(label, codeType)) {
                        LOGI("compare label OK!\n");
                        ret = 0;
                    }
                }
            } else {
                LOGE("find uuid fail %s != %s or %s != %s\n", uuidname, path_uui, uuidname, ex_dev);
            }
            if (0 == ret) {
                strncpy(package_dev, devname, strlen(devname));
                break;
            }
        }
    } while(1);
    free(p);
    closedir(dirp);
    return ret;
}
// MStar Android Patch End

// If the package contains an update binary, extract it and run it.
static int
try_update_binary(const char *path, ZipArchive *zip, int* wipe_cache) {
    const ZipEntry* binary_entry =
            mzFindZipEntry(zip, ASSUMED_UPDATE_BINARY_NAME);
    if (binary_entry == NULL) {
        mzCloseZipArchive(zip);
        return INSTALL_CORRUPT;
    }

    const char* binary = "/tmp/update_binary";
    unlink(binary);
    int fd = creat(binary, 0755);
    if (fd < 0) {
        mzCloseZipArchive(zip);
        LOGE("Can't make %s\n", binary);
        return INSTALL_ERROR;
    }
    bool ok = mzExtractZipEntryToFile(zip, binary_entry, fd);
    close(fd);
    mzCloseZipArchive(zip);

    if (!ok) {
        LOGE("Can't copy %s\n", ASSUMED_UPDATE_BINARY_NAME);
        return INSTALL_ERROR;
    }

    int pipefd[2];
    pipe(pipefd);

    // When executing the update binary contained in the package, the
    // arguments passed are:
    //
    //   - the version number for this interface
    //
    //   - an fd to which the program can write in order to update the
    //     progress bar.  The program can write single-line commands:
    //
    //        progress <frac> <secs>
    //            fill up the next <frac> part of of the progress bar
    //            over <secs> seconds.  If <secs> is zero, use
    //            set_progress commands to manually control the
    //            progress of this segment of the bar
    //
    //        set_progress <frac>
    //            <frac> should be between 0.0 and 1.0; sets the
    //            progress bar within the segment defined by the most
    //            recent progress command.
    //
    //        firmware <"hboot"|"radio"> <filename>
    //            arrange to install the contents of <filename> in the
    //            given partition on reboot.
    //
    //            (API v2: <filename> may start with "PACKAGE:" to
    //            indicate taking a file from the OTA package.)
    //
    //            (API v3: this command no longer exists.)
    //
    //        ui_print <string>
    //            display <string> on the screen.
    //
    //   - the name of the package zip file.
    //

    const char** args = (const char**)malloc(sizeof(char*) * 5);
    args[0] = binary;
    args[1] = EXPAND(RECOVERY_API_VERSION);   // defined in Android.mk
    char* temp = (char*)malloc(10);
    sprintf(temp, "%d", pipefd[1]);
    args[2] = temp;
    args[3] = (char*)path;
    args[4] = NULL;

    pid_t pid = fork();
    if (pid == 0) {
        umask(022);
        close(pipefd[0]);
        execv(binary, (char* const*)args);
        fprintf(stdout, "E:Can't run %s (%s)\n", binary, strerror(errno));
        _exit(-1);
    }
    close(pipefd[1]);

    *wipe_cache = 0;

    char buffer[1024];
    FILE* from_child = fdopen(pipefd[0], "r");
    while (fgets(buffer, sizeof(buffer), from_child) != NULL) {
        char* command = strtok(buffer, " \n");
        if (command == NULL) {
            continue;
        } else if (strcmp(command, "progress") == 0) {
            // MStar Android Patch Begin
            char mount_point[512];
            memset(mount_point, 0, 512);
            char *tmp1;
            if (0 == strncmp(path, "/mnt/usb/", strlen("/mnt/usb/"))) {
                tmp1 = strstr(path+9, "/");
                strncpy(mount_point, path, tmp1-path);
            } else if (0 == strncmp(path, "/mnt/sdcard", strlen("/mnt/sdcard"))) {
                tmp1 = strstr(path+11, "/");
                strncpy(mount_point, path, tmp1-path);
            } else {
                tmp1 = strrchr(path, '/');
                strncpy(mount_point, path, tmp1-path);
            }
            int ret = 0;
            if (0 == strncmp(mount_point, "/mnt", strlen("/mnt"))) {
                // EosTek Patch Begin
                // comment : skip this step for upgrading from sdcard
                //ret = scan_device(mount_point);
                // EosTek Patch End
            }
            if (-1 == ret){
                LOGE("Donot find storage equipment %s\n", mount_point);
                return INSTALL_PAUSE;
            }
            // MStar Android Patch End
            char* fraction_s = strtok(NULL, " \n");
            char* seconds_s = strtok(NULL, " \n");

            float fraction = strtof(fraction_s, NULL);
            int seconds = strtol(seconds_s, NULL, 10);

            ui->ShowProgress(fraction * (1-VERIFICATION_PROGRESS_FRACTION), seconds);
        } else if (strcmp(command, "set_progress") == 0) {
            char* fraction_s = strtok(NULL, " \n");
            float fraction = strtof(fraction_s, NULL);
            ui->SetProgress(fraction);
        } else if (strcmp(command, "ui_print") == 0) {
            char* str = strtok(NULL, "\n");
            if (str) {
                ui->Print("%s", str);
                // MStar Android Patch Begin
                if (strcmp(str, "Start to upgrade partition") == 0) {
                    mstar_system("busybox echo 0 > /sys/block/mmcblk0boot0/force_ro");
                    mstar_system("busybox sync");
                    set_bootloader_message(&boot);
                }
                // MStar Android Patch End
            } else {
                ui->Print("\n");
            }
            fflush(stdout);
        } else if (strcmp(command, "wipe_cache") == 0) {
            *wipe_cache = 1;
        // MStar Android Patch Begin
#ifdef USE_MBOOT_UTILS
        } else if (strcmp(command, "update_mboot") == 0) {
            char* filename = strtok(NULL, "\n");
            if (-1 == mbootutils_write(filename)) {
                LOGE("update mboot failed.\n");
                return INSTALL_ERROR;
            }
#endif
#ifdef BUILD_WITH_SECURE_BOOT
        } else if (strcmp(command, "update_secure_info") == 0) {
            char* partition = strtok(NULL, " \n");
            char* filename = strtok(NULL, " \n");
            if (-1 == update_secure_info(partition, filename)) {
                LOGE("update %s secureinfo failed.\n", partition);
                return INSTALL_ERROR;
            }
#ifdef BUILD_WITH_TEE
        } else if (strcmp(command, "update_nuttx_config") == 0) {
            char* config = strtok(NULL, " \n");
            char* filename = strtok(NULL, " \n");
            if (-1 == update_nuttx_config(config, filename)) {
                LOGE("update nuttx config failed.\n");
                return INSTALL_ERROR;
            }
#endif
#endif
#ifdef USE_URSA_UTILS
        } else if (strcmp(command, "update_ursa") == 0) {
            char* filename = strtok(NULL, " \n");
            ui->SetTipTitle(RecoveryUI::TIP_TITLE_BLANK_SCREEN);
            sleep(8);
            if (-1 == update_ursa(filename)) {
                LOGE("update ursa failed.\n");
                return INSTALL_ERROR;
            }
        } else if (strcmp(command, "update_raptors") == 0) {
            char* filename = strtok(NULL, " \n");
            ui->SetTipTitle(RecoveryUI::TIP_TITLE_BLANK_SCREEN);
            sleep(8);
            if (-1 == update_raptors(filename)) {
                LOGE("update raptors failed.\n");
                return INSTALL_ERROR;
            }
#endif
        // MStar Android Patch End
        } else if (strcmp(command, "clear_display") == 0) {
            ui->SetBackground(RecoveryUI::NONE);
        } else if (strcmp(command, "enable_reboot") == 0) {
            // packages can explicitly request that they want the user
            // to be able to reboot during installation (useful for
            // debugging packages that don't exit).
            ui->SetEnableReboot(true);
        // MStar Android Patch Begin
        } else if (strncmp(command, "reload_env", strlen("reload_env")) == 0) {
            char* str = strtok(NULL, "\n");
            ui->Print("[%s: %d] reload env now ...\n", __func__, __LINE__);
            reload_env_enable = 1;
        } else if (strncmp(command, "reload_panel", strlen("reload_panel")) == 0) {
            char* str = strtok(NULL, "\n");
            ui->Print("[%s: %d] reload panel now ...\n", __func__, __LINE__);
            reload_panel_enable = 1;
        // MStar Android Patch End
        } else {
            LOGE("unknown command [%s]\n", command);
        }
    }
    fclose(from_child);

    int status;
    waitpid(pid, &status, 0);
    if (!WIFEXITED(status) || WEXITSTATUS(status) != 0) {
        LOGE("Error in %s\n(Status %d)\n", path, WEXITSTATUS(status));
        return INSTALL_ERROR;
    }

    return INSTALL_SUCCESS;
}

static int
really_install_package(const char *path, int* wipe_cache, bool needs_mount) {
    // MStar Android Patch Begin
    ui->SetBackground(RecoveryUI::INSTALLING_UPDATE);
    ui->SetTipTitle(RecoveryUI::TIP_TITLE_INSTALL_PACKAGE);
    // MStar Android Patch End
    ui->Print("Finding update package...\n");
    // Give verification half the progress bar...
    ui->SetProgressType(RecoveryUI::DETERMINATE);
    ui->ShowProgress(VERIFICATION_PROGRESS_FRACTION, VERIFICATION_PROGRESS_TIME);
    // MStar Android Patch Begin
    // some USB devices is so slow, so we have to sleep 10s, in order to get uuid/label successfully
    sleep(10);
    LOGI("Update location: %s\n", path);
    //confirm uuid and package path
    ui->Print("confirm uuid and package path\n");
    if (0 == strncmp(path, "/mnt/", 5)) {
        // EosTek Patch Begin
        if(!strncmp(path,"/mnt/sdcard",11)) {
            #define SDCARD_BLK_DEV_SYMLINK "/dev/block/platform/mstar_mci.0/by-name/vrsdcard"
            
            struct stat s;
            int length;
            char link[PATH_MAX];

            if (lstat(SDCARD_BLK_DEV_SYMLINK, &s) < 0)
                return INSTALL_NONE;
            if ((s.st_mode & S_IFMT) != S_IFLNK)
                return INSTALL_NONE;
           
            // we have a symlink    
            length = readlink(SDCARD_BLK_DEV_SYMLINK, link, PATH_MAX);
            if (length <= 0) 
                return INSTALL_NONE;
            link[length] = 0;
            
            if (-1 == add_usb_device((char *)path,link)){
                LOGE("mount internal sdcard failed !\n");
                return INSTALL_NONE;
            }
            #undef SDCARD_BLK_DEV_SYMLINK
        } else {
            if (dev_uuid == NULL) {
                LOGE("dev_uuid is %s\n", dev_uuid);
                return INSTALL_NONE;
            }
            char devname[128];
            const char *uui = dev_uuid;
            memset(devname, 0, 128);
            if (-1 == compareVolumeinfo(uui, devname)) {
                LOGE("Can't find %s\n", uui);
                return INSTALL_NONE;
            }

            LOGI("devname:%s\n", devname);
            strstr(path, devname);
            char devpath[128] = "/dev/block/";
            strcat(devpath, devname);
            if (-1 == add_usb_device((char *)path, devpath)){
                return INSTALL_NONE;
            }
        }
        // EosTek Patch End
    } else {
        if (ensure_path_mounted(path) != 0) {
            LOGE("Can't mount %s\n", path);
            return INSTALL_NONE;
        }
    }
    // MStar Android Patch End

    // Map the update package into memory.
    ui->Print("Opening update package...\n");

    if (path && needs_mount) {
        if (path[0] == '@') {
            ensure_path_mounted(path+1);
        } else {
            ensure_path_mounted(path);
        }
    }

    MemMapping map;
    if (sysMapFile(path, &map) != 0) {
        LOGE("failed to map file\n");
        return INSTALL_CORRUPT;
    }

    int numKeys;
    Certificate* loadedKeys = load_keys(PUBLIC_KEYS_FILE, &numKeys);
    if (loadedKeys == NULL) {
        LOGE("Failed to load keys\n");
        // MStar Android Patch Begin
        return INSTALL_NONE;
        // MStar Android Patch End
    }
    LOGI("%d key(s) loaded from %s\n", numKeys, PUBLIC_KEYS_FILE);

    ui->Print("Verifying update package...\n");

    int err;
    err = verify_file(map.addr, map.length, loadedKeys, numKeys);
    free(loadedKeys);
    LOGI("verify_file returned %d\n", err);
    if (err != VERIFY_SUCCESS) {
        LOGE("signature verification failed\n");
        sysReleaseMap(&map);
        // MStar Android Patch Begin
        return INSTALL_NONE;
        // MStar Android Patch End
    }

    /* Try to open the package.
     */
    ZipArchive zip;
    err = mzOpenZipArchive(map.addr, map.length, &zip);
    if (err != 0) {
        LOGE("Can't open %s\n(%s)\n", path, err != -1 ? strerror(err) : "bad");
        sysReleaseMap(&map);
        // MStar Android Patch Begin
        return INSTALL_NONE;
        // MStar Android Patch End
    }

    /* Verify and install the contents of the package.
     */
    ui->Print("Installing update...\n");
    ui->SetEnableReboot(false);
    int result = try_update_binary(path, &zip, wipe_cache);
    ui->SetEnableReboot(true);
    ui->Print("\n");

    sysReleaseMap(&map);

    return result;
}

int
install_package(const char* path, int* wipe_cache, const char* install_file,
                bool needs_mount) {
	// EosTek Patch Begin
#ifndef SCIFLY	
    FILE* install_log = fopen_path(install_file, "w");
    if (install_log) {
        fputs(path, install_log);
        fputc('\n', install_log);
    } else {
        LOGE("failed to open last_install: %s\n", strerror(errno));
    }
    int result;
    if (setup_install_mounts() != 0) {
        LOGE("failed to set up expected mounts for install; aborting\n");
        result = INSTALL_ERROR;
    } else {
        result = really_install_package(path, wipe_cache, needs_mount);
    }
    if (install_log) {
        fputc(result == INSTALL_SUCCESS ? '1' : '0', install_log);
        fputc('\n', install_log);
        fclose(install_log);
    }
    return result;
#else
	int result;
    if (setup_install_mounts() != 0) {
        LOGE("failed to set up expected mounts for install; aborting\n");
        result = INSTALL_ERROR;
    } else {
        result = really_install_package(path, wipe_cache, needs_mount);
    }
	
	FILE* install_log = fopen_path(install_file, "w");
    if (install_log) {
        printf("write last_install ...\n");
        fputc(result == INSTALL_SUCCESS ? '1' : '0', install_log);
        fputc(' ', install_log);
        fputs(path, install_log);
        fputc('\n', install_log);
        fclose(install_log);
    } else {
        printf("failed to open last_install: %s\n", strerror(errno));
    }
	
	return result;
#endif	
	// EosTek Patch End
}

// MStar Android Patch Begin
int
get_reload_env_enable() {
    int enable = reload_env_enable;
    return enable;
}

int
get_reload_panel_enable() {
    int enable = reload_panel_enable;
    return enable;
}
// MStar Android Patch End
