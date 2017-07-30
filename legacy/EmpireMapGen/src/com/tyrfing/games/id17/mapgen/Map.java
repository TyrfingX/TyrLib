package com.tyrfing.games.id17.mapgen;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import be.humphreys.simplevoronoi.GraphEdge;
import be.humphreys.simplevoronoi.Voronoi;

import com.tyrfing.games.id17.colors.Color4;
import com.tyrfing.games.id17.crestgen.Crest;
import com.tyrfing.games.id17.mapgen.gui.GUI;
import com.tyrfing.games.id17.mapgen.objects.MapObject;
import com.tyrfing.games.id17.mapgen.objects.MapObjectType;
import com.tyrfing.games.id17.mapgen.objects.River;
import com.tyrfing.games.id17.mapgen.objects.Road;
import com.tyrfing.games.id17.mapgen.zones.Area;
import com.tyrfing.games.id17.mapgen.zones.AreaType;
import com.tyrfing.games.id17.mapgen.zones.Barony;
import com.tyrfing.games.id17.mapgen.zones.Biome;
import com.tyrfing.games.id17.mapgen.zones.Corner;
import com.tyrfing.games.id17.mapgen.zones.TileType;
import com.tyrfing.games.id17.names.HoldingNameGen;
import com.tyrfing.games.id17.noise.ImprovedNoise;

public class Map {
	
	public static final int BASE_ELEVATION = 16;
	
	public List<GraphEdge> allEdges;
	public List<Area> allAreas = new ArrayList<Area>();
	public List<Barony> allBaronies = new ArrayList<Barony>();
	public List<Area> cornerAreas = new ArrayList<Area>();
	public List<River> allRivers = new ArrayList<River>();
	public List<Corner> allCorners = new ArrayList<Corner>();
	public List<MapObject> allHoldings = new ArrayList<MapObject>();
	public List<House> allHouses = new ArrayList<House>();
	public List<House> allHeadHouses = new ArrayList<House>();
	public double[] generatorsX;
	public double[] generatorsY;
	public int countGenerators;
	public float minDistanceBetweenGenerators;
	public double generatedMaxElevation;
	private List<List<GraphEdge>> edgeLists = new ArrayList<List<GraphEdge>>();
	
	public Color4[][] tiles;
	public Area[][] areaAssignment;
	
	public BufferedImage tileMap;
	public BufferedImage baronyMap;
	public boolean reachable[][];
	public List<Road> roads = new ArrayList<Road>();

	public Map(int countGenerators, int size, float minDistanceBetweenGenerators) {
		Random random = new Random();

		this.countGenerators = countGenerators;
		this.generatorsX = new double[countGenerators];
		this.generatorsY = new double[countGenerators];
		this.minDistanceBetweenGenerators = minDistanceBetweenGenerators;

		for (int i = 0; i < countGenerators; ++i) {
			generatorsX[i] = random.nextFloat();
			generatorsY[i] = random.nextFloat();
		}			

		createVoronoi();
	}

	private void createVoronoi() {
		Voronoi v = new Voronoi(minDistanceBetweenGenerators);  
		allEdges = v.generateVoronoi(	generatorsX, generatorsY, 
				0, 1, 0, 1);  
	}

	public void lloydRelax() {
		int[] count = new int[countGenerators];
		for (int j = 0; j < countGenerators; ++j) {
			generatorsX[j] = 0;
			generatorsY[j] = 0;
		}

		for (int i = 0; i < allEdges.size(); ++i) {
			GraphEdge e = allEdges.get(i);
			generatorsX[e.site1] += e.x1;
			generatorsY[e.site1] += e.y1;

			count[e.site1]++;
			generatorsX[e.site2] += e.x2;
			generatorsY[e.site2] += e.y2;

			count[e.site2]++;
		}

		for (int j = 0; j < countGenerators; ++j) {
			if (count[j] != 0) {
				generatorsX[j] /= count[j];
				generatorsY[j] /= count[j];
			}
		}
			


		createVoronoi();
	}

