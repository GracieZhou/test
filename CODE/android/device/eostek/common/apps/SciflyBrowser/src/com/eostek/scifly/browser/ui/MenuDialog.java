
package com.eostek.scifly.browser.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BrowserContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.browser.AddBookmarkPage;
import com.android.browser.Bookmarks;
import com.android.browser.Controller;
import com.android.browser.DownloadTouchIcon;
import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.BrowserApplication;
import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.business.QRCodeHelper;
import com.eostek.scifly.browser.business.WebViewHelper;
import com.eostek.scifly.browser.settool.SetToolFragment;
import com.eostek.scifly.browser.util.Constants;

public class MenuDialog extends Dialog {

    private final String TAG = "MenuDialog";

    private BrowserActivity mActivity;

    private GridView mNineGridView;

    private LinearLayout mPagesLayout;

    private ImageView mAddView;

    private ImageView mAddImage;

    private ImageView mQRCodeImg;

    private RelativeLayout mAddRelativeLayout;

    private NineGridViewAdapter mAdapter;

    private MenuItemClickListener mListener;

    private MenuItemSelectListener mSelectListener;

    private SearchDialog mSearchDialog;

    private ArrayList<Item> mList;

    private LayoutInflater mInflater;

    private final int SEARCHE_POSITION = 0;

    private final int BACK_POSITION = 1;

    private final int FORWARD_POSITION = 2;

    private final int REFRESH_STOP_POSITION = 3;

    private final int HOME_POSITION = 4;

    private final int COLLECT_POSITION = 5;

    private final int DOWNLOAD_POSITION = 6;

    private final int MULTI_PAGE_POSITION = 7;

    private final int HISTORY_POSITION = 8;

    private final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.eostek.documentui";

    private final String DOWNLOAD_MANAGER_CLASS_NAME = "com.eostek.documentui.DocumentsActivity";

    boolean isNineGridViewFocus = false;

    boolean isPagesLayoutFocus = false;

    private WebViewHelper mHelper;

    // Message IDs
    private static final int SAVE_BOOKMARK = 100;

    private static final int SHOW_QR_CODE = 101;
    
    private static final int OFFSET_0XFF = 0xff;

