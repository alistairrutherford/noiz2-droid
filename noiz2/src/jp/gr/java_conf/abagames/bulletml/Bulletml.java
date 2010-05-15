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
package jp.gr.java_conf.abagames.bulletml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;

/**
 * Bulletml xml data.
 * 
 * @version $Revision: 1.1.1.1 $
 */
public class Bulletml
{
	public final static String TAG_ACTION = "action";
	public final static String TAG_BULLET = "bullet";
	public final static String TAG_FIRE = "fire";
	public final static String TAG_REPEAT = "repeat";
	public final static String TAG_FIRE_REF = "fireRef";
	public final static String TAG_CHANGE_SPEED = "changeSpeed";
	public final static String TAG_CHANGE_DIRECTION = "changeDirection";
	public final static String TAG_ACCEL = "accel";
	public final static String TAG_WAIT = "wait";
	public final static String TAG_VANISH = "vanish";
	public final static String TAG_ACTION_REF = "actionRef";
	public final static String TAG_DIRECTION = "direction";
	public final static String TAG_SPEED = "speed";
	public final static String TAG_BULLET_REF = "bulletRef";
	public final static String TAG_TERM = "term";
	public final static String TAG_VERTICAL = "vertical";
	public final static String TAG_HORIZONTAL = "horizontal";
	public final static String TAG_PARAM = "param";
	public final static String TAG_TIMES = "times";

	public final static String ATTR_LABEL = "label";
	public final static String ATTR_TYPE = "type";

	public final static String VALUE_AIM = "aim";
	
	private XmlPullParserFactory factory = null;
	private XmlPullParser parser = null;

	public Bulletml(InputStream is, BulletmlManager manager) throws IOException
	{
		fetch(is, manager);

		IChoice[] iac = manager.bulletmlUtil.getActionStartsWith("top");
		
		manager.setTopActions(iac);
	}

	/**
	 * Fetch and parse data.
	 * 
	 * @param stream
	 * @param manager
	 * 
	 * @return The status of parse.
	 */
	public boolean fetch(InputStream stream, BulletmlManager manager)
	{
		boolean status = true;

		try
		{
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(false);

			parser = factory.newPullParser();

			parser.setInput(stream, null);

			int type = 0;
			
			// Start parsing loop
			while (((type=parser.nextToken())!=XmlPullParser.END_DOCUMENT))
			{
				if (type==XmlPullParser.START_TAG)
				{
					String name = parser.getName();
					
					if (name.equals(Bulletml.TAG_ACTION))
					{
						Action node = new Action(parser);
						manager.bulletmlUtil.addAction(node);
					} 
					else if (name.equals(Bulletml.TAG_BULLET))
					{
						manager.bulletmlUtil.addBullet(new Bullet(parser));
					} 
					else if (name.equals(Bulletml.TAG_FIRE))
					{
						manager.bulletmlUtil.addFire(new Fire(parser));
					}
				}
			}
		} 
		catch (XmlPullParserException e)
		{
			status = false;
		} 
		catch (IOException e)
		{
			status = false;
		}

		return status;
	}

}
