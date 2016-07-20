package com.android.packageinstaller;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ApkSeletedItemAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private PackageInstallerActivity mContext;
    List<PackageModel> mList;
    
    private final String TAG = "ApkSeletedItemAdapter";
    
    public ApkSeletedItemAdapter(PackageInstallerActivity context) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
    }
    
    public ApkSeletedItemAdapter(PackageInstallerActivity context, List<PackageModel> list) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        this.mList = list;
    }

    public void setmList(List<PackageModel> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        
        convertView = mInflater.inflate(R.layout.apk_installing_item, null);
        ImageView apk_thumbnail_View = (ImageView)convertView.findViewById(R.id.apk_thumbnail);
        TextView apk_title_View = (TextView)convertView.findViewById(R.id.apk_title);
        TextView apk_version_View = (TextView)convertView.findViewById(R.id.apk_version);
        TextView apk_size_View = (TextView)convertView.findViewById(R.id.apk_size);
        ImageView status_view = (ImageView)convertView.findViewById(R.id.apk_install_status);
        ProgressBar installing_circle_progress = (ProgressBar)convertView.findViewById(R.id.apk_installing_circleprogress);
        
        PackageModel apk = mList.get(position);
        apk_thumbnail_View.setImageDrawable(apk.getIcon());
        apk_title_View.setText(apk.getAppName());
        apk_version_View.setText(apk.getVersionName());
        apk_size_View.setText("" + apk.getSize());
        
        if (apk.getStatus() == PackageModel.FLAG_INSTALLING){
            status_view.setVisibility(View.GONE);
            installing_circle_progress.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "" + installing_circle_progress);
            installing_circle_progress.setVisibility(View.GONE);
            status_view.setVisibility(View.VISIBLE);
            
            if (apk.getStatus() == PackageModel.FLAG_SELECTED){
                status_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_selected));
            } else if (apk.getStatus() == PackageModel.FLAG_NOT_SELECTED) {
                status_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.apk_not_selected));
            } else if (apk.getStatus() == PackageModel.FLAG_INSTALL_WAITING) {
                status_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.install_wait));
            } else if (apk.getStatus() == PackageModel.FLAG_INSTALL_SUCCESS) {
                status_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.install_success));
            } else if (apk.getStatus() == PackageModel.FLAG_INSTALL_FAILUER) {
                status_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.install_fail));
            }
        }
        return convertView;
    }
    

}
