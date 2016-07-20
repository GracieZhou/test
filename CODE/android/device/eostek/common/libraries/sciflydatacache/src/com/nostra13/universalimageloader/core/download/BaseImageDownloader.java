/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.download;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scifly.datacache.DataCacheManager;

/**
 * Provides retrieving of {@link InputStream} of image by URI from network or file system or app resources.<br />
 * {@link URLConnection} is used to retrieve image stream from network.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see HttpClientImageDownloader
 * @since 1.8.0
 */
public class BaseImageDownloader implements ImageDownloader {
    /** {@value} */
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    /** {@value} */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

    /** {@value} */
    protected static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
    /** {@value} */
    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    protected static final int MAX_REDIRECT_COUNT = 5;

    protected static final String CONTENT_CONTACTS_URI_PREFIX = "content://com.android.contacts/";

    private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. "
            + "You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";

    protected final Context context;
    protected final int connectTimeout;
    protected final int readTimeout;

    private static Map<String, RequestInfo> map = new HashMap<String, BaseImageDownloader.RequestInfo>();

    class RequestInfo {
        private long lastModified;
        private String etag;

        public RequestInfo(long lastModified, String etag) {
            this.lastModified = lastModified;
            this.etag = etag;
        }

        public long getLastModified() {
            return lastModified;
        }

        public String getEtag() {
            return etag;
        }
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        synchronized (map) {
            map.clear();
        }
    }

    @Override
    public void clear(String uri) {
        // TODO Auto-generated method stub
        synchronized (map) {
            if (map.containsKey(uri)) {
                map.remove(uri);
            }
        }
    }

    public BaseImageDownloader(Context context) {
        this.context = context.getApplicationContext();
        this.connectTimeout = DEFAULT_HTTP_CONNECT_TIMEOUT;
        this.readTimeout = DEFAULT_HTTP_READ_TIMEOUT;
    }

