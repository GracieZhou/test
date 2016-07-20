
package com.eostek.scifly.messagecenter.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * the View for show message horizotal.
 */
public class MessageHorizotalScrollView extends HorizontalScrollView {

    /**
     * Constructor.
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MessageHorizotalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Constructor.
     * 
     * @param context
     * @param attrs
     */
    public MessageHorizotalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor.
     * 
     * @param context
     */
    public MessageHorizotalScrollView(Context context) {
        super(context);
    }

    @Override
    public void fling(int velocityY) {
        // TODO Auto-generated method stub
        super.fling(velocityY / 4);
    }

}
