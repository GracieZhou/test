
package com.eostek.isynergy.setmeup.timezone;

import java.util.List;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.model.TimeZoneModel;
import com.eostek.isynergy.setmeup.utils.Utils;

public class TimeZoneFragment extends Fragment {

    protected static final String TAG = TimeZoneFragment.class.getSimpleName();

    private ListView mTimeZoneLv;

    private TimezoneLogic mTimezoneLogic;

    private int mPosition;

    private TimeZoneAdapter mAdapterTimezone;

    private List<TimeZoneModel> mTimeZones;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            Utils.print(TAG, "onGlobalLayout");
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mTimeZoneLv.setSelection(mAdapterTimezone.getCurrentTimeZonePosition());
                    mTimeZoneLv.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);

                }
            });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_fragment_timezone, container, false);

        mTimeZoneLv = (ListView) v.findViewById(R.id.lv_timezone);

        mTimezoneLogic = new TimezoneLogic(this);

        // v.requestFocus();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        mTimeZones = mTimezoneLogic.getTimeZones();

        mAdapterTimezone = new TimeZoneAdapter(getActivity(), mTimeZones);

        mTimeZoneLv.setAdapter(mAdapterTimezone);
        mTimeZoneLv.requestFocus();

        mTimeZoneLv.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

        mTimeZoneLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long arg3) {
                setTimeZone(mTimeZones.get(position).getTimeZoneId());
                mAdapterTimezone.setPosition(position, mTimeZoneLv);
            }
        });
        mTimeZoneLv.setSelection(getTimezoneId());
    }

    private int getTimezoneId() {
        String timeZoneId = TimeZone.getDefault().getID();
        for (TimeZoneModel timeZone: mTimeZones) {
            if (timeZone.getTimeZoneId().equals(timeZoneId)) {
                return mTimeZones.indexOf(timeZone);
            }
        }
        return 0;
    }

    /**
     * set time zone by timezoneId
     * 
     * @param timezoneId
     */
    public void setTimeZone(String timezoneId) {
        AlarmManager timeZone = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        timeZone.setTimeZone(timezoneId);
    }

}
