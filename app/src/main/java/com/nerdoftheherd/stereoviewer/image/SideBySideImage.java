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
