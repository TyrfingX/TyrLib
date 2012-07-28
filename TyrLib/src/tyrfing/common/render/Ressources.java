package tyrfing.common.render;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tyrfing.common.math.Vector2;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Ressources {
	
	private static Map<String, Bitmap> res;
	private static Activity a;
	
	public static void init(Activity a)
	{
		Ressources.a = a;
		res = new ConcurrentHashMap<String, Bitmap>();	
		
		
	}
	
	public static void loadRes(String name, int drawable)
	{
		Bitmap bitmap = BitmapFactory.decodeResource( a.getResources(), drawable );

		res.put(name, bitmap); 			
	}

	public static void loadRes(String name, int drawable, Vector2 size)
	{
		
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeResource( a.getResources(), drawable,options );
		bitmap = Ressources.scaleBitmap(bitmap, size);
		res.put(name, bitmap); 			
	}
	
	public static void freeRes(String name)
	{
		Bitmap bitmap = Ressources.getBitmap(name);
		bitmap.recycle();
		Ressources.res.remove(name);
	}
	
	public static void add(String name, Bitmap bitmap)
	{
		res.put(name, bitmap);
	}
	
	public static Bitmap getBitmap(String name)
	{
		return res.get(name);
	}
	
	public static String getNameOfRes(Bitmap bitmap) {
		for (String name : res.keySet()) {
			if (res.get(name) == bitmap) {
				return name;
			}
		}
		
		return null;
	}
	
	
	public static Bitmap scaleBitmap(Bitmap bitmap, Vector2 size)
	{
		return Bitmap.createScaledBitmap(bitmap, (int)(size.x), (int)(size.y), true);
	}
	
	public static Bitmap getScaledBitmap(String name, Vector2 size)
	{
		Bitmap bitmap = Ressources.getBitmap(name);
		if (bitmap.getWidth() != size.x || bitmap.getHeight() != size.y)
		{
			return Ressources.scaleBitmap(bitmap, size);
		}
		else
		{
			return bitmap;
		}
	}
	
	public static void free()
	{
		Collection<Bitmap> bitmaps = res.values();
		for (Bitmap bitmap : bitmaps)
		{
			bitmap.recycle();
		}
	}
}
