
package com.heran.launcher2.message;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
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

    private HomeActivity mContext;

    private MainViewHolder mHolder;

    private ViewBean mViewBean;

    BtnFocusChangeListener mBtnFocusChangeListener;

    BtnOnKeyListener mBtnOnKeyListener;

    BtnOnClickListener mBtnOnClickListener;

    TextView msg_text1, msg_text2, msg_text3, msg_text4, totalPage, choicePage;

    ImageView icon1, icon2, icon3, icon4;

    ImageButton prePage, nextPage;

    private String[] Msg_Texts;

    int TextLength = 0; // 訊息數目

    int numPage = 1; // 目前頁數

    int numTotal = 0; // 總頁數

    private final static String TAG = "MessageMainFragment";

    public MessageMainFragment(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        this.mHolder = mHolder;
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
        Msg_Texts = mContext.mainLogic.GetAdTexts();
        TextLength = Msg_Texts.length;
        if (TextLength % 4 == 0) {
            numTotal = TextLength / 4;
        } else {
            numTotal = TextLength / 4 + 1;
        }

        Log.i("willy2", " numTotal = " + numTotal);
        msg_text1 = (TextView) mview.findViewById(R.id.msg_text);
        msg_text2 = (TextView) mview.findViewById(R.id.msg_text2);
        msg_text3 = (TextView) mview.findViewById(R.id.msg_text3);
        msg_text4 = (TextView) mview.findViewById(R.id.msg_text4);
        totalPage = (TextView) mview.findViewById(R.id.totalpage);
        choicePage = (TextView) mview.findViewById(R.id.choicepage);

        icon1 = (ImageView) mview.findViewById(R.id.msg_icon1);
        icon2 = (ImageView) mview.findViewById(R.id.msg_icon2);
        icon3 = (ImageView) mview.findViewById(R.id.msg_icon3);
        icon4 = (ImageView) mview.findViewById(R.id.msg_icon4);
        prePage = (ImageButton) mview.findViewById(R.id.prepage);
        nextPage = (ImageButton) mview.findViewById(R.id.nextpage);

        mBtnOnKeyListener = new BtnOnKeyListener();
        mBtnFocusChangeListener = new BtnFocusChangeListener();
        mBtnOnClickListener = new BtnOnClickListener();

        totalPage.setText(String.valueOf(numTotal));

        SetOnListener(prePage);
        SetOnListener(nextPage);

        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(prePage, mViewBean);
        addViewGlobalLayoutListener(nextPage, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            Log.d(TAG, "getmCurFocusView() =null nextPage.requestFocus();");
            nextPage.requestFocus();
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
        if (mViewBean.getmCurFocusView() == prePage) {
            prePage.requestFocus();
        } else {
            nextPage.requestFocus();
        }
        if (TextLength <= 4) {
            for (int i = 0; i < TextLength; i++) {
                ShowText(i);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                ShowText(i);
            }
        }
        super.onResume();
        numPage = 1;
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
                msg_text1.setVisibility(View.VISIBLE);
                msg_text1.setText(Msg_Texts[i + (numPage - 1) * 4]);
                icon1.setVisibility(View.VISIBLE);
                break;
            case 1:
                msg_text2.setVisibility(View.VISIBLE);
                msg_text2.setText(Msg_Texts[i + (numPage - 1) * 4]);
                icon2.setVisibility(View.VISIBLE);
                break;
            case 2:
                msg_text3.setVisibility(View.VISIBLE);
                msg_text3.setText(Msg_Texts[i + (numPage - 1) * 4]);
                icon3.setVisibility(View.VISIBLE);
                break;
            case 3:
                msg_text4.setText(Msg_Texts[i + (numPage - 1) * 4]);
                msg_text4.setVisibility(View.VISIBLE);
                icon4.setVisibility(View.VISIBLE);
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
        msg_text1.setVisibility(View.GONE);
        icon1.setVisibility(View.GONE);
        msg_text2.setVisibility(View.GONE);
        icon2.setVisibility(View.GONE);
        msg_text3.setVisibility(View.GONE);
        icon3.setVisibility(View.GONE);
        msg_text4.setVisibility(View.GONE);
        icon4.setVisibility(View.GONE);

    }

    class BtnFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean focus) {
            if (focus) {
                drawFocus(v);
                switch (v.getId()) {
                    case R.id.prepage:
                        prePage.setImageResource(R.drawable.msg_prepage_s);
                        break;
                    case R.id.nextpage:
                        nextPage.setImageResource(R.drawable.msg_nextpage_s);
                        break;

                    default:
                        break;
                }
            } else {
                prePage.setImageResource(R.drawable.msg_prepage_ns);
                nextPage.setImageResource(R.drawable.msg_nextpage_ns);
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
                        mHolder.fragmentBtn.requestFocus();

                        return true;
                    }
                    break;

                case R.id.nextpage:
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keycode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        mContext.playVoice(KeyEvent.KEYCODE_DPAD_DOWN);
                        mHolder.fragmentBtn.requestFocus();

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
            if (TextLength > 4) {
                Invisible();
                switch (v.getId()) {
                    case R.id.nextpage:

                        if (numPage != numTotal) {
                            numPage++;
                        }

                        if (numPage != numTotal) {

                            for (int i = 0; i < 4; i++) {
                                ShowText(i);
                            }
                        } else {
                            for (int i = 0; i < TextLength % 4; i++) {
                                ShowText(i);
                            }
                        }

                        break;
                    case R.id.prepage:
                        if (numPage != 1) {
                            numPage--;
                        }

                        for (int i = 0; i < 4; i++) {
                            ShowText(i);
                        }

                        break;
                }

                choicePage.setText(String.valueOf(numPage));

            }

        }

    }

}
