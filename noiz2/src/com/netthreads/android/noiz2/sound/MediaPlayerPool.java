package com.netthreads.android.noiz2.sound;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Implements a pool of MediaPlayer objects for target sound resources.
 * 
 */
public class MediaPlayerPool implements Pool
{
    private HashMap<Integer, MediaPlayer[]> pool = null;
    private HashMap<Integer, Integer> next = null;

    private Context context = null;

    private int type = AudioManager.STREAM_MUSIC;

    public MediaPlayerPool(Context context, int type)
    {
        this.context = context;
        this.type = type;

        pool = new HashMap<Integer, MediaPlayer[]>();
        next = new HashMap<Integer, Integer>();
    }

    @Override
    public void load(int id, int poolsize)
    {
        MediaPlayer[] players = new MediaPlayer[poolsize];

        for (int index = 0; index < poolsize; index++)
        {
            players[index] = create(id);
        }

        // Place into pool.
        pool.put(id, players);
        next.put(id, 0);
    }

    @Override
    public void release()
    {
        for (MediaPlayer[] players : pool.values())
        {
            for (MediaPlayer player : players)
            {
                if (player.isPlaying())
                {
                    player.stop();
                }

                player.release();
            }
        }

        pool.clear();
    }

    @Override
    public synchronized void play(int id)
    {
        MediaPlayer[] players = pool.get(id);

        int count = players.length;

        int index = next.get(id);

        if (!players[index].isPlaying())
        {
            AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = streamVolumeCurrent / streamVolumeMax;

            players[index].start();

            players[index].setVolume(volume, volume);

            next.put(id, (index++) % count);
        }
        else
        {
            Log.w("Player Busy", "Increase the size of pool for resource, " + id);
        }
    }

    /**
     * Create player instance against sound sample.
     * 
     * @param id
     * 
     * @return The player
     */
    private MediaPlayer create(int id)
    {
        MediaPlayer player = MediaPlayer.create(context, id);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            public void onCompletion(MediaPlayer player)
            {
                player.seekTo(0);
            }
        });

        player.setAudioStreamType(type);

        return player;
    }

}
