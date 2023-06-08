/*
 * Copyright 2018 The Android Open Source Project
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

package com.humaxdigital.automotive.settings.bluetooth;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.android.car.settings.common.Logger;
import com.android.car.settings.R;
import com.humaxdigital.automotive.settings.common.ButtonPreference;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;


import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import android.bluetooth.BluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;


/**
 * Preference which represents a specific {@link CachedBluetoothDevice}. The title, icon, and
 * summary are kept in sync with the device when the preference is shown. When the device is busy,
 * the preference is disabled. The equality and sort order of this preference is determined by the
 * underlying cached device {@link CachedBluetoothDevice#equals(Object)} and {@link
 * CachedBluetoothDevice#compareTo(CachedBluetoothDevice)}. If two devices are considered equal, the
 * default preference sort ordering is used (see {@link #compareTo(Preference)}.
 */
public class BluetoothDevicePreference extends ButtonPreference {
    private static final Logger LOG = new Logger(BluetoothDevicePreference.class);
	private static final boolean DBG = true;

    private static final String BLUETOOTH_SHOW_DEVICES_WITHOUT_NAMES_PROPERTY =
            "persist.bluetooth.showdeviceswithoutnames";

    private final CachedBluetoothDevice mCachedDevice;
    private final boolean mShowDevicesWithoutNames;
    private final CachedBluetoothDevice.Callback mDeviceCallback = this::refreshUi;

    private ImageView mAudioImageView;
    private ImageView mPhoneImageView;

    public BluetoothDevicePreference(Context context, CachedBluetoothDevice cachedDevice) {
        super(context);
        mCachedDevice = cachedDevice;
        mShowDevicesWithoutNames = SystemProperties.getBoolean(
                BLUETOOTH_SHOW_DEVICES_WITHOUT_NAMES_PROPERTY, false);
        // Hide action by default.
        showAction(false);

        setLayoutResource(R.layout.hmx_bt_device_preference);
		if(DBG){
			LOG.d("[BluetoothDevicePreference] ");
   		}		
    }

    /**
     * Returns the {@link CachedBluetoothDevice} represented by this preference.
     */
    public CachedBluetoothDevice getCachedDevice() {
        return mCachedDevice;
    }

    @Override
    public void onAttached() {
        super.onAttached();
        mCachedDevice.registerCallback(mDeviceCallback);
        refreshUi();
    }

    @Override
    public void onDetached() {
        super.onDetached();
        mCachedDevice.unregisterCallback(mDeviceCallback);
    }

    private void refreshUi() {
		//paneMenuPreference.setSelected(true);

		
        setTitle(mCachedDevice.getName());
        setSummary(mCachedDevice.getCarConnectionSummary());


        final Pair<Drawable, String> pair = com.android.settingslib.bluetooth.BluetoothUtils
                .getBtClassDrawableWithDescription(getContext(), mCachedDevice);
        if (pair.first != null) {
//            setIcon(pair.first);
//            getIcon().setTintList(Themes.getAttrColorStateList(getContext(), R.attr.iconColor));

            // TODO: set an icon according to the bt class
            if(mCachedDevice.isConnected() == true)
	            setIcon(getContext().getDrawable(R.drawable.set_ico_bt_s));
			else
	            setIcon(getContext().getDrawable(R.drawable.set_ico_bt));
        }

        setEnabled(!mCachedDevice.isBusy());
        setVisible(mShowDevicesWithoutNames || mCachedDevice.hasHumanReadableName());

        setA2dpConnected(isConnectedA2dpsinkDevice());
        setHfpConnected(isConnectedHfpClientDevice());

        // Notify since the ordering may have changed.
        notifyHierarchyChanged();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BluetoothDevicePreference)) {
            return false;
        }
        return mCachedDevice.equals(((BluetoothDevicePreference) o).mCachedDevice);
    }

    @Override
    public int hashCode() {
        return mCachedDevice.hashCode();
    }

    @Override
    public int compareTo(@NonNull Preference another) {
        if (!(another instanceof BluetoothDevicePreference)) {
            // Rely on default sort.
            return super.compareTo(another);
        }

        return mCachedDevice
                .compareTo(((BluetoothDevicePreference) another).mCachedDevice);
    }
@Override
public final void onBindViewHolder(PreferenceViewHolder holder) {
	super.onBindViewHolder(holder);
	TextView titleView = (TextView) holder.findViewById(android.R.id.title);
	TextView summaryView = (TextView) holder.findViewById(android.R.id.summary);
	if(mCachedDevice.isConnected() == true)
	{
		titleView.setTextColor(getContext().getResources().getColor(R.color.color_selected));
		summaryView.setTextColor(getContext().getResources().getColor(R.color.color_selected));
	}
	else
	{
		titleView.setTextColor(getContext().getResources().getColor(R.color.color_normal));
		summaryView.setTextColor(getContext().getResources().getColor(R.color.color_normal));
	}
	//LOG.d("onBindViewHolder titleView : "+ titleView.getText());
}


    @Override
    protected void onBindWidgetFrame(View widgetFrame) {

        ImageView settings = widgetFrame.findViewById(R.id.bt_settings_button);
        settings.setOnClickListener(v -> performButtonClick());

        mAudioImageView = widgetFrame.findViewById(R.id.audio_icon);
        mPhoneImageView = widgetFrame.findViewById(R.id.phone_icon);

		setA2dpConnected(isConnectedA2dpsinkDevice());
		setHfpConnected(isConnectedHfpClientDevice());
    }

	private boolean isConnectedA2dpsinkDevice() {
        LocalBluetoothManager manager = BluetoothUtils.getLocalBtManager(getContext());
        LocalBluetoothProfileManager profileManager = manager.getProfileManager();

        LocalBluetoothProfile a2dpSinkProfile = profileManager.getA2dpSinkProfile();
        return a2dpSinkProfile != null
                && a2dpSinkProfile.getConnectionStatus(mCachedDevice.getDevice()) == BluetoothProfile.STATE_CONNECTED;
    }

	private boolean isConnectedHfpClientDevice() {
        LocalBluetoothManager manager = BluetoothUtils.getLocalBtManager(getContext());
        LocalBluetoothProfileManager profileManager = manager.getProfileManager();

        LocalBluetoothProfile hfpClientProfile = profileManager.getHfpClientProfile();
        return hfpClientProfile != null
                && hfpClientProfile.getConnectionStatus(mCachedDevice.getDevice()) == BluetoothProfile.STATE_CONNECTED;
    }

    private void setA2dpConnected(boolean connected) {
    	if(DBG){
			LOG.d("[BluetoothDevicePreference] onBindWidgetFrame a2dp : "+ connected);
   		}
		
        if (mAudioImageView != null) {
            if (connected) {
                mAudioImageView.setVisibility(View.VISIBLE);
            }
            else {
                mAudioImageView.setVisibility(View.GONE);
            }
        }
    }

    private void setHfpConnected(boolean connected) {
    	if(DBG){
			LOG.d("[BluetoothDevicePreference] onBindWidgetFrame hfp : "+ connected);
   		}
		
        if (mPhoneImageView != null) {
            if (connected) {
                mPhoneImageView.setVisibility(View.VISIBLE);
            }
            else {
                mPhoneImageView.setVisibility(View.GONE);
            }
        }
    }
}
