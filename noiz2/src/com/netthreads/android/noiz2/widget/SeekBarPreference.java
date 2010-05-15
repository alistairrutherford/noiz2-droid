/**
 * Copyright (C) 2009 Alistair Rutherford, Glasgow, Scotland, UK, www.netthreads.co.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.netthreads.android.noiz2.widget;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.netthreads.android.noiz2.R;

/**
 * SeekBar preference dialog control.
 * 
 * This component requires the 'seekbar_dialog' layout and drawable resource
 * 'seekbar_icon'.
 * 
 * CAVEAT: You _must_ call setProgress when you create the control. The method
 * 'onSetInitialValue' won't get called unless the preference has actually been
 * persisted which it won't be until you attempt to access it. This wouldn't
 * matter but we must have a value to set the pre title.
 * 
 */
public class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener
{
    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_VALUE = 0;

    private SeekBar barControl = null;
    private TextView valueControl = null;

    private int initialValue = 0;
    private int progressValue = 0;

    private int offset = 0;

    private int max = DEFAULT_MAX_VALUE;

    private CharSequence title = "";

    /**
     * Constructor.
     * 
     * @param context
     */
    public SeekBarPreference(Context context)
    {
        super(context, null);

        initView();
    }

    /**
     * Constructor.
     * 
     * @param context
     * @param attrs
     */
    public SeekBarPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initView();
    }

    /**
     * Initialise view elements.
     * 
     */
    private void initView()
    {
        setDialogLayoutResource(R.layout.seekbar_dialog);
        setDialogTitle(getTitle());
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        // Steal the XML dialogIcon attribute's value
        setDialogIcon(getContext().getResources().getDrawable(R.drawable.icon_seekbar));

        setMax(DEFAULT_MAX_VALUE);
    }

    /**
     * Load initial value from specified preference.
     * 
     * This is called _before_ the onBindDialogView.
     * 
     * @param Restore
     *            flag.
     * @param Default
     *            value.
     * 
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
    {
        initialValue = restorePersistedValue ? getPersistedInt(DEFAULT_VALUE) : DEFAULT_VALUE;

        setProgress(initialValue);
    }

    /**
     * Called when preference selected by clicking on the title.
     * 
     * @param The
     *            calling view.
     */
    @Override
    protected void onBindDialogView(View view)
    {
        super.onBindDialogView(view);

        barControl = (SeekBar) view.findViewById(R.id.seekbar);
        barControl.setMax(max-offset);
        barControl.setProgress(progressValue);
        barControl.setOnSeekBarChangeListener(this);

        valueControl = (TextView) view.findViewById(R.id.seekbar_value);
        valueControl.setText(String.valueOf(progressValue+offset));

        setPersistent(true);
    }

    /**
     * Called when we select Okay or Cancel.
     * 
     * @param The
     *            result status.
     */
    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        // Only persist if the dialog result is 'okay'
        if (positiveResult)
        {
            setProgress(progressValue);

            // Map progress value back to title.
            updateTitle();
        }
        else
        {
            // Reset the local value to the initial value.
            progressValue = initialValue;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
    {
        int newValue = barControl.getProgress();

        if (!callChangeListener(newValue))
        {
            return;
        }

        progressValue = newValue;
        
        // Update view.
        valueControl.setText(String.valueOf(progressValue+offset));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        // Nothing

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // Nothing
    }

    /**
     * Capture the actual title.
     * 
     * @param The
     *            raw title.
     */
    @Override
    public void setTitle(CharSequence title)
    {
        super.setTitle(title);

        this.title = title;
    }

    /**
     * Update title with value using raw string plus progress.
     * 
     */
    public void updateTitle()
    {
        int total = getProgress()+offset;
        
        String formatted = this.title + " (" + total + ")";

        super.setTitle(formatted);
    }

    // -------------------------------------------------------------------
    // Attributes
    // -------------------------------------------------------------------

    public int getMax()
    {
        return max;
    }

    public void setMax(int value)
    {
        max = value;
    }

    public int getProgress()
    {
        return progressValue;
    }

    /**
     * Set preference value.
     * 
     * @param value
     */
    public void setProgress(int value)
    {
        initialValue = value;

        progressValue = value;

        persistInt(value);

        notifyChanged();

        updateTitle();
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

}
