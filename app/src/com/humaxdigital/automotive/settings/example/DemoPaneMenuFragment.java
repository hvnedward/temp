package com.humaxdigital.automotive.settings.example;

import androidx.annotation.XmlRes;

import com.android.car.settings.R;
import com.android.car.settings.common.SettingsFragment;

import com.humaxdigital.automotive.settings.common.PaneMenuSettingsFragment;

public class DemoPaneMenuFragment extends PaneMenuSettingsFragment {

    public static DemoPaneMenuFragment newInstance() {
        DemoPaneMenuFragment
                fragment = new DemoPaneMenuFragment();
        return fragment;
    }

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.demo_pane_menu_fragment;
    }
}
