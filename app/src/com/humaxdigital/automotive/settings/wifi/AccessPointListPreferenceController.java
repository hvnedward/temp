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

package com.humaxdigital.automotive.settings.wifi;

import android.car.drivingstate.CarUxRestrictions;
import android.car.drivingstate.CarUxRestrictionsManager;
import android.content.Context;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;

import com.android.car.settings.R;
import com.android.car.settings.common.CarUxRestrictionsHelper;
import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.Logger;
import com.android.car.settings.wifi.WifiBasePreferenceController;
import com.android.car.settings.wifi.WifiUtil;
import com.android.settingslib.wifi.AccessPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a list of {@link AccessPoint} as a list of preferences.
 */
public class AccessPointListPreferenceController extends
        WifiBasePreferenceController<PreferenceGroup> implements
        Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener,
        CarUxRestrictionsManager.OnUxRestrictionsChangedListener {
    private static final Logger LOG = new Logger(AccessPointListPreferenceController.class);
    private final WifiManager.ActionListener mConnectionListener =
            new WifiUtil.ActionFailedListener(getContext(), R.string.wifi_failed_connect_message);
    private List<AccessPoint> mAccessPoints = new ArrayList<>();

    public AccessPointListPreferenceController(@NonNull Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
    }

    @Override
    protected Class<PreferenceGroup> getPreferenceType() {
        return PreferenceGroup.class;
    }

    @Override
    protected void updateState(PreferenceGroup preferenceGroup) {
        if (getCarWifiManager() == null) {
            return;
        }
        mAccessPoints = CarUxRestrictionsHelper.isNoSetup(getUxRestrictions())
                ? getCarWifiManager().getSavedAccessPoints()
                : getCarWifiManager().getAllAccessPoints();
        LOG.d("showing accessPoints: " + mAccessPoints.size());

        preferenceGroup.setVisible(!mAccessPoints.isEmpty());
        preferenceGroup.removeAll();
        for (AccessPoint accessPoint : mAccessPoints) {
            preferenceGroup.addPreference(createAccessPointPreference(accessPoint));
        }
    }

    @Override
    protected void onApplyUxRestrictions(CarUxRestrictions uxRestrictions) {
        // Since the list dynamically changes based on the ux restrictions, we enable this fragment
        // regardless of the restriction. Intentional no-op.
    }

    @Override
    public void onAccessPointsChanged() {
        refreshUi();
    }

    @Override
    public void onWifiStateChanged(int state) {
        if (state == WifiManager.WIFI_STATE_ENABLED) {
            refreshUi();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        AccessPoint accessPoint = ((AccessPointPreference) preference).getAccessPoint();
        if (accessPoint.getSecurity() != AccessPoint.SECURITY_NONE && (!accessPoint.isSaved()
                || WifiUtil.isAccessPointDisabledByWrongPassword(accessPoint))) {
            WifiPasswordPreferenceDialogFragment dialogFragment
                    = WifiPasswordPreferenceDialogFragment.newInstance(preference.getKey());
            dialogFragment.setOnPasswordListener(
                    new WifiPasswordPreferenceDialogFragment.OnSetPasswordListener() {
                        @Override
                        public void onSetPassword(String password) {
                            onPreferenceChange(preference, password);
                        }
                    });

            getFragmentController().showDialog(dialogFragment, WifiPasswordPreferenceDialogFragment.TAG);
        }

        // For new open unsecuried wifi network, connect to it right away.
        if (accessPoint.getSecurity() == AccessPoint.SECURITY_NONE
                && !accessPoint.isSaved() && !accessPoint.isActive()) {
            getCarWifiManager().connectToPublicWifi(accessPoint, mConnectionListener);
        } else if (accessPoint.isActive()) {
            getFragmentController().showDialog(WifiDetailsDialogFragment.getInstance(accessPoint),
                    WifiDetailsDialogFragment.TAG);
        } else if (accessPoint.isSaved() && !WifiUtil.isAccessPointDisabledByWrongPassword(
                accessPoint)) {
            getCarWifiManager().connectToSavedWifi(accessPoint, mConnectionListener);
        }

        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        LOG.d("onPreferenceChange: " + newValue.toString());
        AccessPoint accessPoint = ((AccessPointPreference) preference).getAccessPoint();
        WifiUtil.connectToAccessPoint(getContext(), accessPoint.getSsid().toString(),
                accessPoint.getSecurity(), newValue.toString(), /* hidden= */ false);
        return true;
    }

    private AccessPointPreference createAccessPointPreference(AccessPoint accessPoint) {
        LOG.d("Adding preference for " + WifiUtil.getKey(accessPoint));
        AccessPointPreference accessPointPreference = new AccessPointPreference(getContext(),
                accessPoint);
        accessPointPreference.setKey(accessPoint.getKey());
        accessPointPreference.setTitle(accessPoint.getConfigName());
        accessPointPreference.setValue(accessPoint.getSummary());
        accessPointPreference.setOnPreferenceClickListener(this);
        accessPointPreference.setOnPreferenceChangeListener(this);

        return accessPointPreference;
    }
}
