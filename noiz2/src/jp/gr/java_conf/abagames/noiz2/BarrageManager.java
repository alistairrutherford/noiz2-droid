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
/*
 * $Id: BarrageManager.java,v 1.3 2002/10/01 09:41:03 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;

import java.util.Random;

import jp.gr.java_conf.abagames.bulletml.BulletImpl;
import jp.gr.java_conf.abagames.bulletml.BulletmlManager;
import jp.gr.java_conf.abagames.bulletml.BulletmlPlayer;

import android.content.Context;

import com.netthreads.android.noiz2.ApplicationPreferences;
import com.netthreads.android.noiz2.R;
import com.netthreads.android.noiz2.data.StateData;

/**
 * Barrage pattern manager.
 * 
 * @version $Revision: 1.3 $
 */
public class BarrageManager
{
	// Constants
    private static final int BARRAGE_TYPE_NUM = 3;
    private static final int STAGE_ENDLESS = 10;

	/*
	 * Types of enemies. ('zako' - small enemies, 'middle' - middle class
	 * enemies, 'boss' - boss type enemies.)
	 */
	private static final int BARRAGE_MAX = 12;

	private static final int QUICK_APP_NUM = 1;

	public static final int INTERVAL_BASE = 16;
	private static final int PROCESS_SPEED_DOWN_BULLETS_NUM = 80;

	private static final int SCENE_TERM = 1000;
	private static final int SCENE_END_TERM = 100;
	private static final int SCENE_DISP_TERM = 172;

	private static final int ZAKO_APP_TERM = 1500;

	// Predefined
	
	// -------------------------------------------------------------------
	// We're going to adjust the difficulty a bit as the game is impossible
	// to play using the touch screen at higher levels of stage.
	// -------------------------------------------------------------------
	private final float[] difficulty = { 0.0f, 0.3f, 0.5f, 0.6f, 0.8f, 1.0f, 3.0f, 5.0f, 7.0f, 10.0f, 12.0f };

	private final float[][] stageSpec = {
            { 0, 0.0f, 0.2f }, { 1, 0.3f, 0.4f }, { 2, 1.0f, 0.3f }, { 3, 3.0f, 0.5f },
			{ 4, 5.0f, 0.2f }, { 5, 1.5f, 0.6f }, { 6, 4.0f, 1.5f }, { 7, 7.0f, 0.7f }, 
			{ 8, 6.0f, 1.2f }, { 9, 12.0f, 0.2f }, { -1, 1.0f, 0.4f } };
	
	private final int[] appFreq = { 90, 360, 800 };
	private final int[] shield = { 1, 2, 3 };

	private Barrage[] barrage = new Barrage[BARRAGE_MAX];

	// Vars

	private int screenHeight = 0;
	private int screenWidth = 0;

	private Barrage[][] barragePattern;
	private Barrage[][] barrageQueue;

	private Ship ship;

	private BulletmlPlayer player;

	// A Random instance for controlling the enemies' pattern.
	private Random patternRnd = new Random();

	private int stage, scene;

	private int sceneCount;
	private float level, levelInc;
	private String sceneStr, sceneScoreStr;
	private boolean endless;

	private int barrageNum;
	private int bulletNum;
	private boolean bossMode;

	private int sceneDispCnt;
	private int zakoAppCnt;

	private Random rnd = new Random();
	private int[] enNum = new int[QUICK_APP_NUM];

	private int[] pax = new int[QUICK_APP_NUM];
	private int[] pay = new int[QUICK_APP_NUM];

	private ApplicationPreferences preferences = null;
	private Context context = null;
	
	/**
	 * Load the files of BulletML.
	 * 
	 * @throws Exception
	 */
	public BarrageManager(Context context, StateData state)
	{
	    this.context = context;
		this.preferences = ApplicationPreferences.getInstance();
		
		this.screenWidth = state.viewWidth;
		this.screenHeight = state.viewHeight;

		init();

		BarrageCache cache = BarrageCache.instance(context);

		if (!cache.isLoaded())
		{
			// Force load if we haven't already pulled files in.
			cache.load(screenWidth, screenHeight);
		}

		barrageQueue = cache.getQueue();
		barragePattern = cache.getPattern();
	}

	/**
	 * Initialise
	 * 
	 */
	private void init()
	{
		for (int i = 0; i < QUICK_APP_NUM; i++)
		{
			pax[i] = -1;
		}
	}

