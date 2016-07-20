/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: G:\\SVN-Res\\I_HMS_HVS_02\\trunk\\StreamNetService\\StreamNetPlusService\\src\\com\\eostek\\streamnetplusservice\\service\\IResultListener.aidl
 */

package com.eostek.streamnetplusservice.service;

public interface IResultListener extends android.os.IInterface {
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements
            com.eostek.streamnetplusservice.service.IResultListener {
        private static final java.lang.String DESCRIPTOR = "com.eostek.streamnetplusservice.service.IResultListener";

        /** Construct the stub at attach it to the interface. */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an
         * com.eostek.streamnetplusservice.service.IResultListener interface,
         * generating a proxy if needed.
         */
        public static com.eostek.streamnetplusservice.service.IResultListener asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.eostek.streamnetplusservice.service.IResultListener))) {
                return ((com.eostek.streamnetplusservice.service.IResultListener) iin);
            }
            return new com.eostek.streamnetplusservice.service.IResultListener.Stub.Proxy(obj);
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
                case TRANSACTION_OnCreated: {
                    data.enforceInterface(DESCRIPTOR);
                    java.util.List<TaskInfoInternal> _arg0;
                    _arg0 = data.createTypedArrayList(TaskInfoInternal.CREATOR);
                    this.OnCreated(_arg0);
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

        private static class Proxy implements com.eostek.streamnetplusservice.service.IResultListener {
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
             * Set result listener
             * 
             * @param taskList: task list
             */
            @Override
            public void OnCreated(java.util.List<TaskInfoInternal> taskList) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedList(taskList);
                    mRemote.transact(Stub.TRANSACTION_OnCreated, _data, _reply, 0);
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
             * @param taskID: detail information of error
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

        static final int TRANSACTION_OnCreated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);

        static final int TRANSACTION_OnError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    /**
     * Set result listener
     * 
     * @param taskList: task list
     */
    public void OnCreated(java.util.List<TaskInfoInternal> taskList) throws android.os.RemoteException;

    /**
     * Set error listener
     * 
     * @param code: error code
     * @param taskID: detail information of error
     */
    public void OnError(int code, java.lang.String detail) throws android.os.RemoteException;
}
