
package com.eostek.scifly.messagecenter.datacenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Messages;
import scifly.provider.metadata.Msg;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.eostek.scifly.messagecenter.model.MessageSender;
import com.eostek.scifly.messagecenter.util.Constants;

/**
 * data provider of MessageCenter.
 * 
 * @author Charles.Tai & Youpeng.Wan
 * @since 2.0
 * @date 2014-6-12
 */
public class MsgCenterManager {

    private static final String[] PROJECTION_DEFAULT = {
            "_id", "userid", "title", "time", "status", "data", "extra", "category", "reserve"
    };

    private static final String[] PROJECTION_USER = {
            "_id", "userid", "nickname", "thumbnail", "blocked", "source"
    };

    // Temp Code:
    private Map<String, String> mSenderMaps = new HashMap<String, String>();

    // Temp Code:

    private static final String CATEGORY = Messages.CATEGORY_APK + "," + Messages.CATEGORY_EPG + ","
            + Messages.CATEGORY_IMAGE + "," + Messages.CATEGORY_VOICE + "," + Messages.CATEGORY_TEXT + ","
            + Messages.CATEGORY_VIDEO + "," + Messages.CATEGORY_EPG_CACHE + "," + Messages.CATEGORY_URL + ","
            + Messages.CATEGORY_DOCUMENT + "," + Messages.CATEGORY_MUSIC + "," + Messages.CATEGORY_MSG_LIVE;

    private static final String SELECTION_MESSAGE = "status=? and userid=? and category in " + "(" + CATEGORY + ")";

    private static final String SELECTION_MESSAGE_CATEGORY_IMG = "userid=? and category in " + "("
            + Messages.CATEGORY_IMAGE + ")";

    private static final String SELECTION_MESSAGE_CATEGORY = "userid=? and category in " + "(" + CATEGORY + ")";

    private static final String SELECTION_USERID = "userid=?";

    private static final boolean debug = true;

    private static final String TAG = "MsgCenterManager";

    private static Context mContext;

    private static MsgCenterManager dateCenter;

    private boolean mHasUnReadMsgs = false;

    // private MessageSender mSender = new MessageSender();

    private Map<String, MessageSender> mBlackListMap = new HashMap<String, MessageSender>();

    /**
     * Constructor.
     */
    private MsgCenterManager() {
        mSenderMaps.put(Messages.USERID_CPE, Messages.USERID_CPE);
    }

    /**
     * get an instance of MsgCenterManager.
     * 
     * @return an data manager of Message-Center.
     */
    public static MsgCenterManager getInstance(Context context) {
        if (dateCenter == null) {
            dateCenter = new MsgCenterManager();
        }
        mContext = context;
        return dateCenter;
    }

    /**
     * get distinct senders
     * 
     * @return
     */
    public synchronized List<MessageSender> getDistinctSenders() {
        List<MessageSender> senderList = new ArrayList<MessageSender>();

        // List<Msg> msgListTest =
        // SciflyStore.Messages.getDistinctMessages(mContext.getContentResolver());

        return senderList;
    }

