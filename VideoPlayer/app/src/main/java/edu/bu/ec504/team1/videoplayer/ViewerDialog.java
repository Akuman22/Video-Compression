package edu.bu.ec504.team1.videoplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import static edu.bu.ec504.team1.videoplayer.VideoViewerActivity.effectsID;

/**
 * Created by heqifan on 11/10/17.
 */

public class ViewerDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Effects")
                .setItems(R.array.effects_names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Effect: ", "" + which);
                        // The 'which' argument contains the index position
                        // of the selected item
                        effectsID = which;
                    }
                });
        return builder.create();
    }
}