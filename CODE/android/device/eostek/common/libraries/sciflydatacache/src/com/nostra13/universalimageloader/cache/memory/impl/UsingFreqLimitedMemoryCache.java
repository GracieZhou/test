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
import com.nostra13.universalimageloader.utils.L;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Limited {@link Bitmap bitmap} cache. Provides {@link Bitmap bitmaps} storing. Size of all stored bitmaps will not to
 * exceed size limit. When cache reaches limit size then the bitmap which used the least frequently is deleted from
 * cache.<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored Bitmaps. Strong references - for limited count of
 * Bitmaps (depends on cache size), weak references - for all other cached Bitmaps.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class UsingFreqLimitedMemoryCache extends LimitedMemoryCache {
    /**
     * Contains strong references to stored objects (keys) and last object usage date (in milliseconds). If hard cache
     * size will exceed limit then object with the least frequently usage is deleted (but it continue exist at
     * {@link #softMap} and can be collected by GC at any time)
     */
    private final Map<Object, Integer> usingCounts = Collections.synchronizedMap(new HashMap<Object, Integer>());
    private final int singleMemorySize;
    
    public UsingFreqLimitedMemoryCache(int sizeLimit) {
        this(sizeLimit, 0);
    }

    public UsingFreqLimitedMemoryCache(int sizeLimit, int singleMemorySize) {
        super(sizeLimit);
        this.singleMemorySize = singleMemorySize;
    }

    @Override
    public boolean put(String key, Object value) {
        final int size = getSize(value);
        if (singleMemorySize > 0 && size > singleMemorySize) {
            L.d("singleMemorySize=%d, size=%d", singleMemorySize, size);
            return false;
        }
        if (super.put(key, value)) {
            usingCounts.put(value, 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object get(String key) {
        Object value = super.get(key);
        // Increment usage count for value if value is contained in hardCahe
        if (value != null) {
            Integer usageCount = usingCounts.get(value);
            if (usageCount != null) {
                usingCounts.put(value, usageCount + 1);
            }
        }
        return value;
    }

    @Override
    public Object remove(String key) {
        Object value = super.get(key);
        if (value != null) {
            usingCounts.remove(value);
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        usingCounts.clear();
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
        Integer minUsageCount = null;
        Object leastUsedValue = null;
        Set<Entry<Object, Integer>> entries = usingCounts.entrySet();
        synchronized (usingCounts) {
            for (Entry<Object, Integer> entry : entries) {
                if (leastUsedValue == null) {
                    leastUsedValue = entry.getKey();
                    minUsageCount = entry.getValue();
                } else {
                    Integer lastValueUsage = entry.getValue();
                    if (lastValueUsage < minUsageCount) {
                        minUsageCount = lastValueUsage;
                        leastUsedValue = entry.getKey();
                    }
                }
            }
        }
        usingCounts.remove(leastUsedValue);
        return leastUsedValue;
    }

    @Override
    protected Reference<Object> createReference(Object value) {
        return new WeakReference<Object>(value);
    }
}
