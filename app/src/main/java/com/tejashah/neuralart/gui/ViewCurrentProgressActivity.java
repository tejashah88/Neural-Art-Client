package com.tejashah.neuralart.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abysmel.dashspinner.DashSpinner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tejashah.neuralart.R;
import com.tejashah.neuralart.internal.SimpleDashSpinner;
import com.tejashah.neuralart.internal.SimpleValueEventListener;

public class ViewCurrentProgressActivity extends BaseActivity {
    private SimpleDashSpinner sdSpinner;
	private Button btnSeeResults;
	private TextView txtError;

	private DatabaseReference rootDBRef = FirebaseDatabase.getInstance().getReference();
	private SimpleValueEventListener progressValueListener = new SimpleValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			float newProgress = Float.valueOf(dataSnapshot.getValue().toString());
			sdSpinner.setProgress(newProgress);

			if (newProgress >= 1.0f) {
				sdSpinner.showSuccess();
				rootDBRef.removeEventListener(this);
			}
		}
	}, errorListener = new SimpleValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			if (!"none".equals(dataSnapshot.getValue().toString())) {
				txtError.setText(dataSnapshot.getValue().toString());
				sdSpinner.showFailure();
			}
		}
	};

	public void gotoViewResultActivity(View v) {
		gotoActivity(ViewResultActivity.class);
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_current_progress);

		txtError = (TextView) findViewById(R.id.txtError);
		txtError.setText("");

		sdSpinner = new SimpleDashSpinner((DashSpinner) findViewById(R.id.progressSpinner));
		sdSpinner.resetValues();
		sdSpinner.setProgress(0.0f);

		sdSpinner.setOnDownloadIntimationListener(new DashSpinner.OnDownloadIntimationListener() {
			@Override
			public void onDownloadIntimationDone(DashSpinner.DASH_MODE dashMode) {
				switch (dashMode) {
					case SUCCESS:
						rootDBRef.child("progress").removeEventListener(progressValueListener);
						rootDBRef.child("error").removeEventListener(errorListener);

						rootDBRef.child("progress").setValue(0.0f);

						btnSeeResults.setVisibility(View.VISIBLE);
						break;
				}
			}
		});

		btnSeeResults = (Button) findViewById(R.id.btnSeeResults);
		btnSeeResults.setVisibility(View.INVISIBLE);

		rootDBRef.child("progress").addValueEventListener(progressValueListener);
		rootDBRef.child("error").addValueEventListener(errorListener);
	}
}
