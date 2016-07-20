
package com.android.settings.datetimecity;

import java.util.Calendar;
import java.util.TimeZone;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;
/**
 * show UI of timezone
 */
public class TimeZoneHolder {

    private static final String TAG = "TIMEZONE";

    private TimeZoneSettingActivity mActivity;

    private ListView mTimeZoneListView;

    private TitleWidget mTitleWidget;

    private TextView mOriginKeyTV;

    private TextView mOriginValueTV;

    private TextView mKeyTv;

    private TextView mValueTv;

    private ImageView mOriginImage;

    private ImageView mImage;

    private TimeZoneAdapter mSimpleAdapter;

    public TimeZoneHolder(TimeZoneSettingActivity activity) {
        this.mActivity = activity;
    }
/**
 * find and init views.
 */
    public void findViews() {
        mTimeZoneListView = (ListView) mActivity.findViewById(R.id.time_zone_list);
        mActivity.mLogic.getDetailArray();
        mTitleWidget = (TitleWidget) mActivity.findViewById(R.id.set_timezone_title);
        mKeyTv = (TextView) mActivity.findViewById(R.id.timezone_key);
        mValueTv = (TextView) mActivity.findViewById(R.id.timezone_value);
        mTitleWidget.setSubTitleText(mActivity.getResources().getString(R.string.area_time), mActivity.getResources()
                .getString(R.string.set_time_zone));

    }
/**
 * set register adapter.
 */
    @SuppressWarnings("static-access")
    public void registerAdapter() {

        mSimpleAdapter = new TimeZoneAdapter(mActivity, mActivity.mLogic.timezoneArray);
        mTimeZoneListView.setAdapter(mSimpleAdapter);
        
	int currentPosition = TimeZoneLogic.getCurrentTimeZone(mSimpleAdapter,
				Calendar.getInstance().getTimeZone());
		if (!mTimeZoneListView.isFocused()) {
			mTimeZoneListView.requestFocus();
			mTimeZoneListView.setSelection(currentPosition);
		}
        mTimeZoneListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mKeyTv = (TextView) view.findViewById(R.id.timezone_key);
                mValueTv = (TextView) view.findViewById(R.id.timezone_value);
                if (mOriginKeyTV == null) {
                    mOriginKeyTV = mKeyTv;
                }
                if (mOriginValueTV == null) {
                    mOriginValueTV = mValueTv;
                }
                mOriginKeyTV.setTextColor(mOriginKeyTV.getResources().getColor(R.color.white));
                mKeyTv.setTextColor(view.getResources().getColor(R.color.green));
                mOriginKeyTV = mKeyTv;
                mOriginValueTV.setTextColor(mOriginValueTV.getResources().getColor(R.color.white));
                mValueTv.setTextColor(view.getResources().getColor(R.color.green));
                mOriginValueTV = mValueTv;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mTimeZoneListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

                TimeZone tz = Calendar.getInstance().getTimeZone();
                int id = TimeZoneLogic.getCurrentTimeZone(mSimpleAdapter, tz);
                Log.d(TAG, "<<<<<<< tz<<<<<<id<<<<<<<<<<<" + tz + "<<<<<" + id);
                Log.d(TAG, "<<<<<<<<<mTimeZoneListView.getChildAt(id)<<<<<<<<<" + mTimeZoneListView.getChildAt(id));
                if (mTimeZoneListView.getChildAt(id) != null) {
                    mTimeZoneListView.getChildAt(id).findViewById(R.id.timezone_image).setVisibility(View.INVISIBLE);
                }
                mImage = (ImageView) view.findViewById(R.id.timezone_image);
                if (mOriginImage == null) {
                    mOriginImage = mImage;
                }
                mOriginImage.setVisibility(View.INVISIBLE);
                mImage.setVisibility(View.VISIBLE);
                mOriginImage = mImage;
                //set the onclick timezone as current timezone.
                mActivity.mLogic.setTimeZone(mActivity.mLogic.timezoneIdStr[position]);
                Log.d(TAG, "<<<<<<< tz<<<<<<id<<<<<<<<<<<" + Calendar.getInstance().getTimeZone());
                mSimpleAdapter.setCurrentTimeZone(position);
            }
        });

    }

}
