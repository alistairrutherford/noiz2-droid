/**
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netthreads.android.noiz2.graphics.canvas;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.netthreads.android.noiz2.ProfileRecorder;

/**
 * Implements a surface view which writes updates to the surface's canvas using
 * a separate rendering thread. This class is based heavily on GLSurfaceView.
 */
public class CanvasSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private boolean mSizeChanged = true;

    private SurfaceHolder mHolder;
    private CanvasThread mCanvasThread;

    private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();

    public CanvasSurfaceView(Context context)
    {
        super(context);
        init();
    }

    public CanvasSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    public SurfaceHolder getSurfaceHolder()
    {
        return mHolder;
    }

    /** Sets the user's renderer and kicks off the rendering thread. */
    public void setRenderer(Renderer renderer)
    {
        mCanvasThread = new CanvasThread(mHolder, renderer);
        mCanvasThread.start();
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        mCanvasThread.surfaceCreated();
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // Surface will be destroyed when we return
        mCanvasThread.surfaceDestroyed();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        // Surface size or format has changed. This should not happen in this
        // example.
        mCanvasThread.onWindowResize(w, h);
    }

    /** Inform the view that the activity is paused. */
    public void onPause()
    {
        mCanvasThread.onPause();
    }

    /** Inform the view that the activity is resumed. */
    public void onResume()
    {
        mCanvasThread.onResume();
    }

    /** Inform the view that the window focus has changed. */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mCanvasThread.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mCanvasThread.requestExitAndWait();
    }

    protected void stopDrawing()
    {
        mCanvasThread.requestExitAndWait();
    }

    /**
     * Queue an "event" to be run on the GL rendering thread.
     * 
     * @param r
     *            the runnable to be run on the GL rendering thread.
     */
    public void queueEvent(Runnable r)
    {
        synchronized (this)
        {
            mEventQueue.add(r);
        }
    }

    private Runnable getEvent()
    {
        synchronized (this)
        {
            if (mEventQueue.size() > 0)
            {
                return mEventQueue.remove(0);
            }
        }
        return null;
    }

    // ----------------------------------------------------------------------

    /** A generic renderer interface. */
    public interface Renderer
    {

        /**
         * Surface changed size. Called after the surface is created and
         * whenever the surface size changes. Set your viewport here.
         * 
         * @param width
         * @param height
         */
        void sizeChanged(int width, int height);

        /**
         * Draw the current frame.
         * 
         * @param canvas
         *            The target canvas to draw into.
         */
        void drawFrame(Canvas canvas);
    }

    /**
     * A generic Canvas rendering Thread. Delegates to a Renderer instance to do
     * the actual drawing.
     */
    class CanvasThread extends Thread
    {
        private boolean mDone;
        private boolean mPaused;
        private boolean mHasFocus;
        private boolean mHasSurface;
        private boolean mContextLost;
        private int mWidth;
        private int mHeight;
        private Renderer mRenderer;
        private SurfaceHolder mSurfaceHolder;

        private final ProfileRecorder profiler = ProfileRecorder.sSingleton;
        
        CanvasThread(SurfaceHolder holder, Renderer renderer)
        {
            super();
            mDone = false;
            mWidth = 0;
            mHeight = 0;
            mRenderer = renderer;
            mSurfaceHolder = holder;
            setName("CanvasThread");
        }

        @Override
        public void run()
        {

            boolean tellRendererSurfaceChanged = true;

            /*
             * This is our main activity thread's loop, we go until asked to
             * quit.
             */
            while (!mDone)
            {
                profiler.start(ProfileRecorder.PROFILE_FRAME);
                
                /*
                 * Update the asynchronous state (window size)
                 */
                int w;
                int h;
                synchronized (this)
                {

                    Runnable r;
                    while ((r = getEvent()) != null)
                    {
                        r.run();
                    }

                    if (needToWait())
                    {
                        while (needToWait())
                        {
                            try
                            {
                                wait();
                            } catch (InterruptedException e)
                            {

                            }
                        }
                    }
                    if (mDone)
                    {
                        break;
                    }
                    tellRendererSurfaceChanged = mSizeChanged;
                    w = mWidth;
                    h = mHeight;
                    mSizeChanged = false;
                }

                if (tellRendererSurfaceChanged)
                {
                    mRenderer.sizeChanged(w, h);
                    tellRendererSurfaceChanged = false;
                }

                if ((w > 0) && (h > 0))
                {
                    // Get ready to draw.
                    // We record both lockCanvas() and unlockCanvasAndPost()
                    // as part of "page flip" time because either may block
                    // until the previous frame is complete.
                    Canvas canvas = mSurfaceHolder.lockCanvas();
                    if (canvas != null)
                    {
                        // Draw a frame!
                        mRenderer.drawFrame(canvas);

                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                profiler.stop(ProfileRecorder.PROFILE_FRAME);
                profiler.endFrame();
            }
        }

        private boolean needToWait()
        {
            return (mPaused || (!mHasFocus) || (!mHasSurface) || mContextLost) && (!mDone);
        }

        public void surfaceCreated()
        {
            synchronized (this)
            {
                mHasSurface = true;
                mContextLost = false;
                notify();
            }
        }

        public void surfaceDestroyed()
        {
            synchronized (this)
            {
                mHasSurface = false;
                notify();
            }
        }

        public void onPause()
        {
            synchronized (this)
            {
                mPaused = true;
            }
        }

        public void onResume()
        {
            synchronized (this)
            {
                mPaused = false;
                notify();
            }
        }

        public void onWindowFocusChanged(boolean hasFocus)
        {
            synchronized (this)
            {
                mHasFocus = hasFocus;
                if (mHasFocus == true)
                {
                    notify();
                }
            }
        }

        public void onWindowResize(int w, int h)
        {
            synchronized (this)
            {
                mWidth = w;
                mHeight = h;
                mSizeChanged = true;
            }
        }

        public void requestExitAndWait()
        {
            // don't call this from CanvasThread thread or it is a guaranteed
            // deadlock!
            synchronized (this)
            {
                mDone = true;
                notify();
            }
            try
            {
                join();
            } catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

}
