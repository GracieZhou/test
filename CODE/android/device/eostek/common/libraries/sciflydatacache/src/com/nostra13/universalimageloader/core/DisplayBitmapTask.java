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
package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.os.Handler;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

/**
 * Displays bitmap in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware}. Must be called on UI thread.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoadingListener
 * @see BitmapDisplayer
 * @since 1.3.1
 */
final class DisplayBitmapTask implements Runnable {

    private static final String LOG_DISPLAY_IMAGE_IN_IMAGEAWARE = "Display image in ImageAware (loaded from %1$s) [%2$s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";

    private final Bitmap bitmap;
    private final String imageUri;
    private final ImageAware imageAware;
    private final String memoryCacheKey;
    private final BitmapDisplayer displayer;
    private final ImageLoadingListener listener;
    private final ImageLoaderEngine engine;
    private final LoadedFrom loadedFrom;
    private final ImageLoadingInfo imageLoadingInfo;
    private final Handler handler;
    private final boolean syncLoading;

    public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine,
            LoadedFrom loadedFrom) {
        this.bitmap = bitmap;
        imageUri = imageLoadingInfo.uri;
        imageAware = imageLoadingInfo.imageAware;
        memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        displayer = imageLoadingInfo.options.getDisplayer();
        listener = imageLoadingInfo.listener;
        this.engine = engine;
        this.loadedFrom = loadedFrom;
        this.imageLoadingInfo = imageLoadingInfo;
        this.syncLoading = false;
        this.handler = null;
    }
    
    public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine,
            LoadedFrom loadedFrom, boolean syncLoading, Handler handler) {
        this.bitmap = bitmap;
        imageUri = imageLoadingInfo.uri;
        imageAware = imageLoadingInfo.imageAware;
        memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        displayer = imageLoadingInfo.options.getDisplayer();
        listener = imageLoadingInfo.listener;
        this.engine = engine;
        this.loadedFrom = loadedFrom;
        this.imageLoadingInfo = imageLoadingInfo;
        this.syncLoading = syncLoading;
        this.handler = handler;
    }

    @Override
    public void run() {
        if (imageAware.isCollected()) {
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED, memoryCacheKey);
            listener.onLoadingCancelled(imageUri, imageAware.getWrappedView());
        } else if (isViewWasReused()) {
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_REUSED, memoryCacheKey);
            listener.onLoadingCancelled(imageUri, imageAware.getWrappedView());
        } else if (loadedFrom == LoadedFrom.LOCAL_NETWORK) {
            if (bitmap != null) {
                L.d("Checking image completed (loaded from %1$s) [%2$s]", loadedFrom, memoryCacheKey);
                displayer.display(bitmap, imageAware, LoadedFrom.NETWORK);
                listener.onCheckingComplete(imageUri, imageAware.getWrappedView(), bitmap);
            }
            engine.cancelDisplayTaskFor(imageAware);
        } else {
            L.d(LOG_DISPLAY_IMAGE_IN_IMAGEAWARE, loadedFrom, memoryCacheKey);
            displayer.display(bitmap, imageAware, loadedFrom);
            listener.onLoadingComplete(imageUri, imageAware.getWrappedView(), bitmap);
            // check if bitmap modified from network when load from memory or disk
            if (loadedFrom != LoadedFrom.NETWORK && imageLoadingInfo.options.shouldCheckIfModified()) {
                L.d("Need to check if image modified (loaded from %1$s) [%2$s]", loadedFrom, memoryCacheKey);
                CheckAndDisplayImageTask checkTask = new CheckAndDisplayImageTask(engine, imageLoadingInfo, handler);
                engine.submit(checkTask);
            } else {
                L.d("No need to check if image modified (loaded from %1$s) [%2$s]", loadedFrom, memoryCacheKey);
                engine.cancelDisplayTaskFor(imageAware);
            }
        }
    }

    /** Checks whether memory cache key (image URI) for current ImageAware is actual */
    private boolean isViewWasReused() {
        String currentCacheKey = engine.getLoadingUriForView(imageAware);
        return !memoryCacheKey.equals(currentCacheKey);
    }
}
