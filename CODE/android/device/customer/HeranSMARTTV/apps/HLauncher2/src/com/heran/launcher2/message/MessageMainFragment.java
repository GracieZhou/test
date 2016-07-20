
package com.heran.launcher2.message;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.widget.ViewBean;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageMainFragment extends PublicFragment {

    private final static String TAG = "MessageMainFragment";

    private HomeActivity mContext;

    private ViewBean mViewBean;

    private BtnFocusChangeListener mBtnFocusChangeListener;

    private BtnOnKeyListener mBtnOnKeyListener;

    private BtnOnClickListener mBtnOnClickListener;

    private TextView mMsgText1;

    private TextView mMsgText2;

    private TextView mMsgText3;

    private TextView mMsgText4;

    private TextView mTotalPage;

    private TextView mChoicePage;

    private ImageView mIcon1;

    private ImageView mIcon2;

    private ImageView mIcon3;

    private ImageView mIcon4;

    private ImageButton mPrePage;

    private ImageButton mNextPage;

    private String[] mMsgTexts;

    private int mTextLength = 0; // 訊息數目

    private int mNumPage = 1; // 目前頁數

    private int mNumTotal = 0; // 總頁數

    public MessageMainFragment(HomeActivity context) {
        super();
        this.mContext = context;
        mViewBean = new ViewBean(null, null);
        Log.d(TAG, "(HomeActivity context, MainViewHolder mHolder)");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.message_main_content, container, false);

        InitView(mview);
        setRetainInstance(true);
        return mview;
    }

    public void InitView(View mview) {
        mMsgTexts = mContext.mainLogic.GetAdTexts();
        mTextLength = mMsgTexts.length;
        if (mTextLength % 4 == 0) {
            mNumTotal = mTextLength / 4;
        } else {
            mNumTotal = mTextLength / 4 + 1;
        }

        Log.i("willy2", " numTotal = " + mNumTotal);
        mMsgText1 = (TextView) mview.findViewById(R.id.msg_text);
        mMsgText2 = (TextView) mview.findViewById(R.id.msg_text2);
        mMsgText3 = (TextView) mview.findViewById(R.id.msg_text3);
        mMsgText4 = (TextView) mview.findViewById(R.id.msg_text4);
        mTotalPage = (TextView) mview.findViewById(R.id.totalpage);
        mChoicePage = (TextView) mview.findViewById(R.id.choicepage);

        mIcon1 = (ImageView) mview.findViewById(R.id.msg_icon1);
        mIcon2 = (ImageView) mview.findViewById(R.id.msg_icon2);
        mIcon3 = (ImageView) mview.findViewById(R.id.msg_icon3);
        mIcon4 = (ImageView) mview.findViewById(R.id.msg_icon4);
        mPrePage = (ImageButton) mview.findViewById(R.id.prepage);
        mNextPage = (ImageButton) mview.findViewById(R.id.nextpage);

        mBtnOnKeyListener = new BtnOnKeyListener();
        mBtnFocusChangeListener = new BtnFocusChangeListener();
        mBtnOnClickListener = new BtnOnClickListener();

        mTotalPage.setText(String.valueOf(mNumTotal));

        SetOnListener(mPrePage);
        SetOnListener(mNextPage);

        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(mPrePage, mViewBean);
        addViewGlobalLayoutListener(mNextPage, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            Log.d(TAG, "getmCurFocusView() =null nextPage.requestFocus();");
            mNextPage.requestFocus();
        }

    }

    private void drawFocus(View view) {
        Log.d(TAG, "draw view:" + view);
        mViewBean.setmCurFocusView(view);
        mViewBean.getmFocusObject().startAnimation(view);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "on resum nextPage.requestFocus();");
        if (mViewBean.getmCurFocusView() == mPrePage) {
            mPrePage.requestFocus();
        } else {
            mNextPage.requestFocus();
        }
        if (mTextLength <= 4) {
            for (int i = 0; i < mTextLength; i++) {
                ShowText(i);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                ShowText(i);
            }
        }
        super.onResume();
        mNumPage = 1;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "on onStop;");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "on onDestroy;");
        super.onDestroy();
    }

    private void ShowText(int i) {

        switch (i) {
            case 0:
                mMsgText1.setVisibility(View.VISIBLE);
                mMsgText1.setText(mMsgTexts[i + (mNumPage - 1) * 4]);
                mIcon1.setVisibility(View.VISIBLE);
                break;
            case 1:
                mMsgText2.setVisibility(View.VISIBLE);
                mMsgText2.setText(mMsgTexts[i + (mNumPage - 1) * 4]);
                mIcon2.setVisibility(View.VISIBLE);
                break;
            case 2:
                mMsgText3.setVisibility(View.VISIBLE);
                mMsgText3.setText(mMsgTexts[i + (mNumPage - 1) * 4]);
                mIcon3.setVisibility(View.VISIBLE);
                break;
            case 3:
                mMsgText4.setText(mMsgTexts[i + (mNumPage - 1) * 4]);
                mMsgText4.setVisibility(View.VISIBLE);
                mIcon4.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }

    }

    private void SetOnListener(View v) {
        v.setOnClickListener(mBtnOnClickListener);
        v.setOnKeyListener(mBtnOnKeyListener);
        v.setOnFocusChangeListener(mBtnFocusChangeListener);
    }

    private void Invisible() {
        mMsgText1.setVisibility(View.GONE);
        mIcon1.setVisibility(View.GONE);
        mMsgText2.setVisibility(View.GONE);
        mIcon2.setVisibility(View.GONE);
        mMsgText3.setVisibility(View.GONE);
        mIcon3.setVisibility(View.GONE);
        mMsgText4.setVisibility(View.GONE);
        mIcon4.setVisibility(View.GONE);

    }

    class BtnFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean focus) {
            if (focus) {
                drawFocus(v);
                switch (v.getId()) {
                    case R.id.prepage:
                        mPrePage.setImageResource(R.drawable.msg_prepage_s);
                        break;
                    case R.id.nextpage:
                        mNextPage.setImageResource(R.drawable.msg_nextpage_s);
                        break;

                    default:
                        break;
                }
            } else {
                mPrePage.setImageResource(R.drawable.msg_prepage_ns);
                mNextPage.setImageResource(R.drawable.msg_nextpage_ns);
            }
        }
    }

    class BtnOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keycode, KeyEvent event) {
            switch (v.getId()) {
                case R.id.prepage:
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keycode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        mContext.mviewHolder.fragmentBtn.requestFocus();

                        return true;
                    }
                    break;

                case R.id.nextpage:
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keycode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        mContext.mviewHolder.fragmentBtn.requestFocus();

                        return true;
                    }
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keycode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                        mContext.mviewHolder.setHomeBtnFocus(0);
                        return true;
                    }
                    break;

                default:
                    break;
            }
            return false;
        }

    }

    class BtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mTextLength > 4) {
                Invisible();
                switch (v.getId()) {
                    case R.id.nextpage:

                        if (mNumPage != mNumTotal) {
                            mNumPage++;
                        }

                        if (mNumPage != mNumTotal) {

                            for (int i = 0; i < 4; i++) {
                                ShowText(i);
                            }
                        } else {
                            for (int i = 0; i < mTextLength % 4; i++) {
                                ShowText(i);
                            }
                        }

                        break;
                    case R.id.prepage:
                        if (mNumPage != 1) {
                            mNumPage--;
                        }

                        for (int i = 0; i < 4; i++) {
                            ShowText(i);
                        }

                        break;
                }

                mChoicePage.setText(String.valueOf(mNumPage));

            }

        }

    }

}
