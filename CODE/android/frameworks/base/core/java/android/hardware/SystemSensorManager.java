    /*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.hardware;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import dalvik.system.CloseGuard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// EosTek Patch Begin
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import android.net.LocalSocketAddress;
import android.net.LocalSocket;
import java.lang.InterruptedException;

import android.os.HandlerThread;
import android.os.Message;
// EosTek Patch End
/**
 * Sensor manager implementation that communicates with the built-in
 * system sensors.
 *
 * @hide
 */
public class SystemSensorManager extends SensorManager {
    private static native void nativeClassInit();
    private static native int nativeGetNextSensor(Sensor sensor, int next);

    private static boolean sSensorModuleInitialized = false;
    private static final Object sSensorModuleLock = new Object();
    private static final ArrayList<Sensor> sFullSensorsList = new ArrayList<Sensor>();
    private static final SparseArray<Sensor> sHandleToSensor = new SparseArray<Sensor>();
    // EosTek Patch Begin
    private boolean hasAccelerometer = false;
    private boolean hasGyro = false;
    private boolean hasGravity = false;
    private static final boolean RC_SENSOR_DEBUG = false;
    private RemoteHandle mHandler;
    // EosTek Patch End

    // Listener list
    private final HashMap<SensorEventListener, SensorEventQueue> mSensorListeners =
            new HashMap<SensorEventListener, SensorEventQueue>();
    private final HashMap<TriggerEventListener, TriggerEventQueue> mTriggerListeners =
            new HashMap<TriggerEventListener, TriggerEventQueue>();

    // Looper associated with the context in which this instance was created.
    private final Looper mMainLooper;
    private final int mTargetSdkLevel;

    /** {@hide} */
    public SystemSensorManager(Context context, Looper mainLooper) {
        mMainLooper = mainLooper;
        mTargetSdkLevel = context.getApplicationInfo().targetSdkVersion;
        synchronized(sSensorModuleLock) {
            if (!sSensorModuleInitialized) {
                sSensorModuleInitialized = true;

                nativeClassInit();

                // initialize the sensor list
                final ArrayList<Sensor> fullList = sFullSensorsList;
                int i = 0;
                do {
                    Sensor sensor = new Sensor();
                    i = nativeGetNextSensor(sensor, i);
                    if (i>=0) {
                        //Log.d(TAG, "found sensor: " + sensor.getName() +
                        //        ", handle=" + sensor.getHandle());
                        fullList.add(sensor);
                        sHandleToSensor.append(sensor.getHandle(), sensor);
                    }
                } while (i>0);
                // EosTek Patch Begin
                if (!hasAccelerometer) {
                    boolean accSensor = false;
                    for (Sensor s : fullList) {
                        if (s.getType() == Sensor.TYPE_ACCELEROMETER) {
                            accSensor = true;
                            break;
                        }
                    }

                    if (!accSensor) {
                        // simulater a sensor with remote control
                        Sensor sensor = new Sensor(Sensor.TYPE_ACCELEROMETER);
                        fullList.add(sensor);
                        sHandleToSensor.append(sensor.getHandle(), sensor);
                        Log.i(TAG, "simulate an accelerometer");
                    } else {
                        hasAccelerometer = true;// really has accelerometer
                        Log.i(TAG, "really has accelerometer");
                    }
                }

                if (!hasGyro) {
                    boolean gyroSensor = false;
                    for (Sensor s : fullList) {
                        if (s.getType() == Sensor.TYPE_GYROSCOPE) {
                            gyroSensor = true;
                            break;
                        }
                    }
                    if (!gyroSensor) {
                        // simulater a sensor with remote control
                        Sensor sensor = new Sensor(Sensor.TYPE_GYROSCOPE);
                        fullList.add(sensor);
                        sHandleToSensor.append(sensor.getHandle(), sensor);
                        Log.i(TAG, "simulate a gyroscope");
                    } else {
                        hasGyro = true;// really has gyro
                        Log.i(TAG, "really has gyro");
                    }
                }

                if (!hasGravity) {
                    boolean gSensor = false;
                    for (Sensor s : fullList) {
                        if (s.getType() == Sensor.TYPE_GRAVITY) {
                            gSensor = true;
                            break;
                        }
                    }
                    if (!gSensor) {
                        // simulater a sensor with remote control
                        Sensor sensor = new Sensor(Sensor.TYPE_GRAVITY);
                        fullList.add(sensor);
                        sHandleToSensor.append(sensor.getHandle(), sensor);
                        Log.i(TAG, "simulate a gSensor");
                    } else {
                        hasGravity = true;// really has gyro
                        Log.i(TAG, "really has gSensor");
                    }
                }
                // EosTek Patch End
            }
            // EosTek Patch Begin
            if(null == mHandler) {
                HandlerThread ht = new HandlerThread("sync thread");
                ht.start();
                mHandler = new RemoteHandle(ht.getLooper());
            }
            // EosTek Patch End
        }
    }


