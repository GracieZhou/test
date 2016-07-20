
package com.android.settings.update.ota;

import java.io.File;
import java.util.regex.Pattern;

import android.os.Environment;

public interface Constants {

    /**
     * SharedPreference name.
     */
    public static final String PREFERENCE_NAME = "ota_preferences";

    public static final String DOWNLOAD_FINISHED = "download_finished";

    public static final String DOWNLOAD_HTTP_ID = "download_http_id";

    public static final String DOWNLOAD_P2P_ID = "download_p2p_id";

    public static final String P2P_BACKUP_ID = "p2p_backup_id";

    public static final String P2P_BACKUP_PATH = "p2p_backup_path";

    public static final String DOWNLOAD_PATH = "download_path";

    public static final String DOWNLOAD_ENGINE = "download_engine";

    public static final String ETAG_CHECKSUM = "etag_checksum";

    public static final String FORCE_OTA = "force_ota";

    public static final String FORCE_OTA_MD5 = "force_ota_md5";

    // intent to compact with old settings=ota
    public static final String OLD_PREFERENCE_NAME = "last_update_info";

    public static final String OLD_PREFERENCE_LAST_FILE_PATH = "last_file_path";

    public static final String OLD_PREFERENCE_LAST_SN_FILE_PATH = "last_sn_file_path";

    public static final String DEFAULT_DOWNLOAD_PATH = new File(Environment.getExternalStorageDirectory(),
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    /**
     * Assume all version are like: v2.3.0.15756
     */
    public static final Pattern LEGAL_KEY_PATTERN = Pattern
            .compile(".[0-9]{1,9}\\.[0-9]{1,9}\\.[0-9]{1,9}\\.[0-9]{1,9}");

    /**
     * Uploading server address.
     */
    public static final String DEFAULT_SERVER_PROP = "ro.scifly.service.url";

    public static final String DEFAULT_SERVER_URL = "http://172.23.67.78:8013/TMS/interface/clientService.jsp";

    /**
     * Invalid version String display in Activity
     */
    public static final String VERSION_INVALID = "---------------";

    public static final String INVALID_STRING = "invalid";

    // OTA Package Type define here
    public static final int OTA_TYPE_INVALID = -1;

    public static final int OTA_TYPE_INCREASE = 0;

    public static final int OTA_TYPE_FULL = 1;

    /**
     * Enum download engine.
     */
    public static final String DOWNLOAD_ENGINE_HTTP = "http";

    public static final String DOWNLOAD_ENGINE_P2P = "p2p";

    /**
     * Sn+ task state type.
     */
    public static final int SN_TASK_STATE_READY = 0;

    public static final int SN_TASK_STATE_RUNNING = 1;

    public static final int SN_TASK_STATE_PAUSED = 2;

    public static final int SN_TASK_STATE_COMPLETE = 3;

    public static final int SN_TASK_STATE_ERROR = 4;

    /**
     * Enum ui status.
     */
    public static final int UI_INVALID_ID = -1;

    public static final int UI_VERSION_EQUALS = 1;

    public static final int UI_VERSION_NEWER = 2;

    public static final int UI_VERSION_CHECK_FAILED = 3;

    public static final int UI_SPACE_NOT_ENOUGH = 4;

    public static final int UI_SHOW_PROGRESS = 5;

    /**
     * Action to start UpdateService.
     */
    public static final String ACTION_UPDATE_INCREMENTAL_ALL = "scifly.intent.action.UPDATE_INCREMENTAL_ALL";

    public static final String ACTION_UPDATE_CLOUD_PUSH = "scifly.intent.action.UPDATE_CLOUD_PUSH";

    public static final String EXTRA_CLOUD_PUSH_TASKID = "extra_cloud_push_taskid";

    public static final String EXTRA_CLOUD_PUSH_FORCE = "extra_cloud_push_force";

    public static final String EXTRA_INCREMENTAL_ALL = "extra_incremental_all";

    public static final String ACTION_DOWNLOAD_FINISHED = "scifly.intent.action.UPDATE_DOWNLOAD_FINISHED";

    public static final String EXTRA_PATH = "path";

    public static final String EXTRA_MD5 = "md5";

    // New version found dialog button click event type;
    /**
     * Enum messages.
     */
    static final int MSG_BASE = 300;

    public static final int MSG_UPGRADE = MSG_BASE + 11;

    public static final int MSG_CANCEL = MSG_BASE + 12;

    public static final int MSG_REBOOT_NOW = MSG_BASE + 14;

    public static final int MSG_REBOOT_LATER = MSG_BASE + 15;

    public static final int MSG_MD5_MATCH = MSG_BASE + 16;

    public static final int MSG_MD5_MISMATCH = MSG_BASE + 17;

    public static final int MSG_NEW_VERSION = MSG_BASE + 18;

    public static final int MSG_CLOUD_PUSH = MSG_BASE + 19;

    // Reocovery
    public static final String DIR_CACHE = "/cache";

    public static final String RECOVERY_DIR = "/cache/recovery";

    public static final String COMMAND_FILE_NAME = "command";

    // Update Package info's key in json
    public static final String JSONKEY_UDS = "uds";

    public static final String JSONKEY_VER = "ver";

    public static final String JSONKEY_URL = "url";

    public static final String JSONKEY_MD5 = "md5";

    public static final String JSONKEY_SIZE = "size";

    public static final String JSONKEY_PUBTIME = "pubTime";

    public static final String JSONKEY_FORCE = "fd";

    public static final String JSONKEY_ETAG = "Etag";

    public static final String JSONKEY_FACVER = "facVer";

    public static final int HTTP_STATUS_NOT_MODIFIED = 304;

    public static final int HTTP_STATUS_OK = 200;

    public static final int HTTP_STATUS_REDIRECT = 302;

}
