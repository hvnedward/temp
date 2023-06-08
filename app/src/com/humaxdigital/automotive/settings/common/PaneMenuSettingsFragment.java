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

package com.humaxdigital.automotive.settings.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import com.android.car.settings.R;
import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.Logger;
import com.android.car.settings.common.SettingsFragment;

public abstract class PaneMenuSettingsFragment extends SettingsFragment implements FragmentController {
    private static final Logger LOG = new Logger(PaneMenuSettingsFragment.class);

    private String subFragmentClassName;

    @LayoutRes
    protected int getActionBarLayoutId() {
        return R.layout.hmx_menu_action_bar;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);

        selectInitialSubPreference();
    }

    @LayoutRes
    protected int getRecyclerViewLayoutId() {
        return R.layout.hmx_settings_spinning_layout_recycler_view;
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        RecyclerView recyclerView;
        recyclerView = (RecyclerView)inflater.inflate(getRecyclerViewLayoutId(), parent, false);
        return recyclerView;
    }

    /**
     * select sub fragment class. sub fragment should be in menu preference fragment property
     */
    public void setSubFragmentClass(String classname) {
        subFragmentClassName = classname;

        LOG.i("set sub fragment (class = " + classname + ")");

        PaneMenuPreference selectPreference = findPreferenceByFragmentClass(classname);
        if (selectPreference != null) {
            selectMenuPreference(selectPreference);
        }
    }

    private PaneMenuPreference findPreferenceByFragmentClass(String classname) {
        PaneMenuPreference selectPreference = null;

        PreferenceScreen preferenceScreen = checkAndGetPreferenceScreen();
        if (preferenceScreen == null) {
            return null;
        }

        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            if (preferenceScreen.getPreference(i) instanceof PaneMenuPreference) {
                if (preferenceScreen.getPreference(i).getFragment() != null) {
                    if (preferenceScreen.getPreference(i).getFragment().equals(classname)) {
                        selectPreference = (PaneMenuPreference) preferenceScreen.getPreference(
                                i);
                        break;
                    }
                }
            }
        }
        return selectPreference;
    }

    private PreferenceScreen checkAndGetPreferenceScreen() {
        if (getPreferenceManager() != null) {
            return getPreferenceScreen();
        }
        return null;
    }

    private void selectInitialSubPreference() {
        PaneMenuPreference selectPreference = null;
        if (subFragmentClassName != null) {
            selectPreference = findPreferenceByFragmentClass(subFragmentClassName);
        }

        // if no selected sub fragment, select the first one by default
        if (selectPreference == null) {
            PreferenceScreen preferenceScreen = checkAndGetPreferenceScreen();
            if (preferenceScreen != null) {
                for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
                    if (preferenceScreen.getPreference(i) instanceof PaneMenuPreference) {
                        selectPreference = (PaneMenuPreference)preferenceScreen.getPreference(i);
                        break;
                    }
                }
            }
        }

        if (selectPreference != null) {
            selectMenuPreference(selectPreference);
        }
    }

    private void selectMenuPreference(Preference preference) {
        PreferenceScreen preferenceScreen = checkAndGetPreferenceScreen();
        if (preferenceScreen == null) {
            return;
        }

        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            if (preferenceScreen.getPreference(i) instanceof PaneMenuPreference) {
                PaneMenuPreference paneMenuPreference = (PaneMenuPreference) preferenceScreen.getPreference(i);

                if (preference == paneMenuPreference) {
                    LOG.i("selected preference (fragment = "
                            + preference.getFragment() + ")");

                    paneMenuPreference.setSelected(true);
                    scrollToPreference(paneMenuPreference);
                }
                else {
                    paneMenuPreference.setSelected(false);
                }
            }
        }
    }
}
