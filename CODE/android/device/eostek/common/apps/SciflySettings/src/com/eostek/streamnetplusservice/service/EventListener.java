
package com.eostek.streamnetplusservice.service;

import android.os.RemoteException;

public class EventListener extends IEventListener.Stub {

    @Override
    public void OnInfo(int what, int detail, String extra) throws RemoteException {

    }

    @Override
    public void OnError(int code, String detail) throws RemoteException {

    }
}
