package com.humaxdigital.automotive.settings.example;

import android.content.Context;
import android.car.drivingstate.CarUxRestrictions;

import android.os.UserManager;

import com.android.car.settings.R;
import com.android.car.settings.common.ConfirmationDialogFragment;
import com.android.car.settings.common.ErrorDialog;
import com.android.car.settings.common.FragmentController;
import com.android.car.settings.common.PreferenceController;

import androidx.preference.Preference;

public class MyCustomRestrictionsPreferenceController extends
    PreferenceController<Preference> {

    private final UserManager mUserManager;

    private final ConfirmationDialogFragment.ConfirmListener mConfirmListener = arguments -> {
        // If failed, need to show error dialog for users.
        getFragmentController().showDialog(
                ErrorDialog.newInstance(R.string.delete_user_error_title), /* tag= */ null);

//        getFragmentController().goBack();
    };

    public MyCustomRestrictionsPreferenceController(Context context, String
        preferenceKey, FragmentController fragmentController,
        CarUxRestrictions uxRestrictions) {
        super(context, preferenceKey, fragmentController, uxRestrictions);
        mUserManager = UserManager.get(context);
    }

    @Override
    protected Class<Preference> getPreferenceType() {
        return Preference.class;
    }

    @Override
    public int getAvailabilityStatus() {
        return mUserManager.isAdminUser() ? AVAILABLE : DISABLED_FOR_USER;
    }

    @Override
    protected void onCreateInternal() {
        super.onCreateInternal();
        ConfirmationDialogFragment dialogFragment =
                (ConfirmationDialogFragment) getFragmentController().findDialogByTag(
                        ConfirmationDialogFragment.TAG);

        ConfirmationDialogFragment.resetListeners(dialogFragment,
                mConfirmListener, /* rejectListener= */ null);
//
//        getPreference().setOnPreferenceClickListener(pref -> {
//            userClicked();
//            return true;
//        });
    }

    @Override
    protected boolean handlePreferenceClicked(Preference preference) {
        userClicked();
        return true;
    }

    protected void userClicked() {
        ConfirmationDialogFragment dialogFragment = new ConfirmationDialogFragment.Builder(getContext())
                .setTitle(R.string.grant_admin_permissions_title)
                .setMessage("This is test")
                .setPositiveButton(R.string.confirm_grant_admin, mConfirmListener)
                .setNegativeButton(android.R.string.cancel, null)
//                .addArgumentParcelable(KEY_USER_TO_MAKE_ADMIN, userToMakeAdmin)
                .build();

        getFragmentController().showDialog(dialogFragment, ConfirmationDialogFragment.TAG);
    }
}

