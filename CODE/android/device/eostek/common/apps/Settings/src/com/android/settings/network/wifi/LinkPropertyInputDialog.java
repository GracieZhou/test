
package com.android.settings.network.wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class LinkPropertyInputDialog extends AlertDialog {

    private static final String TAG = "LinkPropertyInputDialog";

    Context mContext;

    private int value;

    private String mValueStr;

    private View mMainView;

    LinkPropertyInputView minputView;

    private PropertyChangeListener mPropertyChangeListener;

    private String nullIpInfo="0.0.0.0";

    public LinkPropertyInputDialog(Context context, String valueStr) {
        super(context);
        mContext = context;
        mValueStr = valueStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mMainView = getLayoutInflater().inflate(R.layout.wifi_link_property_input_layout, null);
        setView(mMainView);

        super.onCreate(savedInstanceState);

        findViews();

    }

    private void findViews() {
        minputView = new LinkPropertyInputView(mContext, mMainView);
        if(mValueStr==null){
            mValueStr = nullIpInfo;
        }
        minputView.setValue(mValueStr);
        setTitleWidget();
    }

    public void setTitleWidgetText(String string) {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setSecondSubTitleText(""+string);
        }
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setMainTitleText(mContext.getString(R.string.action_settings));
            tw.setFirstSubTitleText(mContext.getString(R.string.network_settings), false);
            tw.setSecondSubTitleText(mContext.getString(R.string.wifi_settings));
        }
    }

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

    public void setPropertyChangeListener(PropertyChangeListener l) {
        this.mPropertyChangeListener = l;
    }

    public interface PropertyChangeListener {

        public void onPropertyChange(String value);
    }

}
