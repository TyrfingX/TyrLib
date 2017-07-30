package tyrfing.common.graph;

import java.util.Vector;

import tyrfing.common.math.Vector2;

public class Vertex<V extends Object> implements Comparable<Vertex<V>> {
	
	private float x,y;
	private float distance;
	private Vector<Vertex<V>> adjacentList;
	private Vertex<V> parent = null;
	private V content;
	
	public Vertex(V content, float x, float y)
	{
		this.x = x;
		this.y = y;
		this.distance = 0;
		adjacentList = new Vector<Vertex<V>>();
		this.content = content;
	}
	
	public Vertex(V content, Vector2 pos) {
		this(content, pos.x, pos.y);
	}
	
	public void addEdge(Vertex<V> neighbour)
	{
		if (!this.isNeighbour(neighbour)) adjacentList.add(neighbour);
	}
	
	public void removeEdge(Vertex<V> neighbour)
	{
		for (Vertex<V> node : adjacentList)
		{
			if (node == neighbour)
			{
				adjacentList.remove(node);
				break;
			}
		}
	}
	
	public void removeAllEdges()
	{
		adjacentList.clear();
	}
	
	public Vertex<V> getNeighbour(int index)
	{
		return adjacentList.get(index);
	}
	
	public boolean isNeighbour(Vertex<V> node)
	{
		for (Vertex<V> node2 : adjacentList)
		{
			if (node2 == node)
			{
				return true;
			}
		}		
		
		return false;
	}
	
	public V getContent()
	{
		return content;
	}
	
	public int getDegree()
	{
		return adjacentList.size();
	}
	
	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}
	
	public float getDistance()
	{
		return distance;
	}
	
	public void setDistance(float distance)
	{
		this.distance = distance;
	}
	
	public void setParent(Vertex<V> parent)
	{
		this.parent = parent;
	}
	
	public Vertex<V> getParent()
	{
		return this.parent;
	}
	
	public float distanceTo(Vertex<V> node)
	{
		return (float) Math.sqrt((this.x - node.getX()) * (this.x - node.getX()) 
								+ (this.y - node.getY()) * (this.y - node.getY()));
	}

	@Override
	public int compareTo(Vertex<V> node) {
		
		if (node.getDistance() < this.getDistance()) return 1;
		else if (node.getDistance() == this.getDistance()) return 0;
		else return -1;
	}
	
	public Vertex<V> clone()
	{
		Vertex<V> node = new Vertex<V>(content, x,y);
		return node;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
}
