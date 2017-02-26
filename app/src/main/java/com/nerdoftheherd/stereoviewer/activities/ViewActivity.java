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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nerdoftheherd.stereoviewer.R;
import com.nerdoftheherd.stereoviewer.image.SideBySideImage;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

public class ViewActivity extends Activity {
    public static final String INTENT_DATA_IMAGEURI = "imageuri";
    public static final String INTENT_DATA_IMAGEURIS = "imageuris";

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private boolean mVisible = true;
    private int mImageIndex = 0;
    private List<Uri> mImageUris;

    private View mContentView;
    private ImageView mLeftImage;
    private ImageView mRightImage;
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
        mLeftImage = (ImageView)findViewById(R.id.left_image);
        mRightImage = (ImageView)findViewById(R.id.right_image);
        mPrevious = (ImageView)findViewById(R.id.showPrevious);
        mNext = (ImageView)findViewById(R.id.showNext);

        mImageUris = getIntent().getParcelableArrayListExtra(INTENT_DATA_IMAGEURIS);

        if(mImageUris == null) {
            mImageUris = Collections.singletonList((Uri)getIntent().getParcelableExtra(INTENT_DATA_IMAGEURI));
        }

        loadImage(mImageUris.get(mImageIndex));

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
        if(mImageIndex + 1 < mImageUris.size())
        {
            loadImage(mImageUris.get(++mImageIndex));
        }
    }

    public void showPrevious(View view) {
        if(mImageIndex > 0)
        {
            loadImage(mImageUris.get(--mImageIndex));
        }
    }

    private void loadImage(Uri uri) {
        SideBySideImage img;

        try {
            img = new SideBySideImage(uri, this.getContentResolver());
        }
        catch(FileNotFoundException exp) {
            // Just crash the app for the time being
            throw new RuntimeException(exp);
        }

        mLeftImage.setImageBitmap(img.LeftImage());
        mRightImage.setImageBitmap(img.RightImage());
    }
}
