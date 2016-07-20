/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.eostek.scifly.album.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * @ClassName: ImagePagerActivity.
 * @Description:ImagePagerActivity.
 * @author: lucky.li.
 * @date: Dec 4, 2015 4:10:13 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class ImageDisplayActivity extends Activity {

    ImageDisplayHolder mHolder;

    private int mPeriodTime = 1;

    @ViewInject(R.id.image_pager)
    private ViewPager mViewPager;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == Constants.SLIDE_SHOW) {
                mHolder.mImagePager.setCurrentItem(mHolder.mCurrentPosition + 2);
                mHandler.sendEmptyMessageDelayed(Constants.SLIDE_SHOW, mPeriodTime * 1000);
            }
        };
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);
        ViewUtils.inject(this);
        mHolder = new ImageDisplayHolder(this);
        mHolder.mImagePager = mViewPager;
        mHolder.initDatas();
        mHolder.initPager();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mHolder.initDatas();
        mHolder.resetView();
    }

    public void slideShow() {
        mPeriodTime = mHolder.mPreferences.getInt(Constants.PERIOD_KEY, 1);
        mHandler.removeMessages(Constants.SLIDE_SHOW);
        mHandler.sendEmptyMessageDelayed(Constants.SLIDE_SHOW, mPeriodTime * 1000);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mHandler.removeMessages(Constants.SLIDE_SHOW);
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            Intent intent = new Intent(Constants.MENU_ACTION);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(Constants.IMAGE_PATHS, (ArrayList<String>) mHolder.mImageUrls);
            bundle.putInt(Constants.CURRENT_POSITION, mHolder.mCurrentPosition);
            intent.putExtra("bundle", bundle);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            slideShow();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
