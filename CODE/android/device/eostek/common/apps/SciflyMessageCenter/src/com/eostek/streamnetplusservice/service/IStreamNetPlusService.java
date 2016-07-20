/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: G:\\SVN-Res\\I_HMS_HVS_02\\trunk\\StreamNetService\\StreamNetPlusService\\src\\com\\eostek\\streamnetplusservice\\service\\IStreamNetPlusService.aidl
 */

package com.eostek.streamnetplusservice.service;

public interface IStreamNetPlusService extends android.os.IInterface {
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements
            com.eostek.streamnetplusservice.service.IStreamNetPlusService {
        private static final java.lang.String DESCRIPTOR = "com.eostek.streamnetplusservice.service.IStreamNetPlusService";

        /** Construct the stub at attach it to the interface. */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an
         * com.eostek.streamnetplusservice.service.IStreamNetPlusService
         * interface, generating a proxy if needed.
         */
        public static com.eostek.streamnetplusservice.service.IStreamNetPlusService asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.eostek.streamnetplusservice.service.IStreamNetPlusService))) {
                return ((com.eostek.streamnetplusservice.service.IStreamNetPlusService) iin);
            }
            return new com.eostek.streamnetplusservice.service.IStreamNetPlusService.Stub.Proxy(obj);
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
                case TRANSACTION_init: {
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg0;
                    _arg0 = (0 != data.readInt());
                    boolean _arg1;
                    _arg1 = (0 != data.readInt());
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    java.lang.String _arg3;
                    _arg3 = data.readString();
                    this.init(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_destory: {
                    data.enforceInterface(DESCRIPTOR);
                    this.destory();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getPlayURL: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    MyMap _arg1;
                    if ((0 != data.readInt())) {
                        _arg1 = MyMap.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    java.lang.String _result = this.getPlayURL(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_getPlayURL2: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _result = this.getPlayURL2(_arg0);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_stopPlay: {
                    data.enforceInterface(DESCRIPTOR);
                    this.stopPlay();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getDownloadSpeed: {
                    data.enforceInterface(DESCRIPTOR);
                    int _result = this.getDownloadSpeed();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_stopDownload: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.stopDownload(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_startDownload: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.startDownload(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_createDownloadTask: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    int _arg3;
                    _arg3 = data.readInt();
                    com.eostek.streamnetplusservice.service.IResultListener _arg4;
                    _arg4 = com.eostek.streamnetplusservice.service.IResultListener.Stub.asInterface(data
                            .readStrongBinder());
                    MyMap _arg5;
                    if ((0 != data.readInt())) {
                        _arg5 = MyMap.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    this.createDownloadTask(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_push: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    MyMap _arg2;
                    if ((0 != data.readInt())) {
                        _arg2 = MyMap.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    this.push(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setTaskListener: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    com.eostek.streamnetplusservice.service.ITaskListener _arg1;
                    _arg1 = com.eostek.streamnetplusservice.service.ITaskListener.Stub.asInterface(data
                            .readStrongBinder());
                    boolean _arg2;
                    _arg2 = (0 != data.readInt());
                    boolean _result = this.setTaskListener(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                case TRANSACTION_setEventListener: {
                    data.enforceInterface(DESCRIPTOR);
                    com.eostek.streamnetplusservice.service.IEventListener _arg0;
                    _arg0 = com.eostek.streamnetplusservice.service.IEventListener.Stub.asInterface(data
                            .readStrongBinder());
                    boolean _arg1;
                    _arg1 = (0 != data.readInt());
                    this.setEventListener(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setTrackers: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.setTrackers(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setUploadSpeedLimit: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.setUploadSpeedLimit(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setDownloadSpeedLimit: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.setDownloadSpeedLimit(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setMaxConnectionCount: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.setMaxConnectionCount(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_removeTask: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.removeTask(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_removeTaskAndFile: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.removeTaskAndFile(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_changeTaskState: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    this.changeTaskState(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_updatePlayingURL: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.updatePlayingURL(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_updateDownloadURL: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    this.updateDownloadURL(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getTaskInfo: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    TaskInfoInternal _result = this.getTaskInfo(_arg0);
                    reply.writeNoException();
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
                case TRANSACTION_getPlayingTaskInfo: {
                    data.enforceInterface(DESCRIPTOR);
                    TaskInfoInternal _result = this.getPlayingTaskInfo();
                    reply.writeNoException();
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
                case TRANSACTION_IsDiskReady: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    boolean _result = this.IsDiskReady(_arg0);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                case TRANSACTION_addDiskPath: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    boolean _result = this.addDiskPath(_arg0);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                case TRANSACTION_removeDiskPath: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    boolean _result = this.removeDiskPath(_arg0);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                case TRANSACTION_getDownloadTaskList: {
                    data.enforceInterface(DESCRIPTOR);
                    java.util.List<java.lang.String> _result = this.getDownloadTaskList();
                    reply.writeNoException();
                    reply.writeStringList(_result);
                    return true;
                }
                case TRANSACTION_getUploadSpeedLimit: {
                    data.enforceInterface(DESCRIPTOR);
                    int _result = this.getUploadSpeedLimit();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_getDownloadSpeedLimit: {
                    data.enforceInterface(DESCRIPTOR);
                    int _result = this.getDownloadSpeedLimit();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_getMaxConnectionCount: {
                    data.enforceInterface(DESCRIPTOR);
                    int _result = this.getMaxConnectionCount();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_clearCache: {
                    data.enforceInterface(DESCRIPTOR);
                    this.clearCache();
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements com.eostek.streamnetplusservice.service.IStreamNetPlusService {
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

            @Override
            public void init(boolean memoryOnly, boolean isSDCARD, java.lang.String cachePath,
                    java.lang.String downloadPath) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(((memoryOnly) ? (1) : (0)));
                    _data.writeInt(((isSDCARD) ? (1) : (0)));
                    _data.writeString(cachePath);
                    _data.writeString(downloadPath);
                    mRemote.transact(Stub.TRANSACTION_init, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 释放资源
             */
            @Override
            public void destory() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_destory, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取播放地址
             * 
             * @param orgUrl 原网络播放地址
             * @return 返回StreamNet能力平台能识别的播放地址
             */
            @Override
            public java.lang.String getPlayURL(java.lang.String orgUrl, MyMap extra) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(orgUrl);
                    if ((extra != null)) {
                        _data.writeInt(1);
                        extra.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_getPlayURL, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public java.lang.String getPlayURL2(java.lang.String orgUrl) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(orgUrl);
                    mRemote.transact(Stub.TRANSACTION_getPlayURL2, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * 停止播放
             */
            @Override
            public void stopPlay() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_stopPlay, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取全局下载速度 (单位：Bytes/s)
             * 
             * @return 全局下载速度
             */
            @Override
            public int getDownloadSpeed() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getDownloadSpeed, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * ֹͣ停止下载
             * 
             * @param url
             */
            @Override
            public void stopDownload(java.lang.String url) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    mRemote.transact(Stub.TRANSACTION_stopDownload, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 开始下载
             * 
             * @param url
             */
            @Override
            public void startDownload(java.lang.String url) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    mRemote.transact(Stub.TRANSACTION_startDownload, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 创建任务
             * 
             * @param url 下载地址
             * @param storagePath 存储地址
             * @param urlType url类型{@link TaskURLType}
             * @param listener 异步回调监听对象
             */
            @Override
            public void createDownloadTask(java.lang.String url, java.lang.String resumePath,
                    java.lang.String storagePath, int urlType,
                    com.eostek.streamnetplusservice.service.IResultListener listener, MyMap extra)
                    throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeString(resumePath);
                    _data.writeString(storagePath);
                    _data.writeInt(urlType);
                    _data.writeStrongBinder((((listener != null)) ? (listener.asBinder()) : (null)));
                    if ((extra != null)) {
                        _data.writeInt(1);
                        extra.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_createDownloadTask, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void push(java.lang.String url, int urlType, MyMap extra) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeInt(urlType);
                    if ((extra != null)) {
                        _data.writeInt(1);
                        extra.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_push, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 设置单个任务监听
             * 
             * @param url
             * @param listener
             * @param listen
             */
            @Override
            public boolean setTaskListener(java.lang.String url,
                    com.eostek.streamnetplusservice.service.ITaskListener listener, boolean listen)
                    throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeStrongBinder((((listener != null)) ? (listener.asBinder()) : (null)));
                    _data.writeInt(((listen) ? (1) : (0)));
                    mRemote.transact(Stub.TRANSACTION_setTaskListener, _data, _reply, 0);
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * 设置全局监听，监听下载速度
             * 
             * @param listener
             */
            @Override
            public void setEventListener(com.eostek.streamnetplusservice.service.IEventListener listener, boolean listen)
                    throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((listener != null)) ? (listener.asBinder()) : (null)));
                    _data.writeInt(((listen) ? (1) : (0)));
                    mRemote.transact(Stub.TRANSACTION_setEventListener, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 设置tracker e.g:http://ip:port/***
             * 
             * @param tracker
             */
            @Override
            public void setTrackers(java.lang.String tracker) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(tracker);
                    mRemote.transact(Stub.TRANSACTION_setTrackers, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setUploadSpeedLimit(int speed) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(speed);
                    mRemote.transact(Stub.TRANSACTION_setUploadSpeedLimit, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setDownloadSpeedLimit(int speed) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(speed);
                    mRemote.transact(Stub.TRANSACTION_setDownloadSpeedLimit, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setMaxConnectionCount(int count) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(count);
                    mRemote.transact(Stub.TRANSACTION_setMaxConnectionCount, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 删除任务
             * 
             * @param url
             */
            @Override
            public void removeTask(java.lang.String url) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    mRemote.transact(Stub.TRANSACTION_removeTask, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void removeTaskAndFile(java.lang.String url) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    mRemote.transact(Stub.TRANSACTION_removeTaskAndFile, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 改变状态״̬
             * 
             * @param url
             * @param state
             */
            @Override
            public void changeTaskState(java.lang.String url, int state) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeInt(state);
                    mRemote.transact(Stub.TRANSACTION_changeTaskState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void updatePlayingURL(java.lang.String newUrl) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(newUrl);
                    mRemote.transact(Stub.TRANSACTION_updatePlayingURL, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void updateDownloadURL(java.lang.String oldUrl, java.lang.String newUrl)
                    throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(oldUrl);
                    _data.writeString(newUrl);
                    mRemote.transact(Stub.TRANSACTION_updateDownloadURL, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public TaskInfoInternal getTaskInfo(java.lang.String url) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                TaskInfoInternal _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(url);
                    mRemote.transact(Stub.TRANSACTION_getTaskInfo, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        _result = TaskInfoInternal.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public TaskInfoInternal getPlayingTaskInfo() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                TaskInfoInternal _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getPlayingTaskInfo, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        _result = TaskInfoInternal.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public boolean IsDiskReady(java.lang.String path) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(path);
                    mRemote.transact(Stub.TRANSACTION_IsDiskReady, _data, _reply, 0);
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public boolean addDiskPath(java.lang.String path) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(path);
                    mRemote.transact(Stub.TRANSACTION_addDiskPath, _data, _reply, 0);
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public boolean removeDiskPath(java.lang.String path) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(path);
                    mRemote.transact(Stub.TRANSACTION_removeDiskPath, _data, _reply, 0);
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public java.util.List<java.lang.String> getDownloadTaskList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.util.List<java.lang.String> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getDownloadTaskList, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createStringArrayList();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int getUploadSpeedLimit() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getUploadSpeedLimit, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int getDownloadSpeedLimit() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getDownloadSpeedLimit, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int getMaxConnectionCount() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getMaxConnectionCount, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void clearCache() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_clearCache, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_init = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);

        static final int TRANSACTION_destory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

        static final int TRANSACTION_getPlayURL = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);

        static final int TRANSACTION_getPlayURL2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);

        static final int TRANSACTION_stopPlay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);

        static final int TRANSACTION_getDownloadSpeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);

        static final int TRANSACTION_stopDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);

        static final int TRANSACTION_startDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);

        static final int TRANSACTION_createDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);

        static final int TRANSACTION_push = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);

        static final int TRANSACTION_setTaskListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);

        static final int TRANSACTION_setEventListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);

        static final int TRANSACTION_setTrackers = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);

        static final int TRANSACTION_setUploadSpeedLimit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);

        static final int TRANSACTION_setDownloadSpeedLimit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);

        static final int TRANSACTION_setMaxConnectionCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);

        static final int TRANSACTION_removeTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);

        static final int TRANSACTION_removeTaskAndFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);

        static final int TRANSACTION_changeTaskState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);

        static final int TRANSACTION_updatePlayingURL = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);

        static final int TRANSACTION_updateDownloadURL = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);

        static final int TRANSACTION_getTaskInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);

        static final int TRANSACTION_getPlayingTaskInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);

        static final int TRANSACTION_IsDiskReady = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);

        static final int TRANSACTION_addDiskPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);

        static final int TRANSACTION_removeDiskPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);

        static final int TRANSACTION_getDownloadTaskList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);

        static final int TRANSACTION_getUploadSpeedLimit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);

        static final int TRANSACTION_getDownloadSpeedLimit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);

        static final int TRANSACTION_getMaxConnectionCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);

        static final int TRANSACTION_clearCache = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
    }

    public void init(boolean memoryOnly, boolean isSDCARD, java.lang.String cachePath, java.lang.String downloadPath)
            throws android.os.RemoteException;

    /**
     * 释放资源
     */
    public void destory() throws android.os.RemoteException;

    /**
     * 获取播放地址
     * 
     * @param orgUrl 原网络播放地址
     * @return 返回StreamNet能力平台能识别的播放地址
     */
    public java.lang.String getPlayURL(java.lang.String orgUrl, MyMap extra) throws android.os.RemoteException;

    public java.lang.String getPlayURL2(java.lang.String orgUrl) throws android.os.RemoteException;

    /**
     * 停止播放
     */
    public void stopPlay() throws android.os.RemoteException;

    /**
     * 获取全局下载速度 (单位：Bytes/s)
     * 
     * @return 全局下载速度
     */
    public int getDownloadSpeed() throws android.os.RemoteException;

    /**
     * ֹͣ停止下载
     * 
     * @param url
     */
    public void stopDownload(java.lang.String url) throws android.os.RemoteException;

    /**
     * 开始下载
     * 
     * @param url
     */
    public void startDownload(java.lang.String url) throws android.os.RemoteException;

    /**
     * 创建任务
     * 
     * @param url 下载地址
     * @param storagePath 存储地址
     * @param urlType url类型{@link TaskURLType}
     * @param listener 异步回调监听对象
     */
    public void createDownloadTask(java.lang.String url, java.lang.String resumePath, java.lang.String storagePath,
            int urlType, com.eostek.streamnetplusservice.service.IResultListener listener, MyMap extra)
            throws android.os.RemoteException;

    public void push(java.lang.String url, int urlType, MyMap extra) throws android.os.RemoteException;

    /**
     * 设置单个任务监听
     * 
     * @param url
     * @param listener
     * @param listen
     */
    public boolean setTaskListener(java.lang.String url,
            com.eostek.streamnetplusservice.service.ITaskListener listener, boolean listen)
            throws android.os.RemoteException;

    /**
     * 设置全局监听，监听下载速度
     * 
     * @param listener
     */
    public void setEventListener(com.eostek.streamnetplusservice.service.IEventListener listener, boolean listen)
            throws android.os.RemoteException;

    /**
     * 设置tracker e.g:http://ip:port/***
     * 
     * @param tracker
     */
    public void setTrackers(java.lang.String tracker) throws android.os.RemoteException;

    public void setUploadSpeedLimit(int speed) throws android.os.RemoteException;

    public void setDownloadSpeedLimit(int speed) throws android.os.RemoteException;

    public void setMaxConnectionCount(int count) throws android.os.RemoteException;

    /**
     * 删除任务
     * 
     * @param url
     */
    public void removeTask(java.lang.String url) throws android.os.RemoteException;

    public void removeTaskAndFile(java.lang.String url) throws android.os.RemoteException;

    /**
     * 改变状态״̬
     * 
     * @param url
     * @param state
     */
    public void changeTaskState(java.lang.String url, int state) throws android.os.RemoteException;

    public void updatePlayingURL(java.lang.String newUrl) throws android.os.RemoteException;

    public void updateDownloadURL(java.lang.String oldUrl, java.lang.String newUrl) throws android.os.RemoteException;

    public TaskInfoInternal getTaskInfo(java.lang.String url) throws android.os.RemoteException;

    public TaskInfoInternal getPlayingTaskInfo() throws android.os.RemoteException;

    public boolean IsDiskReady(java.lang.String path) throws android.os.RemoteException;

    public boolean addDiskPath(java.lang.String path) throws android.os.RemoteException;

    public boolean removeDiskPath(java.lang.String path) throws android.os.RemoteException;

    public java.util.List<java.lang.String> getDownloadTaskList() throws android.os.RemoteException;

    public int getUploadSpeedLimit() throws android.os.RemoteException;

    public int getDownloadSpeedLimit() throws android.os.RemoteException;

    public int getMaxConnectionCount() throws android.os.RemoteException;

    public void clearCache() throws android.os.RemoteException;
}
