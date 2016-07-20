
package com.eostek.history.model;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class of message.
 */
public class Messages {
    private static final String BUNDLE_NAME = "com.eostek.history.model.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    /**
     * Get string from key.
     * 
     * @param key
     * @return
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
