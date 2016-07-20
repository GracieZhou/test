
package com.eostek.scifly.album.icloud.onedrive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.icloud.CloudDiskImpl;
import com.eostek.scifly.album.utils.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Folder;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;

public class OneDriverCloud extends CloudDiskImpl {

    private static final String TAG = "OneDriver";

    private boolean isExistSciflyAlum = false;

    /**
     * The name of onedrive's root
     */
    private static final String ROOT = "root";

    /**
     * The prefix for the item breadcrumb when the parent reference is
     * unavailable
     */
    private static final String DRIVE_PREFIX = "/drive/";

    /**
     * Expansion options to get all children, thumbnails of children, and
     * thumbnails
     */
    private static final String EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS = "children(expand=thumbnails),thumbnails";

    /**
     * Expansion options to get all children, thumbnails of children, and
     * thumbnails when limited
     */
    private static final String EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS_LIMITED = "children,thumbnails";

    private static final String EORR_BACK = "Unable to login with MSA";

    private static HashMap<String, Item> sciflyFloderMap = Maps.newLinkedHashMap();

    OneDriveBase mCloudDiskOneDriveBase;

    private static OneDriverCloud mInstance;

    /**
     * @Title: getInstance.
     * @Description: get the instance.
     * @param: @param activity
     * @param: @return.
     * @return: OneDriverCloud.
     * @throws
     */
    public static OneDriverCloud getInstance(Activity activity) {
        if (mInstance == null) {
            synchronized (OneDriverCloud.class) {
                if (mInstance == null) {
                    mInstance = new OneDriverCloud(activity);
                }
            }
        }
        return mInstance;
    }

    /**
     * @Title: OneDriverCloud.
     * @Description: constructor.
     * @param: @param activity.
     * @throws
     */
    public OneDriverCloud(Activity activity) {
        setActivity(activity);
        mCloudDiskOneDriveBase = new OneDriveBase();
    }