    private static final int MD5_SUBSTRING_LENGTH = 16;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SAVE_BOOKMARK:
                    if (1 == msg.arg1) {
                        Toast.makeText(mActivity, R.string.bookmark_saved, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mActivity, R.string.bookmark_not_saved, Toast.LENGTH_LONG).show();
                    }
                    break;
                case SHOW_QR_CODE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    mQRCodeImg.setImageBitmap(bitmap);
                    break;
            }
        };
    };

    public MenuDialog(BrowserActivity activity) {
        super(activity, android.R.style.Theme_Translucent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivity = activity;
        initData();
        findView();
        setListener();
    }

    private void initData() {
        if (mList == null) {
            mList = new ArrayList<Item>();
            Resources resources = mActivity.getResources();
            Item item1 = new Item(R.drawable.search, resources.getString(R.string.search_txt));
            Item item2 = new Item(R.drawable.back_off, resources.getString(R.string.back_txt));
            Item item3 = new Item(R.drawable.forward_off, resources.getString(R.string.forward_txt));
            Item item4 = new Item(R.drawable.refresh, resources.getString(R.string.refresh_stop_txt));
            Item item5 = new Item(R.drawable.home, resources.getString(R.string.homepage_txt));
            Item item6 = new Item(R.drawable.store_off, resources.getString(R.string.collect_txt));
            Item item7 = new Item(R.drawable.download, resources.getString(R.string.download_txt));
            Item item8 = new Item(R.drawable.pages, resources.getString(R.string.multi_pages_txt));
            Item item9 = new Item(R.drawable.history, resources.getString(R.string.history_txt));
            mList.add(item1);
            mList.add(item2);
            mList.add(item3);
            mList.add(item4);
            mList.add(item5);
            mList.add(item6);
            mList.add(item7);
            mList.add(item8);
            mList.add(item9);
        }

        if (mAdapter == null) {
            mAdapter = new NineGridViewAdapter();
        }

        if (mListener == null) {
            mListener = new MenuItemClickListener();
        }

        if (mSelectListener == null) {
            mSelectListener = new MenuItemSelectListener();
        }

        if (mInflater == null) {
            mInflater = LayoutInflater.from(mActivity);
        }

        if (mHelper == null) {
            mHelper = WebViewHelper.getInstance((BrowserActivity)mActivity);
        }
        showQRcode();
    }

    private void findView() {
        Window window = getWindow();
        window.setContentView(R.layout.menu_layout);
        mNineGridView = (GridView) window.findViewById(R.id.menu_gridview);
        mNineGridView.setAdapter(mAdapter);
        mPagesLayout = (LinearLayout) window.findViewById(R.id.menu_pages);
        mAddView = (ImageView) window.findViewById(R.id.add_bg); //handle click event.
        mAddImage = (ImageView) window.findViewById(R.id.add_view); //handle bg image.
        mAddRelativeLayout = (RelativeLayout)window.findViewById(R.id.add_layout);
        mQRCodeImg = (ImageView) window.findViewById(R.id.QR);
    }
    private void setListener() {
        mNineGridView.setOnItemClickListener(mListener);
        mNineGridView.setOnItemSelectedListener(mSelectListener);

        mAddView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                MenuDialog.this.dismiss();
                mActivity.mLogic.showHome();
                mHelper.createWebView();
            }
        });
    }

    public void showDialog() {
        createScreenShot();
        if (mNineGridView != null) {
            mNineGridView.invalidate();
        }
        if (mPagesLayout != null) {
            refreshPagesLayout();
        }
        this.show();
    }

    class NineGridViewAdapter extends BaseAdapter {

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
            MenuItemViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new MenuItemViewHolder();
                convertView = mInflater.inflate(R.layout.menu_item, null);
                viewHolder.mImgView = (ImageView) convertView.findViewById(R.id.menu_item_image);
                viewHolder.mTxtView = (TextView) convertView.findViewById(R.id.menu_item_Text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MenuItemViewHolder) convertView.getTag();
            }
            viewHolder.mImgView.setImageResource(mList.get(position).id);
            viewHolder.mTxtView.setText(mList.get(position).text);

            //inti collect back forword item.
            if (!mActivity.mLogic.isShowHomeLayout() && mHelper != null && mHelper.getCurrentWebView() != null) {
                if (position == COLLECT_POSITION) {
                    if (Bookmarks.isBookmark(mActivity, mHelper.getCurrentWebView().getUrl())) {
                        viewHolder.mImgView.setImageResource(R.drawable.store);
                    } else {
                        viewHolder.mImgView.setImageResource(R.drawable.store_off);
                    }
                }
                if (position == BACK_POSITION) {
                    if ( mHelper.getCurrentWebView().canGoBack()) {
                        viewHolder.mImgView.setImageResource(R.drawable.back);
                    } else {
                        viewHolder.mImgView.setImageResource(R.drawable.back_off);
                    }
                }
                if (position == FORWARD_POSITION) {
                    if (mHelper.getCurrentWebView().canGoForward()) {
                        viewHolder.mImgView.setImageResource(R.drawable.forward);
                    } else {
                        viewHolder.mImgView.setImageResource(R.drawable.forward_off);
                    }
                }
            }
            return convertView;
        }

        class MenuItemViewHolder {
            public ImageView mImgView;

            public TextView mTxtView;
        }
    }

    class MenuItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            Log.d(TAG, "click position=" + position);
            switch (position) {
                case SEARCHE_POSITION:
                    if (mSearchDialog == null) {
                        mSearchDialog = new SearchDialog(mActivity);
                    }
                    mSearchDialog.show();
                    MenuDialog.this.dismiss();
                    break;

                case BACK_POSITION:
                    mHelper.goBack();
                    mAdapter.notifyDataSetChanged();
                    break;

                case FORWARD_POSITION:
                    mHelper.goForward();
                    mAdapter.notifyDataSetChanged();
                    break;

                case REFRESH_STOP_POSITION:
                    mHelper.stopOrRefresh();
                    break;

                case HOME_POSITION:
                    MenuDialog.this.dismiss();
                    mActivity.mLogic.showHome();
                    mActivity.mHolder.showMainTileView();
                    mActivity.mHolder.gotoHomeFragment();
                    break;

                case COLLECT_POSITION:
                    String url = mHelper.getCurrentWebView().getUrl();
                    Log.d(TAG, "collect url=" + url);
                    if (!TextUtils.isEmpty(url) && !Bookmarks.isBookmark(mActivity, url)) {
                        saveBookmark();
                        ImageView imgView = (ImageView) view.findViewById(R.id.menu_item_image);
                        imgView.setImageResource(R.drawable.store);
                    }
                    break;

                case DOWNLOAD_POSITION:
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName(DOWNLOAD_MANAGER_PACKAGE_NAME, DOWNLOAD_MANAGER_CLASS_NAME);
                    intent.setComponent(componentName);
                    try {
                        mActivity.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(mActivity, mActivity.getResources().getString(R.string.start_download_manager_error_tip), Toast.LENGTH_SHORT).show();
                        dismiss();
                        Log.d(TAG, "start download manager error.");
                    }
                    break;

                case MULTI_PAGE_POSITION:
                    if (mPagesLayout.getVisibility() == View.VISIBLE) {
                        mPagesLayout.setVisibility(View.GONE);
                    } else {
                        mPagesLayout.setVisibility(View.VISIBLE);
                    }
                    break;

                case HISTORY_POSITION:
                    mActivity.mLogic.showHome();
                    mActivity.mLogic.gotoState(Constants.POSITION_SETTOOL);
                    SetToolFragment fragment = mActivity.mHolder.getSetToolFragment();
                    fragment.mHandler.sendEmptyMessage(fragment.MSG_SHOW_HISTORY);
                    MenuDialog.this.dismiss();
                    break;

                default:
                    break;
            }
        }

    }

    class MenuItemSelectListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
            Log.d(TAG, "position=" + position);
            if (position == MULTI_PAGE_POSITION) {
                mPagesLayout.setVisibility(View.VISIBLE);
            } else {
                mPagesLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!isPagesLayoutFocus && mNineGridView.getSelectedItemPosition() == MULTI_PAGE_POSITION) {
                    mPagesLayout.requestFocus();
                    isPagesLayoutFocus = true;
                    isNineGridViewFocus = false;
                    return true;
                }
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!isNineGridViewFocus && mPagesLayout.isFocused()) {
                    mNineGridView.requestFocus();
                    isNineGridViewFocus = true;
                    isPagesLayoutFocus = false;
                    return true;
                }

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public View createPage() {
        if (mHelper.getWebViewsList().size() == WebViewHelper.WEBVIEW_LENGTH - 1) {
            mAddRelativeLayout.setVisibility(View.GONE);
        }
        View view = mInflater.inflate(R.layout.page_item_layout, null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.corner_layer);
        final Button closebtn = (Button) view.findViewById(R.id.close);
        imageView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    closebtn.setVisibility(View.VISIBLE);
                } else {
                    if (!closebtn.isFocused()) {
                        closebtn.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                View view = (View)imageView.getParent().getParent().getParent();
                for (int i = 0; i < mHelper.getWebViewsList().size(); i++) {
                    if (view == mHelper.getWebViewsList().get(i).mView) {
                        mHelper.showWebView(i);
                        if (mActivity.mLogic.isShowHomeLayout()) {
                            mActivity.mLogic.showWeb();
                        }
                        break;
                    }
                }
                refreshPagesLayout();
            }
        });
        closebtn.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setVisibility(View.INVISIBLE);
                }
            }
        });
        closebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View view = (View)closebtn.getParent().getParent();
