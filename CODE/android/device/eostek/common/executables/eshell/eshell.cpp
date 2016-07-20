#define LOG_NDEBUG 1
#define LOG_TAG "eshell"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <errno.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <cutils/sockets.h>
#include <cutils/log.h>

#define BUFFER_MAX    1024 
#define REPLY_MAX     256   
#define SOCKET_PATH "eshell"

#define MD5_HASH_LENGTH 16
#define MD5_LENGTH 33

static char g_digitalSignature[256];
static char g_packageName[32];

extern void Mstar_DecodeBase64(char *strSource, int nSourceLen, char *strDest);
extern void Mstar_EncodeBase64(char *strSource, int nSourceLen, char *strDest);

static int permissionDenied() {
    ALOGE("eshell: permission denied\n");
    return -1;
}

int getPackageName(int pid) {
    char str[128] = "";
    char str1[128] = "";
    char str2[128] = "wuhan#$(^";
    char statline[1024];
    char cmdline[1024];
    char macline[1024];
    struct stat stats;
    int fd, r;
    int length = 0;

    sprintf(statline, "/proc/%d", pid);
    stat(statline, &stats);

    sprintf(statline, "/proc/%d/stat", pid);
    sprintf(cmdline, "/proc/%d/cmdline", pid);
    snprintf(macline, sizeof(macline), "/proc/%d/attr/current", pid);
    fd = open(cmdline, O_RDONLY);
    if (fd == 0) {
        r = 0;
    } else {
        r = read(fd, cmdline, 1023);
        close(fd);
        if (r < 0) {
            r = 0;
        }
    }
    cmdline[r] = 0;

    fd = open(statline, O_RDONLY);
    if (fd == 0)
        return -1;
    r = read(fd, statline, 1023);
    close(fd);
    if (r < 0) {
        return -1;
    }
    statline[r] = 0;

    strcpy(str1, cmdline);
    length = strlen(cmdline);
    if (length > 12) {
        strncpy(str, &str1[length - 12], 12);
        strcat(str, "jrm");
        strcat(str2, str);

        ALOGV("str2:%s\n", str2);
    } else {
        strcat(str1, "jrm");
        strcat(str2, str1);

        ALOGV("str2:%s\n", str2);
    }
    ALOGV("PackageNam = %s\n", str2);
    strcpy(g_packageName, str2);
    return 0;
}

static int checkPermission() {
    int ret = 0;
    char str[128] = "";
    char encData_base64[256] = "";

    if (!(strcmp(g_digitalSignature, ""))) {
        printf("g_digitalSignature is NULL!\n");
        return -1;
    }

    Mstar_EncodeBase64((char *) g_packageName, strlen(g_packageName),
            encData_base64);
    strcpy(str, &encData_base64[8]);

    ALOGV("str1:%s\n", g_digitalSignature);
    ALOGV("str2:%s\n", str);

    if (!strcmp(g_digitalSignature, str)) {
        ALOGV("checkPermission success !\n");
        ret = 0;
    } else {
        ALOGE("checkPermission failed!\n");
        ret = -1;
    }

    return ret;
}
static int authorize_by_pid(const char *src) {
    int pid = -1;

    char *l_bracket = strrchr(src, '[');
    char *dash = strrchr(src, '-');
    char *r_bracket = strrchr(src, ']');

    if (!l_bracket || !dash || !r_bracket) {
        ALOGE("Non-authorize command:%s\n", src);
        return permissionDenied();
    }
    if (l_bracket) {
        *l_bracket = 0;
    }

    pid = atoi(l_bracket + 1);
    strncpy(g_digitalSignature, dash + 1, r_bracket - dash - 1);

    ALOGV("Pid:%d ; DigitalSignature:%s\n", pid, g_digitalSignature);

    if (getPackageName(pid) != 0) {
        return permissionDenied();
    }

    if (checkPermission() != 0)
        return permissionDenied();

    return 0;
}