    /** @hide */
    @Override
    protected List<Sensor> getFullSensorList() {
        return sFullSensorsList;
    }

    // EosTek Patch Begin
    private boolean isSimulateSensor(int type) {
        if (((!hasAccelerometer) && (Sensor.TYPE_ACCELEROMETER == type))
                || ((!hasGyro) && (Sensor.TYPE_GYROSCOPE == type))
                || ((!hasGravity) && (Sensor.TYPE_GRAVITY == type))) {
            return true;
        }
        return false;
    }

    // EosTek Patch End

    /** @hide */
    @Override
    protected boolean registerListenerImpl(SensorEventListener listener, Sensor sensor,
            int delayUs, Handler handler, int maxBatchReportLatencyUs, int reservedFlags) {
        if (listener == null || sensor == null) {
            Log.e(TAG, "sensor or listener is null");
            return false;
        }
        // EosTek Patch Begin
        if(null != sensor)
            Log.i(TAG, "registerListenerImpl:: " + sensor.toString());
        // EosTek Patch End
        // Trigger Sensors should use the requestTriggerSensor call.
        if (sensor.getReportingMode() == Sensor.REPORTING_MODE_ONE_SHOT) {
            Log.e(TAG, "Trigger Sensors should use the requestTriggerSensor.");
            return false;
        }
        if (maxBatchReportLatencyUs < 0 || delayUs < 0) {
            Log.e(TAG, "maxBatchReportLatencyUs and delayUs should be non-negative");
            return false;
        }

        // Invariants to preserve:
        // - one Looper per SensorEventListener
        // - one Looper per SensorEventQueue
        // We map SensorEventListener to a SensorEventQueue, which holds the looper
        synchronized (mSensorListeners) {
            SensorEventQueue queue = mSensorListeners.get(listener);
            // EosTek Patch Begin
            boolean isSimulateSensor = isSimulateSensor(sensor.getType());
            // EosTek Patch End
            if (queue == null) {
                Looper looper = (handler != null) ? handler.getLooper() : mMainLooper;
                queue = new SensorEventQueue(listener, looper, this);
                // EosTek Patch Begin
                if(isSimulateSensor) {
                    if(!queue.addSimulateSensor(sensor)) {
                        Log.e(TAG, "add simulate sensor failed.");
                        return false;
                    }
                    registerToRemoteSensor(listener);
                } else {
                    if (!queue.addSensor(sensor, delayUs, maxBatchReportLatencyUs, reservedFlags)) {
                        queue.dispose();
                        return false;
                    }
                }
                // EosTek Patch End
                mSensorListeners.put(listener, queue);
                return true;
            } else {
                // EosTek Patch Begin
                if(isSimulateSensor) {
                    return queue.addSimulateSensor(sensor);
                } else {
                    return queue.addSensor(sensor, delayUs, maxBatchReportLatencyUs, reservedFlags);
                }
                // EosTek Patch End
            }
        }
    }

