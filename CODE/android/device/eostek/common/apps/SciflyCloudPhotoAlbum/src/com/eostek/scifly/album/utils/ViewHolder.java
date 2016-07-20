
package com.eostek.scifly.album.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * @ClassName: ViewHolder.
 * @Description:ViewHolder.
 * @author: lucky.li.
 * @date: Sep 16, 2015 11:54:24 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public final class ViewHolder {
    /**
     * 
     */
    private ViewHolder() {

    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View convertView, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            convertView.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
