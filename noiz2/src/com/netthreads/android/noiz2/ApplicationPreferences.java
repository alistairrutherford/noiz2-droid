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
import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationPreferences 
{
	public static final String NAME = "preferences";

    public static final String SOUND_TEXT = "Sound on/off";
    public static final boolean SOUND_DEFAULT = true;

	public static final String RANK_KEY = "Difficulty";
	public static final int RANK_DEFAULT = 0;
	public static final int RANK_MAX = 10;
	
	public static final String LINE_WIDTH_KEY = "Line Width";
	public static final int LINE_WIDTH_DEFAULT = 0;
	public static final int LINE_WIDTH_MAX = 4;

    public static final String FIGHTER_OFFSET_KEY = "Fighter Offset";
    public static final int FIGHTER_OFFSET_DEFAULT = 10;
    public static final int FIGHTER_OFFSET_MAX = 20;

    public static final String TRACKBALL_VELOCITY_KEY = "Trackball Velocity";
    public static final int TRACKBALL_VELOCITY_DEFAULT = 11;
    public static final int TRACKBALL_VELOCITY_MAX = 30;
    
	public static final String SHOW_PROFILE_KEY = "Show Profile";
	public static final boolean SHOW_PROFILE_DEFAULT = false;

	public static final String RENDER_KEY = "OpenGL";
	public static final boolean RENDER_DEFAULT = true;

    public static final String PLAY_MODE_KEY = "Mode";
    public static String PLAY_MODE_DEFAULT = "";

	// Preferences 
	private static ApplicationPreferences instance = null;
    private SharedPreferences settings = null;
    
	/**
	 * Singleton access.
	 * 
	 * @param context
	 * 
	 * @return The preferences object.
	 */
	public static ApplicationPreferences getInstance()
	{
		if (instance==null)
		{
			instance = new ApplicationPreferences();
		}
		
		return instance;
	}

	/**
	 * Initialise preference context
	 * 
	 * @param context
	 */
	public void init(Context context)
	{
	    settings = context.getSharedPreferences(ApplicationPreferences.NAME, Activity.MODE_PRIVATE);
	    
	    // Initialise default play mode.
	    ApplicationPreferences.PLAY_MODE_DEFAULT = context.getString(R.string.play_mode_adjustable_difficulty_text);
	}
	
    /**
     * Return Rank
     * 
     * @return Rank value
     */
    public int getRank()
    {
		int value = settings.getInt(RANK_KEY, RANK_DEFAULT);

		return value;
    }

    /**
     * Set the rank
     *
     * @param The value string
     */
    public void setRank(int value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(RANK_KEY, value);
        editor.commit();
    }

    /**
     * Return line width
     * 
     * @return value
     */
    public int getLineWidth()
    {
		int value = settings.getInt(LINE_WIDTH_KEY, LINE_WIDTH_DEFAULT);

		return value;
    }

    /**
     * Set the line width
     *
     * @param The value
     */
    public void setLineWidth(int value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LINE_WIDTH_KEY, value);
        editor.commit();
    }

    /**
     * Return fighter finger offset
     * 
     * @return value
     */
    public int getFighterOffset()
    {
        int value = settings.getInt(FIGHTER_OFFSET_KEY, FIGHTER_OFFSET_DEFAULT);

        return value;
    }

    /**
     * Set the finger fighter offset
     *
     * @param The value
     */
    public void setFighterOffset(int value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(FIGHTER_OFFSET_KEY, value);
        editor.commit();
    }

    /**
     * Return trackball velocity
     * 
     * @return value
     */
    public int getTrackballVelocity()
    {
        int value = settings.getInt(TRACKBALL_VELOCITY_KEY, TRACKBALL_VELOCITY_DEFAULT);

        return value;
    }

    /**
     * Set the trackball velocity
     *
     * @param The value
     */
    public void setTrackballVelocity(int value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(TRACKBALL_VELOCITY_KEY, value);
        editor.commit();
    }

    /**
     * Return profiler setting
     * 
     * @return value
     */
    public boolean getSound()
    {
        boolean value = settings.getBoolean(SOUND_TEXT, SOUND_DEFAULT);

        return value;
    }
    
    /**
     * Return profiler setting
     * 
     * @return value
     */
    public boolean getShowProfile()
    {
		boolean value = settings.getBoolean(SHOW_PROFILE_KEY, SHOW_PROFILE_DEFAULT);

		return value;
    }

    /**
     * Set the profiler setting
     *
     * @param The value
     */
    public void setSound(boolean value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SOUND_TEXT, value);
        editor.commit();
    }
    
    /**
     * Set the profiler setting
     *
     * @param The value
     */
    public void setShowProfile(boolean value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SHOW_PROFILE_KEY, value);
        editor.commit();
    }

    /**
     * Return renderer setting
     * 
     * @return value
     */
    public boolean getRenderer()
    {
		boolean value = settings.getBoolean(RENDER_KEY, RENDER_DEFAULT);

		return value;
    }

    /**
     * Set the renderer setting
     *
     * @param The value
     */
    public void setRenderer(boolean value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(RENDER_KEY, value);
        editor.commit();
    }
    
    /**
     * Return play mode
     * 
     * @return value
     */
    public String getPlayMode()
    {
        String value = settings.getString(PLAY_MODE_KEY, PLAY_MODE_DEFAULT);

        return value;
    }

    /**
     * Set the play mode
     *
     * @param The value
     */
    public void setPlayMode(String value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PLAY_MODE_KEY, value);
        editor.commit();
    }    
    
}
