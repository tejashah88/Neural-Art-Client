package com.tejashah.neuralart.gui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tejashah.neuralart.R;

public class MainMenuActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	public void gotoMakeArtActivity(View v) {
		gotoActivity(MakeArtActivity.class);
	}

	public void workInProgress(View v) {
		new MaterialDialog.Builder(this)
				.title("Work In Progress!")
				.content("This function is still under development.")
				.neutralText("OK")
				.onNeutral(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						dialog.dismiss();
					}
				})
				.show();
	}


}