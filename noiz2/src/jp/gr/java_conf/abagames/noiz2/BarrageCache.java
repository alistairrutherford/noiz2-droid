/**
 * Copyright 2002 Kenta Cho. All rights reserved.
 * 			Original
 * Copyright (C) 2009 Alistair Rutherford, Glasgow, Scotland, UK, www.netthreads.co.uk
 * 			Various modifications.
 * 
 * Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided that
 * the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *      
 *  2. Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution. 

 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 *  THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 *  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package jp.gr.java_conf.abagames.noiz2;

import java.io.IOException;
import java.io.InputStream;

import jp.gr.java_conf.abagames.bulletml.Bulletml;
import jp.gr.java_conf.abagames.bulletml.BulletmlManager;
import android.content.Context;
import android.content.res.AssetManager;

/**
 * Cache our bulletml files so we don't have to reload them each time we run the main
 * game activity.
 * 
 */
public class BarrageCache
{
	public static final int BARRAGE_TYPE_NUM = 3;

	public static final int BARRAGE_TYPE_ZAKO = 0;
	public static final int BARRAGE_TYPE_MIDDLE = 1;
	public static final int BARRAGE_TYPE_BOSS = 2;

	private static BarrageCache _instance = null;

	private int total = 0;
	
	/*
	 * Types of enemies. ('zako' - small enemies, 'middle' - middle class
	 * enemies, 'boss' - boss type enemies.)
	 */
	private Barrage[][] pattern;
	private Barrage[][] queue;
	
    private String[][] files = {
            {
                "bar.xml",
                "nway.xml",
                "248shot.xml",
                "slowdown.xml",
                "twin.xml",
                "bee.xml",
                "3waychase.xml",
                "spread.xml",
                "accel.xml",
                "accum.xml",
                "thrust.xml",
                "stay.xml",
                "[Ikaruga]_r5_vrp.xml",
            }, 
            {
                "23accel.xml",
                "spreadnn.xml",
                "4waccel.xml",
                "[daiouzyou]_r1_boss_3.xml",
                "mnway.xml",
                "[Ikaruga]_rf_l.xml",
                "xfire.xml",
                "2round.xml",
                "4way.xml",
                "vrtlas.xml",
                "22way.xml",
                "spread_bf.xml",
                "growround.xml",
                "[daiouzyou]_r1_boss_2.xml",
                "grow.xml",
                "[Ikaruga]_r3_mdl_3.xml",
            }, 
            {
                "[daiouzyou]_r1_boss_l.xml",
                "rollbar.xml",
                "[Guwange]_round_4_boss_eye_ball.xml",
                "[Progear]_round_5_middle_boss_rockets.xml",
                "88way.xml",
                "[daiouzyou]_r1_boss_4.xml",
                "[Progear]_round_3_boss_wave_bullets.xml",
                "[daiouzyou]_hibati_2_2_r.xml",
                "[daiouzyou]_r1_boss_1.xml",
                "[Progear]_round_2_boss_struggling.xml",
                "[Psyvariar]_X-A_boss_opening.xml",
                "[Ikaruga]_r1_mdl.xml",
                "[daiouzyou]_hibati_2_2_b.xml",
                "[Guwange]_round_2_boss_circle_fire.xml",
                "[Progear]_round_5_boss_last_round_wave.xml",
                "[Progear]_round_1_boss_grow_bullets.xml",
                "[Ikaruga]_drc2.xml",
                "[daiouzyou]_r1_boss_5.xml",
                "[Progear]_round_3_boss_back_burst.xml",
                "[Guwange]_round_3_boss_fast_3way.xml",
                "[G_DARIUS]_homing_laser.xml",
            }
            };
	
	private int currentBarrage = 0;
	private int currentFile = 0;

	private AssetManager assetManager = null;

	private boolean loaded = false;

	/**
	 * Construct bulletml structures.
	 * 
	 * @param context
	 */
	public BarrageCache(Context context)
	{
		queue = new Barrage[BARRAGE_TYPE_NUM][];
		pattern = new Barrage[BARRAGE_TYPE_NUM][];
		
		// Generate structures.
		for (int barrageIndex=0; barrageIndex<BARRAGE_TYPE_NUM; barrageIndex++)
		{
			queue[barrageIndex] = new Barrage[files[barrageIndex].length];
			pattern[barrageIndex] = new Barrage[files[barrageIndex].length];
		}
		
		// Provide access to local assets.
    	assetManager = context.getResources().getAssets();
    	
    	total = calcTotal();
	}

	/**
	 * Singleton access.
	 * 
	 * It's a but weird since we have to pass in some setup info.
	 * 
	 * @param context
	 * @param state
	 * 
	 * @return Instance of this class.
	 */
	public static synchronized BarrageCache instance(Context context)
	{
		if (_instance==null)
		{
			_instance = new BarrageCache(context);
		}
		return _instance;
	}
	
	/**
	 * Load bulletml files ones at a time.
	 * 
	 * @return True then all files have been loaded.
	 * 
	 * @throws IOException 
	 */
	public boolean loadFile(int screenWidth, int screenHeight) throws IOException
	{
		boolean status = false;
		
		if (currentBarrage< BARRAGE_TYPE_NUM)
		{
			int fileCount = files[currentBarrage].length;

			if (currentFile < fileCount)
			{
				BulletmlManager manager = new BulletmlManager(screenWidth, screenHeight);
				manager.init();
				
				String fileName = files[currentBarrage][currentFile];
				
	        	InputStream stream = assetManager.open(fileName);
				
	        	@SuppressWarnings("unused")
				Bulletml bulletml = new Bulletml(stream, manager);
				
				stream.close();
				
				pattern[currentBarrage][currentFile] = new Barrage(manager);
				pattern[currentBarrage][currentFile].type = currentBarrage;
				
				currentFile++;
			}
			else
			{
				currentFile = 0;
				currentBarrage++;
			}
		}
		else
		{
			status = true;
			loaded = true;
		}
		
		return status;
	}

	/**
	 * Force load.
	 * 
	 * The intention is to load the data piecemeal to stop the startup from being delayed. If for
	 * some reason that isn't done then this method will load all the data.
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 */
	public void load(int screenWidth, int screenHeight)
	{
		boolean done = false;
		
		while (!done)
		{
			try
			{
				done = loadFile(screenWidth, screenHeight);
			}
			catch (IOException e)
			{
				// Failed to load!
				e.printStackTrace();
			}
		}
	}
	
	public Barrage[][] getPattern()
	{
		return pattern;
	}

	public Barrage[][] getQueue()
	{
		return queue;
	}

	/**
	 * Indicates if the structures have been loaded.
	 * 
	 * @return Status
	 */
	public boolean isLoaded()
	{
		return loaded;
	}

	/**
	 * Calculate the total number of files in the filename cache.
	 * 
	 * @return count.
	 */
	private int calcTotal()
	{
        int total = 0;
        
        int columns = files.length;
        for (int i=0; i<columns; i++)
        {
            int rows = files[i].length;
            
            total = total + rows;
        }
        
        return total;
	}
	
	/**
	 * Total number of barrage files.
	 * 
	 * @return count
	 */
	public int getTotal()
	{
	    return total;
	}
}
