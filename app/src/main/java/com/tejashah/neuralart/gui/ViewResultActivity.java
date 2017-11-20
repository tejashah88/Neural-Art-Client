package com.tejashah.neuralart.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tejashah.neuralart.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewResultActivity extends BaseActivity {
	private String resultFilename;
	private ImageView imgResult;
	private File resultFileInternal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_result);

		resultFilename = "result_" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + ".png";

		imgResult = (ImageView) findViewById(R.id.imgResult);
		resultFileInternal = new File(getFilesDir(), resultFilename);

		final MaterialDialog downloadImageLoadingDialog = new MaterialDialog.Builder(this)
				.title("Downloading result image...")
				.progress(false, 100, true)
				.build();

		//next, prepare to upload the images
		StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://neural-art-87142.appspot.com");

		downloadImageLoadingDialog.show();

		fileRef.child("result.png").getFile(Uri.fromFile(resultFileInternal)).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
			@Override
			public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
				downloadImageLoadingDialog.setProgress((int) map(taskSnapshot.getBytesTransferred(), 0, taskSnapshot.getTotalByteCount(), 0, 100));
			}
		}).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
				displayImage();
				downloadImageLoadingDialog.dismiss();
			}
		});
	}

	public void saveImage(View v) throws RuntimeException {
		if (isExternalStorageWritable()) {
			File resultFileExternal = new File(getAlbumStorageDir("NeuralArt").getAbsolutePath() + "/" + resultFilename);

			try {
				copyFile(resultFileInternal, resultFileExternal);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(resultFileExternal)));

			shortToast("Artwork sucessfully saved!");
		}
	}

	public void displayImage() {
		Picasso.with(ViewResultActivity.this)
				.load(resultFileInternal)
				.centerInside()
				.resize(getWindowManager().getDefaultDisplay().getWidth()*2, getWindowManager().getDefaultDisplay().getHeight()*2)
				.into(imgResult);
	}

	private void saveRotatedImage(int rotation) {
		try {
			Bitmap bm = BitmapFactory.decodeFile(resultFileInternal.getAbsolutePath());

			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);
			Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

			FileOutputStream fos = new FileOutputStream(resultFileInternal);
			rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void rotateImageLeft(View v) {
		/*saveRotatedImage(-90);
		displayImage();
		infoLog("rotated left!");*/
		infoLog("Feature unavailable!");
	}

	public void rotateImageRight(View v) {
		/*saveRotatedImage(90);
		displayImage();
		infoLog("rotated right!");*/
		infoLog("Feature unavailable!");
	}

	public void gotoMainMenu(View v) {
		resultFileInternal.delete();
		gotoActivity(MainMenuActivity.class);
	}
}