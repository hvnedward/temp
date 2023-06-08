/*
 * Copyright (C) 2022 The Android Open Source Project
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
 * limitations under the License
 */

package com.humaxdigital.automotive.settings.language;

import android.car.drivingstate.CarUxRestrictions;
import android.content.Context;

import androidx.preference.Preference;

import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.PreferenceController;
import com.android.car.settingslib.language.LanguagePickerUtils;
import com.android.internal.app.LocaleHelper;

import com.humaxdigital.automotive.settings.common.DropDownPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Updates the language settings entry summary with the currently configured locale. */
public class LanguageSettingsEntryPreferenceController extends PreferenceController<DropDownPreference> {

    public LanguageSettingsEntryPreferenceController(Context context, String preferenceKey,
            FragmentController fragmentController, CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
    }

    @Override
    protected void updateState(DropDownPreference preference) {
//        Locale locale = LanguagePickerUtils.getConfiguredLocale();
//
//        List<String> list = new ArrayList<>();
//        list.add(LocaleHelper.getDisplayName(locale, locale, /* sentenceCase= */ true));
//
//        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
//        getPreference().setEntries(cs);
    }

    @Override
    protected Class<DropDownPreference> getPreferenceType() {
        return DropDownPreference.class;
    }
}
