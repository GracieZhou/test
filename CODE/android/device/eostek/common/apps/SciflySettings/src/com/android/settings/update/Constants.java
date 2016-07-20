
package com.android.settings.update;

import java.util.regex.Pattern;

public class Constants {
    public static final String TAG = "Update";

    public static final boolean DBG = true;

    /**
     * Assume all version are like: v2.3.0.15756
     */
    public static final Pattern LEGAL_KEY_PATTERN = Pattern
            .compile(".[0-9]{1,9}\\.[0-9]{1,9}\\.[0-9]{1,9}\\.[0-9]{1,9}");

    /**
     * Uploading server address.
     */
    public static final String DEFAULT_SERVER_URL = "http://172.23.67.78:8013/TMS/interface/clientService.jsp";

    /**
     * Recovery command file path.
     */
    public static final String RECOVERY_DIR = "/cache/recovery";

    public static final String COMMAND_FILE_NAME = "command";

    /**
     * SharedPreference name.
     */
    public static final String SHARED_PREFERENCE_NAME = "last_update_info";

    /**
     * Last version info.
     */
    public static final String PREFERENCE_LAST_PUB_TIME = "last_pub_time";

    public static final String PREFERENCE_LAST_UDS = "last_uds";

    public static final String PREFERENCE_LAST_MD5 = "last_md5";

    public static final String PREFERENCE_LAST_VER = "last_ver";

    public static final String PREFERENCE_LAST_SIZE = "last_size";

    public static final String PREFERENCE_LAST_DOWNLOAD_ID = "last_download_id";

    public static final String PREFERENCE_LAST_SN_ID = "last_sn_id";

    public static final String PREFERENCE_LAST_FILE_PATH = "last_file_path";

    public static final String PREFERENCE_LAST_SN_FILE_PATH = "last_sn_file_path";

    public static final String PREFERENCE_LAST_PACKAGE_TYPE = "last_package_type";

    public static final String PREFERENCE_LAST_ENGINE = "last_engine";

    /**
     * Booting status.
     */
    public static final String PREFERENCE_BOOT_COMPLETED = "is_boot_completed";

    /**
     * Default SharedPreference values.
     */
    public static final String INVALID_STRING = "invalid";

    /**
     * Enum version status.
     */
    public static final int VERSION_STATUS_INIT = -1;

    public static final int VERSION_STATUS_CURRENT = 0;

    public static final int VERSION_STATUS_NETWORK = 1;

    public static final int VERSION_STATUS_FAILED = 2;

    public static final int VERSION_STATUS_LAST = 3;

    public static final int VERSION_STORAGE_FAIL = 4;

    /**
     * Action to get version info.
     */
    public static final String ACTION_VERSION_INFO_AVAILABLE = "scifly.intent.action.VERSION_INFO_AVAILABLE";

    public static final String EXTRA_VERSION_INFO = "version_info";

    /**
     * Action to start UpdateService.
     */
    public static final String ACTION_UPDATE_INCREMENTAL_ALL = "scifly.intent.action.UPDATE_INCREMENTAL_ALL";

    public static final String EXTRA_INCREMENTAL_ALL = "extra_incremental_all";

    public static final String EXTRA_INCREMENTAL = "0";

    public static final String EXTRA_ALL = "1";

    /**
     * Enum messages.
     */
    public static final int MSG_SHOW_COUNT_DIALOG = 300;

    public static final int MSG_DIALOG_OK = 301;

    public static final int MSG_DIALOG_CANCEL = 302;

    public static final int MSG_CPE_VERSION_INFO_DIALOG = 303;

    public static final int MSG_CPE_DIALOG_OK = 304;

    public static final int MSG_CPE_DIALOG_CANCEL = 305;

    public static final int MSG_VERSION_INFO_AVAILABLE = 306;

    public static final int MSG_PROGRESS_AVAILABLE = 307;

    public static final int MSG_CONTINUE_DOWNLOAD = 308;

    public static final int MSG_DELAY_DOWNLOAD = 309;

    public static final int MSG_DELAY_DELETE_SN_TASK = 310;

    public static final int MSG_STORAGE_EXTERNAL_FULL = 311;

    /**
     * Dialog params.
     */
    public static long COUNTING_DIALOG_TIME = 30 * 1000;

    public static long COUNTING_DIALOG_INTERVAL_TIME = 1000;

    /**
     * Enum triggering updating source.
     */
    public static final int SOURCE_ACTIVITY = 100;

    public static final int SOURCE_BOOT = 101;

    public static final int SOURCE_CPE = 102;

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
     * Invalid version String display in Activity
     */
    public static final String VERSION_INVALID = "---------------";

}
