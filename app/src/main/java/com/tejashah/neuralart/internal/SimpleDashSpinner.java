package com.tejashah.neuralart.internal;

import com.abysmel.dashspinner.DashSpinner;

/**
 * Created by Tejas on 8/25/2016
 */
public class SimpleDashSpinner {
	private float progress = 0.0f;
	private DashSpinner dspinner;

	public SimpleDashSpinner(DashSpinner dashSpinner) {
		dspinner = dashSpinner;
	}

	public void setOnDownloadIntimationListener(DashSpinner.OnDownloadIntimationListener listener) {
		dspinner.setOnDownloadIntimationListener(listener);
	}

	public void resetValues() {
		dspinner.resetValues();
	}

	private void updateProgess() {
		dspinner.setProgress(progress);
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float value) {
		progress = value;
		updateProgess();
	}

	public void addProgress(float value) {
		setProgress(getProgress() + value);
	}

	public void subtractProgress(float value) {
		setProgress(getProgress() - value);
	}

	public void showSuccess() {
		dspinner.showSuccess();
	}

	public void showFailure() {
		dspinner.showFailure();
	}

	public void showUnknown() {
		dspinner.showUnknown();
	}
}
