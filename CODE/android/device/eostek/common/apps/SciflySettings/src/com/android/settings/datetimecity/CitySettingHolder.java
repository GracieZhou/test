
package com.android.settings.datetimecity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;

import com.android.settings.R;
import com.android.settings.userbackup.BackUpData;
import com.android.settings.util.Utils;
import com.android.settings.widget.TitleWidget;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * CitySettingHolder refresh the UI of city
 */
public class CitySettingHolder {

    private String TAG = "CitySetting";

    private CitySettingActivity mActivity;

    private TextView mCurrntCity;

    private TextView mOnClickCity;

    private TitleWidget mTitleWidget;

    private Button mAutoReceivebtn;

    private Button mSearchReceivebtn;

    private EditText mCitySeachEdit;

    private ListView mCityListView;

    private SearchCityAdapter mSearchCityAdapter;

    private TextView mOriginKeyTV;

    private TextView mKeyTv;

    public CitySettingHolder(CitySettingActivity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    /**
     * to find views.
     */
    @SuppressWarnings("static-access")
    public void findViews() {
        // set title
        mTitleWidget = (TitleWidget) mActivity.findViewById(R.id.set_city_title);
        mTitleWidget.setSubTitleText(mActivity.getString(R.string.area_time),
                mActivity.getResources().getString(R.string.set_city));
        mAutoReceivebtn = (Button) mActivity.findViewById(R.id.auto_receive);
        try {
            JSONObject json = new JSONObject(Global.getString(mActivity.getContentResolver(), Global.LOCATION));
            String location_city = json.getString("city");
            Log.d(TAG, "<<<<<<<<location_city<<<<<<<<<<<" + location_city);
            if (!TextUtils.isEmpty(location_city)) {
                mAutoReceivebtn.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        mSearchReceivebtn = (Button) mActivity.findViewById(R.id.search);
        mCitySeachEdit = (EditText) mActivity.findViewById(R.id.edittext);
        mCityListView = (ListView) mActivity.findViewById(R.id.city_setting_list);
        mCurrntCity = (TextView) mActivity.findViewById(R.id.current_city);
        mCurrntCity.setText(mActivity.mLogic.getCurrentCity(mActivity));
    }

    /**
     * To set up button click event
     */
    public void registerListener() {
        mAutoReceivebtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.d(TAG, "auto get city");
                mCurrntCity.setText(mActivity.mLogic.autoGetCity());
            }
        });
        mSearchReceivebtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                registerAdapter();
            }
        });

        mAutoReceivebtn.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    mAutoReceivebtn.setTextColor(mActivity.getResources().getColor(R.color.white));
                } else {
                    mAutoReceivebtn.setTextColor(mActivity.getResources().getColor(R.color.dark));
                }
            }
        });

        mSearchReceivebtn.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    mSearchReceivebtn.setTextColor(mActivity.getResources().getColor(R.color.white));
                } else {
                    mSearchReceivebtn.setTextColor(mActivity.getResources().getColor(R.color.dark));
                }
            }
        });

    }

    /**
     * set adapter and the listener of listView.
     */
    public void registerAdapter() {

        String search = mCitySeachEdit.getText().toString();
        List<String> list = (mActivity.mLogic.citySearching(search));
        Log.d(TAG, ">>>>>>>list.size()>>>>>>>" + list.size());
        if (list.size() == 0) {
            Utils.showToast(mActivity, R.string.failed_find_city);
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains("[CityName]")) {
                list.remove(i);
                break;
            }
        }
        mSearchCityAdapter = new SearchCityAdapter(mActivity, list);
        mCityListView.setAdapter(mSearchCityAdapter);

        mCityListView.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("static-access")
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                mOnClickCity = (TextView) view.findViewById(R.id.city_setting_list_item);
                String onclickCity = (String) mOnClickCity.getText();
                mCurrntCity.setText(onclickCity);
                String citystr = mActivity.mLogic.citySearching(mCitySeachEdit.getText().toString()).get(position);
                for (int i = 0; i < mActivity.mLogic.mFullCitiesList.size(); i++) {
                    if (mActivity.mLogic.mFullCitiesList.get(i).contains(citystr)) {
                        citystr = mActivity.mLogic.mFullCitiesList.get(i);
                    }
                }
                Log.d(TAG, "<<<<onclickAllCity<<<<<<<" + citystr);
                SciflyStore.Global.putString(mActivity.getContentResolver(), Global.CITY_NAME, citystr);
                BackUpData.backupData("city", "my_city", citystr);
            }
        });
        mCityListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mKeyTv = (TextView) view.findViewById(R.id.city_setting_list_item);
                if (mOriginKeyTV == null) {
                    mOriginKeyTV = mKeyTv;
                }
                mOriginKeyTV.setTextColor(mOriginKeyTV.getResources().getColor(R.color.white));
                mKeyTv.setTextColor(view.getResources().getColor(R.color.green));
                mOriginKeyTV = mKeyTv;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

}
