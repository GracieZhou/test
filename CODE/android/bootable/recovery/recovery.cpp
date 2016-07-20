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
#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <getopt.h>
#include <limits.h>
#include <linux/input.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/klog.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>
// MStar Android Patch Begin
#include <sys/mount.h>
// MStar Android Patch End

#include "bootloader.h"
#include "common.h"
#include "cutils/properties.h"
#include "cutils/android_reboot.h"
#include "install.h"
#include "minui/minui.h"
#include "minzip/DirUtil.h"
#include "roots.h"
#include "ui.h"
#include "screen_ui.h"
#include "device.h"
// MStar Android Patch Begin
#include "backup_restore.h"
#ifdef USE_LED
#include "drvGPIO.h"
#include "drvSYS.h"
#endif
#ifdef USE_ENV_UTILS
#include <mbootenv.h>
#endif
// MStar Android Patch End
#include "adb_install.h"
extern "C" {
#include "minadbd/adb.h"
#include "fuse_sideload.h"
#include "fuse_sdcard_provider.h"
}

// MStar Android Patch Begin
#define BOOT_STATUS_CUSTOMER_ACTIONS_STR    "customer-actions"
#define BOOT_STATUS_ACTION_RELOADENV_BIT    (char)(0x01 << 0)
#define BOOT_STATUS_ACTION_RELOADPANEL_BIT  (char)(0x01 << 1)
const char *dev_uuid = NULL;
const char *dev_label = NULL;
struct bootloader_message boot;
// MStar Android Patch End
struct selabel_handle *sehandle;

// MStar Android Patch Begin
static const struct option OPTIONS[] = {
  { "send_intent", required_argument, NULL, 's' },
  { "update_package", required_argument, NULL, 'u' },
  { "uuid", required_argument, NULL, 'd' },
  { "label", required_argument, NULL, 'a' },
  { "wipe_data", no_argument, NULL, 'w' },
  { "wipe_cache", no_argument, NULL, 'c' },
  { "show_text", no_argument, NULL, 't' },
  { "just_exit", no_argument, NULL, 'x' },
  { "locale", required_argument, NULL, 'l' },
  { "stages", required_argument, NULL, 'g' },
  { "shutdown_after", no_argument, NULL, 'p' },
  { "reason", required_argument, NULL, 'r' },
  { "backup_system", no_argument, NULL, 'b' },
  { "restore_system", no_argument, NULL, 'z' },
  { "wait_time", required_argument, NULL, 'q' },
#ifdef SCIFLY
  {	"customer_launcher", required_argument, NULL, 'L'},
#endif
  { NULL, 0, NULL, 0 },
};
// MStar Android Patch End

#define LAST_LOG_FILE "/cache/recovery/last_log"

static const char *CACHE_LOG_DIR = "/cache/recovery";
static const char *COMMAND_FILE = "/cache/recovery/command";
static const char *INTENT_FILE = "/cache/recovery/intent";
static const char *LOG_FILE = "/cache/recovery/log";
static const char *LAST_INSTALL_FILE = "/cache/recovery/last_install";
static const char *LOCALE_FILE = "/cache/recovery/last_locale";
static const char *CACHE_ROOT = "/cache";
// MStar Android Patch Begin
static const char *SDCARD_ROOT = "/mnt/sdcard";
// MStar Android Patch End
static const char *TEMPORARY_LOG_FILE = "/tmp/recovery.log";
static const char *TEMPORARY_INSTALL_FILE = "/tmp/last_install";
static const char *LAST_KMSG_FILE = "/cache/recovery/last_kmsg";
#define KLOG_DEFAULT_LEN (64 * 1024)

#define KEEP_LOG_COUNT 10

// Number of lines per page when displaying a file on screen
#define LINES_PER_PAGE 30
// MStar Android Patch Begin
static const char *SIDELOAD_TEMP_DIR = "/tmp/sideload";
// MStar Android Patch End
#define KEEP_LOG_COUNT 10

RecoveryUI* ui = NULL;
char* locale = NULL;
char recovery_version[PROPERTY_VALUE_MAX+1];
char* stage = NULL;
char* reason = NULL;

/*
 * The recovery tool communicates with the main system through /cache files.
 *   /cache/recovery/command - INPUT - command line for tool, one arg per line
 *   /cache/recovery/log - OUTPUT - combined log file from recovery run(s)
 *   /cache/recovery/intent - OUTPUT - intent that was passed in
 *
 * The arguments which may be supplied in the recovery.command file:
 *   --send_intent=anystring - write the text out to recovery.intent
 *   --update_package=path - verify install an OTA package file
 *   --wipe_data - erase user data (and cache), then reboot
 *   --wipe_cache - wipe cache (but not user data), then reboot
 *   --set_encrypted_filesystem=on|off - enables / diasables encrypted fs
 *   --just_exit - do nothing; exit and reboot
 *
 * After completing, we remove /cache/recovery/command and reboot.
 * Arguments may also be supplied in the bootloader control block (BCB).
 * These important scenarios must be safely restartable at any point:
 *
 * FACTORY RESET
 * 1. user selects "factory reset"
 * 2. main system writes "--wipe_data" to /cache/recovery/command
 * 3. main system reboots into recovery
 * 4. get_args() writes BCB with "boot-recovery" and "--wipe_data"
 *    -- after this, rebooting will restart the erase --
 * 5. erase_volume() reformats /data
 * 6. erase_volume() reformats /cache
 * 7. finish_recovery() erases BCB
 *    -- after this, rebooting will restart the main system --
 * 8. main() calls reboot() to boot main system
 *
 * OTA INSTALL
 * 1. main system downloads OTA package to /cache/some-filename.zip
 * 2. main system writes "--update_package=/cache/some-filename.zip"
 * 3. main system reboots into recovery
 * 4. get_args() writes BCB with "boot-recovery" and "--update_package=..."
 *    -- after this, rebooting will attempt to reinstall the update --
 * 5. install_package() attempts to install the update
 *    NOTE: the package install must itself be restartable from any point
 * 6. finish_recovery() erases BCB
 *    -- after this, rebooting will (try to) restart the main system --
 * 7. ** if install failed **
 *    7a. prompt_and_wait() shows an error icon and waits for the user
 *    7b; the user reboots (pulling the battery, etc) into the main system
 * 8. main() calls maybe_install_firmware_update()
 *    ** if the update contained radio/hboot firmware **:
 *    8a. m_i_f_u() writes BCB with "boot-recovery" and "--wipe_cache"
 *        -- after this, rebooting will reformat cache & restart main system --
 *    8b. m_i_f_u() writes firmware image into raw cache partition
 *    8c. m_i_f_u() writes BCB with "update-radio/hboot" and "--wipe_cache"
 *        -- after this, rebooting will attempt to reinstall firmware --
 *    8d. bootloader tries to flash firmware
 *    8e. bootloader writes BCB with "boot-recovery" (keeping "--wipe_cache")
 *        -- after this, rebooting will reformat cache & restart main system --
 *    8f. erase_volume() reformats /cache
 *    8g. finish_recovery() erases BCB
 *        -- after this, rebooting will (try to) restart the main system --
 * 9. main() calls reboot() to boot main system
 */

static const int MAX_ARG_LENGTH = 4096;
static const int MAX_ARGS = 100;

// open a given path, mounting partitions as necessary
FILE*
fopen_path(const char *path, const char *mode) {
    if (ensure_path_mounted(path) != 0) {
        LOGE("Can't mount %s\n", path);
        return NULL;
    }

    // When writing, try to create the containing directory, if necessary.
    // Use generous permissions, the system (init.rc) will reset them.
    if (strchr("wa", mode[0])) dirCreateHierarchy(path, 0777, NULL, 1, sehandle);

    FILE *fp = fopen(path, mode);
    return fp;
}

static void redirect_stdio(const char* filename) {
    // If these fail, there's not really anywhere to complain...
    freopen(filename, "a", stdout); setbuf(stdout, NULL);
    freopen(filename, "a", stderr); setbuf(stderr, NULL);
}

// close a file, log an error if the error indicator is set
static void
check_and_fclose(FILE *fp, const char *name) {
    fflush(fp);
    if (ferror(fp)) LOGE("Error in %s\n(%s)\n", name, strerror(errno));
    fclose(fp);
}

// MStar Android Patch Begin
// Return the path of TEMPORARY_INSTALL_FILE
const char*
get_temporary_install_file_path() {
    return TEMPORARY_INSTALL_FILE;
}

// write BCB
static void
write_bootloader_message(int enable) {
    struct bootloader_message boot_info;

    memset(&boot_info, 0, sizeof(boot_info));

    if (enable == 1) {
        strlcpy(boot_info.command, "boot-recovery", sizeof(boot_info.command));
        strlcpy(boot_info.recovery, "recovery\n", sizeof(boot_info.recovery));
    }

    set_bootloader_message(&boot_info);
}

static void write_bootloader_env_flag(char enable_mode) {
    memset(&boot, 0, sizeof(boot));
    strlcpy(boot.command, BOOT_STATUS_CUSTOMER_ACTIONS_STR, sizeof(boot.command));
    boot.status[0] = enable_mode;
    set_bootloader_message(&boot);
}
// MStar Android Patch End

