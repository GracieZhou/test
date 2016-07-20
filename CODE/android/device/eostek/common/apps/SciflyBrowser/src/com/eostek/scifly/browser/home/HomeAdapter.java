package com.eostek.scifly.browser.home;

import java.util.List;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.eostek.scifly.browser.BrowserApplication;
import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.modle.UrlModle;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeAdapter extends BaseAdapter{

    private final String TAG= "HomeAdapter";
    private List<UrlModle> mList;

    private Context mContext;

    private LayoutInflater mInflater;

    public HomeAdapter(Context context, List<UrlModle> list) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }
    
    public void setList(List<UrlModle> list) {
        mList = list;
    }
    
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getview position=" + position);
        SuggestViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new SuggestViewHolder();
            convertView= mInflater.inflate(R.layout.suggest_item_layout, null);
            viewHolder.mImageView = (ImageView)convertView.findViewById(R.id.suggest_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SuggestViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(mList.get(position).mImgUrl, viewHolder.mImageView,
                BrowserApplication.getInstance().getDisplayImageOptions());
        return convertView;
    }

    class SuggestViewHolder {
        public ImageView mImageView;
    }
}
