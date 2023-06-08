package com.humaxdigital.automotive.settings.example;

import androidx.annotation.XmlRes;

import com.android.car.settings.R;
import com.android.car.settings.common.SettingsFragment;

public class DemoPaneSub1Fragment extends SettingsFragment {

    public static DemoPaneSub1Fragment newInstance() {
        DemoPaneSub1Fragment
                fragment = new DemoPaneSub1Fragment();
        return fragment;
    }

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.demo_pane_sub1_fragment;
    }
}
