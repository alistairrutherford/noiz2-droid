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


import java.io.IOException;

import jp.gr.java_conf.abagames.noiz2.BarrageCache;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.netthreads.android.noiz2.Eula.OnEulaAgreedTo;
import com.netthreads.android.noiz2.thread.UserTask;

/**
 * Main entry point for application.
 * 
 */
public class LaunchActivity extends Activity implements OnEulaAgreedTo
{
    private static final int GAME_ACTIVITY = Menu.FIRST + 1;
    private static final int SETTINGS_ACTIVITY = Menu.FIRST + 2;
    private static final int RESULTS_DIALOG = Menu.FIRST + 3;
    private static final int LOADING_DIALOG_KEY = Menu.FIRST + 4;

    // Loading
    private LoadAndLaunchTask task = null;
    private BarrageCache barrageCache = null;

    private DisplayMetrics displayMetrics = null;

    private ProgressDialog progressDialog = null;

    private int screenWidth = 0;
    private int screenHeight = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // ---------------------------------------------------------------
        // Initialise preferences
        // ---------------------------------------------------------------
        ApplicationPreferences.getInstance().init(this);
        
        // ---------------------------------------------------------------
        // View
        // ---------------------------------------------------------------
        setContentView(R.layout.main);

        TextView textView = (TextView) this.findViewById(R.id.app_version);

        String version = AboutActivity.getVersion(this);

        textView.setText(version);

        // ---------------------------------------------------------------
        // Buttons
        // ---------------------------------------------------------------
        
        Button runButton = (Button) findViewById(R.id.runGame);
        runButton.setOnClickListener(runGameListener);

        Button settingsButton = (Button) findViewById(R.id.runSetting);
        settingsButton.setOnClickListener(runSettingListener);
        
        Button helpButton = (Button) findViewById(R.id.runHelp);
        helpButton.setOnClickListener(runHelpListener);

        Button aboutButton = (Button) findViewById(R.id.runAbout);
        aboutButton.setOnClickListener(runAboutListener);
        
