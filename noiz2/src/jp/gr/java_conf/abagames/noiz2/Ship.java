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
 * $Id: Ship.java,v 1.3 2002/10/01 09:41:04 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;


import java.util.Random;

import jp.gr.java_conf.abagames.bulletml.BulletImpl;
import jp.gr.java_conf.abagames.bulletml.BulletmlPlayer;

import com.netthreads.android.noiz2.R;
import com.netthreads.android.noiz2.data.StateData;
import com.netthreads.android.noiz2.sound.SoundPoolPlayer;

/**
 * Handle my ship.
 * 
 * @version $Revision: 1.3 $
 */
public class Ship
{
    public static final int SHIP_SIZE = 96;
    
	public int xPos, yPos, pxPos, pyPos;
	private int cnt;

	/*
	 * Horming lasers data.
	 */
	private static final int HORMING_LASER_NUM = 8;
	private HomingLaser[] hl = new HomingLaser[HORMING_LASER_NUM];
	private int hlIdx = HORMING_LASER_NUM, hlCnt = 0;

	/*
	 * Bonus items data.
	 */
	private static final int BONUS_NUM = 256;
	private Bonus[] bonus = new Bonus[BONUS_NUM];
	private int bnIdx = BONUS_NUM;

	/*
	 * Sparks data.
	 */
	private static final int SPARK_NUM = 64;
	private Spark[] spark = new Spark[SPARK_NUM];
	private int spIdx = SPARK_NUM;

	private BarrageManager manager;
	private BulletmlPlayer player;
	private PrefManager prMng;

	private int score, bonusScore, extendScore, sceneScore;
	private static final int BONUS_SCORE_INIT = 10;
	private static final int EXTEND_EVERY = 100000;
	private int left, invCnt, extCnt;
	private static final int INVINCIBLE_TIME = 180;

    private int screenHeight = 0;
	private int screenWidth = 0;

	private StateData stateData = null;

    private SoundPoolPlayer soundPoolPlayer = null;
	
	/**
	 * Construct ship elements.
	 * 
	 * @param manager
	 * @param player
	 * @param prMng
	 * @param stateData
	 */
	public Ship(BarrageManager manager, BulletmlPlayer player, PrefManager prMng, StateData stateData)
	{
		this.manager = manager;
		this.player = player;
		this.prMng = prMng;
		this.stateData = stateData;
		
    	this.screenWidth = stateData.viewWidth;
    	this.screenHeight = stateData.viewHeight;
		
		for (int i = 0; i < HORMING_LASER_NUM; i++)
		{
			hl[i] = new HomingLaser(player, this);
		}
		for (int i = 0; i < BONUS_NUM; i++)
		{
			bonus[i] = new Bonus(player, this);
		}
		for (int i = 0; i < SPARK_NUM; i++)
		{
			spark[i] = new Spark(player);
		}

		// Create instance of sound player
		soundPoolPlayer = SoundPoolPlayer.instance();
	}

	/**
	 * Initialise elements.
	 * 
	 */
	public void init()
	{
		for (int i = 0; i < HORMING_LASER_NUM; i++)
		{
			hl[i].x = HomingLaser.NOT_EXIST;
		}
		for (int i = 0; i < BONUS_NUM; i++)
		{
			bonus[i].x = Bonus.NOT_EXIST;
		}
		// Bonus.setInhaleMag(1.0f);
		for (int i = 0; i < SPARK_NUM; i++)
		{
			spark[i].x = Spark.NOT_EXIST;
		}
		
		pxPos = xPos = (screenWidth / 2) << 8;
		pyPos = yPos = (screenHeight / 4 * 3) << 8;
		
		cnt = 0;
		hlCnt = 0;
		score = 0;
		bonusScore = BONUS_SCORE_INIT;
		left = 2;
		invCnt = INVINCIBLE_TIME;
		extendScore = EXTEND_EVERY;
		extCnt = 0;
		sceneScore = 0;
	}

	/**
	 * Move spark.
	 * 
	 */
	public final void moveSpark()
	{
		for (int i = 0; i < SPARK_NUM; i++)
		{
			if (spark[i].x != Spark.NOT_EXIST)
			{
				spark[i].move();
			}
		}
	}

	/**
	 * Move screen elements.
	 * 
	 */
	public final void move()
	{
        pxPos = xPos;
		pyPos = yPos;

		
		// WTF!!
		xPos = (int)stateData.currentX<<8;
		yPos = (int)stateData.currentY<<8;
		
		// WTF!!
		if (pxPos == xPos && pyPos == yPos)
		{
			pyPos = yPos + 1;
		}

		for (int i = 0; i < HORMING_LASER_NUM; i++)
		{
			if (hl[i].x != HomingLaser.NOT_EXIST)
			{
				hl[i].move();
			}
		}
		for (int i = 0; i < BONUS_NUM; i++)
		{
			if (bonus[i].x != Bonus.NOT_EXIST)
			{
				bonus[i].move();
			}
		}
		moveSpark();

		if (score >= extendScore)
		{
			if (left < 9)
			{
				left++;
				extCnt = 96;
			}
			extendScore += EXTEND_EVERY;
		}

		if (hlCnt > 0)
			hlCnt--;
		if (invCnt > 0)
			invCnt--;
		if (extCnt > 0)
			extCnt--;
		cnt++;
	}

	/**
	 * Draw spark.
	 * 
	 * @param screen
	 */
	public final void drawSpark()
	{
		for (int i = 0; i < SPARK_NUM; i++)
		{
			if (spark[i].x != Spark.NOT_EXIST)
			{
				spark[i].draw();
			}
		}
	}

