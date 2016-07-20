
package com.eostek.sciflyui.voicecontroller.command;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class used to save useful information parsed by the JSon string got from
 * IFlytek semantic open-platform.
 * 
 * @author Mars.Li
 */
public class Command {

    public static final String OPERATION_ANSWER = "ANSWER";

    public static final String OPERATION_LAUNCH = "LAUNCH";

    public static final String OPERATION_QUERY = "QUERY";

    public static final String OPERATION_PLAY = "PLAY";

    public static final String OPERATION_SERVICE_MUSIC = "music";

    public static final String OPERATION_SERVICE_VIDEO = "video";

    public static final String OPERATION_SERVICE_APP = "app";

    public static final String OPERATION_SERVICE_OPENQA = "openQA";

    public static final String OPERATION_DEVICE_CONTROL = "deviceControl";

    public static final String OPERATION_DEVICE_CONTROL_SHUTDWON = "shutDown";

    public static final String OPERATION_DEVICE_CONTROL_VOLUME_MUTE = "volumeMute";

    public static final String OPERATION_DEVICE_CONTROL_VOLUME_PLUS = "volumePlus";

    public static final String OPERATION_DEVICE_CONTROL_VOLUME_MINUS = "volumeMinus";

    public static final String OPERATION_DEVICE_CONTROL_VOLUME_RECOVERY = "volumeRecovery";

    public static final String OPERATION_DEVICE_CONTROL_KEY_BACK = "keyBack";

    public static final String OPERATION_DEVICE_CONTROL_KEY_HOME = "keyHome";

    public static final String OPERATION_DEVICE_CONTROL_KEY_ENTER = "keyEnter";

    public static final String OPERATION_DEVICE_CONTROL_CHANNEL_PLUS = "channelPlus";

    public static final String OPERATION_DEVICE_CONTROL_CHANNEL_MINUS = "channelMinus";

    public static final String OPERATION_LAUNCH_APP = "launchApp";

    public static final int RESULT_CODE_UNKNOWN = 4;

    public static final int TYPE_DEFAULT = 0;

    /**
     * Launch application command type.
     */
    public static final int TYPE_LAUNCH_APP = 1;

    /**
     * Query application and try to launch application if exist in device.
     */
    public static final int TYPE_QUERY_APP = 2;

    /**
     * Device control command type.
     */
    public static final int TYPE_DEVICE_CONTROL = 3;

    /**
     * Command type.
     */
    private int mCommandType;

    private String mOriginalJsonString;

    private String mMoreResultJsonString;

    private int mResultCode;

    private String mText;

    private String mService;

    private String mOperation;

    private String mApplicationName;

    private String mApplicationPackageName;

    private String mApplicationClassName;

    private String mAnswerType;

    private String mAnswerText;

    private String mCustomOperation;

    private String mCustomCommand;

    public String getOriginalJsonString() {
        return mOriginalJsonString;
    }

    public void setOriginalJsonString(String mOriginalJsonString) {
        this.mOriginalJsonString = mOriginalJsonString;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int mResultCode) {
        this.mResultCode = mResultCode;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getService() {
        return mService;
    }

    public void setService(String mService) {
        this.mService = mService;
    }

    public String getOperation() {
        return mOperation;
    }

    public void setOperation(String mOperation) {
        this.mOperation = mOperation;
    }

    public String getApplicationName() {
        return mApplicationName;
    }

    public void setApplicationName(String mApplicationName) {
        this.mApplicationName = mApplicationName;
    }

    public String getAnswerType() {
        return mAnswerType;
    }

    public void setAnswerType(String mAnswerType) {
        this.mAnswerType = mAnswerType;
    }

    public String getAnswerText() {
        return mAnswerText;
    }

    public void setAnswerText(String mAnswerText) {
        this.mAnswerText = mAnswerText;
    }

    public String getCustomOperation() {
        return mCustomOperation;
    }

    public void setCustomOperation(String mCustomOperationType) {
        this.mCustomOperation = mCustomOperationType;
    }

    public String getCustomCommand() {
        return mCustomCommand;
    }

    public void setCustomCommand(String mCustomOperationCommand) {
        this.mCustomCommand = mCustomOperationCommand;
    }

    public String getApplicationPackageName() {
        return mApplicationPackageName;
    }

    public void setApplicationPackageName(String mApplicationPackageName) {
        this.mApplicationPackageName = mApplicationPackageName;
    }

    public int getCommandType() {
        return mCommandType;
    }

    public void setCommandType(int mCommandType) {
        this.mCommandType = mCommandType;
    }

    public String getApplicationClassName() {
        return mApplicationClassName;
    }

    public void setApplicationClassName(String mApplicationClassName) {
        this.mApplicationClassName = mApplicationClassName;
    }

    public String getMoreResultJsonString() {
        return mMoreResultJsonString;
    }

    public void setMoreResultJsonString(String mMoreResultJsonString) {
        this.mMoreResultJsonString = mMoreResultJsonString;
    }

    @Override
    public String toString() {
        JSONObject jsonObj = new JSONObject();

        try {
            if (mOperation != null) {
                jsonObj.put("operation", mOperation);
            }

            if (mApplicationName != null) {
                jsonObj.put("application", mApplicationName);
            }

            if (mCustomCommand != null) {
                jsonObj.put("deviceCommand", mCustomCommand);
            }

            if (mText != null) {
                jsonObj.put("text", mText);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }
}
