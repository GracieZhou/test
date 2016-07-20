
package com.heran.launcher2.advert;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class BootVideoView extends VideoView {

    public BootVideoView(Context context) {
        super(context);
    }

    public BootVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BootVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

}
