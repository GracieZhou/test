
package com.eostek.isynergy.setmeup.config.restore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.config.IfService;
import com.eostek.isynergy.setmeup.config.ServiceManager;
import com.eostek.isynergy.setmeup.service.WifiService;

public class RestoreFactoryService implements IfService {
    private final String TAG = "RestoreFactoryService";

    private static File RECOVERY_DIR = new File("/cache/recovery");

    private static File COMMAND_FILE = new File(RECOVERY_DIR, "command");

    private static File LOG_FILE = new File(RECOVERY_DIR, "log");

    private Context context;

    private RestoreFactoryService factoryService;

    public RestoreFactoryService(Context context) {
        Log.d(TAG, "RestoreFactoryService construct!");
        this.context = context;
    }

    @Override
    public int doAction(ACTION_TYPE type, String paras) {
        Log.d(TAG, "paras = " + paras);
        if (null == paras || "".equals(paras) || "0,".equals(paras)) {
            Log.d(TAG, "RESTORE SYSTEM START");

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            pm.reboot("recovery");
        } else {
            Log.d(TAG, "FACTORY RESET START");
            try {
                bootCommand(context.getApplicationContext(), "--wipe_data\n--locale=" + Locale.getDefault().toString());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        int ret = Constants.ErrorCode.INVALID_PARA.getValue();

        if (paras != null && paras.length() > 0) {
            String paraArray[] = paras.split(Constants.SET_ME_UP_PARA_SPLIT);
            if (paraArray.length == 2) {
                restoringFactory(Constants.ACTION_TYPE.RESTORE_FACTORY, paraArray[1]);
            }
        }

        return ret;
    }

    private static void bootCommand(Context context, String arg) throws IOException {
        RECOVERY_DIR.mkdirs(); // In case we need it
        COMMAND_FILE.delete(); // In case it's not writable
        LOG_FILE.delete();

        FileWriter command = new FileWriter(COMMAND_FILE);
        try {
            command.write(arg);
            command.write("\n");
        } finally {
            command.close();
        }

        // Having written the command file, go ahead and reboot
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");

        throw new IOException("Reboot failed (no permissions?)");
    }

    private int restoringFactory(ACTION_TYPE type, String timeZone) {
        int ret = Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
        Intent intent = new Intent(context, WifiService.class);
        intent.putExtra(Constants.NAME_ACTION_TYPE, type.getValue());
        intent.putExtra(Constants.NAME_PARAMETER, timeZone);
        context.startService(intent);
        return ret;
    }
}
