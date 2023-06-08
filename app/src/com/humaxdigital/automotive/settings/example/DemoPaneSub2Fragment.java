package com.humaxdigital.automotive.settings.example;

import androidx.annotation.XmlRes;

import com.android.car.settings.R;
import com.android.car.settings.common.SettingsFragment;

public class DemoPaneSub2Fragment extends SettingsFragment {

    public static DemoPaneSub2Fragment newInstance() {
        DemoPaneSub2Fragment
                fragment = new DemoPaneSub2Fragment();
        return fragment;
    }

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.demo_pane_sub2_fragment;
    }
}