    /**
     * get all message senders. FIXME
     * 
     * @return a list of message senders.
     */
    public synchronized List<MessageSender> getAllSenders() {
        Map<String, MessageSender> notUsedSender = new HashMap<String, MessageSender>();
        notUsedSender.putAll(mBlackListMap);

        List<Msg> msgList = new ArrayList<Msg>();
        List<MessageSender> msgSenders = new ArrayList<MessageSender>();
        boolean isCPEAdded = false;

        msgList = SciflyStore.Msgusers.getMsguser(mContext.getContentResolver(), PROJECTION_USER, "_id>=?",
                new String[] {
                    "0"
                });

        for (Msg msg : msgList) {

            if (Messages.USERID_CPE.equals(msg.mUserId)) {
                isCPEAdded = true;
            }

            List<Msg> unreadMsg = getUnReadMsg(msg.mUserId);
            int unReadMsgs = unreadMsg.size();
            int msgCountInDB = getMessageBySenderCategory(msg.mUserId).size();

            if (unReadMsgs > 0) {
                mHasUnReadMsgs = true;
            }

            if (msg.mBlocked == 0 && msgCountInDB == 0) {
                deleteUser(msg.mUserId);
                continue;
            }

            MessageSender sender = new MessageSender();
            sender.setdbId(msg.mId);
            sender.setSenderId(msg.mUserId);
            sender.setName(msg.mUserInfo);
            sender.setImgURL(msg.mImgUrl);
            sender.setDescription(msg.mUserInfo);
            sender.setSource(msg.mSource);
            sender.setReserve(msg.mReserve);
            sender.setUnReadCount(unReadMsgs);

            if (mBlackListMap.isEmpty() || mBlackListMap.get(msg.mUserId) == null) {
                sender.setBlocked(false);
            } else {
                sender.setBlocked(true);
                notUsedSender.remove(msg.mUserId);
            }
            Log.w(TAG, "sender:" + sender.toString());

            msgSenders.add(sender);
        }

        if (!isCPEAdded || msgSenders.size() == 0) {
            msgSenders.add(new MessageSender(Messages.USERID_CPE, "CPE", "", Messages.SOURCE_CPE, ""));
        }
        if (!notUsedSender.isEmpty()) {
            for (String key : notUsedSender.keySet()) {
                MessageSender sd = notUsedSender.get(key);
                sd.setUnReadCount(0);
                Log.e(TAG, "blkLst2Add:" + sd);

                msgSenders.add(sd);
            }
        }

        // try {
        // Log.i(TAG, " size " + msgSenders.get(0).toString());
        // } catch (IndexOutOfBoundsException e) {
        // e.printStackTrace();
        // }
        return msgSenders;
    }

    /**
     * if has unread messages.
     * 
     * @return if true have else not
     */
    public boolean hasUnReadMsg() {
        return mHasUnReadMsgs;
    }

    /**
     * get all messages that send by one sender .
     * 
     * @param senderId identity of a sender.
     * @return a list of messages.
     */
    public List<Msg> getMessageBySender(String senderId) {
        return SciflyStore.Messages.getMessage(mContext.getContentResolver(), senderId);
    }

    /**
     * get all messages that send by one sender and in certain category.
     * 
     * @param senderId identity of a sender.
     * @return a list of messages.
     */
    public List<Msg> getPhotosBySenderCategory(String senderId) {
        return SciflyStore.Messages.getMessage(mContext.getContentResolver(), PROJECTION_DEFAULT,
                SELECTION_MESSAGE_CATEGORY_IMG, new String[] {
                    senderId
                });
        // return new ArrayList<Msg>();
    }

    /**
     * get all messages that send by one sender and in certain category.
     * 
     * @param senderId identity of a sender.
     * @return a list of messages.
     */
    public List<Msg> getMessageBySenderCategory(String senderId) {
        return SciflyStore.Messages.getMessage(mContext.getContentResolver(), PROJECTION_DEFAULT,
                SELECTION_MESSAGE_CATEGORY, new String[] {
                    senderId
                });
        // return new ArrayList<Msg>();
    }

    /**
     * get unread messages .
     * 
     * @param senderId identity of a sender.
     * @return a list of message.
     */
    public List<Msg> getUnReadMsg(String senderId) {
        if (senderId == null || senderId.equals("null")) {
            senderId = "cpe_2";
        }
        return SciflyStore.Messages.getMessage(mContext.getContentResolver(), PROJECTION_DEFAULT, SELECTION_MESSAGE,
                new String[] {
                        String.valueOf(Constants.MESSAGE_UNREAD), senderId
                });
        // return new ArrayList<Msg>();
    }

    /**
     * mark the status of a unread message.
     * 
     * @param msg message that need to be updated.
     * @return ture if update succeed.
     */
    public boolean readMsg(Msg msg) {
        msg.mStatus = Constants.MESSAGE_READ;
        return SciflyStore.Messages.updateMessage(mContext.getContentResolver(), msg);
    }

    /**
     * mark the status of a unread messages by senderId.
     * 
     * @param msgs messages that need to be updated.
     * @param userId
     * @return
     */
    public boolean readMsgsBySender(String userId) {
        ContentValues values = new ContentValues();
        values.put("status", Constants.MESSAGE_READ);
        return SciflyStore.Messages.updateMessage(mContext.getContentResolver(), values, SELECTION_USERID,
                new String[] {
                    userId
                });
    }

