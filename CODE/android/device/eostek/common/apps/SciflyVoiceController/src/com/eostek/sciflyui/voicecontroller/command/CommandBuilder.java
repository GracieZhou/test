
package com.eostek.sciflyui.voicecontroller.command;

import android.content.Context;

public class CommandBuilder {

    // private Context mContext;

    private JsonParser mJsonParser;

    public CommandBuilder(Context context) {
//        mContext = context;
        mJsonParser = new JsonParser();
    }

    public Command getCommand(String jsonString) {

        Command command = mJsonParser.parseResult(jsonString);

        return command;
    }

}
