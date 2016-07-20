package com.android.settings.network.connectivity;

public interface ConnectionListener {

    public void onStatusChanged(int status, boolean success, int errorCode);
}
