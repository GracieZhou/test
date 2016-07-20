
package scifly.app.blacklist;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.app.common.Commons;
import scifly.device.Device;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

/**
 * @author frankzhang
 */
public class BlacklistUpdater {

    private Context mContext;

    private static final Commons.CommonLog LOG = new Commons.CommonLog(BlacklistUpdater.class.getSimpleName());

    private static final Commons.DeviceInfo DEV_INFO = new Commons.DeviceInfo();

    // -----------------------------
    // init REQUEST_JSON_OBJECT
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
        DEV_INFO.ifid = "AppBlackList";
        DEV_INFO.devName = Device.getDeviceName(mContext);
        DEV_INFO.devCode = Device.getDeviceCode();
        DEV_INFO.mac = devMac;
        DEV_INFO.bbno = Device.getBb();
    }

    /**
     * @return JSON object contains full request info.
     */
    public JSONObject makeJsonObject() {
        LOG.d("makeJsonObject for blacklist .");
        long timeStamp = System.currentTimeMillis();
        String md5sum = Commons.caclMd5(DEV_INFO.toString() + timeStamp + Commons.DIGEST_EXTRA);

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(Commons.KEY_IFID, DEV_INFO.ifid);
            requestJson.put(Commons.KEY_MAC, DEV_INFO.mac);
            requestJson.put(Commons.KEY_DEVNAME, DEV_INFO.devName);
            requestJson.put(Commons.KEY_DEVCODE, DEV_INFO.devCode);
            requestJson.put(Commons.KEY_BBNO, DEV_INFO.bbno);
            requestJson.put(Commons.KEY_STAMP, timeStamp);
            requestJson.put(Commons.KEY_SERIAL, md5sum);
        } catch (JSONException e) {
            LOG.e(e.getMessage());
        }
        LOG.d("reqestJson:" + requestJson.toString());
        return requestJson;
    }

    /**
     * @param ctx context of a process's.
     */
    public BlacklistUpdater(Context ctx) {
        LOG.d("Constructor");
        mContext = ctx;
        init();
    }

    /**
     * @return true if updated from remote server, else false.
     */
    public boolean update() {
        LOG.d("update");
        String response = Commons.getServerResponse(mContext, makeJsonObject(), Commons.CONNECT_TYPE_BLACK_LIST);

        if (TextUtils.isEmpty(response)) {
            LOG.e("server no response.");
            return false;
        }

        ArrayList<Blacklist> blacklists = parse(response);
        if (null != blacklists && blacklists.size() > 0) {
            LOG.d("Have got blacklist from server.");
            resetDB();
            for (Blacklist cell : blacklists) {
                insert(cell);
            }
        }

        return true;
    }

    /**
     * @param response returned by remote server.
     * @return an array list of Blacklists to caller.
     */
    public ArrayList<Blacklist> parse(String response) {
        LOG.d("parse:" + response);
        ArrayList<Blacklist> blacklists = new ArrayList<Blacklist>();

        if (TextUtils.isEmpty(response)) {
            return blacklists;
        }

        try {
            JSONObject respondJson = new JSONObject(response);
            int err = respondJson.getInt(Commons.KEY_ERROR);
            JSONObject body = respondJson.optJSONObject(Commons.KEY_BODY);
            if (0 == err && null != body) {
                String md5sum = body.getString(Commons.KEY_SERIAL);
                String timeStamp = body.getString(Commons.KEY_STAMP);
                JSONArray blacklistArray = body.optJSONArray(Commons.KEY_BLACKLIST);

                if (null != md5sum) {
                    LOG.d("begin parse body.");
                    StringBuilder pkgSb = new StringBuilder();
                    if (blacklistArray != null && blacklistArray.length() > 0) {
                        for (int i = 0; i < blacklistArray.length(); i++) {
                            JSONObject blacklistJson = blacklistArray.optJSONObject(i);
                            String pkg = blacklistJson.getString(Commons.KEY_PKG);
                            String desc = blacklistJson.getString(Commons.KEY_DESC);
                            int factor = blacklistJson.optInt(Commons.KEY_FACTOR, -1);
                            if (!TextUtils.isEmpty(pkg)) {
                                blacklists.add(new Blacklist(pkg, factor, desc));
                                pkgSb.append(pkg);
                            }
                        }
                    }
                    // now check response's md5sum
                    String represent = Commons.caclMd5(pkgSb.toString() + timeStamp + err + Commons.DIGEST_EXTRA);
                    LOG.d("imd5sum:" + represent);
                    if (!md5sum.equalsIgnoreCase(represent)) {
                        LOG.e("md5sum dosen't match .");
                        return null;
                    }

                } else {
                    LOG.e("get md5sum failed.");
                }
            } else {
                LOG.e("parse json failed.");
            }
        } catch (JSONException e) {
            LOG.e(e.getMessage());
        }

        return blacklists;
    }

    // -------------------------------------------------------
    // database related
    /**
     * @param cell to insert into database.
     */
    private void insert(Blacklist cell) {
        ContentValues values = new ContentValues();
        values.put(Commons.COLUMN_PKG, cell.pkg);
        values.put(Commons.COLUMN_FACTOR, cell.factor);
        values.put(Commons.COLUMN_DESC, cell.desc);
        values.put(Commons.COLUMN_EXTRA, String.valueOf(1));
        mContext.getContentResolver().insert(Commons.SECURITY_TABLE_URI, values);
    }

    /**
     * Clean database's all records.
     */
    public void resetDB() {
        int rows = mContext.getContentResolver().delete(Commons.SECURITY_TABLE_URI, "extra=?", new String[] {
            String.valueOf(1),
        });
        LOG.i("total delete " + rows + " rows");
    }
}