	/**
	 * setPlayer
	 * 
	 * @param player
	 */
	public void setPlayer(BulletmlPlayer player)
	{
		this.player = player;
		for (int i = 0; i < BarrageCache.BARRAGE_TYPE_NUM; i++)
		{
		    int length = barragePattern[i].length;
		    
			for (int j = 0; j < length; j++)
			{
				barragePattern[i][j].manager.setPlayer(player);
			}
		}
	}

    /**
     * Initialise stage.
     * 
     * @param stage
     */
    public void initStage(int stage)
    {
        this.stage = stage;
        
        if (stage==STAGE_ENDLESS)
        {
            endless = true;
        }
        else
        {
            endless = false;
        }
        
        // Examine the play mode. Custom setting allows the adjustable difficulty
        if (preferences.getPlayMode().equals(context.getString(R.string.play_mode_adjustable_difficulty_text)))
        {
            initQueue((long) stageSpec[stage][0]);
            
            int rankValue = preferences.getRank();
            
            this.level = difficulty[rankValue];
            this.levelInc = 0;
        }
        else
        {
            initQueue((long) stageSpec[stage][0]);
            
            this.level = stageSpec[stage][1];
            this.levelInc = stageSpec[stage][2];
        }

        this.scene = 0;
        this.sceneCount = 0;
        this.barrageNum = 0;
        this.sceneStr = "";
        
        ship.init();
    }
    
    /**
     * 
     * @param seed
     */
    public void initQueue(long seed)
    {
        // Initialise a barrage queue.
        for (int i = 0; i < BARRAGE_TYPE_NUM; i++)
        {
            int length = barragePattern[i].length;
            
            for (int j = 0; j < length; j++)
            {
                barrageQueue[i][j] = barragePattern[i][j];
                barragePattern[i][j].manager.reinit();
            }
        }
    }
	
	/**
	 * 
	 * @param stage
	 */
	public void initStageAsDemo(int stage)
	{
		initStage(stage);
		setBarrages(level, false, false);
		sceneCount = Integer.MAX_VALUE;
	}

	/**
	 * Initialise barrages.
	 * 
	 * @param seed
	 * @param startLevel
	 * @param levelInc
	 */
	public void init(long seed, float startLevel, float levelInc)
	{
		// Initialise a barrage queue.
		for (int i = 0; i < BarrageCache.BARRAGE_TYPE_NUM; i++)
		{
		    int length = barragePattern[i].length;
		    
			for (int j = 0; j < length; j++)
			{
				barrageQueue[i][j] = barragePattern[i][j];
				barragePattern[i][j].manager.reinit();
			}
		}

		// Shuffle.
		if (seed >= 0)
		{
			patternRnd.setSeed(seed);
			endless = false;
		}
		else
		{
			patternRnd.setSeed(rnd.nextLong());
			endless = true;
		}
		for (int i = 0; i < BarrageCache.BARRAGE_TYPE_NUM; i++)
		{
			int barrageNumber = barrageQueue[i].length;
			
			for (int j = 0; j < 64; j++)
			{
				int n1 = Math.abs(patternRnd.nextInt()) % barrageNumber, n2 = Math.abs(patternRnd.nextInt()) % barrageNumber;
				Barrage tb = barrageQueue[i][n1];
				barrageQueue[i][n1] = barrageQueue[i][n2];
				barrageQueue[i][n2] = tb;
			}
			
			for (int j = 0; j < barrageNumber; j++)
			{
				barrageQueue[i][j].maxRank = (float) (Math.abs(patternRnd.nextInt()) % 70) / 100 + 0.3f;
			}
		}

		sceneCount = 0;
		barrageNum = 0;
		level = startLevel;
		this.levelInc = levelInc;
	}

	/**
	 * Roll the barrage queue after the new barrage pattern is set.
	 * 
	 * @param br
	 */
	private void rollBarragePattern(Barrage[] br)
	{
		int n = (int) ((float) br.length / ((float) (Math.abs(patternRnd.nextInt()) % 32) / 32 + 1));
		if (n == 0)
			return;
		Barrage tbr = br[0];
		for (int i = 0; i < n - 1; i++)
		{
			br[i] = br[i + 1];
		}
		br[n - 1] = tbr;
		br[0].maxRank *= 2;
		while (br[0].maxRank > 1)
			br[0].maxRank -= 0.7f;
	}

