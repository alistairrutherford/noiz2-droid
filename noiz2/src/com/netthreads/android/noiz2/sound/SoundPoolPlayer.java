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
package com.netthreads.android.noiz2.sound;

import java.util.HashMap;

import com.netthreads.android.noiz2.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundPoolPlayer implements Player
{
    private static SoundPoolPlayer _instance = null;

    private SoundPool pool = null;

    private Context context = null;

    private HashMap<Integer, Integer> sounds = null;

    private boolean sound = false;
    
    /**
     * Singleton access.
     * 
     * @return instance
     */
    public static synchronized SoundPoolPlayer instance()
    {
        if (_instance == null)
        {
            _instance = new SoundPoolPlayer();
        }

        return _instance;
    }

    /**
     * Private ctor.
     * 
     */
    private SoundPoolPlayer()
    {
	    sounds = new HashMap<Integer, Integer>();
    }

    /**
     * Call this to open pool.
     * 
     * @param The application context.
     */
    @Override
    public void open(Context context)
    {
        this.context = context;
        
        pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);

        Log.d("open", "Load resources.");

        sounds.put(R.raw.enemy_arrival, pool.load(context, R.raw.enemy_arrival, 1));
        sounds.put(R.raw.ship_explosion1, pool.load(context, R.raw.ship_explosion1, 1));
        sounds.put(R.raw.ship_explosion2, pool.load(context, R.raw.ship_explosion2, 1));
        sounds.put(R.raw.zap1, pool.load(context, R.raw.zap1, 1));
        
        // Always initially no sound
        sound = false;
    }

    @Override
    public void close()
    {
        Log.d("close", "Release resources.");

        pool.release();
    }

    /**
     * Play specified sound.
     * 
     * @param Sound id.
     */
    @Override
    public void play(int id)
    {
        if (sound)
        {
            AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    
            float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = streamVolumeCurrent / streamVolumeMax;
    
            pool.play(sounds.get(id), volume, volume, 1, 0, 1f);
        }
    }

    /**
     * Set sound state.
     * 
     * @param sound
     */
    public void setSound(boolean sound)
    {
        this.sound = sound;
    }
    
}
