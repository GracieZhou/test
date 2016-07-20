package com.eostek.scifly.devicemanager.manage.appmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.ui.DownloadProcessView;

import java.util.List;

public class AppManagementAdapter extends BaseAdapter{

    private LayoutInflater minflater;
    
    private List<AppManagementInfo> mAppManagementInfoList;
    
    private Context mContext;
    
    private ViewHolder mViewHolder;
    
    public AppManagementAdapter(Context context, List<AppManagementInfo> list) {
        minflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mAppManagementInfoList = list;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mAppManagementInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mAppManagementInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = minflater.inflate(R.layout.act_appmanagement_gridview_item, null);
            mViewHolder.mIvUpdateIcon = (ImageView)convertView.findViewById(R.id.act_appmanagement_gridview_item_iv_update);
            mViewHolder.mIvAppIcon = (ImageView)convertView.findViewById(R.id.act_appmanagement_gridview_item_iv_icon);
            mViewHolder.mAppName = (TextView)convertView.findViewById(R.id.act_appmanagement_gridview_item_tv_name);
            mViewHolder.mPorcessView = (DownloadProcessView)convertView.findViewById(R.id.act_appmanagement_gridview_item_process_view);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder)convertView.getTag();
        }
        mAppManagementInfoList.get(position).setmPosition(position);
        AppManagementInfo appInfo = mAppManagementInfoList.get(position);
        if(appInfo != null) {
            if(appInfo.getmDrawable() != null) {
                mViewHolder.mIvAppIcon.setImageDrawable(appInfo.getmDrawable());
            } else {
                mViewHolder.mIvAppIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_icon));
            }
            if(appInfo.getmUpdateFlag() == true && appInfo.getmSystemApp() == false) {
                mViewHolder.mIvUpdateIcon.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.mIvUpdateIcon.setVisibility(View.GONE);
            }
            if(appInfo.getIsUpdating() == false) {
                mViewHolder.mPorcessView.setProgress(100);
            }
            mViewHolder.mAppName.setText(appInfo.getmName());
        }

        return convertView;
    }
    
    public static class ViewHolder {
        public ImageView mIvUpdateIcon;
        
        public ImageView mIvAppIcon;
        
        public TextView mAppName;
        
        public DownloadProcessView mPorcessView;
    }

}
