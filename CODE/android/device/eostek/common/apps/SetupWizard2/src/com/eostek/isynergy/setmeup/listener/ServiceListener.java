
package com.eostek.isynergy.setmeup.listener;

public interface ServiceListener {
    int onCallService(int sessionID, int actionType, String content);
}