// command line args come from, in decreasing precedence:
//   - the actual command line
//   - the bootloader control block (one per line, after "recovery")
//   - the contents of COMMAND_FILE (one per line)
static void
get_args(int *argc, char ***argv) {
    // MStar Android Patch Begin
    memset(&boot, 0, sizeof(boot));
    get_bootloader_message(&boot);  // this may fail, leaving a zeroed structure
    // MStar Android Patch End
    stage = strndup(boot.stage, sizeof(boot.stage));

    if (boot.command[0] != 0 && boot.command[0] != 255) {
        LOGI("Boot command: %.*s\n", (int)sizeof(boot.command), boot.command);
    }

    if (boot.status[0] != 0 && boot.status[0] != 255) {
        LOGI("Boot status: %.*s\n", (int)sizeof(boot.status), boot.status);
    }

    // --- if arguments weren't supplied, look in the bootloader control block
    if (*argc <= 1) {
        boot.recovery[sizeof(boot.recovery) - 1] = '\0';  // Ensure termination
        const char *arg = strtok(boot.recovery, "\n");
        if (arg != NULL && !strcmp(arg, "recovery")) {
            *argv = (char **) malloc(sizeof(char *) * MAX_ARGS);
            (*argv)[0] = strdup(arg);
            for (*argc = 1; *argc < MAX_ARGS; ++*argc) {
                if ((arg = strtok(NULL, "\n")) == NULL) break;
                (*argv)[*argc] = strdup(arg);
            }
            LOGI("Got arguments from boot message\n");
        } else if (boot.recovery[0] != 0 && boot.recovery[0] != 255) {
            LOGE("Bad boot message\n\"%.20s\"\n", boot.recovery);
        }
    }

    // --- if that doesn't work, try the command file
    if (*argc <= 1) {
        FILE *fp = fopen_path(COMMAND_FILE, "r");
        if (fp != NULL) {
            char *token;
            char *argv0 = (*argv)[0];
            *argv = (char **) malloc(sizeof(char *) * MAX_ARGS);
            (*argv)[0] = argv0;  // use the same program name

            char buf[MAX_ARG_LENGTH];
            for (*argc = 1; *argc < MAX_ARGS; ++*argc) {
                if (!fgets(buf, sizeof(buf), fp)) break;
                token = strtok(buf, "\r\n");
                if (token != NULL) {
                    (*argv)[*argc] = strdup(token);  // Strip newline.
                } else {
                    --*argc;
                }
            }

            check_and_fclose(fp, COMMAND_FILE);
            LOGI("Got arguments from %s\n", COMMAND_FILE);
        }
    }

    // --> write the arguments we have back into the bootloader control block
    // always boot into recovery after this (until finish_recovery() is called)
    strlcpy(boot.command, "boot-recovery", sizeof(boot.command));
    strlcpy(boot.recovery, "recovery\n", sizeof(boot.recovery));
    int i;
    for (i = 1; i < *argc; ++i) {
        strlcat(boot.recovery, (*argv)[i], sizeof(boot.recovery));
        strlcat(boot.recovery, "\n", sizeof(boot.recovery));
    }
    // MStar Android Patch Begin
    boot.status[1] += 1;
    printf("boot.status[1]:%d\n", boot.status[1]);
    if (1 == boot.status[1])
        write_bootloader_message(0);
    // MStar Android Patch End
}

// MStar Android Patch Begin
void
set_sdcard_update_bootloader_message() {
    memset(&boot, 0, sizeof(boot));
    strlcpy(boot.command, "boot-recovery", sizeof(boot.command));
    strlcpy(boot.recovery, "recovery\n", sizeof(boot.recovery));
    set_bootloader_message(&boot);
}
// MStar Android Patch End

// read from kernel log into buffer and write out to file
static void
save_kernel_log(const char *destination) {
    int n;
    char *buffer;
    int klog_buf_len;
    FILE *log;

    klog_buf_len = klogctl(KLOG_SIZE_BUFFER, 0, 0);
    if (klog_buf_len <= 0) {
        LOGE("Error getting klog size (%s), using default\n", strerror(errno));
        klog_buf_len = KLOG_DEFAULT_LEN;
    }

    buffer = (char *)malloc(klog_buf_len);
    if (!buffer) {
        LOGE("Can't alloc %d bytes for klog buffer\n", klog_buf_len);
        return;
    }

    n = klogctl(KLOG_READ_ALL, buffer, klog_buf_len);
    if (n < 0) {
        LOGE("Error in reading klog (%s)\n", strerror(errno));
        free(buffer);
        return;
    }

    log = fopen_path(destination, "w");
    if (log == NULL) {
        LOGE("Can't open %s\n", destination);
        free(buffer);
        return;
    }
    fwrite(buffer, n, 1, log);
    check_and_fclose(log, destination);
    free(buffer);
}

// How much of the temp log we have copied to the copy in cache.
static long tmplog_offset = 0;

static void
copy_log_file(const char* source, const char* destination, int append) {
    FILE *log = fopen_path(destination, append ? "a" : "w");
    if (log == NULL) {
        LOGE("Can't open %s\n", destination);
    } else {
        FILE *tmplog = fopen(source, "r");
        if (tmplog != NULL) {
            if (append) {
                fseek(tmplog, tmplog_offset, SEEK_SET);  // Since last write
            }
            char buf[4096];
            while (fgets(buf, sizeof(buf), tmplog)) fputs(buf, log);
            if (append) {
                tmplog_offset = ftell(tmplog);
            }
            check_and_fclose(tmplog, source);
        }
        check_and_fclose(log, destination);
    }
}

// Rename last_log -> last_log.1 -> last_log.2 -> ... -> last_log.$max
// Overwrites any existing last_log.$max.
static void
rotate_last_logs(int max) {
    char oldfn[256];
    char newfn[256];

    int i;
    for (i = max-1; i >= 0; --i) {
        snprintf(oldfn, sizeof(oldfn), (i==0) ? LAST_LOG_FILE : (LAST_LOG_FILE ".%d"), i);
        snprintf(newfn, sizeof(newfn), LAST_LOG_FILE ".%d", i+1);
        // ignore errors
        rename(oldfn, newfn);
    }
}

static void
copy_logs() {
    // Copy logs to cache so the system can find out what happened.
    copy_log_file(TEMPORARY_LOG_FILE, LOG_FILE, true);
    copy_log_file(TEMPORARY_LOG_FILE, LAST_LOG_FILE, false);
    copy_log_file(TEMPORARY_INSTALL_FILE, LAST_INSTALL_FILE, false);
    save_kernel_log(LAST_KMSG_FILE);
    chmod(LOG_FILE, 0600);
    chown(LOG_FILE, 1000, 1000);   // system user
    chmod(LAST_KMSG_FILE, 0600);
    chown(LAST_KMSG_FILE, 1000, 1000);   // system user
    chmod(LAST_LOG_FILE, 0640);
    // EosTek Patch Begin
#ifdef SCIFLY
    chmod(LAST_INSTALL_FILE, 0666);
#else
    chmod(LAST_INSTALL_FILE, 0644);
#endif
    // EosTek Patch End
    sync();
}

// clear the recovery command and prepare to boot a (hopefully working) system,
// copy our log file to cache as well (for the system to read), and
// record any intent we were asked to communicate back to the system.
// this function is idempotent: call it as many times as you like.
static void
finish_recovery(const char *send_intent) {
    // By this point, we're ready to return to the main system...
    if (send_intent != NULL) {
        FILE *fp = fopen_path(INTENT_FILE, "w");
        if (fp == NULL) {
            LOGE("Can't open %s\n", INTENT_FILE);
        } else {
            fputs(send_intent, fp);
            check_and_fclose(fp, INTENT_FILE);
        }
    }

    // Save the locale to cache, so if recovery is next started up
    // without a --locale argument (eg, directly from the bootloader)
    // it will use the last-known locale.
    if (locale != NULL) {
        LOGI("Saving locale \"%s\"\n", locale);
        FILE* fp = fopen_path(LOCALE_FILE, "w");
        fwrite(locale, 1, strlen(locale), fp);
        fflush(fp);
        fsync(fileno(fp));
        check_and_fclose(fp, LOCALE_FILE);
    }

    copy_logs();

    // Reset to normal system boot so recovery won't cycle indefinitely.
    // MStar Android Patch Begin
    memset(&boot, 0, sizeof(boot));
    // MStar Android Patch End
    set_bootloader_message(&boot);

    // Remove the command file, so recovery won't repeat indefinitely.
    if (ensure_path_mounted(COMMAND_FILE) != 0 ||
        (unlink(COMMAND_FILE) && errno != ENOENT)) {
        LOGW("Can't unlink %s\n", COMMAND_FILE);
    }

    ensure_path_unmounted(CACHE_ROOT);
    sync();  // For good measure.
}

typedef struct _saved_log_file {
    char* name;
    struct stat st;
    unsigned char* data;
    struct _saved_log_file* next;
} saved_log_file;

