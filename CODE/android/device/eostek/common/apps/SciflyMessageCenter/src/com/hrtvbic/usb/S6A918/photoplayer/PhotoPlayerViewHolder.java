package com.hrtvbic.usb.S6A918.photoplayer;

import com.eostek.scifly.messagecenter.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 初始化图片控制条组件
 * 
 * @author 
 * 
 */
public class PhotoPlayerViewHolder {

	private PhotoPlayerActivity mPhotoPlayAct;

	// 存放显示图片
	protected LinearLayout imageSurfaceLL;
	protected LinearLayout imageLL;
	
	protected ImageSurfaceView mImageSurface;
    protected ImageViewTouch mImageView;
	// 控件定义
	protected ImageView bt_photoPre;
	protected ImageView bt_photoPlay;
	protected ImageView bt_photoNext;
	protected ImageView bt_photoEnlarge;
	protected ImageView bt_photoPptPlayMode;
	protected ImageView bt_photoPlayMode;
	//protected ImageView bt_photoInfo;
	protected ImageView bt_photoTRight;
	//protected ImageView bt_music_play;

	protected TextView current_num;
	protected TextView total_num;
	protected RelativeLayout photo_right_list_bg;
	protected RelativeLayout photo_left_list;  //左侧包含图片列表的layout
	protected ImageView previousphotoImageView;//上方箭头下面的图片
	protected ImageView firstPhotoImageView;//右侧第一张图片
    protected ImageView secendPhotoImageView;//右侧第二张图片
    protected ImageView bgPhotoImageView;//右侧第2张图的背景
    protected ImageView thirdPhotoImageView;//右侧第3张图
    protected ImageView fourthPhotoImageView;//右侧第4张图
    protected ImageView nextphotoImageView;//下方箭头下面的图片
    protected RelativeLayout photoLeftRelativelayout;
    
    protected RelativeLayout photo_changephoto;//下方提示文字
    protected RelativeLayout photo_stop;  //下方提示文字
    protected RelativeLayout photo_fun_enl;
    //protected ImageView photo_showmenu;//提示menu键出菜单
    protected RelativeLayout photo_fun_lenearlayout;//点击放大后 新弹出来的用来显示缩略图的框
    protected ProgressBar photo_progressbar;
    protected RelativeLayout photo_move;
    protected ImageView photo_ok_enlImage;
    protected RelativeLayout photo_autoplay;
    protected TextView textphotoPre;
    protected   TextView textphotoPlay;
	protected TextView textphotoNext;
	protected TextView textphotoEnlarge;
	protected TextView textphotoPptPlayMode;
	protected TextView textphotoPlayMode;
	protected TextView textphotoTurnRight;
	protected ImageView photo_play;
	protected ImageView photo_upImage;//上箭头
	protected ImageView photo_downImage;//下箭头
	protected LinearLayout photo_play_mode_order;
	protected RelativeLayout photo_play_mode_selector_area;
	protected LinearLayout photo_play_mode_all_repeat;
	protected LinearLayout photo_play_mode_random;
	protected RelativeLayout ppt_play_mode_selector_area;
	protected LinearLayout ppt_play_mode_a;
	protected LinearLayout ppt_play_mode_b;
	protected LinearLayout ppt_play_mode_c;
	protected LinearLayout ppt_play_mode_d;
	protected LinearLayout ppt_play_mode_e;
	protected LinearLayout ppt_play_mode_f;
	protected LinearLayout ppt_play_mode_g;
	protected LinearLayout ppt_play_mode_h;
	public PhotoPlayerViewHolder(PhotoPlayerActivity act) {
		mPhotoPlayAct = act;
	}

