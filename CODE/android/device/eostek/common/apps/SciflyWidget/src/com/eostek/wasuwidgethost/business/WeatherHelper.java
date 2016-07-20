
package com.eostek.wasuwidgethost.business;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Xml;

import com.eostek.scifly.widget.R;
import com.eostek.wasuwidgethost.model.WeatherItem;

/**
 * projectName： WasuWidgetHost.
 * moduleName： WeatherHelper.java
 *
 */
public class WeatherHelper {
    private static final String TAG = "WeatherHelper";

    private static final String HTTP_SINA_URL = "http://php.weather.sina.com.cn/xml.php";

    private static final String HTTP_PASSWORD = "DJOYnieT8234jlsK";

    private static final String HTTP_ENCODE = "gb2312";

    private static final String DEFAULT_CITY = "深圳,深圳";

    private SharedPreferences mSharedPreferences;

    private Context mContext;

    private String condition = "";

    /**
     * construct method.
     * @param context
     */
    public WeatherHelper(Context context) {
        this.mContext = context;
    }

    /**
     * get current city info.
     * @return String current city.
     */
    public final String getCurCity() {
        String whichCity = SciflyStore.Global.getString(mContext.getContentResolver(), Global.CITY_NAME);
        if (whichCity == null || whichCity.equals("")) {
            Log.v(TAG, "Global.CITY_NAME is null");
            whichCity = mContext.getString(R.string.default_show_city) + ",深圳";
        }
        String[] citys = whichCity.split(",");
        if (citys.length < 3) {
            return mContext.getString(R.string.default_show_city);
        }
        Log.v(TAG, "whichCity : " + whichCity);
        String country = mContext.getResources().getConfiguration().locale.getCountry();
        Log.v(TAG, "country:" + country);
        if ("US".equals(country)) {
            whichCity = citys[0];
        } else if ("CN".equals(country)) {
            whichCity = citys[1];
        } else if ("TW".equals(country)) {
            whichCity = citys[2];
        } else {
            whichCity = citys[1];
        }
        return whichCity;
    }
    /**
     * @param whichDay
     * @return get the object of the current weather.
     */
    public final WeatherItem getCurrentWeather(int whichDay) {
        String whichCity = SciflyStore.Global.getString(mContext.getContentResolver(), Global.CITY_NAME);
        if (whichCity == null || whichCity.equals("")) {
            Log.e(TAG, "Global.CITY_NAME is null");
            whichCity = "Shenzhen,深圳,深圳";
        }
        String[] citys = whichCity.split(",");
        if (citys.length < 3) {
            Log.e(TAG, "get Global.CITY_NAME is error");
            return null;
        }
        Log.v(TAG, "whichCity = " + whichCity);
        WeatherItem weather = null;
        try {
            String c = java.net.URLEncoder.encode(citys[1], HTTP_ENCODE);
            String url = HTTP_SINA_URL + "?city=" + c + "&password=" + HTTP_PASSWORD + "&day=" + whichDay;
            HttpGet httpGet = new HttpGet(url);
            HttpClient hc = new DefaultHttpClient();
            hc.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5 * 1000);
            // set read data timeout
            hc.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 10 * 1000);
            HttpResponse ht = hc.execute(httpGet);
            if (ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity he = ht.getEntity();
                InputStream is = he.getContent();
                weather = parse(is, whichDay);
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weather;
    }

    /**
     * parse the InputStream.
     * 
     * @param is
     * @param day
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private WeatherItem parse(InputStream is, int day) throws IOException, XmlPullParserException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream=null");
        }
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        WeatherItem weather = null;

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("Weather")) {
                        weather = new WeatherItem();
                        weather.setDay(day);
                    } else if (parser.getName().equals("city")) {
                        parser.next();
                        // weather.city = parser.getText();
                    } else if (parser.getName().equals("status1")) {
                        parser.next();
                        weather.setStatus1(parser.getText());
                    } else if (parser.getName().equals("status2")) {
                        parser.next();
                        weather.setStatus2(parser.getText());
                    } else if (parser.getName().equals("direction1")) {
                        parser.next();
                        weather.setDirection1(parser.getText());
                    } else if (parser.getName().equals("direction2")) {
                        parser.next();
                        weather.setDirection2(parser.getText());
                    } else if (parser.getName().equals("power1")) {
                        parser.next();
                        weather.setPower1(parser.getText());
                    } else if (parser.getName().equals("power2")) {
                        parser.next();
                        weather.setPower2(parser.getText());
                    } else if (parser.getName().equals("temperature1")) {
                        parser.next();
                        weather.setTemperature1(parser.getText());
                    } else if (parser.getName().equals("temperature2")) {
                        parser.next();
                        weather.setTemperature2(parser.getText());
                    } else if (parser.getName().equals("tgd1")) {
                        parser.next();
                        weather.setTgd1(parser.getText());
                    } else if (parser.getName().equals("tgd2")) {
                        parser.next();
                        weather.setTgd2(parser.getText());
                    } else if (parser.getName().equals("zwx_l")) {
                        parser.next();
                        weather.setZwxl(parser.getText());
                    } else if (parser.getName().equals("chy_l")) {
                        parser.next();
                        weather.setChyl(parser.getText());
                    } else if (parser.getName().equals("pollution_l")) {
                        parser.next();
                        weather.setPollutionl(parser.getText());
                    } else if (parser.getName().equals("yd_l")) {
                        parser.next();
                        weather.setYdl(parser.getText());
                    } else if (parser.getName().equals("savedate_weather")) {
                        parser.next();
                        weather.setSavedateWeather(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return weather;
    }

    /**
     * get show city.
     * 
     * @return The name of city to show
     */
    public final String getShowCity() {
        String city = null;
        mSharedPreferences = mContext.getSharedPreferences("weatherPreference", Context.MODE_PRIVATE);
        city = mSharedPreferences.getString("show_city", mContext.getString(R.string.default_show_city));
        Log.d(TAG, "show_city=======================" + city);
        return city;
    }