	public void createAreas() {
		
		int size = allEdges.size();
		
		for (int i = 0; i < countGenerators; ++i) {
			edgeLists.add(new ArrayList<GraphEdge>());
		}
		
		for (int i = 0; i < size; ++i) {
			GraphEdge e = allEdges.get(i);
			List<GraphEdge> edgeList1 = edgeLists.get(e.site1);
			List<GraphEdge> edgeList2 = edgeLists.get(e.site2);
			edgeList1.add(e);
			edgeList2.add(e);
		}
		
		for (int i = 0; i < countGenerators; ++i) {
			Path2D.Double p = new Path2D.Double();
			boolean added = false;

			List<GraphEdge> edgeList = edgeLists.get(i);
			
			GraphEdge e = edgeList.get(0);
			p.moveTo(e.x1*GUI.WINDOW_SIZE, e.y1*GUI.WINDOW_SIZE);
			p.lineTo(e.x2*GUI.WINDOW_SIZE, e.y2*GUI.WINDOW_SIZE);
			
			do {
				added = false;
				for (int j = 0; j < edgeList.size(); ++j) {
					e = edgeList.get(j);
					if (		Math.abs(p.getCurrentPoint().getX() - e.x1*GUI.WINDOW_SIZE) < 0.001
							&&	Math.abs(p.getCurrentPoint().getY() - e.y1*GUI.WINDOW_SIZE) < 0.001) {
						added = true;
						edgeList.remove(j);
						p.lineTo(e.x2*GUI.WINDOW_SIZE, e.y2*GUI.WINDOW_SIZE);
					} else if (		Math.abs(p.getCurrentPoint().getX() - e.x2*GUI.WINDOW_SIZE) < 0.001
								&&	Math.abs(p.getCurrentPoint().getY() - e.y2*GUI.WINDOW_SIZE) < 0.001) {
						edgeList.remove(j);
						added = true;
						p.lineTo(e.x1*GUI.WINDOW_SIZE, e.y1*GUI.WINDOW_SIZE);
					}
				}
			} while (added);
			
			allAreas.add(new Area(	this, allAreas.size(), p, 
									new Point2D.Double(generatorsX[i]*GUI.WINDOW_SIZE, generatorsY[i]*GUI.WINDOW_SIZE)));
		}
		
		
		for (int i = 0; i < size; ++i) {
			GraphEdge e = allEdges.get(i);
			Area a1 = allAreas.get(e.site1);
			Area a2 = allAreas.get(e.site2);
			
			Point2D.Double p1 =  new Point2D.Double(e.x1, e.y1);
			Point2D.Double p2 =  new Point2D.Double(e.x2, e.y2);
			
			Corner c1 = hasCorner(p1);
			if (c1 == null) {
				c1 = new Corner(p1);
				allCorners.add(c1);
			} 
			
			Corner c2 = hasCorner(p2);
			if (c2 == null) {
				c2 = new Corner(p2);
				allCorners.add(c2);
			}
			
			if (!c1.connects.contains(a1)) {
				c1.connects.add(a1);
			}
			
			if (!c1.connects.contains(a2)) {
				c1.connects.add(a2);
			}
			
			if (!c2.connects.contains(a1)) {
				c2.connects.add(a1);
			}
			
			if (!c2.connects.contains(a2)) {
				c2.connects.add(a2);
			}
			
			if (!c1.neighbours.contains(c2)) {
				c1.neighbours.add(c2);
			}
			
			if (!c2.neighbours.contains(c1)) {
				c2.neighbours.add(c1);
			}
			
			if (!a1.corners.contains(c1)) {
				a1.corners.add(c1);
			}
			
			if (!a1.corners.contains(c2)) {
				a2.corners.add(c2);
			}
			
			if (!a2.corners.contains(c1)) {
				a2.corners.add(c1);
			}
			
			if (!a2.corners.contains(c2)) {
				a2.corners.add(c2);
			}
			
		}
		
		for (int i = 0; i < size; ++i) {
			GraphEdge e = allEdges.get(i);
			List<GraphEdge> edgeList1 = edgeLists.get(e.site1);
			List<GraphEdge> edgeList2 = edgeLists.get(e.site2);
			edgeList1.add(e);
			edgeList2.add(e);
		}
		
		for (int i = 0; i < countGenerators; ++i) {
			Area area = allAreas.get(i);
			List<GraphEdge> edgeList = edgeLists.get(i);
			int edges = edgeList.size();
			for (int j = 0; j < edges; ++j) {
				GraphEdge e = edgeList.get(j);
				if (e.site1 == i) {
					Area other = allAreas.get(e.site2);
					area.addCorner(other);
				} else if (e.site2 == i) {
					Area other = allAreas.get(e.site1);
					area.addCorner(other);
				}
			}
		}
				
	}
	
	public Corner hasCorner(Point2D.Double point) {
		for (int i = 0; i < allCorners.size(); ++i) {
			Corner corner = allCorners.get(i);
			if (	Math.abs(corner.point.getX() - point.getX()) < 0.0001  
				&&	Math.abs(corner.point.getY() - point.getY()) < 0.0001 ) {
				return corner;
			}
		}
		
		return null;
	}
	
