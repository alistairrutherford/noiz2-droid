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

import java.util.HashMap;

import android.content.Context;

public class GLBitmapCache implements BitmapCache
{
    private static GLBitmapCache instance = null;

    private HashMap<Integer, Integer> map = null;
    private int[] resid = null;
    
    private int count = 0;
    
    public synchronized static GLBitmapCache getInstance()
    {
        if (instance==null)
        {
            instance = new GLBitmapCache();
        }
        
        return instance;
    }

    public GLBitmapCache()
    {
        map = new HashMap<Integer, Integer>();
        resid = new int[CACHE_SIZE];
    }

    /**
     * The GL load only takes place when the GL surface is available. What we
     * do here is make a texture place-marker which will be used to provide the
     * appropriate info when the GL surface is created. 
     * 
     * @param resid
     * 
     * @return id of resource
     */
    @Override
    public int load(Context context, int resource)
    {
        Integer index = map.get(resource);

        if (index==null)
        {
            TextureCache cache = TextureCache.getInstance();
            
            try
            {
                index = count;
                
                cache.define(new SpriteTexture(resource, 1, 1, 32, 32), 32, 32);
                
                resid[count++] = resource;
                
                map.put(resource, index);
            } 
            catch (Exception e)
            {
                // Oh dear
            }
        }
        
        return index;
    }

}
