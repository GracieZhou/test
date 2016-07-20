
package com.eostek.scifly.messagecenter.datacenter;

import java.util.List;

import com.eostek.scifly.messagecenter.model.MessageSender;

public interface IStatusListener {
    void onAddResult(boolean isSucceed,String msg);

    void onDelResult(boolean isSucceed,String msg);

    void getBlackList(List<MessageSender> senders);
}
