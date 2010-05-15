/**
 * Copyright (C) 2009 Alistair Rutherford, Glasgow, Scotland, UK, www.netthreads.co.uk
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.netthreads.android.noiz2;

import android.app.Activity;

import android.os.Bundle;

import android.widget.TextView;


/**
 * About view with version number.
 *
 */
public class HelpActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help);

        // Load text view with formatted text.
        TextView textView = (TextView)this.findViewById(R.id.help_text_view);

        textView.setText(R.string.help_text);
        
        textView.setTextSize(getResources().getDimension(R.dimen.help_text_font_size));
    }
    
}
