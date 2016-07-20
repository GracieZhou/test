/**
 * SyncEvent wrap the event of synchronize event, it's structure as below:
 * byte[0]: the type of this event, maybe words or sensor.
 * byte[1 ~ n]: the event data of this type.
 */

package com.eostek.scifly.ime.sync;

/**
 * @author frank.zhang
 * @since API 2.4
 * @date 2014-12-1
 */
public class SyncEvent {

    public byte type;

    public static final byte TYPE_WORDS = 1;

    public static final byte TYPE_SENSOR = 2;

    public byte[] data;

    public SyncEvent(byte type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public SyncEvent(byte type, String words) {
        this.type = type;
        this.data = words.getBytes();
    }

    public byte[] getBytes() {

        if (null == data) {
            return null;
        }

        byte[] msg = new byte[1 + data.length];

        msg[0] = type;
        for (int i = 1; i < msg.length; i++) {
            msg[i] = data[i - 1];
        }

        return msg;
    }

}
