
package com.hrtvbic.usb.S6A918.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public class FileItem {

    private Uri mUri;
    
    private String mDownloadURL;

    private FileType mType;

    private String mName;

    private String mTitle;

    private long lastModified;

    private VolumeItem mVolumeItem;

    private String lyricPath;

    private String lyricName;

    private long mSize;

    public FileItem(Uri uri, String mData, FileType type, String name) {
        mUri = uri;
        mDownloadURL = mData;
        mType = type;
        mName = name;
    }

    public void setFileType(FileType type) {
        mType = type;
    }
    
    public String getDownloadURL(){
        return mDownloadURL;
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

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
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

    public void setVolume(VolumeItem item) {
        mVolumeItem = item;
    }

    public VolumeItem getVolumeItem() {
        return mVolumeItem;
    }

    public void setLyricPath(String path) {
        lyricPath = path;
    }

    public String getLyricPath() {
        return lyricPath;
    }

    public void setLyricName(String name) {
        lyricName = name;
    }

    public String getLyricName() {
        return lyricName;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public long getSize() {
        return mSize;
    }

    public Bitmap loadBitmap(Context context, int w, int h) {
        return loadBitmap(context, w, h, Bitmap.Config.RGB_565);
    }

    public Bitmap loadBitmap(Context context, int w, int h, Bitmap.Config config) {
        return null;
    }

    public enum FileType {
        DISK, DLNA_DISK, FOLDER, VIDEO, MUSIC, PHOTO, APK, OFFICE, TXT, NULL, OTHER;
    }

    public static final String DISK = "DISK";

    public static final String DLNA_DISK = "DLNA_DISK";

    public static final String FOLDER = "FOLDER";

    public static final String VIDEO = "VIDEO";

    public static final String MUSIC = "MUSIC";

    public static final String PHOTO = "PHOTO";

    public static final String APK = "APK";

    public static final String OFFICE = "OFFICE";

    public static final String TXT = "TXT";

    public static final String OTHER = "OTHER";

    public String getTypeString() {
        return mType.toString();
    }

    public String getLocation() {
        if (mUri == null) {
            return "";
        }

        if (mUri.getScheme().equals("http")) {
            return mUri.toString();
        }

        return mUri.getPath();
    }
}
