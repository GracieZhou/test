/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eostek.documentui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.eostek.documentui.data.DataProxy;

/**
 * @ClassName: DocumentsActivity.
 * @Description:the main ui.
 * @author: lucky.li.
 * @date: Oct 15, 2015 6:12:56 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class DocumentsActivity extends FragmentActivity {
    private final String TAG = "DocumentsActivity";

    /**
     * the holder to control ui
     */
    private DocumentsHolder mHolder;

    /**
     * class of operating the datas
     */
    private DataProxy mDataProxy;

    private String version = "1.0.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Name : documentui, Version :" + version
                + ", Date : 2015-10-22, Publisher : lucky.li, Revision : 44297");
        setContentView(R.layout.new_activity_main);
        getWindow().setBackgroundDrawable(null);
        mDataProxy = new DataProxy(this);
        mHolder = new DocumentsHolder(this, mDataProxy);
        
        DisplayMetrics outMetrics = new DisplayMetrics();
        
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        
        StringBuffer buffer = new StringBuffer("outMetrics : \n");
        buffer.append("outMetrics.density").append(outMetrics.density).append("\n");
        buffer.append("outMetrics.densityDpi").append(outMetrics.densityDpi).append("\n");
        buffer.append("outMetrics.heightPixels").append(outMetrics.heightPixels).append("\n");
        buffer.append("outMetrics.scaledDensity").append(outMetrics.scaledDensity).append("\n");
        buffer.append("outMetrics.widthPixels").append(outMetrics.widthPixels).append("\n");
        buffer.append("outMetrics.xdpi").append(outMetrics.xdpi).append("\n");
        buffer.append("outMetrics.ydpi").append(outMetrics.ydpi).append("\n");

        Log.e(TAG, buffer.toString());
    }

    /**
     * @Title: getHolder.
     * @Description: get the holder.
     * @param: @return.
     * @return: DocumentsHolder.
     * @throws
     */
    public DocumentsHolder getHolder() {
        return mHolder;
    }
}
