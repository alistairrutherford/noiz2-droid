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
 * $Id: SCTable.java,v 1.2 2002/09/21 02:23:45 kenta Exp $
 *
 * Copyright 2001 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.util;

/**
 * Sin Cos table.
 *
 * @version $Revision: 1.2 $
 */
public class SCTable {
  public static final int TABLE_SIZE = DegUtil.DIV;

  static public int[] sintbl = new int[TABLE_SIZE];
  static public int[] costbl = new int[TABLE_SIZE];

  static {
    for ( int i=TABLE_SIZE-1 ; i>=0 ; i-- ) {
      sintbl[i] = (int)(Math.sin(i*(6.28/TABLE_SIZE))*256);
      costbl[i] = (int)(Math.cos(i*(6.28/TABLE_SIZE))*256);
    }
  }
}
