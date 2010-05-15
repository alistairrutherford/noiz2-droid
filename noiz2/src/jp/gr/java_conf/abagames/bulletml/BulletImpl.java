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
 * $Id: BulletImpl.java,v 1.2 2002/10/01 09:41:02 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.bulletml;

import jp.gr.java_conf.abagames.util.*;
import jp.gr.java_conf.abagames.noiz2.BarrageManager;

/**
 * Bullet implementation.
 * 
 * @version $Revision: 1.2 $
 */
public class BulletImpl
{
	public static final int NOT_EXIST = Integer.MIN_VALUE;

	private static final int ACTION_MAX = 8;

	private ActionImpl[] action = new ActionImpl[ACTION_MAX];
	private int acIdx;

	public int x, y, px, py;
	public int sx, sy;

	public Direction drcElm;
	public Speed spdElm;
	public float direction, speed;

	public float mx, my;

	public int clr;

	private int cnt;

	private BulletmlManager manager;

	private float[] prms;

	//private int tmx, tmy;
	public BulletImpl parent;

	public int shield, locked;
	public int type;

	public BulletImpl(BulletmlManager bm)
	{
		manager = bm;
		x = NOT_EXIST;
	}

	public void changeAction(ActionImpl bfr, ActionImpl aft)
	{
		for (int i = 0; i < acIdx; i++)
		{
			if (action[i].equals(bfr))
			{
				action[i] = aft;
				return;
			}
		}
	}

	public void set(IChoice[] aec, int x, int y, int d, int clr, int shield, int type)
	{
		this.x = px = sx = x;
		this.y = py = sy = y;
		mx = my = 0;
		//tmx = tmy = 0;
		this.clr = clr;
		cnt = 0;
		acIdx = 0;
		
		int length = aec.length;
		
		for (int i = 0; i < length; i++)
		{
			action[acIdx] = manager.getActionImplInstance();
			if (action[acIdx] == null)
				break;
			action[acIdx].set(manager.bulletmlUtil.getActionElm(aec[i]), this);
			float[] actPrms = manager.bulletmlUtil.getActionParams(aec[i], prms);
			if (actPrms == null)
			{
				action[acIdx].setParams(prms);
			}
			else
			{
				action[acIdx].setParams(actPrms);
			}
			acIdx++;
			if (acIdx >= ACTION_MAX)
				break;
		}
		direction = d;
		speed = 0;
		parent = null;
		this.shield = locked = shield;
		this.type = type;
	}

	/**
	 * Initialise bullet.
	 * 
	 * @param bullet
	 * @param x
	 * @param y
	 * @param ci
	 * @param bImpl Parent of bullet. If null then it is enemy.
	 */
	public void set(Bullet bullet, int x, int y, int ci, BulletImpl bImpl)
	{
		drcElm = bullet.getDirection();
		spdElm = bullet.getSpeed();
		IChoice[] aec = bullet.getActionElm();
		set(aec, x, y, 0, ci, 0, 0);
		parent = bImpl;
	}

	public void setParams(float[] prms)
	{
		this.prms = prms;
	}

	public float getAimDeg()
	{
		return (float) DegUtil.getDeg(manager.shipX - x, manager.shipY - y) * 360 / SCTable.TABLE_SIZE;
	}

	public void vanish()
	{
		if (type == BarrageManager.BOSS_TYPE)
		{
			for (int i = 0; i < acIdx; i++)
			{
				action[i].rewind();
			}
			return;
		}

		vanishForced();
	}

	public void vanishForced()
	{
		for (int i = 0; i < acIdx; i++)
		{
			action[i].vanish();
		}
		x = NOT_EXIST;
	}

	public boolean isAllActionFinished()
	{
		for (int i = 0; i < acIdx; i++)
		{
			if (action[i].pc != ActionImpl.NOT_EXIST)
			{
				return false;
			}
		}
		return true;
	}

	public final void move()
	{
		for (int i = 0; i < acIdx; i++)
		{
			action[i].move();
		}

		cnt++;

		int d = (int) (direction * SCTable.TABLE_SIZE / 360);
		d &= (SCTable.TABLE_SIZE - 1);

		int mvx = ((int) (speed * SCTable.sintbl[d]) << 1) + (int) (mx * 512);
		int mvy = ((int) (-speed * SCTable.costbl[d]) << 1) + (int) (my * 512);
		int pmvx = mvx, pmvy = mvy;
		x += mvx;
		//tmx = mvx;
		y += mvy;
		//tmy = mvy;
		if (pmvx == 0 && pmvy == 0)
		{
			pmvx = SCTable.sintbl[d] << 0;
			pmvy = -SCTable.costbl[d] << 0;
		}
		if (cnt < 4)
		{
			px = x - pmvx;
			py = y - pmvy;
		}
		else
			if (cnt < 8)
			{
				px = x - (pmvx << 1);
				py = y - (pmvy << 1);
			}
			else
			{
				px = x - (pmvx << 2);
				py = y - (pmvy << 2);
			}

		if (px < 0 || px >= BulletmlManager.screenWidth || py < 0 || py >= BulletmlManager.screenHeight)
		{
			vanish();
		}
	}

	public final void draw()
	{
		if (parent == null)
		{
			manager.drawEnemy(x, y, cnt, shield, type);
		}
		else
		{
			manager.drawBullet(x, y, px, py, sx, sy, clr, cnt);
		}
	}

	public boolean hit()
	{
		shield--;
		if (shield <= 0)
		{
			vanishForced();
			return true;
		}
		return false;
	}
}
