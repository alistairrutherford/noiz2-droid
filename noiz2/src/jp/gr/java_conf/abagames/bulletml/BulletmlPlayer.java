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
 * $Id: BulletmlPlayer.java,v 1.3 2002/10/04 14:05:12 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.bulletml;

import jp.gr.java_conf.abagames.noiz2.AttractManager;
import jp.gr.java_conf.abagames.noiz2.BarrageManager;
import jp.gr.java_conf.abagames.noiz2.Ship;
import jp.gr.java_conf.abagames.util.SCTable;
import android.content.Context;

import com.netthreads.android.noiz2.graphics.IScreen;

/**
 * Handle a tread.
 * 
 * @version $Revision: 1.3 $
 */
public class BulletmlPlayer
{
    private static final int WAKE_COUNT = 64;
    
    private BarrageManager manager;
    private Ship ship;

    private AttractManager attractManager;

    private IScreen screen = null;

    public void init(Context context, BarrageManager manager, Ship ship, AttractManager attractManager)
    {
        this.manager = manager;
        manager.setPlayer(this);
        this.ship = ship;
        this.attractManager = attractManager;
    }

    /*
     * Draw enemy cubes/bullets/my ship/bonus items/sparks.
     */

    private static final int[][] enpb =
        {
        { 1, 1, 1 },
        { -1, 1, 1 },
        { -1, -1, 1 },
        { 1, -1, 1 },
        { 1, 1, -1 },
        { -1, 1, -1 },
        { -1, -1, -1 },
        { 1, -1, -1 }, };

    private static final int[][] enpc =
        {
        { 0, 1, 2, 3 },
        { 4, 5, 6, 7 },
        { 0, 1, 5, 4 },
        { 1, 2, 6, 5 },
        { 2, 3, 7, 6 },
        { 3, 0, 4, 7 } };

    private final int[][] enp = new int[8][2];

