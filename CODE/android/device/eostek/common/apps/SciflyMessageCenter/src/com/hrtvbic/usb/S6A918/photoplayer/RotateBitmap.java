/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * 
 */

package com.hrtvbic.usb.S6A918.photoplayer;

/**
 * 主要用于旋转位图.
 */
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class RotateBitmap {
    public static final String TAG = "RotateBitmap";

    // 旋转的位�?
    private Bitmap mBitmap;

    // 旋转的角�?
    private int mRotation;

    /**
     * 构�?器，初始化旋转角度为0.
     * 
     * @param bitmap
     */
    public RotateBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mRotation = 0;
    }

    /**
     * 构�?器，指定初始化旋转角�?
     * 
     * @param bitmap
     * @param rotation
     */
    public RotateBitmap(Bitmap bitmap, int rotation) {
        mBitmap = bitmap;
        mRotation = rotation % 360;
    }

    public void setRotation(int rotation) {
        mRotation = rotation;
    }

    public int getRotation() {
        return mRotation;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    /**
     * 根据设置的位图和旋转角度，获取旋转矩�?
     * 
     * @return Matrix 用于翻转位图的矩�?
     */
    public Matrix getRotateMatrix() {
        // By default this is an identity matrix.
        Matrix matrix = new Matrix();
        if (mRotation != 0) {
            // We want to do the rotation at origin, but since the bounding
            // rectangle will be changed after rotation, so the delta values
            // are based on old & new width/height respectively.
            int cx = mBitmap.getWidth() / 2;
            int cy = mBitmap.getHeight() / 2;
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(mRotation);
            matrix.postTranslate(getWidth() / (float) 2, getHeight() / (float) 2);
        }
        return matrix;
    }

    public boolean isOrientationChanged() {
        return (mRotation / 90) % 2 != 0;
    }

    public int getHeight() {
        if (isOrientationChanged()) {
            return mBitmap.getWidth();
        } else {
            return mBitmap.getHeight();
        }
    }

    public int getWidth() {
        if (isOrientationChanged()) {
            return mBitmap.getHeight();
        } else {
            return mBitmap.getWidth();
        }
    }

    /**
     * 回收位图.
     */
    public void recycle() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
