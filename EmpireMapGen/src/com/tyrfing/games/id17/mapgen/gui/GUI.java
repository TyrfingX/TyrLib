package com.tyrfing.games.id17.mapgen.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.tyrfing.games.id17.crestgen.CrestGen;
import com.tyrfing.games.id17.mapgen.Map;
import com.tyrfing.games.id17.mapgen.printers.BaronyPrinter;
import com.tyrfing.games.id17.mapgen.printers.HousePrinter;
import com.tyrfing.games.id17.mapgen.zones.Area;
import com.tyrfing.games.id17.mapgen.zones.AreaType;
import com.tyrfing.games.id17.mapgen.zones.Barony;

public class GUI extends JFrame {

	private Map map;
	
	public static final int HOUSES = 144;
	public static final int WINDOW_SIZE = 300;
	public static final String MAP_NAME = "small";
	public static final int AREAS = 750;
	
	//public static final int HOUSES = 400;
	//public static final int WINDOW_SIZE = 512;
	//public static final String MAP_NAME = "medium";
	//public static final int AREAS = 2500;
	
	//public static final int HOUSES = 900;
	//public static final int WINDOW_SIZE = 800;
	//public static final String MAP_NAME = "huge";
	//public static final int AREAS = 6000;
	
	//public static final int HOUSES = 1600;
	//public static final int WINDOW_SIZE = 350;
	
	public static final int RELAXATION_STEPS = 6;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2736046183199667210L;