    /** @hide */
    @Override
    protected void unregisterListenerImpl(SensorEventListener listener, Sensor sensor) {
        // Trigger Sensors should use the cancelTriggerSensor call.
        if (sensor != null && sensor.getReportingMode() == Sensor.REPORTING_MODE_ONE_SHOT) {
            return;
        }
        // EosTek Patch Begin
        if(null != sensor)
            Log.i(TAG, "unregisterListenerImpl:: " + sensor.toString());
        // EosTek Patch End

        synchronized (mSensorListeners) {
            SensorEventQueue queue = mSensorListeners.get(listener);
            if (queue != null) {
                boolean result;
                if (sensor == null) {
                    result = queue.removeAllSensors();
                } else {
                    result = queue.removeSensor(sensor, true);
                }
                if (result && !queue.hasSensors()) {
                    // EosTek Patch Begin
                    if(null != sensor && isSimulateSensor(sensor.getType())) {
                        unregisterToRemoteSensor(listener);
                    }
                    // EosTek Patch End
                    mSensorListeners.remove(listener);
                    queue.dispose();
                }
            }
        }
    }

    /** @hide */
    @Override
    protected boolean requestTriggerSensorImpl(TriggerEventListener listener, Sensor sensor) {
        if (sensor == null) throw new IllegalArgumentException("sensor cannot be null");

        if (sensor.getReportingMode() != Sensor.REPORTING_MODE_ONE_SHOT) return false;

        synchronized (mTriggerListeners) {
            TriggerEventQueue queue = mTriggerListeners.get(listener);
            if (queue == null) {
                queue = new TriggerEventQueue(listener, mMainLooper, this);
                if (!queue.addSensor(sensor, 0, 0, 0)) {
                    queue.dispose();
                    return false;
                }
                mTriggerListeners.put(listener, queue);
                return true;
            } else {
                return queue.addSensor(sensor, 0, 0, 0);
            }
        }
    }

    /** @hide */
    @Override
    protected boolean cancelTriggerSensorImpl(TriggerEventListener listener, Sensor sensor,
            boolean disable) {
        if (sensor != null && sensor.getReportingMode() != Sensor.REPORTING_MODE_ONE_SHOT) {
            return false;
        }
        synchronized (mTriggerListeners) {
            TriggerEventQueue queue = mTriggerListeners.get(listener);
            if (queue != null) {
                boolean result;
                if (sensor == null) {
                    result = queue.removeAllSensors();
                } else {
                    result = queue.removeSensor(sensor, disable);
                }
                if (result && !queue.hasSensors()) {
                    mTriggerListeners.remove(listener);
                    queue.dispose();
                }
                return result;
            }
            return false;
        }
    }

    protected boolean flushImpl(SensorEventListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");

        synchronized (mSensorListeners) {
            SensorEventQueue queue = mSensorListeners.get(listener);
            if (queue == null) {
                return false;
            } else {
                return (queue.flush() == 0);
            }
        }
    }

    /*
     * BaseEventQueue is the communication channel with the sensor service,
     * SensorEventQueue, TriggerEventQueue are subclases and there is one-to-one mapping between
     * the queues and the listeners.
     */
    private static abstract class BaseEventQueue {
        private native long nativeInitBaseEventQueue(BaseEventQueue eventQ, MessageQueue msgQ,
                float[] scratch);
        private static native int nativeEnableSensor(long eventQ, int handle, int rateUs,
                int maxBatchReportLatencyUs, int reservedFlags);
        private static native int nativeDisableSensor(long eventQ, int handle);
        private static native void nativeDestroySensorEventQueue(long eventQ);
        private static native int nativeFlushSensor(long eventQ);
        private long nSensorEventQueue;
        private final SparseBooleanArray mActiveSensors = new SparseBooleanArray();
        protected final SparseIntArray mSensorAccuracies = new SparseIntArray();
        protected final SparseBooleanArray mFirstEvent = new SparseBooleanArray();
        private final CloseGuard mCloseGuard = CloseGuard.get();
        private final float[] mScratch = new float[16];
        protected final SystemSensorManager mManager;

