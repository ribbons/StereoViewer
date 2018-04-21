/*
 * This file is part of Stereo Viewer.
 * Copyright © 2017-2018 by the authors - see the AUTHORS file for details.
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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nerdoftheherd.stereoviewer.R;
import com.nerdoftheherd.stereoviewer.image.ImageWorker;

import java.util.Collections;
import java.util.List;

public class ViewActivity extends Activity {
    public static final String INTENT_DATA_IMAGEURI = "imageuri";
    public static final String INTENT_DATA_IMAGEURIS = "imageuris";

    private final Handler mHideHandler = new Handler();

    private boolean mVisible = true;
    private ImageWorker mWorker;

    private View mContentView;
    private ImageView mImage;
    private ImageView mPrevious;
    private ImageView mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContentView = findViewById(R.id.fullscreen_content);
        mImage = (ImageView)findViewById(R.id.image);
        mPrevious = (ImageView)findViewById(R.id.showPrevious);
        mNext = (ImageView)findViewById(R.id.showNext);

        List<Uri> imageUris = getIntent().getParcelableArrayListExtra(INTENT_DATA_IMAGEURIS);

        if(imageUris == null) {
            imageUris = Collections.singletonList((Uri)getIntent().getParcelableExtra(INTENT_DATA_IMAGEURI));
        }

        this.mWorker = new ImageWorker(
                imageUris,
                screenSize(),
                this.getContentResolver(),
                (Bitmap image) -> mImage.post(() -> mImage.setImageBitmap(image))
        );

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener((View view) -> toggle());
    }

    private Size screenSize()
    {
        Point pointSize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(pointSize);
        return new Size(pointSize.x, pointSize.y);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        mHideHandler.postDelayed(this::hide, 100);
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
        mPrevious.setAlpha(0f);
        mNext.setAlpha(0f);

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
        mPrevious.setAlpha(1f);
        mNext.setAlpha(1f);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    public void showNext(View view) {
        mWorker.moveNext();
    }

    public void showPrevious(View view) {
        mWorker.movePrev();
    }
}
