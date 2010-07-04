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

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.netthreads.android.noiz2.graphics.RendererGL;
import com.netthreads.android.noiz2.graphics.opengl.Grid;

/**
 * Manages GL textures cache.
 * 
 * This acts a kind of implementation layer below the GLBitmapCache.
 * 
 */
public class TextureCache
{
    private static TextureCache instance = null;
    
    private SpriteTexture[] images = null;

    private boolean useHardwareBuffers = false;
    private boolean useVerts = false;
    private int count = 0;

    private BitmapFactory.Options bitmapOptions = null;
    
    /**
     * Create structures.
     *
     */
    public TextureCache()
    {
        images = new SpriteTexture[BitmapCache.CACHE_SIZE];

        bitmapOptions = new BitmapFactory.Options();

        // Set default. R5G6B5 
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    /**
     * Access singleton instance
     *
     * @return instance of class
     */
    public synchronized static TextureCache getInstance()
    {
        if (instance == null)
        {
            instance = new TextureCache();
        }

        return instance;
    }

    /**
     * Get pre-loaded texture. The id is really an index into an array of defns.
     * This is faster than using a Hash-Map. 
     *
     * @param id
     *
     * @return GL texture id
     */
    public SpriteTexture get(int id)
    {
    	SpriteTexture target = images[id];

        return target;
    }
    
    /**
     * Top level cache access, get texture defn from cache.
     *
     * @param context
     * @param resourceId
     * @param renderer
     * @param gl
     * 
     * @return Texture defn
     */
    public void load(Context context, SpriteTexture target, RendererGL renderer, GL10 gl)
    {
    	try
    	{
        	if (!target.isLoaded())
        	{
        		loadTexture(context, target, renderer, gl);
        	}
        }
        catch (Exception e)
        {
        	// Texture definition not pre-loaded
            //Log.d("Error", e.getMessage());
        }
    }

    /**
     * Remove from cache.
     * 
     * @param resourceId
     * @param gl
     */
    public void unload(SpriteTexture texture, GL10 gl, boolean freeHardwareBuffers)
    {
    	if (texture!=null && texture.isLoaded())
    	{
    		gl.glDeleteTextures(1, texture.getTextureIds(), 0);

    		if (useHardwareBuffers && freeHardwareBuffers)
    		{
    			texture.getGrid().freeHardwareBuffers(gl);
    		}
    		
    		texture.setLoaded(false);
    		
    		texture.setTextureIds(null);
    	}
    }
    
    /**
     * Load all textures into GL context.
     * 
     * @param context
     * @param resourceId
     * @param renderer
     * @param gl
     */
    public void loadAll(Context context, RendererGL renderer, GL10 gl)
    {
		int length = images.length;

    	for (int index = 0; index< length; index++)
    	{
    	    SpriteTexture texture = images[index];
    	    
			Grid grid = texture.getGrid();
			
    		if (useHardwareBuffers && grid!=null)
    		{
	        	grid.forgetHardwareBuffers();
    		}
    		
    		load(context, texture, renderer, gl);
    		
    		if (useHardwareBuffers && grid!=null)
    		{
	            grid.generateHardwareBuffers(gl);
	        }    		
    	}
    }
    
    /**
     * Unload all textures from gl context.
     * 
     * @param gl
     */
    public void unloadAll(GL10 gl, boolean freeHardwareBuffers)
    {
        int length = images.length;
        
        for (int index = 0; index< length; index++)
        {
            SpriteTexture texture = images[index];
            
    		unload(texture, gl, freeHardwareBuffers);
    	}
    }
    
    /**
     * Load textures.
     * 
     * @param context
     * @param texture
     * @param renderer
     * @param gl
     * @throws IOException
     */
    private void loadTexture(Context context, SpriteTexture texture, RendererGL renderer, GL10 gl) throws IOException
    {
    	if (texture.getCols()>1)
    	{
    		loadMultipleTexture(context, texture, renderer, gl);
    	}
    	else
    	{
    		loadSingleTexture(context, texture, renderer, gl);
    	}
    }
    
    /**
     * Load single texture
     * 
     * @param context
     * @param texture
     * @param renderer
     * @param gl
     * @throws IOException
     */
    private void loadSingleTexture(Context context, SpriteTexture texture, RendererGL renderer, GL10 gl) throws IOException
    {
    	int[] id = loadSingleImage(context, texture.getResourceId(), texture.getResourceId(), renderer, gl);

    	texture.setTextureIds(id);
    	texture.setLoaded(true);
    }

    /**
     * Load multiple textures.
     * 
     * @param context
     * @param texture
     * @param renderer
     * @param gl
     * @throws IOException
     */
    private void loadMultipleTexture(Context context, SpriteTexture texture, RendererGL renderer, GL10 gl) throws IOException
    {
    	int[] ids = loadMultipleImage(context, texture.getResourceId(), texture.getRows(), texture.getCols(), texture.getWidth(), texture.getHeight(), texture.getXOffset(), texture.getYOffset(), renderer, gl);

    	texture.setTextureIds(ids);
    	texture.setLoaded(true);
    }
    
    /**
     * Pre-define a texture definition which will be used when the texture context 
     * becomes valid.
     * 
     * @param context
     * @param resourceId
     * @param defaultId
     * @param renderer
     * @param gl
     * 
     * @throws Exception 
     */
    public void define(SpriteTexture texture, int width, int height) throws Exception
    {
		if (useVerts)
		{
			Grid grid = createGrid(texture, width, height);
			
			texture.setGrid(grid);
		}
		
        images[count++] = texture;
    }
    
	/**
	 * Create sprite grid.
	 * 
	 * Only activated if hardware buffers used.
	 * 
	 * @return The sprite grid.
	 */
	public Grid createGrid(SpriteTexture texture, int width, int height)
	{
        // Setup a quad for the sprites to use.  All sprites will use the
        // same sprite grid instance.
		Grid spriteGrid = new Grid(2, 2);
        spriteGrid.set(0, 0, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        spriteGrid.set(1, 0, width, 0.0f, 0.0f, 1.0f, 1.0f);
        spriteGrid.set(0, 1, 0.0f, height, 0.0f, 0.0f, 0.0f);
        spriteGrid.set(1, 1, width, height, 0.0f, 1.0f, 0.0f);
		
        return spriteGrid;
	}    
	
    /**
     * Load single image.
     * 
     * @param context
     * @param resourceId
     * @param defaultId
     * @param renderer
     * @param gl
     * 
     * @return id
     * 
     * @throws IOException
     */
    private int[] loadSingleImage(Context context, int resourceId, int defaultId, RendererGL renderer, GL10 gl) throws IOException
    {
    	int id[] = null;
    	
        Bitmap image = loadBitmap(context, resourceId);
        
        if (image==null)
        {
        	// Try default id
        	image = loadBitmap(context, defaultId);
        }
        
        if (image!=null)
        {
        	int textureId = renderer.loadBitmap(gl, image);

        	id = new int[1];
        	id[0] = textureId;
        	
        	// Unload from memory
        	image.recycle();
        }

        return id;
    }

    /**
     * Load animation textures.
     * 
     * @param context
     * @param resourceId
     * @param numCols
     * @param numRows
     * @param width
     * @param height
     * @param xOffSet
     * @param yOffSet
     * @param renderer
     * @param gl
     * 
     * @return id list
     * 
     * @throws IOException
     */
    private int[] loadMultipleImage(Context context, int resourceId, int rows, int cols, int width, int height, int xOffSet, int yOffSet, RendererGL renderer, GL10 gl) throws IOException
    {
        Bitmap image = loadBitmap(context, resourceId);
        
    	int ids[] = new int[rows*cols];
    	
		for (int row=0; row<rows; row++)
		{
			for (int col=0; col<cols; col++)
			{
				int offsetX = col*width+xOffSet;
				int offsetY = row*height+yOffSet;
				
				int target = loadClippedImage(context, image, offsetX, offsetY, width, height, renderer, gl);
				
				ids[row*cols+col] = target;
			}
    	}
		
    	image.recycle();

		return ids;
    }
    
	/**
	 * Load clipped texture.
	 * 
	 * @param xOffSet
	 * @param yOffSet
	 * @param textWidth
	 * @param textHeight
	 * @param context
	 * @param resourceId
	 * @param renderer
	 * @param gl
	 * 
     * @return GL Texture id
     *
     * @throws IOException
	 */
	private int loadClippedImage(Context context, Bitmap image, int xOffSet, int yOffSet, int textWidth, int textHeight, RendererGL renderer, GL10 gl) throws IOException 
	{
        int imageId = -1;

        Bitmap clippedImage = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888);
        Canvas clippedCanvas = new Canvas(clippedImage);
        clippedCanvas.translate(-xOffSet, -yOffSet);

        clippedCanvas.drawBitmap(image, 0, 0, null);

        if (clippedImage!=null)
        {
        	imageId = renderer.loadBitmap(gl, clippedImage);
        }
        
        if (image!=null)
        {
        	clippedImage.recycle();
        }
	    
	    return imageId;
	}
	
    /**
     * Load bitmap.
     * 
     * @param context
     * @param resourceId
     * @param defaultId
     * 
     * @return Target bitmap or default if target wasn't found.
     */
    private Bitmap loadBitmap(Context context, int resourceId) throws IOException
    {
        Bitmap target = null;

        InputStream is = context.getResources().openRawResource(resourceId);
        
        try 
        {
            target = BitmapFactory.decodeStream(is, null, bitmapOptions);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch (IOException e) 
            {
                throw new IOException(e.getMessage());
            }
        }

        return target;
    }
	
}

