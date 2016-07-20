
package com.eostek.scifly.album.icloud.sinavdisk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.icloud.CloudDiskImpl;
import com.eostek.scifly.album.utils.Util;
import com.vdisk.android.VDiskAuthSession;
import com.vdisk.android.VDiskDialogListener;
import com.vdisk.net.VDiskAPI;
import com.vdisk.net.VDiskAPI.Account;
import com.vdisk.net.VDiskAPI.Entry;
import com.vdisk.net.VDiskAPI.VDiskLink;
import com.vdisk.net.exception.VDiskDialogError;
import com.vdisk.net.exception.VDiskException;
import com.vdisk.net.session.AccessToken;
import com.vdisk.net.session.AppKeyPair;
import com.vdisk.net.session.VSession;

public class SinaVDisk extends CloudDiskImpl implements VDiskDialogListener {

    private static final String CONSUMER_KEY = "3882318316";

    private static final String CONSUMER_SECRET = "e26964a5aece7e6759b532bf17a3566e";

    private static final String REDIRECT_URL = "http://vdisk.weibo.com";

    private static final String SINA_PRE = "sina_pref";

    private VDiskAuthSession session;

    private VDiskAPI<VDiskAuthSession> mApi;

    private static SinaVDisk mInstance;

    @SuppressWarnings("unused")
    private static final String REQUEST_ERROR = "request error";

    /**
     * @Title: getInstance.
     * @Description: get the instance .
     * @param: @param activity
     * @param: @return.
     * @return: SinaVDisk.
     * @throws
     */
    public static SinaVDisk getInstance(Activity activity) {
        if (mInstance == null) {
            synchronized (SinaVDisk.class) {
                if (mInstance == null) {
                    mInstance = new SinaVDisk(activity);
                }
            }
        }
        return mInstance;
    }

    /**
     * @Title: SinaVDisk.
     * @Description: constructor.
     * @param: @param activity.
     * @throws
     */
    public SinaVDisk(Activity activity) {
        setActivity(activity);
        AppKeyPair appKeyPair = new AppKeyPair(CONSUMER_KEY, CONSUMER_SECRET);
        session = VDiskAuthSession.getInstance(activity, appKeyPair, VSession.AccessType.VDISK);
        mApi = new VDiskAPI<VDiskAuthSession>(session);
        // Log.i("debug", "从本地获得token--->" +
        // session.getAccessToken().mAccessToken);
    }

    @Override
    public void onComplete(Bundle values) {
        Log.i("debug", "onComplete");
        requestData();
        if (values != null) {
            AccessToken mToken = (AccessToken) values.getSerializable(VDiskAuthSession.OAUTH2_TOKEN);
            Log.e("debug", "token-->" + mToken.mAccessToken + "; uid-->" + mToken.mUid + "; expires-->"
                    + mToken.mExpiresIn + "; refreshToken-->" + mToken.mRefreshToken);
            // 认证成功后，保存token至本地
            if (session != null) {
                saveAuth(session.getAccessToken().mAccessToken);
            }
            // session.finishAuthorize(mToken);
        }
        saveBooleanValueToPreferences(Constants.SINA_AUTOLOGIN, true);

    }

