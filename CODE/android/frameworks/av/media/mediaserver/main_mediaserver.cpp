/*
**
** Copyright 2008, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

#define LOG_TAG "mediaserver"
//#define LOG_NDEBUG 0

#include <fcntl.h>
#include <sys/prctl.h>
#include <sys/wait.h>
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#include <cutils/properties.h>
#include <utils/Log.h>
#include "RegisterExtensions.h"

// from LOCAL_C_INCLUDES
#include "AudioFlinger.h"
#include "CameraService.h"
#include "MediaLogService.h"
#include "MediaPlayerService.h"
#include "AudioPolicyService.h"
#include "SoundTriggerHwService.h"

// EosTek Patch Begin
#include <sys/types.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>
// EosTek Patch End

using namespace android;

// EosTek Patch Begin
static int readSymLink(const char *path, char *link, size_t max) {
    struct stat s;
    int length;

    if (lstat(path, &s) < 0)
        return 0;
    if ((s.st_mode & S_IFMT) != S_IFLNK)
        return 0;

    // we have a symlink
    length = readlink(path, link, max- 1);
    if (length <= 0)
        return 0;
    link[length] = 0;
    return 1;
}

static int pathMatchesMountPoint(const char* path, const char* mountPoint) {
    int length = strlen(mountPoint);
    if (length > 1 && strncmp(path, mountPoint, length) == 0) {
        // we need to do extra checking if mountPoint does not end in a '/'
        if (mountPoint[length - 1] == '/')
            return 1;
        // if mountPoint does not have a trailing slash, we need to make sure
        // there is one in the path to avoid partial matches.
        return (path[length] == 0 || path[length] == '/');
    }

    return 0;
}

static int getFd(const char *s) {
    int result = 0;
    while (*s) {
        if (!isdigit(*s)) return -1;
        result = 10 * result + (*s++ - '0');
    }
    return result;
}

static int checkFileDescriptorSymLinks(const char *mountPoint) {

    int pid = getpid();
    // compute path to process's directory of open files
    char    path[PATH_MAX];
    sprintf(path, "/proc/%d/fd", pid);
    DIR *dir = opendir(path);
    if (!dir)
        return 0;

    // remember length of the path
    int parent_length = strlen(path);
    // append a trailing '/'
    path[parent_length++] = '/';

    struct dirent* de;
    while ((de = readdir(dir))) {
        if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, "..")
                || strlen(de->d_name) + parent_length + 1 >= PATH_MAX)
            continue;

        // append the file name, after truncating to parent directory
        path[parent_length] = 0;
        strcat(path, de->d_name);

        char link[PATH_MAX];

        if (readSymLink(path, link, sizeof(link)) && pathMatchesMountPoint(link, mountPoint)) {
            ALOGI("close file : fd: %s, path:%s \n", de->d_name, link);
            int fd = getFd(de->d_name);
            close(fd);
        }
    }

    closedir(dir);
    return 0;
}

static void handle_term(int signo) {
    if(SIGTERM == signo) {
        checkFileDescriptorSymLinks("/mnt/usb"); // for mstar's platform
        checkFileDescriptorSymLinks("/storage/external_storage"); // for amlogic's platform
    }
}
// EosTek Patch End

int main(int argc __unused, char** argv)
{
    signal(SIGPIPE, SIG_IGN);
    // EosTek Patch Begin
    signal(SIGTERM, handle_term);
    // EosTek Patch End
    char value[PROPERTY_VALUE_MAX];
    bool doLog = (property_get("ro.test_harness", value, "0") > 0) && (atoi(value) == 1);
    pid_t childPid;
    // FIXME The advantage of making the process containing media.log service the parent process of
    // the process that contains all the other real services, is that it allows us to collect more
    // detailed information such as signal numbers, stop and continue, resource usage, etc.
    // But it is also more complex.  Consider replacing this by independent processes, and using
    // binder on death notification instead.
    if (doLog && (childPid = fork()) != 0) {
        // media.log service
        //prctl(PR_SET_NAME, (unsigned long) "media.log", 0, 0, 0);
        // unfortunately ps ignores PR_SET_NAME for the main thread, so use this ugly hack
        strcpy(argv[0], "media.log");
        sp<ProcessState> proc(ProcessState::self());
        MediaLogService::instantiate();
        ProcessState::self()->startThreadPool();
        for (;;) {
            siginfo_t info;
            int ret = waitid(P_PID, childPid, &info, WEXITED | WSTOPPED | WCONTINUED);
            if (ret == EINTR) {
                continue;
            }
            if (ret < 0) {
                break;
            }
            char buffer[32];
            const char *code;
            switch (info.si_code) {
            case CLD_EXITED:
                code = "CLD_EXITED";
                break;
            case CLD_KILLED:
                code = "CLD_KILLED";
                break;
            case CLD_DUMPED:
                code = "CLD_DUMPED";
                break;
            case CLD_STOPPED:
                code = "CLD_STOPPED";
                break;
            case CLD_TRAPPED:
                code = "CLD_TRAPPED";
                break;
            case CLD_CONTINUED:
                code = "CLD_CONTINUED";
                break;
            default:
                snprintf(buffer, sizeof(buffer), "unknown (%d)", info.si_code);
                code = buffer;
                break;
            }
            struct rusage usage;
            getrusage(RUSAGE_CHILDREN, &usage);
            ALOG(LOG_ERROR, "media.log", "pid %d status %d code %s user %ld.%03lds sys %ld.%03lds",
                    info.si_pid, info.si_status, code,
                    usage.ru_utime.tv_sec, usage.ru_utime.tv_usec / 1000,
                    usage.ru_stime.tv_sec, usage.ru_stime.tv_usec / 1000);
            sp<IServiceManager> sm = defaultServiceManager();
            sp<IBinder> binder = sm->getService(String16("media.log"));
            if (binder != 0) {
                Vector<String16> args;
                binder->dump(-1, args);
            }
            switch (info.si_code) {
            case CLD_EXITED:
            case CLD_KILLED:
            case CLD_DUMPED: {
                ALOG(LOG_INFO, "media.log", "exiting");
                _exit(0);
                // not reached
                }
            default:
                break;
            }
        }
    } else {
        // all other services
        if (doLog) {
            prctl(PR_SET_PDEATHSIG, SIGKILL);   // if parent media.log dies before me, kill me also
            setpgid(0, 0);                      // but if I die first, don't kill my parent
        }
        sp<ProcessState> proc(ProcessState::self());
        sp<IServiceManager> sm = defaultServiceManager();
        ALOGI("ServiceManager: %p", sm.get());
        AudioFlinger::instantiate();
        MediaPlayerService::instantiate();
        CameraService::instantiate();
        AudioPolicyService::instantiate();
        SoundTriggerHwService::instantiate();
        registerExtensions();
        ProcessState::self()->startThreadPool();
        IPCThreadState::self()->joinThreadPool();
    }
}
