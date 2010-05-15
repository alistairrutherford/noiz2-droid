package com.netthreads.android.noiz2.sound;

import com.netthreads.android.noiz2.R;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class MediaPlayerPlayer implements Player
{
    private static MediaPlayerPlayer _instance = null;
    
    private MediaPlayerPool pool = null;

    /**
     * Singleton access.
     * 
     * @return instance
     */
    public static synchronized MediaPlayerPlayer instance()
    {
        if (_instance == null)
        {
            _instance = new MediaPlayerPlayer();
        }

        return _instance;
    }
    
    /**
     * Need to call this before using.
     * 
     * @param The application context.
     */
    @Override
    public void open(Context context)
    {
        pool = new MediaPlayerPool(context, AudioManager.STREAM_MUSIC);
        
        Log.d("open", "Load resources.");
        
        pool.load(R.raw.enemy_arrival, 4);
        pool.load(R.raw.ship_explosion1, 2);
        pool.load(R.raw.ship_explosion2, 2);
    }

    @Override
    public void close()
    {
        Log.d("close", "Release resources.");
        
        pool.release();
    }

    @Override
    public void play(int id)
    {
        pool.play(id);
    }

}
