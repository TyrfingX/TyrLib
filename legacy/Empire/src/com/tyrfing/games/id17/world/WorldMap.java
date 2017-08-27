package com.tyrfing.games.id17.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.Army;
import com.tyrlib2.graphics.particles.ComplexParticleSystem;
import com.tyrlib2.graphics.particles.Emitter;
import com.tyrlib2.graphics.particles.ParticleSystem;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Direction4;


public class WorldMap implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1726267477592721994L;

	public static final int HUGE_DISTANCE = 999999;
	
	public final int width;
	public final int height;
	private transient House[][] houseMap;
	private transient Barony[][] baronyMap;
	public transient Tile[][] tileMap;
	
	private int distances[][];
	private transient int dynDistances[];
	private transient int fDistances[];
	private transient Integer pre[];
	private boolean areNeighboursBaronies[][];
	private boolean areNeighboursHoldings[][];
	private Barony neighboursBaronies[][];
	private int neihboursHoldingsIndex[][];
	private Holding neihboursHoldings[][];
	private RoadMap roadMap;
	
	private boolean built;
	

	private transient PriorityQueue<Integer> openList;
	private transient Set<Integer> closedList = new TreeSet<Integer>();
	
	private transient FogMap fogMap;
	
	public static final int UNEXPLORED_FACTOR = 4;

	private class DistanceComparator implements Comparator<Integer> {

		@Override
		public int compare(Integer i1, Integer i2) {
			if (fDistances[i1] < fDistances[i2]) return -1;
			if (fDistances[i1] > fDistances[i2]) return 1;
			return 0;
		}
		
	}
	
	public WorldMap(int width, int height) {
		this.width = width;
		this.height = height;
		createFogmap();
		createArrays();
		createRoadMap();
	}
	
	public void createFogmap() {
		if (fogMap == null) {
			fogMap = new FogMap(this, "FOG_MAP");
		}
	}
	
	public void createRoadMap() {
		if (roadMap == null) {
			roadMap = new RoadMap(width, height);
		}
	}
	
	public void createArrays() {
		houseMap = new House[width][height];
		baronyMap = new Barony[width][height];
		tileMap = new Tile[width][height];
	}
	
	public FogMap getFogMap() {
		return fogMap;
	}
	
	public Barony getBarony(int x, int y) {
		if (x < 0) return null;
		if (x >= width) return null;
		if (y < 0) return null;
		if (y >= height) return null;
		
		return baronyMap[x][y];
	}
	
	public House getHouse(int x, int y) {
		if (x < 0) return null;
		if (x >= width) return null;
		if (y < 0) return null;
		if (y >= height) return null;
		
		return houseMap[x][y];
	}
	
	public List<Boolean> getVisibleBorders(Barony barony) {
		List<Boolean> visible = new ArrayList<Boolean>();
		List<BorderBlock> coords = barony.getWorldChunk().getBorder().getCoords();
		House house = barony.getOwner();
		
		Vector3 pos = barony.getNode().getRelativePos();
		int baseX = (int) (pos.x / WorldChunk.BLOCK_SIZE) + width/2;
		int baseY = (int) (pos.y / WorldChunk.BLOCK_SIZE) + height/2;
		
		for (int i = 0; i < coords.size(); ++i) {
			int x = coords.get(i).x;
			int y = coords.get(i).y;
			Direction4 dir = coords.get(i).direction;
			
			if (dir == Direction4.LEFT) {
				House houseLeft = getHouse(x-1+baseX,y+baseY);
				if (houseLeft == null || !houseLeft.haveSameOverlordWith(house)) {
					visible.add(true);
					continue;
				} 
			} else if (dir == Direction4.RIGHT) {
				House houseRight = getHouse(x+1+baseX,y+baseY);
				if (houseRight == null || !houseRight.haveSameOverlordWith(house)) {
					visible.add(true);
					continue;
				}
			} else if (dir == Direction4.TOP) {
				House houseTop = getHouse(x+baseX,y-1+baseY);
				if (houseTop == null || !houseTop.haveSameOverlordWith(house)) {
					visible.add(true);
					continue;
				}
			} else if (dir == Direction4.BOTTOM) {
				House houseBottom = getHouse(x+baseX,y+1+baseY);
				if (houseBottom == null || !houseBottom.haveSameOverlordWith(house)) {
					visible.add(true);
					continue;
				}
			}
			
			visible.add(false);
		}
		
		return visible;
	}
	
	public void insertHouse(House house) {
		List<Barony> baronies = house.getBaronies();
		for (int i = 0; i < house.getBaronies().size(); ++i) {
			Barony barony = baronies.get(i);
			
			WorldChunk worldChunk = barony.getWorldChunk();
			
			Vector3 pos = barony.getNode().getRelativePos();
			int baseX = (int) (pos.x / WorldChunk.BLOCK_SIZE) + width/2;
			int baseY = (int) (pos.y / WorldChunk.BLOCK_SIZE) + height/2;
			
			for (int tile = 0, countTiles = worldChunk.getCountTiles(); tile < countTiles; ++tile) {
				Tile t = worldChunk.getTile(tile);
				if (tileMap[t.x+baseX][t.y+baseY] == null) {
					houseMap[t.x+baseX][t.y+baseY] = house;
					baronyMap[t.x+baseX][t.y+baseY] = barony;
					tileMap[t.x+baseX][t.y+baseY] = t;
					t.x = t.x+baseX;
					t.y = t.y+baseY;
					if (t.isRoad()) {
						roadMap.insertRoadPoint(t.x, t.y, t.holdingID);
					}
				} 
			}
		}
	}
	
	public void changeOwner(Barony barony) {
		
		WorldChunk worldChunk = barony.getWorldChunk();
		House house = barony.getOwner();
		
		for (int tile = 0, countTiles = worldChunk.getCountTiles(); tile < countTiles; ++tile) {
			Tile t = worldChunk.getTile(tile);
			houseMap[t.x][t.y] = house;
		}
	}
	
	public void constructEdgeList() {
		
		int numBaronies = World.getInstance().getCountBaronies();
		int numHoldings = World.getInstance().getHoldings().size();
		distances = new int[numHoldings][numHoldings];
		areNeighboursBaronies = new boolean[numBaronies][numBaronies];
		areNeighboursHoldings = new boolean[numHoldings][numHoldings];
		
		for (int i = 0; i < numHoldings; ++i) {
			for (int j = 0; j < numHoldings; ++j) {
				distances[i][j] = HUGE_DISTANCE;
			}
		}
		
		for (int i = 0; i < numHoldings; ++i) {
			distances[i][i] = 0;
		}
		
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (baronyMap[x][y] != null) {
					int index = baronyMap[x][y].getIndex();
					Barony baronyLeft = getBarony(x-1,y);
					Barony baronyRight = getBarony(x+1,y);
					Barony baronyTop = getBarony(x,y-1);
					Barony baronyBottom = getBarony(x,y+1);
			
					if (baronyLeft != null && baronyLeft != baronyMap[x][y]) {
						areNeighboursBaronies[index][baronyLeft.getIndex()] = true;
					} 
					
					if (baronyRight != null && baronyRight != baronyMap[x][y]) {
						areNeighboursBaronies[index][baronyRight.getIndex()] = true;
					} 
					
					if (baronyTop != null && baronyTop != baronyMap[x][y]) {
						areNeighboursBaronies[index][baronyTop.getIndex()] = true;
					} 
					
					if (baronyBottom != null && baronyBottom != baronyMap[x][y]) {
						areNeighboursBaronies[index][baronyBottom.getIndex()] = true;
					} 
					
				}
			}
			
		}
		
		neighboursBaronies = new Barony[numBaronies][];
		neihboursHoldingsIndex = new int[numHoldings][];
		neihboursHoldings = new Holding[numHoldings][];
		
		for (int i = 0; i < numBaronies; ++i) {
			int numNeighbours = 0;
			for (int j = 0; j < numBaronies; ++j) {
				if (areNeighboursBaronies[i][j]) {
					numNeighbours++;
				}
			}
			
			neighboursBaronies[i] = new Barony[numNeighbours];
			int neighbour = 0;
			
			for (int j = 0; j < numBaronies; ++j) {
				if (areNeighboursBaronies[i][j]) {
					neighboursBaronies[i][neighbour++] = World.getInstance().getBarony(j);
				}
			}
			
		}
		
		SceneManager.getInstance().getRootSceneNode().update();
		World.getInstance().getOctree().update();
		
		System.out.println("Filling distances..");
		
		for (int i = 0; i < numBaronies; ++i) {
			Barony barony = World.getInstance().getBarony(i);
			Barony[] neighbours = this.getNeighbours(barony);
			
			int countChecks = 0;
			countChecks += barony.getCountSubHoldings(); 
			
			for (int k = 0; k < neighbours.length; ++k) {
				countChecks += neighbours[k].getCountSubHoldings();
			}
			
			AABB[] checks = new AABB[countChecks];
			countChecks = 0;
			for (int j = 0; j < barony.getCountSubHoldings(); ++j) {
				checks[countChecks++] = barony.getSubHolding(j).holdingData.worldEntity.getBoundingBox();
			}
			
			for (int k = 0; k < neighbours.length; ++k) {
				for (int j = 0; j < neighbours[k].getCountSubHoldings(); ++j) {
					checks[countChecks++] = neighbours[k].getSubHolding(j).holdingData.worldEntity.getBoundingBox();
				}
			}
			
			for (int j = 0; j < barony.getCountSubHoldings(); ++j) {
				Holding holding = barony.getSubHolding(j);
				fillDistances(holding, barony, neighbours, checks);
				
				for (int k = 0; k < neighbours.length; ++k) {
					fillDistances(holding, neighbours[k], neighbours, checks);
				}
			}
		}
		
		for (int i = 0; i < numHoldings; ++i) {
			int numNeighbours = 0;
			for (int j = 0; j < numHoldings; ++j) {
				if (distances[i][j] != HUGE_DISTANCE && distances[i][j] != 0) {
					numNeighbours++;
				}
			}
			
			neihboursHoldingsIndex[i] = new int[numNeighbours];
			neihboursHoldings[i] = new Holding[numNeighbours];
			int neighbour = 0;
			for (int j = 0; j < numHoldings; ++j) {
				if (distances[i][j] != HUGE_DISTANCE && distances[i][j] != 0) {
					neihboursHoldingsIndex[i][neighbour] = j;
					neihboursHoldings[i][neighbour++] = World.getInstance().getHoldings().get(j);
				}
			}
		}
		
		System.out.println("Finished filling distances");
	}
	
	private void fillDistances(final Holding holding, final Barony barony, final Barony[] neighbours, final AABB[] checks) {
		
		int index = holding.getHoldingID();
		
		for (int k = 0; k < barony.getCountSubHoldings(); ++k) {
			final Holding other = barony.getSubHolding(k);
			if (holding != other) {
				int indexOther = other.getHoldingID();
				List<RoadNode> path = roadMap.hasDirectRealizeablePath(index, indexOther);
				
				if (path != null) {
					int distance = path.size();
					distances[index][indexOther] = distance;
					distances[indexOther][index] = distance;
					areNeighboursHoldings[index][indexOther] = true;
					areNeighboursHoldings[indexOther][index] = true;
				}
			}
		}
	}
	
	public void constructShortestPaths() {
		System.out.print("Constructing shortest paths... " );
		List<Holding> holdings = World.getInstance().getHoldings();
		int numHoldings = holdings.size();
		
		for (int i = 0; i < numHoldings; ++i) {
			Vector3 pos1 =  holdings.get(i).holdingData.worldEntity.getAbsolutePos();
			for (int j = i+1; j < numHoldings; ++j) {
				Vector3 pos2 =  holdings.get(j).holdingData.worldEntity.getAbsolutePos();
				int length = (int) Vector3.length(pos1.x-pos2.x, pos1.y-pos2.y, pos1.z-pos2.z);
				distances[i][j] = length;
				distances[j][i] = length;
			}
		}
		System.out.println("... finished constructing paths.");
	}
	
	public List<Integer> getPath(House armyOwner, int start, int end) {
		
		int numHoldings = World.getInstance().getHoldings().size();
		dynDistances = new int[numHoldings];
		pre = new Integer[numHoldings];
		fDistances = new int[numHoldings];
		
		for (int i = 0; i < numHoldings; ++i) {
			if (i != start) {
				dynDistances[i] = HUGE_DISTANCE;
			}
		}
		
		openList = new PriorityQueue<Integer>(numHoldings, new DistanceComparator());
		closedList = new TreeSet<Integer>();
		
		openList.add(start);
		Integer current = null;
		
		while (!openList.isEmpty()) {
			current = openList.poll();
			if (current.equals(end)) {
				break;
			}
			
			closedList.add(current);
			
			expandNode(armyOwner, current, end);
		}
		
		List<Integer> path = new ArrayList<Integer>();
		
		while (current != null) {
			if (!current.equals(start)) {
				path.add(0, current);
			}
			current = pre[current];
		}
		
		return path;
	}
	
	private void expandNode(House armyOwner, Integer node, int end) {
		for (int i = 0; i < neihboursHoldingsIndex[node].length; ++i) {
			int j = neihboursHoldingsIndex[node][i];
			Army army = neihboursHoldings[node][i].getMainPositionedArmy();
			if ((army == null || i == end || (army.getOwner().isEnemy(armyOwner) != null && (!army.isFighting() || army.getBattle().isSiege())))  && !closedList.contains(j)) {
				int newDistance = dynDistances[node] + 10 * (armyOwner.isVisible(j) ? 1 : UNEXPLORED_FACTOR) ;
				if (openList.contains(j) && newDistance >= dynDistances[j]) {
					continue;
				}
				
				pre[j] = node;
				dynDistances[j] = newDistance;
				fDistances[j] = newDistance + distances[j][end];
				
				openList.add(j);
			}
		}
	}
	
	public int getDistance(int start, int end) {
		return distances[start][end];
	}
	
	public boolean isNeighbour(Barony barony1, Barony barony2) {
		return areNeighboursBaronies[barony1.getIndex()][barony2.getIndex()];
	}
	
	public Barony[] getNeighbours(Barony barony) {
		return neighboursBaronies[barony.getIndex()];
	}
	
	public Holding[] getNeighboursHolding(Holding holding) {
		return neihboursHoldings[holding.getHoldingID()];
	}
	
	public void constructWaterfalls() {
		System.out.print("Constructing waterfalls... ");
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				Tile t = tileMap[x][y];
				if (t != null && t.type == 3) {
					Tile t2;
					
					if (x - 1 >= 0) {
						t2 = tileMap[x-1][y];
						if (t2 != null && Math.abs(t2.height - t.height+8) < 0.01f && t2.type == 3 && t.height > t2.height) {
							createWaterfall(x, y, Direction4.LEFT);
						}
					}
					
					if (x + 1 < width) {
						t2 = tileMap[x+1][y];
						if (t2 != null && Math.abs(t2.height - t.height+8) < 0.01f && t2.type == 3 && t.height > t2.height) {
							createWaterfall(x, y, Direction4.RIGHT);
						}
					}
					
					if (y - 1 >= 0) {
						t2 = tileMap[x][y-1];
						if (t2 != null && Math.abs(t2.height - t.height+8) < 0.01f && t2.type == 3 && t.height > t2.height) {
							createWaterfall(x, y, Direction4.TOP);
						}
					}
					
					if (y + 1 < height) {
						t2 = tileMap[x][y+1];
						if (t2 != null && Math.abs(t2.height - t.height+8) < 0.01f && t2.type == 3 && t.height > t2.height) {
							createWaterfall(x, y, Direction4.BOTTOM);
						}
					}
				}
			}
		}
		System.out.println("... finished constructing waterfalls");
	}
	
	public void createWaterfall(int x, int y, Direction4 dir) {
		Vector3 pos = new Vector3((x+0.5f- width/2) * WorldChunk.BLOCK_SIZE , (y+0.5f- height/2) * WorldChunk.BLOCK_SIZE , tileMap[x][y].getScaledHeight());
		ParticleSystem waterfall = SceneManager.getInstance().createParticleSystem("particle/waterfall.xml");
		SceneManager.getInstance().getRootSceneNode().createChild(pos).attachSceneObject(waterfall);
		
		ParticleSystem waterfallSmoke = SceneManager.getInstance().createParticleSystem("particle/waterfallsmoke.xml");
		SceneManager.getInstance().getRootSceneNode().createChild(pos).attachSceneObject(waterfallSmoke);
		World.getInstance().addWaterfall((ComplexParticleSystem)waterfall, (ComplexParticleSystem)waterfallSmoke);
		
		adjustEmitter(waterfall.getEmitter(0), dir);
		adjustEmitter(waterfallSmoke.getEmitter(0), dir);
		
		Barony b = this.getBarony(x, y);
		b.addWaterfall(waterfall, waterfallSmoke);
	}
	
	private void adjustEmitter(Emitter emitter, Direction4 dir) {
		Vector3 velocity = new Vector3(emitter.getVelocity());
		Vector3 rndVelocity = new Vector3(emitter.getRandomVelocity());
		Vector3 pPos = new Vector3(emitter.getRelativePos());
		Vector3 rndPos = new Vector3(emitter.getRandomPos());
		
		float velocityY = velocity.y;
		float rndVelocityY = rndVelocity.y;
		float pPosY = pPos.y;
		float rndPosY = rndVelocity.y;
		
		switch(dir) {
		case LEFT:
			velocity.y = velocity.x;
			rndVelocity.y = rndVelocity.x;
			pPos.y = pPos.x;
			rndPos.y = rndPos.x;
			
			velocity.x = -velocityY;
			rndVelocity.x = -rndVelocityY;
			pPos.x = -pPosY;
			rndPos.x = -rndPosY;
			
			break;
		case RIGHT:
			
			velocity.y = velocity.x;
			rndVelocity.y = rndVelocity.x;
			pPos.y = pPos.x;
			rndPos.y = rndPos.x;
			
			velocity.x = -velocityY;
			rndVelocity.x = -rndVelocityY;
			pPos.x = -pPosY;
			rndPos.x = -rndPosY;
			break;
		case TOP:

			velocity.y = -velocityY;
			rndVelocity.y = -rndVelocityY;
			pPos.y = -pPosY;
			rndPos.y = -rndPosY;
			break;
		case BOTTOM:
			break;
		}
		
		emitter.setVelocity(velocity);
		emitter.setRandomVelocity(rndVelocity);
		emitter.setRelativePos(pPos);
		emitter.setRandomPos(rndPos);
	}
	
	public boolean isBuilt() {
		return built;
	}
	
	public void finishBuild() {
		built = true;
	}
	
	public float getHeight(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height || tileMap[x][y] == null) {
			return 0;
		}
		
		return tileMap[x][y].getScaledHeight();
	}

	public void constructWaterFlow() {
		System.out.print("Constructing Waterflow... ");
		Queue<Tile> sourceTiles = new LinkedList<Tile>();
		Set<Tile> closedSet =  new HashSet<Tile>();
		
		for (int x = 0; x < width; ++x)  {
			for (int y = 0; y < height; ++y)  {
				
				// Go "backwards", source tiles are tiles where water def. flows downwards
				// Closed Tiles are finished tiles we wont be looking at again
				
				Tile t = tileMap[x][y];
				if (t != null && t.type == 3) {
					
					Renderable r = t.chunk.getWorldChunkRenderable(2);
					
					if (r == null) continue;
					
					Mesh mesh = t.chunk.getWorldChunkRenderable(2).getMesh();
					
					boolean sourceTile = false;
					
					for (int i = 0; i < 8; ++i) {
						Tile other = getNeighbour(x,y,i);
						if (other == null || (other.height < t.height && !other.marked)) {
							sourceTile = true;
							
							int index = t.meshPos+t.chunk.blockTypes[2].mat.getNormalOffset();
							float valueX = 0;
							float valueY = 0;
							
							if (i == 0 || i == 3 || i == 5) {
								valueX = 1;
							} else if (i == 2 || i == 4 || i ==  7) {
								valueX = -1;
							}
							
							if (i <= 3) {
								valueY = 1;
							} else if (i >= 5) {
								valueY = -1;
							}
							
							for (int j = 0; j < 20; ++j) {
								mesh.setVertexInfo(index, mesh.getVertexInfo(index)+valueX);
								mesh.setVertexInfo(index+1, mesh.getVertexInfo(index+1)+valueY);
								index += t.chunk.blockTypes[2].mat.getByteStride();
							}
							
							if (other != null && other.type == 3) {
								sourceTiles.offer(other);
								other.marked = true;
							}
						}
					}
					
					if (sourceTile) {
						closedSet.add(t);
					}
					
				}
			}
		}
		
		while (!sourceTiles.isEmpty()) {
			Tile t = sourceTiles.poll();
			Renderable r = t.chunk.getWorldChunkRenderable(2);
			
			if (r == null) continue;
			
			Mesh mesh = t.chunk.getWorldChunkRenderable(2).getMesh();
			
			for (int i = 0; i < 8; ++i) {
				Tile other = getNeighbour(t.x,t.y,i);
				if (other != null && closedSet.contains(other)) {
					
					int index = t.meshPos+t.chunk.blockTypes[2].mat.getNormalOffset();
					float valueX = 0;
					float valueY = 0;
					
					if (i == 0 || i == 3 || i == 5) {
						valueX = 1;
					} else if (i == 2 || i == 4 || i ==  7) {
						valueX = -1;
					}
					
					if (i <= 3) {
						valueY = 1;
					} else if (i >= 5) {
						valueY = -1;
					}
					
					for (int j = 0; j < 12; ++j) {
						mesh.setVertexInfo(index, mesh.getVertexInfo(index)+valueX);
						mesh.setVertexInfo(index+1, mesh.getVertexInfo(index+1)+valueY);
						index += t.chunk.blockTypes[2].mat.getByteStride();
					}

				} else if (other != null && other.type == 3 && !sourceTiles.contains(other) && other.height == t.height) {
					sourceTiles.offer(other);
				}
			}
			
			closedSet.add(t);
			
		}
		
		System.out.println(" ... finished constructing Waterflow");
		
	}
	
	public Tile getNeighbour(int x, int y, int i) {
		if (i <= 2) {
			return getTile(x-1+i,y-1);
		} else if (i >= 5) {
			return getTile(x-1+i-5,y+1);
		} else if (i == 3){
			return getTile(x-1,y);
		} else {
			return getTile(x+1,y);
		}
	}
	
	public Tile getTile(int x, int y) {
		if (x >= 0 && y >= 0) {
			if (x < width && y < height) {
				return tileMap[x][y];
			}
		}
		
		return null;
	}
	
	public RoadMap getRoadMap() {
		return roadMap;
	}

	public Tile getTileFromWorldCoord(Vector3 pos) {
		int baseX = (int) (pos.x / WorldChunk.BLOCK_SIZE) + width/2;
		int baseY = (int) (pos.y / WorldChunk.BLOCK_SIZE) + height/2;
		
		return getTile(baseX, baseY);
	}

	public boolean isHoldingNeighbour(Holding h, Holding n) {
		return areNeighboursHoldings[h.getHoldingID()][n.getHoldingID()];
	}
}
