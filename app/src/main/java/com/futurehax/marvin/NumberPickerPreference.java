/*******************************************************************************
 * Copyright 2013 Gabriele Mariotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.futurehax.marvin;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class NumberPickerPreference extends Preference {
    private View mPreviewView;
    private int mValue = 1;

    public NumberPickerPreference(Context context) {
        super(context);
        setWidgetLayoutResource(R.layout.number_picker_preview);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.number_picker_preview);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWidgetLayoutResource(R.layout.number_picker_preview);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        mPreviewView = view.findViewById(R.id.preview_text);
        setNumberViewValue(mPreviewView, mValue);
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            mValue = value;
            persistInt(value);
            notifyChanged();
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        NumberPickerDialog dialog = NumberPickerDialog.newInstance(mValue, "Select flash count", new int[] {0, 10});

        Activity activity = (Activity) getContext();
        activity.getFragmentManager().beginTransaction()
                .add(dialog, "number_picker")
                .commit();

        dialog.setListener(listener);
    }
    
    /**
     * Implement listener to get selected color value
     */
    OnNumberSetListener listener = new OnNumberSetListener() {

        @Override
        public void onNumberSet(int selectedNumber) {
            setValue(selectedNumber);
        }
    };

    @Override
    public void onAttached() {
        super.onAttached();

        Activity activity = (Activity) getContext();
        NumberPickerDialog numberPicker = (NumberPickerDialog) activity
                .getFragmentManager().findFragmentByTag(getFragmentTag());
        if (numberPicker != null) {
            // re-bind listener to fragment
            numberPicker.setListener(listener);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public String getFragmentTag() {
        return "number_picker_" + getKey();
    }

    public int getValue() {
        return mValue;
    }

    private static void setNumberViewValue(View view, int value) {
        if (view instanceof TextView) {
            ((TextView) view).setText(Integer.toString(value));
        }
    }
}
