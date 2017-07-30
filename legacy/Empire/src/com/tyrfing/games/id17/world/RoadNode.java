package com.tyrfing.games.id17.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.movement.ITargetProvider;

public class RoadNode implements ITargetProvider {
	public final int x,y,holdingID;
	public RoadNode pred;
	public int distance;
	
	public Map<RoadNode, List<RoadNode>> paths = new HashMap<RoadNode, List<RoadNode>>();
	public Map<RoadNode, List<RoadNode>> unrealizedPaths = new HashMap<RoadNode, List<RoadNode>>();
	
	public final Vector3 pos; 
	
	private boolean realized;
	
	public RoadNode(int x, int y, int holdingID) {
		this.x = x;
		this.y = y;
		this.holdingID = holdingID;
		
		int bX = x-World.getInstance().getMap().width/2;
		int bY = y-World.getInstance().getMap().height/2;
		
		pos = new Vector3(	
			(bX+0.5f)*WorldChunk.BLOCK_SIZE, 
			(bY+0.5f)*WorldChunk.BLOCK_SIZE, 
			World.getInstance().getMap().getTile(x, y).getScaledHeight()
		);
	}
	
	public List<RoadNode> getPath(RoadNode dstNode, boolean realized) {
		if (realized) {
			return paths.get(dstNode);
		} else {
			return unrealizedPaths.get(dstNode);
		}
	}
	
	public void addPath(RoadNode dstNode, List<RoadNode> path, boolean realized) {
		if (realized) {
			paths.put(dstNode, path);
		} else {
			unrealizedPaths.put(dstNode, path);
		}
	}

	@Override
	public Vector3 getTargetPos() {
		return pos;
	}

	public void clearPaths() {
		paths.clear();
	}

	public boolean isRealized() {
		return realized;
	}
	
	public void realize() {
		realized = true;
	}
	
	@Override
	public String toString() {
		return "[x: " + x + ", y: " + y + ", holdingID: " + holdingID + "]";
	}
	
}
