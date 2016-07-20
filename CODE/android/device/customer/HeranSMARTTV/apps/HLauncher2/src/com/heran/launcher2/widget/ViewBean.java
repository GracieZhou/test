/**
 * 
 */

package com.heran.launcher2.widget;

import com.heran.launcher2.util.FocusView;

import android.util.Log;
import android.view.View;

/**
 * projectName： HLauncher2 moduleName：ViewBean.java
 * 
 * @author laird.li
 * @version 1.0.0
 * @time 2016-03-30
 * @Copyright © 2016 Eos, Inc.
 */
public class ViewBean {

    String TAG = "ViewBean";

    // the focusview object
    private FocusView mFocusObject;

    // the current focus view
    private View mCurFocusView;

    public ViewBean(FocusView mFocusObject, View mCurrFocusView) {
        this.mFocusObject = mFocusObject;
        this.mCurFocusView = mCurrFocusView;
    }

    public FocusView getmFocusObject() {
        return mFocusObject;
    }

    public void setmFocusObject(FocusView mFocusView) {
        this.mFocusObject = mFocusView;
    }

    /**
     * get the current focus view
     * 
     * @return The current focus view
     */
    public View getmCurFocusView() {
        return mCurFocusView;
    }

    /**
     * set current focus view
     * 
     * @param view
     */
    public void setmCurFocusView(View mCurFocusView) {
        Log.d(TAG, "setmCurFocusView" + mCurFocusView);
        this.mCurFocusView = mCurFocusView;
    }

}
