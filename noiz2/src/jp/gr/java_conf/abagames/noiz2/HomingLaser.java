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
 * $Id: HormingLaser.java,v 1.2 2002/10/04 14:05:12 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;

import jp.gr.java_conf.abagames.bulletml.*;

import java.util.Random;

/**
 * Handle a homing laser.
 * 
 * @version $Revision: 1.2 $
 */
public class HomingLaser
{
	public static final int NOT_EXIST = Integer.MIN_VALUE;

	public int x, y;
	private int px, py, mx, my, mpx, mpy, cnt;
	private BulletImpl target;

	private BulletmlPlayer player;
	private Ship ship;

	public HomingLaser(BulletmlPlayer player, Ship ship)
	{
		this.player = player;
		this.ship = ship;
		x = NOT_EXIST;
	}

	private static Random rnd = new Random();

	public void set(BulletImpl target, int x, int y)
	{
		this.target = target;
		this.x = px = x;
		this.y = py = y;
		mx = mpx = (rnd.nextInt() % 512);
		my = mpy = 1024;
		cnt = 0;
	}

	public final void move()
	{
		if (target.x == NOT_EXIST)
		{
			x = NOT_EXIST;
			return;
		}
		int c;
		if (cnt < 8)
			c = 8 - cnt;
		else
			c = 0;
		mx += (target.x - x) >> (7 + c);
		my += (target.y - y) >> (7 + c);
		mpx += (target.x - x) >> 9;
		mpy += (target.y - y) >> 9;
		x += mx;
		y += my;
		px += mpx;
		py += mpy;
		if (y < target.y)
		{
			ship.hitLaser(target.x, target.y, mx, my);
			x = NOT_EXIST;
			if (target.hit())
			{
				ship.destroyEnemy(target.px, target.py, target.type);
			}
			return;
		}
		cnt++;
	}

	public final void draw()
	{
		player.drawHormingLaser(x, y, px, py, cnt);
	}
}