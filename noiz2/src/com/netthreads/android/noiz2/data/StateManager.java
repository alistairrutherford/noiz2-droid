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


/**
 * Update our state structure.
 * 
 */
public class StateManager
{
    private static final float VELOCITY = 5.0f;

    private StateData state = null;
    private int offsetY;
    private float velocity;

    /**
     * Create mover command against layer.
     * 
     * @param layer
     */
    public StateManager(StateData state, int offset)
    {
        this.state = state;

        offsetY = offset*5;

        velocity = VELOCITY / 1000.0f;
    }

    /**
     * Update ship position
     * 
     */
    private void updateShip()
    {
        float v = state.timeDeltaMsec * velocity;

        float vx = (state.controlX - state.currentX) * v;

        state.currentX += vx;

        // Note, you have to include the offset in calculating how much the current
        // position should move.
        float vy = (state.controlY - state.currentY - offsetY) * v;

        state.currentY += vy;

        // Clamp
        if (state.currentY < 0) state.currentY = 0;
        if (state.currentX < 0) state.currentX = 0;
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
