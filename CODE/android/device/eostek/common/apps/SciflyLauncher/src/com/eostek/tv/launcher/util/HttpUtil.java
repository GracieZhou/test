
package com.eostek.tv.launcher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.util.Log;

public class HttpUtil {
    public static final String POST = "POST";

    public static final String GET = "GET";

    private String url;

    private Map<String, List<String>> parameterMap; // 参数列表

    private Map<String, String> heardMap; // 参数列表

    private String requestMethod = GET; // 请求方式

    private String encode = "UTF-8";

    private String contentType = "text/html; charset=UTF-8";

    private int connectTimeout = 30000;

    private int readTimeout = 30000;

    private HttpURLConnection conn;

    private String eTag;

    public String geteTag() {
        return eTag;
    }

    /**
     * 设置url 和 参数
     * 
     * @param urlStr
     * @param parameter
     */
    public HttpUtil(String urlStr, Map<String, List<String>> parameter) {
        parameterMap = parameter;
        this.url = urlStr;
    }

    /**
     * 设置url 和 参数
     * 
     * @param urlStr
     * @param parameter
     */
    public HttpUtil(String urlStr, String parameter) {
        addParameter("", parameter);
        this.url = urlStr;
    }

    /**
     * 设置url
     * 
     * @param urlStr
     */
    public HttpUtil(String urlStr) {
        this.url = urlStr;
    }

    /**
     * 获取网页内容
     * 
     * @throws IOException
     * @return The connect input stream string
     */
    public String getUrlContent() throws IOException {
        BufferedReader breader = null;
        try {
            InputStream in = getUrlInputStream();
            if ("gzip".equals(conn.getContentEncoding())) {
                in = new GZIPInputStream(in);
            }
            if (in == null) {
                return "";
            }
            breader = new BufferedReader(new InputStreamReader(in, encode));
            StringBuilder content = new StringBuilder();
            String str;
            while ((str = breader.readLine()) != null) {
                content.append(str).append("\n");
            }
            if (content.length() > 0) {
                content.deleteCharAt(content.length() - 1);
            }
            return content.toString();
        } finally {
            if (breader != null) {
                try {
                    breader.close();
                } catch (IOException ie) {
                    Log.e("HttpUtil", "IOException when getUrlContent");
                }
            }
        }
    }

    /**
     * 下载文件到本地
     * 
     * @param filename 本地文件名
     * @throws IOException 各种异常
     */
    public void download(String filename) throws IOException {
        getHttpURLConnection(); // 初始conn
        OutputStream os = null;
        InputStream is = null;
        try {
            is = getUrlInputStream();
            if (is == null) {
                throw new IOException("UrlInputStream is null");
            }
            File file = new File(filename);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            byte[] bs = new byte[1024]; // 1K的数据缓冲
            os = new FileOutputStream(filename);
            int len;
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ie) {
                    Log.e("HttpUtil", "IOException when download");
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ie) {
                    Log.e("HttpUtil", "IOException when download");
                }
            }
        }
    }

    /**
     * add head property for request
     * 
     * @param name
     * @param value
     */
    public void addHeardMap(String name, String value) {
        if (heardMap == null) {
            heardMap = new HashMap<String, String>();
        }
        heardMap.put(name, value);
    }

    /**
     * add head property for request
     * 
     * @param heads
     */
    public void addHeadMaps(HashMap<String, String> heads) {
        if (heads == null) {
            return;
        }
        if (heardMap == null) {
            heardMap = new HashMap<String, String>();
        }
        this.heardMap = heads;
    }

    /**
     * 添加参数
     * 
     * @param name
     * @param value
     */
    public void addParameter(String name, String value) {
        List<String> values = new ArrayList<String>();
        values.add(value);
        addParameter(name, values);
    }

    /**
     * 添加参数
     * 
     * @param name
     * @param values
     */
    public void addParameter(String name, List<String> values) {
        if (parameterMap == null) {
            parameterMap = new HashMap<String, List<String>>();
        }
        parameterMap.put(name, values);
    }

    /**
     * 添加参数
     * 
     * @param param
     */
    public void addParameterMap(Map<String, List<String>> param) {
        if (param == null) {
            this.parameterMap = param;
        } else {
            this.parameterMap.putAll(param);
        }
    }

    /**
     * 获取参数
     * 
     * @return The param map
     */
    public Map<String, List<String>> getParameterMap() {
        return parameterMap;
    }

    /**
     * 获取请求属性
     * 
     * @return The request property map
     */
    public Map<String, List<String>> getRequestProperty() {
        return conn.getRequestProperties();
    }

    /**
     * 获取url的流
     * 
     * @return
     * @throws IOException
     */
    private InputStream getUrlInputStream() throws IOException {
        getHttpURLConnection(); // 初始conn

        if (heardMap != null && heardMap.size() > 0) {
            Iterator<String> keys = heardMap.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                conn.setRequestProperty(key, heardMap.get(key));
            }
            heardMap.clear();
        }
        // 设置http 头属性
        conn.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");
        conn.setRequestProperty("Accept", "application/octet-stream");

        // 设置超时
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setAllowUserInteraction(false);

        if (POST.equals(requestMethod) && parameterMap != null && parameterMap.size() > 0) {
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);
            byte[] post = toString(parameterMap).getBytes();
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Content-Length", String.valueOf(post.length));
            conn.getOutputStream().write(post);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
        }
        conn.connect();

        eTag = conn.getHeaderField("Etag");

        try {
            return conn.getInputStream();
        } catch (java.io.IOException e) {
            return conn.getErrorStream();
        }
    }

    /**
     * 获取HttpURLConnection
     * 
     * @throws IOException
     * @return HttpURLConnection
     */
    private HttpURLConnection getHttpURLConnection() throws IOException {
        if (conn == null) {
            String tempUrl = url;
            if (GET.equals(requestMethod) && parameterMap != null && parameterMap.size() > 0) {
                tempUrl += tempUrl.indexOf("?") < 0 ? "?" : "&";
                tempUrl += toString(parameterMap);
            }
            Log.d("HttpUtil", tempUrl);
            conn = (HttpURLConnection) (new URL(tempUrl)).openConnection();
        }
        return conn;
    }

    /**
     * list 格式化成字符串
     * 
     * @param list
     * @return
     */
    private String toString(List<String> list) {
        if (list != null && list.size() > 0) {
            StringBuilder str = new StringBuilder();
            for (String s : list) {
                str.append(s).append("; ");
            }
            str.delete(str.length() - 2, str.length());
            return str.toString();
        }
        return "";
    }

    /**
     * map 格式化成字符串
     * 
     * @param map
     * @return
     */
    private String toString(Map<String, List<String>> map) {
        if (map != null) {
            StringBuilder str = new StringBuilder();

            Iterator<String> keys = map.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    if (key != null && !key.equals("")) {
                        str.append(key).append("=").append(java.net.URLEncoder.encode(toString(map.get(key)), encode))
                                .append("&");
                    } else {
                        str.append(toString(map.get(key))).append("&");
                    }
                } catch (UnsupportedEncodingException e) {
                    str.append(key).append("=").append(toString(map.get(key))).append("&");
                }
            }
            str.deleteCharAt(str.length() - 1);
            return str.toString();
        }
        return "";
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String request) {
        this.requestMethod = request;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String code) {
        this.encode = code;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readtimeout) {
        this.readTimeout = readtimeout;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contType) {
        this.contentType = contType;
    }
}
