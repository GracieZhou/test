
package com.eostek.tv.player.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class RunningTextView extends TextView {

    public RunningTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public RunningTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RunningTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
    }

}
