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
 * $Id: Bonus.java,v 1.2 2002/10/01 09:41:03 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;

import jp.gr.java_conf.abagames.bulletml.*;

/**
 * Bonus item.
 * 
 * @version $Revision: 1.2 $
 */
public class Bonus
{
	public static final int NOT_EXIST = Integer.MIN_VALUE;

	private static final int INHALE_WIDTH = 10240;
	private static final int SHIP_WIDTH = 4096;
	private static final int SPEED = 172;

	/*
	 * private static int inhaleWidth, shipWidth;
	 * 
	 * public static void setInhaleMag(float mag) { inhaleWidth =
	 * (int)(mag*INHALE_WIDTH); shipWidth = (int)(mag*SHIP_WIDTH); }
	 */

	private static final int inhaleWidth, shipWidth;

	static
	{
		inhaleWidth = INHALE_WIDTH;
		shipWidth = SHIP_WIDTH;
	}

	public int x;
	private int y, mx, my, cnt;
	private boolean down;
	private BulletmlPlayer player;
	private Ship ship;

	public Bonus(BulletmlPlayer player, Ship ship)
	{
		this.player = player;
		this.ship = ship;
		x = NOT_EXIST;
	}

	public void set(int x, int y, int mx, int my)
	{
		this.x = x;
		this.y = y;
		this.mx = mx;
		this.my = my;
		cnt = 0;
		down = true;
	}

	public final void move()
	{
		x += mx;
		mx -= mx >> 5;
		if (x <= BulletmlManager.screenWidth / 6)
		{
			x = BulletmlManager.screenWidth / 6;
			if (mx < 0)
				mx = -mx;
		} else if (x > BulletmlManager.screenWidth / 6 * 5)
		{
			x = BulletmlManager.screenWidth / 6 * 5;
			if (mx > 0)
				mx = -mx;
		}
		y += my;
		if (down)
		{
			my += (SPEED - my) >> 5;
			if (y >= BulletmlManager.screenHeight)
			{
				down = false;
				my = -my;
			}
		} else
		{
			my += (-SPEED - my) >> 5;
			if (y <= 0)
			{
				ship.missBonus();
				x = NOT_EXIST;
				return;
			}
		}

		int d = Math.abs(ship.xPos - x) + Math.abs(ship.yPos - y);
		if (d < shipWidth)
		{
			ship.getBonus();
			x = NOT_EXIST;
			return;
		} else if (d < inhaleWidth)
		{
			mx += (long) (ship.xPos - x) * (inhaleWidth - d) >> 18;
			my += (long) (ship.yPos - y) * (inhaleWidth - d) >> 18;
		}
		cnt++;
	}

	public final void draw()
	{
		player.drawBonus(x, y, cnt);
	}
}
