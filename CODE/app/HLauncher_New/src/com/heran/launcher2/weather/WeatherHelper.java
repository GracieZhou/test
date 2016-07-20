
package com.heran.launcher2.weather;

import java.util.HashMap;
import java.util.Map;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;

import android.content.Context;
import android.util.Log;
import scifly.provider.SciflyStore.Global;

public class WeatherHelper {

    private Context context;

    public WeatherHelper(Context context) {
        this.context = context;
    }

    private static String getCurrentCityName(Context mContext) {
        String[] citys = mContext.getResources().getStringArray(R.array.city_array);
        String cityName = citys[0];

        String city[] = Global.getString(mContext.getContentResolver(), Global.CITY_NAME).split(",");
        if (city.length > 2) {
            Constants.city = city[2];
            for (int i = 0; i < citys.length; i++) {
                if (Constants.city.equals(citys[i].substring(0, 2))) {
                    Log.d("city", "city_num[" + i + "] : " + citys[i].substring(0, 2));
                    cityName = citys[i];
                }
            }
        }
        return cityName;
    }

    public static Map<String, Object> getCurrentCity(int chooice) {
        Map<String, Object> map = new HashMap<String, Object>();
        int cityIndex = 0;
        String[] citys;
        if (chooice == 1) {

            citys = HomeApplication.getInstance().getResources().getStringArray(R.array.city_array);
        } else {
            citys = HomeApplication.getInstance().getResources().getStringArray(R.array.city_array1);
        }

        String city[] = Global.getString(HomeApplication.getInstance().getContentResolver(), Global.CITY_NAME)
                .split(",");
        if (city.length > 2) {
            Constants.city = city[2];
            for (int i = 0; i < citys.length; i++) {
                if (Constants.city.equals(citys[i].substring(0, 2))) {
                    Log.d("city", "city_num[" + i + "] : " + citys[i].substring(0, 2));
                    cityIndex = i;
                }
            }
        }
        map.put("cityName", citys[cityIndex]);
        map.put("cityIndex", cityIndex);
        return map;
    }
}
