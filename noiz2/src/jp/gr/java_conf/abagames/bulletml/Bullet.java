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

import java.io.*;
import java.util.*;

/**
 * Bullet xml data.
 * 
 */
public class Bullet implements IChoice
{
	private String label;
	private Direction direction;
	private Speed speed;
	private IChoice[] iac;

	XmlPullParser parser = null;

	public Bullet(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		this.parser = parser;

		boolean done = false;

		label = parser.getAttributeValue(null, Bulletml.ATTR_LABEL);

		Vector<IChoice> actions = new Vector<IChoice>();

		do
		{
			int type = parser.nextToken();

			if (type == XmlPullParser.END_TAG)
			{
				String endTag = parser.getName();

				if (endTag != null)
				{
					done = processEndTag(endTag, Bulletml.TAG_BULLET);
				}
			}
			else
				if (type == XmlPullParser.START_TAG)
				{
					String startTag = parser.getName();

					if (startTag != null)
					{
						processStartTag(parser, startTag, actions);
					}
				}
	            else
	            	if (type == XmlPullParser.END_DOCUMENT)
	            	{
	            		done = true;
	            	}

		}
		while (!done);

		iac = new IChoice[actions.size()];
		actions.copyInto(iac);
	}

	/**
	 * Process Start tag.
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void processStartTag(XmlPullParser parser, String tag, Vector<IChoice> actions) throws IOException, XmlPullParserException
	{
		if (tag.equals(Bulletml.TAG_DIRECTION))
		{
			direction = new Direction(parser);
		}
		else
			if (tag.equals(Bulletml.TAG_SPEED))
			{
				speed = new Speed(parser);
			}
			else
				if (tag.equals(Bulletml.TAG_ACTION))
				{
					actions.addElement(new Action(parser));
				}
				else
					if (tag.equals(Bulletml.TAG_ACTION_REF))
					{
						actions.addElement(new ActionRef(parser));
					}
	}

	/**
	 * Process end tag.
	 * 
	 * @param tag
	 * 
	 * @return True indicates end processing.
	 */
	private boolean processEndTag(String tag, String endTag)
	{
		boolean status = false;

		if (tag.equals(endTag))
		{
			status = true;
		}

		return status;
	}

	public final String getLabel()
	{
		return label;
	}

	public final Direction getDirection()
	{
		return direction;
	}

	public final Speed getSpeed()
	{
		return speed;
	}

	public final IChoice[] getActionElm()
	{
		return iac;
	}
}
