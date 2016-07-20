
package com.android.settings.network.wifi;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;
/**
 * the dialog use to input the information of wired
 *
 */
public class LinkPropertyInputDialog extends AlertDialog {
    private TitleWidget mTitleWidget;

    Context mContext;

    private String mValueStr;

    private View mMainView;

    LinkPropertyInputView minputView;

    private PropertyChangeListener mPropertyChangeListener;

    private final static String DEFAULTIP="0.0.0.0";

    public LinkPropertyInputDialog(Context context, String valueStr) {
        super(context);
        mContext = context;
        mValueStr = valueStr;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mMainView = getLayoutInflater().inflate(R.layout.wifi_link_property_input_layout,null);
        setView(mMainView);

        super.onCreate(savedInstanceState);

        findViews();

    }

    private void findViews() {
        minputView = new LinkPropertyInputView(mContext, mMainView);
        mTitleWidget = (TitleWidget) findViewById(R.id.title_widget);
        if(mValueStr==null){
            mValueStr = DEFAULTIP;
        }
        minputView.setValue(mValueStr);
//        setTitleWidget();
    }

/**
 * the title name according to the string introducted by outside
 * @param string
 */
    public void setTitleWidget(String string) {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
                if(TextUtils.isEmpty(string)){
                    tw.setSubTitleText(mContext.getString(R.string.network_settings));
                }else{
                    tw.setSubTitleText(mContext.getString(R.string.network_settings),string);
          }
        }
    }
/**
 * save the last input information,and show it next. 
 */
    @Override
    public void dismiss() {
        super.dismiss();
        if (mPropertyChangeListener != null) {
            String value = null;
            if (minputView != null) {
                value = minputView.getValue();
            }
            mPropertyChangeListener.onPropertyChange(value);
        }

    }
/**
 * setPropertyChangeListener when input change.
 * @param l
 */
    public void setPropertyChangeListener(PropertyChangeListener l) {
        this.mPropertyChangeListener = l;
    }

    public interface PropertyChangeListener {

        public void onPropertyChange(String value);
    }

    public void setTitle(String str1,String str2) {
        mTitleWidget.setSubTitleText(mContext.getResources().getString(R.string.network_setting),str1,str2);
    }

    public void setTitle(int resId1,int resId2) {
        setTitle(mContext.getResources().getString(resId1),mContext.getResources().getString(resId2));
    }
}
