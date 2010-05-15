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
 * $Id: LetterRender.java,v 1.2 2002/10/04 14:05:12 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;

import jp.gr.java_conf.abagames.util.SCTable;

import com.netthreads.android.noiz2.graphics.IScreen;

/**
 * Render letters.
 * 
 * @version $Revision: 1.2 $
 */
public class LetterRender
{
	private static IScreen screen = null;

	public static void setScreen(IScreen screen)
	{
	    LetterRender.screen = screen;
	}

	private static int[][] vtxs = new int[6][2];
	private static float[][] indices = new float[6][2];

	private static void drawLetter(int idx, int lx, int ly, int ltSize, int color)
	{
	    int spDataLength = spData[idx].length;
	    
		for (int i = 0; i < spDataLength; i++)
		{
			float x = spData[idx][i][0];
			float y = -spData[idx][i][1];
			float length = spData[idx][i][2] / 2;
			float size = spData[idx][i][3] / 2;
			
			int deg = (int) spData[idx][i][4] * SCTable.TABLE_SIZE / 360;
			
			indices[0][0] = -length - size;
			indices[1][0] = -length;
			indices[1][1] = size;
			indices[2][0] = length;
			indices[2][1] = size;
			indices[3][0] = length + size;
			indices[4][0] = length;
			indices[4][1] = -size;
			indices[5][0] = -length;
			indices[5][1] = -size;
			for (int j = 0; j < 6; j++)
			{
				float rx = ((indices[j][0] * SCTable.costbl[deg] - indices[j][1] * SCTable.sintbl[deg]) / 256) + x, ry = ((indices[j][0]
						* SCTable.sintbl[deg] + indices[j][1] * SCTable.costbl[deg]) / 256)
						+ y;
				vtxs[j][0] = (int) (rx * ltSize + lx);
				vtxs[j][1] = (int) (ry * ltSize + ly);
			}

			for (int j = 0; j < 5; j++)
			{
				screen.drawLine(vtxs[j][0], vtxs[j][1], vtxs[j + 1][0], vtxs[j + 1][1], color);
			}
		}
	}

	public static void drawString(String str, int lx, int ly, int ltSize, int color)
	{
		int x = lx + ltSize, y = (int) (ly + ltSize * 1.25f);
		
		int strLength = str.length();
		
		for (int i = 0; i < strLength; i++)
		{
			int c = (int) str.charAt(i), idx;
			if (c != ' ')
			{
				if (c >= '0' && c <= '9')
				{
					idx = c - '0';
				}
				else
					if (c >= 'A' && c <= 'Z')
					{
						idx = c - 'A' + 10;
					}
					else
						if (c >= 'a' && c <= 'z')
						{
							idx = c - 'a' + 10;
						}
						else
							if (c == '.')
							{
								idx = 36;
							}
							else
								if (c == '-')
								{
									idx = 38;
								}
								else
									if (c == '+')
									{
										idx = 39;
									}
									else
									{
										idx = 37;
									}
				drawLetter(idx, x, y, ltSize, color);
			}
			x += ltSize * 2;
		}
	}

	public static void drawStringFromRight(String str, int lx, int ly, int ltSize, int color)
	{
		drawString(str, lx - ltSize * (str.length() * 2 + 2), ly, ltSize, color);
	}

