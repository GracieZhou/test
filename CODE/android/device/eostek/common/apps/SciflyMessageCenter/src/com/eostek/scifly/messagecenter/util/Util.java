
package com.eostek.scifly.messagecenter.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scifly.provider.metadata.Msg;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.eostek.scifly.video.player.service.VideoInfo;

/**
 * utils for project.
 */
public class Util {

    private static final String TAG = Util.class.getSimpleName();

    /**
     * get diemnsion pixel size
     * 
     * @param context
     * @param id
     * @return
     */
    public static int getDiemnsionPixelSize(Context context, int id) {
        return context.getResources().getDimensionPixelSize(id);
    }

    /**
     * @param time from Msg.mTime.
     * @param format of {@link Constants.TIME_YMD},{@link Constants.TIME_YMD_HM}
     *            ,{@link Constants.TIME_YMD_HMS}
     * @return time string
     */
    @SuppressLint("SimpleDateFormat")
    public static String mill2String(long time, String format) {
        long time_in_millis = time * 1000;
        String timeLabel = new SimpleDateFormat(format).format(new Date(time_in_millis));
        return timeLabel;
    }

    /**
     * copy files by file to file.
     * 
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            copyFile(inBuff, targetFile);
        } finally {
            // 关闭流
            if (inBuff != null) {
                inBuff.close();
            }
        }
    }

    /**
     * copy files by Inputstream to files.
     * 
     * @param inputStream
     * @param toFile
     */
    public static void copyFile(InputStream inputStream, File toFile) {
        if (!toFile.exists()) {
            try {
                toFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(toFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, count);
                }
                outputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Copy data from a source stream to destFile.
     * @param inputStream : source stream
     * @param destFile : destFile
     * @return Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                Log.d(TAG, "rm " + destFile + (destFile.delete() ? " successed!" : " failed!"));
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);

                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * MD5 encryption string.
     * 
     * @param input input string
     * @return
     */
    public static String calcMD5(String input) {
        return calcMD5(input.getBytes());
    }

    private static final int OFFSET_0XFF = 0xff;

    private static final int MD5_SUBSTRING_LENGTH = 16;

    private static String calcMD5(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        // generate MessageDigest
        md.update(data);
        byte[] hash = md.digest();

        // translate to string
        StringBuffer sbRet = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            int v = hash[i] & OFFSET_0XFF;
            if (v < MD5_SUBSTRING_LENGTH) {
                sbRet.append("0");
            }
            sbRet.append(Integer.toString(v, MD5_SUBSTRING_LENGTH));
        }
        return sbRet.toString();
    }

