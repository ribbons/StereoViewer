/*
 * This file is part of Stereo Viewer.
 * Copyright © 2018 by the authors - see the AUTHORS file for details.
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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Size;

import java.io.FileNotFoundException;
import java.util.List;

public class ImageWorker {
    private static final int MIDDLE_SPACING = 2;

    public interface SetImages {
        void setImage(Bitmap image);
    }

    private List<Uri> imageUris;
    private ContentResolver resolver;
    private SetImages callback;
    private Size size;
    private int index = 0;

    public ImageWorker(List<Uri> imageUris, Size size, ContentResolver resolver, SetImages callback) {
        this.imageUris = imageUris;
        this.resolver = resolver;
        this.callback = callback;
        this.size = size;
        renderImage();
    }

    public void movePrev() {
        if (index > 0) {
            index--;
            renderImage();
        }
    }

    public void moveNext() {
        if (index + 1 < imageUris.size()) {
            index++;
            renderImage();
        }
    }

    private void renderImage() {
        SideBySideImage img;

        try {
            img = new SideBySideImage(imageUris.get(index), this.resolver);
        } catch (FileNotFoundException exp) {
            // Just crash the app for the time being
            throw new RuntimeException(exp);
        }

        Bitmap rendered = Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rendered);

        int srcWidth = img.LeftImage().getWidth();
        int srcHeight = img.LeftImage().getHeight();

        int maxEyeWidth = (size.getWidth() - MIDDLE_SPACING) / 2;
        int maxEyeHeight = size.getHeight();
        int eyeWidth, eyeHeight, xSpacing, ySpacing;

        if (srcWidth > srcHeight) {
            eyeWidth = maxEyeWidth;
            eyeHeight = (int)(((double)maxEyeWidth / srcWidth) * srcHeight);
            xSpacing = 0;
            ySpacing = (maxEyeHeight - eyeHeight) / 2;
        } else {
            eyeWidth = ((int)((double)maxEyeHeight / srcHeight) * srcWidth);
            eyeHeight = maxEyeHeight;
            xSpacing = (maxEyeWidth - eyeWidth) / 2;
            ySpacing = 0;
        }

        int rightOffset = maxEyeWidth + MIDDLE_SPACING;

        Rect leftRect = new Rect(xSpacing, ySpacing, xSpacing + eyeWidth, ySpacing + eyeHeight);
        Rect rightRect = new Rect(rightOffset + xSpacing, ySpacing, rightOffset + xSpacing + eyeWidth, ySpacing + eyeHeight);

        canvas.drawBitmap(img.LeftImage(), null, leftRect, null);
        canvas.drawBitmap(img.RightImage(), null, rightRect, null);

        callback.setImage(rendered);
    }
}
