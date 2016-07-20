
package scifly.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

/**
 * 
 * @author frank.zhang@ieostek.com
 * @created 2014-5-30
 * @copyright all rights reserved by EosTek
 *
 */

/**
 * @comment class Ping provided some methods to implement to linux ping command
 *          caller can use some methods to get what they wanted. NOTICE: Before
 *          you use it in your APKs , you must add permission: <uses-permission
 *          android:name="android.permission.INTERNET"/> in AndroidManifest.xml
 */
public class Ping {

    /**
     * @param args
     */
    private static final String TAG = "Ping";

    private boolean mFlag = false;

    // ping cmd args
    private String mHost = null;

    private int mPackageNum = 5;

    private int mPackageSize = 40;

    private int mTimeout = 3;

    // ping cmd received args
    private long mReceivedPackageCnt = 0;

    private long mLostPackagePercent = 0;

    private long mTotalTime = 0;

    private double mAvgRespondTime = 0;

    private double minRespondTime = 0;

    private double maxRespondTime = 0;

    private String mExecOutput = null;

    // Constructor
    public Ping(String host) {
        mHost = host;
    }

    // Constructor
    public Ping(String host, int packageNum, int packageSize, int timeout) {
        mHost = host;
        mPackageNum = packageNum;
        mPackageSize = packageSize;
        mTimeout = timeout / 1000;
    }

    /**
     * @see after you new an instance of class Ping with the constructor, you
     *      should call this method to run the ping cmd
     * @return true or false
     */
    public boolean run() {

        String cmd = "ping -q -c " + mPackageNum + " -s " + mPackageSize + " -W " + mTimeout + " " + mHost;
        Log.d(TAG, "cmd:" + cmd);

        String output = this.exec(cmd);
        if (output.equals("") || output == null || output.contains("ping: unknown host")) {
            Log.e(TAG, "run cmd error !");
            return false;
        } else {
            if (processOutput(output)) {
               
                if (mReceivedPackageCnt < 1) {
                    return false;
                }
                
                mFlag = true;
                
                return true;
            } else {
                Log.e(TAG, "Process output failed !");
                return false;
            }
        }
    }

    /**
     * @see You can call these methods to get the ping cmd results of received
     *      packages count
     */
    public long getReceivedPackage() {
        if (mFlag) {
            return mReceivedPackageCnt;
        } else {
            return -1;
        }
    }

    /**
     * @see You can call these methods to get the ping cmd results of Lost
     *      packages Percent(already percentage * 100)
     */
    public long getLostPackagePercent() {
        if (mFlag) {
            return mLostPackagePercent;
        } else {
            return -1;
        }
    }

    /**
     * @see You can call these methods to get the ping cmd results of total time
     *      (unit: ms)
     */
    public long getTotalTime() {
        if (mFlag) {
            return mTotalTime;
        } else {
            return -1;
        }
    }

    /**
     * @see You can call these methods to get the ping cmd results of min
     *      respond time (unit: ms)
     */
    public double getMinRespondTime() {
        if (mFlag) {
            return minRespondTime;
        } else {
            return -1.0;
        }
    }

    /**
     * @see You can call these methods to get the ping cmd results of avg
     *      respond time (unit: ms)
     */
    public double getAvgRespondTime() {
        if (mFlag) {
            return mAvgRespondTime;
        } else {
            return -1.0;
        }
    }

    /**
     * @see You can call these methods to get the ping cmd results of max
     *      respond time (unit: ms)
     */
    public double getMaxRespondTime() {
        if (mFlag) {
            return maxRespondTime;
        } else {
            return -1.0;
        }
    }

    /**
     * @see You can call these methods to get the ping cmd results
     */
    public String print() {
        return (mReceivedPackageCnt + " received, " + mLostPackagePercent + "% packet loss, time " + mTotalTime + "ms"
                + "\n" + "rtt min/avg/max = " + minRespondTime + "/" + mAvgRespondTime + "/" + maxRespondTime);
    }

    private String exec(String cmd) {
        try {

            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            mExecOutput = output.toString();
            return mExecOutput;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean processOutput(String output) {
        try {
            int index = output.indexOf("transmitted, ", 0);
            mReceivedPackageCnt = atoi(output.substring(index + "transmitted, ".length()));

            index = output.indexOf("received, ", 0);
            mLostPackagePercent = atoi(output.substring(index + "received, ".length()));

            index = output.indexOf("time ", 0);
            mTotalTime = atoi(output.substring(index + "time ".length()));

            index = output.indexOf("rtt min/avg/max/mdev = ", 0);
            String subStr = output.substring(index + "rtt min/avg/max/mdev = ".length());

            minRespondTime = atod(subStr);

            index = subStr.indexOf('/');
            mAvgRespondTime = atod(subStr.substring(index + 1));

            index = subStr.indexOf('/', index + 1);
            maxRespondTime = atod(subStr.substring(index + 1));

            Log.d(TAG, mReceivedPackageCnt + " received, " + mLostPackagePercent + "% packet loss, time " + mTotalTime
                    + "ms");
            Log.d(TAG, "rtt min/avg/max = " + minRespondTime + "/" + mAvgRespondTime + "/" + maxRespondTime);

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private long atoi(String str) throws Exception {

        boolean negative = false;
        long value = 0;

        if (str == null || str.equals("")) {
            throw new Exception("null string or the string has no character!");
        }
        int i = 0;
        while (str.charAt(i) == ' ' || str.charAt(i) == '+')
            i++;
        while (str.charAt(i) == '-') {
            negative = true;
            i++;
        }

        while (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
            value = value * 10 + (str.charAt(i) - '0');
            if (value > Integer.MAX_VALUE) {
                throw new Exception("OUT OF INTEGER RANGE");
            }
            i++;
        }

        return negative == true ? value * -1 : value;
    }

    private double atod(String str) throws Exception {

        boolean negative = false;
        double value = 0;

        if (str == null || str.equals("")) {
            throw new Exception("null string or the string has no character!");
        }
        int i = 0;
        while (str.charAt(i) == ' ' || str.charAt(i) == '+')
            i++;
        while (str.charAt(i) == '-') {
            negative = true;
            i++;
        }

        while (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
            value = value * 10.0 + (str.charAt(i) - '0');
            if (value > Double.MAX_VALUE) {
                throw new Exception("OUT OF DOUBLE RANGE");
            }
            i++;
        }

        if (str.charAt(i) == '.') {
            i++;
            double exp = 1;
            while (str.charAt(i) >= '0' && str.charAt(i) <= '9') {

                value += (str.charAt(i) - '0') * Math.pow(0.1, exp++);
                if (value > Double.MAX_VALUE) {
                    throw new Exception("OUT OF DOUBLE RANGE");
                }
                i++;
            }
        }

        return negative == true ? value * -1 : value;
    }
}
