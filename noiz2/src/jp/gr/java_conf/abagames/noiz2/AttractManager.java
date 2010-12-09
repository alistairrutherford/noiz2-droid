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
 * $Id: AttractManager.java,v 1.3 2002/10/01 09:41:03 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;


import jp.gr.java_conf.abagames.bulletml.Colors;
import android.content.Context;
import android.util.Log;

import com.netthreads.android.noiz2.ApplicationPreferences;
import com.netthreads.android.noiz2.R;
import com.netthreads.android.noiz2.data.BitmapCache;
import com.netthreads.android.noiz2.data.StateData;
import com.netthreads.android.noiz2.graphics.IScreen;
import com.netthreads.android.noiz2.sound.SoundPoolPlayer;

/**
 * Handle title/game over.
 * 
 * @version $Revision: 1.3 $
 */
public class AttractManager
{
    private static final int BOX_SIZE = 32;
    
	public static final int TITLE = 0;
	public static final int IN_GAME = 1;
	public static final int GAME_OVER = 2;
	public static final int STAGE_CLEAR = 3;

	private static final int BOTTOM_OFFSET = 96;
	
	public int state;
	private int cnt;

	public static final int STAGE_NUM = 11;
    public static final int SCENE_NUM = 10;

    private int[] stageX = new int[STAGE_NUM];
	private int[] stageY = new int[STAGE_NUM];

	private BarrageManager manager;
	private Ship ship;
	private PrefManager prMng;

	private static final int TITLE_IMAGE_NUM = 5;
	public int[] titleImage = new int[TITLE_IMAGE_NUM];

	private Context context = null;

	private int screenHeight = 0;
	private int screenWidth = 0;

	private String version = "Unknown";
	private StateData stateData = null;
	
    private IScreen screen = null;
    private BitmapCache cache = null;

	public AttractManager(Context context, BarrageManager manager, Ship ship, PrefManager prMng, StateData stateData, BitmapCache cache, String version)
	{
		this.context = context;
		this.version = version;
		this.stateData = stateData;
        this.cache = cache;

		this.screenWidth = stateData.viewWidth;
		this.screenHeight = stateData.viewHeight;

		this.manager = manager;
		this.ship = ship;
		this.prMng = prMng;
		int s = 0;
		int y = screenHeight / 3;

		for (int i = 0; i < 4; i++, y += BOX_SIZE)
		{
			int x = screenWidth / 7 * 4 - BOX_SIZE * i / 2;

			for (int j = 0; j <= i; j++, s++, x += BOX_SIZE)
			{
				stageX[s] = x;
				stageY[s] = y;
			}
		}
		stageX[10] = stageX[0];
		stageY[10] = y;
	}

	/*
	 * Load title images.
	 */

	public void loadImages()
	{
        titleImage[0] = cache.load(context, R.drawable.n);
        titleImage[1] = cache.load(context, R.drawable.o);
        titleImage[2] = cache.load(context, R.drawable.i);
        titleImage[3] = cache.load(context, R.drawable.z);
        titleImage[4] = cache.load(context, R.drawable.two);
	}

	/*
	 * Initialize title/game/gameover/stageclear.
	 */
	private int selectedStage;
	private boolean inDemo;
	private int stBtnY;

	public void initTitle()
	{
		state = TITLE;
		selectedStage = -1;
		inDemo = false;
		stBtnY = screenHeight-BOTTOM_OFFSET;
		cnt = 0;
		manager.initStageAsDemo(0);
		inDemo = true;
	}

	/**
	 * Start main game loop.
	 * 
	 * @param stage
	 */
	public void initGame(int stage)
	{
		state = IN_GAME;
		manager.initStage(stage);

		// Switch sound on if it is set
		boolean sound = ApplicationPreferences.getInstance().getSound();
		SoundPoolPlayer.instance().setSound(ApplicationPreferences.getInstance().getSound());
		
		Log.d("initGame", ""+sound);
	}

	/**
	 * Stop main game loop.
	 * 
	 */
	public void initGameover()
	{
		state = GAME_OVER;
		cnt = 0;
		
        // Switch sound off
        SoundPoolPlayer.instance().setSound(false);
	}

	public void initClear()
	{
		state = STAGE_CLEAR;

		// Bonus.setInhaleMag(8.0f);
		cnt = 0;
	}

	public boolean isInDemo()
	{
		return inDemo;
	}

	public void moveTitle()
	{
		cnt++;
		if (stateData.touched)
		{
			stateData.touched = false;
			
			if (selectedStage >= 0 && stateData.controlY > stBtnY)
			{
				initGame(selectedStage);
				return;
			}

			for (int i = 0; i < STAGE_NUM; i++)
			{
				if (stateData.controlX > stageX[i] && stateData.controlX < stageX[i] + BOX_SIZE && stateData.controlY > stageY[i] && stateData.controlY < stageY[i] + BOX_SIZE
						&& prMng.isOpened(i))
				{
					selectedStage = i;

					if (i == 10)
					{
						stageStr = "ENDLESS";
					}
					else
					{
						stageStr = "STAGE " + (i + 1);
					}

					hiscoreStr = Integer.toString(prMng.getStageScore(i));
					manager.initStageAsDemo(i);
					inDemo = true;
				}
			}
		}

	}

