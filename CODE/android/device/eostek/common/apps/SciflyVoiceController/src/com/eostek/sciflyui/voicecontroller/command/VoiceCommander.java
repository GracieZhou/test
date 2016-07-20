
package com.eostek.sciflyui.voicecontroller.command;

import android.content.Context;

public class VoiceCommander {

    private Context mContext;

    private CommandBuilder mCommandBuilder;

    private CommandDispatcher mCommandDispatcher;

    public VoiceCommander(Context context) {

        mContext = context;
        mCommandBuilder = new CommandBuilder(mContext);
        mCommandDispatcher = new CommandDispatcher(mContext);

    }

    public Command processSemanticResult(String jsonString) {

        Command command = mCommandBuilder.getCommand(jsonString);

        mCommandDispatcher.dispatchCommand(command);

        return command;

    }

}
