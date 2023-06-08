/*
 * Copyright (C) 2019 The Android Open Source Project
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


package com.humaxdigital.automotive.settings.system;

import android.car.CarNotConnectedException;
import android.car.VehiclePropertyIds;
import android.car.VehicleUnit;
import android.car.drivingstate.CarUxRestrictions;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.Context;

import androidx.annotation.CallSuper;
import androidx.preference.ListPreference;

import com.android.car.settings.R;
import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.PreferenceController;
import com.android.car.settings.units.CarUnitsManager;
import com.android.car.settings.units.Unit;
import com.android.car.settings.units.UnitsMap;
import com.android.internal.annotations.VisibleForTesting;

import com.humaxdigital.automotive.settings.common.ToggleSwitchPreference;

/**
 * Shared business logic for preference controllers related to Units.
 */
public class UnitsTemperaturePreferenceController extends PreferenceController<ToggleSwitchPreference> {

    @VisibleForTesting
    protected final CarUnitsManager.OnCarServiceListener mOnCarServiceListener =
            new CarUnitsManager.OnCarServiceListener() {
                @Override
                public void handleServiceConnected(CarPropertyManager carPropertyManager) {
                    try {
                        if (carPropertyManager != null) {
                            carPropertyManager.registerCallback(mCarPropertyEventCallback,
                                    getPropertyId(), CarPropertyManager.SENSOR_RATE_ONCHANGE);
                        }
                        mSupportedUnits = mCarUnitsManager.getUnitsSupportedByProperty(
                                getPropertyId());
                        if (mSupportedUnits != null && mSupportedUnits.length > 0) {
                            // first element in the config array is the default Unit per VHAL spec.
                            mDefaultUnit = mSupportedUnits[0];
                            getPreference().setSwitchChecked(
                                    getUnitUsedByThisProperty().getId() != VehicleUnit.CELSIUS);
                            refreshUi();
                        }

                        mIsCarUnitsManagerStarted = true;
                    } catch (CarNotConnectedException e) {
                    }
                }

                @Override
                public void handleServiceDisconnected() {
                    mIsCarUnitsManagerStarted = false;
                }
            };

    @VisibleForTesting
    protected final CarPropertyManager.CarPropertyEventCallback mCarPropertyEventCallback =
            new CarPropertyManager.CarPropertyEventCallback() {
                @Override
                public void onChangeEvent(CarPropertyValue value) {
                    if (value != null && value.getStatus() == CarPropertyValue.STATUS_AVAILABLE) {
                        mUnitBeingUsed = UnitsMap.MAP.get(value.getValue());
                        refreshUi();
                    }
                }

                @Override
                public void onErrorEvent(int propId, int zone) {
                }
            };

    private Unit[] mSupportedUnits;
    private Unit mUnitBeingUsed;
    private Unit mDefaultUnit;
    private boolean mIsCarUnitsManagerStarted = false;
    private CarUnitsManager mCarUnitsManager;

    public UnitsTemperaturePreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
    }

    @Override
    @CallSuper
    protected void onCreateInternal() {
        super.onCreateInternal();
        mCarUnitsManager = new CarUnitsManager(getContext());
        mCarUnitsManager.connect();
        mCarUnitsManager.registerCarServiceListener(mOnCarServiceListener);
    }

    @Override
    @CallSuper
    protected void onDestroyInternal() {
        super.onDestroyInternal();
        mCarUnitsManager.disconnect();
        mCarUnitsManager.unregisterCarServiceListener();
    }

    @Override
    protected Class<ToggleSwitchPreference> getPreferenceType() {
        return ToggleSwitchPreference.class;
    }

    @Override
    @CallSuper
    protected void updateState(ToggleSwitchPreference preference) {
        if (mIsCarUnitsManagerStarted && mUnitBeingUsed != null) {
            preference.setSwitchChecked(mUnitBeingUsed.getId() != VehicleUnit.CELSIUS);
        }

        preference.setEnabled(mSupportedUnits != null && mSupportedUnits.length > 0);
    }

    @Override
    @CallSuper
    public boolean handlePreferenceChanged(ToggleSwitchPreference preference, Object newValue) {
        int unitId = (boolean)newValue ? VehicleUnit.FAHRENHEIT : VehicleUnit.CELSIUS;
        mCarUnitsManager.setUnitUsedByProperty(getPropertyId(), unitId);
        return true;
    }

//    @Override
//    protected int getAvailabilityStatus() {
//        return mSupportedUnits != null && mSupportedUnits.length > 0
//                ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
//    }

    protected int getPropertyId() {
        return VehiclePropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS;
    }

    protected String[] getEntriesOfSupportedUnits() {
        String[] names = new String[mSupportedUnits.length];
        for (int i = 0; i < names.length; i++) {
            Unit unit = mSupportedUnits[i];
            names[i] = generateEntryStringFromUnit(unit);
        }
        return names;
    }

    protected String generateSummaryFromUnit(Unit unit) {
        return getContext().getString(unit.getAbbreviationResId());
    }

    protected String generateEntryStringFromUnit(Unit unit) {
        return getContext().getString(R.string.units_list_entry,
                getContext().getString(unit.getAbbreviationResId()),
                getContext().getString(unit.getNameResId()));
    }

    protected String[] getIdsOfSupportedUnits() {
        String[] ids = new String[mSupportedUnits.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Integer.toString(mSupportedUnits[i].getId());
        }
        return ids;
    }

    protected CarUnitsManager getCarUnitsManager() {
        return mCarUnitsManager;
    }

    private Unit getUnitUsedByThisProperty() {
        Unit savedUnit = mCarUnitsManager.getUnitUsedByProperty(getPropertyId());
        if (savedUnit == null) {
            return mDefaultUnit;
        }
        return savedUnit;
    }

}
