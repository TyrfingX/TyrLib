package com.tyrfing.games.id17.world;

import java.util.HashMap;
import java.util.List;

import com.tyrlib2.graphics.materials.ColoredMaterial;
import com.tyrlib2.graphics.renderer.BoundedRenderable;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.Direction4;

public class Border extends BoundedRenderable implements IRenderable {
	
	private static final Color neutralColor = new Color(0.25f,0.25f,0.25f,0.9f);
	private static final Color enemyColor = new Color(0.65f,0.15f,0.15f,0.9f);
	private static final Color hegemonColor = new Color(0.6f,0.6f,0.2f,0.9f);
	private static final Color playerColor = new Color(0.3f, 0.3f, 0.85f, 0.9f);
	private static final Color subordinateColor = new Color(0.5f, 0.5f, 0.85f, 0.9f);
	private static final Color allyColor = new Color(0.2f, 0.4f, 0.2f, 0.9f);
	
	public static final short[] DRAW_ORDER = { 6, 7, 3, 6, 3, 2, 5, 7, 6, 4, 5, 6, 0, 1, 4, 6, 2, 4};
	
	public enum Status {
		NEUTRAL, ENEMY, SUBORDINATE, HEGEMON, PLAYER, ALLY
	}
	
	private static HashMap<Status, ColoredMaterial> materialMap = new HashMap<Status, ColoredMaterial>();
	
	static {
		materialMap.put(Status.NEUTRAL, new ColoredMaterial(new Color[] { Color.WHITE }, neutralColor));
		materialMap.put(Status.ENEMY, new ColoredMaterial(new Color[] { Color.WHITE }, enemyColor));
		materialMap.put(Status.SUBORDINATE, new ColoredMaterial(new Color[] { Color.WHITE }, subordinateColor));
		materialMap.put(Status.PLAYER, new ColoredMaterial(new Color[] { Color.WHITE }, playerColor));
		materialMap.put(Status.ALLY, new ColoredMaterial(new Color[] { Color.WHITE }, allyColor));
		materialMap.put(Status.HEGEMON, new ColoredMaterial(new Color[] { Color.WHITE }, hegemonColor));
	}
	
	private List<BorderBlock> coords;
	private List<Boolean> visible;
	private SceneNode parent;
	private WorldChunk worldChunk;

	private static final float DE_SCALE = 0.85f;
	
	private Renderable borderRenderable;
	
	private Status status;
	private Status oldStatus;
	
	private float[] vertexData;
	private	short[] indexData;
	
	private boolean render = true;
	private int insertionID;
	
	public Border(List<BorderBlock> coords, WorldChunk worldChunk, SceneNode parent) {
		this.coords = coords;
		this.parent = parent;
		this.worldChunk = worldChunk;
		parent.attachSceneObject(this);
	}
	
	public void build(Status status) {
		this.status = status;
		build();
	}
	
	public void setMainCoords(List<Boolean> visible) {
		this.visible = visible;
	}
	
	public List<BorderBlock> getCoords() {
		return coords;
	}
	
	public void rebuild() {
		if (vertexData == null) {
			if (coords.size() > 0) {
				build();
			}
		} else {
			
			int countPoints = 8;
			
			if (vertexData == null) {
				vertexData = new float[coords.size() * materialMap.get(status).getByteStride() * countPoints];
		 		indexData = new short[DRAW_ORDER.length * coords.size()];
			}
			
			ColoredMaterial mat = materialMap.get(status);
			
			int byteStride = mat.getByteStride();
			int colorOffset = mat.getColorOffset();
			
			for (int i = 0, countCoords = coords.size(); i < countCoords; ++i) {
				int arrPos = i*byteStride*countPoints;
				
				if (visible == null || !visible.get(i)) {
					for (int k = 0; k < countPoints; ++k) {
						vertexData[arrPos + k * byteStride + colorOffset + 3] = 0.2f;
					}
				} else {
					for (int k = 0; k < countPoints; ++k) {
						vertexData[arrPos + k * byteStride + colorOffset + 3] = 0.9f;
					}
				}
			}
			
			Mesh mesh = new Mesh(vertexData, indexData, coords.size()*8, true);
			
			if (borderRenderable != null) {
				borderRenderable.destroy();
			}
			
			borderRenderable = new Renderable(mesh, materialMap.get(status));
			parent.attachSceneObject(borderRenderable);
		}
	}
	
