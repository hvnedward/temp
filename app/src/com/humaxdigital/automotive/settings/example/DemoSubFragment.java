package com.humaxdigital.automotive.settings.example;

import androidx.annotation.XmlRes;

import com.android.car.settings.R;
import com.android.car.settings.common.SettingsFragment;

public class DemoSubFragment extends SettingsFragment {

    public static DemoSubFragment newInstance() {
        DemoSubFragment
                fragment = new DemoSubFragment();
        return fragment;
    }

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.demo_sub_fragment;
    }
}