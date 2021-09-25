/*
 * Copyright © 2017 Matt Robinson
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.nerdoftheherd.stereoviewer.image;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SideBySideImage extends StereoImage {
    private Bitmap mLeftImage;
    private Bitmap mRightImage;

    public SideBySideImage(Uri sourceUri, ContentResolver resolver) throws FileNotFoundException {
        InputStream inStream = resolver.openInputStream(sourceUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inStream);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        mLeftImage = Bitmap.createBitmap(bitmap, 0, 0, width / 2, height);
        mRightImage = Bitmap.createBitmap(bitmap, (width / 2) - 1, 0, width / 2, height);
    }

    @Override
    public Bitmap LeftImage() {
        return this.mLeftImage;
    }

    @Override
    public Bitmap RightImage() {
        return this.mRightImage;
    }
}
