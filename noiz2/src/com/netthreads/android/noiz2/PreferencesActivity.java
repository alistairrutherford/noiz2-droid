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
package com.netthreads.android.noiz2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.netthreads.android.noiz2.widget.SeekBarPreference;

/**
 * Preferences settings activity.
 * 
 */
public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
    private SeekBarPreference rankPref = null;
    private SeekBarPreference lineWidthPref = null;
    private SeekBarPreference fighterOffsetPref = null;
    private SeekBarPreference trackballVelocityPref = null;
    private ListPreference playModePref = null;

    /*
     * View Create
     * 
     * (non-Javadoc)
     * 
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set the name of the preferences this PreferenceActivity will manage
        getPreferenceManager().setSharedPreferencesName(ApplicationPreferences.NAME);

        // Create and note geocode pref screen
        setPreferenceScreen(createPreferenceScreen());
    }

    /**
     * On resume.
     * 
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(ApplicationPreferences.NAME, Activity.MODE_PRIVATE);

        // Set up a listener whenever a preference changes
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * On pause we uninstall changed listener.
     * 
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences settings = getSharedPreferences(ApplicationPreferences.NAME, Activity.MODE_PRIVATE);

        // Set up a listener whenever a preference changes
        settings.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Build preference view.
     * 
     * (*) Annoyingly we have to do this. If you miss it out then the preference
     * title will not get initialised properly. The reason for this is the at
     * the control requires a value for the default to exist.
     * 
     * @return view
     */
    private PreferenceScreen createPreferenceScreen()
    {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        ApplicationPreferences preferences = ApplicationPreferences.getInstance();

        // ---------------------------------------------------------------
        // Preferences Category
        // ---------------------------------------------------------------
        PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle(R.string.data_preferences);
        root.addPreference(inlinePrefCat);

        // Sound on/off
        boolean soundChecked = preferences.getSound();
        CheckBoxPreference soundPref = new CheckBoxPreference(this);
        soundPref.setKey(ApplicationPreferences.SOUND_TEXT);
        soundPref.setTitle(this.getString(R.string.sound_text));
        soundPref.setChecked(soundChecked);
        soundPref.setSummary(R.string.sound_summary);

        inlinePrefCat.addPreference(soundPref);
        
        // Play mode
        playModePref = new ListPreference(this);
        playModePref.setKey(ApplicationPreferences.PLAY_MODE_KEY);
        playModePref.setEntries(R.array.play_mode_type);
        playModePref.setEntryValues(R.array.play_mode_type);
        playModePref.setDialogTitle(this.getString(R.string.play_mode_text));
        playModePref.setTitle(this.getString(R.string.play_mode_text));
        playModePref.setValue(this.getString(R.string.play_mode_adjustable_difficulty_text)); // adjustable difficulty
        playModePref.setSummary(preferences.getPlayMode());

        inlinePrefCat.addPreference(playModePref);

        // Difficulty slider
        rankPref = new SeekBarPreference(this);

        rankPref.setKey(ApplicationPreferences.RANK_KEY);
        rankPref.setTitle(this.getString(R.string.rank_text));
        rankPref.setOffset(1);
        rankPref.setMax(ApplicationPreferences.RANK_MAX);
        rankPref.setProgress(preferences.getRank()); // See note (*)
        rankPref.setSummary(R.string.difficulty_summary);

        inlinePrefCat.addPreference(rankPref);

        // Finger fighter offset
        fighterOffsetPref = new SeekBarPreference(this);

        fighterOffsetPref.setKey(ApplicationPreferences.FIGHTER_OFFSET_KEY);
        fighterOffsetPref.setTitle(this.getString(R.string.fighter_offset_text));
        fighterOffsetPref.setOffset(0);
        fighterOffsetPref.setMax(ApplicationPreferences.FIGHTER_OFFSET_MAX);
        fighterOffsetPref.setProgress(preferences.getFighterOffset()); // See
        // note
        // (*)
        fighterOffsetPref.setSummary(R.string.fighter_offset_summary);

        inlinePrefCat.addPreference(fighterOffsetPref);

        // Finger fighter offset
        trackballVelocityPref = new SeekBarPreference(this);

        trackballVelocityPref.setKey(ApplicationPreferences.TRACKBALL_VELOCITY_KEY);
        trackballVelocityPref.setTitle(this.getString(R.string.trackball_velocity_text));
        trackballVelocityPref.setOffset(1);
        trackballVelocityPref.setMax(ApplicationPreferences.TRACKBALL_VELOCITY_MAX);
        trackballVelocityPref.setProgress(preferences.getTrackballVelocity()); // See
        // note
        // (*)
        trackballVelocityPref.setSummary(R.string.trackball_velocity_summary);

        inlinePrefCat.addPreference(trackballVelocityPref);

        // OpenGL renderer
        boolean rendererChecked = preferences.getRenderer();
        CheckBoxPreference rendererPref = new CheckBoxPreference(this);
        rendererPref.setKey(ApplicationPreferences.RENDER_KEY);
        rendererPref.setTitle(this.getString(R.string.render_text));
        rendererPref.setChecked(rendererChecked);
        rendererPref.setSummary(R.string.opengl_summary);

        inlinePrefCat.addPreference(rendererPref);

        // Line width slider
        lineWidthPref = new SeekBarPreference(this);

        lineWidthPref.setKey(ApplicationPreferences.LINE_WIDTH_KEY);
        lineWidthPref.setTitle(this.getString(R.string.line_width_text));
        lineWidthPref.setOffset(1);
        lineWidthPref.setMax(ApplicationPreferences.LINE_WIDTH_MAX);
        lineWidthPref.setProgress(preferences.getLineWidth()); // See note (*)
        lineWidthPref.setSummary(R.string.line_width_summary);

        inlinePrefCat.addPreference(lineWidthPref);
        
        // Profile data switch
        boolean profilerChecked = preferences.getShowProfile();
        CheckBoxPreference profilerPref = new CheckBoxPreference(this);
        profilerPref.setKey(ApplicationPreferences.SHOW_PROFILE_KEY);
        profilerPref.setTitle(this.getString(R.string.show_profile_text));
        profilerPref.setChecked(profilerChecked);
        profilerPref.setSummary(R.string.profile_summary);

        inlinePrefCat.addPreference(profilerPref);

        setDifficultyState(preferences.getPlayMode());
        setLineWidthState(preferences.getRenderer());

        return root;
    }

    /**
     * Triggered by a change to the shared preferences.
     * 
     * We use this to update the view.
     * 
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        ApplicationPreferences preferences = ApplicationPreferences.getInstance();

        if (key.equals(ApplicationPreferences.PLAY_MODE_KEY))
        {
            String mode = preferences.getPlayMode();

            playModePref.setSummary(mode);

            setDifficultyState(mode);
        }
        else
            if (key.equals(ApplicationPreferences.RENDER_KEY))
            {
                boolean state = preferences.getRenderer();

                setLineWidthState(state);
            }
    }

    /**
     * Set difficulty state based on our play mode.
     * 
     * @param mode
     */
    private void setDifficultyState(String mode)
    {
        // If anything but the adjustable setting..
        String dtext = this.getString(R.string.play_mode_adjustable_difficulty_text);
        if (!mode.equals(dtext))
        {
            rankPref.setEnabled(false);
        }
        else
        {
            rankPref.setEnabled(true);
        }

    }
    
    /**
     * Enable disable line width state
     * 
     * @param mode
     */
    private void setLineWidthState(boolean state)
    {
        lineWidthPref.setEnabled(!state);
    }
    
}
