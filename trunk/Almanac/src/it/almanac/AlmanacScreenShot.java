//Tutorial Screenshot
//Copyright (C) 2010 Daystrom
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package it.almanac;

import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class AlmanacScreenShot {

	public static void shot(Context context, View view, String appName) {

		try {

			Bitmap bm;
			// gets the bitmap from the view
			view.setDrawingCacheEnabled(true);
			bm = Bitmap.createBitmap(view.getDrawingCache());

			// sets files properties
			String filename = String.valueOf(System.currentTimeMillis());
			filename = appName + "_" + filename;
			ContentValues values = new ContentValues();
			values.put(Images.Media.TITLE, filename);
			values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
			values.put(Images.Media.MIME_TYPE, "image/jpeg");

			// gets image directory
			Uri uri = context.getContentResolver().insert(
					Images.Media.EXTERNAL_CONTENT_URI, values);

			// save image
			OutputStream outStream = context.getContentResolver()
					.openOutputStream(uri);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
			outStream.flush();
			outStream.close();

			CharSequence text = "Saving: " + filename;
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();

		} catch (Exception e) {
			CharSequence text = "Fatal error: " + e.getMessage();
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
		}

	}

}
