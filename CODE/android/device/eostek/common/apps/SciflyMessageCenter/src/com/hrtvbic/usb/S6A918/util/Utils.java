
package com.hrtvbic.usb.S6A918.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import scifly.provider.SciflyStore;
import scifly.provider.metadata.Footprint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.widget.Toast;

import com.hrtvbic.usb.S6A918.model.FileItem;
//import scifly.tv.provider.metadata.FootPrints;
//import scifly.tv.provider.SciflyStore;
//import scifly.tv.provider.SciflyStore.Footprints;
//import com.mstar.android.tv.TvCommonManager;
//import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

public class Utils {
    private static final String TAG = "Utils";

    // private static EnumInputSource inputSource = null;
    // private static TvCommonManager appSkin = null;

    public static final String CACHE_DIR = "/data/data/com.hrtvbic.usb.S6A918";

    public static final String THUMBNAIL_DIR = CACHE_DIR + "/thumbnail";

    public static final String TEMPORARY_FILE = CACHE_DIR + "/temp";

    public static boolean deleteFile(String path) {
        if (path == null) {
            return false;
        }

        return deleteFile(new File(path));
    }

    public static boolean deleteFile(File file) {
        if (file == null) {
            return false;
        }

        if (file.exists()) {
            if (file.isDirectory()) {
                if (file.list() != null && file.list().length > 0) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
            }

            return file.delete();
        }

        return false;
    }

    public static boolean clearDir(String path) {
        if (path == null) {
            return false;
        }

        return clearDir(new File(path));
    }

    public static boolean clearDir(File file) {
        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            if (file.list() != null && file.list().length > 0) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }

