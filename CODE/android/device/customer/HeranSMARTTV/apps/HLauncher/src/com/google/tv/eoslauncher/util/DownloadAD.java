package com.google.tv.eoslauncher.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.extreamax.videoad.ExtreamaxVideoAdService;
import net.extreamax.videoad.bean.VideoAdBean;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

public class DownloadAD extends AsyncTask<Void, Void, Void> {

	int back = 0;
	Activity Act;
	VideoAdBean bean;
	String TAG = "DownloadAD";
	String AD_Url;
	String mac;

	// boolean OPEN_AD;
	@Override
	protected Void doInBackground(Void... params) {
		try {
			mac = TvManager.getInstance().getEnvironment("ethaddr");

		} catch (TvCommonException e) {
			e.printStackTrace();
		}
		Get_AD();

		return null;
	}

	public void Get_AD() {
		bean = ExtreamaxVideoAdService.getInstance().getVideoAd(
				"a2aea910b97ab0a66c196b0581610e5e", mac);

		Log.v(TAG, "Hash:" + bean.getHash());
		Log.v(TAG, "Status:" + bean.getStatus());

		Log.v(TAG, "發送紀錄:" + back);

		AD_Url = bean.getUrl();

		ADVedio(AD_Url);
		Log.v(TAG, "下載的url:" + AD_Url);

		VideoTime();
	}

	// 影片長度固定10秒
	public void VideoTime() {
		if (bean.getHash() != null) {
			Log.v(TAG, "bean.getHash()的值:" + bean.getHash());
			int ok = ExtreamaxVideoAdService.getInstance().sendVideoAdLog(
					bean.getHash(), 10000);
			Log.v(TAG, "bean.getHash()_status:" + ok);
		}
	}

	@Override
	protected void onPostExecute(Void result) {

		super.onPostExecute(result);
	}

	// 下載影片
	public void ADVedio(final String address) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FileOutputStream fos = null;
				InputStream is = null;
				try {
					URL url = new URL(address);
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					HttpParams parameter = new BasicHttpParams();
					int connectionTO = 10000;
					int socketTO = 10000;
					HttpConnectionParams.setConnectionTimeout(parameter,
							connectionTO);
					HttpConnectionParams.setSoTimeout(parameter, socketTO);
					HttpClient httpclient = new DefaultHttpClient(parameter);
					HttpGet httpGet = new HttpGet(address);

					Log.d(TAG, "downloading video from:"
							+ httpGet.getURI().toString());
					HttpResponse response = httpclient.execute(httpGet);
					Log.d(TAG, "response.getStatusLine().getStatusCode():"
							+ response.getStatusLine().getStatusCode());
					Log.d(TAG, "HttpStatus.SC_OK" + HttpStatus.SC_OK);

					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String PATH = "/data/video";
						File file = new File(PATH);
						file.mkdirs();

						Log.v(TAG, "PATH:" + PATH);
						File outputFile = new File(file, "video.ts");
						// Log.v(TAG, "outputFile :" + outputFile);
						fos = new FileOutputStream(outputFile);
						is = response.getEntity().getContent();

						byte[] buffer = new byte[1024];
						int length = 0;
						long bytesum = 0;
						int oldProgress = 0;
						long bytetotal = urlConnection.getContentLength();

						Log.v(TAG, "檔案大小 :" + bytetotal);

						while ((length = is.read(buffer)) != -1) {
							bytesum += length;
							fos.write(buffer, 0, length);
							int progress = (int) (bytesum * 100L / bytetotal);
							// Log.v(TAG, "progress : " + progress);
							if (progress != oldProgress) {
								// updateProgress(progress);
							}
							oldProgress = progress;

						}

						fos.close();
						is.close();
						// 下載完成
						Log.v(TAG, "outputFile :" + outputFile);
//						VideoTime(getVideoLength("/data/video/"));
					} else {

					}
				} catch (Exception e) {
					e.printStackTrace();
					String PATH = "/data/video/video.ts";
					File file = new File(PATH);
					if (file.exists()) {
						file.delete();
					}
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	// 抓取影片長度 <暫時沒用到>
	public long getVideoLength(String your_data_source) {
		Log.v(TAG, "your_data_source :" + your_data_source);
		ContentResolver resolver = Act.getContentResolver();
		String ImagePath = "file://" + your_data_source;
		Uri uri = Uri.parse(ImagePath);
		Cursor cursor = resolver.query(uri, null, null, null,
				MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		long timeInmillisec = cursor.getInt(cursor.
				getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
//		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//		retriever.setDataSource(your_data_source);
//		String time = retriever
//				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//		Log.v(TAG, "time :" + time);
//		long timeInmillisec = Long.parseLong(time);
//		long duration = timeInmillisec / 1000;
//		long hours = duration / 3600;
//		long minutes = (duration - hours * 3600) / 60;
//		long seconds = duration - (hours * 3600 + minutes * 60);
		Log.v(TAG, "timeInmillisec :" + timeInmillisec);
		return timeInmillisec;
	}
}