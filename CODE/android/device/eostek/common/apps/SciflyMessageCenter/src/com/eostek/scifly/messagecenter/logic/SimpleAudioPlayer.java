
package com.eostek.scifly.messagecenter.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.eostek.scifly.messagecenter.R;
import com.media.eos.EosPlayer2;

/**
 * Audio player for playing the audio.
 */
public class SimpleAudioPlayer extends EosPlayer2 {

    private static final String TAG = "SimpleAudioPlayer";

    private List<String> mPlayList = new ArrayList<String>();

    private String mCurrentDataSource;

    private OnAudioStopListener mAudioStopListener;

    private Context mContext;

    /**
     * Constructor.
     * 
     * @param context
     */
    public SimpleAudioPlayer(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * set listener for audio stop.
     * 
     * @param l
     */
    public void setOnAudioStopListener(OnAudioStopListener l) {
        mAudioStopListener = l;
    }

    /**
     * add the path of audio files.
     * 
     * @param path
     */
    public void addPlayPath(String path) {
        mPlayList.add(path);
    }

    /**
     * clear audio path.
     */
    public void clearPlayPath() {
        mPlayList.clear();
    }

    /**
     * add audio file path to list.
     * 
     * @param index
     * @param path
     */
    public void addPlayPathAt(int index, String path) {
        if (index < 0 || index > mPlayList.size()) {
            Log.i(TAG, "新语音在队列中的位置不合理! " + " index = " + index + " size " + mPlayList.size());
            return;
        }
        mPlayList.add(index, path);
    }

    /**
     * play audio.
     */
    public void playAudio() {
        if (!isPlaying()) {
            if (mPlayList.size() > 0) {
                try {
                    reset();
                    mCurrentDataSource = mPlayList.get(0);
                    setDataSource(mPlayList.remove(0));
                    prepareAsync();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_expired),
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else {
            stop();
            if (mPlayList.size() > 0) {
                String dataSource = mPlayList.get(0);
                if (!dataSource.equals(mCurrentDataSource)) {
                    playAudio();
                }
            }
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        if (mAudioStopListener != null) {
            mAudioStopListener.onAudioStop();
        }
    }

    /**
     * interface for audio stop.
     */
    public interface OnAudioStopListener {
        /**
         * audio stop.
         */
        void onAudioStop();

    }

    /**
     * count the size of play list.
     * 
     * @return
     */
    public int getCount() {
        return mPlayList.size();
    }

}