static int readx(int s, void *_buf, int count) {
    char *buf = (char *) _buf;
    int n = 0, r;
    if (count < 0)
        return -1;
    while (n < count) {
        r = read(s, buf + n, count - n);
        if (r < 0) {
            if (errno == EINTR)
                continue;
            ALOGE("read error: %s\n", strerror(errno));
            return -1;
        }
        if (r == 0) {
            ALOGE("eof\n");
            return -1; /* EOF */
        }
        n += r;
    }
    ALOGV("read %d bytes : %s", count, buf);
    return 0;
}

static int writex(int s, const void *_buf, int count) {
    const char *buf = (const char *) _buf;
    int n = 0, r;
    if (count < 0)
        return -1;
    while (n < count) {
        r = write(s, buf + n, count - n);
        if (r < 0) {
            if (errno == EINTR)
                continue;
            ALOGE("write error: %s\n", strerror(errno));
            return -1;
        }
        n += r;
    }
    return 0;
}

static int execute(int s, char cmd[BUFFER_MAX]) {
    char reply[REPLY_MAX];
    unsigned n = 0;
    unsigned short count;
    int ret = -1;
    int rc = -1;
    int child_status = -1;
    FILE *pp = NULL;

    ALOGV("execute('%s')\n", cmd);
    memset(reply, 0, REPLY_MAX);
    pp = popen(cmd, "r");
    if (!pp) {
        ALOGE("exec %s failed .\n", cmd);
        goto done;
    }

    ret = fread(reply, 1, sizeof(reply), pp);
    if (ret < 0) {
        ALOGE("read pipe failed.\n");
    }

    rc = pclose(pp);
    child_status = WEXITSTATUS(rc);
    ALOGV("rc=%d; child_status=%d\n",rc,child_status);
done: 
    if(!child_status) {
        ret = 0;
    } else {
        ret = -1;
        snprintf(reply,REPLY_MAX,"exec '%s' failed with exit code %d.\n",cmd,child_status);
    }
    
    if (reply[0]) {
        n = snprintf(cmd, BUFFER_MAX, "%d %s", ret, reply);
    } else {
        n = snprintf(cmd, BUFFER_MAX, "%d", ret);
    }
    if (n > BUFFER_MAX)
        n = BUFFER_MAX;
    count = n;

    ALOGV("reply: '%s'\n", cmd);
    if (writex(s, &count, sizeof(count)))
        return -1;
    if (writex(s, cmd, count))
        return -1;
    return 0;
}

int main(const int argc, const char *argv[]) {
    char buf[BUFFER_MAX];
    struct sockaddr addr;
    socklen_t alen;
    int lsocket, s, count;

    ALOGI("Eshell firing up\n");

    lsocket = android_get_control_socket(SOCKET_PATH);
    if (lsocket < 0) {
        ALOGE("Failed to get socket from environment: %s\n", strerror(errno));
        exit(1);
    }
    if (listen(lsocket, 5)) {
        ALOGE("Listen on socket failed: %s\n", strerror(errno));
        exit(1);
    }
    fcntl(lsocket, F_SETFD, FD_CLOEXEC);

    for (;;) {
        alen = sizeof(addr);
        s = accept(lsocket, &addr, &alen);
        if (s < 0) {
            ALOGE("Accept failed: %s\n", strerror(errno));
            continue;
        }
        fcntl(s, F_SETFD, FD_CLOEXEC);

        ALOGI("new connection\n");
        for (;;) {
            unsigned short count;
            if (readx(s, &count, sizeof(count))) {
                ALOGE("failed to read size\n");
                break;
            }
            if ((count < 1) || (count >= BUFFER_MAX)) {
                ALOGE("invalid size %d\n", count);
                break;
            }
            if (readx(s, buf, count)) {
                ALOGE("failed to read command\n");
                break;
            }
            buf[count] = 0;

            if (!authorize_by_pid(buf)) {
                execute(s, buf);
                break;
            } else {
                ALOGV("Non-authorized command:%s\n", buf);
                break;
            }
        }
        ALOGI("closing connection\n");
        close(s);
    }

    close(lsocket);
    return 0;
}
