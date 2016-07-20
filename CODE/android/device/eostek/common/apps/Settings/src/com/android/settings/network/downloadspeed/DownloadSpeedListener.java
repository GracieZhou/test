package com.android.settings.network.downloadspeed;

public interface DownloadSpeedListener {

    public void onDownloadSpeedChanged(int max, int min, int avg,
            boolean complete);

    public void onDownloadProgress(int progress);
}
