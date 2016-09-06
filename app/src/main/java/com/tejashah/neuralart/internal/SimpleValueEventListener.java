package com.tejashah.neuralart.internal;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Tejas on 8/23/2016.
 */
public abstract class SimpleValueEventListener implements ValueEventListener {
	@Override
	public void onCancelled(DatabaseError databaseError) {
		FirebaseCrash.report(databaseError.toException());
	}
}
