package com.tyrfing.games.id17.world;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.geometry.Grass;
import com.tyrfing.games.id17.holdings.BaronyWindow;
import com.tyrfing.games.id17.world.Border.Status;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderer.BoundedRenderable;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.renderer.UParam1f;
import com.tyrlib2.graphics.renderer.VertexLayout;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Direction4;
import com.tyrlib2.util.Valuef;

public class WorldChunk extends BoundedRenderable {
	
	private Tile blockMatrix[][];
	
	public static final float BLOCK_SIZE = 1.875f;
	public static final float HEIGHT_FACTOR = 0.1f;
	
	public final TileMaterial[] blockTypes = new TileMaterial[8];
	public final TileMaterial[] blockTypes_occu = new TileMaterial[8];
	public static final List<String> objectTypes = new ArrayList<String>();
	public static final List<Integer> mainObjectTypes = new ArrayList<Integer>();
	public static final List<String> seasonalObjectTypes = new ArrayList<String>();
	private int width;
	private int height;
	private SceneNode parent;
	
	private AABB aabb;
	
	public static final short[] DRAW_ORDER_NEW = { 1, 3, 2, 0, 1, 2 };
	public static final short[] DRAW_ORDER_REV = { 2, 1, 0, 2, 3, 1 };
	
	public static final Vector3[] NORMALS = {
		new Vector3(0,0,-1), 
		new Vector3(1,0,-1).unitVector(),
		new Vector3(1,1,-1).unitVector(),
		new Vector3(0,1,-1).unitVector(),
		new Vector3(-1,1,-1).unitVector(),
		new Vector3(-1,0,-1).unitVector(),
		new Vector3(-1,-1,-1).unitVector(),
		new Vector3(0,-1,-1).unitVector(),
		new Vector3(1,-1,-1).unitVector(),
	};
	
	public static final int HOLDING_MASK = 2;
	public static final int ARMY_MASK = 3;
	public static final int CHUNK_MASK = 10;
	
	private List<Entity> objects = new ArrayList<Entity>();
	private List<Entity> mainObjects = new ArrayList<Entity>();
	private List<Tile> mainObjectTiles = new ArrayList<Tile>();
	private List<BorderBlock> defaultBorderCoordinates;

	public int[] countTiles;
	
	private List<Renderable> worldChunks = new ArrayList<Renderable>();
	private List<Integer> chunkTypes = new ArrayList<Integer>();
	private List<Tile> tiles = new ArrayList<Tile>();
	
	public static final int MAX_OBJECTS = 20;
	private Entity[][] objectBuckets;
	private int[] objectCounts;
	
	private Grass[][] grassBuckets;
	private int[] grassCounts;
	
	private Renderable[] worldChunkBuckets;
	
	private Border border;
	
	private Entity castleEntity;
	private Tile castleTile;
	
	public static final int WATER_CHUNK_SIZE = 50;
	public static final int CHUNK_SIZE = 100;
	
	//public static final int MAX_SIZE = 800;
	
	private Valuef ownerValue = new Valuef(SeasonMaterial.NONE);

	private int insertionID;
	
	private static VertexLayout VERTEX_LAYOUT;
	private static final int OWNER_INFO = 4;
	
	private static final float MIN_HEIGHT = -2*BLOCK_SIZE*HEIGHT_FACTOR;
	
	//ShortBuffer indexBuffer;
	//int indexCount;
	
	public WorldChunk(int width, int height, SceneNode node) {
		init();
		this.width = width;
		this.height = height;
		this.parent = node;
		blockMatrix = new Tile[width][height];
		countTiles = new int[blockTypes.length];
		
		objectBuckets = new Entity[objectTypes.size()][width*height];
		objectCounts = new int[objectTypes.size()];
		
		grassBuckets = new Grass[2][width*height];
		grassCounts = new int[2];
		
		worldChunkBuckets = new Renderable[blockTypes.length];
	}
	
	public void setBlock(int x, int y, Tile tile) {
		if (blockMatrix[x][y] == null) {
			countTiles[tile.type-1]++;
			tiles.add(tile);
		}
		
		tile.x = x;
		tile.y = y;
		blockMatrix[x][y] = tile;
	}
	
