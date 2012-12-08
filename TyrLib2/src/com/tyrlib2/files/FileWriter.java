package com.tyrlib2.files;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

/**
 * Writes content to a file. The file is associated with a context,
 * usually an activity.
 * @author Sascha
 *
 */

public class FileWriter {
	
	/**
	 * Writes a string representation of the data into the file
	 * @param context	The instance of the activity class.
	 * @param name		The name of the file.
	 * @param value		The content to be written into the file.
	 */
	
	public static void writeFile(Context context, String name, String value)
	{	
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(name, Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
