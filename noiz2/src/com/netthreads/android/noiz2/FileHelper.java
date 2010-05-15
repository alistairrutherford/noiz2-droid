/**
 * Copyright (C) 2008 The Android Open Source Project
 * Copyright (C) 2008 Alistair Rutherford, Glasgow, Scotland, UK, www.netthreads.co.uk
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
package com.netthreads.android.noiz2;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;

public class FileHelper 
{
    /**
     * Read Help file
     * 
     * @param activity
     * 
     * @return Characters
     */
    public static CharSequence readAsset(String asset, Context context)
    {
        BufferedReader in = null;

        try
        {
            in = new BufferedReader(new InputStreamReader(context.getAssets().open(asset)));

            String line;
            StringBuilder buffer = new StringBuilder();

            while ((line = in.readLine()) != null)
            {
                buffer.append(line).append('\n');
            }

            return buffer;
        }
        catch (IOException e)
        {
            return "";
        }
        finally
        {
            closeStream(in);
        }
    }

    /**
     * Closes the specified stream.
     *
     * @param stream The stream to close.
     */
    public static void closeStream(Closeable stream)
    {
        if (stream != null)
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                // Ignore
            }
        }
    }

}
