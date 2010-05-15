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
package com.netthreads.android.noiz2.graphics;

import jp.gr.java_conf.abagames.bulletml.BulletmlPlayer;
import jp.gr.java_conf.abagames.noiz2.AttractManager;
import jp.gr.java_conf.abagames.noiz2.LetterRender;
import android.graphics.Canvas;

import com.netthreads.android.noiz2.ProfileRecorder;
import com.netthreads.android.noiz2.data.StateManager;
import com.netthreads.android.noiz2.graphics.canvas.CanvasSurfaceView;

/**
 * Renderer for game surface view.
 * 
 */
public class RendererCanvas implements CanvasSurfaceView.Renderer
{
    private ScreenCanvas screen = null;

    private BulletmlPlayer gameManager = null;

    private StateManager stateManager = null;

    private AttractManager attractManager = null;
    
    /**
     * Construct.
     * 
     * @param Manager
     *            object.
     * 
     */
    public RendererCanvas(BulletmlPlayer gameManager, StateManager stateManager, AttractManager attractManager, int lineWidth, int screenWidth,
            int screenHeight)
    {
        this.gameManager = gameManager;
        this.stateManager = stateManager;
        this.attractManager = attractManager;

        screen = new ScreenCanvas(lineWidth, screenWidth, screenHeight);

        // This will save us having to pass the screen object around but its horrible design.
        this.gameManager.setScreen(screen);
        this.attractManager.setScreen(screen);
        
        LetterRender.setScreen(screen);
    }

    @Override
    public void drawFrame(Canvas canvas)
    {
        final ProfileRecorder profileRecorder = ProfileRecorder.sSingleton;

        profileRecorder.start(ProfileRecorder.PROFILE_DRAW);
        
        screen.setCanvas(canvas);

        profileRecorder.start(ProfileRecorder.PROFILE_SIM);

        stateManager.update();

        // Update view elements
        gameManager.update();

        profileRecorder.stop(ProfileRecorder.PROFILE_SIM);

        // Draw view elements
        gameManager.draw();
        
        profileRecorder.stop(ProfileRecorder.PROFILE_DRAW);
    }

    @Override
    public void sizeChanged(int width, int height)
    {
        // Nowt
    }

    /**
     * Return screen reference.
     * 
     * @return
     */
    public IScreen getScreen()
    {
        return screen;
    }

}
