
package com.eostek.scifly.devicemanager.manage.process;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.util.Util;

public class ProcessGridAdapter extends BaseAdapter {

    public static final String TAG = ProcessGridAdapter.class.getSimpleName();

    private List<ProcessInfo> mProcessInfoList;
    private LayoutInflater mInflater;
    private int mLastSelectedPosition = -1;
    private UpdateStatu mUpdateStatu;
    private ViewHolder mViewHolder;
    private Context mContext;

    public ProcessGridAdapter(Context context, List<ProcessInfo> list) {
        this.mProcessInfoList = list;
        mInflater = LayoutInflater.from(context);
        mUpdateStatu = new UpdateStatu();
        this.mContext = context;
    }

    public int getLastSelectedPostion() {
        return mLastSelectedPosition;
    }

    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.mLastSelectedPosition = lastSelectedPosition;
    }

    private class UpdateStatu {

        private boolean mIsUpdate = false;

        private int mUpdatePosition = -1;

        private int mLastUpdatePosition = -1;

        public int getLastUpdatePosition() {
            return mLastUpdatePosition;
        }

        public void setLastUpdatePosition(int mLastUpdatePosition) {
            this.mLastUpdatePosition = mLastUpdatePosition;
        }

        public boolean isUpdate() {
            return mIsUpdate;
        }

        public void setIsUpdate(boolean mIsUpdate) {
            this.mIsUpdate = mIsUpdate;
        }

        public int getUpdatePosition() {
            return mUpdatePosition;
        }

        public void setUpdatePosition(int mUpdatePosition) {
            this.mUpdatePosition = mUpdatePosition;
        }

        public void setAll(boolean mIsUpdate, int mUpdatePosition, int mLastUpdatePosition) {
            setIsUpdate(mIsUpdate);
            setUpdatePosition(mUpdatePosition);
            setLastUpdatePosition(mLastUpdatePosition);
        }
    }

    public void notifyDataSetUpdate(int startPosition) {
        mUpdateStatu.setAll(true, startPosition, -1);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mProcessInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProcessInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.act_manage_process_gird_item, null);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        
        ProcessInfo processInfo = mProcessInfoList.get(position);
        if (null != processInfo) {
            mViewHolder.mProcessName.setText(processInfo.getName());
            mViewHolder.mProcessIcon.setImageDrawable(processInfo.getIcon());
            mViewHolder.mProcessIcon.setTag(processInfo.getPackageName());
            mViewHolder.mProcessMemory.setText(Util.formatKB2MB(processInfo.getMemory()));
            convertView.setBackgroundColor(processInfo.getColor());
        }

        if (mUpdateStatu.getLastUpdatePosition() == -1) {
            mUpdateStatu.setLastUpdatePosition(position);
        }

        if (mUpdateStatu.getLastUpdatePosition() > position) {
            if (position >= mUpdateStatu.getUpdatePosition() + 7 - 1) {
                mUpdateStatu.setAll(false, -1, -1);
            }
        } else if (mUpdateStatu.getLastUpdatePosition() < position) {
            if (position == 0) {
                mUpdateStatu.setAll(false, -1, -1);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView mProcessMemory;
        ImageView mProcessIcon;
        TextView mProcessName;
        
        public ViewHolder(View view) {
            this.mProcessMemory = (TextView) view.findViewById(R.id.tv_process_memory);
            this.mProcessIcon = (ImageView) view.findViewById(R.id.iv_process_icon);
            this.mProcessName = (TextView) view.findViewById(R.id.tv_process_name);
        }
    }

    public void setProcessInfoList(List<ProcessInfo> mProcessInfoList) {
        this.mProcessInfoList = mProcessInfoList;
    }

    public List<ProcessInfo> getmProcessInfoList() {
        return mProcessInfoList;
    }

}