	/**
	 * Make the barrage pattern of this scene.
	 * 
	 * @param level
	 * @param bossMode
	 * @param midMode
	 */
	public void setBarrages(float level, boolean bossMode, boolean midMode)
	{
		this.bossMode = bossMode;

		int bpn = 0;
		barrageNum = 0;
		for (int barrageIndex = 0; barrageIndex < BARRAGE_MAX; barrageIndex++)
		{
			if (bossMode)
			{
				if (barrageIndex == 0)
					bpn = 0;
				else
					bpn = 2;
			}
			barrageNum++;
			rollBarragePattern(barrageQueue[bpn]);
			if (level < barrageQueue[bpn][0].maxRank)
			{
				if (level < 0)
					level = 0;
				barrage[barrageIndex] = barrageQueue[bpn][0];
				barrage[barrageIndex].manager.setRank(level);
				break;
			}
			barrage[barrageIndex] = barrageQueue[bpn][0];
			barrage[barrageIndex].manager.setRank(barrageQueue[bpn][0].maxRank);
			if (!bossMode)
			{
				// level /= 2+(barrageQueue[bpn][0].maxRank*2);
				level -= 1 + barrageQueue[bpn][0].maxRank;
			}
			else
			{
				// if ( bn > 0 ) level /= 8+(barrageQueue[bpn][0].maxRank*12);
				if (barrageIndex > 0)
					level -= 4 + (barrageQueue[bpn][0].maxRank * 6);
			}

			if (midMode)
			{
				bpn = BarrageCache.BARRAGE_TYPE_NUM - 1;
			}
			else
			{
				bpn++;
				if (bpn >= BarrageCache.BARRAGE_TYPE_NUM)
					bpn = 0;
			}
		}

		pax[0] = (Math.abs(patternRnd.nextInt()) % (screenWidth * 2 / 3) + (screenWidth / 6)) << 8;
		pay[0] = (Math.abs(patternRnd.nextInt()) % (screenHeight / 6) + (screenHeight / 10)) << 8;

		for (int i = 1; i < QUICK_APP_NUM; i++)
			pax[i] = -1;

		scene++;
		if (!endless)
		{
			sceneStr = (stage + 1) + "-" + scene;
		}
		else
		{
			sceneStr = Integer.toString(scene);
		}
	}

	/**
	 * Process bullet move.
	 * 
	 */
	public void moveBullets()
	{
		for (int i = 0; i < barrageNum; i++)
		{
			barrage[i].manager.setShipPos(ship.xPos, ship.yPos, ship.pxPos, ship.pyPos);
		}
		bulletNum = 0;
		for (int i = 0; i < QUICK_APP_NUM; i++)
		{
			enNum[i] = 0;
		}
		
		for (int i = 0; i < barrageNum; i++)
		{
		    BulletmlManager manager = barrage[i].manager;
		    
			manager.moveBullets();
			
			bulletNum += manager.getBulletsNum();
			
			for (int j = 0; j < QUICK_APP_NUM; j++)
			{
				enNum[j] += manager.getEnemiesNum(j);
			}
		}
		
		// A game speed becomes slow as many bullets appears.
		int itv = INTERVAL_BASE;
		if (bulletNum > PROCESS_SPEED_DOWN_BULLETS_NUM)
		{
			itv += (bulletNum - PROCESS_SPEED_DOWN_BULLETS_NUM) * INTERVAL_BASE / PROCESS_SPEED_DOWN_BULLETS_NUM;
			if (itv > INTERVAL_BASE * 2)
				itv = INTERVAL_BASE * 2;
		}
		player.setInterval(itv);
	}

	/**
	 * Process bullet draw.
	 * 
	 * @param screen
	 */
	public void drawBullets()
	{
		for (int i = 0; i < barrageNum; i++)
		{
			barrage[i].manager.drawBullets();
		}
	}

	/**
	 * 
	 */
	public void clearZakoBullets()
	{
		for (int i = 0; i < barrageNum; i++)
		{
			barrage[i].manager.clearZako(ship);
		}
	}

	/**
	 * 
	 */
	public void clearBullets()
	{
		for (int i = 0; i < barrageNum; i++)
		{
			barrage[i].manager.clear(ship);
		}
	}

