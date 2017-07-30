package com.tyrfing.games.id17;

import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class QuitDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final boolean host = EmpireFrameListener.MAIN_FRAME.getNetwork().isHost();
        builder.setMessage(host ? "Quit & Save" : "Quit")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   final EmpireActivity a = ((EmpireActivity)getActivity());
                	   SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
							@Override
							public void run() {
								if (host) {
									World.getInstance().saveAs("save");
								}
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
