
package com.eostek.documentui.fragment;

import java.lang.reflect.Method;

import android.R.dimen;
import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.documentui.Constants;
import com.eostek.documentui.DocumentsActivity;
import com.eostek.documentui.R;
import com.eostek.documentui.util.ViewHolder;

@SuppressLint("ValidFragment")
public class DownloadedFragment extends Fragment {
    private DocumentsActivity mActivity;

    // private View mRootView;

    private String[] itemNames;

    private int[] itemIcon;

    private DownloadedAdapter mAdapter;

    private GridView mGridView;

    private int currentIndex = 0;
    
    private int mlastposition = 0; 

    private final int VIDEOINDEX = 0;

    private final int MUSICINDEX = 1;

    private final int PICTRUEINDEX = 2;

    private final int DOCUMENTINDEX = 3;
    
    private final int OTHERTINDEX = 5;

    private final String ACTIVITYCLASSIFYACTION = "android.intent.action.DOWNLOADEDCLASSIFY";

    public DownloadedFragment(DocumentsActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGridView = (GridView) inflater.inflate(R.layout.dloaded_gridviewlayout, container, false);
        initAdapter();
        findViews();
        return mGridView;
    }

    private void findViews() {
        // mGridView = (GridView) mRootView.findViewById(R.id.downloaded_grid);
        mGridView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                currentIndex = mGridView.getSelectedItemPosition();
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if((currentIndex == PICTRUEINDEX||currentIndex == OTHERTINDEX)
                            && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                        return true;
                    }
                    if ((currentIndex == VIDEOINDEX || currentIndex == MUSICINDEX || currentIndex == PICTRUEINDEX)
                            && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        mActivity.getHolder().reuqestFocus(Constants.DownloadedFragmentIndex);
                        return true;
                    }
                    if ((currentIndex == VIDEOINDEX || currentIndex == DOCUMENTINDEX)
                            && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                }
                return false;
            }

        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ACTIVITYCLASSIFYACTION);
                intent.putExtra("type", position);
                startActivity(intent);
            }
        });
        mGridView.setAdapter(mAdapter);
        mGridView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    try {
                        @SuppressWarnings("unchecked")
                        Class<GridView> c = (Class<GridView>) Class.forName("android.widget.GridView");
                        Method[] flds = c.getDeclaredMethods();
                        for (Method f : flds) {
                            if ("setSelectionInt".equals(f.getName())) {
                                f.setAccessible(true);
                                f.invoke(v, new Object[] {
                                    Integer.valueOf(-1)
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    View viewitem;
                    ImageView itemfocusimg;
                    ImageView itemimg;
                    final int btheight = getResources().getDimensionPixelSize(R.dimen.img_button_height);
                    final int btwidth = getResources().getDimensionPixelSize(R.dimen.img_button_width);
                    if (mGridView.getChildAt(mlastposition) != null) {
                        viewitem = mGridView.getChildAt(mlastposition);
                        itemfocusimg = (ImageView) viewitem.findViewById(R.id.focusimg);
                        itemfocusimg.setVisibility(View.INVISIBLE);
                        itemimg = (ImageView) viewitem.findViewById(R.id.imageitemview);
                        ViewGroup.LayoutParams lp;
                        lp = itemimg.getLayoutParams();
                        lp.height = btheight;
                        lp.width = btwidth;
                        itemimg.setLayoutParams(lp);
                    }
                }else{
                    //currentIndex = mGridView.getSelectedItemPosition();
                    mlastposition = 0;
                    View viewitem;
                    ImageView itemfocusimg;
                    ImageView itemimg;
                    final int btheight = getResources().getDimensionPixelSize(R.dimen.img_button_height);
                    final int btwidth = getResources().getDimensionPixelSize(R.dimen.img_button_width);
                    if(mGridView.getChildAt(mlastposition) != null){
                        viewitem = mGridView.getChildAt(mlastposition);
                        itemfocusimg = (ImageView) viewitem.findViewById(R.id.focusimg);
                        itemfocusimg.setVisibility(View.VISIBLE);
                        itemimg = (ImageView) viewitem.findViewById(R.id.imageitemview);
                        ViewGroup.LayoutParams lp;
                        lp = itemimg.getLayoutParams();
                        lp.height = (int) (btheight * 1.1);
                        lp.width = (int) (btwidth * 1.1);
                        itemimg.setLayoutParams(lp);
                    }
                }
            }
        });
        mGridView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                View viewitem;
                ImageView itemfocusimg;
                ImageView itemimg;
                final int btheight = getResources().getDimensionPixelSize(R.dimen.img_button_height);
                final int btwidth = getResources().getDimensionPixelSize(R.dimen.img_button_width);
                if (mGridView.getChildAt(mlastposition) != null) {
                    viewitem = mGridView.getChildAt(mlastposition);
                    itemfocusimg = (ImageView) viewitem.findViewById(R.id.focusimg);
                    itemfocusimg.setVisibility(View.INVISIBLE);
                    itemimg = (ImageView) viewitem.findViewById(R.id.imageitemview);
                    ViewGroup.LayoutParams lp;
                    lp = itemimg.getLayoutParams();
                    lp.height = btheight;
                    lp.width = btwidth;
                    itemimg.setLayoutParams(lp);
                }
                if (mGridView.getChildAt(position) != null) {
                    viewitem = mGridView.getChildAt(position);
                    itemfocusimg = (ImageView) viewitem.findViewById(R.id.focusimg);
                    itemfocusimg.setVisibility(View.VISIBLE);
                    itemimg = (ImageView) viewitem.findViewById(R.id.imageitemview);
                    ViewGroup.LayoutParams lp;
                    lp = itemimg.getLayoutParams();
                    lp.height = (int) (btheight * 1.1);
                    lp.width = (int) (btwidth * 1.1);
                    itemimg.setLayoutParams(lp);
                }
                mlastposition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                View viewitem;
                ImageView itemfocusimg;
                ImageView itemimg;
                final int btheight = getResources().getDimensionPixelSize(R.dimen.img_button_height);
                final int btwidth = getResources().getDimensionPixelSize(R.dimen.img_button_width);
                if (mGridView.getChildAt(mlastposition) != null) {
                    viewitem = mGridView.getChildAt(mlastposition);
                    itemfocusimg = (ImageView) viewitem.findViewById(R.id.focusimg);
                    itemfocusimg.setVisibility(View.INVISIBLE);
                    itemimg = (ImageView) viewitem.findViewById(R.id.imageitemview);
                    ViewGroup.LayoutParams lp;
                    lp = itemimg.getLayoutParams();
                    lp.height = btheight;
                    lp.width = btwidth;
                    itemimg.setLayoutParams(lp);
                }
            }
        });

    }

    private void initAdapter() {
        itemNames = mActivity.getResources().getStringArray(R.array.downloaded_item_name);
        itemIcon = new int[] {
                R.drawable.icon1_new, R.drawable.icon2_new, R.drawable.icon3_new, R.drawable.icon4_new, 
                R.drawable.icon5_new,R.drawable.icon6_new
        };
        mAdapter = new DownloadedAdapter();
    }

    class DownloadedAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemNames.length;
        }

        @Override
        public String getItem(int position) {
            return itemNames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.dloaded_gridviewitem, null);
            }
            TextView itemView = ViewHolder.get(convertView, R.id.itemtext);
            final ImageView focusimg = ViewHolder.get(convertView, R.id.focusimg);
            itemView.setText(getItem(position));
            Drawable icon = mActivity.getResources().getDrawable(itemIcon[position]);
            ImageView itemimg = ViewHolder.get(convertView, R.id.imageitemview);
            itemimg.setBackground(icon);
            
            return convertView;
        }
    }
}