static int
erase_volume(const char *volume) {
    bool is_cache = (strcmp(volume, CACHE_ROOT) == 0);
	// EosTek Patch Begin
#ifndef SCIFLY
    // MStar Android Patch Begin
    ui->SetBackground(RecoveryUI::ERASING);
    ui->SetProgressType(RecoveryUI::INDETERMINATE);
    if (strcmp(volume, "/cache") == 0) {
        ui->SetTipTitle(RecoveryUI::TIP_TITLE_WIPE_CACHE);
    } else if (strcmp(volume, "/data") == 0) {
        ui->SetTipTitle(RecoveryUI::TIP_TITLE_WIPE_DATA);
    }
    // MStar Android Patch End
#endif
	// EosTek Patch End
    saved_log_file* head = NULL;

    if (is_cache) {
        // If we're reformatting /cache, we load any
        // "/cache/recovery/last*" files into memory, so we can restore
        // them after the reformat.

        ensure_path_mounted(volume);

        DIR* d;
        struct dirent* de;
        d = opendir(CACHE_LOG_DIR);
        if (d) {
            char path[PATH_MAX];
            strcpy(path, CACHE_LOG_DIR);
            strcat(path, "/");
            int path_len = strlen(path);
            while ((de = readdir(d)) != NULL) {
                if (strncmp(de->d_name, "last", 4) == 0) {
                    saved_log_file* p = (saved_log_file*) malloc(sizeof(saved_log_file));
                    strcpy(path+path_len, de->d_name);
                    p->name = strdup(path);
                    if (stat(path, &(p->st)) == 0) {
                        // truncate files to 512kb
                        if (p->st.st_size > (1 << 19)) {
                            p->st.st_size = 1 << 19;
                        }
                        p->data = (unsigned char*) malloc(p->st.st_size);
                        FILE* f = fopen(path, "rb");
                        fread(p->data, 1, p->st.st_size, f);
                        fclose(f);
                        p->next = head;
                        head = p;
                    } else {
                        free(p);
                    }
                }
            }
            closedir(d);
        } else {
            if (errno != ENOENT) {
                printf("opendir failed: %s\n", strerror(errno));
            }
        }
    }

    ui->Print("Formatting %s...\n", volume);

    ensure_path_unmounted(volume);
    int result = format_volume(volume);

    if (is_cache) {
        while (head) {
            FILE* f = fopen_path(head->name, "wb");
            if (f) {
                fwrite(head->data, 1, head->st.st_size, f);
                fclose(f);
                chmod(head->name, head->st.st_mode);
                chown(head->name, head->st.st_uid, head->st.st_gid);
            }
            free(head->name);
            free(head->data);
            saved_log_file* temp = head->next;
            free(head);
            head = temp;
        }

        // Any part of the log we'd copied to cache is now gone.
        // Reset the pointer so we copy from the beginning of the temp
        // log.
        tmplog_offset = 0;
        copy_logs();
    }

    return result;
}

// MStar Android Patch Begin
char*
copy_sideloaded_package(const char* original_path) {
    if (ensure_path_mounted(original_path) != 0) {
        LOGE("Can't mount %s\n", original_path);
        return NULL;
    }

    if (ensure_path_mounted(SIDELOAD_TEMP_DIR) != 0) {
        LOGE("Can't mount %s\n", SIDELOAD_TEMP_DIR);
        return NULL;
    }

    if (mkdir(SIDELOAD_TEMP_DIR, 0700) != 0) {
        if (errno != EEXIST) {
            LOGE("Can't mkdir %s (%s)\n", SIDELOAD_TEMP_DIR, strerror(errno));
            return NULL;
        }
    }

    // verify that SIDELOAD_TEMP_DIR is exactly what we expect: a
    // directory, owned by root, readable and writable only by root.
    struct stat st;
    if (stat(SIDELOAD_TEMP_DIR, &st) != 0) {
        LOGE("failed to stat %s (%s)\n", SIDELOAD_TEMP_DIR, strerror(errno));
        return NULL;
    }
    if (!S_ISDIR(st.st_mode)) {
        LOGE("%s isn't a directory\n", SIDELOAD_TEMP_DIR);
        return NULL;
    }
    if ((st.st_mode & 0777) != 0700) {
        LOGE("%s has perms %o\n", SIDELOAD_TEMP_DIR, st.st_mode);
        return NULL;
    }
    if (st.st_uid != 0) {
        LOGE("%s owned by %lu; not root\n", SIDELOAD_TEMP_DIR, st.st_uid);
        return NULL;
    }

    char copy_path[PATH_MAX];
    strcpy(copy_path, SIDELOAD_TEMP_DIR);
    strcat(copy_path, "/package.zip");

    char* buffer = (char*)malloc(BUFSIZ);
    if (buffer == NULL) {
        LOGE("Failed to allocate buffer\n");
        return NULL;
    }

    size_t read;
    FILE* fin = fopen(original_path, "rb");
    if (fin == NULL) {
        LOGE("Failed to open %s (%s)\n", original_path, strerror(errno));
        return NULL;
    }
    FILE* fout = fopen(copy_path, "wb");
    if (fout == NULL) {
        LOGE("Failed to open %s (%s)\n", copy_path, strerror(errno));
        return NULL;
    }

    while ((read = fread(buffer, 1, BUFSIZ, fin)) > 0) {
        if (fwrite(buffer, 1, read, fout) != read) {
            LOGE("Short write of %s (%s)\n", copy_path, strerror(errno));
            return NULL;
        }
    }

    free(buffer);

    if (fclose(fout) != 0) {
        LOGE("Failed to close %s (%s)\n", copy_path, strerror(errno));
        return NULL;
    }

    if (fclose(fin) != 0) {
        LOGE("Failed to close %s (%s)\n", original_path, strerror(errno));
        return NULL;
    }

    // "adb push" is happy to overwrite read-only files when it's
    // running as root, but we'll try anyway.
    if (chmod(copy_path, 0400) != 0) {
        LOGE("Failed to chmod %s (%s)\n", copy_path, strerror(errno));
        return NULL;
    }

    return strdup(copy_path);
}
// MStar Android Patch End

// MStar Android Patch Begin
const char**
prepend_title(const char* const* headers) {
// MStar Android Patch End
    // count the number of lines in our title, plus the
    // caller-provided headers.
    int count = 3;   // our title has 3 lines
    const char* const* p;
    for (p = headers; *p; ++p, ++count);

    const char** new_headers = (const char**)malloc((count+1) * sizeof(char*));
    const char** h = new_headers;
    *(h++) = "Android system recovery <" EXPAND(RECOVERY_API_VERSION) "e>";
    *(h++) = recovery_version;
    *(h++) = "";
    for (p = headers; *p; ++p, ++h) *h = *p;
    *h = NULL;

    return new_headers;
}

// MStar Android Patch Begin
int
get_menu_selection(const char* const * headers, const char* const * items,
                   int menu_only, int initial_selection, Device* device) {
// MStar Android Patch End
    // throw away keys pressed previously, so user doesn't
    // accidentally trigger menu items.
    ui->FlushKeys();

    ui->StartMenu(headers, items, initial_selection);
    int selected = initial_selection;
    int chosen_item = -1;

    while (chosen_item < 0) {
        int key = ui->WaitKey();
        int visible = ui->IsTextVisible();

        if (key == -1) {   // ui_wait_key() timed out
            if (ui->WasTextEverVisible()) {
                continue;
            } else {
                LOGI("timed out waiting for key input; rebooting.\n");
                ui->EndMenu();
                return 0; // XXX fixme
            }
        }

        int action = device->HandleMenuKey(key, visible);

        if (action < 0) {
            switch (action) {
                case Device::kHighlightUp:
                    --selected;
                    selected = ui->SelectMenu(selected);
                    break;
                case Device::kHighlightDown:
                    ++selected;
                    selected = ui->SelectMenu(selected);
                    break;
                case Device::kInvokeItem:
                    chosen_item = selected;
                    break;
                // MStar Android Patch Begin
                case Device::kReboot:
                    chosen_item = 2580;
                    break;
                // MStar Android Patch End
                case Device::kNoAction:
                    break;
            }
        } else if (!menu_only) {
            chosen_item = action;
        }
    }

    ui->EndMenu();
    return chosen_item;
}

// MStar Android Patch Begin
int compare_string(const void* a, const void* b) {
    return strcmp(*(const char**)a, *(const char**)b);
}

static void set_bootloader_message_EXTupdate(char *path) {
    char tmp_path[128];
    char tmp_uuid[128];
    char tmp_label[128];
    memset(tmp_path, 0, 128);
    memset(tmp_label, 0, 128);
    memset(tmp_uuid, 0, 128);

    strcpy(tmp_path, "--update_package=");
    strcat(tmp_path, path);
    if (0 == strncmp(path, "/mnt/", 5)) {
        strcpy(tmp_uuid, "--uuid=");
        strcat(tmp_uuid, dev_uuid);

        strcpy(tmp_label, "--label=");
        strcat(tmp_label, dev_label);
    }
    memset(&boot, 0, sizeof(boot));
    strlcpy(boot.command, "boot-recovery", sizeof(boot.command));
    strcpy(boot.recovery, "recovery\n");
    strcat(boot.recovery, tmp_path);
    strcat(boot.recovery, "\n");
    if (0 == strncmp(path, "/mnt/", 5)) {
        strcat(boot.recovery, tmp_uuid);
        strcat(boot.recovery, "\n");
        strcat(boot.recovery, tmp_label);
        strcat(boot.recovery, "\n");
    }
    boot.status[1] += 1;
    set_bootloader_message(&boot);

}
// MStar Android Patch End

