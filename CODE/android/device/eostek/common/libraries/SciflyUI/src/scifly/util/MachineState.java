
package scifly.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;
import android.util.Log;

/**
 * This class provides static public methods to get the percentage of CPU usage
 * and memory usage. Notice that, the method getCPUUsage() will block your
 * thread or process more then 1 second; the algorithm as below:
 * CPUUsage_Percnetage = ( 1 - delta(IdleCpuTime)/delta(TotalCpuTime) ) * 100;
 * delta(IdleCpuTime) = CurrentIdleCpuTime_Delay_1_Sec - CurrentIdleCpuTime;
 * delta(TotalCpuTime) = CurrentTotalCpuTime_Delay_1_Sec - CurrentTotalCpuTime;
 * 
 * @author frank.zhang
 * @since API 2.0
 * @created 2014-6-13
 */
public class MachineState {

    private static final String TAG = "MachineState";

    private static final String PROC_STAT = "/proc/stat";

    /**
     * @return the CPUUsage percentage to the caller if the machine does not
     *         support to get CPUUsage , it returns -1;
     */
    public static long getCPUUsage() {
        CPUTime startTime = new CPUTime();
        CPUTime endTime = new CPUTime();
        long mCpuUsage = -1;

        getcpuTime(startTime);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getcpuTime(endTime);

        long totalTime = endTime.getTotalTime() - startTime.getTotalTime();
        if (totalTime == 0) {
            mCpuUsage = 0;
        } else {
            mCpuUsage = (long) ((1 - (((double) (endTime.getIdleTime() - startTime.getIdleTime())) / totalTime)) * 100);
        }

        Log.d(TAG, "CPUUsage: " + mCpuUsage + "%\n");
        return mCpuUsage;
    }

    private static void getcpuTime(CPUTime t) {
        BufferedReader fr = null;
        try {
            fr = new BufferedReader(new InputStreamReader(new FileInputStream(PROC_STAT)));

            String oneLine = null;
            while ((oneLine = fr.readLine()) != null) {
                Log.d(TAG, oneLine);
                if (oneLine.startsWith("cpu  ")) {
                    String[] vals = oneLine.substring(5).split(" ");
                    if (vals.length != 10) {
                        Log.e(TAG, "read an error line string!");
                    } else {
                        t.setTotalTime(Long.parseLong(vals[0]) + Long.parseLong(vals[1]) + Long.parseLong(vals[2])
                                + Long.parseLong(vals[3]) + Long.parseLong(vals[4]) + Long.parseLong(vals[5])
                                + Long.parseLong(vals[6]) + Long.parseLong(vals[7]) + Long.parseLong(vals[8])
                                + Long.parseLong(vals[9]));
                        t.setIdleTime(Long.parseLong(vals[3]));
                        break;
                    }
                }
            }
            Log.d(TAG, "TotalCPUTime:" + t.getTotalTime() + "\n" + "IdleCPUTime:" + t.getIdleTime() + "\n");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * NOTICE: This method need your android API Level above 16 .
     * 
     * @param ctx: You can call {@link getApplicationContext()} to get a Context
     *            object
     * @return the memory usage percentage to the caller
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getMemUsage(Context ctx) {
        long MEM_UNUSED;
        long mTotal;
        long Proportion;

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        MEM_UNUSED = mi.availMem;
        mTotal = mi.totalMem;

        Proportion = (long) (((double) (mTotal - MEM_UNUSED) / (double) mTotal) * 100.00);
        Log.d(TAG, "MEM_UNUSED:" + MEM_UNUSED + "\nMEM_TOTAL:" + mTotal + "\nMemUsage:" + Proportion + "%");
        return Proportion;
    }

    public static boolean isNetworkIdle() {
        long start_rx = TrafficStats.getTotalRxBytes();
        long start_tx = TrafficStats.getTotalTxBytes();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end_rx = TrafficStats.getTotalRxBytes();
        long end_tx = TrafficStats.getTotalTxBytes();

        long delta_rx = end_rx - start_rx;
        long delat_tx = end_tx - start_tx;
        Log.d(TAG, "delta(rx):" + delta_rx + "\n" + "delta(tx):" + delat_tx);
        /** Experience network idle speed value < 50KB/s **/
        if ((delta_rx + delat_tx) < 50 * 1024) {
            Log.d(TAG, "isNetworkIdle:true");
            return true;
        } else {
            Log.d(TAG, "isNetworkIdle:false");
            return false;
        }
    }
}
