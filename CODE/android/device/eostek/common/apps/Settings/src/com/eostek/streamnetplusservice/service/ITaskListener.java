/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: G:\\SVN-Res\\I_HMS_HVS_02\\trunk\\StreamNetService\\StreamNetPlusService\\src\\com\\eostek\\streamnetplusservice\\service\\ITaskListener.aidl
 */

package com.eostek.streamnetplusservice.service;

public interface ITaskListener extends android.os.IInterface {
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements
            com.eostek.streamnetplusservice.service.ITaskListener {
        private static final java.lang.String DESCRIPTOR = "com.eostek.streamnetplusservice.service.ITaskListener";

        /** Construct the stub at attach it to the interface. */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an
         * com.eostek.streamnetplusservice.service.ITaskListener interface,
         * generating a proxy if needed.
         */
        public static com.eostek.streamnetplusservice.service.ITaskListener asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.eostek.streamnetplusservice.service.ITaskListener))) {
                return ((com.eostek.streamnetplusservice.service.ITaskListener) iin);
            }
            return new com.eostek.streamnetplusservice.service.ITaskListener.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
                throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_OnInfo: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    this.OnInfo(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_OnComplete: {
                    data.enforceInterface(DESCRIPTOR);
                    this.OnComplete();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_OnTaskChanged: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.OnTaskChanged(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_OnError: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    this.OnError(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements com.eostek.streamnetplusservice.service.ITaskListener {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            /**
             * Set information listener
             * 
             * @param progress: current download progress in percent
             * @param speed: current download speed(Unit:Bytes/s)
             */
            @Override
            public void OnInfo(int progress, int speed) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(progress);
                    _data.writeInt(speed);
                    mRemote.transact(Stub.TRANSACTION_OnInfo, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * Set complete listener
             */
            @Override
            public void OnComplete() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_OnComplete, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * Set task change listener
             * 
             * @param state: current state of task
             */
            @Override
            public void OnTaskChanged(int state) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(state);
                    mRemote.transact(Stub.TRANSACTION_OnTaskChanged, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * Set error listener
             * 
             * @param code: error code
             * @param detail: detail information of error
             */
            @Override
            public void OnError(int code, java.lang.String detail) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(detail);
                    mRemote.transact(Stub.TRANSACTION_OnError, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_OnInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);

        static final int TRANSACTION_OnComplete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

        static final int TRANSACTION_OnTaskChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);

        static final int TRANSACTION_OnError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    }

    /**
     * Set information listener
     * 
     * @param progress: current download progress in percent
     * @param speed: current download speed(Unit:Bytes/s)
     */
    public void OnInfo(int progress, int speed) throws android.os.RemoteException;

    /**
     * Set complete listener
     */
    public void OnComplete() throws android.os.RemoteException;

    /**
     * Set task change listener
     * 
     * @param state: current state of task
     */
    public void OnTaskChanged(int state) throws android.os.RemoteException;

    /**
     * Set error listener
     * 
     * @param code: error code
     * @param detail: detail information of error
     */
    public void OnError(int code, java.lang.String detail) throws android.os.RemoteException;
}
