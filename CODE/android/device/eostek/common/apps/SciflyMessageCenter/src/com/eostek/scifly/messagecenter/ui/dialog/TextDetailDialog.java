
package com.eostek.scifly.messagecenter.ui.dialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eostek.scifly.messagecenter.MainActivity;
import com.eostek.scifly.messagecenter.R;

/**
 * the Dialog for showing detail message text.
 */
public class TextDetailDialog extends AlertDialog {

    private static final String TAG = "TextDetailDialog";

    MainActivity mContext;

    private TextView mTextContent;

    private Button mCloseButton;

    private TextView mTimeText;

    private ScrollView mScrollView;

    /**
     * Constructor.
     * 
     * @param context
     */
    public TextDetailDialog(MainActivity context) {
        super(context);
        this.mContext = context;
    }

    /**
     * set content for the dialog to show.
     * 
     * @param timeString
     * @param content
     */
    public void setContent(String timeString, String content) {
        mTimeText.setText("" + timeString);
        mTextContent.setText("" + content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_detail_dialog);

        findViews();
        setListener();
    }

    private void setListener() {

        setCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TextDetailDialog.this.dismiss();
            }
        });

        mScrollView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int size = mScrollView.getHeight();
                int y = mScrollView.getScrollY();

                if (mScrollView.getChildAt(0).getMeasuredHeight() <= v.getHeight() + v.getScrollY()) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                        Log.i(TAG, "time to change focus");
                        mCloseButton.requestFocus();
                        mCloseButton.requestFocusFromTouch();
                    }
                }
                return false;
            }
        });
    }

    private void findViews() {
        mTimeText = (TextView) findViewById(R.id.text_detail_time);
        mTextContent = (TextView) findViewById(R.id.text_detail_content);
        mCloseButton = (Button) findViewById(R.id.text_detail_close);
        mScrollView = (ScrollView) findViewById(R.id.text_detail_scrollview);
    }

    /**
     * set the listener to close the dialog.
     * 
     * @param listener
     */
    public void setCloseListener(View.OnClickListener listener) {
        mCloseButton.setOnClickListener(listener);

    }

    /**
     * show message text by the dialog.
     * 
     * @param userName
     */
    public void show(String userName) {
        this.show();
    }
}