        BaseEventQueue(Looper looper, SystemSensorManager manager) {
            nSensorEventQueue = nativeInitBaseEventQueue(this, looper.getQueue(), mScratch);
            mCloseGuard.open("dispose");
            mManager = manager;
        }

        public void dispose() {
            dispose(false);
        }

        // EosTek Patch Begin
        public boolean addSimulateSensor(Sensor sensor) {
            // Check if already present.
            int handle = sensor.getHandle();
            if (mActiveSensors.get(handle))
                return false;

            // Get ready to receive events before calling enable.
            mActiveSensors.put(handle, true);
            addSensorEvent(sensor);
            return true;
        }
        // EosTek Patch End
        public boolean addSensor(
                Sensor sensor, int delayUs, int maxBatchReportLatencyUs, int reservedFlags) {
            // Check if already present.
            int handle = sensor.getHandle();
            if (mActiveSensors.get(handle)) return false;

            // Get ready to receive events before calling enable.
            mActiveSensors.put(handle, true);
            addSensorEvent(sensor);
            if (enableSensor(sensor, delayUs, maxBatchReportLatencyUs, reservedFlags) != 0) {
                // Try continuous mode if batching fails.
                if (maxBatchReportLatencyUs == 0 ||
                    maxBatchReportLatencyUs > 0 && enableSensor(sensor, delayUs, 0, 0) != 0) {
                  removeSensor(sensor, false);
                  return false;
                }
            }
            return true;
        }

        public boolean removeAllSensors() {
            for (int i=0 ; i<mActiveSensors.size(); i++) {
                if (mActiveSensors.valueAt(i) == true) {
                    int handle = mActiveSensors.keyAt(i);
                    Sensor sensor = sHandleToSensor.get(handle);
                    if (sensor != null) {
                        disableSensor(sensor);
                        mActiveSensors.put(handle, false);
                        removeSensorEvent(sensor);
                    } else {
                        // it should never happen -- just ignore.
                    }
                }
            }
            return true;
        }

        public boolean removeSensor(Sensor sensor, boolean disable) {
            final int handle = sensor.getHandle();
            if (mActiveSensors.get(handle)) {
                if (disable) disableSensor(sensor);
                mActiveSensors.put(sensor.getHandle(), false);
                removeSensorEvent(sensor);
                return true;
            }
            return false;
        }

        public int flush() {
            if (nSensorEventQueue == 0) throw new NullPointerException();
            return nativeFlushSensor(nSensorEventQueue);
        }

        public boolean hasSensors() {
            // no more sensors are set
            return mActiveSensors.indexOfValue(true) >= 0;
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                dispose(true);
            } finally {
                super.finalize();
            }
        }

        private void dispose(boolean finalized) {
            if (mCloseGuard != null) {
                if (finalized) {
                    mCloseGuard.warnIfOpen();
                }
                mCloseGuard.close();
            }
            if (nSensorEventQueue != 0) {
                nativeDestroySensorEventQueue(nSensorEventQueue);
                nSensorEventQueue = 0;
            }
        }

        private int enableSensor(
                Sensor sensor, int rateUs, int maxBatchReportLatencyUs, int reservedFlags) {
            if (nSensorEventQueue == 0) throw new NullPointerException();
            if (sensor == null) throw new NullPointerException();
            return nativeEnableSensor(nSensorEventQueue, sensor.getHandle(), rateUs,
                    maxBatchReportLatencyUs, reservedFlags);
        }