	/**
	 * 获取按钮
	 * 
	 * @param rootLayout
	 */
	void findViews() {
		
		photo_progressbar = (ProgressBar)mPhotoPlayAct.findViewById(R.id.photo_progress_bar);
		
		imageSurfaceLL = (LinearLayout) mPhotoPlayAct.findViewById(R.id.surface_view_ll);
		imageLL = (LinearLayout) mPhotoPlayAct.findViewById(R.id.photo_image_ll);
		
		mImageSurface = (ImageSurfaceView) mPhotoPlayAct.findViewById(R.id.photo_surface);
		mImageView = (ImageViewTouch) mPhotoPlayAct.findViewById(R.id.photo_image);
		photo_right_list_bg = (RelativeLayout)mPhotoPlayAct.findViewById(R.id.photo_right_list_bg);
		photo_left_list = (RelativeLayout)mPhotoPlayAct.findViewById(R.id.photo_right_list);
		bt_photoPre = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_player_previous);		
		bt_photoPlay = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_play);
		//bt_photoPlay.setBackgroundResource(R.drawable.player_icon_play_focus);
		bt_photoNext = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_next);
		bt_photoEnlarge = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_enlarge);
		bt_photoPptPlayMode = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_ppt_play_mode);
		bt_photoPlayMode = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_play_mode);
		//bt_photoInfo = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_info);
		bt_photoTRight = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_turn_right);
		//bt_music_play  = (ImageView) mPhotoPlayAct.findViewById(R.id.photo_play_control);
		firstPhotoImageView=(ImageView)mPhotoPlayAct.findViewById(R.id.photo_firstphoto);
        secendPhotoImageView=(ImageView)mPhotoPlayAct.findViewById(R.id.photo_secendphoto);
        bgPhotoImageView=(ImageView)mPhotoPlayAct.findViewById(R.id.photo_bgphoto2);
        thirdPhotoImageView=(ImageView)mPhotoPlayAct.findViewById(R.id.photo_thirdphoto);
        fourthPhotoImageView=(ImageView)mPhotoPlayAct.findViewById(R.id.photo_fourthphoto);
        previousphotoImageView=(ImageView)mPhotoPlayAct.findViewById(R.id.photo_previousphoto);
        nextphotoImageView=(ImageView)mPhotoPlayAct.findViewById(R.id.photo_nextphoto);
        photo_upImage  =(ImageView)mPhotoPlayAct.findViewById(R.id.photo_up);
        photo_downImage  =(ImageView)mPhotoPlayAct.findViewById(R.id.photo_down);
        photoLeftRelativelayout = (RelativeLayout)mPhotoPlayAct.findViewById(R.id.photo_left_relativelayout);
        //previousphotoImageView.setAlpha(60);//设置透明度
        //nextphotoImageView.setAlpha(60);//设置透明度
        mImageView.setDrawingCacheEnabled(false);
        photo_fun_lenearlayout = (RelativeLayout)mPhotoPlayAct.findViewById(R.id.photo_fun_enl_lenearlayout);
        photo_changephoto = (RelativeLayout)mPhotoPlayAct.findViewById(R.id.relativelayout_photo_changephoto);
        photo_stop = (RelativeLayout)mPhotoPlayAct.findViewById(R.id.relativelayout_photo_stop);
        photo_fun_enl=(RelativeLayout)mPhotoPlayAct.findViewById(R.id.relativelayout_photo_ok_enl );//提示按ok放大
        photo_move=(RelativeLayout)mPhotoPlayAct.findViewById(R.id.relativelayout_photo_move);
        photo_ok_enlImage=(ImageView)mPhotoPlayAct.findViewById(R.id.photo_fun_enl);
        photo_autoplay=(RelativeLayout)mPhotoPlayAct.findViewById(R.id.relativelayout_photo_autoplay);
        //photo_showmenu = (ImageView)mPhotoPlayAct.findViewById(R.id.photo_showmenu);
        photo_play = (ImageView)mPhotoPlayAct.findViewById(R.id.photo_autoplay);
        textphotoPre = (TextView)mPhotoPlayAct.findViewById(R.id.photo_pre_text);
    	textphotoPlay = (TextView)mPhotoPlayAct.findViewById(R.id.photo_play_text);
    	textphotoNext = (TextView)mPhotoPlayAct.findViewById(R.id.photo_next_text);
    	textphotoEnlarge = (TextView)mPhotoPlayAct.findViewById(R.id.photo_enlarge_text);
    	textphotoPptPlayMode = (TextView)mPhotoPlayAct.findViewById(R.id.photo_ppt_play_mode_text);
    	textphotoPlayMode = (TextView)mPhotoPlayAct.findViewById(R.id.photo_play_mode_text);
    	textphotoTurnRight = (TextView)mPhotoPlayAct.findViewById(R.id.photo_turn_right_text);
    	
    	
    	photo_play_mode_order = (LinearLayout)mPhotoPlayAct.findViewById(R.id.photo_play_mode_order);
    	photo_play_mode_selector_area = (RelativeLayout)mPhotoPlayAct.findViewById(R.id.photo_play_mode_selector_area);
    	photo_play_mode_all_repeat = (LinearLayout)mPhotoPlayAct.findViewById(R.id.photo_play_mode_all_repeat);
    	photo_play_mode_random = (LinearLayout)mPhotoPlayAct.findViewById(R.id.photo_play_mode_random);
    	
    	ppt_play_mode_selector_area = (RelativeLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_selector_area);
    	ppt_play_mode_a = (LinearLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_a);
    	ppt_play_mode_b = (LinearLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_b);
    	ppt_play_mode_c = (LinearLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_c);
    	ppt_play_mode_d = (LinearLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_d);
    	ppt_play_mode_e = (LinearLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_e);
    	ppt_play_mode_f = (LinearLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_f);
    	ppt_play_mode_g = (LinearLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_g);
    	ppt_play_mode_h = (LinearLayout)mPhotoPlayAct.findViewById(R.id.ppt_play_mode_h);
	}
	
	//
	public void setOnClickListener(OnClickListener listener)
	{
		if(listener!=null)
		{
			bt_photoPre.setOnClickListener(listener);
			bt_photoPlay.setOnClickListener(listener);	
			bt_photoNext.setOnClickListener(listener);
			bt_photoEnlarge.setOnClickListener(listener);
			bt_photoPptPlayMode.setOnClickListener(listener);
			bt_photoPlayMode.setOnClickListener(listener);
			bt_photoTRight.setOnClickListener(listener);
			photo_upImage.setOnClickListener(listener);
			previousphotoImageView.setOnClickListener(listener);
			firstPhotoImageView.setOnClickListener(listener);
			secendPhotoImageView.setOnClickListener(listener);
			thirdPhotoImageView.setOnClickListener(listener);
			fourthPhotoImageView.setOnClickListener(listener);
			nextphotoImageView.setOnClickListener(listener);
			photo_downImage.setOnClickListener(listener);
			photo_play_mode_order.setOnClickListener(listener);
			photo_play_mode_all_repeat.setOnClickListener(listener);
			photo_play_mode_random.setOnClickListener(listener);
			ppt_play_mode_a.setOnClickListener(listener);
			ppt_play_mode_b.setOnClickListener(listener);
			ppt_play_mode_c.setOnClickListener(listener);
			ppt_play_mode_d.setOnClickListener(listener);
			ppt_play_mode_e.setOnClickListener(listener);
			ppt_play_mode_f.setOnClickListener(listener);
			ppt_play_mode_g.setOnClickListener(listener);
			ppt_play_mode_h.setOnClickListener(listener);
			photo_fun_lenearlayout.setOnClickListener(listener);
			photoLeftRelativelayout.setOnClickListener(listener);
		}		
	}

	//
	public void setAllUnSelect(boolean pptPlaying,boolean isPlaying)
	{
		setPhotoPreSelect(false);
		setPhotoPlaySelect(false,pptPlaying);
		setPhotoNextSelect(false);
		setPhotoEnlargeSelect(false);
		setPhotoNarrowSelect(false);
		setPhotoTurnRightSelect(false);
		setPhotoTurnLeftSelect(false);
	}
	
	public void setPhotoPreSelect(boolean bSelect)
	{
		if(bSelect)
		{
			bt_photoPre.setFocusable(true);
	    	textphotoPre.setVisibility(View.VISIBLE);
			bt_photoPre.setBackgroundResource(R.drawable.list_focused_holo2);
		}
		else
		{
			bt_photoPre.setFocusable(false);
//			bt_photoPre.setBackgroundResource(R.drawable.player_icon_previous);
			textphotoPre.setVisibility(View.INVISIBLE);
			bt_photoPre.setBackgroundResource(R.drawable.one_pixel);
		}
	}
	
	public void setPhotoPlaySelect(boolean bSelect,boolean isPlaying)
	{
		bt_photoPlay.setFocusable(bSelect);		
		if(bSelect)
		{
			if(isPlaying)
			{
				bt_photoPlay.setBackgroundResource(R.drawable.photo_player_icon_pause_focus);
				bt_photoPlay.setBackgroundResource(R.drawable.list_focused_holo2);
				textphotoPlay.setVisibility(View.VISIBLE);
			}
			else
			{
				bt_photoPlay.setBackgroundResource(R.drawable.photo_player_icon_play_focus);
				bt_photoPlay.setBackgroundResource(R.drawable.list_focused_holo2);
				textphotoPlay.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			if(isPlaying)
			{
				bt_photoPlay.setBackgroundResource(R.drawable.photo_player_icon_pause_focus);
				
				bt_photoPlay.setBackgroundResource(R.drawable.one_pixel);
				textphotoPlay.setVisibility(View.INVISIBLE);
			}
			else
			{
				bt_photoPlay.setBackgroundResource(R.drawable.photo_player_icon_play_focus);
				
				bt_photoPlay.setBackgroundResource(R.drawable.one_pixel);
				textphotoPlay.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void setPhotoNextSelect(boolean bSelect)
	{
		if(bSelect)
		{
			bt_photoNext.setFocusable(true);
//			bt_photoNext.setBackgroundResource(R.drawable.player_icon_next_focus);
			bt_photoNext.setBackgroundResource(R.drawable.list_focused_holo2);
			textphotoNext.setVisibility(View.VISIBLE);
		}
		else
		{
			bt_photoNext.setFocusable(false);
//			bt_photoNext.setBackgroundResource(R.drawable.player_icon_next);
			bt_photoNext.setBackgroundResource(R.drawable.one_pixel);
			textphotoNext.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setPhotoEnlargeSelect(boolean bSelect)
	{
		if(bSelect)
		{
			bt_photoEnlarge.setFocusable(true);
//			bt_photoEnlarge.setBackgroundResource(R.drawable.player_icon_amplification_focus);
			bt_photoEnlarge.setBackgroundResource(R.drawable.list_focused_holo2);
			textphotoEnlarge.setVisibility(View.VISIBLE);
		}
		else
		{
			bt_photoEnlarge.setFocusable(false);
//			bt_photoEnlarge.setBackgroundResource(R.drawable.player_icon_amplification);
			bt_photoEnlarge.setBackgroundResource(R.drawable.one_pixel);
			textphotoEnlarge.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setPhotoNarrowSelect(boolean bSelect)
	{
		if(bSelect)
		{
			bt_photoPptPlayMode.setFocusable(true);
//			bt_photoPptPlayMode.setBackgroundResource(R.drawable.player_icon_narrow_focus);
			bt_photoPptPlayMode.setBackgroundResource(R.drawable.list_focused_holo2);
			textphotoPptPlayMode.setVisibility(View.VISIBLE);
		}
		else
		{
			bt_photoPptPlayMode.setFocusable(false);
//			bt_photoPptPlayMode.setBackgroundResource(R.drawable.player_icon_narrow);
			bt_photoPptPlayMode.setBackgroundResource(R.drawable.one_pixel);
			textphotoPptPlayMode.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setPhotoTurnLeftSelect(boolean bSelect)
	{
		if(bSelect)
		{
			bt_photoPlayMode.setFocusable(true);
//			bt_photoPlayMode.setBackgroundResource(R.drawable.player_icon_left_focus);
			bt_photoPlayMode.setBackgroundResource(R.drawable.list_focused_holo2);
			textphotoPlayMode.setVisibility(View.VISIBLE);
		}
		else
		{
			bt_photoPlayMode.setFocusable(false);
//			bt_photoPlayMode.setBackgroundResource(R.drawable.player_icon_left);
			bt_photoPlayMode.setBackgroundResource(R.drawable.one_pixel);
			textphotoPlayMode.setVisibility(View.INVISIBLE);
		}
	}
	

	public void setPhotoTurnRightSelect(boolean bSelect)
	{
		if(bSelect)
		{
			bt_photoTRight.setFocusable(true);
//			bt_photoTRight.setBackgroundResource(R.drawable.player_icon_right_focus);
			bt_photoTRight.setBackgroundResource(R.drawable.list_focused_holo2);
			textphotoTurnRight.setVisibility(View.VISIBLE);
		}
		else
		{
			bt_photoTRight.setFocusable(false);
//			bt_photoTRight.setBackgroundResource(R.drawable.player_icon_right);
			bt_photoTRight.setBackgroundResource(R.drawable.one_pixel);
			textphotoTurnRight.setVisibility(View.INVISIBLE);
		}
	}

	public void showImageSurfaceViewLL()
	{
		imageSurfaceLL.setVisibility(View.VISIBLE);
		imageLL.setVisibility(View.GONE);
	}
	
	public void showImageLL()
	{
		imageSurfaceLL.setVisibility(View.GONE);
		imageLL.setVisibility(View.VISIBLE);
	}
}
