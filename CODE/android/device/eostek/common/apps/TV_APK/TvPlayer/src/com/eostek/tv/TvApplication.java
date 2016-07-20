
package com.eostek.tv;

import android.app.Application;

/**
 * projectName： Tv_2.13 moduleName： TvApplication.java
 * 
 * @author lucky.li
 * @version 1.0.0
 * @time 2015-2-13 上午11:24:22
 * @Copyright © 2012 MStar Semiconductor, Inc.
 */
public class TvApplication extends Application {

    private static TvApplication instance;

    public void onCreate() {
        instance = this;
    };

    /**
     * get the instance double lock
     * 
     * @return
     */
    public static TvApplication getInstance() {
        return instance;
    }

}
