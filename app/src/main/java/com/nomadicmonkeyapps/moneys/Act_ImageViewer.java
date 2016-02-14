package com.nomadicmonkeyapps.moneys;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.ImageView;
import com.nomadicmonkeyapps.moneys.R;

public class Act_ImageViewer extends Activity {

	Bundle extras;
	private ImageView imageView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_imageviewer);

		extras = getIntent().getExtras();
		String photoFilePath = extras.getString("photopath");

		imageView = (ImageView) findViewById(R.id.act_imageviewer_imageview);
		
		Bitmap image = BitmapFactory.decodeFile(photoFilePath);

		try {
			ExifInterface exif = new ExifInterface(photoFilePath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
			} else if (orientation == 3) {
				matrix.postRotate(180);
			} else if (orientation == 8) {
				matrix.postRotate(270);
			}
			image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true); // rotating
																											// bitmap
		} catch (Exception e) {

		}
		
		imageView.setImageBitmap(image);

	}
}
