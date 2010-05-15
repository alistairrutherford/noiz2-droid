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
 * $Id: PrefManager.java,v 1.3 2002/10/01 09:41:04 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;

import android.content.Context;

import com.netthreads.android.noiz2.GameState;

/**
 * Save/load preferences(hiscore).
 * 
 */
public class PrefManager
{
    private int[] stageScore = new int[AttractManager.STAGE_NUM];
    private int[][] sceneScore = new int[AttractManager.STAGE_NUM - 1][AttractManager.SCENE_NUM];
    private boolean[] stageOpened = new boolean[AttractManager.STAGE_NUM]; 
    
    private boolean[] stageCleared = new boolean[AttractManager.STAGE_NUM];

    private Context context = null;
    
    /*
     * Stages are locked until some stages are cleared.
     */
    private int[][] nextOpen =
        {
            { 1, 2 }, // 1
            { 2, 3 }, // 2 
            { 3, 4 }, // 3
            { 4, 5 }, // 4
            { 5, 6 }, // 5
            { 6, 7 }, // 6
            { 7, 8 }, // 7
            { 8, 9 }, // 8 
            { 8, 9 }, // 9
            { 8, 9 }  // 10
        };
    
    /**
     * We need context to persist state.
     * 
     * @param context
     */
    public PrefManager(Context context)
    {
        this.context = context;
    }
    
    /**
     * Initialise stage state.
     * 
     */
    public void init()
    {
        for (int i = 0; i < AttractManager.STAGE_NUM; i++)
        {
            stageScore[i] = 10000;
            stageOpened[i] = false;
            stageCleared[i] = false;

            if (i == (AttractManager.STAGE_NUM - 1))
            {
                continue;
            }

            for (int j = 0; j < AttractManager.SCENE_NUM; j++)
            {
                sceneScore[i][j] = 1000;
            }
        }
        
        stageOpened[0] = true;
    }

    /**
     * Save game state.
     * 
     */
    public void save()
    {
        GameState gameState = GameState.getInstance(context);
        
        for (int i = 0; i < AttractManager.STAGE_NUM; i++)
        {
            gameState.setStageScore(i, stageScore[i]);
            if (stageOpened[i]) gameState.setStageOpened(i, stageOpened[i]);
            if (stageCleared[i]) gameState.setStageCleared(i, stageCleared[i]);

            if (i == (AttractManager.STAGE_NUM - 1))
            {
                continue;
            }

            for (int j = 0; j < AttractManager.SCENE_NUM; j++)
            {
                gameState.setSceneScore(i, sceneScore[i][j]);
            }
        }

    }

    /**
     * Load game state.
     * 
     */
    public void load()
    {
        GameState gameState = GameState.getInstance(context);

        for (int i = 0; i < AttractManager.STAGE_NUM; i++)
        {
            stageScore[i] = gameState.getStageScore(i);
            stageOpened[i] = gameState.getStageOpened(i);
            stageCleared[i] = gameState.getStageCleared(i);

            if (i == (AttractManager.STAGE_NUM - 1))
            {
                continue;
            }

            for (int j = 0; j < AttractManager.SCENE_NUM; j++)
            {
                sceneScore[i][j] = gameState.getSceneScore(i);
            }
        }
        
        stageOpened[0] = true;
        
        clearStage(10);
    }

    /**
     * Clear scene state.
     * 
     * @param ss
     * @param stage
     * @param scene
     * 
     * @return score
     */
    public int clearScene(int ss, int stage, int scene)
    {
        int ssof = 0;

        try
        {
            int rss = sceneScore[stage][scene];
            ssof = ss - rss;

            if (ss > rss)
            {
                sceneScore[stage][scene] = ss;
            }
            
        } catch (Throwable t)
        {
            System.out.printf(t.toString());
        }

        return ssof;
    }

    /**
     * Clear stage state.
     * 
     * @param stage
     */
    public void clearStage(int stage)
    {
        stageCleared[stage] = true;

        if (stage < nextOpen.length)
        {
            stageOpened[nextOpen[stage][0]] = true;
            stageOpened[nextOpen[stage][1]] = true;
        }

        // Test to see if all 0..9 stages are clear. If so then endless mode is unlocked.
        boolean endless = true;
        for (int i = 0; i < (AttractManager.STAGE_NUM - 1); i++)
        {
            endless &=stageCleared[i];
        }

        stageOpened[AttractManager.STAGE_NUM - 1] = endless;
    }

    /**
     * Return state of stage.
     * 
     * @param stage
     * 
     * @return state
     */
    public boolean isOpened(int stage)
    {
        return stageOpened[stage];
    }
    

    /**
     * Get stage score.
     * 
     * @param stage
     * 
     * @return score
     */
    public int getStageScore(int stage)
    {
        return stageScore[stage];
    }

    /**
     * Set stage score.
     * 
     * @param stage
     * @param score
     */
    public void setStageScore(int stage, int score)
    {
        stageScore[stage] = score;
    }    
}