    /**
     * delete message from provider of the item select.
     * 
     * @param msg message that need to be deleted.
     * @return true if succeed.
     */
    public boolean delMessage(Msg msg) {
        return SciflyStore.Messages.deleteMessage(mContext.getContentResolver(), msg);
    }

    /**
     * delete all messages that send by one sender.
     * 
     * @param senderId identity of a sender.
     * @return The status of delete Msg.
     */
    public boolean delMessageBySender(String senderId) {
        return SciflyStore.Messages.deleteMessage(mContext.getContentResolver(), SELECTION_USERID, new String[] {
            senderId
        });
    }

    public boolean isInBlackList(String senderId) {
        List<Msg> msgList = SciflyStore.Msgusers.getMsguser(mContext.getContentResolver(), new String[] {
            "blocked"
        }, SELECTION_USERID, new String[] {
            senderId
        });

        if (msgList.size() > 0 && msgList.get(0).mBlocked == 1) {
            return true;
        } else {
            return false;
        }

    }

    // public boolean add2BlackList(String senderId) {
    // Log.i(TAG, "add2BlackList " + senderId);
    // ContentValues values = new ContentValues();
    // values.put("blocked", Constants.SENDER_BLOCKED);
    // return SciflyStore.Msgusers.updateMsguser(mContext.getContentResolver(),
    // values, SELECTION_USERID,
    // new String[] {
    // senderId
    // });
    //
    // }

    // public boolean removeFromBlackList(String senderId) {
    // Log.i(TAG, "removeFromBlackList " + senderId);
    // ContentValues values = new ContentValues();
    // values.put("blocked", Constants.SENDER_UNBLOCKED);
    // return SciflyStore.Msgusers.updateMsguser(mContext.getContentResolver(),
    // values, SELECTION_USERID,
    // new String[] {
    // senderId
    // });
    // }

    private void print(String log) {
        if (debug) {
            Log.i(TAG, ""+log);
        }
    }

    public boolean deleteUser(String senderId) {
        return SciflyStore.Msgusers.deleteMsguser(mContext.getContentResolver(), SELECTION_USERID, new String[] {
            senderId
        });
    }

    /**
     * Blacklist logic:
     */
    static {
        String device = Build.DEVICE;
        if (device.equalsIgnoreCase("Leader")) {
            URL_SERVER = "http://weixin.smarttvshop.tongshuai.com";
        }
        URL_SERVER = "http://isynergy.88popo.com";
    }

    private static final int DEFAULT_TIMEOUT = 10;

    private static String URL_SERVER = "http://isynergy.88popo.com";

    private final String URL_GET_ALL_BLKLIST = URL_SERVER + "/getBlackList/";

    private final String URL_ADD_BLKLIST = URL_SERVER + "/addInvalidUser";

    private final String URL_DEL_BLKLIST = URL_SERVER + "/deleteInvalidUser";

    private final int REQUEST_METHOD_GET = 1;// GET.

    private final int REQUEST_METHOD_POST = 0;// POST.

    private IStatusListener mStatusListener;

    public void setListener(IStatusListener statusListener) {
        this.mStatusListener = statusListener;

    }

    // public BlackListManager(IStatusListener statusListener) {
    // this.mStatusListener = statusListener;
    //
    // }

    /**
     * @param deviceIdentifier
     * @param listener
     */
    public void getBlackListFromServer() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                String response = getJsonString(URL_GET_ALL_BLKLIST + Device.getBb(), REQUEST_METHOD_GET, "",
                        DEFAULT_TIMEOUT);

