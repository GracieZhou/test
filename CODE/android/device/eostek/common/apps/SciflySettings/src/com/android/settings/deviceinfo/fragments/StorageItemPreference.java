/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.settings.deviceinfo.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.UserHandle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;

public class StorageItemPreference extends Preference {

    private static final String TAG = "StorageItemPreference";

    private  CharSequence mTitle;

    private String mUsage = null;

    int mColor;

    int mUserHandle;

    public StorageItemPreference(Context context, int titleRes, int colorRes) {
        this(context, context.getText(titleRes), colorRes, UserHandle.USER_NULL);
    }

    public StorageItemPreference(Context context, CharSequence title, int colorRes, int userHandle) {
        super(context);
        setLayoutResource(R.layout.preference_storage_item);

        this.mTitle = title;
        if (colorRes != 0) {
            this.mColor = context.getResources().getColor(colorRes);
        } else {
            this.mColor = Color.TRANSPARENT;
        }
        this.mUserHandle = userHandle;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        final Resources res = getContext().getResources();
        final int width = res.getDimensionPixelSize(R.dimen.device_memory_usage_button_width);
        final int height = res.getDimensionPixelSize(R.dimen.device_memory_usage_button_height);

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageDrawable(createRectShape(width, height, this.mColor));

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(this.mTitle);

        TextView usage = (TextView) view.findViewById(R.id.usage);
        if (mUsage == null) {
            usage.setText(R.string.memory_calculating_size);
        } else {
            Log.d(TAG, "update usage : " + mUsage);
            usage.setText(mUsage);
        }
    }

    private static ShapeDrawable createRectShape(int width, int height, int color) {
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.setIntrinsicHeight(height);
        shape.setIntrinsicWidth(width);
        shape.getPaint().setColor(color);
        return shape;
    }

    protected void updateSize(String size) {
        mUsage = size;
    }
    protected void updateTitle(String title) {
        mTitle = title;
    }
}
