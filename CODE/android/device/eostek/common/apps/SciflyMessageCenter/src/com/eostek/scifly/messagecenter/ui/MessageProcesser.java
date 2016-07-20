
package com.eostek.scifly.messagecenter.ui;

import static com.eostek.scifly.messagecenter.util.Constants.DOCX_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.DOC_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.EPUB_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.PDF_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.PPTX_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.PPT_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.TXT_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.XLSX_POSTFIX;
import static com.eostek.scifly.messagecenter.util.Constants.XLS_POSTFIX;

import java.util.Locale;

import scifly.provider.SciflyStore.Messages;
import scifly.provider.metadata.Msg;
import android.content.Context;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.eostek.scifly.messagecenter.R;
import com.eostek.scifly.messagecenter.logic.MessageDataCache;
import com.eostek.scifly.messagecenter.ui.MessageBaseAdapter.MessageGridViewHolder;
import com.eostek.scifly.messagecenter.util.Constants;
import com.eostek.scifly.messagecenter.util.Util;
import com.eostek.scifly.video.player.service.VideoInfo;

/**
 * the process handler for message.
 * 
 * @author mars.li
 */
public class MessageProcesser {

    private Context mContext;

    private MessageDataCache mImageLoader;

    /**
     * Constructor.
     * 
     * @return
     */
    public MessageDataCache getImageLoader() {
        return mImageLoader;
    }

    /**
     * Constructor.
     * 
     * @param mContext
     */
    public MessageProcesser(Context mContext) {
        this.mContext = mContext;
        mImageLoader = MessageDataCache.getCacheLoader(mContext);
    }

    /**
     * process voice.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processVoice(MessageGridViewHolder viewHolder, Msg msg) {
        String time;

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** type display */
        viewHolder.message_content_type.setText(R.string.type_voice);

        /** thumbnail display */
        viewHolder.message_img.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        lp.height = getDiemnsionPixelSize(mContext, R.dimen.VOICE_THUMB_HEIGHT);
        lp.width = getDiemnsionPixelSize(mContext, R.dimen.VOICE_THUMB_WIDTH);
        viewHolder.message_img.setBackgroundResource(0);
        // viewHolder.message_img.setImageResource(R.drawable.msg_bg_sound);
        viewHolder.message_img.setBackgroundResource(R.anim.voice_play_anim);
        viewHolder.message_img.setTag(("voice" + msg.mId).hashCode());

