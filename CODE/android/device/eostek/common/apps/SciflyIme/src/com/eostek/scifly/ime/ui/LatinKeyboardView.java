
package com.eostek.scifly.ime.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodSubtype;

import com.eostek.scifly.ime.LatinKeyboard;
import com.eostek.scifly.ime.LatinKeyboard.KeyRow;
import com.eostek.scifly.ime.LatinKeyboard.LatinKey;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.util.Constans;

/**
 * Layout of keyboard.
 * 
 * @author Youpeng
 */
public class LatinKeyboardView extends KeyboardView {

    private static String TAG = "LatinKeyboardView";

    /**
     * keycode options.
     */
    public static final int KEYCODE_OPTIONS = -100;

    private final static int[] KEY_STATE_NORMAL = {};

    private final static int[] KEY_STATE_NORMAL_ON = {
            android.R.attr.state_checkable, android.R.attr.state_checked
    };

    private final static int[] KEY_STATE_PRESSED_ON = {
            android.R.attr.state_pressed, android.R.attr.state_checkable, android.R.attr.state_checked
    };

    private final static int[] KEY_STATE_NORMAL_OFF = {
        android.R.attr.state_checkable
    };

    private final static int[] KEY_STATE_PRESSED_OFF = {
            android.R.attr.state_pressed, android.R.attr.state_checkable
    };

    private final static int[] KEY_STATE_PRESSED = {
        android.R.attr.state_pressed
    };

    private LatinKeyboard mCurrentKeyboard;

    private int currentKeyIndex = 0;

    private List<Key> keys = new ArrayList<Key>();

    private List<KeyRow> mKeyRows = new ArrayList<KeyRow>();

    private List<Integer> keyWidths = new ArrayList<Integer>();

    private Key focusedKey;

    private Rect rect;

    private boolean inCandidatesRang = false;

    private Drawable keyBackground = null;

    private int mLabelTextSize;

    private int mKeyTextSize;

    private int mKeyTextSizeSmall;

    private int mKeyTextColor;

    private Paint mPaint;

    private int mShadowColor;

    private float mTouchX = -1;

    /**
     * Constructor.
     * 
     * @param context Context.
     * @param attrs attributes.
     */
    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = context.getResources();
        keyBackground = res.getDrawable(R.drawable.key);

        mLabelTextSize = res.getDimensionPixelSize(R.dimen.inputview_key_textsize);
        mKeyTextSize = res.getDimensionPixelSize(R.dimen.inputview_key_textsize);
        mKeyTextSizeSmall = res.getDimensionPixelSize(R.dimen.inputview_key_textsize_small);

