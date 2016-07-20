
package scifly.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.InputStreamReader;

import android.util.Log;

/**
 * RecoverySystemExtra contains methods for interacting with the Android
 * recovery system (the separate partition that can be used to install system
 * updates, wipe user data, etc.)
 * 
 * @author frank.zhang
 * @since API 2.0
 */
public class RecoverySystemExtra {
    private static final String TAG = "RecoverySystemExtra";

    /** Used to communicate with recovery. See bootable/recovery/recovery.c. */
    private static File RECOVERY_DIR = new File("/cache/recovery");

    private static File COMMAND_FILE = new File(RECOVERY_DIR, "command");

    private static File LOG_FILE = new File(RECOVERY_DIR, "log");

    private static String OTA_INSTALL_LOG = "/cache/recovery/last_install";

    public static final int UPGRADE_UNKONW = -1;

    /**UPGRADE_FAILED UPGRADE_SUCCESS used to determinate to show the dialog **/
    public static final int UPGRADE_FAILED = 0;

    public static final int UPGRADE_SUCCESS = 1;

    /**LAST_UPGRADE_FAILED LAST_UPGRADE_SUCCESS used to determinate to download OTA package **/
    public static final int LAST_UPGRADE_FAILED = 2;

    public static final int LAST_UPGRADE_SUCCESS = 3;

    /**
     * This static provided the method to implements a non-forced OTA update. It
     * co-work with the VOLD to relized . when power on the VOLD will check
     * COMMAND_FILE and it's content, when the content matches the trigger , the
     * main system will rebooting to recovery mode.
     */
    public static void nonForcedOTAUpdate() throws IOException {
        RECOVERY_DIR.mkdirs(); // In case we need it
        COMMAND_FILE.delete(); // In case it's not writable
        LOG_FILE.delete();

        String arg = "--update_package=/cache/update_signed.zip non-forced";
        FileWriter command = new FileWriter(COMMAND_FILE);
        try {
            command.write(arg);
            command.write("\n");
        } finally {
            command.close();
        }
        Log.d(TAG, "Set non-forced OTA update done !");
    }

    public static int getSystemUpgradeState() {
        int ret = UPGRADE_UNKONW;
        int result = UPGRADE_UNKONW;
        String oneLine = null;
        int retries = 3;
        try {
            oneLine = getLine(OTA_INSTALL_LOG);
            if (oneLine == null || oneLine.isEmpty()) {
                Log.e(TAG, "Can't read " + OTA_INSTALL_LOG);
            } else {
                Log.d(TAG, oneLine);
                result = Integer.parseInt(oneLine.substring(0,1));
                switch (result) {
                    case UPGRADE_FAILED: {
                        ret = UPGRADE_FAILED;
                        while(retries > 0) {
                            if(writeLine(OTA_INSTALL_LOG, "2 ") == true) {
                                retries = 0;
                                break;
                            } else {
                                retries--;
                            }
                        }
                    }
                        break;
                    case UPGRADE_SUCCESS: {
                        ret = UPGRADE_SUCCESS;
                        while(retries > 0) {
                            if(writeLine(OTA_INSTALL_LOG, "3 ") == true) {
                                retries = 0;
                                break;
                            } else {
                                retries--;
                            }
                        }
                    }
                        break;
                    case LAST_UPGRADE_FAILED: {
                        ret = LAST_UPGRADE_FAILED;
                    }
                        break;
                    case LAST_UPGRADE_SUCCESS: {
                        ret = LAST_UPGRADE_SUCCESS;
                    }
                        break;
                    default: {
                        ret = UPGRADE_UNKONW;
                    }
                        break;
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return ret;
    }

    private static String getLine(String filePath) {
        BufferedReader fr = null;
        String oneLine = null;
        try {
            fr = new BufferedReader(new InputStreamReader(new FileInputStream(OTA_INSTALL_LOG)));
            oneLine = fr.readLine();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }

        return oneLine;
    }

    private static boolean writeLine(String filePath, String content) {
        boolean flag = true;
        FileWriter command = null;
        try {
            command = new FileWriter(new File(filePath));
            command.write(content);
            command.write("\n");
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            flag = false;
        } finally {
            if (command != null) {
                try {
                    command.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    flag = false;
                }
            }
        }

        return flag;
    }

    private RecoverySystemExtra() {
    } // Do not instantiate
}
