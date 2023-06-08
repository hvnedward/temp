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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.car.settings.R;
import com.android.car.settings.common.Logger;

import com.android.car.settings.common.TwoActionPreference;
import com.android.car.settings.wifi.WifiUtil;
import com.android.settingslib.wifi.AccessPoint;

/** Renders a {@link AccessPoint} as a preference. */
public class AccessPointPreference extends Preference {
    private static final Logger LOG = new Logger(AccessPointPreference.class);
    private static final int[] STATE_SECURED = {
            com.android.settingslib.R.attr.state_encrypted
    };
    private static final int[] STATE_NONE = {};
    private static int[] sWifiSignalAttributes = {com.android.settingslib.R.attr.wifi_signal};
    private static int[] sWifiSelectedSignalAttributes = {R.attr.wifi_selected_signal};

    private final StateListDrawable mWifiSld;
    private final StateListDrawable mWifiSelectedSld;

    private final AccessPoint mAccessPoint;

    private boolean mSelected;
    private CharSequence mValueText;

    public AccessPointPreference(
            Context context,
            AccessPoint accessPoint) {
        super(context);
        setLayoutResource(R.layout.hmx_wifi_access_point_preference);

        mWifiSld = (StateListDrawable) context.getTheme()
                .obtainStyledAttributes(sWifiSignalAttributes).getDrawable(0);
        mWifiSelectedSld = (StateListDrawable) context.getTheme()
                .obtainStyledAttributes(sWifiSelectedSignalAttributes).getDrawable(0);

        mAccessPoint = accessPoint;
        LOG.d("creating preference for ap: " + mAccessPoint);

        mSelected = mAccessPoint.isActive();
        setIcon(getAccessPointIcon());
    }

    /**
     * Returns the {@link AccessPoint}.
     */
    public AccessPoint getAccessPoint() {
        return mAccessPoint;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        setIcon(getAccessPointIcon());

        TextView textView = (TextView) holder.findViewById(android.R.id.title);
        textView.setSelected(mSelected);

        TextView textValue = (TextView) holder.findViewById(R.id.value);
        if (mValueText != null) {
            textValue.setText(mValueText);
            textValue.setSelected(mSelected);
        }

        ImageView imageView = (ImageView) holder.findViewById(R.id.info_icon);
        if (imageView != null) {
            if (mSelected) {
                imageView.setVisibility(View.VISIBLE);
            }
            else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setValue(CharSequence value) {
        if (!TextUtils.equals(this.mValueText, value)) {
            this.mValueText = value;
            this.notifyChanged();
        }
    }

    private Drawable getAccessPointIcon() {
        if (mWifiSld == null || mWifiSelectedSld == null) {
            LOG.w("wifiSld is null.");
            return null;
        }

        StateListDrawable listDrawable = mSelected ? mWifiSelectedSld : mWifiSld;

        listDrawable.setState(
                (mAccessPoint.getSecurity() != AccessPoint.SECURITY_NONE)
                        ? STATE_SECURED
                        : STATE_NONE);
        Drawable drawable = listDrawable.getCurrent();
        drawable.setLevel(mAccessPoint.getLevel());
        return drawable;
    }
}