// MStar Android Patch Begin
static int
browse_directory(const char* path, const char* unmount_when_done,
                 int* wipe_cache, Device* device) {
// MStar Android Patch End
    const char* MENU_HEADERS[] = { "Choose a package to install:",
                                   path,
                                   "",
                                   NULL };
    DIR* d;
    struct dirent* de;
    d = opendir(path);
    if (d == NULL) {
        LOGE("error opening %s: %s\n", path, strerror(errno));
        if (unmount_when_done != NULL) {
            ensure_path_unmounted(unmount_when_done);
        }
        return 0;
    }

    const char** headers = prepend_title(MENU_HEADERS);

    int d_size = 0;
    int d_alloc = 10;
    char** dirs = (char**)malloc(d_alloc * sizeof(char*));
    int z_size = 1;
    int z_alloc = 10;
    char** zips = (char**)malloc(z_alloc * sizeof(char*));
    zips[0] = strdup("../");

    while ((de = readdir(d)) != NULL) {
        int name_len = strlen(de->d_name);

        if (de->d_type == DT_DIR) {
            // skip "." and ".." entries
            if (name_len == 1 && de->d_name[0] == '.') continue;
            if (name_len == 2 && de->d_name[0] == '.' &&
                de->d_name[1] == '.') continue;

            if (d_size >= d_alloc) {
                d_alloc *= 2;
                dirs = (char**)realloc(dirs, d_alloc * sizeof(char*));
            }
            dirs[d_size] = (char*)malloc(name_len + 2);
            strcpy(dirs[d_size], de->d_name);
            dirs[d_size][name_len] = '/';
            dirs[d_size][name_len+1] = '\0';
            ++d_size;
        } else if (de->d_type == DT_REG &&
                   name_len >= 4 &&
                   strncasecmp(de->d_name + (name_len-4), ".zip", 4) == 0) {
            if (z_size >= z_alloc) {
                z_alloc *= 2;
                zips = (char**)realloc(zips, z_alloc * sizeof(char*));
            }
            zips[z_size++] = strdup(de->d_name);
        }
    }
    closedir(d);

    qsort(dirs, d_size, sizeof(char*), compare_string);
    qsort(zips, z_size, sizeof(char*), compare_string);

    // append dirs to the zips list
    if (d_size + z_size + 1 > z_alloc) {
        z_alloc = d_size + z_size + 1;
        zips = (char**)realloc(zips, z_alloc * sizeof(char*));
    }
    memcpy(zips + z_size, dirs, d_size * sizeof(char*));
    free(dirs);
    z_size += d_size;
    zips[z_size] = NULL;

    // MStar Android Patch Begin
    int result;
    int chosen_item = 0;
    do {
        chosen_item = get_menu_selection(headers, zips, 1, chosen_item, device);

        char* item = zips[chosen_item];
        int item_len = strlen(item);
        if (chosen_item == 0) {          // item 0 is always "../"
            // go up but continue browsing (if the caller is browse_directory)
            result = -1;
            break;
        } else if (item[item_len-1] == '/') {
            // recurse down into a subdirectory
            char new_path[PATH_MAX];
            strlcpy(new_path, path, PATH_MAX);
            strlcat(new_path, "/", PATH_MAX);
            strlcat(new_path, item, PATH_MAX);
            new_path[strlen(new_path)-1] = '\0';  // truncate the trailing '/'
            result = browse_directory(new_path, unmount_when_done, wipe_cache, device);
            if (result >= 0) break;
        } else {
            // selected a zip file:  attempt to install it, and return
            // the status to the caller.
            char new_path[PATH_MAX];
            strlcpy(new_path, path, PATH_MAX);
            strlcat(new_path, "/", PATH_MAX);
            strlcat(new_path, item, PATH_MAX);

            ui->Print("\n-- Install %s ...\n", path);
            if (unmount_when_done != NULL) {
                ensure_path_unmounted(unmount_when_done);
            }
            set_bootloader_message_EXTupdate(new_path);
            result = install_package(new_path, wipe_cache, TEMPORARY_INSTALL_FILE, true);
            if (INSTALL_SUCCESS == result) {
                memset(&boot, 0, sizeof(boot));
                set_bootloader_message(&boot);
            }
            break;
        }
    } while (true);
    // MStar Android Patch End

    int i;
    for (i = 0; i < z_size; ++i) free(zips[i]);
    free(zips);
    free(headers);

    // MStar Android Patch Begin
    if (unmount_when_done != NULL) {
        ensure_path_unmounted(unmount_when_done);
    }
    // MStar Android Patch End
    return result;
}

// MStar Android Patch Begin
static int
storage_update(int* wipe_cache, Device* device) {
    char dev_string[128][20];
    char uuid[128][20];
    char tmp_label[128];
    char tmp_uuid[128];
    char tmp_dev[128];
    DIR *dirp;
    struct dirent *dp;
    int ret = -1;
    int dev_num = 0;
    int d_alloc = 5;

    char **label = (char**)malloc(d_alloc * sizeof(char*));
    if (NULL == label) {
        LOGE("malloc error\n");
        return ret;
    }
    const char* MENU_HEADERS[] = { "Choose a package to install:",
                                   "external storage",
                                   "",
                                   NULL };
    const char** headers = prepend_title(MENU_HEADERS);
    memset(dev_string, 0, sizeof(dev_string));
    memset(uuid, 0, sizeof(uuid));
    strcpy(dev_string[dev_num], "../");
    label[dev_num] = strdup("../");
    strcpy(uuid[dev_num], dev_string[dev_num]);
    dev_num++;
    dirp = opendir("/dev/block");
    if (NULL == dirp) {
        LOGE("Can't open /dev/block\n");
        free(label);
        return ret;
    }
    char* p = (char*)malloc(128);
    if (NULL == p) {
        LOGE("malloc error\n");
        free(label);
        return ret;
    }

    do {
        if (NULL == (dp = readdir(dirp))) {
            break;
        }
        memset(p, 0, 128);
        strncpy(p, "/dev/block/", strlen("/dev/block/"));
        strcat(p, dp->d_name);
        memset(tmp_label, 0, 128);
        memset(tmp_uuid, 0, 128);
        memset(tmp_dev, 0, 128);

        if (0 == get_volume_info(p, tmp_label, tmp_uuid, tmp_dev)) {
            if (dev_num >= d_alloc) {
                d_alloc *= 2;
                label = (char**)realloc(label, d_alloc * sizeof(char*));
            }
            // dev
            strcpy(dev_string[dev_num], tmp_dev);
            printf("dev_string[%d]:%s\n", dev_num, dev_string[dev_num]);

            // label
            label[dev_num] = strdup(tmp_label);
            // uuid
            strcpy(uuid[dev_num], tmp_uuid);

            dev_num++;
        }
        ret = 0;
    } while(1);
    free(p);
    closedir(dirp);
    label[dev_num] = NULL;

    // display the label to panel
    int chosen_item = 0;
    do {
        chosen_item = get_menu_selection(headers, label, 1, chosen_item, device);

        char* item = dev_string[chosen_item];
        printf("item:%s\n", item);
        int item_len = strlen(item);
        if (chosen_item == 0) {          // item 0 is always "../"
            // go up but continue browsing (if the caller is browse_directory)
            ret = -1;
            break;
        } else{
            // recurse down into a subdirectory
            char new_path[PATH_MAX];
            char dev[128];
            memset(dev, 0, 128);
            strcpy(dev, "/dev/block/");
            strlcpy(new_path, "/mnt/usb/sda1", PATH_MAX);
            strcat(dev, item);
            LOGI("devname:%s\n", dev);
            dev_uuid = uuid[chosen_item];
            dev_label = label[chosen_item];
            umount("/mnt/usb/sda1");
            if (0 != mount(dev, new_path, "vfat", MS_NOATIME|MS_NODEV|MS_NODIRATIME, "")) {
                LOGE("can't mount %s on %s\n", dev, new_path);
                // package in Mobile hard disk possible
                printf("--trying mount again--\n");
                unsigned long flags = MS_NODEV | MS_NOEXEC | MS_NOSUID | MS_DIRSYNC;
                if (0 != mount(dev, new_path, "ntfs", flags, "")) {
                    LOGE("can't mount %s on %s\n", dev, new_path);
                    ret = -1;
                    break;
                }
            }
            new_path[strlen(new_path)] = '\0';  // truncate the trailing '/'
            ret = browse_directory(new_path, NULL, wipe_cache, device);
            if (ret >= 0) break;
        }
    } while (true);

    for(int i = 0;i < dev_num; i++) free(label[i]);
    free(label);
    free(headers);
    return ret;
}

