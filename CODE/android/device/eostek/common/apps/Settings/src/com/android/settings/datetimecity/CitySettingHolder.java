
package com.android.settings.datetimecity;

import java.util.List;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;

import com.android.settings.R;
import com.android.settings.userbackup.BackUpData;
import com.android.settings.util.Utils;
import com.android.settings.widget.TitleWidget;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.Toast;

public class CitySettingHolder {

    private String TAG = "CITY";

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

    private String cityAll;

    public CitySettingHolder(CitySettingActivity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    public void findview() {
        mTitleWidget = (TitleWidget) mActivity.findViewById(R.id.set_city_title);
        mCurrntCity = (TextView) mActivity.findViewById(R.id.current_city);
        mAutoReceivebtn = (Button) mActivity.findViewById(R.id.auto_receive);
        mSearchReceivebtn = (Button) mActivity.findViewById(R.id.search);
        mCitySeachEdit = (EditText) mActivity.findViewById(R.id.edittext);
        mCityListView = (ListView) mActivity.findViewById(R.id.city_setting_list);
    }

    public void initData() {
        mTitleWidget.setMainTitleText(mActivity.getResources().getString(R.string.setting));
        mTitleWidget.setFirstSubTitleText(mActivity.getResources().getString(R.string.area_time), false);
        mTitleWidget.setSecondSubTitleText(mActivity.getResources().getString(R.string.set_city));
        mCurrntCity.setText(mActivity.mLogic.getCurrentCity());
    }

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
                	mAutoReceivebtn.setTextColor(Color.rgb(255, 255, 255));
                } else {
                	mAutoReceivebtn.setTextColor(Color.rgb(0, 0, 0));
                }
            }
        });
        
        mSearchReceivebtn.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                	mSearchReceivebtn.setTextColor(Color.rgb(255, 255, 255));
                } else {
                	mSearchReceivebtn.setTextColor(Color.rgb(0, 0, 0));
                }
            }
        });

    }

    public void registerAdapter() {

        String search = mCitySeachEdit.getText().toString();
        List list = (mActivity.mLogic.citySearching(search));
        list.clear();
        mSearchCityAdapter = new SearchCityAdapter(mActivity, list);
        mCityListView.setAdapter(mSearchCityAdapter);
        list = (mActivity.mLogic.citySearching(search));
        Log.d(TAG, ">>>>>>>list.size()>>>>>>>" + list.size());
        if (list.size() == 0) {
            Log.d(TAG, ">>>>>>>lssssssssssss>>>>>>>" + list.size());
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.failed_find_city), Toast.LENGTH_SHORT)
                    .show();
        }
        mSearchCityAdapter = new SearchCityAdapter(mActivity, list);
        mCityListView.setAdapter(mSearchCityAdapter);

        mCityListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                mOnClickCity = (TextView) view.findViewById(R.id.city_setting_list_item);
                String onclickCity = (String) mOnClickCity.getText();
                mCurrntCity.setText(onclickCity);
                Log.d(TAG,
                        "<<<<onclickAllCity<<<<<<<" + mActivity.mLogic.mCityListZHCN.get(position) + ","
                                + mActivity.mLogic.mCityListEN.get(position) + ","
                                + mActivity.mLogic.mCityListZHTW.get(position));
                SharedPreferences mySharedPreferences = mActivity.getSharedPreferences("test", mActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("city", onclickCity);
                editor.commit();
                String zhCNCity = mActivity.mLogic.mCityListZHCN.get(position).toString();
                String enUSCity = mActivity.mLogic.mCityListEN.get(position).toString();
                String zhTWCity = mActivity.mLogic.mCityListZHTW.get(position).toString();
                SciflyStore.Global.putString(mActivity.getContentResolver(), Global.CITY_NAME, zhCNCity + ","
                        + enUSCity + "," + zhTWCity);
                BackUpData.backupData("city", "my_city", zhCNCity + "," + enUSCity + "," + zhTWCity);
            }
        });
        mCityListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mKeyTv = (TextView) view.findViewById(R.id.city_setting_list_item);
                if (mOriginKeyTV == null) {
                    mOriginKeyTV = mKeyTv;
                }
                mOriginKeyTV.setTextColor(mOriginKeyTV.getResources().getColor(R.color.white));
//                if(mAutoReceivebtn.hasFocus()||mSearchReceivebtn.hasFocus()||mCityListView.hasFocus()){
//                	mKeyTv.setTextColor(view.getResources().getColor(R.color.white));
//                }else{
                	mKeyTv.setTextColor(view.getResources().getColor(R.color.green));
//                }
                mOriginKeyTV = mKeyTv;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
 
    }

}