	public void generateLandShape(double minBorderDistance) {
		int seed = new Random().nextInt();
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.generator.x >= minBorderDistance && a.generator.y >= minBorderDistance) {
				if (	a.generator.x <= GUI.WINDOW_SIZE - minBorderDistance 
					&& 	a.generator.y <= GUI.WINDOW_SIZE - minBorderDistance) {
					
					PathIterator itr = a.polygon.getPathIterator(null);
					
					int corners = 0;
					int landCorners = 0;
					
					while (!itr.isDone()) {
						corners++;
						
						if (isLand(a.polygon.getCurrentPoint(), seed)) {
							landCorners++;
						}
					
						itr.next();
					}
					
					if ((float) landCorners / corners > 0.9f) {
						a.type = AreaType.LAND;
					}
					
				}
			}
		}
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.type == AreaType.OCEAN || a.type == AreaType.LAKE) {
				for (int j = 0; j < a.neighbours.size(); ++j) {
					Area neighbour = a.neighbours.get(j);
					if (neighbour.type == AreaType.LAND) {
						if (!cornerAreas.contains(neighbour)) {
							cornerAreas.add(neighbour);
						}
					}
					
					if (a.type == AreaType.OCEAN) {
						neighbour.isBeachArea = true;
					}
				}
			} 
		}
		
	}
	
	private boolean isLand(Point2D point, int seed) {
	    double noise = Math.abs(ImprovedNoise.noise(point.getX()/255, point.getY()/255, seed));
	    double distance = point.distance(GUI.WINDOW_SIZE/2, GUI.WINDOW_SIZE/2) / GUI.WINDOW_SIZE;
	    return noise > distance - 0.15;
	}
	
	private boolean isValley(Point2D point, int seed, Area a) {
	    double noise = Math.abs(ImprovedNoise.noise(point.getX()/100, point.getY()/100, seed));
	    return noise > 0.6 - 0.35*a.elevation / generatedMaxElevation;
	}
	
	public void assignElevation(int maxElevation) {
		distanceElevation(maxElevation);
		
		int seed = new Random().nextInt();
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.type == AreaType.LAND) {
				if (!cornerAreas.contains(a) && isValley(a.generator, seed, a)) {
					if (Math.random() <= 0.1f) {
						a.type = AreaType.LAKE;
					} else {
						cornerAreas.add(a);
					}
				}
			}
			
			a.elevation = 1;
		}
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.type == AreaType.LAKE) {
				for (int j = 0; j < a.neighbours.size(); ++j) {
					Area other = a.neighbours.get(j);
					other.type = AreaType.LAKE;
					cornerAreas.remove(other);
				}
			}
		}
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.type == AreaType.LAKE) {
				for (int j = 0; j < a.neighbours.size(); ++j) {
					Area neighbour = a.neighbours.get(j);
					if (!cornerAreas.contains(neighbour)) {
						cornerAreas.add(neighbour);
					}
				}
			}
		}
		
		generatedMaxElevation = 1;
		
		distanceElevation(maxElevation);
	}
	
	public void distanceElevation(int maxElevation) {
		Set<Area> set = new HashSet<Area>();
		set.addAll(cornerAreas);
		
		Queue<Area> q = new LinkedList<Area>();
		q.addAll(cornerAreas);
		
		Random gauss = new Random();
		
		while (!q.isEmpty()) {
			Area a = q.poll();
			
			for (int i = 0; i < a.neighbours.size(); ++i) {
				Area neighbour = a.neighbours.get(i);
				if (neighbour.type == AreaType.LAND) {
					if (!set.contains(neighbour)) {
						int incease = Math.abs((int)(gauss.nextGaussian()))+1;
						neighbour.elevation = Math.min(a.elevation+incease*9, maxElevation);
						generatedMaxElevation = Math.max(neighbour.elevation, generatedMaxElevation);
						q.offer(neighbour);
						set.add(neighbour);
					}
				}
			}
		}
		

	}

	public void generateBaronies(int maxHoldings) {
		List<Area> areas = new ArrayList<Area>();
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.type == AreaType.LAND && a.biome != Biome.BEACH) {
				areas.add(a);
			}
		}
		
		genBarony: while (!areas.isEmpty()) {
			int random = (int)(Math.random()*areas.size());
			Area a = areas.get(random);
			areas.remove(random);
			
			if (a.type == AreaType.LAND) {
				Barony b = new Barony();
				b.areas.add(a);
				a.barony = b;
				
				for (int i = 0; i < a.neighbours.size(); ++i) {
					Area neighbour = a.neighbours.get(i);
					if (neighbour.type == AreaType.LAND) {
						if (areas.contains(neighbour)) {
							b.areas.add(neighbour);
							neighbour.barony = b;
							
							if (b.areas.size() == maxHoldings) {
								allBaronies.add(b);
								areas.remove(neighbour);
								continue genBarony;
							}
						}
					} 
					
					areas.remove(neighbour);
				}
				
				allBaronies.add(b);
			}
	
			
		}
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			if (b.areas.size() == 1) {
				Barony n = null;
				Area a = b.areas.get(0);
				for (int j = 0; j < a.neighbours.size(); ++j) {
					Area a2 = a.neighbours.get(j);
					if (a2.type == AreaType.LAND && a2.barony != null) {
						if (n == null || n.areas.size() > a2.barony.areas.size()) {
							n = a2.barony;
						}
					} 
				}
				
				if (n != null) {
					allBaronies.remove(i);
					--i;
					
					n.areas.add(a);
					a.barony = n;
				}
			}
		}
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			for (int j = 0; j < b.areas.size(); ++j) {
				for (int k = 0; k < b.areas.get(j).neighbours.size(); ++k) {
					Barony other = b.areas.get(j).neighbours.get(k).barony;
					if (other != null && other != b && !b.neighbours.contains(other)) {
						b.neighbours.add(other);
					}
				}
			}
		}
		
	}
	
	public void generateRivers() {
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.elevation/generatedMaxElevation >= 0.75) {
				for (int j = 0; j < a.corners.size(); ++j) {
					Corner corner = a.corners.get(j);
					if (Math.random() <= 0.1f) {
						River river = new River(corner);
						allRivers.add(river);
					}
				}
			}
		}
		
		for (int i = 0; i < allRivers.size(); ++i) {
			River r = allRivers.get(i);
			Corner currentCorner = r.corners.get(r.corners.size()-1);
			
			while (currentCorner != null) {	
				double height = currentCorner.getHeight();
				double leastHeight = height;
				Corner nextCorner = null;
				
				for (int j = 0; j < currentCorner.neighbours.size(); ++j) {
					Corner other = currentCorner.neighbours.get(j);
					double heightOther = other.getHeight();
					
					if (heightOther <= leastHeight && !r.corners.contains(other)) {
						if (heightOther < leastHeight || nextCorner == null || Math.random() <= 0.5) {
							nextCorner = other;
							leastHeight = heightOther;
						}
					}
				}
				
				boolean flowIntoRiver = false;
				
				if (nextCorner != null && !r.corners.contains(nextCorner)) {
					if (nextCorner.partOfRiver) {
						currentCorner = null;
						flowIntoRiver = true;
					} else {
						currentCorner = nextCorner;
					}
					r.addCorner(nextCorner);
				} else{
					currentCorner = null;
				}
				
				if (currentCorner != null && currentCorner.isCoastCorner()) {
					currentCorner = null;
				}
				
				if (currentCorner == null && !flowIntoRiver){
					Corner lastCorner = r.corners.get(r.corners.size()-1);
					if (!lastCorner.isCoastCorner()) {
						for (int j = 0; j < lastCorner.connects.size(); ++j) {
							lastCorner.connects.get(j).type = AreaType.LAKE;
						}
					}
				}
				
			}
			
			r.close();
		}
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			boolean isolated = true;
			for (int j = 0; j < a.neighbours.size(); ++j) {
				Area b = a.neighbours.get(j);
				if (b.type != AreaType.LAKE && b.type != AreaType.OCEAN) {
					isolated = false;
				}
			}
			if (isolated)  {
				a.type = AreaType.OCEAN;
			}
		}
		
	}

	public void assignMoisture(double moistureDec) {
		for (int i = 0; i < allCorners.size(); ++i) {
			Corner c = allCorners.get(i);
			if (c.partOfRiver || c.isCoastCorner()) {
				c.moisture = 0.95;
			}
		}
		
		for (int i = 0; i < allCorners.size(); ++i) {
			Corner c = allCorners.get(i);
			if (!(c.partOfRiver || c.isCoastCorner())) {
				
				Corner nearest = null;
				for (int j = 0; j < allCorners.size(); ++j) {
					Corner other = allCorners.get(j);
					if (other.partOfRiver || other.isCoastCorner()) {
						if (	nearest == null 
							|| 	other.point.distance(c.point) <= nearest.point.distance(c.point)) {
							nearest = other;
						}
					}
				}
				
				if (nearest != null) {
					double distance = nearest.point.distance(c.point)*100;
					c.moisture = Math.pow(moistureDec, distance);
				}
				
			}
		}
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.type == AreaType.LAND) {
				for (int j = 0; j < a.corners.size(); ++j) {
					a.moisture += a.corners.get(j).moisture;
				}
				a.moisture /= a.corners.size();
				a.moisture = a.moisture * a.moisture;
			}
		}
	}

	public void assignBiomes() {
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.type == AreaType.LAND && !a.isBeachArea) {
			
				double elevation = a.elevation/generatedMaxElevation;
				double moisture = a.moisture;
				
				int elevationZone = 0;
				int moistureZone = 0;
				
				for (int j = 0; j < 4; ++j) {
					if (elevation <= 0.25*(j+1)) {
						elevationZone = j;
						break;
					}
				}
				
				for (int j = 0; j < 7; ++j) {
					if (moisture <= (1d/7)*(j+1)) {
						moistureZone = j;
						break;
					}
				}
				
				int biomeCoord = elevationZone*7+moistureZone;
				a.biome = Biome.BIOME_TABLE[biomeCoord];
			} else if (a.type != AreaType.LAND){
				a.biome = Biome.WATER;
			} else if (a.isBeachArea){
				a.biome = Biome.BEACH;
			} else {
				a.biome = Biome.GRASSLAND;
			}
		}
	}
	
	public void generateMapObjects() {
		// Place the castles first
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			int random = (int)(b.areas.size()*Math.random());
			Area a = b.areas.get(random);
			
			MapObject o = new MapObject(MapObjectType.CASTLE, a);
			o.x = (int)(a.getCenter().x+0.5);
			o.y = (int)(a.getCenter().y+0.5);
			b.mapObjects.add(o);
			allHoldings.add(o);
			a.hasMapObject = true;
		}
		
		generateSupplyHoldings();
		generateVillages();
		generateWorkerHoldings();
	}

	public void generateTileMap() {
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			
			for (int j = 0; j < b.mapObjects.size(); ++j) {
				MapObject o = b.mapObjects.get(j);
				b.tiles[o.x-b.x][o.y-b.y].b = o.type.getInt();
				b.tiles[o.x-b.x][o.y-b.y].a = 255;
				if (o.type == MapObjectType.FARM) {
					b.tiles[o.x-b.x][o.y-b.y].r = b.tiles[o.x-b.x][o.y-b.y].r | 0x02;
				}
			}
		}
		
		tiles = new Color4[GUI.WINDOW_SIZE][GUI.WINDOW_SIZE];
		tileMap = new BufferedImage(GUI.WINDOW_SIZE, GUI.WINDOW_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		
		for (int x = 0; x < tileMap.getWidth(); ++x) {
			for (int y = 0; y < tileMap.getHeight(); ++y) {
				tiles[x][y] = new Color4();
			}
		}
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			
			for (int x = 0; x < b.width; ++x) {
				for (int y = 0; y < b.height; ++y) {	
					if (b.tiles[x][y].a != 0) {
						tiles[x+b.x][y+b.y].r = b.tiles[x][y].r;
						tiles[x+b.x][y+b.y].g = b.tiles[x][y].g;
						if (tiles[x+b.x][y+b.y].r != TileType.BEACH.getInt()) {
							tiles[x+b.x][y+b.y].g += BASE_ELEVATION;
						} else {
							tiles[x+b.x][y+b.y].g += BASE_ELEVATION/2;
						}
						if (tiles[x+b.x][y+b.y].b == 0) {
							tiles[x+b.x][y+b.y].b = b.tiles[x][y].b;
						}
						tiles[x+b.x][y+b.y].a = b.tiles[x][y].a;
					} else {
						if (tiles[x+b.x][y+b.y].b == 0) {
							tiles[x+b.x][y+b.y].b = b.tiles[x][y].b;
							tiles[x+b.x][y+b.y].a = 255;
						} 
					}
				}
			}
		}
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			
			for (int j = 0; j < b.mapObjects.size(); ++j) {
				MapObject o = b.mapObjects.get(j);
				
				double g = 0;
				int count = 0;
				for (int x = o.x-1; x < o.x+1; ++x) {
					for (int y = o.y-1; y < o.y+1; ++y) {
						if (x > 0 && y > 0 && x < GUI.WINDOW_SIZE-1 && y < GUI.WINDOW_SIZE-1) {
							if (tiles[x][y].a != 0) {
								g += tiles[x][y].g;
								count++;
							}
						}
					}	
				}
				
				g = (g/count+0.5);
				
				int offset = 2;
				if (o.type == MapObjectType.TREE3) {
					offset = 3;
				}
				
				for (int x = o.x-offset; x < o.x+offset; ++x) {
					for (int y = o.y-offset; y < o.y+offset; ++y) {
						if (x > 0 && y > 0 && x < GUI.WINDOW_SIZE-1 && y < GUI.WINDOW_SIZE-1) {
							if (tiles[x][y].a != 0) {
								tiles[x][y].g = (int) g;
								tiles[x][y].r = (tiles[x][y].r & 0x0f) | o.area.baseTileType.getInt();
							}
						}
					}	
				}
			}
		}		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			
			for (int j = 0; j < b.mapObjects.size(); ++j) {
				MapObject o = b.mapObjects.get(j);

				int offset = 2;
				if (o.type == MapObjectType.TREE3) {
					offset = 3;
				}
				
				for (int x = o.x-offset; x < o.x+offset; ++x) {
					for (int y = o.y-offset; y < o.y+offset; ++y) {
						if (x > 0 && y > 0 && x < GUI.WINDOW_SIZE-1 && y < GUI.WINDOW_SIZE-1) {
							if (tiles[x][y].a != 0) {
								tiles[x][y].r = (tiles[x][y].r & 0x0f) | o.area.baseTileType.getInt();
							}
						}
					}	
				}
			}
		}
		
		for (int i = 0; i < roads.size(); ++i) {
			roads.get(i).render(tiles);
		}
		
		for (int i = 0; i < 10; ++i) {
			redistributeElevation(false);
		}
		
		redistributeElevation(true);
		redistributeElevation(true);
		
		for (int i = 0; i < 1; ++i) {
			redistributeRoadElevation();
		}
		
		for (int x = 0; x < tileMap.getWidth(); ++x) {
			for (int y = 0; y < tileMap.getHeight(); ++y) {
				if (tiles[x][y].r != TileType.WATER.getInt() && tiles[x][y].r != TileType.BEACH.getInt()) {
					int g = tiles[x][y].g;
					if (x > 0 && tiles[x-1][y].r == TileType.WATER.getInt()) { 
						g = Math.max(g, tiles[x-1][y].g+1);
					}
					if (x < tileMap.getWidth() -1 && tiles[x+1][y].r == TileType.WATER.getInt()) { 
						g = Math.max(g, tiles[x+1][y].g+1);
					}
					if (y > 0 && tiles[x][y-1].r == TileType.WATER.getInt()) { 
						g = Math.max(g, tiles[x][y-1].g+1);
					}
					if (y < tileMap.getHeight() - 1 && tiles[x][y+1].r == TileType.WATER.getInt()) { 
						g = Math.max(g, tiles[x][y+1].g+1);
					}
					tiles[x][y].g = g;
				}
			}
		}
		
		for (int x = 0; x < tileMap.getWidth(); ++x) {
			for (int y = 0; y < tileMap.getHeight(); ++y) {
				if (tiles[x][y].r == TileType.WATER.getInt() && tiles[x][y].g <= BASE_ELEVATION/3) {
					tiles[x][y].r = TileType.BEACH.getInt();
				}
			}
		}
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			
			for (int j = 0; j < b.mapObjects.size(); ++j) {
				MapObject o = b.mapObjects.get(j);
				
				double g = 0;
				int count = 0;
				for (int x = o.x-1; x < o.x+1; ++x) {
					for (int y = o.y-1; y < o.y+1; ++y) {
						if (x > 0 && y > 0 && x < GUI.WINDOW_SIZE-1 && y < GUI.WINDOW_SIZE-1) {
							if (tiles[x][y].a != 0) {
								g += tiles[x][y].g;
								count++;
							}
						}
					}	
				}
				
				g = (g/count+0.5);
				
				int offset = 2;
				if (o.type == MapObjectType.TREE3) {
					offset = 3;
				}
				
				for (int x = o.x-offset; x < o.x+offset; ++x) {
					for (int y = o.y-offset; y < o.y+offset; ++y) {
						if (x > 0 && y > 0 && x < GUI.WINDOW_SIZE-1 && y < GUI.WINDOW_SIZE-1) {
							if (tiles[x][y].a != 0) {
								tiles[x][y].g = (int) g;
							}
						}
					}	
				}
			}
		}
		
		for (int x = 0; x < GUI.WINDOW_SIZE; ++x) {
			for (int y = 0; y < GUI.WINDOW_SIZE; ++y) {
				Color c = new Color(tiles[x][y].r,tiles[x][y].g, 
									tiles[x][y].b,tiles[x][y].a);
				tileMap.setRGB(x, y, c.getRGB() );
			}
		}
		
		
		baronyMap = new BufferedImage(GUI.WINDOW_SIZE, GUI.WINDOW_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		areaAssignment = new Area[GUI.WINDOW_SIZE][GUI.WINDOW_SIZE];
		
		// Draw the assignment map of the tiles
		
		for (int j = 0; j < allBaronies.size(); ++j) {
			Barony b = allBaronies.get(j);
			b.printColor = new Color(j+1);
		}
		
		Rectangle2D.Double r = new Rectangle2D.Double(0, 0, 1.6, 1.6);
		
		for (int x = 0; x < baronyMap.getWidth(); ++x) {
			for (int y = 0; y < baronyMap.getHeight(); ++y) {
				for (int i = 0; i < allAreas.size(); ++i) {
					Area a = allAreas.get(i);
					if (a.type == AreaType.LAND) {
						r.x = x-0.8;
						r.y = y-0.8;
						if (a.polygon.intersects(r)) {
							baronyMap.setRGB(x,y,a.barony.printColor.getRGB());
							areaAssignment[x][y] = a;
							break;
						}
					}
				}
			}
		}
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			
			for (int j = 0; j < b.mapObjects.size(); ++j) {
				MapObject o = b.mapObjects.get(j);
				baronyMap.setRGB(o.x,o.y,b.printColor.getRGB());
			}
		}
		
		
	}
	
	public void generateHouses(int count, double ratio) {
		
		// Reserve 1 space for the rebel house
		count--;
		
		int highHouses = (int)(count * ratio);
		
		for (int i = 0; i < count; ++i) {		
			House h = new House();
			allHouses.add(h);
		}
		
		// Assign a rank to each house
		// 0: Low house (high chance for "low" holdings, low chance for "high" holdings)
		// 1: High house (reverse)
		
		List<Barony> baronies = new ArrayList<Barony>(allBaronies);
		
		for (int i = 0; i < highHouses; ++i) {
			House h = allHouses.get(i);
			allHeadHouses.add(h);
			int random = (int)(Math.random()*baronies.size());
			Barony b = baronies.get(random);
			MapObject o = b.mapObjects.get(0);
			
			h.holdings.add(o);
			h.freeHoldings += b.mapObjects.size()-1;
			o.owner = h;
			baronies.remove(random);
			
			/*
			for (int j = 0; j < b.neighbours.size() && Math.random() <= 0.075f; ++j) {
				Barony other = b.neighbours.get(j);
				if (baronies.contains(other)) {
					baronies.remove(other);
					o = other.mapObjects.get(0);
					o.owner = h;
					h.holdings.add(o);
					h.freeHoldings += b.mapObjects.size()-1;
				}
			}
			*/
			
			if (baronies.size() == 0) {
				break;
			}
		}
		
		if (baronies.size() > 0) {
			System.out.println("Error: Not all baronies distributed!");
		}
		
		
		while(baronies.size() > 0) {
			int random = (int)(Math.random()*baronies.size());
			Barony b = baronies.get(random);
			baronies.remove(random);
			
			House nearest = null;  
			double nearestDistance = 0;
			
			for (int i = 0; i < allHouses.size(); ++i) {
				House h = allHouses.get(i);
				for (int j = 0; j < h.holdings.size(); ++j) {
					Barony other = h.holdings.get(j).area.barony;
					
					for (int m = 0; m < b.areas.size() && Math.random() <= 0.5; ++m) {
						Area a = b.areas.get(m);
						for (int n = 0; n < other.areas.size(); ++n) {
							Area otherArea = other.areas.get(n);
							double distance = a.generator.distance(otherArea.generator);
							if (nearest == null || nearestDistance <= distance) {
								nearestDistance = distance;
								nearest = h;
							}
						}
					}
				}
			}
			
			b.mapObjects.get(0).owner = nearest;
			nearest.holdings.add(b.mapObjects.get(0));
		}
		
		int[] holdings = new int[highHouses];
		int total = 0;
		for (int i = 0; i < highHouses; ++i) {
			House h = allHouses.get(i);
			total += h.freeHoldings;
			holdings[i] = total;
			
		}
		
		for (int i = highHouses; i < count; ++i) {
			House h = allHouses.get(i);
			int random = (int)(Math.random() * total);
			
			House parent = allHouses.get(highHouses-1);
			for (int j = 0; j < highHouses; ++j) {
				if (random < holdings[j]) {
					parent = allHouses.get(j);
					break;
				}
			}
			
			parent.subHouses.add(h);
			h.parent = parent;
		}
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			for (int j = 1; j < b.mapObjects.size(); ++j) {
				MapObject h = b.mapObjects.get(j);
				int random = (int)(Math.random() * (b.getOwner().subHouses.size()+1));
				House owner = null;
				if (random == 0) {
					owner = b.getOwner();
				} else {
					owner = b.getOwner().subHouses.get(random-1);
				}
				
				h.owner = owner;
				owner.holdings.add(h);
			}
			
			/*
			if (b.getOwner().parent == null) {
				for (int j = 0; j < b.neighbours.size() && Math.random() <= 0.05f; ++j) {
					Barony other = b.neighbours.get(j);
					if (other.getOwner().parent == null && other.getOwner() != b.getOwner() && other != b) {
						other.getOwner().parent = b.getOwner();
						other.getOwner().parent.subHouses.add(other.getOwner());
						allHeadHouses.remove(other.getOwner());
					}
				}
			}
			*/
			
		}
		
		House rebels = new House();
		rebels.name = "Rebels";
		allHouses.add(0, rebels);
		allHeadHouses.add(0, rebels);
	}
	
	public void generateNames(List<Crest> crests) {
		List<Crest> freeNames = new ArrayList<Crest>(crests);
		
		House rebels = allHouses.get(0);
		String name = freeNames.get(0).name;
		rebels.name = name;
		freeNames.remove(0);
		
		for (int i = 1; i < allHouses.size(); ++i) {		
			House h = allHouses.get(i);
			int random = (int)(Math.random()*freeNames.size());
			name = freeNames.get(random).name;
			h.name = name;
			freeNames.remove(random);
			
			if (freeNames.size()==0) break;
		}
		
		// Check Uniqueness of names
		for (int i = 0; i < allHouses.size(); ++i) {
			for (int j = 0; j < allHouses.size(); ++j) {
				if (allHouses.get(i).name.equals(allHouses.get(j).name) && i != j) {
					System.out.println("Error: Assigned House names not unqiue! Here: " + allHouses.get(i).name);
				}
			}	
		}
		
		for (int i = 0; i < allHoldings.size(); ++i) {
			if (allHoldings.get(i).owner == null || allHoldings.get(i).owner.name == null) {
				System.out.println("Error: Invalid owner");
			}
		}
		
		HoldingNameGen gen = new HoldingNameGen();
		
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			for (int j = 0; j < b.mapObjects.size();++j) {
				b.mapObjects.get(j).name = gen.generateNext();
			}
			b.name = b.mapObjects.get(0).name;
		}
	}
	
	public void redistributeElevation(boolean beach) {
		for (int x = 0; x < tiles.length; ++x) {
			for (int y = 0; y < tiles[x].length; ++y) {
				if (tiles[x][y].a != 0 && tiles[x][y].r != TileType.WATER.getInt()  && (beach || tiles[x][y].r == TileType.BEACH.getInt())) {
					int g = tiles[x][y].g;
					int neighbours = 1;
					
					if (x > 0) {
						neighbours++;
						g += tiles[x-1][y].g;
					}
					
					if (y > 0) {
						neighbours++;
						g += tiles[x][y-1].g;
					}
					
					if (x < tiles.length-1) {
						neighbours++;
						g += tiles[x+1][y].g;
					}
					
					if (y < tiles[x].length) {
						neighbours++;
						g += tiles[x][y+1].g;
					}
					
					g /= (int)(neighbours+0.5);
					
					tiles[x][y].g = g;
				} else if (tiles[x][y].a != 0 && tiles[x][y].r == TileType.WATER.getInt()) {
					int g = tiles[x][y].g;
					int neighbours = 1;
					
					if (x > 0 && (tiles[x-1][y].r == TileType.WATER.getInt() || tiles[x-1][y].r == TileType.BEACH.getInt())) {
						neighbours++;
						g += tiles[x-1][y].g;
					}
					
					if (y > 0 && (tiles[x][y-1].r == TileType.WATER.getInt() || tiles[x][y-1].r == TileType.BEACH.getInt())) {
						neighbours++;
						g += tiles[x][y-1].g;
					}
					
					if (x < tiles.length-1 && (tiles[x+1][y].r == TileType.WATER.getInt() || tiles[x+1][y].r == TileType.BEACH.getInt())) {
						neighbours++;
						g += tiles[x+1][y].g;
					}
					
					if (y < tiles[x].length && (tiles[x][y+1].r == TileType.WATER.getInt() || tiles[x][y+1].r == TileType.BEACH.getInt())) {
						neighbours++;
						g += tiles[x][y+1].g;
					}
					
					g /= (int)(neighbours+0.5);
					
					tiles[x][y].g = g;
				}
			}
		}
	}
	
	public void redistributeRoadElevation() {
		for (int x = 0; x < tiles.length; ++x) {
			for (int y = 0; y < tiles[x].length; ++y) {
				if (tiles[x][y].a != 0 && tiles[x][y].r == TileType.SOIL.getInt()) {
					int g = tiles[x][y].g;
					int neighbours = 1;
					
					if (x > 0) {
						neighbours++;
						g += tiles[x-1][y].g;
					}
					
					if (y > 0) {
						neighbours++;
						g += tiles[x][y-1].g;
					}
					
					if (x < tiles.length-1) {
						neighbours++;
						g += tiles[x+1][y].g;
					}
					
					if (y < tiles[x].length) {
						neighbours++;
						g += tiles[x][y+1].g;
					}
					
					g /= (int)(neighbours+0.5);
					
					tiles[x][y].g = g;
				}
			}
		}
	}
	
	public void generateSupplyHoldings() {
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (!a.hasMapObject) {
				MapObject h = null;
				if (a.biome == Biome.GRASSLAND && a.moisture >= 0.4 && Math.random() <= 0.5 && a.elevation/this.generatedMaxElevation <= 0.7 && !a.isBeachArea) {
					h = new MapObject(MapObjectType.FARM, a); 
				} else if (a.biome == Biome.FOREST && a.moisture >= 0.6 && Math.random() <= 0.6) {
					h = new MapObject(MapObjectType.TREE3, a);
				} else if (a.biome == Biome.GRASSLAND && a.moisture >= 0.3 && Math.random() <= 0.7 && a.elevation/this.generatedMaxElevation >= 0.4) {
					h = new MapObject(MapObjectType.CATTLE, a);
				}
				
				if (h != null) {
					Barony b = a.barony;
					h.x = (int)(a.getCenter().x+0.5);
					h.y = (int)(a.getCenter().y+0.5);
					b.mapObjects.add(h);
					allHoldings.add(h);
					a.supplies = 1;
					a.hasMapObject = true;
				}
			}
		}
		
		assignSupplies(0.93);
	}
	
	public void assignSupplies(double dec) {
		
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (a.supplies != 1) {
				
				Area nearest = null;
				for (int j = 0; j < allAreas.size(); ++j) {
					Area other = allAreas.get(j);
					if (other.supplies == 1) {
						if (	nearest == null 
							|| 	other.generator.distance(a.generator) <= nearest.generator.distance(a.generator)) {
							nearest = other;
						}
					}
				}
				
				if (nearest != null) {
					double distance = nearest.generator.distance(a.generator);
					a.supplies = Math.pow(dec, distance);
				}
				
			}
		}
	}
	
	public void generateVillages() {
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (!a.hasMapObject && a.type == AreaType.LAND && a.biome != Biome.BEACH) {
				MapObject h = null;
				if (a.biome != Biome.ROCK && a.supplies >= 0.5 && Math.random() <= 0.8) {
					h = new MapObject(MapObjectType.VILLAGE, a); 
				}
				
				if (h != null) {
					Barony b = a.barony;
					h.x = (int)(a.getCenter().x+0.5);
					h.y = (int)(a.getCenter().y+0.5);
					b.mapObjects.add(h);
					allHoldings.add(h);
					a.hasMapObject = true;
				}
			}
		}
	}
	
	private void generateWorkerHoldings() {
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			if (!a.hasMapObject && a.type == AreaType.LAND && a.biome != Biome.BEACH) {
				MapObject h = null;
				if (a.biome == Biome.ROCK && Math.random() <= 0.4) {
					h = new MapObject(MapObjectType.QUARRY, a); 
				} else if ((a.biome == Biome.ROCK || a.biome == Biome.DRYLAND) && Math.random() <= 0.6) {
					h = new MapObject(MapObjectType.MINE, a); 
				} else if (a.biome == Biome.FOREST && Math.random() <= 0.8) {
					h = new MapObject(MapObjectType.TREE1, a); 
				} else if (a.biome == Biome.GRASSLAND && Math.random() <= 0.3) {
					h = new MapObject(MapObjectType.HORSE, a); 
				} else if (a.supplies >= 0.3 && Math.random() <= 0.4) {
					h = new MapObject(MapObjectType.WINDMILL, a); 
				}
				
				if (h != null) {
					Barony b = a.barony;
					h.x = (int)(a.getCenter().x+0.5);
					h.y = (int)(a.getCenter().y+0.5);
					b.mapObjects.add(h);
					allHoldings.add(h);
					a.hasMapObject = true;
				}
			}
		}
	}

	public void checkCorrectness() {
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			for (int j = 0; j < b.mapObjects.size(); ++j) {
				MapObject o = b.mapObjects.get(j);
				boolean hasEntity = false;
				for (int x = 0; x < tiles.length; ++x) {
					for (int y = 0; y < tiles[x].length; ++y) {
						Color c = new Color(tileMap.getRGB(x, y));
						if (baronyMap.getRGB(x, y) == b.printColor.getRGB()) {
							if (c.getBlue() == o.type.getInt()) {
								hasEntity = true;
							}
						}
					}
				}
				
				if (!hasEntity) {
					System.out.println("Fatal Error!! Object without entity generated!");
				}
				
			}
		}
	}
	
	public void generateBeaches() {
		
		boolean change = true;
		
		while (change) {
			change = false;
			for (int i = 0; i < allAreas.size(); ++i) {
				Area a = allAreas.get(i);
				if (a.biome == Biome.BEACH && a.barony == null) {
					for (int j = 0; j < a.neighbours.size(); ++j) {
						Area other = a.neighbours.get(j);
						if (other.type == AreaType.LAND && other.barony != null) {
							other.elevation += 1;
							other.barony.areas.add(a);
							a.barony = other.barony;
							change = true;
							break;
						}
					}
				}
			}
		}
	}
	
	public void generateBaronyBounds(){
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony b = allBaronies.get(i);
			b.calcBounds();
			b.color = b.getGreedyColor();
		}
	}
	
	public void generateDetails() {
		generateTrees();
		generateGrass();
	}
	
	public void generateTrees() {
		
		int seed = new Random().nextInt();
		
		for (int x = 0; x < tiles.length; ++x) {
			for (int y = 0; y < tiles[x].length; ++y) {
				Area a = areaAssignment[x][y];
				if (	a != null 
					&& 	placeTrees(x,y,a,seed)
					&&	noObstruction(x,y)) {
					assignMapObject(x,y,MapObjectType.TREE2);
				}
			}
		}
	}
	
	public void assignMapObject(int x, int y, MapObjectType object) {
		Color c = new Color(tiles[x][y].r,tiles[x][y].g, 
							object.getInt(),tiles[x][y].a);
		tiles[x][y].b = object.getInt();
		tileMap.setRGB(x, y, c.getRGB());
	}
	
	public boolean noObstruction(int x, int y) {
		
		for (int x2 = x - 1; x2 <= x+1; ++x2) {
			for (int y2 = y - 1; y2 <= y+1; ++y2) {
				if (x2 >= 0 && y2 >= 0 && x2 < tiles.length && y2 < tiles[x].length) {
					if (tiles[x2][y2].r == TileType.WATER.getInt()) return false;
					if (	tiles[x2][y2].b != 0 
						&&  ((   x2 == x && y2 == y ) || (
						 	tiles[x2][y2].b != MapObjectType.TREE1.getInt()
						&&  tiles[x2][y2].b != MapObjectType.TREE2.getInt()
						&&  tiles[x2][y2].b != MapObjectType.TREE3.getInt()))) return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean placeTrees(int x, int y, Area a, int seed) {
		double prop = 0.001;
		
		if (a.biome == Biome.FOREST && a.barony.hasObject(MapObjectType.TREE3)) {
			prop = 0.2;
		} else if (a.biome == Biome.GRASSLAND) {
			prop = 0.005;
		} else if (a.biome == Biome.BEACH) {
			prop = 0.0001;
		} else if (a.biome == Biome.ROCK) {
			prop = 0;
		}
		
		return prop > Math.random();
	}
	
	public void generateGrass() {
		
		int seed = new Random().nextInt();
		
		for (int x = 0; x < tiles.length; ++x) {
			for (int y = 0; y < tiles[x].length; ++y) {
				Area a = areaAssignment[x][y];
				if (	a != null 
					&& 	placeGrass(x,y,a,seed)
					&&	tiles[x][y].r != TileType.WATER.getInt()
					&&  tiles[x][y].b != MapObjectType.FARM.getInt()) {
					assignGrass(x,y);
				}
			}
		}
	}
	
	public boolean placeGrass(int x, int y, Area a, int seed) {
		double prop = 0.001;
		
		if (a.biome == Biome.GRASSLAND) {
			if (a.hasMapObject) {
				prop = 0.01; 
			} else { 
				prop = 0.15;
			}
		} else if (a.biome == Biome.FOREST) {
			prop = 0.007;
		} else if (a.biome == Biome.BEACH) {
			prop = 0.0;
		} else if (a.biome == Biome.ROCK) {
			prop = 0;
		}
		
		return prop > Math.random();
	}
	
	public void assignGrass(int x, int y) {
		tiles[x][y].r = tiles[x][y].r | 0x01;
		Color c = new Color(tiles[x][y].r,tiles[x][y].g, 
							tiles[x][y].b,tiles[x][y].a);
		tileMap.setRGB(x, y, c.getRGB());
	}
	
	public void generateRoads() {
		for (int i = 0; i < allAreas.size(); ++i) {
			Area a = allAreas.get(i);
			for (int j = 0; j < a.neighbours.size(); ++j) {
				Area n = a.neighbours.get(j);
				if (	a.index < n.index && !a.isBeachArea && !n.isBeachArea && a.biome != Biome.WATER
					&&	n.biome != Biome.WATER) {
					a.roadDegree++;
					n.roadDegree++;
					Road r = new Road(a, n);
					roads.add(r);
				}
			}
		}
	}
}
