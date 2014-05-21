package com.TyrLib2.PC.main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.TyrLib2.PC.bitmap.PCCanvas;
import com.TyrLib2.PC.bitmap.PCDrawableBitmap;
import com.TyrLib2.PC.bitmap.PCPaint;
import com.TyrLib2.PC.bitmap.PCTypeface;
import com.tyrlib2.bitmap.ICanvas;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.bitmap.IPaint;
import com.tyrlib2.bitmap.ITypeface;
import com.tyrlib2.files.IBitmap;
import com.tyrlib2.graphics.text.GLText;
import com.tyrlib2.graphics.text.IGLText;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector2;


public class PCMedia extends Media {

	private Map<String, Integer> resourceIDs = new HashMap<String, Integer>();;
	private List<String> resourceNames = new ArrayList<String>();
	private List<String> resourceEndings = new ArrayList<String>();
	
	private PCOpenGLActivity context;
	
	public PCMedia(PCOpenGLActivity context) {
		
		this.context = context;
		
		System.out.println("-----Index resources-----");
		
		URL url = getClass().getResource("/res");
		File folder = new File(url.getPath());
		
		loadFolder(folder);
		
		System.out.println("-----Finished indexing resources-----");

	}
	
	private void loadFolder(File file) {
		
		String type = file.getName();
		File[] listOfFiles = file.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String name = type + "/" + listOfFiles[i].getName();
				
				String[] split = name.split("\\.");
				
				resourceNames.add(split[0]);
				resourceIDs.put(split[0], resourceNames.size() - 1);
				
				if (split.length > 1) {
					resourceEndings.add(split[1]);
				} else {
					resourceEndings.add("");
				}
				
				System.out.println(split[0] +  ": " + String.valueOf(resourceNames.size() - 1));
			} else {
				loadFolder(listOfFiles[i]);
			}
		}
	}
	
	@Override
	public InputStream openAsset(String fileName) throws IOException {
		String path = "/res/assets/"+ fileName;
		URL url = getClass().getResource(path);
		return new FileInputStream(url.getPath());
	}

	@Override
	public FileInputStream openFileInput(String fileName) throws IOException {
		String path = "/res/"+ fileName + "." + resourceEndings.get(resourceIDs.get(fileName));
		URL url = getClass().getResource(path);
		return new FileInputStream(url.getPath());
	}

	@Override
	public InputStream openRawResource(int id) throws IOException {
		String path = "/res/"+ resourceNames.get(id) +  (resourceEndings.get(id).equals("") ? "" : "." + resourceEndings.get(id));
		URL url = getClass().getResource(path);
		return new FileInputStream(url.getPath());
	}

	@Override
	public int getResourceID(String source, String resType) {
		return resourceIDs.get(resType + "/" + source);
	}

	@Override
	public IGLText createTextRenderer(String fontSource, int size) {
		IGLText glText = new GLText();
		glText.load( fontSource, size, 2, 2 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
		glText.setScale(1);
		return glText;
	}

	@Override
	public IBitmap loadBitmap(int resID, boolean prescaling) {
		
		String path = "/res/"+ resourceNames.get(resID) + "." + resourceEndings.get(resID);
		URL url = getClass().getResource(path);
		return new PCBitmap(url.getPath());
	}

	@Override
	public IDrawableBitmap createAlphaBitmap(int width, int height) {
		return new PCDrawableBitmap(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
	}

	@Override
	public ITypeface createFromAsset(String file) {
		String path = "/res/assets/"+ file;
		URL url = getClass().getResource(path);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File(url.getPath()));
			ge.registerFont(font);
			return new PCTypeface(font);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	@Override
	public IPaint createPaint(ICanvas canvas) {
		Graphics g = ((PCCanvas)canvas).canvas.getGraphics();
		return new PCPaint(g);
	}

	@Override
	public ICanvas createCanvas() {
		return new PCCanvas(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
	}

	@Override
	public Vector2 getScreenSize() {
		Dimension d = context.getGLView().getSize();
		return new Vector2(d.width, d.height);
	}

	@Override
	public IDrawableBitmap createBitmap(int width, int height) {
		return new PCDrawableBitmap(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
	}

	@Override
	public ICanvas createCanvas(IDrawableBitmap bitmap) {
		PCDrawableBitmap b = (PCDrawableBitmap) bitmap;
		return new PCCanvas(b.canvas);
	}

	@Override
	public void loadBitmap(IBitmap bitmap) {

	}

	@Override
	public void serializeTo(Serializable s, String target, String fileName) {
		try {
			URL url = getClass().getResource(target);
			String path = url.getPath() + "/" + fileName;
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(s);
			out.close();
			fileOut.close();
			System.out.printf("Saved successful to " + target);
		}catch(IOException i) {
			i.printStackTrace();
		}
	}

	@Override
	public Object deserializeFrom(String target, String fileName) {
		InputStream fis = null;
		Object result = null;
		
		try
		{
			
		  URL url = getClass().getResource(target);
		  String path = url.getPath() + "/" + fileName;
		  
		  fis = new FileInputStream( path );

		  ObjectInputStream o = new ObjectInputStream( fis );
		  result = o.readObject();
		  o.close();
		  
		}
		catch ( IOException e ) { System.err.println( e ); }
		catch ( ClassNotFoundException e ) { System.err.println( e ); }
		finally { try { fis.close(); } catch ( Exception e ) { } }
		
		return result;
	}


}
