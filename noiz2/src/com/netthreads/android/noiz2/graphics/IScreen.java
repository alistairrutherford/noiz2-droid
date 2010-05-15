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

package com.netthreads.android.noiz2.graphics;

public interface IScreen
{
	public void clear();
	public void drawLine(int x1, int y1, int x2, int y2, int color);
	public void drawThickLine(int x1, int y1, int x2, int y2, int color1, int color2);
	public void drawDot(int x1, int y1, int color);
	public void drawBitmap(int id, float left, float top);
}
