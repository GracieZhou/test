
package com.eostek.tvmenu;

import com.eostek.tvmenu.utils.Constants;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

public class TvMenuLogic {
    Activity mActivity;

    TvMenuLogic(Activity activity) {
        mActivity = activity;
    }

    /**
     * query the current input source
     * 
     * @return InputSourceType
     */
    public int queryCurInputSrc() {
        int value = 0;
        Cursor cursor = mActivity.getContentResolver().query(Uri.parse(Constants.USERSETTING_SYSTEMSETTING),
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex(Constants.ENINPUTSOURCETYPE));
        }
        if (cursor != null) {
            cursor.close();
        }
        return value;
    }
}
