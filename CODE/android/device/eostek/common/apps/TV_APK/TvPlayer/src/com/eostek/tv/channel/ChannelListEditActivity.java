
package com.eostek.tv.channel;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.eostek.tv.R;
import com.eostek.tv.utils.ChannelManagerExt;
import com.eostek.tv.widget.PasswordCheckDialog;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.EnumProgramAttribute;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

public class ChannelListEditActivity extends Activity {

    /**
     * Load the actions related the views
     */
    private ChannelListEditHolder mHolder;

    /**
     * channel info
     */
    private List<ProgramInfo> mChannels = null;

    /**
     * selected the item position
     */
    private int mCurPosition = 0;

    /**
     * isMove the state
     */
    private boolean isMove = false;

    /**
     * the currrent move index
     */
    private int mMoveIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChannels = ChannelManagerExt.getInstance().getChannelsAll();
        if (mChannels.size() <= 0) {
            ChannelManagerExt.getInstance().getAllChannels(this,
                    TvCommonManager.getInstance().getCurrentTvInputSource());
            mChannels = ChannelManagerExt.getInstance().getChannelsAll();
        }
        if (mChannels == null ||mChannels.size() <= 0) {
            Toast.makeText(this, R.string.noprogram_tip, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mHolder = new ChannelListEditHolder(this, mChannels);
        setListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            ProgramInfo info = mChannels.get(mCurPosition);
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    // to reset channel after channels edit.
                    ChannelManagerExt.getInstance().getAllChannels(this,
                            TvCommonManager.INPUT_SOURCE_DTV);
                    finish();
                    return true;
                case KeyEvent.KEYCODE_PROG_RED:
                    changeFavoriteStatus(info);
                    return true;
                case KeyEvent.KEYCODE_PROG_GREEN:
                    // show password dialog.
                    new PasswordCheckDialog(this, PasswordCheckDialog.LOCK).show();
                    return true;
                case KeyEvent.KEYCODE_PROG_YELLOW:
                    doMove();
                    return true;
                case KeyEvent.KEYCODE_PROG_BLUE:
                    changeSkipStatus(info);
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * set favorite channel.
     * 
     * @param info
     */
    private void changeFavoriteStatus(ProgramInfo info) {
        if (mChannels.get(mCurPosition).favorite == 0) {
            mChannels.get(mCurPosition).favorite = 1;
            TvChannelManager.getInstance().addProgramToFavorite(
                    TvChannelManager.PROGRAM_FAVORITE_ID_1, info.number, info.serviceType, 0x00);
        } else {
            mChannels.get(mCurPosition).favorite = 0;
            TvChannelManager.getInstance().deleteProgramFromFavorite(
                    TvChannelManager.PROGRAM_FAVORITE_ID_1, info.number, info.serviceType, 0x00);
        }
        mHolder.refreshAdapter();
    }

    /**
     * lock or unlock channel. and refresh ui
     */
    public void changeLockStatus() {
        ProgramInfo info = mChannels.get(mCurPosition);
        boolean lock = mChannels.get(mCurPosition).isLock;
        lock = !lock;
        mChannels.get(mCurPosition).isLock = lock;
        TvChannelManager.getInstance().setProgramAttribute(TvChannelManager.PROGRAM_ATTRIBUTE_LOCK, info.number, 
                info.serviceType, 0x00, lock);
        mHolder.refreshAdapter();
    }

    /**
     * move channel.
     */
    private void doMove() {
        if (!isMove && mMoveIndex == -1) {
            isMove = true;
            mMoveIndex = mCurPosition;
        } else if (isMove && mMoveIndex != -1) {
            isMove = false;
            mMoveIndex = -1;
        }
        mHolder.refreshAdapter();
    }

    /**
     * hide channel.
     * 
     * @param info
     */
    private void changeSkipStatus(ProgramInfo info) {
        if (mChannels.get(mCurPosition).isSkip) {
            mChannels.get(mCurPosition).isSkip = false;
            ChannelManagerExt.getInstance().setProgramAttribute(EnumProgramAttribute.E_SKIP,
                    info.number, info.serviceType, 0x00, false);
            mHolder.refreshAdapter();
        } else {
            int unSipCount = 0;
            for (int i = 0; i < mChannels.size(); i++) {
                if (!mChannels.get(i).isSkip) {
                    unSipCount++;
                    if (unSipCount == 2) {
                        break;
                    }
                }
            }
            // you can't hide all channels.
            if (unSipCount == 1) {
                Toast.makeText(this, R.string.cantskipall, Toast.LENGTH_LONG).show();
            } else {
                mChannels.get(mCurPosition).isSkip = true;
                ChannelManagerExt.getInstance().setProgramAttribute(EnumProgramAttribute.E_SKIP,
                        info.number, info.serviceType, 0x00, true);
                mHolder.refreshAdapter();
            }
        }
    }

    private void setListener() {
        mHolder.getmChannels_lv().setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
                mCurPosition = position;
                if (isMove) {
                    if (mMoveIndex == mCurPosition) {
                        return;
                    } else {
                        ChannelManagerExt.getInstance().move(mMoveIndex, mCurPosition);
                        ProgramInfo LastInfo = mChannels.get(mMoveIndex);
                        mChannels.remove(mMoveIndex);
                        mChannels.add(mCurPosition, LastInfo);
                        mHolder.refreshAdapter();
                        mMoveIndex = mCurPosition;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                mHolder.showChannelSelector(false);
            }
        });
    }

    public boolean isMove() {
        return isMove;
    }

    public int getMoveIndex() {
        return mMoveIndex;
    }
}
