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

public class StateData 
{
    // Time data
    private long lastTime = 0;
    
	// Control position
	public float controlX = 0;
	public float controlY = 0;
	public boolean touched = false;

    // Actual position
    public float currentX = 0;
    public float currentY = 0;
	
    public long time = 0;
    public long timeDelta = 0;
    public float timeDeltaMsec = 0;
    
	public int viewWidth = 0;
    public int viewHeight = 0;
    
    /**
     * Initialise state data.
     * 
     * @param viewWidth
     * @param viewHeight
     */
    public StateData(int viewWidth, int viewHeight)
    {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        
        this.controlX = viewWidth/2;
        this.controlY = viewWidth/2;
        
        this.currentX = controlX;
        this.currentY = controlY;
    }
    
    /**
     * Update time values.
     * 
     */
    public void updateTime()
    {
        time = System.currentTimeMillis();
        timeDelta = time - lastTime;
        timeDeltaMsec = (lastTime > 0.0f) ? timeDelta  : 0.0f;
        
        lastTime = time;
    }
    
}
