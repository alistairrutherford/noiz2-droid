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
 * $Id: BulletmlManager.java,v 1.3 2002/10/01 09:41:02 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.bulletml;


import com.netthreads.android.noiz2.R;
import com.netthreads.android.noiz2.sound.SoundPoolPlayer;

import jp.gr.java_conf.abagames.noiz2.*;

/**
 * Handle bulletimpls and actionimpls.
 * 
 * @version $Revision: 1.3 $
 */
public class BulletmlManager
{
    private static final int CLS_WIDTH = 100;
    private static final int LOCK_WIDTH = 12 << 8;
    private static final int ENEMY_TYPE_NUM = 4;
    
	public static int screenWidth, screenHeight;

	private BulletmlPlayer player;

    private static final int BULLET_MAX = 128;
	private BulletImpl[] bullet = new BulletImpl[BULLET_MAX];
	private int bltIdx = 0;

	private static final int ACTION_MAX = 512;
	private ActionImpl[] action = new ActionImpl[ACTION_MAX];
	private int actIdx = 0;

	public int shipX, shipY, shipPx, shipPy;

	public int cnt;

	public BulletmlUtil bulletmlUtil = new BulletmlUtil();

	// Sound.
	private SoundPoolPlayer soundPoolPlayer = null;
	
	public BulletmlManager(int screenWidth, int screenHeight)
	{
	    soundPoolPlayer = SoundPoolPlayer.instance();
	    
		BulletmlManager.screenWidth = screenWidth << 8;
		BulletmlManager.screenHeight = screenHeight << 8;
	}

	public void reinit()
	{
	    int bulletLength = bullet.length;
	    
		for (int i = 0; i < bulletLength; i++)
		{
			bullet[i].vanish();
		}
		
		int actionLength = action.length;
		
		for (int i = 0; i < actionLength; i++)
		{
			action[i].vanish();
		}
		cnt = 0;
	}

	/**
	 * Initialise manager.
	 * 
	 */
	public void init()
	{
        int bulletLength = bullet.length;
        
        for (int i = 0; i < bulletLength; i++)
		{
			bullet[i] = new BulletImpl(this);
		}

        int actionLength = action.length;
        
        for (int i = 0; i < actionLength; i++)
		{
			action[i] = new ActionImpl(this);
		}
        
		reinit();
	}

	/**
	 * Assign player.
	 * 
	 * @param player
	 */
	public void setPlayer(BulletmlPlayer player)
	{
		this.player = player;
	}

	/**
	 * Assign ship position.
	 * 
	 * @param x
	 * @param y
	 * @param px
	 * @param py
	 */
	public void setShipPos(int x, int y, int px, int py)
	{
		shipX = x;
		shipY = y;
		shipPx = px;
		shipPy = py;
	}

	public BulletImpl getBulletImplInstance()
	{
		for (int i = BULLET_MAX - 1; i >= 0; i--)
		{
			bltIdx++;
			bltIdx &= (BULLET_MAX - 1);
			if (bullet[bltIdx].x == BulletImpl.NOT_EXIST)
			{
				return bullet[bltIdx];
			}
		}
		return null;
	}

	/**
	 * Return instance.
	 * 
	 * @return The instance.
	 */
	public ActionImpl getActionImplInstance()
	{
		for (int i = ACTION_MAX - 1; i >= 0; i--)
		{
			actIdx++;
			actIdx &= (ACTION_MAX - 1);
			if (action[actIdx].pc == ActionImpl.NOT_EXIST)
			{
				return action[actIdx];
			}
		}
		return null;
	}

	private static int bulletNum;
	private int[] enemyNum = new int[ENEMY_TYPE_NUM];

	/**
	 * Animate the bullets.
	 * 
	 */
	public void moveBullets()
	{
		long a, b, c, d, e, f, dnm, x, y;
		int a1x, a2x, a1y, a2y;
		int b1x, b2x, b1y, b2y;

		if (shipX < shipPx)
		{
			a1x = shipX - CLS_WIDTH;
			a2x = shipPx + CLS_WIDTH;
		}
		else
		{
			a1x = shipPx - CLS_WIDTH;
			a2x = shipX + CLS_WIDTH;
		}
		if (shipY < shipPy)
		{
			a1y = shipY - CLS_WIDTH;
			a2y = shipPy + CLS_WIDTH;
		}
		else
		{
			a1y = shipPy - CLS_WIDTH;
			a2y = shipY + CLS_WIDTH;
		}

		bulletNum = 0;
		for (int i = 0; i < ENEMY_TYPE_NUM; i++)
		{
			enemyNum[i] = 0;
		}
		
		for (int i = BULLET_MAX - 1; i >= 0; i--)
		{
		    BulletImpl bl = bullet[i];
		    
			if (bl.x != BulletImpl.NOT_EXIST)
			{
				bl.move();

				// Check if the bullet hits my ship.
				if (bl.py < bl.y)
				{
					b1y = bl.py - CLS_WIDTH;
					b2y = bl.y + CLS_WIDTH;
				}
				else
				{
					b1y = bl.y - CLS_WIDTH;
					b2y = bl.py + CLS_WIDTH;
				}
				if (a2y >= b1y && b2y >= a1y)
				{
					if (bl.px < bl.x)
					{
						b1x = bl.px - CLS_WIDTH;
						b2x = bl.x + CLS_WIDTH;
					}
					else
					{
						b1x = bl.x - CLS_WIDTH;
						b2x = bl.px + CLS_WIDTH;
					}
					if (a2x >= b1x && b2x >= a1x)
					{
						a = shipY - shipPy;
						b = shipPx - shipX;
						c = shipPx * shipY - shipPy * shipX;
						d = bl.py - bl.y;
						e = bl.x - bl.px;
						f = bl.x * bl.py - bl.y * bl.px;
						dnm = b * d - a * e;
						if (dnm != 0)
						{
							x = (b * f - c * e) / dnm;
							y = (c * d - a * f) / dnm;
							if (a1x <= x && x <= a2x && a1y <= y && y <= a2y && b1x <= x && x <= b2x && b1y <= y
									&& y <= b2y)
							{
								player.hitShip();
							}
						}
					}
				}
				// Check if the enemy is locked by a homing laser.
				if (bl.locked > 0 && Math.abs(shipX - bl.x) < LOCK_WIDTH && bl.y < shipY)
				{
					player.lockShip(bl);
				}

				bulletNum++;
				if (bl.parent == null)
				{
					enemyNum[bl.type]++;
				}
			}
		}
		
		cnt++;
	}

