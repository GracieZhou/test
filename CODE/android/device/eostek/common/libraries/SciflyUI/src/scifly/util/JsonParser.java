
package scifly.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import libcore.io.IoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

/**
 * Util for parser json file and {@link JSONObject}.
 */
public class JsonParser {

    private static final String TAG = "JsonParser";

    private static final boolean DBG = true;

    /**
     * Parse the JSON file.
     * 
     * @param path path of JSON file.
     * @return {@link JSONObject}.
     */
    public static JSONObject parse(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        if (DBG) {
            Log.d(TAG, "parse " + path);
        }
        File jsonFile = new File(path);
        if (!jsonFile.exists()) {
            Log.e(TAG, "File Not Found. make sure the file is exists.");
            return null;
        }

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile)));
            String data = null;
            while ((data = br.readLine()) != null) {
                sb.append(data);
            }
            if (DBG) {
                Log.d(TAG, "json string" + sb.toString());
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            return null;
        } finally {
            IoUtils.closeQuietly(br);
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(sb.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSONException ", e);
        } finally {
            IoUtils.closeQuietly(br);
        }

        return jsonObject;
    }

}
