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

package com.netthreads.android.noiz2.data;

import jp.gr.java_conf.abagames.noiz2.Ship;

/**
 * Update our state structure. 
 * 
 */
public class StateManager 
{
    private static final float SPRING = 0.4f;
    private static final float DAMPING = 0.9f;
    private static final float VELOCITY = 1.0f;

    private StateData state = null;
    private int offsetX = 0;
    private int offsetY = 0;
    
    /**
     * Create mover command against layer.
     * 
     * @param layer
     */
    public StateManager(StateData state, int offset) 
    {
    	this.state = state;

    	offsetX = Ship.SHIP_SIZE/2;
    	offsetY = offset;
	}
    
    /**
     * Update ship position
     * 
     */
    private void updateShip() 
	{
    	float velocity = state.timeDeltaSeconds*VELOCITY;
    	
        float vx = ((state.controlX)-state.currentX)*SPRING;

        vx *=DAMPING;
            
        state.currentX += vx + velocity;
            
        float vy = ((state.controlY-offsetX)-state.currentY)*SPRING;
            
        vy *=DAMPING;
            
        state.currentY += vy + velocity-offsetY;

        // Clamp
        if (state.currentY<0) state.currentY = 0;
    }

    /**
     * Update all.
     * 
     */
    public void update()
    {
        state.updateTime();
        
        updateShip();
    }
}
