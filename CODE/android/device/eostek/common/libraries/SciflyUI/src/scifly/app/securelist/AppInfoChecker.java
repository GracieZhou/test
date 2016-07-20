
package scifly.app.securelist;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.app.common.Commons;
import scifly.device.Device;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

/**
 * @author frankzhang
 */
public class AppInfoChecker {

    private Context mContext;

    private static final Commons.CommonLog LOG = new Commons.CommonLog(AppInfoChecker.class.getSimpleName());

    private PackageManager mPm;

    private Handler mTargetHandler;

    private static final Commons.DeviceInfo DEV_INFO = new Commons.DeviceInfo();

    private static final int MSG_REQUEST_INFO = 10;

    /**
     * AppCheckHandler use to handle app's security info request.
     * @author frankzhang
     *
     */
    class AppCheckHandler extends Handler {
        private static final int REQUEST_TIMEOUT = 2000;

        public AppCheckHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REQUEST_INFO:
                    synchronized (mObs) {
                        Timer reminderTimer = new Timer();
                        reminderTimer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                if (null != mTargetHandler) {
                                    mTargetHandler.obtainMessage(Commons.MSG_REMINDER_SHOW).sendToTarget();
                                }
                            }
                        }, REQUEST_TIMEOUT);

                        mObs.reset();
                        mObs.response = Commons.getServerResponse(mContext, (JSONObject) msg.obj,
                                Commons.CONNECT_TYPE_SECURE_LIST);
                        LOG.d("response:" + mObs.response);
                        mObs.finished = true;
                        mObs.notifyAll();

                        reminderTimer.cancel();
                        if (null != mTargetHandler) {
                            mTargetHandler.obtainMessage(Commons.MSG_REMINDER_DISMISS).sendToTarget();
                        }
                    }
                    break;

                default:
                    break;
            }

        }
    }

    /**
     * RequestObserver observe the request answer.
     * @author frankzhang
     *
     */
    class RequestObserver {
        /**
         * Whether request finished.
         */
        public boolean finished;

        /**
         * Server's response.
         */
        public String response;

        public RequestObserver() {
            finished = false;
            response = null;
        }

        public void reset() {
            finished = false;
            response = null;
        }
    }

    private AppCheckHandler mHandler;

    private RequestObserver mObs = new RequestObserver();

    private void init() {
        LOG.d("init");
        String devMac = Device.getHardwareAddress(mContext);
        if (devMac != null && !devMac.equals("")) {
            String[] strArray = devMac.split(":");
            StringBuffer modifiedMac = new StringBuffer();
            for (int i = 0; i < strArray.length; i++) {
                modifiedMac.append(strArray[i]);
            }
            devMac = modifiedMac.toString();
        } else {
            devMac = "000000000000";
        }
        DEV_INFO.ifid = "AppSecureList";
        DEV_INFO.devName = Device.getDeviceName(mContext);
        DEV_INFO.devCode = Device.getDeviceCode();
        DEV_INFO.mac = devMac;
        DEV_INFO.bbno = Device.getBb();
    }

    /**
     * @param ctx context
     * @param targetHandler external handler for receive message to handle.
     */
    public AppInfoChecker(Context ctx, Handler targetHandler) {
        LOG.d("Constructor");
        mContext = ctx;
        mPm = ctx.getPackageManager();
        mTargetHandler = targetHandler;
        init();
        HandlerThread ht = new HandlerThread("check thread", Process.THREAD_PRIORITY_BACKGROUND);
        ht.start();
        mHandler = new AppCheckHandler(ht.getLooper());
    }

    /**
     * @param archiveFilePath the install path.
     * @param info use to be filled in here.
     * @return true if this app in security list.
     */
    public boolean isSafe(String archiveFilePath, String info) {
        LOG.d("isSafe:: install-->" + archiveFilePath);

        // We think that the app is safe when network is disconnected.
        if (!Commons.isNetworkConnected(mContext)) {
            LOG.i("network disconnected .");
            return true;
        }
        PackageInfo pkgInfo = mPm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);

        if (null != pkgInfo) {
            LOG.d("pkg:" + pkgInfo.packageName);
            String response = getAppInfoFromServer(makeJsonObject(pkgInfo.packageName));

            if (TextUtils.isEmpty(response)) {
                LOG.e("Server no response.");
                return false;
            }

            return getResultFromJson(response, info);

        }

        return false;
    }

    private String getAppInfoFromServer(JSONObject requestJson) {
        if (null == requestJson) {
            return null;
        }

        mHandler.obtainMessage(MSG_REQUEST_INFO, requestJson).sendToTarget();
        synchronized (mObs) {
            while (!mObs.finished) {
                LOG.d("waiting ...");
                try {
                    mObs.wait();
                } catch (InterruptedException e) {
                    LOG.e(e.getMessage());
                    return null;
                }
            }
            return mObs.response;
        }
    }

    private JSONObject makeJsonObject(String pkg) {
        LOG.d("makeJsonObject: " + pkg);
        if (TextUtils.isEmpty(pkg)) {
            return null;
        }
        long timeStamp = System.currentTimeMillis();
        String md5sum = Commons.caclMd5(DEV_INFO.toString() + pkg + timeStamp + Commons.DIGEST_EXTRA);
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(Commons.KEY_IFID, DEV_INFO.ifid);
            requestJson.put(Commons.KEY_MAC, DEV_INFO.mac);
            requestJson.put(Commons.KEY_DEVNAME, DEV_INFO.devName);
            requestJson.put(Commons.KEY_DEVCODE, DEV_INFO.devCode);
            requestJson.put(Commons.KEY_BBNO, DEV_INFO.bbno);
            requestJson.put(Commons.KEY_PKG, pkg);
            requestJson.put(Commons.KEY_STAMP, timeStamp);
            requestJson.put(Commons.KEY_SERIAL, md5sum);
        } catch (JSONException e) {
            LOG.e(e.getMessage());
        }
        LOG.d("reqestJson:" + requestJson.toString());
        return requestJson;
    }

    private boolean getResultFromJson(String response, String info) {
        if (TextUtils.isEmpty(response)) {
            return false;
        }

        int err = -1;
        String result = "false";
        String timeStamp = null;
        try {
            JSONObject respondJson = new JSONObject(response);
            err = respondJson.getInt(Commons.KEY_ERROR);
            result = respondJson.getString(Commons.KEY_RESULT);
            timeStamp = respondJson.getString(Commons.KEY_STAMP);
            if (null != info) {
                info = respondJson.getString(Commons.KEY_INFO);
            }
            respondJson = null;
        } catch (JSONException e) {
            LOG.e(e.getMessage());
        }

        LOG.i("err: " + err + "\tresult: " + result + "\t info: " + info);
        String target = Commons.caclMd5("true" + timeStamp + Commons.DIGEST_EXTRA);
        LOG.d("imd5sum:" + target);
        if (0 == err && !TextUtils.isEmpty(target)) {

            if (target.equalsIgnoreCase(result)) {
                return true;
            } else {
                LOG.e("The result was false .");
            }
        }

        return false;
    }

}
