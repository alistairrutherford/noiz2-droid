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

/**
 * ChangeDirection xml data.
 * 
 */
public class ChangeDirection implements IChoice
{
	private Direction direction;
	private String term;
	private boolean inTerm = false;

	XmlPullParser parser = null;
	
	public ChangeDirection(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		this.parser = parser;
		
		boolean done= false;
		
		do
		{
			int type = parser.nextToken();
			
            if (type == XmlPullParser.TEXT)
            {
                String text = parser.getText();

                if (text != null)
                {
                    processText(text);
                }
            }
            else if (type == XmlPullParser.END_TAG)
            {
                String endTag = parser.getName();

                if (endTag != null)
                {
                    done = processEndTag(endTag, Bulletml.TAG_CHANGE_DIRECTION);
                }
            }
            else if (type == XmlPullParser.START_TAG)
            {
                String startTag = parser.getName();

                if (startTag != null)
                {
                    processStartTag(parser, startTag);
                }
            }
            else
            	if (type == XmlPullParser.END_DOCUMENT)
            	{
            		done = true;
            	}

		} while (!done);

	}

    /**
     * Process Start tag.
     * 
     * @param tag
     * @throws IOException 
     * @throws XmlPullParserException 
     */
    private void processStartTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException
    {
		if (tag.equals(Bulletml.TAG_DIRECTION))
		{
			direction = new Direction(parser);
		}
		else
			if (tag.equals(Bulletml.TAG_TERM))
			{
				inTerm = true;
			}
    }
    
    /**
     * Process text.
     *
     * @param text
     */
    private void processText(String text)
    {
    	if (inTerm)
    	{
    		term = parser.getText();
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
		else
			if (tag.equals(Bulletml.TAG_TERM))
			{
				inTerm = false;
			}
        
        return status;
    }
    
	
	public final Direction getDirection()
	{
		return direction;
	}

	public final String getTerm()
	{
		return term;
	}
}
