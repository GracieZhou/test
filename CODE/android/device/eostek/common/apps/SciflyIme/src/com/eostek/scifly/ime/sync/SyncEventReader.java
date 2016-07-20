/**
 * SyncEventReader read or write message which contains msg_length+msg_body,
 * the first two bytes are the length of the message, so when you read or write any message,
 * firstly should read the first two bytes then read the len bytes message.
 */

package com.eostek.scifly.ime.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.eostek.scifly.ime.util.Constans;

/**
 * @author frank.zhang
 * @since API 2.4
 * @date 2014-12-1
 */
public class SyncEventReader {

    private static final String TAG = "SyncEventReader";

    private InputStream mIn;

    private OutputStream mOut;

    private Socket mSocket;

    private byte buf[] = new byte[1024];

    private int buflen = 0;

    public SyncEventReader(Socket socket) {
        mSocket = socket;
        try {
            mIn = new DataInputStream(mSocket.getInputStream());
            mOut = new DataOutputStream(mSocket.getOutputStream());
        } catch (IOException e) {
            Constans.printE(TAG, e.getMessage());
        }

    }

    private boolean readBytes(byte buffer[], int len) {

        if (mSocket.isClosed()) {
            return false;
        }

        int off = 0, count;
        if (len < 0)
            return false;
        while (off != len) {
            try {
                count = mIn.read(buffer, off, len - off);
                if (count <= 0) {
                    Constans.printE(TAG, "read error " + count);
                    break;
                }
                off += count;
            } catch (IOException ex) {
                close();
                Constans.printE(TAG, "read exception");
                return false;
            }
        }

        if (false) {
            Constans.printE(TAG, "read " + len + " bytes from iSynger");
        }

        if (off == len)
            return true;

        return false;
    }

    public boolean readReply() {

        int len;
        buflen = 0;
        // firstly, read 2 bytes .
        if (!readBytes(buf, 2))
            return false;

        // compute the length of this message.
        len = (((int) buf[0]) & 0xff) | ((((int) buf[1]) & 0xff) << 8);
        if ((len < 1) || (len > 1024)) {
            Constans.printE(TAG, "invalid reply length (" + len + ")");
            return false;
        }
        // then read the message body.
        if (!readBytes(buf, len))
            return false;

        buflen = len;

        return true;
    }

    public boolean writeSyncEvent(SyncEvent message) {
        byte[] msg = message.getBytes();
        int len = msg.length;

        if ((len < 1) || (len > 1024))
            return false;
        // fill in the first 2 bytes with the message length.
        buf[0] = (byte) (len & 0xff);
        buf[1] = (byte) ((len >> 8) & 0xff);

        try {
            if (mSocket.isConnected()) {
                // firstly, write the message's length to the socket.
                mOut.write(buf, 0, 2);
                // then ,write the message body to the socket.
                mOut.write(msg);
                mOut.flush();
            } else {
                return false;
            }
        } catch (IOException e) {
            close();
            Constans.printE(TAG, e.getMessage());
            return false;
        }

        return true;
    }

    public boolean writeReply(boolean result) {
        byte[] msg = String.valueOf(result).getBytes();
        int len = msg.length;

        if ((len < 1) || (len > 1024))
            return false;
        buf[0] = (byte) (len & 0xff);
        buf[1] = (byte) ((len >> 8) & 0xff);

        try {
            if (mSocket.isConnected()) {
                mOut.write(buf, 0, 2);
                mOut.write(msg);
                mOut.flush();
            } else {
                return false;
            }
        } catch (IOException e) {
            close();
            Constans.printE(TAG, e.getMessage());
            return false;
        }

        return true;
    }

    public byte[] getData() {

        byte[] data = new byte[buflen - 1];
        for (int i = 1; i < buflen; i++) {
            data[i - 1] = buf[i];
        }

        return data;
    }

    public byte getType() {
        return buf[0];
    }

    public void close() {
        try {
            if (null != mIn) {
                mIn.close();
            }

            if (null != mOut) {
                mOut.close();
            }
        } catch (IOException e) {
            Constans.printE(TAG, e.getMessage());
        }
    }
}
