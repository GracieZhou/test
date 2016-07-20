
package com.android.settings.network.wifi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;

public class LinkPropertyInputView {

    private static final String TAG = "LinkPropertyInputView";

    private static final int MAX_GROUP_NUMBER = 4;

    private Context mContext;

    private View inputView;

    private List<LinkPropertyGroup> viewList = new ArrayList<LinkPropertyGroup>();

    private static final int UP_ARROW_FLASH_ANIMATION = 0;

    protected static final int UP_ARROW_FLASH_ANIMATION_RECOVERY = 1;

    public static final int DOWN_ARROW_FLASH_ANIMATION = 3;

    public static final int DOWN_ARROW_FLASH_ANIMATION_RECOVERY = 4;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case UP_ARROW_FLASH_ANIMATION:
                    View view = (View) msg.obj;
                    if (view != null) {
                        ImageView up = (ImageView) view.findViewById(R.id.link_property_number_up_arrow);
                        if (up != null) {
                            up.setImageResource(R.drawable.up_arrow_green);
                            Message newMsg = obtainMessage();
                            newMsg.obj = view;
                            newMsg.what = UP_ARROW_FLASH_ANIMATION_RECOVERY;
                            sendMessageDelayed(newMsg, 100);
                        }
                    }
                    break;
                case UP_ARROW_FLASH_ANIMATION_RECOVERY:
                    view = (View) msg.obj;
                    if (view != null) {
                        ImageView up = (ImageView) view.findViewById(R.id.link_property_number_up_arrow);
                        if (up != null) {
                            up.setImageResource(R.drawable.up_arrow_white);
                        }
                    }
                    break;
                case DOWN_ARROW_FLASH_ANIMATION:
                    view = (View) msg.obj;
                    if (view != null) {
                        ImageView up = (ImageView) view.findViewById(R.id.link_property_number_down_arrow);
                        if (up != null) {
                            up.setImageResource(R.drawable.down_arrow_green);
                            Message newMsg = obtainMessage();
                            newMsg.obj = view;
                            newMsg.what = DOWN_ARROW_FLASH_ANIMATION_RECOVERY;
                            sendMessageDelayed(newMsg, 100);
                        }
                    }
                    break;
                case DOWN_ARROW_FLASH_ANIMATION_RECOVERY:
                    view = (View) msg.obj;
                    if (view != null) {
                        ImageView up = (ImageView) view.findViewById(R.id.link_property_number_down_arrow);
                        if (up != null) {
                            up.setImageResource(R.drawable.down_arrow_white);
                        }
                    }
                    break;
            }

        };
    };

    public LinkPropertyInputView(Context context, View view) {
        mContext = context;
        inputView = view;
        View group1 = inputView.findViewById(R.id.link_property_input_group_1);
        View group2 = inputView.findViewById(R.id.link_property_input_group_2);
        View group3 = inputView.findViewById(R.id.link_property_input_group_3);
        View group4 = inputView.findViewById(R.id.link_property_input_group_4);
        viewList.add(new LinkPropertyGroup(group1));
        viewList.add(new LinkPropertyGroup(group2));
        viewList.add(new LinkPropertyGroup(group3));
        viewList.add(new LinkPropertyGroup(group4));
    }

    public void setValue(int groupIndex, int[] value) {

        if (groupIndex >= viewList.size() || groupIndex >= MAX_GROUP_NUMBER) {
            Log.i(TAG, "invalid group index ");
            return;
        }

        if (value.length != 3) {
            Log.i(TAG, "invalid value ");
            return;
        }
        viewList.get(groupIndex).setValue(value[0], value[1], value[2]);
    }

    public void setValue(String value) {

        Log.i(TAG, "value " + value);

        if (value == null || "".endsWith(value)) {
            Log.i(TAG, "invalid value ");
            return;
        }

        String[] ips = value.split("\\.");

        if (ips.length != MAX_GROUP_NUMBER) {
            Log.i(TAG, "ips.length is not equal 4");
        }

        try {
            for (int i = 0; i < MAX_GROUP_NUMBER; i++) {
                int groupValue = Integer.valueOf(ips[i]);
                if (groupValue < 0 || groupValue > 255) {
                    Log.i(TAG, "invalid groupValue ");
                    return;
                }
                setValue(i, new int[] {
                        groupValue / 100, (groupValue / 10) % 10, (groupValue % 10)
                });
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();

        }

    }

    public String getValue() {
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < MAX_GROUP_NUMBER - 1; i++) {
            resultString.append(viewList.get(i).getValue());
            resultString.append(".");
        }
        resultString.append(viewList.get(MAX_GROUP_NUMBER - 1).getValue());
        return resultString.toString();
    }

    private class LinkPropertyGroup {

        View groupView;

        TextView numberTv1;

        TextView numberTv2;

        TextView numberTv3;

        LinkPropertyGroup(View view) {
            groupView = view;
            try {
                numberTv1 = (TextView) groupView.findViewById(R.id.link_property_number_1).findViewById(
                        R.id.link_property_number);
                numberTv2 = (TextView) groupView.findViewById(R.id.link_property_number_2).findViewById(
                        R.id.link_property_number);
                numberTv3 = (TextView) groupView.findViewById(R.id.link_property_number_3).findViewById(
                        R.id.link_property_number);

                numberTv1.setOnKeyListener(new NumberTextOnKeyListener(groupView
                        .findViewById(R.id.link_property_number_1)));
                numberTv1.setOnFocusChangeListener(new NumberTextFocusChangeListener(groupView
                        .findViewById(R.id.link_property_number_1)));
                numberTv2.setOnKeyListener(new NumberTextOnKeyListener(groupView
                        .findViewById(R.id.link_property_number_2)));
                numberTv2.setOnFocusChangeListener(new NumberTextFocusChangeListener(groupView
                        .findViewById(R.id.link_property_number_2)));
                numberTv3.setOnKeyListener(new NumberTextOnKeyListener(groupView
                        .findViewById(R.id.link_property_number_3)));
                numberTv3.setOnFocusChangeListener(new NumberTextFocusChangeListener(groupView
                        .findViewById(R.id.link_property_number_3)));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        class NumberTextFocusChangeListener implements OnFocusChangeListener {

            private View parent;

            public NumberTextFocusChangeListener(View p) {
                parent = p;
            }

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ImageView upImg = (ImageView) parent.findViewById(R.id.link_property_number_up_arrow);
                ImageView downImg = (ImageView) parent.findViewById(R.id.link_property_number_down_arrow);

                if (hasFocus) {
                    if (upImg != null) {
                        upImg.setVisibility(View.VISIBLE);
                    }
                    if (downImg != null) {
                        downImg.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (upImg != null) {
                        upImg.setVisibility(View.INVISIBLE);
                    }
                    if (downImg != null) {
                        downImg.setVisibility(View.INVISIBLE);
                    }
                }

            }
        }

        class NumberTextOnKeyListener implements OnKeyListener {

            private View parent;

            public NumberTextOnKeyListener(View p) {
                parent = p;
            }

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // Do nothing when key up.
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return false;
                }

                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        numberChange(parent, v, keyCode);
                        return true;
                }
                return false;
            }
        }

        private void numberChange(View parent, View v, int keyCode) {

            if (!(v instanceof TextView)) {
                Log.i(TAG, "not instance of TextView");
                return;
            }

            String text = ((TextView) v).getText().toString();

            if (text == null || "".equals(text) || text.length() > 1) {
                Log.i(TAG, "invalid string");
                return;
            }
            try {
                int value = Integer.valueOf(text);
                int oldValue = value;
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    value++;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    value--;
                }

                if (value > 9) {
                    value = 0;
                } else if (value < 0) {
                    value = 9;
                }

                ((TextView) v).setText("" + value);

                int result = getValue();

                if (result > 255 || result < 0) {
                    ((TextView) v).setText("" + oldValue);
                }

                Message msg = mHandler.obtainMessage();
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    msg.obj = parent;
                    msg.what = UP_ARROW_FLASH_ANIMATION;
                    mHandler.sendMessage(msg);

                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    msg.obj = parent;
                    msg.what = DOWN_ARROW_FLASH_ANIMATION;
                    mHandler.sendMessage(msg);
                }

            } catch (NumberFormatException e) {
                Log.i(TAG, "invalid string,not a number");
                e.printStackTrace();
            }

        }

        public void setValue(int first, int second, int third) {
            try {
                numberTv1.setText("" + first);
                numberTv2.setText("" + second);
                numberTv3.setText("" + third);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        public int getValue() {

            int result = -1;
            try {
                int first = Integer.valueOf(numberTv1.getText().toString());
                int second = Integer.valueOf(numberTv2.getText().toString());
                int third = Integer.valueOf(numberTv3.getText().toString());
                result = first * 100 + second * 10 + third;

            } catch (NumberFormatException e) {
                Log.e(TAG, "invalid string,not a number");
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

}
