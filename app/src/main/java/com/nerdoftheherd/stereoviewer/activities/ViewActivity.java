/*
 * This file is part of Stereo Viewer.
 * Copyright © 2017 by the authors - see the AUTHORS file for details.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nerdoftheherd.stereoviewer.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;

import com.nerdoftheherd.stereoviewer.R;
import com.nerdoftheherd.stereoviewer.image.SideBySideImage;

import java.io.FileNotFoundException;

public class ViewActivity extends Activity {
    public static final String INTENT_DATA_IMAGEURI = "imageuri";

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private boolean mVisible;

    private View mContentView;
    private ImageView mLeftImage;
    private ImageView mRightImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        mLeftImage = (ImageView)findViewById(R.id.left_image);
        mRightImage = (ImageView)findViewById(R.id.right_image);

        Uri fileUri = getIntent().getParcelableExtra(INTENT_DATA_IMAGEURI);
        SideBySideImage img;

        try {
            img = new SideBySideImage(fileUri, this.getContentResolver());
        }
        catch(FileNotFoundException exp) {
            // Just crash the app for the time being
            throw new RuntimeException(exp);
        }

        mLeftImage.setImageBitmap(img.LeftImage());
        mRightImage.setImageBitmap(img.RightImage());

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        mHideHandler.postDelayed(mHideRunnable, 100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            Intent upIntent = this.getParentActivityIntent();
            this.navigateUpTo(upIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void show() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }
}
