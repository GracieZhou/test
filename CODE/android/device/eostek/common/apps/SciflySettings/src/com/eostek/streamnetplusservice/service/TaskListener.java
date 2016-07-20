
package com.eostek.streamnetplusservice.service;

import android.os.RemoteException;

public class TaskListener extends ITaskListener.Stub {

    @Override
    public void OnInfo(int progress, int speed) throws RemoteException {

    }

    @Override
    public void OnComplete() throws RemoteException {

    }

    @Override
    public void OnTaskChanged(int state) throws RemoteException {

    }

    @Override
    public void OnError(int code, String detail) throws RemoteException {

    }
}
