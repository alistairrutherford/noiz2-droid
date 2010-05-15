package com.netthreads.android.noiz2.sound;

import android.content.Context;

public interface Player
{
    /**
     * Open player.
     * 
     * @param context
     */
    public void open(Context context);
    
    /**
     * Close player.
     * 
     */
    public void close();
    
    /**
     * Play sound.
     * 
     * @param id
     */
    public void play(int id);
}
