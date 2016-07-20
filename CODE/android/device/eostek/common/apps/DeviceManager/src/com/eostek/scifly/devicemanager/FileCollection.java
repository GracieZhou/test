package com.eostek.scifly.devicemanager;

import java.io.File;
import java.util.ArrayList;

import com.eostek.scifly.devicemanager.util.FileTool;

/**
 * This class is a collection of file absolute path.
 * The size of this collection increases automatically when a file is added;
 * Users can get the total size by byte through getSize().
 * 
 * @author Psso.Song
 *
 */
public class FileCollection {
    private long size;
    private ArrayList<String> files;

    public FileCollection() {
        size = 0;
        files = new ArrayList<String>();
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }

    public long getSize() {
        return size;
    }

    public ArrayList<String> getFiles() {
        ArrayList<String> result = new ArrayList<String>();
        for (String file : files) {
            result.add(file);
        }
        return result;
    }

    /*
     * Add a new fileName to this collection.
     * We need to check if fileName is a directory because directory.length() is 4096 bytes.
     * Besides, we must make sure none of the files in this collection is a parent of others,
     * so that we won't count the file repeatedly.
     */
    public boolean add(String fileName) {
        if (files == null) {
            return false;
        }
        File file = new File(fileName);
        if (file.exists() && !files.contains(fileName)) {
            if (files.add(fileName)) {
                size += FileTool.getFileLength(file);
                return true;
            }
        }
        return false;
    }

    public boolean contains(String fileName) {
        if (files == null) {
            return false;
        }
        return files.contains(fileName);
    }

    public boolean remove(String fileName) {
        if (files == null) {
            return false;
        }
        File file = new File(fileName);
        if (file.exists() && files.contains(fileName)) {
            if (files.remove(fileName)) {
                size -= FileTool.getFileLength(file);
                return true;
            }
        }
        return false;
    }
}