//                mPagesLayout.removeView(view); //remove from pagesLayout(UI)
                if (mHelper.getWebViewsList().size() == WebViewHelper.WEBVIEW_LENGTH) {
                    mAddRelativeLayout.setVisibility(View.VISIBLE);
                }
                removePageData(view);
                refreshPagesLayout();
            }
        });
        Resources resources = mActivity.getResources();
        mPagesLayout.addView(view, 1); //index = 1, To sure the new webview is on first position.
        return view;
    }

    private void saveBookmark() {
        WebView webView = mHelper.getCurrentWebView();
        if (webView != null) {
            Bundle bundle = new Bundle();
            bundle.putString(BrowserContract.Bookmarks.TITLE, webView.getTitle());
            bundle.putString(BrowserContract.Bookmarks.URL, webView.getUrl());
            bundle.putParcelable(BrowserContract.Bookmarks.FAVICON, webView.getFavicon());
            bundle.putParcelable(BrowserContract.Bookmarks.THUMBNAIL, 
                    Controller.createScreenshot(webView, 
                            Controller.getDesiredThumbnailWidth(mActivity),
                            Controller.getDesiredThumbnailHeight(mActivity)));
//            bundle.putBoolean(REMOVE_THUMBNAIL, !urlUnmodified);
            String touchIconUrl = webView.getTouchIconUrl();
            if (touchIconUrl != null) {
                bundle.putString(AddBookmarkPage.TOUCH_ICON_URL, touchIconUrl);
            }
            Message msg = Message.obtain(mHandler, SAVE_BOOKMARK);
            msg.setData(bundle);
            // Start a new thread so as to not slow down the UI
            Thread t = new Thread(new SaveBookmarkRunnable(mActivity, msg));
            t.start();
        }
    }

    private class SaveBookmarkRunnable implements Runnable {
        private Message mMessage;
        private Context mContext;
        public SaveBookmarkRunnable(Context ctx, Message msg) {
            mContext = ctx.getApplicationContext();
            mMessage = msg;
        }
        public void run() {
            // Unbundle bookmark data.
            Bundle bundle = mMessage.getData();
            String title = bundle.getString(BrowserContract.Bookmarks.TITLE);
            String url = bundle.getString(BrowserContract.Bookmarks.URL);
//            boolean invalidateThumbnail = bundle.getBoolean(REMOVE_THUMBNAIL);
//            Bitmap thumbnail = invalidateThumbnail ? null
//                    : (Bitmap) bundle.getParcelable(BrowserContract.Bookmarks.THUMBNAIL);
            Bitmap thumbnail = (Bitmap) bundle.getParcelable(BrowserContract.Bookmarks.THUMBNAIL);
            String touchIconUrl = bundle.getString(AddBookmarkPage.TOUCH_ICON_URL);

            saveBitmap(thumbnail,calcMD5(url));
            // Save to the bookmarks DB.
            try {
                final ContentResolver cr = mActivity.getContentResolver();
                Bookmarks.addBookmark(mActivity, false, url,
                        title, thumbnail, 1);
                if (touchIconUrl != null) {
                    new DownloadTouchIcon(mContext, cr, url).execute(touchIconUrl);
                }
                mMessage.arg1 = 1;
            } catch (IllegalStateException e) {
                mMessage.arg1 = 0;
            }
            mMessage.sendToTarget();
        }
    }

    private void createScreenShot() {
        Log.d(TAG, "begin to createScreenShot");
        final Bitmap screenShot = Controller.createScreenshot(mHelper.getCurrentWebView(), Controller.getDesiredThumbnailWidth(mActivity),
                Controller.getDesiredThumbnailHeight(mActivity));
        Log.d(TAG, "begin to createScreenShot, screenShot=" + screenShot);
        for (int i = 0; i < mHelper.getWebViewsList().size(); i++) {
            Log.d(TAG, "createScreenShot = " + i);
            if (mHelper.getCurrentWebView() == mHelper.getWebViewsList().get(i).mWebView) {
                Log.d(TAG, "createScreenShot = " + i + " , is current webview");
                mHelper.getWebViewsList().get(i).mBitmap = screenShot;
                mHelper.getWebViewsList().get(i).mTime = "" + System.currentTimeMillis();
                break;
            }
        }
    }

    private void removePageData(View view) {
        for (int i = 0; i < mHelper.getWebViewsList().size(); i++) {
            if (view == mHelper.getWebViewsList().get(i).mView) {
                // close is current webview.
                WebView webView = mHelper.getWebViewsList().get(i).mWebView;
                if (mHelper.getCurrentWebView() == mHelper.getWebViewsList().get(i).mWebView) {
                    Log.d(TAG, "close current webview.");
                        mHelper.getWebViewsList().remove(i);
                        if (mHelper.getWebViewsList().size() <= 0) {
                            mHelper.createWebView();
                            mActivity.mLogic.showHome();
                        }
                        mHelper.showWebView(0);
                        mPagesLayout.removeView(view); //remove from pagesLayout(UI)
                    this.dismiss();
                } else {
                    // remove data from webviewlist;
                    // alway Leave one.
                    if (mHelper.getWebViewsList().size() > 1) {
                        mPagesLayout.removeView(view); //remove from pagesLayout(UI)
                        mHelper.getWebViewsList().remove(i);
                    }
                }
                webView.removeAllViews();
                webView.destroy();
                break;
            }
        }
    }

    public GridView getNineGridView() {
        return mNineGridView;
    }

    public LinearLayout getPagesLayout() {
        return mPagesLayout;
    }

    class Item {
        public int id;

        public String text;

        public Item(int id, String text) {
            super();
            this.id = id;
            this.text = text;
        }
    }


    public void saveBitmap(Bitmap thumbnail, String md5) {
    	  Log.e("ahri", "保存图片");
    	  File file = new File("/sdcard/Android/data/com.eostek.scifly.browser");
          if (!file.exists())
        	  Log.e("ahri", "/sdcard/Android/data/com.eostek.scifly.browser  !file.exists");
              file.mkdir();
          
          File file1 = new File("/sdcard/Android/data/com.eostek.scifly.browser/collect");
          if (!file1.exists())
        	  Log.e("ahri", "/sdcard/Android/data/com.eostek.scifly.browser/collect  !file.exists");
              file1.mkdir();
          
    	  String picName = md5 + ".png";
    	  File f = new File("/sdcard/Android/data/com.eostek.scifly.browser/collect", picName);
    	  if (f.exists()) {
    	   f.delete();
    	  }
    	  
    	  try {
    	   FileOutputStream out = new FileOutputStream(f);
    	   thumbnail.compress(Bitmap.CompressFormat.PNG, 50, out);
    	   out.flush();
    	   out.close();
    	   Log.i("ahri", "已经保存");
    	  } catch (FileNotFoundException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
    	  } catch (IOException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
    	  }

    	 }
    
    public static String calcMD5(String input) {
        return calcMD5(input.getBytes());
    }

    private static String calcMD5(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        // generate MessageDigest
        md.update(data);
        byte[] hash = md.digest();

        // translate to string
        StringBuffer sbRet = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            int v = hash[i] & OFFSET_0XFF;
            if (v < MD5_SUBSTRING_LENGTH) {
                sbRet.append("0");
            }
            sbRet.append(Integer.toString(v, MD5_SUBSTRING_LENGTH));
        }
        return sbRet.toString();
    }

    private void refreshPagesLayout() {
        Log.d(TAG, "begin to refreshPagesLayout");
        for (int i = 0; i < mHelper.getWebViewsList().size(); i++) {
            Log.d(TAG, "begin to refreshPagesLayout ----> " + i);
            LinearLayout layout = (LinearLayout) mHelper.getWebViewsList().get(i).mView;
            layout.setVisibility(View.VISIBLE);
            ImageView imageView = (ImageView)layout.findViewById(R.id.page);
            imageView.setImageBitmap(mHelper.getWebViewsList().get(i).mBitmap);
            
            ImageView currentIcon = (ImageView)layout.findViewById(R.id.current_icon);
            currentIcon.setVisibility(View.INVISIBLE);
            
            TextView textView = (TextView)layout.findViewById(R.id.page_item_text);
            Log.d(TAG, "begin to refreshPagesLayout, url=" + mHelper.getWebViewsList().get(i).mWebView.getTitle());
            if (!TextUtils.isEmpty(mHelper.getWebViewsList().get(i).mWebView.getTitle())) {
                textView.setText(mHelper.getWebViewsList().get(i).mWebView.getTitle());
            }
            if (mHelper.getCurrentWebView() == mHelper.getWebViewsList().get(i).mWebView) {
                Log.d(TAG, "current webview position = " + i);
                currentIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showQRcode() {
        final File file = new File(Constants.CACHE_PATH, Constants.QR_CODE_NAME);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            mHandler.obtainMessage(SHOW_QR_CODE, bitmap).sendToTarget();
        } else {
            BrowserApplication.getInstance().addThreadTask(new Runnable() {
                
                @Override
                public void run() {
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        mHandler.obtainMessage(SHOW_QR_CODE, bitmap).sendToTarget();
                    } else {
                        Bitmap bitmap = QRCodeHelper.getInstance(mActivity).getQRCodeBitmap();
                        if (bitmap != null) {
                            mHandler.obtainMessage(SHOW_QR_CODE, bitmap).sendToTarget();
                        }
                    }
                }
            });
        }
    }

    public void invalidate() {
        mAdapter.notifyDataSetChanged();
    }
}
