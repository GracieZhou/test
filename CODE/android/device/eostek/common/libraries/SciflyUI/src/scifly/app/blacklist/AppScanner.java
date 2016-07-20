
package scifly.app.blacklist;

import java.util.List;
import java.util.Random;

import scifly.app.common.Commons;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

/**
 * @author frankzhang
 */
public class AppScanner extends Thread {

    private static final Commons.CommonLog LOG = new Commons.CommonLog(AppScanner.class.getSimpleName());

    private static final long LOOP_SLEEP_TIME = 60 * 60 * 1000;

    private static final int RANDOM_FACTOR_RANGE = 100;

    private static final int RANDOM_FACTOR_MIN = 0;

    private static final int RANDOM_FACTOR_MAX = 100;

    private BlacklistManager mBLManager;

    private ActivityManager mActivityManager;

    private Context mContext;

    /**
     * @param ctx use to get system service
     */
    public AppScanner(Context ctx) {
        LOG.d("Constructor");
        mContext = ctx;
        mBLManager = BlacklistManager.getInstance(ctx);
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void run() {

        if (!blacklistEnable()) {
            return;
        }

        while (true) {

            ActivityManager.RunningAppProcessInfo foregroundprocessInfo = getForegroundProcessInfo();
            if (null != foregroundprocessInfo && (Process.SYSTEM_UID != foregroundprocessInfo.uid)) {

                Random random = new Random(System.currentTimeMillis());
                int systemRF = random.nextInt(RANDOM_FACTOR_RANGE);
                LOG.d("System random factor is " + systemRF);

                if (mBLManager.isBlack(foregroundprocessInfo.processName)) {

                    int factor = mBLManager.getPkgFactor(foregroundprocessInfo.processName);

                    if (RANDOM_FACTOR_MIN == factor) {
                        LOG.d("skip process with factor equals zero : " + foregroundprocessInfo.processName);
                    } else if (RANDOM_FACTOR_MAX == factor || systemRF > factor) {
                        LOG.d("Killing process " + foregroundprocessInfo.processName + "(pid:"
                                + foregroundprocessInfo.pid + ")" + " with factor " + factor);
                        mActivityManager.forceStopPackage(foregroundprocessInfo.processName);
                    }
                }
                // release the reference
                foregroundprocessInfo = null;
            }

            if (mBLManager.shouldQuit()) {
                LOG.i("The blacklist is empty even though updated from server");
                return;
            }

            try {
                sleep(LOOP_SLEEP_TIME);
            } catch (InterruptedException e) {
                LOG.e(e.getMessage());
            }
        }
    }

    /**
     * @return all running app's RunningAppProcessInfo
     */
    public List<ActivityManager.RunningAppProcessInfo> getAllRunningApps() {
        return mActivityManager.getRunningAppProcesses();
    }

    /**
     * @param list dump the list to LOG ring buffer
     */
    public void dumpRunningProcesses(List<ActivityManager.RunningAppProcessInfo> list) {
        if (null == list || list.size() < 1) {
            LOG.e("RunningAppProcess is empty.");
            return;
        }

        LOG.d("RunningAppProcessInfo:{");
        for (ActivityManager.RunningAppProcessInfo processInfo : list) {
            LOG.d(String.format("\tprocess:%-32s\tpid:%-6d\tuid:%-6d", processInfo.processName, processInfo.pid,
                    processInfo.uid));
        }
        LOG.d("}");
    }

    private boolean blacklistEnable() {
        final int value = android.os.SystemProperties.getInt("ro.scifly.whitelist.enable", 0);
        return 1 == value;
    }

    private ActivityManager.RunningAppProcessInfo getForegroundProcessInfo() {
        List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        ActivityManager.RunningAppProcessInfo processInfo = processInfos.get(0);
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            LOG.d("runningPackage: " + processInfo.pkgList[0]);
            return processInfo;
        }
        return null;
    }

}
