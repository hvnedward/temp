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

package com.humaxdigital.automotive.settings.common;

import android.car.drivingstate.CarUxRestrictions;
import android.car.drivingstate.CarUxRestrictionsManager.OnUxRestrictionsChangedListener;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.android.car.apps.common.util.Themes;
import com.android.car.settings.R;
import com.android.car.settings.common.ActivityResultCallback;
import com.android.car.settings.common.BaseFragment;
import com.android.car.settings.common.CarUxRestrictionsHelper;
import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.Logger;
import com.android.car.settings.common.UxRestrictionsProvider;

/**
 * Base activity class for car settings, provides a action bar with a back button that goes to
 * previous activity.
 */
public abstract class DualPaneSettingsActivity extends FragmentActivity implements
        FragmentController, OnUxRestrictionsChangedListener, UxRestrictionsProvider,
        OnBackStackChangedListener, PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private static final Logger LOG = new Logger(DualPaneSettingsActivity.class);

    private CarUxRestrictionsHelper mUxRestrictionsHelper;
    private View mRestrictedMessage;

    private PaneMenuSettingsFragment mPaneMenuSettingsFragment;

    // Default to minimum restriction.
    private CarUxRestrictions mCarUxRestrictions = new CarUxRestrictions.Builder(
            /* reqOpt= */ true,
            CarUxRestrictions.UX_RESTRICTIONS_BASELINE,
            /* timestamp= */ 0
    ).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hmx_dual_pane_settings_activity);
        if (mUxRestrictionsHelper == null) {
            mUxRestrictionsHelper = new CarUxRestrictionsHelper(/* context= */ this, /* listener= */
                    this);
        }
        mUxRestrictionsHelper.start();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        mRestrictedMessage = findViewById(R.id.restricted_message);

        // launch initial menu fragment and sub fragment
        PaneMenuSettingsFragment paneMenuSettingsFragment = getMenuFragment();
        if (paneMenuSettingsFragment != null) {
            launchMenuFragment(getMenuFragment());

            Fragment subFragment = getInitialSubFragment();
            if (subFragment != null) {
                launchFragment(subFragment);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUxRestrictionsHelper.stop();
        mUxRestrictionsHelper = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideKeyboard();
        // If the backstack is empty, finish the activity.
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }

    @Override
    public void launchFragment(Fragment fragment) {
        LOG.d("launchFragment: " + fragment.getClass().getName());

        if (fragment instanceof DialogFragment) {
            throw new IllegalArgumentException(
                    "cannot launch dialogs with launchFragment() - use showDialog() instead");
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_sub_container, fragment,
                        Integer.toString(getSupportFragmentManager().getBackStackEntryCount()))
                .commit();

        String subFragmentClass = fragment.getClass().getName();
        if (mPaneMenuSettingsFragment != null) {
            mPaneMenuSettingsFragment.setSubFragmentClass(subFragmentClass);
        }
    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void showBlockingMessage() {
        Toast.makeText(this, R.string.restricted_while_driving, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog(DialogFragment dialogFragment, @Nullable String tag) {
        dialogFragment.show(getSupportFragmentManager(), tag);
    }

    @Override
    @Nullable
    public DialogFragment findDialogByTag(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof DialogFragment) {
            return (DialogFragment) fragment;
        }
        return null;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode,
            ActivityResultCallback callback) {
        throw new UnsupportedOperationException(
                "Unimplemented for activities that implement FragmentController");
    }

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode,
            @Nullable Intent fillInIntent, int flagsMask, int flagsValues, Bundle options,
            ActivityResultCallback callback) {
        throw new UnsupportedOperationException(
                "Unimplemented for activities that implement FragmentController");
    }

    @Override
    public void onUxRestrictionsChanged(CarUxRestrictions restrictionInfo) {
        mCarUxRestrictions = restrictionInfo;
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof OnUxRestrictionsChangedListener) {
            ((OnUxRestrictionsChangedListener) currentFragment)
                    .onUxRestrictionsChanged(restrictionInfo);
        }
        updateBlockingView(currentFragment);
    }

    @Override
    public CarUxRestrictions getCarUxRestrictions() {
        return mCarUxRestrictions;
    }

    @Override
    public void onBackStackChanged() {
        onUxRestrictionsChanged(getCarUxRestrictions());
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
         if (pref.getFragment() != null) {
            Fragment fragment = Fragment.instantiate(/* context= */ this, pref.getFragment(),
                    pref.getExtras());
            launchFragment(fragment);
            return true;
        }
        return false;
    }

    /**
     * Gets the pane menu fragment to show onCreate. If null, the activity will not perform an initial
     * fragment transaction.
     */
    @Nullable
    protected abstract PaneMenuSettingsFragment getMenuFragment();

    /**
     * Gets the initial sub fragment to show onCreate.
     * If null, the activity will not perform an initial sub fragment transaction.
     */
    @Nullable
    protected abstract Fragment getInitialSubFragment();

    protected void launchIfDifferent(Fragment newFragment) {
        Fragment currentFragment = getCurrentFragment();
        if ((newFragment != null) && differentFragment(newFragment, currentFragment)) {
            LOG.d("launchIfDifferent: " + newFragment + " replacing " + currentFragment);
            launchFragment(newFragment);
        }
    }

    protected void launchMenuFragment(PaneMenuSettingsFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        Themes.getAttrResourceId(/* context= */ this,
                                android.R.attr.fragmentOpenEnterAnimation),
                        Themes.getAttrResourceId(/* context= */ this,
                                android.R.attr.fragmentOpenExitAnimation),
                        Themes.getAttrResourceId(/* context= */ this,
                                android.R.attr.fragmentCloseEnterAnimation),
                        Themes.getAttrResourceId(/* context= */ this,
                                android.R.attr.fragmentCloseExitAnimation))
                .replace(R.id.fragment_menu_container, fragment,
                        "0")
                .addToBackStack(null)
                .commit();

        mPaneMenuSettingsFragment = fragment;
    }

    protected Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_sub_container);
    }

    /**
     * Returns {code true} if newFragment is different from current fragment.
     */
    private boolean differentFragment(Fragment newFragment, Fragment currentFragment) {
        return (currentFragment == null)
                || (!currentFragment.getClass().equals(newFragment.getClass()));
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void updateBlockingView(@Nullable Fragment currentFragment) {
        if (currentFragment instanceof BaseFragment) {
            boolean canBeShown = !CarUxRestrictionsHelper.isNoSetup(mCarUxRestrictions);

            mRestrictedMessage.setVisibility(canBeShown ? View.GONE : View.VISIBLE);
        }
    }
}
