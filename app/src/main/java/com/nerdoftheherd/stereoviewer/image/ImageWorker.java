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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageWorker {
    private static final int MIDDLE_SPACING = 2;
    private static final int RENDER_AHEAD = 2;

    public interface SetImages {
        void setImage(Bitmap image);
    }

    private final List<Uri> imageUris;
    private final LinkedList<Integer> loadQueue;
    private final List<Bitmap> rendered;
    private final ContentResolver resolver;
    private final SetImages callback;
    private final Size size;

    private int displayIndex = 0;

    public ImageWorker(List<Uri> imageUris, Size size, ContentResolver resolver, SetImages callback) {
        this.imageUris = imageUris;
        this.loadQueue = new LinkedList<>();
        this.rendered = new ArrayList<>(Collections.nCopies(imageUris.size(), null));
        this.resolver = resolver;
        this.callback = callback;
        this.size = size;

        setImage();
    }

    public void movePrev() {
        synchronized (rendered) {
            if (displayIndex > 0) {
                displayIndex--;
                setImage();
            }
        }
    }

    public void moveNext() {
        synchronized (rendered) {
            if (displayIndex + 1 < imageUris.size()) {
                displayIndex++;
                setImage();
            }
        }
    }

    private void setImage()
    {
        synchronized (rendered) {
            Bitmap rendered = this.rendered.get(displayIndex);

            if (rendered != null) {
                callback.setImage(rendered);
            }

            for(int i = displayIndex; i <= displayIndex + RENDER_AHEAD; i++) {
                if (i == imageUris.size()) {
                    break;
                }

                if(this.rendered.get(i) == null) {
                    this.loadQueue.add(i);
                }
            }

            if(this.loadQueue.size() > 0) {
                new Thread(this::loadImages).start();
            }
        }
    }

    private void loadImages() {
        for (; ; ) {
            Integer loadIndex;

            synchronized (rendered) {
                loadIndex = this.loadQueue.pollFirst();

                if(loadIndex == null) {
                    break;
                }
            }

            SideBySideImage img;

            try {
                img = new SideBySideImage(imageUris.get(loadIndex), this.resolver);
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
                eyeHeight = (int) (((double) maxEyeWidth / srcWidth) * srcHeight);
                xSpacing = 0;
                ySpacing = (maxEyeHeight - eyeHeight) / 2;
            } else {
                eyeWidth = ((int) ((double) maxEyeHeight / srcHeight) * srcWidth);
                eyeHeight = maxEyeHeight;
                xSpacing = (maxEyeWidth - eyeWidth) / 2;
                ySpacing = 0;
            }

            int rightOffset = maxEyeWidth + MIDDLE_SPACING;

            Rect leftRect = new Rect(xSpacing, ySpacing, xSpacing + eyeWidth, ySpacing + eyeHeight);
            Rect rightRect = new Rect(rightOffset + xSpacing, ySpacing, rightOffset + xSpacing + eyeWidth, ySpacing + eyeHeight);

            canvas.drawBitmap(img.LeftImage(), null, leftRect, null);
            canvas.drawBitmap(img.RightImage(), null, rightRect, null);

            synchronized (this.rendered) {
                this.rendered.set(loadIndex, rendered);

                if(displayIndex == loadIndex) {
                    callback.setImage(rendered);
                }
            }
        }
    }
}
