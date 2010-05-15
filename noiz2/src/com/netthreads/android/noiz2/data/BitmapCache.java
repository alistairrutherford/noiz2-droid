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

import android.content.Context;

public interface BitmapCache
{
    public static final int CACHE_SIZE = 5;
    
    /**
     * Loads resource and returns key.
     * 
     * @param context
     * @param resource
     * 
     * @return unique key for resource. This is not the same as the resource id.
     */
    public int load(Context context, int resource);
    
}
