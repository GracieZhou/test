/* //device/content/providers/media/src/com/android/providers/media/MediaScannerReceiver.java
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.android.providers.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
// MStar Android Patch Begin
// Add two broadcast to stop or start mediascanner
import android.media.MediaScanner;
// MStar Android Patch End

import java.io.File;
import java.io.IOException;
//EosTek Patch Begin
import android.content.SharedPreferences;
//EosTek Patch End

public class MediaScannerReceiver extends BroadcastReceiver {
    private final static String TAG = "MediaScannerReceiver";

    // EosTek Patch Begin
    // comment : just scan /system/media once at first boot
    private final static String FIRST_BOOT = "first_boot";

    private void writeToSharedPreferences(String key, String value, Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        Log.d(TAG, "writeToSharedPreferences::" + key + "=" + value);
    }

    private String getFromSharedPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "true");
        return value;
    }
    // EosTek Patch End
    
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final Uri uri = intent.getData();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Scan both internal and external storage
			// EosTek Patch Begin
            if ("true".equals(getFromSharedPreferences(FIRST_BOOT, context))) {
                scan(context, MediaProvider.INTERNAL_VOLUME);
                writeToSharedPreferences(FIRST_BOOT, "false", context);
            }
            //scan(context, MediaProvider.INTERNAL_VOLUME);
			// EosTek Patch End
            scan(context, MediaProvider.EXTERNAL_VOLUME);
        // MStar Android Patch Begin
        // Stop or start mediascanner when receive broadcast below
        } else if (action.equals("action_media_scanner_stop")) {
            MediaScanner.stopMediaScanner();
        } else if (action.equals("action_media_scanner_start")) {
            MediaScanner.startMediaScanner();
            scan(context, MediaProvider.EXTERNAL_VOLUME);
        // MStar Android Patch End

        } else {
            if (uri.getScheme().equals("file")) {
                // handle intents related to external storage
                String path = uri.getPath();
                String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
                String legacyPath = Environment.getLegacyExternalStorageDirectory().getPath();

                try {
                    path = new File(path).getCanonicalPath();
                } catch (IOException e) {
                    Log.e(TAG, "couldn't canonicalize " + path);
                    return;
                }
                if (path.startsWith(legacyPath)) {
                    path = externalStoragePath + path.substring(legacyPath.length());
                }

                Log.d(TAG, "action: " + action + " path: " + path);
                if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                    // scan whenever any volume is mounted
                    scan(context, MediaProvider.EXTERNAL_VOLUME);
                } else if (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE.equals(action) &&
                        path != null && path.startsWith(externalStoragePath + "/")) {
                    scanFile(context, path);
                }
            }
        }
    }

    private void scan(Context context, String volume) {
        Bundle args = new Bundle();
        args.putString("volume", volume);
        context.startService(
                new Intent(context, MediaScannerService.class).putExtras(args));
    }    

    private void scanFile(Context context, String path) {
        Bundle args = new Bundle();
        args.putString("filepath", path);
        context.startService(
                new Intent(context, MediaScannerService.class).putExtras(args));
    }    
}
