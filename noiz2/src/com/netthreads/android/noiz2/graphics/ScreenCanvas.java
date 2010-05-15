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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.netthreads.android.noiz2.data.CanvasBitmapCache;

/**
 * Implements Canvas screen drawing routines.
 * 
 */
public class ScreenCanvas implements IScreen
{
    private static final int DOT_RADIUS = 2;
    private static final int COLOUR_MASK = 0xFF000000;

    private Canvas surface = null;

    private Paint paintLineStyleA = null;
    private Paint paintLineStyleB = null;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private CanvasBitmapCache cache = null;

    public ScreenCanvas(int lineWidth, int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        paintLineStyleA = new Paint();
        paintLineStyleA.setStyle(Paint.Style.STROKE);
        paintLineStyleA.setStrokeWidth(lineWidth);
        paintLineStyleA.setAntiAlias(true);

        paintLineStyleB = new Paint();
        paintLineStyleB.setStyle(Paint.Style.STROKE);
        paintLineStyleB.setStrokeWidth(lineWidth + 1);
        paintLineStyleB.setAntiAlias(true);

        this.cache = CanvasBitmapCache.getInstance();
    }

    /**
     * Clear the screen.
     * 
     */
    @Override
    public void clear()
    {
        surface.drawARGB(0xFF, 00, 00, 00);
    }

    /**
     * draw alpha blended line
     * 
     * @param surface
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     */
    @Override
    public void drawLine(int x1, int y1, int x2, int y2, int color)
    {
        float xa = x1;
        float ya = y1;
        float xb = x2;
        float yb = y2;

        int c = COLOUR_MASK | color;
        paintLineStyleA.setColor(c);

        surface.drawLine(xa, ya, xb, yb, paintLineStyleA);
    }

    /**
     * draw alpha blended line
     * 
     * @param surface
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     */
    @Override
    public void drawThickLine(int x1, int y1, int x2, int y2, int color1, int color2)
    {
        float xa = x1;
        float ya = y1;
        float xb = x2;
        float yb = y2;

        int c = COLOUR_MASK | color1;
        paintLineStyleB.setColor(c);

        surface.drawLine(xa, ya, xb, yb, paintLineStyleB);
    }

    /**
     * draw alpha blended line
     * 
     * @param surface
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     */
    @Override
    public final void drawDot(int x1, int y1, int color)
    {
        float xa = x1;
        float ya = y1;

        int c = COLOUR_MASK | color;
        paintLineStyleA.setColor(c);

        surface.drawLine(xa, ya, xa + DOT_RADIUS, ya + DOT_RADIUS, paintLineStyleA);
    }

    /**
     * Draw bitmap onto screen.
     * 
     * @param bitmap
     * @param left
     * @param top
     */
    @Override
    public void drawBitmap(int id, float left, float top)
    {
        // Use id to lookup data item
        Bitmap bitmap = cache.fetch(id);
        
        this.surface.drawBitmap(bitmap, left, top, null);
    }

    /**
     * Set surface to draw to.
     * 
     * @param canvas
     */
    public void setCanvas(Canvas canvas)
    {
        this.surface = canvas;
    }

    public int getScreenWidth()
    {
        return screenWidth;
    }

    public int getScreenHeight()
    {
        return screenHeight;
    }

}
