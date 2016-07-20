
package com.mstar.tv.menu.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.tv.menu.R;

public class ParentalGuidanceDialog extends Dialog {

    private ListView guidancelist;

    private String[] parental;

    private boolean[] rating;

    private GuidanceListAdapter adapter;

    private Context mcontext;

    public ParentalGuidanceDialog(Context context) {
        super(context);
        mcontext = context;
    }

    private class GuidanceListAdapter extends BaseAdapter {
        String[] content = null;

        private Context mContext;

        private boolean[] rating;

        public GuidanceListAdapter(Context context, String[] data, boolean[] rate) {
            mContext = context;
            content = data;
            rating = rate;
        }

        @Override
        public int getCount() {
            return content.length;
        }

        @Override
        public Object getItem(int arg0) {
            return content[arg0];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.guidance_item, null);
            }
            convertView = LayoutInflater.from(mContext).inflate(R.layout.guidance_item, null);
            TextView tmpText = (TextView) convertView.findViewById(R.id.guidance_content);
            tmpText.setText(content[position]);
            ImageView islock = (ImageView) convertView.findViewById(R.id.ifchoice);
            if (rating[position]) {
                islock.setVisibility(View.GONE);
            }
            return convertView;
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parental_guidance);
        parental = mcontext.getResources().getStringArray(R.array.guidance_list);
        guidancelist = (ListView) findViewById(R.id.parental_guidance_list);
        int rate = TvManager.getInstance().getParentalcontrolManager().GetParentalControlRating();
        if (rate == 0) {
            // when we close parental lock,the rate set 19;
            TvManager.getInstance().getParentalcontrolManager().setParentalControlRating(19);
        }
        rating = new boolean[parental.length];
        for (int i = 0; i < parental.length; i++) {
            rating[i] = true;
        }
        if ((rate < 19) && (rate > 4)) {
            rating[rate - 4] = false;
        } else {
            rating[0] = false;
        }
        adapter = new GuidanceListAdapter(mcontext, parental, rating);
        guidancelist.setAdapter(adapter);
        setListener();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (guidancelist.getSelectedItemPosition() == 0) {
                guidancelist.setSelection(parental.length - 1);
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (guidancelist.getSelectedItemPosition() == parental.length - 1) {
                guidancelist.setSelection(0);
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setListener() {
        guidancelist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                int oldrate = TvManager.getInstance().getParentalcontrolManager()
                        .GetParentalControlRating();
                if (arg2 == 0) {
                    if (oldrate == 19) {
                        return;
                    } else {
                        rating[oldrate - 4] = true;
                        rating[arg2] = false;
                    }
                    TvManager.getInstance().getParentalcontrolManager()
                            .setParentalControlRating(19);
                } else {
                    if (oldrate == 19) {
                        rating[0] = true;

                    } else {
                        rating[oldrate - 4] = true;
                    }
                    rating[arg2] = false;
                    TvManager.getInstance().getParentalcontrolManager()
                            .setParentalControlRating(arg2 + 4);
                }

                adapter.notifyDataSetChanged();

            }
        });
    }
}
