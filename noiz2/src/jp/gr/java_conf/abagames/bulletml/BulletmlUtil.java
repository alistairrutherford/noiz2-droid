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
 * $Id: BulletmlUtil.java,v 1.1.1.1 2002/09/20 16:25:23 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.bulletml;

import java.util.*;

/**
 * Utility class for BulletML.
 * 
 * @version $Revision: 1.1.1.1 $
 */
public class BulletmlUtil
{
	private Hashtable<String, Expression> expressions = new Hashtable<String, Expression>();

	public int getIntValue(String v, float[] prms)
	{
		return (int) evalExpression(v, prms);
	}

	public float getFloatValue(String v, float[] prms)
	{
		return evalExpression(v, prms);
	}

	private char[] expChr;
	private float rank = 0.5f;

	public void setRank(float rk)
	{
		rank = rk;
	}

	public float getRank()
	{
		return rank;
	}

	private void evalFloatValue(Expression ep, int stIdx, int lgt, float sign)
	{
		if (expChr[stIdx] == '$')
		{
			String label = new String(expChr, stIdx + 1, lgt - 1);
			if (label.equals("rand"))
			{
				ep.push(0, Expression.STACK_RAND);
			}
			else
				if (label.equals("rank"))
				{
					ep.push(0, Expression.STACK_RANK);
				}
				else
				{
					int idx;
					try
					{
						idx = new Integer(label).intValue() - 1;
					}
					catch (NumberFormatException e)
					{
						ep.push(0, Expression.STACK_NUM);
						return;
					}
					ep.push(0, Expression.STACK_VARIABLE + idx);
				}
		}
		else
		{
			try
			{
				ep.push(new Float(new String(expChr, stIdx, lgt)).floatValue() * sign, Expression.STACK_NUM);
			}
			catch (NumberFormatException e)
			{
				ep.push(0, Expression.STACK_NUM);
			}
		}
	}

	private void evalExpPart(Expression ep, int stIdx, int edIdx)
	{
		int op[] = new int[] { -1, -1 };
		while (expChr[stIdx] == '(' && expChr[edIdx - 1] == ')')
		{
			stIdx++;
			edIdx--;
		}
		for (int i = edIdx - 1; i >= stIdx; i--)
		{
			char c = expChr[i];
			if (c == ')')
			{
				do
				{
					i--;
				}
				while (expChr[i] != '(');
			}
			else
				if (op[0] < 0 && (c == '*' || c == '/' || c == '%'))
				{
					op[0] = i;
				}
				else
					if (c == '+' || c == '-')
					{
						op[1] = i;
						break;
					}
		}
		if (op[1] < 0)
		{
			if (op[0] < 0)
			{
				evalFloatValue(ep, stIdx, edIdx - stIdx, 1);
			}
			else
			{
				switch (expChr[op[0]])
				{
				case '*':
					evalExpPart(ep, stIdx, op[0]);
					evalExpPart(ep, op[0] + 1, edIdx);
					ep.setOperator(Expression.MULTIPLE);
					break;
				case '/':
					evalExpPart(ep, stIdx, op[0]);
					evalExpPart(ep, op[0] + 1, edIdx);
					ep.setOperator(Expression.DIVISION);
					break;
				case '%':
					evalExpPart(ep, stIdx, op[0]);
					evalExpPart(ep, op[0] + 1, edIdx);
					ep.setOperator(Expression.MODULO);
					break;
				}
			}
		}
		else
		{
			if (op[1] == stIdx)
			{
				switch (expChr[op[1]])
				{
				case '-':
					evalFloatValue(ep, stIdx + 1, edIdx - stIdx - 1, -1);
					break;
				case '+':
					evalFloatValue(ep, stIdx + 1, edIdx - stIdx - 1, 1);
					break;
				}
			}
			else
			{
				switch (expChr[op[1]])
				{
				case '+':
					evalExpPart(ep, stIdx, op[1]);
					evalExpPart(ep, op[1] + 1, edIdx);
					ep.setOperator(Expression.PLUS);
					break;
				case '-':
					evalExpPart(ep, stIdx, op[1]);
					evalExpPart(ep, op[1] + 1, edIdx);
					ep.setOperator(Expression.MINUS);
					break;
				}
			}
		}
	}

