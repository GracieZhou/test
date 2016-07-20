
package scifly.permission;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import android.util.Log;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

public class Permission {

    private static final String TAG = "Permission";
    
    private boolean LOCAL_DEBUG = false;

    private InputStream mIn;

    private OutputStream mOut;

    private LocalSocket mSocket;

    private byte buf[] = new byte[1024];

    private int buflen = 0;
    
    private String mDigitalSignature = null;
    
    public Permission(String digitalSignature) {
        int pid = android.os.Process.myPid();
        mDigitalSignature = "[" + pid + "-" + digitalSignature +"]";
        if(LOCAL_DEBUG)
            Log.d(TAG,"digitalSignature:" + mDigitalSignature);
    }

    private boolean connect() {
        if(LOCAL_DEBUG)
            Log.d(TAG,"goto connect.");
        if (mSocket != null) {
            return true;
        }
        Log.i(TAG, "connecting...");
        try {
            mSocket = new LocalSocket();

            LocalSocketAddress address = new LocalSocketAddress("eshell", LocalSocketAddress.Namespace.RESERVED);

            mSocket.connect(address);

            mIn = mSocket.getInputStream();
            mOut = mSocket.getOutputStream();
        } catch (IOException ex) {
            disconnect();
            return false;
        }
        return true;
    }
    
    private void disconnect() {
        if(LOCAL_DEBUG)
            Log.i(TAG, "disconnecting...");
        try {
            if (mSocket != null)
                mSocket.close();
        } catch (IOException ex) {
        }
        try {
            if (mIn != null)
                mIn.close();
        } catch (IOException ex) {
        }
        try {
            if (mOut != null)
                mOut.close();
        } catch (IOException ex) {
        }
        mSocket = null;
        mIn = null;
        mOut = null;
    }

    private boolean readBytes(byte buffer[], int len) {
        if(LOCAL_DEBUG)
            Log.d(TAG,"goto readBytes.");
        int off = 0, count;
        if (len < 0)
            return false;
        while (off != len) {
            try {
                count = mIn.read(buffer, off, len - off);
                if (count <= 0) {
                    Log.e(TAG, "read error " + count);
                    break;
                }
                off += count;
            } catch (IOException ex) {
                Log.e(TAG, "read exception");
                break;
            }
        }
        if (LOCAL_DEBUG) {
            Log.i(TAG, "read " + len + " bytes");
        }
        if (off == len)
            return true;
        disconnect();
        return false;
    }
    
    private boolean readReply() {
        if(LOCAL_DEBUG)
            Log.d(TAG,"goto readReply.");
        int len;
        buflen = 0;
        if (!readBytes(buf, 2))
            return false;
        len = (((int) buf[0]) & 0xff) | ((((int) buf[1]) & 0xff) << 8);
        if ((len < 1) || (len > 1024)) {
            Log.e(TAG, "invalid reply length (" + len + ")");
            disconnect();
            return false;
        }
        if (!readBytes(buf, len))
            return false;
        buflen = len;
        return true;
    }
    
    private boolean writeCommand(String _cmd) {
        if(LOCAL_DEBUG)
            Log.d(TAG,"goto writeCommand.");
        _cmd += mDigitalSignature;
        byte[] cmd = _cmd.getBytes();
        int len = cmd.length;
        if ((len < 1) || (len > 1024))
            return false;
        buf[0] = (byte) (len & 0xff);
        buf[1] = (byte) ((len >> 8) & 0xff);
        try {
            mOut.write(buf, 0, 2);
            mOut.write(cmd, 0, len);
        } catch (IOException ex) {
            Log.e(TAG, "write error");
            disconnect();
            return false;
        }
        return true;
    }
    
    private synchronized String transaction(String cmd) {
        if(LOCAL_DEBUG)
            Log.d(TAG,"goto transaction.");
        if (!connect()) {
            Log.e(TAG, "connection failed");
            return "-1";
        }

        if (!writeCommand(cmd)) {
            Log.e(TAG, "write command failed? reconnect!");
            if (!connect() || !writeCommand(cmd)) {
                return "-1";
            }
        }
        if (LOCAL_DEBUG) {
            Log.i(TAG, "send: '" + cmd + "'");
        }
        if (readReply()) {
            String s = new String(buf, 0, buflen);
            if (LOCAL_DEBUG) {
                Log.i(TAG, "recv: '" + s + "'");
            }
            return s;
        } else {
            if (LOCAL_DEBUG) {
                Log.i(TAG, "fail");
            }
            return "-1";
        }
    }

    public boolean exec(String cmd) {
        String res = transaction(cmd);
        try {
            disconnect();
            return Integer.parseInt(res) == 0 ? true : false;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
}