	/**
	 * Draw ship elements.
	 * 
	 * @param screen
	 */
	public final void draw()
	{
		drawSpark();
		
		for (int i = 0; i < HORMING_LASER_NUM; i++)
		{
			if (hl[i].x != HomingLaser.NOT_EXIST)
			{
				hl[i].draw();
			}
		}
		for (int i = 0; i < BONUS_NUM; i++)
		{
			if (bonus[i].x != Bonus.NOT_EXIST)
			{
				bonus[i].draw();
			}
		}
		
		player.drawShip(xPos, yPos, pxPos, pyPos, cnt);
	}

	/**
	 * Draw the score and the number of my ships.
	 * 
	 */
	public final void drawScore()
	{
		LetterRender.drawStringFromRight(Integer.toString(score), screenWidth / 3 * 2, 4, 7, 0xffaaffee);
		LetterRender.drawStringFromRight(Integer.toString(bonusScore), screenWidth, 4, 5, 0xffeeffaa);
		
		if (invCnt > 0 || extCnt > 0)
		{
			// LetterRender.drawStringFromRight("LEFT-" +
			// Integer.toString(left),
			// screenWidth, screenHeight-28, 8, 0xffeeaaff);
			LetterRender.drawStringFromRight("LEFT-" + Integer.toString(left), screenWidth, 24, 8, 0xffeeaaff);
		}
	}

	/**
	 * My ship was destroyed.
	 * 
	 */
	public void hit()
	{
	    // **SOUND**
	    soundPoolPlayer.play(R.raw.ship_explosion1);
	    
		if (invCnt > 0 || left < 0)
			return;
		left--;
		invCnt = INVINCIBLE_TIME;
		bonusScore = BONUS_SCORE_INIT;
		if (left < 0)
		{
			player.gameover();
			invCnt = extCnt = 0;
		} else
		{
			manager.clearZakoBullets();
		}
		for (int i = 0; i < 64; i++)
		{
			addSpark(xPos, yPos, rnd.nextInt() % 2048, rnd.nextInt() % 2048);
		}
	}

	/**
	 * A homing laser locks the enemy.
	 * 
	 */
	public void lock(BulletImpl bl)
	{
		if (hlCnt > 0)
			return;
		hlIdx--;
		if (hlIdx < 0)
			hlIdx = HORMING_LASER_NUM - 1;
		hl[hlIdx].set(bl, xPos, yPos);
		bl.locked--;
		hlCnt = 12;
	}

	private Random rnd = new Random();

	/**
	 * Bullets change to bonus items.
	 * 
	 * @param x
	 * @param y
	 * @param mx
	 * @param my
	 */
	public void addBonusWiped(int x, int y, int mx, int my)
	{
		bnIdx--;
		if (bnIdx < 0)
			bnIdx = BONUS_NUM - 1;
		bonus[bnIdx].set(x, y, mx, my);
	}

	/**
	 * Add spark.
	 * 
	 * @param x
	 * @param y
	 * @param mx
	 * @param my
	 */
	public final void addSpark(int x, int y, int mx, int my)
	{
		spIdx--;
		if (spIdx < 0)
			spIdx = SPARK_NUM - 1;
		spark[spIdx].set(x, y, mx, my);
	}

	/**
	 * Clear stage screen.
	 * 
	 * @param stage
	 * @param scene
	 * 
	 * @return score
	 */
	public int clearScene(int stage, int scene)
	{
		int ssof = prMng.clearScene(sceneScore, stage, scene);
		sceneScore = 0;
		return ssof;
	}

	/**
	 * Update score.
	 * 
	 * @param sc
	 */
	private void addScore(int sc)
	{
		score += sc;
		sceneScore += sc;
	}

	private static final int BULLET_WIPE_WIDTH = 5120;
	private static final int enemyScore[] = { 100, 300, 1000, 10000 };

	/**
	 * Enemy destroy.
	 * 
	 * @param x
	 * @param y
	 * @param type
	 */
	public void destroyEnemy(int x, int y, int type)
	{
		addScore(enemyScore[type]);
		
		manager.wipeBullets(x, y, (type + 1) * BULLET_WIPE_WIDTH);
	}

	/**
	 * A homing laser hits a enemy.
	 * 
	 * @param x
	 * @param y
	 * @param hmx
	 * @param hmy
	 */
	public void hitLaser(int x, int y, int hmx, int hmy)
	{
	    // **SOUND**
        soundPoolPlayer.play(R.raw.ship_explosion2);
        
	    int rndIndex = Math.abs(rnd.nextInt()) % 8 + 4;
	    
		for (int i = 0; i < rndIndex; i++)
		{
			addSpark(x, y, (int) (hmx * (1.0f + (float) (rnd.nextInt() % 16) / 64)), (int) (hmy * (0.2f + (float) (rnd.nextInt() % 8) / 64)));
		}
	}

	/**
	 * Collect bonus.
	 * 
	 */
	public final void getBonus()
	{
		addScore(bonusScore);
		if (bonusScore < 1000)
			bonusScore += 10;
	}

	/**
	 * A bonus item passes away.
	 * 
	 */
	public final void missBonus()
	{
		bonusScore /= 10;
		bonusScore >>= 1;
		bonusScore *= 10;
		if (bonusScore < 10)
			bonusScore = 10;
	}

	/**
	 * Total bonuses.
	 * 
	 */
	public void addLeftBonus()
	{
		addScore(left * 10000);
		left = 99;
		invCnt = extCnt = 0;
	}

	/**
	 * Return total score.
	 * 
	 * @return score.
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * Return scene score.
	 * 
	 * @return The scene score.
	 */
	public int getSceneScore()
	{
		return sceneScore;
	}
}
