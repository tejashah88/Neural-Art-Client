package com.tejashah.neuralart.internal;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Tejas on 8/25/2016
 */
public abstract class TextChangeWatcher implements TextWatcher {
	@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	@Override public void afterTextChanged(Editable s) {}
}
