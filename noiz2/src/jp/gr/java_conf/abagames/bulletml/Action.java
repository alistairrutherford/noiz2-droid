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

import java.io.IOException;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Action xml data.
 * 
 */
public class Action implements IChoice
{
	private String label;
	private IChoice[] iac;

	XmlPullParser parser = null;

	public Action(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		this.parser = parser;

		boolean done = false;

		label = parser.getAttributeValue(null, Bulletml.ATTR_LABEL);

		Vector<IChoice> choices = new Vector<IChoice>();

		do
		{
			int type = parser.nextToken();

			if (type == XmlPullParser.END_TAG)
			{
				String endTag = parser.getName();

				if (endTag != null)
				{
					done = processEndTag(endTag, Bulletml.TAG_ACTION);
				}
			}
			else
				if (type == XmlPullParser.START_TAG)
				{
					String startTag = parser.getName();

					if (startTag != null)
					{
						processStartTag(parser, startTag, choices);
					}
				}
	            else
	            	if (type == XmlPullParser.END_DOCUMENT)
	            	{
	            		done = true;
	            	}
		}
		while (!done);

		int size = choices.size();
		iac = new IChoice[size];
		choices.copyInto(iac);
	}

	/**
	 * Process Start tag.
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void processStartTag(XmlPullParser parser, String tag, Vector<IChoice> choices) throws IOException,
			XmlPullParserException
	{
		if (tag.equals(Bulletml.TAG_REPEAT))
		{
			choices.addElement(new Repeat(parser));
		}
		else
			if (tag.equals(Bulletml.TAG_FIRE))
			{
				choices.addElement(new Fire(parser));
			}
			else
				if (tag.equals(Bulletml.TAG_FIRE_REF))
				{
					choices.addElement(new FireRef(parser));
				}
				else
					if (tag.equals(Bulletml.TAG_CHANGE_SPEED))
					{
						choices.addElement(new ChangeSpeed(parser));
					}
					else
						if (tag.equals(Bulletml.TAG_CHANGE_DIRECTION))
						{
							choices.addElement(new ChangeDirection(parser));
						}
						else
							if (tag.equals(Bulletml.TAG_ACCEL))
							{
								choices.addElement(new Accel(parser));
							}
							else
								if (tag.equals(Bulletml.TAG_WAIT))
								{
									choices.addElement(new Wait(parser));
								}
								else
									if (tag.equals(Bulletml.TAG_VANISH))
									{
										choices.addElement(new Vanish());
									}
									else
										if (tag.equals(Bulletml.TAG_ACTION))
										{
											choices.addElement(new Action(parser));
										}
										else
											if (tag.equals(Bulletml.TAG_ACTION_REF))
											{
												choices.addElement(new ActionRef(parser));
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

	public final IChoice[] getContent()
	{
		return iac;
	}
}
