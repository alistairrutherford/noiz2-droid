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
 * $Id: ActionImpl.java,v 1.1.1.1 2002/09/20 16:25:23 kenta Exp $
 *
 * Copyright 2001 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.bulletml;

/**
 * Action status.
 * 
 * @version $Revision: 1.1.1.1 $
 */
public class ActionImpl
{
    public static final int NOT_EXIST = Integer.MIN_VALUE;

    private static final String ABSOLUTE_KEYWORD = "absolute";
    private static final String RELATIVE_KEYWORD = "relative";
    private static final String SEQUENCE_KEYWORD = "sequence";
    private static final String AIM_KEYWORD = "aim";

    public IChoice[] action;
    private int repeat;
    public int pc;
    private int waitCnt;
    private float aimSpeed, mvSpeed;
    private int mvspCnt;
    private float aimDrc, mvDrc;
    private boolean isAim;
    private int mvdrCnt;
    private float prvFireDrc, prvFireSpeed;
    private int acclCnt;
    private float aimMx, aimMy, mvMx, mvMy;

    private ActionImpl parent;
    private BulletImpl bullet;

    private BulletmlManager manager;

    private float[] prms;

    public ActionImpl(BulletmlManager bm)
    {
        manager = bm;
        pc = NOT_EXIST;
    }

    public void rewind()
    {
        repeat = 1;
        pc = -1;
        waitCnt = mvspCnt = mvdrCnt = acclCnt = 0;
        // **
        mvMx = mvMy = aimMx = aimMy = 0;
        prvFireDrc = 0;
        prvFireSpeed = 1;
        parent = null;
        prms = null;
    }

    public void set(Action action, BulletImpl bullet)
    {
        this.action = action.getContent();
        this.bullet = bullet;
        rewind();
    }

    public void setParams(float[] prms)
    {
        this.prms = prms;
    }

    public void setRepeat(int repeat)
    {
        this.repeat = repeat;
    }

    public void setParent(ActionImpl parent)
    {
        this.parent = parent;
    }

    public void setMoveStatus(int mvdrCnt, float mvDrc, boolean isAim, int mvspCnt, float mvSpeed, int acclCnt, float mvMx, float mvMy)
    {
        this.mvdrCnt = mvdrCnt;
        this.mvDrc = mvDrc;
        this.isAim = isAim;
        this.mvspCnt = mvspCnt;
        this.mvSpeed = mvSpeed;
        this.acclCnt = acclCnt;
        this.mvMx = mvMx;
        this.mvMy = mvMy;
    }

    public void setPrvFireStatus(float pfd, float pfs)
    {
        prvFireDrc = pfd;
        prvFireSpeed = pfs;
    }

    public void vanish()
    {
        if (parent != null)
        {
            parent.vanish();
        }
        pc = NOT_EXIST;
    }

