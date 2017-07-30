package com.tyrfing.games.id17.crestgen;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class CrestShape {
	private List<Place> places = new ArrayList<Place>();
	private List<Integer> imgClasses = new ArrayList<Integer>();
	
	public void addPlace(Place p) {
		places.add(p);
		if (!imgClasses.contains(p.imgClass)) {
			imgClasses.add(p.imgClass);
		}
	}
	
	public int getCountPlaces() {
		return places.size();
	}
	
	public Place getPlace(int i) {
		return places.get(i);
	}
	
	public int getCountImgClasses() {
		return imgClasses.size();
	}
	
	public Integer getImgClass(int i) {
		return imgClasses.get(i);
	}
	
	public static CrestShape createFromFile(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		image = Resources.resize(image, CrestGen.CREST_SIZE, CrestGen.CREST_SIZE, Image.SCALE_REPLICATE);
		
		CrestShape shape = new CrestShape();
		
		int rgbWhite = Color.decode("#ffffff").getRGB();
		
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				int rgb = image.getRGB(x,y);
				if (rgb != rgbWhite) {
					Rectangle rect = new Rectangle();
					rect.x = x;
					rect.y = y;
					
					for (; x+rect.width < image.getWidth() && image.getRGB(x+rect.width,y) == rgb; ++rect.width);
					for (; y+rect.height < image.getHeight() && image.getRGB(x,y+rect.height) == rgb; ++rect.height);
					
					for (int x2 = 0; x2 < rect.width; ++x2) {
						for (int y2 = 0; y2 < rect.height; ++y2) {
							image.setRGB(x+x2, y+y2, rgbWhite);
						}	
					}
					
					shape.addPlace(new Place(rect, rgb));
				}
			}	
		}
		
		return shape;
	}
}
