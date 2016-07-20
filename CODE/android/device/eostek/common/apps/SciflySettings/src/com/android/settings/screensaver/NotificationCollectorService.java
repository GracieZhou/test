package com.android.settings.screensaver;

import android.content.Intent;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/*
 * projectName： Settings
 * moduleName： NotificationCollectorService.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-6-29 下午2:47:31
 * @Copyright © 2014 Eos Inc.
 */

public class NotificationCollectorService extends NotificationListenerService  {
    
    private final Intent mDreamingStoppedIntent = new Intent(Intent.ACTION_DREAMING_STOPPED)
    .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);

    /* (non-Javadoc)
     * @see android.service.notification.NotificationListenerService#onNotificationPosted(android.service.notification.StatusBarNotification)
     */
    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        Log.v("NotificationCollectorService", "onNotificationPosted");
        getApplicationContext().sendBroadcastAsUser(mDreamingStoppedIntent, UserHandle.ALL);
    }

    /* (non-Javadoc)
     * @see android.service.notification.NotificationListenerService#onNotificationRemoved(android.service.notification.StatusBarNotification)
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {
        Log.v("NotificationCollectorService", "onNotificationRemoved");
    }

}