static int
reset_tvdatabase() {
    // Recovery mode have the lowest of permissions to restore the factory settings.
    // Recovery mode only allow to fix customer's configuration items,the configuration items in user_setting.db.
    // Recovery mode not allow to fix  non-customer's configuration items,the configuration items in factory.db.
    // check TvBackup
    if (access("/tvconfig/TvBackup", F_OK) != 0) {
        LOGE("error, no TvBackup in tvconfig\n");
        return -1;
    }
    //format_volume("/tvdatabase");
    ensure_path_mounted("/tvdatabase");
    //mkdir("/tvdatabase/Database", 755);
    mstar_system("busybox cp -r /tvconfig/TvBackup/Database/user_setting.db /tvdatabase/Database");
    mstar_system("busybox cp -r /tvconfig/TvBackup/Database/user_setting.db-journal /tvdatabase/Database");
    mstar_system("busybox cp -r /tvconfig/TvBackup/Database/customer.db /tvdatabase/Database");
    mstar_system("busybox cp -r /tvconfig/TvBackup/Database/customer.db-journal /tvdatabase/Database");
    //mstar_system("busybox cp -r /tvconfig/Database/factory.db /tvdatabase/Database");
    //mstar_system("busybox cp -r /tvconfig/Database/factory.db-journal /tvdatabase/Database");
	
	// EosTek Patch Begin
	// wait fix: reset atv、dtv data
	mstar_system("busybox cp -r /tvconfig/TvBackup/Database/atv_cmdb.bin /tvdatabase/Database");
	mstar_system("busybox cp -r /tvconfig/TvBackup/Database/dtv_cmdb_0.bin /tvdatabase/Database");
	mstar_system("busybox cp -r /tvconfig/TvBackup/Database/factory.bin /tvdatabase/Database");
	mstar_system("busybox cp -r /tvconfig/TvBackup/Database/user_setting.bin /tvdatabase/Database");
	// EosTek Patch End
	
    mstar_system("busybox chmod -R 755 /tvdatabase/*");
    mstar_system("busybox chmod -R 644 /tvdatabase/Database/*");
    // change owner to system permission
    mstar_system("busybox chown -R 1000:1000 /tvdatabase/Database/*");
    ensure_path_unmounted("/tvdatabase");
    return 0;
}

#ifdef USE_ENV_UTILS
static int
reset_reproducerate() {
    char *rate;
    char *tmp;
    int len = 0;
    if (-1 == mbootenv_init()) {
        LOGE("init env in emmc failed!\n");
        return -1;
    }

   const char* p_bootargs = mbootenv_get("bootargs");
    if (NULL == p_bootargs) {
        LOGE("get bootargs failed!\n");
        return -1;
    }

    char *bootargs = (char*)malloc(strlen(p_bootargs) + 1);
    strcpy(bootargs, p_bootargs);
    LOGI("bootargs:%s\n", bootargs);
    if (NULL == (rate = strstr(bootargs, "rate"))) {
        LOGE("there is no rate in bootargs!\n");
        return 0;
    }

    while (NULL != rate) {
        tmp = strchr(rate, '=');
        len = tmp - rate;
        if (0 == memcmp(rate, "rate2160", len)) {
            if (-1 == mbootenv_set("reproducerate2160","")) {
                goto error;
            }
            if (-1 == mbootenv_set2bootargs("rate2160=","")) {
                goto error;
            }
        } else if (0 == memcmp(rate, "rate1080", len)) {
            if (-1 == mbootenv_set("reproducerate1080","")) {
                goto error;
            }
            if (-1 == mbootenv_set2bootargs("rate1080=","")) {
                goto error;
            }
        } else if (0 == memcmp(rate, "rate720", len)) {
            if (-1 == mbootenv_set("reproducerate720","")) {
                goto error;
            }
            if (-1 == mbootenv_set2bootargs("rate720=","")) {
                goto error;
            }
        } else if (0 == memcmp(rate, "rate576", len)) {
            if (-1 == mbootenv_set("reproducerate576","")) {
                goto error;
            }
            if (-1 == mbootenv_set2bootargs("rate576=","")) {
                goto error;
            }
        } else if (0 == memcmp(rate, "rate480", len)) {
            if (-1 == mbootenv_set("reproducerate480","")) {
                goto error;
            }
            if (-1 == mbootenv_set2bootargs("rate480=","")) {
                goto error;
            }
        }
        if (-1 == mbootenv_set2bootargs("reproducerate=", "")) {
            goto error;
        }
        if (-1 == mbootenv_set("resolution_reset", "4")) {
            goto error;
        }
        rate = strstr(tmp, "rate");
    }
    mbootenv_save();
    if (bootargs) {
        free(bootargs);
    }
    return 0;
error:
    if (bootargs) {
        free(bootargs);
    }
    return -1;
}
#endif
// MStar Android Patch End

static void
wipe_data(int confirm, Device* device) {
    if (confirm) {
        static const char** title_headers = NULL;

        if (title_headers == NULL) {
            const char* headers[] = { "Confirm wipe of all user data?",
                                      "  THIS CAN NOT BE UNDONE.",
                                      "",
                                      NULL };
            title_headers = prepend_title((const char**)headers);
        }

        const char* items[] = { " No",
                                " No",
                                " No",
                                " No",
                                " No",
                                " No",
                                " No",
                                " Yes -- delete all user data",   // [7]
                                " No",
                                " No",
                                " No",
                                NULL };

        int chosen_item = get_menu_selection(title_headers, items, 1, 0, device);
        if (chosen_item != 7) {
            return;
        }
    }

    ui->Print("\n-- Wiping data...\n");
    // MStar Android Patch Begin
    if (device->WipeData() && erase_volume("/data") && erase_volume("/cache")) {
        ui->Print("[ERROR1] Data wipe failed.\n");
    }

    if (reset_tvdatabase() != 0) {
        ui->Print("[ERROR2] reset factory db failed.\n");
    }
#ifdef USE_ENV_UTILS
    if (reset_reproducerate() != 0) {
        ui->Print("[ERROR3] reset reproducerate failed.\n");
    }
#endif
    // MStar Android Patch End
    ui->Print("Data wipe complete.\n");
}

static void file_to_ui(const char* fn) {
    FILE *fp = fopen_path(fn, "re");
    if (fp == NULL) {
        ui->Print("  Unable to open %s: %s\n", fn, strerror(errno));
        return;
    }
    char line[1024];
    int ct = 0;
    int key = 0;
    redirect_stdio("/dev/null");
    while(fgets(line, sizeof(line), fp) != NULL) {
        ui->Print("%s", line);
        ct++;
        if (ct % LINES_PER_PAGE == 0) {
            // give the user time to glance at the entries
            key = ui->WaitKey();

            if (key == KEY_POWER) {
                break;
            }

            if (key == KEY_VOLUMEUP) {
                // Go back by seeking to the beginning and dumping ct - n
                // lines.  It's ugly, but this way we don't need to store
                // the previous offsets.  The files we're dumping here aren't
                // expected to be very large.
                int i;

                ct -= 2 * LINES_PER_PAGE;
                if (ct < 0) {
                    ct = 0;
                }
                fseek(fp, 0, SEEK_SET);
                for (i = 0; i < ct; i++) {
                    fgets(line, sizeof(line), fp);
                }
                ui->Print("^^^^^^^^^^\n");
            }
        }
    }

    // If the user didn't abort, then give the user time to glance at
    // the end of the log, sorry, no rewind here
    if (key != KEY_POWER) {
        ui->Print("\n--END-- (press any key)\n");
        ui->WaitKey();
    }

    redirect_stdio(TEMPORARY_LOG_FILE);
    fclose(fp);
}

static void choose_recovery_file(Device* device) {
    unsigned int i;
    unsigned int n;
    static const char** title_headers = NULL;
    char *filename;
    const char* headers[] = { "Select file to view",
                              "",
                              NULL };
    // "Go back" + LAST_KMSG_FILE + KEEP_LOG_COUNT + terminating NULL entry
    char* entries[KEEP_LOG_COUNT + 3];
    memset(entries, 0, sizeof(entries));

    n = 0;
    entries[n++] = strdup("Go back");

    // Add kernel kmsg file if available
    if ((ensure_path_mounted(LAST_KMSG_FILE) == 0) && (access(LAST_KMSG_FILE, R_OK) == 0)) {
        entries[n++] = strdup(LAST_KMSG_FILE);
    }

    // Add LAST_LOG_FILE + LAST_LOG_FILE.x
    for (i = 0; i < KEEP_LOG_COUNT; i++) {
        char *filename;
        if (asprintf(&filename, (i==0) ? LAST_LOG_FILE : (LAST_LOG_FILE ".%d"), i) == -1) {
            // memory allocation failure - return early. Should never happen.
            return;
        }
        if ((ensure_path_mounted(filename) != 0) || (access(filename, R_OK) == -1)) {
            free(filename);
            entries[n++] = NULL;
            break;
        }
        entries[n++] = filename;
    }

    title_headers = prepend_title((const char**)headers);

    while(1) {
        int chosen_item = get_menu_selection(title_headers, entries, 1, 0, device);
        if (chosen_item == 0) break;
        file_to_ui(entries[chosen_item]);
    }

    for (i = 0; i < (sizeof(entries) / sizeof(*entries)); i++) {
        free(entries[i]);
    }
}

