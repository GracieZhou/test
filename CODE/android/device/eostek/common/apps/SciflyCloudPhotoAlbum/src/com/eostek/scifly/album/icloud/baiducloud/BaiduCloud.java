
package com.eostek.scifly.album.icloud.baiducloud;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.icloud.CloudDiskImpl;
import com.eostek.scifly.album.utils.Util;
import com.google.common.collect.Lists;

public class BaiduCloud extends CloudDiskImpl {
    private final static String TAG = "BaiduDiskObject";

    private final String APIKEY = "CuOLkaVfoz1zGsqFKDgfvI0h";

    private final static int IMAGE_LIST_FINISH = 1;

    private final static int REQUEST_ING = IMAGE_LIST_FINISH + 1;

    private final static int REQUEST_FINISH = IMAGE_LIST_FINISH + 2;

    private final static int IMAGE_URLS_FROM_FOLDER_FINISH = IMAGE_LIST_FINISH + 3;

    private String mbOauth = null;

    private List<String> mSciflyBelowDirectory;

    @SuppressWarnings("unused")
    private Activity mActivity;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IMAGE_LIST_FINISH:
                    getCoverUrlForFolder();
                    break;
                case REQUEST_ING:
                    requestData();
                    break;
                case REQUEST_FINISH:
                    setUserName(getStringValueFromPreferences(Constants.BAIDU_MASTER));
                    if (mCallback != null) {
                        mCallback.parseSuccess();
                    }
                    break;
                case IMAGE_URLS_FROM_FOLDER_FINISH:
                    if (mCallback != null) {
                        mCallback.parseSuccess();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private static BaiduCloud mInstance;

    /**
     * @Title: getInstance.
     * @Description: get the instance.
     * @param: @param activity
     * @param: @return.
     * @return: BaiduCloud.
     * @throws
     */
    public static BaiduCloud getInstance(Activity activity) {
        if (mInstance == null) {
            synchronized (BaiduCloud.class) {
                if (mInstance == null) {
                    mInstance = new BaiduCloud(activity);
                }
            }
        }
        return mInstance;
    }

    /**
     * @Title: BaiduCloud.
     * @Description: constructor.
     * @param: @param activity.
     * @throws
     */
    public BaiduCloud(Activity activity) {
        mActivity = activity;
        mSciflyBelowDirectory = new ArrayList<String>();
    }

    @Override
    public void logIn() {
        if (Util.isNetworkAvailable(getActivity())) {
            String token = getStringValueFromPreferences(Constants.BAIDU_TOKEN);
            mbOauth = token;
            if (TextUtils.isEmpty(mbOauth)) {
                getToken();
            } else {
                createSciflyAlbumFolder();
            }
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    @Override
    public void logOut() {
        if (Util.isNetworkAvailable(getActivity())) {
            saveStringValueToPreferences(Constants.BAIDU_TOKEN, "");
            saveStringValueToPreferences(Constants.BAIDU_MASTER, "");
            getRootImageUris().clear();
            getAlbumFolderNameList().clear();
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    @Override
    public void createSciflyAlbumFolder() {
        if (null != mbOauth) {

            new Thread() {
                public void run() {
                    BaiduPCSClient api = new BaiduPCSClient();
                    api.setAccessToken(mbOauth);
                    String path = Constants.mBrootPath + File.separator + SCIFLYALBUMSTRING;
                    api.makeDir(path);
                    saveStringValueToPreferences(Constants.BAIDU_TOKEN, mbOauth);

                    mHandler.sendEmptyMessage(REQUEST_ING);
                };
            }.start();
        }
    }

    @Override
    public void requestData() {
        if (Util.isNetworkAvailable(getActivity())) {
            mHandlerAlbum.sendEmptyMessage(1000);
            getAlbumFolderNameList().clear();
            Thread workThread = new Thread(new Runnable() {
                public void run() {
                    BaiduPCSClient api = new BaiduPCSClient();
                    api.setAccessToken(mbOauth);
                    String path = Constants.mBrootPath + File.separator + SCIFLYALBUMSTRING;
                    final BaiduPCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
                    List<PCSCommonFileInfo> list = ret.list;
                    if ((list != null) && list.size() > 0) {
                        List<String> RootPhotoUris = Lists.newArrayList();
                        for (int i = 0; i < list.size(); i++) {
                            boolean isDir = list.get(i).isDir;
                            if (isDir) {
                                mSciflyBelowDirectory.add(list.get(i).path);
                            } else {
                                String temPath = list.get(i).path;
                                if (Util.isPictrueFile(temPath)) {
                                    String downLoadUrl = Constants.STREAM_URL + "?method=download&access_token="
                                            + mbOauth + "&path=" + temPath;
                                    getRootImageUris().add(downLoadUrl);
                                    RootPhotoUris.add(downLoadUrl);
                                }
                            }
                        }
                        setRootImageUris(RootPhotoUris);
                    }
                    if (getRootImageUris().size() > 0) {
                        // Storage cover
                        getAlbumFolderNameList().put(Constants.ROOT_ALBUM_NAME, getRootImageUris().get(0));
                    }
                    mHandler.sendEmptyMessage(IMAGE_LIST_FINISH);
                }
            });
            workThread.start();
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    /**
     * 获取Access Token
     */
    private void getToken() {
        BaiduOAuth oauthClient = new BaiduOAuth();
        oauthClient.startOAuth(getActivity(), APIKEY, new String[] {
                "basic", "netdisk"
        }, new BaiduOAuth.OAuthListener() {
            @Override
            public void onException(String msg) {
                Log.i(TAG, "Login failed " + msg);
                if (mCallback != null) {
                    mCallback.parseError(getActivity().getString(R.string.prompt_information));
                }
            }

            @Override
            public void onComplete(BaiduOAuthResponse response) {
                if (null != response) {
                    mbOauth = response.getAccessToken();
                    Log.i(TAG, "mbOauth=" + response.getAccessToken());
                    saveStringValueToPreferences(Constants.BAIDU_MASTER, response.getUserName());
                    createSciflyAlbumFolder();
                }
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Login cancelled");
            }
        });
    }

    @Override
    public void getImageUrlsFromFolder(final String folderName) {
        if (Util.isNetworkAvailable(getActivity())) {

            getFolderImagesUris().clear();
            if (getAlbumFolderInfos().get(folderName) == null || getAlbumFolderInfos().get(folderName).size() <= 0) {
            }
            Thread workThread = new Thread(new Runnable() {
                public void run() {
                    BaiduPCSClient api = new BaiduPCSClient();
                    api.setAccessToken(mbOauth);
                    String path = Constants.mBrootPath + File.separator + SCIFLYALBUMSTRING + File.separator
                            + folderName;
                    Log.i("life", "path=" + path);
                    Log.i(TAG, "SciflyDirectoryBelowName=" + folderName);
                    final BaiduPCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
                    List<PCSCommonFileInfo> list = ret.list;
                    if (list != null) {
                        if (list.size() > 0) {
                            List<String> sciflyTempPath = new ArrayList<String>();
                            for (int i = 0; i < list.size(); i++) {
                                boolean isDir = list.get(i).isDir;
                                if (!isDir) {
                                    String temPath = list.get(i).path;
                                    if (Util.isPictrueFile(temPath)) {
                                        String downLoadUrl = Constants.STREAM_URL + "?method=download&access_token="
                                                + mbOauth + "&path=" + temPath;
                                        sciflyTempPath.add(downLoadUrl);
                                        getFolderImagesUris().add(downLoadUrl);
                                        Log.i(TAG, "SciflyDirectoryBelowName_downLoadUrl=" + downLoadUrl);
                                    }
                                }
                            }
                        }
                    }
                    if (getFolderImagesUris().size() > 0) {
                        getAlbumFolderInfos().put(folderName, getFolderImagesUris());
                    }
                    mHandler.sendEmptyMessage(IMAGE_URLS_FROM_FOLDER_FINISH);
                }
            });
            workThread.start();
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    public void getCoverUrlForFolder() {
        if (!mSciflyBelowDirectory.isEmpty()) {
            Thread workThread = new Thread(new Runnable() {
                public void run() {
                    BaiduPCSClient api = new BaiduPCSClient();
                    api.setAccessToken(mbOauth);
                    for (int j = 0; j < mSciflyBelowDirectory.size(); j++) {
                        String path = mSciflyBelowDirectory.get(j);
                        int lastIndexOf = path.lastIndexOf("/");
                        String directoryName = path.substring(lastIndexOf + 1);// Directory
                        Log.i(TAG, "SciflyDirectoryBelowName=" + directoryName);
                        final BaiduPCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
                        List<PCSCommonFileInfo> list = ret.list;
                        if (list != null) {
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    boolean isDir = list.get(i).isDir;
                                    if (!isDir) {
                                        String temPath = list.get(i).path;
                                        if (Util.isPictrueFile(temPath)) {
                                            String downLoadUrl = Constants.STREAM_URL
                                                    + "?method=download&access_token=" + mbOauth + "&path=" + temPath;
                                            getAlbumFolderNameList().put(directoryName, downLoadUrl);
                                            Log.i(TAG, "SciflyDirectoryBelowName_downLoadUrl=" + downLoadUrl);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    mHandler.sendEmptyMessage(REQUEST_FINISH);
                }
            });
            workThread.start();
        } else {
            mHandler.sendEmptyMessage(REQUEST_FINISH);
        }
    }
}
