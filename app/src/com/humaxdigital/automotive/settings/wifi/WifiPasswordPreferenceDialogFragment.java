/*
 * Copyright 2019 The Android Open Source Project
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.DialogPreference;

import com.android.car.settings.R;

/**
 * Abstract base class which presents a dialog associated with a {@link
 * DialogPreference}. Since the preference object may not be available during
 * fragment re-creation, the necessary information for displaying the dialog is read once during
 * the initial call to {@link #onCreate(Bundle)} and saved/restored in the saved instance state.
 * Custom subclasses should also follow this pattern.
 *
 * <p>Note: this is borrowed as-is from androidx.preference.PreferenceDialogFragmentCompat with
 * updates to formatting to match the project style. CarSettings needs to use custom dialog
 * implementations in order to launch the platform {@link AlertDialog} instead of the one in the
 * support library.
 */
public class WifiPasswordPreferenceDialogFragment extends DialogFragment implements
        DialogInterface.OnClickListener {
    public static final String TAG = WifiPasswordPreferenceDialogFragment.class.getSimpleName();

    private static final int DIALOG_WIDTH = 785;
    private static final int DIALOG_HEIGHT = 384;

    protected static final String ARG_KEY = "key";

    private EditText mEditText;
    private OnSetPasswordListener mOnPasswordListener;

    /** Which button was clicked. */
    private int mWhichButtonClicked;

    public interface OnSetPasswordListener {
        void onSetPassword(String password);
    }

    public static WifiPasswordPreferenceDialogFragment newInstance(String key) {
        WifiPasswordPreferenceDialogFragment fragment =
                new WifiPasswordPreferenceDialogFragment();
        Bundle b = new Bundle(/* capacity= */ 1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    public void setOnPasswordListener(OnSetPasswordListener listener) {
        mOnPasswordListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int width = (int) (DIALOG_WIDTH * scale + 0.5f);
        int height = (int) (DIALOG_HEIGHT * scale + 0.5f);

        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View contentView = onCreateDialogView(context);
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.setView(contentView);
        }

        onPrepareDialogBuilder(builder);

        // Create the dialog
        Dialog dialog = builder.create();
        if (contentView != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (needInputMethod()) {
            // Request input only after the dialog is shown. This is to prevent an issue where the
            // dialog view collapsed the content on small displays.
            dialog.setOnShowListener(d -> requestInputMethod(dialog));
        }

        return dialog;
    }

    /**
     * Prepares the dialog builder to be shown when the preference is clicked. Use this to set
     * custom properties on the dialog.
     *
     * <p>Do not {@link AlertDialog.Builder#create()} or {@link AlertDialog.Builder#show()}.
     */
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
    }

    /**
     * Returns whether the preference needs to display a soft input method when the dialog is
     * displayed. Default is false. Subclasses should override this method if they need the soft
     * input method brought up automatically.
     *
     * <p>Note: Ensure your subclass manually requests focus (ideally in {@link
     * #onBindDialogView(View)}) for the input field in order to
     * correctly attach the input method to the field.
     */
    protected boolean needInputMethod() {
        return false;
    }

    /**
     * Sets the required flags on the dialog window to enable input method window to show up.
     */
    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     * Creates the content view for the dialog (if a custom content view is required). By default,
     * it inflates the dialog layout resource if it is set.
     *
     * @return the content View for the dialog.
     * @see DialogPreference#setLayoutResource(int)
     */
    protected View onCreateDialogView(Context context) {
        int resId = R.layout.hmx_preference_dialog_custom_password;
        if (resId == 0) {
            return null;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(resId, null);
    }

    /**
     * Binds views in the content View of the dialog to data.
     *
     * <p>Make sure to call through to the superclass implementation.
     *
     * @param view the content View of the dialog, if it is custom.
     */
    @CallSuper
    protected void onBindDialogView(View view) {
        Button saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonClicked = DialogInterface.BUTTON_POSITIVE;
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
                dismiss();
            }
        });

        mEditText = view.findViewById(android.R.id.edit);
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        CheckBox cb = view.findViewById(R.id.checkbox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEditText.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    mEditText.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // Place cursor at the end
                mEditText.setSelection(mEditText.getText().length());
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mWhichButtonClicked = which;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogClosed(mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE);
    }

    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = mEditText.getText().toString();

            if (mOnPasswordListener != null) {
                mOnPasswordListener.onSetPassword(value);
            }
        }
    }
}
