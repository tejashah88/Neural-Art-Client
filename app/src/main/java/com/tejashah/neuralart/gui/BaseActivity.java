package com.tejashah.neuralart.gui;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canelmas.let.DeniedPermission;
import com.canelmas.let.Let;
import com.canelmas.let.RuntimePermissionListener;
import com.canelmas.let.RuntimePermissionRequest;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Created by Tejas on 8/19/2016.
 *
 * The base activity class in which all other Activities created should extend from this one. This class has a bunch of utility methods and preset bindings to certain libraries.
 */
public class BaseActivity extends AppCompatActivity implements RuntimePermissionListener {
	public void reportError(Throwable err) {
        FirebaseCrash.report(err);
    }

	public void shortToast(Object obj) {
		Toast.makeText(this, obj.toString(), Toast.LENGTH_SHORT).show();
	}

	public void longToast(Object obj) {
		Toast.makeText(this, obj.toString(), Toast.LENGTH_LONG).show();
	}

	public void verboseLog(Object obj) {
		Log.v("NEURAL_ART", obj.toString());
	}

	public void debugLog(Object obj) {
		Log.d("NEURAL_ART", obj.toString());
	}

	public void infoLog(Object obj) {
		Log.i("NEURAL_ART", obj.toString());
	}

	public void warnLog(Object obj) {
		Log.w("NEURAL_ART", obj.toString());
	}

	public void errorLog(Object obj) {
		Log.e("NEURAL_ART", obj.toString());
	}

	public void assertLog(Object obj) {
		Log.wtf("NEURAL_ART", obj.toString());
	}

	public void killSelf() {
		this.finishAffinity();
	}

	public void gotoActivity(Class actClass) {
		startActivity(new Intent(this, actClass));
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		Let.handle(this, requestCode, permissions, grantResults);
	}

	@Override
	public void onShowPermissionRationale(List<String> permissions, final RuntimePermissionRequest request) {
		String content;

		if (permissions.get(0).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			content = "We need to be able to save your beautiful work on your SD card.";
		} else if (permissions.get(0).equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
			content = "We need to be able to read from your SD card in order to access your beautiful work.";
		} else if (permissions.get(0).equals(Manifest.permission.CAMERA)) {
			content = "We need to have access to your camera in order to allow you direct access to take pictures within this app.";
		} else {
			content = "Unhandled permission " + permissions.get(0);
			errorLog(content);
		}

		new MaterialDialog.Builder(this)
				.title("This app needs granted permission(s).")
				.content(content)
				.neutralText("OK")
				.onNeutral(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						dialog.dismiss();
						request.retry();
					}
				})
				.show();
	}

	@Override
	public void onPermissionDenied(List<DeniedPermission> deniedPermissionList) {
		new MaterialDialog.Builder(this)
				.title("Unable to run app!")
				.content("This app cannot function until you grant it the needed permission(s).")
				.neutralText("OK")
				.onNeutral(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						dialog.dismiss();
						killSelf();
					}
				})
				.show();
	}

	public void copyFile(File src, File dst) throws IOException {
		FileInputStream inStream = new FileInputStream(src);
		FileOutputStream outStream = new FileOutputStream(dst);
		FileChannel inChannel = inStream.getChannel();
		FileChannel outChannel = outStream.getChannel();
		inChannel.transferTo(0, inChannel.size(), outChannel);
		inStream.close();
		outStream.close();
	}

	public File getAlbumStorageDir(String albumName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
		if (!file.mkdirs() && !file.exists()) {
			errorLog("Directory not created");
		}
		return file;
	}

	public long map(long x, long in_min, long in_max, long out_min, long out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public int boundToRange(int x, int min, int max) {
		if (x < min) x = min;
		if (x > max) x = max;
		return x;
	}

	public boolean validInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