	public void addObject(int x, int y, int objectID) {
		float height = getHeight(x,y) - 0.1f;
		
		boolean seasonalObject = seasonalObjectTypes.contains(objectTypes.get(objectID-1)) && !SceneManager.getInstance().getRenderer().isInServerMode();
		
		Entity object = 	seasonalObject ?
							SceneManager.getInstance().createEntity(objectTypes.get(objectID-1), 
																	!SceneManager.getInstance().getRenderer().isInServerMode(),
																	0)
						:	SceneManager.getInstance().createEntity(objectTypes.get(objectID-1), 
																	!SceneManager.getInstance().getRenderer().isInServerMode());
		
		object.setCastShadow(true);
							
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			DefaultMaterial3 mat = (DefaultMaterial3)object.getSubEntity(0).getMaterial();
			
			if (seasonalObject) {
				String textureName = mat.getTextureName();
				String winTextureName = textureName + "_WIN";
				Texture winTexture = TextureManager.getInstance().getTexture(winTextureName);
				
				if (winTexture == null) {
					throw new RuntimeException("WorldChunk::addObject Could not find loaded texture of name " + winTexture);
				}
				
				SeasonMaterial seasonMat = new SeasonMaterial(ProgramManager.getInstance().getProgram("SEASON_PROGRAM"), this, winTextureName, mat.getTextureName(), 1, 1, null, false);
				seasonMat.addParam(new UParam1f("u_Terrain", new Valuef(0.0f)));
				object.getSubEntity(0).setMaterial(seasonMat);
			} else {
				mat = (DefaultMaterial3) mat.copy();
				object.getSubEntity(0).setMaterial(mat);
				if (mat.isAnimated())  {
					mat.setProgram(ProgramManager.getInstance().getProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME+"_ANIMATED"));
				} else {
					mat.setProgram(ProgramManager.getInstance().getProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME));
				}
			}
				
			mat.addParam(new UParam1f("u_Owner", ownerValue));
			SceneManager.getInstance().getRenderer().removeRenderable(object);
		}
		
		SceneNode node = parent.createChild(x * BLOCK_SIZE, y * BLOCK_SIZE, height);
		node.attachSceneObject(object);
		//object.setBoundingBoxVisible(true);
		//node.scale(new Vector3(-0.8f, -0.8f, -0.8f));
		
		if (objectID-1 == 0) {
			castleEntity = object;
			castleTile = blockMatrix[x][y];
		}
		
		object.setMask(HOLDING_MASK);
		objects.add(object);
		
		if (mainObjectTypes.contains(objectID-1)) {
			mainObjects.add(object);
			mainObjectTiles.add(blockMatrix[x][y]);
		}
		
		objectBuckets[objectID-1][objectCounts[objectID-1]++] = object;
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (object.hasAnimation("Idle")) {
				object.playAnimation("Idle");
				World.getInstance().getUpdater().addItem(object);
			}
		}
		
		WorldObject worldObject = new WorldObject(object);
		node.attachSceneObject(worldObject);
		World.getInstance().getOctree().addObject(worldObject);
		
	}
	
	public float getHeight(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height || blockMatrix[x][y] == null) {
			return 0;
		}
		
		return blockMatrix[x][y].getScaledHeight();
	}
	
	public void build(){
		
		defaultBorderCoordinates = new ArrayList<BorderBlock>();
		
		float lightZ = SceneManager.getInstance().getLight(0).getLightVector()[0];
		
		Vector3 min = new Vector3();
		Vector3 max = new Vector3();
			
		WorldMap map = World.getInstance().getMap();
		
		float maxHeight = 0;
		for (int j = 0; j<blockTypes.length; ++j) {
			
			SeasonMaterial mat = blockTypes[j].mat;
			int byteStride = mat.getByteStride();
			int diffuseOffset = DefaultMaterial3.DIFFUSE_OFFSET;
			int normalOffset = mat.getNormalOffset();
			int uvOffset = mat.getUVOffset();
			
			int sideFaces = blockTypes[j].hasSides ? 4 : 0;
			int countPoints = 4 * (sideFaces + mat.countLayers);
			
			float[] blockData = new float[countTiles[blockTypes[j].baseTile] * countPoints * byteStride];
			short[] indexData = new short[(DRAW_ORDER_NEW.length * (sideFaces + mat.countLayers)) * countTiles[blockTypes[j].baseTile]];
			
			int arrPos = 0;
			int drawOrderPos = 0;
			for (int x = 0; x < width;  ++x) {
				
				int arrPosStart = arrPos;
				int arrPosStartX = arrPos;
				Tile startTileX = null;
				for (int y = 0; y < height;  ++y) {
					Tile tile = blockMatrix[x][y];
					if (tile != null && tile.type-1 == blockTypes[j].baseTile) {
	
						float tileHeight = tile.height*BLOCK_SIZE*HEIGHT_FACTOR;
						maxHeight = Math.max(tileHeight, maxHeight);
						
						Tile tX = map.getTile(tile.x-1, tile.y);
						Tile tY = map.getTile(tile.x, tile.y+1);
						
						Tile tX2 = map.getTile(tile.x+1, tile.y);
						Tile tY2 = map.getTile(tile.x, tile.y-1);
						
						float bottom = tile.height;
						
						if (tX == null || tX2 == null) {
							bottom = MIN_HEIGHT;
						} else if (tY == null || tY2 == null) {
							bottom = MIN_HEIGHT;
						} else {
							bottom = Math.min(bottom, Math.min(tX.height,tX2.height));
							bottom = Math.min(bottom, Math.min(tY.height,tY2.height));
							bottom -= 2;
							bottom *= BLOCK_SIZE*HEIGHT_FACTOR;
						}
						
						float stretch = 0;
						
						// Create a new block
						min.set((x-stretch)*BLOCK_SIZE, (y-stretch)*BLOCK_SIZE, bottom);
						max.set((x+1+stretch)*BLOCK_SIZE, (y+1+stretch)*BLOCK_SIZE, tileHeight);
						
						
						int pos = arrPos*byteStride*countPoints;
						
						// SIDES
						if (blockTypes[j].hasSides) {
							blockData[pos] = min.x; blockData[pos+1] = min.y; blockData[pos+2] = min.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[8]; blockData[pos+uvOffset+1] = blockTypes[j].uv[9]; 
							pos += byteStride;
							
							blockData[pos] = min.x; blockData[pos+1] = min.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[10]; blockData[pos+uvOffset+1] = blockTypes[j].uv[11]; 
							pos += byteStride;
							
							blockData[pos] = min.x; blockData[pos+1] = max.y; blockData[pos+2] = min.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[12]; blockData[pos+uvOffset+1] = blockTypes[j].uv[13]; 
							pos += byteStride;
							
							blockData[pos] = min.x; blockData[pos+1] = max.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[14]; blockData[pos+uvOffset+1] = blockTypes[j].uv[15]; 
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = max.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[16]; blockData[pos+uvOffset+1] = blockTypes[j].uv[17]; 
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = max.y; blockData[pos+2] = min.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[18]; blockData[pos+uvOffset+1] = blockTypes[j].uv[19]; 
							pos += byteStride;
							
							blockData[pos] = min.x; blockData[pos+1] = max.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[20]; blockData[pos+uvOffset+1] = blockTypes[j].uv[21]; 
							pos += byteStride;
							
							blockData[pos] = min.x; blockData[pos+1] = max.y; blockData[pos+2] = min.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[22]; blockData[pos+uvOffset+1] = blockTypes[j].uv[23]; 
							pos += byteStride;
							
							
							// Hidden faces
							
							blockData[pos] = max.x; blockData[pos+1] = min.y; blockData[pos+2] = min.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[8]; blockData[pos+uvOffset+1] = blockTypes[j].uv[9]; 
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = min.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[10]; blockData[pos+uvOffset+1] = blockTypes[j].uv[11]; 
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = max.y; blockData[pos+2] = min.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[12]; blockData[pos+uvOffset+1] = blockTypes[j].uv[13]; 
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = max.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[14]; blockData[pos+uvOffset+1] = blockTypes[j].uv[15]; 
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = min.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[16]; blockData[pos+uvOffset+1] = blockTypes[j].uv[17]; 
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = min.y; blockData[pos+2] = min.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[18]; blockData[pos+uvOffset+1] = blockTypes[j].uv[19]; 
							pos += byteStride;
							
							blockData[pos] = min.x; blockData[pos+1] = min.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[20]; blockData[pos+uvOffset+1] = blockTypes[j].uv[21]; 
							pos += byteStride;
							
							blockData[pos] = min.x; blockData[pos+1] = min.y; blockData[pos+2] = min.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[22]; blockData[pos+uvOffset+1] = blockTypes[j].uv[23]; 
							pos += byteStride;
						
						}
						
						// TOP for each layer
						
						max.z += mat.grassHeight;
						
						for (int i = 0; i < mat.countLayers; ++i) {
							blockData[pos] = min.x; blockData[pos+1] = min.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[0]; blockData[pos+uvOffset+1] = blockTypes[j].uv[1];
					
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = min.y; blockData[pos+2] = max.z;
							blockData[pos+uvOffset] = blockTypes[j].uv[2]; blockData[pos+uvOffset+1] = blockTypes[j].uv[3]; 
							pos += byteStride;
							
							blockData[pos] = min.x; blockData[pos+1] = max.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[4]; blockData[pos+uvOffset+1] = blockTypes[j].uv[5]; 
							pos += byteStride;
							
							blockData[pos] = max.x; blockData[pos+1] = max.y; blockData[pos+2] = max.z; 
							blockData[pos+uvOffset] = blockTypes[j].uv[6]; blockData[pos+uvOffset+1] = blockTypes[j].uv[7]; 
							pos += byteStride;
							
							max.z += mat.grassHeight;
						
						}
						
						pos = arrPos*byteStride*countPoints;
						
						if (j != 2) {
							for (int i = 0; i < countPoints; ++i) {
								if (i < 16 && i != 1 && i != 3 && i != 4 && i != 6 && blockTypes[j].hasSides) {
									blockData[pos + i*byteStride + diffuseOffset + 0] = -lightZ * ((float)(bottom+15)/50);
								} else {
									blockData[pos + i*byteStride + diffuseOffset + 0] = -lightZ * ((float)(tile.height+15)/50);
								}
								
								if (mat.getVertexLayout().getPos(VertexLayout.NORMAL) != -1) {
									blockData[pos + i*byteStride + normalOffset + 0] = 0;
									blockData[pos + i*byteStride + normalOffset + 1] = 0;
									blockData[pos + i*byteStride + normalOffset + 2] = (float) (i/4)/mat.countLayers;
								}
							}
						} else {
							for (int i = 0; i < countPoints; ++i) {
								blockData[pos + i*byteStride + normalOffset + 0] = 0;
								blockData[pos + i*byteStride + normalOffset + 1] = 0;
							}
						}

						
						tile.meshPos = pos;
						tile.chunk = this;
						
						boolean makeXFace = tX == null || tX.height < tile.height;
						if (makeXFace && blockTypes[j].hasSides) {
							
							if (startTileX == null) {
								startTileX = tile;
								arrPosStartX = arrPos;
							}
							
							Tile tXNext = map.getTile(tile.x-1, tile.y+1);
							boolean nextXSegment = (tXNext != null && (tX == null || tXNext.height != tX.height));
							
							if (nextXSegment || tY == null || tY.height != tile.height || tY.type != tile.type || y == height-1 || blockMatrix[x][y+1] == null || tile.type == 3) {
								for (int k = 0; k < DRAW_ORDER_NEW.length; ++k) {
									if (DRAW_ORDER_NEW[k] == 0) {
										indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPosStartX*countPoints); 
									} else if (DRAW_ORDER_NEW[k] == 1) {
										indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPosStartX*countPoints); 
									} else if (DRAW_ORDER_NEW[k] == 2) {
										indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPos*countPoints); 
									} else {
										indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPos*countPoints); 
									}
								}
								drawOrderPos += DRAW_ORDER_NEW.length;
								startTileX  = null;
							}
						}
						
						if (tY == null || tY.height != tile.height || tY.type != tile.type || y == height-1 || blockMatrix[x][y+1] == null || tile.type == 3 || tile.type == 1) {
							
							for (int i = 0; i < mat.countLayers; ++i) {
								for (int k = 0; k < DRAW_ORDER_NEW.length; ++k) {
									if (DRAW_ORDER_NEW[k] == 0) {
										indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPosStart*countPoints + sideFaces*4 + i*4); 
									} else if (DRAW_ORDER_NEW[k] == 1) {
										indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPosStart*countPoints + sideFaces*4 + i*4); 
									} else if (DRAW_ORDER_NEW[k] == 2) {
										indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPos*countPoints + sideFaces*4 + i*4); 
									} else {
										indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPos*countPoints + sideFaces*4 + i*4); 
									}
								}
								
								drawOrderPos += DRAW_ORDER_NEW.length;
							}
						
							arrPos++;
							arrPosStart = arrPos;
						} else {
							arrPos++;
						}
						
						if (blockTypes[j].hasSides) {
							if (x+1 >= width || blockMatrix[x+1][y] == null) {
								addDefaultBorderBlock(x,y, Direction4.RIGHT);
							}
							if (y+1 >= height || blockMatrix[x][y+1] == null) {
								addDefaultBorderBlock(x,y, Direction4.BOTTOM);
							}
							if (x-1 < 0 || blockMatrix[x-1][y] == null) {
								addDefaultBorderBlock(x,y, Direction4.LEFT);
							}
							if (y-1 < 0 || blockMatrix[x][y-1] == null) {
								addDefaultBorderBlock(x,y, Direction4.TOP);
							}
						}
					}
					
				}		
			}
			
			if (blockTypes[j].hasSides) {
			
				for (int x = 0; x < width;  ++x) {
					for (int y = 0; y < height;  ++y) {
						Tile tile = blockMatrix[x][y];
						if (tile != null && tile.type-1 == j) {
							arrPos = tile.meshPos/byteStride;
							
							for (int k = 0; k < DRAW_ORDER_REV.length; ++k) {
								if (DRAW_ORDER_REV[k] == 0) {
									indexData[drawOrderPos + k] = (short) (DRAW_ORDER_REV[k] + arrPos + 8); 
								} else if (DRAW_ORDER_REV[k] == 1) {
									indexData[drawOrderPos + k] = (short) (DRAW_ORDER_REV[k] + arrPos + 8); 
								} else if (DRAW_ORDER_REV[k] == 2) {
									indexData[drawOrderPos + k] = (short) (DRAW_ORDER_REV[k] + arrPos + 8); 
								} else {
									indexData[drawOrderPos + k] = (short) (DRAW_ORDER_REV[k] + arrPos + 8); 
								}
							}
							drawOrderPos += DRAW_ORDER_REV.length;
							
							for (int k = 0; k < DRAW_ORDER_REV.length; ++k) {
								if (DRAW_ORDER_REV[k] == 0) {
									indexData[drawOrderPos + k] = (short) (DRAW_ORDER_REV[k] + arrPos + 12); 
								} else if (DRAW_ORDER_REV[k] == 1) {
									indexData[drawOrderPos + k] = (short) (DRAW_ORDER_REV[k] + arrPos + 12); 
								} else if (DRAW_ORDER_REV[k] == 2) {
									indexData[drawOrderPos + k] = (short) (DRAW_ORDER_REV[k] + arrPos + 12); 
								} else {
									indexData[drawOrderPos +k] = (short) (DRAW_ORDER_REV[k] + arrPos + 12); 
								}
							}
							drawOrderPos += DRAW_ORDER_REV.length;
							
							
						}
						
					}		
				}
				
				for (int y = 0; y < height;  ++y) {
					int arrPosStartX = arrPos;
					Tile startTileX = null;
					for (int x = 0; x < width;  ++x) {
						Tile tile = blockMatrix[x][y];
						if (tile != null && tile.type-1 == j)  {
							Tile tX = map.getTile(tile.x+1, tile.y);
							Tile tY = map.getTile(tile.x, tile.y+1);
							
							boolean makeYFace = tY == null || tY.height < tile.height;
							
							if (makeYFace) {
	
								arrPos = tile.meshPos/(byteStride*countPoints);
								
								if (startTileX == null) {
									startTileX = tile;
									arrPosStartX = arrPos;
								}
								
								Tile tYNext = map.getTile(tile.x+1, tile.y+1);
								boolean nextYSegment = (tYNext != null && (tY == null || tYNext.height != tY.height));
								
								if (nextYSegment || tX == null || tX.height != tile.height || tX.type != tile.type || x == width-1 || blockMatrix[x+1][y] == null || tile.type == 3) {
									for (int k = 0; k < DRAW_ORDER_NEW.length; ++k) {
										if (DRAW_ORDER_NEW[k] == 0) {
											indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPos*countPoints +4); 
										} else if (DRAW_ORDER_NEW[k] == 1) {
											indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPos*countPoints +4); 
										} else if (DRAW_ORDER_NEW[k] == 2) {
											indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPosStartX*countPoints +4); 
										} else {
											indexData[drawOrderPos + k] = (short) (DRAW_ORDER_NEW[k] + arrPosStartX*countPoints +4); 
										}
									}
									drawOrderPos += DRAW_ORDER_NEW.length;
									startTileX  = null;
								}
							}
							
						}
					}
				
				}
			}


			if (countTiles[blockTypes[j].baseTile]> 0) {
				short[] indexDataReal = new short[drawOrderPos];
				System.arraycopy(indexData, 0, indexDataReal, 0, indexDataReal.length);
				Mesh mesh = new Mesh(blockData, indexDataReal, countTiles[blockTypes[j].baseTile]*countPoints, TyrGL.GL_USE_VBO == 1);
				Renderable chunkRenderable = new Renderable(mesh, blockTypes[j].mat) {
					@Override
					public void render(float[] vpMatrix)  {
						super.render(vpMatrix);
						World.CHUNKS++;
					}
				};
				
				worldChunkBuckets[j] = chunkRenderable;
				parent.attachSceneObject(chunkRenderable);
				worldChunks.add(chunkRenderable);
				chunkTypes.add(j);
			}
		}
		
		aabb = new AABB(new Vector3(0,0,0), new Vector3(width*BLOCK_SIZE, height*BLOCK_SIZE, maxHeight));
		parent.attachSceneObject(this);
		
		// Next build the border for this world chunk
		border = new Border(new ArrayList<BorderBlock>(defaultBorderCoordinates), this, parent);
		border.setStatus(Status.NEUTRAL);
	}
	
	public void addDefaultBorderBlock(int x, int y, Direction4 direction) {
		//BorderBlock block = new BorderBlock(x,y, direction);
		
		BorderBlock block = null;
		Tile current = blockMatrix[x][y];
		int ordinal = direction.ordinal();
		
		if (direction == Direction4.LEFT || direction == Direction4.RIGHT) {
			if (y-1 < 0 || 
				blockMatrix[x][y-1] == null || 
				blockMatrix[x][y-1].height != current.height ||
				blockMatrix[x][y-1].borderBlocks[ordinal] == null) {
				block = new BorderBlock(x,y, direction);
				defaultBorderCoordinates.add(block);
			} else {
				block = blockMatrix[x][y-1].borderBlocks[ordinal];
				block.length++;
			}
		} else if (direction == Direction4.TOP || direction == Direction4.BOTTOM) {
			if (x-1 < 0 || 
					blockMatrix[x-1][y] == null || 
					blockMatrix[x-1][y].height != current.height ||
					blockMatrix[x-1][y].borderBlocks[ordinal] == null) {
					block = new BorderBlock(x,y, direction);
					defaultBorderCoordinates.add(block);
				} else {
					block = blockMatrix[x-1][y].borderBlocks[ordinal];
					block.length++;
				}
			}
		
		blockMatrix[x][y].borderBlocks[ordinal] = block;
	}
	
	public Renderable getWorldChunkRenderable(int index) {
		return worldChunkBuckets[index];
	}
	
	public static void sInit() {
		
		System.out.println("(Setting up vertex layouts)");
		
		VERTEX_LAYOUT = DefaultMaterial3.DEFAULT_LAYOUT.copy();
		VERTEX_LAYOUT.setPos(OWNER_INFO, VERTEX_LAYOUT.getByteStride());
		VERTEX_LAYOUT.setSize(OWNER_INFO, 1);
		
		System.out.println("(Defining object types)");
		
		objectTypes.add("entities/castle.iqm");
		objectTypes.add("entities/tree1.iqm");
		objectTypes.add("entities/village.iqm");
		objectTypes.add("entities/shrooms1.iqm");
		objectTypes.add("entities/mine.iqm");
		objectTypes.add("entities/windmill.iqm");
		objectTypes.add("entities/tree2.iqm");
		objectTypes.add("entities/farm.iqm");
		objectTypes.add("entities/horse.iqm");
		objectTypes.add("entities/tree3.iqm");
		objectTypes.add("entities/cattle.iqm");
		objectTypes.add("entities/quarry.iqm");
		
		mainObjectTypes.add(0);
		mainObjectTypes.add(1);
		mainObjectTypes.add(2);
		mainObjectTypes.add(4);
		mainObjectTypes.add(5);
		mainObjectTypes.add(7);
		mainObjectTypes.add(8);
		mainObjectTypes.add(9);
		mainObjectTypes.add(10);
		mainObjectTypes.add(11);
		
		System.out.println("(Defining seasonal object types)");
		
		seasonalObjectTypes.add("entities/tree1.iqm");
		seasonalObjectTypes.add("entities/tree2.iqm");
		seasonalObjectTypes.add("entities/tree3.iqm");
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			
			TextureManager.getInstance().createTexture("BUMP_MAP_TEST", Media.CONTEXT.getResourceID("water_bump", "drawable"));
			TextureManager.getInstance().createTexture("TILES", Media.CONTEXT.getResourceID("tiles", "drawable"));
			TextureManager.getInstance().createTexture("TILES_WINTER", Media.CONTEXT.getResourceID("tileswin", "drawable"));
			TextureManager.getInstance().createTexture("SOIL_OCCU", Media.CONTEXT.getResourceID("soil_occu", "drawable"));
			TextureManager.getInstance().createTexture("GRASS_OCCU", Media.CONTEXT.getResourceID("grass_occu", "drawable"));
			SeasonMaterial.createFurTexture("TILES", 1f);
		}
	}
	
	public void init() {
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			
			/** Unrealized Road, treated as grass **/
			blockTypes[0] = new TileMaterial(this, "TILES_WINTER", "TILES", new float[] {
					0.1f,0.01f,
					0.49f,0.01f,
					0,0.5f,
					0.49f,0.5f,
					
					0,0.75f,
					0,0.5f,
					0.49f,0.75f,
					0.49f,0.5f,
					
					0.49f,0.5f,
					0.49f,0.75f,
					0,0.5f,
					0,0.75f
			}, false, 0);
			blockTypes[0].mat.setWind(1/100.f);
			
			/** Grass **/
			blockTypes[1] = new TileMaterial(this, "TILES_WINTER", "TILES", new float[] {
					0.1f,0.01f,
					0.49f,0.01f,
					0,0.5f,
					0.49f,0.5f,
					
					0,0.75f,
					0,0.5f,
					0.49f,0.75f,
					0.49f,0.5f,
					
					0.49f,0.5f,
					0.49f,0.75f,
					0,0.5f,
					0,0.75f
			}, false, 1);
			blockTypes[1].mat.setWind(1/100.f);
			
			/** Water **/
			blockTypes[2] = new TileMaterial(this, "ICE", "WATER", new float[] {
					0.0f,0.0f,
					1f,0.0f,
					0.0f,1,
					1,1,
					
					0.0f,0.0f,
					1f,0.0f,
					0.0f,1,
					1,1,
					
					0.0f,0.0f,
					1f,0.0f,
					0.0f,1,
					1,1,
			}, true, 2);
			blockTypes[2].mat.setWind(1/10.f);
			
			/** Rock **/
			blockTypes[3] = new TileMaterial(this, "TILES_WINTER", "TILES_WINTER", new float[] {
					0.6f,0.01f,
					0.9f,0.01f,
					0.6f,0.4f,
					0.9f,0.4f,
					
					0.6f,0.01f,
					0.9f,0.01f,
					0.6f,0.4f,
					0.9f,0.4f,
					
					0.6f,0.01f,
					0.9f,0.01f,
					0.6f,0.4f,
					0.9f,0.4f,
			}, false, 3);
			
			/** Ice **/
			blockTypes[4] = new TileMaterial(this, "TILES_WINTER", "TILES_WINTER", new float[] {
					0.1f,0.01f,
					0.49f,0.01f,
					0,0.5f,
					0.49f,0.5f,
					
					0,1f,
					0,0.5f,
					0.49f,1,
					0.49f,0.5f,
					
					0.49f,0.5f,
					0.49f,1,
					0,0.5f,
					0,1
			}, false, 4);
			
			/** Beach **/
			blockTypes[5] = new TileMaterial(this, "TILES_WINTER", "TILES", new float[] {
					0.6f,0.61f,
					1f,0.61f,
					0.5f,0.95f,
					1,0.95f,
					
					0.51f,0.61f,
					1f,0.61f,
					0.5f,0.95f,
					1,0.95f,
					
					0.6f,0.61f,
					1f,0.61f,
					0.5f,0.95f,
					1,0.95f,
			}, false, 5);
			
			/** Additional grass layer **/
			blockTypes[6] = new TileMaterial(this, "TILES_WINTER", "TILES", new float[] {
					0.1f,0.01f,
					0.49f,0.01f,
					0,0.5f,
					0.49f,0.5f,
					
					0,0.75f,
					0,0.5f,
					0.49f,0.75f,
					0.49f,0.5f,
					
					0.49f,0.5f,
					0.49f,0.75f,
					0,0.5f,
					0,0.75f
			}, 10, 3, 6);
			blockTypes[6].mat.setWind(1/100.f);
			
			/** Realized Road **/
			blockTypes[7] = new TileMaterial(this, "TILES_WINTER", "TILES", new float[] {
					0.6f,0.01f,
					1f,0.01f,
					0.5f,0.5f,
					1,0.5f,
					
					0.51f,0.01f,
					1f,0.01f,
					0.5f,0.5f,
					1,0.5f,
					
					0.6f,0.01f,
					1f,0.01f,
					0.5f,0.5f,
					1,0.5f,
			}, false, 7);
			
			blockTypes_occu[0] = new TileMaterial(this, "SOIL_OCCU", "SOIL_OCCU", new float[] {
					0,0,
					0,1,
					1,0,
					1,1f,
					
					0,0,
					0,1,
					1,0,
					1,1f,
					
					0,0,
					0,1,
					1,0,
					1,1f,
			}, false, 0);
			
			
			blockTypes_occu[1] = new TileMaterial(this, "GRASS_OCCU", "GRASS_OCCU", new float[] {
					0.1f,0.01f,
					1,0.01f,
					0,0.5f,
					1,0.5f,
					
					0,1f,
					0,0.5f,
					1,1,
					1,0.5f,
					
					1,0.5f,
					1,1,
					0,0.5f,
					0,1
			}, false, 1);
			blockTypes_occu[1].mat.setWind(1/100.f);
			blockTypes_occu[2] = blockTypes[2];
			blockTypes_occu[3] = blockTypes[0];
			blockTypes_occu[4] = blockTypes[1];
			blockTypes_occu[5] = blockTypes[5];

			blockTypes_occu[6] = new TileMaterial(this, "GRASS_OCCU", "GRASS_OCCU", new float[] {
					0.1f,0.01f,
					1,0.01f,
					0,0.5f,
					1,0.5f,
					
					0,1f,
					0,0.5f,
					1,1,
					1,0.5f,
					
					1,0.5f,
					1,1,
					0,0.5f,
					0,1
			}, 10, 3, 1);
			blockTypes_occu[6].mat.setWind(1/100.f);
			blockTypes_occu[7] = blockTypes[0];
		
		}
		

	}
	
	public static WorldChunk createFromMapFile(MapFile file, SceneNode parent, BaronyWindow baronyWindow){
		
		short width = baronyWindow.w;
		short height = baronyWindow.h;
		
		WorldChunk worldChunk = new WorldChunk(width, height, parent);
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int pixelBarony = file.baronyMap.getRGB(x+baronyWindow.x, y+baronyWindow.y);
			
				if (pixelBarony == baronyWindow.color) {
					int pixel = file.tileMap.getRGB(x+baronyWindow.x, y+baronyWindow.y);
					
					int red = (pixel >> 16) & 0xFF;
					int green = (pixel >>8 ) & 0xFF;
					int blue = (pixel) & 0xFF;
					int alpha = pixel >>> 24;
					
					int blockType = red / 16;
					int depth = green;
					int object = blue / 16;
					
					if (blockType != 0 && alpha != 0) {
						if (blockType != 3) {
							worldChunk.setBlock(x, y, new Tile(blockType, depth+2));
	
						} else {
							worldChunk.setBlock(x, y, new Tile(blockType, depth+1));
						}
						if (object != 0) {
							worldChunk.addObject(x, y, object);
						}
						
						int subType = red & 0x0f;
						
						float z = worldChunk.getHeight(x,y);
						
						
						if (blockType != 3) {
							if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
								float grassX = (x+0.5f) * BLOCK_SIZE;
								float grassY = (y+0.5f) * BLOCK_SIZE;
								float grassZ = z+0.075f;
								if (subType == 1) {
									Grass grass = new Grass(worldChunk, worldChunk.parent.createChild(grassX,grassY,grassZ), "GRASS_BLADES", "entities/grass.iqm");
									worldChunk.grassBuckets[0][worldChunk.grassCounts[0]++] = grass;
								} else if (subType == 2) {
									Grass grass = new Grass(worldChunk, worldChunk.parent.createChild(grassX,grassY,grassZ), "GRAIN",  "entities/grain.iqm");
									worldChunk.grassBuckets[1][worldChunk.grassCounts[1]++] = grass;
								}
							}
						} else {
							if (subType == 1) {
	
							}
						}
					}
				}

			}
		}
		
		worldChunk.setMask(CHUNK_MASK + World.getInstance().getCountBaronies());
		
		return worldChunk;
	}
	
	public static WorldChunk createWaterChunk(SceneNode parent){
		
		WorldChunk worldChunk = new WorldChunk(WATER_CHUNK_SIZE, WATER_CHUNK_SIZE, parent);
		WorldMap map = World.getInstance().getMap();
		
		int baseX = (int) (parent.getRelativePos().x / WorldChunk.BLOCK_SIZE) + map.width/2;
		int baseY = (int) (parent.getRelativePos().y / WorldChunk.BLOCK_SIZE) + map.height/2;
		
		for (int y = 0; y < WATER_CHUNK_SIZE; ++y) {
			for (int x = 0; x < WATER_CHUNK_SIZE; ++x) {
				
				int posX = x + baseX;
				int posY = y + baseY;
				
				if (posX < 0 || posY < 0 || posX >= map.width || posY >= map.height || map.tileMap[posX][posY] == null) {
					worldChunk.setBlock(x, y, new Tile(3, 0));
				}
			}
		}
		
		worldChunk.setMask(CHUNK_MASK + World.getInstance().getCountBaronies() + 1);
		
		return worldChunk;
	}

	@Override
	public void render(float[] vpMatrix) {
		
		//border.render(vpMatrix);
	}

	@Override
	protected AABB createUntransformedBoundingBox() {
		return aabb;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Tile getBlock(int x, int y) {
		return blockMatrix[x][y];
	}
	
	public Entity getObject(int index) {
		return objects.get(index);
	}
	
	public int getCountObjects() {
		return objects.size();
	}
	
	public Entity getCastleEntity() {
		return castleEntity;
	}
	
	public Border getBorder() {
		return border;
	}
	
	public void setOccupiedTextures(boolean occupiedTextures) {

		if (occupiedTextures) {
			for (int i = 0; i < worldChunks.size(); ++i) {
				worldChunks.get(i).setMaterial(blockTypes_occu[chunkTypes.get(i)].mat);
			}
		} else {
			for (int i = 0; i < worldChunks.size(); ++i) {
				worldChunks.get(i).setMaterial(blockTypes[chunkTypes.get(i)].mat);
			}
		}
		
	}
	
	public void fillInObjects(int id) {
		for (int i = 0; i < objectBuckets[id].length; ++i) {
			if (objectBuckets[id][i] != null) {
				SceneManager.getInstance().getRenderer().addRenderable(objectBuckets[id][i]);
			}
		}
	}
	
	public void fillInChunks(int id) {
		if (worldChunkBuckets[id] != null) {
			SceneManager.getInstance().getRenderer().addRenderable(worldChunkBuckets[id]);
		}
	}
	
	public void fillInGrass(int id) {
		for (int i = 0; i < grassBuckets[id].length; ++i) {
			if (grassBuckets[id][i] != null) {
				SceneManager.getInstance().getRenderer().addRenderable(grassBuckets[id][i].grass, OpenGLRenderer.TRANSLUCENT_CHANNEL_1);
			}
		}
	}
	
	public void setOwnerValue(float value) {
		ownerValue.value = value;
	}
	
	public float getOwnerValue() {
		return ownerValue.value;
	}

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}

	public int getCountGrassBuckets() {
		return grassBuckets.length;
	}

	public int getCountGrasses(int id) {
		return grassBuckets[id].length;
	}
	
	public Grass getGrass(int id, int index) {
		return grassBuckets[id][index];
	}

	public int getCountTiles() {
		return tiles.size();
	}
	
	public Tile getTile(int index) {
		return tiles.get(index);
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public Entity getMainObject(int objectNo) {
		return mainObjects.get(objectNo);
	}
	
	public Tile getMainObjectTile(int objectNo) {
		return mainObjectTiles.get(objectNo);
	}

	public Tile getCastleTile() {
		return castleTile;
	}

	public void changeTileType(Tile t, int newTileType) {
		Renderable chunk = worldChunkBuckets[t.type-1];
		Mesh mesh = chunk.getMesh();
		
		int byteStride = blockTypes[t.type-1].mat.getByteStride();
		int pos = t.meshPos + 16 * byteStride;
		int uvOffset = blockTypes[t.type-1].mat.getUVOffset();
		
		mesh.setVertexInfo(pos+uvOffset, blockTypes[newTileType-1].uv[0]);
		mesh.setVertexInfo(pos+uvOffset+1, blockTypes[newTileType-1].uv[1]);
		pos += byteStride;
		
		mesh.setVertexInfo(pos+uvOffset, blockTypes[newTileType-1].uv[2]);
		mesh.setVertexInfo(pos+uvOffset+1, blockTypes[newTileType-1].uv[3]);
		pos += byteStride;
		
		mesh.setVertexInfo(pos+uvOffset, blockTypes[newTileType-1].uv[4]);
		mesh.setVertexInfo(pos+uvOffset+1, blockTypes[newTileType-1].uv[5]);
		pos += byteStride;
		
		mesh.setVertexInfo(pos+uvOffset, blockTypes[newTileType-1].uv[6]);
		mesh.setVertexInfo(pos+uvOffset+1, blockTypes[newTileType-1].uv[7]);
		pos += byteStride;
	}
}
