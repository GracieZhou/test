
package com.eostek.scifly.messagecenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import scifly.permission.Permission;

import com.eostek.scifly.messagecenter.util.Constants;
import com.eostek.scifly.messagecenter.util.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * projectName : SciflyMessageCenter
 * 
 * @author Shirley.jiang
 * @version 1.0.0
 * @time 2016-4-7 20:05
 */
public class MsgReceiver extends BroadcastReceiver {

    private static final String TAG = MsgReceiver.class.getSimpleName();

    private Context mContext;

    private boolean DBG = true;

    private final String BROADCAST_ACTION = "com.eostek.scifly.messagecenter.MsgReceiver";

    private final String RESPONSE_ACTION = "com.eostek.scifly.messagecenter.response";

    private final String BOOT_ANIMATION_FILE_PATH = "/data/local/bootanimation.zip";

    private final String BOOT_VIDEO_FILE_PATH = "/data/video/video.ts";

    private final String BOOT_VIDEO_DIR_PATH = "/data/video/";

    private static final String BOOT_VIDEO_FILE_NAME = "video.ts";

    private final String BOOT_VIDEO_PREDICT_FILE_NAME = "video_predict";

    private final String INTENT_KEY_PATH = "bootAnimationPath";

    private final String INTENT_KEY_MD5 = "bootAnimationMd5";

    private final long FILE_LENGTH_MAX = 20971520; // 20M

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (DBG) {
            Log.i(TAG, "SciflyMessageCenter, Version:1.0.0 Date:2016-4-7, Publisher:Shirley.jiang REV:43915");
        }

        if (BROADCAST_ACTION.equals(intent.getAction())) {
            String path = intent.getStringExtra(INTENT_KEY_PATH);
            String md5 = intent.getStringExtra(INTENT_KEY_MD5);
            setBootAnimation(path, md5);
        }
    }

    private void setBootAnimation(String fromPath, String md5) {
        boolean result = false;
        if (verify(fromPath, md5)) {
            if (fromPath.endsWith("bootanimation.zip")) {
                result = setBootAnimation(fromPath);
                result = result & deleteFile(result, false);
            } else {
                result = setBootVideo(fromPath);
                result = result & deleteFile(result, true);
            }
        }
        responseResult(result);
    }

    private boolean verify(String path, String md5) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(md5)) {
            return false;
        }

        File file = new File(path);
        if (!file.exists()) {
            Log.d(TAG, "Verify MD5, file " + path + " not exists");
            return false;
        }

        //文件格式，文件大小验证
        if (!"bootanimation.zip".equals(file.getName()) && !"video.ts".equals(file.getName())) {
            Log.d(TAG, path + " is not bootanimation.zip or video.ts");
            return false;
        }

        //文件大小验证
        if (file.length() > FILE_LENGTH_MAX) {
            Log.d(TAG, path + " is too large");
            return false;
        }

        //文件MD5验证
        String fileMd5 = Util.calcMD5(file);
        Log.d(TAG, "Verifly MD5, path=" + path + ", md5=" + md5 + ", fileMd5=" + fileMd5);
        return md5.equals(fileMd5);
    }

    private boolean setBootAnimation(String fromFile) {
        Permission permission = new Permission("JCheZXNzYWdlY2VudGVyanJt");
        boolean b_shell = permission.exec("cp " + fromFile + " " + BOOT_ANIMATION_FILE_PATH);
        if (b_shell) {
            permission.exec("chmod 644 " + BOOT_ANIMATION_FILE_PATH);
            Log.d(TAG, "copy file succeed ! ");
            return true;
        } else {
            Log.d(TAG, "copy file failed");
            return false;
        }
    }

    private boolean setBootVideo(String fromPath) {
        File file = new File(BOOT_VIDEO_DIR_PATH + BOOT_VIDEO_PREDICT_FILE_NAME);
        String toPath = "";
        if (file.exists() && !file.isDirectory()) {
            Log.d(TAG, "predict video is exist.");
            toPath = BOOT_VIDEO_DIR_PATH + "/" + BOOT_VIDEO_PREDICT_FILE_NAME;
        } else {
            Log.d(TAG, "predict video is not exist.");
            toPath = BOOT_VIDEO_FILE_PATH;
        }

        File toFile = new File(toPath);
        File toFileDir = toFile.getParentFile();
        if (!toFileDir.exists() || !toFileDir.isDirectory()) {
            Log.d(TAG, "to file dir make " + (toFileDir.mkdirs() ? "successed !" : "failed !"));
        }
        try {
            boolean success = Util.copyToFile(new FileInputStream(new File(fromPath)), toFile);
            Log.d(TAG, "cp " + fromPath + " " + toPath + (success ? " successed !" : " failed !"));
            if (success && toPath.endsWith(BOOT_VIDEO_PREDICT_FILE_NAME)) {
                File destFile = new File(toFileDir.getAbsolutePath() + "/" + BOOT_VIDEO_FILE_NAME);
                boolean renameSuccess = toFile.renameTo(destFile);
                Log.d(TAG, "rename from " + toFile.getAbsolutePath() + " to " + destFile.getAbsolutePath() + (renameSuccess ? "successed !" : "failed !"));
                return true;
            }
            return success;
        } catch (IOException e) {
            Log.d(TAG, "cp " + fromPath + " " + toPath + "failed");
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteFile(boolean result, boolean isPostVideo) {
        if (!result) {
            return false;
        }
        boolean isDeleteSuccess = false;
        if (isPostVideo) { //delete bootanimatin.zip
            File file = new File(BOOT_ANIMATION_FILE_PATH);
            if (file.exists() && !file.isDirectory()) {
                Permission permission = new Permission("JCheZXNzYWdlY2VudGVyanJt");
                isDeleteSuccess = permission.exec("rm " + BOOT_ANIMATION_FILE_PATH);
            } else {
                isDeleteSuccess = true;
            }
        } else {
            File file = new File(BOOT_VIDEO_FILE_PATH);
            if (file.exists() && !file.isDirectory()) {
                isDeleteSuccess = file.renameTo(new File(BOOT_VIDEO_DIR_PATH + BOOT_VIDEO_PREDICT_FILE_NAME));
            } else {
                isDeleteSuccess = true;
            }
        }
        Log.d(TAG, "delete file " + isDeleteSuccess);
        return isDeleteSuccess;
    }
    private void responseResult(boolean result) {
        Intent intent = new Intent(RESPONSE_ACTION);
        intent.putExtra("response", result ? 1 : -1);
        mContext.sendBroadcast(intent);
    }
}