// Return REBOOT, SHUTDOWN, or REBOOT_BOOTLOADER.  Returning NO_ACTION
// means to take the default, which is to reboot or shutdown depending
// on if the --shutdown_after flag was passed to recovery.
static Device::BuiltinAction
prompt_and_wait(Device* device, int *status) {
    // MStar Android Patch Begin
    const char* const* headers = prepend_title(device->GetMenuHeaders());
    if (1 < boot.status[1]) {
        set_bootloader_message(&boot);
    }
    // MStar Android Patch End

    for (;;) {
        ui->SetProgressType(RecoveryUI::EMPTY);

        int chosen_item = get_menu_selection(headers, device->GetMenuItems(), 0, 0, device);
        // MStar Android Patch Begin
        // ir press "2580" keys,exit recovery mode.
        if (2580 == chosen_item) {
            finish_recovery(NULL);
            return Device::NO_ACTION;
        }
        // MStar Android Patch End

        // device-specific code may take some action here.  It may
        // return one of the core actions handled in the switch
        // statement below.
        Device::BuiltinAction chosen_action = device->InvokeMenuItem(chosen_item);

        int wipe_cache = 0;
        switch (chosen_action) {
            case Device::NO_ACTION:
                break;

            case Device::REBOOT:
            case Device::SHUTDOWN:
            case Device::REBOOT_BOOTLOADER:
                return chosen_action;

            case Device::WIPE_DATA:
                wipe_data(ui->IsTextVisible(), device);
                ui->SetTipTitle(RecoveryUI::TIP_TITLE_SUCCESS);
                if (!ui->IsTextVisible()) return Device::NO_ACTION;
                break;

            case Device::WIPE_CACHE:
                ui->Print("\n-- Wiping cache...\n");
                erase_volume("/cache");
                ui->Print("Cache wipe complete.\n");
                ui->SetTipTitle(RecoveryUI::TIP_TITLE_SUCCESS);
                if (!ui->IsTextVisible()) return Device::NO_ACTION;
                break;

            case Device::APPLY_EXT:
                // MStar Android Patch Begin
                *status = storage_update(&wipe_cache, device);
                if (*status == INSTALL_SUCCESS && wipe_cache) {
                    ui->Print("\n-- Wiping cache (at package request)...\n");
                    if (erase_volume("/cache")) {
                        ui->Print("Cache wipe failed.\n");
                    } else {
                        ui->Print("Cache wipe complete.\n");
                    }
                }
                if (*status >= 0) {
                    if (*status != INSTALL_SUCCESS) {
                        ui->SetBackground(RecoveryUI::ERROR);
                        ui->SetTipTitle(RecoveryUI::TIP_TITLE_ERROR);
                        ui->Print("[ERROR0] Installation aborted.\n");
                    } else if (!ui->IsTextVisible()) {
                        return Device::NO_ACTION;  // reboot if logs aren't visible
                    } else {
                        ui->SetTipTitle(RecoveryUI::TIP_TITLE_SUCCESS);
                        ui->Print("\nInstall from EXT complete.\n");
                    }
                }
                break;
                // MStar Android Patch End

            case Device::APPLY_CACHE:
                // MStar Android Patch Begin
                // why do unmount system?one case:from setting select local upgrade,enter recovery mode do OTA upgrade,upgrading system
                // plug U disk,system partition is mountting;then use IR select "apply update from cache' in recovery mode.
                // do OTA upgrade again,if dont unmount system,then when execute OTA upgrade-script to fromat system partition,it will fail.
                // Associated with the mantis 0515614
                ensure_path_unmounted("/system");
                // Don't unmount cache at the end of this.
                *status = browse_directory(CACHE_ROOT, NULL, &wipe_cache, device);
                if (*status == INSTALL_SUCCESS && wipe_cache) {
                    ui->Print("\n-- Wiping cache (at package request)...\n");
                    if (erase_volume("/cache")) {
                        ui->Print("Cache wipe failed.\n");
                    } else {
                        ui->Print("Cache wipe complete.\n");
                    }
                }
                if (*status >= 0) {
                    if (*status != INSTALL_SUCCESS) {
                        ui->SetBackground(RecoveryUI::ERROR);
                        ui->SetTipTitle(RecoveryUI::TIP_TITLE_ERROR);
                        ui->Print("[ERROR0] Installation aborted.\n");
                    } else if (!ui->IsTextVisible()) {
                        ui->SetTipTitle(RecoveryUI::TIP_TITLE_SUCCESS);
                        return Device::NO_ACTION;  // reboot if logs aren't visible
                    } else {
                        ui->SetTipTitle(RecoveryUI::TIP_TITLE_SUCCESS);
                        ui->Print("\nInstall from cache complete.\n");
                    }
                }
                // MStar Android Patch End
                break;

            case Device::READ_RECOVERY_LASTLOG:
                choose_recovery_file(device);
                break;

            case Device::APPLY_ADB_SIDELOAD:
                // MStar Android Patch Begin
                *status = apply_from_adb(ui, &wipe_cache, TEMPORARY_INSTALL_FILE);
                if (*status >= 0) {
                    if (*status != INSTALL_SUCCESS) {
                        ui->SetBackground(RecoveryUI::ERROR);
                        ui->SetTipTitle(RecoveryUI::TIP_TITLE_ERROR);
                        ui->Print("[ERROR0] Installation aborted.\n");
                        copy_logs();
                    } else if (!ui->IsTextVisible()) {
                        ui->SetTipTitle(RecoveryUI::TIP_TITLE_SUCCESS);
                        return Device::NO_ACTION;  // reboot if logs aren't visible
                    } else {
                        ui->SetTipTitle(RecoveryUI::TIP_TITLE_SUCCESS);
                        ui->Print("\nInstall from ADB complete.\n");
                    }
                }
                // MStar Android Patch End
                break;

            // MStar Android Patch Begin
            case Device::SYSTEM_BACKUP:
                // why do unmount system?one case:from setting select local upgrade,enter recovery mode do OTA upgrade,upgrading system
                // plug U disk,system partition is mountting;then use IR select "system backup" in recovery mode.
                // do system backup,system backup will operation system partition,In order backup success,do unmount system in here.
                // Associated with the mantis 0515614
                ensure_path_unmounted("/system");
                ui->Print("\n-- Backup the system...\n");
                if(0 == init_backup_recovery(device, 1))
                    break;
                write_bootloader_message(1);
                if (system_backup(NULL)) {
                    ui->SetTipTitle(RecoveryUI::TIP_TITLE_ERROR);
                    ui->Print("\n----- Backup the system err! -----\n");
                }
                write_bootloader_message(0);
                break;

            case Device::SYSTEM_RESTORE:
                // why do unmount system?one case:from setting select local upgrade,enter recovery mode do OTA upgrade,upgrading system
                // plug U disk,system partition is mountting;then use IR select "system restore" in recovery mode.
                // do system backup,system restore will operation system partition,In order restore success,do unmount system in here.
                // Associated with the mantis 0515614
                ensure_path_unmounted("/system");
                ui->Print("\n-- Restore the system...\n");
                if(0 == init_backup_recovery(device, 1))
                    break;
                write_bootloader_message(1);
                if (system_restore(NULL,  device)) {
                    ui->SetTipTitle(RecoveryUI::TIP_TITLE_ERROR);
                    ui->Print("\n----- Restore the system err! -----\n");
                }
                write_bootloader_message(0);
                break;
            // MStar Android Patch End
        }
    }
}

static void
print_property(const char *key, const char *name, void *cookie) {
    printf("%s=%s\n", key, name);
}

static void
load_locale_from_cache() {
    FILE* fp = fopen_path(LOCALE_FILE, "r");
    char buffer[80];
    if (fp != NULL) {
        fgets(buffer, sizeof(buffer), fp);
        int j = 0;
        unsigned int i;
        for (i = 0; i < sizeof(buffer) && buffer[i]; ++i) {
            if (!isspace(buffer[i])) {
                buffer[j++] = buffer[i];
            }
        }
        buffer[j] = 0;
        locale = strdup(buffer);
        check_and_fclose(fp, LOCALE_FILE);
    // MStar Android Patch Begin
    } else {
        // assign a default font format
        locale = "en_US";
    // MStar Android Patch End
    }
}

static RecoveryUI* gCurrentUI = NULL;

void
ui_print(const char* format, ...) {
    char buffer[256];

    va_list ap;
    va_start(ap, format);
    vsnprintf(buffer, sizeof(buffer), format, ap);
    va_end(ap);

    if (gCurrentUI != NULL) {
        gCurrentUI->Print("%s", buffer);
    } else {
        fputs(buffer, stdout);
    }
}

// MStar Android Patch Begin
#ifdef USE_LED
void*
led_thread(void *cookie) {
    // need set PAD_PM_SPI_CZ output mode in mboot
    #define PAD_PM_SPI_CZ 1
    #define BALL_K8 PAD_PM_SPI_CZ
    #define LED_GPIO BALL_K8
    MDrv_SYS_GlobalInit();
    mdrv_gpio_init();
    while(1) {
        mdrv_gpio_set_high(LED_GPIO);
        // 0.5s
        usleep(500000);
        mdrv_gpio_set_low(LED_GPIO);
        usleep(500000);
    }
    return NULL;
}
#endif
// MStar Android Patch End
#ifdef SCIFLY
char* copy_file(const char* original_path, const char *dest_path) {

	if(!original_path || !dest_path) {
		LOGE("Invalid path.\n");
		return NULL;
	}

	char* buffer = (char*)malloc(BUFSIZ);
	if (!buffer) {
		LOGE("Failed to allocate buffer\n");
		return NULL;
	}

	size_t read;
	FILE* fin = fopen_path(original_path, "rb");
	if (!fin) {
		LOGE("Failed to open %s (%s)\n", original_path, strerror(errno));
		return NULL;
	}

	FILE* fout = fopen_path(dest_path, "wb");
	if (!fout) {
		LOGE("Failed to open %s (%s)\n", dest_path, strerror(errno));
		return NULL;
	}

	while ((read = fread(buffer, 1, BUFSIZ, fin)) > 0) {
		if (fwrite(buffer, 1, read, fout) != read) {
			LOGE("Short write of %s (%s)\n", dest_path, strerror(errno));
			return NULL;
		}
	}

	free(buffer);

	if (fclose(fout) != 0) {
		LOGE("Failed to close %s (%s)\n", dest_path, strerror(errno));
		return NULL;
	}

	if (fclose(fin) != 0) {
		LOGE("Failed to close %s (%s)\n", original_path, strerror(errno));
		return NULL;
	}

	return strdup(dest_path);
}

