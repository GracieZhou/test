
package com.eostek.scifly.album.icloud.dropbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.DropboxAPI.DropboxLink;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.icloud.CloudDiskImpl;
import com.eostek.scifly.album.utils.Util;

public class DropboxCloud extends CloudDiskImpl {
    private static DropboxCloud mInstance;

    private static final String APP_KEY = "6xouuey1z5sxnnw";

    private static final String APP_SECRET = "g59tix2tmls9ob8";

    private static final String ACCOUNT_PREFS_NAME = "Dropbox";

    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";

    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    public static final String SCIFLYALBUMSTRING = "Scifly Album";

    public static final int ERROR = 0;

    DropboxAPI<AndroidAuthSession> mApi;

    private AndroidAuthSession session;

    /**
     * @Title: getInstance.
     * @Description: get the instance.
     * @param: @param activity
     * @param: @return.
     * @return: DropboxCloud.
     * @throws
     */
    public static DropboxCloud getInstance(Activity activity) {
        if (mInstance == null) {
            synchronized (DropboxCloud.class) {
                if (mInstance == null) {
                    mInstance = new DropboxCloud(activity);
                }
            }
        }
        return mInstance;
    }

    public DropboxCloud(Activity activity) {
        setActivity(activity);
        Log.i("hu", "DropboxCloud");
        checkAppKeySetup();
        session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void logIn() {
        if (isDropBoxBinded(getActivity())) {
            loadAuth(session);
            Log.i("hu", "createSciflyAlbumFolder");
            createSciflyAlbumFolder();
            requestData();
        } else {
            if (Util.isNetworkAvailable(getActivity())) {
                if (!session.isLinked()) {
                    if (USE_OAUTH1) {
                        Log.i("hu", "USE_OAUTH1");
                        mApi.getSession().startAuthentication(getActivity());
                    } else {
                        Log.i("hu", "OAuth2Authentication");
                        mApi.getSession().startOAuth2Authentication(getActivity());
                    }
                }
            } else {
                Util.showToast(getActivity(), R.string.no_network);
            }
        }
    }

    public void authenticationSuccessful() {
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();
                Log.i("hu", "authenticationSuccessful");
                // Store it locally in our app for later use
                storeAuth(session);
                saveBooleanValueToPreferences(Constants.DROPBOX_AUTOLOGIN, true);
                createSciflyAlbumFolder();
                requestData();
            } catch (IllegalStateException e) {
                if (mCallback != null) {
                    mCallback.parseError(getActivity().getString(R.string.prompt_information));
                }
            }
        }
    }

    @Override
    public void logOut() {
        if (Util.isNetworkAvailable(getActivity())) {
            mApi.getSession().unlink();
            clearKeys();
            saveBooleanValueToPreferences(Constants.DROPBOX_AUTOLOGIN, false);
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    @Override
    public void requestData() {
        if (Util.isNetworkAvailable(getActivity())) {
            mHandlerAlbum.sendEmptyMessage(1000);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Account account = mApi.accountInfo();
                        Log.i("hu", "userName" + account.displayName);
                        setUserName(account.displayName);
                        setRootImageUris(getImageUris(File.separator + SCIFLYALBUMSTRING));
                        getAllFolderNameAndCovers();
                        if (mCallback != null) {
                            mCallback.parseSuccess();
                        }
                    } catch (DropboxException e) {
                        if (mCallback != null) {
                            mCallback.parseError(getActivity().getString(R.string.prompt_information));
                        }
                    }
                }
            }).start();
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    private void getAllFolderNameAndCovers() {
        if (getRootImageUris().size() > 0) {
            getAlbumFolderNameList().put(Constants.ROOT_ALBUM_NAME, getRootImageUris().get(0));
        }
        List<Entry> folderNameList = getHasPictureAlbumFolderNameList(File.separator + SCIFLYALBUMSTRING);
        for (Entry ent : folderNameList) {
            String folderName = ent.fileName();
            String url = getImageUris(ent.path).get(0);
            getAlbumFolderNameList().put(folderName, url);

        }
    }

    private List<Entry> getHasPictureAlbumFolderNameList(String dir) {
        ArrayList<Entry> folderList = new ArrayList<Entry>();
        try {
            Entry dirent = mApi.metadata(dir, 1000, null, true, null);
            Log.i("hu", "size" + dirent.contents.size());
            if (dirent != null) {
                for (Entry ent : dirent.contents) {
                    Log.i("hu", ent.fileName() + " " + ent.thumbExists);
                    Log.i("hu", ent.parentPath() + " " + ent.thumbExists);
                    if (ent.isDir) {
                        List<String> list = getImageUris(ent.path);
                        if (list.size() > 0) {
                            folderList.add(ent);
                        }
                    }
                }
            }
        } catch (DropboxException e) {
            if (mCallback != null) {
                mCallback.parseError(getActivity().getString(R.string.prompt_information));
            }
        }
        return folderList;
    }

    private List<String> getImageUris(String dir) {
        ArrayList<String> thumbs = new ArrayList<String>();
        try {
            Entry dirent = mApi.metadata(dir, 1000, null, true, null);
            Log.i("hu", "size" + dirent.contents.size());
            if (dirent != null) {
                for (Entry ent : dirent.contents) {
                    Log.i("hu", ent.fileName() + " " + ent.thumbExists);
                    Log.i("hu", ent.parentPath() + " " + ent.thumbExists);
                    if (ent.thumbExists) {
                        DropboxLink dropBoxLink = mApi.media(ent.path, false);
                        String url = dropBoxLink.url;
                        Log.i("hu", "url  " + url);
                        thumbs.add(url);
                    }
                }
            }
        } catch (DropboxException e) {
            if (mCallback != null) {
                mCallback.parseError(getActivity().getString(R.string.prompt_information));
            }
        }
        return thumbs;
    }

    @Override
    public void createSciflyAlbumFolder() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Log.i("hu", "SHAREPREFRER_STRING");
                    Entry dirent = mApi.metadata(File.separator, 1000, null, true, null);
                    if (!isSciflyCloudAlbumExist(dirent)) {
                        Log.i("hu", "dirent");
                        mApi.createFolder(File.separator + SCIFLYALBUMSTRING);
                    }
                } catch (DropboxException e) {
                    if (mCallback != null) {
                        mCallback.parseError(getActivity().getString(R.string.prompt_information));
                    }
                }
            }
        }).start();
    }

    private Boolean isSciflyCloudAlbumExist(Entry dirent) {
        if (dirent != null) {
            for (Entry ent : dirent.contents) {
                Log.i("hu", ent.fileName() + " " + ent.thumbExists);
                if (ent.isDir) {
                    String dirName = ent.fileName();
                    Log.i("hu", ent.parentPath() + " " + ent.thumbExists);
                    if (SCIFLYALBUMSTRING.equals(dirName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void getImageUrlsFromFolder(final String folderName) {
        if (Util.isNetworkAvailable(getActivity())) {
            getFolderImagesUris().clear();
            Log.i("hu", "folderName" + folderName);
            if (getAlbumFolderInfos().get(folderName) == null) {
                // showDialog(getActivity(),
                // getActivity().getString(R.string.dialog_msg1));
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("hu", "getImageUrlsFromFolder");
                    Log.i("hu", File.separator + SCIFLYALBUMSTRING + folderName);
                    List<String> list = getImageUris(File.separator + SCIFLYALBUMSTRING + File.separator + folderName);
                    setFolderImagesUris(list);
                    if (list.size() > 0) {
                        getAlbumFolderInfos().put(folderName, list);
                    }
                    if (mCallback != null) {
                        mCallback.parseSuccess();
                    }
                }
            }).start();
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0)
            return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is
            // for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one. This is only
        // necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private void checkAppKeySetup() {
        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getActivity().getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " + "manifest is not set up correctly. You should have a "
                    + "com.dropbox.client2.android.AuthActivity with the " + "scheme: " + scheme);
            getActivity().finish();
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
        error.show();
    }
}
