
package com.bq.tv.task.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * 描述一个文件.
 */
public class FileItem {

    private Uri mUri;

    // 表示文件类型.
    private FileType mType;

    private String mName;

    private long lastModified;

    public FileItem(Uri uri, FileType type, String name) {
        mUri = uri;
        mType = type;
        mName = name;
    }

    public FileType getFileType() {
        return mType;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long lastModified() {
        return lastModified;
    }

    public boolean showLabel() {
        return true;
    }

    public Bitmap loadBitmap(Context context, int w, int h) {
        return loadBitmap(context, w, h, Bitmap.Config.RGB_565);
    }

    public Bitmap loadBitmap(Context context, int w, int h, Bitmap.Config config) {
        return null;
    }

    public void destroy() {
    }

    public enum FileType {
        FOLDER, VIDEO, MUSIC, PHOTO, APK, TXT, OTHER;
    }

    public static final String FOLDER = "FOLDER";

    public static final String VIDEO = "VIDEO";

    public static final String MUSIC = "MUSIC";

    public static final String PHOTO = "PHOTO";

    public static final String APK = "APK";

    public static final String TXT = "TXT";

    public static final String OTHER = "OTHER";

    public String getTypeString() {
        return mType.toString();
    }

    public enum State {
        NOT_LOAD, LOADING, LOADED, LOADED_ERROR
    }
}
