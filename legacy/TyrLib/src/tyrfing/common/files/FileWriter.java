package tyrfing.common.files;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class FileWriter {
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
