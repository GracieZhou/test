
package com.eostek.tv.epg;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eostek.tv.R;
import com.eostek.tv.utils.UtilsTools;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

public class EpgHolder {
    private Activity mContext;

    private ListView mChannelLst;

    private ListView mEpgLst;

    private TextView mDescriptor_title_txt;

    private TextView mDescriptor_content_txt;

    private TextView mCurtime_txt;

    private TextView mEpg_tip_txt;

    private ProgressBar mEpg_tip_bar;

    private TextView mEpg_des_tip_txt;

    private ProgressBar mEpg_des_tip_bar;

    private View mChannelselector;

    public EpgHolder(Activity mContext) {
        this.mContext = mContext;
        initViews();
    }

    public void initViews() {
        mContext.setContentView(R.layout.epg_activity);
        mChannelLst = (ListView) mContext.findViewById(R.id.channel_lst);
        mEpgLst = (ListView) mContext.findViewById(R.id.epg_lst);
        mDescriptor_title_txt = (TextView) mContext.findViewById(R.id.descriptor_title);
        mDescriptor_content_txt = (TextView) mContext.findViewById(R.id.descriptor_content);
        mCurtime_txt = (TextView) mContext.findViewById(R.id.currenttime);
        mEpg_tip_txt = (TextView) mContext.findViewById(R.id.epg_tip);
        mEpg_tip_bar = (ProgressBar) mContext.findViewById(R.id.epg_tip_progress);
        mEpg_des_tip_txt = (TextView) mContext.findViewById(R.id.epg_des_tip);
        mEpg_des_tip_bar = (ProgressBar) mContext.findViewById(R.id.epg_des_tip_progress);
        mCurtime_txt.setText(UtilsTools.formatDate(System.currentTimeMillis(), "MM.dd.yyyy  HH:mm"));
        mChannelselector = mContext.findViewById(R.id.channel_selector);
    }

    public View getChannelselector() {
        return mChannelselector;
    }

    public Activity getmContext() {
        return mContext;
    }

    public ListView getChannelLst() {
        return mChannelLst;
    }

    public ListView getEpgLst() {
        return mEpgLst;
    }

    public TextView getTitleText() {
        return mDescriptor_title_txt;
    }

    public TextView getContentTexxt() {
        return mDescriptor_content_txt;
    }

    public void getTime() {
        mCurtime_txt.setText(UtilsTools.formatDate(System.currentTimeMillis(), "MM.dd.yyyy  HH:mm"));
    }

    public void setEpgInfoVisible() {
        mEpg_tip_txt.setVisibility(View.VISIBLE);
        mEpg_des_tip_txt.setVisibility(View.VISIBLE);
    }

    public void EpgInfoChanged() {
        mEpg_tip_bar.setVisibility(View.VISIBLE);
        mEpg_des_tip_bar.setVisibility(View.VISIBLE);
        mEpg_tip_txt.setVisibility(View.VISIBLE);
        mEpg_des_tip_txt.setVisibility(View.VISIBLE);
        mEpg_tip_txt.setText(R.string.getepg_tip);
        mEpg_des_tip_txt.setText(R.string.getepgdes_tip);
    }

    public void getEPGInfo() {
        mEpg_tip_bar.setVisibility(View.GONE);
        mEpg_des_tip_bar.setVisibility(View.GONE);
        mEpg_tip_txt.setVisibility(View.GONE);
        mEpg_des_tip_txt.setVisibility(View.GONE);
    }

    public void loadingEPGInfo() {
        mEpg_tip_bar.setVisibility(View.VISIBLE);
        mEpg_des_tip_bar.setVisibility(View.VISIBLE);
        mEpg_tip_txt.setText(R.string.getepg_tip);
        mEpg_des_tip_txt.setText(R.string.getepgdes_tip);
    }

    public void setTitleText(List<EpgEventInfo> mEventInfos, int index) {
        mDescriptor_title_txt.setText(mEventInfos.get(index).name);
    }

    public void setContentNone() {
        mDescriptor_title_txt.setText("");
        mDescriptor_content_txt.setText("");
    }
}
