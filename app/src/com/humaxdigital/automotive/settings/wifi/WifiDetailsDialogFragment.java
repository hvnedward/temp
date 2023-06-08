/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.humaxdigital.automotive.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.android.car.settings.R;
import com.humaxdigital.automotive.settings.wifi.details.WifiDetailsFragment;

import com.android.car.settings.common.Logger;
import com.android.car.settings.wifi.WifiUtil;
import com.android.car.settings.wifi.details.WifiInfoProvider;
import com.android.car.settings.wifi.details.WifiInfoProviderImpl;
import com.android.settingslib.wifi.AccessPoint;

import javax.annotation.Nullable;

public class WifiDetailsDialogFragment extends DialogFragment {
    private static final String EXTRA_AP_STATE = "extra_ap_state";
    public static final String TAG = "WifiDetailsDialogFragment";
    private static final Logger LOG = new Logger(WifiDetailsDialogFragment.class);

    private final static int DIALOG_WIDTH = 785;
    private final static int DIALOG_HEIGHT = 515;

    private WifiManager mWifiManager;
    private AccessPoint mAccessPoint;

    private Button mForgetButton;
    private Button mConnectButton;
    private WifiInfoProvider mWifiInfoProvider;

    private class ActionFailListener implements WifiManager.ActionListener {
        @StringRes
        private final int mMessageResId;

        ActionFailListener(@StringRes int messageResId) {
            mMessageResId = messageResId;
        }

        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailure(int reason) {
            Toast.makeText(getContext(), mMessageResId, Toast.LENGTH_SHORT).show();
        }
    }

    public static WifiDetailsDialogFragment newInstance(String title) {
        WifiDetailsDialogFragment frag = new WifiDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public static WifiDetailsDialogFragment getInstance(AccessPoint accessPoint) {
        WifiDetailsDialogFragment wifiDetailsFragment = new WifiDetailsDialogFragment();
        Bundle bundle = new Bundle();
        Bundle accessPointState = new Bundle();
        accessPoint.saveWifiState(accessPointState);
        bundle.putBundle(EXTRA_AP_STATE, accessPointState);
        wifiDetailsFragment.setArguments(bundle);
        return wifiDetailsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mAccessPoint = new AccessPoint(getContext(), getArguments().getBundle(EXTRA_AP_STATE));
        mWifiManager = context.getSystemService(WifiManager.class);
        LOG.d("Creating WifiInfoProvider for " + mAccessPoint);
        if (mWifiInfoProvider == null) {
            mWifiInfoProvider = new WifiInfoProviderImpl(getContext(), mAccessPoint);
        }
        getLifecycle().addObserver(mWifiInfoProvider);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hmx_preference_dialog_wifi_details, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChildFragmentManager().beginTransaction()
                .add(R.id.detail_fragment, WifiDetailsFragment.getInstance(mAccessPoint))
                .commit();

        ((TextView) view.findViewById(R.id.title)).setText(mAccessPoint.getSsid());

        mConnectButton = view.findViewById(R.id.action_button2);
        mConnectButton.setText(R.string.wifi_setup_connect);
        mConnectButton.setOnClickListener(v -> {
            mWifiManager.connect(mAccessPoint.getConfig(),
                    new ActionFailListener(R.string.wifi_failed_connect_message));
            dismiss();
        });
        mForgetButton = view.findViewById(R.id.action_button1);
        mForgetButton.setText(R.string.forget);
        mForgetButton.setOnClickListener(v -> {
            WifiUtil.forget(getContext(), mAccessPoint);
            dismiss();
        });

        mConnectButton.setVisibility(needConnect() ? View.VISIBLE : View.GONE);
        mForgetButton.setVisibility(canForgetNetwork() ? View.VISIBLE : View.GONE);
    }

    private boolean needConnect() {
        return mAccessPoint.isSaved() && !mAccessPoint.isActive();
    }

    /**
     * Returns whether the network represented by this fragment can be forgotten.
     */
    private boolean canForgetNetwork() {
        return (mWifiInfoProvider.getWifiInfo() != null
                && mWifiInfoProvider.getWifiInfo().isEphemeral()) || canModifyNetwork();
    }

    /**
     * Returns whether the network represented by this preference can be modified.
     */
    private boolean canModifyNetwork() {
        WifiConfiguration wifiConfig = mWifiInfoProvider.getNetworkConfiguration();
        LOG.d("wifiConfig is: " + wifiConfig);
        return wifiConfig != null && !WifiUtil.isNetworkLockedDown(getContext(), wifiConfig);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int width = (int) (DIALOG_WIDTH * scale + 0.5f);
        int height = (int) (DIALOG_HEIGHT * scale + 0.5f);

        getDialog().getWindow().setLayout(width, height);
    }
}
