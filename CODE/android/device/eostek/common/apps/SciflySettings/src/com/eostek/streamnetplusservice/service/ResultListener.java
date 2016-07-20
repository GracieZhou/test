
package com.eostek.streamnetplusservice.service;

import java.util.List;

import android.os.RemoteException;

public class ResultListener extends IResultListener.Stub {

    @Override
    public void OnCreated(List<TaskInfoInternal> taskList) throws RemoteException {

    }

    @Override
    public void OnError(int code, String detail) throws RemoteException {

    }
}
