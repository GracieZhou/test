
package com.android.settings.system.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import scifly.device.Device;

import com.android.settings.system.SystemSettingsActivity;
import com.android.settings.userbackup.BackUpData;

import com.android.settings.R;

public class DeviceNameFragment extends Fragment {

    public static final String TAG = "DeviceNameSettingsHolder";

    private String mDeviceName;

    private EditText mEditText;

    private Button mBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_system_settings_devicename, container, false);

        ((SystemSettingsActivity) getActivity()).setSubTitle(R.string.device_name);
        mBtn = (Button) root.findViewById(R.id.device_btn_ok);

        mDeviceName = Device.getDeviceName(getActivity());
        mEditText = (EditText) root.findViewById(R.id.device_name_et);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        mEditText.setText(mDeviceName == null ? "" : mDeviceName);
        mEditText.setSelection(mEditText.length());

        registerListener();
        return root;
    }

    public void registerListener() {
        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDeviceName = mEditText.getText().toString();

                if (!TextUtils.isEmpty(newDeviceName) && !newDeviceName.equals(mDeviceName)) {
                    BackUpData.backupData("device", "device_name", newDeviceName);
                    Device.setDeviceName(getActivity(), newDeviceName);
                }

                getActivity().getFragmentManager().popBackStack();

                InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newDeviceName = mEditText.getText().toString();

                if (TextUtils.isEmpty(newDeviceName)) {
                    Toast.makeText(getActivity(), R.string.disable_input_null, Toast.LENGTH_SHORT).show();
                    mBtn.setClickable(false);
                } else {
                    mBtn.setClickable(true);
                    if (newDeviceName.length() == 20) {
                        Toast.makeText(getActivity(), R.string.string_outbound, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