	public void build() {
		
		int countPoints = 8;
		
		if (vertexData == null) {
			vertexData = new float[coords.size() * materialMap.get(status).getByteStride() * countPoints];
	 		indexData = new short[DRAW_ORDER.length * coords.size()];
		}
 		
 		WorldMap map = World.getInstance().getMap();
		Vector3 pos = worldChunk.getParent().getRelativePos();
		int baseX = (int) (pos.x / WorldChunk.BLOCK_SIZE) + map.width/2;
		int baseY = (int) (pos.y / WorldChunk.BLOCK_SIZE) + map.height/2;
 		
		float displace = (float)Math.random()/10;
		
		ColoredMaterial mat = materialMap.get(status);
		Color c = mat.getColors()[0];
		
		int byteStride = mat.getByteStride();
		int colorOffset = mat.getColorOffset();
		
		Vector3 min = new Vector3();
		Vector3 max = new Vector3();
		
		for (int i = 0, countCoords = coords.size(); i < countCoords; ++i) {
			BorderBlock coord = coords.get(i);
			
			float height = worldChunk.getHeight(coord.x, coord.y);
			
			float height2 = Math.min(map.getHeight(baseX+coord.x-1, baseY+coord.y), map.getHeight(baseX+coord.x+1, baseY+coord.y));
			height2 = Math.min(map.getHeight(baseX+coord.x, baseY+coord.y-1), height2);
			height2 = Math.min(map.getHeight(baseX+coord.x, baseY+coord.y+1), height2);
			
			min.set(coord.x * WorldChunk.BLOCK_SIZE - WorldChunk.BLOCK_SIZE * (1-DE_SCALE)/2, coord.y * WorldChunk.BLOCK_SIZE + WorldChunk.BLOCK_SIZE * (1-DE_SCALE)/2, height2);
			max.set(WorldChunk.BLOCK_SIZE+min.x, WorldChunk.BLOCK_SIZE+min.y, height+1-DE_SCALE+displace);
			
			if (coord.direction == Direction4.LEFT) {
				max.x -= WorldChunk.BLOCK_SIZE * DE_SCALE;
				min.x -= WorldChunk.BLOCK_SIZE * (1-DE_SCALE)/2;
				max.y += coord.length*WorldChunk.BLOCK_SIZE;
			} else if (coord.direction == Direction4.RIGHT) {
				max.x += WorldChunk.BLOCK_SIZE * (1-DE_SCALE)/2;
				min.x += WorldChunk.BLOCK_SIZE * DE_SCALE;
				max.y += coord.length*WorldChunk.BLOCK_SIZE;
			} else if (coord.direction == Direction4.TOP) {
				max.y -= WorldChunk.BLOCK_SIZE * DE_SCALE;
				min.y -= WorldChunk.BLOCK_SIZE * (1-DE_SCALE)/2;
				max.x += coord.length*WorldChunk.BLOCK_SIZE;
			} else if (coord.direction == Direction4.BOTTOM) {
				max.y += WorldChunk.BLOCK_SIZE * (1-DE_SCALE)/2;
				min.y += WorldChunk.BLOCK_SIZE * DE_SCALE;
				max.x += coord.length*WorldChunk.BLOCK_SIZE;
			}
			
			int arrPos = i*byteStride*countPoints;
			
			vertexData[arrPos] = min.x; vertexData[arrPos+1] = max.y; vertexData[arrPos+2] = min.z; 
			vertexData[arrPos+colorOffset] = c.r; vertexData[arrPos+colorOffset+1] =  c.g;
			vertexData[arrPos+colorOffset+2] = c.b; vertexData[arrPos+colorOffset+3] =  c.a;
			arrPos += byteStride;
			
			vertexData[arrPos] = max.x; vertexData[arrPos+1] = min.y; vertexData[arrPos+2] = min.z; 
			vertexData[arrPos+colorOffset] = c.r; vertexData[arrPos+colorOffset+1] =  c.g;
			vertexData[arrPos+colorOffset+2] = c.b; vertexData[arrPos+colorOffset+3] =  c.a;
			arrPos += byteStride;
			
			vertexData[arrPos] = min.x; vertexData[arrPos+1] = max.y; vertexData[arrPos+2] = min.z; 
			vertexData[arrPos+colorOffset] = c.r; vertexData[arrPos+colorOffset+1] =  c.g;
			vertexData[arrPos+colorOffset+2] = c.b; vertexData[arrPos+colorOffset+3] =  c.a;
			arrPos += byteStride;
			
			vertexData[arrPos] = max.x; vertexData[arrPos+1] = max.y; vertexData[arrPos+2] = min.z; 
			vertexData[arrPos+colorOffset] = c.r; vertexData[arrPos+colorOffset+1] =  c.g;
			vertexData[arrPos+colorOffset+2] = c.b; vertexData[arrPos+colorOffset+3] =  c.a;
			arrPos += byteStride;
			
			
			vertexData[arrPos] = min.x; vertexData[arrPos+1] = min.y; vertexData[arrPos+2] = max.z; 
			vertexData[arrPos+colorOffset] = c.r; vertexData[arrPos+colorOffset+1] =  c.g;
			vertexData[arrPos+colorOffset+2] = c.b; vertexData[arrPos+colorOffset+3] =  c.a;
			arrPos += byteStride;
			
			vertexData[arrPos] = max.x; vertexData[arrPos+1] = min.y; vertexData[arrPos+2] = max.z; 
			vertexData[arrPos+colorOffset] = c.r; vertexData[arrPos+colorOffset+1] =  c.g;
			vertexData[arrPos+colorOffset+2] = c.b; vertexData[arrPos+colorOffset+3] =  c.a;
			arrPos += byteStride;
			
			vertexData[arrPos] = min.x; vertexData[arrPos+1] = max.y; vertexData[arrPos+2] = max.z; 
			vertexData[arrPos+colorOffset] = c.r; vertexData[arrPos+colorOffset+1] =  c.g;
			vertexData[arrPos+colorOffset+2] = c.b; vertexData[arrPos+colorOffset+3] =  c.a;
			arrPos += byteStride;
			
			vertexData[arrPos] = max.x; vertexData[arrPos+1] = max.y; vertexData[arrPos+2] = max.z; 
			vertexData[arrPos+colorOffset] = c.r; vertexData[arrPos+colorOffset+1] =  c.g;
			vertexData[arrPos+colorOffset+2] = c.b; vertexData[arrPos+colorOffset+3] =  c.a;
			
			arrPos = i*byteStride*countPoints;
			
			if (visible == null || !visible.get(i)) {
				for (int k = 0; k < countPoints; ++k) {
					vertexData[arrPos + k * byteStride + colorOffset + 3] = 0.2f;
				}
			}
			
			for (int j = 0; j < DRAW_ORDER.length; ++j) {
				indexData[i*DRAW_ORDER.length + j] = (short) (DRAW_ORDER[j] + i * 8);
			}
			
		}
		
		Mesh mesh = new Mesh(vertexData, indexData, coords.size()*8, true);
		
		if (borderRenderable != null) {
			parent.detachSceneObject(borderRenderable);
		}
		
		borderRenderable = new Renderable(mesh, materialMap.get(status));
		parent.attachSceneObject(borderRenderable);
	}

	@Override
	public void render(float[] vpMatrix) {
		if (render && borderRenderable != null) {
			borderRenderable.render(vpMatrix);
		}
	}
	
	public void setRender(boolean state) {
		this.render = state;
	}
	
	public void setStatus(Status status) {
		if (this.status != status) {
			oldStatus = this.status;
			this.status = status;
			if (borderRenderable != null) {
				borderRenderable.setMaterial(materialMap.get(status));
			}
		}
	}
	
	public Status getOldStatus() {
		return oldStatus;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	protected AABB createUntransformedBoundingBox() {
		return worldChunk.getUntransformedBoundingBox();
	}

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
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
		if (parent != null) {
			parent.detach();
		}
		
		if (borderRenderable != null) {
			borderRenderable.destroy();
		}
	}
}
