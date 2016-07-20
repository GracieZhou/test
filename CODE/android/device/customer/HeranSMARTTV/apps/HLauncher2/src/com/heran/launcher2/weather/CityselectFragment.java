package com.heran.launcher2.weather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.MainViewHolder.MyMoreOnClickListener;
import com.heran.launcher2.MainViewHolder.MyMoreOnKeyListener;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.widget.ViewBean;

public class CityselectFragment extends PublicFragment {

	private String TAG = "cityselectFragment";
	
	private View mView;
	
	private HomeActivity mContext;
	
	private MainViewHolder mHolder;
	
	private ViewBean mViewBean;
	
	private TextView city_title;
	
	public ImageButton fragmentBtn;
	
	private TextView citytxt1,citytxt2,citytxt3,citytxt4,citytxt5,citytxt6,citytxt7,citytxt8,citytxt9,citytxt10,
					 citytxt11,citytxt12,citytxt13,citytxt14,citytxt15,citytxt16,citytxt17,citytxt18,citytxt19,
					 citytxt20,citytxt21,citytxt22;
	
	private ImageButton btn_ok,btn_cancel;
	
	private SharedPreferences sp;
	
	private SharedPreferences.Editor editor;
	
	private int cityID =0;
	int cityFocus=0;
	
	private ImageView map,map_left;
	
	private FrameLayout city_frame;
	
	private MyOnKey mMyOnKey;
	
	public MyMoreOnClickListener myMoreOnClickListener;

    public MyMoreOnKeyListener myMoreOnKeyListener;
	
	private String[] citys ;
	
	private TextView[] txt = null;
	
	
	
    public CityselectFragment(HomeActivity context) {
        super();
        this.mContext = context;
        this.mHolder = mContext.mviewHolder;
        mViewBean = new ViewBean(null, null);
      
    }
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        
	        Log.d(TAG, "onCreateView");
	        mView = inflater.inflate(R.layout.city_layout, container, false);
	        try{
	        	init();
	        }catch(Exception e){
	            Log.d(TAG, e.toString());
	        }
	        return mView;
	    }
	 
	 private void init(){
		    citys = mContext.getResources().getStringArray(R.array.city_array);
	        city_title = (TextView) mView.findViewById(R.id.city_title);
	        map = (ImageView) mView.findViewById(R.id.map);
	        map_left = (ImageView) mView.findViewById(R.id.map_left);
	        city_frame =(FrameLayout) getActivity().findViewById(R.id.city_frame);
	        fragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
	        mMyOnKey = new MyOnKey();
	        myMoreOnClickListener = mHolder.new MyMoreOnClickListener();
	        myMoreOnKeyListener = mHolder.new MyMoreOnKeyListener();
	        
	        fragmentBtn.setOnClickListener(myMoreOnClickListener);
	        fragmentBtn.setOnKeyListener(myMoreOnKeyListener);
	        
		 txt = new TextView[]{
		 citytxt1 = (TextView) mView.findViewById(R.id.city1),
		 citytxt2 = (TextView) mView.findViewById(R.id.city2),
		 citytxt3 = (TextView) mView.findViewById(R.id.city3),
		 citytxt4 = (TextView) mView.findViewById(R.id.city4),
		 citytxt5 = (TextView) mView.findViewById(R.id.city5),
		 citytxt6 = (TextView) mView.findViewById(R.id.city6),
		 citytxt7 = (TextView) mView.findViewById(R.id.city7),
		 citytxt8 = (TextView) mView.findViewById(R.id.city8),
		 citytxt9 = (TextView) mView.findViewById(R.id.city9),
		 citytxt10 = (TextView) mView.findViewById(R.id.city10),
		 citytxt11 = (TextView) mView.findViewById(R.id.city11),
		 citytxt12 = (TextView) mView.findViewById(R.id.city12),
		 citytxt13 = (TextView) mView.findViewById(R.id.city13),
		 citytxt14 = (TextView) mView.findViewById(R.id.city14),
		 citytxt15 = (TextView) mView.findViewById(R.id.city15),
		 citytxt16 = (TextView) mView.findViewById(R.id.city16),
		 citytxt17 = (TextView) mView.findViewById(R.id.city17),
		 citytxt18 = (TextView) mView.findViewById(R.id.city18),
		 citytxt19 = (TextView) mView.findViewById(R.id.city19),
		 citytxt20 = (TextView) mView.findViewById(R.id.city20),
		 citytxt21 = (TextView) mView.findViewById(R.id.city21),
		 citytxt22 = (TextView) mView.findViewById(R.id.city22),
		 };
		 openClickAndFocus();

		 sp = mContext.getSharedPreferences("SP", 0);
         editor = sp.edit();
         
         city_title.setText(WeatherHelper.getCurrentCityName(getActivity()));
		 
		 btn_ok = (ImageButton) mView.findViewById(R.id.city_confirm);
		 btn_cancel = (ImageButton) mView.findViewById(R.id.city_cancel);
		 
		 for(int i=0;i<txt.length;i++){
			 textOnFocusListener(txt[i]);
			 textOnClickListener(txt[i]);
			 textOnKeyListener(txt[i]);
		 }
		 buttonListener();
		
		 
		 
	 }
	 
	 private void textOnKeyListener(TextView txt){
		 txt.setOnKeyListener(mMyOnKey);
	 }
	 
	 private void buttonListener(){
		 btn_ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG,"btn_ok");
					 editor.putString("city",citys[cityID]);
	                 editor.commit();//要記得加
	                 closeClickAndFocus();
