
package com.eostek.tv.launcher.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.model.WeatherItem;

/**
 * projectName： WasuWidgetHost. moduleName： WeatherHelper.java
 */
public class WeatherHelper {
    private static final String TAG = "WeatherHelper";

    private static final String HTTP_SINA_URL = "http://php.weather.sina.com.cn/xml.php";

    private static final String HTTP_PASSWORD = "DJOYnieT8234jlsK";

    private static final String HTTP_ENCODE = "gb2312";

    private static final String DEFAULT_CITY = "台北,Taipei,臺北";

    private Context mContext;

    private String condition = "";

    /**
     * construct method.
     * 
     * @param context
     */
    public WeatherHelper(Context context) {
        this.mContext = context;
    }

    /**
     * ss get current city info.
     * 
     * @return String current city.
     */
    public final String getCurCity() {
        String whichCity = SciflyStore.Global.getString(mContext.getContentResolver(), Global.CITY_NAME);
        if (whichCity == null || whichCity.equals("")) {
            Log.v(TAG, "Global.CITY_NAME is null");
            whichCity = DEFAULT_CITY;
        }
        String[] citys = whichCity.split(",");
        if (citys.length < 3) {
            return mContext.getString(R.string.default_show_city);
        }
        Log.v(TAG, "whichCity : " + whichCity);
        String country = mContext.getResources().getConfiguration().locale.getCountry();
        Log.v(TAG, "country:" + country);
        if ("CN".equals(country)) {
            whichCity = citys[0];
        } else if ("US".equals(country)) {
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
            whichCity = DEFAULT_CITY;
        }
        String[] citys = whichCity.split(",");
        if (citys.length < 3) {
            Log.e(TAG, "get Global.CITY_NAME is error");
            return null;
        }
        Log.v(TAG, "whichCity = " + whichCity);
        WeatherItem weather = null;
        try {
            String c = java.net.URLEncoder.encode(citys[0], HTTP_ENCODE);
            String url = HTTP_SINA_URL + "?city=" + c + "&password=" + HTTP_PASSWORD + "&day=" + whichDay;
            HttpGet httpGet = new HttpGet(url);
            HttpClient hc = new DefaultHttpClient();
            hc.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5 * 1000);
            // set read data timeout
            hc.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 10 * 1000);
            HttpResponse ht = hc.execute(httpGet);

            int code = ht.getStatusLine().getStatusCode();

