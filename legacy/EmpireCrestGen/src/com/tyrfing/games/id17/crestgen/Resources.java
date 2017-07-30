package com.tyrfing.games.id17.crestgen;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.tyrfing.games.id17.names.HouseNameGen;

public class Resources {
	public final List<CrestShape> shapes = new ArrayList<CrestShape>();
	public final List<CrestShape> bgShapes = new ArrayList<CrestShape>();
	public final List<CrestObject> bgs = new ArrayList<CrestObject>();
	public final List<CrestObject> objects = new ArrayList<CrestObject>();
	public final HouseNameGen nameGen = new HouseNameGen();
	public CrestObject rebelCrest;
	
	public void load() throws IOException {
		File[] files;
		
		files = getFiles("/shapes");
		for (int i = 0; i < files.length; ++i) {
			shapes.add(CrestShape.createFromFile(files[i]));
		}
		
		files = getFiles("/bgshapes");
		for (int i = 0; i < files.length; ++i) {
			bgShapes.add(CrestShape.createFromFile(files[i]));
		}
		
		files = getFiles("/bgs");
		for (int i = 0; i < files.length; ++i) {
			BufferedImage image = ImageIO.read(files[i]);
			image = resize(image, CrestGen.CREST_SIZE, CrestGen.CREST_SIZE);
			bgs.add(new CrestObject(image));
		}
		
		files = getFiles("/objects");
		for (int i = 0; i < files.length; ++i) {
			BufferedImage image = ImageIO.read(files[i]);
			image = resize(image, CrestGen.CREST_SIZE, CrestGen.CREST_SIZE);
			objects.add(new CrestObject(image));
		}
		
		BufferedImage image = ImageIO.read(getFile("/rebels.png"));
		rebelCrest = new CrestObject(resize(image, CrestGen.CREST_SIZE, CrestGen.CREST_SIZE));		
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH, int hints) { 
	    Image tmp = img.getScaledInstance(newW, newH, hints);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_4BYTE_ABGR);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    return resize(img, newW, newH, Image.SCALE_SMOOTH);
	}  
	
	private File[] getFiles(String path) {
		URL url = getClass().getResource(path);
		File folder = new File(url.getPath());
		return folder.listFiles();
	}
	
	private File getFile(String path) {
		URL url = getClass().getResource(path);
		File file = new File(url.getPath());
		return file;
	}
}