	public GUI() throws IOException {
		super("MapGen");
		
		FlowLayout layout = new FlowLayout();
		
		this.setLayout(layout);
		this.setSize((WINDOW_SIZE+layout.getHgap())*4, (WINDOW_SIZE+layout.getVgap()*3)*2);
		
		addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		    	System.exit(0);
		    }
		});
		
		map = new Map(AREAS, 10000, 0.00001f);
		for (int i = 0; i < RELAXATION_STEPS; ++i) {
			map.lloydRelax();
		}
		
		map.createAreas();
		map.generateLandShape(22);
		//map.generateLandShape(40);
		map.assignElevation(255);
		map.generateRivers();
		map.assignMoisture(0.94);
		map.assignBiomes();
		map.generateBaronies(5);
		map.generateMapObjects();
		map.generateBeaches();
		map.generateBaronyBounds();
		map.generateRoads();
		map.generateTileMap();	
		map.generateDetails();
		
		CrestGen crestGen = new CrestGen(HOUSES, MAP_NAME);
		crestGen.generate();
		
		map.generateHouses(HOUSES, 0.8);
		map.generateNames(crestGen.getCrests());
		map.checkCorrectness();
		
		createPanes();
		
		File outputfile = new File("/home/sascha/dev/workspace/PCPortTest/bin/res/drawable/tilemap" + MAP_NAME + ".png");
		try {
			ImageIO.write(map.tileMap, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		outputfile = new File("/home/sascha/dev/workspace/PCPortTest/bin/res/drawable/baronymap" + MAP_NAME + ".png");
		try {
			ImageIO.write(map.baronyMap, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HousePrinter housePrinter = new HousePrinter(map);
		housePrinter.print("/home/sascha/dev/workspace/PCPortTest/bin/res/assets/maps/" + MAP_NAME + "/Houses.xml");
	
		BaronyPrinter baronyPrinter = new BaronyPrinter(map);
		baronyPrinter.print("/home/sascha/dev/workspace/PCPortTest/bin/res/assets/maps/" + MAP_NAME + "/Baronies.xml");
	}
	
    private void createPanes() {
    	DrawPane elevationPane = new DrawPane();
		DrawPane baronyPane = new DrawPane();
		DrawPane moisturePane = new DrawPane();
		DrawPane biomePane = new DrawPane();
		DrawPane mapPane = new DrawPane();
		DrawPane housePane = new DrawPane();
		DrawPane supplyPane = new DrawPane();
		this.getContentPane().add(elevationPane);
		this.getContentPane().add(moisturePane);
		this.getContentPane().add(biomePane);
		this.getContentPane().add(baronyPane);
		this.getContentPane().add(housePane);
		this.getContentPane().add(mapPane);
		this.getContentPane().add(supplyPane);
		
		elevationPane.drawAreas();
		moisturePane.drawMoisture();
		biomePane.drawBiomes();
		baronyPane.drawBaronies();
		housePane.drawHouses();
		mapPane.drawMap();
		supplyPane.drawSupplies();
	}

	class DrawPane extends JPanel{
		private static final long serialVersionUID = -6673588659300909744L;

		private boolean drawAreas = false;
		private boolean drawBaronies = false;
		private boolean drawMoisture = false;
		private boolean drawBiome = false;
		private boolean drawMap = false;
		private boolean drawHouses = false;

		private boolean drawSupplies;
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			Graphics2D g2d = (Graphics2D) g;
			
			if (drawAreas) {
		        g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
										RenderingHints.VALUE_ANTIALIAS_ON); 
				
				g2d.setColor(AreaType.OCEAN.getColor());
				g2d.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
				
				for (int i = 0; i < map.allAreas.size(); ++i) {
					Area a = map.allAreas.get(i);
					g2d.setColor(a.getElevationColor());
					g2d.fill(a.polygon);
				}
				
				g2d.setStroke(new BasicStroke(1.5f));
				
				for (int i = 0; i < map.allRivers.size(); ++i) {
					g2d.setColor(AreaType.LAKE.getColor());
					g2d.draw(map.allRivers.get(i).path);
				}
			}
			
			if (drawMoisture) {
		        g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
										RenderingHints.VALUE_ANTIALIAS_ON); 
				
				g2d.setColor(AreaType.OCEAN.getColor());
				g2d.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
				
				for (int i = 0; i < map.allAreas.size(); ++i) {
					Area a = map.allAreas.get(i);
					g2d.setColor(a.getMoistureColor());
					g2d.fill(a.polygon);
				}
			}
			
			if (drawSupplies) {
		        g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
										RenderingHints.VALUE_ANTIALIAS_ON); 
				
				g2d.setColor(AreaType.OCEAN.getColor());
				g2d.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
				
				for (int i = 0; i < map.allAreas.size(); ++i) {
					Area a = map.allAreas.get(i);
					g2d.setColor(a.getSupplyColor());
					g2d.fill(a.polygon);
				}
			}
			
			if (drawBiome) {
		        g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
										RenderingHints.VALUE_ANTIALIAS_ON); 
				
				g2d.setColor(AreaType.OCEAN.getColor());
				g2d.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
				
				for (int i = 0; i < map.allAreas.size(); ++i) {
					Area a = map.allAreas.get(i);
					g2d.setColor(a.biome.getMapColor());
					g2d.fill(a.polygon);
				}
			}
			
			if (drawMap) {
				g2d.setColor(AreaType.OCEAN.getColor());
				g2d.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
				g2d.drawImage(map.tileMap, 	0, 0, GUI.WINDOW_SIZE, GUI.WINDOW_SIZE, 
											0, 0, GUI.WINDOW_SIZE,GUI.WINDOW_SIZE, null);
				
			}
			
			if (drawBaronies) {
		        g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
										RenderingHints.VALUE_ANTIALIAS_ON); 
		        
				g2d.setColor(AreaType.OCEAN.getColor());
				g2d.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
				
				for (int j = 0; j < map.allBaronies.size(); ++j) {
					Barony b = map.allBaronies.get(j);
					g2d.setColor(b.color);
					for (int i = 0; i < b.areas.size(); ++i) {
						Area a = b.areas.get(i);
						g2d.fill(a.polygon);
						g2d.draw(a.polygon);
					}
				}
				
				
				for (int j = 0; j < map.allBaronies.size(); ++j) {
					Barony b = map.allBaronies.get(j);
					g2d.setColor(Color.BLACK);
					g2d.drawRect(	b.mapObjects.get(0).x-1, b.mapObjects.get(0).y-1, 
									2, 2);
				}
				
			}
			
			if (drawHouses) {
		        g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
										RenderingHints.VALUE_ANTIALIAS_ON); 
		        
				g2d.setColor(AreaType.OCEAN.getColor());
				g2d.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
				
				for (int j = 0; j < map.allBaronies.size(); ++j) {
					Barony b = map.allBaronies.get(j);
					try {
					g2d.setColor(b.mapObjects.get(0).owner.color);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < b.areas.size(); ++i) {
						Area a = b.areas.get(i);
						g2d.fill(a.polygon);
						g2d.draw(a.polygon);
					}
				}
				
			}
         }
		
		public void drawAreas() {
			this.drawAreas = true;
			this.repaint();
		}
		
		public void drawBaronies() {
			this.drawBaronies = true;
			this.repaint();
		}
		
		public void drawMoisture() {
			this.drawMoisture = true;
			this.repaint();
		}
		
		public void drawBiomes() {
			this.drawBiome = true;
			this.repaint();
		}
		
		public void drawMap() {
			this.drawMap = true;
			this.repaint();
		}
		
		public void drawHouses() {
			this.drawHouses  = true;
			this.repaint();
		}
		
		public void drawSupplies() {
			this.drawSupplies  = true;
			this.repaint();
		}
		
		
	    @Override
	    public Dimension getPreferredSize() {
	        return new Dimension(WINDOW_SIZE, WINDOW_SIZE);
	    }
		
	
     }
	
}