                if (mStatusListener != null) {
                    mStatusListener.getBlackList(parseJson4Msgs(response));
                }

            }
        }).start();

    }

    /**
     * @param deviceIdentifier
     * @param sender
     * @param listener
     */
    public void add2BlackList(final MessageSender sender) {
        print("add:" + sender.toString());

        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("userID", sender.getSenderId());
                    json.put("deviceID", Device.getBb());
                    json.put("userNick", sender.getName());
                    json.put("thumbnail", sender.getImgURL());
                    json.put("origin", sender.getSource());
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mStatusListener != null) {
                        mStatusListener.onAddResult(false, "json exception.");
                    }
                }
                String response = getJsonString(URL_ADD_BLKLIST, REQUEST_METHOD_POST, json.toString(), DEFAULT_TIMEOUT);
                print("response:"+response);

                if (mStatusListener != null) {
                    boolean result = parseJson4Result(response);
                    if (result) {
                        sender.setBlocked(true);
                        sender.setUnReadCount(0);
                        mBlackListMap.put(sender.getSenderId(), sender);
                    }
                    mStatusListener.onAddResult(result, "");
                }

            }
        }).start();

    }

    public void deleteFromBlackList(final MessageSender sender) {
        print("del:" + sender.toString());

        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("userID", sender.getSenderId());
                    json.put("deviceID", Device.getBb());
                    // json.put("userNick", sender.getName());
                    // json.put("thumbnail", sender.getImgURL());
                    // json.put("origin", sender.getSource());
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mStatusListener != null) {
                        mStatusListener.onDelResult(false, "");
                    }
                }
                String response = getJsonString(URL_DEL_BLKLIST, REQUEST_METHOD_POST, json.toString(), DEFAULT_TIMEOUT);
                print("response"+response);
                if (mStatusListener != null) {
                    boolean result = parseJson4Result(response);
                    if (result) {
                        sender.setBlocked(false);
                        mBlackListMap.remove(sender.getSenderId());
                    }

                    mStatusListener.onDelResult(result, "");
                }

            }
        }).start();

    }

    /**
     * @param json jsonstring
     * @param timeout time
     * @param requestUrl
     * @return String
     */
    public String getJsonString(String requestUrl, int requestMethod, String json, int timeout) {
        timeout = timeout < 0 ? DEFAULT_TIMEOUT : timeout;
        StringBuffer buffer = new StringBuffer();
        HttpURLConnection conn = null;
        Writer writer = null;
        BufferedReader reader = null;
        try {

            URL url = new URL(requestUrl);
            Log.i("",requestUrl+"_ifc");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/json; charset=UTF-8");

            if (requestMethod == REQUEST_METHOD_GET) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
            }
            conn.setConnectTimeout(timeout * 1000);
            conn.setReadTimeout(timeout * 1000);
            conn.setDoOutput(true);

            Log.d(TAG, "getJsonString.connect:" + json + "--;" + timeout);
            conn.connect();

            if (requestMethod != 1) {
                writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                writer.write(json);
                writer.flush();
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\r\n");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getClass().getName() + ": " + e.getMessage(), e);
            e.printStackTrace();
            return null;
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return buffer.toString();
    }

    private boolean parseJson4Result(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return false;
        }

        try {
            JSONObject responseJson = new JSONObject(jsonString);
            if (responseJson.getInt("err") != 0) {
                return false;
            }

            String msg = responseJson.getString("msg");
            print("msg:" + msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private List<MessageSender> parseJson4Msgs(String jsonString) {
        List<MessageSender> senders = new ArrayList<MessageSender>();
        if (TextUtils.isEmpty(jsonString)) {
            return senders;
        }

        mBlackListMap.clear();
        mBlackListMap = new HashMap<String, MessageSender>();

        try {
            JSONObject responseJson = new JSONObject(jsonString);
            if (responseJson.getInt("err") != 0) {
                return senders;
            }

            JSONArray sendersJson = responseJson.getJSONArray("bd");
            int length = sendersJson.length();

            for (int i = 0; i < length; i++) {
                MessageSender sender = new MessageSender();
                JSONObject senderJson = (JSONObject) sendersJson.get(i);
                sender.setSenderId(senderJson.getString("userID"));
                sender.setName(senderJson.getString("userNick"));
                // Log.e(TAG, "userIMG:" + senderJson.getString("thumbnail"));
                sender.setImgURL(senderJson.getString("thumbnail"));
                sender.setSource(senderJson.getInt("origin"));
                sender.setBlocked(true);
                sender.setUnReadCount(0);

                Log.i(TAG, "sender:" + sender);
                mBlackListMap.put(sender.getSenderId(), sender);
                senders.add(sender);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return senders;
    }
}
