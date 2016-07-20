/**
 * 
 */

package com.heran.launcher.model;

import com.heran.launcher.util.FocusView;

import android.view.View;

/*
 * projectName： EosLauncher
 * moduleName： ViewBean.java
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2013-12-24 下午6:07:16
 * @Copyright © 2013 Eos Inc.
 */

public class ViewBean {

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
        this.mCurFocusView = mCurFocusView;
    }

}
