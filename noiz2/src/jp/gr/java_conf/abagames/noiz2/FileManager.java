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
 * $Id: GameManager.java,v 1.5 2001/06/03 00:19:12 ChoK Exp $
 *
 * Copyright 2001 Kenta Cho. All rights reserved.
 */
package jp.gr.java_conf.abagames.noiz2;

import java.io.InputStream;

import jp.gr.java_conf.abagames.bulletml.Bulletml;
import jp.gr.java_conf.abagames.bulletml.BulletmlManager;
import android.content.Context;
import android.content.res.AssetManager;

/**
 * Handle game status.
 *
 * @version $Revision: 1.5 $
 */
public class FileManager
{
	private Context context = null;
	
    /**
     * @param context
     * @param screenHeight
     * @param screenWidth
     */
    public FileManager(Context context) 
    {
    	this.context = context;
	}
    

    /**
     * Load ML definition file.
     * 
     * @param The document name. 
     */
    public Bulletml loadBulletML(String document, BulletmlManager manager)
    {
    	Bulletml bulletML = null;
    	
        try
        {
        	AssetManager assetManager = context.getResources().getAssets(); 
        	InputStream stream = assetManager.open(document);
            
            bulletML = new Bulletml(stream, manager);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return bulletML;
    }
    
}
