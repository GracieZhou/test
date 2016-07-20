package com.eostek.wasuwidgethost.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * projectName： WasuWidgetHost.
 * moduleName： HttpUtil.java
 *
 */
public class HttpUtil {
    private static final String  TAG = "HttpUtil";
    public static final String POST = "POST";
    public static final String GET = "GET"; 
    private String mUrl;
    private Map<String, List<String>> mParameterMap;
    private Map<String, String> heardMap;
    private String mRequestMethod = GET;
    private String mEncode = "UTF-8";
    private String mContentType = "text/html; charset=UTF-8";

	private int mConnectTimeout = 30000;
	private int mReadTimeout = 30000;
	private HttpURLConnection conn;
	
    /** 默认编码方式 -UTF8 */
    private static final String DEFAULT_ENCODE = "UTF-8";
	
	/**
	 * set url and param.
	 * 
	 * @param url
	 * @param parameter
	 */
	public HttpUtil(String url, Map<String, List<String>> parameter) {
		mParameterMap = parameter;
		this.mUrl = url;
	}

	/**
	 * set url and param.
	 * 
	 * @param url
	 * @param parameter
	 */
	public HttpUtil(String url, String parameter) {
		addParameter("", parameter);
		this.mUrl = url;
	}

	/**
	 * set url and param.
	 * 
	 * @param url
	 */
	public HttpUtil(String url) {
		this.mUrl = url;
	}

