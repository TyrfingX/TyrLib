package com.tyrfing.games.id17.crestgen;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


public class CrestGen {
	
	public static final int CREST_SIZE = 256;
	public static final int DESCALE = 2;
	
	public static void main(String[] args) {
		try {
			CrestGen gen = new CrestGen(400, "medium");
			gen.generate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private BufferedImage image;
	private int rows;
	
	private final int countCrests;
	private final Resources resources = new Resources();
	private final List<Crest> crests = new ArrayList<Crest>();
	private String mapName;
	
	public CrestGen(int countCrests, String mapName) throws IOException {
		this.countCrests = countCrests;
		resources.load();
		this.mapName = mapName;
	}
	
	public void generate() {
		rows = (int) Math.sqrt(countCrests);
		int size = rows * CREST_SIZE;
		
		image = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
		
		generateCrests();
		saveCrests();
	}
	
	private void generateCrests() {
		System.out.println("Generating Crests...");
		int progressSteps = countCrests / 10;
		
		{
			// Generate rebel crest
			Crest crest = Crest.createRebelCrest(resources.rebelCrest);
			crests.add(crest);
		}
		
		for (int i = 1, step = 0; i < countCrests; ++i, ++step) {
			CrestShape shape = resources.shapes.get((int)(Math.random()*resources.shapes.size()));
			CrestShape bgShape = resources.bgShapes.get((int)(Math.random()*resources.bgShapes.size()));
			
			Crest crest = Crest.create(shape, bgShape, resources);
			while (!isNewHash(crest.getHash())) {
				resources.nameGen.forgetName(crest.name);
				crest = Crest.create(shape, bgShape, resources);
			}
				
			crests.add(crest);
		
			if(step == progressSteps) {
				step = 0;
				System.out.println("Progress: " + (int)((float)i/countCrests*100) + "%");
			}
		}
		
		System.out.println("Progress: 100%");
		System.out.println("... finished!");
	}
	
	private boolean isNewHash(String hash) {
		for (Crest crest : crests) {
			String hashOther = crest.getHash();
			if (hashOther.equals(hash)) {
				return false;
			}
		}
		
		return true;
	}
	
	private void saveCrests() {
		System.out.println("Rendering Crests...");
		Rectangle targetArea = new Rectangle(CREST_SIZE, CREST_SIZE);
		
		int progressSteps = countCrests / 10;
		
		for (int i = 0, step = 0; i < crests.size(); ++i, ++step) {
			Crest crest = crests.get(i);
			crest.renderOnto(image, targetArea);
			
			crest.region.x = targetArea.x / DESCALE;
			crest.region.y = targetArea.y / DESCALE;
			crest.region.width = targetArea.width / DESCALE;
			crest.region.height = targetArea.height / DESCALE;
			
			if(step == progressSteps) {
				step = 0;
				System.out.println("Progress: " + (int)((float)i/countCrests*100) + "%");
			}
			
			if (i % rows == rows-1) {
				targetArea.x = 0;
				targetArea.y += CREST_SIZE;
			} else {
				targetArea.x += CREST_SIZE;
			}
		}
		
		System.out.println("... finished!");
		
		image = Resources.resize(image, rows*CREST_SIZE/DESCALE, rows*CREST_SIZE/DESCALE);
		
		File outputfile = new File("/home/sascha/dev/workspace/PCPortTest/bin/res/drawable/sigils" + mapName + ".png");
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CrestAtlasPrinter printer = new CrestAtlasPrinter(crests);
		printer.print("/home/sascha/dev/workspace/PCPortTest/bin/res/assets/maps/" + mapName + "/sigils1.xml");
		
	}

	public List<Crest> getCrests() {
		return crests;
	}
}
