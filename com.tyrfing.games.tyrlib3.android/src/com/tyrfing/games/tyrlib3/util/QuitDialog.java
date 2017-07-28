package com.tyrfing.games.tyrlib3.util;

import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.main.AndroidOpenGLActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class QuitDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Quit")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   final AndroidOpenGLActivity a = ((AndroidOpenGLActivity)getActivity());
                	   SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
							@Override
							public void run() {
								if (a != null) {
									a.close();
								}
							}
                	   });
                	   
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
