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
 * $Id: Expression.java,v 1.2 2002/09/21 02:23:44 kenta Exp $
 *
 * Copyright 2002 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.bulletml;

import java.util.Random;

/**
 * Turn an expression into RPN.
 * 
 * @version $Revision: 1.2 $
 */
public class Expression
{
	private static Random rnd = new Random();

	private static final int MAX_LENGTH = 128;

	public static final int STACK_VARIABLE = 11;
	public static final int STACK_RANK = -2;
	public static final int STACK_RAND = -1;
	public static final int STACK_NUM = 0;
	public static final int PLUS = 1;
	public static final int MINUS = 2;
	public static final int MULTIPLE = 3;
	public static final int DIVISION = 4;
	public static final int MODULO = 5;

	private float[] num = new float[MAX_LENGTH];
	private int[] opr = new int[MAX_LENGTH];
	private int idx;

	private float[] stack = new float[MAX_LENGTH];

	private BulletmlUtil bulletmlUtil;

	public Expression(BulletmlUtil bulletmlUtil)
	{
		this.bulletmlUtil = bulletmlUtil;
		idx = 0;
	}

	private float calcOp(int op, float n1, float n2)
	{
		switch (op)
		{
		case PLUS:
			return n1 + n2;
		case MINUS:
			return n1 - n2;
		case MULTIPLE:
			return n1 * n2;
		case DIVISION:
			return n1 / n2;
		case MODULO:
			return n1 % n2;
		}
		return 0;
	}

	public void setOperator(int op)
	{
		if (idx >= MAX_LENGTH)
			return;
		if (opr[idx - 1] == STACK_NUM && opr[idx - 2] == STACK_NUM)
		{
			num[idx - 2] = calcOp(op, num[idx - 2], num[idx - 1]);
			idx--;
		}
		else
		{
			opr[idx] = op;
			idx++;
		}
	}

	public void push(float nm, int vr)
	{
		if (idx >= MAX_LENGTH)
			return;
		num[idx] = nm;
		opr[idx] = vr;
		idx++;
	}

	public final float calc(float[] prms)
	{
		int stkIdx = 0;
		for (int i = 0; i < idx; i++)
		{
			switch (opr[i])
			{
			case STACK_NUM:
				stack[stkIdx] = num[i];
				stkIdx++;
				break;
			case STACK_RAND:
				stack[stkIdx] = rnd.nextFloat();
				stkIdx++;
				break;
			case STACK_RANK:
				stack[stkIdx] = bulletmlUtil.getRank();
				stkIdx++;
				break;
			default:
				if (opr[i] >= STACK_VARIABLE)
				{
					stack[stkIdx] = prms[opr[i] - STACK_VARIABLE];
					stkIdx++;
				}
				else
				{
					stack[stkIdx - 2] = calcOp(opr[i], stack[stkIdx - 2], stack[stkIdx - 1]);
					stkIdx--;
				}
				break;
			}
		}
		return stack[0];
	}
}
