package com.netthreads.android.noiz2.sound;

public interface Pool
{
    /**
     * Load sound into pool, assign a pool size of sound instances.
     * 
     * @param id
     * @param poolsize
     */
    public void load(int id, int poolsize);
    
    /**
     * Release pool resources.
     * 
     */
    public void release();
    
    /**
     * Play sound from pool.
     * 
     * @param id
     */
    public void play(int id);
}
