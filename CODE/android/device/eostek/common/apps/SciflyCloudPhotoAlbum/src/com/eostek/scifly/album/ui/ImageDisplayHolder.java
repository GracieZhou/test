
package com.eostek.scifly.album.ui;

import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.AlbumApplication;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.utils.FixedSpeedScroller;
import com.eostek.scifly.album.utils.ViewHolder;
import com.eostek.scifly.album.utils.effect.AccordionTransformer;
import com.eostek.scifly.album.utils.effect.CubeTransformer;
import com.eostek.scifly.album.utils.effect.DepthPageTransformer;
import com.eostek.scifly.album.utils.effect.InRightDownTransformer;
import com.eostek.scifly.album.utils.effect.InRightUpTransformer;
import com.eostek.scifly.album.utils.effect.RotateDownPageTransformer;
import com.eostek.scifly.album.utils.effect.ZoomOutPageTransformer;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageDisplayHolder {
    private ImageDisplayActivity mActivity;

    @ViewInject(R.id.image_pager)
    public ViewPager mImagePager;

    public List<String> mImageUrls;

    public int mCurrentPosition = 0;

    private boolean isAutoplay = false;

    private int mEffectIndex = 0;

    public SharedPreferences mPreferences;

    public ImageDisplayHolder(ImageDisplayActivity activity) {
        this.mActivity = activity;
        mPreferences = mActivity.getSharedPreferences(Constants.SHAREPREFRER_STRING, Context.MODE_PRIVATE);
    }

    public void initDatas() {
        Bundle bundle = mActivity.getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            mImageUrls = bundle.getStringArrayList(Constants.IMAGE_PATHS);
            mCurrentPosition = bundle.getInt(Constants.CURRENT_POSITION, 0);
            isAutoplay = bundle.getBoolean(Constants.AUTO_PLAY, false);
        }
    }

    public void initPager() {
        mImagePager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int pageIndex = position;
                if (position == 0) {
                    pageIndex = mImageUrls.size();
                } else if (position >= mImageUrls.size() + 1) {
                    pageIndex = 1;
                }
                if (position != pageIndex) {
                    mImagePager.setCurrentItem(pageIndex, false);
                }
                mCurrentPosition = pageIndex - 1;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        setPagerSwitchTime();
        mImagePager.setAdapter(new ImagePagerAdapter());
        mImagePager.setCurrentItem(mCurrentPosition + 1);
        mEffectIndex = mPreferences.getInt(Constants.EFFECT_KEY, 0);
        setupEffect(mEffectIndex);
        if (isAutoplay) {
            mActivity.slideShow();
        }
    }

    public void resetView() {
        mImagePager.removeAllViews();
        mImagePager.setAdapter(new ImagePagerAdapter());
        mImagePager.setCurrentItem(mCurrentPosition + 1);
        mEffectIndex = mPreferences.getInt(Constants.EFFECT_KEY, 0);
        setupEffect(mEffectIndex);
        if (isAutoplay) {
            mActivity.slideShow();
        }
    }

    /**
     * @Title: setPagerDuringTime.
     * @Description: control the speed of the ViewPager.
     * @param: .
     * @return: void.
     * @throws
     */
    private void setPagerSwitchTime() {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mImagePager.getContext(), new AccelerateInterpolator());
            field.set(mImagePager, scroller);
            scroller.setmDuration(500);
        } catch (Exception e) {
            Log.d("ImagePagerActivity", "catch exception:", e);
        }
    }

    public void setupEffect(int index) {
        PageTransformer transformer = null;
        switch (index) {
            case 0:
                break;
            case 1:
                transformer = new DepthPageTransformer();
                break;
            case 2:
                transformer = new ZoomOutPageTransformer();
                break;
            case 3:
                transformer = new RotateDownPageTransformer();
                break;
            case 4:
                transformer = new CubeTransformer();
                break;
            case 5:
                transformer = new AccordionTransformer();
                break;
            case 6:
                transformer = new InRightUpTransformer();
                break;
            case 7:
                transformer = new InRightDownTransformer();
                break;
            default:
                break;
        }
        mImagePager.setPageTransformer(true, transformer);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return mImageUrls.size() + 2;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {

            View pagerView = LayoutInflater.from(mActivity).inflate(R.layout.item_image_pager, view, false);
            ImageView imageView = ViewHolder.get(pagerView, R.id.photo);
            if ((mImageUrls != null) && (mImageUrls.size() > 0)) {

                if (position == 0) {
                    ImageLoader.getInstance().displayImage(mImageUrls.get(mImageUrls.size() - 1), imageView,
                            AlbumApplication.getInstance().getDisplayImageOptions());
                } else if (position == mImageUrls.size() + 1) {
                    if (mImageUrls.size() > 0) {
                        ImageLoader.getInstance().displayImage(mImageUrls.get(0), imageView,
                                AlbumApplication.getInstance().getDisplayImageOptions());
                    }
                } else {
                    ImageLoader.getInstance().displayImage(mImageUrls.get(position - 1), imageView,
                            AlbumApplication.getInstance().getDisplayImageOptions());
                }
            }
            ((ViewPager) view).addView(pagerView, 0);
            return pagerView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