    /**
     * Calculate MD5 code of the given file.
     * 
     * @param file File that need to be calculate.
     * @return MD5 code String.
     */
    public static String calcMD5(File file) {
        FileInputStream is = null;
        MessageDigest digest = null;
        byte[] buffer = new byte[1024];
        int length;
        try {
            digest = MessageDigest.getInstance("MD5");
            is = new FileInputStream(file);
            while ((length = is.read(buffer)) != -1) {
                digest.update(buffer, 0, length);
            }
            byte[] hash = digest.digest();
            StringBuffer sbRet = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                int v = hash[i] & OFFSET_0XFF;
                if (v < MD5_SUBSTRING_LENGTH) {
                    sbRet.append(0);
                }
                sbRet.append(Integer.toString(v, MD5_SUBSTRING_LENGTH));
            }
            return sbRet.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * get path by name.
     * 
     * @param desPath
     * @param suffix
     * @param uriStr
     * @return
     */
    public static String getPathByName(String desPath, String suffix, String uriStr) {
        if (TextUtils.isEmpty(uriStr)) {
            uriStr = "null";
        }

        String fileName = "unknown";
        try {
            // calcMD5(uriStr.getBytes());
            fileName = String.valueOf(uriStr.hashCode());

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        if (suffix != null) {
            return desPath + File.separator + fileName + suffix;
        }
        return desPath + File.separator + fileName;
    }

    /**
     * get uri by name.
     * 
     * @param desPath
     * @param suffix
     * @param uriStr
     * @return
     */
    public static Uri getUriByName(String desPath, String suffix, String uriStr) {
        if (TextUtils.isEmpty(uriStr)) {
            uriStr = "null";
        }

        String fileName = "unknown";
        try {
            fileName = String.valueOf(uriStr.hashCode());// calcMD5(uriStr.getBytes());

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        if (suffix != null) {
            return Uri.parse("file://" + desPath + File.separator + fileName + suffix);
        }
        return Uri.parse("file://" + desPath + File.separator + fileName);
    }

    /**
     * whether file is exist.
     * 
     * @param uri
     * @return
     */
    public static boolean fileExists(Uri uri) {
        String filePath = uri.getPath();

        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * cut string.
     * 
     * @param str
     * @param len
     * @return
     */
    public static String cutString(String str, int len) {
        try {
            if (TextUtils.isEmpty(str)) {
                return "";
            }
            int counterOfDoubleByte = 0;
            byte[] b = str.getBytes("gb2312");
            if (b.length <= len) {
                return str;
            }
            for (int i = 0; i < len; i++) {
                if (b[i] < 0) {
                    counterOfDoubleByte++;
                }
            }
            if (counterOfDoubleByte % 2 == 0) {
                return new String(b, 0, len, "gb2312") + "...";
            } else {
                return new String(b, 0, len - 1, "gb2312") + "...";
            }
        } catch (Exception ex) {
            return "";
        }
    }

    public static boolean isRoll(String str, int len) {
        try {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            int counterOfDoubleByte = 0;
            byte[] b = str.getBytes("gb2312");
            if (b.length <= len) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * parse video info
     * 
     * @param msg
     * @return
     */
    public static VideoInfo parseVideoInfo(Msg msg) {
        String mData = msg.mData;
        VideoInfo info = new VideoInfo();
        String url = "";
        try {
            url = mData.substring(0, mData.lastIndexOf("?"));

            String lastInfo = mData.substring(mData.lastIndexOf("?") + 1);
            System.out.println(lastInfo);
            String[] values = lastInfo.split("&");
            for (String value : values) {
                String[] mp = value.split("=");
                if (mp[0].equals("sourceUrl")) {
                    url = mp[1];
                } else if (mp[0].equals("programId")) {
                    info.programId = Integer.parseInt(mp[1]);
                } else if (mp[0].equals("pgrpId")) {
                    info.pgrpId = Integer.parseInt(mp[1]);
                } else if (mp[0].equals("channelCode")) {
                    info.channelCode = mp[1];
                } else if (mp[0].equals("videoSource")) {
                    info.videoSource = mp[1];
                } else if (mp[0].equals("videoName")) {
                    String fileName = URLDecoder.decode(mp[1]);
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = msg.mTitle;
                    }

                    int position = fileName.indexOf("$");
                    if (position != -1) {
                        info.videoName = fileName.substring(position + 1, fileName.length());
                    } else {
                        info.videoName = fileName;
                    }

                } else if (mp[0].equals("curPosition")) {
                    info.curPosition = Integer.parseInt(mp[1]);
                } else if (mp[0].equals("videoType")) {
                    info.videoType = Integer.parseInt(mp[1]);
                } else if (mp[0].equals("programIndex")) {
                    info.programIndex = Integer.parseInt(mp[1]);
                } else if (mp[0].equals("hd")) {
                    info.hd = Integer.parseInt(mp[1]);
                } else if (mp[0].equals("contentId")) {
                    info.contentId = mp[1];
                } else if (mp[0].equals("pgmContentId")) {
                    info.pgmContentId = mp[1];
                }
            }
        } catch (Exception e) {
            info.videoType = 0;
        }

        info.videoUrl = url;

        return info;
    }

    /**
     * parse live info
     * 
     * @param msg
     * @return
     */
    public static HashMap<String, String> parseLiveInfo(String msgData) {
        HashMap<String, String> liveInfo = new HashMap<String, String>();
        if (msgData.contains(Constants.LIVE_SCIFLYKU)) {
            liveInfo.put(Constants.LIVE_KEY_PLAYURL, msgData.substring(0, msgData.indexOf(Constants.LIVE_SCIFLYKU) - 1));
            liveInfo.put(Constants.LIVE_KEY_PLAYTITLE, msgData.substring(msgData.lastIndexOf("=") + 1));
        } else {
            String lastInfo = msgData.substring(msgData.lastIndexOf("?") + 1);
            String[] values = lastInfo.split("&");
            for (String value : values) {
                String[] mp = value.split("=");
                if (mp[0].equals(Constants.LIVE_KEY_PGM_ID)) {
                    liveInfo.put(Constants.LIVE_KEY_PGM_ID, mp[1]);
                } else if (mp[0].equals(Constants.LIVE_KEY_CONTENT_ID)) {
                    liveInfo.put(Constants.LIVE_KEY_CONTENT_ID, mp[1]);
                }
            }
        }
        return liveInfo;
    }

    public static Map parseUrl(Msg msg) {
        Map<String, String> map = new HashMap<String, String>();
        String url = msg.mData;
        String[] subUrl = url.split("[?]");
        if (subUrl.length > 1) {
            if (subUrl[1] != null) {
                String[] paramArrUrl = subUrl[1].split("[&]");
                for (String param : paramArrUrl) {
                    String[] keyValue = param.split("[=]");
                    map.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return map;
    }

    public static boolean isInstalled(Msg msg, Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> packinfos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo installedInfo : packinfos) {
            if (installedInfo.packageName != null && installedInfo.packageName.equals(parseUrl(msg).get("id"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the extensions of the file by name.
     * 
     * @example filename : "music.mp3", return : ".mp3"
     * @param fileName
     * @return
     */
    public static String getExtensionName(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length() - 1))) {
                return fileName.substring(dot);
            }
        }
        return fileName;
    }
}
