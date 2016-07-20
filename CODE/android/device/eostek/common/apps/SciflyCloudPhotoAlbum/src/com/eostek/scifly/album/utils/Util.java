
package com.eostek.scifly.album.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Util {
    private static Toast mToast;

    /**
     * @Title: isPictrueFile.
     * @Description: 判断文件是否是否是图片文件.
     * @param: @param checkString
     * @param: @return.
     * @return: boolean.
     * @throws
     */
    public static boolean isPictrueFile(String checkString) {
        return checkString.endsWith("gif") || checkString.endsWith("jpeg") || checkString.endsWith("jpg")
                || checkString.endsWith("jpe") || checkString.endsWith("png") || checkString.endsWith("tiff")
                || checkString.endsWith("bmp") || checkString.endsWith("icon") || checkString.endsWith("svg");
    }

    /**
     * to judge whether connect network current.
     * 
     * @param Context
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * used to toast message.
     * 
     * @param Context ,message
     * @return void
     */
    public static void showToast(Context context, int id) {
        String message = context.getResources().getString(id);
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

}
