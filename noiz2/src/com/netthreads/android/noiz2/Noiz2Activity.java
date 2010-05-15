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


import jp.gr.java_conf.abagames.bulletml.BulletmlPlayer;
import jp.gr.java_conf.abagames.noiz2.AttractManager;
import jp.gr.java_conf.abagames.noiz2.BarrageManager;
import jp.gr.java_conf.abagames.noiz2.PrefManager;
import jp.gr.java_conf.abagames.noiz2.Ship;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.netthreads.android.noiz2.data.BitmapCache;
import com.netthreads.android.noiz2.data.StateData;
import com.netthreads.android.noiz2.data.StateManager;
import com.netthreads.android.noiz2.sound.SoundPoolPlayer;

/**
 * Main Game Activity View.
 * 
 */
public class Noiz2Activity extends Activity
{
    // Game
    private BarrageManager manager = null;
    private BulletmlPlayer player = null;
    private AttractManager attractManager = null;

    private Ship ship = null;

    private PrefManager prefManager = null;
    private DisplayMetrics displayMetrics = null;

    // State
    private StateData state = null;
    private StateManager stateManager = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    	// ---------------------------------------------------------------
        // View
        // ---------------------------------------------------------------
        setContentView(R.layout.game);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Handles tracking state controlled elements.
        int offset = ApplicationPreferences.getInstance().getFighterOffset();

        state = new StateData(screenWidth, screenHeight);
        
        stateManager = new StateManager(state, offset);

        // ---------------------------------------------------------------
        // Initialise sounds
        // ---------------------------------------------------------------
        SoundPoolPlayer.instance().open(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        // ---------------------------------------------------------------
        // Clean up sounds
        // ---------------------------------------------------------------
        SoundPoolPlayer.instance().close();
    }
    
    /**
     * Start BulletML view elements.
     * 
     */
    public void start(BitmapCache cache)
    {
        // ---------------------------------------------------------------
        // Initialise Game elements
        // ---------------------------------------------------------------
        prefManager = new PrefManager(this);
        manager = new BarrageManager(this, state);
        player = new BulletmlPlayer();

        ship = new Ship(manager, player, prefManager, state);
        manager.setShip(ship);
        
        String version  = AboutActivity.getVersion(this);
        attractManager = new AttractManager(this, manager, ship, prefManager, state, cache, version);
        
        // ---------------------------------------------------------------
        // Load assets
        // ---------------------------------------------------------------

        attractManager.loadImages();
        
        // ---------------------------------------------------------------
        // Initialise Game elements
        // ---------------------------------------------------------------
        player.init(this, manager, ship, attractManager);
        
        prefManager.init();
        
        prefManager.load();
        
		ship.init();
		
		attractManager.initTitle();
    }
    
    public BulletmlPlayer getPlayer()
    {
        return player;
    }

    public StateManager getStateManager()
    {
        return stateManager;
    }

    public AttractManager getAttractManager()
    {
        return attractManager;
    }

    public DisplayMetrics getDisplayMetrics()
    {
        return displayMetrics;
    }

    public StateData getState()
    {
        return state;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    
}
