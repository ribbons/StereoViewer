/*
 * Copyright © 2017-2021 Matt Robinson
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

    private ImageView mImage;
    private ImageView mPrevious;
    private ImageView mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        // Set the screen to constant 100% brightness while images are shown
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        getWindow().setAttributes(params);

        // Set up the user interaction to manually show or hide the system UI.
        mImage.setOnClickListener((View view) -> toggle());
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

        mImage.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    private void show() {
        mImage.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BUTTON_R1:
                case KeyEvent.KEYCODE_BUTTON_R2:
                case KeyEvent.KEYCODE_BUTTON_A:
                    mWorker.moveNext();
                    return true;
                case KeyEvent.KEYCODE_BUTTON_L1:
                case KeyEvent.KEYCODE_BUTTON_L2:
                case KeyEvent.KEYCODE_BUTTON_B:
                    mWorker.movePrev();
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }
}
