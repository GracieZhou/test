
package com.bq.tv.traxex.util;

import android.util.Log;

/**
 * Class of Item loader.
 */
public abstract class ItemLoader {
    private boolean allViewportItemsLoaded;// 是否整个视窗的item都已被loadded.

    private int bufferEndIndex;// 缓冲最后一个元素索引.

    private int bufferStartIndex;// 缓冲的第一个元素的索引.

    private boolean isPaused;// 当前是否处于pause状态.

    private int newBufferEndIndex;// 设置的新的缓存最后一个元素索引

    private int newBufferStartIndex;// 设置的新的缓存的第一个元素索引

    private boolean newIsPaused;// 是否设置了pause.

    private int newItemCount;// 新 item数量

    private int newViewportEndIndex;// 新视窗中做后一个元素索引.

    private int newViewportStartIndex;// 新视窗第一个元素索引.

    private int viewportEndIndex;// 当前视窗最后一个元素索引

    private int viewportStartIndex;// 当前视窗第一个元素索引

    private void ensureItemLoadingOrLoaded(int paramInt) {
        ItemState state = itemState(paramInt);

        if (state == ItemState.NOT_LOADED) {
            requestStartLoadingItem(paramInt);
        } else if (state == ItemState.LOADING_PAUSED) {
            requestResumeLoadingItem(paramInt);
        }
    }

    private void ensureItemNotLoaded(int paramInt) {
        ItemState state = itemState(paramInt);

        if (state == ItemState.LOADING || state == ItemState.LOADING_PAUSED) {
            requestCancelLoadingItem(paramInt);
        } else if (state == ItemState.LOADED) {
            requestUnloadItem(paramInt);
        }
    }

    private void ensureItemNotLoading(int paramInt) {
        ItemState state = itemState(paramInt);

        if (state == ItemState.LOADING) {
            requestPauseLoadingItem(paramInt);
        }
    }

    private void requestCancelLoadingItem(int paramInt) {
        Log.d("ItemLoader", "cancelLoadingItem(" + paramInt + ")");
        ItemState state = itemState(paramInt);
        if ((state == ItemState.LOADING) || (state == ItemState.LOADING_PAUSED)) {

            setItemState(paramInt, ItemState.NOT_LOADED);
            cancelLoadingItem(paramInt);
        }
    }

    private void requestPauseLoadingItem(int paramInt) {
        Log.d("ItemLoader", "pauseLoadingItem(" + paramInt + ")");
        if (itemState(paramInt) == ItemState.LOADING) {
            setItemState(paramInt, ItemState.LOADING_PAUSED);
            pauseLoadingItem(paramInt);
        }
    }

    private void requestResumeLoadingItem(int paramInt) {
        Log.d("ItemLoader", "resumeLoadingItem(" + paramInt + ")");
        if (itemState(paramInt) == ItemState.LOADING_PAUSED) {
            setItemState(paramInt, ItemState.LOADING);
            resumeLoadingItem(paramInt);
        }
    }

    private void requestStartLoadingItem(int paramInt) {
        Log.d("ItemLoader", "startLoadingItem(" + paramInt + ")");
        if (itemState(paramInt) == ItemState.NOT_LOADED) {
            setItemState(paramInt, ItemState.LOADING);
            startLoadingItem(paramInt);
        }
    }

    private void requestUnloadItem(int paramInt) {
        Log.d("ItemLoader", "unloadItem(" + paramInt + ")");
        if (itemState(paramInt) == ItemState.LOADED) {
            setItemState(paramInt, ItemState.NOT_LOADED);
            unloadItem(paramInt);
        }
    }

    /**
     * Get all loaded items view port.
     * 
     * @return
     */
    public final boolean allViewportItemsLoaded() {
        return this.allViewportItemsLoaded;
    }

    protected abstract void cancelLoadingItem(int paramInt);

    /**
     * Load item.
     * 
     * @param paramInt
     */
    public final void didLoadItem(int paramInt) {
        Log.d("ItemLoader", "didLoadItem(" + paramInt + ")");
        setItemState(paramInt, ItemState.LOADED);
        updateItems();
    }

    protected abstract ItemState itemState(int paramInt);

    protected abstract void pauseLoadingItem(int paramInt);

    protected abstract void resumeLoadingItem(int paramInt);

    /**
     * Set end index of buffer.
     * 
     * @param paramInt
     */
    public final void setBufferEndIndex(int paramInt) {
        this.newBufferEndIndex = paramInt;
    }

    /**
     * Set start index of buffer.
     * 
     * @param paramInt
     */
    public final void setBufferStartIndex(int paramInt) {
        this.newBufferStartIndex = paramInt;
    }

    /**
     * Set count of item.
     * 
     * @param paramInt
     */
    public final void setItemCount(int paramInt) {
        this.newItemCount = paramInt;
    }

    protected abstract void setItemState(int paramInt, ItemState paramItemState);

    /**
     * Set paused.
     * 
     * @param paramBoolean
     */
    public final void setPaused(boolean paramBoolean) {
        this.newIsPaused = paramBoolean;
    }

    /**
     * Set end index of Viewport.
     * 
     * @param paramInt
     */
    public final void setViewportEndIndex(int paramInt) {
        this.newViewportEndIndex = paramInt;
    }

    /**
     * Set start index of Viewport.
     * 
     * @param paramInt
     */
    public final void setViewportStartIndex(int paramInt) {
        this.newViewportStartIndex = paramInt;
    }

    protected abstract void startLoadingItem(int paramInt);

    protected abstract void unloadItem(int paramInt);

    /**
     * Update Items.
     */
    public final void updateItems() {

        for (int i = this.bufferStartIndex; i < this.newBufferStartIndex; i++)
            ensureItemNotLoaded(i);

        for (int j = this.newBufferEndIndex; j < Math.min(this.bufferEndIndex, this.newItemCount); j++)
            ensureItemNotLoaded(j);

        this.viewportStartIndex = this.newViewportStartIndex;
        this.viewportEndIndex = this.newViewportEndIndex;
        this.bufferStartIndex = this.newBufferStartIndex;
        this.bufferEndIndex = this.newBufferEndIndex;
        this.isPaused = this.newIsPaused;

        if (this.isPaused) {
            for (int i1 = this.bufferStartIndex; i1 < this.bufferEndIndex; i1++)
                ensureItemNotLoading(i1);
        }

        boolean viewportItem = true;

        for (int k = this.viewportStartIndex; k < this.viewportEndIndex; k++) {
            ensureItemLoadingOrLoaded(k);
            if (itemState(k) != ItemState.LOADED)
                viewportItem = false;
        }

        this.allViewportItemsLoaded = viewportItem;

        if (this.allViewportItemsLoaded) {
            for (int m = this.bufferStartIndex; m < this.viewportStartIndex; m++)
                ensureItemLoadingOrLoaded(m);
            for (int n = this.viewportEndIndex; n < this.bufferEndIndex; n++)
                ensureItemLoadingOrLoaded(n);
        }
    }

    /**
     * Item state.
     */
    public static enum ItemState {
        NOT_LOADED, LOADING, LOADING_PAUSED, LOADED
    }
}
