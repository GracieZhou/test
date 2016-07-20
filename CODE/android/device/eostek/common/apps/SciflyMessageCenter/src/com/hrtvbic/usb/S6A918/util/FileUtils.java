
package com.hrtvbic.usb.S6A918.util;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;

import android.os.Environment;
import android.util.Log;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static final FileFilter IMAGE_FILTER = new FileFilter() {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory() || IMAGE_EXTENSIONS.contains(getExtension(file.getName()))) {
                return true;
            }
            return false;
        }
    };

    public static final FileFilter VIDEO_FILTER = new FileFilter() {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory() || VIDEO_EXTENSIONS.contains(getExtension(file.getName()))) {
                return true;
            }
            return false;
        }
    };

    public static final FileFilter MUSIC_FILTER = new FileFilter() {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory() || AUDIO_EXTENSIONS.contains(getExtension(file.getName()))) {
                return true;
            }
            return false;
        }
    };

    public final static HashSet<String> IMAGE_EXTENSIONS;

    public final static HashSet<String> VIDEO_EXTENSIONS;

    public final static HashSet<String> AUDIO_EXTENSIONS;

    public final static HashSet<String> APK_EXTENSION;

    public final static HashSet<String> OFFICE_EXTENSION;

    public final static String EXTENSIONS_REGEX;

    public final static HashSet<String> FOLDER_BLACKLIST;

    static {
        String[] image_extensions = {
                ".bmp", ".gif", ".ico", ".jpeg", ".jpg", ".mpo", ".png"
        // ".tif"
        };

        String[] video_extensions = {
                ".3g2", ".3gp", ".3gp2", ".3gpp", ".amv", ".asf", ".avi", ".dat", ".divx", ".drc", ".dv", ".f4v",
                ".flv", ".gvi", ".gxf", ".iso", ".m1v", ".m2v", ".m2t", ".m2ts", ".m3u8", ".m4v", ".mkv", ".mov",
                ".mp2v", ".mp4", ".mp4v", ".mpe", ".mpeg", ".mpeg1", ".mpeg2", ".mpeg4", ".mpg", ".mpv2", ".mts",
                ".mtv", ".mxf", ".mxg", ".nsv", ".nuv", ".ogm", ".ogv", ".ogx", ".ps", ".rec", ".rm", ".rmvb", ".tod",
                ".tp", ".trp", ".ts", ".tts", ".vob", ".vro", ".webm", ".wm", ".wmv", ".wtv", ".xesc"
        };

        String[] audio_extensions = {
                ".3ga", ".a52", ".aac", ".ac3", ".adt", ".adts", ".aif", ".aifc", ".aiff", ".amr", ".aob", ".ape",
                ".awb", ".caf", ".dts", ".flac", ".it", ".m4a", ".m4p", ".mid", ".mka", ".mlp", ".mpa", ".mp1", ".mp2",
                ".mp3", ".mpc", ".oga", ".ogg", ".oma", ".opus", ".ra", ".ram", ".rmi", ".s3m", ".spx", ".tta", ".voc",
                ".vqf", ".w64", ".wav", ".wma", ".wv", ".xa", ".xm"
        };

        String[] apk_extensions = {
            ".apk"
        };

        String[] office_extensions = {
                ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"
        };

        String[] folder_blacklist = {
                "/alarms", "/notifications", "/ringtones", "/media/alarms", "/media/notifications", "/media/ringtones",
                "/media/audio/alarms", "/media/audio/notifications", "/media/audio/ringtones", "/Android/data/"
        };

        IMAGE_EXTENSIONS = new HashSet<String>();
        for (String item : image_extensions) {
            IMAGE_EXTENSIONS.add(item);
        }
        VIDEO_EXTENSIONS = new HashSet<String>();
        for (String item : video_extensions) {
            VIDEO_EXTENSIONS.add(item);
        }
        AUDIO_EXTENSIONS = new HashSet<String>();
        for (String item : audio_extensions) {
            AUDIO_EXTENSIONS.add(item);
        }
        APK_EXTENSION = new HashSet<String>();
        for (String item : apk_extensions) {
            APK_EXTENSION.add(item);
        }

        OFFICE_EXTENSION = new HashSet<String>();
        for (String item : office_extensions) {
            OFFICE_EXTENSION.add(item);
        }

        StringBuilder sb = new StringBuilder(115);
        sb.append(".+(\\.)((?i)(");
        sb.append(video_extensions[0].substring(1));
        for (int i = 1; i < video_extensions.length; i++) {
            sb.append('|');
            sb.append(video_extensions[i].substring(1));
        }
        for (int i = 0; i < audio_extensions.length; i++) {
            sb.append('|');
            sb.append(audio_extensions[i].substring(1));
        }
        sb.append("))");
        EXTENSIONS_REGEX = sb.toString();
        Log.i(TAG, "extensions regex: " + EXTENSIONS_REGEX);
        FOLDER_BLACKLIST = new HashSet<String>();
        for (String item : folder_blacklist) {
            FOLDER_BLACKLIST.add(Environment.getExternalStorageDirectory().getAbsolutePath() + item);
        }
    }

    public static String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            return fileName.substring(dotIndex).toLowerCase();
        }

        return "";
    }

    static final String SUBTITLE_EXTENSION[] = new String[] {
            ".ass", ".idx", ".smi", ".srt", ".ssa", ".sub", ".txt"
    };
}
