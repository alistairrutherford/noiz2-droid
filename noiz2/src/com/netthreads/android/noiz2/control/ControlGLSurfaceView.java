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

package com.netthreads.android.noiz2.control;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.netthreads.android.noiz2.ApplicationPreferences;
import com.netthreads.android.noiz2.data.StateData;

/**
 * GL Surface view with input handling. 
 * 
 *
 */
public class ControlGLSurfaceView extends GLSurfaceView 
{
    private static final long SLEEP_TIME = 100L;

    private StateData state = null;

    private int velocity = ApplicationPreferences.TRACKBALL_VELOCITY_DEFAULT;

    public ControlGLSurfaceView(Context context, StateData stateData)
    {
        super(context);

        this.state = stateData;
        
        this.velocity = ApplicationPreferences.getInstance().getTrackballVelocity();
    }

    /**
     * Triggered on touch event.
     * 
     * We sleep a little to allow scheduler to give time to draw thread.
     * 
     * We defer the processing by queueing it.
     * 
     * NB: This is supposed to be faster than overriding onTouchEvent but I am
     * not convinced.
     * 
     * @param The
     *            touch event.
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        // -----------------------------------------------------------
        // We need to relinquish some CPU time to the draw thread. To
        // do this we have a wee sleep here which will let the
        // scheduler switch back to the draw thread. Otherwise the
        // UI thread and the draw thread compete for CPU and the FPS
        // rate drops.
        // -----------------------------------------------------------
        SystemClock.sleep(SLEEP_TIME);

        queueEvent(new Runnable()
        {
            public void run()
            {
                float x = event.getX();
                float y = event.getY();
 
                // **BUG** switching to use the trackball sometimes causes a ghost
                // event to be sent with values <1 for x and y. How annoying
                if (x>1&&y>1)
                {
                    state.controlX = x; 
                    state.controlY = y;
    
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        state.touched = true;
                    }
                    else
                    {
                        state.touched = false;
                    }
                }
            }
        });

        return true;
    }

    @Override
    public boolean onTrackballEvent(final MotionEvent event)
    {
        SystemClock.sleep(SLEEP_TIME);

        queueEvent(new Runnable()
        {
            public void run()
            {
                handleTrackball(event);
            }
        });

        return true;
    }

    /**
     * Handle Trackball events.
     * 
     * @param Trackball
     *            event
     */
    public boolean handleTrackball(final MotionEvent event)
    {
        
        final float scaleX = event.getXPrecision();
        final float scaleY = event.getYPrecision();


        float x = state.controlX + Math.round(event.getX() * scaleX * velocity);
        float y = state.controlY + Math.round(event.getY() * scaleY * velocity);

        if (x>0 && y>0)
        {
            state.controlX = x;
            state.controlY = y;
            
            int width = state.viewWidth;
            int height = state.viewHeight;
    
            if (state.controlX < 0)
                state.controlX = 0;
            else
                if (state.controlX > width)
                    state.controlX = width;
    
            if (state.controlY < 0)
                state.controlY = 0;
            else
                if (state.controlY > height)
                    state.controlY = height;
    
        }
        
        return true;
    }

}