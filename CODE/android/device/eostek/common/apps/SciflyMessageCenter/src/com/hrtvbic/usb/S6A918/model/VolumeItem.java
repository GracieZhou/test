package com.hrtvbic.usb.S6A918.model;

import java.io.File;
import java.io.FileFilter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

//import com.hrtvbic.usb.S6A918.util.BucketHelper2;
//import com.hrtvbic.usb.S6A918.util.BucketHelper2.BucketEntry;
import com.hrtvbic.usb.S6A918.util.FileUtils;

public class VolumeItem extends FolderItem {

	private static final String TAG = "VolumnItem";
	
//	private ThumbCacheService mThumbCacheService;
	private Context mContext;

	// Before HoneyComb there is no Files table. Thus, we need to query the
    // bucket info from the Images and Video tables and then merge them
    // together.
    //
    // A bucket can exist in both tables. In this case, we need to find the
    // latest timestamp from the two tables and sort ourselves. So we add the
    // MAX(date_taken) to the projection and remove the media_type since we
    // already know the media type from the table we query from.
	private static final String[] PROJECTION_BUCKET_IN_ONE_TABLE = {
			ImageColumns.BUCKET_ID, ImageColumns.DATA,
			ImageColumns.BUCKET_DISPLAY_NAME, "count(*)" };
    
    // When query from the Images or Video tables, we only need to group by BUCKET_ID.
    private static final String BUCKET_GROUP_BY_IN_ONE_TABLE = "1) GROUP BY (1";
	
	public VolumeItem(File file, Context context,
			FileFilter filter, FileBuilder fileBuilder, String name) {
		super(file, filter, fileBuilder, name);
		setFileType(FileType.DISK);
		mContext = context;
	}
	
	@Override
	public void loadChildren() {
		mChildren.clear();
		
		File[] files = mFile.listFiles(mFilter);
		if (files != null)
			for (File file : files) {
				FileItem item = mFileBuilder.buildItem(file);
				if (item == null || item.getFileType() == FileType.OTHER) {
					continue;
				}
				
				item.setVolume(this);
				mChildren.add(item);
			}
	}
	
	public void loadChildrenFromDatabase(Uri uri, String selection) {
		mChildren.clear();

		Cursor cursor = mContext.getContentResolver()
				.query(uri,	PROJECTION_BUCKET_IN_ONE_TABLE, selection, null,
						ImageColumns.BUCKET_DISPLAY_NAME + " ASC");
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String bucket_display_name = cursor.getString(cursor.getColumnIndexOrThrow(ImageColumns.BUCKET_DISPLAY_NAME));
				String _data = cursor.getString(cursor.getColumnIndexOrThrow(ImageColumns.DATA));
				String bucketPath = _data.substring(0, _data.lastIndexOf("/"));
				Log.i(TAG, "bucket_display_name: " + bucket_display_name + ", bucketPath: " + bucketPath);

				FileItem item = mFileBuilder.buildItem(new File(bucketPath));
				if (item == null || item.getFileType() == FileType.OTHER) {
					continue;
				}

				item.setVolume(this);
				mChildren.add(item);
			}

			cursor.close();
		}
	}
	
//	public void loadChildrenFromDatabase(int mediaType, String volumeName) {
//		mChildren.clear();
//
//		BucketEntry[] entries = BucketHelper2.loadBucketEntries(mContext.getContentResolver(), mediaType, volumeName);
//		int length = entries.length;
//		for (int i = 0; i < length; ++i) {
//			if (entries[i].bucketPath.equals(mFile.getAbsolutePath())) {
//				continue;
//			}
//
//			Log.i(TAG, "bucket_display_name: " + entries[i].bucketDisplayName);
//			FileItem item = mFileBuilder.buildItem(new File(entries[i].bucketPath));
//			if (item == null || item.getFileType() == FileType.OTHER) {
//				continue;
//			}
//
//			item.setVolume(this);
//			mChildren.add(item);
//		}
//		
//		FileFilter filter = null;
//		if (mediaType == BucketHelper2.MEDIA_TYPE_VIDEO) {
//			filter = FileUtils.VIDEO_FILTER;
//		} else if (mediaType == BucketHelper2.MEDIA_TYPE_AUDIO) {
//			filter = FileUtils.MUSIC_FILTER;
//		} else if (mediaType == BucketHelper2.MEDIA_TYPE_IMAGE) {
//			filter = FileUtils.IMAGE_FILTER;
//		}
//		
//		// 加载根目录下的媒体文件
//		File[] files = mFile.listFiles(filter);
//		if (files != null)
//			for (File file : files) {
//				FileItem item = mFileBuilder.buildItem(file);
//				if (item == null || item.getFileType() == FileType.OTHER) {
//					continue;
//				}
//
//				item.setVolume(this);
//				mChildren.add(item);
//			}
//	}
}
