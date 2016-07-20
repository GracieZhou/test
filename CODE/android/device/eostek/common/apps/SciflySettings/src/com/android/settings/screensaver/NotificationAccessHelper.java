
package com.android.settings.screensaver;

import java.util.HashSet;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.provider.Settings;

/*
 * projectName： Settings
 * moduleName： NotificationAccessHelper.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-7-1 上午10:22:34
 * @Copyright © 2014 Eos Inc.
 */

public class NotificationAccessHelper {
    /** the ComponentName to enable NotificationListenerService **/
    private ComponentName mEnableName;

    private final HashSet<ComponentName> mEnabledServices = new HashSet<ComponentName>();

    public NotificationAccessHelper(ComponentName name) {
        this.mEnableName = name;
    }

    /**
     * get all the enabled NotificationListenerService
     * 
     * @param mResolver The ContentResolver object
     */
    private void getEnableServices(ContentResolver mResolver) {
        mEnabledServices.clear();
        final String flat = Settings.Secure.getString(mResolver, Settings.Secure.ENABLED_NOTIFICATION_LISTENERS);
        if (flat != null && !"".equals(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    mEnabledServices.add(cn);
                }
            }
        }
    }

    /**
     * save all enabled NotificationListenerService to setting providers
     * 
     * @param mResolver The ContentResolver object
     */
    private void saveEnabledServices(ContentResolver mResolver) {
        StringBuilder sb = null;
        for (ComponentName cn : mEnabledServices) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(':');
            }
            sb.append(cn.flattenToString());
        }
        Settings.Secure.putString(mResolver, Settings.Secure.ENABLED_NOTIFICATION_LISTENERS, sb != null ? sb.toString()
                : "");
    }

    /**
     * save the given NotificationListenerService to setting providers
     * 
     * @param mResolver The ContentResolver object
     * @param name The ComponentName to be saved
     */
    public void saveCurentEnableService(ContentResolver mResolver) {
        getEnableServices(mResolver);
        if (mEnabledServices.contains(mEnableName)) {
            return;
        } else {
            mEnabledServices.add(mEnableName);
            saveEnabledServices(mResolver);
        }
    }

    /**
     * remove the given NotificationListenerService from setting providers
     * 
     * @param mResolver The ContentResolver object
     * @param name The ComponentName to be removed
     */
    public void removeCurentEnableService(ContentResolver mResolver) {
        getEnableServices(mResolver);
        if (mEnabledServices.contains(mEnableName)) {
            mEnabledServices.remove(mEnableName);
            saveEnabledServices(mResolver);
        } else {
            return;
        }
    }
}
