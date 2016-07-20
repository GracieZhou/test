
package com.eostek.sciflyui.thememanager.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;

import com.eostek.sciflyui.thememanager.ThemeDisplayAct;
import com.eostek.sciflyui.thememanager.download.UpgradeManager;
import com.eostek.sciflyui.thememanager.task.ThemeModel;
import com.eostek.sciflyui.thememanager.task.ThemeModel.TYPE;
import com.eostek.tm.cpe.manager.CpeManager;

/**
 * ThemeManagerUtils.
 */
public class ThemeManagerUtils {

    private static final String TAG = "ThemeManagerUtils";

    private static final int DEFAULT_TIMEOUT = 10;

    private static final String SERVER_TVOS_URL = "http://tvosapp.babao.com/interface/clientService.jsp";

    private static final String DEFAULT_PATH = Constant.CACHE_PATH + "/icon/";

    // private static int mCount = 1;
 
    /**
     * @param filePath filePath
     * @return filePath
     */
    public static String readLocalFile(String filePath) {
        File file = new File(filePath);

        return readLocalFile(file);
    }

    private static String readLocalFile(File file) {
        if (!file.exists()) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }

        StringBuffer stringb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String data = null;
            while ((data = br.readLine()) != null) {
                stringb.append(data);
            }
            return stringb.toString();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param path path
     * @param mThemeNames mThemeNames
     * @return ThemeModel list
     * @throws IOException IOException
     */
    public static List<ThemeModel> getLocalThemesByFolder(String path, Map<String, String> mThemeNames)
            throws IOException {
        System.out.println("localFolder.path:" + path);
        List<ThemeModel> localThemes = new ArrayList<ThemeModel>();
        File file = new File(path);
        if (file.isDirectory()) {
            File files[] = file.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    if (pathname.getName().endsWith(".zip") || pathname.getName().endsWith(".stz")) {
                        return true;
                    }
                    return false;
                }
            });
            if (files != null) {
                for (File f : files) {

                    String description;
                    try {
                        description = unzipDescription(f.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    Log.i(TAG, "description:" + description);
                    // getEncoding(description);
                    if (description != null && (!"".equals(description))) {
                        ThemeModel model = parseLocalTheme(description);
                        model.mLocalUrl = f.getAbsolutePath();

                        String fileName = model.mTitle + model.mThemeVersion;

                        model.mThumbUrl = Constant.IMAGE_CACHE + fileName + ".png";

                        if (Constant.SYSTEM_DEFAULT_PATH.equals(path)) {
                            model.mType = TYPE.DEFAULT;
                        }
                        // model.mId = mCount++;
                        Log.i(TAG, "local" + model.mTitle + model.mThemeVersion);
                        if (mThemeNames.get((model.mTitle + model.mThemeVersion)) == null) {
                            localThemes.add(model);
                            mThemeNames.put(model.mTitle + model.mThemeVersion, "");
                        } else {
                            Log.i(TAG, "theme exists already.");
                        }
                    }
                }
            }
        }
        return localThemes;
    }

    /**
     * @param string path
     * @return Bitmap
     */
    public static Bitmap getWallpaperFromZip(String string) {
        File file = new File(string);
        if (!file.exists()) {
            return null;
        }
        try {
            return getWallpaperFromZip(file);
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param file file
     * @return Bitmap
     * @exception IOException IOException
     */
    public static Bitmap getWallpaperFromZip(File file) throws IOException {
        Bitmap bitmap = null;
        ZipFile zf = new ZipFile(file);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            // Log.i(TAG, "" + entry.getName());
            if (entry.getName().toLowerCase(Locale.getDefault()).startsWith("wallpaper/wallpaper")
                    && checkExtension(entry.getName(), new String[] {
                            "png", "jpg"
                    })) {
                Log.i(TAG, "wallpaper found.");
                try {
                    bitmap = BitmapFactory.decodeStream(zf.getInputStream(entry));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Load wallpaper failed.");
                }
            }
        }
        return bitmap;
    }

    /**
     * @param name name
     * @param exs exs
     * @return checkExtension result
     */
    public static boolean checkExtension(String name, String[] exs) {
        for (int i = 0; i < exs.length; i++) {
            if (name.toLowerCase(Locale.getDefault()).endsWith(exs[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param source source
     * @return String String
     * @throws ZipException ZipException
     * @throws IOException IOException
     */
    public static String unzipDescription(String source) throws IOException {

        ZipFile zf = new ZipFile(source);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());

            if (entry.isDirectory()) {
                continue;
            } else {
                InputStream in = zf.getInputStream(entry);

                if (entry.getName().endsWith("description.xml")) {
                    Log.i(TAG, "entry.getName():" + entry.getName());
                    StringBuffer stringb = new StringBuffer();
                    BufferedReader br = null;

                    try {
                        br = new BufferedReader(new InputStreamReader(in));
                        String data = null;

                        while ((data = br.readLine()) != null) {
                            stringb.append(data);
                        }

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } finally {

                        if (br != null) {
                            try {
                                br.close();

                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    return stringb.toString();
                } else {
                    continue;
                }

            }
        }
        zf.close();
        return "";
    }

    /**
     * @param string string
     * @return ThemeModel
     */
    public static ThemeModel parseLocalTheme(String string) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            XMLReader reader = spf.newSAXParser().getXMLReader();
            XmlParser handler = new XmlParser();

            reader.setContentHandler(handler);
            if (string != null) {
                reader.parse(new InputSource(new StringReader(string)));
                return handler.getResults().get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param timeout timeout
     * @param mThemeNames mThemeNames
     * @param mThemes mThemes
     * @param requestUrl TODO
     * @return List<ThemeModel>
     */
    public static List<ThemeModel> parseOnlineTheme(int timeout, Map<String, String> mThemeNames,
            List<ThemeModel> mThemes, String requestUrl) {
        List<ThemeModel> themes = new ArrayList<ThemeModel>();
        String json = getRequestString(); // "{\"ns\":\"theme\",\"nm\":\"GetTheme\",\"op\":1,\"bd\":{\"pgi\":1,\"pgn\":2,\"all\":3,\"spv\":4.2.2,\"ord\":1}}";;
                                          // //getRequestString();
        // "{\"ns\":\"theme\",\"nm\":\"GetTheme\",\"op\":1,\"bd\":{\"pgi\":1,\"pgn\":2,\"all\":3,\"spv\":4.2.2,\"ord\":1}}";;
        // //getRequestString();
        // json = json.replace("\\", "");
        Log.i(TAG, "request:" + json);
        String jsonString = getJsonString(json, timeout, requestUrl);
        Log.d(TAG, "response:" + jsonString);

        if (jsonString == null) {
            return themes;
        }

        try {
            JSONObject responseJson = new JSONObject(jsonString);
            if (responseJson.getInt("err") != 0) {
                return themes;
            }

            JSONObject bodyJson = responseJson.getJSONObject("bd");

            // int totalPage = bodyJson.getInt("tpg");
            // int totalThemeCount = bodyJson.getInt("tnm");

            JSONArray themesJson = bodyJson.getJSONArray("its");
            int length = themesJson.length();

            for (int i = 0; i < length; i++) {
                ThemeModel online = new ThemeModel(ThemeModel.TYPE.ONLINE);
                JSONObject theme = (JSONObject) themesJson.get(i);
                online.mId = theme.getInt("id"); // the unique id
                online.mTitle = theme.getString("ti");
                online.mAuther = theme.getString("at");
                online.mThemeVersion = theme.getString("ver");
                online.mPlatformVersion = theme.getString("spv");
                online.mThumbUrl = theme.getString("icon");
                online.mIntroduction = theme.getString("dsr");
                online.mDownloadUrl = theme.getString("dln");
                online.mLocalUrl = Constant.CACHE_PATH + "/" + online.mTitle + "_" + online.mThemeVersion + ".zip";
                // online.mId = mCount++;
                Log.i(TAG, "online!!!!!!!!!!!" + Constant.CACHE_PATH + "/" + online.mTitle + "_" + online.mThemeVersion
                        + ".zip");
                if (mThemeNames.get((online.mTitle + online.mThemeVersion)) == null) {
                    themes.add(online);
                    mThemeNames.put(online.mTitle + online.mThemeVersion, "");
                } else {
                    Log.i(TAG, "model exists already.");
                    for (int j = 0; j < mThemes.size(); j++) {
                        ThemeModel tm = mThemes.get(j);
                        if ((online.mTitle + online.mThemeVersion).equals(tm.mTitle + tm.mThemeVersion)) {
                            tm.mDownloadUrl = online.mDownloadUrl;
                            tm.mThumbUrl = online.mThumbUrl;
                            Log.i(TAG, "add downloadUrl to local theme!");
                            break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return themes;
    }

    private static String getRequestString() {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("ns", "theme"); //
            requestJson.put("nm", "GetTheme");
            requestJson.put("op", "1");

            JSONObject childJson = new JSONObject();
            childJson.put("pgi", "1");
            childJson.put("pgn", "2");
            childJson.put("all", "0");
            childJson.put("spv", "4.2.2");
            childJson.put("ord", "1");

            requestJson.put("bd", childJson);
            // Log.i(TAG,requestJson.toString());

            return requestJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param json jsonstring
     * @param timeout time
     * @param requestUrl TODO
     * @return String
     */
    public static String getJsonString(String json, int timeout, String requestUrl) {
        timeout = timeout < 0 ? DEFAULT_TIMEOUT : timeout;
        StringBuffer buffer = new StringBuffer();
        HttpURLConnection conn = null;
        Writer writer = null;
        BufferedReader reader = null;
        try {

            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/json; charset=UTF-8");

            //终端特征码(机型) 数据获取:
            CpeManager manager = CpeManager.getInstance();
            String productclass = manager.getProductClass();

            Log.i(TAG, "platform_code:" + productclass);
            String platform = productclass; 
            conn.setRequestProperty("Ttag", productclass); 
            //conn.setRequestProperty("Ttag", Build.DISPLAY.split(" ")[0]); 
            
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(timeout * 1000);
            conn.setReadTimeout(timeout * 1000);
            conn.setDoOutput(true);

            Log.d(TAG, "getJsonString.connect:" + json + "--;" + timeout);
            conn.connect();

            writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            writer.write(json);
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\r\n");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getClass().getName() + ": " + e.getMessage(), e);
            e.printStackTrace();
            return null;
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return buffer.toString();
    }

    /**
     * @param context context
     * @param source source
     * @return Drawable
     * @exception IOException IOException
     */
    public static Drawable unzipThumbnail(Context context, String source) throws IOException {
        Drawable dr = null;

        ZipFile zf = new ZipFile(source);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());

            if (entry.isDirectory()) {
                continue;
            } else {
                InputStream in = zf.getInputStream(entry);

                if (entry.getName().endsWith("thumbnail.png")) {
                    TypedValue value = new TypedValue();
                    dr = Drawable.createFromResourceStream(context.getResources(), value, in, "", null);
                    return dr;
                } else {
                    continue;
                }
            }
        }
        zf.close();
        return dr;
    }

    /**
     * @param filePath filePath
     * @param width width
     * @param height height
     * @return Bitmap
     */
    public static Bitmap getLocalImage(File filePath, int width, int height) {

        Bitmap finalBitmap = null;

        Rect outPadding = new Rect();
        outPadding.top = outPadding.bottom = -1;
        outPadding.left = outPadding.right = -1;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        InputStream is1;
        try {
            is1 = new FileInputStream(filePath);
            Bitmap bitmap = BitmapFactory.decodeStream(is1, outPadding, options);
            try {
                is1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 计算缩放比
            // int be = (int) (options.outHeight / (float) height);
            float be1 = options.outWidth / width;
            float be2 = options.outHeight / height;

            int be = (int) ((be1 > be2) ? be2 : be1);

            if (be <= 0) {
                be = 1;
            }
            options.inSampleSize = be;

            options.inJustDecodeBounds = false;
            InputStream is2 = new FileInputStream(filePath); // contentResolver.openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(is2, outPadding, options);
            try {
                is2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap == null) {
                return null;
            }

            int mWidth = bitmap.getWidth();
            int mHeight = bitmap.getHeight();

            int focusX = mWidth / 2;
            int focusY = mHeight / 2;
            int cropX;
            int cropY;
            int cropWidth;
            int cropHeight;
            // float scaleFactor;

            if (width * mHeight < height * mWidth) {
                // Vertically constrained.
                cropWidth = width * mHeight / height;
                cropX = Math.max(0, Math.min(focusX - cropWidth / 2, mWidth - cropWidth));
                cropY = 0;
                cropHeight = mHeight;
                // scaleFactor = (float) height / mHeight;
            } else {
                // Horizontally constrained.
                cropHeight = height * mWidth / width;
                cropY = Math.max(0, Math.min(focusY - cropHeight / 2, mHeight - cropHeight));
                cropX = 0;
                cropWidth = mWidth;
                // scaleFactor = (float) width / mWidth;
            }

            finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            final Canvas canvas = new Canvas(finalBitmap);
            final Paint paint = new Paint();
            paint.setDither(true);
            paint.setFilterBitmap(true);
            canvas.drawColor(0);
            canvas.drawBitmap(bitmap, new Rect(cropX, cropY, cropX + cropWidth, cropY + cropHeight), new Rect(0, 0,
                    width, height), paint);

            // noted by w.p.
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } // contentResolver.openInputStream(imageUri);

        return finalBitmap;
    }

    /**
     * @param context context
     * @param name name
     * @param url url
     * @return Bitmap
     */
    public static Bitmap downloadBitmap(Context context, String name, String url) {

        String imgPath = DEFAULT_PATH + name + ".png";
        File saveFile = new File(imgPath); // task.getLocalUrl()

        if (saveFile.exists()) {
            Bitmap drawable = getLocalImage(saveFile, Constant.THUMBNAIL_WIDTH, Constant.THUMBNAIL_HEIGHT);
            return drawable;
        }

        // TODO if image exists.
        // //find url in cache first
        Bitmap bitmap = null; // = AsyncCache.getInstance().readCache(url,
                              // Bitmap.class);
        // get bitmap from server
        URL myFileUrl = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        ByteArrayOutputStream baos = null;
        try {
            Log.d(TAG, Thread.currentThread().getName() + ",ImageAsyncTask start:" + name);
            myFileUrl = new URL(url);
            conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(18 * 1000);
            conn.connect();

            if (conn.getContentLength() >= 1024 * 1024) {
                Log.i(TAG, "the online bitmap size = " + conn.getContentLength() + "  \n from url = " + url);
                return null;
            }

            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            int len = -1;
            byte[] b = new byte[1024 * 512];
            while (-1 != (len = is.read(b))) {
                baos.write(b, 0, len);
            }
            byte[] bb = baos.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bb, 0, bb.length);
            File parentFile = new File(DEFAULT_PATH);
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
        } catch (Exception e) {
            Log.e(TAG, "ImageAsyncTask.doInBackground.error: " + name + "," + url + "  e.getClass.getName: "
                    + e.getClass().getName());
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            is = null;
            baos = null;
        }
        return bitmap;
    }

    /**
     * delete the selected theme by deleting the theme file in cache and
     * changing the data in database and changing the ThemeModel state.
     * 
     * @param context context
     * @param position position
     * @return whether successes
     */
    public static boolean deleteTheme(Context context, int position) {

        ThemeDisplayAct activity = (ThemeDisplayAct) context;

        if (position < 0 || position > activity.getmThemes().size()) {
            return false;
        }

        ThemeModel tm = activity.getmThemes().get(position);

        tm.mType = ThemeModel.TYPE.ONLINE;
        tm.downloadIsCompleted = false;
        tm.isError = false;
        tm.isDownload = false;
        tm.mProgress = 0;

        String zipFilePath = tm.mLocalUrl;
        Log.i(TAG, "deleting file :" + zipFilePath);

        File file = new File(zipFilePath);

        if (file.exists() && file.isFile()) {
            file.delete();
        } else {
            return false;
        }

        UpgradeManager upgradeManager = new UpgradeManager(context);
        upgradeManager.deleteTask(tm.mLocalUrl);

        return true;
    }

}
