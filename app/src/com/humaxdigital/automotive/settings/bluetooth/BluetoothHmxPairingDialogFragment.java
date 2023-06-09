/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.humaxdigital.automotive.settings.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.car.settings.R;
import com.android.car.settings.common.Logger;
import com.android.internal.annotations.VisibleForTesting;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;


/**
 * A dialogFragment used by {@link BluetoothHmxPairingDialog} to create an appropriately styled dialog
 * for the bluetooth device.
 */
public class BluetoothHmxPairingDialogFragment extends DialogFragment implements
        TextWatcher, OnClickListener {

    private static final Logger LOG = new Logger(BluetoothHmxPairingDialogFragment.class);

    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private BluetoothHmxPairingController mPairingController;
    private BluetoothHmxPairingDialog mPairingDialogActivity;
    private EditText mPairingView;
	private View mDialogView;

	private TextView mTitleView = null;
	private TextView mPositiveView = null;
	private	TextView mNegativeView = null;
    /**
     * The interface we expect a listener to implement. Typically this should be done by
     * the controller.
     */
    public interface BluetoothPairingDialogListener {

        void onDialogNegativeClick(BluetoothHmxPairingDialogFragment dialog);

        void onDialogPositiveClick(BluetoothHmxPairingDialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (!isPairingControllerSet()) {
            throw new IllegalStateException(
                "Must call setPairingController() before showing dialog");
        }
        if (!isPairingDialogActivitySet()) {
            throw new IllegalStateException(
                "Must call setPairingDialogActivity() before showing dialog");
        }
        mBuilder = new AlertDialog.Builder(getActivity());
        mDialog = setupDialog();
        mDialog.setCanceledOnTouchOutside(false);

		mPositiveView.setOnClickListener(v -> {
			mPairingController.onDialogPositiveClick(this);
			mPairingDialogActivity.dismiss();
		});
		mNegativeView.setOnClickListener(v -> {
			mPairingController.onDialogNegativeClick(this);
			mPairingDialogActivity.dismiss();
		});
		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		
        return mDialog;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        // enable the positive button when we detect potentially valid input
        Button positiveButton = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setEnabled(mPairingController.isPasskeyValid(s));
        }
        // notify the controller about user input
        mPairingController.updateUserInput(s.toString());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            mPairingController.onDialogPositiveClick(this);
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            mPairingController.onDialogNegativeClick(this);
        }
        mPairingDialogActivity.dismiss();
    }

    /**
     * Used in testing to get a reference to the dialog.
     * @return - The fragments current dialog
     */
    protected AlertDialog getmDialog() {
        return mDialog;
    }

    /**
     * Sets the controller that the fragment should use. this method MUST be called
     * before you try to show the dialog or an error will be thrown. An implementation
     * of a pairing controller can be found at {@link BluetoothHmxPairingController}. A
     * controller may not be substituted once it is assigned. Forcibly switching a
     * controller for a new one will lead to undefined behavior.
     */
    public void setPairingController(BluetoothHmxPairingController pairingController) {
        if (isPairingControllerSet()) {
            throw new IllegalStateException("The controller can only be set once. "
                    + "Forcibly replacing it will lead to undefined behavior");
        }
        mPairingController = pairingController;
    }

    /**
     * Checks whether mPairingController is set
     * @return True when mPairingController is set, False otherwise
     */
    public boolean isPairingControllerSet() {
        return mPairingController != null;
    }

    /**
     * Sets the BluetoothHmxPairingDialog activity that started this fragment
     * @param pairingDialogActivity The pairing dialog activty that started this fragment
     */
    public void setPairingDialogActivity(BluetoothHmxPairingDialog pairingDialogActivity) {
        if (isPairingDialogActivitySet()) {
            throw new IllegalStateException("The pairing dialog activity can only be set once");
        }
        mPairingDialogActivity = pairingDialogActivity;
    }

    /**
     * Checks whether mPairingDialogActivity is set
     * @return True when mPairingDialogActivity is set, False otherwise
     */
    public boolean isPairingDialogActivitySet() {
        return mPairingDialogActivity != null;
    }

    /**
     * Creates the appropriate type of dialog and returns it.
     */
    private AlertDialog setupDialog() {
        AlertDialog dialog;
        switch (mPairingController.getDialogType()) {
            case BluetoothHmxPairingController.USER_ENTRY_DIALOG:
                dialog = createUserEntryDialog();
                break;
            case BluetoothHmxPairingController.CONFIRMATION_DIALOG:
                dialog = createConsentDialog();
                break;
            case BluetoothHmxPairingController.DISPLAY_PASSKEY_DIALOG:
                dialog = createDisplayPasskeyOrPinDialog();
                break;
            default:
                dialog = null;
                LOG.e("Incorrect pairing type received, not showing any dialog");
        }
        return dialog;
    }

    /**
     * Helper method to return the text of the pin entry field - this exists primarily to help us
     * simulate having existing text when the dialog is recreated, for example after a screen
     * rotation.
     */
    @VisibleForTesting
    CharSequence getPairingViewText() {
        if (mPairingView != null) {
            return mPairingView.getText();
        }
        return null;
    }

    /**
     * Returns a dialog with UI elements that allow a user to provide input.
     */
   private AlertDialog createUserEntryDialog() {
		/*	  

	TextView title = mDialogView.findViewById(R.id.bt_paring_title);
	TextView positive_btn = mDialogView.findViewById(R.id.bt_paring_positive);
	TextView negative_btn = mDialogView.findViewById(R.id.bt_paring_negative);	
	title.setText(getString(R.string.bluetooth_pairing_request,
                mPairingController.getDeviceName()));
	positive_btn.setText(getString(android.R.string.ok));
	negative_btn.setText(getString(android.R.string.cancel));
		*/		
/*
        mBuilder.setTitle(getString(R.string.bluetooth_pairing_request,
                mPairingController.getDeviceName()));
        mBuilder.setPositiveButton(getString(android.R.string.ok), this);
        mBuilder.setNegativeButton(getString(android.R.string.cancel), this);
*/
        mBuilder.setView(createPinEntryView());
        AlertDialog dialog = mBuilder.create();
        dialog.setOnShowListener(d -> {
            if (TextUtils.isEmpty(getPairingViewText())) {
                mDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
            }
            if (mPairingView != null && mPairingView.requestFocus()) {
                InputMethodManager imm = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(mPairingView, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        return dialog;
    }

	/**
	 * Creates the custom view with UI elements for user input.
	 */
    private View createPinEntryView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.hmx_bt_pin_entry, null);

		mTitleView = view.findViewById(R.id.bt_paring_title);
		mPositiveView = view.findViewById(R.id.bt_paring_positive);
		mNegativeView = view.findViewById(R.id.bt_paring_negative);				
		mTitleView.setText(getString(R.string.bluetooth_pairing_request,
						mPairingController.getDeviceName()));
		mPositiveView.setText(getString(android.R.string.ok));
		mNegativeView.setText(getString(android.R.string.cancel));

		
        TextView messageViewCaptionHint = (TextView) view.findViewById(R.id.pin_values_hint);
        TextView messageView2 = (TextView) view.findViewById(R.id.message_below_pin);
        CheckBox alphanumericPin = (CheckBox) view.findViewById(R.id.alphanumeric_pin);
        CheckBox contactSharing = (CheckBox) view.findViewById(
                R.id.phonebook_sharing_message_entry_pin);
        contactSharing.setText(getString(R.string.bluetooth_pairing_shares_phonebook,
                mPairingController.getDeviceName()));
        EditText pairingView = (EditText) view.findViewById(R.id.text);

        contactSharing.setVisibility(mPairingController.isProfileReady()
                ? View.GONE : View.VISIBLE);
        contactSharing.setOnCheckedChangeListener(mPairingController);
        contactSharing.setChecked(mPairingController.getContactSharingState());

        mPairingView = pairingView;

        pairingView.setInputType(InputType.TYPE_CLASS_NUMBER);
        pairingView.addTextChangedListener(this);
        alphanumericPin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // change input type for soft keyboard to numeric or alphanumeric
            if (isChecked) {
                mPairingView.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                mPairingView.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });

        int messageId = mPairingController.getDeviceVariantMessageId();
        int messageIdHint = mPairingController.getDeviceVariantMessageHintId();
        int maxLength = mPairingController.getDeviceMaxPasskeyLength();
        alphanumericPin.setVisibility(mPairingController.pairingCodeIsAlphanumeric()
                ? View.VISIBLE : View.GONE);
        if (messageId != BluetoothHmxPairingController.INVALID_DIALOG_TYPE) {
            messageView2.setText(messageId);
        } else {
            messageView2.setVisibility(View.GONE);
        }
        if (messageIdHint != BluetoothHmxPairingController.INVALID_DIALOG_TYPE) {
            messageViewCaptionHint.setText(messageIdHint);
        } else {
            messageViewCaptionHint.setVisibility(View.GONE);
        }
        pairingView.setFilters(new InputFilter[]{
                new LengthFilter(maxLength)});

        return view;
    }

    /**
     * Creates a dialog with UI elements that allow the user to confirm a pairing request.
     */
    private AlertDialog createConfirmationDialog() {
/*
        mBuilder.setTitle(getString(R.string.bluetooth_pairing_request,
                mPairingController.getDeviceName()));
        mBuilder.setPositiveButton(getString(R.string.bluetooth_pairing_accept), this);
        mBuilder.setNegativeButton(getString(R.string.bluetooth_pairing_decline), this);
*/		
        mBuilder.setView(createView(BluetoothHmxPairingController.CONFIRMATION_DIALOG));
        AlertDialog dialog = mBuilder.create();
        return dialog;
    }

    /**
     * Creates a dialog with UI elements that allow the user to consent to a pairing request.
     */
    private AlertDialog createConsentDialog() {
        return createConfirmationDialog();
    }

    /**
     * Creates a dialog that informs users of a pairing request and shows them the passkey/pin
     * of the device.
     */
    private AlertDialog createDisplayPasskeyOrPinDialog() {
/*    
        mBuilder.setTitle(getString(R.string.bluetooth_pairing_request,
                mPairingController.getDeviceName()));
        mBuilder.setNegativeButton(getString(android.R.string.cancel), this);
*/		
        mBuilder.setView(createView(BluetoothHmxPairingController.DISPLAY_PASSKEY_DIALOG));
        AlertDialog dialog = mBuilder.create();

        // Tell the controller the dialog has been created.
        mPairingController.notifyDialogDisplayed();

        return dialog;
    }

    /**
     * Creates a custom view for dialogs which need to show users additional information but do
     * not require user input.
     */
    private View createView(int type) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.hmx_bt_pin_confirm, null);

		mTitleView = view.findViewById(R.id.bt_paring_title);
		mPositiveView = view.findViewById(R.id.bt_paring_positive);
		mNegativeView = view.findViewById(R.id.bt_paring_negative);
		
		mTitleView.setText(getString(R.string.bluetooth_pairing_request,
						mPairingController.getDeviceName()));

		if(type == BluetoothHmxPairingController.DISPLAY_PASSKEY_DIALOG)
		{
			mPositiveView.setVisibility(View.GONE);
			mNegativeView.setText(getString(R.string.cancel));
		}
		else
		{
			mPositiveView.setText(getString(R.string.bluetooth_pairing_accept));
			mNegativeView.setText(getString(R.string.bluetooth_pairing_decline));
    	}
		
        TextView pairingViewCaption = (TextView) view.findViewById(R.id.pairing_caption);
        TextView pairingViewContent = (TextView) view.findViewById(R.id.pairing_subhead);
        TextView messagePairing = (TextView) view.findViewById(R.id.pairing_code_message);
        View contactSharingContainer = view.findViewById(
                R.id.phonebook_sharing_message_confirm_pin_container);
        TextView contactSharingText = (TextView) view.findViewById(
                R.id.phonebook_sharing_message_confirm_pin_text);
        CheckBox contactSharing = (CheckBox) view.findViewById(
                R.id.phonebook_sharing_message_confirm_pin);
        contactSharingText.setText(getString(R.string.bluetooth_pairing_shares_phonebook,
                mPairingController.getDeviceName()));
        contactSharingContainer.setVisibility(
                mPairingController.isProfileReady() ? View.GONE : View.VISIBLE);
        contactSharing.setChecked(mPairingController.getContactSharingState());
        contactSharing.setOnCheckedChangeListener(mPairingController);

        messagePairing.setVisibility(mPairingController.isDisplayPairingKeyVariant()
                ? View.VISIBLE : View.GONE);
        if (mPairingController.hasPairingContent()) {
            pairingViewCaption.setVisibility(View.VISIBLE);
            pairingViewContent.setVisibility(View.VISIBLE);
            pairingViewContent.setText(mPairingController.getPairingContent());
        }
        return view;
    }

}