char *backup_customer_launcher(const char *orignal_path) {
	if(!orignal_path || !strstr(orignal_path,".apk")) {
		LOGE("Invalid launcher path:%s\n",orignal_path);
		return NULL;
	}

	char *p = strrchr(orignal_path,'/');
	if(!p) {
		LOGE("Invalid path(%s), can't find a valid filename.\n",orignal_path);
		return NULL;
	}

	char *backup_name = strdup(p + 1);

	char dest_path[PATH_MAX];
	strcpy(dest_path, SIDELOAD_TEMP_DIR);
	strcat(dest_path, "/");
	strcat(dest_path, backup_name);
	free(backup_name);

	return copy_file(orignal_path, dest_path);
}

char *backup_customer_file(const char *orignal_path) {
	if(!orignal_path) {
		LOGE("Invalid file path:%s\n",orignal_path);
		return NULL;
	}

	char *p = strrchr(orignal_path,'/');
	if(!p) {
		LOGE("Invalid path(%s), can't find a valid filename.\n",orignal_path);
		return NULL;
	}

	char *backup_name = strdup(p + 1);

	char dest_path[PATH_MAX];
	strcpy(dest_path, SIDELOAD_TEMP_DIR);
	strcat(dest_path, "/");
	strcat(dest_path, backup_name);
	free(backup_name);

	return copy_file(orignal_path, dest_path);
}
int restore_customer_file(const char *file_backup_path, const char *orignal_path) {
	if(!file_backup_path || !orignal_path) {
		LOGE("Invalid path.\n");
		return -1;
	}
	if(!copy_file(file_backup_path,orignal_path)) {
		LOGE("copy %s to %s failed:%s\n",file_backup_path, orignal_path,strerror(errno));
		return -1;
	}

	if (chmod(orignal_path, 0644) != 0) {
		LOGE("Failed to chmod %s (%s)\n",orignal_path, strerror(errno));
		return -1;
	}

	if (chown(orignal_path, 1000, 1000) != 0) {
		LOGE("Failed to chown %s (%s)\n",orignal_path, strerror(errno));
		return -1;
	}

	return 0;
}

