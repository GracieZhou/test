package com.eostek.uitemplate.widget;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eostek.uitemplate.R;

public class SciflyToast {

    public static void showShortToast(Context context, CharSequence text) {
        
        Toast toast = new Toast(context); 

        LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflate.inflate(R.layout.scifly_toast_layout,  null);
        Button content = (Button) layout.findViewById(R.id.custom_toast_btn);
        content.setText(text);
        
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 30);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    
    public static void showLongToast(Context context, CharSequence text) {
        Toast toast = new Toast(context); 

        LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflate.inflate(R.layout.scifly_toast_layout,  null);
        Button content = (Button) layout.findViewById(R.id.custom_toast_btn);
        content.setText(text);
        
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 30);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    
    public static void showShortToast(Context context, int resId) throws Resources.NotFoundException{
        
        Toast toast = new Toast(context); 

        LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflate.inflate(R.layout.scifly_toast_layout,  null);
        Button content = (Button) layout.findViewById(R.id.custom_toast_btn);
        content.setText(context.getResources().getText(resId));
        
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 30);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    
    public static void showLongToast(Context context, int resId) throws Resources.NotFoundException{
        Toast toast = new Toast(context); 

        LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflate.inflate(R.layout.scifly_toast_layout,  null);
        Button content = (Button) layout.findViewById(R.id.custom_toast_btn);
        content.setText(context.getResources().getText(resId));
        
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 30);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
