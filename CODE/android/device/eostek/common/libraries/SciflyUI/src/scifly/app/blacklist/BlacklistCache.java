
package scifly.app.blacklist;

import java.util.ArrayList;

import scifly.app.common.Commons;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * @author frankzhang
 */
public final class BlacklistCache {

    private static final Commons.CommonLog LOG = new Commons.CommonLog(BlacklistCache.class.getSimpleName());

    private static BlacklistCache sInstance;

    private Context mContext;

    private static ArrayList<Blacklist> mCacheEntity = new ArrayList<Blacklist>();

    private BlacklistCache(Context ctx) {
        mContext = ctx;
        load();
    }

    /**
     * @param ctx context for access database
     * @return  Singleton Instance of BlacklistCache
     */
    public static BlacklistCache getInstance(Context ctx) {
        LOG.d("getInstance");
        if (null == sInstance) {
            sInstance = new BlacklistCache(ctx);
        }

        return sInstance;
    }

    /**
     * @return true if cache entity has no entry , else false.
     */
    public boolean isEmpty() {
        return mCacheEntity.size() < 1;
    }

    /**
     * @return the size of cache entity's.
     */
    public int size() {
        return mCacheEntity.size();
    }

    /**
     * @param pkg package name of an app's.
     * @return true if the pkg match any cache entry.
     */
    public boolean isBlack(String pkg) {
        if (!TextUtils.isEmpty(pkg) && !isEmpty()) {
            for (Blacklist cell : mCacheEntity) {
                if (pkg.equals(cell.pkg)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param pkg package name of an app's.
     * @return the random factor of an app's.
     */
    public int getPkgFactor(String pkg) {
        if (!TextUtils.isEmpty(pkg) && !isEmpty()) {
            for (Blacklist cell : mCacheEntity) {
                if (pkg.equals(cell.pkg)) {
                    return cell.factor;
                }
            }
        }
        return -1;
    }

    /**
     * @param pkg package name of an app's.
     * @return the description of an app's.
     */
    public String getPkgDesc(String pkg) {
        LOG.d("getPkgDesc:" + pkg);
        if (!TextUtils.isEmpty(pkg) && !isEmpty()) {
            for (Blacklist cell : mCacheEntity) {
                if (pkg.equals(cell.pkg)) {
                    return cell.desc;
                }
            }
        }
        return "";
    }

    /**
     * Dump the current cached blacklist.
     */
    public void dumpCache() {
        if (mCacheEntity.size() > 0) {
            for (Blacklist cell : mCacheEntity) {
                LOG.d(cell.toString());
            }
        }
    }

    // ------------------------------------------------------------------------------
    // database related methods
    /**
     * @return true if really loaded data from database, else false.
     */
    public boolean load() {
        LOG.d("load");

        Cursor c = mContext.getContentResolver().query(Commons.SECURITY_TABLE_URI, new String[] {
                Commons.COLUMN_PKG, Commons.COLUMN_DESC, Commons.COLUMN_FACTOR
        }, null, null, null);
        if (null == c || c.getCount() < 1) {
            if (null != c) {
                c.close();
            }
            LOG.e("Database is empty for blacklist.");
            return false;
        }

        LOG.d("Will load " + c.getCount() + " records from DB.");
        c.moveToFirst();
        Blacklist first = new Blacklist();
        first.pkg = c.getString(c.getColumnIndex(Commons.COLUMN_PKG));
        first.factor = c.getInt(c.getColumnIndex(Commons.COLUMN_FACTOR));
        first.desc = c.getString(c.getColumnIndex(Commons.COLUMN_DESC));
        mCacheEntity.add(first);
        while (c.moveToNext()) {
            Blacklist cell = new Blacklist();
            cell.pkg = c.getString(c.getColumnIndex(Commons.COLUMN_PKG));
            cell.factor = c.getInt(c.getColumnIndex(Commons.COLUMN_FACTOR));
            cell.desc = c.getString(c.getColumnIndex(Commons.COLUMN_DESC));
            mCacheEntity.add(cell);
        }
        c.close();
        //dumpCache();

        return true;
    }

    /**
     * @return true if reload records from database, else false.
     */
    public boolean reload() {
        LOG.d("reload");
        if (!isEmpty()) {
            mCacheEntity.clear();
        }

        return load();
    }

}
