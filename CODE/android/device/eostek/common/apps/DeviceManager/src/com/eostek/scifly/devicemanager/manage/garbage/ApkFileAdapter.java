
package com.eostek.scifly.devicemanager.manage.garbage;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.R;

public class ApkFileAdapter extends BaseAdapter {

    private LayoutInflater minflater;

    private List<ApkFileInfo> list;

    private Context context;
    
    private boolean isShow = false;

    public void setShow(boolean isShow){
    	this.isShow = isShow;
    }
    public boolean isShow(){
    	return isShow;
    }
    
    public ApkFileAdapter(final Context context, final List<ApkFileInfo> list) {
        minflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = minflater.inflate(R.layout.act_manage_garbage_apk_file_grid_item, null);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.cb_apk_file);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_apk_file);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.mSize = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        bindView(viewHolder, list.get(position));

        return convertView;
    }

	private void bindView(ViewHolder viewHolder, ApkFileInfo bigFileInfo) {
		if (bigFileInfo == null) {
			return;
		}
		if (isShow) {
			viewHolder.mCheckBox.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mCheckBox.setVisibility(View.GONE);
		}
		

		viewHolder.mCheckBox.setChecked(bigFileInfo.ismIsChecked());
		viewHolder.mImageView.setBackgroundResource(R.drawable.icon_big_file);
		viewHolder.mName.setText(bigFileInfo.getmName());
		viewHolder.mSize.setText(context.getResources().getString(R.string.act_bigfile_tv_big_file_space)
				+ bigFileInfo.getmSize());
	}

    public static class ViewHolder {

        public ImageView mImageView;

        public TextView mName;

        public TextView mSize;

        public CheckBox mCheckBox;
    }

}
