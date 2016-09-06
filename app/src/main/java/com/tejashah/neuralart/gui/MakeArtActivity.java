package com.tejashah.neuralart.gui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;
import com.canelmas.let.AskPermission;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tejashah.neuralart.R;
import com.tejashah.neuralart.internal.SeekBarProgressChangeListener;
import com.tejashah.neuralart.internal.TextChangeWatcher;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class MakeArtActivity extends BaseActivity {
	private ImageView imgContent, imgStyle;
	private Switch toggleTransferStyle;
	private EditText txtNumIter;
	private SeekBar sbarNumIter;
	private Button btnSendCommand;

	private File imgContentFile = null;
	private String styleImgName = null;

	int[] imageIDs = {
			R.drawable.style1,
			R.drawable.style2,
			R.drawable.style3,
			R.drawable.style4,
			R.drawable.style5,
			R.drawable.style6,
			R.drawable.style7,
			R.drawable.style8,
			R.drawable.style9,
			R.drawable.style10,
			R.drawable.style11,
			R.drawable.style12,
			R.drawable.style13,
			R.drawable.style14,
			R.drawable.style15,
			R.drawable.style16,
	};

	public class ImageAdapter extends BaseAdapter {
		private Context context;

		public ImageAdapter(Context c) {
			context = c;
		}

		public int getCount() {
			return imageIDs.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;

			if (convertView == null) {
				imageView = new ImageView(context);
				imageView.setLayoutParams(new GridView.LayoutParams((int) (getWindowManager().getDefaultDisplay().getWidth() / 2.3), getWindowManager().getDefaultDisplay().getHeight() / 4));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				//imageView.setPadding(10, 10, 10, 10);
				imageView.setPaddingRelative(10, 10, 10, 10);
			} else {
				imageView = (ImageView) convertView;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(getResources(), imageIDs[position], options);

			infoLog("image " + (position + 1) + ":" + options.outWidth + "," + options.outHeight);

			Picasso.with(MakeArtActivity.this)
					.load(imageIDs[position])
					.resize(options.outWidth / 4, options.outHeight / 4)
					.into(imageView);

			return imageView;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_art);

		imgContent = (ImageView) findViewById(R.id.imgContent);
		imgStyle = (ImageView) findViewById(R.id.imgStyle);
		toggleTransferStyle = (Switch) findViewById(R.id.toggleTransferStyle);

		txtNumIter = (EditText) findViewById(R.id.txtNumIter);
		sbarNumIter = (SeekBar) findViewById(R.id.sbarNumIters);

		txtNumIter.addTextChangedListener(new TextChangeWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (validInteger(s.toString())) {
					int lastPosition = txtNumIter.getSelectionStart();
					sbarNumIter.setProgress(Integer.parseInt(s.toString()));
					txtNumIter.setSelection(lastPosition);
				}
			}
		});

		setTextNumIter(1);

		sbarNumIter.setOnSeekBarChangeListener(new SeekBarProgressChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setTextNumIter(progress);
			}
		});

		btnSendCommand = (Button) findViewById(R.id.btnSendCommand);
		btnSendCommand.setVisibility(View.INVISIBLE);

		EasyImage.configuration(this).setImagesFolderName("NACameraCache").saveInRootPicturesDirectory();
	}

	public void setTextNumIter(int numIter) {
		numIter = boundToRange(numIter, 1, 1000);
		txtNumIter.setText(String.format(Locale.US, "%d", numIter));
	}

	public void checkValidPics() {
		btnSendCommand.setVisibility((styleImgName != null && imgContentFile != null) ? View.VISIBLE : View.INVISIBLE);
	}

	public void getContentPicture(View v) {
		new MaterialDialog.Builder(this)
				.title("Choose the method of retrieving the photo")
				.items(Arrays.asList("Camera", "Gallery"))
				.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
						if ("Camera".equals(text)) {
							launchCamera();
						} else if ("Gallery".equals(text)) {
							launchGallery();
						}

						return true;
					}
				})
				.show();
	}

	@AskPermission(Manifest.permission.CAMERA)
	public void launchCamera() {
		EasyImage.openCamera(MakeArtActivity.this, 88);
	}

	public void launchGallery() {
		EasyImage.openGallery(MakeArtActivity.this, 88);
	}

	public void getStylePicture(View v) {
		final GridView gridView = new GridView(this);

		gridView.setAdapter(new ImageAdapter(this));
		gridView.setNumColumns(2);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				shortToast("Style picture has been selected");

				styleImgName = "style" + (position + 1) + ".jpg";

				Picasso.with(MakeArtActivity.this)
						.load(imageIDs[position])
						.centerInside()
						.resize(getWindowManager().getDefaultDisplay().getWidth()*2, getWindowManager().getDefaultDisplay().getHeight()*2)
						.into(imgStyle);

				checkValidPics();
			}
		});

		new AlertDialog.Builder(this)
				.setTitle("Choose Style Picture")
				.setView(gridView)
				.show();
	}

	public void submitData(View v) {
		final MaterialDialog uploadImageLoadingDialog = new MaterialDialog.Builder(this)
				.title("Uploading content image...")
				.progress(false, 100, true)
				.build();

		//first, prepare to upload the image
		StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://neural-art-87142.appspot.com");

		uploadImageLoadingDialog.show();

		fileRef.child("content.jpg").putFile(Uri.fromFile(imgContentFile)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
				uploadImageLoadingDialog.setProgress((int) map(taskSnapshot.getBytesTransferred(), 0, taskSnapshot.getTotalByteCount(), 0, 100));
			}
		}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				uploadImageLoadingDialog.dismiss();

				//first, submit all related data to the real-time DB
				DatabaseReference rootDBRef = FirebaseDatabase.getInstance().getReference();
				rootDBRef.child("cmd").setValue(String.format(Locale.US,
						"th neural-style/neural_style.lua -content_image content.jpg -style_image styles/%s -gpu -0 -print_iter 1 -save_iter 0 -backend cudnn -cudnn_autotune -original_colors %d -num_iterations %d -proto_file neural-style/models/VGG_ILSVRC_19_layers_deploy.prototxt -model_file neural-style/models/VGG_ILSVRC_19_layers.caffemodel",
						styleImgName,
						toggleTransferStyle.isChecked() ? 1 : 0,
						Integer.parseInt(txtNumIter.getText().toString()))
				);

				gotoActivity(ViewCurrentProgressActivity.class);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
			@Override
			public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
				errorLog(e);
			}

			@Override
			public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
				//Handle the image
				imgContentFile = imageFile;

				Picasso.with(MakeArtActivity.this)
						.load(imageFile)
						.centerInside()
						.resize(getWindowManager().getDefaultDisplay().getWidth()*2, getWindowManager().getDefaultDisplay().getHeight()*2)
						.into(imgContent);

				checkValidPics();
			}

			@Override
			public void onCanceled(EasyImage.ImageSource source, int type) {
				//Cancel handling, you might wanna remove taken photo if it was canceled
				if (source == EasyImage.ImageSource.CAMERA) {
					File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MakeArtActivity.this);
					if (photoFile != null) photoFile.delete();
				}
			}
		});
	}
}
