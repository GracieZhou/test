
package com.eostek.isynergy.setmeup.application;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;

public class SetmeUpApplication extends Application {
    private Map<String, Activity> activityHis = new HashMap<String, Activity>();

    public Activity getActivity(String key) {
        return activityHis.get(key);
    }

    public void addActivity(String key, Activity activity) {
        activityHis.put(key, activity);
    }
}
