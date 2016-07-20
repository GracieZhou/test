
package com.eostek.scifly.album.icloud;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.icloud.baiducloud.BaiduCloud;
import com.eostek.scifly.album.icloud.dropbox.DropboxCloud;
import com.eostek.scifly.album.icloud.onedrive.OneDriverCloud;
import com.eostek.scifly.album.icloud.sinavdisk.SinaVDisk;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @ClassName: CloudDiskImpl.
 * @Description:抽象云盘类.
 * @author: lucky.li.
 * @date: Dec 8, 2015 3:55:45 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public abstract class CloudDiskImpl {

    public static CloudDiskImpl getCloudDisk(int type, Activity activity) {
        if (mPreferences == null) {
            mPreferences = activity.getSharedPreferences(Constants.SHAREPREFRER_STRING, Context.MODE_PRIVATE);
        }
        switch (type) {
            case com.eostek.scifly.album.Constants.BAIDU_CLOUD_DISK:
                return BaiduCloud.getInstance(activity);
            case com.eostek.scifly.album.Constants.SINA_VDISK:
                return SinaVDisk.getInstance(activity);
            case com.eostek.scifly.album.Constants.DROPBOX_CLOUD_DISK:
                return DropboxCloud.getInstance(activity);
            case com.eostek.scifly.album.Constants.ONEDRIVE_CLOUD_DISK:
                return OneDriverCloud.getInstance(activity);
            default:
                break;
        }
        return null;
    }

    public static SharedPreferences mPreferences;

    public static final String SCIFLYALBUMSTRING = "Scifly Album";

    /**
     * 保存Scifly Album根目录下的图片的uri（不包括文件夹）
     */
    private List<String> mRootImagesUris = Lists.newArrayList();

    /**
     * 保存某一目录里面的所有图片的uri
     */
    private List<String> mFolderImagesUris = Lists.newArrayList();

    /**
     * 保存账号信息
     */
    private String userName;

    /**
     * 保存所有文件夹的名称（系统root文件夹存在也放入） key保存文件夹的名称 value保存的文件夹的封面url.
     */
    private Map<String, String> mAlbumFolderNameList = Maps.newLinkedHashMap();

    /**
     * 保存所有文件夹的信息 key保存文件名，value则是所有图片urls
     */
    private Map<String, List<String>> mAlbumFolderInfos = Maps.newHashMap();

    public IRequestCallback mCallback;

    private Activity mActivity;

    public Handler mHandlerAlbum;

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setHander(Handler handler) {
        mHandlerAlbum = handler;
    }

    public void setCallback(IRequestCallback mCallback) {
        this.mCallback = mCallback;
    }

    public IRequestCallback getCallback() {
        return mCallback;
    }

    public List<String> getRootImageUris() {
        return mRootImagesUris;
    }

    public void setRootImageUris(List<String> mRootPhotoUris) {
        this.mRootImagesUris = mRootPhotoUris;
    }

    public Map<String, String> getAlbumFolderNameList() {
        return mAlbumFolderNameList;
    }

    public void setAlbumFolderNameList(Map<String, String> mAlbumFolderNameList) {
        this.mAlbumFolderNameList = mAlbumFolderNameList;
    }

    public List<String> getFolderImagesUris() {
        return mFolderImagesUris;
    }

    public void setFolderImagesUris(List<String> mFolderImagesUris) {
        this.mFolderImagesUris = mFolderImagesUris;
    }

    public Map<String, List<String>> getAlbumFolderInfos() {
        return mAlbumFolderInfos;
    }

    public void setAlbumFolderInfos(Map<String, List<String>> mAlbumFolderinfos) {
        this.mAlbumFolderInfos = mAlbumFolderinfos;
    }

    public interface IRequestCallback {
        public void parseSuccess();

        public void parseError(String errorMsg);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String usernamme) {
        this.userName = usernamme;
    }

    public static boolean isBaiduCloudBinded(Activity activity) {
        if (mPreferences == null) {
            mPreferences = activity.getSharedPreferences(Constants.SHAREPREFRER_STRING, Context.MODE_PRIVATE);
        }
        String baiduToken = getStringValueFromPreferences(Constants.BAIDU_TOKEN);
        if (Strings.isNullOrEmpty(baiduToken)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isSinaVDiskBinded(Activity activity) {
        if (mPreferences == null) {
            mPreferences = activity.getSharedPreferences(Constants.SHAREPREFRER_STRING, Context.MODE_PRIVATE);
        }
        return getBooleanValueFromPreferences(Constants.SINA_AUTOLOGIN);
    }

    public static boolean isOneDriverBinded(Activity activity) {
        if (mPreferences == null) {
            mPreferences = activity.getSharedPreferences(Constants.SHAREPREFRER_STRING, Context.MODE_PRIVATE);
        }
        return getBooleanValueFromPreferences(Constants.ONEDRIVE_AUTOLOGIN);
    }

    public static boolean isDropBoxBinded(Activity activity) {
        if (mPreferences == null) {
            mPreferences = activity.getSharedPreferences(Constants.SHAREPREFRER_STRING, Context.MODE_PRIVATE);
        }
        return getBooleanValueFromPreferences(Constants.DROPBOX_AUTOLOGIN);
    }

    public static String getStringValueFromPreferences(String key) {
        return mPreferences.getString(key, "");
    }

    public static void saveStringValueToPreferences(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean getBooleanValueFromPreferences(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public static void saveBooleanValueToPreferences(String key, Boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * @Title: requestDatas.
     * @Description: get the cover for the folder that we need .
     * @param: folderName.
     * @return: void.
     * @throws
     */
    public String getCoverUrlForFolder(String folderName) {
        if (mAlbumFolderNameList.size() > 0) {
            return mAlbumFolderNameList.get(folderName);
        }
        return null;

    }

    // -----abstract method -----//
    /**
     * @Title: createSciflyAlbumFolder.
     * @Description: Creates a SciflyAlbum folder if it does not exist.
     * @param: .
     * @return: void.
     * @throws
     */
    public abstract void createSciflyAlbumFolder();

    /**
     * @Title: logIn.
     * @Description: log in the cloud .
     * @param: .
     * @return: void.
     * @throws
     */
    public abstract void logIn();

    /**
     * @Title: logIn.
     * @Description: log out the cloud .
     * @param: .
     * @return: void.
     * @throws
     */
    public abstract void logOut();

    /**
     * @Title: requestDatas.
     * @Description: request the data which we need.
     * @param: .
     * @return: void.
     * @throws
     */
    public abstract void requestData();

    /**
     * @Title: getImageUrlsFromFolder.
     * @Description: get the all imageUrls from the folder.
     * @param: @param folderName
     * @param: @return.
     * @return: String[].
     * @throws
     */
    public abstract void getImageUrlsFromFolder(String folderName);

}
