package com.tyrlib2.files;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.tyrlib2.main.Media;

import android.content.Context;

/**
 * Reads a textfile belonging to a Context (Activity usually)
 * @author Sascha
 *
 */

public class FileReader {
	
	/**
	 * Reads a textfile.
	 * @param context 	The instance of the activity class.
	 * @param name		Name of the file to be read
	 * @return			A String representation of the file.
	 */
	
	public static String readFile(String name)
	{
		FileInputStream fis;
		try {
			fis = Media.CONTEXT.openFileInput(name);
			int ch;
			StringBuffer fileContent = new StringBuffer();
			while( (ch = fis.read()) != -1)
				  fileContent.append((char)ch);
			
			fis.close();
			
			return new String(fileContent);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return "";
	
	}
	
	public static String readRawFile(int id) {
		
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(Media.CONTEXT.openRawResource(id));
			char[] ch = new char[50];
			StringBuffer fileContent = new StringBuffer();
			int length = 0;
			while( (length = isr.read(ch)) != -1)
				  fileContent.append(ch, 0, length);
			
			isr.close();
			
			return new String(fileContent);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return "";
	}
	
	/**
	 * Checks if a file exists.
	 * @param context 	The instance of the activity class.
	 * @param name		Name of the file to be read
	 * @return			True iff the file exists.
	 */
	
	public static boolean fileExists(Context context, String fileName)
	{
		FileInputStream fis;
		try {
			fis = context.openFileInput(fileName);
			fis.close();
			return true;
		} catch (FileNotFoundException e) {			
		} catch (IOException e) {
		}
		
		return false;
	}
}
