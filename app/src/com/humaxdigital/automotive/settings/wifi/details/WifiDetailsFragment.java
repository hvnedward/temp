/*
 * Copyright (C) 2018 The Android Open Source Project
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
 * limitations under the License.
 */

package com.humaxdigital.automotive.settings.wifi.details;

import android.content.Context;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.annotation.XmlRes;
import androidx.recyclerview.widget.RecyclerView;

import com.android.car.settings.R;
import com.android.car.settings.common.Logger;
import com.android.car.settings.common.SettingsFragment;
import com.android.car.settings.wifi.WifiUtil;
import com.android.car.settings.wifi.details.WifiDetailsBasePreferenceController;
import com.android.car.settings.wifi.details.WifiDnsPreferenceController;
import com.android.car.settings.wifi.details.WifiFrequencyPreferenceController;
import com.android.car.settings.wifi.details.WifiGatewayPreferenceController;
import com.android.car.settings.wifi.details.WifiInfoProvider;
import com.android.car.settings.wifi.details.WifiInfoProviderImpl;
import com.android.car.settings.wifi.details.WifiIpAddressPreferenceController;
import com.android.car.settings.wifi.details.WifiIpv6AddressPreferenceController;
import com.android.car.settings.wifi.details.WifiLinkSpeedPreferenceController;
import com.android.car.settings.wifi.details.WifiMacAddressPreferenceController;
import com.android.car.settings.wifi.details.WifiSecurityPreferenceController;
import com.android.car.settings.wifi.details.WifiSignalStrengthPreferenceController;
import com.android.car.settings.wifi.details.WifiSubnetPreferenceController;
import com.android.settingslib.wifi.AccessPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows details about a wifi network, including actions related to the network,
 * e.g. ignore, disconnect, etc. The intent should include information about
 * access point, use that to render UI, e.g. show SSID etc.
 */
public class WifiDetailsFragment extends SettingsFragment
        implements WifiInfoProvider.Listener {
    private static final String EXTRA_AP_STATE = "extra_ap_state";
    private static final Logger LOG = new Logger(WifiDetailsFragment.class);

    private static final int INVALID_LAYOUT_ID = 0;

    private AccessPoint mAccessPoint;
    private List<WifiDetailsBasePreferenceController> mControllers = new ArrayList<>();

    private WifiInfoProvider mWifiInfoProvider;

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        RecyclerView recyclerView;
        recyclerView = (RecyclerView)inflater.inflate(R.layout.hmx_settings_scrollbar_recycler_view2, parent, false);
        return recyclerView;
    }

    /**
     * Gets an instance of this class.
     */
    public static WifiDetailsFragment getInstance(AccessPoint accessPoint) {
        WifiDetailsFragment wifiDetailsFragment = new WifiDetailsFragment();
        Bundle bundle = new Bundle();
        Bundle accessPointState = new Bundle();
        accessPoint.saveWifiState(accessPointState);
        bundle.putBundle(EXTRA_AP_STATE, accessPointState);
        wifiDetailsFragment.setArguments(bundle);
        return wifiDetailsFragment;
    }

    @Override
    @LayoutRes
    protected int getActionBarLayoutId() {
        return INVALID_LAYOUT_ID;
    }

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.hmx_wifi_detail_fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAccessPoint = new AccessPoint(getContext(), getArguments().getBundle(EXTRA_AP_STATE));
        LOG.d("Creating WifiInfoProvider for " + mAccessPoint);
        if (mWifiInfoProvider == null) {
            mWifiInfoProvider = new WifiInfoProviderImpl(getContext(), mAccessPoint);
        }
        getLifecycle().addObserver(mWifiInfoProvider);

        LOG.d("Creating WifiInfoProvider.Listeners.");
        mControllers.add(use(
                WifiSignalStrengthPreferenceController.class, R.string.pk_wifi_signal_strength)
                .init(mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiFrequencyPreferenceController.class, R.string.pk_wifi_frequency)
                .init(mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiSecurityPreferenceController.class, R.string.pk_wifi_security)
                .init(mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiMacAddressPreferenceController.class, R.string.pk_wifi_mac_address)
                .init(mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiIpAddressPreferenceController.class, R.string.pk_wifi_ip).init(
                mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiGatewayPreferenceController.class, R.string.pk_wifi_gateway).init(
                mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiSubnetPreferenceController.class, R.string.pk_wifi_subnet_mask)
                .init(mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiDnsPreferenceController.class, R.string.pk_wifi_dns).init(
                mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiLinkSpeedPreferenceController.class, R.string.pk_wifi_link_speed)
                .init(mAccessPoint, mWifiInfoProvider));
        mControllers.add(use(WifiIpv6AddressPreferenceController.class, R.string.pk_wifi_ipv6).init(
                mAccessPoint, mWifiInfoProvider));
        LOG.d("Done init.");
    }

    @Override
    public void onStart() {
        super.onStart();
        mWifiInfoProvider.addListener(this);
        updateUi();
    }

    @Override
    public void onStop() {
        super.onStop();
        mWifiInfoProvider.removeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getLifecycle().removeObserver(mWifiInfoProvider);
    }

    @Override
    public void onWifiChanged(NetworkInfo networkInfo, WifiInfo wifiInfo) {
        updateUi();
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        updateUi();
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        updateUi();
    }

    @Override
    public void onLost(Network network) {
        updateUi();
    }

    @Override
    public void onWifiConfigurationChanged(WifiConfiguration wifiConfiguration,
            NetworkInfo networkInfo, WifiInfo wifiInfo) {
        updateUi();
    }

    private void updateUi() {
        LOG.d("updating.");
        // No need to fetch LinkProperties and NetworkCapabilities, they are updated by the
        // callbacks. mNetwork doesn't change except in onResume.
        if (mWifiInfoProvider.getNetwork() == null
                || mWifiInfoProvider.getNetworkInfo() == null
                || mWifiInfoProvider.getWifiInfo() == null) {
            LOG.d("WIFI not available.");
            return;
        }
        LOG.d("updated.");
    }
}