    /**
     * get location city.
     * 
     * @return The name of location city
     */
    public final String getLocationCity() {
        String city = null;
        mSharedPreferences = mContext.getSharedPreferences("weatherPreference", Context.MODE_PRIVATE);
        city = mSharedPreferences.getString("weather_city", null);
        Log.d(TAG, "weather_city=======================" + city);
        if (city != null && !"".equals(city)) {
            return city;
        }
        if ((city == null || "".equals(city)) && !isNetworkConnected(mContext)) {
            city = getCurrentCity();
            if (city == null) {
                return DEFAULT_CITY;
            } else {
                return city;
            }
        }
        return DEFAULT_CITY;
    }

    /**
     * Get the current city by IP.
     * 
     * @return The name of the current city
     */
    private String getCurrentCity() {
        String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php";
        String mCurrentCity = null;
        HttpGet httpGet = new HttpGet(url);
        HttpClient hc = new DefaultHttpClient();
        hc.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5 * 1000);
        // set read data timeout
        hc.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 10 * 1000);
        HttpResponse httpResponse;
        try {
            httpResponse = hc.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity()).trim();
                // parse the result to get the city name
                String[] results = result.split("\t");
                if (results.length >= 5) {
                    mCurrentCity = results[5];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCurrentCity = null;
        }
        return mCurrentCity;
    }

    /**
     * By weather conditions to select the picture.
     * 
     * @param status
     * @return get icon id.
     */
    public final int getIcon(String status) {
        condition = mContext.getString(R.string.sun);
        int id = R.drawable.ic_weather_clear_day_l;
        if (status.equals("晴")) {
            condition = mContext.getString(R.string.sun);
            id = R.drawable.ic_weather_clear_day_l;
        } else if (status.equals("雾") || status.equals("霾")) {
            condition = mContext.getString(R.string.fog);
            id = R.drawable.ic_weather_fog_l;
        } else if (status.equals("阴") || status.equals("多云")) {
            condition = mContext.getString(R.string.cloudy);
            id = R.drawable.ic_weather_cloudy_l;
        } else if (status.equals("小到中雨") || status.equals("小雨") || status.equals("中雨") || status.equals("大雨") || status.equals("暴雨")) {
            condition = mContext.getString(R.string.rain);
            id = R.drawable.ic_weather_heavy_rain_l;
        } else if (status.equals("小雪") || status.equals("中雪") || status.equals("大雪") || status.equals("暴雪")) {
            condition = mContext.getString(R.string.snow);
            id = R.drawable.ic_weather_flurries_l;
        } else if (status.equals("雨夹雪")) {
            condition = mContext.getString(R.string.rain_snow);
            id = R.drawable.ic_weather_snow_rain_xl;
        } else if (status.equals("阵雨")) {
            condition = mContext.getString(R.string.shower);
            id = R.drawable.ic_weather_chance_of_rain_l;
        } else if (status.equals("阵雪")) {
            condition = mContext.getString(R.string.snow_shower);
            id = R.drawable.ic_weather_chance_snow_l;
        } else if (status.equals("雷阵雨") || status.equals("雷阵雪") || status.equals("雷雨")) {
            condition = mContext.getString(R.string.thunder_shower);
            id = R.drawable.ic_weather_chance_storm_l;
        }
        return id;
    }

    /**
     * return true if the network connected, false otherwise. 
     * @param context
     * @return boolean
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public final String getCondition() {
        return condition;
    }

}
