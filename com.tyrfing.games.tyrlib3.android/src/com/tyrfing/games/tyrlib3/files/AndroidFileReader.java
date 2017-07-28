package com.tyrfing.games.tyrlib3.files;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.tyrfing.games.tyrlib3.files.FileReader;

import android.content.Context;

public class AndroidFileReader extends FileReader {
	
	/**
	 * Checks if a file exists.
	 * @param context 	The instance of the activity class.
	 * @param name		Name of the file to be read
	 * @return			True iff the file exists.
	 */
	
	public static boolean fileExists(Context context, String fileName)
	{
		try {
			FileInputStream fis = context.openFileInput(fileName);
			fis.close();
			return true;
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
