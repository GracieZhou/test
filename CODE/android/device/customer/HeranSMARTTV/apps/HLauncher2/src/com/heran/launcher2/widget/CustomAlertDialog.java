
package com.heran.launcher2.widget;

import com.heran.launcher2.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CustomAlertDialog {
    Context context;

    AlertDialog ad;

    TextView titleView;

    TextView messageView;

    LinearLayout buttonLayout;

    public CustomAlertDialog(Context context) {
        this.context = context;
        ad = new AlertDialog.Builder(context).create();
        ad.show();
        // 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
        Window window = ad.getWindow();
        window.setContentView(R.layout.custom_alertdialog);
        titleView = (TextView) window.findViewById(R.id.title);
        messageView = (TextView) window.findViewById(R.id.message);
        buttonLayout = (LinearLayout) window.findViewById(R.id.buttonLayout);
    }

    public void setTitle(int resId) {
        titleView.setText(resId);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setMessage(int resId) {
        messageView.setText(resId);
    }

    public void setMessage(String message) {
        messageView.setText(message);
    }

    /**
     * 设置按钮
     * 
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text, final View.OnClickListener listener) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 55);
        button.setLayoutParams(params);
        button.setBackgroundResource(R.drawable.custom_alertdialog_button);
        button.setText(text);
        button.setPadding(10, 1, 10, 1);
        button.setTextColor(context.getResources().getColor(R.color.green));
        button.setTextSize(15);
        button.setOnClickListener(listener);
        buttonLayout.addView(button);
        button.requestFocus();
        button.setFocusable(true);
    }

    /**
     * 设置按钮
     * 
     * @param id
     * @param listener
     */
    public void setPositiveButton(int id, final View.OnClickListener listener) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 55);
        button.setLayoutParams(params);
        button.setBackgroundResource(R.drawable.custom_alertdialog_button);
        button.setText(id);
        button.setPadding(10, 1, 10, 1);
        button.setTextColor(context.getResources().getColor(R.color.green));
        button.setTextSize(15);
        button.setOnClickListener(listener);
        buttonLayout.addView(button);
        button.requestFocus();
        button.setFocusable(true);
    }

    /**
     * 设置按钮
     * 
     * @param text
     * @param listener
     */
    public void setNegativeButton(String text, final View.OnClickListener listener) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 55);
        button.setLayoutParams(params);
        button.setBackgroundResource(R.drawable.custom_alertdialog_button);
        button.setText(text);
        button.setPadding(10, 1, 10, 1);
        button.setTextColor(context.getResources().getColor(R.color.green));
        button.setTextSize(15);
        button.setOnClickListener(listener);
        if (buttonLayout.getChildCount() > 0) {
            params.setMargins(20, 0, 0, 0);
            button.setLayoutParams(params);
            buttonLayout.addView(button, 1);
        } else {
            button.setLayoutParams(params);
            buttonLayout.addView(button);
        }
    }

    /**
     * 设置按钮
     * 
     * @param text
     * @param listener
     */
    public void setNegativeButton(int id, final View.OnClickListener listener) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 55);
        button.setLayoutParams(params);
        button.setBackgroundResource(R.drawable.custom_alertdialog_button);
        button.setText(id);
        button.setPadding(10, 1, 10, 1);
        button.setTextColor(context.getResources().getColor(R.color.green));
        button.setTextSize(15);
        button.setOnClickListener(listener);
        if (buttonLayout.getChildCount() > 0) {
            params.setMargins(20, 0, 0, 0);
            button.setLayoutParams(params);
            buttonLayout.addView(button, 1);
        } else {
            button.setLayoutParams(params);
            buttonLayout.addView(button);
        }

    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
    }

}