        /** description display */
        if (TextUtils.isEmpty(msg.mTitle) || (msg.mTitle.equals("null"))) {
            viewHolder.content_description.setText("");

        } else {
            viewHolder.content_description.setText("" + msg.mTitle);

        }
        viewHolder.content_description.setTag(msg.mId);
        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.listen_resource));

    }

    /**
     * process music.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processMusic(MessageGridViewHolder viewHolder, Msg msg) {
        String time;

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** type display */
        viewHolder.message_content_type.setText(R.string.type_music);

        /** thumbnail display */
        viewHolder.message_img.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        lp.height = getDiemnsionPixelSize(mContext, R.dimen.MUSIC_THUMB_HEIGHT);
        lp.width = getDiemnsionPixelSize(mContext, R.dimen.MUSIC_THUMB_WIDTH);
        viewHolder.message_img.setVisibility(View.VISIBLE);
        viewHolder.message_img.setBackgroundResource(R.drawable.msg_music_img);
        viewHolder.message_img.setTag(("music" + msg.mId).hashCode());

        /** description display */
        if (TextUtils.isEmpty(msg.mTitle) || (msg.mTitle.equals("null"))) {
            viewHolder.content_description.setText("");

        } else {
            viewHolder.content_description.setText("" + msg.mTitle);
        }
        viewHolder.content_description.setTag(msg.mId);
        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.play_resource));
    }

    /**
     * process apk.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processApk(MessageGridViewHolder viewHolder, Msg msg) {
        String time;

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** thumbnail display */
        viewHolder.message_img.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        lp.height = getDiemnsionPixelSize(mContext, R.dimen.APK_THUMB_HEIGHT);
        lp.width = getDiemnsionPixelSize(mContext, R.dimen.APK_THUMB_WIDTH);
        // resId = mContext.getResources().getIdentifier(msg.mThumb, "drawable",
        // mContext.getPackageName());
        // viewHolder.message_img.setImageResource(resId);
        mImageLoader.loadImage(msg.mThumb, viewHolder.message_img, R.drawable.ic_launcher, R.drawable.ic_launcher,
                R.drawable.ic_launcher);
        /** type display */
        viewHolder.message_content_type.setText(R.string.type_apk);

        /** description display */
        if (msg.mTitle != null) {
            viewHolder.content_description.setText("" + msg.mTitle);
        } else {
            viewHolder.content_description.setText("");
        }
        viewHolder.content_description.setTag(msg.mId);
        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.install_resource));

    }

    /**
     * process video.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processVideo(MessageGridViewHolder viewHolder, Msg msg) {
        String time;

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** type display */
        viewHolder.message_content_type.setText(R.string.type_video);

        /** thumbnail display */
        viewHolder.message_img.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        lp.height = getDiemnsionPixelSize(mContext, R.dimen.VIDEO_THUMB_HEIGHT);
        lp.width = getDiemnsionPixelSize(mContext, R.dimen.VIDEO_THUMB_WIDTH);
        mImageLoader.loadImage(msg.mThumb, viewHolder.message_img, R.drawable.msg_video_img, R.drawable.msg_video_img,
                R.drawable.msg_video_img);

        /** play icon display */
        // ImageView playIcon = (ImageView)
        // viewHolder.findViewById(R.id.message_play_icon);
        // viewHolder.message_play_icon.setVisibility(View.VISIBLE);

        /** description display */
        if (TextUtils.isEmpty(msg.mTitle) || (msg.mTitle.equals("null"))) {
            viewHolder.content_description.setText("");

        } else {
            viewHolder.content_description.setText("" + msg.mTitle);

        }
        viewHolder.content_description.setTag(msg.mId);

        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.play_resource));

    }

    /**
     * process text.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processText(MessageGridViewHolder viewHolder, Msg msg) {
        String time;
        // convertView = mInflater.inflate(R.layout.message_item, null);

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** type display */
        viewHolder.message_content_type.setText(R.string.type_text);

        /**
         * thumbnail display ,the imageview should not be GONE ,or the layout
         * witdh will be 0
         */
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        lp.height = 0;
        lp.width = getDiemnsionPixelSize(mContext, R.dimen.TEXT_THUMB_WIDTH);
        viewHolder.message_img.setVisibility(View.INVISIBLE);

        /** data display */
        viewHolder.content_description.setTextSize(getDiemnsionPixelSize(mContext, R.dimen.TEXT_SIZE));
        viewHolder.content_description.setSingleLine(false);
        viewHolder.content_description.setGravity(Gravity.CENTER);
        viewHolder.content_description.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        viewHolder.content_description.getLayoutParams().width = getDiemnsionPixelSize(mContext,
                R.dimen.TEXT_THUMB_WIDTH);
        viewHolder.content_description.setMaxLines(7);
        viewHolder.content_description.setEllipsize(TruncateAt.END);
        viewHolder.content_description.setTextColor(mContext.getResources().getColor(android.R.color.black));
        viewHolder.content_description.setBackgroundResource(R.drawable.msg_bg_txt);
        viewHolder.content_description.setText("" + msg.mData.replace("\n", ""));
        viewHolder.content_description.setTag(msg.mId);
        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.inspect_resource));
        // viewHolder.message_play.setText("" +
        // mContext.getResources().getString(R.string.inspect_resource));
        // viewHolder.message_play.setText(" ");
        // viewHolder.message_play_layout.setBackgroundResource(0);
        // ImageView img = (ImageView)
        // viewHolder.message_play_layout.findViewById(R.id.message_play_layout_icon);
        // img.setVisibility(View.INVISIBLE);

    }

    /**
     * process image.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processImage(MessageGridViewHolder viewHolder, Msg msg) {
        String time;

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** thumbnail display */
        viewHolder.message_img.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        // lp.height = getDiemnsionPixelSize(mContext,
        // R.dimen.IMAGE_THUMB_HEIGHT);
        // lp.width = getDiemnsionPixelSize(mContext,
        // R.dimen.IMAGE_THUMB_WIDTH);
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.width = LayoutParams.WRAP_CONTENT;
        viewHolder.message_img.setAdjustViewBounds(true);
        /*
         * resId = mContext.getResources().getIdentifier(msg.mData, "drawable",
         * mContext.getPackageName());
         * viewHolder.message_img.setImageResource(resId);
         */
        mImageLoader.loadImage(msg.mData, viewHolder.message_img, R.drawable.msg_photo_img, R.drawable.msg_photo_img,
                R.drawable.msg_photo_img);
        /** type display */
        viewHolder.message_content_type.setText(R.string.type_image);

        /** description display */
        viewHolder.content_description.setText("");
        viewHolder.content_description.setTag(msg.mId);

        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.inspect_resource));

    }

    /**
     * process epg.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processEpg(MessageGridViewHolder viewHolder, Msg msg) {
        String time;

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** type display */
        viewHolder.message_content_type.setText(R.string.type_epg);

        viewHolder.message_img.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        lp.height = getDiemnsionPixelSize(mContext, R.dimen.EPG_THUMB_HEIGHT);
        lp.width = getDiemnsionPixelSize(mContext, R.dimen.EPG_THUMB_WIDTH);
        // resId = mContext.getResources().getIdentifier(msg.mThumb, "drawable",
        // mContext.getPackageName());
        // viewHolder.message_img.setImageResource(resId);
        mImageLoader.loadImage(msg.mThumb, viewHolder.message_img, R.drawable.msg_movie_img, R.drawable.msg_movie_img,
                R.drawable.msg_movie_img);
        /** description display */
        String title = String.valueOf(msg.mTitle);
        int position = title.indexOf("$");
        viewHolder.content_description.setVisibility(View.VISIBLE);
        if (position != -1) {
            viewHolder.pgm_index.setVisibility(View.VISIBLE);
            viewHolder.pgm_index.setText(title.substring(0, position));
            viewHolder.content_description.setText(title.substring(position + 1, title.length()));
        } else {
            VideoInfo epgCacheInfo = Util.parseVideoInfo(msg);
            if (!("movie".equals(epgCacheInfo.channelCode))) {
                viewHolder.pgm_index.setVisibility(View.VISIBLE);
                viewHolder.pgm_index.setText(String.format(mContext.getResources().getString(R.string.program_index),
                        epgCacheInfo.programIndex + 1));
            }
            viewHolder.content_description.setText("" + msg.mTitle);
        }

        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.play_resource));

    }
    
    /**
     * process live.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processLive(MessageGridViewHolder viewHolder, Msg msg) {
        String time;

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** type display */
        viewHolder.message_content_type.setText(R.string.type_live);

        viewHolder.message_img.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        lp.height = getDiemnsionPixelSize(mContext, R.dimen.EPG_THUMB_HEIGHT);
        lp.width = getDiemnsionPixelSize(mContext, R.dimen.EPG_THUMB_WIDTH);
        // resId = mContext.getResources().getIdentifier(msg.mThumb, "drawable",
        // mContext.getPackageName());
        // viewHolder.message_img.setImageResource(resId);
        mImageLoader.loadImage(msg.mThumb, viewHolder.message_img, R.drawable.msg_movie_img, R.drawable.msg_movie_img,
                R.drawable.msg_movie_img);
        /** description display */
        if (TextUtils.isEmpty(msg.mTitle) || (msg.mTitle.equals("null"))) {
            viewHolder.content_description.setText("");

        } else {
            viewHolder.content_description.setText("" + msg.mTitle);

        }
        viewHolder.content_description.setTag(msg.mId);
        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.play_resource));

    }

    /**
     * process Document.
     * 
     * @param viewHolder
     * @param msg
     */
    public void processDocument(MessageGridViewHolder viewHolder, Msg msg) {
        String time;

        /** time display */
        time = Util.mill2String(msg.mTime, Constants.TIME_YMD_HM);
        viewHolder.timeLabel.setText("" + time);

        /** type display */
        viewHolder.message_content_type.setText(R.string.type_document);

        /** thumbnail display */
        viewHolder.message_img.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = viewHolder.message_img.getLayoutParams();
        lp.height = getDiemnsionPixelSize(mContext, R.dimen.DOC_THUMB_HEIGHT);
        lp.width = getDiemnsionPixelSize(mContext, R.dimen.DOC_THUMB_WIDTH);
        viewHolder.message_img.setVisibility(View.VISIBLE);
        String postfix = msg.mTitle.toLowerCase(Locale.US);
        if (postfix.endsWith(TXT_POSTFIX)) {
            viewHolder.message_img.setBackgroundResource(R.drawable.documents_icon_text);
        } else if (postfix.endsWith(DOC_POSTFIX) || postfix.endsWith(DOCX_POSTFIX)) {
            viewHolder.message_img.setBackgroundResource(R.drawable.documents_icon_doc);
        } else if (postfix.endsWith(XLS_POSTFIX) || postfix.endsWith(XLSX_POSTFIX)) {
            viewHolder.message_img.setBackgroundResource(R.drawable.documents_icon_xls);
        } else if (postfix.endsWith(PPT_POSTFIX) || postfix.endsWith(PPTX_POSTFIX)) {
            viewHolder.message_img.setBackgroundResource(R.drawable.documents_icon_ppt);
        } else if (postfix.endsWith(PDF_POSTFIX)) {
            viewHolder.message_img.setBackgroundResource(R.drawable.documents_icon_pdf);
        } else if (postfix.endsWith(EPUB_POSTFIX)) {
            viewHolder.message_img.setBackgroundResource(R.drawable.documents_icon_epub);
        } else {
            viewHolder.message_img.setBackgroundResource(R.drawable.documents_icon_other);
        }
        viewHolder.message_img.setTag(("document" + msg.mId).hashCode());

        /** description display */
        if (TextUtils.isEmpty(msg.mTitle) || (msg.mTitle.equals("null"))) {
            viewHolder.content_description.setText("");
        } else {
            viewHolder.content_description.setText("" + msg.mTitle);
        }
        viewHolder.content_description.setTag(msg.mId);
        /** message play button display */
        viewHolder.message_play.setText("" + mContext.getResources().getString(R.string.inspect_resource));
    }

    private int getDiemnsionPixelSize(Context context, int id) {
        return Util.getDiemnsionPixelSize(context, id);
    }

}
