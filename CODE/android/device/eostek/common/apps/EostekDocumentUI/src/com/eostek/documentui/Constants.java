
package com.eostek.documentui;

import android.annotation.SuppressLint;

@SuppressLint("SdCardPath")
public class Constants {
    public static final boolean isDebug = false;

    public static final String MENUACTION = "android.intent.action.DELETEMENU";

    public static final int DownloadedFragmentIndex = 0;

    public static final int DownloadingFragmentIndex = 1;

    public static final int DOWNLOADINGFLAG = 2;

    public static final int PAUSEFLAG = 3;

    public static final int FAILFLAG = 4;

    public static final int WAITINGFLAG = 5;

    public static final int CONTINUEFLAG = 6;

    public static final int DELETEFLAG = 7;
    
    public static final int NOTHINGFINISH = 1216;

    public static final String DOWNLOAD_INNER_LOCATION = "/data/data/com.android.providers.downloads/cache/";
}
