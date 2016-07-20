
package com.eostek.scifly.browser.collect;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.BrowserApplication;
import com.eostek.scifly.browser.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectAdapter extends BaseAdapter {

    private BrowserActivity mActivity;

    public List<CollectItemBean> mCollectAdapterList = new ArrayList<CollectItemBean>();

    public CollectAdapter(List<CollectItemBean> imgUrlList, BrowserActivity activity) {
        mActivity = activity;
        for (int i = 0; i < imgUrlList.size(); i++) {
            mCollectAdapterList.add(imgUrlList.get(i));
        }
    }

    public void setList(ArrayList<CollectItemBean> imgUrlList) {
        if (imgUrlList != null) {
            mCollectAdapterList = (List<CollectItemBean>) imgUrlList.clone();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (mCollectAdapterList == null) {
            return 0;
        } else {
            return mCollectAdapterList.size();
        }
    }

    @Override
    public CollectItemBean getItem(int position) {

        return mCollectAdapterList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.collect_gridview_item, null);
        }
        ImageView mCoverView = ViewHolder.get(convertView, R.id.collect_gv_item_img);
        ImageView mCancelView = ViewHolder.get(convertView, R.id.collect_gv_cancel_img);
        TextView mTextview = ViewHolder.get(convertView, R.id.collect_item_text);
        mTextview.setText(CollectFragment.mQueryList.get(position).mTitle);
        ImageLoader.getInstance().displayImage(getItem(position).getImgUrl(), mCoverView,
                BrowserApplication.getInstance().getDisplayImageOptions());
        
        mCancelView.setBackgroundResource(R.drawable.delete);

        if (getItem(position).isDeleteMode()) {
            mCancelView.setVisibility(View.VISIBLE);
        } else {
            mCancelView.setVisibility(View.INVISIBLE);
        }
        return convertView;

    }

}
