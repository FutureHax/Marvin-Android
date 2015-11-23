/*
 * Copyright (C) 2010-2012 Mike Novak <michael.novakjr@gmail.com>
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
package com.futurehax.marvin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;


public class NumberPickerDialog extends DialogFragment implements DialogInterface.OnClickListener {

    protected int mSelectedValue;
    protected int mInitialValue;
    protected AlertDialog mAlertDialog;

    protected static final String KEY_TITLE = "title";
    protected static final String KEY_INITIAL = "initial_value";
    protected static final String KEY_RANGE = "range";
    private OnNumberSetListener mListener;
    private String mTitle;
    private int[] mRange;


    public static NumberPickerDialog newInstance(int mInitialValue, String mTitle, int[] range) {
        NumberPickerDialog instance = new NumberPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, mTitle);
        bundle.putInt(KEY_INITIAL, mInitialValue);
        bundle.putIntArray(KEY_RANGE, range);
        instance.setArguments(bundle);
        
        return instance;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INITIAL, mInitialValue);
        outState.putString(KEY_TITLE, mTitle);
        outState.putIntArray(KEY_RANGE, mRange);
    }
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTitle = getArguments().getString(KEY_TITLE);
            mInitialValue = getArguments().getInt(KEY_INITIAL);
            mRange = getArguments().getIntArray(KEY_RANGE);
        }

        if (savedInstanceState != null) {
            mSelectedValue = (Integer) savedInstanceState.getSerializable(KEY_INITIAL);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_number_picker, null);
        mNumberPicker = (NumberPicker) view.findViewById(R.id.num_picker);
        mNumberPicker.setCurrent(mInitialValue);
        mNumberPicker.setRange(mRange[0], mRange[1]);
        mAlertDialog = new AlertDialog.Builder(activity)
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton("Set", this).setNegativeButton("Cancel", null)
                .create();
        return mAlertDialog;
    }

    private NumberPicker mNumberPicker;

    /**
     * Retrieve the number picker used in the dialog
     */
    public NumberPicker getNumberPicker() {
        return mNumberPicker;
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mListener != null) {
            mListener.onNumberSet(mNumberPicker.getCurrent());
        }
    }

    public void setListener(OnNumberSetListener mListener) {
        this.mListener = mListener;
    }
}