        // ---------------------------------------------------------------
        // Cache
        // ---------------------------------------------------------------
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        barrageCache = BarrageCache.instance(this);
    }

    /**
     * On start application we always attempt to show EULA.
     * 
     */
    @Override
    protected void onStart()
    {
        super.onStart();

        // Always attempt to show EULA.
        Eula.show(this);
    }

    /**
     * Responds to a click on the Run button by launching activity.
     * 
     */
    private View.OnClickListener runGameListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            task = new LoadAndLaunchTask();
            task.execute();
        }
    };

    /**
     * Responds to a click on the Run button by launching activity.
     * 
     */
    private View.OnClickListener runSettingListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            showSettings();
        }
    };

    /**
     * Responds to a click on the Run button by launching activity.
     * 
     */
    private View.OnClickListener runHelpListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            showHelp();
        }
    };

    /**
     * Responds to a click on the Run button by launching activity.
     * 
     */
    private View.OnClickListener runAboutListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            showAbout();
        }
    };
    
    /**
     * Create dialog with appropriate id.
     * 
     * @param The dialog id.
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
        case RESULTS_DIALOG:
        {
            String dummy = "No results yet.";
            CharSequence sequence = dummy.subSequence(0, dummy.length() - 1);

            AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.dialog_title).setPositiveButton(R.string.dialog_ok, null).setMessage(sequence).create();

            return alertDialog;
        }

        case LOADING_DIALOG_KEY:
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getString(R.string.loading_cache));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.setMax(barrageCache.getTotal());

            return progressDialog;
        }
        default:
            break;
        }

        return super.onCreateDialog(id);
    }

    /**
     * Replaces the dummy message in the test results dialog with a string that
     * describes the actual test results.
     * 
     */
    protected void onPrepareDialog(int id, Dialog dialog)
    {
        if (id == RESULTS_DIALOG)
        {
            // Extract final timing information from the profiler.
            final ProfileRecorder profiler = ProfileRecorder.sSingleton;
            final long frameTime = profiler.getAverageTime(ProfileRecorder.PROFILE_FRAME);
            final long frameMin = profiler.getMinTime(ProfileRecorder.PROFILE_FRAME);
            final long frameMax = profiler.getMaxTime(ProfileRecorder.PROFILE_FRAME);

            final long drawTime = profiler.getAverageTime(ProfileRecorder.PROFILE_DRAW);
            final long drawMin = profiler.getMinTime(ProfileRecorder.PROFILE_DRAW);
            final long drawMax = profiler.getMaxTime(ProfileRecorder.PROFILE_DRAW);

            final long simTime = profiler.getAverageTime(ProfileRecorder.PROFILE_SIM);
            final long simMin = profiler.getMinTime(ProfileRecorder.PROFILE_SIM);
            final long simMax = profiler.getMaxTime(ProfileRecorder.PROFILE_SIM);

            final float fps = frameTime > 0 ? 1000.0f / frameTime : 0.0f;

            String result = "Frame: " + frameTime + "ms (" + fps + " fps)\n" + "\t\tMin: " + frameMin + "ms\t\tMax: " + frameMax + "\n" + "Draw: " + drawTime + "ms\n" + "\t\tMin: " + drawMin + "ms\t\tMax: " + drawMax + "\n" +  "Sim: " + simTime + "ms\n" + "\t\tMin: " + simMin + "ms\t\tMax: " + simMax + "\n";
            
            CharSequence sequence = result.subSequence(0, result.length() - 1);
            AlertDialog alertDialog = (AlertDialog) dialog;
            alertDialog.setMessage(sequence);
        }
    }

    /** Shows the results dialog when the test activity closes. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        // If returning from game screen we check to see if the profiler dialog
        // should be shown
        if (requestCode == GAME_ACTIVITY)
        {
            if (ApplicationPreferences.getInstance().getShowProfile())
            {
                showDialog(RESULTS_DIALOG);
            }
        }
    }

    /**
     * Launch appropriate view based on renderer preferences.
     * 
     * 
     */
    private void launch()
    {
        Intent launchPreferencesIntent = null;

        if (ApplicationPreferences.getInstance().getRenderer())
        {
            launchPreferencesIntent = new Intent().setClass(this, Noiz2ActivityGL.class);
        }
        else
        {
            launchPreferencesIntent = new Intent().setClass(this, Noiz2ActivityCanvas.class);
        }

        // Make it a sub-activity so we know when it returns
        startActivityForResult(launchPreferencesIntent, GAME_ACTIVITY);
    }

    /**
     * Show loading dialog.
     * 
     */
    public void showLoading()
    {
        showDialog(LOADING_DIALOG_KEY);
    }

    /**
     * Hide loading dialog.
     * 
     */
    public void hideLoading()
    {
        try
        {
            dismissDialog(LOADING_DIALOG_KEY);
        }
        catch (Throwable t)
        {
            // Do Nothing
        }
    }

    /**
     * Show settings view
     * 
     */
    public void showSettings()
    {
        Intent launchPreferencesIntent = new Intent().setClass(this, PreferencesActivity.class);

        // Make it a sub-activity so we know when it returns
        startActivityForResult(launchPreferencesIntent, SETTINGS_ACTIVITY);
    }

    /**
     * Show help activity.
     * 
     */
    public void showHelp()
    {
        // Launch activity
        startActivity(new Intent(this, HelpActivity.class));
    }

    /**
     * Show about.
     * 
     */
    public void showAbout()
    {
        // Launch activity
        startActivity(new Intent(this, AboutActivity.class));
    }

    /**
     * Load data structures task.
     * 
     */
    public class LoadAndLaunchTask extends UserTask<Void, Integer, Void>
    {
        Integer progress = new Integer(0);

        @Override
        public void onPreExecute()
        {
            super.onPreExecute();

            showLoading();
        }

        /**
         * Do in background of UI thread.
         * 
         */
        @Override
        public Void doInBackground(Void... params)
        {
            if (!barrageCache.isLoaded())
            {
                boolean done = false;
                while (!done)
                {
                    try
                    {
                        done = barrageCache.loadFile(screenWidth, screenHeight);
                    }
                    catch (IOException e)
                    {
                        // Failed to load!
                        e.printStackTrace();
                        done = true;
                    }

                    progress++;

                    publishProgress(progress);
                }
            }

            return null;
        }

        /**
         * On progress tick.
         * 
         */
        public void onProgressUpdate(Integer... progress)
        {
            // Tick
            if (progressDialog != null)
            {
                progressDialog.setProgress(progress[0]);
            }
        }

        /**
         * Called when task complete.
         * 
         */
        @Override
        public void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            hideLoading();

            launch();
        }

    }

    /**
     * EULA Handler.
     * 
     */
    @Override
    public void onEulaAgreedTo()
    {
        // Nothing
    }

}