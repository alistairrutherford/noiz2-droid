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
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This is kind of a weird cache which keeps a note of what has been loaded and where.
 * The use of the array is to make it fast. The one drawback is that you have to tailor
 * the cache array big enough to fit all your data you can't expect it to keep filling up.
 *
 */
public class CanvasBitmapCache implements BitmapCache
{
    private static CanvasBitmapCache instance = null;
    
    private HashMap<Integer, Integer> map = null;
    private Bitmap[] data = null;
    
    private int count = 0;
    
    private BitmapFactory.Options bitmapOptions = null;

    public synchronized static CanvasBitmapCache getInstance()
    {
        if (instance==null)
        {
            instance = new CanvasBitmapCache();
        }
        
        return instance;
    }

    public CanvasBitmapCache()
    {
        map = new HashMap<Integer, Integer>();
        data = new Bitmap[CACHE_SIZE];
        
        bitmapOptions = new BitmapFactory.Options();
        
        // Set default.
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        
        for (int index=0; index<CACHE_SIZE; index++)
        {
            data[index]=null;
        }
    }

    /**
     * Load resource and return id used to access it.
     * 
     * @param resource id
     * 
     * @return id of resource
     */
    @Override
    public int load(Context context, int resource)
    {
        Integer index = map.get(resource);

        if (index==null)
        {
            try
            {
                Bitmap item = loadBitmap(context, resource);
    
                index = count;
                
                if (item!=null)
                {
                    data[count] = item;

                    map.put(resource, index);
                    
                    count++;
                }
                
            } 
            catch (IOException e)
            {
                // Ignore
            }
        }

        return index;
    }

    /**
     * Returns bitmap data. The id is really an index into the data cache. This is faster than using a 
     * hash-map or something like that.
     * 
     * @param id
     * 
     * @return The resource.
     */
    public Bitmap fetch(int id)
    {
        return data[id];
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
