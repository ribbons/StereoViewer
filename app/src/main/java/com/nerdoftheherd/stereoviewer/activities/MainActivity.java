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
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;

import com.nerdoftheherd.stereoviewer.R;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int DOCUMENT_PICKED = 1;
    private static final int DOCUMENT_TREE_PICKED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void selectFolder(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, DOCUMENT_TREE_PICKED);
    }

    public void selectFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, DOCUMENT_PICKED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DOCUMENT_PICKED && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, ViewActivity.class);
            intent.putExtra(ViewActivity.INTENT_DATA_IMAGEURI, data.getData());
            startActivity(intent);
        }
        else if (requestCode == DOCUMENT_TREE_PICKED && resultCode == RESULT_OK) {
            findChildren(data.getData());
        }
    }

    private void findChildren(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));

        ArrayList<Uri> uris = new ArrayList<Uri>();

        try (Cursor childCursor = contentResolver.query(
                childrenUri,
                new String[]{
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_MIME_TYPE
                },
                null, null, null)) {
            while (childCursor.moveToNext()) {
                if(childCursor.getString(1).equals("image/jpeg")) {
                    Uri childUri = DocumentsContract.buildDocumentUriUsingTree(uri, childCursor.getString(0));
                    uris.add(childUri);
                }
            }
        }

        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra(ViewActivity.INTENT_DATA_IMAGEURIS, uris);
        startActivity(intent);
    }
}
