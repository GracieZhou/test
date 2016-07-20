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
package com.nostra13.universalimageloader.cache.memory.impl;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Limited {@link Object bitmap} cache. Provides {@link Object bitmaps} storing. Size of all stored bitmaps will not to
 * exceed size limit. When cache reaches limit size then the least recently used bitmap is deleted from cache.<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored Bitmaps. Strong references - for limited count of
 * Bitmaps (depends on cache size), weak references - for all other cached Bitmaps.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.3.0
 */
public class LRULimitedMemoryCache extends LimitedMemoryCache {

    private static final int INITIAL_CAPACITY = 10;
    private static final float LOAD_FACTOR = 1.1f;

    /** Cache providing Least-Recently-Used logic */
    private final Map<String, Object> lruCache = Collections.synchronizedMap(new LinkedHashMap<String, Object>(INITIAL_CAPACITY, LOAD_FACTOR, true));

    /** @param maxSize Maximum sum of the sizes of the Bitmaps in this cache */
    public LRULimitedMemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public boolean put(String key, Object value) {
        if (super.put(key, value)) {
            lruCache.put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object get(String key) {
        lruCache.get(key); // call "get" for LRU logic
        return super.get(key);
    }

    @Override
    public Object remove(String key) {
        lruCache.remove(key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        lruCache.clear();
        super.clear();
    }

    @Override
    protected int getSize(Object value) {
        if (value instanceof Bitmap) {
            return ((Bitmap)value).getRowBytes() * ((Bitmap)value).getHeight();
        } else if (value instanceof String) {
            return ((String)value).length();
        }
        return -1;
    }

    @Override
    protected Object removeNext() {
        Object mostLongUsedValue = null;
        synchronized (lruCache) {
            Iterator<Entry<String, Object>> it = lruCache.entrySet().iterator();
            if (it.hasNext()) {
                Entry<String, Object> entry = it.next();
                mostLongUsedValue = entry.getValue();
                it.remove();
            }
        }
        return mostLongUsedValue;
    }

    @Override
    protected Reference<Object> createReference(Object value) {
        return new WeakReference<Object>(value);
    }
}