    private void saveAuth(String token) {
        SharedPreferences prefs = getActivity().getSharedPreferences(SINA_PRE, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.putString("token", token);
        edit.commit();

    }

    @Override
    public void onError(VDiskDialogError error) {
        saveBooleanValueToPreferences(Constants.SINA_AUTOLOGIN, false);
        if (mCallback != null) {
            mCallback.parseError(getActivity().getString(R.string.prompt_information));
        }
    }

    @Override
    public void onVDiskException(VDiskException exception) {
        saveBooleanValueToPreferences(Constants.SINA_AUTOLOGIN, false);
        if (mCallback != null) {
            mCallback.parseError(getActivity().getString(R.string.prompt_information));
        }
    }

    @Override
    public void onCancel() {
        saveBooleanValueToPreferences(Constants.SINA_AUTOLOGIN, false);
        if (mCallback != null) {
            mCallback.parseError(getActivity().getString(R.string.prompt_information));
        }
    }

    @Override
    public void logIn() {
        if (Util.isNetworkAvailable(getActivity())) {
            if (session.isLinked()) {
                String token = getTokenFromPref();
                if (!TextUtils.isEmpty(token)) {
                    session.setAccessTokenPair(new AccessToken(token));
                    Log.i("debug", "重新获得--->" + session.getAccessToken().mAccessToken);
                    requestData();
                }
            } else {
                Log.e("debug", "-->unLinked");
                // 开始授权登陆
                authorize();
            }
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    /**
     * 获取本地保存的token,设置给session
     */
    private String getTokenFromPref() {
        SharedPreferences prefs = getActivity().getSharedPreferences(SINA_PRE, 0);
        return prefs.getString("token", null);
    }

    public void authorize() {
        session.setRedirectUrl(REDIRECT_URL);
        session.authorize(getActivity(), this);
    }

    @Override
    public void logOut() {
        if (Util.isNetworkAvailable(getActivity())) {
            getRootImageUris().clear();
            getAlbumFolderNameList().clear();
            if (session.isLinked()) {
                session.unlink();
            }
            clearToken();
            CookieManager.getInstance().removeAllCookie();
            saveBooleanValueToPreferences(Constants.SINA_AUTOLOGIN, false);
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    /**
     * 清除本地保存的token
     */
    private void clearToken() {
        SharedPreferences prefs = getActivity().getSharedPreferences(SINA_PRE, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    @Override
    public void requestData() {
        if (Util.isNetworkAvailable(getActivity())) {
            mHandlerAlbum.sendEmptyMessage(1000);
            new Thread() {
                public void run() {
                    createSciflyAlbumFolder();
                    setUserName(getAccount());
                    setRootImageUris(getCurDirUrl(File.separator + SCIFLYALBUMSTRING));
                    getFolderNameAndCovers();
                    if (mCallback != null) {
                        mCallback.parseSuccess();
                    }

                };
            }.start();
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    /**
     * 创建sciflyablum文件夹
     */
    @Override
    public void createSciflyAlbumFolder() {
        // 判断网盘根目录下是否存在SciflyAblum文件夹
        if (!isFolderExist("/", SCIFLYALBUMSTRING)) {
            try {
                mApi.createFolder(SCIFLYALBUMSTRING);
            } catch (VDiskException e) {
            }
        }
    }

    /**
     * 判断dir目录下是否存在指定名称文件夹
     * 
     * @param metaData
     * @param folderName 文件夹名称
     * @return
     */
    private boolean isFolderExist(String dir, String folderName) {
        boolean flag = false;
        Entry metaData = null;
        try {
            metaData = mApi.metadata(dir, null, true, false);
        } catch (VDiskException e) {
            e.printStackTrace();
        }
        if (metaData != null) {
            List<Entry> contents = metaData.contents;
            if (contents != null) {
                for (Entry entry : contents) {
                    if (entry.isDir) {
                        String dirName = entry.fileName();
                        if (folderName.equals(dirName)) {
                            flag = true;
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 获取Root目录下图片的url
     * 
     * @param dir
     * @return
     */

    public List<String> getCurDirUrl(String dir) {
        List<String> list = new ArrayList<String>();
        Entry metadata = null;
        try {
            metadata = mApi.metadata(dir, null, true, false);
        } catch (VDiskException e) {
            e.printStackTrace();
        }
        List<Entry> contents = null;
        if (metadata != null) {
            contents = metadata.contents;
        }
        if (contents != null) {
            for (Entry entry : contents) {
                if (!entry.isDir) {
                    VDiskLink vDiskLink = null;
                    try {
                        if (Util.isPictrueFile(entry.mimeType)) {
                            vDiskLink = mApi.media(entry.path, false);
                            String downloadUrl = vDiskLink.url;
                            list.add(downloadUrl);
                        }

                    } catch (VDiskException e) {
                        e.printStackTrace();
                        if (mCallback != null) {
                        }
                    }
                }
            }
        }
        Log.i("debug", dir + "directory url---->" + list);
        return list;
    }

    /**
     * 得到用户昵称
     * 
     * @return
     */
    public String getAccount() {
        Account account = null;
        String userName = "";
        try {
            account = mApi.accountInfo();
            userName = account.screen_name;
        } catch (VDiskException e) {
            e.printStackTrace();
        }
        return userName;
    }

    /**
     * 根据文件的名称获取SciflyAlbum目录下文件夹中图片的下载链接
     */
    @Override
    public void getImageUrlsFromFolder(final String folderName) {
        if (Util.isNetworkAvailable(getActivity())) {
            getFolderImagesUris().clear();
            new Thread() {
                public void run() {
                    List<String> imageUrlFromFolderList = new ArrayList<String>();
                    Entry metadata = null;
                    try {
                        metadata = mApi.metadata(File.separator + SCIFLYALBUMSTRING + File.separator + folderName,
                                null, true, false);
                    } catch (VDiskException e) {
                        e.printStackTrace();
                    }
                    List<Entry> contents = null;
                    if (metadata != null) {
                        contents = metadata.contents;
                    }
                    if (contents != null) {
                        for (Entry entry : contents) {
                            if (!entry.isDir) {
                                VDiskLink vDiskLink = null;
                                try {
                                    if (Util.isPictrueFile(entry.mimeType)) {
                                        vDiskLink = mApi.media(entry.path, false);
                                        String downloadUrl = vDiskLink.url;
                                        imageUrlFromFolderList.add(downloadUrl);
                                    }

                                } catch (VDiskException e) {
                                    if (mCallback != null) {
                                        mCallback.parseError(getActivity().getString(R.string.prompt_information));
                                    }
                                }
                            }
                        }
                        Log.i("debug", folderName + "------》" + imageUrlFromFolderList);
                        setFolderImagesUris(imageUrlFromFolderList);
                        if (mCallback != null) {
                            mCallback.parseSuccess();
                        }
                    }
                };
            }.start();
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }

    }

    /**
     * 获取SciflyAlbum目录下文件夹封面的url
     */
    private String getFolderCoverUrl(String folderName) {
        String coverUrlString = "";
        Entry metadata = null;
        try {
            metadata = mApi.metadata(File.separator + SCIFLYALBUMSTRING + File.separator + folderName, null, true,
                    false);
        } catch (VDiskException e) {
            e.printStackTrace();
        }
        List<Entry> contents = null;
        if (metadata != null) {
            contents = metadata.contents;
        }
        if (contents != null) {
            for (Entry entry : contents) {
                if (!entry.isDir) {
                    VDiskLink vDiskLink = null;
                    try {
                        if (Util.isPictrueFile(entry.mimeType)) {
                            vDiskLink = mApi.media(entry.path, false);
                            return vDiskLink.url;
                        }

                    } catch (VDiskException e) {
                        e.printStackTrace();
                        if (mCallback != null) {
                            mCallback.parseError(getActivity().getString(R.string.prompt_information));
                        }
                    }
                }
            }
        }
        return coverUrlString;
    }

    /**
     * 获取SciflyAlbum目录下的含有图片的文件夹名称
     */
    private List<String> getFolderNameList(String dir) {
        List<String> folderNameList = new ArrayList<String>();
        Entry metadata = null;
        try {
            metadata = mApi.metadata(dir, null, true, false);
        } catch (VDiskException e) {
            e.printStackTrace();
        }
        List<Entry> contents = null;
        if (metadata != null) {
            contents = metadata.contents;
        }

        if (contents != null) {
            for (Entry entry : contents) {
                if (entry.isDir) {
                    // 判断这个目录下是否存在图片文件
                    String path = entry.path; // 文件夹相对的路径
                    String fileName = entry.fileName(); // 文件夹名称
                    if (isContainImgFile(path)) {
                        folderNameList.add(fileName);
                    }
                }
            }
        }
        Log.i("debug", "albumFolderNameList------>" + folderNameList);
        return folderNameList;
    }

    /**
     * 判断某路径的文件夹下是否含有图片文件
     * 
     * @param entry
     * @param path
     * @return
     */
    private boolean isContainImgFile(String dir) {
        Entry metadata = null;
        try {
            metadata = mApi.metadata(dir, null, true, false);
        } catch (VDiskException e) {
            e.printStackTrace();
        }
        List<Entry> contents = null;
        if (metadata != null) {
            contents = metadata.contents;
        }
        if (contents != null) {
            for (Entry entry : contents) {
                if (!entry.isDir) {
                    if (Util.isPictrueFile(entry.mimeType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 得到文件夹和对应的封面
     */
    private void getFolderNameAndCovers() {
        if (getRootImageUris().size() > 0) {
            getAlbumFolderNameList().put(Constants.ROOT_ALBUM_NAME, getRootImageUris().get(0));
        }
        List<String> folderNameList = getFolderNameList(File.separator + SCIFLYALBUMSTRING);
        for (int i = 0; i < folderNameList.size(); i++) {
            String folderName = folderNameList.get(i);
            String coverUrl = getFolderCoverUrl(folderName);
            getAlbumFolderNameList().put(folderName, coverUrl);
        }
    }

}