	public float evalExpression(String exp, float[] p)
	{
		Expression ep = (Expression) expressions.get(exp);
		if (ep == null)
		{
			expChr = new char[exp.length()];
			int ecIdx = 0;
			boolean skip = false;
			StringBuffer buf = new StringBuffer(exp);
			int depth = 0;
			boolean balance = true;
			char ch;
			
			int length = buf.length();
			
			for (int i = 0; i < length; i++)
			{
				ch = buf.charAt(i);
				switch (ch)
				{
				case ' ':
				case '\n':
					skip = true;
					break;
				case ')':
					depth--;
					if (depth < 0)
						balance = false;
					break;
				case '(':
					depth++;
					break;
				}
				if (skip)
				{
					skip = false;
				}
				else
				{
					expChr[ecIdx] = ch;
					ecIdx++;
				}
			}
			if (depth != 0 || !balance)
			{
				return 0;
			}
			ep = new Expression(this);
			evalExpPart(ep, 0, ecIdx);
			expressions.put(exp, ep);
		}
		return ep.calc(p);
	}

	private Hashtable<String, Bullet> bullets = new Hashtable<String, Bullet>();
	private Hashtable<String, Action> actions = new Hashtable<String, Action>();
	private Hashtable<String, Fire> fires = new Hashtable<String, Fire>();

	public void clear()
	{
		bullets.clear();
		actions.clear();
		fires.clear();
		expressions.clear();
	}

	public void addBullet(Bullet blt)
	{
		bullets.put(blt.getLabel(), blt);
	}

	public void addAction(Action act)
	{
		actions.put(act.getLabel(), act);
	}

	public void addFire(Fire fre)
	{
		fires.put(fre.getLabel(), fre);
	}

	public final Bullet getBulletElm(IChoice bec)
	{
		if (bec instanceof BulletRef)
		{
			String label = ((BulletRef) bec).getLabel();
			Bullet blt = (Bullet) bullets.get(label);
			if (blt == null)
			{
				System.out.println("unknown bullet label: " + label);
			}
			return blt;
		}
		else
			if (bec instanceof Bullet)
			{
				return (Bullet) bec;
			}
		return null;
	}

	public final float[] getBulletParams(IChoice bec, float[] prms)
	{
		if (bec instanceof BulletRef)
		{
			BulletRef br = (BulletRef) bec;
			float[] prm = new float[br.getParamCount()];
			
			int length = prm.length;
			
			for (int i = length - 1; i >= 0; i--)
			{
				// prm[i] =
				// BulletmlUtil.getFloatValue(br.getParam(i).getContent(),
				// prms);
				prm[i] = getFloatValue(br.getParam(i), prms);
			}
			return prm;
		}
		return null;
	}

	public final Action getActionElm(IChoice aec)
	{
		if (aec instanceof ActionRef)
		{
			String label = ((ActionRef) aec).getLabel();
			Action act = (Action) actions.get(label);
			if (act == null)
			{
				System.out.println("unknown action label: " + label);
			}
			return act;
		}
		else
			if (aec instanceof Action)
			{
				return (Action) aec;
			}
		return null;
	}

	public final float[] getActionParams(IChoice aec, float[] prms)
	{
		if (aec instanceof ActionRef)
		{
			ActionRef ar = (ActionRef) aec;
			float[] prm = new float[ar.getParamCount()];
			
			int length = prm.length;
			
			for (int i = length - 1; i >= 0; i--)
			{
				// prm[i] =
				// BulletmlUtil.getFloatValue(ar.getParam(i).getContent(),
				// prms);
				prm[i] = getFloatValue(ar.getParam(i), prms);
			}
			return prm;
		}
		return null;
	}

	public final Fire getFireElm(IChoice fec)
	{
		if (fec instanceof FireRef)
		{
			String label = ((FireRef) fec).getLabel();
			Fire fire = (Fire) fires.get(label);
			if (fire == null)
			{
				System.out.println("unknown fire label: " + label);
			}
			return fire;
		}
		else
			if (fec instanceof Fire)
			{
				return (Fire) fec;
			}
		return null;
	}

	public final float[] getFireParams(IChoice fec, float[] prms)
	{
		if (fec instanceof FireRef)
		{
			FireRef fr = (FireRef) fec;
			float[] prm = new float[fr.getParamCount()];
			
			int length = prm.length;
			
			for (int i = length - 1; i >= 0; i--)
			{
				// prm[i] =
				// BulletmlUtil.getFloatValue(fr.getParam(i).getContent(),
				// prms);
				prm[i] = getFloatValue(fr.getParam(i), prms);
			}
			return prm;
		}
		return null;
	}

	public final IChoice[] getActionStartsWith(String label)
	{
		Vector<Action> actVct = new Vector<Action>();

		Enumeration<String> keys = actions.keys();

		while (keys.hasMoreElements())
		{
			String lbl = (String) keys.nextElement();
			if (lbl.startsWith(label))
			{
				actVct.addElement((Action) actions.get(lbl));
			}
		}
		IChoice[] iac = new IChoice[actVct.size()];
		actVct.copyInto(iac);
		return iac;
	}
}
