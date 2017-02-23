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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.nerdoftheherd.stereoviewer.R;

public class MainActivity extends Activity {
    EditText mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFilePath = (EditText) findViewById(R.id.file_path);
    }

    public void viewImage(View view) {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra(ViewActivity.INTENT_FILENAME, mFilePath.getText().toString());
        startActivity(intent);
    }
}