    public BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
        this.context = context.getApplicationContext();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        switch (Scheme.ofUri(imageUri)) {
            case HTTP:
            case HTTPS:
                return getStreamFromNetwork(imageUri, extra);
            case FILE:
                return getStreamFromFile(imageUri, extra);
            case CONTENT:
                return getStreamFromContent(imageUri, extra);
            case ASSETS:
                return getStreamFromAssets(imageUri, extra);
            case DRAWABLE:
                return getStreamFromDrawable(imageUri, extra);
            case UNKNOWN:
            default:
                return getStreamFromOtherSource(imageUri, extra);
        }
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in the network).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
     *                     URL.
     */
    
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        HttpURLConnection conn = createConnection(imageUri, extra);

        int redirectCount = 0;
        int responseCode;
        while ((responseCode = conn.getResponseCode()) / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            L.d("BaseImageDownloader: redirectCount=" + redirectCount + ", responseCode=" + responseCode);
            conn = createConnection(conn.getHeaderField("Location"), extra);
            redirectCount++;
        }
        if (responseCode != 200) {
            L.d("BaseImageDownloader: getStreamFromNetwork: responseCode=" + responseCode
                    + ", Fail to get " + imageUri);
            return null;
        }
        L.d("BaseImageDownloader: redirectCount=" + redirectCount + ", responseCode=" + responseCode);

        long lastModified = conn.getLastModified();
        String etag = conn.getHeaderField("Etag");
        L.d("BaseImageDownloader: GetStreamFromNetwork, lastModified=" + lastModified + ", etag=" + etag);
        // check if current request modified on the server
        synchronized (map) {
            map.put(imageUri, new RequestInfo(lastModified, etag));
        }

        InputStream imageStream;
        try {
            imageStream = conn.getInputStream();
        } catch (IOException e) {
            // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
            IoUtils.readAndCloseStream(conn.getErrorStream());
            throw e;
        }
        return new ContentLengthInputStream(new BufferedInputStream(imageStream, BUFFER_SIZE), conn.getContentLength());
    }

    public InputStream checkStreamFromNetwork(String imageUri, Object extra) throws IOException {
        L.d("BaseImageDownloader: Check if modified " + imageUri);
        HttpURLConnection conn = createCheckConnection(imageUri, extra);

        int redirectCount = 0;
        int responseCode = conn.getResponseCode();
        while ((responseCode = conn.getResponseCode()) / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            L.d("BaseImageDownloader: redirectCount=" + redirectCount + ", responseCode=" + responseCode);
            conn = createCheckConnection(conn.getHeaderField("Location"), extra);
            redirectCount++;
        }
        if (responseCode != 200) {
            L.d("BaseImageDownloader: checkStreamFromNetwork: responseCode=" + responseCode
                    + ", Server not modified: " + imageUri);
            return null;
        }
        L.d("BaseImageDownloader: redirectCount=" + redirectCount + ", responseCode=" + responseCode);

        long lastModified = conn.getLastModified();
        String etag = conn.getHeaderField("Etag");
        L.d("BaseImageDownloader: checkStreamFromNetwork, lastModified=" + lastModified + ", etag=" + etag);
        // check if current request modified on the server
        synchronized (map) {
            if (map.containsKey(imageUri)) {
                RequestInfo info = map.get(imageUri);
                if (lastModified == info.getLastModified()) {
                    if (etag == null) {
                        if (info.getEtag() == null) {
                            L.d("BaseImageDownloader: checkStreamFromNetwork, ETAG=null, assume file not modified!");
                            return null;
                        }
                    } else if (info.getEtag() != null && etag.equalsIgnoreCase(info.getEtag())) {
                        L.d("BaseImageDownloader: checkStreamFromNetwork, ETAG is the same, assume file not modified!");
                        return null;
                    }
                }
            }
            map.put(imageUri, new RequestInfo(lastModified, etag));
        }

        InputStream imageStream;
        try {
            imageStream = conn.getInputStream();
        } catch (IOException e) {
            // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
            IoUtils.readAndCloseStream(conn.getErrorStream());
            throw e;
        }
        return new ContentLengthInputStream(new BufferedInputStream(imageStream, BUFFER_SIZE), conn.getContentLength());
    }

    protected HttpURLConnection createCheckConnection(String url, Object extra) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        // add http headers
        if (extra != null && extra instanceof HashMap<?, ?>) {
            HashMap<String, String> headers = (HashMap<String, String>) extra;
            Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null && !key.equals(DataCacheManager.EXTRA_KEY_POSTFIX)) {
                    L.d("BaseImageDownloader: createCheckConnection, add http header <" + key + ", " +  value + ">");
                    conn.setRequestProperty(key, value);
                }
            }
        }

        long lastModified = 0;
        String etag = null;
        synchronized (map) {
            if (map.containsKey(url)) {
                RequestInfo info = map.get(url);
                lastModified = info.getLastModified();
                etag = info.getEtag();
            }
        }
        L.d("BaseImageDownloader: createCheckConnection, lastModified=" + lastModified + ", etag=" + etag);
        conn.setIfModifiedSince(lastModified);
        if (etag != null) {
            conn.setRequestProperty("If-None-Match", etag);
        }
        return conn;
    }

    /**
     * Create {@linkplain HttpURLConnection HTTP connection} for incoming URL
     *
     * @param url   URL to connect to
     * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *              DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@linkplain HttpURLConnection Connection} for incoming URL. Connection isn't established so it still configurable.
     * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
     *                     URL.
     */
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        String postParam = null;

        // add http headers
        if (extra != null && extra instanceof HashMap<?, ?>) {
            HashMap<String, String> headers = (HashMap<String, String>) extra;
            Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                    continue;
                }
                if (DataCacheManager.EXTRA_KEY_POST.equals(key)) {
                    postParam = value;
                    L.d("BaseImageDownloader: createConnection, POST=" + postParam);
                    continue;
                }
                if (!DataCacheManager.EXTRA_KEY_POSTFIX.equals(key)) {
                    L.d("BaseImageDownloader: createConnection, add http header <" + key + ", " + value + ">");
                    conn.setRequestProperty(key, value);
                }
            }
        }
        if (postParam != null) {
            L.d("BaseImageDownloader: createConnection, POST http request!");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);

            Writer writer = null;
            try {
                writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                writer.write(postParam);
                writer.flush();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }

        return conn;
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located on the local file system or SD card).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs reading from file system
     */
    protected InputStream getStreamFromFile(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        return new ContentLengthInputStream(new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE),
                (int) new File(filePath).length());
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is accessed using {@link ContentResolver}).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws FileNotFoundException if the provided URI could not be opened
     */
    protected InputStream getStreamFromContent(String imageUri, Object extra) throws FileNotFoundException {
        ContentResolver res = context.getContentResolver();

        Uri uri = Uri.parse(imageUri);
        if (isVideoUri(uri)) { // video thumbnail
            Long origId = Long.valueOf(uri.getLastPathSegment());
            Bitmap bitmap = MediaStore.Video.Thumbnails
                    .getThumbnail(res, origId, MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (bitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.PNG, 0, bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
        } else if (imageUri.startsWith(CONTENT_CONTACTS_URI_PREFIX)) { // contacts photo
            return ContactsContract.Contacts.openContactPhotoInputStream(res, uri);
        }

        return res.openInputStream(uri);
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in assets of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs file reading
     */
    protected InputStream getStreamFromAssets(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.ASSETS.crop(imageUri);
        return context.getAssets().open(filePath);
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in drawable resources of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     */
    protected InputStream getStreamFromDrawable(String imageUri, Object extra) {
        String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
        int drawableId = Integer.parseInt(drawableIdString);
        return context.getResources().openRawResource(drawableId);
    }

    /**
     * Retrieves {@link InputStream} of image by URI from other source with unsupported scheme. Should be overriden by
     * successors to implement image downloading from special sources.<br />
     * This method is called only if image URI has unsupported scheme. Throws {@link UnsupportedOperationException} by
     * default.
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException                   if some I/O error occurs
     * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
     */
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        throw new UnsupportedOperationException(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri));
    }

    private boolean isVideoUri(Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);

        if (mimeType == null) {
            return false;
        }

        return mimeType.startsWith("video/");
    }
}