
package com.mstar.tv.menu.setting;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mstar.tv.AnimatedSelector;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;

/*
 * @projectName： EOSTVMenu
 * @moduleName： PublicFragement.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time  2013-12-18
 * @Copyright © 2013 EOSTEK, Inc.
 */
public abstract class PublicFragement extends Fragment {

    private final static String TAG = "PublicFragement";

    protected ListView public_lst;

    protected ListAdapter mAdapter;

    protected AnimatedSelector animatedSelector;

    protected ArrayList<EosSettingItem> mItems;

    private boolean hasShowValue = false;

    private MyHandler mHandler;

    private View mLastView;

    private boolean isMoving = false;

    protected String mTag = "";

    protected SettingItemDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.eos_public_list, null);
        mHandler = new MyHandler();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ListAdapter();
        public_lst = (ListView) getActivity().findViewById(R.id.context_lst);
        View selector = getActivity().findViewById(R.id.list_selector);
        animatedSelector = new AnimatedSelector(selector, public_lst.getSelector());
        initItems();
        public_lst.setSelector(animatedSelector);
        public_lst.setAdapter(mAdapter);
        if (mTag.equals("advance")) {
            EosSettingItem currVersionItem = mItems.get(0);
            mItems.remove(0);

            TextView titleTxt = (TextView) getActivity().findViewById(R.id.curr_ver_title);
            titleTxt.setText(currVersionItem.getTitle());
            titleTxt.setVisibility(View.VISIBLE);

            TextView valueTxt = (TextView) getActivity().findViewById(R.id.current_version);
            valueTxt.setText(currVersionItem.getValues()[currVersionItem.getCurValue()]);
            valueTxt.setVisibility(View.VISIBLE);
        }
        public_lst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
                if (hasFocus) {
                    mHandler.sendEmptyMessageDelayed(1, 220);
                } else {
                    mHandler.removeMessages(1);
                    clearFocus();
                }
            }
        });

        public_lst.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isMoving) {
                    clearFocus();
                }
                isMoving = true;
                if (public_lst.hasFocus()) {
                    mHandler.removeMessages(1);
                    mHandler.sendEmptyMessageDelayed(1, 220);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                animatedSelector.hideView();
            }
        });

        public_lst.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !public_lst.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        animatedSelector.hideView();
                    }
                }
                return false;
            }
        });

        public_lst.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (mItems.get(position).getFocusable()) {
                    if (mItems.get(position).getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
                        mItems.get(position).itemClicked(position);
                    } else if (mTag.equals("pic")) {
                        getActivity().findViewById(R.id.main).setVisibility(View.INVISIBLE);
                        mDialog = new SettingItemDialog(getActivity(), mItems, position);
                        mDialog.show();
                    }
                } else {
                    Log.v(TAG, "The postion is disable,just ignore the click. position = " + position);
                }
            }
        });

        public_lst.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                        case KeyEvent.KEYCODE_DPAD_RIGHT: {
                            int position = public_lst.getSelectedItemPosition();

                            if (position < 0 || position > mItems.size() - 1) {
                                return true;
                            }
                            if (!mItems.get(position).getFocusable()) {
                                Log.v(TAG, "The postion is disable,just ignore the click. position = " + position);
                                return true;
                            }
                            if (mItems.get(position).getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
                                callBack(position);
                            } else {
                                if (mTag.equals("pic")) {
                                    getActivity().findViewById(R.id.main).setVisibility(View.INVISIBLE);
                                    mDialog = new SettingItemDialog(getActivity(), mItems, position);
                                    mDialog.show();
                                } else {
                                    if (mItems.get(position).getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
                                        mItems.get(position).itemClicked(position);
                                    } else if (mItems.get(position).getFocusable()
                                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                                        mItems.get(position).onKeyDown(keyCode, event, position);
                                    }
                                }
                            }
                            return true;
                        }
                        // case KeyEvent.KEYCODE_DPAD_DOWN: {
                        // int position = public_lst.getSelectedItemPosition();
                        // boolean down = (event.getAction() ==
                        // KeyEvent.ACTION_DOWN);
                        // if (down && position == mItems.size() - 1) {
                        // public_lst.setSelection(0);
                        // return true;
                        // }
                        // }
                        default:
                            break;
                    }
                }
                return doKeyDownOrUp(keyCode, event);
            }
        });
    }

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_progressbar, null);
            }

            EosSettingItem item = mItems.get(position);
            TextView titleTxt = (TextView) convertView.findViewById(R.id.title_txt);
            titleTxt.setText(item.getTitle());
            ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.progress);
            FrameLayout layout = (FrameLayout) convertView.findViewById(R.id.progress_context);
            TextView valueTxt = (TextView) convertView.findViewById(R.id.value);
            convertView.setTag(valueTxt);
            if (!hasShowValue) {
                return convertView;
            }
            valueTxt.setVisibility(View.VISIBLE);
            if (item.getItemType() == MenuConstants.ITEMTYPE_ENUM) {
                valueTxt.setText(item.getValues()[item.getCurValue()]);
            } else if (item.getItemType() == MenuConstants.ITEMTYPE_DIGITAL) {
                FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) bar.getLayoutParams();
                if (item.getStartValue() == 0 && item.getEndValue() == 100) {
                    params.width = item.getCurValue() * 6;
                    bar.setLayoutParams(params);
                    if (item.getCurValue() > item.getStartValue()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                } else if (item.getStartValue() == -50 && item.getEndValue() == 50) {
                    params.width = (item.getCurValue() + 50) * 6;
                    bar.setLayoutParams(params);
                    if (item.getCurValue() > item.getStartValue()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                } else if (item.getStartValue() == 0 && item.getEndValue() == 128) {
                    params.width = item.getCurValue() * 5;
                    bar.setLayoutParams(params);
                    if (item.getCurValue() > item.getStartValue()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                }
                valueTxt.setText(String.valueOf(item.getCurValue()));
                valueTxt.setBackgroundResource(android.R.color.transparent);
                layout.setBackgroundResource(R.drawable.setbar_bg);
            } else if (item.getItemType() == MenuConstants.ITEMTYPE_BOOL) {
                if (item.getBoolValue()) {
                    valueTxt.setText(item.getValues()[1]);
                } else {
                    valueTxt.setText(item.getValues()[0]);
                }
                valueTxt.setBackgroundResource(android.R.color.transparent);
                layout.setBackgroundResource(R.drawable.setbar_bg);
            } else if (item.getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
                layout.setBackgroundResource(R.drawable.setbar_bg2);
                valueTxt.setText("");
                valueTxt.setBackgroundResource(R.drawable.enter);
                bar.setVisibility(View.GONE);
            }

            if (item.getFocusable()) {
                titleTxt.setTextColor(Color.WHITE);
                valueTxt.setTextColor(Color.WHITE);
            } else {
                titleTxt.setTextColor(Color.GRAY);
                valueTxt.setTextColor(Color.GRAY);
            }
            return convertView;
        }

        public void setHasShowValue(boolean hasShowValue) {
            PublicFragement.this.hasShowValue = hasShowValue;
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showSelector(boolean bShow) {
        if (animatedSelector == null)
            return;
        if (bShow) {
            animatedSelector.ensureViewVisible();
        } else {
            animatedSelector.hideView();
        }
    }

    protected abstract void initItems();

    protected abstract void initDate();

    /**
     * If the type is integer,call back the value refresh the UI and call
     * tv_service.
     * 
     * @param resultVaule
     */
    abstract void callBack(int resultVaule, int position);

    abstract void callBack(Boolean resultVaule, int position);

    abstract void callBack(int position);

    abstract boolean doKeyDownOrUp(int keyCode, KeyEvent event);

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            isMoving = false;
            mLastView = public_lst.getSelectedView();
            // AnimatorSet set = new AnimatorSet();
            // ObjectAnimator anim = ObjectAnimator.ofFloat(mLastView, "scaleX",
            // 1.15f);
            // anim.setDuration(200);
            // ObjectAnimator anim3 = ObjectAnimator.ofFloat(mLastView,
            // "scaleY", 1.15f);
            // anim3.setDuration(200);
            // set.playTogether(anim, anim3);
            // set.start();
            super.handleMessage(msg);
        }
    }

    private void clearFocus() {
        // if (mLastView != null) {
        // AnimatorSet set = new AnimatorSet();
        // ObjectAnimator anim1 = ObjectAnimator.ofFloat(mLastView, "scaleX",
        // 1.0f);
        // anim1.setDuration(200);
        // ObjectAnimator anim2 = ObjectAnimator.ofFloat(mLastView, "scaleY",
        // 1.0f);
        // anim2.setDuration(200);
        // set.playTogether(anim1, anim2);
        // set.start();
        // }
    }

    protected void setTag(String tag) {
        mTag = tag;
    }

    protected void updateView(final int position, final View view) {
        if (view == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EosSettingItem item = mItems.get(position);
                ProgressBar bar = (ProgressBar) view.findViewById(R.id.progress);
                TextView valueTxt = (TextView) view.findViewById(R.id.value);

                valueTxt.setVisibility(View.VISIBLE);
                if (item.getItemType() == MenuConstants.ITEMTYPE_ENUM) {
                    valueTxt.setText(item.getValues()[item.getCurValue()]);
                } else if (item.getItemType() == MenuConstants.ITEMTYPE_DIGITAL) {
                    FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) bar.getLayoutParams();
                    if (item.getStartValue() == 0 && item.getEndValue() == 100) {
                        params.width = item.getCurValue() * 6;
                        bar.setLayoutParams(params);
                        if (item.getCurValue() > item.getStartValue()) {
                            bar.setVisibility(View.VISIBLE);
                        }
                    } else if (item.getStartValue() == -50 && item.getEndValue() == 50) {
                        params.width = (item.getCurValue() + 50) * 6;
                        bar.setLayoutParams(params);
                        if (item.getCurValue() > item.getStartValue()) {
                            bar.setVisibility(View.VISIBLE);
                        }
                    } else if (item.getStartValue() == 0 && item.getEndValue() == 128) {
                        params.width = item.getCurValue() * 5;
                        bar.setLayoutParams(params);
                        if (item.getCurValue() > item.getStartValue()) {
                            bar.setVisibility(View.VISIBLE);
                        }
                    }
                    valueTxt.setText(String.valueOf(item.getCurValue()));
                } else if (item.getItemType() == MenuConstants.ITEMTYPE_BOOL) {
                    if (item.getBoolValue()) {
                        valueTxt.setText(item.getValues()[1]);
                    } else {
                        valueTxt.setText(item.getValues()[0]);
                    }
                } else if (item.getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
                    valueTxt.setText(R.string.entertip);
                    bar.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void updateView(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EosSettingItem item = mItems.get(position);
                View view = public_lst.getSelectedView();
                if (view == null) {
                    return;
                }
                ProgressBar bar = (ProgressBar) view.findViewById(R.id.progress);
                TextView valueTxt = (TextView) view.findViewById(R.id.value);

                valueTxt.setVisibility(View.VISIBLE);
                if (item.getItemType() == MenuConstants.ITEMTYPE_ENUM) {
                    valueTxt.setText(item.getValues()[item.getCurValue()]);
                } else if (item.getItemType() == MenuConstants.ITEMTYPE_DIGITAL) {
                    FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) bar.getLayoutParams();
                    if (item.getStartValue() == 0 && item.getEndValue() == 100) {
                        params.width = item.getCurValue() * 6;
                        bar.setLayoutParams(params);
                        if (item.getCurValue() > item.getStartValue()) {
                            bar.setVisibility(View.VISIBLE);
                        }
                    } else if (item.getStartValue() == -50 && item.getEndValue() == 50) {
                        params.width = (item.getCurValue() + 50) * 6;
                        bar.setLayoutParams(params);
                        if (item.getCurValue() > item.getStartValue()) {
                            bar.setVisibility(View.VISIBLE);
                        }
                    } else if (item.getStartValue() == 0 && item.getEndValue() == 128) {
                        params.width = item.getCurValue() * 5;
                        bar.setLayoutParams(params);
                        if (item.getCurValue() > item.getStartValue()) {
                            bar.setVisibility(View.VISIBLE);
                        }
                    }
                    valueTxt.setText(String.valueOf(item.getCurValue()));
                } else if (item.getItemType() == MenuConstants.ITEMTYPE_BOOL) {
                    if (item.getBoolValue()) {
                        valueTxt.setText(item.getValues()[1]);
                    } else {
                        valueTxt.setText(item.getValues()[0]);
                    }
                } else if (item.getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
                    valueTxt.setText(R.string.entertip);
                    bar.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
