
package scifly.app.blacklist;

import scifly.app.common.Commons;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

/**
 * @author frankzhang
 */
public final class BlacklistManager {

    private Context mContext;

    private static final Commons.CommonLog LOG = new Commons.CommonLog(BlacklistManager.class.getSimpleName());

    private static BlacklistManager sInstance;

    private BlacklistCache mBlacklistCache;

    private BlacklistUpdater mBlacklistUpdater;

    private boolean mShouldQuit = false;

    private boolean mUpdateOnce = false;

    /**
     * BlacklistHandler handle the update locale blacklist database from remote server.
     * @author frankzhang
     *
     */
    private class BlacklistHandler extends Handler {
        static final int MSG_UPDATE_BLACKLIST = 100;

        public BlacklistHandler(Looper looper) {
            super(looper);
            LOG.d("BlacklistHandler");
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_BLACKLIST:
                    if (mBlacklistUpdater.update()) {
                        mContext.unregisterReceiver(mConnectionRecevier);
                        if (mBlacklistCache.isEmpty()) {
                            mShouldQuit = true;
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * @return true if the cache still empty after updated from remote server.
     */
    public boolean shouldQuit() {
        return mShouldQuit;
    }

    private BlacklistHandler mHandler;

    private ContentObserver mDBObserver = new ContentObserver(null) {
        public void onChange(boolean selfChange) {
            LOG.d("onChange:" + selfChange);
            mBlacklistCache.reload();
        }
    };

    private BroadcastReceiver mConnectionRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            LOG.d("onReceive::" + intent.toString());
            if (Commons.isNetworkConnected(mContext) && !mUpdateOnce) {
                LOG.d("isNetworkConnected = true");
                // pull blacklists from server
                mUpdateOnce = true;
                mHandler.obtainMessage(BlacklistHandler.MSG_UPDATE_BLACKLIST).sendToTarget();
            }
        }
    };

    private void registerDBObserver() {
        LOG.d("registerDBObserver");
        mContext.getContentResolver().registerContentObserver(Commons.SECURITY_TABLE_URI, true, mDBObserver);
    }

    private void registerConnectionRecevier() {
        LOG.d("registerConnectionRecevier");
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mConnectionRecevier, iFilter);
    }

    // -------------------------------------------------------------
    private BlacklistManager(Context ctx) {
        LOG.d("BlacklistManager");
        mContext = ctx;
        mBlacklistCache = BlacklistCache.getInstance(ctx);
        registerDBObserver();
        registerConnectionRecevier();

        HandlerThread ht = new HandlerThread("update thread", Process.THREAD_PRIORITY_BACKGROUND);
        ht.start();
        mHandler = new BlacklistHandler(ht.getLooper());
        mBlacklistUpdater = new BlacklistUpdater(ctx);
    }

    /**
     * @param ctx context form register observers.
     * @return  Singleton BlacklistManager instance.
     */
    public static BlacklistManager getInstance(Context ctx) {
        LOG.d("getInstance");
        if (null == sInstance) {
            sInstance = new BlacklistManager(ctx);
        }
        return sInstance;
    }

    /**
     * @param pkg package name of an app's.
     * @return true if the pkg match any cache entry.
     */
    public boolean isBlack(String pkg) {
        boolean isBlack = mBlacklistCache.isBlack(pkg);
        // LOG.d("isBlack:: " + pkg + " : " + isBlack);
        return isBlack;
    }

    /**
     * @param pkg package name of an app's.
     * @return the random factor of an app's.
     */
    public int getPkgFactor(String pkg) {
        return mBlacklistCache.getPkgFactor(pkg);
    }

    /**
     * @param pkg package name of an app's.
     * @return the description of an app's.
     */
    public String getPkgDesc(String pkg) {
        return mBlacklistCache.getPkgDesc(pkg);
    }

}
