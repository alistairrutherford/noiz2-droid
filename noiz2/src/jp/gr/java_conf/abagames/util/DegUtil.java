/**
 * Copyright 2002 Kenta Cho. All rights reserved.
 * 			Original
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
 * $Id: DegUtil.java,v 1.2 2002/09/21 02:23:45 kenta Exp $
 *
 * Copyright 2001 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.util;

/**
 * Changing the coordinate into the angle.
 * 
 * @version $Revision: 1.2 $
 */
public class DegUtil
{
    public static final int DIV = 1024;

    public static final int TAN_TABALE_SIZE = 1024;

    static int[] tantbl = new int[TAN_TABALE_SIZE + 2];

    static
    {
        int i, d = 0;
        double od = Math.PI * 2 / DIV;
        for (i = 0; i < TAN_TABALE_SIZE; i++)
        {
            while ((int) (Math.sin(d * od) / Math.cos(d * od) * TAN_TABALE_SIZE) < i)
                d++;
            tantbl[i] = d;
        }
        tantbl[TAN_TABALE_SIZE] = tantbl[TAN_TABALE_SIZE + 1] = 128;
    }

    public static int getDeg(int x, int y)
    {
        int tx, ty;
        int f, od, tn;

        if (x == 0 && y == 0)
        {
            return (0);
        }

        if (x < 0)
        {
            tx = -x;
            if (y < 0)
            {
                ty = -y;
                if (tx > ty)
                {
                    f = 1;
                    od = DIV * 3 / 4;
                    tn = ty * TAN_TABALE_SIZE / tx;
                } else
                {
                    f = -1;
                    od = DIV;
                    tn = tx * TAN_TABALE_SIZE / ty;
                }
            } else
            {
                ty = y;
                if (tx > ty)
                {
                    f = -1;
                    od = DIV * 3 / 4;
                    tn = ty * TAN_TABALE_SIZE / tx;
                } else
                {
                    f = 1;
                    od = DIV / 2;
                    tn = tx * TAN_TABALE_SIZE / ty;
                }
            }
        } else
        {
            tx = x;
            if (y < 0)
            {
                ty = -y;
                if (tx > ty)
                {
                    f = -1;
                    od = DIV / 4;
                    tn = ty * TAN_TABALE_SIZE / tx;
                } else
                {
                    f = 1;
                    od = 0;
                    tn = tx * TAN_TABALE_SIZE / ty;
                }
            } else
            {
                ty = y;
                if (tx > ty)
                {
                    f = 1;
                    od = DIV / 4;
                    tn = ty * TAN_TABALE_SIZE / tx;
                } else
                {
                    f = -1;
                    od = DIV / 2;
                    tn = tx * TAN_TABALE_SIZE / ty;
                }
            }
        }
        return (od + tantbl[tn] * f);
    }

    /*
     * public static int getDistance(int x, int y) { if ( x < 0 ) x = -x; if ( y
     * < 0 ) y = -y; if ( x > y ) { return x + (y>>1); } else { return y +
     * (x>>1); } }
     */
}