        private int disableSensor(Sensor sensor) {
            if (nSensorEventQueue == 0) throw new NullPointerException();
            if (sensor == null) throw new NullPointerException();
            return nativeDisableSensor(nSensorEventQueue, sensor.getHandle());
        }
        protected abstract void dispatchSensorEvent(int handle, float[] values, int accuracy,
                long timestamp);
        protected abstract void dispatchFlushCompleteEvent(int handle);

        protected abstract void addSensorEvent(Sensor sensor);
        protected abstract void removeSensorEvent(Sensor sensor);
    }

    static final class SensorEventQueue extends BaseEventQueue {
        private final SensorEventListener mListener;
        private final SparseArray<SensorEvent> mSensorsEvents = new SparseArray<SensorEvent>();

        public SensorEventQueue(SensorEventListener listener, Looper looper,
                SystemSensorManager manager) {
            super(looper, manager);
            mListener = listener;
        }

        @Override
        public void addSensorEvent(Sensor sensor) {
            SensorEvent t = new SensorEvent(Sensor.getMaxLengthValuesArray(sensor,
                    mManager.mTargetSdkLevel));
            synchronized (mSensorsEvents) {
                mSensorsEvents.put(sensor.getHandle(), t);
            }
        }

        @Override
        public void removeSensorEvent(Sensor sensor) {
            synchronized (mSensorsEvents) {
                mSensorsEvents.delete(sensor.getHandle());
            }
        }

        // Called from native code.
        @SuppressWarnings("unused")
        @Override
        protected void dispatchSensorEvent(int handle, float[] values, int inAccuracy,
                long timestamp) {
            final Sensor sensor = sHandleToSensor.get(handle);
            SensorEvent t = null;
            synchronized (mSensorsEvents) {
                t = mSensorsEvents.get(handle);
            }

            if (t == null) {
                // This may happen if the client has unregistered and there are pending events in
                // the queue waiting to be delivered. Ignore.
                return;
            }
            // Copy from the values array.
            System.arraycopy(values, 0, t.values, 0, t.values.length);
            t.timestamp = timestamp;
            t.accuracy = inAccuracy;
            t.sensor = sensor;

            // call onAccuracyChanged() only if the value changes
            final int accuracy = mSensorAccuracies.get(handle);
            if ((t.accuracy >= 0) && (accuracy != t.accuracy)) {
                mSensorAccuracies.put(handle, t.accuracy);
                mListener.onAccuracyChanged(t.sensor, t.accuracy);
            }
            mListener.onSensorChanged(t);
        }

        @SuppressWarnings("unused")
        protected void dispatchFlushCompleteEvent(int handle) {
            if (mListener instanceof SensorEventListener2) {
                final Sensor sensor = sHandleToSensor.get(handle);
                ((SensorEventListener2)mListener).onFlushCompleted(sensor);
            }
            return;
        }
    }

    static final class TriggerEventQueue extends BaseEventQueue {
        private final TriggerEventListener mListener;
        private final SparseArray<TriggerEvent> mTriggerEvents = new SparseArray<TriggerEvent>();

        public TriggerEventQueue(TriggerEventListener listener, Looper looper,
                SystemSensorManager manager) {
            super(looper, manager);
            mListener = listener;
        }

        @Override
        public void addSensorEvent(Sensor sensor) {
            TriggerEvent t = new TriggerEvent(Sensor.getMaxLengthValuesArray(sensor,
                    mManager.mTargetSdkLevel));
            synchronized (mTriggerEvents) {
                mTriggerEvents.put(sensor.getHandle(), t);
            }
        }

        @Override
        public void removeSensorEvent(Sensor sensor) {
            synchronized (mTriggerEvents) {
                mTriggerEvents.delete(sensor.getHandle());
            }
        }

        // Called from native code.
        @SuppressWarnings("unused")
        @Override
        protected void dispatchSensorEvent(int handle, float[] values, int accuracy,
                long timestamp) {
            final Sensor sensor = sHandleToSensor.get(handle);
            TriggerEvent t = null;
            synchronized (mTriggerEvents) {
                t = mTriggerEvents.get(handle);
            }
            if (t == null) {
                Log.e(TAG, "Error: Trigger Event is null for Sensor: " + sensor);
                return;
            }

            // Copy from the values array.
            System.arraycopy(values, 0, t.values, 0, t.values.length);
            t.timestamp = timestamp;
            t.sensor = sensor;

            // A trigger sensor is auto disabled. So just clean up and don't call native
            // disable.
            mManager.cancelTriggerSensorImpl(mListener, sensor, false);

            mListener.onTrigger(t);
        }

        @SuppressWarnings("unused")
        protected void dispatchFlushCompleteEvent(int handle) {
        }
    }

    // EosTek Patch Begin
    /*
     * add for remote control remote control thread, read data from
     * socket(eostek_rc_sensor)
     */
    private RemoteControlThread mRCThread;

    class RemoteControlThread extends Thread {

        private InputStream mIn;

        private OutputStream mOut;

        private LocalSocket mSocket;

        private ArrayList<SensorEventListener> mRCRegisteredListeners = new ArrayList<SensorEventListener>();

        public void addRCListener(SensorEventListener listener) {
            if (!mRCRegisteredListeners.contains(listener)) {
                mRCRegisteredListeners.add(listener);
            }
        }

        public void romoveRCListener(SensorEventListener listener) {
            if (mRCRegisteredListeners.contains(listener)) {
                mRCRegisteredListeners.remove(listener);
            }
        }

        public boolean hasListeners() {
            return mRCRegisteredListeners.size() > 0;
        }

        private int getInt(byte[] bytes, int offset) {
            return (0xff & bytes[0 + offset]) | (0xff00 & (bytes[1 + offset] << 8))
                    | (0xff0000 & (bytes[2 + offset] << 16)) | (0xff000000 & (bytes[3 + offset] << 24));
        }

        private boolean connect() {
            if (mSocket != null && mSocket.isConnected()) {
                return true;
            }
            if (RC_SENSOR_DEBUG)
                Log.i(TAG, "connecting...");
            try {
                mSocket = new LocalSocket();

                LocalSocketAddress address = new LocalSocketAddress("eostek_rc_sensor");
                mSocket.connect(address);

                mIn = new DataInputStream(mSocket.getInputStream());
                mOut = new DataOutputStream(mSocket.getOutputStream());
            } catch (IOException ex) {
                if (RC_SENSOR_DEBUG)
                    Log.e(TAG, "connect exception" + ex);
                disconnect();
                return false;
            }
            return true;
        }

        private void disconnect() {
            if (RC_SENSOR_DEBUG)
                Log.i(TAG, "disconnecting...");

            try {
                if (mSocket != null)
                    mSocket.close();

                if (mIn != null)
                    mIn.close();

                if (mOut != null)
                    mOut.close();
            } catch (IOException ex) {
                Log.e(TAG, "disconnect exception" + ex);
            }

            mSocket = null;
            mIn = null;
            mOut = null;
        }

        public void run() {
            if (!connect()) {
                Log.i(TAG, "connect to rc_sensor socket failed .");
                return;
            }
            byte data[] = new byte[2];
            int dataLen = 0;
            int bytesLeft = 0;
            int bytesRead = 0;
            byte inStream[] = null;

            while (true) {

                if ((null == mSocket) || (null == mIn) || (null == mOut)) {
                    break;
                }

                try {
                    if (mIn.read(data, 0, 2) < 2) {
                        Log.d(TAG, "read data length fail");
                        break;
                    }
                } catch (IOException ex) {
                    Log.e(TAG, "read length exception:" + ex);
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "unknown exception:" + e);
                    break;
                }

                dataLen = 0;
                bytesLeft = 0;
                bytesRead = 0;
                inStream = null;

                for (int i = 0; i < 2; i++) {
                    dataLen += (data[i] & 0xff) << (8 * i);
                }

                if (RC_SENSOR_DEBUG)
                    Log.d(TAG, "receive data length = " + dataLen);

                bytesLeft = dataLen;
                inStream = new byte[dataLen];
                while (bytesLeft > 0) {
                    try {
                        bytesRead = mIn.read(inStream, bytesRead, bytesLeft);
                        bytesLeft -= bytesRead;
                    } catch (IOException ex) {
                        Log.e(TAG, "read data exception:" + ex);
                        break;
                    }
                }

                if (!hasListeners()) {
                    if (RC_SENSOR_DEBUG)
                        Log.e(TAG, "no app register sensor Listener !!!");
                    continue;
                }

                if (dataLen >= 17) {
                    byte type;
                    int intBits, accuracy;
                    final float[] values = new float[3];
                    final long timestamp;

                    type = inStream[0];
                    intBits = getInt(inStream, 1);
                    values[0] = Float.intBitsToFloat(intBits);
                    intBits = getInt(inStream, 5);
                    values[1] = Float.intBitsToFloat(intBits);
                    intBits = getInt(inStream, 9);
                    values[2] = Float.intBitsToFloat(intBits);
                    accuracy = getInt(inStream, 13);

                    if (RC_SENSOR_DEBUG)
                        Log.i(TAG, "[type:" + type + ",x:" + values[0] + ",y:" + values[1] + ",z:" + values[2]
                                + "], accuracy = " + accuracy);

                    Sensor sensorObject = null;
                    if (Sensor.TYPE_ACCELEROMETER == type) {
                        sensorObject = new Sensor(type);
                    } else if (Sensor.TYPE_GYROSCOPE == type) {
                        sensorObject = new Sensor(type);
                    } else if (Sensor.TYPE_GRAVITY == type) {
                        sensorObject = new Sensor(type);
                    }

                    timestamp = System.currentTimeMillis();
                    synchronized (mSensorListeners) {
                        if (hasListeners()) {
                            final int size = mRCRegisteredListeners.size();
                            for (int i = 0; i < size; i++) {
                                SensorEventQueue queue = mSensorListeners.get(mRCRegisteredListeners.get(i));

                                try {
                                    queue.dispatchSensorEvent(sensorObject.getHandle(), values, accuracy, timestamp);
                                } catch (Exception ex) {
                                    if(RC_SENSOR_DEBUG)
                                        Log.e(TAG, "Dispatch Exception: " + ex.getMessage());
                                }
                            }
                        }
                    }
                }
            }
            disconnect();
        }
    }

    private void registerToRemoteSensor(SensorEventListener listener) {
        if(null != listener) {
            mHandler.obtainMessage(MSG_REGISTER_SENSOR, listener).sendToTarget();
        }
    }

    private void unregisterToRemoteSensor(SensorEventListener listener) {
        if(null != listener) {
            mHandler.obtainMessage(MSG_UNREGISTER_SENSOR, listener).sendToTarget();
        }
    }

    // Handler related
    private static final int MSG_REGISTER_SENSOR = 1000;
    private static final int MSG_UNREGISTER_SENSOR = 1001;

    /**
     * @hide
     */
    class RemoteHandle extends Handler {
        public RemoteHandle(Looper looper) {
            super(looper);
        }

        private void handleRegisterSensor(SensorEventListener listener) {
            if (null == mRCThread) {
                mRCThread = new RemoteControlThread();
                mRCThread.start();
            }
            mRCThread.addRCListener(listener);
        }

        private void handleUnregisterSensor(SensorEventListener listener) {
            if(null != mRCThread) {
                mRCThread.romoveRCListener(listener);
            }
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_SENSOR:
                    handleRegisterSensor((SensorEventListener) msg.obj);
                    break;
                case MSG_UNREGISTER_SENSOR:
                    handleUnregisterSensor((SensorEventListener) msg.obj);
                    break;

                default:
                    break;
            }
        }
    }

    // EosTek Patch End
}
