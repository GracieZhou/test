
package com.eostek.scifly.browser;

import android.os.Message;
import android.util.Log;
import android.view.View;

import com.eostek.scifly.browser.business.WebViewHelper;
import com.eostek.scifly.browser.util.Constants;

/**
 * projectName： Browser moduleName： BrowserLogic.java
 * 
 * @author Shirley.jiang & Ahri.chen
 * @time 2016-1-27 
 */
public class BrowserLogic extends StateMachine {

    private final String TAG ="BrowserLogic";

    private BrowserActivity mActivity;

    private HomeState mHomeState = new HomeState();

    private CollectState mCollectState = new CollectState();

    private SetToolState mSetToolState = new SetToolState();

    private static boolean isShowHomeLayout = true;
    
    public static boolean isCollectWebPage = false;

    /**
     * init BroserLogic.
     * @param name
     * @param activity
     */
    public BrowserLogic(String name, BrowserActivity activity) {
        super(name);
        mActivity = activity;
        addState(mHomeState);
        addState(mCollectState);
        addState(mSetToolState);
        setInitialState(mHomeState);
        start();
    }

    private class HomeState extends State {
        @Override
        public void enter() {
            Log.e(TAG, "gotoHomeFragment");
            mActivity.mHolder.gotoHomeFragment();
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case Constants.POSITION_HOME:
                    transitionTo(mHomeState);
                    break;
                case Constants.POSITION_COLLECT:
                    transitionTo(mCollectState);
                    break;
                case Constants.POSITION_SETTOOL:
                    transitionTo(mSetToolState);
                    break;

                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
        }
    }

    private class CollectState extends State {
        @Override
        public void enter() {
            Log.e(TAG, "gotoCollectFragment");
            mActivity.mHolder.gotoCollectFragment();
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case Constants.POSITION_HOME:
                    transitionTo(mHomeState);
                    break;
                case Constants.POSITION_COLLECT:
                    transitionTo(mCollectState);
                    break;
                case Constants.POSITION_SETTOOL:
                    transitionTo(mSetToolState);
                    break;

                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
        }
    }

    private class SetToolState extends State {
        @Override
        public void enter() {
            Log.e(TAG, "gotoSetToolFragment");
            mActivity.mHolder.gotoSetToolFragment();
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case Constants.POSITION_HOME:
                    transitionTo(mHomeState);
                    break;
                case Constants.POSITION_COLLECT:
                    transitionTo(mCollectState);
                    break;
                case Constants.POSITION_SETTOOL:
                    transitionTo(mSetToolState);
                    break;

                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
        }
    }

    /**
     * Switch state(page)
     * @param position {@link Constants.POSITION_HOME, POSITION_COLLECT, POSITION_SETTOOL}
     */
    public void gotoState(int position) {
        mActivity.setPosition(position);
        sendMessage(position);
    }

    /**
     * show main page.
     */
    public void showHome() {
        mActivity.mHolder.mWebLayout.setVisibility(View.GONE);
        mActivity.mHolder.mHomeLayout.setVisibility(View.VISIBLE);
        WebViewHelper.getInstance(mActivity).onPause();
        isShowHomeLayout = true;
        isCollectWebPage = false;
        Log.d(TAG, "showHome...");
    }

    /**
     * show Webview.
     */
    public void showWeb() {
        mActivity.mHolder.mHomeLayout.setVisibility(View.GONE);
        mActivity.mHolder.mWebLayout.setVisibility(View.VISIBLE);
        isShowHomeLayout = false;
        Log.d(TAG, "showWeb...");
    }

    /**
     * from collect page to webview.
     */
    public void showCollectWeb() {
        mActivity.mHolder.mWebLayout.setVisibility(View.VISIBLE);
        isCollectWebPage = true;
    }

    /**
     * exit webview to collect page.
     */
    public void exitCollectWeb() {
        mActivity.mHolder.mWebLayout.setVisibility(View.GONE);
        isCollectWebPage = false;
    }

    /**
     * current show is mainLayout(homeLayout) or webview.
     * @return  if true, current is mainLayout(homeLayout); if false, current is Webview.
     */
    public boolean isShowHomeLayout() {
        return isShowHomeLayout;
    }
}
