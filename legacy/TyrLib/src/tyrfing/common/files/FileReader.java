package tyrfing.common.files;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.content.Context;

public class FileReader {
	public static String readFile(Context context, String name)
	{
		FileInputStream fis;
		try {
			fis = context.openFileInput(name);
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