#endif
int
main(int argc, char **argv) {
#ifdef USE_LED
    // led light
    pthread_t led_t;
    pthread_create(&led_t, NULL, led_thread, NULL);
#endif

    time_t start = time(NULL);

    redirect_stdio(TEMPORARY_LOG_FILE);

    // If this binary is started with the single argument "--adbd",
    // instead of being the normal recovery binary, it turns into kind
    // of a stripped-down version of adbd that only supports the
    // 'sideload' command.  Note this must be a real argument, not
    // anything in the command file or bootloader control block; the
    // only way recovery should be run with this argument is when it
    // starts a copy of itself from the apply_from_adb() function.
    if (argc == 2 && strcmp(argv[1], "--adbd") == 0) {
        adb_main();
        return 0;
    }

    printf("Starting recovery (pid %d) on %s", getpid(), ctime(&start));

    load_volume_table();
    ensure_path_mounted(LAST_LOG_FILE);
    rotate_last_logs(KEEP_LOG_COUNT);
    get_args(&argc, &argv);

    // MStar Android Patch Begin
    const char *send_intent = NULL;
    const char *update_package = NULL;
    int wipe_data = 0, wipe_cache = 0, show_text = 0, backup_system = 0, restore_system = 0;
    bool just_exit = false;
    bool shutdown_after = false;
    int wait_time = -1;
    int arg;
#ifdef SCIFLY
	const char *customer_launcher_path = NULL;
#endif    
    // MStar Android Patch Begin
    while ((arg = getopt_long(argc, argv, "", OPTIONS, NULL)) != -1) {
        switch (arg) {
        case 's': send_intent = optarg; break;
        case 'u': update_package = optarg; break;
        case 'd': dev_uuid = optarg; break;
        case 'a': dev_label= optarg; break;
        case 'w': wipe_data = wipe_cache = 1; break;
        case 'c': wipe_cache = 1; break;
        case 't': show_text = 1; break;
        case 'x': just_exit = true; break;
        case 'l': locale = optarg; break;
        case 'g': {
            if (stage == NULL || *stage == '\0') {
                char buffer[20] = "1/";
                strncat(buffer, optarg, sizeof(buffer)-3);
                stage = strdup(buffer);
            }
            break;
        }
        case 'p': shutdown_after = true; break;
        case 'r': reason = optarg; break;
        case 'b': backup_system = 1; break;
        case 'z': restore_system = 1; break;
        case 'q': wait_time = atoi(optarg); break;
#ifdef SCIFLY
		case 'L': customer_launcher_path = optarg; break;
#endif        
        case '?':
            LOGE("Invalid command argument\n");
            continue;
        }
    }
    // MStar Android Patch End

    if (locale == NULL) {
        load_locale_from_cache();
    }
    printf("locale is [%s]\n", locale);
    printf("stage is [%s]\n", stage);
    printf("reason is [%s]\n", reason);

    Device* device = make_device();
    ui = device->GetUI();
    gCurrentUI = ui;

    ui->SetLocale(locale);
    ui->Init();

    int st_cur, st_max;
    if (stage != NULL && sscanf(stage, "%d/%d", &st_cur, &st_max) == 2) {
        ui->SetStage(st_cur, st_max);
    }

    ui->SetBackground(RecoveryUI::NONE);
    if (show_text) ui->ShowText(true);

    struct selinux_opt seopts[] = {
      { SELABEL_OPT_PATH, "/file_contexts" }
    };

    sehandle = selabel_open(SELABEL_CTX_FILE, seopts, 1);

    if (!sehandle) {
        ui->Print("Warning: No file_contexts\n");
    }

    // MStar Android Patch Begin
    // select EXIT key to exit recovery before starting to recovery system
    time_t old = time(NULL);
    time_t now = time(NULL);
    int timespace = now - old;
    int keypressed = 0;
    int waitTime;
    // get how many seconds we will wait
    if (wait_time == -1) {
        // not get args from setting or mboot, set 5s default
        waitTime = 5;
    } else {
        // get args from setting or mboot , need compare wait_time with the max seconds we support
        waitTime = wait_time > (ui->GetMaxWaitTime())?(ui->GetMaxWaitTime()):wait_time;
    }
	// EosTek Patch Begin
#ifndef SCIFLY
    while (timespace < waitTime) {
        // show exit recovery tip picture
        ui->SetExitRecoveryTipTitle(waitTime-1-timespace);
        // check EXIT key
        keypressed = ui->IsKeyPressed(KEY_BACK);
        if (keypressed == 1) {
            finish_recovery(send_intent);
            ui->Print("Rebooting...\n");
            android_reboot(ANDROID_RB_RESTART, 0, 0);
            return EXIT_SUCCESS;
        }
        now = time(NULL);
        timespace = now - old;
    }
#endif
	// EosTek Patch End
    // not show exit recvoery tip picture later
    ui->SetExitRecoveryTipTitle(-1);
    // MStar Android Patch End

    device->StartRecovery();

    printf("Command:");
    for (arg = 0; arg < argc; arg++) {
        printf(" \"%s\"", argv[arg]);
    }
    printf("\n");

    // MStar Android Patch Begin
    printf("\ndev_uuid : %s\n", dev_uuid);
    printf("dev_label : %s\n", dev_label);
    // MStar Android Patch End

    if (update_package) {
        // For backwards compatibility on the cache partition only, if
        // we're given an old 'root' path "CACHE:foo", change it to
        // "/cache/foo".
        if (strncmp(update_package, "CACHE:", 6) == 0) {
            int len = strlen(update_package) + 10;
            char* modified_path = (char*)malloc(len);
            strlcpy(modified_path, "/cache/", len);
            strlcat(modified_path, update_package+6, len);
            printf("(replacing path \"%s\" with \"%s\")\n",
                   update_package, modified_path);
            update_package = modified_path;
        }
    }
    printf("\n");

    property_list(print_property, NULL);
    property_get("ro.build.display.id", recovery_version, "");
    printf("\n");

    int status = INSTALL_SUCCESS;

    if (update_package != NULL) {
        status = install_package(update_package, &wipe_cache, TEMPORARY_INSTALL_FILE, true);
        if (status == INSTALL_SUCCESS && wipe_cache) {
            if (erase_volume("/cache")) {
                // MStar Android Patch Begin
                status = INSTALL_ERROR;
                // MStar Android Patch End
                LOGE("Cache wipe (requested by package) failed.");
            }
        }
		// EosTek Patch Begin
#ifdef SCIFLY
        if(status == INSTALL_SUCCESS) {
            ui->SetBackground(RecoveryUI::EOS_UPDATE_FINISH);
            ui->ShowProgress(0,0);
            ui->SetProgress(1.0);
        }
#endif
		// EosTek Patch End
        if (status != INSTALL_SUCCESS) {
            // MStar Android Patch Begin
            ui->Print("[ERROR0] Installation aborted.\n");
            // MStar Android Patch End
            ui->Print("OTA failed! Please power off the device to keep it in this state and file a bug report!\n");

            // If this is an eng or userdebug build, then automatically
            // turn the text display on if the script fails so the error
            // message is visible.
            char buffer[PROPERTY_VALUE_MAX+1];
            property_get("ro.build.fingerprint", buffer, "");
            if (strstr(buffer, ":userdebug/") || strstr(buffer, ":eng/")) {
                ui->ShowText(true);
            }
			// EosTek Patch Begin
#ifdef SCIFLY
            copy_logs();

            ui->SetBackground(RecoveryUI::EOS_UPDATE_ERROR);
            sleep(3);

            struct bootloader_message boot;
            memset(&boot, 0, sizeof(boot));
            set_bootloader_message(&boot);
            ui->Print("Rebooting...\n");

            android_reboot(ANDROID_RB_RESTART, 0, 0);
#endif
			// EosTek Patch End
        }
    } else {
        // MStar Android Patch Begin
        set_bootloader_message(&boot);
        // MStar Android Patch End
        if (wipe_data) {
#ifdef SCIFLY
#define USER_BOOTANIMATION_FILE  "/data/local/bootanimation.zip"
#define USER_BOOTVIDEO_FILE  "/data/video/video.ts"
#define SCIFLY_VIRTUAL_MAC_FILE "/data/local/virtual_mac"

		    char* backup_bootanimation_path = NULL;
		    char* backup_bootvideo_path = NULL;
		    char* backup_launcher_path = NULL;
		    char* backup_virtual_mac_path = NULL;


		    backup_bootanimation_path = backup_customer_file(USER_BOOTANIMATION_FILE);
		    if(!backup_bootanimation_path) {
			    LOGE("Backup customer file (%s) failed:%s\n",USER_BOOTANIMATION_FILE,strerror(errno));
		    }

		    backup_bootvideo_path = backup_customer_file(USER_BOOTVIDEO_FILE);
		    if(!backup_bootvideo_path) {
			    LOGE("Backup customer file (%s) failed:%s\n",USER_BOOTVIDEO_FILE,strerror(errno));
		    }

		    backup_virtual_mac_path = backup_customer_file(SCIFLY_VIRTUAL_MAC_FILE);
			if(!backup_virtual_mac_path) {
				LOGE("Backup customer file (%s) failed:%s\n",SCIFLY_VIRTUAL_MAC_FILE,strerror(errno));
			}

		     
		    if(customer_launcher_path) {
			    backup_launcher_path = backup_customer_launcher(customer_launcher_path);
			    if(!backup_launcher_path) {
				    LOGE("Backup customer launcher %s failed:%s\n",customer_launcher_path,strerror(errno));
			    }
		    }
            int progress_interval = 17;
            ui->SetBackground(RecoveryUI::INSTALLING_UPDATE);
            ui->ShowProgress(0.99,progress_interval);
            time_t start_t = time(NULL);
#endif
            if (device->WipeData()) status = INSTALL_ERROR;
            if (erase_volume("/data")) status = INSTALL_ERROR;
            if (wipe_cache && erase_volume("/cache")) status = INSTALL_ERROR;
            // MStar Android Patch Begin
            if (status != INSTALL_SUCCESS) {
                ui->Print("[ERROR1] Data wipe failed.\n");
            }
            if (reset_tvdatabase() != 0) {
                status = INSTALL_ERROR;
                ui->Print("[ERROR2] reset factory db failed.\n");
            }
#ifdef USE_ENV_UTILS
            if (reset_reproducerate() != 0) {
                status = INSTALL_ERROR;
                ui->Print("[ERROR3] reset reproducerate failed.\n");
            }
#endif
            // MStar Android Patch End
#ifdef SCIFLY
		    if(customer_launcher_path) {
			    // restore custom launcher to /data/app
			    if(backup_launcher_path) {
			    
				    if(INSTALL_SUCCESS == restore_customer_file(backup_launcher_path, customer_launcher_path)) {
					    LOGI("Restore customer launcher %s success!\n",customer_launcher_path);
				    } else {
					    LOGE("Restore customer launcher %s failed!\n",customer_launcher_path);
				    }
			    }
		    }

		    if(backup_bootanimation_path) {

			    if(INSTALL_SUCCESS == restore_customer_file(backup_bootanimation_path, USER_BOOTANIMATION_FILE)) {
				    LOGI("Restore customer file (%s) success!\n",USER_BOOTANIMATION_FILE);
			    } else {
				    LOGE("Restore customer file (%s) failed!\n",USER_BOOTANIMATION_FILE);
			    }
		    }

		    if(backup_bootvideo_path) {
		    
			    if(INSTALL_SUCCESS == restore_customer_file(backup_bootvideo_path, USER_BOOTVIDEO_FILE)) {
				    LOGI("Restore customer file (%s) success!\n",USER_BOOTVIDEO_FILE);
			    } else {
				    LOGE("Restore customer file (%s) failed!\n",USER_BOOTVIDEO_FILE);
			    }
		    }
 	         if(backup_virtual_mac_path) {
		    
			   if(INSTALL_SUCCESS == restore_customer_file(backup_virtual_mac_path, SCIFLY_VIRTUAL_MAC_FILE)) {
				    LOGI("Restore customer file (%s) success!\n",SCIFLY_VIRTUAL_MAC_FILE);
			    } else {
				    LOGE("Restore customer file (%s) failed!\n",SCIFLY_VIRTUAL_MAC_FILE);
			    }
		    }
            if(status != INSTALL_SUCCESS) {
                ui->SetBackground(RecoveryUI::ERROR);
            } else {
                int interval = int(time(NULL) - start_t);
                if(interval < progress_interval) {
                    sleep(progress_interval - interval);
                }
                ui->ShowProgress(0.01,1);
                sleep(1);
                ui->ShowProgress(0.0,0);
            }
            
            sleep(2);
            ui->SetBackground(RecoveryUI::NONE);
#endif            
        } else if (wipe_cache) {
            if (wipe_cache && erase_volume("/cache")) status = INSTALL_ERROR;
            if (status != INSTALL_SUCCESS) ui->Print("Cache wipe failed.\n");
        // MStar Android Patch Begin
        }  else if (backup_system) {
            if (init_backup_recovery(NULL, 0) && system_backup("/sdcard/backup_system")) status = INSTALL_ERROR;
            if (status != INSTALL_SUCCESS) ui->Print("backup system failed.\n");
        }  else if (restore_system) {
            if (init_backup_recovery(NULL, 0)  && system_restore("/sdcard/backup_system/", NULL)) status = INSTALL_ERROR;
            if (status != INSTALL_SUCCESS) ui->Print("restore system failed.\n");
        // MStar Android Patch End
        } else if (!just_exit) {
            status = INSTALL_NONE;  // No command specified
            // MStar Android Patch Begin
            memset(&boot, 0, sizeof(boot));
            set_bootloader_message(&boot);
            // MStar Android Patch End
        }
    }

    // MStar Android Patch Begin
    if (status != INSTALL_SUCCESS) {
        copy_logs();
        ui->SetBackground(RecoveryUI::ERROR);
        ui->SetTipTitle(RecoveryUI::TIP_TITLE_ERROR);
        // show error UI 2s
        sleep(2);
    }
    // MStar Android Patch End

    Device::BuiltinAction after = shutdown_after ? Device::SHUTDOWN : Device::REBOOT;
    if (status != INSTALL_SUCCESS || ui->IsTextVisible()) {
        ui->ShowText(1);
        Device::BuiltinAction temp = prompt_and_wait(device, &status);
        if (temp != Device::NO_ACTION) after = temp;
    }

    // Save logs and clean up before rebooting or shutting down.
    // MStar Android Patch Begin
    if (status == INSTALL_SUCCESS) {
        finish_recovery(send_intent);
        char reload_env_flag = (char)(0x00);
        if (get_reload_env_enable()) {
            reload_env_flag = reload_env_flag | BOOT_STATUS_ACTION_RELOADENV_BIT;
        }

        if (get_reload_panel_enable()) {
            reload_env_flag = reload_env_flag | BOOT_STATUS_ACTION_RELOADPANEL_BIT;
        }

        if ((int)(reload_env_flag) > 0) {
            write_bootloader_env_flag(reload_env_flag);
        }
    }
    // MStar Android Patch End

    switch (after) {
        case Device::SHUTDOWN:
            ui->Print("Shutting down...\n");
            property_set(ANDROID_RB_PROPERTY, "shutdown,");
            break;

        case Device::REBOOT_BOOTLOADER:
            ui->Print("Rebooting to bootloader...\n");
            property_set(ANDROID_RB_PROPERTY, "reboot,bootloader");
            break;

        default:
            ui->Print("Rebooting...\n");
            property_set(ANDROID_RB_PROPERTY, "reboot,");
            break;
    }
    sleep(5); // should reboot before this finishes
    return EXIT_SUCCESS;
}
