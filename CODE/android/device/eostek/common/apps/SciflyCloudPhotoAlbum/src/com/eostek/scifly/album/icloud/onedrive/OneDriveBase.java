
package com.eostek.scifly.album.icloud.onedrive;

import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;

import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.OneDriveClient;
import com.onedrive.sdk.logger.LoggerLevel;

public class OneDriveBase {

    /**
     * 对用户的基本个人资料信息进行读取访问。还可实现对用户的联系人列表进行读取访问。
     */
    private final String WL_BASIC = "wl.basic";

    /**
     * 应用能够随时读取和更新用户的信息。如果没有此作用域，那么，只有当用户已登录 Live Connect
     * 而且正在使用你的应用时，你的应用才能够访问用户的信息。
     */
    private final String WL_OFFLINE_ACCESS = "wl.offline_access";

    /**
     * 单一登录行为。通过单一登录，已登录 Live Connect 的用户也会自动登录到你的网站。
     */
    private final String WL_SIGNIN = "wl.signin";

    /**
     * onedrive 文件读写权限
     */
    private final String ONEDRVIE_RW = "onedrive.readwrite";

    /**
     * onedrive 文件夹操作权限
     */
    private final String ONEDRVIE_AF = "onedrive.appfolder";

    /**
     * The service instance
     */
    private final AtomicReference<IOneDriveClient> mClient = new AtomicReference<IOneDriveClient>();

    /**
     * Create the client configuration
     * 
     * @return the newly created configuration
     */
    private IClientConfig createConfig() {
        final MSAAuthenticator msaAuthenticator = new MSAAuthenticator() {
            @Override
            public String getClientId() {
                // my id
                return "000000004C177C1E";
            }

            @Override
            public String[] getScopes() {
                return new String[] {
                        ONEDRVIE_AF, ONEDRVIE_RW, WL_OFFLINE_ACCESS, WL_SIGNIN, WL_OFFLINE_ACCESS, WL_BASIC
                };
            }
        };

        final IClientConfig config = DefaultClientConfig.createWithAuthenticator(msaAuthenticator);
        config.getLogger().setLoggingLevel(LoggerLevel.Debug);
        return config;
    }

    private boolean results;

    /**
     * Clears out the auth token from the application store
     */
    boolean signOneDriveOut() {
        results = false;
        if (mClient.get() == null) {
            return false;
        }
        mClient.get().getAuthenticator().logout(new ICallback<Void>() {
            @Override
            public void success(final Void result) {
                results = true;
                mClient.set(null);
            }

            @Override
            public void failure(final ClientException ex) {
                results = false;
            }
        });
        return results;
    }

    /**
     * Clears out the auth token from the application store
     */
    void signOut() {
        if (mClient.get() == null) {
            return;
        }
        mClient.get().getAuthenticator().logout(new ICallback<Void>() {
            @Override
            public void success(final Void result) {
                mClient.set(null);
            }

            @Override
            public void failure(final ClientException ex) {
            }
        });
    }

    /**
     * Get an instance of the service
     * 
     * @return The Service
     */
    synchronized IOneDriveClient getOneDriveClient() {
        if (mClient.get() == null) {
            throw new UnsupportedOperationException("Unable to generate a new service object");
        }
        return mClient.get();
    }

    /**
     * Used to setup the Services
     * 
     * @param activity the current activity
     * @param serviceCreated the callback
     */
    synchronized void createOneDriveClient(final Activity activity, final ICallback<Void> serviceCreated) {
        final DefaultCallback<IOneDriveClient> callback = new DefaultCallback<IOneDriveClient>(activity) {
            @Override
            public void success(final IOneDriveClient result) {
                mClient.set(result);
                serviceCreated.success(null);
            }

            @Override
            public void failure(final ClientException error) {
                serviceCreated.failure(error);
            }
        };
        new OneDriveClient.Builder().fromConfig(createConfig()).loginAndBuildClient(activity, callback);
    }

}