            if (code == HttpStatus.SC_OK) {
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
                    String name = parser.getName();
                    if (name == null || name.equals("")) {
                        continue;
                    }

                    if (name.equals("Profiles")) {
                        parser.next();
                    } else if (name.equals("Weather")) {
                        weather = new WeatherItem();
                        weather.setDay(day);
                        parser.next();
                    } else if (name.equals("city")) {
                        parser.next();
                        weather.setCity(parser.getText());
                    } else if (name.equals("status1")) {
                        parser.next();
                        weather.setStatus1(parser.getText());
                    } else if (name.equals("status2")) {
                        parser.next();
                        weather.setStatus2(parser.getText());
                    } else if (name.equals("direction1")) {
                        parser.next();
                        weather.setDirection1(parser.getText());
                    } else if (name.equals("direction2")) {
                        parser.next();
                        weather.setDirection2(parser.getText());
                    } else if (name.equals("power1")) {
                        parser.next();
                        weather.setPower1(parser.getText());
                    } else if (name.equals("power2")) {
                        parser.next();
                        weather.setPower2(parser.getText());
                    } else if (name.equals("temperature1")) {
                        parser.next();
                        weather.setTemperature1(parser.getText());
                    } else if (name.equals("temperature2")) {
                        parser.next();
                        weather.setTemperature2(parser.getText());
                    } else if (name.equals("tgd1")) {
                        parser.next();
                        weather.setTgd1(parser.getText());
                    } else if (name.equals("tgd2")) {
                        parser.next();
                        weather.setTgd2(parser.getText());
                    } else if (name.equals("zwx_l")) {
                        parser.next();
                        weather.setZwxl(parser.getText());
                    } else if (name.equals("chy_l")) {
                        parser.next();
                        weather.setChyl(parser.getText());
                    } else if (name.equals("pollution_l")) {
                        parser.next();
                        weather.setPollutionl(parser.getText());
                    } else if (name.equals("yd_l")) {
                        parser.next();
                        weather.setYdl(parser.getText());
                    } else if (name.equals("savedate_weather")) {
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
     * By weather conditions to select the picture.
     * 
     * @param status
     * @return get icon id.
     */
    public final String getIcon(String status) {
        // int id = R.drawable.ic_weather_clear_day_l;
        // if (status.equals("晴")) {
        // id = R.drawable.ic_weather_clear_day_l;
        // } else if (status.equals("雾") || status.equals("霾")) {
        // id = R.drawable.ic_weather_fog_l;
        // } else if (status.equals("阴") || status.equals("多云")) {
        // id = R.drawable.ic_weather_cloudy_l;
        // } else if (status.equals("小到中雨") || status.equals("小雨") ||
        // status.equals("中雨") || status.equals("大雨")
        // || status.equals("暴雨")) {
        // id = R.drawable.ic_weather_heavy_rain_l;
        // } else if (status.equals("小雪") || status.equals("中雪") ||
        // status.equals("大雪") || status.equals("暴雪")) {
        // id = R.drawable.ic_weather_flurries_l;
        // } else if (status.equals("雨夹雪")) {
        // id = R.drawable.ic_weather_snow_rain_xl;
        // } else if (status.equals("阵雨")) {
        // id = R.drawable.ic_weather_chance_of_rain_l;
        // } else if (status.equals("阵雪")) {
        // id = R.drawable.ic_weather_chance_snow_l;
        // } else if (status.equals("雷阵雨") || status.equals("雷阵雪") ||
        // status.equals("雷雨")) {
        // id = R.drawable.ic_weather_chance_storm_l;
        // }
        // return id;
        String id = "ic_weather_clear_day_l";
        if (status.equals("晴")) {
            id = "ic_weather_clear_day_l";
        } else if (status.equals("雾") || status.equals("霾")) {
            id = "ic_weather_fog_l";
        } else if (status.equals("阴") || status.equals("多云")) {
            id = "ic_weather_cloudy_l";
        } else if (status.equals("小到中雨") || status.equals("小雨") || status.equals("中雨") || status.equals("大雨")
                || status.equals("暴雨")) {
            id = "ic_weather_heavy_rain_l";
        } else if (status.equals("小雪") || status.equals("中雪") || status.equals("大雪") || status.equals("暴雪")) {
            id = "ic_weather_flurries_l";
        } else if (status.equals("雨夹雪")) {
            id = "ic_weather_snow_rain_xl";
        } else if (status.equals("阵雨")) {
            id = "ic_weather_chance_of_rain_l";
        } else if (status.equals("阵雪")) {
            id = "ic_weather_chance_snow_l";
        } else if (status.equals("雷阵雨") || status.equals("雷阵雪") || status.equals("雷雨")) {
            id = "ic_weather_chance_storm_l";
        }
        return id;
    }

    public WeatherItem getWeather(int whichDay) {
        String whichCity = SciflyStore.Global.getString(mContext.getContentResolver(), Global.CITY_NAME);
        if (whichCity == null || whichCity.equals("")) {
            Log.e(TAG, "Global.CITY_NAME is null");
            whichCity = DEFAULT_CITY;
        }
        String[] citys = whichCity.split(",");
        if (citys.length < 3) {
            Log.e(TAG, "get Global.CITY_NAME is error");
            return null;
        }
        Log.v(TAG, "whichCity = " + whichCity);

        WeatherItem weather = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            String param = URLEncoder.encode("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\""+ citys[1] + "\")","utf-8");
            String url = "https://query.yahooapis.com/v1/public/yql" + "?q=" + param + "&format=json&u=c";
            Log.d(TAG, "weather url=" + url);
            URL urlLoc = new URL(url);
            conn = (HttpURLConnection) urlLoc.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(30 *  1000);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                Log.d(TAG, sb.toString());
                weather = parseWeatherInfo(sb.toString(), whichDay);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weather;
    }

    public WeatherItem parseWeatherInfo(String weatherInfo, int whichDay) {
        WeatherItem weatherItem = null;
        if (!TextUtils.isEmpty(weatherInfo)) {
            try {
                JSONObject json = new JSONObject(weatherInfo);
                JSONArray array = json.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast");
                JSONObject chiObject = array.getJSONObject(whichDay);
                weatherItem = new WeatherItem();
                weatherItem.setTemperature1(C2F(chiObject.getString("high")));
                weatherItem.setTemperature2(C2F(chiObject.getString("low")));
                weatherItem.setStatus1(chiObject.getString("code"));
                weatherItem.setStatus2(chiObject.getString("code"));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return weatherItem;
    }

    public String C2F(String fahrenheit) {
        int f = Integer.parseInt(fahrenheit);
        int c = (int) Math.round((f - 32) / 1.8);
        return "" + c;
    }

    public final String getNewIcon(String status) {
        String id = "ic_weather_clear_day_l";
        int s = Integer.parseInt(status);
        if ((s >= 31 &&s <= 34) || s == 36) {
            id = "ic_weather_clear_day_l";
        } else if (s >= 19 && s <= 30) {
            id = "ic_weather_fog_l";
        } else if ((s >= 0 && s <= 2) || s == 44) {
            id = "ic_weather_cloudy_l";
        } else if ((s >= 8 && s <= 10) || s == 44) {
            id = "ic_weather_heavy_rain_l";
        } else if (s == 13 || (s >= 15 && s <= 17) || s == 41 || s == 43 || s == 46) {
            id = "ic_weather_flurries_l";
        } else if ((s >= 5 && s <= 7) || s == 18 || s == 42) {
            id = "ic_weather_snow_rain_xl";
        } else if (s == 11 || s == 12 || s == 37) {
            id = "ic_weather_chance_of_rain_l";
        } else if (s == 14 || s == 40) {
            id = "ic_weather_chance_snow_l";
        } else if (s == 3 || s == 4 || s == 38 || s == 39 || s == 45 || s == 47) {
            id = "ic_weather_chance_storm_l";
        }
        return id;
    }
}