    /**
     * Draw enemy figure.
     * 
     * @param screen
     * @param x
     * @param y
     * @param cnt
     * @param shield
     * @param type
     */
    public final void drawEnemy(int x, int y, int cnt, int shield, int type)
    {
        int size, d1, d2;
        int ty;
        int eci1, eci2;
        if (cnt < 32)
            size = cnt << 1;
        else
            size = 64;
        size += (shield << 5);
        d1 = (cnt * 5) & (SCTable.TABLE_SIZE - 1);
        d2 = (cnt * 7) & (SCTable.TABLE_SIZE - 1);
        for (int i = 0; i < 8; i++)
        {
            enp[i][0] = (enpb[i][0] * SCTable.costbl[d1] - enpb[i][1] * SCTable.sintbl[d1]) * size;
            ty = (enpb[i][0] * SCTable.sintbl[d1] + enpb[i][1] * SCTable.costbl[d1]);
            enp[i][1] = (((ty * SCTable.costbl[d2]) >> 8) - enpb[i][2] * SCTable.sintbl[d2]) * size;
            enp[i][0] >>= 4;
            enp[i][1] >>= 4;
        }
        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                eci1 = enpc[i][j];
                eci2 = enpc[i][(j + 1) & 3];
                screen.drawLine((x + enp[eci1][0]) >> 8, (y + enp[eci1][1]) >> 8, (x + enp[eci2][0]) >> 8, (y + enp[eci2][1]) >> 8, Colors.ENEMY_COLOR[type]);
            }
        }
    }

    public final void drawBullet(int x, int y, int px, int py, int sx, int sy, int cl, int cnt)
    {
        if (cnt < WAKE_COUNT)
        {
            int wc = (WAKE_COUNT - cnt) << 2;
            wc = ((wc >> 1) << 16 | (wc >> 0) << 8 | (wc >> 1) | 0xff000000);

            screen.drawLine(sx >> 8, sy >> 8, px >> 8, py >> 8, wc);
        }

        screen.drawThickLine(x >> 8, y >> 8, px >> 8, py >> 8, Colors.BULLET_COLOR[cl % 3], 0xffffffff);
    }

    private int shipX, shipY;

    public final void drawShip(int x, int y, int px, int py, int cnt)
    {
        shipX = x >> 8;
        shipY = y >> 8;

        screen.drawThickLine(shipX, shipY, px >> 8, py >> 8, Colors.SHIP_COLOR1, Colors.SHIP_COLOR2);

        int ly = (cnt & 31) * 12;

        screen.drawLine(shipX, shipY, shipX, shipY - ly, Colors.LOCK_COLOR1);

        screen.drawLine(shipX, shipY - ly, shipX, 0, Colors.LOCK_COLOR2);

        drawShipBody(x, y, px, py, cnt);
    }

    /**
     * We give the ship a body. This is so we can put something on the
     * touch-screen other than the laser.
     * 
     * @param x
     * @param y
     * @param px
     * @param py
     * @param cnt
     * 
     */
    public final void drawShipBody(int x, int y, int px, int py, int cnt)
    {
        // Draw body

        int size = Ship.SHIP_SIZE;

        int d1 = (cnt * 5) & (SCTable.TABLE_SIZE - 1);
        int d2 = 180; // (cnt * 7) & (SCTable.TABLE_SIZE - 1);

        for (int i = 0; i < 8; i++)
        {
            enp[i][0] = (enpb[i][0] * SCTable.costbl[d1] - enpb[i][1] * SCTable.sintbl[d1]) * size;
            int ty = (enpb[i][0] * SCTable.sintbl[d1] + enpb[i][1] * SCTable.costbl[d1]);
            enp[i][1] = (((ty * SCTable.costbl[d2]) >> 8) - enpb[i][2] * SCTable.sintbl[d2]) * size;
            enp[i][0] >>= 4;
            enp[i][1] >>= 4;
        }
        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                int eci1 = enpc[i][j];
                int eci2 = enpc[i][(j + 1) & 3];
                screen.drawLine((x + enp[eci1][0]) >> 8, (y + enp[eci1][1]) >> 8, (x + enp[eci2][0]) >> 8, (y + enp[eci2][1]) >> 8, Colors.SHIP_COLOR);
            }
        }

    }

    public final void drawHormingLaser(int x, int y, int px, int py, int cnt)
    {
        screen.drawLine(shipX, shipY, px >> 8, py >> 8, Colors.LOCK_WAKE_COLOR);
        screen.drawThickLine(x >> 8, y >> 8, px >> 8, py >> 8, Colors.HL_COLOR1, Colors.HL_COLOR2);
    }

    private final int BONUS_COLOR = 0xff44ee22;

    public final void drawBonus(int x, int y, int cnt)
    {
        int d, ox, oy, sx, sy;
        if (cnt < 16)
            d = (32 - cnt) * cnt;
        else
            d = 16 * 16 + cnt * 16;
        d &= (SCTable.TABLE_SIZE - 1);
        ox = SCTable.costbl[d] >> 6;
        oy = SCTable.sintbl[d] >> 6;
        sx = x >> 8;
        sy = y >> 8;

        screen.drawLine(sx - ox, sy - oy, sx + ox, sy + oy, BONUS_COLOR);
        screen.drawLine(sx + oy, sy - ox, sx - oy, sy + ox, BONUS_COLOR);
    }

    private final int SPARK_COLOR = 0xffddff33;

    public final void drawSpark(int x, int y)
    {
        screen.drawDot(x >> 8, y >> 8, SPARK_COLOR);
    }

    public void hitShip()
    {
        ship.hit();
    }

    public void lockShip(BulletImpl bl)
    {
        ship.lock(bl);
    }

    public void clearStage()
    {
        attractManager.initClear();
    }

    public void gameover()
    {
        attractManager.initGameover();
    }

    /*
     * Methods to handle a thread.
     */
    private int interval = BarrageManager.INTERVAL_BASE;

    public void start()
    {
    }

    public void setInterval(int itv)
    {
        interval = itv;
    }

    public void deactive()
    {
    }

    public void terminate()
    {
    }

    public final void wakeUp()
    {
        try
        {
            update();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private long prvTickCount = 0;

    /**
     * Thread main loop.
     */
    public void update()
    {
        long nowTick = System.currentTimeMillis();
        int frame = (int) (nowTick - prvTickCount) / interval;
        if (frame <= 0)
        {
            frame = 1;
            prvTickCount = nowTick;
        } else if (frame > 5)
        {
            frame = 5;
            prvTickCount = nowTick;
        } else
        {
            prvTickCount += frame * interval;
        }

        int state = attractManager.state;
        
        // Movements.
        for (int i = 0; i < frame; i++)
        {
            switch (state)
            {
            case AttractManager.TITLE:
                attractManager.moveTitle();
                if (attractManager.isInDemo())
                {
                    manager.addBullets();
                    manager.moveBullets();
                }
                break;
            case AttractManager.IN_GAME:
                ship.move();
                manager.addBullets();
                manager.moveBullets();
                break;
            case AttractManager.STAGE_CLEAR:
                attractManager.moveClear();
                ship.move();
                manager.moveBullets();
                break;
            case AttractManager.GAME_OVER:
                attractManager.moveGameover();
                ship.moveSpark();
                manager.moveBullets();
                break;
            }
        }
    }

    /**
     * Draw managed items.
     * 
     * @param canvas
     */
    public void draw()
    {
        // Clear the screen
        screen.clear();

        switch (attractManager.state)
        {
        case AttractManager.TITLE:
            if (attractManager.isInDemo())
            {
                manager.drawBullets();
            }
            attractManager.drawTitle();
            attractManager.drawTitleBoard();
            break;

        case AttractManager.IN_GAME:
            ship.draw();
            manager.drawScene();
            manager.drawBullets();
            ship.drawScore();
            break;

        case AttractManager.STAGE_CLEAR:
            ship.draw();
            manager.drawBullets();
            ship.drawScore();
            manager.drawScene();
            attractManager.drawClear();
            break;
        case AttractManager.GAME_OVER:
            ship.drawSpark();
            manager.drawBullets();
            ship.drawScore();
            attractManager.drawGameover();
            break;
        }

    }

    /**
     * Return reference to ship.
     * 
     * @return Ship reference.
     */
    public Ship getShip()
    {
        return ship;
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
