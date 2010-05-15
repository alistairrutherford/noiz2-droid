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

import android.os.Bundle;
import android.view.MotionEvent;

import com.netthreads.android.noiz2.control.ControlCanvasSurfaceView;
import com.netthreads.android.noiz2.data.BitmapCache;
import com.netthreads.android.noiz2.data.CanvasBitmapCache;
import com.netthreads.android.noiz2.graphics.RendererCanvas;

/**
 * Main Game Activity Canvas View.
 * 
 * This is a compromise because I can't alter the Renderer interfaces for the
 * SurfaceView types.
 * 
 * I don't like this layout because it relies on mysterious stuff happening in
 * the super-class to set up the game structures. This is not good design.
 * 
 */
public class Noiz2ActivityCanvas extends Noiz2Activity
{
    private ControlCanvasSurfaceView surfaceView = null;
    
    private RendererCanvas renderer = null;

    /** 
     * Called when the activity is first created. 
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); // Perform main activity setup.

        start(CanvasBitmapCache.getInstance());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        
        surfaceView.onPause();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        
        surfaceView.onResume();
    }

    /**
     * Start up Canvas surface.
     * 
     * This routine called from the superclass when it has finished loading structures
     * for the game. We blast away the old view with our game view. Setting the renderer kicks off
     * the action.
     * 
     */
    @Override
    public void start(BitmapCache cache)
    {
        super.start(cache);
        
        int screenWidth = getDisplayMetrics().widthPixels;
        int screenHeight = getDisplayMetrics().heightPixels;

        int lineWidth = ApplicationPreferences.getInstance().getLineWidth()+1;

        // ---------------------------------------------------------------
        // Renderer
        // ---------------------------------------------------------------
        renderer = new RendererCanvas(getPlayer(), getStateManager(), getAttractManager(), lineWidth, screenWidth, screenHeight);

        
        // ---------------------------------------------------------------
        // Surface
        // ---------------------------------------------------------------
         surfaceView = new ControlCanvasSurfaceView(this, getState());
                
         setContentView(surfaceView);
         
         surfaceView.setRenderer(renderer);
    }
    
    /**
     * Pass trackball events to surface.
     * 
     */
    @Override
    public boolean onTrackballEvent(MotionEvent event)
    {
        return surfaceView.onTrackballEvent(event);
    }

}
