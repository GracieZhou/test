
package com.hrtvbic.usb.S6A918;

import java.io.File;
import java.io.FileFilter;

//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.vlc.MediaDatabase;
//import org.videolan.vlc.VLCApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.eostek.scifly.messagecenter.R;
import com.hrtvbic.usb.S6A918.model.FileBuilder;
import com.hrtvbic.usb.S6A918.model.FileItem;
import com.hrtvbic.usb.S6A918.model.FileItem.FileType;
import com.hrtvbic.usb.S6A918.model.FolderItem;
import com.hrtvbic.usb.S6A918.util.BitmapUtils;
import com.hrtvbic.usb.S6A918.util.FileUtils;
import com.hrtvbic.usb.S6A918.util.Utils;

public class MediaFileBuilderImpl implements FileBuilder {
    private Context mContext;

    private FileFilter mFileFilter;

    public MediaFileBuilderImpl(Context context, FileFilter fileFilter) {
        this.mContext = context;
        mFileFilter = fileFilter;
    }

    @Override
    public FileItem buildItem(File file) {
        if (!file.exists()) {
            return null;
        }
        if (file.getName().startsWith(".")) {
            return null;
        }
        FileItem item = null;

        if (file.isDirectory()) {
            if (!checkEmptyDirectory(file)) {
                item = new FolderItem(file, mFileFilter, this);
                item.setLastModified(file.lastModified());
            }
        } else {
            final String fileName = file.getName();
            String ext = FileUtils.getExtension(fileName);
            FileType type = FileType.OTHER;

            if (FileUtils.VIDEO_EXTENSIONS.contains(ext)) {
                type = FileType.VIDEO;
            } else if (FileUtils.AUDIO_EXTENSIONS.contains(ext)) {
                type = FileType.MUSIC;
            } else if (FileUtils.IMAGE_EXTENSIONS.contains(ext)) {
                type = FileType.PHOTO;
            } else if (FileUtils.APK_EXTENSION.contains(ext)) {
                type = FileType.APK;
            } else if (FileUtils.OFFICE_EXTENSION.contains(ext)) {
                type = FileType.OFFICE;
            }
            final String path = file.getAbsolutePath();
            item = new FileItem(Uri.fromFile(file), "", type, fileName) {
                @Override
                public Bitmap loadBitmap(Context context, int w, int h) {
                    String path = getUri().getPath();
                    FileType fileType = getFileType();
                    String target = "/" + getVolumeItem().getUri().getPath().replace("/", "_") + "/";
                    String thumbnailPath = Utils.THUMBNAIL_DIR + target + path.hashCode();
                    Log.i("MediaFileBuilderImpl", "thumbnailPath: " + thumbnailPath);
                    File dir = new File(Utils.THUMBNAIL_DIR + target);
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            Log.d("MediaFileBuilderImpl", "mkdirs failed...");
                            return BitmapUtils.getImageThumbnail(thumbnailPath, -1, -1);
                        }
                    }

                    if (fileType == FileType.PHOTO) {
                        File file = new File(thumbnailPath);
                        if (file.exists()) {
                            return BitmapUtils.getImageThumbnail(thumbnailPath, -1, -1);
                        } else {
                            Bitmap b = BitmapUtils.getImageThumbnail(path, w, h);
                            if (b == null) {
                                Drawable drawable = mContext.getResources().getDrawable(R.drawable.type_photo);
                                BitmapDrawable bd = (BitmapDrawable) drawable;
                                BitmapUtils.saveBitmap(bd.getBitmap(), thumbnailPath);
                            } else {
                                BitmapUtils.saveBitmap(b, thumbnailPath);
                            }
                            return b;
                        }
                    }
                    // else if (fileType == FileType.VIDEO) {
                    // Log.i("zzz", "filepath = "+path);
                    // return
                    // com.hrtvbic.usb.S6A918.util.Utils.createVideoThumbnail(path,
                    // 0);
                    //
                    // } else if (fileType == FileType.MUSIC) {
                    // File file = new File(thumbnailPath);
                    // if (file.exists()) {
                    // return BitmapUtils.getImageThumbnail(thumbnailPath, -1,
                    // -1);
                    // } else {
                    // Bitmap b = BitmapUtils.getImageThumbnail(path, w, h);
                    // if (b == null) {
                    // Drawable drawable =
                    // VLCApplication.getAppResources().getDrawable(R.drawable.type_music);
                    // BitmapDrawable bd = (BitmapDrawable) drawable;
                    // BitmapUtils.saveBitmap(bd.getBitmap(), thumbnailPath);
                    // } else {
                    // BitmapUtils.saveBitmap(b, thumbnailPath);
                    // }
                    // return b;
                    // }
                    // } else if (fileType == FileType.APK) {
                    // File file = new File(thumbnailPath);
                    // if (file.exists()) {
                    // return BitmapUtils.getImageThumbnail(thumbnailPath, -1,
                    // -1);
                    // } else {
                    // Bitmap b = BitmapUtils.getApkIcon(context, path, w, h);
                    // if (b == null) {
                    // Drawable drawable =
                    // VLCApplication.getAppResources().getDrawable(R.drawable.type_apk);
                    // BitmapDrawable bd = (BitmapDrawable) drawable;
                    // BitmapUtils.saveBitmap(bd.getBitmap(), thumbnailPath);
                    // } else {
                    // BitmapUtils.saveBitmap(b, thumbnailPath);
                    // }
                    // return b;
                    // }
                    // } else if (fileType == FileType.OFFICE) {
                    // File file = new File(thumbnailPath);
                    // if (file.exists()) {
                    // return BitmapUtils.getImageThumbnail(thumbnailPath, -1,
                    // -1);
                    //
                    // } else {
                    // Bitmap b = BitmapUtils.getImageThumbnail(path, w, h);
                    // if (b == null) {
                    // Drawable drawable;
                    // if (fileName.endsWith(".doc") ||
                    // fileName.endsWith(".docx")) {
                    // drawable =
                    // VLCApplication.getAppResources().getDrawable(R.drawable.type_doc);
                    // } else if (fileName.endsWith(".xls") ||
                    // fileName.endsWith(".xlsx")) {
                    // drawable =
                    // VLCApplication.getAppResources().getDrawable(R.drawable.type_excel);
                    // } else if (fileName.endsWith(".ppt") ||
                    // fileName.endsWith(".pptx")) {
                    // drawable =
                    // VLCApplication.getAppResources().getDrawable(R.drawable.type_ppt);
                    // } else {
                    // drawable =
                    // VLCApplication.getAppResources().getDrawable(R.drawable.type_doc);
                    // }
                    // BitmapDrawable bd = (BitmapDrawable) drawable;
                    // BitmapUtils.saveBitmap(bd.getBitmap(), thumbnailPath);
                    // } else {
                    // BitmapUtils.saveBitmap(b, thumbnailPath);
                    // }
                    // return b;
                    // }
                    // }

                    return null;
                }
            };
            int pos = fileName.lastIndexOf(".");
            if (pos > 0) {
                item.setTitle(fileName.substring(0, pos));
            } else {
                item.setTitle(fileName);
            }
            if (type == FileType.MUSIC) {
                String lyricName = fileName.substring(0, pos) + ".lrc";
                item.setLyricName(lyricName);
                item.setLyricPath(file.getParent() + "/" + lyricName);
            }
            item.setLastModified(file.lastModified());
            item.setSize(file.length());

            // if (type == FileType.MUSIC) {
            // String location = LibVLC.PathToURI(file.getAbsolutePath());
            // if
            // (!MediaDatabase.getInstance(VLCApplication.getAppContext()).mediaItemExists(location))
            // {
            // MediaDatabase.getInstance(VLCApplication.getAppContext()).addMedia(
            // new Media(LibVLC.getExistingInstance(), location));
            // }
            // }
        }

        return item;
    }

    private boolean checkEmptyDirectory(File directory) {
        if (directory == null) {
            return true;
        }

        String[] files = directory.list();
        if (files == null || files.length <= 0) {
            return true;
        }

        return false;
    }
}