	private void checkHiscore()
	{
		int sc = ship.getScore();

		if (sc > prMng.getStageScore(selectedStage))
		{
			prMng.setStageScore(selectedStage, sc);
		}
	}

	public void moveGameover()
	{
		cnt++;

		if (cnt > 900 || (cnt > 128 && stateData.touched))
		{
			checkHiscore();

			prMng.save();

			initTitle();
		}
		
		stateData.touched = false;
	}

	public void moveClear()
	{
		cnt++;

		if (cnt > 900 || (cnt > 128 && stateData.touched))
		{
			checkHiscore();
			prMng.clearStage(selectedStage);

			prMng.save();

			initTitle();
		}
		
		stateData.touched = false;
	}

	private String stageStr, hiscoreStr;

	public void drawTitle()
	{
		for (int i = 0; i < STAGE_NUM; i++)
		{
			if (!prMng.isOpened(i))
				continue;

			int x = stageX[i], y = stageY[i];

			if (i != selectedStage)
			{
				screen.drawLine(x + 2, y, x + BOX_SIZE - 2, y, Colors.BOX_COLOR1);
				screen.drawLine(x + BOX_SIZE, y + 2, x + BOX_SIZE, y + BOX_SIZE - 2, Colors.BOX_COLOR1);
				screen.drawLine(x + BOX_SIZE - 2, y + BOX_SIZE, x + 2, y + BOX_SIZE, Colors.BOX_COLOR1);
				screen.drawLine(x, y + BOX_SIZE - 2, x, y + 2, Colors.BOX_COLOR1);
			}
			else
			{
				screen.drawThickLine(x, y, x + BOX_SIZE, y, Colors.BOX_COLOR1, Colors.BOX_COLOR2);
				screen.drawThickLine(x + BOX_SIZE, y, x + BOX_SIZE, y + BOX_SIZE, Colors.BOX_COLOR1, Colors.BOX_COLOR2);
				screen.drawThickLine(x + BOX_SIZE, y + BOX_SIZE, x, y + BOX_SIZE, Colors.BOX_COLOR1, Colors.BOX_COLOR2);
				screen.drawThickLine(x, y + BOX_SIZE, x, y, Colors.BOX_COLOR1, Colors.BOX_COLOR2);
			}
			if (i < 9)
			{
				LetterRender.drawString(Integer.toString(i + 1), x + BOX_SIZE / 4, y + BOX_SIZE / 4, 8, Colors.LETTER_COLOR);
			}
			else
				if (i == 9)
				{
					LetterRender.drawString(Integer.toString(i + 1), x, y + BOX_SIZE / 4, 7, Colors.LETTER_COLOR);
				}
				else
				{
					LetterRender.drawString("0", x + BOX_SIZE / 4, y + BOX_SIZE / 4, 8, Colors.LETTER_COLOR);
				}
		}
		
		if (selectedStage >= 0)
		{
			if ((cnt & 63) < 32)
			{
				LetterRender.drawString("START", screenWidth / 3, stBtnY + 6, 8, Colors.LETTER_COLOR);
			}
			screen.drawLine(0, stBtnY, screenWidth, stBtnY, Colors.BOX_COLOR1);
			LetterRender.drawStringFromRight(stageStr, screenWidth, 40, 10, Colors.LETTER_COLOR);
			LetterRender.drawStringFromRight(hiscoreStr, screenWidth, 72, 7, Colors.LETTER_COLOR);
		}
		else
		{

			LetterRender.drawString("VER " + version, 4, 40, 4, Colors.LETTER_COLOR);
			LetterRender.drawString("SELECT STAGE", 4, screenHeight - BOTTOM_OFFSET, 8, Colors.LETTER_COLOR);
		}
	}

	public void drawTitleBoard()
	{
		int x = 4, y = 4;

		for (int i = 0; i < TITLE_IMAGE_NUM; i++, x += 33)
		{
			screen.drawBitmap(titleImage[i], x, y);
		}
	}

	public void drawGameover()
	{
		int y;
		if (cnt < 128)
		{
			y = screenHeight / 3 * cnt / 128;
		}
		else
		{
			y = screenHeight / 3;
		}
		LetterRender.drawString("GAMEOVER", screenWidth / 4, y, 8, Colors.LETTER_COLOR);
	}

	public void drawClear()
	{
		int y;

		if (cnt < 128)
		{
			y = screenHeight - screenHeight / 3 * 2 * cnt / 128;
		}
		else
		{
			y = screenHeight / 3;
		}

		LetterRender.drawString("STAGE CLEAR", screenWidth / 8, y, 8, Colors.LETTER_COLOR);
	}

    /**
     * Set draw surface.
     * 
     * @param screen
     */
    public void setScreen(IScreen screen)
    {
        this.screen = screen;
    }    
	
}
