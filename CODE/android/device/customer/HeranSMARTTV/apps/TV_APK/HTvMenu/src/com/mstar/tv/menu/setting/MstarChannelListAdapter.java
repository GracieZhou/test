//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.mstar.tv.menu.setting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mstar.tv.menu.R;

public class MstarChannelListAdapter extends BaseAdapter implements Filterable {
    private Context mContext;

    public static final int ONE_PART_CHANNEL_REPLACE = 1000;

    // The items to be displayed.
    private List<ChannelInfo> mChannels;

    // Stores original items while filtering or switch
    private ArrayList<ChannelInfo> mOriginalValues;

    private final Object mLock = new Object();

    private ArrayFilter mFilter;

    private boolean shoudRefreshDataWhenSetItems = true;

    public void setRefreshWhenSetItems(boolean refresh) {
        shoudRefreshDataWhenSetItems = refresh;
    }

    public MstarChannelListAdapter(Context context) {
        mContext = context;
        mChannels = new ArrayList<ChannelInfo>();
    }

    public void setItems(List<ChannelInfo> items) {
        mChannels = items;
        mOriginalValues = new ArrayList<ChannelInfo>(items);
        if (shoudRefreshDataWhenSetItems)
            notifyDataSetChanged();
    }

    public List<ChannelInfo> getChannels() {
        return mChannels;
    }

    public List<ChannelInfo> getOriginalChannels() {
        return mOriginalValues;
    }

    /**
     * Add one channel, Should be called in Main thread
     * 
     * @param object the channel to be added
     */
    public void add(ChannelInfo object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
            }
            if (mChannels != null) {
                mChannels.add(object);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     * 
     * @param object The object to remove.
     */
    public void remove(ChannelInfo object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(object);
            }
            if (mChannels != null) {
                mChannels.remove(object);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            }
            if (mChannels != null) {
                mChannels.clear();
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChannels != null ? mChannels.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (mChannels == null || mChannels.size() == 0)
            return null;
        return mChannels.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (mChannels == null || mChannels.size() == 0)
            return 0;
        return mChannels.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.channel_item, null);
        }
        TextView channelId = (TextView) convertView.findViewById(R.id.channelid_txt);
        channelId.setText(mChannels.get(position).getChannelNumber());
        TextView channelName = (TextView) convertView.findViewById(R.id.channelname_txt);
        channelName.setText(mChannels.get(position).getChannelName());
        ImageView eye = (ImageView) convertView.findViewById(R.id.channel_eye);
        ImageView lock = (ImageView) convertView.findViewById(R.id.channel_lock);
        if (mChannels.get(position).getChannelDisable() == 0) {
            eye.setImageResource(R.drawable.eye);
        } else {
            eye.setImageResource(R.drawable.eye_dis);
        }
        if (mChannels.get(position).isLock()) {
            lock.setImageResource(R.drawable.menu_icon_lock);
        } else {
            lock.setImageResource(R.drawable.one_px);
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence filter) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    if (mOriginalValues == null)
                        mOriginalValues = new ArrayList<ChannelInfo>(mChannels);
                }
            }

            if (filter == null || filter.length() == 0) {
                ArrayList<ChannelInfo> list = new ArrayList<ChannelInfo>(mOriginalValues);
                results.values = list;
                results.count = list.size();
            } else {
                String filterString = filter.toString().toLowerCase();

                final int count = mOriginalValues.size();
                ArrayList<ChannelInfo> newChannels = new ArrayList<ChannelInfo>();
                for (int i = 0; i < count; i++) {
                    final String value = mOriginalValues.get(i).getChannelNumber();
                    if (value.startsWith(filterString)) {
                        newChannels.add(mOriginalValues.get(i));
                    }
                }
                results.values = newChannels;
                results.count = newChannels.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Clear previous date first.
            mChannels.clear();
            mChannels = (List<ChannelInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
