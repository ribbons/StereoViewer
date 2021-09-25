/*
 * Copyright © 2017 Matt Robinson
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.nerdoftheherd.stereoviewer.image;

import android.graphics.Bitmap;

public abstract class StereoImage {
    public abstract Bitmap LeftImage();
    public abstract Bitmap RightImage();
}
