package com.eostek.scifly.ime.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.ui.CandidatesContainer.PageArrowClickListener;
import com.eostek.scifly.ime.util.Constans;

/**
 * View to show candidates.
 */
public class CandidateView extends View {

    private static final int OUT_OF_BOUNDS = -1;

    private PageArrowClickListener mPageArrowClickListener;

    private List<String> mSuggestionsAll = new ArrayList<String>();

    private List<String> mSuggestions2show = new ArrayList<String>();

    List<String> mLeft2NextPage = new ArrayList<String>();

    private int mSelectedIndex;

    private int mTouchX = OUT_OF_BOUNDS;

    private Drawable mSelectionHighlight;

    private Rect mBgPadding;

    private Rect mTextBg;

    private Rect mTextHighlightBg;

    private int suggestionsPerPage;

    private int mCurrentPage = 1;

    private int mMaxPage = 0;

    private int[] mWordWidth;

    private int[] mWordX;

    /**
     * max page of candidates.
     */
    public static final int MAX_CANDIDATE_COUNT = 6;

    private static final String TAG = "CandidateView";

    private int mColorNormal;

    private int mColorSuggestion;

    private int mVerticalPadding;

    private Paint mPaint;

    private int mTotalWidth;

    private Context mContext;

    private Rect candidateRect[] = new Rect[MAX_CANDIDATE_COUNT];

    private GestureDetector mGestureDetector = null;

    private int mTextPadding;

    /**
     * Listens to candidate-view actions.
     */
    public interface CandidateViewListener {
        /**
         * on which candidate picked.
         * 
         * @param candidate picked candidate.
         */
        void onPickCandidate(String candidate);
    }

    /**
     * Constructor.
     * 
     * @param context context.
     * @param attrs attributes.
     */
    public CandidateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        suggestionsPerPage = Constans.SUGGESTIONS_PER_PAGE;

        mWordWidth = new int[suggestionsPerPage];

        mWordX = new int[suggestionsPerPage];

        setLongClickable(true);

        mGestureDetector = new GestureDetector(context, new CandidateGestureListener());

        mSelectionHighlight = context.getResources().getDrawable(R.drawable.key_new_focoused);
        mSelectionHighlight.setState(new int[] {
                android.R.attr.state_enabled, android.R.attr.state_focused, android.R.attr.state_window_focused,
                android.R.attr.state_pressed
        });

        Resources r = context.getResources();

        setBackgroundColor(r.getColor(R.color.candidate_suggestion));
      