//					city_frame.setVisibility(View.GONE);
	                changCity();
					finish();
				}
				
			});
			 
			 btn_ok.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean focus) {
					if(focus){
						 SetTextClickColor();
						 btn_ok.setImageResource(R.drawable.city_bt6);
					}					
					else{
						btn_ok.setImageResource(R.drawable.city_bt3);
					}
				}
			});
			 
			 btn_ok.setOnKeyListener(mMyOnKey);
			 btn_cancel.setOnKeyListener(mMyOnKey);
			 btn_cancel.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean focus) {
						if(focus){
							SetTextClickColor();
							btn_cancel.setImageResource(R.drawable.city_bt7);
						}else{
							btn_cancel.setImageResource(R.drawable.city_bt5);
						}
						
					}
				});
			 
			 btn_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG,"btn_cancel");
					closeClickAndFocus();
					finish();
//					city_frame.setVisibility(View.GONE);
				}
			});
	 }
	 
	 
	 @Override
	public void onResume() {
		// TODO Auto-generated method stub
		
		
		String city = sp.getString("city","臺北市");       
        for (int i = 0; i < citys.length; i++) {
            if (city.equals(citys[i])) {
                cityID = i;
            }
        }
        
		if(cityID!=0){
        txt[cityID].setTextColor(Color.WHITE);
		txt[cityID].setBackground(mContext.getResources().getDrawable(R.drawable.city_textbg)); 
		}
		citytxt1.requestFocus();
		
		super.onResume();
	}
	 
	 private void finish(){
//		 getActivity().getFragmentManager().beginTransaction().remove(this).commit();
		 mHolder.updatePIFragment(Constants.CITYSELECT_CLOSE,2);
	 }
	 
	 private void textOnFocusListener( final TextView txt){
		 txt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean arg1) {
				 Log.d(TAG,"onFocusChange");
//				 drawFocus(arg0);
				// TODO Auto-generated method stub
				if(arg1){
					 
					 Log.d(TAG,"isfocus");
					 switch (v.getId()) {
						case R.id.city1:
							cityFocus = 0;
							break;
						case R.id.city2:
							cityFocus = 1;
							break;
						case R.id.city3:
							cityFocus = 2;
							break;
						case R.id.city4:
							cityFocus = 3;
							break;
						case R.id.city5:
							cityFocus = 4;
							break;
						case R.id.city6:
							cityFocus = 5;
							break;
						case R.id.city7:
							cityFocus = 6;
							break;
						case R.id.city8:
							cityFocus = 7;
							break;
						case R.id.city9:
							cityFocus = 8;
							break;
						case R.id.city10:
							cityFocus = 9;
							break;
						case R.id.city11:
							cityFocus = 10;
							break;
						case R.id.city12:
							cityFocus = 11;
							break;
						case R.id.city13:
							cityFocus = 12;
							break;
						case R.id.city14:
							cityFocus = 13;
							break;
						case R.id.city15:
							cityFocus = 14;
							break;
						case R.id.city16:
							cityFocus = 15;
							break;
						case R.id.city17:
							cityFocus = 16;
							break;
						case R.id.city18:
							cityFocus = 17;
							break;
						case R.id.city19:
							cityFocus = 18;
							break;
						case R.id.city20:
							cityFocus = 19;
							break;
						case R.id.city21:
							cityFocus = 20;
							break;
						case R.id.city22:
							cityFocus = 21;
							break;
						default:
							break;
						}
	                showImg(cityFocus);
					SetTextClickColor();
					txt.setTextColor(Color.WHITE);
					txt.setBackground(getResources().getDrawable(R.drawable.city_bt2));
				}else{
					 txt.setTextColor(Color.parseColor("#3300ff"));
					 txt.setBackground(null);	 	
				}																	
			}
		});
	 }
	 // if Text Click TextColor Change
	 protected void SetTextClickColor() {
		 for(int i=0;i<txt.length;i++){
			 if(cityID!=i){
				 txt[i].setTextColor(Color.parseColor("#3300ff"));
				 txt[i].setBackground(null);	 
			 }else{
				 txt[i].setTextColor(Color.WHITE);
				 txt[i].setBackground(mContext.getResources().getDrawable(R.drawable.city_textbg)); 
			 }
			 
		 }
		
	}
	 
	 
	 private void textOnClickListener( final TextView txts){
		 		 
		 txts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(cityID!=cityFocus){
				txt[cityID].setTextColor(Color.parseColor("#3300ff"));
				txt[cityID].setBackground(null);
				}	
				Log.d(TAG, "onClick");
				 switch (v.getId()) {
					case R.id.city1:
						cityID = 0;
						break;
					case R.id.city2:
						cityID = 1;
						break;
					case R.id.city3:
						cityID = 2;
						break;
					case R.id.city4:
						cityID = 3;
						break;
					case R.id.city5:
						cityID = 4;
						break;
					case R.id.city6:
						cityID = 5;
						break;
					case R.id.city7:
						cityID = 6;
						break;
					case R.id.city8:
						cityID = 7;
						break;
					case R.id.city9:
						cityID = 8;
						break;
					case R.id.city10:
						cityID = 9;
						break;
					case R.id.city11:
						cityID = 10;
						break;
					case R.id.city12:
						cityID = 11;
						break;
					case R.id.city13:
						cityID = 12;
						break;
					case R.id.city14:
						cityID = 13;
						break;
					case R.id.city15:
						cityID = 14;
						break;
					case R.id.city16:
						cityID = 15;
						break;
					case R.id.city17:
						cityID = 16;
						break;
					case R.id.city18:
						cityID = 17;
						break;
					case R.id.city19:
						cityID = 18;
						break;
					case R.id.city20:
						cityID = 19;
						break;
					case R.id.city21:
						cityID = 20;
						break;
					case R.id.city22:
						cityID = 21;
						break;
					default:
						break;
					}				 
				 city_title.setText(citys[cityID]);
			}
		});
	 }
	 
	 private void showImg(int id) {
		 Log.d(TAG, "showImg :"+id);
		 map.setBackground(getResources().getDrawable(R.drawable.city_map6));
		 map_left.setBackground(getResources().getDrawable(R.drawable.city_map1));
		 switch (id) {
		case 0:
			map.setBackground(getResources().getDrawable(R.drawable.city_map9));
			break;
		case 1:
			map.setBackground(getResources().getDrawable(R.drawable.city_map8));
			break;
		case 2:
			map.setBackground(getResources().getDrawable(R.drawable.city_map10));
			break;
		case 3:
			map.setBackground(getResources().getDrawable(R.drawable.city_map12));
			break;
		case 4:
			map.setBackground(getResources().getDrawable(R.drawable.city_map11));
			break;
		case 5:
			map.setBackground(getResources().getDrawable(R.drawable.city_map13));
			break;
		case 6:
			map.setBackground(getResources().getDrawable(R.drawable.city_map14));
			break;
		case 7:
			map.setBackground(getResources().getDrawable(R.drawable.city_map15));
			break;
		case 8:
			map.setBackground(getResources().getDrawable(R.drawable.city_map17));
			break;
		case 9:
			map.setBackground(getResources().getDrawable(R.drawable.city_map19));
			break;
		case 10:
			map.setBackground(getResources().getDrawable(R.drawable.city_map20));
			break;
		case 11:
			map.setBackground(getResources().getDrawable(R.drawable.city_map18));
			break;
		case 12:
			map.setBackground(getResources().getDrawable(R.drawable.city_map21));
			break;
		case 13:
			map.setBackground(getResources().getDrawable(R.drawable.city_map7));
			break;
		case 14:
			map.setBackground(getResources().getDrawable(R.drawable.city_map25));
			break;
		case 15:
			map.setBackground(getResources().getDrawable(R.drawable.city_map24));
			break;
		case 16:
			map.setBackground(getResources().getDrawable(R.drawable.city_map16));
			break;
		case 17:
			map.setBackground(getResources().getDrawable(R.drawable.city_map23));
			break;
		case 18:
			map.setBackground(getResources().getDrawable(R.drawable.city_map22));
			break;
		case 19:
			map_left.setBackground(getResources().getDrawable(R.drawable.city_map2));
			break;
		case 20:
			map_left.setBackground(getResources().getDrawable(R.drawable.city_map3));
			break;
		case 21:
			map_left.setBackground(getResources().getDrawable(R.drawable.city_map4));
			break;
	

		default:
			break;
		}
		 
	}
	 
	 
	 class MyOnKey implements OnKeyListener{

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction()==KeyEvent.ACTION_DOWN){
				if(keyCode == KeyEvent.KEYCODE_BACK){
					finish();
					return true;
				}
			switch (v.getId()) {
			case R.id.city1:
				if (v.getId()==R.id.city1 && keyCode == KeyEvent.KEYCODE_DPAD_UP){
					btn_ok.requestFocus();
					return true;
				}
			case R.id.city2:
			case R.id.city3:
			case R.id.city4:
			case R.id.city5:
			case R.id.city6:
			case R.id.city7:
			case R.id.city8:
			case R.id.city9:
			case R.id.city10:
			case R.id.city11:
			case R.id.city12:
			case R.id.city13:
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
					citytxt14.requestFocus();
					return true;
					
				}
				
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
					citytxt20.requestFocus();
					return true;
				}
				break;
			case R.id.city14:
				if (v.getId()==R.id.city14 && keyCode == KeyEvent.KEYCODE_DPAD_UP){
					btn_cancel.requestFocus();
					return true;
				}
			case R.id.city15:
			case R.id.city16:
			case R.id.city17:
			case R.id.city18:
			case R.id.city19:
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
					citytxt1.requestFocus();
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
					return true;
				}
				if (v.getId()==R.id.city19 && keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					btn_cancel.requestFocus();
					return true;
				}
				break;
			case R.id.city20:
				if (v.getId()==R.id.city20 && keyCode == KeyEvent.KEYCODE_DPAD_UP){
					return true;
				}
			case R.id.city21:
			case R.id.city22:
				if (v.getId()==R.id.city22 && keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
					citytxt1.requestFocus();
					return true;
				}
				break;
			case R.id.city_confirm:
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
					citytxt13.requestFocus();
					return true;
				}
				break;
			case R.id.city_cancel:
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
					citytxt19.requestFocus();
					return true;
				}
				break;
			default:
				break;
			}
			
			}
			return false;
		}


		 
	 }
	 
	 private void closeClickAndFocus(){
		 citytxt1.setClickable(false);
		 citytxt1.setFocusable(false);
		 citytxt2.setClickable(false);
		 citytxt2.setFocusable(false);
		 citytxt3.setClickable(false);
		 citytxt3.setFocusable(false);
		 citytxt4.setClickable(false);
		 citytxt4.setFocusable(false);
		 citytxt5.setClickable(false);
		 citytxt5.setFocusable(false);
		 citytxt6.setClickable(false);
		 citytxt6.setFocusable(false);
		 citytxt7.setClickable(false);
		 citytxt7.setFocusable(false);
		 citytxt8.setClickable(false);
		 citytxt8.setFocusable(false);
		 citytxt9.setClickable(false);
		 citytxt9.setFocusable(false);
		 citytxt10.setClickable(false);
		 citytxt10.setFocusable(false);
		 citytxt11.setClickable(false);
		 citytxt11.setFocusable(false);
		 citytxt12.setClickable(false);
		 citytxt12.setFocusable(false);
		 citytxt13.setClickable(false);
		 citytxt13.setFocusable(false);
		 citytxt14.setClickable(false);
		 citytxt14.setFocusable(false);
		 citytxt15.setClickable(false);
		 citytxt15.setFocusable(false);
		 citytxt16.setClickable(false);
		 citytxt16.setFocusable(false);
		 citytxt17.setClickable(false);
		 citytxt17.setFocusable(false);
		 citytxt18.setClickable(false);
		 citytxt18.setFocusable(false);
		 citytxt19.setClickable(false);
		 citytxt19.setFocusable(false);
		 citytxt20.setClickable(false);
		 citytxt20.setFocusable(false);
		 citytxt21.setClickable(false);
		 citytxt21.setFocusable(false);
		 citytxt22.setClickable(false);
		 citytxt22.setFocusable(false);
	 }
	 
	 private void openClickAndFocus(){
		 citytxt1.setClickable(true);
		 citytxt1.setFocusable(true);
		 citytxt2.setClickable(true);
		 citytxt2.setFocusable(true);
		 citytxt3.setClickable(true);
		 citytxt3.setFocusable(true);
		 citytxt4.setClickable(true);
		 citytxt4.setFocusable(true);
		 citytxt5.setClickable(true);
		 citytxt5.setFocusable(true);
		 citytxt6.setClickable(true);
		 citytxt6.setFocusable(true);
		 citytxt7.setClickable(true);
		 citytxt7.setFocusable(true);
		 citytxt8.setClickable(true);
		 citytxt8.setFocusable(true);
		 citytxt9.setClickable(true);
		 citytxt9.setFocusable(true);
		 citytxt10.setClickable(true);
		 citytxt10.setFocusable(true);
		 citytxt11.setClickable(true);
		 citytxt11.setFocusable(true);
		 citytxt12.setClickable(true);
		 citytxt12.setFocusable(true);
		 citytxt13.setClickable(true);
		 citytxt13.setFocusable(true);
		 citytxt14.setClickable(true);
		 citytxt14.setFocusable(true);
		 citytxt15.setClickable(true);
		 citytxt15.setFocusable(true);
		 citytxt16.setClickable(true);
		 citytxt16.setFocusable(true);
		 citytxt17.setClickable(true);
		 citytxt17.setFocusable(true);
		 citytxt18.setClickable(true);
		 citytxt18.setFocusable(true);
		 citytxt19.setClickable(true);
		 citytxt19.setFocusable(true);
		 citytxt20.setClickable(true);
		 citytxt20.setFocusable(true);
		 citytxt21.setClickable(true);
		 citytxt21.setFocusable(true);
		 citytxt22.setClickable(true);
		 citytxt22.setFocusable(true);
	 }
	 
	 private void changCity(){
         mHolder.mWeatherFragment.setCityText();
         //設定為今日氣象
         if(mHolder.mWeatherMainFragment.Pos==Constants.WEATHER_TODAY){
        	mHolder.mWeatherMainFragment.mWeatherMainFragment_today.updateDatas();
         }else{
         mHolder.mWeatherMainFragment.handler_onlick(0,getActivity().getResources().getString(R.string.weather_today),
         mContext.getResources().getString(R.string.weather_title_txt),Constants.WEATHER_TODAY);  
         }
        

         mHolder.mWeatherMainFragment.UpDateUI();
//         mHolder.mWeatherMainFragment_today.setCityText();
//         mHolder.mWeatherMainFragment_week.setCityText();
         
         
	 }
	 
	 
}
