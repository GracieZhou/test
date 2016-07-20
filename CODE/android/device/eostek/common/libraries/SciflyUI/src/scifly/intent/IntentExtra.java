
package scifly.intent;

import android.content.Intent;

public class IntentExtra extends Intent {

    /**
     * Broadcast Action: Blacklist have changed.
     */
    public static final String ACTION_BLACK_LIST_CHANGED = "com.eostek.scifly.intent.action.BLACKLIST_CHANGED";

    /**
     * @hide Broadcast Action: Broadcast for all interested module that the
     *       authorize stated changed.
     */
    public static final String ACTION_AUTHORIZE_STATE_CHANGED = "com.eostek.scifly.intent.action.AUTHORIZE_STATE_CHANGED";

    /**
     * @hide extra key for intent using to show authorize state
     */
    public static final String EXTRA_AUTHORIZE_STATE = "authorize_state";

    /**
     * @hide extra key for intent using to show authorize message
     */
    public static final String EXTRA_AUTHORIZE_MESSAGE = "authorize_message";

    /**
     * @hide authorize failed.
     */
    public static final int AUTHORIZE_STATE_FAILED = 0;

    /**
     * @hide authorize successful.
     */
    public static final int AUTHORIZE_STATE_SUCCESSFUL = 1;

    /**
     * @hide Broadcast Action: Broadcast for all interested module that the user
     *       press "reset" button.
     */
    public static final String ACTION_RESET_BUTTON = "com.eostek.scifly.intent.action.RESET_BUTTON";

    /**
     * @hide Broadcast Action: Broadcast for all interested module that the user
     *       long press "reset" button.
     */
    public static final String ACTION_LONG_PRESS_RESET_BUTTON = "com.eostek.scifly.intent.action.LONG_PRESS_RESET_BUTTON";

    /**
     * @hide Broadcast Action: Broadcast for all interested module that the device
     *       location changed
     */
    public static final String ACTION_LOCATION_CHANGED = "com.eostek.scifly.intent.action.LOCATION_CHANGED";

}