        mColorNormal = r.getColor(R.color.candidate_normal);
        mColorSuggestion = r.getColor(R.color.candidate_suggestion);
        mVerticalPadding = r.getDimensionPixelSize(R.dimen.candidate_vertical_padding);
        mTextPadding = r.getDimensionPixelSize(R.dimen.candidateview_text_padding);
        mPaint = new Paint();
        mPaint.setColor(mColorNormal);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(r.getDimensionPixelSize(R.dimen.candidate_font_height));
    }

    /**
     * get suggestions.
     * 
     * @return list of sugestions.
     */
    public List<String> getSuggestions() {
        return mSuggestions2show;
    }

    /**
     * Construct a CandidateView for showing suggested words for completion.
     * 
     * @param context context.
     */
    public CandidateView(Context context) {
        super(context);
    }

    /**
     * A connection back to the service to communicate with the text field.
     * 
     * @param listener input-method service.
     */
    public void setService(AbstractIME listener) {
        mPageArrowClickListener = listener;
    }

    @Override
    public int computeHorizontalScrollRange() {
        return mTotalWidth;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = resolveSize(50, widthMeasureSpec);
        // Get the desired height of the icon menu view (last row of items does
        // not have a divider below)
        Rect padding = new Rect();
        mSelectionHighlight.getPadding(padding);
        final int desiredHeight = ((int) mPaint.getTextSize()) + mVerticalPadding + padding.top + padding.bottom;
        // Maximum possible width and desired height
        setMeasuredDimension(measuredWidth, resolveSize(desiredHeight, heightMeasureSpec));
    }

    /**
     * If the canvas is null, then only touch calculations are performed to pick
     * the target candidate.
     * 
     * @param canvas canvas to draw.
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCandidates(canvas);
    }

    private void drawHighlight(Canvas canvas, Rect rect) {
        if (highlightIndex >= 0) {
            mSelectionHighlight.setBounds(rect);
            mSelectionHighlight.draw(canvas);
        }
    }

    private void drawCandidates(Canvas canvas) {
        if (canvas == null) {
            return;
        }

        mTotalWidth = 0;
        if (mSuggestions2show == null) {
            return;
        }

        if (mBgPadding == null) {
            mBgPadding = new Rect(0, 0, 0, 0);
            if (getBackground() != null) {
                getBackground().getPadding(mBgPadding);
            }
        }

        if (mTextBg == null) {
            mTextBg = new Rect(0, 0, 0, 0);
            mTextHighlightBg = new Rect(0, 0, 0, 0);
        }

        int x = 0;
        // final int count = mSuggestions.size();
        final int count = suggestionsPerPage;
        final int height = getHeight();
        final Rect bgPadding = mBgPadding;
        // final Paint mPaint = mPaint;
        final int y = (int) (((height - mPaint.getTextSize()) / 4) - mPaint.ascent());

        for (int i = 0; i < count && i < mSuggestions2show.size(); i++) {
            String suggestion = mSuggestions2show.get(i);
            float textWidth = mPaint.measureText(suggestion);

            int wordWidth = (int) mPaint.measureText(suggestion) + mTextPadding;
            int offset = 3;

            mWordX[i] = x;
            mWordWidth[i] = wordWidth;
            mPaint.setColor(mColorNormal);

//            mPaint.setFakeBoldText(true);
           mPaint.setColor(mColorSuggestion);
            mTextBg.set(x + 1, bgPadding.top, x + wordWidth, height);
            mTextHighlightBg.set(x - offset, bgPadding.top - offset, x + wordWidth + offset, height + offset);
            canvas.drawRect(mTextBg, mPaint);

            if (i == highlightIndex) {
                drawHighlight(canvas, mTextHighlightBg);
            }

            mPaint.setColor(mColorNormal);
            canvas.drawText(suggestion, x + (wordWidth - textWidth) / 2, y, mPaint);
//            mPaint.setFakeBoldText(false);
            x += wordWidth;
        }
    }

    /**
     * set suggestions in case of showing.
     * 
     * @param suggestions list of suggestions.
     * @param completions if completed.
     * @param typedWordValid is word valid.
     */
    @SuppressLint("WrongCall")
    public void setSuggestions(List<String> suggestions, boolean completions, boolean typedWordValid) {
        clear();
        if (suggestions != null) {
            mSuggestionsAll = new ArrayList<String>(suggestions);

            Constans.print(TAG, mSuggestionsAll + ",");

            showNextPage(-1);

            mMaxPage = (int) Math.ceil(mSuggestionsAll.size() / (suggestionsPerPage * 1.0));
            Constans.print(TAG, " max suggestion page = " + mMaxPage);
        }
    
        onDraw(null);  
       this. invalidate();
       this. requestLayout();
    }

    /**
     * reset.
     */
    public void clear() {
        mSuggestions2show = new ArrayList<String>();
        mLeft2NextPage = new ArrayList<String>();
        mTouchX = OUT_OF_BOUNDS;
        mSelectedIndex = -1;
        mCurrentPage = 1;
        pageStartIndex = 0;
        currentPageCandCount = 0;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {

        int action = me.getAction();
        int x = (int) me.getX();
        int y = (int) me.getY();
        mTouchX = x;

        highlightIndex = getIndexByX(mTouchX);

        Constans.print(TAG, "highlightIndex = " + highlightIndex);

        if (mGestureDetector.onTouchEvent(me)) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
                Constans.print(TAG, "ACTION_UP " + (mPageArrowClickListener == null) + " highlightIndex = " + highlightIndex);
                if (highlightIndex >= 0 && mPageArrowClickListener != null) {
                    mPageArrowClickListener.pickSuggestionManually(highlightIndex);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private int getIndexByX(int x) {
        int i;

        for (i = 0; i < mWordX.length - 1 && i < mSuggestions2show.size() - 1; i++) {

            if (x >= mWordX[i] && x < mWordX[i + 1]) {
                return i;
            }
        }

        if (x >= mWordX[i] && x < mWordX[i] + mWordWidth[i]) {
            return i;
        }

        return -1;
    }

    /**
     * For flick through from keyboard, call this method with the x coordinate
     * of the flick gesture.
     * 
     * @param x position.
     */
    @SuppressLint("WrongCall")
    public void takeSuggestionAt(float x) {
        mTouchX = (int) x;
        // To detect candidate
        onDraw(null);
        if (mSelectedIndex >= 0) {
            mPageArrowClickListener.pickSuggestionManually(mSelectedIndex);
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final int top = 0;
        final int bottom = h;
        final int candidateWidth = w / MAX_CANDIDATE_COUNT;

        // Set the first candidate 1-pixel wider since it'd accommodate two
        // candidate-separators.
        candidateRect[0] = new Rect(0, top, candidateWidth + 1, bottom);
        for (int i = 1, x = candidateRect[0].right; i < MAX_CANDIDATE_COUNT; i++) {
            candidateRect[i] = new Rect(x, top, x += candidateWidth, bottom);
        }
    }

    private void removeHighlight() {
        invalidate();
    }

    /**
     * Not used yet.
     * 
     * @return false.
     */
    public boolean pickHighlighted() {
        return false;
    }

    private int highlightIndex = -1;

    /**
     * set highlight index.
     * 
     * @param index which one to highlight.
     */
    public void setHighlighIndex(int index) {
        highlightIndex = index;
        // drawHighlight(canvas);
        invalidate();
    }

    /**
     * get highlight index.
     * 
     * @return highlight index.
     */
    public int getHighlightIndex() {
        return highlightIndex;
    }

    /**
     * reset.
     */
    public void highlightDefault() {
        if (mSuggestions2show.size() > 0) {
            highlightIndex = -1;
            invalidate();
        }
    }

    /**
     * get length of candidates.
     * 
     * @return size of candidates.
     */
    public int getCandidatesLength() {
        return mSuggestions2show.size();
    }

    /**
     * show next page.
     * 
     * @param page which page to show.
     */
    int pageStartIndex = 0;

    int currentPageCandCount = 0;

    public void showNextPage(int page) {
        mSuggestions2show.clear();

        pageStartIndex = pageStartIndex + currentPageCandCount;
        currentPageCandCount = 0;
        int x = 0;
        for (int i = pageStartIndex; i < mSuggestionsAll.size(); i++) {
            String suggestion = mSuggestionsAll.get(i);
            final int maxWidth = getWidth();
            int wordWidth = (int) mPaint.measureText(suggestion) + mTextPadding;
            if (x >= maxWidth || x + wordWidth >= maxWidth) {
                break;
            }
            mSuggestions2show.add(suggestion);
            currentPageCandCount++;
            x += wordWidth;
        }
        Constans.print(TAG, "startIndex " + pageStartIndex);
        Constans.print(TAG, "currentPageCandCount " + currentPageCandCount);
        invalidate();
    }

    public void showPrePage(int page) {
        mSuggestions2show.clear();

        currentPageCandCount = 0;
        Constans.print(TAG, "startIndex = " + pageStartIndex);

        int x = 0;
        for (int i = pageStartIndex-1 ; i >= 0; i--) {
            String suggestion = mSuggestionsAll.get(i);

            final int maxWidth = getWidth();
            int wordWidth = (int) mPaint.measureText(suggestion) + mTextPadding;

            if (x > maxWidth || x + wordWidth > maxWidth) {
                break;
            }
            currentPageCandCount++;
            mSuggestions2show.add(0, suggestion);
            x += wordWidth;
        }
        pageStartIndex = pageStartIndex - currentPageCandCount;
        Constans.print(TAG, "startIndex " + pageStartIndex);
        Constans.print(TAG, "currentPageCandCount " + currentPageCandCount);

        invalidate();
    }

    /**
     * get current page.
     * 
     * @return current page.
     */
    public int getCurrentPage() {
        return mCurrentPage;
    }

    /**
     * set current page.
     * 
     * @param currentPage which page to set.
     */
    public void setCurrentPage(int currentPage) {
        this.mCurrentPage = currentPage;
    }

    /**
     * get max page.
     * 
     * @return max page.
     */
    public int getMaxPage() {
        return mMaxPage;
    }

    /**
     * if there's next page.
     * 
     * @return exists or not.
     */
    public boolean hasNextPage() {
        if (getCurrentPage() < getMaxPage()) {
            return true;
        }
        return false;
    }

    /**
     * if there's previous page.
     * 
     * @return exists or not.
     */
    public boolean hasPrePage() {
        if (getCurrentPage() > 1) {
            return true;
        }

        return false;
    }

    /**
     * scroll listener for candidates.
     */
    class CandidateGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean handled = false;
            return handled;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

    }

    public void setPageStartIndex(int startIndex) {
        this.pageStartIndex = startIndex;
    }

    public int getCandIndexInAll(int i) {
        return pageStartIndex + i;
    }

    public void setPickUpListener(PageArrowClickListener arrowClickListener) {
        this.mPageArrowClickListener = arrowClickListener;
        
    }

}