    public final void move()
    {
        if (mvspCnt > 0)
        {
            mvspCnt--;
            bullet.speed += mvSpeed;
        }
        if (mvdrCnt > 0)
        {
            mvdrCnt--;
            // **
            // if ( mvdrCnt == 0 ) {
            // if ( isAim ) {
            // bullet.direction = bullet.getAimDeg();
            // }
            // } else {
            // bullet.direction += mvDrc;
            // }
            bullet.direction += mvDrc;
        }
        if (acclCnt > 0)
        {
            acclCnt--;
            bullet.mx += mvMx;
            bullet.my += mvMy;
        }

        if (pc == NOT_EXIST)
            return;

        if (waitCnt > 0)
        {
            waitCnt--;
            return;
        }

        for (;;)
        {

            pc++;
            if (pc >= action.length)
            {
                repeat--;
                if (repeat <= 0)
                {
                    pc = NOT_EXIST;
                    if (parent != null)
                    {
                        parent.setMoveStatus(mvdrCnt, mvDrc, isAim, mvspCnt, mvSpeed, acclCnt, mvMx, mvMy);
                        parent.setPrvFireStatus(prvFireDrc, prvFireSpeed);
                        bullet.changeAction(this, parent);
                    }
                    break;
                } else
                {
                    pc = 0;
                }
            }

            IChoice ac = action[pc];
            if (ac instanceof Repeat)
            {
                // Repeat action.
                ActionImpl newAction = manager.getActionImplInstance();
                if (newAction != null)
                {
                    Repeat rp = (Repeat) ac;
                    int rpNum = manager.bulletmlUtil.getIntValue(rp.getTimes(), prms);
                    if (rpNum <= 0)
                        return;
                    newAction.set(manager.bulletmlUtil.getActionElm(rp.getActionElm()), bullet);
                    float[] actPrms = manager.bulletmlUtil.getActionParams(rp.getActionElm(), prms);
                    if (actPrms == null)
                    {
                        newAction.setParams(prms);
                    } else
                    {
                        newAction.setParams(actPrms);
                    }
                    newAction.setRepeat(rpNum);
                    newAction.setParent(this);
                    newAction.setMoveStatus(mvdrCnt, mvDrc, isAim, mvspCnt, mvSpeed, acclCnt, mvMx, mvMy);
                    newAction.setPrvFireStatus(prvFireDrc, prvFireSpeed);
                    bullet.changeAction(this, newAction);
                    newAction.move();
                    break;
                }
            } else if (ac instanceof Action || ac instanceof ActionRef)
            {
                // Action.
                ActionImpl newAction = manager.getActionImplInstance();
                if (newAction != null)
                {
                    IChoice aec = (IChoice) ac;
                    newAction.set(manager.bulletmlUtil.getActionElm(aec), bullet);
                    float[] actPrms = manager.bulletmlUtil.getActionParams(aec, prms);
                    if (actPrms == null)
                    {
                        newAction.setParams(prms);
                    } else
                    {
                        newAction.setParams(actPrms);
                    }
                    newAction.setRepeat(1);
                    newAction.setParent(this);
                    newAction.setMoveStatus(mvdrCnt, mvDrc, isAim, mvspCnt, mvSpeed, acclCnt, mvMx, mvMy);
                    newAction.setPrvFireStatus(prvFireDrc, prvFireSpeed);
                    bullet.changeAction(this, newAction);
                    newAction.move();
                    break;
                }
            } else if (ac instanceof Fire || ac instanceof FireRef)
            {
                // Fire action.
                IChoice fec = (IChoice) ac;
                Fire fire = manager.bulletmlUtil.getFireElm(fec);
                float[] firePrms = manager.bulletmlUtil.getFireParams(fec, prms);
                // BulletImpl bi =
                // manager.getBulletImplInstance(bl.clr+1);
                BulletImpl bi = manager.getBulletImplInstance();
                if (bi != null)
                {
                    if (firePrms == null)
                    {
                        bi.setParams(manager.bulletmlUtil.getBulletParams(fire.getBulletElm(), prms));
                    } else
                    {
                        bi.setParams(firePrms);
                    }
                    bi.set(manager.bulletmlUtil.getBulletElm(fire.getBulletElm()), bullet.x, bullet.y, bullet.clr + 1, bullet);
                    Direction d = fire.getDirection();
                    if (d == null)
                    {
                        d = bi.drcElm;
                    }
                    float drc;
                    if (d != null)
                    {
                        if (firePrms == null)
                        {
                            drc = manager.bulletmlUtil.getFloatValue(d.getContent(), prms);
                        } else
                        {
                            drc = manager.bulletmlUtil.getFloatValue(d.getContent(), firePrms);
                        }
                        String type = d.getType();
                        if (type != null)
                        {
                            if (type.equals(AIM_KEYWORD))
                            {
                                drc += bullet.getAimDeg();
                            } else if (type.equals(SEQUENCE_KEYWORD))
                            {
                                drc += prvFireDrc;
                            } else if (type.equals(RELATIVE_KEYWORD))
                            {
                                drc += bullet.direction;
                            }
                        }
                    } else
                    {
                        drc = bullet.getAimDeg();
                    }
                    bi.direction = prvFireDrc = drc;
                    Speed s = fire.getSpeed();
                    if (s == null)
                    {
                        s = bi.spdElm;
                    }
                    float spd = 1;
                    if (s != null)
                    {
                        if (firePrms == null)
                        {
                            spd = manager.bulletmlUtil.getFloatValue(s.getContent(), prms);
                        } else
                        {
                            spd = manager.bulletmlUtil.getFloatValue(s.getContent(), firePrms);
                        }
                        String type = s.getType();
                        if (type != null && (type.equals(RELATIVE_KEYWORD) || type.equals(SEQUENCE_KEYWORD)))
                        {
                            spd += prvFireSpeed;
                        }
                    }
                    bi.speed = prvFireSpeed = spd;
                }
            } else if (ac instanceof ChangeSpeed)
            {
                // Change speed action.
                Speed s = ((ChangeSpeed) ac).getSpeed();
                String type = s.getType();
                mvspCnt = manager.bulletmlUtil.getIntValue(((ChangeSpeed) ac).getTerm(), prms);
                if (type != null && type.equals(SEQUENCE_KEYWORD))
                {
                    mvSpeed = manager.bulletmlUtil.getFloatValue(s.getContent(), prms);
                    // aimSpeed = bullet.speed + mvSpeed*mvspCnt;
                } else
                {
                    aimSpeed = manager.bulletmlUtil.getFloatValue(s.getContent(), prms);
                    if (type != null && (type.equals(RELATIVE_KEYWORD) || type.equals(SEQUENCE_KEYWORD)))
                    {
                        aimSpeed += bullet.speed;
                    }
                    mvSpeed = (aimSpeed - bullet.speed) / mvspCnt;
                }
            } else if (ac instanceof ChangeDirection)
            {
                // Change direction action.
                Direction d = ((ChangeDirection) ac).getDirection();
                String type = d.getType();
                mvdrCnt = manager.bulletmlUtil.getIntValue(((ChangeDirection) ac).getTerm(), prms);
                if (type != null && type.equals(SEQUENCE_KEYWORD))
                {
                    isAim = false;
                    mvDrc = manager.bulletmlUtil.getFloatValue(d.getContent(), prms);
                    // aimDrc = bullet.direction +
                    // mvDrc*mvdrCnt;
                } else
                {
                    aimDrc = manager.bulletmlUtil.getFloatValue(d.getContent(), prms);
                    if (type != null && type.equals(ABSOLUTE_KEYWORD))
                    {
                        isAim = false;
                        mvDrc = (aimDrc - bullet.direction) % 360;
                    } else if (type != null && type.equals(RELATIVE_KEYWORD))
                    {
                        isAim = false;
                        aimDrc += bullet.direction;
                        mvDrc = (aimDrc - bullet.direction) % 360;
                    } else
                    {
                        isAim = true;
                        mvDrc = (aimDrc + bullet.getAimDeg() - bullet.direction) % 360;
                    }
                    if (mvDrc > 180)
                        mvDrc -= 360;
                    if (mvDrc < -180)
                        mvDrc += 360;
                    mvDrc /= mvdrCnt;
                }
            } else if (ac instanceof Accel)
            {
                // Accel bullet.
                Accel al = (Accel) ac;
                Horizontal hrz = al.getHorizontal();
                acclCnt = manager.bulletmlUtil.getIntValue(al.getTerm(), prms);
                if (hrz != null)
                {
                    String type = hrz.getType();
                    if (type != null && type.equals(SEQUENCE_KEYWORD))
                    {
                        mvMx = manager.bulletmlUtil.getFloatValue(hrz.getContent(), prms);
                        // aimMx = bullet.mx + mvMx*acclCnt;
                    } else
                    {
                        aimMx = manager.bulletmlUtil.getFloatValue(hrz.getContent(), prms);
                        if (type != null && type.equals(RELATIVE_KEYWORD))
                        {
                            aimMx += bullet.mx;
                        }
                        mvMx = (aimMx - bullet.mx) / acclCnt;
                    }
                }
                Vertical vtc = al.getVertical();
                if (vtc != null)
                {
                    String type = vtc.getType();
                    if (type != null && type.equals(RELATIVE_KEYWORD))
                    {
                        mvMy = manager.bulletmlUtil.getFloatValue(vtc.getContent(), prms);
                        // aimMy = bullet.my + mvMy*acclCnt;
                    } else
                    {
                        aimMy = manager.bulletmlUtil.getFloatValue(vtc.getContent(), prms);
                        if (type != null && (type.equals(RELATIVE_KEYWORD) || type.equals(SEQUENCE_KEYWORD)))
                        {
                            aimMy += bullet.my;
                        }
                        mvMy = (aimMy - bullet.my) / acclCnt;
                    }
                }
            } else if (ac instanceof Wait)
            {
                // Wait action.
                waitCnt = manager.bulletmlUtil.getIntValue(((Wait) ac).getContent(), prms);
                break;
            } else if (ac instanceof Vanish)
            {
                // Vanish action.
                bullet.vanish();
                break;
            }

        }
    }
}