        int keyTextSize = 0;
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(keyTextSize);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setAlpha(255);
        mKeyTextColor = res.getColor(R.color.input_keytext);
        mShadowColor = res.getColor(R.color.input_shadow);

    }

    /**
     * Constructor.
     * 
     * @param context context.
     * @param attrs attributes.
     * @param defStyle default style.
     */
    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {

        keys = mCurrentKeyboard.getKeys();

        int keyIndex = currentKeyIndex;
        Paint paint = mPaint;
        mPaint.setColor(mKeyTextColor);

        if (!mCurrentKeyboard.isResized()) {
            resizeKeys();
        }

        drawKbd(canvas, paint);

        if (keyIndex < 0 || keyIndex > keys.size() - 1) {
            return;
        }

        setKeyPressed(keyIndex, false);

    }

    /**
     * change status if key pressed.
     * 
     * @param index index of key.
     * @param pressed is pressed.
     */
    public void setKeyPressed(int index, boolean pressed) {
        try {
            keys = mCurrentKeyboard.getKeys();
            if (index >= keys.size()) {
                return;
            }

            focusedKey = keys.get(index);
            focusedKey.pressed = pressed;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    private void resizeKeys() {

        int keyWidthOffset = Constans.KEY_WIDTH_OFFSET;
        int keyHeightOffset = Constans.KEY_HEIGHT_OFFSET;

        // int keyWidthOffset = -2;
        // int keyHeightOffset = -5;

        Key key;

        int savedWidth = 0;
        float scale = 0.8f;

        if (mKeyRows == null || mKeyRows.size() == 0) {
            return;
        }

        int[] keyX = new int[mKeyRows.get(0).mSoftKeys.size()];
        float unitWidth = -1;
        float unitHeight = -1;

        // specify the first row's keys' x coordinate
        for (int n = 0; n < keyX.length; n++) {
            key = keys.get(n);

            if (n == 0) {
                key.width = (int) (key.width * scale) + keyWidthOffset;
                key.height += keyHeightOffset;
                keyX[n] += key.x;

                unitHeight = key.height;

                if (unitWidth < 0 && ((LatinKey) key).mKeySize == 1) {
                    unitWidth = key.width;
                }

                continue;
            }

            savedWidth += keyWidths.get(n - 1) * (1 - scale);

            key.x -= savedWidth;
            keyX[n] = key.x;
            key.width = (int) (key.width * scale) + keyWidthOffset;
            key.height += keyHeightOffset;

            if (unitWidth < 0 && ((LatinKey) key).mKeySize == 1) {
                unitWidth = key.width;
            }

        }

        // //specify the other row's keys' x coordinate by first row
        if (mKeyRows.size() == 1) {
            return;
        }

        int index = 0;
        LatinKey lKey;
        for (int n = mKeyRows.get(1).mStartIndex; n < keys.size(); n++) {
            lKey = (LatinKey) keys.get(n);

            if (isNewRow(n, mKeyRows)) {
                index = 0;
            }

            if (index >= keyX.length) {
                break;
            }

            lKey.x = keyX[index];

            if (lKey.mKeySize == 1 && unitWidth > 0) {
                lKey.width = (int) unitWidth;
            } else if (lKey.mKeySize > 1 && unitWidth > 0) {
                int witdh = (int) (keyX[index + lKey.mKeySize - 1] - lKey.x + unitWidth);
                lKey.width = witdh;
            } else {
                lKey.width = (int) (lKey.width * scale) + keyWidthOffset;
            }

            if (unitHeight > 0) {
                lKey.height = (int) unitHeight;
            } else {
                lKey.height += keyHeightOffset;
            }

            index += lKey.mKeySize;
        }
        mCurrentKeyboard.setResized(true);
    }

    private void drawKbd(Canvas canvas, Paint paint) {
        Constans.print(TAG, "drawKbd " + keys.get(getLastKeyIndex()).pressed);

        Key key;
        int logoIndex = ((LatinKeyboard) getLatinKeyboard()).mLogoKeyIndex;
        boolean isLogoOn = ((LatinKeyboard) getLatinKeyboard()).isLogoOn();
        for (int n = 0; n < keys.size(); n++) {
            key = keys.get(n);

            String label = key.label == null ? null : adjustCase(key.label).toString();

            int[] drawableState = key.getCurrentDrawableState();

            /** The logo state is decided by the state of ISynergy connection. */
            if (n == logoIndex) {
                drawableState = getLogoState(key, isLogoOn);
            }

            keyBackground.setState(drawableState);
            final Rect bounds = keyBackground.getBounds();

            if (key.width != bounds.right || key.height != bounds.bottom) {
                keyBackground.setBounds(0, 0, key.width, key.height);
            }

            canvas.translate(key.x + getPaddingLeft(), key.y + getPaddingTop());
            
            keyBackground.draw(canvas);

            if (label != null) {
                // For characters, use large font. For labels like "Done", use
                // small font.
                paint.setFakeBoldText(true);
                if (label.length() > 1 && key.codes.length < 2) {
                    paint.setTextSize(mLabelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    paint.setTextSize(mKeyTextSize);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.setShadowLayer(1.0f, 0, 0, mShadowColor);

                if (((LatinKey) key).textSizeInPx > LatinKey.TEXT_SIZE_NOT_SETED) {

                    paint.setTextSize(((LatinKey) key).textSizeInPx);

                    paint.setTypeface(Typeface.DEFAULT);

                } else if (((LatinKey) key).isSmallText) {
                    // paint.setFakeBoldText(false);
                    paint.setTextSize(mKeyTextSizeSmall);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                canvas.drawText(
                        label,
                        (key.width )/2,
                     (key.height-getPaddingBottom())/2
                                + getPaddingTop()+getPaddingTop()/2+getPaddingTop()/4, paint);
               
                
                // Turn off drop shadow
                paint.setShadowLayer(0, 0, 0, 0);
                paint.setFakeBoldText(false);
            } else if (key.icon != null) {
                final int drawableX = (key.width - key.icon.getIntrinsicWidth()) / 2;
                final int drawableY = (key.height - key.icon
                        .getIntrinsicHeight()) / 2 ;

                int[] iconDrawableState = key.getCurrentDrawableState();
                key.icon.setState(iconDrawableState);

                canvas.translate(drawableX, drawableY);

                key.icon.setBounds(0, 0, key.icon.getIntrinsicWidth(), key.icon.getIntrinsicHeight());

                key.icon.draw(canvas);

                canvas.translate(-drawableX, -drawableY);
            }

            canvas.translate(-key.x - getPaddingLeft(), -key.y - getPaddingTop());
        }
    }

    private int[] getLogoState(Key key, boolean isLogoOn) {
        int[] drawableState = KEY_STATE_NORMAL;

        if (isLogoOn) {
            if (key.pressed) {
                drawableState = KEY_STATE_PRESSED_ON;
            } else {
                drawableState = KEY_STATE_NORMAL_ON;
            }
        } else {
            if (key.sticky) {
                if (key.pressed) {
                    drawableState = KEY_STATE_PRESSED_OFF;
                } else {
                    drawableState = KEY_STATE_NORMAL_OFF;
                }
            } else {
                if (key.pressed) {
                    drawableState = KEY_STATE_PRESSED;
                }
            }
        }
        return drawableState;
    }

    private boolean isNewRow(int i, List<KeyRow> keyRows) {

        for (KeyRow keyRow : keyRows) {
            if (i == keyRow.mStartIndex) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setKeyboard(Keyboard keyboard) {
        // android.os.Debug.waitForDebugger();
        if (keyboard != null) {
            mCurrentKeyboard = (LatinKeyboard) keyboard;
            mCurrentKeyboard.updateStickyKeys();
            keys = mCurrentKeyboard.getKeys();

            this.setCurrentKeyIndex(-1);

            for (Key key : keys) {
                keyWidths.add(key.width);
            }
            mKeyRows = mCurrentKeyboard.getKeyRows();

        }

        super.setKeyboard(keyboard);
    }

    /**
     * get current keyboard.
     * 
     * @return current keyboard.
     */
    public LatinKeyboard getLatinKeyboard() {
        return mCurrentKeyboard;
    }

    /**
     * refresh keyboard.
     * 
     * @param keyboard keyboard need to update.
     */
    public void updateKeyBoard(Keyboard keyboard) {
        super.setKeyboard(keyboard);

        keys = mCurrentKeyboard.getKeys();
        for (Key key : keys) {
            keyWidths.add(key.width);
        }

    }

    /**
     * set sub-type.
     * 
     * @param subtype type.
     */
    public void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        keyboard.setSpacePro(getResources());
        invalidateAllKeys();
    }

    /**
     * provide lastKeyIndex access.
     * 
     * @return index.
     */
    public int getLastKeyIndex() {
        return currentKeyIndex;
    }

    /**
     * set key index.
     * 
     * @param index current index.
     */
    public void setCurrentKeyIndex(int index) {
        this.currentKeyIndex = index;
    }

    /**
     * set candidate rang.
     * 
     * @param candidatesRang candidates.
     */
    public void setinCandidatesRang(boolean candidatesRang) {
        this.inCandidatesRang = candidatesRang;
    }

    private CharSequence adjustCase(CharSequence label) {
        if (mCurrentKeyboard.isShifted() && label != null && label.length() < 3
                && Character.isLowerCase(label.charAt(0))) {
            label = label.toString().toUpperCase(Locale.getDefault());
        }
        return label;
    }

    /**
     * get position.
     * 
     * @return x position.
     */
    public float getTouchX() {
        return mTouchX;
    }

    /**
     * set position.
     * 
     * @param touchX where touched.
     */
    public void setTouchX(float touchX) {
        this.mTouchX = touchX;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {

        setTouchX(me.getX());

        return super.onTouchEvent(me);
    }

    /**
     * get index by id.
     * 
     * @return index.
     */
    public int getSpaceIndexByX() {

        Key key;

        for (int i = 0; i < mCurrentKeyboard.mSpaceKeyIndex.size(); i++) {
            key = keys.get(mCurrentKeyboard.mSpaceKeyIndex.get(i));
            if (mTouchX > key.x && mTouchX < (key.x + key.width + getPaddingLeft())) {
                Constans.print(TAG, "index of space = " + mCurrentKeyboard.mSpaceKeyIndex.get(i));
                return mCurrentKeyboard.mSpaceKeyIndex.get(i);
            }
        }
        return -1;
    }

    public void setItialed(boolean isItialed) {
        this.mCurrentKeyboard.setResized(isItialed);
    }

}