	public void drawBullets()
	{
		for (int i = BULLET_MAX - 1; i >= 0; i--)
		{
            BulletImpl bl = bullet[i];
            
			if (bl.x != BulletImpl.NOT_EXIST)
			{
			    bl.draw();
			}
		}
	}

	public final void drawEnemy(int x, int y, int cnt, int shield, int type)
	{
		player.drawEnemy(x, y, cnt, shield, type);
	}

	public final void drawBullet(int x, int y, int px, int py, int sx, int sy, int cl, int cnt)
	{
		player.drawBullet(x, y, px, py, sx, sy, cl, cnt);
	}

	private IChoice[] topActions;

	public void setTopActions(IChoice[] actions)
	{
		topActions = actions;
	}

	/**
	 * 
	 * BARRAGE_TYPE_ZAKO = 0;
	 * BARRAGE_TYPE_MIDDLE = 1;
	 * BARRAGE_TYPE_BOSS = 2;
	 *     
	 * @param x
	 * @param y
	 * @param d
	 * @param shield
	 * @param type
	 * @return
	 */
	public BulletImpl addTopBullet(int x, int y, int d, int shield, int type)
	{
	    switch (type)
	    {
	        case 0:
	            soundPoolPlayer.play(R.raw.zap1);
	            break;
            case 1:
                soundPoolPlayer.play(R.raw.zap1);
                break;
            case 2:
                soundPoolPlayer.play(R.raw.zap1);
                break;
            default:
                break;
	    }
	    
		BulletImpl topBullet = getBulletImplInstance();
		if (topBullet != null)
		{
			topBullet.set(topActions, x, y, d, 180, shield, type);
		}
		return topBullet;
	}

	/**
	 * Set barrage rank 0..1
	 *  
	 * @param rank
	 */
	public void setRank(float rank)
	{
		bulletmlUtil.setRank(rank);
	}

	public boolean isFinished()
	{
		return (cnt > 1 && bulletNum <= 0);
	}

	public int getBulletsNum()
	{
		return bulletNum;
	}

	public int getEnemiesNum(int type)
	{
		return enemyNum[type];
	}

	public void wipeBullets(Ship ship, int x, int y, int width)
	{
		for (int i = BULLET_MAX - 1; i >= 0; i--)
		{
            BulletImpl bl = bullet[i];
            
			if (bl.x != BulletImpl.NOT_EXIST && bl.parent != null)
			{
				if (Math.abs(bl.x - x) + Math.abs(bl.y - y) < width)
				{
					ship.addBonusWiped(bl.x, bl.y, (bl.x - bl.px) >> 2, (bl.y - bl.py) >> 2);
					
					bl.x = BulletImpl.NOT_EXIST;
				}
			}
		}
	}

	public void clearZako(Ship ship)
	{
		for (int i = BULLET_MAX - 1; i >= 0; i--)
		{
		    BulletImpl bl = bullet[i];
		    
			if (bl.x == BulletImpl.NOT_EXIST)
				continue;
			
			if (bl.type == BarrageManager.BOSS_TYPE)
				continue;
			
			ship.addSpark(bl.x, bl.y, (bl.x - bl.px) >> 2, (bl.y - bl.py) >> 2);
			bl.vanishForced();
		}
	}

	public void clear(Ship ship)
	{
		for (int i = BULLET_MAX - 1; i >= 0; i--)
		{
            BulletImpl bl = bullet[i];
            
			if (bl.x == BulletImpl.NOT_EXIST)
				continue;
			
			ship.addSpark(bl.x, bl.y, (bl.x - bl.px) >> 2, (bl.y - bl.py) >> 2);
			bl.vanishForced();
		}
	}
}
