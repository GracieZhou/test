
package com.eostek.scifly.browser.collect;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.browser.BookmarkUtils;
import com.android.browser.provider.BrowserProvider2;
import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.business.WebViewHelper;
import com.eostek.scifly.browser.modle.UrlModle;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;

public class CollectFragment extends Fragment {

    private final String TAG = "CollectFragment";
    BrowserActivity mActivity;

    private TwoWayGridView mCollectGridView;

    private CollectAdapter mCollectAdapter;
    
    private TextView mNoCollectTv;

    public static boolean isCancelImgVisible;

    private static final String path = "/sdcard/Android/data/com.eostek.scifly.browser/collect";

    private ArrayList<CollectItemBean> mList;

    private List<String> mFileNameList;

    private List<String> mMD5List;
    
    public static List<UrlModle> mQueryList;

    private File ImgFiles[];

    private String[] mDefaultbmStr;

    public CollectFragment() {
    }

    @SuppressLint("ValidFragment")
    public CollectFragment(BrowserActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView...");
        View view = inflater.inflate(R.layout.collect_layout, container, false);
        initView(view);
        setListener();

        return view;
    }

    private void initView(View view) {
        mNoCollectTv = (TextView)view.findViewById(R.id.no_collect_tv);
        mDefaultbmStr = mActivity.getResources().getStringArray(R.array.default_bookmarks_title);
        initImgList();
        mCollectGridView = (TwoWayGridView) view.findViewById(R.id.collect_gridview);
        mCollectGridView.setSmoothScrollbarEnabled(true);
        mCollectAdapter = new CollectAdapter(mList, mActivity);
        mCollectGridView.setAdapter(mCollectAdapter);
        mCollectGridView.setSelector(R.drawable.transparent_selector);
    }

    private void setListener() {
        mCollectGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
                if (CollectItemBean.isDeleteMode) {
                    mList.remove(position);
                    ImgFiles[position].delete();
                    initImgList();
                    clearDatabase(position);
                    mCollectAdapter = new CollectAdapter(mList, mActivity);
                    mCollectGridView.setAdapter(mCollectAdapter);
                    mCollectGridView.setSelection(position);
                } else {
                    mActivity.mLogic.showCollectWeb();
                    
                    WebViewHelper.getInstance(mActivity).loadUrlFromContext(
                            mQueryList.get(position).mUrl);
                }
            }

        });
    }

    private void initImgList() {
        getAllFiles(new File(path));
        mList = new ArrayList<CollectItemBean>();
        queryBookmarks(mActivity);

        for (int i = 0; i < mFileNameList.size(); i++) {
            mList.add(new CollectItemBean(mFileNameList.get(i), mMD5List.get(i), mQueryList.get(i).mUrl));
            Log.e("ahri", "mFileNameList.get(i) =" + mFileNameList.get(i));
            Log.e("ahri", "mMD5List.get(i) =" + mMD5List.get(i));
            Log.e("ahri", "queryBookmarks(mActivity).get(i).mUrl) =" + mQueryList.get(i).mUrl);
        }
    }

    public void showDelCollectItem() {
        for (Iterator<CollectItemBean> iterator = mList.iterator(); iterator.hasNext();) {
            CollectItemBean itemBean = (CollectItemBean) iterator.next();
            itemBean.setDeleteMode(true);
        }
        mCollectAdapter.notifyDataSetChanged();
    }

    public void hideDelCollectItem() {
        for (Iterator<CollectItemBean> iterator = mList.iterator(); iterator.hasNext();) {
            CollectItemBean itemBean = (CollectItemBean) iterator.next();
            itemBean.setDeleteMode(false);
        }
        mCollectAdapter.notifyDataSetChanged();
    }

    private void getAllFiles(File root) {
    	Log.e("ahri", "getAllFiles");
        ImgFiles = root.listFiles();
        mFileNameList = new ArrayList<String>();
        mMD5List = new ArrayList<String>();
        if (ImgFiles == null || ImgFiles.length == 0) {
            Log.e("ahri", "files == null");
            mNoCollectTv.setVisibility(View.VISIBLE);
        } else {
        	mNoCollectTv.setVisibility(View.INVISIBLE);
        	Log.e("ahri", "files != null");
            for (int i = 0; i < ImgFiles.length; i++) {
                mFileNameList.add("file://" + path + "/" + ImgFiles[i].getName());
                mMD5List.add(getFileNameNoEx(ImgFiles[i].getName()));
                Log.e("ahri", "md5 =" + mMD5List.get(i));
            }
        }
    }

    // 查询数据库中每个收藏元素的title、url
    public void queryBookmarks(Context context) {
        Uri uri = BookmarkUtils.getBookmarksUri(context);
        Cursor cursor = context.getContentResolver().query(uri, new String[] {
                "title", "url"
        }, null, null, null);
       mQueryList = new ArrayList<UrlModle>();
        while (cursor.moveToNext()) {
            boolean add = true;
            String titile = cursor.getString(cursor.getColumnIndex("title"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            UrlModle urlModle = new UrlModle();
            urlModle.mTitle = titile;
            urlModle.mUrl = url;
            for (int i = 0; i < mDefaultbmStr.length; i++) {
                if (urlModle.mTitle.equals(mDefaultbmStr[i])) {
                    add = false;
                    break;
                }
            }

            if (add) {
                mQueryList.add(urlModle);
            }
        }
    }

    private void clearDatabase(int position) {
        String title =mQueryList.get(position).mTitle;
        Log.d("bookmark", "---------"+title);
      int result=  BrowserProvider2.deleteId(title);
      if(result !=0){
              Toast.makeText(mActivity, R.string.removed_from_bookmarks,
                      Toast.LENGTH_LONG).show();
      }
        Log.d("bookmark", "---------result"+result);
    }

    // 获取url的md5值
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public void refreshCollectUI() {
        try {
            initImgList();
            mActivity.runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    mCollectAdapter.setList(mList);
                }
            });
        } catch (Exception e) {
            // NullPointerException IndexOutOfBoundsException
            e.printStackTrace();
        }
    }
}