            return true;
        }

        return false;
    }

    public static String getDisplayActivityPackageName(Context context) {
        if (context == null) {
            return "";
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return "";
        }

        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getPackageName();
    }

    public static String getDisplayActivityClassName(Context context) {
        if (context == null) {
            return "";
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return "";
        }

        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    public static void showToast(Context context, int resId, int duration) {
        Toast.makeText(context.getApplicationContext(), resId, duration).show();
    }

    public static int getIntPref(Context context, String name, int def) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(name, def);
    }

    public static void setIntPref(Context context, String name, int value) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Editor ed = prefs.edit();
        ed.putInt(name, value);
        SharedPreferencesCompat.apply(ed);
    }

    /*
     * Get video subtitle file path list
     */
    public static List<String> getSubtitlePathList(String path) {
        List<String> list = new ArrayList<String>();
        File file = new File(path);
        if (path == null || !file.exists()) {
            return list;
        }

        return getSubtitlePathList(file);
    }

    /*
     * Get video subtitle file path list
     */
    public static List<String> getSubtitlePathList(File file) {
        List<String> list = getStartWithNameList(file);
        if (list.size() == 0) {
            return list;
        }

        for (int i = 0; i < list.size(); ++i) {
            if (!checkExtension(list.get(i), FileUtils.SUBTITLE_EXTENSION)) {
                list.remove(i);
                i--;
            }
        }
        // if had the .idx file and .sub file then delete the corresponding .sub
        // file
        // if had only .sub file then do nothing
        String name1 = "";
        String name2 = "";
        for (int i = 0; i < list.size(); ++i) {
            boolean check1 = checkExtension(list.get(i), new String[] {
                ".idx"
            });
            name1 = list.get(i).substring(0, list.get(i).lastIndexOf('.'));
            if (check1) {
                for (int j = 0; j < list.size(); ++j) {
                    name2 = list.get(j).substring(0, list.get(j).lastIndexOf('.'));
                    boolean check2 = checkExtension(list.get(j), new String[] {
                        ".sub"
                    });
                    if (name1.equals(name2) && check2) {
                        list.remove(j);
                        j--;
                    }
                }
            }
        }
        return list;
    }

    public static List<String> getStartWithNameList(String path) {
        List<String> list = new ArrayList<String>();
        File file = new File(path);
        if (path == null || !file.exists()) {
            return list;
        }

        return getStartWithNameList(file);
    }

    public static List<String> getStartWithNameList(File file) {
        List<String> list = new ArrayList<String>();
        if (file == null || !file.exists()) {
            return list;
        }

        String[] fileList = file.getParentFile().list();
        if (fileList == null || fileList.length <= 0) {
            return list;
        }

        String fileName = getFileTitle(file.getAbsolutePath());
        File[] files = file.getParentFile().listFiles();
        for (File currentFile : files) {
            if (currentFile.getName().startsWith(fileName)) {
                list.add(currentFile.getAbsolutePath());
            }
        }

        return list;
    }

    /*
     * Get file name with extension
     */
    public static final String getFileName(String path) {
        if (path == null) {
            return "";
        }

        int index = path.lastIndexOf("/");
        if (index != -1) {
            return path.substring(index + 1);
        }

        return "";
    }

    /*
     * Get file name without extension
     */
    public static final String getFileTitle(String path) {
        if (path == null) {
            return "";
        }

        int index = path.lastIndexOf("/");
        if (index != -1) {

            int i = path.lastIndexOf(".");
            if (i != -1) {
                return path.substring(index + 1, i);
            }

            return path.substring(index + 1);
        }

        return "";
    }

    public static boolean checkExtension(String checked, String[] extensions) {
        if (checked == null) {
            return false;
        }

        for (String end : extensions) {
            if (checked.toLowerCase().endsWith(end)) {
                return true;
            }
        }

        return false;
    }

    /*
     * Switch source from other source to storage.
     */
    public static void switchSourceStorage(final boolean isEnter) {

    }

    /**
     * Format milliseconds to hh:mm:ss
     */
    public static String formatMillisecond(long millisecond) {
        long time = millisecond / 1000;
        if (time <= 0) {
            return "00:00:00";
        }

        return String.format("%02d:%02d:%02d", time / 60 / 60, time / 60 % 60, time % 60);
    }

    // 播放时间格式�?
    public static String formatTime(int j) {
        String str = "";
        if (j < 10) {
            str = String.format("0%d", j);
        } else {
            str = String.format("%d", j);
        }

        return str;
    }

    /**
     * 转换文件大小
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static void closeSilently(Cursor c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Throwable t) {
            Log.w(TAG, "fail to close", t);
        }
    }

    public static String uri2Path(Uri uri, ContentResolver resolver) {
        String _path = "";
        if (uri == null) {
            return _path;
        }

        String scheme = uri.getScheme();
        if ("content".equals(scheme)) {
            String[] proj = {
                MediaColumns.DATA
            };
            Cursor cursor = resolver.query(uri, proj, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    _path = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DATA));
                }

                closeSilently(cursor);
            }
        } else if ("file".equals(scheme)) {
            _path = uri.getPath();
        }
        Log.i(TAG, "_path: " + _path);
        return _path;
    }

    public static void updateHistory(FileItem item, ContentResolver resolver, String packageName, String className) {
        Footprint footprints = new Footprint();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(item.getUri(), "video/*");
        intent.setClassName(packageName, className);
        footprints.mData = intent.toUri(Intent.URI_INTENT_SCHEME);
        footprints.mTitle = item.getName();
        footprints.mCategory = SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO;
        Bitmap bitmap = createVideoThumbnail(item.getLocation(), 0);
        footprints.mThumb = bitmap;

        SciflyStore.Footprints.putFootprints(resolver, footprints);
    }

    public static Bitmap createVideoThumbnail(String filePath, int duration) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Log.v(TAG, "---------------------filePath:" + filePath);
        Class clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();

            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);
            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                Method method1 = clazz.getMethod("getFrameAtTime", long.class);
                long time = (long) (Math.random() * duration) * 1000L + 6000000L;
                if (duration * 1000L < time) {
                    time = duration * 1000L;
                }
                if (time < 0) {
                    time = 0;
                }
                return (Bitmap) method1.invoke(instance, time);
            }
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } catch (InstantiationException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "createVideoThumbnail", e);
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