    @Override
    public void logIn() {
        if (Util.isNetworkAvailable(getActivity())) {
            final ICallback<Void> serviceCreated = new DefaultCallback<Void>(getActivity()) {
                @Override
                public void success(final Void result) {
                    saveBooleanValueToPreferences(Constants.ONEDRIVE_AUTOLOGIN, true);
                    requestData();
                }

                @Override
                public void failure(ClientException error) {
                    super.failure(error);
                    if (mCallback != null) {
                        if (!error.toString().contains(EORR_BACK)) {
                            mCallback.parseError(getActivity().getString(R.string.prompt_information));
                        }
                    }
                }
            };
            try {
                if (!isOneDriverBinded(getActivity())) {
                    boolean res = mCloudDiskOneDriveBase.signOneDriveOut();
                    if (res) {
                        saveBooleanValueToPreferences(Constants.ONEDRIVE_AUTOLOGIN, true);
                        logIn();
                    } else {
                        mCloudDiskOneDriveBase.getOneDriveClient();
                    }
                } else {
                    requestData();
                    mCloudDiskOneDriveBase.getOneDriveClient();
                }
            } catch (final UnsupportedOperationException ignored) {
                mCloudDiskOneDriveBase.createOneDriveClient(getActivity(), serviceCreated);
            }
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    @Override
    public void logOut() {
        if (Util.isNetworkAvailable(getActivity())) {
            setUserName("");
            getRootImageUris().clear();
            getAlbumFolderNameList().clear();
            saveBooleanValueToPreferences(Constants.ONEDRIVE_AUTOLOGIN, false);
            mCloudDiskOneDriveBase.signOneDriveOut();
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    @Override
    public void requestData() {
        if (Util.isNetworkAvailable(getActivity())) {
            mHandlerAlbum.sendEmptyMessage(1000);
            sciflyFloderMap.clear();
            getAlbumFolderNameList().clear();
            requst(ROOT);
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

    @Override
    public void createSciflyAlbumFolder() {
        final ICallback<Item> callback = new DefaultCallback<Item>(getActivity()) {
            @Override
            public void success(final Item createdItem) {
                requestData();
            }

            @Override
            public void failure(final ClientException error) {
                super.failure(error);
                if (mCallback != null) {
                    mCallback.parseError(getActivity().getString(R.string.prompt_information));
                }
            }
        };
        final Item newItem = new Item();
        newItem.name = SCIFLYALBUMSTRING;
        newItem.folder = new Folder();
        mCloudDiskOneDriveBase.getOneDriveClient().getDrive().getItems(ROOT).getChildren().buildRequest()
                .create(newItem, callback);
    }

    /**
     * Creates a callback for drilling into root and Scifly_Alumb item
     * 
     * @param context The application context to display messages
     * @return The callback to refresh this item with
     */
    private ICallback<Item> getSciflyRootItemCallback(Context context) {
        return new DefaultCallback<Item>(context) {
            @Override
            public void success(final Item item) {
                Item scifyAlumItem = null;
                List<String> sciflyRootImageUris = Lists.newArrayList();
                Map<String, String> sciflyFolderNameMap = Maps.newLinkedHashMap();
                if (item.children != null && !item.children.getCurrentPage().isEmpty()) {
                    if (sciflyRootImageUris != null) {
                        sciflyRootImageUris.clear();
                    }
                    for (final Item childItem : item.children.getCurrentPage()) {
                        if (ROOT.equals(item.name)) {
                            setUserName(item.createdBy.user.displayName);
                            Log.d(TAG, "----" + DRIVE_PREFIX + item.name + "-->childrens：" + childItem.name);
                            if (SCIFLYALBUMSTRING.equals(childItem.name)) {
                                isExistSciflyAlum = true;
                                scifyAlumItem = childItem;
                                break;
                            }
                        } else if (SCIFLYALBUMSTRING.equals(item.name)) {
                            Log.d(TAG, "----" + item.parentReference.path + "/" + item.name + "-->childrens："
                                    + childItem.name);
                            if (childItem.file != null) {
                                if (Util.isPictrueFile(childItem.file.mimeType) & hasThumbnail(childItem)) {
                                    String largeThumbnailsUrl = childItem.thumbnails.getCurrentPage().get(0).large.url;
                                    sciflyRootImageUris.add(largeThumbnailsUrl);
                                }
                            }
                            if (childItem.folder != null) {
                                if (hasThumbnail(childItem)) {
                                    sciflyFloderMap.put(childItem.name, childItem);
                                    String largeThumbnailsUrl = childItem.thumbnails.getCurrentPage().get(0).large.url;
                                    sciflyFolderNameMap.put(childItem.name, largeThumbnailsUrl);
                                    // getAlbumFolderNameList().put(childItem.name,
                                    // largeThumbnailsUrl);
                                }

                            }
                        }
                    }
                }
                if (ROOT.equals(item.name)) {
                    if (isExistSciflyAlum) {
                        requst(scifyAlumItem.id);
                    } else {
                        createSciflyAlbumFolder();
                    }
                    return;
                } else if (SCIFLYALBUMSTRING.equals(item.name)) {
                    if (sciflyRootImageUris != null && sciflyRootImageUris.size() > 0) {
                        getAlbumFolderNameList().put(Constants.ROOT_ALBUM_NAME, sciflyRootImageUris.get(0));
                        setRootImageUris(sciflyRootImageUris);
                    }
                    for (Iterator<String> it = sciflyFolderNameMap.keySet().iterator(); it.hasNext();) {
                        String key = it.next();
                        getAlbumFolderNameList().put(key, sciflyFolderNameMap.get(key));
                    }
                }
                if (mCallback != null) {
                    mCallback.parseSuccess();
                }
                Log.d(TAG, "--------------------------------updata scifly root finish. name :" + item.name);
            }

            @Override
            public void failure(final ClientException error) {
                if (mCallback != null) {
                    mCallback.parseError(getActivity().getString(R.string.prompt_information));
                }
            }
        };
    }

    protected void requst(String itemId) {
        final IOneDriveClient oneDriveClient = mCloudDiskOneDriveBase.getOneDriveClient();
        final ICallback<Item> itemCallback = getSciflyRootItemCallback(getActivity());
        oneDriveClient.getDrive().getItems(itemId).buildRequest().expand(getExpansionOptions(oneDriveClient))
                .get(itemCallback);
    }

    /**
     * Gets the expansion options for requests on items
     * 
     * @see {https://github.com/OneDrive/onedrive-api-docs/issues/203}
     * @param oneDriveClient the OneDrive client
     * @return The string for expand options
     */
    private String getExpansionOptions(final IOneDriveClient oneDriveClient) {
        final String expansionOption;
        switch (oneDriveClient.getAuthenticator().getAccountInfo().getAccountType()) {
            case MicrosoftAccount:
                expansionOption = EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS;
                break;

            default:
                expansionOption = EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS_LIMITED;
                break;
        }
        return expansionOption;
    }

    /**
     * Determine if an item has a thumbnail used for visualization
     * 
     * @return If the item has a thumbnail
     */
    private boolean hasThumbnail(Item mItem) {
        return mItem.thumbnails != null && mItem.thumbnails.getCurrentPage() != null
                && !mItem.thumbnails.getCurrentPage().isEmpty()
                && mItem.thumbnails.getCurrentPage().get(0).small != null
                && mItem.thumbnails.getCurrentPage().get(0).small.url != null;
    }

    @Override
    public void getImageUrlsFromFolder(String folderName) {
        if (Util.isNetworkAvailable(getActivity())) {
            getFolderImagesUris().clear();
            if (getAlbumFolderInfos().get(folderName) == null) {
                // showDialog(getActivity(),
                // getActivity().getString(R.string.dialog_msg1));
            }
            String itemId = null;
            if (!sciflyFloderMap.isEmpty()) {
                Log.d(TAG, "reqest name:" + folderName);
                Item mItem = sciflyFloderMap.get(folderName);
                if (mItem != null) {
                    itemId = sciflyFloderMap.get(folderName).id;
                }
            }
            if (itemId != null) {
                final ICallback<Item> getScifyChildrenCallback = new DefaultCallback<Item>(getActivity()) {
                    @Override
                    public void success(final Item item) {
                        if (item.children != null && !item.children.getCurrentPage().isEmpty()) {
                            for (final Item childItem : item.children.getCurrentPage()) {
                                if (childItem.file != null) {
                                    if (Util.isPictrueFile(childItem.file.mimeType) & hasThumbnail(childItem)) {
                                        String largeThumbnailsUrl = childItem.thumbnails.getCurrentPage().get(0).large.url;
                                        Log.d(TAG, "childItem url:" + largeThumbnailsUrl);
                                        getFolderImagesUris().add(largeThumbnailsUrl);
                                    }
                                }
                            }
                        }
                        if (mCallback != null) {
                            mCallback.parseSuccess();
                        }
                        if (getFolderImagesUris().size() > 0) {
                            getAlbumFolderInfos().put(item.name, getFolderImagesUris());
                        }
                        Log.d(TAG, "------------------------updata scifly children finish ,name :" + item.name);
                    }

                    @Override
                    public void failure(final ClientException error) {
                        super.failure(error);
                        if (mCallback != null) {
                            mCallback.parseError(getActivity().getString(R.string.prompt_information));
                        }
                    }
                };
                final IOneDriveClient oneDriveClient = mCloudDiskOneDriveBase.getOneDriveClient();
                oneDriveClient.getDrive().getItems(itemId).buildRequest().expand(getExpansionOptions(oneDriveClient))
                        .get(getScifyChildrenCallback);
            }
        } else {
            Util.showToast(getActivity(), R.string.no_network);
        }
    }

}
