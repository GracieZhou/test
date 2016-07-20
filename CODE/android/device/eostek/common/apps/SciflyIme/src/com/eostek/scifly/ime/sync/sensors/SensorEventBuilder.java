
package com.eostek.scifly.ime.sync.sensors;

import com.eostek.scifly.ime.sync.ByteUtil;

/**
 * @author frank.zhang
 * @since API 2.4
 * @date 2014-12-1
 */
public class SensorEventBuilder {

    private float[] mValues;

    private int mAccuracy;

    private int mSensorType = -1;

    public int getSensorType() {
        return mSensorType;
    }

    public void setSensorType(int mSensorType) {
        this.mSensorType = mSensorType;
    }

    public int getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(int mAccuracy) {
        this.mAccuracy = mAccuracy;
    }

    public float[] getValues() {
        return mValues;
    }

    public SensorEventBuilder() {
        mValues = new float[3];
    }

    public SensorEventBuilder(int accuracy, float[] values, int sensorType) {
        mAccuracy = accuracy;
        mValues = values;
        mSensorType = sensorType;
    }

    /**
     * Intent to convert attr to byte. 4 bytes accuracy 8 bytes timestamp x
     * bytes sensorInfo y bytes valeus total bytes <= 1023
     * 
     * @param
     * @return
     * @since API 2.4
     */
    public byte[] encode() {
        byte type = (byte) (mSensorType & 0xff);
        byte[] accuracy = ByteUtil.getBytes(mAccuracy);

        byte buf[] = new byte[17];

        // sensor type
        buf[0] = type;

        // value x,y,z
        for (int j = 0; j < 3; j++) {
            byte[] value = ByteUtil.getBytes(mValues[j]);
            memncpy(buf, value, 1 + 4 * j, 4);
        }

        // accuracy
        memncpy(buf, accuracy, 13, 4);

        return buf;
    }

    public boolean decode(byte[] data) {
        
        if(null == data || data.length < 17) {
            return false;
        }
        
        mAccuracy = 0;
        mSensorType = -1;
   
        mSensorType = data[0];
        int i = 1;
        for (int j = 0; j < 3; j++) {

            mValues[j] = Float.intBitsToFloat(ByteUtil.getInt(data, i));

            i += 4;
        }
        
        mAccuracy = ByteUtil.getInt(data, 13);

        return true;
    }

    private int memncpy(byte[] buf, byte[] src, int offset, int byteCount) {
        for (int i = 0; i < byteCount; i++) {
            buf[i + offset] = src[i];
        }

        return byteCount;
    }
}