	/**
	 * get the web content.
	 * 
	 * @return String
	 * @throws IOException
	 * @throws IOException
	 * @throws IOException
	 */
	public final String getUrlContent() throws IOException {
		BufferedReader breader = null;
		try {
			InputStream in = getUrlInputStream();
			if ("gzip".equals(conn.getContentEncoding())) {
				in = new GZIPInputStream(in);
			}
			if (in == null) {
				return "";
			}
			breader = new BufferedReader(new InputStreamReader(in, mEncode));
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
                    ie.printStackTrace();
                }
			}
		}
	}

	/**
	 * download the file into the local.
	 * 
	 * @param filename 本地文件名
	 */
	public final void download(String filename) throws IOException {
		getHttpURLConnection();
		OutputStream os = null;
		InputStream is = null;
		try {
			is = getUrlInputStream();
			if (is == null) {
			    throw new IOException("UrlInputStream is null");
			}
			File file = new File(filename);
			if (!file.getParentFile().exists()) {
				boolean mkdirs = file.getParentFile().mkdirs();
			}
			byte[] bs = new byte[1024];
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
                    ie.printStackTrace();
                }
			}
			if (is != null) {
			    try {
                    is.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                } 
			}
		}
	}

	/**
	 * add value into the map.
	 * @param name
	 * @param value
	 */
	public final void addHeardMap(String name, String value) {
		if (heardMap == null) {
		    heardMap = new HashMap<String, String>();
		}
		heardMap.put(name, value);
	}

	/**
	 * add params.
	 * 
	 * @param name
	 * @param value
	 */
	public final void addParameter(String name, String value) {
		List<String> values = new ArrayList<String>();
		values.add(value);
		addParameter(name, values);
	}

	/**
	 * add params.
	 * 
	 * @param name
	 * @param values
	 */
	public final void addParameter(String name, List<String> values) {
		if (mParameterMap == null) {
		    mParameterMap = new HashMap<String, List<String>>();
		}
		mParameterMap.put(name, values);
	}

	/**
	 * add params.
	 * 
	 * @param parameterMap
	 */
	public final void addParameterMap(Map<String, List<String>> parameterMap) {
		if (this.mParameterMap == null) {
		    this.mParameterMap = parameterMap;
		} else {
		    this.mParameterMap.putAll(parameterMap);
		}
	}

	/**
	 * add params.
	 * 
	 * @return Map
	 */
	public final Map<String, List<String>> getParameterMap() {
		return mParameterMap;
	}

	/**
	 * get the request attribution.
	 * 
	 * @return Map
	 */
	public final Map<String, List<String>> getRequestProperty() {
		return conn.getRequestProperties();
	}

	/**
	 * get url stram.
	 * 
	 * @return InputStream
	 * @throws IOException
	 */
	public final InputStream getUrlInputStream() throws IOException {
		getHttpURLConnection(); // 初始conn

		if (heardMap != null && heardMap.size() > 0) {
			Iterator<String> keys = heardMap.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();

				conn.setRequestProperty(key, heardMap.get(key));
			}
			heardMap.clear();
		}
		// 设置超时
		conn.setConnectTimeout(mConnectTimeout);
		conn.setReadTimeout(mReadTimeout);
		conn.setAllowUserInteraction(false);

		if (POST.equals(mRequestMethod) && mParameterMap != null && mParameterMap.size() > 0) {
			conn.setRequestMethod(mRequestMethod);
			conn.setDoOutput(true);
			byte[] post = toString(mParameterMap).getBytes();
			conn.setRequestProperty("Content-Type", mContentType);
			conn.setRequestProperty("Content-Length", String.valueOf(post.length));
			conn.getOutputStream().write(post);
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
		}
		conn.connect();

		try {
			return conn.getInputStream();
		} catch (java.io.IOException e) {
			return conn.getErrorStream();
		}
	}

	/**
	 * get HttpURLConnection.
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private HttpURLConnection getHttpURLConnection() throws IOException {
		if (conn == null) {
			String tempUrl = mUrl;
			if (GET.equals(mRequestMethod) && mParameterMap != null && mParameterMap.size() > 0) {
			    if (tempUrl.indexOf("?") < 0) {
			        tempUrl += "?";
			    } else {
			        tempUrl += "&";
			    }
//				tempUrl += tempUrl.indexOf("?") < 0 ? "?" : "&";
				tempUrl += toString(mParameterMap);
			}
			Log.d("HttpUtil", tempUrl);
			conn = (HttpURLConnection) (new URL(tempUrl)).openConnection();
		}
		return conn;
	}

	/**
	 * transfer the List into String.
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
	 * transfer the Map into String.
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
						str.append(key).append("=").append(java.net.URLEncoder.encode(toString(map.get(key)), 
						        mEncode)).append("&");
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

	public final String getRequestMethod() {
		return mRequestMethod;
	}

	public final void setRequestMethod(String requestMethod) {
		this.mRequestMethod = requestMethod;
	}

	public final String getEncode() {
		return mEncode;
	}

	public final void setEncode(String encode) {
		this.mEncode = encode;
	}

	public final int getConnectTimeout() {
		return mConnectTimeout;
	}

	public final void setConnectTimeout(int connectTimeout) {
		this.mConnectTimeout = connectTimeout;
	}

	public final int getReadTimeout() {
		return mReadTimeout;
	}

	public final void setReadTimeout(int readTimeout) {
		this.mReadTimeout = readTimeout;
	}

	public final String getContentType() {
		return mContentType;
	}

	public final void setContentType(String contentType) {
		this.mContentType = contentType;
	}
	
	public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", (SocketFactory) sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
	
	public static String getUrlAsString(String url) throws Exception {
        return getUrlAsString(url, null, DEFAULT_ENCODE);
    }
	
	public static String getUrlAsString(String url, Map<String, String> params) throws Exception {
        return getUrlAsString(url, params, DEFAULT_ENCODE);
    }
	
	public static String getUrlAsString(String url, Map<String, String> params, String encode) throws Exception {
        // 开始时间
        long t1 = System.currentTimeMillis();
        // 获得HttpGet对象
        HttpGet httpGet = getHttpGet(url, params, encode);
        // 调试信息
        Log.d(TAG, "url:" + url);
        if (params != null && !params.isEmpty()) {
            Log.d(TAG, "params:" + params.toString());
        }

        Log.d(TAG, "encode:" + encode);
        // 发送请求
        String result = executeHttpRequest(httpGet, null);
        // 结束时间
        long t2 = System.currentTimeMillis();
        // 调试信息
        Log.d(TAG, "result:" + result);
        Log.d(TAG, "consume time:" + ((t2 - t1)));
        // 返回结果
        return result;
    }
	
	public static String postUrlAsString(String url) throws Exception {
        return postUrlAsString(url, null, null, null);
    }
	
	public static String postUrlAsString(String url, Map<String, String> params) throws Exception {
        return postUrlAsString(url, params, null, null);
    }
	
	public static String postUrlAsString(String url, Map<String, String> params, Map<String, String> reqHeader)
            throws Exception {
        return postUrlAsString(url, params, reqHeader, null);
    }
	
	public static String postUrlAsString(String url, Map<String, String> params, Map<String, String> reqHeader,
            String encode) throws Exception {
        // 开始时间
        long t1 = System.currentTimeMillis();
        // 获得HttpPost对象
        HttpPost httpPost = getHttpPost(url, params, encode);
        // 发送请求
        String result = executeHttpRequest(httpPost, reqHeader);
        // 结束时间
        long t2 = System.currentTimeMillis();
        // 调试信息
        Log.d(TAG, "url:" + url);
        if (params != null && !params.isEmpty()) {
            Log.d(TAG, "params:" + params.toString());
        }

        if (reqHeader != null && !reqHeader.isEmpty()) {
            Log.d(TAG, "reqHeader:" + reqHeader);
        }

        Log.d(TAG, "encode:" + encode);
        Log.d(TAG, "result:" + result);
        Log.d(TAG, "consume time:" + ((t2 - t1)));
        // 返回结果
        return result;
    }
	
	private static HttpGet getHttpGet(String url, Map<String, String> params, String encode) {
        StringBuffer buf = new StringBuffer(url);
        if (params != null) {
            // 地址增加?或者&
            String flag = (url.indexOf('?') == -1) ? "?" : "&";
            // 添加参数
            for (String name : params.keySet()) {
                buf.append(flag);
                buf.append(name);
                buf.append("=");
                try {
                    String param = params.get(name);
                    if (param == null) {
                        param = "";
                    }
                    buf.append(URLEncoder.encode(param, encode));
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "URLEncoder Error,encode=" + encode + ",param=" + params.get(name), e);
                }
                flag = "&";
            }
        }
        HttpGet httpGet = new HttpGet(buf.toString());
        return httpGet;
    }
	
	private static HttpPost getHttpPost(String url, Map<String, String> params, String encode) {
        HttpPost httpPost = new HttpPost(url);
        if (params != null) {
            List<NameValuePair> form = new ArrayList<NameValuePair>();
            for (String name : params.keySet()) {
                if (null != params.get(name)) {
                    form.add(new BasicNameValuePair(name, params.get(name)));
                }
            }
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, encode);

                httpPost.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "UrlEncodedFormEntity Error,encode=" + encode + ",form=" + form, e);
            }
        }
        return httpPost;
    }
	
	private static String executeHttpRequest(HttpUriRequest request, Map<String, String> reqHeader) throws Exception {
        HttpClient client = null;
        String result = null;
        try {
            URI uri = request.getURI();
            Log.e(TAG, " ---- request : " + request.toString());
            Log.e(TAG, " ---- uri : " + uri.toString());
            // 创建HttpClient对象
            if (uri.toString().indexOf("https:") == -1) {
                client = new DefaultHttpClient();
                Log.e(TAG, " ---- http ----");
            } else {
                client = (DefaultHttpClient) getNewHttpClient();
                Log.e(TAG, " ---- https ----");
            }

            // 设置连接超时时间
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            // 设置Socket超时时间
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
            // 设置请求头信息
            if (reqHeader != null) {
                for (String name : reqHeader.keySet()) {
                    request.addHeader(name, reqHeader.get(name));
                }
            }

            // 获得返回结果
            HttpResponse response = client.execute(request);
            // 如果成功
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
            }
            // 如果失败
            else {
                StringBuffer errorMsg = new StringBuffer();
                errorMsg.append("httpStatus:");
                errorMsg.append(response.getStatusLine().getStatusCode());
                errorMsg.append(response.getStatusLine().getReasonPhrase());
                errorMsg.append(", Header: ");
                Header[] headers = response.getAllHeaders();
                for (Header header : headers) {
                    errorMsg.append(header.getName());
                    errorMsg.append(":");
                    errorMsg.append(header.getValue());
                }
            }
        } catch (IllegalArgumentException ec) {
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
            // throw new Exception("http连接异常");
        } finally {
            try {
                client.getConnectionManager().shutdown();
            } catch (Exception e) {
                Log.e(TAG, "finally HttpClient shutdown error", e);
            }
        }
        return result;
    }
	
	public static String postBody(String urlPath, String json) throws Exception {

        HttpClient client = null;
        String result = null;

        try {
            if (urlPath.indexOf("https") == -1) {
                client = new DefaultHttpClient();
                Log.e(TAG, " ---- http ----");
            } else {
                client = (DefaultHttpClient) getNewHttpClient();
                Log.e(TAG, " ---- https ----");
            }
            
            // 设置连接超时时间
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            // 设置Socket超时时间
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
            
            HttpPost httppost = new HttpPost(urlPath);
            // 添加http头信息
            httppost.addHeader("accept", "*/*"); // 认证token
            httppost.addHeader("Content-Type", "application/json; encoding=utf-8");
            httppost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // http post的json数据格式： {"name": "your name","parentId":
            // "id_of_parent"}

            httppost.setEntity(new StringEntity(json));
            HttpResponse response;
            response = client.execute(httppost);
            // 检验状态码，如果成功接收数据
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
            } else {
                StringBuffer errorMsg = new StringBuffer();
                errorMsg.append("httpStatus:");
                errorMsg.append(response.getStatusLine().getStatusCode());
                errorMsg.append(response.getStatusLine().getReasonPhrase());
                errorMsg.append(", Header: ");
                Header[] headers = response.getAllHeaders();
                for (Header header : headers) {
                    errorMsg.append(header.getName());
                    errorMsg.append(":");
                    errorMsg.append(header.getValue());
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                client.getConnectionManager().shutdown();
            } catch (Exception e) {
                Log.e(TAG, "finally HttpClient shutdown error", e);
            }
        }
        return result;
    }
	public static boolean downloadFile(final String path, String url) throws IOException {
        Log.d(TAG, "downloadFile path :" + path);
        Log.d(TAG, "downloadFile url :" + url);
        HttpClient client = null;
        try {
            // 创建HttpClient对象
            if (url.indexOf("https") == -1) {
                client = new DefaultHttpClient();
                Log.e(TAG, " ---- http ----");
            } else {
                client = (DefaultHttpClient) getNewHttpClient();
                Log.e(TAG, " ---- https ----");
            }
            // 设置连接超时时间
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            // 设置Socket超时时间
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
            // 获得HttpGet对象
            HttpGet httpGet = getHttpGet(url, null, null);
            // 发送请求获得返回结果
            HttpResponse response = client.execute(httpGet);
            // 如果成功
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                byte[] result = EntityUtils.toByteArray(response.getEntity());
                BufferedOutputStream bw = null;
                try {
                    // 创建文件对象
                    File f = new File(path);
                    // 创建文件路径
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    // 写入文件
                    bw = new BufferedOutputStream(new FileOutputStream(path));
                    bw.write(result);
                    bw.close();
                    
                    Log.e(TAG, "下载二维码完成！");
                    return true;
                    
                } catch (Exception e) {
                } finally {
                    try {
                        if (bw != null)
                            bw.close();
                    } catch (Exception e) {
                        Log.e(TAG, "finally BufferedOutputStream shutdown close", e);
                    }
                }
            }
            // 如果失败
        } catch (ClientProtocolException e) {
            Log.e(TAG, "下载文件保存到本地,http连接异常,path=" + path + ",url=" + url, e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "下载文件保存到本地,文件操作异常,path=" + path + ",url=" + url, e);
            throw e;
        } finally {
            try {
                client.getConnectionManager().shutdown();
            } catch (Exception e) {
                Log.e(TAG, "finally HttpClient shutdown error", e);
            }
        }
        return false;
    }
}

class SSLSocketFactoryEx extends SSLSocketFactory {

    private SSLContext sslContext = SSLContext.getInstance("TLS");

    public SSLSocketFactoryEx(final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        TrustManager tm = new X509TrustManager() {

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(final java.security.cert.X509Certificate[] chain, final String authType)
                    throws java.security.cert.CertificateException {
            }

            public void checkServerTrusted(final java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {

            }
        };

        sslContext.init(null, new TrustManager[] {
            tm
        }, null);
    }

    @Override
    public Socket createSocket(final Socket socket, final String host, int port, final boolean autoClose)
            throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    public String[] getDefaultCipherSuites() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getSupportedCipherSuites() {
        // TODO Auto-generated method stub
        return null;
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        // TODO Auto-generated method stub
        return null;
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException,
            UnknownHostException {
        // TODO Auto-generated method stub
        return null;
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
