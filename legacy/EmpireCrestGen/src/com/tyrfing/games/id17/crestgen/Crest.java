package com.tyrfing.games.id17.crestgen;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Crest {
	public final String name;
	
	private String hash = null;
	
	private List<CrestObjectInstance> backgrounds = new ArrayList<CrestObjectInstance>();
	private List<CrestObjectInstance> objects = new ArrayList<CrestObjectInstance>();
	
	public final Rectangle region = new Rectangle();
	
	public Crest(String name) {
		this.name = name;
	}
	
	
	public void addObject(Place place, CrestObject object) {
		objects.add(new CrestObjectInstance(place, object));
	}

	public void addBackground(Place place, CrestObject object) {
		backgrounds.add(new CrestObjectInstance(place, object));
	}
	
	public String getHash() {
		if (hash == null) {
			hash = "";
			for (CrestObjectInstance instance : backgrounds) {
				hash += instance.object.hash;
			}
			
			for (CrestObjectInstance instance : objects) {
				hash += instance.object.hash;
			}
		}
		
		return hash;
	}
	
	public void renderOnto(BufferedImage target, Rectangle targetArea) {
		for (CrestObjectInstance instance : backgrounds) {
			CrestObject object = instance.object;
			Rectangle rectangle = 	instance.place != null ? 
									instance.place.area :
									new Rectangle(	targetArea.x,targetArea.y,
													targetArea.width,targetArea.height);
			
			for (int x = rectangle.x; x < rectangle.width+rectangle.x; ++x) {
				for (int y = rectangle.y; y < rectangle.height+rectangle.y; ++y) {
					int targetX = targetArea.x+x;
					int targetY = targetArea.y+y;
					Color color = getColor(object.image, x, y);
					target.setRGB(targetX, targetY, color.getRGB());
				}
			}
		}
		
		for (CrestObjectInstance instance : objects) {
			CrestObject object = instance.object;
			Rectangle rectangle = instance.place.area;
			
			BufferedImage image = Resources.resize(object.image, rectangle.width, rectangle.height);
			
			for (int x = 0; x < image.getWidth(); ++x) {
				for (int y = 0; y < image.getHeight(); ++y) {
					int targetX = targetArea.x+rectangle.x+x;
					int targetY = targetArea.y+rectangle.y+y;
					Color colorDest = getColor(target, targetX, targetY);
					Color colorSrc = getColor(image, x, y);
					Color color = blend(colorDest, colorSrc, 0.9f);
					
					target.setRGB(targetX, targetY, color.getRGB());
				}
			}
		}
	}
	
	public static Color blend(Color c1, Color c2, float weight) {
		int alpha2 = c2.getAlpha();
		weight = weight*alpha2/255;
		
		float r = c1.getRed()*(1-weight)+c2.getRed()*weight;
		float g = c1.getGreen()*(1-weight)+c2.getGreen()*weight;
		float b = c1.getBlue()*(1-weight)+c2.getBlue()*weight;
		
		int alpha = c1.getAlpha();
		
		return new Color((int) r, (int) g, (int) b, alpha);
	}
	
	public static Color getColor(BufferedImage image, int x, int y) {
		Color color = new Color(image.getRGB(x, y));
		int alpha = getAlpha(image, x, y);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
	
	public static int getAlpha(BufferedImage image, int x, int y)
	{
	    return (image.getRGB(x, y) >> 24) & 0xFF;
	}
	
	public static Crest create(CrestShape shape, CrestShape bgShape, Resources resources) {
		Crest crest = new Crest(resources.nameGen.generateNext());
		
		Map<Integer, CrestObject> objects = new TreeMap<Integer, CrestObject>();
		for (int i = 0; i < bgShape.getCountImgClasses(); ++i) {
			CrestObject object = resources.bgs.get((int)(Math.random()*resources.bgs.size()));
			objects.put(bgShape.getImgClass(i), object);
		}
		
		for (int i = 0; i < bgShape.getCountPlaces(); ++i) {
			Place place = bgShape.getPlace(i);
			CrestObject object = objects.get(place.imgClass);
			crest.addBackground(place, object);
		}
		
		
		objects = new TreeMap<Integer, CrestObject>();
		for (int i = 0; i < shape.getCountImgClasses(); ++i) {
			CrestObject object = resources.objects.get((int)(Math.random()*resources.objects.size()));
			objects.put(shape.getImgClass(i), object);
		}
		
		for (int i = 0; i < shape.getCountPlaces(); ++i) {
			Place place = shape.getPlace(i);
			CrestObject object = objects.get(place.imgClass);
			crest.addObject(place, object);
		}
		
		return crest;
	}


	public static Crest createRebelCrest(CrestObject rebelCrest) {
		Crest crest = new Crest("Rebels");
		crest.addBackground(null, rebelCrest);
		return crest;
	}
}
