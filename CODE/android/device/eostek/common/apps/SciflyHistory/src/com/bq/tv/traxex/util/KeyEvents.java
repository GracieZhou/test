
package com.bq.tv.traxex.util;

import android.view.KeyEvent;

/**
 * Class of Key events.
 */
public class KeyEvents {
    /**
     * Get whether should skip navigation event.
     * 
     * @param keyEvent
     * @return
     */
    public static boolean shouldSkipNavigationEvent(KeyEvent keyEvent) {
        if (keyEvent.getRepeatCount() == 0) {
            return false;
        }

        if (keyEvent.getRepeatCount() < 40) {
            return (keyEvent.getRepeatCount() % 2 == 0);
        }

        return false;
    }
}
