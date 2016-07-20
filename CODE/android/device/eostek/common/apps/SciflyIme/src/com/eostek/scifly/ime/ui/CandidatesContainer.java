package com.eostek.scifly.ime.ui;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.util.Constans;

/**
 * Contains all candidates in pages where users could move forward (next page)
 * or move backward (previous) page to select one of these candidates.
 */
public class CandidatesContainer extends LinearLayout {

    private static final String TAG = "CandidatesContainer";

    private CandidateView candidateView;

    private ImageButton leftArrow;

    private ImageButton rightArrow;

    private TextView charInputTip;

    private int lArrowRes;

    private int rArrowRes;
    
    private PageArrowClickListener mArrowClickListener;

    private AbstractIME mService;

    private boolean mIsCandidateViewShow = false;

    /**
     * Constructor.
     * 
     * @param context context.
     */
    public CandidatesContainer(Context context) {
        super(context);
    }

    /**
     * Constructor.
     * 
     * @param context context.
     * @param attrs attributes.
     */
    public CandidatesContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        candidateView = (CandidateView) findViewById(R.id.candidate_view);

     //   lArrowActiveRes = R.drawable.arrow_left_active;
        lArrowRes = R.drawable.arrow_left;

     //   rArrowActiveRes = R.drawable.arrow_right_active;
        rArrowRes = R.drawable.arrow_right;

        leftArrow = (ImageButton) findViewById(R.id.arrow_left);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPrePage(0);
            }
        });

        rightArrow = (ImageButton) findViewById(R.id.arrow_right);
        rightArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showNextPage(0);
            }
        });

        charInputTip = (TextView) findViewById(R.id.char_input_tip);
        updateCharInputTip(null);
    }

    /**
     * set service.
     * 
     * @param service input-method service.
     */
    public void setService(AbstractIME service) {
        mService = service;
        candidateView.setService(service);
    }

    /**
     * get suggestions.
     * 
     * @return list of suggestions.
     */
    public List<String> getSuggestions() {
        return candidateView.getSuggestions();
    }

    /**
     * Not used by now.
     * 
     * @return false.
     */
    public boolean pickHighlighted() {
        return candidateView.pickHighlighted();
    }

    /**
     * set index.
     * 
     * @param index which index to highlight.
     */
    public void setHighlightIndex(int index) {
        candidateView.setHighlighIndex(index);
    }

    /**
     * get index.
     * 
     * @return current highlight index.
     */
    public int getHighlightIndex() {
        return candidateView.getHighlightIndex();
    }

    /**
     * show page.
     * 
     * @param page which page to show.
     */
    public void showPage(int page) {
    }

    /**
     * show next page.
     * 
     * @param page which page to show.
     */
    public void showNextPage(int page) {
        if (!hasNextPage()) {
            return;
        }

        if (mArrowClickListener != null) {
            mArrowClickListener.onNextPageClick(candidateView);
        }

        candidateView.setCurrentPage(candidateView.getCurrentPage() + 1);
        candidateView.showNextPage(page);
        updateArrows();
    }

    /**
     * if there's next page.
     * 
     * @return exists or not.
     */
    public boolean hasNextPage() {
        return candidateView.hasNextPage();
    }

    /**
     * if there's previous page.
     * 
     * @return exits or not.
     */
    public boolean hasPrePage() {
        return candidateView.hasPrePage();
    }

    /**
     * show previous page.
     * 
     * @param page which page to show.
     */
    public void showPrePage(int page) {
        if (!hasPrePage()) {
            return;
        }

        if (mArrowClickListener != null) {
            mArrowClickListener.onPreviousPageClick(candidateView);
        }

        candidateView.setCurrentPage(candidateView.getCurrentPage() - 1);
        candidateView.showPrePage(page);
        updateArrows();
    }

    /**
     * Checks if it's an empty page holding no candidates.
     * 
     * @param page which page.
     * @return is page empty.
     */
    public boolean isPageEmpty(int page) {
        return candidateView.getMaxPage() == 0;
    }

    /**
     * get length of candidates.
     * 
     * @return length of candidates.
     */
    public int getCandidatesLength() {
        return candidateView.getCandidatesLength();
    }

    /**
     * get page count.
     * 
     * @return how many pages.
     */
    public int getPageCount() {
        return candidateView.getMaxPage();
    }

    private void updateArrows() {
        if (candidateView.hasNextPage()) {
            rightArrow.setVisibility(View.VISIBLE);
            rightArrow.setImageResource(rArrowRes);
        } else {
            rightArrow.setVisibility(View.INVISIBLE);
            rightArrow.setImageResource(rArrowRes);
        }

        if (candidateView.hasPrePage()) {
            leftArrow.setVisibility(View.VISIBLE);
            leftArrow.setImageResource(lArrowRes);
        } else {
            leftArrow.setVisibility(View.INVISIBLE);
            leftArrow.setImageResource(lArrowRes);
        }
    }

    /**
     * set candidates.
     * 
     * @param words words of candidates.
     * @param completions is completed.
     * @param typedWordValid is word valid.
     */
    public void setCandidates(List<String> words, boolean completions, boolean typedWordValid) {

        if (words == null || words.size() == 0) {
           //  this.setVisibility(View.GONE);
            Log.d(TAG, "mService.setCandidatesViewShown(false)");
           mService.setCandidatesViewShown(false);
           mIsCandidateViewShow = false;
            return;
        } else {
//            if (this.getVisibility() == View.GONE) {
//          this.setVisibility(View.VISIBLE);
//          }
            Log.d(TAG, "mService.setCandidatesViewShown(true)");
            mService.setCandidatesViewShown(true);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.candidatebkg);
            Resources resources = linearLayout.getResources();
            Drawable btnDrawable = resources.getDrawable(R.drawable.candidate_bk);
            linearLayout.setBackgroundDrawable(btnDrawable);
            mIsCandidateViewShow = true;
        }
        setHighlightIndex(-1);
        candidateView.setSuggestions(words, completions, typedWordValid);
        updateArrows();
    }

    public boolean getIsCandidateViewShow() {
        return mIsCandidateViewShow;
    }

    /** Update Char Input Tip */
    public void updateCharInputTip(String str) {
        if (str != null && !"".equals(str)) {
            // Constans.print(TAG, "charInputTip " + str);
            charInputTip.setText(str);
            if (charInputTip.getVisibility() == INVISIBLE) {
                this.setVisibility(View.VISIBLE);
                charInputTip.setVisibility(VISIBLE);
            }
        } else {
            charInputTip.setText("");
            charInputTip.setVisibility(INVISIBLE);
        }
    }

    /** get Char Input Tip */
    public String getCharInputTip() {
        return charInputTip.getText().toString();
    }

    public interface PageArrowClickListener {

        void onNextPageClick(View candidateView);

        void onPreviousPageClick(View candidateView);

        void pickSuggestionManually(int index);
    }

    public void setPageArrowClickListener(PageArrowClickListener mArrowClickListener) {
        this.mArrowClickListener = mArrowClickListener;

        if (candidateView != null) {
            candidateView.setPickUpListener(mArrowClickListener);
            Constans.print("", "mPageArrowClickListener not nullllllllllllllllllllllllllllll");
        } else {
            Constans.print("", "mPageArrowClickListener nullllllllllllllllllllllllllllll");

        }
    }

    public void setPageStartIndex(int i) {
        candidateView.setPageStartIndex(i);
    }

    public int getCandIndexInAll(int i) {
        return candidateView.getCandIndexInAll(i);
    }

}