	/**
	 * Add enemies.
	 */
	public void addBullets()
	{
		// Scene time control.
		sceneCount--;
		if (sceneDispCnt > 0)
			sceneDispCnt--;
		if (sceneCount < 0)
		{
			if (!endless && scene > 0)
			{
				int ss = ship.getSceneScore();
				int ssof = ship.clearScene(stage, scene - 1);
				if (ssof < 0)
				{
					sceneScoreStr = ss + " " + ssof;
				}
				else
				{
					sceneScoreStr = ss + " +" + ssof;
				}
			}
			else
			{
				sceneScoreStr = "";
			}
			clearBullets();
			if (!endless && scene >= 10)
			{
				sceneStr = "";
				sceneDispCnt = SCENE_DISP_TERM;
				ship.addLeftBonus();
				player.clearStage();
				return;
			}
			if (scene % 10 == 9)
			{
				sceneCount = Integer.MAX_VALUE;
				zakoAppCnt = ZAKO_APP_TERM;
				setBarrages(level, true, false);
				addBossBullet();
			}
			else
			{
				sceneCount = SCENE_TERM;
				if (scene % 10 == 4)
				{
					setBarrages(level, false, true);
				}
				else
				{
					setBarrages(level, false, false);
				}
			}
			level += levelInc;
			sceneDispCnt = SCENE_DISP_TERM;
		}
		if (sceneCount < SCENE_END_TERM)
			return;
		if (bossMode)
		{
			if (bossBullet.x == BulletImpl.NOT_EXIST) // TODO THIS LINE CRASHING WHEN TRYING TO ALTER DIFFICULTY
			{
				sceneCount = 0;
			}
		}

		int x, y;
		for (int i = 0; i < barrageNum; i++)
		{
			if (bossMode)
			{
				if (i > 0)
					break;
				if (zakoAppCnt <= 0)
					break;
				zakoAppCnt--;
			}
			int type = barrage[i].type;
			int frq = appFreq[type];
			// An additional enemy appears when there is no enemy of the same
			// type.
			if (type < QUICK_APP_NUM && enNum[type] == 0 && pax[type] != -1)
			{
				x = pax[type];
				y = pay[type];
				barrage[i].manager.addTopBullet(x, y, 180, shield[type], type);
				enNum[type]++;
				break;
			}

			if ((Math.abs(patternRnd.nextInt()) % frq) == 0)
			{
				x = Math.abs(patternRnd.nextInt()) % (BulletmlManager.screenWidth * 2 / 3)
						+ (BulletmlManager.screenWidth / 6);
				y = Math.abs(patternRnd.nextInt()) % (BulletmlManager.screenHeight / 6)
						+ (BulletmlManager.screenHeight / 10);
				if (type < QUICK_APP_NUM)
				{
					pax[type] = x;
					pay[type] = y;
				}
				barrage[i].manager.addTopBullet(x, y, 180, shield[type], type);
			}
		}
	}

	public static final int BOSS_TYPE = 3;
	private static final int BOSS_SHIELD = 24;
	private BulletImpl bossBullet;

	/**
	 * Add boss to the scene.
	 *  
	 */
	public void addBossBullet()
	{
		bossBullet = null;
		for (int i = 0; i < barrageNum; i++)
		{
		    Barrage currBarrage = barrage[i]; 
		    if (currBarrage!=null)
		    {
    			if (currBarrage.type != BarrageCache.BARRAGE_TYPE_BOSS)
    				continue;
    			
    			BulletImpl bl = currBarrage.manager.addTopBullet(BulletmlManager.screenWidth / 2, BulletmlManager.screenHeight / 5, 180, BOSS_SHIELD, BOSS_TYPE);
    
    			if (bossBullet == null)
    			{
    				bossBullet = bl;
    			}
    			else
    			{
    				bl.parent = bossBullet;
    			}
		    }
		}
	}

	public void setShip(Ship ship)
	{
		this.ship = ship;
	}

	/**
	 * Wipe bullets around the destroyed enemy.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 */
	public void wipeBullets(int x, int y, int width)
	{
		for (int i = 0; i < barrageNum; i++)
		{
			barrage[i].manager.wipeBullets(ship, x, y, width);
		}
	}

	/**
	 * Draw the scene indicator.
	 * 
	 */
	public void drawScene()
	{
		if (sceneDispCnt <= 0)
			return;
		
		LetterRender.drawString(sceneStr, 0, 24, 7, 0xffffdddd);
		LetterRender.drawString(sceneScoreStr, 0, 45, 5, 0xffddffdd);
	}
}
