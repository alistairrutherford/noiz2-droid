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
package com.netthreads.android.noiz2.data;

import com.netthreads.android.noiz2.graphics.opengl.Grid;


public class SpriteTexture
{
    private int resourceId = -1;
    private int[] textureIds = null; // public to avoid indirect access call in draw
    private int width = 0;
    private int height = 0;
    private int rows = 0;
    private int cols = 0;
    private int xOffset = 0;
    private int yOffset = 0;
    private boolean loaded = false;
    private Grid grid = null;

    public SpriteTexture(int resourceId)
    {
        this.resourceId = resourceId;

        this.cols = 1;
        this.rows = 1;
    }

    public SpriteTexture(int resourceId, int rows, int cols, int width, int height)
    {
        this.resourceId = resourceId;

        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded;
    }

    public int[] getTextureIds()
    {
        return textureIds;
    }

    public void setTextureIds(int[] textureIds)
    {
        this.textureIds = textureIds;
    }

    public int getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(int resourceId)
    {
        this.resourceId = resourceId;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getRows()
    {
        return rows;
    }

    public void setRows(int rows)
    {
        this.rows = rows;
    }

    public int getCols()
    {
        return cols;
    }

    public void setCols(int cols)
    {
        this.cols = cols;
    }

    public int getXOffset()
    {
        return xOffset;
    }

    public void setXOffset(int offset)
    {
        xOffset = offset;
    }

    public int getYOffset()
    {
        return yOffset;
    }

    public void setYOffset(int offset)
    {
        yOffset = offset;
    }

    public Grid getGrid()
    {
        return grid;
    }

    public void setGrid(Grid grid)
    {
        this.grid = grid;
    }
}
