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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

import com.netthreads.android.noiz2.data.SpriteTexture;
import com.netthreads.android.noiz2.data.TextureCache;

/**
 * Implements OpenGL screen drawing routines.
 * 
 */
public class ScreenGL implements IScreen
{
    private static final int DOT_RADIUS = 2;
    
	private GL10 surface = null;

	private ByteBuffer lineBuffer = null;
    private ByteBuffer colorBuffer = null;

	private IntBuffer lineIntBuffer = null;
    private IntBuffer colorIntBuffer = null;
	
	private int lineWidth = 1;

	private int screenWidth = 0;
	private int screenHeight = 0;
	
    private static int[] line = null;
    private static int[] color = null;

    private static final int ONE = 0x10000;
    
    private static final int LINE_BUFFER_SIZE = 3500;
    
    private int vertexIndex = 0;
    private int colorIndex = 0;
    private int lineIndex = 0;

    private TextureCache cache = null;
    
	public ScreenGL(int lineWidth, int screenWidth, int screenHeight)
	{
	    line = new int[LINE_BUFFER_SIZE*2*3];  // size*points_per_line*vertice_per_point
        color = new int[LINE_BUFFER_SIZE*2*4]; // size*points_per_line*color_value_per_point (r, g, b, a)
	    
		this.lineWidth = lineWidth;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		// // Buffer
        lineBuffer = ByteBuffer.allocateDirect(line.length*4);
		lineBuffer.order(ByteOrder.nativeOrder());
        lineIntBuffer = lineBuffer.asIntBuffer();

		// Color
        colorBuffer = ByteBuffer.allocateDirect(color.length*4);
        colorBuffer.order(ByteOrder.nativeOrder());
		
		colorIntBuffer = colorBuffer.asIntBuffer();
		
        this.cache = TextureCache.getInstance();
	}

	/**
	 * Clear the screen.
	 * 
	 */
	@Override
	public void clear()
	{
		// Implement
		surface.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// Draw buffer components filled through last pass through data.
		drawBuffer();		
	}

    /**
     * Draw alpha blended line.
     * 
     * @param surface
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param lineColor
     */
    @Override
    public void drawLine(int x1, int y1, int x2, int y2, int lineColor)
    {
        fillBuffer(x1, y1, x2, y2, lineColor);

        lineIndex++;
    }

    /**
     * Draw line buffer contents filled on the last pass through the data.
     * 
     */
    private void drawBuffer()
    {
        if (lineIndex>0)
        {
            // Reset
//            lineIntBuffer.clear(); // Workaround
//            lineIntBuffer.compact(); // Workaround
//            colorIntBuffer.clear(); // Workaround
//            colorIntBuffer.compact(); // Workaround
            
            lineIntBuffer.put(line);
            lineIntBuffer.position(0);
            
            colorIntBuffer.put(color);
            colorIntBuffer.position(0);
            
            surface.glVertexPointer(3, GL10.GL_FIXED, 0, lineIntBuffer);
            surface.glColorPointer(4, GL10.GL_FIXED, 0, colorIntBuffer);
            
            surface.glEnableClientState(GL10.GL_VERTEX_ARRAY); // Enable vertex array
            surface.glEnableClientState(GL10.GL_COLOR_ARRAY);  // Enable colour array   
            
            surface.glLineWidthx(lineWidth);
            
            surface.glDrawArrays(GL10.GL_LINES, 0, lineIndex*2);
    
            surface.glDisableClientState(GL10.GL_VERTEX_ARRAY); // Disable use of  vertex array
            surface.glDisableClientState(GL10.GL_COLOR_ARRAY); // Disable use of  colour array
            
            vertexIndex = 0;
            colorIndex = 0;
            lineIndex = 0;
        }
        
    }
    /**
     * Fill buffer.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param lineColor
     */
    private void fillBuffer(int x1, int y1, int x2, int y2, int lineColor)
    {
        int red = ((lineColor&0x00FF0000))/255;
        int green = ((lineColor&0x0000FF00)<<8)/255;
        int blue = ((lineColor&0x000000FF)<<16)/255;
        
        line[vertexIndex++] = x1<<16;
        line[vertexIndex++] = (screenHeight - y1)<<16;
        line[vertexIndex++] = 0;

        line[vertexIndex++] = x2<<16;
        line[vertexIndex++] = (screenHeight - y2)<<16;
        line[vertexIndex++] = 0;

        color[colorIndex++] = red;
        color[colorIndex++] = green;
        color[colorIndex++] = blue;
        color[colorIndex++] = ONE;
        
        color[colorIndex++] = red;
        color[colorIndex++] = green;
        color[colorIndex++] = blue;
        color[colorIndex++] = ONE;
    }
    
	/**
	 * Draw bitmap to screen.
	 * 
	 * @param bitmap
	 * @param left
	 * @param top
	 */
	@Override
	public void drawBitmap(int id, float left, float top)
	{
        SpriteTexture texture = cache.get(id);
      
        if (texture!=null)
        {
            int[] ids = texture.getTextureIds();
          
            if (ids!=null)
            {
                surface.glEnable(GL10.GL_TEXTURE_2D);
                
                surface.glBindTexture(GL10.GL_TEXTURE_2D, ids[0]);
        
                // Draw using the DrawTexture extension.
                int height  = texture.getHeight();
                
                ((GL11Ext)surface).glDrawTexfOES(left, screenHeight - top - height, 0, texture.getWidth(), height);
                
                surface.glDisable(GL10.GL_TEXTURE_2D);
            }
        }
	}

    @Override
    public void drawDot(int x1, int y1, int color)
    {
        drawLine(x1, y1, x1+DOT_RADIUS, y1+DOT_RADIUS, color);
    }
    

    @Override
    public void drawThickLine(int x1, int y1, int x2, int y2, int color1, int color2)
    {
        drawLine(x1, y1, x2, y2, color1);
    }
    
    /**
     * No canvas no surface to draw to.
     * 
     * @param canvas
     */
    public void setSurface(GL10 surface)
    {
        this.surface = surface;
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
