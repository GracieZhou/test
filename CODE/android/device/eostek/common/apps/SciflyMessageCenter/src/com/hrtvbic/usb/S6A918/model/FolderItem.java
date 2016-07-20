package com.hrtvbic.usb.S6A918.model;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

//import org.videolan.libvlc.LibVLC;

import android.net.Uri;

import com.google.common.collect.Lists;

/**
 * 描述一个文件夹
 */
public class FolderItem extends FileItem {

	/*
	 * 该文件夹的子目录.
	 */
	protected List<FileItem> mChildren = Lists.newArrayList();
	protected File mFile;
	protected FileFilter mFilter;
	protected FileBuilder mFileBuilder;

	public FolderItem(File folder, FileFilter filter, FileBuilder fileBuilder) {
		this(Uri.fromFile(folder),"", FileType.FOLDER, folder.getName());
		mFile = folder;
		mFilter = filter;
		mFileBuilder = fileBuilder;
	}

	public FolderItem(File folder, FileFilter filter, FileBuilder fileBuilder,
			String name) {
		this(Uri.fromFile(folder),"", FileType.FOLDER, name);
		mFile = folder;
		mFilter = filter;
		mFileBuilder = fileBuilder;
	}
	
	public FolderItem(Uri uri,String data, FileType type, String name) {
		super(uri,"", type, name);
	}
	
	public void setFileFilter(FileFilter filter) {
		mFilter = filter;
	}

	/**
	 * 加载目录文件. 目前只支�?�?
	 */
	public void loadChildren() {
		mChildren.clear();

		File[] files = mFile.listFiles(mFilter);
		if (files != null) {
			for (File file : files) {
				FileItem item = mFileBuilder.buildItem(file);
				if (item == null || item.getFileType() == FileType.OTHER) {
					continue;
				}

				item.setVolume(getVolumeItem());
				mChildren.add(item);
			}
		}
	}
	
	public void setChildren(List<FileItem> children) {
		mChildren = children;
	}

	public List<FileItem> getChildren() {
		return mChildren;
	}

	public int getChildrenSize() {
		return mChildren.size();
	}
	
//	public List<String> getLocations() {
//        List<String> locations = Lists.newArrayList();
//        for (int i = 0; i < mChildren.size(); i++) {
//        	Uri uri = mChildren.get(i).getUri();
//        	if (uri.getScheme().equals("http")) {
//        		locations.add(uri.toString());
//        	} else {
//        		locations.add(LibVLC.PathToURI(uri.getPath()));
//        	}
//        }
//        return locations;
//    }
}