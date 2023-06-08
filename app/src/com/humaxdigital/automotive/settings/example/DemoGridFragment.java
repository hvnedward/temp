package com.humaxdigital.automotive.settings.example;

import androidx.annotation.XmlRes;

import com.android.car.settings.R;
import com.android.car.settings.common.SettingsFragment;

public class DemoGridFragment extends SettingsFragment {

    public static DemoGridFragment newInstance() {
        DemoGridFragment
                fragment = new DemoGridFragment();
        return fragment;
    }

    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.demo_grid_fragment;
    }
}