	private static final float[][][] spData = {
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.6f, 0.55f, 0.65f, 0.3f, 90 }, { 0.6f, 0.55f, 0.65f, 0.3f, 90 },
					{ -0.6f, -0.55f, 0.65f, 0.3f, 90 }, { 0.6f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { 0, 0.55f, 0.65f, 0.3f, 90 }, { 0, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ 0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ 0.65f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ 0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ 0, 0, 0.65f, 0.3f, 0 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ 0, 0, 0.65f, 0.3f, 0 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{
					// A
					{ 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ 0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0.65f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { -0.1f, 1.15f, 0.45f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.45f, 0.55f, 0.65f, 0.3f, 90 },
					{ -0.1f, 0, 0.45f, 0.3f, 0 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { -0.1f, 1.15f, 0.45f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.45f, 0.4f, 0.65f, 0.3f, 90 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{// F
			{ 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.25f, 0, 0.25f, 0.3f, 0 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 0.55f, 0.65f, 0.3f, 90 }, { 0, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0.65f, -0.75f, 0.25f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{// K
			{ -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.45f, 0.55f, 0.65f, 0.3f, 90 }, { -0.1f, 0, 0.45f, 0.3f, 0 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { -0.3f, 1.15f, 0.25f, 0.3f, 0 }, { 0.3f, 1.15f, 0.25f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ 0.65f, 0.55f, 0.65f, 0.3f, 90 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, 0.55f, 0.65f, 0.3f, 90 }, { 0, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{// P
			{ 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ 0, 0, 0.65f, 0.3f, 0 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.65f, 0.3f, 0 }, { 0.2f, -0.6f, 0.45f, 0.3f, 360 - 300 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ -0.1f, 0, 0.45f, 0.3f, 0 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0.45f, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0, 0, 0.65f, 0.3f, 0 },
					{ 0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { -0.4f, 1.15f, 0.45f, 0.3f, 0 }, { 0.4f, 1.15f, 0.45f, 0.3f, 0 }, { 0, 0.55f, 0.65f, 0.3f, 90 },
					{ 0, -0.55f, 0.65f, 0.3f, 90 }, },
			{// U
			{ -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 }, { -0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, },
			{ { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ -0.5f, -0.55f, 0.65f, 0.3f, 90 }, { 0.5f, -0.55f, 0.65f, 0.3f, 90 },
					{ 0, -1.15f, 0.45f, 0.3f, 0 }, },
			{ { -0.65f, 0.55f, 0.65f, 0.3f, 90 }, { 0.65f, 0.55f, 0.65f, 0.3f, 90 },
					{ -0.65f, -0.55f, 0.65f, 0.3f, 90 }, { 0.65f, -0.55f, 0.65f, 0.3f, 90 },
					{ -0.3f, -1.15f, 0.25f, 0.3f, 0 }, { 0.3f, -1.15f, 0.25f, 0.3f, 0 }, { 0, 0.55f, 0.65f, 0.3f, 90 },
					{ 0, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { -0.4f, 0.6f, 0.85f, 0.3f, 360 - 120 }, { 0.4f, 0.6f, 0.85f, 0.3f, 360 - 60 },
					{ -0.4f, -0.6f, 0.85f, 0.3f, 360 - 240 }, { 0.4f, -0.6f, 0.85f, 0.3f, 360 - 300 }, },
			{ { -0.4f, 0.6f, 0.85f, 0.3f, 360 - 120 }, { 0.4f, 0.6f, 0.85f, 0.3f, 360 - 60 },
					{ 0, -0.55f, 0.65f, 0.3f, 90 }, },
			{ { 0, 1.15f, 0.65f, 0.3f, 0 }, { 0.35f, 0.5f, 0.65f, 0.3f, 360 - 60 },
					{ -0.35f, -0.5f, 0.65f, 0.3f, 360 - 240 }, { 0, -1.15f, 0.65f, 0.3f, 0 }, }, {// .
			{ 0, -1.15f, 0.05f, 0.3f, 0 }, }, {// _
			{ 0, -1.15f, 0.65f, 0.3f, 0 }, }, {// -
			{ 0, 0, 0.65f, 0.3f, 0 }, }, {// +
			{ -0.4f, 0, 0.45f, 0.3f, 0 }, { 0.4f, 0, 0.45f, 0.3f, 0 }, { 0, 0.55f, 0.65f, 0.3f, 90 },
					{ 0, -0.55f, 0.65f, 0.3f, 90 }, } };
}
