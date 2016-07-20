
package com.eostek.isynergy.setmeup.jni;

import com.eostek.isynergy.setmeup.listener.ServiceListener;

public class JNIImpl {
    private native void JNI_UTC_Initialize(ServiceListener listener, String devName, String descriptionFile);

    private native void JNI_UTC_Finalize();

    private native String JNI_UTC_GetVersion();

    private native void JNI_UTC_ServiceNotify(int serviceType);

    public void initialize(ServiceListener listener, String devName, String descriptionFile) {
        JNI_UTC_Initialize(listener, devName, descriptionFile);
    }

    public void finalize() {
        JNI_UTC_Finalize();
    }

    public String getVersion() {
        return JNI_UTC_GetVersion();
    }

    public void serviceNotify(int serviceType) {
        JNI_UTC_ServiceNotify(serviceType);
    }

    static {
        System.loadLibrary("SetMeUpServer");
    }
}
