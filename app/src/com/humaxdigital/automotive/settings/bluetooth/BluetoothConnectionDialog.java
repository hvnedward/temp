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

package com.humaxdigital.automotive.settings.bluetooth;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import com.android.car.settings.R;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;

public class BluetoothConnectionDialog extends Dialog {

    private Context context;
	private BluetoothConnectionListener mListener;
	Button mConnection;

    public BluetoothConnectionDialog(@NonNull Context context, BluetoothConnectionListener listener) {
        super(context);
        this.context = context;
		this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				setContentView(R.layout.hmx_bt_connection);
		
				TextView mCancel = findViewById(R.id.bt_cancel);
				TextView mForgot = findViewById(R.id.bt_forget);
		
				mCancel.setOnClickListener(v -> {
					this.mListener.onBtCancelClick();
					dismiss();
				});
				mForgot.setOnClickListener(v -> {
					this.mListener.onBtForgetClick();
					dismiss();
				});
    }
}

