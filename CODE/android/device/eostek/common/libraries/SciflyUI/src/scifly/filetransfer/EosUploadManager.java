
package scifly.filetransfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author Psso.Song
 * @since Framework-ext API 2.0. This upload Manager is an EosTek component that
 *        handles long-running HTTP uploads. Clients may request that a URL to
 *        be uploaded to a particular server. The upload manager will conduct
 *        the upload in the background, taking care of HTTP interactions. Note
 *        that the application must have the
 *        {@link android.Manifest.permission#INTERNET} permission to use this
 *        class.
 */

public class EosUploadManager {
    public static final String TAG = "EosUploadManager";

    public static final int UPLOAD_METHOD_HTTP_POST = 0;

    public static final int UPLOAD_METHOD_HTTP_GET = 1;

    public static final int UPLOAD_METHOD_FTP = 2;

    public static final String PARAM_IFID = "ifid";

    public static final String PARAM_DEVICE_NAME = "devName";

    public static final String PARAM_USERID = "userId";

    public static final String PARAM_DEVCODE = "devCode";

    public static final String PARAM_UPDTIME = "updTime";

    public static final String PARAM_IFVER = "ifVer";

    public static final String PARAM_MAC = "mac";

    public static final String PARAM_VERSION = "ver";

    public static final String PARAM_TEXT = "text";

    public static final String PARAM_BB = "bb";

    private static final String PARAM_DEVICE_CODE = "devCode";

    public static final String PARAM_TTAG = "ttag";

    public static final String PARAM_TYPE = "type";

    public static final String UPLOAD_TYPE_LOG = "log";

    public static final String UPLOAD_TYPE_STATISTICS = "statistics";

    public static final int RESULT_SUCCESSFUL = 0;

    public static final int RESULT_HTTP_FAIL = 1;

    public static final int RESULT_FILE_NOT_FOUND_FAIL = 2;

    public static final int RESULT_NEED_UPDATE = 100;

    private ExecutorService threadPool;

    private static final int THREAD_POOL_SIZE = 3;

    /**
     * Constructor of this class, but do nothing.
     */
    public EosUploadManager() {
        Log.d(TAG, "Initialize ThreadPool...");
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    /**
     * @author Psso.Song This class is a thread that runs the upload in the
     *         background.
     */
    private class UploadThread implements Runnable {
        private EosUploadTask task;

        public UploadThread(EosUploadTask uploadTask) {
            task = uploadTask;
        }

        @Override
        public void run() {
            Log.d(TAG, "UploadThread ID: " + Thread.currentThread().getId());

            switch (task.getMethod()) {
                case UPLOAD_METHOD_HTTP_POST:
                    int result = httpPostUpload();
                    Log.d(TAG, "Upload completed, result: " + result);
                    if (task.getListener() != null) {
                        task.getListener().onUploadCompleted(result);
                    }
                    break;
                case UPLOAD_METHOD_HTTP_GET:
                    Log.d(TAG, "Upload Method HTTP_GET");
                    break;
                case UPLOAD_METHOD_FTP:
                    Log.d(TAG, "Upload Method FTP");
                    break;
                default:
                    Log.d(TAG, "Upload Method Unknown");
            }
        }

        private int httpPostUpload() {
            JSONObject json = getJsonFrom(task);
            if (json == null) {
                Log.e(TAG, "ERROR, cancel upload!");
                return RESULT_FILE_NOT_FOUND_FAIL;
            }
            try {
                URL url = new URL(task.getUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");

                conn.setRequestMethod("POST");
                if (isTaskStatistics(task)) {
                    conn.setRequestProperty("Content-Encoding", "gzip");
                } else {
                    conn.addRequestProperty("Ttag", task.getParam().get(PARAM_TTAG));
                    conn.addRequestProperty("Tcip", getTcip(task, json));
                }
                conn.setDoOutput(true);
                conn.setDoInput(true);

                Writer writer = null;
                if (isTaskStatistics(task)) {
                    writer = new OutputStreamWriter(new GZIPOutputStream(conn.getOutputStream()), "utf-8");
                } else {
                    writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                }
                int length = json.toString().length();
                Log.d(TAG, "JSON size: " + length);
                Log.d(TAG,
                        "+++++++JSON DATA begin(first 300 bytes)++++++\n"
                                + json.toString().substring(0, (length > 300 ? 300 : length))
                                + "\n++++++++++++++++JSON DATA end++++++++++++++++");
                writer.write(json.toString());
                writer.flush();
                writer.close();

                Log.d(TAG, "Response Code = " + conn.getResponseCode());
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                String result = buffer.toString();
                br.close();
                is.close();
                Log.d(TAG, "Response InputStream = " + result);
                JSONObject res = new JSONObject(result);
                if (res.getInt("err") == 0) {
                    return RESULT_SUCCESSFUL;
                } else if (res.getInt("err") == 100) {
                    return RESULT_NEED_UPDATE;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return RESULT_HTTP_FAIL;
        }
    }

    private String getTcip(EosUploadTask task, JSONObject json) {
        String urlParam = new StringBuffer(task.getUrl()).append(json.toString()).toString();
        String bb = task.getParam().get(PARAM_BB);
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        String key = currentTime.toString();
        String time = currentTime.toString();
        String deviceCode = task.getParam().get(PARAM_DEVICE_CODE);
        StringBuffer md5 = new StringBuffer(bb);
        md5.append("_");
        md5.append(Util.calcMD5Lim2(new StringBuffer(bb).append(urlParam).append(key).append(time).toString()));
        md5.append("_");
        md5.append(time);
        md5.append("_");
        md5.append(Util.calcMD5Lim2(new StringBuffer(bb).append(urlParam).append(deviceCode).append(time).toString()));
        return md5.toString();
    }

    private JSONObject getJsonFrom(EosUploadTask task) {
        JSONObject json = new JSONObject();
        try {
            json.put(PARAM_IFID, task.getParam().get(PARAM_IFID));
            json.put(PARAM_DEVICE_NAME, task.getParam().get(PARAM_DEVICE_NAME));
            json.put(PARAM_MAC, task.getParam().get(PARAM_MAC));
            json.put(PARAM_VERSION, task.getParam().get(PARAM_VERSION));
            if (isTaskStatistics(task)) {
                // Trying to upload a Statistics
                json.put(PARAM_UPDTIME, task.getParam().get(PARAM_UPDTIME));
                json.put(PARAM_IFVER, task.getParam().get(PARAM_IFVER));
                json.put(PARAM_USERID, task.getParam().get(PARAM_USERID));
                json.put(PARAM_DEVCODE, task.getParam().get(PARAM_DEVCODE));

                String obj = task.getPath();
                if (!TextUtils.isEmpty(obj)) {
                    json.put("log", new JSONArray(obj));
                }
            } else {
                // Trying to upload a File.
                json.put(PARAM_TEXT, task.getParam().get(PARAM_TEXT));

                String obj = Util.fileToString(task.getPath());
                if (!TextUtils.isEmpty(obj)) {
                    json.put("log", obj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private final boolean isTaskStatistics(EosUploadTask task) {
        if (task.getParam().get(PARAM_TYPE) != null
                && task.getParam().get(PARAM_TYPE).equalsIgnoreCase(UPLOAD_TYPE_STATISTICS)) {
            return true;
        }
        return false;
    }

    /**
     * @param task {@link EosUploadTask}
     * @return result True if add task successfully, otherwise False.
     */
    public final boolean addTask(EosUploadTask task) {
        if (task == null) {
            Log.e(TAG, "ERROR: Upload task NOT INITIALIZED");
            return false;
        } else if (task.getMethod() < UPLOAD_METHOD_HTTP_POST || task.getMethod() > UPLOAD_METHOD_FTP) {
            Log.e(TAG, "ERROR: Upload method NOT SET");
            return false;
        } else if (task.getPath() == null || task.getPath().isEmpty()) {
            Log.e(TAG, "ERROR: Upload path NOT SET");
            return false;
        } else if (task.getUrl() == null || task.getUrl().isEmpty()) {
            Log.e(TAG, "ERROR: Upload url NOT SET");
            return false;
        } else if (task.getParam() == null) {
            Log.e(TAG, "ERROR: Upload params NOT SET");
            return false;
        }
        threadPool.submit(new UploadThread(task));
        return true;
    }

}
