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

/**
 * This is a shadow of the preferences manager from the original game. 
 * 
 * Preference management is a handy way to persist the game state.
 * 
 */
public class GameState
{
    public static final String NAME = "gamestate";

    public static final String STAGE_SCORE_TXT = "score_";
    public static final String SCENE_SCORE_TXT = "scene_";
    public static final String STAGE_OPENED_TXT = "opened_";
    public static final String STAGE_CLEARED_TXT = "cleared_";

    // Preferences 
    private static GameState instance = null;
    private SharedPreferences state = null;

    /**
     * Singleton access.
     * 
     * @param context
     * 
     * @return The preferences object.
     */
    public static GameState getInstance(Context context)
    {
        if (instance==null)
        {
            instance = new GameState(context);
        }
        
        return instance;
    }

    /**
     * We need context for share preferences persistence.
     * 
     * @param context
     */
    private GameState(Context context) 
    {
        state = context.getSharedPreferences(GameState.NAME, Activity.MODE_PRIVATE);
    }
    
    /**
     * Store stage score.
     * 
     * @param Score index 
     * @param Score value
     */
    public void setStageScore(int index, int value)
    {
        SharedPreferences.Editor editor = state.edit();
        editor.putInt(STAGE_SCORE_TXT+index, value);
        editor.commit();
    }

    /**
     * Return stage score.
     * 
     * @return value
     */
    public int getStageScore(int index)
    {
        int value = state.getInt(STAGE_SCORE_TXT+index, 0);

        return value;
    }

    /**
     * Store stage score.
     * 
     * @param Score index 
     * @param Score value
     */
    public void setSceneScore(int index, int value)
    {
        SharedPreferences.Editor editor = state.edit();
        editor.putInt(SCENE_SCORE_TXT+index, value);
        editor.commit();
    }

    /**
     * Return stage score.
     * 
     * @return value
     */
    public int getSceneScore(int index)
    {
        int value = state.getInt(SCENE_SCORE_TXT+index, 0);

        return value;
    }

    /**
     * Store stage status.
     * 
     * @param Score index 
     * @param Score value
     */
    public void setStageOpened(int index, boolean status)
    {
        SharedPreferences.Editor editor = state.edit();
        editor.putBoolean(STAGE_OPENED_TXT+index, status);
        editor.commit();
    }

    /**
     * Return stage score.
     * 
     * @return value
     */
    public boolean getStageOpened(int index)
    {
        boolean value = state.getBoolean(STAGE_OPENED_TXT+index, false);

        return value;
    }
    
    /**
     * Store stage status.
     * 
     * @param Score index 
     * @param Score value
     */
    public void setStageCleared(int index, boolean status)
    {
        SharedPreferences.Editor editor = state.edit();
        editor.putBoolean(STAGE_CLEARED_TXT+index, status);
        editor.commit();
    }

    /**
     * Return stage score.
     * 
     * @return value
     */
    public boolean getStageCleared(int index)
    {
        boolean value = state.getBoolean(STAGE_CLEARED_TXT+index, false);

        return value;
    }    
}
