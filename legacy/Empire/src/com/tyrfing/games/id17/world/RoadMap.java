package com.tyrfing.games.id17.world;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.movement.DirectMovement;

public class RoadMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3662735097350307344L;

	private class DistanceComparator implements Comparator<RoadNode> {
		
		private PathFinder finder;
		
		public DistanceComparator(PathFinder finder) {
			this.finder = finder;
		}
		
		@Override
		public int compare(RoadNode n1, RoadNode n2) {
			int d1 = n1.distance + getDistance(n1, finder.dstNode);
			int d2 = n2.distance + getDistance(n2, finder.dstNode);
			
			if (d1 < d2) return -1;
			if (d1 > d2) return 1;
			
			return 0;
		}
	}
	
	public static final int NO_HOLDING = -1;
	
	private TIntObjectMap<RoadNode> holdingToNode = new TIntObjectHashMap<RoadNode>(); 
	private RoadNode[][] roadNetwork;
	
	private class PathFinder {
		public final boolean pathRealized;
		public PriorityQueue<RoadNode> openList;
		public Set<RoadNode> closedList;
		public RoadNode dstNode;
		
		public PathFinder(boolean pathRealized) {
			this.pathRealized = pathRealized;
			openList = new PriorityQueue<RoadNode>(roadNetwork.length, new DistanceComparator(this));
			closedList = new HashSet<RoadNode>();
		}
		
		private void expandNode(RoadNode n) {
			if (n.x > 0) updateNode(n, roadNetwork[n.x-1][n.y]);
			if (n.y > 0) updateNode(n, roadNetwork[n.x][n.y-1]);
			if (n.x < roadNetwork.length - 1) updateNode(n, roadNetwork[n.x+1][n.y]);
			if (n.y < roadNetwork.length - 1) updateNode(n, roadNetwork[n.x][n.y+1]);
		}
		
		private void updateNode(RoadNode pred, RoadNode succ) {
			if (succ != null && (!pathRealized || succ.isRealized())) {
				if (!closedList.contains(succ)) {
					if (succ.distance > pred.distance + 1) {
						succ.distance = pred.distance + 1;
						succ.pred = pred;
						openList.add(succ);
					}
				}
			}
		}
		
		private List<RoadNode> getPath(int startID, int endID) {
			RoadNode srcNode = getRoadNode(startID);
			dstNode = getRoadNode(endID);
			
			List<RoadNode> path = srcNode.getPath(dstNode, pathRealized);
			if (path == null) {
				
				for (int x = 0; x < roadNetwork.length; ++x) {
					for (int y = 0; y < roadNetwork[x].length; ++y) {
						if (roadNetwork[x][y] != null) { 
							roadNetwork[x][y].pred = null;
							roadNetwork[x][y].distance = WorldMap.HUGE_DISTANCE;
						}
					}
				}

				openList.add(srcNode);
				
				srcNode.distance = 0;
				
				while(!openList.isEmpty()) {
					RoadNode current = openList.poll();
					
					if (current == dstNode) break;
					
					closedList.add(current);
					
					expandNode(current);
				}
				
				path = new ArrayList<RoadNode>();
				ArrayList<RoadNode> revPath = new ArrayList<RoadNode>();
				
				if (dstNode.pred != null) {
					RoadNode current = dstNode;
					
					do {
						path.add(0, current);
						revPath.add(current);
						
						current = current.pred;
					} while (current != null);
				}
				
				
				srcNode.addPath(dstNode, path, pathRealized);
				dstNode.addPath(srcNode, revPath, pathRealized);
				
			}
			
			return path;
		}
	}
	
	public RoadMap(int width, int height) {
		roadNetwork = new RoadNode[width][height];
	}
	
	/**
	 * Insert a point into the road network
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param holdingID ID of holding it connects to, NO_HOLDING 
	 * 					if its an intermediary part of the road
	 */
	
	public void insertRoadPoint(int x, int y, int holdingID) {
		RoadNode node = new RoadNode(x,y,holdingID);
		roadNetwork[x][y] = node;
		
		if (holdingToNode.get(holdingID) == null) {
			holdingToNode.put(holdingID, node);
		}
	}
	
	public void insertRoadPoint(int x, int y) {
		insertRoadPoint(x,y,NO_HOLDING);
	}
	
	public RoadNode getRoadNode(int holdingID) {
		return holdingToNode.get(holdingID);
	}
	
	public void realizePath(int startID, int endID) {
		
		PathFinder finder = new PathFinder(false);
		List<RoadNode> path = finder.getPath(startID, endID);
		
		for (int i = 0; i < path.size(); ++i) {
			RoadNode n = path.get(i);
			n.realize();
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				if (i > 0 && i < path.size() - 1) {
					Tile t = World.getInstance().getMap().getTile(n.x, n.y);
					t.chunk.changeTileType(t, 8);	
				}
			}
		}
		
		resetPaths();
	}
	
	/**
	 * Checks if there is a (possibly unrealized) direct path between two holdings.
	 * @param startID index of the start holding
	 * @param endID	index of the end holding
	 * @return null if no such path exists, otherwise the path
	 */
	
	public List<RoadNode> hasDirectRealizeablePath(int startID, int endID) {
		PathFinder finder = new PathFinder(false);
		List<RoadNode> path =  finder.getPath(startID, endID);
		
		for (int i = 0; i < path.size(); ++i) {
			RoadNode n = path.get(i);
			if (isObstructed(startID, endID, n)) return null;
			if (n.x > 0 && isObstructed(startID, endID, roadNetwork[n.x-1][n.y])) return null;
			if (n.y > 0 && isObstructed(startID, endID, roadNetwork[n.x][n.y-1])) return null;
			if (n.x < roadNetwork.length - 1 && isObstructed(startID, endID, roadNetwork[n.x+1][n.y])) return null;
			if (n.y < roadNetwork.length - 1 && isObstructed(startID, endID, roadNetwork[n.x][n.y+1])) return null;
		}
		
		return path;
	}
	
	public boolean hasDirectPath(short startID, short endID) {
		PathFinder finder = new PathFinder(true);
		List<RoadNode> path = finder.getPath(startID, endID);
		
		if (path.size() == 0) return false;
		
		for (int i = 0; i < path.size(); ++i) {
			RoadNode n = path.get(i);
			if (isObstructed(startID, endID, n)) return false;
			if (n.x > 0 && isObstructed(startID, endID, roadNetwork[n.x-1][n.y])) return false;
			if (n.y > 0 && isObstructed(startID, endID, roadNetwork[n.x][n.y-1])) return false;
			if (n.x < roadNetwork.length - 1 && isObstructed(startID, endID, roadNetwork[n.x+1][n.y])) return false;
			if (n.y < roadNetwork.length - 1 && isObstructed(startID, endID, roadNetwork[n.x][n.y+1])) return false;
		}
		
		return true;
	}
	
	public void resetPaths() {
		for (int x = 0; x < roadNetwork.length; ++x) {
			for (int y = 0; y < roadNetwork[x].length; ++y) {
				if (roadNetwork[x][y] != null) { 
					roadNetwork[x][y].clearPaths();
				}
			}
		}
	}
	

	
	public List<RoadNode> getRealizedPath(int startID, int endID) {
		PathFinder finder = new PathFinder(true);
		return finder.getPath(startID, endID);
	}
	
	public void moveFromTo(DirectMovement movement, int startID, int endID) {
		PathFinder finder = new PathFinder(true);
		List<RoadNode> path =  finder.getPath(startID, endID);
		
		movement.clear();
		for (int i = 0; i < path.size(); ++i) {
			movement.addTarget(new RoadNodeProvider(path.get(i)));
		}
	}
	
	private boolean isObstructed(int startID, int endID, RoadNode node) {
		if (node != null) {
			int holdingID = node.holdingID;
			if (holdingID != NO_HOLDING) {
				if (holdingID != startID && holdingID!= endID) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int getDistance(RoadNode n1, RoadNode n2) {
		return Math.abs(n1.x -  n2.x) + Math.abs(n1.y - n2.y);
	}
	
	public List<RoadNode> getPath(int startID, int endID, boolean realizable) {
		PathFinder finder = new PathFinder(realizable);
		List<RoadNode> path = finder.getPath(startID, endID);
		return path;
	}
}
