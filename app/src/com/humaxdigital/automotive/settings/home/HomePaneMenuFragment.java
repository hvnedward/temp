package com.humaxdigital.automotive.settings.home;

import androidx.annotation.XmlRes;

import com.android.car.settings.R;

import com.humaxdigital.automotive.settings.common.PaneMenuSettingsFragment;

public class HomePaneMenuFragment extends PaneMenuSettingsFragment {

    public static HomePaneMenuFragment newInstance() {
        HomePaneMenuFragment
                fragment = new HomePaneMenuFragment();
        return fragment;
    }
    @Override
    @XmlRes
    protected int getPreferenceScreenResId() {
        return R.xml.hmx_home_pane_menu_fragment;
    }
}