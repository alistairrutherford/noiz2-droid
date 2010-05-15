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
 * $Id: Spark.java,v 1.2 2002/10/04 14:05:13 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;

import jp.gr.java_conf.abagames.bulletml.*;

import java.util.Random;

/**
 * Handle a spark.
 * 
 * @version $Revision: 1.2 $
 */
public class Spark
{
	public static final int NOT_EXIST = Integer.MIN_VALUE;

	public int x;
	private int y, mx, my, cnt;
	private BulletmlPlayer player;

	public Spark(BulletmlPlayer player)
	{
		this.player = player;
		x = NOT_EXIST;
	}

	private static Random rnd = new Random();

	public void set(int x, int y, int mx, int my)
	{
		this.x = x;
		this.y = y;
		this.mx = mx;
		this.my = my;
		cnt = Math.abs(rnd.nextInt()) % 24 + 16;
	}

	public final void move()
	{
		x += mx;
		y += my;
		mx -= mx >> 5;
		my -= my >> 5;
		cnt--;
		if (cnt < 0)
			x = NOT_EXIST;
	}

	public final void draw()
	{
		player.drawSpark(x, y);
	}
}
