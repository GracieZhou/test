
package com.eostek.sciflyui.voicecontroller.command;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public class JsonParser {

    private static final String TAG = "JsonParser";

    private static final String COM_IMUSIC_KALAOK2_WELCOME_ACTIVITY = "com.imusic.kalaok2.WelcomeActivity";

    private static final String COM_IMUSIC_KALAOK2 = "com.imusic.kalaok2";

    /**
     * parse result JSON string
     * 
     * @param jsonString
     * @return return voice command to dispatch.
     */
    public Command parseResult(String jsonString) {

        Command command = new Command();

        try {
            JSONTokener jsonParser = new JSONTokener(jsonString);
            JSONObject result = (JSONObject) jsonParser.nextValue();
            // 语义识别返回的JSON字符串有很多种结构,但是一定包含以下两个"名称/值对"
            // {"text":"这是一段语音","rc":4}
            command.setResultCode(result.getInt("rc"));
            command.setText(result.getString("text"));
            // 如果rc结果是4,则说明语义识别失败,只有简单的语音识别结果返回,我们直接对这个结果进行处理.
            if (Command.RESULT_CODE_UNKNOWN == command.getResultCode()) {
                return command;
            }

            command.setService(result.getString("service"));
            command.setOperation(result.getString("operation"));
            if (Command.OPERATION_SERVICE_APP.equals(command.getService())) {
                // operation类型为app,代表跟"应用"相关的语义场景.

                // {"semantic":
                // {
                // "slots":{"name":"消息中心"}
                // },
                // "rc":0,
                // "operation":"LAUNCH",
                // "service":"app",
                // "moreResults":[
                // {
                // "rc":0,
                // "answer":{"text":"{\"operation\":\"launchApp\",\"package\":\"com.eostek.scifly.messagecenter\",\"className\":\"com.eostek.scifly.messagecenter.MainActivity\"}","type":"T"},
                // "service":"openQA",
                // "text":"打开消息中心",
                // "operation":"ANSWER"
                // }
                // ],
                // "text":"打开消息中心"}

                JSONObject semantic = result.getJSONObject("semantic");
                JSONObject slots = semantic.getJSONObject("slots");

                // "name" 名称/值对就是代表我们想要打开的应用的名称.
                command.setApplicationName(slots.getString("name"));

                // commandType代表语音命令的类型
                command.setCommandType(Command.TYPE_LAUNCH_APP);

                // moreResults不为空,代表返回的结果是我们在开发者平台定制的结果和官方返回的结果的混合,我们优先处理定制的结果,
                // 我们定义的结果也是JSON字符串,需要提取出来再进行解析.
                if (result.isNull("moreResults")) {
                    Log.i(TAG, "try to launch :" + command.getApplicationName());
                    command.setCommandType(Command.TYPE_QUERY_APP);
                } else {
                    JSONArray moreResults = result.getJSONArray("moreResults");

                    int count = moreResults.length();
                    for (int i = 0; i < count; i++) {

                        command.setMoreResultJsonString(moreResults.getString(i));

                        JSONTokener moreResultTokener = new JSONTokener(command.getMoreResultJsonString());
                        JSONObject moreResultObject = (JSONObject) moreResultTokener.nextValue();

                        // moreResults是一个数组,我们找其中有answer名称/值对的元素,代表我们定制的结果.
                        if (moreResultObject.isNull("answer")) {
                            continue;
                        }

                        Log.i(TAG, "moreResults:: " + moreResults.getString(i));
                        // 处理定制的结果
                        parseOpenQA(command, moreResultObject);
                    }
                }

            } else if (Command.OPERATION_SERVICE_MUSIC.equals(command.getService())) {

                // operation类型为music,代表跟"音乐"相关的语义场景,我们直接进入"爱音乐"应用.

                // {
                // "semantic":{
                // "slots":{"song":"东风破"}
                // },
                // "rc":0,
                // "operation":"PLAY",
                // "service":"music",
                // "text":"我要听东风破"
                // }

                parseMusic(command);
            } else if (Command.OPERATION_SERVICE_VIDEO.equals(command.getService())) {

                // operation类型为video,代表跟"视频"相关的语义场景,有两种可能情况.
                // 如果解析出了电影/电视剧名,则启动赛飞视频的搜索界面搜索该电影/电视剧
                // {
                // "semantic":{
                // "slots":{"keywords":"功夫熊猫"}
                // },
                // "rc":0,
                // "operation":"PLAY",
                // "service":"video",
                // "text":"我要看功夫熊猫"
                // }

                // 如果解析出了视频的种类,则启动赛飞视频的搜索界面搜索该类型视频.
                // {
                // "semantic":{
                // "slots":{"videoCategory":"电视剧"}
                // },
                // "rc":0,
                // "operation":"QUERY",
                // "service":"video",
                // "text":"电视剧"
                // }
                parseVideo(command, result);
            } else if (Command.OPERATION_SERVICE_OPENQA.equals(command.getService())) {
                // operation类型为openQA,代表自定义问答语义场景,是我们在开发者平台自定义结果的返回.
                // {
                // "rc":0,
                // "operation":"ANSWER",
                // "service":"openQA",
                // "answer":{
                // "type":"T",
                // "text":"{\"operation\":\"launchApp\",\"package\":\"com.eostek.history\",\"className\":\"com.eostek.history.MainActivity\"}"
                // },
                // "text":"启动历史记录"
                // }

                // {
                // "rc":0,
                // "operation":"ANSWER",
                // "service":"openQA",
                // "answer":{
                // "type":"T",
                // "text":"{\"operation\":\"deviceControl\",\"command\":\"volumeMinus\"}"
                // },
                // "text":"音量减小"
                // }
                parseOpenQA(command, result);
            }

        } catch (JSONException ex) {
            // 异常处理代码
            ex.printStackTrace();
        }

        return command;
    }

    private void parseMusic(Command command) {
        // 设置命令类型,程序包名类名.
        command.setApplicationPackageName(COM_IMUSIC_KALAOK2);
        command.setApplicationClassName(COM_IMUSIC_KALAOK2_WELCOME_ACTIVITY);
        command.setCommandType(Command.TYPE_LAUNCH_APP);
    }

    private void parseVideo(Command command, JSONObject result) throws JSONException {

        command.setCommandType(Command.TYPE_QUERY_APP);

        JSONObject semantic = result.getJSONObject("semantic");
        JSONObject slots = semantic.getJSONObject("slots");
        if (!slots.isNull("keywords")) {
            // 提取语义识别的关于视频的关键词
            command.setText(slots.getString("keywords"));
        } else if (!slots.isNull("videoCategory")) {
            // 提取语义识别的视频种类
            command.setText(slots.getString("videoCategory"));
        }
    }

    private void parseOpenQA(Command command, JSONObject result) throws JSONException {
        /** set default type */
        command.setCommandType(Command.TYPE_QUERY_APP);

        JSONObject answer = result.getJSONObject("answer");
        command.setAnswerType(answer.getString("type"));
        command.setAnswerText(answer.getString("text"));

        JSONTokener customJsonParser = new JSONTokener(command.getAnswerText());
        JSONObject customResult = (JSONObject) customJsonParser.nextValue();

        command.setCustomOperation(customResult.getString("operation"));
        if (Command.OPERATION_DEVICE_CONTROL.equals(command.getCustomOperation())) {
            // 定制结果类型为设备控制类型
            command.setCustomCommand(customResult.getString("command"));
            command.setCommandType(Command.TYPE_DEVICE_CONTROL);
            Log.i(TAG, "try to operate " + command.getCustomOperation() + " " + command.getCustomCommand());
        } else if (Command.OPERATION_LAUNCH_APP.equals(command.getCustomOperation())) {
            // 定制结果类型为启动应用类型
            command.setApplicationPackageName(customResult.getString("package"));
            command.setApplicationClassName(customResult.getString("className"));
            command.setCommandType(Command.TYPE_LAUNCH_APP);
            Log.i(TAG, "try to operate " + command.getCustomOperation() + " " + command.getApplicationPackageName());
        }
    }
}
