package com.dnashot.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.dnareader.v0.R;
import com.dnashot.system.ResultManager;

public class DeleteResultDialogFragment extends DialogFragment {
	private Activity activity;
	private long resultId;
	private int resultPosition;
	private boolean close;
	
	public DeleteResultDialogFragment(Activity activity, long id, int position, boolean close) {
		this.activity = activity;
		this.resultId = id;
		this.resultPosition = position;
		this.close = close;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.delete_result_title)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								if (MainActivity.thread != null)
									MainActivity.thread.interrupt();
								ResultManager.deleteResult(activity, resultId);
								MainActivity.listResults.remove(resultPosition);
								if(close) activity.finish();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								// User cancelled the dialog
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}